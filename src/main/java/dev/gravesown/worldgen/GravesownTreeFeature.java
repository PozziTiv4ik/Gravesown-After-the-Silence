package dev.gravesown.worldgen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Seven original, bounded tree silhouettes with deterministic natural variation. */
public final class GravesownTreeFeature extends Feature<NoneFeatureConfiguration> {
    public enum Shape {
        RIBROOT_FORKED,
        EMBERBARK_WINDSWEPT,
        PALEVINE_TALL,
        CAIRNWOOD_SHRUB,
        SUTUREWOOD_ROOTED,
        MOSSWAKE_BROAD,
        SUNVEIL_CROWN
    }

    private final Supplier<? extends Block> stem;
    private final Supplier<? extends Block> foliage;
    private final Shape shape;

    public GravesownTreeFeature(
            Supplier<? extends Block> stem,
            Supplier<? extends Block> foliage,
            Shape shape
    ) {
        super(NoneFeatureConfiguration.CODEC);
        this.stem = stem;
        this.foliage = foliage;
        this.shape = shape;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return placeTree(
                context.level(),
                context.random(),
                context.origin(),
                this.stem.get(),
                this.foliage.get(),
                this.shape
        );
    }

    public static boolean placeTree(
            WorldGenLevel level,
            RandomSource random,
            BlockPos base,
            Block stem,
            Block foliage,
            Shape shape
    ) {
        BlockPos groundPos = base.below();
        if (!level.getBlockState(groundPos).isFaceSturdy(level, groundPos, Direction.UP)) {
            return false;
        }

        Map<BlockPos, BlockState> planned = new LinkedHashMap<>();
        BlockState trunk = stem.defaultBlockState();
        BlockState leaves = foliage.defaultBlockState();
        if (leaves.hasProperty(LeavesBlock.PERSISTENT)) {
            leaves = leaves.setValue(LeavesBlock.PERSISTENT, false);
        }
        if (leaves.hasProperty(LeavesBlock.DISTANCE)) {
            leaves = leaves.setValue(LeavesBlock.DISTANCE, 1);
        }

        switch (shape) {
            case RIBROOT_FORKED -> planRibroot(planned, random, base, trunk, leaves);
            case EMBERBARK_WINDSWEPT -> planEmberbark(planned, random, base, trunk, leaves);
            case PALEVINE_TALL -> planPalevine(planned, random, base, trunk, leaves);
            case CAIRNWOOD_SHRUB -> planCairnwood(planned, random, base, trunk, leaves);
            case SUTUREWOOD_ROOTED -> planSuturewood(planned, random, base, trunk, leaves);
            case MOSSWAKE_BROAD -> planMosswake(planned, random, base, trunk, leaves);
            case SUNVEIL_CROWN -> planSunveil(planned, random, base, trunk, leaves);
        }

        for (Map.Entry<BlockPos, BlockState> entry : planned.entrySet()) {
            BlockState current = level.getBlockState(entry.getKey());
            if (!current.isAir() && !current.canBeReplaced()) {
                return false;
            }
        }
        planned.forEach((pos, state) -> level.setBlock(pos, state, Block.UPDATE_CLIENTS));
        return true;
    }

