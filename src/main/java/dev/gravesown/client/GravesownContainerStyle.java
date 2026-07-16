package dev.gravesown.client;

import net.minecraft.client.gui.GuiGraphics;

/** Shared hard-edged container palette and geometry. */
final class GravesownContainerStyle {
    static final int INK = GravesownUiPalette.INK;
    static final int TEXT = GravesownUiPalette.TEXT;
    static final int PANEL = GravesownUiPalette.PANEL;
    static final int PANEL_DARK = GravesownUiPalette.PANEL_DARK;
    static final int SLOT = GravesownUiPalette.SLOT;
    static final int SLOT_DARK = GravesownUiPalette.SLOT_DARK;
    static final int ACCENT = GravesownUiPalette.BORDER;
    static final int DETAIL = GravesownUiPalette.MUTED;

    private GravesownContainerStyle() {
    }

    static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, PANEL_DARK);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, PANEL);
        graphics.fill(x + 5, y + 5, x + width - 5, y + height - 5, GravesownUiPalette.CANVAS);
        frame(graphics, x, y, x + width, y + height);
    }

    static void frame(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, top + 1, ACCENT);
        graphics.fill(left, bottom - 1, right, bottom, ACCENT);
        graphics.fill(left, top, left + 1, bottom, ACCENT);
        graphics.fill(right - 1, top, right, bottom, ACCENT);
        graphics.fill(left + 2, top + 2, left + 12, top + 3, TEXT);
        graphics.fill(right - 12, bottom - 3, right - 2, bottom - 2, DETAIL);
    }

    static void well(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, bottom, SLOT_DARK);
        graphics.fill(left + 1, top + 1, right - 1, bottom - 1, GravesownUiPalette.PANEL_DARK);
    }

    static void slot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, INK);
        graphics.fill(x, y, x + 16, y + 16, SLOT);
        graphics.fill(x + 1, y + 1, x + 15, y + 2, GravesownUiPalette.SLOT_HIGHLIGHT);
        graphics.fill(x + 1, y + 14, x + 15, y + 15, GravesownUiPalette.PANEL_DARK);
    }

    static void playerSlots(GuiGraphics graphics, int x, int y) {
        well(graphics, x + 6, y + 81, x + 170, y + 161);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                slot(graphics, x + 8 + column * 18, y + 84 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            slot(graphics, x + 8 + column * 18, y + 142);
        }
    }
}
