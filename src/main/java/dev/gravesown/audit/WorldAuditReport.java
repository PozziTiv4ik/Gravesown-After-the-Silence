package dev.gravesown.audit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class WorldAuditReport {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public final int schemaVersion = 3;
    public final String createdAtUtc = Instant.now().toString();
    public String status = "RUNNING";
    public String enforcement;
    public String profile;
    public String reportId;
    public String minecraftVersion;
    public String neoForgeVersion;
    public String modVersion;
    public String dimension;
    public String levelName;
    public long seed;
    public long durationMillis;
    public boolean complete;
    public String error;
    public final Contract contract = new Contract();
    public final Region region = new Region();
    public final Totals totals = new Totals();
    public final Map<String, Long> biomeHistogram = new TreeMap<>();
    public final Map<String, Long> biomeProbeHistogram = new TreeMap<>();
    public final Map<String, Long> blockHistogram = new TreeMap<>();
    public final Map<String, Long> fluidHistogram = new TreeMap<>();
    public final Map<String, Long> blockEntityHistogram = new TreeMap<>();
    public final List<String> missingExpectedBiomes = new ArrayList<>();
    public final Violations violations = new Violations();

    public void recordBiome(String id) {
        increment(biomeHistogram, id);
        totals.biomeSamples++;
    }

    public void recordBiomeProbe(String id) {
        increment(biomeProbeHistogram, id);
        totals.biomeProbeSamples++;
    }

    public void recordBlock(String id, boolean air) {
        increment(blockHistogram, id);
        totals.blockPositions++;
        if (!air) {
            totals.nonAirBlocks++;
        }
    }

    public void recordFluid(String id, boolean empty) {
        increment(fluidHistogram, id);
        totals.fluidPositions++;
        if (!empty) {
            totals.nonEmptyFluids++;
        }
    }

    public void recordBlockEntity(String id) {
        increment(blockEntityHistogram, id);
        totals.blockEntities++;
    }

    public void recordViolation(
            String kind,
            String id,
            int x,
            int y,
            int z,
            int chunkX,
            int chunkZ,
            int sampleLimit
    ) {
        violations.total++;
        increment(violations.byKind, kind);
        long sampledForKind = violations.sampledByKind.getOrDefault(kind, 0L);
        if (sampledForKind < sampleLimit) {
            violations.samples.add(new ViolationSample(kind, id, x, y, z, chunkX, chunkZ));
            increment(violations.sampledByKind, kind);
        }
    }

    public void finish(WorldAuditConfig config, long startedNanos) {
        durationMillis = (System.nanoTime() - startedNanos) / 1_000_000L;
        missingExpectedBiomes.clear();
        config.expectedBiomes().stream()
                .map(Object::toString)
                .filter(id -> !biomeHistogram.containsKey(id) && !biomeProbeHistogram.containsKey(id))
                .sorted()
                .forEach(missingExpectedBiomes::add);
        complete = true;
        if (error != null) {
            status = "ERROR";
        } else if (violations.total == 0L) {
            status = "PASS";
        } else if (config.isStrict()) {
            status = "FAIL";
        } else {
            status = "BASELINE_RECORDED";
        }
    }

    public ReportPaths write(Path directory, String reportId) throws IOException {
        Files.createDirectories(directory);
        Path json = directory.resolve("world-audit-" + reportId + ".json");
        Path text = directory.resolve("world-audit-" + reportId + ".txt");
        atomicWrite(json, GSON.toJson(this) + System.lineSeparator());
        atomicWrite(text, toHumanText());
        return new ReportPaths(json, text);
    }

    private String toHumanText() {
        StringBuilder text = new StringBuilder();
        text.append("Gravesown real-chunk world audit\n");
        text.append("Status: ").append(status).append('\n');
        text.append("Enforcement: ").append(enforcement).append('\n');
        text.append("Profile: ").append(profile).append('\n');
        text.append("Seed: ").append(seed).append('\n');
        text.append("Dimension: ").append(dimension).append('\n');
        text.append("Region: ").append(region.chunkCount).append(" FULL chunks around ")
                .append(region.centerChunkX).append(", ").append(region.centerChunkZ).append('\n');
        text.append("Build height: ").append(region.minY).append("..").append(region.maxYExclusive - 1).append('\n');
        text.append("Positions scanned: ").append(totals.blockPositions).append('\n');
        text.append("Biome samples: ").append(totals.biomeSamples).append('\n');
        text.append("Wide biome-source probe: ").append(totals.biomeProbeSamples)
                .append(" samples across +/-").append(region.biomeProbeRadiusChunks)
                .append(" chunks every ").append(region.biomeProbeStepChunks).append(" chunks\n");
        text.append("Missing expected biomes: ")
                .append(missingExpectedBiomes.isEmpty() ? "none" : String.join(", ", missingExpectedBiomes))
                .append('\n');
        text.append("Block entities: ").append(totals.blockEntities).append('\n');
        text.append("Violations: ").append(violations.total).append('\n');
        text.append("Duration: ").append(durationMillis).append(" ms\n");
        if (error != null) {
            text.append("Error: ").append(error).append('\n');
        }
        appendTop(text, "Biomes", biomeHistogram);
        appendTop(text, "Wide biome-source probe", biomeProbeHistogram);
        appendTop(text, "Blocks", blockHistogram);
        appendTop(text, "Fluids", fluidHistogram);
        appendTop(text, "Block entities", blockEntityHistogram);
        if (!violations.samples.isEmpty()) {
            text.append("\nViolation samples (limited per kind):\n");
            for (ViolationSample sample : violations.samples) {
                text.append("- ").append(sample.kind()).append(' ').append(sample.id())
                        .append(" at ").append(sample.x()).append(',').append(sample.y()).append(',').append(sample.z())
                        .append(" (chunk ").append(sample.chunkX()).append(',').append(sample.chunkZ()).append(")\n");
            }
        }
        return text.toString();
    }

    private static void appendTop(StringBuilder text, String label, Map<String, Long> histogram) {
        text.append('\n').append(label).append(" (top 20):\n");
        histogram.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(20)
                .forEach(entry -> text.append("- ").append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append('\n'));
    }

    private static void increment(Map<String, Long> map, String key) {
        map.merge(key, 1L, Long::sum);
    }

    private static void atomicWrite(Path target, String content) throws IOException {
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temporary, content, StandardCharsets.UTF_8);
        try {
            Files.move(
                    temporary,
                    target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException ignored) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static final class Contract {
        public List<String> expectedBiomes = new ArrayList<>();
        public String allowedContentNamespace;
        public List<String> technicalBlockAllowlist = new ArrayList<>();
        public List<String> technicalFluidAllowlist = new ArrayList<>();
        public int violationSampleLimitPerKind;
    }

    public static final class Region {
        public int centerChunkX;
        public int centerChunkZ;
        public int radiusChunks;
        public int chunkCount;
        public int biomeProbeRadiusChunks;
        public int biomeProbeStepChunks;
        public int minY;
        public int maxYExclusive;
    }

    public static final class Totals {
        public long chunks;
        public long blockPositions;
        public long nonAirBlocks;
        public long fluidPositions;
        public long nonEmptyFluids;
        public long biomeSamples;
        public long biomeProbeSamples;
        public long blockEntities;
    }

    public static final class Violations {
        public long total;
        public final Map<String, Long> byKind = new TreeMap<>();
        public final Map<String, Long> sampledByKind = new TreeMap<>();
        public final List<ViolationSample> samples = new ArrayList<>();
    }

    public record ViolationSample(
            String kind,
            String id,
            int x,
            int y,
            int z,
            int chunkX,
            int chunkZ
    ) {
    }

    public record ReportPaths(Path json, Path text) {
    }
}
