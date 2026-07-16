package dev.gravesown.client;

import dev.gravesown.menu.GraveworkMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** 4x4 workbench screen using the same complete frame and slot language as other containers. */
public final class GraveworkScreen extends AbstractContainerScreen<GraveworkMenu> {
    public GraveworkScreen(GraveworkMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 190;
        this.inventoryLabelY = 96;
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
        GravesownContainerStyle.well(graphics, x + 7, y + 14, x + 95, y + 96);
        GravesownContainerStyle.well(graphics, x + 99, y + 27, x + 166, y + 78);
        GravesownContainerStyle.well(graphics, x + 6, y + 102, x + 170, y + 185);

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                GravesownContainerStyle.slot(graphics, x + 18 + column * 18, y + 18 + row * 18);
            }
        }
        GravesownContainerStyle.slot(graphics, x + 137, y + 45);
        graphics.hLine(x + 103, x + 127, y + 53, GravesownContainerStyle.TEXT);
        graphics.fill(x + 124, y + 50, x + 130, y + 57, GravesownContainerStyle.ACCENT);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 108 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 166);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY,
                GravesownContainerStyle.TEXT, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
                GravesownContainerStyle.TEXT, false);
        graphics.drawString(this.font, Component.literal("4 x 4"), 106, 32,
                GravesownContainerStyle.ACCENT, false);
    }
}
