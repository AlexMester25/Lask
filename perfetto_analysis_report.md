# Lask — статистика производительности по Perfetto-трассам

Дата измерений: 8 ноября 2026.
Устройство: **TFY-LX1 (HONOR), Android 13, API 33**, `ro.build.type=user`, экран 1080×2388 @ 90 Гц.
Артефакт: release-сборка `dev.alexmester.lask` 1.0, `arm64: [status=speed-profile]` (скомпилирована с Baseline Profile).

Обе трассы записаны Perfetto (`perfetto v25.0` на устройстве) с одним и тем же конфигом:

- буферы: 64 МБ, RING_BUFFER; `duration_ms: 12000`;
- `linux.ftrace`: `sched_switch`, `sched_wakeup/waking`, `cpu_frequency`, `cpu_idle`, `binder_transaction(_received)`; atrace-категории `gfx`, `view`, `wm`, `am`, `input`, `res`; atrace-приложение `dev.alexmester.lask`;
- `android.surfaceflinger.frametimeline`, `linux.process_stats`, `android.packages_list`.

Анимации системы были выключены (`window_animation_scale` / `transition_animation_scale` / `animator_duration_scale` = 0), чтобы не смешивать анимации переходов с работой приложения. После записи значения восстановлены в 1.0.

Файлы:

| Трасса | Размер | Длительность | Пакет/upid |
|---|---|---|---|
| `...\Temp\perfetto\traces\startup.pftrace` | 6.02 МБ | 12.75 с | `dev.alexmester.lask`, upid 828, pid 21499 |
| `...\Temp\perfetto\traces\scroll.pftrace` | 20.60 МБ | 15.59 с | `dev.alexmester.lask`, upid 806, pid 21499 |

Health обеих трасс: единственная проблема — `trace_sorter_negative_timestamp_dropped: 2` (не влияет на анализ).

---

## 1. Холодный старт

`am start -W`: `LaunchState: COLD`, `TotalTime: 303 мс`.
Perfetto `android_startups`: **cold startup = 303.87 мс** (startup_id 41).

### Разбивка старта по причинам (`android_startup_opinionated_breakdown`)

| Причина | Кол-во | Сумма, мс |
|---|---:|---:|
| `choreographer_do_frame` | 55 | 76.74 |
| `launch_delay` | 1 | 59.15 |
| `binder` | 349 | 58.75 |
| `bind_application` | 33 | 39.63 |
| `Running` (main thread) | 41 | 34.57 |
| `activity_start` | 30 | 11.50 |
| `activity_resume` | 28 | 8.43 |
| `R+` | 15 | 7.15 |
| `S` | 3 | 3.34 |
| `resources_manager_get_resources` | 3 | 3.00 |
| `inflate` | 1 | 0.98 |

Итого: ~60 мс уходит на системную задержку запуска (иконка → `bindApplication`), ~40 мс на `bindApplication` (инициализация приложения), остальное — кадры первого экрана и IPC.

### CPU в первые 700 мс (по потокам приложения)

| Поток | CPU, мс | Слайсов |
|---|---:|---:|
| `alexmester.lask` (main) | 287.7 | 220 |
| `RenderThread` | 191.4 | 92 |
| `DefaultDispatch` (Coil) | 119.9 | 191 |
| `cg.r Dispatcher` | 17.3 | 22 |
| `binder:21499_3` | 13.8 | 79 |
| `arch_disk_io_0` | 10.2 | 9 |
| `Profile Saver` | 9.8 | 34 |
| `EmojiCompatInit` | 5.7 | 20 |

Main thread за 700 мс: `Running` 287.7 мс, `S` 344.3 мс. RenderThread: `Running` 191.4 мс, `S` 369.6 мс.

### Тяжёлые кадры (main thread)

| Старт, мс от начала старта | Длительность кадра |
|---:|---:|
| 204.0 | 24.34 мс |
| 236.3 | **61.78 мс** |
| 301.3 | 10.89 мс |
| 325.4 | **76.09 мс** |
| 402.7 | 25.96 мс |
| далее (с 430 мс) | 0.6–8.6 мс |

