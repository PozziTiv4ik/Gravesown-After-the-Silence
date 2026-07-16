package dev.gravesown.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

/** Shared Gravesown frame for the four-row Reliquary Crate. */
public final class ReliquaryCrateScreen extends AbstractContainerScreen<ChestMenu> {
    private final int rows;

    public ReliquaryCrateScreen(ChestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.rows = menu.getRowCount();
        this.imageWidth = 176;
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
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
        GravesownContainerStyle.well(graphics, x + 6, y + 14, x + 170, y + 22 + this.rows * 18);
        GravesownContainerStyle.well(graphics, x + 6, y + 28 + this.rows * 18, x + 170, y + this.imageHeight - 5);
        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < 9; column++) {
                GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 18 + row * 18);
            }
        }
        int offset = (this.rows - 4) * 18;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 103 + row * 18 + offset);
            }
        }
        for (int column = 0; column < 9; column++) {
            GravesownContainerStyle.slot(graphics, x + 8 + column * 18, y + 161 + offset);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, GravesownContainerStyle.TEXT, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY,
                GravesownContainerStyle.TEXT, false);
    }
}