    private static void planRibroot(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 5 + random.nextInt(4);
        Direction lean = randomHorizontal(random);
        BlockPos top = crookedColumn(out, base, height, trunk, lean, variant == 0 ? height : height - 2, 2);

        Direction fork = variant == 3 ? lean.getOpposite() : lean.getClockWise();
        int forkY = height - 3 + random.nextInt(2);
        BlockPos forkStart = trunkPos(base, forkY, lean, variant == 0 ? height : height - 2, 2);
        BlockPos forkEnd = branch(out, forkStart, fork, 1 + random.nextInt(2), trunk, true);
        irregularCrown(out, random, top.above(), 2 + (variant == 1 ? 1 : 0), 1, leaves, 0.38F);
        irregularCrown(out, random, forkEnd.above(), 2, 1, leaves, 0.46F);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == lean || random.nextBoolean()) {
                out.put(base.relative(direction), axis(trunk, direction.getAxis()));
            }
        }
    }

    private static void planEmberbark(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 5 + random.nextInt(4);
        Direction wind = randomHorizontal(random);
        int bendStart = 2 + random.nextInt(2);
        BlockPos top = crookedColumn(out, base, height, trunk, wind, bendStart, variant == 0 ? 3 : 2);
        Direction side = random.nextBoolean() ? wind.getClockWise() : wind.getCounterClockWise();
        BlockPos forkStart = trunkPos(base, Math.max(2, height - 3), wind, bendStart, variant == 0 ? 3 : 2);
        BlockPos forkEnd = branch(out, forkStart, side, 2, trunk, true);
        if (variant >= 2) {
            branch(out, forkStart.above(), side.getOpposite(), 1 + random.nextInt(2), trunk, true);
        }
        irregularCrown(out, random, top.above(), 3, 1, leaves, 0.52F);
        irregularCrown(out, random, forkEnd.above(), 2, 1, leaves, 0.44F);
    }

    private static void planPalevine(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 7 + random.nextInt(4);
        Direction sway = randomHorizontal(random);
        BlockPos top = crookedColumn(out, base, height, trunk, sway, 4 + (variant & 1), 3);
        Direction highBranch = variant < 2 ? sway.getClockWise() : sway.getCounterClockWise();
        BlockPos branchStart = trunkPos(base, height - 3, sway, 4 + (variant & 1), 3);
        BlockPos branchEnd = branch(out, branchStart, highBranch, 2, trunk, true);
        irregularCrown(out, random, top.above(), 2, 2, leaves, 0.34F);
        irregularCrown(out, random, branchEnd.above(), 2, 1, leaves, 0.48F);
        if (variant == 3) {
            BlockPos second = branch(out, branchStart.above(), highBranch.getOpposite(), 2, trunk, true);
            irregularCrown(out, random, second.above(), 1, 1, leaves, 0.28F);
        }
    }

    private static void planCairnwood(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 2 + random.nextInt(3);
        Direction offset = randomHorizontal(random);
        BlockPos top = crookedColumn(out, base, height, trunk, offset, variant == 0 ? height : 1, 2);
        int branchY = Math.max(1, height - 2);
        int branchCount = 2 + random.nextInt(3);
        Direction first = randomHorizontal(random);
        int placed = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Direction candidate = rotate(first, direction.get2DDataValue());
            if (placed++ >= branchCount) {
                break;
            }
            BlockPos start = trunkPos(base, branchY, offset, variant == 0 ? height : 1, 2);
            BlockPos end = branch(out, start, candidate, 1 + random.nextInt(2), trunk, random.nextBoolean());
            irregularCrown(out, random, end.above(), 1, 1, leaves, 0.32F);
        }
        irregularCrown(out, random, top.above(), 2, 1, leaves, 0.48F);
    }

    private static void planSuturewood(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 5 + random.nextInt(4);
        Direction lean = randomHorizontal(random);
        BlockPos top = crookedColumn(out, base, height, trunk, lean, variant == 0 ? height : height - 2, 3);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Direction.Axis axis = direction.getAxis();
            out.put(base.relative(direction, 2), axis(trunk, axis));
            out.put(base.relative(direction).above(), axis(trunk, axis));
            if (direction == lean || random.nextBoolean()) {
                out.put(base.relative(direction).above(2), axis(trunk, axis));
            }
        }
        irregularCrown(out, random, top, 3, 2, leaves, 0.48F);
        Direction side = variant < 2 ? lean.getClockWise() : lean.getCounterClockWise();
        BlockPos branchStart = trunkPos(base, height - 3, lean, variant == 0 ? height : height - 2, 3);
        BlockPos branchEnd = branch(out, branchStart, side, 1 + random.nextInt(2), trunk, true);
        irregularCrown(out, random, branchEnd.above(), 2, 1, leaves, 0.52F);
    }

    private static void planMosswake(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 5 + random.nextInt(4);
        Direction lean = randomHorizontal(random);
        BlockPos top = crookedColumn(out, base, height, trunk, lean, variant == 0 ? height : height - 2, 3);
        int satellites = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.72F) {
                BlockPos start = trunkPos(base, height - 3 + random.nextInt(2), lean,
                        variant == 0 ? height : height - 2, 3);
                BlockPos end = branch(out, start, direction, 1 + random.nextInt(2), trunk, random.nextBoolean());
                irregularCrown(out, random, end.above(), 1 + random.nextInt(2), 1, leaves, 0.42F);
                satellites++;
            }
        }
        if (satellites == 0) {
            BlockPos end = branch(out, top.below(2), lean.getClockWise(), 2, trunk, true);
            irregularCrown(out, random, end.above(), 2, 1, leaves, 0.42F);
        }
        irregularCrown(out, random, top, 3, 2, leaves, 0.44F);
    }

    private static void planSunveil(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos base,
            BlockState trunk,
            BlockState leaves
    ) {
        int variant = random.nextInt(4);
        int height = 7 + random.nextInt(4);
        Direction lean = randomHorizontal(random);
        BlockPos top = crookedColumn(out, base, height, trunk, lean, 5 + (variant & 1), 3);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.78F) {
                BlockPos start = trunkPos(base, height - 3 + random.nextInt(2), lean, 5 + (variant & 1), 3);
                BlockPos end = branch(out, start, direction, 1 + random.nextInt(2), trunk, true);
                irregularCrown(out, random, end.above(), 1, 1, leaves, 0.28F);
            }
        }
        irregularCrown(out, random, top.above(), 2 + (variant == 2 ? 1 : 0), 1, leaves, 0.34F);
        irregularCrown(out, random, top.below(1), 2, 1, leaves, 0.46F);
    }

    private static BlockPos crookedColumn(
            Map<BlockPos, BlockState> out,
            BlockPos base,
            int height,
            BlockState trunk,
            Direction lean,
            int bendStart,
            int bendEvery
    ) {
        BlockPos top = base;
        for (int y = 0; y < height; y++) {
            top = trunkPos(base, y, lean, bendStart, bendEvery);
            out.put(top, trunk);
        }
        return top;
    }

    private static BlockPos trunkPos(
            BlockPos base,
            int y,
            Direction lean,
            int bendStart,
            int bendEvery
    ) {
        int sideways = y < bendStart ? 0 : 1 + (y - bendStart) / Math.max(1, bendEvery);
        return base.above(y).relative(lean, sideways);
    }

    private static BlockPos branch(
            Map<BlockPos, BlockState> out,
            BlockPos start,
            Direction direction,
            int length,
            BlockState trunk,
            boolean rising
    ) {
        BlockPos end = start;
        for (int step = 1; step <= length; step++) {
            end = start.relative(direction, step).above(rising && step == length ? 1 : 0);
            out.put(end, axis(trunk, direction.getAxis()));
        }
        return end;
    }

    private static void irregularCrown(
            Map<BlockPos, BlockState> out,
            RandomSource random,
            BlockPos center,
            int radius,
            int halfHeight,
            BlockState leaves,
            float edgeGapChance
    ) {
        for (int dy = -halfHeight; dy <= halfHeight; dy++) {
            int layerRadius = Math.max(1, radius - Math.max(0, Math.abs(dy) - 1));
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    int distance = Math.abs(dx) + Math.abs(dz);
                    int limit = layerRadius + (dy == 0 ? 1 : 0);
                    if (distance > limit || (distance >= layerRadius && random.nextFloat() < edgeGapChance)) {
                        continue;
                    }
                    out.putIfAbsent(center.offset(dx, dy, dz), leaves);
                }
            }
        }
    }

    private static Direction randomHorizontal(RandomSource random) {
        return Direction.Plane.HORIZONTAL.getRandomDirection(random);
    }

    private static Direction rotate(Direction direction, int quarterTurns) {
        Direction rotated = direction;
        for (int index = 0; index < Math.floorMod(quarterTurns, 4); index++) {
            rotated = rotated.getClockWise();
        }
        return rotated;
    }

    private static BlockState axis(BlockState state, Direction.Axis axis) {
        return state.hasProperty(RotatedPillarBlock.AXIS)
                ? state.setValue(RotatedPillarBlock.AXIS, axis)
                : state;
    }
}
