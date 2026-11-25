package com.aurora.client.modules;

import com.aurora.client.module.HudModule;
import com.aurora.client.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public class NicknameModule extends HudModule {
    public NicknameModule() {
        super("Player Nickname", "Показує твій нікнейм на екрані", 1, 22);
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        String name = StreamerModeModule.displayName(mc);
        int w = 12 + mc.font.width(name);
        int h = 16;
        int x = ax(g, w);
        int y = ay(g, h);
        track(x, y, w, h);

        Theme.hudPanel(g, x, y, w, h);
        g.fill(x, y + 2, x + 2, y + h - 2, color.get());
        g.drawString(mc.font, name, x + 8, y + 4, 0xFFFFFFFF);
    }
}
