package dev.gravesown.client;

import dev.gravesown.Gravesown;
import dev.gravesown.menu.PitchKilnMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** Furnace-compatible screen rendered in the same style as Gravework and inventory. */
public final class PitchKilnScreen extends AbstractFurnaceScreen<PitchKilnMenu> {
    private static final ResourceLocation UNUSED_TEXTURE = Gravesown.id("textures/gui/transparent.png");
    private static final ResourceLocation LIT = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BURN = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    public PitchKilnScreen(PitchKilnMenu menu, Inventory inventory, Component title) {
        super(menu, new SmeltingRecipeBookComponent(), inventory, title, UNUSED_TEXTURE, LIT, BURN);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        GravesownContainerStyle.panel(graphics, x, y, this.imageWidth, this.imageHeight);
        GravesownContainerStyle.well(graphics, x + 43, y + 12, x + 133, y + 71);
        GravesownContainerStyle.playerSlots(graphics, x, y);
        GravesownContainerStyle.slot(graphics, x + 56, y + 17);
        GravesownContainerStyle.slot(graphics, x + 56, y + 53);
        GravesownContainerStyle.slot(graphics, x + 116, y + 35);

        if (this.menu.isLit()) {
            int flame = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            graphics.fill(x + 57, y + 50 - flame, x + 69, y + 50, GravesownUiPalette.READY);
            graphics.fill(x + 60, y + 50 - flame, x + 66, y + 48, 0xFFD5A34D);
        }
        int progress = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        graphics.fill(x + 79, y + 34, x + 79 + progress, y + 50, GravesownContainerStyle.ACCENT);
        if (progress > 2) {
            graphics.fill(x + 81, y + 37, x + 77 + progress, y + 47, GravesownUiPalette.BORDER);
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
