package com.aurora.client.ui;

import com.aurora.client.config.ConfigManager;
import com.aurora.client.module.HudModule;
import com.aurora.client.module.Module;
import com.aurora.client.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * HUD-редактор: самі елементи малює HUD (він видимий під екраном),
 * а тут лише рамки та перетягування мишею.
 */
public class HudEditorScreen extends Screen {
    private final Screen parent;
    private HudModule dragged;
    private int offX;
    private int offY;

    public HudEditorScreen(Screen parent) {
        super(Component.literal("HUD Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HudModule.editing = true;
    }

    @Override
    public void removed() {
        HudModule.editing = false;
        ConfigManager.save(ConfigManager.DEFAULT);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x55000000);

        String hint = "Перетягуй елементи мишкою  •  ESC — назад до меню";
        int hw = font.width(hint) + 20;
        Theme.round(g, (width - hw) / 2, 8, hw, 20, 6, Theme.BG);
        g.drawString(font, hint, (width - hw) / 2 + 10, 14, Theme.TEXT);

        HudModule hovered = pick(mouseX, mouseY);
        for (HudModule h : huds()) {
            if (!h.hasBounds()) continue;
            boolean active = h == dragged || (dragged == null && h == hovered);
            int c = active ? Theme.ACCENT : 0x99FFFFFF;
            outline(g, h.boundsX() - 1, h.boundsY() - 1, h.boundsW() + 2, h.boundsH() + 2, c);
            if (active) {
                g.drawString(font, h.name, h.boundsX(), h.boundsY() - 11, Theme.ACCENT);
            }
        }
    }

    private static void outline(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    private List<HudModule> huds() {
        List<HudModule> out = new java.util.ArrayList<>();
        for (Module m : ModuleManager.all()) {
            if (m instanceof HudModule h && h.isEnabled()) out.add(h);
        }
        return out;
    }

    /** Верхній (останній у списку) елемент під курсором. */
    private HudModule pick(double mx, double my) {
        List<HudModule> list = huds();
        for (int i = list.size() - 1; i >= 0; i--) {
            HudModule h = list.get(i);
            if (h.hasBounds() && h.contains(mx, my)) return h;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            HudModule h = pick(event.x(), event.y());
            if (h != null) {
                dragged = h;
                offX = (int) event.x() - h.boundsX();
                offY = (int) event.y() - h.boundsY();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragged != null) {
            dragged.moveTo((int) event.x() - offX, (int) event.y() - offY, width, height);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragged = null;
        return super.mouseReleased(event);
    }
}
