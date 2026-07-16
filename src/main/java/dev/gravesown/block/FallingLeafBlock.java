package dev.gravesown.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Leaves with a cheap, presentation-only fall effect that automatically uses the
 * owning foliage block's texture. {@link Block#animateTick} is invoked only on the
 * client, so this creates no server tick cost or networking traffic.
 */
public final class FallingLeafBlock extends LeavesBlock {
    public FallingLeafBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(34) != 0 || !level.getBlockState(pos.below()).isAir()) {
            return;
        }

        double x = pos.getX() + 0.15D + random.nextDouble() * 0.70D;
        double y = pos.getY() - 0.04D;
        double z = pos.getZ() + 0.15D + random.nextDouble() * 0.70D;
        double driftX = (random.nextDouble() - 0.5D) * 0.018D;
        double driftZ = (random.nextDouble() - 0.5D) * 0.018D;
        level.addParticle(
                new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                x, y, z,
                driftX, -0.025D, driftZ
        );
    }
}
