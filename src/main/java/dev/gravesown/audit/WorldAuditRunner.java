package dev.gravesown.audit;

import dev.gravesown.Gravesown;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public final class WorldAuditRunner {
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        if (!WorldAuditConfig.isEnabled()) {
            return;
        }

        try {
            WorldAuditConfig config = WorldAuditConfig.fromEnvironment();
            Gravesown.LOGGER.info(
                    "Starting Gravesown {} world audit: radius={} chunks, reportId={}",
                    config.enforcement().name().toLowerCase(),
                    config.radiusChunks(),
                    config.reportId()
            );
            WorldAuditReport report = WorldAuditScanner.scan(event.getServer(), config);
            WorldAuditReport.ReportPaths paths = report.write(config.reportDirectory(), config.reportId());
            Gravesown.LOGGER.info(
                    "GRAVESOWN_WORLD_AUDIT_RESULT status={} violations={} json={} text={}",
                    report.status,
                    report.violations.total,
                    paths.json(),
                    paths.text()
            );
        } catch (Throwable throwable) {
            Gravesown.LOGGER.error("Could not complete or write the Gravesown world audit", throwable);
        } finally {
            // ServerStartedEvent runs on the server thread before the main tick loop.
            // Setting running=false here performs the normal save/close path without
            // killing Gradle or leaving a Java process behind.
            event.getServer().halt(false);
        }
    }
}
