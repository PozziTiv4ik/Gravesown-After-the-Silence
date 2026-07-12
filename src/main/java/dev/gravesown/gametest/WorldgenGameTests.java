package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModEntities;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldgenGameTests {
    private static final ResourceKey<Biome> SOWN_GRAVE = ResourceKey.create(
            Registries.BIOME,
            Gravesown.id("sown_grave")
    );
    private static final ResourceKey<NoiseGeneratorSettings> SOWN_GRAVE_NOISE = ResourceKey.create(
            Registries.NOISE_SETTINGS,
            Gravesown.id("sown_grave")
    );
    private static final ResourceKey<WorldPreset> AFTER_THE_SILENCE = ResourceKey.create(
            Registries.WORLD_PRESET,
            Gravesown.id("after_the_silence")
    );

    private WorldgenGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void totalConversionWorldgenRegistriesKeepTheirContract(GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        Biome biome = registries.registryOrThrow(Registries.BIOME).getOrThrow(SOWN_GRAVE);

        helper.assertTrue(!biome.hasPrecipitation(), "Sown Grave must not create vanilla rain or snow");
        helper.assertTrue(
                biome.getGenerationSettings().getCarvingStages().isEmpty(),
                "Sown Grave must not install any carvers"
        );
        helper.assertTrue(
                biome.getGenerationSettings().features().stream().allMatch(features -> features.size() == 0),
                "Sown Grave must not install vanilla or unreviewed placed features"
        );

        MobSpawnSettings mobSettings = biome.getMobSettings();
        for (MobCategory category : MobCategory.values()) {
            List<MobSpawnSettings.SpawnerData> spawns = mobSettings.getMobs(category).unwrap();
            if (category == MobCategory.CREATURE) {
                helper.assertTrue(spawns.size() == 1, "Sown Grave must have exactly one creature spawn entry");
                MobSpawnSettings.SpawnerData spawn = spawns.getFirst();
                helper.assertTrue(
                        spawn.type == ModEntities.HOLLOW_GRAZER.get(),
                        "Hollow Grazer must be the only natural creature"
                );
                helper.assertTrue(
                        spawn.minCount == 1 && spawn.maxCount == 3,
                        "Hollow Grazer natural groups must remain 1-3 creatures"
                );
            }
            else {
                helper.assertTrue(spawns.isEmpty(), "Sown Grave must keep " + category + " spawns empty");
            }
        }

        NoiseGeneratorSettings noiseSettings = registries
                .registryOrThrow(Registries.NOISE_SETTINGS)
                .getOrThrow(SOWN_GRAVE_NOISE);
        helper.assertTrue(
                noiseSettings.defaultBlock().is(ModBlocks.HUSHSTONE.get()),
                "Hushstone must remain the base terrain block"
        );
        helper.assertTrue(
                noiseSettings.defaultFluid().equals(Blocks.AIR.defaultBlockState()),
                "The terrain generator must use technical air instead of a vanilla fluid"
        );
        helper.assertTrue(!noiseSettings.isAquifersEnabled(), "Aquifers must remain disabled");
        helper.assertTrue(!noiseSettings.oreVeinsEnabled(), "Vanilla ore veins must remain disabled");
        helper.assertTrue(noiseSettings.seaLevel() == 0, "The fluidless terrain sea level must remain zero");
        helper.assertTrue(
                noiseSettings.noiseSettings().minY() == -64 && noiseSettings.noiseSettings().height() == 384,
                "Sown Grave terrain must cover the full Overworld build height"
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
                generator.getBiomeSource() instanceof FixedBiomeSource,
                "After the Silence must use a fixed single-biome source"
        );
        Set<Holder<Biome>> possibleBiomes = generator.getBiomeSource().possibleBiomes();
        helper.assertTrue(possibleBiomes.size() == 1, "After the Silence must expose exactly one possible biome");
        helper.assertTrue(
                possibleBiomes.iterator().next().is(SOWN_GRAVE),
                "The only possible biome must be gravesown:sown_grave"
        );

        helper.succeed();
    }
}
