package com.aurora.client.modules;

import com.aurora.client.module.HudModule;
import com.aurora.client.setting.BoolSetting;
import com.aurora.client.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public class WatermarkModule extends HudModule {
    private final BoolSetting showName = add(new BoolSetting("Nickname", true));
    private final BoolSetting showFps = add(new BoolSetting("FPS", true));
    private final BoolSetting showPing = add(new BoolSetting("Ping", true));

    public WatermarkModule() {
        super("Watermark", "Логотип клієнта з ніком, FPS і пінгом", 1, 1);
        enableByDefault();
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        StringBuilder rest = new StringBuilder();
        if (showName.get()) rest.append("  |  ").append(StreamerModeModule.displayName(mc));
        if (showFps.get()) rest.append("  |  ").append(mc.getFps()).append(" fps");
        if (showPing.get()) rest.append("  |  ").append(PingModule.currentPing(mc)).append(" ms");

        String logo = "AURORA";
        int logoW = mc.font.width(logo);
        int w = 12 + logoW + mc.font.width(rest.toString()) + 6;
        int h = 16;
        int x = ax(g, w);
        int y = ay(g, h);
        track(x, y, w, h);

        Theme.hudPanel(g, x, y, w, h);
        g.fill(x, y + 2, x + 2, y + h - 2, color.get());
        g.drawString(mc.font, logo, x + 8, y + 4, color.get());
        g.drawString(mc.font, rest.toString(), x + 8 + logoW, y + 4, 0xFFFFFFFF);
    }
}
