package dev.gravesown.block;

import com.mojang.serialization.MapCodec;
import dev.gravesown.blockentity.PitchKilnBlockEntity;
import dev.gravesown.registry.ModBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** First Gravesown processing station, backed by the vanilla smelting contract. */
public final class PitchKilnBlock extends AbstractFurnaceBlock {
    public static final MapCodec<PitchKilnBlock> CODEC = simpleCodec(PitchKilnBlock::new);

    public PitchKilnBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PitchKilnBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return createFurnaceTicker(level, blockEntityType, ModBlockEntities.PITCH_KILN.get());
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PitchKilnBlockEntity) {
            player.openMenu((MenuProvider)blockEntity);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY();
        double centerZ = pos.getZ() + 0.5D;
        if (random.nextDouble() < 0.12D) {
            level.playLocalSound(centerX, centerY, centerZ, SoundEvents.CAMPFIRE_CRACKLE,
                    SoundSource.BLOCKS, 0.8F, 0.72F, false);
        }
        Direction facing = state.getValue(FACING);
        double side = random.nextDouble() * 0.6D - 0.3D;
        double xOffset = facing.getAxis() == Direction.Axis.X ? facing.getStepX() * 0.52D : side;
        double zOffset = facing.getAxis() == Direction.Axis.Z ? facing.getStepZ() * 0.52D : side;
        level.addParticle(ParticleTypes.SMOKE, centerX + xOffset, centerY + 0.45D, centerZ + zOffset,
                0.0D, 0.015D, 0.0D);
    }
}
