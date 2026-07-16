# Changelog

- Externalized Java, Gradle project state, client/server instances, logs and test
  worlds to `%LOCALAPPDATA%\Gravesown`, while reusing `%USERPROFILE%\.gradle` for
  dependencies. Routine builds and launches are now strictly offline/no-daemon and
  cannot regrow multi-gigabyte caches inside the repository.
- Added clone-to-play bootstrap behavior to `launcher.cmd` and `play.cmd`: a fresh
  download runs the explicit setup once, then opens the branded launcher or client;
  prepared installations reuse the same external runtime.
- Added GitHub publication tooling: repository-content and large-file safety checks,
  Windows CI/tagged-release workflows, an offline launcher diagnostic, and a release
  packager that produces the mod JAR plus a complete source/launcher ZIP without
  redistributing Minecraft binaries, account state, caches, logs or worlds. Every
  package is re-extracted and verifies its structure, privacy, JAR hash and EXE
  diagnostic before it is accepted.
- Normalized every newly generated Gloamwater bed: reviewed regional ground becomes
  deterministic Gloam Sand/Muck through organic multi-scale coordinate noise, while
  every independent 4x4 ecology cell probes all sixteen columns before placing at most
  one of five aquatic plants. Small ponds no longer receive a second dense pass.
- Adopted Art Language V3: Ashen Sod is the terrain benchmark, natural surfaces use
  brighter vanilla-adjacent value separation without a global gray/pink/cyan wash or
  shared brick-stripe pattern, and all seven wood families retain distinct colors.
  Implemented creatures, armor, items and functional blocks share the same hard-edged
  pixel/material hierarchy.
- Replaced alphabetical art generation with an explicit ownership manifest whose final
  V3 stage covers every shipped texture. `artcheck.cmd` validates native dimensions,
  model references and four review contact sheets; release art requires two complete
  regenerations with identical shipped-texture hashes.
- Added independent scrollable rails for Crafts categories and Guide topics, including
  clipping, scrollbars, hit-testing and selection preservation.
- Corrected the client smoke's Creative navigation to use a real NeoForge page and
  registered custom station recipes in the client category index, removing the false
  `0 / 2` capture and harmless unknown-category warnings.

- Expanded the nine Overworld climate regions to roughly four times the prototype span
  while removing natural shoot carpets; wood-family shoots are player propagation items.
- Made aquatic ecology chunk-aligned across every water-bearing biome, with one growth
  candidate per 4x4 bed cell; a new GameTest locks all 16 cells per chunk. Low-weight
  Veilfin/Rootskimmer groups now keep cross-biome lakes alive without flattening the
  denser Gloam Sea and Rotfin predator niches.
- Fixed Story panning bounds at high zoom, removed the Field Kitchen helper label and
  added a localized dedicated 3x2 kitchen recipe layout in the Codex.
- Rebuilt Ribroot, Emberbark and Palevine doors around one panel/window/hinge grammar.
- Upgraded strict world-audit reports to schema 3 with a non-generating wide climate
  probe; Full still deeply audits 867 FULL chunks and proves the exact nine-biome
  Gravesown allowlist.

Все заметные изменения проекта записываются здесь.

## Unreleased

### Fixed

- Reliquary Crates and Sawmills now have closed opaque undersides, preserve their
  horizontal placement facing and expose their expected open/close or craft sounds.
- Throwing a Hushstone Spear now consumes only the real held stack, preserves the
  exact damaged spear in the projectile and recovers it without duplicating the item.
- Sticky Gravesown ground now preserves its intended horizontal slowdown without
  suppressing the player's normal jump impulse, so a one-block ledge remains climbable.
- Replaced the visible Gloam Sand/Muck checkerboard with deterministic warped multi-scale
  material noise. Aquatic plant placement remains independently bounded by its 4x4
  ecology grid.
- Rebuilt the Creative survival-inventory background from the exact 1.21.1 slot
  coordinates, removed the selected-tab underline and closed the remaining one-pixel
  recipe-row gap. Final FHD capture verifies the corrected player/inventory layout.