Внутри кадра 325.4 мс: `traversal` 73.13 мс, `draw` 72.96 мс, `Record View#draw()` 72.32 мс, `AndroidOwner:measureAndLayout` **66.51 мс**.
Внутри кадра 236.3 мс: `traversal` 48.81 мс, `measure` 32.84 мс, `TextStringSimpleNode::measure` **30.25 мс**.

Суммарно за старт: `TextStringSimpleNode::measure` — 82 вызова, 62.13 мс; `AndroidOwner:measureAndLayout` — 3 вызова, 66.89 мс. То есть основная цена первого экрана — **измерение/раскладка текста и первая компоновка Compose**, а не рекомпозиция (`Compose:recompose` 35.2 мс / 24 вызова).

### Классификация кадров (frame timeline, приложение)

| Кадр (старт, мс) | Длительность | Jank tag / type |
|---:|---:|---|
| 236.3 | 58.46 мс | Self Jank: SurfaceFlinger Scheduling, App Deadline Missed |
| 301.3 | 34.54 мс | Self Jank: App Deadline Missed, Buffer Stuffing |
| 325.4 | 106.13 мс | Self Jank: App Deadline Missed, Buffer Stuffing |
| 402.7–… | ~13.7–14.6 мс ×21 | Buffer Stuffing (переход) |

После ~700 мс кадры стабильны.

### Из чего состоит кадр 236.3 мс (61.78 мс)

`TextStringSimpleNode::measure` — 30.25 мс, и внутри него:

| Слайс | Длительность |
|---|---:|
| `TextLayout:initLayout` | **21.90 мс** |
| `AssetManager::OpenNonAssetFd(res/XS.ttf)` | 0.30 мс |
| `TypefaceCompat static init` | 0.03 мс |

Следующие вызовы `TextLayout:initLayout` в этой же трассе — 0.27–0.67 мс. То есть 21.9 мс — это **одноразовая инициализация текстового стека (Minikin/StaticLayout)**, а не загрузка TTF и не «дорогой заголовок». Она целиком попадает в критический путь первого кадра.

### Фоновые инициализации

`EmojiCompat.FontRequestEmojiCompatConfig.buildTypeface` — 32.5 мс на ~697 мс и ещё 26.9 мс; выполняется вне main thread (`EmojiCompatInit`). **Старт это не задерживает:** инициализация происходит через ~400 мс после первого кадра, на TTID не влияет (исправление к первой версии отчёта). `EmojiCompatInitializer` на 160 мс занимает 0.77 мс.

---

## 2. Скролл ленты

Сценарий: 6 свайпов по 180 мс (`input swipe 540 1800 540 600 180`), между ними 0.6 с. Проанализировано окно 5.39 с активного скролла — **383 кадра**.

### Плавность кадров

| Метрика | Значение |
|---|---:|
| Кадров | 383 |
| FPS по секундам | 89, 87, 88, 84 (далее 16, 19 — затухание флинга) |
| Средняя длительность | 11.32 мс |
| P50 / P90 / P95 / P99 | 11.19 / 12.69 / 13.52 / 14.80 мс |
| Максимум | 16.42 мс |
| Кадров > 16.67 мс (бюджет 60 Гц) | **0** |
| Кадров > 11.11 мс (бюджет 90 Гц) | ~55 % (перелёт 0.1–2.4 мс) |

Дисплей работает на 90 Гц (modeId 1, fps=90; наблюдаемые 84–89 fps), поэтому бюджет кадра 11.1 мс. Приложение фактически упирается в него: 54.8 % кадров попадают в диапазон 11.1–16.7 мс, но **ни один кадр не превысил 16.4 мс**, то есть 60 кадров/с не пробиваются никогда.

### Почему кадры «на границе» — стоимость по потокам

| Поток / метрика | Среднее на кадр | Максимум |
|---|---:|---:|
| main `Choreographer#doFrame` (395 шт.) | **1.38 мс** | 12.73 мс |
| `RenderThread` `Drawing 1080×2388` (383 шт.) | **4.77 мс** | 7.74 мс |
| `RenderThread` ожидание GPU (`GPU completion / waitForever`, 383 шт.) | **5.47 мс** | 12.31 мс |
| `RenderThread` `flush commands` | 1.88 мс | 2.60 мс |
| `RenderThread` `eglSwapBuffersWithDamageKHR` | 1.32 мс | 4.04 мс |
| main `AndroidOwner:measureAndLayout` (460 шт.) | 0.98 мс | 6.62 мс |
| main `traversal` | 0.80 мс | 2.20 мс |
| main `Compose:recompose` (99 шт.) | 0.83 мс | 1.95 мс |
| `deliverInputEvent` (212 шт.) | 0.58 мс | 4.58 мс |

