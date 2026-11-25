package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.setting.NumberSetting;

/** Автоматично натискає "Відродитися" після смерті. */
public class AutoRespawnModule extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 0, 60, 10, 1));
    private int ticks;

    public AutoRespawnModule() {
        super("Auto Respawn", "Автоматичне відродження після смерті", Category.UTILITIES);
    }

    @Override
    public void onTick() {
        if (mc.player == null) {
            ticks = 0;
            return;
        }
        if (mc.player.isDeadOrDying()) {
            if (++ticks >= delay.getInt()) {
                mc.player.respawn();
                ticks = 0;
            }
        } else {
            ticks = 0;
        }
    }
}
