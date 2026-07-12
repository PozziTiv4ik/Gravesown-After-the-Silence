package dev.gravesown.audit;

import dev.gravesown.Gravesown;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record WorldAuditConfig(
        Enforcement enforcement,
        String profile,
        String reportId,
        Path reportDirectory,
        int radiusChunks,
        int sampleLimit,
        ResourceLocation requiredBiome
) {
    static final String ENABLED_PROPERTY = "gravesown.worldAudit.enabled";
    private static final String REPORT_DIRECTORY_PROPERTY = "gravesown.worldAudit.reportDir";
    private static final String ENV_ENFORCEMENT = "GRAVESOWN_WORLD_AUDIT_ENFORCEMENT";
    private static final String ENV_PROFILE = "GRAVESOWN_WORLD_AUDIT_PROFILE";
    private static final String ENV_REPORT_ID = "GRAVESOWN_WORLD_AUDIT_REPORT_ID";
    private static final String ENV_RADIUS = "GRAVESOWN_WORLD_AUDIT_RADIUS";
    private static final String ENV_SAMPLE_LIMIT = "GRAVESOWN_WORLD_AUDIT_SAMPLE_LIMIT";
    private static final String ENV_REQUIRED_BIOME = "GRAVESOWN_WORLD_AUDIT_REQUIRED_BIOME";

    private static final int MAX_RADIUS_CHUNKS = 16;
    private static final int MAX_SAMPLE_LIMIT = 500;
    private static final Set<ResourceLocation> TECHNICAL_BLOCK_ALLOWLIST = Set.of(
            ResourceLocation.withDefaultNamespace("air"),
            ResourceLocation.withDefaultNamespace("cave_air")
    );
    private static final Set<ResourceLocation> TECHNICAL_FLUID_ALLOWLIST = Set.of(
            ResourceLocation.withDefaultNamespace("empty")
    );

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
        int samples = boundedInteger(ENV_SAMPLE_LIMIT, 50, 1, MAX_SAMPLE_LIMIT);
        ResourceLocation requiredBiome = ResourceLocation.parse(
                environment(ENV_REQUIRED_BIOME, Gravesown.MOD_ID + ":sown_grave")
        );
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
                samples,
                requiredBiome
        );
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
