package com.aurora.client.setting;

import com.google.gson.JsonElement;

/** Базовий клас налаштування модуля. */
public abstract class Setting {
    public final String name;

    protected Setting(String name) {
        this.name = name;
    }

    public abstract JsonElement save();

    public abstract void load(JsonElement element);
}
