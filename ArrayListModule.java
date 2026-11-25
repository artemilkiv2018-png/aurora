package com.aurora.client.modules;

import com.aurora.client.module.Module;
import com.aurora.client.module.ModuleManager;
import com.aurora.client.setting.BoolSetting;
import com.aurora.client.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Список увімкнених модулів (праворуч угорі за замовчуванням). */
public class ArrayListModule extends com.aurora.client.module.HudModule {
    private final BoolSetting showHud = add(new BoolSetting("Show HUD modules", false));

    public ArrayListModule() {
        super("Array List", "Список увімкнених модулів", 100, 1);
        enableByDefault();
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        List<String> lines = new ArrayList<>();
        for (Module m : ModuleManager.all()) {
            if (!m.isEnabled() || m == this) continue;
            if (m.isHud() && !showHud.get()) continue;
            lines.add(m.name);
        }
        if (lines.isEmpty()) return;
        lines.sort(Comparator.comparingInt((String s) -> mc.font.width(s)).reversed());

        final int lineH = 12;
        int maxW = 0;
        for (String s : lines) maxW = Math.max(maxW, mc.font.width(s));
        int boxW = maxW + 10;
        int x = ax(g, boxW);
        int y = ay(g, lines.size() * lineH);
        track(x, y, boxW, lines.size() * lineH);

        int i = 0;
        for (String s : lines) {
            int sw = mc.font.width(s);
            int lx = x + boxW - sw - 10;
            int ly = y + i * lineH;
            int col = color.get(i);
            g.fill(lx, ly, x + boxW, ly + lineH, Theme.HUD_BG);
            g.fill(x + boxW - 2, ly, x + boxW, ly + lineH, col);
            g.drawString(mc.font, s, lx + 4, ly + 2, col);
            i++;
        }
    }
}