- Replaced the flashing Gloamwater sheets with vanilla 1.21.1 water animation sprites.
  The custom fluid keeps its Gravesown ids, tint, fog and ordinary water flow behavior.
- Fixed a startup crash caused by registering the Reliquary Crate screen against the
  already-owned vanilla 9x4 menu; the crate now has a dedicated menu type.
- Fixed the Gravework title translation and the final one-pixel Creative slot offset.
- Removed dangling right-edge fragments and slider-handle gaps from the common widget
  sprites. Locks, the recipe-book button/panel and Creative inventory now use the
  Gravesown interface family, and dependency arrows are drawn as aligned pixel geometry.
- Gloamwater now renders translucently with alpha-bearing art and follows vanilla-water
  source conversion, flow distance, level loss, viscosity and tick pacing while keeping
  its separate Gravesown registry family.
- Gloamwater fish no longer receive constant upward velocity or select exposed
  surface targets; depth-aware target choice and wider arrival distance stop the
  repeated surface climb and tight circling.
- The Survivor Codex no longer renders as a blank tiled background. Progression now
  opens the full-screen Survival Hub through a versioned NeoForge payload.

- Кодекс выжившего открывается по правому клику как полноэкранный адаптивный Центр
  выживания с разделами «Сюжет» и «Крафты». Сервер вычисляет условия и подтверждает
  ручные отметки, а клиент получает две семибитные маски через payload протокола 2.
- Одноразовая выдача Кодекса срабатывает и при первом входе сразу в Survival, и при
  первом последующем переходе Creative → Survival. Постоянный серверный флаг не даёт
  получить дубликаты после переподключения или повторной смены режима.

### Changed

- Added `clean-storage.cmd`, a guarded repeatable cleanup for duplicate
  project-local Gradle caches and all disposable ordinary, dedicated-server,
  client-smoke and world-audit worlds. It preserves the project JDK, sources,
  launcher, release JAR, screenshots and verification reports.
- Reduced the over-broad nine-biome climate regions to half their previous linear
  width while retaining broad exploration-scale transitions and the exact allowlist.
- Expanded regional tree placement with multiple bounded silhouettes so forests no
  longer form repeated clean rows or identical crowns.
- Recolored only the client presentation layer and Windows launcher to the approved
  cold navy/steel/fog-blue palette. World blocks, items, entity/armor textures,
  Gloamwater and biome effects retain their reviewed natural local colors.
- Added a non-cumulative immutable launcher background master; one deterministic
  generator now supplies the matching launcher, title and allowlisted screen art.
- The Codex now has four modes: `Story`, categorized `Crafts`, branching `Chain` and a
  scrollable seven-topic `Guide`. The obsolete Survival Hub title, subtitle and chain
  control hint were removed.
- New-world creation is fixed to `After the Silence`: the only exposed preset is the
  Gravesown preset, structures remain enabled and bonus chests/custom preset controls
  cannot change the supported total-conversion state.
- Creation dependencies moved from the Crafts detail view into a separate pannable,
  zoomable and auto-fitted `Chain` mode. Independent roots form real branches, clicking
  a craftable node opens its Crafts entry, and `R` over a container item requests the
  current server Codex state and opens that item's graph directly.
- The built-in total-conversion data filter now removes every vanilla recipe as well as
  every vanilla advancement; the Codex indexes the complete active Gravesown catalog.
- Routine client acceptance is now one final 1920x1080 run; compact 854x480 captures are
  optional regressions and never the sole visual reference.
- Replaced the two-page quest book and ordinary Gravesown advancements with a
  pannable/zoomable `Story / Crafts / Chain / Guide` Codex. Fourteen server-owned conditions become
  complete only after a validated Claim press, with sound and custom slide notice.
- Crafts now reads every recipe synchronized by the active server. Localized-name
  and registry-id search, clickable exact station grids and a draggable recursive
  creation path replace the old passive ingredient-card list.
- Quietskin Hood, Coat, Legwraps and Boots now use symmetric strict Gravework 4x4
  recipes, removing the unreachable vanilla-3x3 survival dead end.
