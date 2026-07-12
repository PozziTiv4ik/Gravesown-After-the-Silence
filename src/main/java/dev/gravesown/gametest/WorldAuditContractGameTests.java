package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.audit.WorldAuditConfig;
import dev.gravesown.audit.WorldAuditReport;
import java.nio.file.Path;
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
        helper.succeed();
    }

    private static WorldAuditConfig config(WorldAuditConfig.Enforcement enforcement) {
        return new WorldAuditConfig(
                enforcement,
                "gametest",
                "gametest",
                Path.of("build", "reports", "gravesown", "world-audit"),
                2,
                50,
                Gravesown.id("sown_grave")
        );
    }
}
