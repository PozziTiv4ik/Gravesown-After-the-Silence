package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.worldgen.AbandonedCampFeature;
import dev.gravesown.worldgen.RemnantGraveFeature;
import dev.gravesown.worldgen.GloamwaterPondFeature;
import dev.gravesown.worldgen.GloamSeaGrowthFeature;
import dev.gravesown.worldgen.RuinedShelterFeature;
import dev.gravesown.worldgen.GravesownTreeFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Gravesown.MOD_ID);

    public static final DeferredHolder<Feature<?>, RemnantGraveFeature> REMNANT_GRAVE =
            FEATURES.register("remnant_grave", RemnantGraveFeature::new);
    public static final DeferredHolder<Feature<?>, GloamwaterPondFeature> GLOAMWATER_POND =
            FEATURES.register("gloamwater_pond", GloamwaterPondFeature::new);
    public static final DeferredHolder<Feature<?>, GloamSeaGrowthFeature> GLOAM_SEA_GROWTH =
            FEATURES.register("gloam_sea_growth", GloamSeaGrowthFeature::new);
    public static final DeferredHolder<Feature<?>, RuinedShelterFeature> RUINED_SHELTER =
            FEATURES.register("ruined_shelter", RuinedShelterFeature::new);
    public static final DeferredHolder<Feature<?>, AbandonedCampFeature> ABANDONED_CAMP =
            FEATURES.register("abandoned_camp", AbandonedCampFeature::new);
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> RIBROOT_TREE =
            FEATURES.register(
                    "ribroot_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.RIBROOT_STEM,
                            ModBlocks.VEIL_FOLIAGE,
                            GravesownTreeFeature.Shape.RIBROOT_FORKED
                    )
            );
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> EMBERBARK_TREE =
            FEATURES.register(
                    "emberbark_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.EMBERBARK_STEM,
                            ModBlocks.EMBERBARK_FOLIAGE,
                            GravesownTreeFeature.Shape.EMBERBARK_WINDSWEPT
                    )
            );
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> PALEVINE_TREE =
            FEATURES.register(
                    "palevine_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.PALEVINE_STEM,
                            ModBlocks.PALEVINE_FOLIAGE,
                            GravesownTreeFeature.Shape.PALEVINE_TALL
                    )
            );
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> CAIRNWOOD_TREE =
            FEATURES.register(
                    "cairnwood_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.CAIRNWOOD_STEM,
                            ModBlocks.CAIRNWOOD_FOLIAGE,
                            GravesownTreeFeature.Shape.CAIRNWOOD_SHRUB
                    )
            );
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> SUTUREWOOD_TREE =
            FEATURES.register(
                    "suturewood_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.SUTUREWOOD_STEM,
                            ModBlocks.SUTUREWOOD_FOLIAGE,
                            GravesownTreeFeature.Shape.SUTUREWOOD_ROOTED
                    )
            );
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> MOSSWAKE_TREE =
            FEATURES.register(
                    "mosswake_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.MOSSWAKE_STEM,
                            ModBlocks.MOSSWAKE_FOLIAGE,
                            GravesownTreeFeature.Shape.MOSSWAKE_BROAD
                    )
            );
    public static final DeferredHolder<Feature<?>, GravesownTreeFeature> SUNVEIL_TREE =
            FEATURES.register(
                    "sunveil_tree",
                    () -> new GravesownTreeFeature(
                            ModBlocks.SUNVEIL_STEM,
                            ModBlocks.SUNVEIL_FOLIAGE,
                            GravesownTreeFeature.Shape.SUNVEIL_CROWN
                    )
            );

    private ModFeatures() {
    }

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}