- Rebuilt the five land-creature silhouettes and Quietskin as centered, mirrored,
  layered geometry with cohesive 128x128 material atlases and an open-face hood.
- Reworked the complete shipped art set under Art Language V3: brighter connected 16x16
  Minecraft-style natural materials, seven distinct wood palettes and reviewed
  higher-detail entity UV atlases. Game UI and the Windows launcher now use the
  separate cold navy/steel/cyan presentation hierarchy.
- Refreshed every implemented creature model around centered layered geometry. Dry-land
  bodies remain readable at Minecraft scale, Buried Remnant is bilateral, and all three
  fish use paired anatomy and segmented tails instead of flat boxes.
- Unified Survival inventory, Gravework and Pitch Kiln around one code-rendered
  panel/slot system. Pitch Kiln now owns a dedicated menu id while preserving normal
  furnace recipes and server behavior.
- Simplified the Windows launcher to centered title/animated Play, Verify/Logs and a
  bottom console. Project client scripts request FML's supported dark early window.

- В разделе «Крафты» теперь можно искать предметы, нажимать на результат, смотреть
  точную сетку нужного рабочего места и прокручивать цепочку создания от природного
  сырья до выбранного предмета. Каталог берётся из рецептов активного сервера.
- Все четыре части Тихошкуры перенесены в рабочие симметричные рецепты Gravework 4x4;
  недоступная в полной конверсии ванильная сетка 3x3 больше не требуется.

- Тихошкура получила четыре отдельные объёмные модели брони на 49 кубов через
  клиентские расширения NeoForge. Слои шкуры, пластины, ремни, обмотки и крепления
  формируют оригинальный scavenger-силуэт, а лицо остаётся открытым.
- Кодекс полностью переработан в свободно перемещаемый и масштабируемый граф четырнадцати
  целей с отдельным прокручиваемым каталогом крафтов. Цели отмечаются только вручную
  после серверной проверки; обычные достижения больше не хранят прогресс мода.
- Меню, выбор мира, настройки и экраны загрузки получили единый более светлый
  forest/wood/bone Gravesown-слой и детерминированные widget-спрайты без Mixin. Deprecated background event
  изолирован в одном клиентском presentation-классе.
- Windows-лаунчер упрощён до названия сверху, анимированной кнопки Play по центру,
  Verify/Logs под ней и компактной консоли снизу; сохранены RU/EN, headless
  `--render-preview`, блокировка второго процесса и существующий ModDev-кэш.
- Полностью переработаны модели и текстуры Пустопаса, Реброскока, Шовоклыка и
  Раночуя: добавлены многослойная геометрия, мелкие объёмные элементы и процедурные
  акценты покоя/движения. Все четыре атласа теперь 128x128 с непрозрачной многотоновой
  страховочной подложкой. Все 143 куба проходят проверку границ, а 101 добавленная
  деталь имеет собственный непересекающийся UV-остров полного размера (24/29/19/29).
  Клиентский smoke-test проверяет точные UUID/network id/type и снимает общий кадр
  всех четырёх.
- Полностью перерисованы иконки и обе UV-текстуры Тихошкуры. Капюшон теперь открытый
  и обрамляет лицо игрока вместо сплошной тёмной маски; front/night снимок проходит.
- Финальный `clienttest.cmd` сохраняет пять проверочных PNG в 1920x1080: Кодекс,
  Гайд, Creative-инвентарь, линейку из пяти сухопутных существ и Тихошкуру с открытым
  лицом; одноразовый integrated world автоматически закрывается после PASS.
- Усиленный client smoke больше не создаёт Кодекс сам: свежий Survival-вход должен
  автоматически выдать ровно один экземпляр и установить persistent attachment;
  открывается и остаётся в инвентаре именно этот stack. Случайные ближайшие мобы
  очищаются, а пять тестовых существ обязаны успешно добавиться и отслеживаться
  клиентом с точным типом.
- Набор обязательных серверных GameTest расширяется вместе с контентом без зафиксированного
  в документации числа: проверяются условия и ручные отметки Центра, фильтр обычных
  достижений, станции, удобрение, одноразовая могила и полный водный вертикальный срез.

