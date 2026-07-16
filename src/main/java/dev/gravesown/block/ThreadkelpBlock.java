package dev.gravesown.block;

import com.mojang.serialization.MapCodec;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModFluids;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A rooted submerged plant that survives only in still Gloamwater. */
public final class ThreadkelpBlock extends BushBlock implements LiquidBlockContainer {
    public static final MapCodec<ThreadkelpBlock> CODEC = simpleCodec(ThreadkelpBlock::new);
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public ThreadkelpBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ThreadkelpBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.SUTURE_SILT.get())
                || state.is(ModBlocks.ABYSSAL_SILT.get())
                || state.is(ModBlocks.BRINEBONE.get())
                || state.is(ModBlocks.GLOAM_MUCK.get())
                || state.is(ModBlocks.GLOAM_SAND.get())
                || state.is(ModBlocks.HUSHSTONE.get());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos)
                && level.getFluidState(pos).isSource()
                && ModFluids.GLOAMWATER.get().isSame(level.getFluidState(pos).getType());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return fluid.isSource() && ModFluids.GLOAMWATER.get().isSame(fluid.getType())
                ? this.defaultBlockState()
                : null;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return ModFluids.GLOAMWATER.get().getSource(false);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (!updated.isAir()) {
            level.scheduleTick(pos, ModFluids.GLOAMWATER.get(), ModFluids.GLOAMWATER.get().getTickDelay(level));
        }
        return updated;
    }

    @Override
    public boolean canPlaceLiquid(
            @Nullable Player player,
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            Fluid fluid
    ) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}
