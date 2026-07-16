package dev.gravesown.client;

import com.mojang.blaze3d.platform.NativeImage;
import dev.gravesown.Gravesown;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Keeps the vanilla main-menu behavior and widgets while replacing only its
 * panorama layer with Gravesown artwork.
 */
public final class GravesownTitleScreen extends TitleScreen {
    private static final ResourceLocation BACKGROUND =
            Gravesown.id("textures/gui/title_background.png");
    private static final Component BRAND = Component.translatable("itemGroup.gravesown.main");
    private static final Component SUBTITLE = Component.translatable("generator.gravesown.after_the_silence");
    private static final int TEXTURE_WIDTH = 1672;
    private static final int TEXTURE_HEIGHT = 941;

    static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof InventoryScreen inventoryScreen
                && !(inventoryScreen instanceof GravesownInventoryScreen)
                && Minecraft.getInstance().player != null
                && !Minecraft.getInstance().gameMode.hasInfiniteItems()) {
            event.setNewScreen(new GravesownInventoryScreen(Minecraft.getInstance().player));
            return;
        }
        if (event.getNewScreen() instanceof TitleScreen
                && !(event.getNewScreen() instanceof GravesownTitleScreen)) {
            event.setNewScreen(new GravesownTitleScreen());
        }
    }

    @Override
    protected void renderPanorama(GuiGraphics graphics, float partialTick) {
        drawCoverTexture(graphics);

        // Preserve the scene while keeping the centered vanilla widgets legible.
        graphics.fillGradient(0, 0, this.width, this.height, 0x18071423, 0x76071423);
        int menuTop = this.height / 4 + 22;
        int menuBottom = Math.min(this.height - 44, menuTop + 164);
        graphics.fill(this.width / 2 - 112, menuTop, this.width / 2 + 112, menuBottom, 0x8010243B);

        if (this.width >= 600) {
            graphics.pose().pushPose();
            graphics.pose().translate(18.0F, 18.0F, 0.0F);
            graphics.pose().scale(2.0F, 2.0F, 1.0F);
            graphics.drawString(this.font, BRAND, 0, 0, GravesownUiPalette.TEXT, true);
            graphics.pose().popPose();
            graphics.drawString(this.font, SUBTITLE, 20, 40, GravesownUiPalette.MUTED, true);
        }
    }

    private void drawCoverTexture(GuiGraphics graphics) {
        float screenAspect = (float)this.width / (float)this.height;
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
                this.width,
                this.height,
                sourceX,
                sourceY,
                sourceWidth,
                sourceHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    /** Validates the packaged bitmap during the opt-in client smoke run. */
    static void verifyBackgroundResource(Minecraft client) {
        Resource resource = client.getResourceManager().getResource(BACKGROUND)
                .orElseThrow(() -> new IllegalStateException("missing title background " + BACKGROUND));
        try (InputStream stream = resource.open(); NativeImage image = NativeImage.read(stream)) {
            if (image.getWidth() != TEXTURE_WIDTH || image.getHeight() != TEXTURE_HEIGHT) {
                throw new IllegalStateException(
                        "expected title background " + TEXTURE_WIDTH + "x" + TEXTURE_HEIGHT
                                + ", got " + image.getWidth() + "x" + image.getHeight()
                );
            }
        } catch (IOException exception) {
            throw new IllegalStateException("could not decode title background " + BACKGROUND, exception);
        }
    }
}
