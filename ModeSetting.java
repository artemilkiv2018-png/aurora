package com.aurora.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ModeSetting extends Setting {
    public final String[] modes;
    private int index;

    public ModeSetting(String name, String def, String... modes) {
        super(name);
        this.modes = modes;
        set(def);
    }

    public String get() {
        return modes[index];
    }

    public boolean is(String mode) {
        return modes[index].equalsIgnoreCase(mode);
    }

    public void set(String mode) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(mode)) {
                index = i;
                return;
            }
        }
    }

    public void next() {
        index = (index + 1) % modes.length;
    }

    public void prev() {
        index = (index - 1 + modes.length) % modes.length;
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(get());
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsString());
        }
    }
}
