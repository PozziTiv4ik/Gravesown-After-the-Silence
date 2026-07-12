package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
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

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
