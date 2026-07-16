package dev.gravesown.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;

/** Vanilla-compatible inventory behavior with the shared Gravesown container skin. */
public final class GravesownInventoryScreen extends InventoryScreen {
    public GravesownInventoryScreen(Player player) {
        super(player);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        GravesownContainerStyle.panel(graphics, x, y, this.imageWidth, this.imageHeight);
        GravesownContainerStyle.well(graphics, x + 7, y + 7, x + 84, y + 83);
        GravesownContainerStyle.playerSlots(graphics, x, y);

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                GravesownContainerStyle.slot(graphics, x + 98 + column * 18, y + 18 + row * 18);
            }
        }
        GravesownContainerStyle.slot(graphics, x + 154, y + 28);
        for (int armor = 0; armor < 4; armor++) {
            GravesownContainerStyle.slot(graphics, x + 8, y + 8 + armor * 18);
        }
        GravesownContainerStyle.slot(graphics, x + 77, y + 62);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics, x + 27, y + 9, x + 74, y + 78, 30, 0.0625F, mouseX, mouseY, this.minecraft.player
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY,
                GravesownContainerStyle.TEXT, false);
    }
}