### Added

- Added fifteen complete native-fauna vertical slices across all nine biomes: Ash
  Hopper, Gloamwing, Rootback, Bark Marten, Crag Ram, Rift Puma, Mire Toad, Reed Lynx,
  Silt Ray, Ember Fox, Cinder Fowl, Pallid Hart, Mossboar, Amber Jay and Sunhorn. Each
  has server-owned AI, attributes, regional spawning, original model/texture, loot,
  spawn egg, EN/RU localization and automated test coverage.
- Added the recoverable Hushstone Spear. Charged throws deal seven damage and apply
  strong slowing for ten seconds while preserving durability and preventing item
  duplication.
- Added a very rare abandoned surface camp with three variants, biome-matched wood,
  fenced layouts and Reliquary Crate supplies in every reviewed biome.
- Added Mosswake Woods and Amberquiet Grove, bringing the reviewed Overworld to nine
  broad biomes with distinct trees, understory and regional creature populations.
- Added complete Cairnwood, Suturewood, Mosswake and Sunveil wood families. Together
  with Ribroot, Emberbark and Palevine, every species provides stems, foliage, ordinary
  and cut planks, stairs, slabs, fences, gates, doors, trapdoors, propagation shoots and
  species-textured visual falling leaves.
- Added Veined Shale, Splintered Marrowstone, Cairnstone, Cairnwood shrubs and Rift Thorn
  to Marrow Rifts; rooted Suturewood and Mire Frond to Suture Mire; and Mossveil/Amber
  Bloom to the two new sod woodland biomes.
- Added the Sawmill and seven one-to-one species-preserving cut-plank recipes.
- Added Gravesown Glass from smelted Gloam Sand and recoverable Tempered Glass from a
  second smelting step, with distinct strength and loot contracts.
- Added Ember Thicket and Pallid Weald as two of the nine reviewed Overworld biomes,
  with distinct surfaces, vegetation and regional creature populations.
- Added complete Emberbark and Palevine stem/plank/stair/slab/fence/gate/door/trapdoor/
  foliage/shoot families with original generated art, loot, tags and 4x4 recipes.
- Added Gloam Muck and Gloam Sand floors plus evenly bounded aquatic plant placement
  to avoid empty ponds and impenetrable vegetation carpets.
- Added three procedural ruined-shelter variants, a Gravesown-only loot table and a
  dedicated 36-slot Reliquary Crate.
- Added Field Kitchen, Bone Cleaver, Stirring Hook, Mirebean Stew, Charred Marrow Pot,
  Gloam Chowder and Needle Sprat fishing with server-owned consumption/tool wear.
- Added seven new Story goals, Cooking/Exploration guide pages, pinnable creation
  chains and custom Gravesown heart, hunger and XP HUD sprites.

- Added the Ribroot stairs, slab, fence, gate, door and trapdoor family plus the Tallow
  Lantern, original models/textures, loot, EN/RU names and Gravework 4x4 recipes.
- Added Gloam Farmland, hoe conversion, Ashgrain and Mirebean crops, seeds, basic foods
  and Gravebloom Dust crop growth. Only Gloamwater hydrates the custom farmland.
- Added luminous Lumen Kelp and denser mixed aquatic gardens to ponds and Gloam Sea,
  plus original visual-only sun and moon phase textures.
- Gravebloom Dust, Cinder Bloom, Sinew Fern and Marrow Reed add a custom fertilizer,
  Ribroot growth path and three naturally placed plant families.
- Gloam Sea, three fish, four aquatic plants, Gloam Skiff, Gloamline Rod, 4x4
  Gravework, Pitch Kiln and the complete first Hushstone tool family.

- Редкая Могила остатка в Засеянной могиле: feature делает попытку 1/64, блок не
  имеет survival-лута, сохраняет одноразовое состояние `OPENED` и при первом успешном
  использовании выпускает Погребённого остатка. Новое существо 108 тиков выбирается
  из земли под серверной защитой, затем включает AI; добавлены модель, текстура, лут,
  яйцо, EN/RU и GameTests.
