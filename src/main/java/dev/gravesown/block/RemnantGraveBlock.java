package dev.gravesown.block;

import com.mojang.serialization.MapCodec;
import dev.gravesown.entity.BuriedRemnant;
import dev.gravesown.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A persistent, one-shot grave marker. Its blockstate is the complete saved
 * interaction state, so opening a grave needs no ticking block entity.
 */
public final class RemnantGraveBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<RemnantGraveBlock> CODEC = simpleCodec(RemnantGraveBlock::new);
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");

    private static final VoxelShape NORTH_SOUTH_SHAPE = Shapes.or(
            Block.box(2.0D, 0.0D, 3.0D, 14.0D, 3.0D, 13.0D),
            Block.box(3.0D, 3.0D, 9.0D, 13.0D, 15.0D, 13.0D)
    );
    private static final VoxelShape EAST_WEST_SHAPE = Shapes.or(
            Block.box(3.0D, 0.0D, 2.0D, 13.0D, 3.0D, 14.0D),
            Block.box(3.0D, 3.0D, 3.0D, 7.0D, 15.0D, 13.0D)
    );

    public RemnantGraveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (state.getValue(OPENED)) {
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel)level;
        BuriedRemnant remnant = ModEntities.BURIED_REMNANT.get().create(serverLevel);
        if (remnant == null) {
            return InteractionResult.FAIL;
        }

        Direction facing = state.getValue(FACING);
        BlockPos front = pos.relative(facing);
        BlockPos emergencePos = new BlockPos(
                front.getX(),
                serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, front.getX(), front.getZ()),
                front.getZ()
        );
        double emergenceY = emergencePos.getY();
        remnant.moveTo(
                emergencePos.getX() + 0.5D,
                emergenceY,
                emergencePos.getZ() + 0.5D,
                facing.toYRot() + 180.0F,
                0.0F
        );
        remnant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(emergencePos), MobSpawnType.EVENT, null);
        remnant.beginEmergence(emergenceY);
        remnant.setPersistenceRequired();

        if (!serverLevel.addFreshEntity(remnant)) {
            return InteractionResult.FAIL;
        }

        serverLevel.setBlock(pos, state.setValue(OPENED, true), Block.UPDATE_ALL);
        serverLevel.playSound(null, pos, SoundEvents.ROOTED_DIRT_BREAK, remnant.getSoundSource(), 0.9F, 0.65F);
        serverLevel.levelEvent(2001, emergencePos, Block.getId(serverLevel.getBlockState(emergencePos.below())));
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
