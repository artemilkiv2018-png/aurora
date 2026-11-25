package com.aurora.client.ui;

import com.aurora.client.config.ConfigManager;
import com.aurora.client.module.Category;
import com.aurora.client.module.Module;
import com.aurora.client.module.ModuleManager;
import com.aurora.client.setting.BoolSetting;
import com.aurora.client.setting.ColorSetting;
import com.aurora.client.setting.ModeSetting;
import com.aurora.client.setting.NumberSetting;
import com.aurora.client.setting.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/** Головне меню AURORA у стилі Winter Blue: вкладки, модулі з тумблерами, налаштування, конфіги. */
public class MenuScreen extends Screen {
    private static final int W = 320;
    private static final int H = 250;

    private enum Tab {
        VISUALS("Visuals"), HUD("HUD"), UTILITIES("Utilities"), CONFIGS("Configs");

        final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private record Hit(int x1, int y1, int x2, int y2, IntConsumer click, DoubleConsumer slide) {
        boolean in(double x, double y) {
            return x >= x1 && x < x2 && y >= y1 && y < y2;
        }
    }

    private static Tab tab = Tab.VISUALS;
    private static double scroll = 0;
    private static final Set<Module> EXPANDED = new HashSet<>();

    private final List<Hit> fixedHits = new ArrayList<>();
    private final List<Hit> listHits = new ArrayList<>();
    private List<String> configs = new ArrayList<>();
    private Hit dragging;
    private int viewTop;
    private int viewBottom;
    private int contentHeight;

    public MenuScreen() {
        super(Component.literal("AURORA"));
    }

    @Override
    protected void init() {
        refreshConfigs();
    }

    private void refreshConfigs() {
        configs = ConfigManager.list();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ConfigManager.save(ConfigManager.DEFAULT);
        super.onClose();
    }

    // ------------------------------------------------------------------ рендер

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        fixedHits.clear();
        listHits.clear();

        g.fill(0, 0, width, height, 0x88000000);

        int px = (width - W) / 2;
        int py = (height - H) / 2;
        Theme.round(g, px, py, W, H, 8, Theme.BG);

        // шапка
        Theme.round(g, px + 8, py + 8, W - 16, 30, 8, Theme.HEADER);
        g.drawString(font, "AURORA", px + 22, py + 19, Theme.ACCENT);
        g.drawString(font, "AURORA", px + 23, py + 19, Theme.ACCENT);
        String editorLabel = "HUD Editor";
        int btnW = 86;
        int btnX = px + W - 14 - btnW;
        Theme.round(g, btnX, py + 14, btnW, 18, 6, Theme.ACCENT_DARK);
        g.drawString(font, editorLabel, btnX + (btnW - font.width(editorLabel)) / 2, py + 19, Theme.TEXT);
        fixedHits.add(new Hit(btnX, py + 14, btnX + btnW, py + 32,
                b -> minecraft.setScreen(new HudEditorScreen(this)), null));

        // вкладки
        int tx = px + 18;
        int ty = py + 46;
        for (Tab t : Tab.values()) {
            int tw = font.width(t.label);
            boolean active = t == tab;
            g.drawString(font, t.label, tx, ty, active ? Theme.ACCENT : Theme.DIM);
            if (active) {
                g.fill(tx, ty + 12, tx + tw, ty + 13, Theme.ACCENT);
            }
            final Tab target = t;
            fixedHits.add(new Hit(tx - 4, ty - 4, tx + tw + 4, ty + 16, b -> {
                tab = target;
                scroll = 0;
                if (target == Tab.CONFIGS) refreshConfigs();
            }, null));
            tx += tw + 22;
        }
        g.fill(px + 12, py + 64, px + W - 12, py + 65, Theme.LINE);

        // список
        viewTop = py + 70;
        viewBottom = py + H - 10;
        int vx = px + 12;
        int vw = W - 24;

        g.enableScissor(vx, viewTop, vx + vw, viewBottom);
        int start = viewTop - (int) scroll;
        int cy = start;
        if (tab == Tab.CONFIGS) {
            cy = drawConfigs(g, vx, cy, vw, mouseX, mouseY);
        } else {
            Category cat = switch (tab) {
                case VISUALS -> Category.VISUALS;
                case HUD -> Category.HUD;
                default -> Category.UTILITIES;
            };
            for (Module m : ModuleManager.byCategory(cat)) {
                cy = drawModule(g, m, vx, cy, vw, mouseX, mouseY);
            }
        }
        g.disableScissor();

        contentHeight = cy - start;
        double maxScroll = Math.max(0, contentHeight - (viewBottom - viewTop));
        scroll = Math.max(0, Math.min(maxScroll, scroll));
    }

    private int drawModule(GuiGraphics g, Module m, int x, int cy, int w, int mx, int my) {
        final int rowH = 26;
        boolean hover = mx >= x && mx < x + w && my >= cy && my < cy + rowH && my >= viewTop && my < viewBottom;
        Theme.round(g, x, cy, w, rowH, 6, hover ? Theme.ROW_HOVER : Theme.ROW);
        g.drawString(font, m.name, x + 12, cy + 9, m.isEnabled() ? Theme.TEXT : Theme.TEXT_OFF);
        Theme.toggle(g, x + w - 40, cy + 6, m.isEnabled());

        boolean hasSettings = !m.getSettings().isEmpty();
        if (hasSettings) {
            boolean open = EXPANDED.contains(m);
            g.drawString(font, "...", x + w - 62, cy + 8, open ? Theme.ACCENT : Theme.DIM);
            listHits.add(new Hit(x + w - 72, cy, x + w - 46, cy + rowH, b -> toggleExpanded(m), null));
        }
        listHits.add(new Hit(x, cy, x + w, cy + rowH, b -> {
            if (b == 1 && hasSettings) {
                toggleExpanded(m);
            } else {
                m.toggle();
            }
        }, null));

        cy += rowH + 4;
        if (hasSettings && EXPANDED.contains(m)) {
            for (Setting s : m.getSettings()) {
                cy = drawSetting(g, s, x + 8, cy, w - 16);
            }
            cy += 4;
        }
        return cy;
    }