CPU-время за окно скролла:

| Поток | CPU, мс | Доля |
|---|---:|---:|
| `RenderThread` | 2014.8 | 5.26 мс/кадр |
| main `alexmester.lask` | 1298.0 | 3.39 мс/кадр |
| `DefaultDispatch` (Coil) | 623.6 | вне main |
| `HeapTaskDaemon` | 167.1 | — |
| `cg.r Dispatcher` | 158.1 | — |
| binder-потоки (`_2`,`_3`,`_4`) | 192.6 | — |

Главный вывод: **UI-поток не является узким местом** (1.38 мс на кадр, 99 рекомпозиций по 0.83 мс). Узкое место — конвейер `RenderThread` + GPU: полный перерисов 1080×2388 каждые ~11 мс, 4.77 мс CPU на команды отрисовки плюс 5.47 мс ожидания завершения GPU. Суммарно ~10.2 мс из 11.1 мс бюджета.

### Состав графической нагрузки (RenderThread, за 383 кадра)

| Слайс | Кол-во | Сумма, мс | На кадр |
|---|---:|---:|---:|
| `FillRectOp` | 17 562 | 294.9 | ~45 шт. |
| `AtlasTextOp` | 6 810 | 155.6 | ~18 шт. |
| `renderLayersImpl` | 383 | 266.6 | 0.70 мс |
| `renderFrame` | 383 | 493.2 | 1.29 мс |
| `drawLayer [graphicsLayer] 1080×300` | 383 | 96.5 | 0.25 мс |

`Compose:lazy:prefetch:*` — 48+47 слайсов, 97.7 + 93.0 мс (`prefetch:measure` max 3.85 мс): prefetch работает и на main thread заметного следа не оставляет.

### Coil / изображения

За скролл декодировано и загружено в GPU **13 изображений** (`allocateHardwareBitmap` 29.2 мс, `uploadHardwareBitmap` 19.1 мс, `onUploadHardwareBitmap` 16.1 мс) — всё на `DefaultDispatch`, main thread не блокируется. `computePalette` — 0.1 мс суммарно.

### Haze / blur

Слайсов с `blur`/`haze`/`RenderEffect` в имени нет; единственный след — `alpha caused saveLayer 1080x300` (383 раза, **1.2 мс суммарно**). Вывод «нижний бар не является проблемой», сделанный по именам слайсов, **не подтвердился** прямым A/B: blur стоит 1.75 мс на кадр в RenderThread — см. раздел 5.

### Binder и память

- `binder transaction` 835 шт., 2092.7 мс (суммарно по всем процессам трассы, не только приложение), `binder reply` 1816.7 мс.
- GC-слайсов за скролл — 0; `HeapTaskDaemon` 167 мс CPU (конкурентная разметка, без пауз в кадрах).
- Jank-теги Android: 379 кадров «Other Jank / SurfaceFlinger Scheduling» (SF не успевает зафиксировать буфер в 11.1 мс), 4 кадра «Self Jank / Unknown Jank» (31.2 мс суммарно, max 16.42 мс). Прикладных (app deadline) джанков нет.

---

## 3. Сводка

| Сценарий | Метрика | Результат |
|---|---|---|
| Холодный старт | TTID (Perfetto / `am start`) | 303.9 / 303 мс |
| Холодный старт | Самая тяжёлая работа | первая раскладка `measureAndLayout` 66.5 мс, измерение текста 62.1 мс |
| Холодный старт | Jank-кадры | 3 Self Jank (58.5 / 34.5 / 106.1 мс) в первые 0.33 с |
| Скролл | FPS | 84–89 (дисплей 90 Гц) |
| Скролл | P50 / P90 / P99 длительности кадра | 11.19 / 12.69 / 14.80 мс |
| Скролл | Кадров > 16.67 мс | 0 из 383 |
| Скролл | Main thread на кадр | 1.38 мс (рекомпозиция 0.83 мс) |
| Скролл | RenderThread + GPU на кадр | 4.77 + 5.47 ≈ 10.2 мс из 11.1 мс |

