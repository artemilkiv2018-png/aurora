package com.aurora.client.module;

import com.aurora.client.setting.ColorSetting;
import com.aurora.client.setting.NumberSetting;
import net.minecraft.client.gui.GuiGraphics;

/** HUD-модуль з позицією у відсотках екрана (X/Y) та кольором. */
public abstract class HudModule extends Module {
    /** true, поки відкритий HUD-редактор: модулі без даних малюють зразок. */
    public static boolean editing;

    protected final NumberSetting posX;
    protected final NumberSetting posY;
    protected final ColorSetting color;

    private int bx;
    private int by;
    private int bw;
    private int bh;
    private long lastFrame;

    protected HudModule(String name, String description, double defX, double defY) {
        super(name, description, Category.HUD);
        this.posX = add(new NumberSetting("X %", 0, 100, defX, 0.5));
        this.posY = add(new NumberSetting("Y %", 0, 100, defY, 0.5));
        this.color = add(new ColorSetting("Color", 0.57f));
    }

    @Override
    public boolean isHud() {
        return true;
    }

    /** Лівий верхній X для елемента шириною w. */
    protected int ax(GuiGraphics g, int w) {
        return Math.round((g.guiWidth() - w) * (posX.getFloat() / 100f));
    }

    /** Лівий верхній Y для елемента висотою h. */
    protected int ay(GuiGraphics g, int h) {
        return Math.round((g.guiHeight() - h) * (posY.getFloat() / 100f));
    }

    /** Запам'ятати межі намальованого елемента (для HUD-редактора). */
    protected void track(int x, int y, int w, int h) {
        this.bx = x;
        this.by = y;
        this.bw = w;
        this.bh = h;
        this.lastFrame = System.currentTimeMillis();
    }

    public boolean hasBounds() {
        return bw > 0 && bh > 0 && System.currentTimeMillis() - lastFrame < 250;
    }

    public int boundsX() {
        return bx;
    }

    public int boundsY() {
        return by;
    }

    public int boundsW() {
        return bw;
    }

    public int boundsH() {
        return bh;
    }

    public boolean contains(double mx, double my) {
        return mx >= bx && mx < bx + bw && my >= by && my < by + bh;
    }

    /** Перемістити так, щоб лівий верхній кут опинився в (x, y) на екрані screenW x screenH. */
    public void moveTo(int x, int y, int screenW, int screenH) {
        int rangeX = screenW - bw;
        int rangeY = screenH - bh;
        if (rangeX > 0) posX.set(Math.max(0, Math.min(rangeX, x)) * 100.0 / rangeX);
        if (rangeY > 0) posY.set(Math.max(0, Math.min(rangeY, y)) * 100.0 / rangeY);
    }
}
