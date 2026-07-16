package dev.gravesown.worldgen;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

/** Three material variants of a compact, lootable survivor ruin. */
public final class RuinedShelterFeature extends Feature<NoneFeatureConfiguration> {
    private static final ResourceKey<LootTable> LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, Gravesown.id("chests/ruined_shelter")
    );
    private static final Set<Block> ALLOWED_GROUND = Set.of(
            ModBlocks.ASHEN_SOD.get(), ModBlocks.GRAVE_LOAM.get(),
            ModBlocks.ROOTFELT.get(), ModBlocks.FIBROUS_LOAM.get(),
            ModBlocks.SCAR_SHALE.get(), ModBlocks.MARROWSTONE.get(),
            ModBlocks.SUTURE_SILT.get(), ModBlocks.DRIED_ICHOR.get(),
            ModBlocks.GLOAM_SAND.get()
    );

    public RuinedShelterFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int floorY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        BlockPos center = new BlockPos(origin.getX(), floorY, origin.getZ());
        if (!stableFootprint(level, center)) {
            return false;
        }

        int variant = random.nextInt(3);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState stem = switch (variant) {
            case 1 -> ModBlocks.EMBERBARK_STEM.get().defaultBlockState();
            case 2 -> ModBlocks.PALEVINE_STEM.get().defaultBlockState();
            default -> ModBlocks.RIBROOT_STEM.get().defaultBlockState();
        };
        BlockState planks = switch (variant) {
            case 1 -> ModBlocks.EMBERBARK_PLANKS.get().defaultBlockState();
            case 2 -> ModBlocks.PALEVINE_PLANKS.get().defaultBlockState();
            default -> ModBlocks.RIBROOT_PLANKS.get().defaultBlockState();
        };
        BlockState slab = switch (variant) {
            case 1 -> ModBlocks.EMBERBARK_SLAB.get().defaultBlockState();
            case 2 -> ModBlocks.PALEVINE_SLAB.get().defaultBlockState();
            default -> ModBlocks.RIBROOT_SLAB.get().defaultBlockState();
        };

        for (int dz = -2; dz <= 2; dz++) {
            for (int dx = -3; dx <= 3; dx++) {
                BlockPos base = local(center, facing, dx, 0, dz);
                setBlock(level, base.below(), random.nextInt(5) == 0
                        ? ModBlocks.SCAR_SHALE.get().defaultBlockState()
                        : ModBlocks.HUSHSTONE.get().defaultBlockState());
                for (int y = 0; y <= 4; y++) {
                    setBlock(level, base.above(y), Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (int y = 0; y <= 3; y++) {
            for (int[] corner : new int[][]{{-3,-2},{3,-2},{-3,2},{3,2}}) {
                if (y < 3 || random.nextBoolean()) {
                    setBlock(level, local(center, facing, corner[0], y, corner[1]), stem);
                }
            }
        }
        for (int dz = -2; dz <= 2; dz++) {
            for (int dx = -3; dx <= 3; dx++) {
                boolean edge = Math.abs(dx) == 3 || Math.abs(dz) == 2;
                boolean doorway = dz == -2 && Math.abs(dx) <= 1;
                if (edge && !doorway && random.nextFloat() < 0.62F) {
                    setBlock(level, local(center, facing, dx, 1, dz), planks);
                    if (random.nextFloat() < 0.48F) {
                        setBlock(level, local(center, facing, dx, 2, dz), planks);
                    }
                }
                if (dz >= 0 && random.nextFloat() < 0.58F) {
                    setBlock(level, local(center, facing, dx, 3, dz), slab);
                }
            }
        }

        BlockPos cratePos = local(center, facing, 2, 0, 1);
        setBlock(level, cratePos, ModBlocks.RELIQUARY_CRATE.get().defaultBlockState());
        RandomizableContainer.setBlockEntityLootTable(level, random, cratePos, LOOT);
        if (random.nextBoolean()) {
            setBlock(level, local(center, facing, -2, 2, 1), ModBlocks.TALLOW_LANTERN.get().defaultBlockState());
        }
        return true;
    }

    private static boolean stableFootprint(WorldGenLevel level, BlockPos center) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int dz = -2; dz <= 2; dz++) {
            for (int dx = -3; dx <= 3; dx++) {
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        center.getX() + dx, center.getZ() + dz);
                if (!ALLOWED_GROUND.contains(level.getBlockState(
                        new BlockPos(center.getX() + dx, y - 1, center.getZ() + dz)).getBlock())) {
                    return false;
                }
                min = Math.min(min, y);
                max = Math.max(max, y);
            }
        }
        return max - min <= 1;
    }

    private static BlockPos local(BlockPos center, Direction facing, int x, int y, int z) {
        return switch (facing) {
            case EAST -> center.offset(-z, y, x);
            case SOUTH -> center.offset(-x, y, -z);
            case WEST -> center.offset(z, y, -x);
            default -> center.offset(x, y, z);
        };
    }
}
