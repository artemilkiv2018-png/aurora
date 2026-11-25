package com.aurora.client.module;

import com.aurora.client.modules.NotificationsModule;
import com.aurora.client.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Базовий клас будь-якого модуля клієнта. */
public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;

    protected final Minecraft mc = Minecraft.getInstance();
    private final List<Setting> settings = new ArrayList<>();
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    /** Ідентифікатор для ключів перекладу й біндів: "Full Bright" -> "full_bright". */
    public String id() {
        return name.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
    }

    protected <S extends Setting> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** Увімкнути модуль за замовчуванням (викликати лише в конструкторі). */
    protected void enableByDefault() {
        this.enabled = true;
    }

    public void toggle() {
        setEnabled(!enabled, false);
    }

    public void setEnabled(boolean value) {
        setEnabled(value, false);
    }

    /** @param silent true — без сповіщення (використовується під час завантаження конфіга) */
    public void setEnabled(boolean value, boolean silent) {
        if (value == enabled) return;
        enabled = value;
        if (value) {
            onEnable();
        } else {
            onDisable();
        }
        if (!silent) {
            NotificationsModule.push(name + (value ? " увімкнено" : " вимкнено"));
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    /** Викликається щотіка, поки модуль увімкнений. */
    public void onTick() {
    }

    public boolean isHud() {
        return false;
    }

    /** Малювання на HUD (лише для HUD-модулів). */
    public void renderHud(GuiGraphics graphics, float partialTick) {
    }
}