    private void toggleExpanded(Module m) {
        if (!EXPANDED.remove(m)) EXPANDED.add(m);
    }

    private int drawSetting(GuiGraphics g, Setting s, int x, int cy, int w) {
        final int h = 20;
        Theme.round(g, x, cy, w, h, 4, Theme.SETTING);
        g.drawString(font, s.name, x + 8, cy + 6, Theme.DIM2);

        if (s instanceof BoolSetting b) {
            Theme.toggleSmall(g, x + w - 30, cy + 4, b.get());
            listHits.add(new Hit(x, cy, x + w, cy + h, btn -> b.toggle(), null));

        } else if (s instanceof NumberSetting n) {
            int bx = x + w - 96;
            int bw = 84;
            Theme.round(g, bx, cy + 8, bw, 4, 2, Theme.TOGGLE_OFF);
            int fill = (int) Math.round(bw * n.fraction());
            if (fill > 0) Theme.round(g, bx, cy + 8, Math.max(fill, 4), 4, 2, Theme.ACCENT);
            Theme.round(g, bx + fill - 3, cy + 6, 6, 8, 3, Theme.TEXT);
            String val = n.step >= 1
                    ? String.valueOf(n.getInt())
                    : String.format(Locale.ROOT, "%.1f", n.get());
            g.drawString(font, val, bx - 8 - font.width(val), cy + 6, Theme.ACCENT);
            listHits.add(new Hit(bx, cy, bx + bw, cy + h, null, n::setFraction));

        } else if (s instanceof ModeSetting mo) {
            String txt = "< " + mo.get() + " >";
            g.drawString(font, txt, x + w - 8 - font.width(txt), cy + 6, Theme.ACCENT);
            listHits.add(new Hit(x, cy, x + w, cy + h, btn -> {
                if (btn == 1) mo.prev();
                else mo.next();
            }, null));

        } else if (s instanceof ColorSetting c) {
            int bx = x + w - 96;
            int bw = 84;
            for (int i = 0; i < bw; i++) {
                g.fill(bx + i, cy + 7, bx + i + 1, cy + 13, ColorSetting.fromHue(i / (float) bw));
            }
            int mxp = bx + Math.round(c.getHue() * (bw - 1));
            g.fill(mxp - 1, cy + 5, mxp + 1, cy + 15, Theme.TEXT);
            g.drawString(font, "R", bx - 14, cy + 6, c.isRainbow() ? Theme.ACCENT : Theme.DIM);
            listHits.add(new Hit(bx - 18, cy, bx - 2, cy + h, btn -> c.setRainbow(!c.isRainbow()), null));
            listHits.add(new Hit(bx, cy, bx + bw, cy + h, null, f -> c.setHue((float) f)));
        }
        return cy + h + 2;
    }

    private int drawConfigs(GuiGraphics g, int x, int cy, int w, int mx, int my) {
        final int rowH = 26;

        Theme.round(g, x, cy, w, rowH, 6, Theme.ACCENT_DARK);
        g.drawString(font, "+ Зберегти поточні налаштування як новий конфіг", x + 12, cy + 9, Theme.TEXT);
        listHits.add(new Hit(x, cy, x + w, cy + rowH, b -> {
            ConfigManager.save(ConfigManager.nextName());
            refreshConfigs();
        }, null));
        cy += rowH + 6;

        g.drawString(font, "Автозбереження: default (при закритті меню та виході)", x + 4, cy + 2, Theme.DIM);
        cy += 16;

        for (String name : new ArrayList<>(configs)) {
            Theme.round(g, x, cy, w, rowH, 6, Theme.ROW);
            g.drawString(font, name, x + 12, cy + 9, Theme.TEXT);

            int delX = x + w - 52;
            int loadX = x + w - 104;
            Theme.round(g, loadX, cy + 4, 46, 18, 5, Theme.ACCENT_DARK);
            g.drawString(font, "Load", loadX + 12, cy + 9, Theme.TEXT);
            Theme.round(g, delX, cy + 4, 46, 18, 5, Theme.TOGGLE_OFF);
            g.drawString(font, "Delete", delX + 7, cy + 9, Theme.TEXT_OFF);

            listHits.add(new Hit(loadX, cy + 4, loadX + 46, cy + 22, b -> ConfigManager.load(name), null));
            listHits.add(new Hit(delX, cy + 4, delX + 46, cy + 22, b -> {
                ConfigManager.delete(name);
                refreshConfigs();
            }, null));
            cy += rowH + 4;
        }
        return cy;
    }

    // ------------------------------------------------------------------ ввід

    private void activate(Hit h, double mx, int button) {
        if (h.click() != null) h.click().accept(button);
        if (h.slide() != null && button == 0) {
            dragging = h;
            h.slide().accept(fraction(h, mx));
        }
    }

    private static double fraction(Hit h, double mx) {
        return (mx - h.x1()) / (double) (h.x2() - h.x1());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x();
        double my = event.y();
        int button = event.button();

        for (Hit h : fixedHits) {
            if (h.in(mx, my)) {
                activate(h, mx, button);
                return true;
            }
        }
        if (my >= viewTop && my < viewBottom) {
            for (Hit h : listHits) {
                if (h.in(mx, my)) {
                    activate(h, mx, button);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging != null && dragging.slide() != null) {
            dragging.slide().accept(fraction(dragging, event.x()));
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll -= scrollY * 18;
        return true;
    }
}
