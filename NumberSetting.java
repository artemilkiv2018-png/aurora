package com.aurora.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting {
    public final double min;
    public final double max;
    public final double step;
    private double value;

    public NumberSetting(String name, double min, double max, double def, double step) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        set(def);
    }

    public double get() {
        return value;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public float getFloat() {
        return (float) value;
    }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        if (step > 0) {
            v = Math.round((v - min) / step) * step + min;
        }
        v = Math.round(v * 10000.0) / 10000.0;
        this.value = Math.max(min, Math.min(max, v));
    }

    public double fraction() {
        return (value - min) / (max - min);
    }

    public void setFraction(double f) {
        f = Math.max(0, Math.min(1, f));
        set(min + (max - min) * f);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            try {
                set(element.getAsDouble());
            } catch (RuntimeException ignored) {
            }
        }
    }
}
