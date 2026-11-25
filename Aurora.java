package com.aurora.client;

import com.aurora.client.config.ConfigManager;
import com.aurora.client.module.Module;
import com.aurora.client.module.ModuleManager;
import com.aurora.client.modules.CrosshairModule;
import com.aurora.client.ui.MenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class Aurora implements ClientModInitializer {
    public static final String ID = "aurora";

    private static KeyMapping menuKey;
    private static final Map<Module, KeyMapping> MODULE_KEYS = new LinkedHashMap<>();

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.load(ConfigManager.DEFAULT);

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ID, "main"));

        // Клавіша меню (Right Shift за замовчуванням, міняється в Controls)
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.aurora.menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));

        // Окрема (спочатку не призначена) клавіша для кожного модуля: Налаштування → Керування → AURORA
        for (Module m : ModuleManager.all()) {
            MODULE_KEYS.put(m, KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    "key.aurora.module." + m.id(),
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    category
            )));
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new MenuScreen());
                }
            }
            for (Map.Entry<Module, KeyMapping> e : MODULE_KEYS.entrySet()) {
                while (e.getValue().consumeClick()) {
                    if (client.screen == null) e.getKey().toggle();
                }
            }
            ModuleManager.tick();
        });

        // HUD-модулі
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(ID, "hud"),
                (graphics, deltaTracker) -> ModuleManager.renderHud(
                        graphics, deltaTracker.getGameTimeDeltaPartialTick(false))
        );

        // Кастомний приціл замість ванільного, коли модуль увімкнено
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (graphics, deltaTracker) -> {
            CrosshairModule crosshair = ModuleManager.get(CrosshairModule.class);
            if (crosshair != null && crosshair.isEnabled()) {
                crosshair.renderCrosshair(graphics);
            } else {
                original.render(graphics, deltaTracker);
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(ConfigManager.DEFAULT));
    }
}
