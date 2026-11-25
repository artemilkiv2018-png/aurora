package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.setting.BoolSetting;
import com.aurora.client.setting.ColorSetting;
import com.aurora.client.setting.NumberSetting;
import net.minecraft.client.gui.GuiGraphics;

/** Власний приціл. Замінює ванільний через HudElementRegistry.replaceElement (див. Aurora). */
public class CrosshairModule extends Module {
    private final NumberSetting size = add(new NumberSetting("Size", 1, 15, 4, 1));
    private final NumberSetting gap = add(new NumberSetting("Gap", 0, 10, 2, 1));
    private final NumberSetting thickness = add(new NumberSetting("Thickness", 1, 4, 1, 1));
    private final BoolSetting dot = add(new BoolSetting("Dot", false));
    private final BoolSetting outline = add(new BoolSetting("Outline", true));
    private final ColorSetting color = add(new ColorSetting("Color", 0.57f));

    public CrosshairModule() {
        super("Crosshair", "Менший кастомний приціл замість ванільного", Category.VISUALS);
    }

    public void renderCrosshair(GuiGraphics g) {
        // Малюємо лише від першої особи, як і ванільний приціл
        if (!mc.options.getCameraType().isFirstPerson()) return;

        int cx = g.guiWidth() / 2;
        int cy = g.guiHeight() / 2;
        int s = size.getInt();
        int gp = gap.getInt();
        int t = thickness.getInt();
        int a = t / 2;
        int col = color.get();

        // межі центру: [cx-a, cx-a+t) x [cy-a, cy-a+t)
        int left1 = cx - a - gp;
        int left0 = left1 - s;
        int right0 = cx - a + t + gp;
        int right1 = right0 + s;
        int up1 = cy - a - gp;
        int up0 = up1 - s;
        int down0 = cy - a + t + gp;
        int down1 = down0 + s;

        if (outline.get()) {
            int o = 0xFF000000;
            rect(g, left0 - 1, cy - a - 1, left1 + 1, cy - a + t + 1, o);
            rect(g, right0 - 1, cy - a - 1, right1 + 1, cy - a + t + 1, o);
            rect(g, cx - a - 1, up0 - 1, cx - a + t + 1, up1 + 1, o);
            rect(g, cx - a - 1, down0 - 1, cx - a + t + 1, down1 + 1, o);
            if (dot.get()) rect(g, cx - a - 1, cy - a - 1, cx - a + t + 1, cy - a + t + 1, o);
        }

        rect(g, left0, cy - a, left1, cy - a + t, col);
        rect(g, right0, cy - a, right1, cy - a + t, col);
        rect(g, cx - a, up0, cx - a + t, up1, col);
        rect(g, cx - a, down0, cx - a + t, down1, col);
        if (dot.get()) rect(g, cx - a, cy - a, cx - a + t, cy - a + t, col);
    }

    private static void rect(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1, y1, x2, y2, color);
    }
}
