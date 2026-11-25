package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.setting.BoolSetting;
import com.aurora.client.setting.ColorSetting;
import com.aurora.client.setting.NumberSetting;
import net.minecraft.core.particles.DustParticleOptions;

/**
 * "Капелюх" над головою з кольорових часток (3 кільця, що звужуються).
 * Це наближення частками; справжня 3D-модель потребує рендера у світі.
 */
public class ChinaHatModule extends Module {
    private final NumberSetting radius = add(new NumberSetting("Radius", 0.3, 1.2, 0.65, 0.05));
    private final NumberSetting height = add(new NumberSetting("Height", 0.0, 0.8, 0.2, 0.05));
    private final NumberSetting points = add(new NumberSetting("Points", 6, 24, 14, 1));
    private final BoolSetting firstPerson = add(new BoolSetting("First person", false));
    private final ColorSetting color = add(new ColorSetting("Color", 0.57f));
    private int tick;

    public ChinaHatModule() {
        super("China Hat", "Кільця з часток над головою", Category.VISUALS);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;
        if (!firstPerson.get() && mc.options.getCameraType().isFirstPerson()) return;

        tick++;
        if (tick % 2 != 0) return;

        double baseY = mc.player.getY() + mc.player.getBbHeight() + height.get();
        for (int level = 0; level < 3; level++) {
            double r = radius.get() * (1.0 - level * 0.33);
            double y = baseY + level * 0.12;
            int n = Math.max(4, points.getInt() * (3 - level) / 3);
            for (int i = 0; i < n; i++) {
                double ang = Math.PI * 2 * i / n + tick * 0.08;
                mc.level.addParticle(new DustParticleOptions(color.get(i + level * 4), 0.6f),
                        mc.player.getX() + Math.cos(ang) * r,
                        y,
                        mc.player.getZ() + Math.sin(ang) * r,
                        0, 0, 0);
            }
        }
    }
}
