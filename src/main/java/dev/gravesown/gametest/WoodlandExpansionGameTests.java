package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.block.FallingLeafBlock;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModFeatures;
import dev.gravesown.registry.ModItems;
import dev.gravesown.worldgen.GravesownTreeFeature;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WoodlandExpansionGameTests {
    private static final TagKey<Block> CUT_PLANK_BLOCKS = blockTag(Gravesown.MOD_ID, "cut_planks");
    private static final TagKey<Item> CUT_PLANK_ITEMS = itemTag(Gravesown.MOD_ID, "cut_planks");

    private WoodlandExpansionGameTests() {
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 40)
    public static void everyNewTimberFamilyIsCompleteAndPlaceable(GameTestHelper helper) {
        List<FamilyCase> families = List.of(
                new FamilyCase("ribroot", ModBlocks.RIBROOT_STEM.get(), ModBlocks.VEIL_FOLIAGE.get(),
                        ModItems.RIBROOT_CUT_PLANKS.get(), GravesownTreeFeature.Shape.RIBROOT_FORKED,
                        ModFeatures.RIBROOT_TREE.get()),
                new FamilyCase("emberbark", ModBlocks.EMBERBARK_STEM.get(), ModBlocks.EMBERBARK_FOLIAGE.get(),
                        ModItems.EMBERBARK_CUT_PLANKS.get(), GravesownTreeFeature.Shape.EMBERBARK_WINDSWEPT,
                        ModFeatures.EMBERBARK_TREE.get()),
                new FamilyCase("palevine", ModBlocks.PALEVINE_STEM.get(), ModBlocks.PALEVINE_FOLIAGE.get(),
                        ModItems.PALEVINE_CUT_PLANKS.get(), GravesownTreeFeature.Shape.PALEVINE_TALL,
                        ModFeatures.PALEVINE_TREE.get()),
                treeCase("cairnwood", ModBlocks.CAIRNWOOD, ModItems.CAIRNWOOD,
                        GravesownTreeFeature.Shape.CAIRNWOOD_SHRUB, ModFeatures.CAIRNWOOD_TREE.get()),
                treeCase("suturewood", ModBlocks.SUTUREWOOD, ModItems.SUTUREWOOD,
                        GravesownTreeFeature.Shape.SUTUREWOOD_ROOTED, ModFeatures.SUTUREWOOD_TREE.get()),
                treeCase("mosswake", ModBlocks.MOSSWAKE, ModItems.MOSSWAKE,
                        GravesownTreeFeature.Shape.MOSSWAKE_BROAD, ModFeatures.MOSSWAKE_TREE.get()),
                treeCase("sunveil", ModBlocks.SUNVEIL, ModItems.SUNVEIL,
                        GravesownTreeFeature.Shape.SUNVEIL_CROWN, ModFeatures.SUNVEIL_TREE.get())
        );
        BlockPos relativeBase = new BlockPos(5, 1, 5);
        BlockPos base = helper.absolutePos(relativeBase);

        for (int index = 0; index < families.size(); index++) {
            FamilyCase family = families.get(index);
            clearTreeVolume(helper, relativeBase);
            helper.setBlock(relativeBase.below(), ModBlocks.ASHEN_SOD.get());

            helper.assertTrue(
                    BuiltInRegistries.BLOCK.getKey(family.stem()).equals(Gravesown.id(family.id() + "_stem")),
                    family.id() + " stem registry id must remain stable"
            );
            helper.assertTrue(
                    BuiltInRegistries.ITEM.getKey(family.cutPlanks())
                            .equals(Gravesown.id(family.id() + "_cut_planks")),
                    family.id() + " cut planks must have a matching item"
            );
            helper.assertTrue(
                    family.foliage() instanceof FallingLeafBlock,
                    family.id() + " foliage must inherit the automatic species-specific leaf drift rule"
            );
            helper.assertTrue(
                    BuiltInRegistries.FEATURE.getKey(family.feature()).equals(Gravesown.id(family.id() + "_tree")),
                    family.id() + " tree feature registry id must remain stable"
            );

            boolean placed = family.feature().place(new FeaturePlaceContext<>(
                    Optional.empty(),
                    helper.getLevel(),
                    helper.getLevel().getChunkSource().getGenerator(),
                    RandomSource.create(91_337L + index),
                    base,
                    NoneFeatureConfiguration.INSTANCE
            ));
            helper.assertTrue(placed, family.id() + " tree must place on Ashen Sod in a clear bounded volume");
            helper.assertBlockPresent(family.stem(), relativeBase);
            helper.assertTrue(
                    countBlock(helper, relativeBase, family.foliage()) >= 8,
                    family.id() + " tree must have a readable foliage mass"
            );
            if (family.shape() == GravesownTreeFeature.Shape.SUTUREWOOD_ROOTED) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    helper.assertBlockPresent(family.stem(), relativeBase.relative(direction, 2));
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 60)
    public static void everyTreeFamilyVariesItsNaturalSilhouetteAcrossSeeds(GameTestHelper helper) {
        List<FamilyCase> families = List.of(
                new FamilyCase("ribroot", ModBlocks.RIBROOT_STEM.get(), ModBlocks.VEIL_FOLIAGE.get(),
                        ModItems.RIBROOT_CUT_PLANKS.get(), GravesownTreeFeature.Shape.RIBROOT_FORKED,
                        ModFeatures.RIBROOT_TREE.get()),
                new FamilyCase("emberbark", ModBlocks.EMBERBARK_STEM.get(), ModBlocks.EMBERBARK_FOLIAGE.get(),
                        ModItems.EMBERBARK_CUT_PLANKS.get(), GravesownTreeFeature.Shape.EMBERBARK_WINDSWEPT,
                        ModFeatures.EMBERBARK_TREE.get()),
                new FamilyCase("palevine", ModBlocks.PALEVINE_STEM.get(), ModBlocks.PALEVINE_FOLIAGE.get(),
                        ModItems.PALEVINE_CUT_PLANKS.get(), GravesownTreeFeature.Shape.PALEVINE_TALL,
                        ModFeatures.PALEVINE_TREE.get()),
                treeCase("cairnwood", ModBlocks.CAIRNWOOD, ModItems.CAIRNWOOD,
                        GravesownTreeFeature.Shape.CAIRNWOOD_SHRUB, ModFeatures.CAIRNWOOD_TREE.get()),
                treeCase("suturewood", ModBlocks.SUTUREWOOD, ModItems.SUTUREWOOD,
                        GravesownTreeFeature.Shape.SUTUREWOOD_ROOTED, ModFeatures.SUTUREWOOD_TREE.get()),
                treeCase("mosswake", ModBlocks.MOSSWAKE, ModItems.MOSSWAKE,
                        GravesownTreeFeature.Shape.MOSSWAKE_BROAD, ModFeatures.MOSSWAKE_TREE.get()),
                treeCase("sunveil", ModBlocks.SUNVEIL, ModItems.SUNVEIL,
                        GravesownTreeFeature.Shape.SUNVEIL_CROWN, ModFeatures.SUNVEIL_TREE.get())
        );
        BlockPos relativeBase = new BlockPos(5, 1, 5);
        BlockPos base = helper.absolutePos(relativeBase);

        for (int familyIndex = 0; familyIndex < families.size(); familyIndex++) {
            FamilyCase family = families.get(familyIndex);
            Set<Set<String>> silhouettes = new HashSet<>();
            for (int seedIndex = 0; seedIndex < 4; seedIndex++) {
                clearVariationTreeVolume(helper, relativeBase);
                helper.setBlock(relativeBase.below(), ModBlocks.ASHEN_SOD.get());
                long seed = 17_171L + familyIndex * 1_009L + seedIndex * 97L;
                boolean placed = family.feature().place(new FeaturePlaceContext<>(
                        Optional.empty(), helper.getLevel(), helper.getLevel().getChunkSource().getGenerator(),
                        RandomSource.create(seed), base, NoneFeatureConfiguration.INSTANCE
                ));
                helper.assertTrue(placed, family.id() + " variation seed must place successfully");
                silhouettes.add(treeSilhouette(helper, relativeBase, family.stem(), family.foliage()));
            }
            helper.assertTrue(silhouettes.size() >= 2,
                    family.id() + " must produce more than one natural silhouette across fixed seeds");
        }
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 50)
    public static void allSevenWoodFamiliesKeepCompleteRegistryTagAndLootContracts(GameTestHelper helper) {
        for (WoodFamilyContract family : allWoodFamilies()) {
            helper.assertTrue(family.blocks().size() == 11 && family.items().size() == 11,
                    family.id() + " must expose all eleven reviewed family members");

            for (int index = 0; index < family.blocks().size(); index++) {
                Block block = family.blocks().get(index);
                Item item = family.items().get(index);
                String componentId = index == 9
                        ? family.foliageId()
                        : family.id() + FAMILY_SUFFIXES.get(index);
                ResourceLocation expectedId = Gravesown.id(componentId);
                helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block).equals(expectedId),
                        componentId + " block registry id must remain stable");
                helper.assertTrue(BuiltInRegistries.ITEM.getKey(item).equals(expectedId),
                        componentId + " item registry id must remain stable");
                helper.assertTrue(item instanceof BlockItem blockItem && blockItem.getBlock() == block,
                        componentId + " item must place its matching block");
                if (index <= 8) {
                    helper.assertTrue(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE),
                            componentId + " must be axe-mineable");
                }
                assertSingleDrop(helper, block, item, ItemStack.EMPTY);
            }

            Block stem = family.blocks().get(0);
            Item stemItem = family.items().get(0);
            TagKey<Block> familyLogs = blockTag(Gravesown.MOD_ID, family.id() + "_logs");
            TagKey<Item> familyLogItems = itemTag(Gravesown.MOD_ID, family.id() + "_logs");
            helper.assertTrue(stem.defaultBlockState().is(BlockTags.LOGS)
                            && stem.defaultBlockState().is(BlockTags.LOGS_THAT_BURN)
                            && stem.defaultBlockState().is(familyLogs),
                    family.id() + " stem must belong to common and family log tags");
            helper.assertTrue(new ItemStack(stemItem).is(ItemTags.LOGS)
                            && new ItemStack(stemItem).is(ItemTags.LOGS_THAT_BURN)
                            && new ItemStack(stemItem).is(familyLogItems),
                    family.id() + " stem item must belong to common and family log tags");

            for (int index : List.of(1, 2)) {
                helper.assertTrue(family.blocks().get(index).defaultBlockState().is(BlockTags.PLANKS),
                        family.id() + FAMILY_SUFFIXES.get(index) + " must be a common plank");
                helper.assertTrue(new ItemStack(family.items().get(index)).is(ItemTags.PLANKS),
                        family.id() + FAMILY_SUFFIXES.get(index) + " item must be a common plank");
            }
            helper.assertTrue(family.blocks().get(2).defaultBlockState().is(CUT_PLANK_BLOCKS)
                            && new ItemStack(family.items().get(2)).is(CUT_PLANK_ITEMS),
                    family.id() + " cut planks must belong to both dedicated cut-plank tags");

            Block foliage = family.blocks().get(9);
            helper.assertTrue(foliage instanceof FallingLeafBlock,
                    family.foliageId() + " must inherit species-specific falling leaves");
            helper.assertTrue(foliage.defaultBlockState().is(BlockTags.LEAVES)
                            && new ItemStack(family.items().get(9)).is(ItemTags.LEAVES),
                    family.foliageId() + " must belong to common leaf tags");
            helper.assertTrue(family.blocks().get(10).defaultBlockState().is(BlockTags.SAPLINGS)
                            && new ItemStack(family.items().get(10)).is(ItemTags.SAPLINGS),
                    family.id() + " shoot must belong to common sapling tags");
        }
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void regionalStonesAndPlantsKeepStableSurvivalContracts(GameTestHelper helper) {
        List<StoneContract> stones = List.of(
                new StoneContract("veined_shale", ModBlocks.VEINED_SHALE.get(), ModItems.VEINED_SHALE.get()),
                new StoneContract("splintered_marrowstone", ModBlocks.SPLINTERED_MARROWSTONE.get(),
                        ModItems.SPLINTERED_MARROWSTONE.get()),
                new StoneContract("cairnstone", ModBlocks.CAIRNSTONE.get(), ModItems.CAIRNSTONE.get())
        );
        ItemStack woodenPickaxe = new ItemStack(Items.WOODEN_PICKAXE);
        for (StoneContract stone : stones) {
            assertRegistryPair(helper, stone.id(), stone.block(), stone.item());
            helper.assertTrue(stone.block().defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE),
                    stone.id() + " must be pickaxe-mineable");
            helper.assertTrue(woodenPickaxe.isCorrectToolForDrops(stone.block().defaultBlockState()),
                    stone.id() + " must remain harvestable at the first reviewed pickaxe tier");
            assertSingleDrop(helper, stone.block(), stone.item(), woodenPickaxe);
        }

        List<PlantContract> plants = List.of(
                new PlantContract("rift_thorn", ModBlocks.RIFT_THORN.get(), ModItems.RIFT_THORN.get(),
                        ModBlocks.SCAR_SHALE.get()),
                new PlantContract("mire_frond", ModBlocks.MIRE_FROND.get(), ModItems.MIRE_FROND.get(),
                        ModBlocks.SUTURE_SILT.get()),
                new PlantContract("mossveil", ModBlocks.MOSSVEIL.get(), ModItems.MOSSVEIL.get(),
                        ModBlocks.ASHEN_SOD.get()),
                new PlantContract("amber_bloom", ModBlocks.AMBER_BLOOM.get(), ModItems.AMBER_BLOOM.get(),
                        ModBlocks.ASHEN_SOD.get())
        );
        for (int index = 0; index < plants.size(); index++) {
            PlantContract plant = plants.get(index);
            assertRegistryPair(helper, plant.id(), plant.block(), plant.item());
            BlockPos support = new BlockPos(1 + index * 2, 1, 1);
            helper.setBlock(support, plant.support());
            helper.assertTrue(plant.block().defaultBlockState()
                            .canSurvive(helper.getLevel(), helper.absolutePos(support.above())),
                    plant.id() + " must survive on its reviewed regional surface");
            helper.setBlock(support, Blocks.DIRT);
            helper.assertTrue(!plant.block().defaultBlockState()
                            .canSurvive(helper.getLevel(), helper.absolutePos(support.above())),
                    plant.id() + " must reject vanilla dirt");
            helper.assertTrue(plant.block().defaultBlockState().is(BlockTags.REPLACEABLE_BY_TREES),
                    plant.id() + " must not obstruct reviewed tree generation");
            helper.assertTrue(plant.block().defaultBlockState()
                            .getCollisionShape(helper.getLevel(), helper.absolutePos(support.above())).isEmpty(),
                    plant.id() + " must remain non-colliding vegetation");
            assertSingleDrop(helper, plant.block(), plant.item(), ItemStack.EMPTY);
        }
        helper.succeed();
    }

    private static void clearTreeVolume(GameTestHelper helper, BlockPos base) {
        for (int y = 0; y <= 12; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    helper.setBlock(base.offset(x, y, z), Blocks.AIR);
                }
            }
        }
    }

    private static void clearVariationTreeVolume(GameTestHelper helper, BlockPos base) {
        for (int y = 0; y <= 16; y++) {
            for (int x = -7; x <= 7; x++) {
                for (int z = -7; z <= 7; z++) {
                    helper.setBlock(base.offset(x, y, z), Blocks.AIR);
                }
            }
        }
    }

    private static int countBlock(GameTestHelper helper, BlockPos base, Block block) {
        int found = 0;
        for (int y = 0; y <= 12; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (helper.getBlockState(base.offset(x, y, z)).is(block)) {
                        found++;
                    }
                }
            }
        }
        return found;
    }

    private static Set<String> treeSilhouette(GameTestHelper helper, BlockPos base, Block stem, Block foliage) {
        Set<String> signature = new HashSet<>();
        for (int y = 0; y <= 12; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    Block block = helper.getBlockState(base.offset(x, y, z)).getBlock();
                    if (block == stem || block == foliage) {
                        signature.add(x + ":" + y + ":" + z + ":" + (block == stem ? "s" : "f"));
                    }
                }
            }
        }
        return Set.copyOf(signature);
    }

    private static void assertRegistryPair(GameTestHelper helper, String id, Block block, Item item) {
        ResourceLocation expectedId = Gravesown.id(id);
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block).equals(expectedId)
                        && BuiltInRegistries.ITEM.getKey(item).equals(expectedId),
                id + " block and item registry ids must remain stable");
        helper.assertTrue(item instanceof BlockItem blockItem && blockItem.getBlock() == block,
                id + " item must place its matching block");
    }

    private static void assertSingleDrop(
            GameTestHelper helper,
            Block block,
            Item expected,
            ItemStack tool
    ) {
        LootTable table = helper.getLevel().getServer().reloadableRegistries().getLootTable(block.getLootTable());
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                .withParameter(LootContextParams.TOOL, tool)
                .create(LootContextParamSets.BLOCK);
        List<ItemStack> drops = table.getRandomItems(params, 1L);
        helper.assertTrue(drops.size() == 1 && drops.getFirst().is(expected) && drops.getFirst().getCount() == 1,
                BuiltInRegistries.BLOCK.getKey(block) + " must drop exactly one matching block item");
    }

    private static TagKey<Block> blockTag(String namespace, String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static final List<String> FAMILY_SUFFIXES = List.of(
            "_stem", "_planks", "_cut_planks", "_stairs", "_slab", "_fence", "_fence_gate",
            "_door", "_trapdoor", "_foliage", "_shoot"
    );

    private static List<WoodFamilyContract> allWoodFamilies() {
        return List.of(
                family("ribroot", "veil_foliage",
                        List.of(ModBlocks.RIBROOT_STEM.get(), ModBlocks.RIBROOT_PLANKS.get(),
                                ModBlocks.RIBROOT_CUT_PLANKS.get(), ModBlocks.RIBROOT_STAIRS.get(),
                                ModBlocks.RIBROOT_SLAB.get(), ModBlocks.RIBROOT_FENCE.get(),
                                ModBlocks.RIBROOT_FENCE_GATE.get(), ModBlocks.RIBROOT_DOOR.get(),
                                ModBlocks.RIBROOT_TRAPDOOR.get(), ModBlocks.VEIL_FOLIAGE.get(),
                                ModBlocks.RIBROOT_SHOOT.get()),
                        List.of(ModItems.RIBROOT_STEM.get(), ModItems.RIBROOT_PLANKS.get(),
                                ModItems.RIBROOT_CUT_PLANKS.get(), ModItems.RIBROOT_STAIRS.get(),
                                ModItems.RIBROOT_SLAB.get(), ModItems.RIBROOT_FENCE.get(),
                                ModItems.RIBROOT_FENCE_GATE.get(), ModItems.RIBROOT_DOOR.get(),
                                ModItems.RIBROOT_TRAPDOOR.get(), ModItems.VEIL_FOLIAGE.get(),
                                ModItems.RIBROOT_SHOOT.get())),
                family("emberbark", "emberbark_foliage",
                        List.of(ModBlocks.EMBERBARK_STEM.get(), ModBlocks.EMBERBARK_PLANKS.get(),
                                ModBlocks.EMBERBARK_CUT_PLANKS.get(), ModBlocks.EMBERBARK_STAIRS.get(),
                                ModBlocks.EMBERBARK_SLAB.get(), ModBlocks.EMBERBARK_FENCE.get(),
                                ModBlocks.EMBERBARK_FENCE_GATE.get(), ModBlocks.EMBERBARK_DOOR.get(),
                                ModBlocks.EMBERBARK_TRAPDOOR.get(), ModBlocks.EMBERBARK_FOLIAGE.get(),
                                ModBlocks.EMBERBARK_SHOOT.get()),
                        List.of(ModItems.EMBERBARK_STEM.get(), ModItems.EMBERBARK_PLANKS.get(),
                                ModItems.EMBERBARK_CUT_PLANKS.get(), ModItems.EMBERBARK_STAIRS.get(),
                                ModItems.EMBERBARK_SLAB.get(), ModItems.EMBERBARK_FENCE.get(),
                                ModItems.EMBERBARK_FENCE_GATE.get(), ModItems.EMBERBARK_DOOR.get(),
                                ModItems.EMBERBARK_TRAPDOOR.get(), ModItems.EMBERBARK_FOLIAGE.get(),
                                ModItems.EMBERBARK_SHOOT.get())),
                family("palevine", "palevine_foliage",
                        List.of(ModBlocks.PALEVINE_STEM.get(), ModBlocks.PALEVINE_PLANKS.get(),
                                ModBlocks.PALEVINE_CUT_PLANKS.get(), ModBlocks.PALEVINE_STAIRS.get(),
                                ModBlocks.PALEVINE_SLAB.get(), ModBlocks.PALEVINE_FENCE.get(),
                                ModBlocks.PALEVINE_FENCE_GATE.get(), ModBlocks.PALEVINE_DOOR.get(),
                                ModBlocks.PALEVINE_TRAPDOOR.get(), ModBlocks.PALEVINE_FOLIAGE.get(),
                                ModBlocks.PALEVINE_SHOOT.get()),
                        List.of(ModItems.PALEVINE_STEM.get(), ModItems.PALEVINE_PLANKS.get(),
                                ModItems.PALEVINE_CUT_PLANKS.get(), ModItems.PALEVINE_STAIRS.get(),
                                ModItems.PALEVINE_SLAB.get(), ModItems.PALEVINE_FENCE.get(),
                                ModItems.PALEVINE_FENCE_GATE.get(), ModItems.PALEVINE_DOOR.get(),
                                ModItems.PALEVINE_TRAPDOOR.get(), ModItems.PALEVINE_FOLIAGE.get(),
                                ModItems.PALEVINE_SHOOT.get())),
                family("cairnwood", ModBlocks.CAIRNWOOD, ModItems.CAIRNWOOD),
                family("suturewood", ModBlocks.SUTUREWOOD, ModItems.SUTUREWOOD),
                family("mosswake", ModBlocks.MOSSWAKE, ModItems.MOSSWAKE),
                family("sunveil", ModBlocks.SUNVEIL, ModItems.SUNVEIL)
        );
    }

    private static FamilyCase treeCase(
            String id,
            ModBlocks.WoodFamily blocks,
            ModItems.WoodFamilyItems items,
            GravesownTreeFeature.Shape shape,
            GravesownTreeFeature feature
    ) {
        return new FamilyCase(
                id, blocks.stem().get(), blocks.foliage().get(), items.cutPlanks().get(), shape, feature
        );
    }

    private static WoodFamilyContract family(
            String id,
            ModBlocks.WoodFamily blocks,
            ModItems.WoodFamilyItems items
    ) {
        return family(id, id + "_foliage",
                List.of(blocks.stem().get(), blocks.planks().get(), blocks.cutPlanks().get(),
                        blocks.stairs().get(), blocks.slab().get(), blocks.fence().get(), blocks.fenceGate().get(),
                        blocks.door().get(), blocks.trapdoor().get(), blocks.foliage().get(), blocks.shoot().get()),
                List.of(items.stem().get(), items.planks().get(), items.cutPlanks().get(), items.stairs().get(),
                        items.slab().get(), items.fence().get(), items.fenceGate().get(), items.door().get(),
                        items.trapdoor().get(), items.foliage().get(), items.shoot().get()));
    }

    private static WoodFamilyContract family(
            String id,
            String foliageId,
            List<Block> blocks,
            List<Item> items
    ) {
        return new WoodFamilyContract(id, foliageId, blocks, items);
    }

    private record FamilyCase(
            String id,
            Block stem,
            Block foliage,
            Item cutPlanks,
            GravesownTreeFeature.Shape shape,
            GravesownTreeFeature feature
    ) {
    }

    private record WoodFamilyContract(String id, String foliageId, List<Block> blocks, List<Item> items) {
    }

    private record StoneContract(String id, Block block, Item item) {
    }

    private record PlantContract(String id, Block block, Item item, Block support) {
    }
}
