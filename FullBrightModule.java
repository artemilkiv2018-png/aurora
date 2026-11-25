package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Повна яскравість: клієнтський ефект нічного бачення без іконки і часток. */
public class FullBrightModule extends Module {
    private boolean applied;

    public FullBrightModule() {
        super("Full Bright", "Максимальна яскравість у темряві", Category.VISUALS);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        MobEffectInstance current = mc.player.getEffect(MobEffects.NIGHT_VISION);
        if (current == null || current.getDuration() < 240) {
            mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0, false, false, false));
            applied = true;
        }
    }

    @Override
    public void onDisable() {
        if (applied && mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
        applied = false;
    }
}
