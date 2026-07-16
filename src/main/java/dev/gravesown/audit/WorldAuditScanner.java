package dev.gravesown.audit;

import dev.gravesown.Gravesown;
import java.util.Comparator;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.fml.ModList;

public final class WorldAuditScanner {
    private WorldAuditScanner() {
    }

    public static WorldAuditReport scan(MinecraftServer server, WorldAuditConfig config) {
        long startedNanos = System.nanoTime();
        ServerLevel level = server.overworld();
        WorldAuditReport report = createReport(server, level, config);

        try {
            scanRegion(level, config, report);
            scanWideBiomeSource(level, config, report);
        } catch (Throwable throwable) {
            report.error = throwable.getClass().getName() + ": " + String.valueOf(throwable.getMessage());
            Gravesown.LOGGER.error("Gravesown world audit failed while scanning", throwable);
        }

        report.finish(config, startedNanos);
        return report;
    }

    private static WorldAuditReport createReport(
            MinecraftServer server,
            ServerLevel level,
            WorldAuditConfig config
    ) {
        WorldAuditReport report = new WorldAuditReport();
        report.enforcement = config.enforcement().name().toLowerCase();
        report.profile = config.profile();
        report.reportId = config.reportId();
        report.minecraftVersion = SharedConstants.getCurrentVersion().getName();
        report.neoForgeVersion = modVersion("neoforge");
        report.modVersion = modVersion(Gravesown.MOD_ID);
        report.dimension = level.dimension().location().toString();
        report.levelName = server.getWorldData().getLevelName();
        report.seed = server.getWorldData().worldGenOptions().seed();
        report.contract.expectedBiomes = config.expectedBiomes().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .toList();
        report.contract.allowedContentNamespace = Gravesown.MOD_ID;
        report.contract.technicalBlockAllowlist = config.technicalBlockAllowlist().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .toList();
        report.contract.technicalFluidAllowlist = config.technicalFluidAllowlist().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .toList();
        report.contract.violationSampleLimitPerKind = config.sampleLimit();

        ChunkPos center = new ChunkPos(level.getSharedSpawnPos());
        report.region.centerChunkX = center.x;
        report.region.centerChunkZ = center.z;
        report.region.radiusChunks = config.radiusChunks();
        report.region.biomeProbeRadiusChunks = config.biomeProbeRadiusChunks();
        report.region.biomeProbeStepChunks = config.biomeProbeStepChunks();
        int side = config.radiusChunks() * 2 + 1;
        report.region.chunkCount = side * side;
        report.region.minY = level.getMinBuildHeight();
        report.region.maxYExclusive = level.getMaxBuildHeight();
        return report;
    }

    /**
     * Samples the uncached biome source without loading or generating chunks. The deep FULL-chunk
     * scan remains responsible for blocks, fluids, block entities, and generated terrain; this
     * wide grid exists only so macro-scale climate regions can be covered economically.
     */
    private static void scanWideBiomeSource(
            ServerLevel level,
            WorldAuditConfig config,
            WorldAuditReport report
    ) {
        int radius = config.biomeProbeRadiusChunks();
        if (radius == 0) {
            return;
        }

        int step = config.biomeProbeStepChunks();
        int centerX = report.region.centerChunkX;
        int centerZ = report.region.centerChunkZ;
        int quartY = QuartPos.fromBlock(level.getSeaLevel());
        for (int offsetZ = -radius; offsetZ <= radius; offsetZ += step) {
            for (int offsetX = -radius; offsetX <= radius; offsetX += step) {
                int chunkX = centerX + offsetX;
                int chunkZ = centerZ + offsetZ;
                int blockX = (chunkX << 4) + 8;
                int blockZ = (chunkZ << 4) + 8;
                Holder<Biome> biome = level.getUncachedNoiseBiome(
                        QuartPos.fromBlock(blockX),
                        quartY,
                        QuartPos.fromBlock(blockZ)
                );
                ResourceLocation biomeId = biome.unwrapKey()
                        .map(key -> key.location())
                        .orElse(ResourceLocation.fromNamespaceAndPath("unregistered", "biome"));
                report.recordBiomeProbe(biomeId.toString());
                if (!config.isAllowedBiome(biomeId)) {
                    report.recordViolation(
                            "biome_probe",
                            biomeId.toString(),
                            blockX,
                            level.getSeaLevel(),
                            blockZ,
                            chunkX,
                            chunkZ,
                            config.sampleLimit()
                    );
                }
            }
        }
    }

    private static String modVersion(String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
    }

