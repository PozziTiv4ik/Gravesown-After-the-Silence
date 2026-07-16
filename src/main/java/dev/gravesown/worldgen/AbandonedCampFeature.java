package dev.gravesown.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

/** A bounded, server-generated surface camp with regional timber and three layouts. */
public final class AbandonedCampFeature extends Feature<AbandonedCampFeature.Configuration> {
    public static final int REVIEWED_CHUNK_RARITY = 192;
    public static final int LAYOUT_VARIANTS = 3;

    private static final ResourceKey<LootTable> LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, Gravesown.id("chests/abandoned_camp")
    );
    private static final Set<Block> ALLOWED_GROUND = Set.of(
            ModBlocks.ASHEN_SOD.get(), ModBlocks.GRAVE_LOAM.get(),
            ModBlocks.ROOTFELT.get(), ModBlocks.FIBROUS_LOAM.get(),
            ModBlocks.SCAR_SHALE.get(), ModBlocks.MARROWSTONE.get(),
            ModBlocks.SUTURE_SILT.get(), ModBlocks.DRIED_ICHOR.get()
    );

    public AbandonedCampFeature() {
        super(Configuration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        if (context.random().nextInt(context.config().rarity()) != 0) {
            return false;
        }
        return placeCampAt(context.level(), context.random(), context.origin());
    }

    /** Bypasses only the rarity roll so the bounded layout can be exercised by GameTests. */
    public static boolean placeCampAt(WorldGenLevel level, RandomSource random, BlockPos origin) {
        int probeY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());
        ResourceLocation probeBiomeId = level.getBiome(new BlockPos(origin.getX(), probeY, origin.getZ())).unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
        boolean sea = probeBiomeId != null && "gloam_sea".equals(probeBiomeId.getPath());
        int floorY = level.getHeight(
                sea ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                origin.getX(), origin.getZ()
        );
        BlockPos center = new BlockPos(origin.getX(), floorY, origin.getZ());
        ResourceLocation biomeId = level.getBiome(center).unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
        // The feature is attached only to Gravesown biomes by datapack. Keeping
        // the bounded placement primitive biome-agnostic makes it testable in
        // the isolated GameTest dimension without weakening runtime placement.
        if (biomeId == null || !stableFootprint(level, center, sea)) {
            return false;
        }

        Palette palette = paletteFor(biomeId);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int variant = random.nextInt(LAYOUT_VARIANTS);
        prepareFoundation(level, random, center, facing);
        placeFence(level, center, facing, palette);
        placeLeanTo(level, random, center, facing, palette, variant);
        placeFireRing(level, center, facing, variant);

        int crateX = variant == 1 ? -2 : 2;
        int crateZ = variant == 2 ? 1 : 0;
        BlockPos cratePos = local(center, facing, crateX, 0, crateZ);
        BlockState crate = orient(ModBlocks.RELIQUARY_CRATE.get().defaultBlockState(), facing.getOpposite());
        setBlock(level, cratePos, crate);
        RandomizableContainer.setBlockEntityLootTable(level, random, cratePos, LOOT);

        if (variant != 1) {
            setBlock(level, local(center, facing, -2, 1, 1), ModBlocks.TALLOW_LANTERN.get().defaultBlockState());
        }
        return true;
    }

    public static String woodFamilyForBiome(ResourceLocation biomeId) {
        return switch (biomeId.getPath()) {
            case "marrow_rifts" -> "cairnwood";
            case "suture_mire" -> "suturewood";
            case "mosswake_woods" -> "mosswake";
            case "amberquiet_grove" -> "sunveil";
            case "ember_thicket" -> "emberbark";
            case "pallid_weald" -> "palevine";
            case "gloam_sea" -> "suturewood";
            default -> "ribroot";
        };
    }

    private static Palette paletteFor(ResourceLocation biomeId) {
        return switch (woodFamilyForBiome(biomeId)) {
            case "cairnwood" -> palette(ModBlocks.CAIRNWOOD);
            case "suturewood" -> palette(ModBlocks.SUTUREWOOD);
            case "mosswake" -> palette(ModBlocks.MOSSWAKE);
            case "sunveil" -> palette(ModBlocks.SUNVEIL);
            case "emberbark" -> new Palette(
                    ModBlocks.EMBERBARK_STEM.get(), ModBlocks.EMBERBARK_PLANKS.get(),
                    ModBlocks.EMBERBARK_SLAB.get(), ModBlocks.EMBERBARK_FENCE.get(),
                    ModBlocks.EMBERBARK_FENCE_GATE.get()
            );
            case "palevine" -> new Palette(
                    ModBlocks.PALEVINE_STEM.get(), ModBlocks.PALEVINE_PLANKS.get(),
                    ModBlocks.PALEVINE_SLAB.get(), ModBlocks.PALEVINE_FENCE.get(),
                    ModBlocks.PALEVINE_FENCE_GATE.get()
            );
            default -> new Palette(
                    ModBlocks.RIBROOT_STEM.get(), ModBlocks.RIBROOT_PLANKS.get(), ModBlocks.RIBROOT_SLAB.get(),
                    ModBlocks.RIBROOT_FENCE.get(), ModBlocks.RIBROOT_FENCE_GATE.get()
            );
        };
    }

    private static Palette palette(ModBlocks.WoodFamily family) {
        return new Palette(
                family.stem().get(), family.planks().get(), family.slab().get(),
                family.fence().get(), family.fenceGate().get()
        );
    }

    private static void prepareFoundation(
            WorldGenLevel level,
            RandomSource random,
            BlockPos center,
            Direction facing
    ) {
        for (int z = -3; z <= 3; z++) {
            for (int x = -4; x <= 4; x++) {
                BlockPos floor = local(center, facing, x, -1, z);
                BlockState stone = random.nextInt(9) == 0
                        ? ModBlocks.CAIRNSTONE.get().defaultBlockState()
                        : ModBlocks.HUSHSTONE.get().defaultBlockState();
                setBlock(level, floor, stone);
                for (int y = 0; y <= 4; y++) {
                    setBlock(level, floor.above(y + 1), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static void placeFence(
            WorldGenLevel level,
            BlockPos center,
            Direction facing,
            Palette palette
    ) {
        for (int x = -4; x <= 4; x++) {
            if (x != 0) {
                setBlock(level, local(center, facing, x, 0, -3),
                        fenceState(palette.fence().defaultBlockState(), facing,
                                true, true, false, Math.abs(x) == 4));
            }
            setBlock(level, local(center, facing, x, 0, 3),
                    fenceState(palette.fence().defaultBlockState(), facing,
                            true, true, Math.abs(x) == 4, false));
        }
        for (int z = -2; z <= 2; z++) {
            setBlock(level, local(center, facing, -4, 0, z),
                    fenceState(palette.fence().defaultBlockState(), facing, false, false, true, true));
            setBlock(level, local(center, facing, 4, 0, z),
                    fenceState(palette.fence().defaultBlockState(), facing, false, false, true, true));
        }
        setBlock(level, local(center, facing, 0, 0, -3), orient(palette.gate().defaultBlockState(), facing));
    }

    private static void placeLeanTo(
            WorldGenLevel level,
            RandomSource random,
            BlockPos center,
            Direction facing,
            Palette palette,
            int variant
    ) {
        int side = variant == 1 ? 1 : -1;
        int anchorX = side * 3;
        // Keep every shelter one block inside the perimeter. Variant two still
        // raises its rear roof, but may no longer replace the enclosing fence.
        int backZ = 1;
        for (int z = backZ - 1; z <= backZ + 1; z++) {
            for (int x = anchorX - 1; x <= anchorX + 1; x++) {
                setBlock(level, local(center, facing, x, 0, z), palette.planks().defaultBlockState());
            }
        }
        for (int xOffset : new int[]{-1, 1}) {
            for (int zOffset : new int[]{-1, 1}) {
                int postHeight = 2 + (zOffset > 0 && variant == 2 ? 1 : 0);
                for (int y = 1; y <= postHeight; y++) {
                    setBlock(level, local(center, facing, anchorX + xOffset, y, backZ + zOffset),
                            palette.stem().defaultBlockState());
                }
            }
        }
        BlockState roof = palette.slab().defaultBlockState();
        if (roof.hasProperty(SlabBlock.TYPE)) {
            roof = roof.setValue(SlabBlock.TYPE, SlabType.TOP);
        }
        for (int z = backZ - 1; z <= backZ + 1; z++) {
            for (int x = anchorX - 1; x <= anchorX + 1; x++) {
                if (variant == 0 && x == anchorX + 1 && z == backZ - 1 && random.nextBoolean()) {
                    continue;
                }
                setBlock(level, local(center, facing, x, variant == 2 && z > backZ ? 3 : 2, z), roof);
            }
        }
    }

    private static void placeFireRing(WorldGenLevel level, BlockPos center, Direction facing, int variant) {
        int fireX = variant == 1 ? 1 : 0;
        int fireZ = variant == 2 ? -1 : 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            setBlock(level, local(center, facing, fireX + direction.getStepX(), 0,
                    fireZ + direction.getStepZ()), ModBlocks.CAIRNSTONE.get().defaultBlockState());
        }
        setBlock(level, local(center, facing, fireX, 0, fireZ), ModBlocks.DRIED_ICHOR.get().defaultBlockState());
    }

    private static boolean stableFootprint(WorldGenLevel level, BlockPos center, boolean sea) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int z = -3; z <= 3; z++) {
            for (int x = -4; x <= 4; x++) {
                int columnX = center.getX() + x;
                int columnZ = center.getZ() + z;
                int y;
                if (sea) {
                    y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, columnX, columnZ);
                    int oceanFloor = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, columnX, columnZ);
                    Block seabed = level.getBlockState(new BlockPos(columnX, oceanFloor - 1, columnZ)).getBlock();
                    int depth = y - oceanFloor;
                    if ((seabed != ModBlocks.GLOAM_SAND.get() && seabed != ModBlocks.GLOAM_MUCK.get()
                            && seabed != ModBlocks.BRINEBONE.get()) || depth < 2 || depth > 12
                            || level.getFluidState(new BlockPos(columnX, y - 1, columnZ)).isEmpty()) {
                        return false;
                    }
                } else {
                    y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, columnX, columnZ);
                    Block ground = level.getBlockState(new BlockPos(columnX, y - 1, columnZ)).getBlock();
                    if (!ALLOWED_GROUND.contains(ground)) {
                        return false;
                    }
                }
                min = Math.min(min, y);
                max = Math.max(max, y);
            }
        }
        return max - min <= 1;
    }

    private static BlockState fenceState(
            BlockState state,
            Direction facing,
            boolean localWest,
            boolean localEast,
            boolean localNorth,
            boolean localSouth
    ) {
        boolean north = connects(facing, Direction.NORTH, localWest, localEast, localNorth, localSouth);
        boolean east = connects(facing, Direction.EAST, localWest, localEast, localNorth, localSouth);
        boolean south = connects(facing, Direction.SOUTH, localWest, localEast, localNorth, localSouth);
        boolean west = connects(facing, Direction.WEST, localWest, localEast, localNorth, localSouth);
        if (state.hasProperty(BlockStateProperties.NORTH)) state = state.setValue(BlockStateProperties.NORTH, north);
        if (state.hasProperty(BlockStateProperties.EAST)) state = state.setValue(BlockStateProperties.EAST, east);
        if (state.hasProperty(BlockStateProperties.SOUTH)) state = state.setValue(BlockStateProperties.SOUTH, south);
        if (state.hasProperty(BlockStateProperties.WEST)) state = state.setValue(BlockStateProperties.WEST, west);
        return state;
    }

    private static boolean connects(
            Direction facing,
            Direction worldDirection,
            boolean localWest,
            boolean localEast,
            boolean localNorth,
            boolean localSouth
    ) {
        if (worldDirection == facing) return localNorth;
        if (worldDirection == facing.getOpposite()) return localSouth;
        if (worldDirection == facing.getClockWise()) return localEast;
        return localWest;
    }

    private static BlockState orient(BlockState state, Direction facing) {
        return state.hasProperty(HorizontalDirectionalBlock.FACING)
                ? state.setValue(HorizontalDirectionalBlock.FACING, facing)
                : state;
    }

    private static BlockPos local(BlockPos center, Direction facing, int x, int y, int z) {
        return switch (facing) {
            case EAST -> center.offset(-z, y, x);
            case SOUTH -> center.offset(-x, y, -z);
            case WEST -> center.offset(z, y, -x);
            default -> center.offset(x, y, z);
        };
    }

    private static void setBlock(WorldGenLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
    }

    public record Configuration(int rarity) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, 4096).fieldOf("rarity").forGetter(Configuration::rarity)
        ).apply(instance, Configuration::new));
    }

    private record Palette(Block stem, Block planks, Block slab, Block fence, Block gate) {
    }
}
