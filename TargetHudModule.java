package com.aurora.client.modules;

import com.aurora.client.module.HudModule;
import com.aurora.client.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

/** Показує ім'я та здоров'я істоти, на яку дивишся. */
public class TargetHudModule extends HudModule {
    private LivingEntity target;
    private long lastSeen;

    public TargetHudModule() {
        super("Target HUD", "Ім'я і здоров'я істоти під прицілом", 50, 62);
    }

    @Override
    public void renderHud(GuiGraphics g, float partialTick) {
        Entity looking = mc.crosshairPickEntity;
        if (looking instanceof LivingEntity living && living.isAlive()) {
            target = living;
            lastSeen = System.currentTimeMillis();
        }
        if (target != null && (!target.isAlive() || System.currentTimeMillis() - lastSeen > 1500)) {
            target = null;
        }

        String name;
        float hp;
        float max;
        if (target != null) {
            name = target.getName().getString();
            hp = target.getHealth();
            max = Math.max(1f, target.getMaxHealth());
        } else if (editing) {
            // зразок для HUD-редактора
            name = "Target";
            hp = 20f;
            max = 20f;
        } else {
            return;
        }
        String hpText = String.format(Locale.ROOT, "%.1f HP", hp);

        int w = Math.max(110, mc.font.width(name) + 20);
        int h = 32;
        int x = ax(g, w);
        int y = ay(g, h);
        track(x, y, w, h);

        Theme.round(g, x, y, w, h, 6, Theme.HUD_BG);
        g.drawString(mc.font, name, x + 8, y + 6, 0xFFFFFFFF);
        g.drawString(mc.font, hpText, x + w - 8 - mc.font.width(hpText), y + 6, color.get());

        int barX = x + 8;
        int barW = w - 16;
        Theme.round(g, barX, y + 20, barW, 5, 2, Theme.TOGGLE_OFF);
        int fill = Math.round(barW * Math.max(0f, Math.min(1f, hp / max)));
        if (fill > 0) Theme.round(g, barX, y + 20, Math.max(fill, 4), 5, 2, color.get());
    }
}
