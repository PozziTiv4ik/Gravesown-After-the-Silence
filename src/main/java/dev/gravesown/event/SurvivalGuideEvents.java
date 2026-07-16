package dev.gravesown.event;

import dev.gravesown.entity.HollowGrazer;
import dev.gravesown.network.SurvivorCodexProgress;
import dev.gravesown.registry.ModAttachments;
import dev.gravesown.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class SurvivalGuideEvents {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            giveCodexIfEligible(serverPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && event.getNewGameMode() == GameType.SURVIVAL) {
            giveCodexIfEligible(serverPlayer, GameType.SURVIVAL);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof HollowGrazer
                && event.getSource().getEntity() instanceof ServerPlayer player) {
            SurvivorCodexProgress.markCondition(player, SurvivorCodexProgress.GRAZER);
        }
    }

    public static boolean giveCodexIfEligible(ServerPlayer player) {
        return giveCodexIfEligible(player, player.gameMode.getGameModeForPlayer());
    }

    static boolean giveCodexIfEligible(ServerPlayer player, GameType intendedGameMode) {
        if (intendedGameMode != GameType.SURVIVAL
                || player.getData(ModAttachments.RECEIVED_SURVIVOR_CODEX)) {
            return false;
        }

        ItemStack codex = new ItemStack(ModItems.SURVIVOR_CODEX.get());
        if (!player.getInventory().add(codex)) {
            ItemEntity droppedCodex = player.drop(codex, false);
            if (droppedCodex == null) {
                return false;
            }
            droppedCodex.setNoPickUpDelay();
            droppedCodex.setTarget(player.getUUID());
        }
        player.setData(ModAttachments.RECEIVED_SURVIVOR_CODEX, true);
        SurvivorCodexProgress.markCondition(player, SurvivorCodexProgress.AWAKENING);
        return true;
    }

    public SurvivalGuideEvents() {
    }
}
