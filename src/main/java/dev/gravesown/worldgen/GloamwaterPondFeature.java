package dev.gravesown.worldgen;

import dev.gravesown.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Carves one small sealed Mire basin without introducing vanilla terrain or fluid. */
public final class GloamwaterPondFeature extends Feature<NoneFeatureConfiguration> {
    private static final int RADIUS = 4;
    public GloamwaterPondFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        List<Column> columns = new ArrayList<>();
        int minSurfaceY = Integer.MAX_VALUE;
        int maxSurfaceY = Integer.MIN_VALUE;

        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                if (!insideBasin(dx, dz)) {
                    continue;
                }
                int surfaceY = level.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        origin.getX() + dx,
                        origin.getZ() + dz
                );
                BlockPos groundPos = new BlockPos(origin.getX() + dx, surfaceY - 1, origin.getZ() + dz);
                if (!isAllowedGround(level.getBlockState(groundPos))) {
                    return false;
                }
                minSurfaceY = Math.min(minSurfaceY, surfaceY);
                maxSurfaceY = Math.max(maxSurfaceY, surfaceY);
                columns.add(new Column(dx, dz));
            }
        }

        if (columns.size() < 28 || maxSurfaceY - minSurfaceY > 2) {
            return false;
        }

        return carveBasin(level, origin, columns, minSurfaceY, maxSurfaceY);
    }

    /** Deterministic GameTest seam that exercises the production carver after a reviewed flat-bed setup. */
    public boolean placeReviewedBasinForTest(WorldGenLevel level, BlockPos origin, int surfaceY) {
        List<Column> columns = new ArrayList<>();
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                if (insideBasin(dx, dz)) {
                    columns.add(new Column(dx, dz));
                }
            }
        }
        return carveBasin(level, origin, columns, surfaceY, surfaceY);
    }

    private static boolean carveBasin(
            WorldGenLevel level,
            BlockPos origin,
            List<Column> columns,
            int minSurfaceY,
            int maxSurfaceY
    ) {
        int waterY = minSurfaceY - 1;
        BlockState fluid = ModBlocks.GLOAMWATER.get().defaultBlockState();
        BlockState muck = ModBlocks.GLOAM_MUCK.get().defaultBlockState();
        BlockState sand = ModBlocks.GLOAM_SAND.get().defaultBlockState();

        for (Column column : columns) {
            int distanceSquared = column.dx * column.dx + column.dz * column.dz;
            int depth = distanceSquared <= 8 ? 2 : 1;
            int bottomY = waterY - depth;
            int x = origin.getX() + column.dx;
            int z = origin.getZ() + column.dz;

            for (int y = bottomY; y <= maxSurfaceY + 1; y++) {
                BlockPos pos = new BlockPos(x, y, z);
                if (y == bottomY) {
                    level.setBlock(pos, depth == 1 ? sand : muck, Block.UPDATE_CLIENTS);
                }
                else if (y <= waterY) {
                    level.setBlock(pos, fluid, Block.UPDATE_CLIENTS);
                }
                else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }

        }

        // A narrow dry rim makes the pond readable from land and prevents the
        // water edge from inheriting a random biome surface block.
        for (int dz = -RADIUS - 1; dz <= RADIUS + 1; dz++) {
            for (int dx = -RADIUS - 1; dx <= RADIUS + 1; dx++) {
                if (insideBasin(dx, dz) || !touchesBasin(dx, dz)) {
                    continue;
                }
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                BlockPos shore = new BlockPos(x, surfaceY, z);
                if (isAllowedGround(level.getBlockState(shore))) {
                    level.setBlock(shore, sand, Block.UPDATE_CLIENTS);
                }
            }
        }

        // Vegetation is deliberately left to GloamSeaGrowthFeature, which runs
        // immediately after this feature in the Mire decoration list. Keeping one
        // chunk-aligned density authority prevents small ponds from receiving this
        // feature's old dense pass plus the universal water-body pass.
        return true;
    }

    private static boolean insideBasin(int dx, int dz) {
        int stretchedDistance = dx * dx * 3 + dz * dz * 4;
        int roughness = Mth.abs(dx * 17 + dz * 31 + dx * dz * 7) % 19;
        return stretchedDistance <= 82 + roughness;
    }

    private static boolean isAllowedGround(BlockState state) {
        Block block = state.getBlock();
        return block == ModBlocks.SUTURE_SILT.get()
                || block == ModBlocks.DRIED_ICHOR.get()
                || block == ModBlocks.GRAVE_LOAM.get()
                || block == ModBlocks.HUSHSTONE.get()
                || block == ModBlocks.GLOAM_MUCK.get()
                || block == ModBlocks.GLOAM_SAND.get();
    }

    private static boolean touchesBasin(int dx, int dz) {
        return insideBasin(dx - 1, dz)
                || insideBasin(dx + 1, dz)
                || insideBasin(dx, dz - 1)
                || insideBasin(dx, dz + 1);
    }

    private record Column(int dx, int dz) {
    }
}
