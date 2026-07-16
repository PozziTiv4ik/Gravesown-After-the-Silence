package dev.gravesown.block;

import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModFluids;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.CommonHooks;

/** Farmland that accepts only Gravesown's Gloamwater and never vanilla water or rain. */
public final class GloamFarmlandBlock extends FarmBlock {
    public GloamFarmlandBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? super.getStateForPlacement(context)
                : ModBlocks.GRAVE_LOAM.get().defaultBlockState();
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToGraveLoam(null, state, level, pos);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);
        if (isNearGloamwater(level, pos)) {
            if (moisture < MAX_MOISTURE) {
                level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), Block.UPDATE_CLIENTS);
            }
        } else if (moisture > 0) {
            level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), Block.UPDATE_CLIENTS);
        } else if (!maintainsFarmland(level, pos)) {
            turnToGraveLoam(null, state, level, pos);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide
                && CommonHooks.onFarmlandTrample(
                        level, pos, ModBlocks.GRAVE_LOAM.get().defaultBlockState(), fallDistance, entity)) {
            turnToGraveLoam(entity, state, level, pos);
        }
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    @Override
    public boolean canBeHydrated(
            BlockState state, BlockGetter level, BlockPos pos, FluidState fluid, BlockPos fluidPos
    ) {
        return ModFluids.GLOAMWATER.get().isSame(fluid.getType());
    }

    private static boolean isNearGloamwater(BlockGetter level, BlockPos pos) {
        for (BlockPos fluidPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (ModFluids.GLOAMWATER.get().isSame(level.getFluidState(fluidPos).getType())) {
                return true;
            }
        }
        return false;
    }

    private static boolean maintainsFarmland(BlockGetter level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(BlockTags.MAINTAINS_FARMLAND) || above.getBlock() instanceof CropBlock;
    }

    private static void turnToGraveLoam(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState replacement = pushEntitiesUp(
                state, ModBlocks.GRAVE_LOAM.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, replacement);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, replacement));
    }
}
