package dev.gravesown.block;

import com.mojang.serialization.MapCodec;
import dev.gravesown.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A non-growing bootstrap plant that can only survive on Gravesown surface soil.
 * Tree growth is deliberately deferred until the Ribroot configured feature exists.
 */
public final class GravesownPlantBlock extends BushBlock {
    public static final MapCodec<GravesownPlantBlock> CODEC = simpleCodec(GravesownPlantBlock::new);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    public GravesownPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GravesownPlantBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.ASHEN_SOD.get())
                || state.is(ModBlocks.GRAVE_LOAM.get())
                || state.is(ModBlocks.ROOTFELT.get())
                || state.is(ModBlocks.FIBROUS_LOAM.get())
                || state.is(ModBlocks.SCAR_SHALE.get())
                || state.is(ModBlocks.SUTURE_SILT.get())
                || state.is(ModBlocks.DRIED_ICHOR.get())
                || state.is(ModBlocks.MARROWSTONE.get())
                || state.is(ModBlocks.GLOAM_SAND.get());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(level, pos);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }
}
