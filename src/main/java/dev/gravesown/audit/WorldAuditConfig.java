package dev.gravesown.audit;

import dev.gravesown.Gravesown;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public record WorldAuditConfig(
        Enforcement enforcement,
        String profile,
        String reportId,
        Path reportDirectory,
        int radiusChunks,
        int biomeProbeRadiusChunks,
        int biomeProbeStepChunks,
        int sampleLimit,
        Set<ResourceLocation> expectedBiomes
) {
    static final String ENABLED_PROPERTY = "gravesown.worldAudit.enabled";
    private static final String REPORT_DIRECTORY_PROPERTY = "gravesown.worldAudit.reportDir";
    private static final String ENV_ENFORCEMENT = "GRAVESOWN_WORLD_AUDIT_ENFORCEMENT";
    private static final String ENV_PROFILE = "GRAVESOWN_WORLD_AUDIT_PROFILE";
    private static final String ENV_REPORT_ID = "GRAVESOWN_WORLD_AUDIT_REPORT_ID";
    private static final String ENV_RADIUS = "GRAVESOWN_WORLD_AUDIT_RADIUS";
    private static final String ENV_BIOME_PROBE_RADIUS = "GRAVESOWN_WORLD_AUDIT_BIOME_PROBE_RADIUS";
    private static final String ENV_BIOME_PROBE_STEP = "GRAVESOWN_WORLD_AUDIT_BIOME_PROBE_STEP";
    private static final String ENV_SAMPLE_LIMIT = "GRAVESOWN_WORLD_AUDIT_SAMPLE_LIMIT";
    private static final String ENV_EXPECTED_BIOMES = "GRAVESOWN_WORLD_AUDIT_EXPECTED_BIOMES";
    private static final String LEGACY_ENV_REQUIRED_BIOME = "GRAVESOWN_WORLD_AUDIT_REQUIRED_BIOME";

    private static final int MAX_RADIUS_CHUNKS = 16;
    private static final int MAX_BIOME_PROBE_RADIUS_CHUNKS = 512;
    private static final int MAX_BIOME_PROBE_STEP_CHUNKS = 64;
    private static final int MAX_SAMPLE_LIMIT = 500;
    private static final Set<ResourceLocation> TECHNICAL_BLOCK_ALLOWLIST = Set.of(
            ResourceLocation.withDefaultNamespace("air"),
            ResourceLocation.withDefaultNamespace("cave_air")
    );
    private static final Set<ResourceLocation> TECHNICAL_FLUID_ALLOWLIST = Set.of(
            ResourceLocation.withDefaultNamespace("empty")
    );
    private static final String DEFAULT_EXPECTED_BIOMES = String.join(",",
            Gravesown.MOD_ID + ":sown_grave",
            Gravesown.MOD_ID + ":mosswake_woods",
            Gravesown.MOD_ID + ":amberquiet_grove",
            Gravesown.MOD_ID + ":ribroot_groves",
            Gravesown.MOD_ID + ":marrow_rifts",
            Gravesown.MOD_ID + ":suture_mire",
            Gravesown.MOD_ID + ":gloam_sea",
            Gravesown.MOD_ID + ":ember_thicket",
            Gravesown.MOD_ID + ":pallid_weald"
    );

    public WorldAuditConfig {
        expectedBiomes = Set.copyOf(expectedBiomes);
        if (expectedBiomes.isEmpty()) {
            throw new IllegalArgumentException("expectedBiomes must contain at least one biome");
        }
    }

    public static boolean isEnabled() {
        return Boolean.getBoolean(ENABLED_PROPERTY);
    }

    public static WorldAuditConfig fromEnvironment() {
        String enforcementText = environment(ENV_ENFORCEMENT, "baseline").toLowerCase(Locale.ROOT);
        Enforcement enforcement = switch (enforcementText) {
            case "baseline" -> Enforcement.BASELINE;
            case "strict" -> Enforcement.STRICT;
            default -> throw new IllegalArgumentException(
                    ENV_ENFORCEMENT + " must be baseline or strict, got " + enforcementText
            );
        };

        String profile = safeId(environment(ENV_PROFILE, "smoke"), "smoke");
        String reportId = safeId(environment(ENV_REPORT_ID, profile), profile);
        int radius = boundedInteger(ENV_RADIUS, 2, 0, MAX_RADIUS_CHUNKS);
        int biomeProbeRadius = boundedInteger(
                ENV_BIOME_PROBE_RADIUS,
                0,
                0,
                MAX_BIOME_PROBE_RADIUS_CHUNKS
        );
        int biomeProbeStep = boundedInteger(
                ENV_BIOME_PROBE_STEP,
                4,
                1,
                MAX_BIOME_PROBE_STEP_CHUNKS
        );
        int samples = boundedInteger(ENV_SAMPLE_LIMIT, 50, 1, MAX_SAMPLE_LIMIT);
        Set<ResourceLocation> expectedBiomes = expectedBiomesFromEnvironment();
        Path reportDirectory = Path.of(System.getProperty(
                REPORT_DIRECTORY_PROPERTY,
                "build/reports/gravesown/world-audit"
        )).toAbsolutePath().normalize();

        return new WorldAuditConfig(
                enforcement,
                profile,
                reportId,
                reportDirectory,
                radius,
                biomeProbeRadius,
                biomeProbeStep,
                samples,
                expectedBiomes
        );
    }

    public boolean isAllowedBiome(ResourceLocation id) {
        return expectedBiomes.contains(id);
    }

    public boolean isAllowedBlock(ResourceLocation id) {
        return Gravesown.MOD_ID.equals(id.getNamespace()) || TECHNICAL_BLOCK_ALLOWLIST.contains(id);
    }

    public boolean isAllowedFluid(ResourceLocation id) {
        return Gravesown.MOD_ID.equals(id.getNamespace()) || TECHNICAL_FLUID_ALLOWLIST.contains(id);
    }

    public boolean isAllowedBlockEntity(ResourceLocation id) {
        return Gravesown.MOD_ID.equals(id.getNamespace());
    }

    public Set<ResourceLocation> technicalBlockAllowlist() {
        return TECHNICAL_BLOCK_ALLOWLIST;
    }

    public Set<ResourceLocation> technicalFluidAllowlist() {
        return TECHNICAL_FLUID_ALLOWLIST;
    }

    public boolean isStrict() {
        return enforcement == Enforcement.STRICT;
    }

    private static String environment(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static Set<ResourceLocation> expectedBiomesFromEnvironment() {
        String configured = System.getenv(ENV_EXPECTED_BIOMES);
        if (configured == null || configured.isBlank()) {
            String legacyRequiredBiome = System.getenv(LEGACY_ENV_REQUIRED_BIOME);
            configured = legacyRequiredBiome == null || legacyRequiredBiome.isBlank()
                    ? DEFAULT_EXPECTED_BIOMES
                    : legacyRequiredBiome;
        }

        Set<ResourceLocation> expected = Arrays.stream(configured.split(",", -1))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(ResourceLocation::parse)
                .collect(Collectors.toUnmodifiableSet());
        if (expected.isEmpty()) {
            throw new IllegalArgumentException(ENV_EXPECTED_BIOMES + " must contain at least one biome id");
        }
        return expected;
    }

    private static int boundedInteger(String name, int fallback, int minimum, int maximum) {
        String text = environment(name, Integer.toString(fallback));
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be an integer, got " + text, exception);
        }
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    name + " must be between " + minimum + " and " + maximum + ", got " + value
            );
        }
        return value;
    }

    private static String safeId(String value, String fallback) {
        String safe = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
        return safe.isBlank() ? fallback : safe;
    }

    public enum Enforcement {
        BASELINE,
        STRICT
    }
}
