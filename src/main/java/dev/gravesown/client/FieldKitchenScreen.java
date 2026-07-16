package dev.gravesown.client;

import dev.gravesown.menu.FieldKitchenMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class FieldKitchenScreen extends AbstractContainerScreen<FieldKitchenMenu> {
    public FieldKitchenScreen(FieldKitchenMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = 92;
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
        GravesownContainerStyle.well(graphics, x + 7, y + 15, x + 96, y + 83);
        GravesownContainerStyle.well(graphics, x + 103, y + 23, x + 168, y + 74);
        GravesownContainerStyle.well(graphics, x + 6, y + 98, x + 170, y + 181);
        for (int[] slot : new int[][]{{26,27},{48,27},{70,27},{37,58},{59,58},{134,43}}) {
            GravesownContainerStyle.slot(graphics, x + slot[0], y + slot[1]);
        }
        graphics.hLine(x + 101, x + 126, y + 51, GravesownContainerStyle.TEXT);
        graphics.fill(x + 123, y + 48, x + 129, y + 55, GravesownContainerStyle.ACCENT);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 104 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 162);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, GravesownContainerStyle.TEXT, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY,
                GravesownContainerStyle.TEXT, false);
    }
}
