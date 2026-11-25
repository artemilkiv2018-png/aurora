package com.aurora.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.aurora.client.module.Module;
import com.aurora.client.module.ModuleManager;
import com.aurora.client.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/** Збереження/завантаження конфігів у .minecraft/config/aurora/*.json */
public final class ConfigManager {
    public static final String DEFAULT = "default";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {
    }

    private static Path dir() {
        return FabricLoader.getInstance().getConfigDir().resolve("aurora");
    }

    private static String clean(String name) {
        String n = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return n.isEmpty() ? "config" : n;
    }

    private static Path file(String name) {
        return dir().resolve(clean(name) + ".json");
    }

    public static List<String> list() {
        List<String> out = new ArrayList<>();
        Path d = dir();
        if (!Files.isDirectory(d)) return out;
        try (Stream<Path> s = Files.list(d)) {
            s.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".json"))
                    .map(n -> n.substring(0, n.length() - 5))
                    .forEach(out::add);
        } catch (IOException ignored) {
        }
        Collections.sort(out);
        return out;
    }

    public static String nextName() {
        List<String> existing = list();
        int i = 1;
        while (existing.contains("config" + i)) i++;
        return "config" + i;
    }

    public static boolean save(String name) {
        JsonObject modules = new JsonObject();
        for (Module m : ModuleManager.all()) {
            JsonObject mo = new JsonObject();
            mo.addProperty("enabled", m.isEnabled());
            JsonObject so = new JsonObject();
            for (Setting s : m.getSettings()) {
                so.add(s.name, s.save());
            }
            mo.add("settings", so);
            modules.add(m.name, mo);
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        root.add("modules", modules);

        try {
            Files.createDirectories(dir());
            try (Writer w = Files.newBufferedWriter(file(name), StandardCharsets.UTF_8)) {
                GSON.toJson(root, w);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean load(String name) {
        Path f = file(name);
        if (!Files.isRegularFile(f)) return false;
        try (Reader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(r);
            if (!parsed.isJsonObject()) return false;
            JsonObject root = parsed.getAsJsonObject();
            if (!root.has("modules") || !root.get("modules").isJsonObject()) return false;
            JsonObject modules = root.getAsJsonObject("modules");

            for (Module m : ModuleManager.all()) {
                if (!modules.has(m.name) || !modules.get(m.name).isJsonObject()) continue;
                JsonObject mo = modules.getAsJsonObject(m.name);
                if (mo.has("settings") && mo.get("settings").isJsonObject()) {
                    JsonObject so = mo.getAsJsonObject("settings");
                    for (Setting s : m.getSettings()) {
                        if (so.has(s.name)) s.load(so.get(s.name));
                    }
                }
                if (mo.has("enabled")) {
                    m.setEnabled(mo.get("enabled").getAsBoolean(), true);
                }
            }
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    public static boolean delete(String name) {
        try {
            return Files.deleteIfExists(file(name));
        } catch (IOException e) {
            return false;
        }
    }
}
