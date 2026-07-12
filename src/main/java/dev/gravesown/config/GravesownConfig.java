package dev.gravesown.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GravesownConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue REMOVE_EXISTING_VANILLA_MOBS = BUILDER
            .comment("Remove vanilla mobs when they enter a loaded server level.")
            .define("world.removeExistingVanillaMobs", true);

    public static final ModConfigSpec.BooleanValue REPLACE_NATURAL_VANILLA_SPAWNS = BUILDER
            .comment("Replace eligible vanilla natural spawns with Gravesown wildlife.")
            .define("world.replaceNaturalVanillaSpawns", true);

    public static final ModConfigSpec.BooleanValue PRESERVE_BOSSES = BUILDER
            .comment("Keep vanilla bosses until Gravesown has progression-safe replacements.")
            .define("world.preserveVanillaBosses", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private GravesownConfig() {
    }
}
