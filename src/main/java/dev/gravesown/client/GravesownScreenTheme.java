package dev.gravesown.client;

import dev.gravesown.Gravesown;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Applies the Gravesown visual language to non-gameplay screens while leaving
 * every vanilla screen's behavior, accessibility and navigation intact.
 *
 * <p>The pinned NeoForge background event draws this layer after a screen has
 * cleared its background but before its widgets and tooltips. Global widget
 * sprites in the resource pack provide the matching hard-edged controls.</p>
 */
@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class GravesownScreenTheme {
    private static final ResourceLocation BACKGROUND =
            Gravesown.id("textures/gui/screen_background.png");
    private static final Component BRAND = Component.translatable("itemGroup.gravesown.main");
    private static final Component SUBTITLE = Component.translatable("generator.gravesown.after_the_silence");
    private static final int TEXTURE_WIDTH = 1672;
    private static final int TEXTURE_HEIGHT = 941;
    private static final int ACCENT = GravesownUiPalette.BORDER;
    private static final int TEXT = GravesownUiPalette.TEXT;

    private static final Set<String> THEMED_SCREENS = Set.of(
            "SelectWorldScreen",
            "CreateWorldScreen",
            "EditWorldScreen",
            "OptionsScreen",
            "VideoSettingsScreen",
            "LanguageSelectScreen",
            "ControlsScreen",
            "KeyBindsScreen",
            "SkinCustomizationScreen",
            "SoundOptionsScreen",
            "ChatOptionsScreen",
            "AccessibilityOptionsScreen",
            "PackSelectionScreen",
            "TelemetryInfoScreen",
            "ReceivingLevelScreen",
            "ProgressScreen",
            "LevelLoadingScreen",
            "ConnectScreen",
            "DisconnectedScreen",
            "GenericDirtMessageScreen",
            "JoinMultiplayerScreen",
            "SafetyScreen",
            "RealmsMainScreen"
    );

    private GravesownScreenTheme() {
    }

    /**
     * The event is present in the project's pinned NeoForge 21.1 API and is
     * intentionally isolated here so a future loader upgrade has one migration
     * point. It is not used for gameplay logic.
     */
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void onBackgroundRendered(ScreenEvent.BackgroundRendered event) {
        Screen screen = event.getScreen();
        if (screen instanceof TitleScreen || !THEMED_SCREENS.contains(screen.getClass().getSimpleName())) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        drawCoverTexture(graphics, screen.width, screen.height);
        graphics.fillGradient(0, 0, screen.width, screen.height, 0x34071423, 0xA6071423);

        int inset = Math.max(12, Math.min(30, screen.width / 32));
        drawFrame(graphics, inset, inset, screen.width - inset, screen.height - inset);

        if (screen.width >= 560) {
            graphics.drawString(Minecraft.getInstance().font, BRAND, inset + 12, inset + 10, TEXT, true);
            graphics.drawString(Minecraft.getInstance().font, SUBTITLE, inset + 12, inset + 23,
                    GravesownUiPalette.MUTED, true);
        }
    }

    private static void drawCoverTexture(GuiGraphics graphics, int width, int height) {
        float screenAspect = (float)width / (float)height;
        float textureAspect = (float)TEXTURE_WIDTH / (float)TEXTURE_HEIGHT;
        int sourceWidth = TEXTURE_WIDTH;
        int sourceHeight = TEXTURE_HEIGHT;
        int sourceX = 0;
        int sourceY = 0;

        if (screenAspect > textureAspect) {
            sourceHeight = Math.max(1, Math.round(TEXTURE_WIDTH / screenAspect));
            sourceY = (TEXTURE_HEIGHT - sourceHeight) / 2;
        } else {
            sourceWidth = Math.max(1, Math.round(TEXTURE_HEIGHT * screenAspect));
            sourceX = (TEXTURE_WIDTH - sourceWidth) / 2;
        }

        graphics.blit(
                BACKGROUND,
                0,
                0,
                width,
                height,
                sourceX,
                sourceY,
                sourceWidth,
                sourceHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private static void drawFrame(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, top + 1, GravesownUiPalette.BORDER_DARK);
        graphics.fill(left, bottom - 1, right, bottom, GravesownUiPalette.BORDER_DARK);
        graphics.fill(left, top, left + 1, bottom, GravesownUiPalette.BORDER_DARK);
        graphics.fill(right - 1, top, right, bottom, GravesownUiPalette.BORDER_DARK);

        int corner = 13;
        graphics.fill(left, top, left + corner, top + 2, ACCENT);
        graphics.fill(left, top, left + 2, top + corner, ACCENT);
        graphics.fill(right - corner, top, right, top + 2, ACCENT);
        graphics.fill(right - 2, top, right, top + corner, ACCENT);
        graphics.fill(left, bottom - 2, left + corner, bottom, ACCENT);
        graphics.fill(left, bottom - corner, left + 2, bottom, ACCENT);
        graphics.fill(right - corner, bottom - 2, right, bottom, ACCENT);
        graphics.fill(right - 2, bottom - corner, right, bottom, ACCENT);
    }
}