Что реально ограничивает:

1. **Старт — первая компоновка экрана.** `measureAndLayout` 66.5 мс и `TextStringSimpleNode::measure` 62.1 мс/82 вызова дают кадры 61.8 и 76.1 мс. Из них 21.9 мс — первая раскладка текста с переносом строк (одноразовая инициализация текстового стека). Это цена первого экрана ленты (широкие карточки + текст), а не инициализация приложения (`bindApplication` 39.6 мс). Разогрев в фоне снимает эти 22 мс с главного потока — см. раздел 4.
2. **Скролл — GPU/отрисовка, а не Compose.** 45 `FillRectOp` и 18 `AtlasTextOp` на кадр при полном перерисе 1080×2388; ожидание GPU 5.47 мс/кадр при бюджете 11.1 мс. UI-поток имеет большой запас. На всём `NavHost` висит `hazeSource` (1080×2388), а нижний бар рисует `hazeEffect` с `blurRadius = 20dp` и `noiseFactor = 0.05f` — единственный в приложении постоянный потребитель полноэкранного слоя и blur-RenderEffect. A/B (раздел 5): blur стоит **1.75 мс на кадр** в RenderThread.
3. **Пустая работа в карточке.** `DefaultLayout` всегда компонирует `LaskBookmarkButton` (`IconButton` + `Icon` + `animateColorAsState`) в `Box` шириной 0dp, хотя в ленте `selectionMode = false` и передан пустой `onBookmarkToggle`. Плюс `Modifier.animateContentSize()` в каждой карточке. Влияет на композицию первого экрана и на появление элементов при скролле, не на каждый кадр. Правка внесена, эффект на TTID не измерим — см. раздел 4.
4. **Анимации списка.** За скролл: 48 новых элементов (`Compose:onRemembered`), `animateItem()` с fade-in по умолчанию, `crossfade(true)` задан дважды (в `ImageLoader` и в `ImageRequest`). `Recomposer:animation` — 292 кадра из 383 (292.3 мс), `animation` callback — 612 слайсов (460.8 мс). Это единственная непрерывная нагрузка на main thread во время флинга.
5. **Coil** — 13 декодирований за 5.4 с скролла уходят на `DefaultDispatch` (29.2 + 19.1 мс), на кадры не влияют.

Ограничения измерений:

- по одной трассе на сценарий (не медиана серии), поэтому абсолютные значения ±несколько мс;
- анимации системы выключены — реальные переходы Activity/окна в эти цифры не входят;
- 90 Гц: «Other Jank / SurfaceFlinger Scheduling» — это пограничные 11.1–11.4 мс, а не пропущенные кадры в терминах 60 Гц;
- трассы сняты на release-сборке со speed-profile; debug/warm-up не измерялись.

---

## 4. Проверка правок: разогрев текста и карточка

Все замеры — та же release-сборка, `arm64: [status=speed-profile]`, те же настройки анимаций, тот же конфиг Perfetto. После каждой установки APK приложение запускалось один раз, затем `cmd package compile -m speed-profile -f` и проверка `[status=speed-profile]`.

### 4.1. Разогрев текста: почему первая попытка не сработала

Первый вариант разогрева мерил строку без ограничения ширины. Результат: `TextLayout:initLayout` на главном потоке остался **21.76 / 22.08 / 21.93 мс**, TTID 335.0 / 281.3 / 294.5 мс — в пределах разброса базы.

Причина найдена прямым замером шагов разогрева через `Log` (release, speed-profile, warm launch):

| Шаг разогрева | Время |
|---|---:|
| первая раскладка без ограничения ширины | 4.16 мс |
| повторная раскладка той же строки | 0.04 мс |
| `Inter`, `maxWidth = 1080px`, `maxLines = 2`, перенос строк | **33.4 мс** |
| `Merriweather`, то же | 15.3 мс |
| только эмодзи (`🇺🇸 🇰🇷 🇩🇪`) | 1.45 мс |

