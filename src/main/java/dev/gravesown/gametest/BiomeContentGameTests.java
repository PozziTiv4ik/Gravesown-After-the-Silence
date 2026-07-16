package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.event.StickyMovementEvents;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BiomeContentGameTests {
    private BiomeContentGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void biomeBlocksHaveStableIdsItemsAndMiningTools(GameTestHelper helper) {
        Block[] blocks = {
                ModBlocks.ROOTFELT.get(),
                ModBlocks.FIBROUS_LOAM.get(),
                ModBlocks.SCAR_SHALE.get(),
                ModBlocks.MARROWSTONE.get(),
                ModBlocks.SUTURE_SILT.get(),
                ModBlocks.DRIED_ICHOR.get()
        };
        Item[] items = {
                ModItems.ROOTFELT.get(),
                ModItems.FIBROUS_LOAM.get(),
                ModItems.SCAR_SHALE.get(),
                ModItems.MARROWSTONE.get(),
                ModItems.SUTURE_SILT.get(),
                ModItems.DRIED_ICHOR.get()
        };
        String[] ids = {
                "rootfelt",
                "fibrous_loam",
                "scar_shale",
                "marrowstone",
                "suture_silt",
                "dried_ichor"
        };

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

            BlockPos position = new BlockPos(1 + index % 3, 1, 1 + index / 3);
            helper.setBlock(position, block);
            helper.assertBlockPresent(block, position);
        }

        assertMineableWith(helper, ModBlocks.ROOTFELT.get().defaultBlockState(), BlockTags.MINEABLE_WITH_SHOVEL);
        assertMineableWith(helper, ModBlocks.FIBROUS_LOAM.get().defaultBlockState(), BlockTags.MINEABLE_WITH_SHOVEL);
        assertMineableWith(helper, ModBlocks.SUTURE_SILT.get().defaultBlockState(), BlockTags.MINEABLE_WITH_SHOVEL);
        assertMineableWith(helper, ModBlocks.DRIED_ICHOR.get().defaultBlockState(), BlockTags.MINEABLE_WITH_SHOVEL);
        assertMineableWith(helper, ModBlocks.SCAR_SHALE.get().defaultBlockState(), BlockTags.MINEABLE_WITH_PICKAXE);
        assertMineableWith(helper, ModBlocks.MARROWSTONE.get().defaultBlockState(), BlockTags.MINEABLE_WITH_PICKAXE);
        helper.assertTrue(
                new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(ModBlocks.SCAR_SHALE.get().defaultBlockState()),
                "A wooden pickaxe must harvest Scar Shale"
        );
        helper.assertTrue(
                new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(ModBlocks.MARROWSTONE.get().defaultBlockState()),
                "A wooden pickaxe must harvest Marrowstone"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void biomeBlocksKeepTheirSelfDropLootTables(GameTestHelper helper) {
        assertSingleDrop(helper, ModBlocks.ROOTFELT.get(), ModItems.ROOTFELT.get(), ItemStack.EMPTY);
        assertSingleDrop(helper, ModBlocks.FIBROUS_LOAM.get(), ModItems.FIBROUS_LOAM.get(), ItemStack.EMPTY);
        assertSingleDrop(
                helper,
                ModBlocks.SCAR_SHALE.get(),
                ModItems.SCAR_SHALE.get(),
                new ItemStack(Items.WOODEN_PICKAXE)
        );
        assertSingleDrop(
                helper,
                ModBlocks.MARROWSTONE.get(),
                ModItems.MARROWSTONE.get(),
                new ItemStack(Items.WOODEN_PICKAXE)
        );
        assertSingleDrop(helper, ModBlocks.SUTURE_SILT.get(), ModItems.SUTURE_SILT.get(), ItemStack.EMPTY);
        assertSingleDrop(helper, ModBlocks.DRIED_ICHOR.get(), ModItems.DRIED_ICHOR.get(), ItemStack.EMPTY);
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void stickyTerrainKeepsAFullBlockPlayerJump(GameTestHelper helper) {
        BlockPos surface = new BlockPos(2, 1, 2);
        BlockPos absoluteSurface = helper.absolutePos(surface);
        helper.setBlock(surface, ModBlocks.DRIED_ICHOR.get());

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.moveTo(
                absoluteSurface.getX() + 0.5D,
                absoluteSurface.getY() + 1.0D,
                absoluteSurface.getZ() + 0.5D,
                0.0F,
                0.0F
        );
        player.setDeltaMovement(Vec3.ZERO);
        player.jumpFromGround();

        double unrestrictedJump = player.getAttributeValue(Attributes.JUMP_STRENGTH) + player.getJumpBoostPower();
        helper.assertTrue(
                ModBlocks.DRIED_ICHOR.get().getJumpFactor() < 1.0F,
                "Dried Ichor must remain horizontally sticky and exercise the reduced vanilla jump path"
        );
        helper.assertTrue(
                player.getDeltaMovement().y + 1.0E-6D >= unrestrictedJump,
                "A server-side player jump from sticky Gravesown terrain must clear one full block"
        );
        helper.assertTrue(
                !StickyMovementEvents.restoreUnrestrictedJump(player),
                "The jump correction must stop once the full unrestricted impulse has been restored"
        );
        helper.succeed();
    }

    private static void assertMineableWith(
            GameTestHelper helper,
            BlockState state,
            net.minecraft.tags.TagKey<Block> tag
    ) {
        helper.assertTrue(state.is(tag), BuiltInRegistries.BLOCK.getKey(state.getBlock()) + " has the wrong mining tag");
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
