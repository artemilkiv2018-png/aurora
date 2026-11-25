package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.setting.ModeSetting;
import com.aurora.client.setting.NumberSetting;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.ThreadLocalRandom;

/** Візуальні частки навколо істоти, по якій ти вдарив (клієнтські, нікому не видно). */
public class HitParticlesModule extends Module {
    private final ModeSetting type = add(new ModeSetting("Type", "Crit",
            "Crit", "Enchant", "Heart", "Flame", "Totem", "End Rod"));
    private final NumberSetting amount = add(new NumberSetting("Amount", 1, 30, 8, 1));

    public HitParticlesModule() {
        super("Hit Particles", "Частки при ударі по істоті", Category.VISUALS);

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (isEnabled() && level.isClientSide()) {
                spawn(entity);
            }
            return InteractionResult.PASS;
        });
    }

    private ParticleOptions particle() {
        return switch (type.get()) {
            case "Enchant" -> ParticleTypes.ENCHANTED_HIT;
            case "Heart" -> ParticleTypes.HEART;
            case "Flame" -> ParticleTypes.FLAME;
            case "Totem" -> ParticleTypes.TOTEM_OF_UNDYING;
            case "End Rod" -> ParticleTypes.END_ROD;
            default -> ParticleTypes.CRIT;
        };
    }

    private void spawn(Entity entity) {
        if (mc.level == null) return;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        ParticleOptions p = particle();
        double cx = entity.getX();
        double cy = entity.getY() + entity.getBbHeight() * 0.5;
        double cz = entity.getZ();
        double spread = Math.max(0.3, entity.getBbWidth() * 0.6);

        for (int i = 0; i < amount.getInt(); i++) {
            mc.level.addParticle(p,
                    cx + r.nextGaussian() * spread * 0.5,
                    cy + r.nextGaussian() * entity.getBbHeight() * 0.25,
                    cz + r.nextGaussian() * spread * 0.5,
                    r.nextGaussian() * 0.1, 0.1 + r.nextDouble() * 0.15, r.nextGaussian() * 0.1);
        }
    }
}
