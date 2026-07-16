package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.block.GravesownPlantBlock;
import dev.gravesown.block.GraveworkBenchBlock;
import dev.gravesown.block.GloamFarmlandBlock;
import dev.gravesown.block.FieldKitchenBlock;
import dev.gravesown.block.FallingLeafBlock;
import dev.gravesown.block.GravesownCropBlock;
import dev.gravesown.block.PitchKilnBlock;
import dev.gravesown.block.RemnantGraveBlock;
import dev.gravesown.block.ReliquaryCrateBlock;
import dev.gravesown.block.ThreadkelpBlock;
import dev.gravesown.block.SawmillBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Gravesown.MOD_ID);

    public static final DeferredBlock<Block> ASHEN_SOD = BLOCKS.registerSimpleBlock(
            "ashen_sod",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.65F)
                    .sound(SoundType.GRASS)
    );
    public static final DeferredBlock<Block> GRAVE_LOAM = BLOCKS.registerSimpleBlock(
            "grave_loam",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.55F)
                    .sound(SoundType.GRAVEL)
    );
    public static final DeferredBlock<Block> HUSHSTONE = BLOCKS.registerSimpleBlock(
            "hushstone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.8F, 7.0F)
                    .sound(SoundType.STONE)
    );
    public static final DeferredBlock<Block> DEEP_HUSHSTONE = BLOCKS.registerSimpleBlock(
            "deep_hushstone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(3.2F, 9.0F)
                    .sound(SoundType.DEEPSLATE)
    );
    public static final DeferredBlock<Block> GRAVEBED = BLOCKS.registerSimpleBlock(
            "gravebed",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(-1.0F, 3_600_000.0F)
                    .sound(SoundType.DEEPSLATE)
                    .noLootTable()
    );
    public static final DeferredBlock<Block> ROOTFELT = BLOCKS.registerSimpleBlock(
            "rootfelt",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_PURPLE)
                    .strength(0.75F)
                    .sound(SoundType.ROOTED_DIRT)
    );
    public static final DeferredBlock<Block> FIBROUS_LOAM = BLOCKS.registerSimpleBlock(
            "fibrous_loam",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.8F)
                    .sound(SoundType.ROOTED_DIRT)
    );
    public static final DeferredBlock<Block> SCAR_SHALE = BLOCKS.registerSimpleBlock(
            "scar_shale",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.7F, 7.0F)
                    .sound(SoundType.TUFF)
    );
    public static final DeferredBlock<Block> MARROWSTONE = BLOCKS.registerSimpleBlock(
            "marrowstone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.35F, 8.0F)
                    .sound(SoundType.CALCITE)
    );
    public static final DeferredBlock<Block> SUTURE_SILT = BLOCKS.registerSimpleBlock(
            "suture_silt",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.6F)
                    .speedFactor(0.88F)
                    .sound(SoundType.MUD)
    );
    public static final DeferredBlock<Block> DRIED_ICHOR = BLOCKS.registerSimpleBlock(
            "dried_ichor",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(1.0F)
                    .speedFactor(0.65F)
                    .jumpFactor(0.85F)
                    .sound(SoundType.HONEY_BLOCK)
    );
    public static final DeferredBlock<Block> ABYSSAL_SILT = BLOCKS.registerSimpleBlock(
            "abyssal_silt",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.7F)
                    .speedFactor(0.82F)
                    .sound(SoundType.MUD)
    );
    public static final DeferredBlock<Block> BRINEBONE = BLOCKS.registerSimpleBlock(
            "brinebone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.1F, 7.5F)
                    .sound(SoundType.CALCITE)
    );
    public static final DeferredBlock<Block> GLOAM_MUCK = BLOCKS.registerSimpleBlock(
            "gloam_muck",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_GREEN)
                    .strength(0.55F)
                    .speedFactor(0.78F)
                    .sound(SoundType.MUD)
    );
    public static final DeferredBlock<Block> GLOAM_SAND = BLOCKS.registerSimpleBlock(
            "gloam_sand",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(0.55F)
                    .sound(SoundType.SAND)
    );
    public static final DeferredBlock<Block> VEINED_SHALE = BLOCKS.registerSimpleBlock(
            "veined_shale",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.4F, 7.5F)
                    .sound(SoundType.DEEPSLATE)
    );
    public static final DeferredBlock<Block> SPLINTERED_MARROWSTONE = BLOCKS.registerSimpleBlock(
            "splintered_marrowstone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.9F, 6.0F)
                    .sound(SoundType.CALCITE)
    );
    public static final DeferredBlock<Block> CAIRNSTONE = BLOCKS.registerSimpleBlock(
            "cairnstone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.8F, 8.5F)
                    .sound(SoundType.TUFF)
    );
    public static final DeferredBlock<SawmillBlock> SAWMILL = BLOCKS.register(
            "sawmill",
            () -> new SawmillBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.5F, 4.0F)
                            .sound(SoundType.WOOD)
                            .ignitedByLava()
            )
    );
    public static final DeferredBlock<Block> RIBROOT_CUT_PLANKS = registerPlanks(
            "ribroot_cut_planks", MapColor.COLOR_BROWN
    );
    public static final DeferredBlock<Block> EMBERBARK_CUT_PLANKS = registerPlanks(
            "emberbark_cut_planks", MapColor.COLOR_ORANGE
    );
    public static final DeferredBlock<Block> PALEVINE_CUT_PLANKS = registerPlanks(
            "palevine_cut_planks", MapColor.SAND
    );
    public static final DeferredBlock<TransparentBlock> GRAVESOWN_GLASS = registerGlass(
            "gravesown_glass", 0.3F, 1.5F
    );
    public static final DeferredBlock<TransparentBlock> TEMPERED_GLASS = registerGlass(
            "tempered_glass", 0.9F, 4.5F
    );
    public static final DeferredBlock<GraveworkBenchBlock> GRAVEWORK_BENCH = BLOCKS.register(
            "gravework_bench",
            () -> new GraveworkBenchBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_PURPLE)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.4F, 4.0F)
                            .sound(SoundType.WOOD)
            )
    );
    public static final DeferredBlock<PitchKilnBlock> PITCH_KILN = BLOCKS.register(
            "pitch_kiln",
            () -> new PitchKilnBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .strength(3.0F, 8.0F)
                            .lightLevel(state -> state.getValue(PitchKilnBlock.LIT) ? 9 : 0)
                            .sound(SoundType.DEEPSLATE)
            )
    );
    public static final DeferredBlock<ReliquaryCrateBlock> RELIQUARY_CRATE = BLOCKS.register(
            "reliquary_crate",
            () -> new ReliquaryCrateBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_PURPLE)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.7F, 4.5F)
                            .sound(SoundType.WOOD)
            )
    );
    public static final DeferredBlock<FieldKitchenBlock> FIELD_KITCHEN = BLOCKS.register(
            "field_kitchen",
            () -> new FieldKitchenBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_BROWN)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.8F, 5.0F)
                            .sound(SoundType.WOOD)
            )
    );
    public static final DeferredBlock<RemnantGraveBlock> REMNANT_GRAVE = BLOCKS.register(
            "remnant_grave",
            () -> new RemnantGraveBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_GRAY)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .strength(2.6F, 8.0F)
                            .sound(SoundType.DEEPSLATE)
                            .noLootTable()
                            .noOcclusion()
            )
    );
    public static final DeferredBlock<LiquidBlock> GLOAMWATER = BLOCKS.register(
            "gloamwater",
            () -> new LiquidBlock(
                    ModFluids.GLOAMWATER.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .replaceable()
                            .noCollission()
                            .strength(100.0F)
                            .noLootTable()
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final DeferredBlock<RotatedPillarBlock> RIBROOT_STEM = BLOCKS.register(
            "ribroot_stem",
            () -> new RotatedPillarBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y
                                    ? MapColor.TERRACOTTA_PURPLE
                                    : MapColor.COLOR_BROWN)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.0F)
                            .sound(SoundType.WOOD)
                            .ignitedByLava()
            )
    );
    public static final DeferredBlock<Block> RIBROOT_PLANKS = BLOCKS.registerSimpleBlock(
            "ribroot_planks",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_PURPLE)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
    );
    public static final DeferredBlock<StairBlock> RIBROOT_STAIRS = BLOCKS.register(
            "ribroot_stairs",
            () -> new StairBlock(RIBROOT_PLANKS.get().defaultBlockState(), ribrootWoodProperties())
    );
    public static final DeferredBlock<SlabBlock> RIBROOT_SLAB = BLOCKS.register(
            "ribroot_slab",
            () -> new SlabBlock(ribrootWoodProperties())
    );
    public static final DeferredBlock<FenceBlock> RIBROOT_FENCE = BLOCKS.register(
            "ribroot_fence",
            () -> new FenceBlock(ribrootWoodProperties())
    );
    public static final DeferredBlock<FenceGateBlock> RIBROOT_FENCE_GATE = BLOCKS.register(
            "ribroot_fence_gate",
            () -> new FenceGateBlock(WoodType.DARK_OAK, ribrootWoodProperties())
    );
    public static final DeferredBlock<DoorBlock> RIBROOT_DOOR = BLOCKS.register(
            "ribroot_door",
            () -> new DoorBlock(BlockSetType.DARK_OAK, ribrootWoodProperties().noOcclusion())
    );
    public static final DeferredBlock<TrapDoorBlock> RIBROOT_TRAPDOOR = BLOCKS.register(
            "ribroot_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.DARK_OAK, ribrootWoodProperties().noOcclusion())
    );
    public static final DeferredBlock<RotatedPillarBlock> EMBERBARK_STEM = registerStem(
            "emberbark_stem", MapColor.COLOR_ORANGE, MapColor.TERRACOTTA_BROWN
    );
    public static final DeferredBlock<Block> EMBERBARK_PLANKS = registerPlanks(
            "emberbark_planks", MapColor.COLOR_ORANGE
    );
    public static final DeferredBlock<StairBlock> EMBERBARK_STAIRS = BLOCKS.register(
            "emberbark_stairs",
            () -> new StairBlock(EMBERBARK_PLANKS.get().defaultBlockState(), woodProperties(MapColor.COLOR_ORANGE))
    );
    public static final DeferredBlock<SlabBlock> EMBERBARK_SLAB = BLOCKS.register(
            "emberbark_slab", () -> new SlabBlock(woodProperties(MapColor.COLOR_ORANGE))
    );
    public static final DeferredBlock<FenceBlock> EMBERBARK_FENCE = BLOCKS.register(
            "emberbark_fence", () -> new FenceBlock(woodProperties(MapColor.COLOR_ORANGE))
    );
    public static final DeferredBlock<FenceGateBlock> EMBERBARK_FENCE_GATE = BLOCKS.register(
            "emberbark_fence_gate",
            () -> new FenceGateBlock(WoodType.ACACIA, woodProperties(MapColor.COLOR_ORANGE))
    );
    public static final DeferredBlock<DoorBlock> EMBERBARK_DOOR = BLOCKS.register(
            "emberbark_door",
            () -> new DoorBlock(BlockSetType.ACACIA, woodProperties(MapColor.COLOR_ORANGE).noOcclusion())
    );
    public static final DeferredBlock<TrapDoorBlock> EMBERBARK_TRAPDOOR = BLOCKS.register(
            "emberbark_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.ACACIA, woodProperties(MapColor.COLOR_ORANGE).noOcclusion())
    );
    public static final DeferredBlock<LeavesBlock> EMBERBARK_FOLIAGE = registerFoliage(
            "emberbark_foliage", MapColor.TERRACOTTA_ORANGE
    );
    public static final DeferredBlock<GravesownPlantBlock> EMBERBARK_SHOOT = registerPlant(
            "emberbark_shoot", plantProperties(MapColor.TERRACOTTA_ORANGE)
    );

    public static final DeferredBlock<RotatedPillarBlock> PALEVINE_STEM = registerStem(
            "palevine_stem", MapColor.SAND, MapColor.QUARTZ
    );
    public static final DeferredBlock<Block> PALEVINE_PLANKS = registerPlanks(
            "palevine_planks", MapColor.SAND
    );
    public static final DeferredBlock<StairBlock> PALEVINE_STAIRS = BLOCKS.register(
            "palevine_stairs",
            () -> new StairBlock(PALEVINE_PLANKS.get().defaultBlockState(), woodProperties(MapColor.SAND))
    );
    public static final DeferredBlock<SlabBlock> PALEVINE_SLAB = BLOCKS.register(
            "palevine_slab", () -> new SlabBlock(woodProperties(MapColor.SAND))
    );
    public static final DeferredBlock<FenceBlock> PALEVINE_FENCE = BLOCKS.register(
            "palevine_fence", () -> new FenceBlock(woodProperties(MapColor.SAND))
    );
    public static final DeferredBlock<FenceGateBlock> PALEVINE_FENCE_GATE = BLOCKS.register(
            "palevine_fence_gate",
            () -> new FenceGateBlock(WoodType.CHERRY, woodProperties(MapColor.SAND))
    );
    public static final DeferredBlock<DoorBlock> PALEVINE_DOOR = BLOCKS.register(
            "palevine_door",
            () -> new DoorBlock(BlockSetType.CHERRY, woodProperties(MapColor.SAND).noOcclusion())
    );
    public static final DeferredBlock<TrapDoorBlock> PALEVINE_TRAPDOOR = BLOCKS.register(
            "palevine_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.CHERRY, woodProperties(MapColor.SAND).noOcclusion())
    );
    public static final DeferredBlock<LeavesBlock> PALEVINE_FOLIAGE = registerFoliage(
            "palevine_foliage", MapColor.COLOR_LIGHT_GREEN
    );
    public static final DeferredBlock<GravesownPlantBlock> PALEVINE_SHOOT = registerPlant(
            "palevine_shoot", plantProperties(MapColor.COLOR_LIGHT_GREEN)
    );

    public static final WoodFamily CAIRNWOOD = registerWoodFamily(
            "cairnwood",
            MapColor.TERRACOTTA_GRAY,
            MapColor.STONE,
            MapColor.COLOR_BROWN,
            MapColor.TERRACOTTA_GREEN,
            WoodType.DARK_OAK,
            BlockSetType.DARK_OAK
    );
    public static final DeferredBlock<RotatedPillarBlock> CAIRNWOOD_STEM = CAIRNWOOD.stem();
    public static final DeferredBlock<Block> CAIRNWOOD_PLANKS = CAIRNWOOD.planks();
    public static final DeferredBlock<Block> CAIRNWOOD_CUT_PLANKS = CAIRNWOOD.cutPlanks();
    public static final DeferredBlock<StairBlock> CAIRNWOOD_STAIRS = CAIRNWOOD.stairs();
    public static final DeferredBlock<SlabBlock> CAIRNWOOD_SLAB = CAIRNWOOD.slab();
    public static final DeferredBlock<FenceBlock> CAIRNWOOD_FENCE = CAIRNWOOD.fence();
    public static final DeferredBlock<FenceGateBlock> CAIRNWOOD_FENCE_GATE = CAIRNWOOD.fenceGate();
    public static final DeferredBlock<DoorBlock> CAIRNWOOD_DOOR = CAIRNWOOD.door();
    public static final DeferredBlock<TrapDoorBlock> CAIRNWOOD_TRAPDOOR = CAIRNWOOD.trapdoor();
    public static final DeferredBlock<LeavesBlock> CAIRNWOOD_FOLIAGE = CAIRNWOOD.foliage();
    public static final DeferredBlock<GravesownPlantBlock> CAIRNWOOD_SHOOT = CAIRNWOOD.shoot();

    public static final WoodFamily SUTUREWOOD = registerWoodFamily(
            "suturewood",
            MapColor.TERRACOTTA_BROWN,
            MapColor.COLOR_BROWN,
            MapColor.TERRACOTTA_BROWN,
            MapColor.TERRACOTTA_GREEN,
            WoodType.MANGROVE,
            BlockSetType.MANGROVE
    );
    public static final DeferredBlock<RotatedPillarBlock> SUTUREWOOD_STEM = SUTUREWOOD.stem();
    public static final DeferredBlock<Block> SUTUREWOOD_PLANKS = SUTUREWOOD.planks();
    public static final DeferredBlock<Block> SUTUREWOOD_CUT_PLANKS = SUTUREWOOD.cutPlanks();
    public static final DeferredBlock<StairBlock> SUTUREWOOD_STAIRS = SUTUREWOOD.stairs();
    public static final DeferredBlock<SlabBlock> SUTUREWOOD_SLAB = SUTUREWOOD.slab();
    public static final DeferredBlock<FenceBlock> SUTUREWOOD_FENCE = SUTUREWOOD.fence();
    public static final DeferredBlock<FenceGateBlock> SUTUREWOOD_FENCE_GATE = SUTUREWOOD.fenceGate();
    public static final DeferredBlock<DoorBlock> SUTUREWOOD_DOOR = SUTUREWOOD.door();
    public static final DeferredBlock<TrapDoorBlock> SUTUREWOOD_TRAPDOOR = SUTUREWOOD.trapdoor();
    public static final DeferredBlock<LeavesBlock> SUTUREWOOD_FOLIAGE = SUTUREWOOD.foliage();
    public static final DeferredBlock<GravesownPlantBlock> SUTUREWOOD_SHOOT = SUTUREWOOD.shoot();

    public static final WoodFamily MOSSWAKE = registerWoodFamily(
            "mosswake",
            MapColor.COLOR_BROWN,
            MapColor.TERRACOTTA_BROWN,
            MapColor.COLOR_GREEN,
            MapColor.COLOR_GREEN,
            WoodType.OAK,
            BlockSetType.OAK
    );
    public static final DeferredBlock<RotatedPillarBlock> MOSSWAKE_STEM = MOSSWAKE.stem();
    public static final DeferredBlock<Block> MOSSWAKE_PLANKS = MOSSWAKE.planks();
    public static final DeferredBlock<Block> MOSSWAKE_CUT_PLANKS = MOSSWAKE.cutPlanks();
    public static final DeferredBlock<StairBlock> MOSSWAKE_STAIRS = MOSSWAKE.stairs();
    public static final DeferredBlock<SlabBlock> MOSSWAKE_SLAB = MOSSWAKE.slab();
    public static final DeferredBlock<FenceBlock> MOSSWAKE_FENCE = MOSSWAKE.fence();
    public static final DeferredBlock<FenceGateBlock> MOSSWAKE_FENCE_GATE = MOSSWAKE.fenceGate();
    public static final DeferredBlock<DoorBlock> MOSSWAKE_DOOR = MOSSWAKE.door();
    public static final DeferredBlock<TrapDoorBlock> MOSSWAKE_TRAPDOOR = MOSSWAKE.trapdoor();
    public static final DeferredBlock<LeavesBlock> MOSSWAKE_FOLIAGE = MOSSWAKE.foliage();
    public static final DeferredBlock<GravesownPlantBlock> MOSSWAKE_SHOOT = MOSSWAKE.shoot();

    public static final WoodFamily SUNVEIL = registerWoodFamily(
            "sunveil",
            MapColor.COLOR_YELLOW,
            MapColor.TERRACOTTA_YELLOW,
            MapColor.SAND,
            MapColor.TERRACOTTA_YELLOW,
            WoodType.BIRCH,
            BlockSetType.BIRCH
    );
    public static final DeferredBlock<RotatedPillarBlock> SUNVEIL_STEM = SUNVEIL.stem();
    public static final DeferredBlock<Block> SUNVEIL_PLANKS = SUNVEIL.planks();
    public static final DeferredBlock<Block> SUNVEIL_CUT_PLANKS = SUNVEIL.cutPlanks();
    public static final DeferredBlock<StairBlock> SUNVEIL_STAIRS = SUNVEIL.stairs();
    public static final DeferredBlock<SlabBlock> SUNVEIL_SLAB = SUNVEIL.slab();
    public static final DeferredBlock<FenceBlock> SUNVEIL_FENCE = SUNVEIL.fence();
    public static final DeferredBlock<FenceGateBlock> SUNVEIL_FENCE_GATE = SUNVEIL.fenceGate();
    public static final DeferredBlock<DoorBlock> SUNVEIL_DOOR = SUNVEIL.door();
    public static final DeferredBlock<TrapDoorBlock> SUNVEIL_TRAPDOOR = SUNVEIL.trapdoor();
    public static final DeferredBlock<LeavesBlock> SUNVEIL_FOLIAGE = SUNVEIL.foliage();
    public static final DeferredBlock<GravesownPlantBlock> SUNVEIL_SHOOT = SUNVEIL.shoot();
    public static final DeferredBlock<Block> TALLOW_LANTERN = BLOCKS.registerSimpleBlock(
            "tallow_lantern",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.7F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 13)
                    .noOcclusion()
    );
    public static final DeferredBlock<GloamFarmlandBlock> GLOAM_FARMLAND = BLOCKS.register(
            "gloam_farmland",
            () -> new GloamFarmlandBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .randomTicks()
                            .strength(0.6F)
                            .sound(SoundType.GRAVEL)
            )
    );
    public static final DeferredBlock<GravesownCropBlock> ASHGRAIN_CROP = BLOCKS.register(
            "ashgrain_crop",
            () -> new GravesownCropBlock(cropProperties(), () -> ModItems.ASHGRAIN_SEEDS.get())
    );
    public static final DeferredBlock<GravesownCropBlock> MIREBEAN_CROP = BLOCKS.register(
            "mirebean_crop",
            () -> new GravesownCropBlock(cropProperties(), () -> ModItems.MIREBEAN_SEEDS.get())
    );
    public static final DeferredBlock<LeavesBlock> VEIL_FOLIAGE = BLOCKS.register(
            "veil_foliage",
            () -> new FallingLeafBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_GREEN)
                            .strength(0.2F)
                            .randomTicks()
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isValidSpawn((state, level, pos, entityType) -> false)
                            .isSuffocating((state, level, pos) -> false)
                            .isViewBlocking((state, level, pos) -> false)
                            .isRedstoneConductor((state, level, pos) -> false)
                            .ignitedByLava()
                            .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final DeferredBlock<GravesownPlantBlock> THREADGRASS = registerPlant(
            "threadgrass",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_GREEN)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XYZ)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<GravesownPlantBlock> RIBROOT_SHOOT = registerPlant(
            "ribroot_shoot",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_PURPLE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<GravesownPlantBlock> PALLID_BULB = registerPlant(
            "pallid_bulb",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .lightLevel(state -> 3)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<GravesownPlantBlock> CINDER_BLOOM = registerPlant(
            "cinder_bloom",
            plantProperties(MapColor.COLOR_ORANGE).lightLevel(state -> 2)
    );
    public static final DeferredBlock<GravesownPlantBlock> RIFT_THORN = registerPlant(
            "rift_thorn", plantProperties(MapColor.TERRACOTTA_GRAY)
    );
    public static final DeferredBlock<GravesownPlantBlock> MIRE_FROND = registerPlant(
            "mire_frond", plantProperties(MapColor.TERRACOTTA_GREEN)
    );
    public static final DeferredBlock<GravesownPlantBlock> MOSSVEIL = registerPlant(
            "mossveil", plantProperties(MapColor.COLOR_GREEN)
    );
    public static final DeferredBlock<GravesownPlantBlock> AMBER_BLOOM = registerPlant(
            "amber_bloom", plantProperties(MapColor.COLOR_YELLOW).lightLevel(state -> 2)
    );
    public static final DeferredBlock<GravesownPlantBlock> SINEW_FERN = registerPlant(
            "sinew_fern",
            plantProperties(MapColor.TERRACOTTA_GREEN)
    );
    public static final DeferredBlock<GravesownPlantBlock> MARROW_REED = registerPlant(
            "marrow_reed",
            plantProperties(MapColor.SAND)
    );
    public static final DeferredBlock<ThreadkelpBlock> THREADKELP = BLOCKS.register(
            "threadkelp",
            () -> new ThreadkelpBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_GREEN)
                            .replaceable()
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
                            .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final DeferredBlock<ThreadkelpBlock> VEILWEED = registerGloamwaterPlant("veilweed");
    public static final DeferredBlock<ThreadkelpBlock> DROWNED_ROOTS = registerGloamwaterPlant("drowned_roots");
    public static final DeferredBlock<ThreadkelpBlock> BLADDERPOD = registerGloamwaterPlant("bladderpod");
    public static final DeferredBlock<ThreadkelpBlock> LUMEN_KELP = BLOCKS.register(
            "lumen_kelp",
            () -> new ThreadkelpBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GREEN)
                            .replaceable()
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
                            .lightLevel(state -> 6)
                            .pushReaction(PushReaction.DESTROY)
            )
    );

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    private static DeferredBlock<GravesownPlantBlock> registerPlant(
            String id,
            BlockBehaviour.Properties properties
    ) {
        return BLOCKS.register(id, () -> new GravesownPlantBlock(properties));
    }

    private static DeferredBlock<ThreadkelpBlock> registerGloamwaterPlant(String id) {
        return BLOCKS.register(
                id,
                () -> new ThreadkelpBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.TERRACOTTA_GREEN)
                                .replaceable()
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.WET_GRASS)
                                .offsetType(BlockBehaviour.OffsetType.XZ)
                                .pushReaction(PushReaction.DESTROY)
                )
        );
    }

    private static DeferredBlock<RotatedPillarBlock> registerStem(
            String id, MapColor verticalColor, MapColor endColor
    ) {
        return BLOCKS.register(
                id,
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y
                                        ? verticalColor
                                        : endColor)
                                .instrument(NoteBlockInstrument.BASS)
                                .strength(2.0F)
                                .sound(SoundType.WOOD)
                                .ignitedByLava()
                )
        );
    }

    private static DeferredBlock<Block> registerPlanks(String id, MapColor color) {
        return BLOCKS.registerSimpleBlock(id, woodProperties(color));
    }

    private static WoodFamily registerWoodFamily(
            String prefix,
            MapColor verticalStemColor,
            MapColor stemEndColor,
            MapColor woodColor,
            MapColor foliageColor,
            WoodType woodType,
            BlockSetType blockSetType
    ) {
        DeferredBlock<RotatedPillarBlock> stem = registerStem(
                prefix + "_stem", verticalStemColor, stemEndColor
        );
        DeferredBlock<Block> planks = registerPlanks(prefix + "_planks", woodColor);
        DeferredBlock<Block> cutPlanks = registerPlanks(prefix + "_cut_planks", woodColor);
        DeferredBlock<StairBlock> stairs = BLOCKS.register(
                prefix + "_stairs", () -> new StairBlock(planks.get().defaultBlockState(), woodProperties(woodColor))
        );
        DeferredBlock<SlabBlock> slab = BLOCKS.register(
                prefix + "_slab", () -> new SlabBlock(woodProperties(woodColor))
        );
        DeferredBlock<FenceBlock> fence = BLOCKS.register(
                prefix + "_fence", () -> new FenceBlock(woodProperties(woodColor))
        );
        DeferredBlock<FenceGateBlock> fenceGate = BLOCKS.register(
                prefix + "_fence_gate", () -> new FenceGateBlock(woodType, woodProperties(woodColor))
        );
        DeferredBlock<DoorBlock> door = BLOCKS.register(
                prefix + "_door", () -> new DoorBlock(blockSetType, woodProperties(woodColor).noOcclusion())
        );
        DeferredBlock<TrapDoorBlock> trapdoor = BLOCKS.register(
                prefix + "_trapdoor", () -> new TrapDoorBlock(blockSetType, woodProperties(woodColor).noOcclusion())
        );
        DeferredBlock<LeavesBlock> foliage = registerFoliage(prefix + "_foliage", foliageColor);
        DeferredBlock<GravesownPlantBlock> shoot = registerPlant(
                prefix + "_shoot", plantProperties(foliageColor)
        );
        return new WoodFamily(stem, planks, cutPlanks, stairs, slab, fence, fenceGate, door, trapdoor, foliage, shoot);
    }

    private static DeferredBlock<TransparentBlock> registerGlass(String id, float hardness, float resistance) {
        return BLOCKS.register(
                id,
                () -> new TransparentBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                .instrument(NoteBlockInstrument.HAT)
                                .strength(hardness, resistance)
                                .sound(SoundType.GLASS)
                                .noOcclusion()
                                .isValidSpawn((state, level, pos, entityType) -> false)
                                .isSuffocating((state, level, pos) -> false)
                                .isViewBlocking((state, level, pos) -> false)
                                .isRedstoneConductor((state, level, pos) -> false)
                )
        );
    }

    private static DeferredBlock<LeavesBlock> registerFoliage(String id, MapColor color) {
        return BLOCKS.register(
                id,
                () -> new FallingLeafBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(color)
                                .strength(0.2F)
                                .randomTicks()
                                .sound(SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, level, pos, entityType) -> false)
                                .isSuffocating((state, level, pos) -> false)
                                .isViewBlocking((state, level, pos) -> false)
                                .isRedstoneConductor((state, level, pos) -> false)
                                .ignitedByLava()
                                .pushReaction(PushReaction.DESTROY)
                )
        );
    }

    private static BlockBehaviour.Properties plantProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .replaceable()
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY);
    }

    public record WoodFamily(
            DeferredBlock<RotatedPillarBlock> stem,
            DeferredBlock<Block> planks,
            DeferredBlock<Block> cutPlanks,
            DeferredBlock<StairBlock> stairs,
            DeferredBlock<SlabBlock> slab,
            DeferredBlock<FenceBlock> fence,
            DeferredBlock<FenceGateBlock> fenceGate,
            DeferredBlock<DoorBlock> door,
            DeferredBlock<TrapDoorBlock> trapdoor,
            DeferredBlock<LeavesBlock> foliage,
            DeferredBlock<GravesownPlantBlock> shoot
    ) {
    }

    private static BlockBehaviour.Properties ribrootWoodProperties() {
        return woodProperties(MapColor.TERRACOTTA_PURPLE);
    }

    private static BlockBehaviour.Properties woodProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_GREEN)
                .randomTicks()
                .noCollission()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY);
    }
}
