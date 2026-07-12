package dev.gravesown.client;

import dev.gravesown.Gravesown;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Opt-in development runner for clienttest.cmd. The system property is never
 * set by normal client or dedicated-server runs.
 */
@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class ClientSmokeRunner {
    private static final String ENABLED_PROPERTY = "gravesown.clientSmoke.enabled";
    private static final String EXPECTED_SEED_PROPERTY = "gravesown.clientSmoke.expectedSeed";
    private static final ResourceKey<Biome> SOWN_GRAVE = ResourceKey.create(
            Registries.BIOME,
            Gravesown.id("sown_grave")
    );
    private static final int SETTLE_TICKS = 60;

    private static int joinedTicks;
    private static boolean finished;

    private ClientSmokeRunner() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (finished || !Boolean.getBoolean(ENABLED_PROPERTY)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.getSingleplayerServer() == null) {
            return;
        }
        if (++joinedTicks < SETTLE_TICKS) {
            return;
        }

        finished = true;
        try {
            long expectedSeed = Long.parseLong(System.getProperty(EXPECTED_SEED_PROPERTY));
            long actualSeed = client.getSingleplayerServer().overworld().getSeed();
            if (actualSeed != expectedSeed) {
                throw new IllegalStateException("expected seed " + expectedSeed + ", got " + actualSeed);
            }
            if (!client.level.getBiome(client.player.blockPosition()).is(SOWN_GRAVE)) {
                throw new IllegalStateException("player did not join gravesown:sown_grave");
            }

            Gravesown.LOGGER.info(
                    "GRAVESOWN_CLIENT_SMOKE_RESULT status=PASS seed={} biome={}",
                    actualSeed,
                    SOWN_GRAVE.location()
            );
        }
        catch (RuntimeException exception) {
            Gravesown.LOGGER.error("GRAVESOWN_CLIENT_SMOKE_RESULT status=FAIL", exception);
        }
        finally {
            client.stop();
        }
    }
}
