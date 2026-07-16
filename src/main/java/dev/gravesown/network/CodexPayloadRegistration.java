package dev.gravesown.network;

import dev.gravesown.Gravesown;
import dev.gravesown.client.SurvivorCodexClientPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import dev.gravesown.registry.ModItems;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Gravesown.MOD_ID)
public final class CodexPayloadRegistration {
    private static final String PROTOCOL_VERSION = "3";

    private CodexPayloadRegistration() {
    }

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                OpenSurvivorCodexPayload.TYPE,
                OpenSurvivorCodexPayload.STREAM_CODEC,
                CodexPayloadRegistration::handleClientbound
        );
        registrar.playToServer(
                ClaimCodexQuestPayload.TYPE,
                ClaimCodexQuestPayload.STREAM_CODEC,
                CodexPayloadRegistration::handleClaim
        );
        registrar.playToServer(
                RequestSurvivorCodexPayload.TYPE,
                RequestSurvivorCodexPayload.STREAM_CODEC,
                CodexPayloadRegistration::handleOpenRequest
        );
    }

    /**
     * Keep the registration lambda owned by this common class. The client handler
     * symbol is resolved only if a client actually receives this clientbound payload.
     */
    private static void handleClientbound(OpenSurvivorCodexPayload payload, IPayloadContext context) {
        SurvivorCodexClientPayloadHandler.handle(payload, context);
    }

    private static void handleClaim(ClaimCodexQuestPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        context.enqueueWork(() -> {
            boolean claimed = SurvivorCodexProgress.claim(player, payload.questIndex());
            PacketDistributor.sendToPlayer(
                    player,
                    new OpenSurvivorCodexPayload(
                            SurvivorCodexProgress.refreshConditions(player),
                            SurvivorCodexProgress.claimedMask(player),
                            claimed ? payload.questIndex() : -1
                    )
            );
        });
    }

    private static void handleOpenRequest(RequestSurvivorCodexPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        context.enqueueWork(() -> {
            boolean ownsCodex = player.getInventory().contains(stack -> stack.is(ModItems.SURVIVOR_CODEX.get()));
            if (!ownsCodex) {
                return;
            }
            sendSnapshot(player, -1);
        });
    }

    static void sendSnapshot(ServerPlayer player, int newlyClaimedQuest) {
        PacketDistributor.sendToPlayer(
                player,
                new OpenSurvivorCodexPayload(
                        SurvivorCodexProgress.refreshConditions(player),
                        SurvivorCodexProgress.claimedMask(player),
                        newlyClaimedQuest
                )
        );
    }
}
