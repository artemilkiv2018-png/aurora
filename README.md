# Aurora (Fabric, Minecraft 1.21.11)

Візуальний клієнт: меню "Winter Blue", HUD-модулі, HUD-редактор, візуальні модулі, JSON-конфіги.

## Як отримати jar (без встановлення нічого на ПК)

1. Створи безкоштовний репозиторій на github.com (кнопка **New repository**).
2. Завантаж у нього **вміст** папки Aurora (розпакуй архів; **Add file → Upload files**; важливо, щоб
   `build.gradle` та папки `src` і `.github` були в корені репозиторію).
3. Відкрий вкладку **Actions**: збірка запускається сама (≈3–5 хв). Якщо ні, обери **Build Aurora → Run workflow**.
4. Коли збірка зелена, відкрий її і внизу в **Artifacts** завантаж `aurora-jar`. Усередині буде `aurora-0.1.0.jar`.
5. Поклади jar у `.minecraft/mods` разом із **Fabric Loader** і **Fabric API** для 1.21.11.

Якщо збірка червона, відкрий її лог, скопіюй помилку і надішли мені, я виправлю.

## Збірка локально (альтернатива)

Потрібні **JDK 21** і Gradle 9.x: `gradle build`, jar буде в `build/libs/`.
Для запуску з IntelliJ краще взяти офіційний шаблон з https://fabricmc.net/develop/template
і скопіювати в нього `src/`, `build.gradle`, `gradle.properties`, `settings.gradle`.

## Керування

- **Right Shift** відкриває меню (міняється в Налаштування → Керування → AURORA).
- Лівий клік по модулю вмикає/вимикає, правий клік або `...` відкриває налаштування.
- Кнопка **HUD Editor** у шапці меню: перетягуй HUD-елементи мишкою, ESC повертає в меню.
- Вкладка **Configs**: збереження, завантаження, видалення конфігів.
  Файли лежать у `.minecraft/config/aurora/*.json`, конфіг `default` зберігається автоматично.
- Для кожного модуля є окрема клавіша (спочатку не призначена): Налаштування → Керування → AURORA.

## Що є

- **Visuals:** Crosshair, Full Bright, Hit Particles, Trails, China Hat (кільця з часток)
- **HUD:** Watermark, Array List, FPS, Ping, Player Nickname, Target HUD, Notifications
- **Utilities:** Auto Respawn, Streamer Mode
- Налаштування: тумблери, повзунки, режими, колір з Rainbow, позиція HUD у % екрана

## Ще не зроблено (потрібен рендер у світі / міксини)

Справжня 3D-модель China Hat, Name Tags, Sky/Fog/Time, Aspect Ratio, Custom Hand, View Model,
Swing/Shield/Block Animation, Hit Color, Fire Customizer, Player Model. Їх варто додавати після того,
як ця версія успішно зібралась і запустилась, бо помилка в міксині може завадити запуску гри.

## Якщо не компілюється

Код написано під офіційні назви Mojang для 1.21.11 і не запускався в грі. Найімовірніші місця правок:
- `mc.getFps()` у FPS/Watermark
- сигнатури `mouseClicked/mouseDragged/mouseReleased` у `MenuScreen` та `HudEditorScreen`
- `HudElementRegistry` / `KeyBindingHelper` в `Aurora.java`
- `new DustParticleOptions(int, float)` у Trails/China Hat

## Як додати свій модуль

1. Створи клас у `modules/`, наслідуй `Module` (або `HudModule` для HUD).
2. Додай налаштування через `add(new NumberSetting(...))`.
3. Зареєструй у `ModuleManager.init()` і додай рядок `key.aurora.module.<id>` у `lang/en_us.json`.