    private static void scanRegion(
            ServerLevel level,
            WorldAuditConfig config,
            WorldAuditReport report
    ) {
        int radius = config.radiusChunks();
        int centerX = report.region.centerChunkX;
        int centerZ = report.region.centerChunkZ;

        for (int chunkZ = centerZ - radius; chunkZ <= centerZ + radius; chunkZ++) {
            for (int chunkX = centerX - radius; chunkX <= centerX + radius; chunkX++) {
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                scanChunk(level, chunk, config, report);
                report.totals.chunks++;
            }
        }
    }

    private static void scanChunk(
            ServerLevel level,
            LevelChunk chunk,
            WorldAuditConfig config,
            WorldAuditReport report
    ) {
        ChunkPos chunkPos = chunk.getPos();
        scanBiomes(chunk, config, report);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    int x = minX + localX;
                    int z = minZ + localZ;
                    pos.set(x, y, z);
                    BlockState blockState = chunk.getBlockState(pos);
                    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
                    report.recordBlock(blockId.toString(), blockState.isAir());
                    if (!config.isAllowedBlock(blockId)) {
                        report.recordViolation(
                                "block",
                                blockId.toString(),
                                x,
                                y,
                                z,
                                chunkPos.x,
                                chunkPos.z,
                                config.sampleLimit()
                        );
                    }

                    FluidState fluidState = blockState.getFluidState();
                    ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluidState.getType());
                    report.recordFluid(fluidId.toString(), fluidState.isEmpty());
                    if (!config.isAllowedFluid(fluidId)) {
                        report.recordViolation(
                                "fluid",
                                fluidId.toString(),
                                x,
                                y,
                                z,
                                chunkPos.x,
                                chunkPos.z,
                                config.sampleLimit()
                        );
                    }
                }
            }
        }

        chunk.getBlockEntities().entrySet().stream()
                .sorted(MapEntryComparator.INSTANCE)
                .forEach(entry -> scanBlockEntity(entry.getValue(), config, report));
    }

    private static void scanBiomes(
            LevelChunk chunk,
            WorldAuditConfig config,
            WorldAuditReport report
    ) {
        ChunkPos chunkPos = chunk.getPos();
        int minQuartY = QuartPos.fromBlock(chunk.getMinBuildHeight());
        int maxQuartY = QuartPos.fromBlock(chunk.getMaxBuildHeight() - 1);
        int minQuartX = QuartPos.fromBlock(chunkPos.getMinBlockX());
        int minQuartZ = QuartPos.fromBlock(chunkPos.getMinBlockZ());

        for (int quartY = minQuartY; quartY <= maxQuartY; quartY++) {
            for (int localQuartZ = 0; localQuartZ < 4; localQuartZ++) {
                for (int localQuartX = 0; localQuartX < 4; localQuartX++) {
                    int quartX = minQuartX + localQuartX;
                    int quartZ = minQuartZ + localQuartZ;
                    Holder<Biome> biome = chunk.getNoiseBiome(quartX, quartY, quartZ);
                    ResourceLocation biomeId = biome.unwrapKey()
                            .map(key -> key.location())
                            .orElse(ResourceLocation.fromNamespaceAndPath("unregistered", "biome"));
                    report.recordBiome(biomeId.toString());
                    if (!config.isAllowedBiome(biomeId)) {
                        report.recordViolation(
                                "biome",
                                biomeId.toString(),
                                QuartPos.toBlock(quartX),
                                QuartPos.toBlock(quartY),
                                QuartPos.toBlock(quartZ),
                                chunkPos.x,
                                chunkPos.z,
                                config.sampleLimit()
                        );
                    }
                }
            }
        }
    }

    private static void scanBlockEntity(
            BlockEntity blockEntity,
            WorldAuditConfig config,
            WorldAuditReport report
    ) {
        ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
        BlockPos pos = blockEntity.getBlockPos();
        report.recordBlockEntity(id.toString());
        if (!config.isAllowedBlockEntity(id)) {
            report.recordViolation(
                    "block_entity",
                    id.toString(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    pos.getX() >> 4,
                    pos.getZ() >> 4,
                    config.sampleLimit()
            );
        }
    }

    private enum MapEntryComparator implements Comparator<java.util.Map.Entry<BlockPos, BlockEntity>> {
        INSTANCE;

        @Override
        public int compare(
                java.util.Map.Entry<BlockPos, BlockEntity> left,
                java.util.Map.Entry<BlockPos, BlockEntity> right
        ) {
            int x = Integer.compare(left.getKey().getX(), right.getKey().getX());
            if (x != 0) {
                return x;
            }
            int y = Integer.compare(left.getKey().getY(), right.getKey().getY());
            return y != 0 ? y : Integer.compare(left.getKey().getZ(), right.getKey().getZ());
        }
    }
}
