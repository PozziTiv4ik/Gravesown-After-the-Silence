package dev.gravesown.client;

import dev.gravesown.menu.SawmillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Compact one-input station using the shared Gravesown container language. */
public final class SawmillScreen extends AbstractContainerScreen<SawmillMenu> {
    public SawmillScreen(SawmillMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        GravesownContainerStyle.panel(graphics, x, y, this.imageWidth, this.imageHeight);
        GravesownContainerStyle.well(graphics, x + 21, y + 17, x + 155, y + 68);
        GravesownContainerStyle.well(graphics, x + 6, y + 78, x + 170, y + 161);
        GravesownContainerStyle.slot(graphics, x + 44, y + 35);
        GravesownContainerStyle.slot(graphics, x + 116, y + 35);
        graphics.hLine(x + 66, x + 106, y + 43, GravesownContainerStyle.TEXT);
        graphics.fill(x + 102, y + 40, x + 109, y + 47, GravesownContainerStyle.ACCENT);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 84 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 142);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY,
                GravesownContainerStyle.TEXT, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
                GravesownContainerStyle.TEXT, false);
    }
}
