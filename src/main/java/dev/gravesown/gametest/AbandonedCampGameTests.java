package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModFeatures;
import dev.gravesown.worldgen.AbandonedCampFeature;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AbandonedCampGameTests {
    private static final ResourceKey<ConfiguredFeature<?, ?>> ABANDONED_CAMP = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Gravesown.id("abandoned_camp")
    );
    private static final ResourceKey<LootTable> CAMP_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, Gravesown.id("chests/abandoned_camp")
    );

    private AbandonedCampGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void rarityAndEveryBiomeTimberPaletteStayReviewed(GameTestHelper helper) {
        ConfiguredFeature<?, ?> configured = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ABANDONED_CAMP);
        helper.assertTrue(configured.feature() == ModFeatures.ABANDONED_CAMP.get(),
                "The abandoned camp configured feature must use the registered bounded feature");
        helper.assertTrue(
                configured.config() instanceof AbandonedCampFeature.Configuration configuration
                        && configuration.rarity() == AbandonedCampFeature.REVIEWED_CHUNK_RARITY
                        && configuration.rarity() == 192,
                "Abandoned camps must retain the reviewed one-in-192 chunk attempt rate"
        );
        helper.assertTrue(AbandonedCampFeature.LAYOUT_VARIANTS == 3,
                "Abandoned camps must retain three deterministic layout variants");

        Map<String, String> palettes = new LinkedHashMap<>();
        palettes.put("sown_grave", "ribroot");
        palettes.put("ribroot_groves", "ribroot");
        palettes.put("marrow_rifts", "cairnwood");
        palettes.put("suture_mire", "suturewood");
        palettes.put("mosswake_woods", "mosswake");
        palettes.put("amberquiet_grove", "sunveil");
        palettes.put("ember_thicket", "emberbark");
        palettes.put("pallid_weald", "palevine");
        palettes.put("gloam_sea", "suturewood");
        for (Map.Entry<String, String> entry : palettes.entrySet()) {
            ResourceLocation biomeId = Gravesown.id(entry.getKey());
            helper.assertTrue(
                    AbandonedCampFeature.woodFamilyForBiome(biomeId).equals(entry.getValue()),
                    biomeId + " must build abandoned camps from " + entry.getValue()
            );
        }
        helper.succeed();
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 50)
    public static void landCampPlacesBoundedFenceShelterAndLootCrate(GameTestHelper helper) {
        BlockPos relativeCenter = new BlockPos(5, 1, 5);
        BlockPos center = helper.absolutePos(relativeCenter);
        for (int z = -4; z <= 4; z++) {
            for (int x = -5; x <= 5; x++) {
                helper.getLevel().setBlockAndUpdate(center.offset(x, -1, z), ModBlocks.ASHEN_SOD.get().defaultBlockState());
                for (int y = 0; y <= 5; y++) {
                    helper.getLevel().setBlockAndUpdate(center.offset(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }

        boolean placed = AbandonedCampFeature.placeCampAt(
                helper.getLevel(), RandomSource.create(0x43414D505F3031L), center
        );
        helper.assertTrue(placed, "The bounded abandoned camp must place on a flat reviewed surface");

        int fences = 0;
        int timber = 0;
        int stones = 0;
        int crates = 0;
        for (int z = -4; z <= 4; z++) {
            for (int x = -5; x <= 5; x++) {
                for (int y = -1; y <= 4; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    Block block = helper.getLevel().getBlockState(pos).getBlock();
                    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                    if (id.getPath().endsWith("_fence")) fences++;
                    if (id.getPath().endsWith("_stem") || id.getPath().endsWith("_planks")
                            || id.getPath().endsWith("_slab")) timber++;
                    if (block == ModBlocks.HUSHSTONE.get() || block == ModBlocks.CAIRNSTONE.get()) stones++;
                    if (block == ModBlocks.RELIQUARY_CRATE.get()) {
                        crates++;
                        helper.assertTrue(helper.getLevel().getBlockEntity(pos) instanceof RandomizableContainer container
                                        && CAMP_LOOT.equals(container.getLootTable()),
                                "The camp Reliquary Crate must retain its server-side food/supply loot table");
                    }
                }
            }
        }
        helper.assertTrue(fences >= 24, "The abandoned camp must have a readable enclosing fence");
        helper.assertTrue(timber >= 15, "The abandoned camp must have a regional timber lean-to");
        helper.assertTrue(stones >= 55, "The abandoned camp must have a complete bounded stone foundation");
        helper.assertTrue(crates == 1, "The abandoned camp must contain exactly one Reliquary Crate");
        helper.succeed();
    }
}
