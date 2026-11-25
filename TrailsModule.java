package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.setting.BoolSetting;
import com.aurora.client.setting.ColorSetting;
import com.aurora.client.setting.ModeSetting;
import com.aurora.client.setting.NumberSetting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.concurrent.ThreadLocalRandom;

/** Шлейф із часток за гравцем (клієнтські частки, їх бачиш тільки ти). */
public class TrailsModule extends Module {
    private final ModeSetting type = add(new ModeSetting("Type", "Dust",
            "Dust", "End Rod", "Snowflake", "Flame", "Crit"));
    private final NumberSetting amount = add(new NumberSetting("Amount", 1, 8, 2, 1));
    private final NumberSetting size = add(new NumberSetting("Size", 0.3, 2.0, 0.8, 0.1));
    private final BoolSetting onlyMoving = add(new BoolSetting("Only moving", true));
    private final ColorSetting color = add(new ColorSetting("Color", 0.57f));
    private int tick;

    public TrailsModule() {
        super("Trails", "Шлейф із часток за гравцем", Category.VISUALS);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;
        boolean moving = mc.player.getDeltaMovement().horizontalDistanceSqr() > 0.0004;
        if (onlyMoving.get() && !moving) return;

        tick++;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < amount.getInt(); i++) {
            ParticleOptions p = switch (type.get()) {
                case "End Rod" -> ParticleTypes.END_ROD;
                case "Snowflake" -> ParticleTypes.SNOWFLAKE;
                case "Flame" -> ParticleTypes.FLAME;
                case "Crit" -> ParticleTypes.CRIT;
                default -> new DustParticleOptions(color.get(tick + i), size.getFloat());
            };
            mc.level.addParticle(p,
                    mc.player.getX() + r.nextGaussian() * 0.15,
                    mc.player.getY() + 0.05 + r.nextDouble() * 0.15,
                    mc.player.getZ() + r.nextGaussian() * 0.15,
                    0, 0.01, 0);
        }
    }
}
