package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModEntities;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldgenGameTests {
    private static final ResourceKey<Biome> SOWN_GRAVE = biomeKey("sown_grave");
    private static final ResourceKey<Biome> MOSSWAKE_WOODS = biomeKey("mosswake_woods");
    private static final ResourceKey<Biome> AMBERQUIET_GROVE = biomeKey("amberquiet_grove");
    private static final ResourceKey<Biome> RIBROOT_GROVES = biomeKey("ribroot_groves");
    private static final ResourceKey<Biome> MARROW_RIFTS = biomeKey("marrow_rifts");
    private static final ResourceKey<Biome> SUTURE_MIRE = biomeKey("suture_mire");
    private static final ResourceKey<Biome> GLOAM_SEA = biomeKey("gloam_sea");
    private static final ResourceKey<Biome> EMBER_THICKET = biomeKey("ember_thicket");
    private static final ResourceKey<Biome> PALLID_WEALD = biomeKey("pallid_weald");
    private static final Set<ResourceKey<Biome>> EXPECTED_BIOMES = Set.of(
            SOWN_GRAVE,
            MOSSWAKE_WOODS,
            AMBERQUIET_GROVE,
            RIBROOT_GROVES,
            MARROW_RIFTS,
            SUTURE_MIRE,
            GLOAM_SEA,
            EMBER_THICKET,
            PALLID_WEALD
    );
    private static final ResourceKey<NoiseGeneratorSettings> SOWN_GRAVE_NOISE = ResourceKey.create(
            Registries.NOISE_SETTINGS,
            Gravesown.id("sown_grave")
    );
    private static final ResourceKey<DensityFunction> CLIMATE_WARMTH = densityKey("climate_warmth");
    private static final ResourceKey<DensityFunction> CLIMATE_WETNESS = densityKey("climate_wetness");
    private static final ResourceKey<DensityFunction> SOWN_TERRAIN = densityKey("sown_terrain");
    private static final ResourceKey<WorldPreset> AFTER_THE_SILENCE = ResourceKey.create(
            Registries.WORLD_PRESET,
            Gravesown.id("after_the_silence")
    );
    private static final Map<ResourceKey<Biome>, BiomeContract> BIOME_CONTRACTS = createBiomeContracts();

    private WorldgenGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void biomeRegistriesKeepReviewedFeaturesAndSpawns(GameTestHelper helper) {
        var biomeRegistry = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);

        for (Map.Entry<ResourceKey<Biome>, BiomeContract> entry : BIOME_CONTRACTS.entrySet()) {
            ResourceKey<Biome> biomeKey = entry.getKey();
            BiomeContract contract = entry.getValue();
            Biome biome = biomeRegistry.getOrThrow(biomeKey);
            String biomeId = biomeKey.location().toString();

            helper.assertTrue(!biome.hasPrecipitation(), biomeId + " must not create vanilla rain or snow");
            helper.assertTrue(
                    biome.getGenerationSettings().getCarvingStages().isEmpty(),
                    biomeId + " must not install any carvers"
            );

            Set<ResourceLocation> actualFeatures = biome.getGenerationSettings().features().stream()
                    .flatMap(features -> features.stream())
                    .map(feature -> feature.unwrapKey().orElseThrow(
                            () -> new IllegalStateException(biomeId + " contains an inline placed feature")
                    ).location())
                    .collect(Collectors.toSet());
            helper.assertTrue(
                    actualFeatures.equals(contract.features()),
                    biomeId + " placed features changed: expected " + contract.features() + ", got " + actualFeatures
            );
            helper.assertTrue(
                    actualFeatures.stream().allMatch(id -> Gravesown.MOD_ID.equals(id.getNamespace())),
                    biomeId + " must not contain vanilla or foreign placed features"
            );

            MobSpawnSettings mobSettings = biome.getMobSettings();
            for (MobCategory category : MobCategory.values()) {
                List<MobSpawnSettings.SpawnerData> spawns = mobSettings.getMobs(category).unwrap();
                List<SpawnContract> expectedSpawns = contract.spawns().stream()
                        .filter(expected -> expected.category() == category)
                        .toList();
                helper.assertTrue(
                        spawns.size() == expectedSpawns.size(),
                        biomeId + " " + category + " spawn count changed"
                );
                for (SpawnContract expected : expectedSpawns) {
                    MobSpawnSettings.SpawnerData spawn = spawns.stream()
                            .filter(candidate -> candidate.type == expected.entityType())
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(
                                    biomeId + " is missing native spawn "
                                            + BuiltInRegistries.ENTITY_TYPE.getKey(expected.entityType())
                            ));
                    helper.assertTrue(
                            spawn.minCount == expected.minCount() && spawn.maxCount == expected.maxCount(),
                            biomeId + " native group size changed for "
                                    + BuiltInRegistries.ENTITY_TYPE.getKey(expected.entityType())
                    );
                    helper.assertTrue(
                            spawn.getWeight().asInt() == expected.weight(),
                            biomeId + " spawn weight changed for "
                                    + BuiltInRegistries.ENTITY_TYPE.getKey(expected.entityType())
                    );
                }

                for (MobSpawnSettings.SpawnerData spawn : spawns) {
                    ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(spawn.type);
                    helper.assertTrue(
                            Gravesown.MOD_ID.equals(entityId.getNamespace()),
                            biomeId + " contains foreign spawn " + entityId
                    );
                }
            }
        }

        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void totalConversionNoiseAndPresetKeepTheirContract(GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        NoiseGeneratorSettings noiseSettings = registries
                .registryOrThrow(Registries.NOISE_SETTINGS)
                .getOrThrow(SOWN_GRAVE_NOISE);
        helper.assertTrue(
                noiseSettings.defaultBlock().is(ModBlocks.HUSHSTONE.get()),
                "Hushstone must remain the base terrain block"
        );
        helper.assertTrue(
                noiseSettings.defaultFluid().is(ModBlocks.GLOAMWATER.get()),
                "The terrain generator must fill submerged basins with Gloamwater"
        );
        helper.assertTrue(!noiseSettings.isAquifersEnabled(), "Aquifers must remain disabled");
        helper.assertTrue(!noiseSettings.oreVeinsEnabled(), "Vanilla ore veins must remain disabled");
        helper.assertTrue(noiseSettings.seaLevel() == 54, "The Gloam Sea level must remain 54");
        helper.assertTrue(
                noiseSettings.noiseSettings().minY() == -64 && noiseSettings.noiseSettings().height() == 384,
                "Gravesown terrain must cover the full Overworld build height"
        );

        assertDensityReference(helper, noiseSettings.noiseRouter().temperature(), CLIMATE_WARMTH, "temperature");
        assertDensityReference(helper, noiseSettings.noiseRouter().vegetation(), CLIMATE_WETNESS, "humidity");
        assertDensityReference(
                helper,
                noiseSettings.noiseRouter().initialDensityWithoutJaggedness(),
                SOWN_TERRAIN,
                "initial terrain"
        );
        assertDensityReference(helper, noiseSettings.noiseRouter().finalDensity(), SOWN_TERRAIN, "final terrain");

        helper.assertTrue(
                noiseSettings.noiseRouter().temperature().minValue()
                        < noiseSettings.noiseRouter().temperature().maxValue(),
                "Temperature climate routing must vary spatially"
        );
        helper.assertTrue(
                noiseSettings.noiseRouter().vegetation().minValue()
                        < noiseSettings.noiseRouter().vegetation().maxValue(),
                "Humidity climate routing must vary spatially"
        );
        helper.assertTrue(
                noiseSettings.noiseRouter().finalDensity().minValue()
                        < noiseSettings.noiseRouter().finalDensity().maxValue(),
                "Terrain density must retain vertical and noise variation"
        );

        WorldPreset preset = registries.registryOrThrow(Registries.WORLD_PRESET).getOrThrow(AFTER_THE_SILENCE);
        LevelStem overworld = preset.overworld().orElseThrow(
                () -> new IllegalStateException("After the Silence is missing its Overworld")
        );
        helper.assertTrue(
                overworld.generator() instanceof NoiseBasedChunkGenerator,
                "After the Silence must use the reviewed noise generator"
        );
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator)overworld.generator();
        helper.assertTrue(
                generator.generatorSettings().is(SOWN_GRAVE_NOISE),
                "After the Silence must use gravesown:sown_grave noise settings"
        );
        helper.assertTrue(
                generator.getBiomeSource() instanceof MultiNoiseBiomeSource,
                "After the Silence must select its nine regions with a multi-noise biome source"
        );
        MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource)generator.getBiomeSource();

        Set<ResourceKey<Biome>> possibleBiomes = biomeSource.possibleBiomes().stream()
                .map(holder -> holder.unwrapKey().orElseThrow(
                        () -> new IllegalStateException("After the Silence contains an inline biome")
                ))
                .collect(Collectors.toSet());
        helper.assertTrue(
                possibleBiomes.equals(EXPECTED_BIOMES),
                "After the Silence possible biomes changed: expected " + EXPECTED_BIOMES + ", got " + possibleBiomes
        );

        assertDryClimateRoute(helper, biomeSource, -1.25F, SOWN_GRAVE);
        assertDryClimateRoute(helper, biomeSource, -0.751F, SOWN_GRAVE);
        assertDryClimateRoute(helper, biomeSource, -0.749F, MOSSWAKE_WOODS);
        assertDryClimateRoute(helper, biomeSource, -0.5F, MOSSWAKE_WOODS);
        assertDryClimateRoute(helper, biomeSource, -0.251F, MOSSWAKE_WOODS);
        assertDryClimateRoute(helper, biomeSource, -0.249F, AMBERQUIET_GROVE);
        assertDryClimateRoute(helper, biomeSource, -0.1F, AMBERQUIET_GROVE);

        helper.succeed();
    }

    private static void assertDensityReference(
            GameTestHelper helper,
            DensityFunction function,
            ResourceKey<DensityFunction> expected,
            String label
    ) {
        helper.assertTrue(
                function instanceof DensityFunctions.HolderHolder holder && holder.function().is(expected),
                "The " + label + " router input must reference " + expected.location()
        );
    }

    private static void assertDryClimateRoute(
            GameTestHelper helper,
            MultiNoiseBiomeSource biomeSource,
            float temperature,
            ResourceKey<Biome> expected
    ) {
        ResourceKey<Biome> actual = biomeSource
                .getNoiseBiome(Climate.target(temperature, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F))
                .unwrapKey()
                .orElseThrow(() -> new IllegalStateException("After the Silence contains an inline biome"));
        helper.assertTrue(
                actual.equals(expected),
                "Dry climate temperature " + temperature + " routed to " + actual.location()
                        + " instead of " + expected.location()
        );
    }

    private static Map<ResourceKey<Biome>, BiomeContract> createBiomeContracts() {
        Map<ResourceKey<Biome>, BiomeContract> contracts = new LinkedHashMap<>();
        contracts.put(SOWN_GRAVE, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.HOLLOW_GRAZER.get(), MobCategory.CREATURE, 12, 1, 3),
                        new SpawnContract(ModEntities.ASH_HOPPER.get(), MobCategory.CREATURE, 18, 2, 4),
                        new SpawnContract(ModEntities.GRAVEWING.get(), MobCategory.CREATURE, 8, 1, 2),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "ribroot_trees_lonely",
                        "threadgrass_patches",
                        "pallid_bulb_patches",
                        "cinder_bloom_patches",
                        "gloam_sea_growth",
                        "remnant_graves_rare",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(RIBROOT_GROVES, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.RIBSPRING.get(), MobCategory.CREATURE, 14, 2, 4),
                        new SpawnContract(ModEntities.ROOTBACK.get(), MobCategory.CREATURE, 7, 1, 2),
                        new SpawnContract(ModEntities.BARK_MARTEN.get(), MobCategory.CREATURE, 4, 1, 1),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "ribroot_trees_sparse",
                        "threadgrass_patches",
                        "ribroot_shoot_patches",
                        "pallid_bulb_patches",
                        "sinew_fern_patches",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(MARROW_RIFTS, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.STITCHTUSK.get(), MobCategory.CREATURE, 9, 1, 2),
                        new SpawnContract(ModEntities.CRAG_RAM.get(), MobCategory.CREATURE, 6, 1, 2),
                        new SpawnContract(ModEntities.RIFT_PUMA.get(), MobCategory.CREATURE, 2, 1, 1),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "marrow_outcrops",
                        "veined_shale_disks",
                        "splintered_marrowstone_disks",
                        "cairnstone_disks",
                        "cairnwood_shrubs",
                        "rift_thorn_patches",
                        "marrow_reed_patches",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(SUTURE_MIRE, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.WOUNDSCENT.get(), MobCategory.MONSTER, 18, 1, 2),
                        new SpawnContract(ModEntities.ROTFIN.get(), MobCategory.WATER_CREATURE, 8, 1, 3),
                        new SpawnContract(ModEntities.MIRE_TOAD.get(), MobCategory.CREATURE, 12, 2, 4),
                        new SpawnContract(ModEntities.REED_LYNX.get(), MobCategory.CREATURE, 2, 1, 1),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "dried_ichor_patches",
                        "suturewood_trees",
                        "mire_frond_patches",
                        "sinew_fern_patches",
                        "gloamwater_puddles_common",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(MOSSWAKE_WOODS, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.RIBSPRING.get(), MobCategory.CREATURE, 13, 2, 4),
                        new SpawnContract(ModEntities.MOSSBOAR.get(), MobCategory.CREATURE, 8, 1, 2),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "mosswake_trees",
                        "mossveil_patches",
                        "threadgrass_patches",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(AMBERQUIET_GROVE, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.HOLLOW_GRAZER.get(), MobCategory.CREATURE, 11, 1, 3),
                        new SpawnContract(ModEntities.AMBER_JAY.get(), MobCategory.CREATURE, 12, 2, 4),
                        new SpawnContract(ModEntities.SUNHORN.get(), MobCategory.CREATURE, 6, 1, 3),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "sunveil_trees",
                        "amber_bloom_patches",
                        "pallid_bulb_patches",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(GLOAM_SEA, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.ROTFIN.get(), MobCategory.WATER_CREATURE, 5, 1, 3),
                        new SpawnContract(ModEntities.VEILFIN.get(), MobCategory.WATER_AMBIENT, 14, 3, 6),
                        new SpawnContract(ModEntities.ROOTSKIMMER.get(), MobCategory.WATER_AMBIENT, 10, 2, 4),
                        new SpawnContract(ModEntities.SILT_RAY.get(), MobCategory.WATER_AMBIENT, 6, 1, 2)
                ),
                featureIds("gloam_sea_growth", "abandoned_camps_very_rare")
        ));
        contracts.put(EMBER_THICKET, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.HOLLOW_GRAZER.get(), MobCategory.CREATURE, 10, 1, 3),
                        new SpawnContract(ModEntities.WOUNDSCENT.get(), MobCategory.MONSTER, 5, 1, 2),
                        new SpawnContract(ModEntities.EMBER_FOX.get(), MobCategory.CREATURE, 4, 1, 2),
                        new SpawnContract(ModEntities.CINDER_FOWL.get(), MobCategory.CREATURE, 10, 2, 3),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "emberbark_trees",
                        "cinder_bloom_patches",
                        "marrow_reed_patches",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        contracts.put(PALLID_WEALD, new BiomeContract(
                List.of(
                        new SpawnContract(ModEntities.RIBSPRING.get(), MobCategory.CREATURE, 12, 2, 4),
                        new SpawnContract(ModEntities.PALLID_HART.get(), MobCategory.CREATURE, 7, 1, 3),
                        ambientVeilfin(),
                        ambientRootskimmer()
                ),
                featureIds(
                        "palevine_trees",
                        "pallid_bulb_patches",
                        "sinew_fern_patches",
                        "threadgrass_patches",
                        "gloam_sea_growth",
                        "ruined_shelters_rare",
                        "abandoned_camps_very_rare"
                )
        ));
        return Map.copyOf(contracts);
    }

    private static Set<ResourceLocation> featureIds(String... ids) {
        return java.util.Arrays.stream(ids).map(Gravesown::id).collect(Collectors.toUnmodifiableSet());
    }

    private static SpawnContract ambientVeilfin() {
        return new SpawnContract(ModEntities.VEILFIN.get(), MobCategory.WATER_AMBIENT, 4, 1, 3);
    }

    private static SpawnContract ambientRootskimmer() {
        return new SpawnContract(ModEntities.ROOTSKIMMER.get(), MobCategory.WATER_AMBIENT, 4, 1, 2);
    }

    private static ResourceKey<Biome> biomeKey(String id) {
        return ResourceKey.create(Registries.BIOME, Gravesown.id(id));
    }

    private static ResourceKey<DensityFunction> densityKey(String id) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Gravesown.id(id));
    }

    private record BiomeContract(
            List<SpawnContract> spawns,
            Set<ResourceLocation> features
    ) {
    }

    private record SpawnContract(
            EntityType<?> entityType,
            MobCategory category,
            int weight,
            int minCount,
            int maxCount
    ) {
    }
}
