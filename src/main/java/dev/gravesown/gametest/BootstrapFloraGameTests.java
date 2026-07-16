package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import dev.gravesown.item.GravebloomDustItem;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
public final class BootstrapFloraGameTests {
    private static final TagKey<Block> RIBROOT_LOGS = BlockTags.create(Gravesown.id("ribroot_logs"));
    private static final TagKey<Block> BOOTSTRAP_PLANTS = BlockTags.create(Gravesown.id("bootstrap_plants"));
    private static final TagKey<Item> RIBROOT_LOG_ITEMS = ItemTags.create(Gravesown.id("ribroot_logs"));
    private static final TagKey<Item> BOOTSTRAP_PLANT_ITEMS = ItemTags.create(Gravesown.id("bootstrap_plants"));

    private BootstrapFloraGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void bootstrapFloraHasStableIdsItemsAndBlockBehavior(GameTestHelper helper) {
        Block[] blocks = floraBlocks();
        Item[] items = floraItems();
        String[] ids = {
                "ribroot_stem",
                "ribroot_planks",
                "veil_foliage",
                "threadgrass",
                "ribroot_shoot",
                "pallid_bulb",
                "cinder_bloom",
                "sinew_fern",
                "marrow_reed"
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
        }

        BlockPos stemPos = new BlockPos(1, 1, 1);
        BlockState sidewaysStem = ModBlocks.RIBROOT_STEM.get()
                .defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
        helper.setBlock(stemPos, sidewaysStem);
        helper.assertTrue(
                helper.getBlockState(stemPos).getValue(RotatedPillarBlock.AXIS) == Direction.Axis.X,
                "Ribroot Stem must preserve its placement axis"
        );
        helper.assertTrue(
                ModBlocks.THREADGRASS.get()
                        .defaultBlockState()
                        .getCollisionShape(helper.getLevel(), helper.absolutePos(new BlockPos(2, 2, 2)))
                        .isEmpty(),
                "Threadgrass must not have collision"
        );
        helper.assertTrue(
                ModBlocks.PALLID_BULB.get()
                        .defaultBlockState()
                        .getLightEmission(helper.getLevel(), helper.absolutePos(new BlockPos(3, 2, 2))) == 3,
                "Pallid Bulb must emit only dim atmospheric light"
        );
        BlockPos foliagePos = helper.absolutePos(new BlockPos(4, 2, 2));
        BlockState foliage = ModBlocks.VEIL_FOLIAGE.get().defaultBlockState();
        helper.assertTrue(
                !foliage.isSuffocating(helper.getLevel(), foliagePos)
                        && !foliage.isViewBlocking(helper.getLevel(), foliagePos)
                        && !foliage.isRedstoneConductor(helper.getLevel(), foliagePos),
                "Veil Foliage must behave as breathable, non-conductive foliage"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void bootstrapPlantsOnlySurviveOnGravesownSoil(GameTestHelper helper) {
        Block[] plants = {
                ModBlocks.THREADGRASS.get(),
                ModBlocks.RIBROOT_SHOOT.get(),
                ModBlocks.PALLID_BULB.get(),
                ModBlocks.CINDER_BLOOM.get(),
                ModBlocks.SINEW_FERN.get(),
                ModBlocks.MARROW_REED.get()
        };
        Block[] validSoils = {ModBlocks.ASHEN_SOD.get(), ModBlocks.GRAVE_LOAM.get()};

        for (int index = 0; index < plants.length; index++) {
            Block plant = plants[index];
            for (int soilIndex = 0; soilIndex < validSoils.length; soilIndex++) {
                BlockPos soilPos = new BlockPos(1 + index * 2, 1, 1 + soilIndex * 2);
                BlockPos plantPos = soilPos.above();
                helper.setBlock(soilPos, validSoils[soilIndex]);
                helper.assertTrue(
                        plant.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                        BuiltInRegistries.BLOCK.getKey(plant) + " must survive on "
                                + BuiltInRegistries.BLOCK.getKey(validSoils[soilIndex])
                );
            }

            BlockPos vanillaSoilPos = new BlockPos(1 + index * 2, 1, 5);
            helper.setBlock(vanillaSoilPos, Blocks.DIRT);
            helper.assertTrue(
                    !plant.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(vanillaSoilPos.above())),
                    BuiltInRegistries.BLOCK.getKey(plant) + " must reject vanilla dirt"
            );
        }
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void gravebloomDustGrowsACompletePersistentRibroot(GameTestHelper helper) {
        BlockPos base = new BlockPos(4, 2, 4);
        helper.setBlock(base.below(), ModBlocks.ROOTFELT.get());
        helper.setBlock(base, ModBlocks.RIBROOT_SHOOT.get());
        helper.assertTrue(
                GravebloomDustItem.growRibroot(helper.getLevel(), helper.absolutePos(base)),
                "Gravebloom Dust must grow a Ribroot Shoot when there is clear space"
        );
        int stems = 0;
        for (int y = 0; y < 7; y++) {
            if (helper.getBlockState(base.above(y)).is(ModBlocks.RIBROOT_STEM.get())) {
                stems++;
            }
        }
        helper.assertTrue(stems >= 5, "The fertilizer-grown tree must have a readable trunk");
        helper.assertTrue(
                BuiltInRegistries.ITEM.getKey(ModItems.GRAVEBLOOM_DUST.get()).equals(Gravesown.id("gravebloom_dust")),
                "Gravebloom Dust registry id must remain stable"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void bootstrapFloraUsesExpectedTagsAndSelfDrops(GameTestHelper helper) {
        BlockState stem = ModBlocks.RIBROOT_STEM.get().defaultBlockState();
        BlockState planks = ModBlocks.RIBROOT_PLANKS.get().defaultBlockState();
        BlockState foliage = ModBlocks.VEIL_FOLIAGE.get().defaultBlockState();
        BlockState threadgrass = ModBlocks.THREADGRASS.get().defaultBlockState();
        BlockState shoot = ModBlocks.RIBROOT_SHOOT.get().defaultBlockState();
        BlockState bulb = ModBlocks.PALLID_BULB.get().defaultBlockState();

        helper.assertTrue(stem.is(BlockTags.LOGS), "Ribroot Stem must be a log");
        helper.assertTrue(stem.is(BlockTags.LOGS_THAT_BURN), "Ribroot Stem must be a burnable log");
        helper.assertTrue(stem.is(RIBROOT_LOGS), "Ribroot Stem must be in the family tag");
        helper.assertTrue(stem.is(BlockTags.MINEABLE_WITH_AXE), "Ribroot Stem must be axe-mineable");
        helper.assertTrue(planks.is(BlockTags.PLANKS), "Ribroot Planks must be planks");
        helper.assertTrue(planks.is(BlockTags.MINEABLE_WITH_AXE), "Ribroot Planks must be axe-mineable");
        helper.assertTrue(foliage.is(BlockTags.LEAVES), "Veil Foliage must be leaves");
        helper.assertTrue(foliage.is(BlockTags.MINEABLE_WITH_HOE), "Veil Foliage must be hoe-mineable");
        helper.assertTrue(threadgrass.is(BOOTSTRAP_PLANTS), "Threadgrass must be a bootstrap plant");
        helper.assertTrue(threadgrass.is(BlockTags.REPLACEABLE_BY_TREES), "Threadgrass must be tree-replaceable");
        helper.assertTrue(shoot.is(BOOTSTRAP_PLANTS), "Ribroot Shoot must be a bootstrap plant");
        helper.assertTrue(shoot.is(BlockTags.SAPLINGS), "Ribroot Shoot must be a sapling");
        helper.assertTrue(bulb.is(BOOTSTRAP_PLANTS), "Pallid Bulb must be a bootstrap plant");
        helper.assertTrue(bulb.is(BlockTags.FLOWERS), "Pallid Bulb must be a flower");

        helper.assertTrue(new ItemStack(ModItems.RIBROOT_STEM.get()).is(ItemTags.LOGS), "Stem item must be a log");
        helper.assertTrue(
                new ItemStack(ModItems.RIBROOT_STEM.get()).is(RIBROOT_LOG_ITEMS),
                "Stem item must be in the family tag"
        );
        helper.assertTrue(new ItemStack(ModItems.RIBROOT_PLANKS.get()).is(ItemTags.PLANKS), "Plank item tag missing");
        helper.assertTrue(new ItemStack(ModItems.VEIL_FOLIAGE.get()).is(ItemTags.LEAVES), "Leaf item tag missing");
        helper.assertTrue(new ItemStack(ModItems.RIBROOT_SHOOT.get()).is(ItemTags.SAPLINGS), "Shoot item tag missing");
        helper.assertTrue(new ItemStack(ModItems.PALLID_BULB.get()).is(ItemTags.FLOWERS), "Bulb item tag missing");
        helper.assertTrue(
                new ItemStack(ModItems.THREADGRASS.get()).is(BOOTSTRAP_PLANT_ITEMS),
                "Threadgrass item must be a bootstrap plant"
        );

        Block[] blocks = floraBlocks();
        Item[] items = floraItems();
        for (int index = 0; index < blocks.length; index++) {
            assertSingleDrop(helper, blocks[index], items[index]);
        }
        helper.succeed();
    }

    private static Block[] floraBlocks() {
        return new Block[]{
                ModBlocks.RIBROOT_STEM.get(),
                ModBlocks.RIBROOT_PLANKS.get(),
                ModBlocks.VEIL_FOLIAGE.get(),
                ModBlocks.THREADGRASS.get(),
                ModBlocks.RIBROOT_SHOOT.get(),
                ModBlocks.PALLID_BULB.get(),
                ModBlocks.CINDER_BLOOM.get(),
                ModBlocks.SINEW_FERN.get(),
                ModBlocks.MARROW_REED.get()
        };
    }

    private static Item[] floraItems() {
        return new Item[]{
                ModItems.RIBROOT_STEM.get(),
                ModItems.RIBROOT_PLANKS.get(),
                ModItems.VEIL_FOLIAGE.get(),
                ModItems.THREADGRASS.get(),
                ModItems.RIBROOT_SHOOT.get(),
                ModItems.PALLID_BULB.get(),
                ModItems.CINDER_BLOOM.get(),
                ModItems.SINEW_FERN.get(),
                ModItems.MARROW_REED.get()
        };
    }

    private static void assertSingleDrop(GameTestHelper helper, Block block, Item expectedItem) {
        LootTable table = helper.getLevel()
                .getServer()
                .reloadableRegistries()
                .getLootTable(block.getLootTable());
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.BLOCK);
        List<ItemStack> drops = table.getRandomItems(params, 1L);
        helper.assertTrue(
                drops.size() == 1 && drops.getFirst().is(expectedItem) && drops.getFirst().getCount() == 1,
                BuiltInRegistries.BLOCK.getKey(block) + " must drop exactly one matching block item"
        );
    }
}
