package dev.gravesown.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.gravesown.Gravesown;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/** Opens the selected item's dependency graph without changing container state. */
@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class SurvivorCodexKeyHandler {
    public static final KeyMapping OPEN_RECIPE_CHAIN = new KeyMapping(
            "key.gravesown.open_recipe_chain",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.gravesown"
    );

    private SurvivorCodexKeyHandler() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_RECIPE_CHAIN);
    }

    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!OPEN_RECIPE_CHAIN.matches(event.getKeyCode(), event.getScanCode())
                || !(event.getScreen() instanceof AbstractContainerScreen<?> container)) {
            return;
        }
        Slot slot = container.getSlotUnderMouse();
        if (slot == null || !slot.hasItem()) {
            return;
        }
        SurvivorCodexClientPayloadHandler.requestCreationChain(slot.getItem().getItem());
        event.setCanceled(true);
    }
}
