package dev.gravesown.worldgen;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Populates Gloam Sea beds without scanning chunks during ordinary gameplay. */
public final class GloamSeaGrowthFeature extends Feature<NoneFeatureConfiguration> {
    private static final int NOISE_UNIT = 1024;
    private static final long WARP_X_SALT = 0x6A09E667F3BCC909L;
    private static final long WARP_Z_SALT = 0xBB67AE8584CAA73BL;
    private static final long LARGE_PATCH_SALT = 0x3C6EF372FE94F82BL;
    private static final long MEDIUM_PATCH_SALT = 0xA54FF53A5F1D36F1L;
    private static final long EDGE_DETAIL_SALT = 0x510E527FADE682D1L;
    private static final TagKey<Block> GLOAMWATER_FLOOR_REPLACEABLE =
            BlockTags.create(Gravesown.id("gloamwater_floor_replaceable"));

    public GloamSeaGrowthFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int chunkMinX = SectionPos.blockToSectionCoord(origin.getX()) << 4;
        int chunkMinZ = SectionPos.blockToSectionCoord(origin.getZ()) << 4;
        return placeChunkGarden(level, random, chunkMinX, chunkMinZ, null) > 0;
    }

    /** Deterministic GameTest seam that exercises the production 4x4 coverage grid. */
    public int placeReviewedChunkGardenForTest(
            WorldGenLevel level,
            RandomSource random,
            BlockPos chunkOrigin,
            int reviewedFloorY
    ) {
        int chunkMinX = SectionPos.blockToSectionCoord(chunkOrigin.getX()) << 4;
        int chunkMinZ = SectionPos.blockToSectionCoord(chunkOrigin.getZ()) << 4;
        return placeChunkGarden(level, random, chunkMinX, chunkMinZ, reviewedFloorY);
    }

    private static int placeChunkGarden(
            WorldGenLevel level,
            RandomSource random,
            int chunkMinX,
            int chunkMinZ,
            Integer reviewedFloorY
    ) {
        int placed = 0;

        // Convert every natural Gloamwater column before planting. Previously the
        // feature accepted only four pre-existing marine blocks, so lakes over an
        // ordinary biome surface were silently skipped even though the placed
        // feature was present in that biome.
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                int x = chunkMinX + localX;
                int z = chunkMinZ + localZ;
                int floorY = findFloorY(level, x, z, reviewedFloorY);
                BlockPos groundPos = new BlockPos(x, floorY, z);
                BlockPos waterPos = groundPos.above();
                BlockState ground = level.getBlockState(groundPos);

                if (isGloamwater(level, waterPos)
                        && ground.is(GLOAMWATER_FLOOR_REPLACEABLE)
                        && !isReviewedAquaticFloor(ground)) {
                    level.setBlock(groundPos, pickBedMaterial(x, floorY, z), Block.UPDATE_CLIENTS);
                }
            }
        }

        // Cover the complete owning chunk from its true 16x16 origin. Each 4x4
        // cell probes all of its columns in a deterministic shuffled order instead
        // of gambling on one coordinate that may happen to be dry shoreline.
        for (int cellZ = 0; cellZ < 4; cellZ++) {
            for (int cellX = 0; cellX < 4; cellX++) {
                int start = random.nextInt(16);
                for (int attempt = 0; attempt < 16; attempt++) {
                    int index = (start + attempt * 5) & 15;
                    int x = chunkMinX + cellX * 4 + (index & 3);
                    int z = chunkMinZ + cellZ * 4 + (index >> 2);
                    int floorY = findFloorY(level, x, z, reviewedFloorY);
                    BlockPos groundPos = new BlockPos(x, floorY, z);
                    BlockPos plantPos = groundPos.above();
                    BlockState ground = level.getBlockState(groundPos);

                    if (!isReviewedAquaticFloor(ground)
                            || !level.getBlockState(plantPos).canBeReplaced()
                            || !level.getFluidState(plantPos).isSource()
                            || !ModFluids.GLOAMWATER.get().isSame(level.getFluidState(plantPos).getType())) {
                        continue;
                    }

                    BlockState growth = pickGrowth(random);
                    if (growth.canSurvive(level, plantPos)) {
                        level.setBlock(plantPos, growth, Block.UPDATE_CLIENTS);
                        placed++;
                        break;
                    }
                }
            }
        }

        return placed;
    }

    private static int findFloorY(WorldGenLevel level, int x, int z, Integer reviewedFloorY) {
        return reviewedFloorY != null
                ? reviewedFloorY
                : level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1;
    }

    private static boolean isGloamwater(WorldGenLevel level, BlockPos pos) {
        return ModFluids.GLOAMWATER.get().isSame(level.getFluidState(pos).getType());
    }

    private static boolean isReviewedAquaticFloor(BlockState state) {
        return state.is(ModBlocks.ABYSSAL_SILT.get())
                || state.is(ModBlocks.BRINEBONE.get())
                || state.is(ModBlocks.GLOAM_MUCK.get())
                || state.is(ModBlocks.GLOAM_SAND.get());
    }

    private static BlockState pickBedMaterial(int x, int y, int z) {
        // Coordinate-warped value noise makes broad, contiguous mud shelves with
        // smaller irregular edges. The bounded fixed-cost samples are independent
        // of chunk order and remain continuous across chunk borders.
        int warpX = (sampleValueNoise(x, z, 43, WARP_X_SALT) - NOISE_UNIT / 2) / 24;
        int warpZ = (sampleValueNoise(x + 137, z - 83, 47, WARP_Z_SALT) - NOISE_UNIT / 2) / 24;
        int largePatch = sampleValueNoise(x + warpX, z + warpZ, 31, LARGE_PATCH_SALT);
        int mediumPatch = sampleValueNoise(x - warpZ, z + warpX, 13, MEDIUM_PATCH_SALT);
        int edgeDetail = sampleValueNoise(
                x + Math.floorDiv(z, 3),
                z - Math.floorDiv(x, 4),
                7,
                EDGE_DETAIL_SALT
        );
        int materialScore = largePatch * 5 + mediumPatch * 3 + edgeDetail * 2;
        return materialScore < 4_950
                ? ModBlocks.GLOAM_MUCK.get().defaultBlockState()
                : ModBlocks.GLOAM_SAND.get().defaultBlockState();
    }

    /** Pure deterministic seam for validating the reviewed bed distribution. */
    public static boolean isReviewedMuckBedForTest(int x, int z) {
        return pickBedMaterial(x, 0, z).is(ModBlocks.GLOAM_MUCK.get());
    }

    private static int sampleValueNoise(int x, int z, int scale, long salt) {
        int gridX = Math.floorDiv(x, scale);
        int gridZ = Math.floorDiv(z, scale);
        int localX = Math.floorMod(x, scale);
        int localZ = Math.floorMod(z, scale);
        int smoothX = smoothStep(localX, scale);
        int smoothZ = smoothStep(localZ, scale);
        int north = lerpFixed(
                hashNoise(gridX, gridZ, salt),
                hashNoise(gridX + 1, gridZ, salt),
                smoothX
        );
        int south = lerpFixed(
                hashNoise(gridX, gridZ + 1, salt),
                hashNoise(gridX + 1, gridZ + 1, salt),
                smoothX
        );
        return lerpFixed(north, south, smoothZ);
    }

    private static int smoothStep(int local, int scale) {
        int t = local * NOISE_UNIT / scale;
        return (int) ((long) t * t * (3L * NOISE_UNIT - 2L * t) / ((long) NOISE_UNIT * NOISE_UNIT));
    }

    private static int lerpFixed(int start, int end, int amount) {
        return start + (end - start) * amount / NOISE_UNIT;
    }

    private static int hashNoise(int gridX, int gridZ, long salt) {
        long hash = (long) gridX * 0x9E3779B97F4A7C15L
                ^ (long) gridZ * 0xC2B2AE3D27D4EB4FL
                ^ salt;
        hash = (hash ^ (hash >>> 30)) * 0xBF58476D1CE4E5B9L;
        hash = (hash ^ (hash >>> 27)) * 0x94D049BB133111EBL;
        hash ^= hash >>> 31;
        return (int) ((hash >>> 16) & (NOISE_UNIT - 1));
    }

    private static BlockState pickGrowth(RandomSource random) {
        return switch (random.nextInt(12)) {
            case 0, 1 -> ModBlocks.BLADDERPOD.get().defaultBlockState();
            case 2, 3, 4 -> ModBlocks.DROWNED_ROOTS.get().defaultBlockState();
            case 5, 6 -> ModBlocks.LUMEN_KELP.get().defaultBlockState();
            case 7, 8 -> ModBlocks.THREADKELP.get().defaultBlockState();
            default -> ModBlocks.VEILWEED.get().defaultBlockState();
        };
    }
}
