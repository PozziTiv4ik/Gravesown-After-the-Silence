package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.audit.WorldAuditConfig;
import dev.gravesown.audit.WorldAuditReport;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldAuditContractGameTests {
    private WorldAuditContractGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void strictWorldAuditRejectsVanillaContent(GameTestHelper helper) {
        WorldAuditConfig strict = config(WorldAuditConfig.Enforcement.STRICT);

        helper.assertTrue(
                strict.isAllowedBlock(ResourceLocation.withDefaultNamespace("air")),
                "Air must be on the technical block allowlist"
        );
        helper.assertTrue(
                strict.isAllowedBlock(ResourceLocation.withDefaultNamespace("cave_air")),
                "Cave air must be on the technical block allowlist"
        );
        helper.assertTrue(
                strict.isAllowedBlock(Gravesown.id("future_hushstone")),
                "Gravesown blocks must pass the namespace contract"
        );
        helper.assertTrue(
                !strict.isAllowedBlock(ResourceLocation.withDefaultNamespace("stone")),
                "Vanilla stone must fail the strict contract"
        );
        helper.assertTrue(
                !strict.isAllowedBlock(ResourceLocation.withDefaultNamespace("void_air")),
                "Unreviewed technical blocks must not be silently allowed"
        );
        helper.assertTrue(
                strict.isAllowedFluid(ResourceLocation.withDefaultNamespace("empty")),
                "The empty fluid state must be technically allowed"
        );
        helper.assertTrue(
                !strict.isAllowedFluid(ResourceLocation.withDefaultNamespace("water")),
                "Vanilla water must fail the strict contract"
        );
        helper.assertTrue(
                !strict.isAllowedBlockEntity(ResourceLocation.withDefaultNamespace("chest")),
                "Vanilla block entities must fail the strict contract"
        );
        helper.assertTrue(
                strict.expectedBiomes().equals(Set.of(
                        Gravesown.id("sown_grave"),
                        Gravesown.id("mosswake_woods"),
                        Gravesown.id("amberquiet_grove"),
                        Gravesown.id("ribroot_groves"),
                        Gravesown.id("marrow_rifts"),
                        Gravesown.id("suture_mire"),
                        Gravesown.id("gloam_sea"),
                        Gravesown.id("ember_thicket"),
                        Gravesown.id("pallid_weald")
                )),
                "The audit contract must contain exactly the nine reviewed Gravesown biomes"
        );
        helper.assertTrue(
                strict.isAllowedBiome(Gravesown.id("marrow_rifts")),
                "Every reviewed Gravesown biome must pass the exact biome allowlist"
        );
        helper.assertTrue(
                !strict.isAllowedBiome(ResourceLocation.withDefaultNamespace("plains")),
                "Foreign biomes must fail the exact biome allowlist"
        );

        WorldAuditReport strictReport = new WorldAuditReport();
        strictReport.recordViolation("block", "minecraft:stone", 0, 0, 0, 0, 0, strict.sampleLimit());
        strictReport.finish(strict, System.nanoTime());
        helper.assertTrue("FAIL".equals(strictReport.status), "Strict mode must fail when a violation exists");

        WorldAuditConfig baseline = config(WorldAuditConfig.Enforcement.BASELINE);
        WorldAuditReport baselineReport = new WorldAuditReport();
        baselineReport.recordViolation("block", "minecraft:stone", 0, 0, 0, 0, 0, baseline.sampleLimit());
        baselineReport.finish(baseline, System.nanoTime());
        helper.assertTrue(
                "BASELINE_RECORDED".equals(baselineReport.status),
                "Baseline mode must record violations without claiming a strict pass"
        );

        WorldAuditReport coverageReport = new WorldAuditReport();
        coverageReport.recordBiome(Gravesown.id("sown_grave").toString());
        coverageReport.recordBiome(Gravesown.id("marrow_rifts").toString());
        coverageReport.recordBiomeProbe(Gravesown.id("ribroot_groves").toString());
        coverageReport.finish(strict, System.nanoTime());
        helper.assertTrue(
                coverageReport.missingExpectedBiomes.equals(List.of(
                        Gravesown.id("amberquiet_grove").toString(),
                        Gravesown.id("ember_thicket").toString(),
                        Gravesown.id("gloam_sea").toString(),
                        Gravesown.id("mosswake_woods").toString(),
                        Gravesown.id("pallid_weald").toString(),
                        Gravesown.id("suture_mire").toString()
                )),
                "Schema v3 must combine deep and wide biome samples when reporting coverage"
        );
        helper.succeed();
    }

    private static WorldAuditConfig config(WorldAuditConfig.Enforcement enforcement) {
        return new WorldAuditConfig(
                enforcement,
                "gametest",
                "gametest",
                Path.of("build", "reports", "gravesown", "world-audit"),
                2,
                8,
                2,
                50,
                Set.of(
                        Gravesown.id("sown_grave"),
                        Gravesown.id("mosswake_woods"),
                        Gravesown.id("amberquiet_grove"),
                        Gravesown.id("ribroot_groves"),
                        Gravesown.id("marrow_rifts"),
                        Gravesown.id("suture_mire"),
                        Gravesown.id("gloam_sea"),
                        Gravesown.id("ember_thicket"),
                        Gravesown.id("pallid_weald")
                )
        );
    }
}
