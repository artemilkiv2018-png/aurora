package com.aurora.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** Колір за відтінком (hue) + опція Rainbow. */
public class ColorSetting extends Setting {
    private float hue;
    private boolean rainbow;

    /** @param hue відтінок 0..1 (0.57 ≈ блакитний) */
    public ColorSetting(String name, float hue) {
        super(name);
        this.hue = hue;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = Math.max(0f, Math.min(1f, hue));
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public int get() {
        return get(0);
    }

    /** @param offset зсув для Rainbow, щоб рядки списку мінялися по черзі */
    public int get(int offset) {
        float h = rainbow ? (((System.currentTimeMillis() / 20L) + offset * 12L) % 360L) / 360f : hue;
        return 0xFF000000 | (java.awt.Color.HSBtoRGB(h, 0.7f, 1f) & 0xFFFFFF);
    }

    public static int fromHue(float h) {
        return 0xFF000000 | (java.awt.Color.HSBtoRGB(h, 0.7f, 1f) & 0xFFFFFF);
    }

    @Override
    public JsonElement save() {
        JsonObject o = new JsonObject();
        o.addProperty("hue", hue);
        o.addProperty("rainbow", rainbow);
        return o;
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            try {
                if (o.has("hue")) setHue(o.get("hue").getAsFloat());
                if (o.has("rainbow")) rainbow = o.get("rainbow").getAsBoolean();
            } catch (RuntimeException ignored) {
            }
        }
    }
}