- В Шовной топи появились редкие малые озёра (1/14) из полностью собственного
  семейства Сумеречной воды: тип, source/flow, liquid block и ведро. В них растёт
  Нитеводоросль и естественно появляется новый водный хищник Гнилоплав с полным
  entity vertical slice и собственным лутом.
- Для allowlisted экранов добавлена отдельная оригинальная 1672x941 ImageGen-
  иллюстрация заражённого озера; точные UI-спрайты, текстуры жидкости, растений,
  существ и брони остаются детерминированным hard-edged pixel art.
- Пресет `gravesown:after_the_silence` расширен до четырёх биомов: Засеянная
  могила, Рощи рёберника, Костномозговые разломы и Шовная топь. Multi-noise
  использует собственные карты тепла/влажности, а макро- и детальный шум делают
  рельеф заметно выше, разнообразнее и менее плоским.
- Шесть региональных блоков с оригинальными 16x16 текстурами, моделями, лутом и
  инструментами: Rootfelt, Fibrous Loam, Scar Shale, Marrowstone, Suture Silt и
  Dried Ichor. Surface rules дают каждому биому узнаваемый верхний и нижний слой.
- Естественная генерация собственных деревьев Рёберника, Нитяной травы, побегов,
  Бледных луковиц, пятен засохшего ихора и выходов Marrowstone в Разломах без
  ванильных features.
- Три новых оригинальных существа с серверным AI, моделями, текстурами, лутом,
  яйцами призыва и русско-английскими названиями: пугливый Реброскок (16 HP),
  Шовоклык (42 HP, 8 урона и предупреждение перед разгоном) и Раночуй (28 HP,
  5 урона и увеличенное чутьё раненой цели).
- Кодекс выжившего: выдача ровно один раз при первом входе в Survival и реальный
  четырнадцатишаговый маршрут от Рёберника/травы до готовки и хранилища.
- Собственная цепочка из четырнадцати серверных целей в Центре выживания. Встроенный data
  pack верхнего приоритета стандартным фильтром убирает обычные достижения без
  Mixin; финальное условие Тихошкуры требует реально надеть весь комплект.
- Добавлены четыре иконки, объёмная симметричная геометрия и актуальные UV-текстуры
  Тихошкуры с открытым капюшоном. Back/daylight/hurt-flash ракурсы остаются ручной
  приёмкой.
- Фирменный главный экран Minecraft и Windows-лаунчер на Java 21 с кнопками
  «Играть», «Проверить» и открытия логов. App-image создаёт настоящий `.exe` и
  запускает уже кэшированный ModDev-клиент без обхода Microsoft-авторизации и без
  распространения Minecraft.
- Общая большая иллюстрация лаунчера/главного экрана создана через ImageGen; точные
  игровые 16x16/64x32/128x128 ассеты остаются детерминированным пиксель-артом.
- Строгий Full-аудит новой генерации: 3 seed, 867 FULL-чанков, 85 229 568 позиций,
  все семь ожидаемых биомов и ноль нарушений. Набор серверных GameTest вырос до
  51/51, а финальный strict Smoke проверил 25 чанков/2 457 600 позиций/0 нарушений.
- Первоначальный TC4a-пресет мира с одним биомом `gravesown:sown_grave` заложил
  проверенную основу и позже был расширен ещё шестью биомами.
- Первоначальная генерация TC4a низких холмов: Ashen Sod, три слоя Grave Loam, Hushstone,
  Deep Hushstone и сплошной Gravebed без ванильных жидкостей, аквиферов, руд,
  карверов, features и структур.
- Worldgen GameTest, впервые добавленный как пятнадцатый тест TC4a, теперь фиксирует
  семь биомов, multi-noise/noise settings, точные features и собственные
  естественные spawn entries для каждой области.
- `clienttest.cmd`: изолированный quick-play smoke-test того же seed на клиенте и
  integrated server с автоматическим PASS-маркером, проверкой фонового ресурса,
  открытием Кодекса, созданием всех четырёх creature renderer, пятью визуальными
  снимками, сохранением и нормальным завершением.