То есть дорогая не «инициализация текста» и не emoji/fallback-шрифт, а **первая раскладка с переносом строк** (инициализация разбиения на строки в Minikin/StaticLayout). Без ограничения ширины этого пути нет — поэтому прогрев не помогал.

### 4.2. Разогрев с переносом строк: результат

Разогрев теперь выполняет две раскладки с `maxWidth = 1080px` и `maxLines = 2` — `Inter` и `Merriweather` — в `App.onCreate` на `Dispatchers.Default`.

| Метрика (окно 700 мс старта) | База (1 трасса) | Неудачный разогрев (3 трассы) | Разогрев с переносом (5 трасс) |
|---|---:|---:|---:|
| `TextLayout:initLayout` на main thread | 21.90 мс | 21.76 / 22.08 / 21.93 | **0.10–0.12 мс** |
| `TextLayout:initLayout` на рабочем потоке | — | 0.15–0.27 мс | **22.85–24.42 мс** |
| `TextStringSimpleNode::measure` (82 вызова) | 62.1 мс | 54.9–64.3 мс | **25.3–31.5 мс** |
| `AndroidOwner:measureAndLayout` | 66.5 мс | 56.5–81.8 мс | **41.7–54.3 мс** |
| TTID, медиана | 303.9 мс | 294.5 мс | **271.1 мс** (304.9 / 262.1 / 271.1 / 305.3 / 271.1) |

Разогрев реально выполняется: в трассах `initLayout` на `DefaultDispatch` виден на 170–204 мс, то есть за ~50–80 мс до первого `measure` ленты, и до него есть только 0.19–0.27 мс вызовы.

### 4.3. Карточка: `LaskBookmarkButton` только в `selectionMode`

A/B на одинаковом разогреве (по 2 трассы, `Compose:recompose` и TTID в окне 700 мс):

| | TTID | `Compose:recompose` |
|---|---:|---:|
| без правки карточки | 273.3 / 278.5 мс | 42.8 / 34.2 мс |
| с правкой карточки | 305.3 / 271.1 мс | 33.4 / 34.1 мс |

Разница в пределах шума (n=2, разброс между прогонами больше эффекта): **на старт правка не влияет**. Оставлена как устранение невидимых интерактивных узлов (кнопка с нулевой шириной и пустым обработчиком в каждой карточке), а не как выигрыш по времени.

Проверено на устройстве: в `Избранном` при включённом edit mode кнопка отрисовывается, нажатие переключает `isKept` (заливка → outline), `animateContentSize` и раскладка карточки не изменились; вне edit mode кнопки нет, заголовок занимает освободившееся место.

### 4.4. Скролл: смена jank-тегов — артефакт классификации

После правки карточки за скролл: 400 кадров, avg 11.49 мс, max 22.84, **399 «No Jank» + 1 «Self Jank»** вместо 383 «Other Jank / SurfaceFlinger Scheduling» в базе. Это **не эффект правки**:

| | База | После |
|---|---:|---:|
| `expected_frame_timeline_slice` | 383 слайса × 16.22 мс | 400 слайсов × 16.22 мс |
| `present_type` | 383 × `Early Present` | 399 × `On-time Present`, 1 × `Late Present` |
| Кадров в секунду | 89 / 87 / 88 / 84 … | 90 / 90 / 90 / 89 |
| Avg длительность кадра | 11.19 мс | 11.49 мс |

Ожидаемый период кадра одинаковый (16.22 мс), фактический темп тоже; изменилась только классификация presented-кадров (`Early` → `On-time`), которая и даёт разные jank-теги. Ни один из вариантов не является пропуском кадра: 0 кадров > 16.67 мс в обеих трассах.

---

## 5. A/B по haze в нижнем баре

Цель — отделить стоимость `hazeEffect` (blur нижнего бара) от остального скролла. Пять сборок, по 5 трасс на каждую, одинаковый сценарий: 6 свайпов по 180 мс с паузами 0.6 с в 12-секундной трассе. Release, `arm64: [status=speed-profile]`, одинаковые настройки анимаций.

### 5.1. Почему сравнивать по `Drawing`, а не по P50

Экран (HONOR, 90 Гц) сам переключает 90↔60 Гц по фазе жеста, причём `dumpsys display` показывает 90 Гц, пока трасса пишет 60. В одной серии P50 меняется от 7.35 до 16.14 мс, поэтому длительность кадра и jank-теги между прогонами несравнимы. Зафиксировать частоту не удалось:

