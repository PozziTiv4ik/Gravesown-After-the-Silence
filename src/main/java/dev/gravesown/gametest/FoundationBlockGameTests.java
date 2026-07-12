package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FoundationBlockGameTests {
    private FoundationBlockGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void foundationBlocksHaveStableIdsItemsAndProperties(GameTestHelper helper) {
        Block[] blocks = {
                ModBlocks.ASHEN_SOD.get(),
                ModBlocks.GRAVE_LOAM.get(),
                ModBlocks.HUSHSTONE.get(),
                ModBlocks.DEEP_HUSHSTONE.get(),
                ModBlocks.GRAVEBED.get()
        };
        Item[] items = {
                ModItems.ASHEN_SOD.get(),
                ModItems.GRAVE_LOAM.get(),
                ModItems.HUSHSTONE.get(),
                ModItems.DEEP_HUSHSTONE.get(),
                ModItems.GRAVEBED.get()
        };
        String[] ids = {"ashen_sod", "grave_loam", "hushstone", "deep_hushstone", "gravebed"};
        float[] destroyTimes = {0.65F, 0.55F, 1.8F, 3.2F, -1.0F};
        BlockPos relativePos = new BlockPos(1, 1, 1);

        for (int index = 0; index < blocks.length; index++) {
            Block block = blocks[index];
            Item item = items[index];
            ResourceLocation expectedId = Gravesown.id(ids[index]);
            helper.assertTrue(
                    expectedId.equals(BuiltInRegistries.BLOCK.getKey(block)),
                    ids[index] + " block registry id must remain stable"
            );
            helper.assertTrue(
                    expectedId.equals(BuiltInRegistries.ITEM.getKey(item)),
                    ids[index] + " block item registry id must remain stable"
            );
            helper.assertTrue(
                    item instanceof BlockItem blockItem && blockItem.getBlock() == block,
                    ids[index] + " item must place its matching block"
            );
            helper.assertTrue(
                    Math.abs(block.defaultDestroyTime() - destroyTimes[index]) < 1.0E-6F,
                    ids[index] + " has an unexpected destroy time"
            );

            helper.setBlock(relativePos, block);
            helper.assertBlockPresent(block, relativePos);
        }

        helper.assertTrue(
                ModBlocks.GRAVEBED.get().getLootTable().equals(BuiltInLootTables.EMPTY),
                "Gravebed must never have a survival loot table"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void foundationBlocksUseExpectedToolsTagsAndLoot(GameTestHelper helper) {
        BlockState ashenSod = ModBlocks.ASHEN_SOD.get().defaultBlockState();
        BlockState graveLoam = ModBlocks.GRAVE_LOAM.get().defaultBlockState();
        BlockState hushstone = ModBlocks.HUSHSTONE.get().defaultBlockState();
        BlockState deepHushstone = ModBlocks.DEEP_HUSHSTONE.get().defaultBlockState();

        helper.assertTrue(ashenSod.is(BlockTags.MINEABLE_WITH_SHOVEL), "Ashen Sod must be shovel-mineable");
        helper.assertTrue(graveLoam.is(BlockTags.MINEABLE_WITH_SHOVEL), "Grave Loam must be shovel-mineable");
        helper.assertTrue(hushstone.is(BlockTags.MINEABLE_WITH_PICKAXE), "Hushstone must be pickaxe-mineable");
        helper.assertTrue(deepHushstone.is(BlockTags.MINEABLE_WITH_PICKAXE), "Deep Hushstone must be pickaxe-mineable");
        helper.assertTrue(
                deepHushstone.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL),
                "A wooden tool must be too weak for Deep Hushstone"
        );
        helper.assertTrue(
                deepHushstone.is(BlockTags.INCORRECT_FOR_GOLD_TOOL),
                "A gold tool must be too weak for Deep Hushstone"
        );
        helper.assertTrue(
                new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(hushstone),
                "A wooden pickaxe must harvest normal Hushstone"
        );
        helper.assertTrue(
                !new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(deepHushstone),
                "A wooden pickaxe must not harvest Deep Hushstone"
        );
        helper.assertTrue(
                !new ItemStack(Items.GOLDEN_PICKAXE).isCorrectToolForDrops(deepHushstone),
                "A gold pickaxe must not harvest Deep Hushstone"
        );
        helper.assertTrue(
                new ItemStack(Items.STONE_PICKAXE).isCorrectToolForDrops(deepHushstone),
                "A stone pickaxe must harvest Deep Hushstone"
        );
        helper.assertTrue(
                !new ItemStack(Items.WOODEN_SHOVEL).isCorrectToolForDrops(hushstone),
                "A shovel must not harvest Hushstone"
        );

        assertSingleDrop(helper, ModBlocks.ASHEN_SOD.get(), ModItems.ASHEN_SOD.get(), ItemStack.EMPTY);
        assertSingleDrop(helper, ModBlocks.GRAVE_LOAM.get(), ModItems.GRAVE_LOAM.get(), ItemStack.EMPTY);
        assertSingleDrop(
                helper,
                ModBlocks.HUSHSTONE.get(),
                ModItems.HUSHSTONE.get(),
                new ItemStack(Items.WOODEN_PICKAXE)
        );
        assertSingleDrop(
                helper,
                ModBlocks.DEEP_HUSHSTONE.get(),
                ModItems.DEEP_HUSHSTONE.get(),
                new ItemStack(Items.STONE_PICKAXE)
        );
        helper.succeed();
    }

    private static void assertSingleDrop(GameTestHelper helper, Block block, Item expectedItem, ItemStack tool) {
        LootTable table = helper.getLevel()
                .getServer()
                .reloadableRegistries()
                .getLootTable(block.getLootTable());
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                .withParameter(LootContextParams.TOOL, tool)
                .create(LootContextParamSets.BLOCK);
        List<ItemStack> drops = table.getRandomItems(params, 1L);
        helper.assertTrue(
                drops.size() == 1 && drops.getFirst().is(expectedItem) && drops.getFirst().getCount() == 1,
                BuiltInRegistries.BLOCK.getKey(block) + " must drop exactly one matching block item"
        );
    }
}