- `worldtest.cmd` и `verify-all.cmd` теперь по умолчанию проверяют собственный
  пресет в строгом режиме; baseline доступен только явным флагом.
- Полностью местная стартовая 2x2-цепочка: щепа Рёберника, Нитяная обвязка,
  осколок Камня тишины, Грубый ручной кайл и Обвязанный нож.
- Семь рецептов без ванильных ингредиентов, обратимая сборка Hushstone, отдельные
  tool/material-теги и 5 оригинальных 16x16 текстур предметов.
- Три GameTest проверяют полный крафтовый путь, ремонт, 48/96 прочности и порог:
  Handpick добывает Hushstone, но не Deep Hushstone; всего теперь 14 тестов.
- Семейство Рёберника и стартовая растительность: ствол, доски, Завесная листва,
  Нитяная трава, побег Рёберника и Бледная луковица со слабым светом.
- Для TC3b добавлены 7 оригинальных 16x16 текстур, cutout-модели, 6 loot table,
  стандартные wood/leaf/sapling/flower-теги и отдельные теги Gravesown.
- Три новых GameTest проверяют стабильные ID, ось ствола, коллизии, свет, почву,
  block/item-теги и точный дроп; полный набор теперь содержит 11 тестов.
- Первые пять блоков собственного мира: пеплокожий дёрн, могильный суглинок,
  камень тишины, глубинный камень тишины и неразрушимое могильное ложе.
- Для них добавлены block items, русские/английские названия, blockstate/model JSON,
  четыре loot table, mining/tier tags и шесть оригинальных бесшовных 16x16 текстур.
- Два GameTest проверяют стабильные ID, размещение, дроп, инструменты и отсутствие
  лута у нижнего неразрушимого слоя.
- `worldtest.cmd`: изолированный headless-аудит настоящих FULL-чанков по всей
  высоте мира с проверкой биомов, блоков, жидкостей и block entities.
- Строгий контракт полного преобразования и baseline-режим для разработки до
  появления собственного пресета; JSON/TXT-отчёты содержат координаты нарушений.
- `verify-all.cmd`: одна кнопка для doctor, GameTests, clean build и world audit.
- Smoke-профиль 5x5 чанков и Full-профиль 3 seed × 17x17 чанков; тестовый мир
  изолирован sentinel-файлом и никогда не затрагивает обычные сохранения.
- Живой план полного преобразования мира: семь биомов Gravesown, собственная
  палитра блоков, ресурсы, этапы TC1–TC8 и строгий контракт проверки.
- Пять оригинальных ресурсов Пустопаса вместо кожи и гнилой плоти: рваная шкура,
  натянутая жила, могильный жир, заражённое мясо и редкая полая челюсть.
- Полный комплект Тихошкуры с рецептами, ремонтом, тегами брони, моделями предметов,
  девятью 16x16 иконками и двумя 64x32 текстурами надетой брони.
- Серверная механика «Мёртвый запах»: каждая часть Тихошкуры уменьшает дневную
  дальность обнаружения раненого игрока на 12,5%, полный комплект — на 50%.
- Пять детерминированных GameTest для сущности, лута, экипировки, дальности запаха
  и завершения уже начатой дневной погони после маскировки или лечения.

- Официальный NeoForge 1.21.1 ModDevGradle scaffold.
- Переносимый one-click workflow для setup, automated tests, play, build, server и diagnostics.
- Постоянная память проекта через AGENTS.md и docs/.
- Первичный дизайн Gravesown: After the Silence.
- Первый оригинальный вид — Пустопас: регистрация, характеристики, базовый AI,
  модель, рендерер, яйцо призыва, русский/английский перевод и временный лут.
- Серверная конфигурация подавления и замены естественных ванильных спавнов в Overworld.
- Оригинальные пиксельные alpha-ассеты: 128x64 текстура существа и 64x64 иконка мода.
- NeoForge GameTest, проверяющий регистрацию, создание и серверные тики Пустопаса.
