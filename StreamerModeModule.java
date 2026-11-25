package com.aurora.client.modules;

import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.module.ModuleManager;
import net.minecraft.client.Minecraft;

/** Ховає твій справжній нік у Watermark та Player Nickname. */
public class StreamerModeModule extends Module {
    public StreamerModeModule() {
        super("Streamer Mode", "Ховає твій нікнейм на HUD клієнта", Category.UTILITIES);
    }

    /** Нік для показу на HUD: "Streamer", якщо модуль увімкнено. */
    public static String displayName(Minecraft mc) {
        StreamerModeModule m = ModuleManager.get(StreamerModeModule.class);
        if (m != null && m.isEnabled()) return "Streamer";
        return mc.getUser().getName();
    }
}
