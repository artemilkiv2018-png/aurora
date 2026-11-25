package com.aurora.client.module;

import com.aurora.client.modules.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {
    }

    public static void init() {
        if (!MODULES.isEmpty()) return;

        // Visuals
        register(new CrosshairModule());
        register(new FullBrightModule());
        register(new HitParticlesModule());
        register(new TrailsModule());
        register(new ChinaHatModule());

        // HUD
        register(new WatermarkModule());
        register(new ArrayListModule());
        register(new FpsModule());
        register(new PingModule());
        register(new NicknameModule());
        register(new TargetHudModule());
        register(new NotificationsModule());

        // Utilities
        register(new AutoRespawnModule());
        register(new StreamerModeModule());
    }

    private static void register(Module module) {
        MODULES.add(module);
    }

    public static List<Module> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static List<Module> byCategory(Category category) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.category == category) out.add(m);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> type) {
        for (Module m : MODULES) {
            if (type.isInstance(m)) return (T) m;
        }
        return null;
    }

    public static void tick() {
        for (Module m : MODULES) {
            if (m.isEnabled()) m.onTick();
        }
    }

    public static void renderHud(GuiGraphics graphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        for (Module m : MODULES) {
            if (m.isEnabled() && m.isHud()) m.renderHud(graphics, partialTick);
        }
    }
}
