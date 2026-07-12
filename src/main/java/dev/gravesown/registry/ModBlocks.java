package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.block.GravesownPlantBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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
    public static final DeferredBlock<LeavesBlock> VEIL_FOLIAGE = BLOCKS.register(
            "veil_foliage",
            () -> new LeavesBlock(
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
}