- `cmd display set-user-preferred-display-mode 1080 2388 90` → `SecurityException: Package android does not belong to 2000`;
- `settings put global peak_refresh_rate 90` и `min_refresh_rate 90` записываются, но `DisplayModeDirector` их игнорирует (`PRIORITY_DEFAULT_REFRESH_RATE -> maxRefreshRate=60.0`);
- `cmd power set-fixed-performance-mode-enabled true` режим не фиксирует.

Поэтому сравнение построено на метриках RenderThread на кадр — они не зависят от частоты обновления. Разброс внутри варианта ±0.05–0.1 мс, на порядок меньше измеряемого эффекта.

### 5.2. Результаты (мс на кадр, 5 трасс на вариант)

| Вариант | `Drawing` | `renderFrame` | `renderLayersImpl` | `flush commands` |
|---|---:|---:|---:|---:|
| **A.** Как есть: `blurRadius 20dp`, `noiseFactor 0.05` | **4.86** (4.80–4.91) | 1.41 | 0.83 | 1.92 |
| **B.** `blurEnabled = false` | **3.11** (3.04–3.18) | 0.53 | 0.002 | 1.32 |
| **C.** Без `hazeEffect` и без `hazeSource` | **3.22** (3.12–3.29) | 0.53 | 0.00 | 1.39 |
| **D.** `blurRadius 12dp`, `noiseFactor 0` | **4.47** (4.38–4.59) | 1.36 | 0.83 | 1.66 |
| **E.** A + `inputScale = HazeInputScale.Auto` | **4.99** (4.86–5.12) | 1.59 | 0.96 | 1.92 |

Дополнительно по трассе A против B: draw-операций на кадр 13.3 против 1.9 (`OpsTask::onExecute` 5338 / 749 на 400 кадров), `FillRectOp` 42.8 против 16.1, `drawLayer [graphicsLayer] 1080×300` 0.27 мс против ~0.

### 5.3. Выводы

1. Blur стоит **1.75 мс на кадр** в RenderThread — это ~16 % бюджета 11.11 мс при 90 Гц и самая крупная известная статья скролла после отрисовки самого списка.
2. Стоимость создаёт **сам факт наличия `RenderEffect`**, а не пиксельная работа размытия: HWUI делает отдельный проход по слою (`renderLayersImpl` 0.83 + `drawLayer` 0.27) и отдельный `renderFrame` 1.41 вместо 0.53.
3. Уменьшение радиуса 20 → 12 dp и удаление шума возвращает только **0.39 мс** — вчетверо меньше, чем весь blur. Менять внешний вид ради этого не имеет смысла.
4. `inputScale` в Haze 1.7.2 делает **хуже** (+0.13 мс): добавляет проходы масштабирования.
5. Тинт и прозрачность бесплатны: вариант B (только тинт) и C (совсем без haze) в пределах 0.1 мс друг от друга.

Практический выбор бинарный:

- **оставить blur** — платим 1.75 мс/кадр (≈157 мс работы RenderThread в секунду) и сохраняем матовое стекло;
- **убрать blur** — возвращаем 1.6–1.75 мс.

Компромиссных вариантов, сохраняющих blur и заметно дешевле, в Haze 1.7.2 нет.

### 5.4. Визуальная разница

Скриншоты на одном и том же месте ленты (`%TEMP%\perfetto\haze_visual_*.png`):

| Вариант | Что видно в нижнем баре | Цена |
|---|---|---:|
| A. blur 20dp | заголовок за баром превращается в мягкое пятно, бар читается | 4.86 мс/кадр |
| B. blur выключен, тинт 0.5 | текст за баром резкий и просвечивает сквозь бар, мешает его содержимому | 3.11 мс/кадр |
| C. без haze, плотный `brand_blue10` | чистый непрозрачный бар, фон не просвечивает | 3.22 мс/кадр |

Вариант B визуально не годится: полупрозрачность без размытия даёт читаемый текст под иконками. Остаётся выбор между «стеклом» (A) и плотным баром (C); C дешевле на 1.64 мс/кадр.

