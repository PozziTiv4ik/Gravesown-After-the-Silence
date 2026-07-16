package dev.gravesown.event;

import dev.gravesown.Gravesown;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/** Installs the always-on high-priority data filter for the total-conversion progression tree. */
public final class AdvancementPackEvents {
    private AdvancementPackEvents() {
    }

    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) {
            return;
        }

        event.addPackFinders(
                Gravesown.id("gravesown_advancement_filter"),
                PackType.SERVER_DATA,
                Component.literal("Gravesown Progression"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP
        );
    }
}
