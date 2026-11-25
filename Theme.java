package com.aurora.client.ui;

import net.minecraft.client.gui.GuiGraphics;

/** Кольори та прості фігури стилю "Winter Blue". Усі кольори — ARGB. */
public final class Theme {
    public static final int BG = 0xF2101118;
    public static final int HEADER = 0xFF1B1D33;
    public static final int ROW = 0xFF181A24;
    public static final int ROW_HOVER = 0xFF1E2130;
    public static final int SETTING = 0xFF131520;
    public static final int ACCENT = 0xFF30A2E8;
    public static final int ACCENT_DARK = 0xFF1F5E8A;
    public static final int TEXT = 0xFFFFFFFF;
    public static final int TEXT_OFF = 0xFFB8BCC8;
    public static final int DIM = 0xFF7E8494;
    public static final int DIM2 = 0xFFA0A6B4;
    public static final int LINE = 0xFF2A2D3D;
    public static final int TOGGLE_OFF = 0xFF2E3140;
    public static final int HUD_BG = 0xC8101118;

    private Theme() {
    }

    /** Прямокутник із заокругленими кутами (радіус r пікселів). */
    public static void round(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            g.fill(x, y, x + w, y + h, color);
            return;
        }
        g.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    /** Перемикач 26x14 у стилі iOS. */
    public static void toggle(GuiGraphics g, int x, int y, boolean on) {
        round(g, x, y, 26, 14, 7, on ? ACCENT : TOGGLE_OFF);
        round(g, on ? x + 14 : x + 2, y + 2, 10, 10, 5, TEXT);
    }

    /** Малий перемикач 22x12. */
    public static void toggleSmall(GuiGraphics g, int x, int y, boolean on) {
        round(g, x, y, 22, 12, 6, on ? ACCENT : TOGGLE_OFF);
        round(g, on ? x + 12 : x + 2, y + 2, 8, 8, 4, TEXT);
    }

    /** Панель для HUD-елементів. */
    public static void hudPanel(GuiGraphics g, int x, int y, int w, int h) {
        round(g, x, y, w, h, 4, HUD_BG);
    }
}