**Решение: оставляем вариант A — матовое стекло.** Полупрозрачный бар — осознанное продуктовое требование, поэтому 1.75 мс/кадр в RenderThread принимаются как цена внешнего вида. Варианты B и C в код не идут; `LaskBottomBar` и `ArticleDetailBottomBar` остаются без изменений.

Та же конфигурация эффекта продублирована в `ArticleDetailBottomBar`, поэтому решение касается и экрана статьи.

Ограничения: одно устройство (Mali-G57/HONOR), один сценарий скролла; числа сопоставимы между вариантами, но не переносимы на другой GPU.

---

## Приложение: запросы, на которых построены цифры

```sql
-- Старт: длительность и разбивка
INCLUDE PERFETTO MODULE android.startup.startups;
SELECT startup_id, package, startup_type, ts, dur/1e6 AS dur_ms FROM android_startups;

INCLUDE PERFETTO MODULE android.startup.startup_breakdowns;
SELECT reason, count(*) AS n, sum(dur)/1e6 AS total_ms
FROM android_startup_opinionated_breakdown WHERE startup_id = 41
GROUP BY reason ORDER BY total_ms DESC;

-- Скролл: длительности кадров и перцентили
SELECT count(*), avg(dur)/1e6, max(dur)/1e6
FROM actual_frame_timeline_slice WHERE upid = 806;

SELECT jank_tag, jank_type, count(*), sum(dur)/1e6, avg(dur)/1e6, max(dur)/1e6
FROM actual_frame_timeline_slice WHERE upid = 806
GROUP BY jank_tag, jank_type ORDER BY 3 DESC;

-- Скролл: стоимость потоков
SELECT th.name, count(*), avg(s.dur)/1e6, max(s.dur)/1e6
FROM slice s JOIN thread_track tt ON s.track_id = tt.id
JOIN thread th ON tt.utid = th.utid
WHERE th.upid = 806
  AND ((th.name = 'alexmester.lask' AND s.name GLOB 'Choreographer#doFrame*')
    OR (th.name = 'RenderThread' AND s.name GLOB 'Drawing *'))
GROUP BY th.name;

-- Скролл: CPU по потокам
SELECT th.name, sum(sc.dur)/1e6 AS cpu_ms
FROM sched_slice sc JOIN thread th ON sc.utid = th.utid
WHERE th.upid = 806 GROUP BY th.name ORDER BY cpu_ms DESC;

-- A/B haze: работа RenderThread на кадр (метрики раздела 5)
SELECT th.name AS thread,
       CASE WHEN sl.name GLOB 'Choreographer#doFrame*' THEN 'Choreographer#doFrame'
            ELSE sl.name END AS slice,
       count(*) AS n, round(sum(sl.dur)/1e6, 1) AS total_ms, round(avg(sl.dur)/1e6, 3) AS avg_ms
FROM slice sl
JOIN thread_track tt ON sl.track_id = tt.id
JOIN thread th ON tt.utid = th.utid
WHERE th.upid = (SELECT upid FROM process WHERE name = 'dev.alexmester.lask' LIMIT 1)
  AND ((th.name = 'GPU completion' AND sl.name = 'waitForever')
    OR (th.name = 'RenderThread' AND (sl.name GLOB 'Drawing*' OR sl.name IN ('flush commands', 'renderFrame', 'renderLayersImpl')))
    OR (th.name = 'alexmester.lask' AND sl.name = 'Recomposer:animation'))
GROUP BY thread, slice ORDER BY thread, total_ms DESC;
```

Воспроизведение записи:

```powershell
adb push app_jank.pftxt /data/misc/perfetto-configs/app_jank.pftxt
adb shell "perfetto -c /data/misc/perfetto-configs/app_jank.pftxt --txt --background-wait -o /data/misc/perfetto-traces/lask_startup.pftrace"
adb shell "am start -W -n dev.alexmester.lask/.MainActivity"
adb pull /data/misc/perfetto-traces/lask_startup.pftrace
python "$SKILL_ROOT/references/perfetto/bin/trace_processor" query <trace> "<SQL>"
```

Примечание: `perfetto` не читает конфиг из `/data/local/tmp` (errno 13) — только из `/data/misc/perfetto-configs/`.
