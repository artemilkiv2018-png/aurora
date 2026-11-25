package com.aurora.client.modules;

import com.aurora.client.module.HudModule;
import com.aurora.client.module.ModuleManager;
import com.aurora.client.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Сповіщення про вмикання/вимикання модулів. */
public class NotificationsModule extends HudModule {
    private static final long LIFETIME_MS = 2200;

    private record Note(String text, long time) {
    }

    private static final List<Note> NOTES = new ArrayList<>();

    public NotificationsModule() {
        super("Notifications", "Сповіщення при вмиканні/вимиканні модулів", 99, 94);
        enableByDefault();
    }

    public static void push(String text) {
        NotificationsModule m = ModuleManager.get(NotificationsModule.class);
        if (m == null || !m.isEnabled()) return;
        NOTES.add(new Note(text, System.currentTimeMillis()));
        while (NOTES.size() > 5) NOTES.remove(0);
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        long now = System.currentTimeMillis();
        for (Iterator<Note> it = NOTES.iterator(); it.hasNext(); ) {
            if (now - it.next().time() > LIFETIME_MS) it.remove();
        }

        List<Note> list = new ArrayList<>(NOTES);
        if (list.isEmpty() && editing) {
            list.add(new Note("Module enabled", now)); // зразок для HUD-редактора
        }
        if (list.isEmpty()) return;

        final int lineH = 18;
        final int gap = 3;
        int maxW = 0;
        for (Note n : list) maxW = Math.max(maxW, mc.font.width(n.text()));
        int w = maxW + 16;
        int totalH = list.size() * lineH + (list.size() - 1) * gap;
        int x = ax(g, w);
        int y = ay(g, totalH);
        track(x, y, w, totalH);

        int i = 0;
        for (int idx = list.size() - 1; idx >= 0; idx--) {
            Note n = list.get(idx);
            int tw = mc.font.width(n.text()) + 16;
            int nx = x + w - tw;
            int ny = y + i * (lineH + gap);
            Theme.hudPanel(g, nx, ny, tw, lineH);
            g.fill(nx, ny + 2, nx + 2, ny + lineH - 2, color.get());
            g.drawString(mc.font, n.text(), nx + 8, ny + 5, 0xFFFFFFFF);
            i++;
        }
    }
}
