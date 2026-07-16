package dev.gravesown.worldgen;

import dev.gravesown.block.RemnantGraveBlock;
import dev.gravesown.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Places a compact, terrain-aware marker without templates or foreign blocks. */
public final class RemnantGraveFeature extends Feature<NoneFeatureConfiguration> {
    private static final int RADIUS = 2;

    public RemnantGraveFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int centerY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        BlockPos center = new BlockPos(origin.getX(), centerY, origin.getZ());

        if (!hasStableFootprint(level, center)) {
            return false;
        }

        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState grave = ModBlocks.REMNANT_GRAVE.get().defaultBlockState()
                .setValue(RemnantGraveBlock.FACING, facing)
                .setValue(RemnantGraveBlock.OPENED, false);

        // A shallow asymmetrical cairn keeps the structure readable without
        // flattening the surrounding Gravesown terrain.
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int surfaceY = level.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        center.getX() + dx,
                        center.getZ() + dz
                );
                BlockPos surface = new BlockPos(center.getX() + dx, surfaceY - 1, center.getZ() + dz);
                if (Math.abs(dx) + Math.abs(dz) <= 2 && random.nextFloat() < 0.74F) {
                    this.setBlock(level, surface, randomMoundState(random));
                }
            }
        }

        BlockPos gravePos = new BlockPos(center.getX(), centerY, center.getZ());
        this.setBlock(level, gravePos.below(), ModBlocks.GRAVE_LOAM.get().defaultBlockState());
        this.setBlock(level, gravePos, grave);

        placeMarkerStone(level, gravePos.offset(2, 0, 1), random);
        placeMarkerStone(level, gravePos.offset(-2, 0, -1), random);
        placeMarkerStone(level, gravePos.offset(1, 0, -2), random);
        return true;
    }

    private static boolean hasStableFootprint(WorldGenLevel level, BlockPos center) {
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        Set<Block> allowedGround = Set.of(
                ModBlocks.ASHEN_SOD.get(),
                ModBlocks.GRAVE_LOAM.get(),
                ModBlocks.ROOTFELT.get(),
                ModBlocks.FIBROUS_LOAM.get(),
                ModBlocks.SCAR_SHALE.get(),
                ModBlocks.MARROWSTONE.get(),
                ModBlocks.SUTURE_SILT.get(),
                ModBlocks.DRIED_ICHOR.get()
        );

        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int y = level.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        center.getX() + dx,
                        center.getZ() + dz
                );
                BlockState ground = level.getBlockState(new BlockPos(center.getX() + dx, y - 1, center.getZ() + dz));
                if (!allowedGround.contains(ground.getBlock())) {
                    return false;
                }
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }
        return maxY - minY <= 1;
    }

    private static BlockState randomMoundState(RandomSource random) {
        int choice = random.nextInt(5);
        if (choice == 0) {
            return ModBlocks.MARROWSTONE.get().defaultBlockState();
        }
        if (choice == 1) {
            return ModBlocks.SCAR_SHALE.get().defaultBlockState();
        }
        if (choice == 2) {
            return ModBlocks.HUSHSTONE.get().defaultBlockState();
        }
        return ModBlocks.GRAVE_LOAM.get().defaultBlockState();
    }

    private void placeMarkerStone(WorldGenLevel level, BlockPos approximatePos, RandomSource random) {
        int y = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                approximatePos.getX(),
                approximatePos.getZ()
        );
        BlockPos pos = new BlockPos(approximatePos.getX(), y, approximatePos.getZ());
        if (level.getBlockState(pos).isAir()) {
            this.setBlock(level, pos, random.nextBoolean()
                    ? ModBlocks.HUSHSTONE.get().defaultBlockState()
                    : ModBlocks.MARROWSTONE.get().defaultBlockState());
        }
    }
}
