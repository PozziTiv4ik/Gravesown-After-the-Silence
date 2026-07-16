package dev.gravesown.client;

import dev.gravesown.Gravesown;
import java.util.ArrayList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.neoforge.client.event.ScreenEvent;

/** Keeps every new Gravesown save on the supported total-conversion preset. */
public final class WorldCreationLockdown {
    private static final ResourceKey<WorldPreset> AFTER_THE_SILENCE = ResourceKey.create(
            Registries.WORLD_PRESET,
            Gravesown.id("after_the_silence")
    );

    private WorldCreationLockdown() {
    }

    public static void onScreenInitialized(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreateWorldScreen screen)) {
            return;
        }

        WorldCreationUiState state = screen.getUiState();
        enforceSupportedSettings(state);
        state.addListener(WorldCreationLockdown::enforceSupportedSettings);

        String typeLabel = Component.translatable("selectWorld.mapType").getString();
        String customizeLabel = Component.translatable("selectWorld.customizeType").getString();
        String structuresLabel = Component.translatable("selectWorld.mapFeatures").getString();
        String bonusLabel = Component.translatable("selectWorld.bonusItems").getString();
        for (var listener : new ArrayList<>(event.getListenersList())) {
            if (!(listener instanceof AbstractWidget widget)) {
                continue;
            }
            String message = widget.getMessage().getString();
            if (message.contains(bonusLabel) || message.contains(customizeLabel)) {
                event.removeListener(widget);
            } else if (message.contains(typeLabel) || message.contains(structuresLabel)) {
                widget.active = false;
            }
        }
    }

    private static void enforceSupportedSettings(WorldCreationUiState state) {
        state.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET)
                .getHolder(AFTER_THE_SILENCE)
                .filter(holder -> !state.getWorldType().preset().equals(holder))
                .ifPresent(holder -> state.setWorldType(new WorldCreationUiState.WorldTypeEntry(holder)));
        if (!state.isGenerateStructures()) {
            state.setGenerateStructures(true);
        }
        if (state.isBonusChest()) {
            state.setBonusChest(false);
        }
    }
}
