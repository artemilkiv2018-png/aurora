package com.aurora.client.modules;

import com.aurora.client.module.HudModule;
import com.aurora.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingModule extends HudModule {
    public PingModule() {
        super("Ping", "Показує затримку до сервера", 1, 15);
    }

    /** Поточний пінг гравця в мс (0 в одиночній грі). */
    public static int currentPing(Minecraft mc) {
        if (mc.player == null || mc.getConnection() == null) return 0;
        PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        return info == null ? 0 : info.getLatency();
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        String label = "Ping ";
        String value = currentPing(mc) + " ms";
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
