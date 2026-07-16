package dev.gravesown.client;

import dev.gravesown.network.OpenSurvivorCodexPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import dev.gravesown.network.RequestSurvivorCodexPayload;

public final class SurvivorCodexClientPayloadHandler {
    private static Item pendingCreationTarget;

    private SurvivorCodexClientPayloadHandler() {
    }

    public static void handle(OpenSurvivorCodexPayload payload, IPayloadContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof SurvivorCodexScreen screen) {
            screen.updateProgress(payload.conditionMask(), payload.claimedMask(), payload.newlyClaimedQuest());
            if (pendingCreationTarget != null) {
                screen.openCreationChain(pendingCreationTarget);
            }
        } else {
            minecraft.setScreen(new SurvivorCodexScreen(
                    payload.conditionMask(),
                    payload.claimedMask(),
                    pendingCreationTarget
            ));
        }
        pendingCreationTarget = null;
        if (payload.newlyClaimedQuest() >= 0 && minecraft.player != null) {
            minecraft.player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 1.0F);
        }
    }

    public static void requestCreationChain(Item item) {
        pendingCreationTarget = item;
        PacketDistributor.sendToServer(RequestSurvivorCodexPayload.INSTANCE);
    }
}
