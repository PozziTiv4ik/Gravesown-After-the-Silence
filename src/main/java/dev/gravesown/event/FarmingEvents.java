package dev.gravesown.event;

import dev.gravesown.registry.ModBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;

/** Server-authoritative soil conversion for Gravesown hoes. */
public final class FarmingEvents {
    @SubscribeEvent
    public void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() != ItemAbilities.HOE_TILL
                || !event.getLevel().getBlockState(event.getPos().above()).isAir()) {
            return;
        }
        if (event.getState().is(ModBlocks.ASHEN_SOD.get())
                || event.getState().is(ModBlocks.GRAVE_LOAM.get())) {
            event.setFinalState(ModBlocks.GLOAM_FARMLAND.get().defaultBlockState());
        }
    }
}
