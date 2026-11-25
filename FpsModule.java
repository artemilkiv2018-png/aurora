package com.aurora.client.modules;

import com.aurora.client.module.HudModule;
import com.aurora.client.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public class FpsModule extends HudModule {
    public FpsModule() {
        super("FPS", "Показує кількість кадрів за секунду", 1, 8);
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        String label = "FPS ";
        String value = String.valueOf(mc.getFps());
        int w = 12 + mc.font.width(label) + mc.font.width(value);
        int h = 16;
        int x = ax(g, w);
        int y = ay(g, h);
        track(x, y, w, h);

        Theme.hudPanel(g, x, y, w, h);
        g.fill(x, y + 2, x + 2, y + h - 2, color.get());
        g.drawString(mc.font, label, x + 8, y + 4, color.get());
        g.drawString(mc.font, value, x + 8 + mc.font.width(label), y + 4, 0xFFFFFFFF);
    }
}
