# Design bible

## Title

Gravesown: After the Silence

Russian working title: Gravesown: После Тишины

Tagline: Связь оборвалась. Планета продолжает жить.

## Canonical premise

The protagonist reaches an uncharted alien planet in a small interstellar craft and
survives a catastrophic landing. The crash destroys the usable ship systems and cuts
all communication: this personal radio silence is the meaning of *After the Silence*.
The planet is not ruined Earth, a zombie world or a parasite outbreak. It is a living,
low-technology biosphere with its own ordinary prey, herd animals, birds, scavengers
and regional apex predators. Hostility comes from hunger, territory, nests and time of
day rather than universal corruption.

No working industrial civilization is available to the player. Early survival is
primitive and local; later progression is about rebuilding capability from native
materials: hand processing, mechanical work, metallurgy, power generation, transport
and automation. This may occupy the same broad design space as classic industrial
Minecraft mods, but every machine, recipe graph, energy rule, name, model, texture and
sound must be original Gravesown work.

Existing bone/flesh/grave vocabulary is interpreted as alien material anatomy and the
castaway's field terminology, not proof that every creature is undead. Older sections
that describe global infection, cleansing or recreated Earth animals are retained only
as historical notes and are not authoritative for new content.

## Living-planet design pillars

1. Each biome supports a recognizable food web, with one to three endemic species and
   a mix of passive, neutral and predatory behavior.
2. Creatures look like plausible native animals: bilateral silhouettes, locomotion,
   feeding/resting/fleeing behavior and restrained regional adaptations.
3. Exploration provides biological and mineral knowledge needed to rebuild technology;
   machines do not appear as unexplained loot from a vanished high-tech civilization.
4. Industrial progression is layered and server-authoritative: manual work leads to
   mechanical processing, then power networks and automation without invalidating the
   ecosystem or early stations.
5. The cold navy palette remains presentation-only. The planet keeps natural local
   daylight colors so biomes and animals remain readable during long play sessions.

## Industrial progression direction

Industrial play is a long-term extension of survival, not a separate creative-menu
machine pack. The planned ladder is:

1. **Fieldcraft** — hand tools, Gravework, Kiln, Kitchen, Sawmill and storage establish
   food, shelter and repeatable material preparation.
2. **Mechanical work** — player-, water- or wind-driven shafts power cutting, crushing,
   pumping and lifting. Recipes expose work rate and mechanical load rather than hiding
   everything in one generic furnace.
3. **Metallurgy** — native deposits are washed, crushed, smelted and alloyed through
   multiple useful by-products; metal is never unlocked by one decorative ore block.
4. **Power** — locally generated energy is stored and routed through original machines
   with explicit capacity, loss and safety rules. Names and units remain Gravesown-owned.
5. **Automation and transport** — item routing, controlled farming, processing lines and
   regional transport reduce repetition after the player has mastered each manual loop.

Machines simulate only loaded, relevant networks; no global per-tick block scan is
allowed. Dedicated-server state, ownership and save/reload behavior are part of every
machine's definition of done.

## Legacy pitch (superseded)

После Тишины обычная фауна исчезает. Земля создаёт новую некротическую
экосистему из костей, останков и почвы. Новые виды сохраняют отдельные
инстинкты прежней природы — пастись, мигрировать, защищать стаю и охотиться —
но не являются простыми зомби-версиями ванильных животных.

Игрок изучает поведение существ, добывает замену привычным ресурсам и постепенно
очищает заражённые территории.

## Design pillars

1. Новая экосистема вместо перекрашенных ванильных мобов.
2. Ванильная читаемость Minecraft при мрачном и тревожном визуале.
3. Поведение важнее голых характеристик: кровь, шум, стаи, миграции и время суток.
4. Сервер управляет миром; одиночная игра и dedicated server используют одни правила.
5. Мир остаётся проходимым: каждому обязательному ванильному ресурсу со временем
   появляется оригинальная замена.

## Meaning of “vanilla mobs are gone”

- Игроки и не-мобовые сущности не затрагиваются.
- Обычный естественный спавн ванильных животных и монстров заменяется Gravesown.
- Уже существующие обычные мобы могут удаляться серверной настройкой.
- Боссы временно сохраняются в MVP, пока нет безопасных замен для прогрессии.
- Незер, Энд и водная среда заменяются по отдельным этапам, а не сырым глобальным
  удалением в первой сборке.
- Команды разработчика и отдельный конфиг должны позволять изолированно тестировать
  ванильных мобов при отладке.

## The Silence

Тишина — событие, после которого нормальные животные и разумные существа исчезли.
Причина в ранней игре неизвестна. Заражение не является паразитической расой:
сама нарушенная земля воспроизводит ошибочную природу вокруг Узлов Тишины.

## First creature: Hollow Grazer / Пустопас

Новый четвероногий вид, собранный из несовместимых костей и плотной почвы.

- Днём бродит небольшими стадами.
- Нейтрален, пока не ранен или не почувствует тяжело раненую добычу.
- После получения урона раскрывает нижнюю челюсть и атакует.
- Ночью быстрее переходит в агрессию.
- Даёт заражённое мясо, сухожилия и обрывки шкуры.

Первая альфа может начать с ограниченного поведения, но публично готовым существо
считается только по entity definition of done из AGENTS.md.

## Ecosystem roster

- Sown Grave: Hollow Grazer / Пустопас, Ash Hopper / Пеплопрыг и
  Gloamwing / Сумракрыл. Редкая Могила остатка одноразово выпускает Buried
  Remnant / Погребённого остатка после 108-тикового подъёма из земли.
- Ribroot Groves: Ribspring / Реброскок, нейтральный Rootback / Корнеспин и
  небольшой охотник Bark Marten / Коровая куница.
- Marrow Rifts: территориальный Stitchtusk / Шовоклык, нейтральный Crag Ram /
  Утёсорог и редкий крупный Rift Puma / Разломная пума.
- Suture Mire: слепой Woundscent / Раночуй, водный охотник Rotfin / Гнилоплав,
  мирный Mire Toad / Топяная жаба и редкий Reed Lynx / Камышовая рысь.
- Gloam Sea: мирные Veilfin / Вуалеплав, Rootskimmer / Корнескат и Silt Ray /
  Иловый скат вместе с редким Rotfin.
- Ember Thicket: Hollow Grazer, небольшой Ember Fox / Углелис и стайный
  Cinder Fowl / Шлакоклюв.
- Pallid Weald: Ribspring и Pallid Hart / Бледнорог.
- Mosswake Woods: Ribspring и нейтральный Mossboar / Мохокабан.
- Amberquiet Grove: Hollow Grazer, Amber Jay / Янтарная сойка и Sunhorn /
  Солнцерог.
- Bellbeak / Колоколклюв, Hushstep / Тихоступ и Choirmaw / Хореглот остаются
  отдельными будущими поведенческими концептами, а не обещанием текущей сборки.

Names are working names and must be checked for uniqueness before public release.

## Creature visual language

The canonical expansion treats creatures as native wildlife. Fur, hide, feathers,
scales, shell, membrane and biome camouflage are all valid primary materials; exposed
bone or bruised tissue is reserved for the older species whose anatomy calls for it.
The roster must not collapse into fifteen recolored horrors or hostile box models.

Creatures must remain readable at Minecraft distance without looking like single
undifferentiated boxes. Their silhouettes combine a strong primary body mass with
small original secondary forms such as exposed supports, layered hide, sensory
growths or restrained asymmetry. Texture value groups must explain those forms with
bone, bruised tissue, bound sinew and sparse sickly accents instead of flat color
panels or random noise.

The current 23 implemented creatures use original model geometry and server-owned
behavior. The established bespoke creatures keep their individual telegraphs, while
14 new land animals share bounded prey, neutral, small-predator, large-predator or
bird goal profiles without sharing registry identity, art, names or loot. Silt Ray has
its own bottom-swimming aquatic profile. Only Rift Puma and Reed Lynx treat players as
routine prey; other predators focus on native animals and all predator spawn weights
remain below their local prey/neutral populations.

Dry-land silhouettes use centered mirrored layered geometry and restrained idle,
locomotion and behavior accents; Buried Remnant adds a synced emergence pose. Rotfin,
Veilfin, Rootskimmer and Silt Ray follow the same bilateral construction rule through
layered bodies, paired fins and segmented tails while occupying distinct depth niches.
Animations support gameplay telegraphs; they must not hide server timing or imply a
hit before the server resolves it. Deterministic hard-edged texture sheets remain the
source of truth, with model/atlas bounds and shipped-resource ownership enforced by
the art gate.

## World progression

The Overworld target is nine visually distinct Gravesown biomes rather than one
uniform field: Sown Grave, Mosswake Woods, Amberquiet Grove, Ribroot Groves,
Marrow Rifts, Suture Mire, Gloam Sea, Ember Thicket and Pallid Weald. They share
one apocalypse and progression economy but must be recognizable from terrain
silhouette alone before fog, vegetation or mobs are considered.
Warmth and wetness target a macro scale about twice as broad as the first seven-biome
prototype: half the width of the over-broad four-times build, but still large enough
for each region to support exploration instead of appearing as a short stripe.
Emberbark and Palevine shoots are player propagation items only: their trees generate
naturally, but loose shoots never decorate either biome. Mosswake Woods and
Amberquiet Grove deliberately reuse Ashen Sod/Grave Loam as the readable turf
foundation while their Mosswake and Sunveil crowns, local plants and palettes create
the regional identity.

The first-hour route begins with the one-time Survivor's Codex. The server grants it
on the player's first Survival login or first later transition into Survival, and a
persistent flag prevents duplicates. Right-click asks the server for two authoritative
fourteen-bit masks: conditions met and quests claimed. A full-screen Survivor Codex exposes
four modes, `Story`, `Crafts`, `Chain` and `Guide`; its quest graph can be panned and zoomed. `Crafts`
reads the recipe catalog synchronized by the active server, supports localized-name
and registry-id search, filters it into All/Tools/Building/Food/Equipment/Materials,
and lets the player click an output to inspect its exact
2x2, 3x3, Gravework 4x4 or Pitch Kiln layout. `Chain` expands craftable prerequisites
as a branching dependency graph back to independent natural-acquisition roots; it
auto-fits on open, supports pan/zoom, and clicking a craftable node jumps to its exact
`Crafts` entry while resetting its filter to All. A pin can preserve a difficult Chain
target while intermediate nodes are inspected. `Guide` provides scrollable Basics,
Farming, Food, Water, Building, Cooking and Exploration topics without becoming a
second recipe authority.
Pressing `R` over an item in a container asks the server for current
Codex state and opens that item's graph directly. A ready quest changes state only after the player
selects it and presses Claim. The server validates the predecessor and condition again,
persists the result, then drives the completion sound and custom slide-out notice.
No Gravesown quest is an ordinary advancement. The built-in data filter also removes
vanilla advancement and recipe data without a Mixin, so neither the old tutorial nor
vanilla crafting can compete with the total-conversion route.

Rare landmarks should reward attention without becoming renewable command-like loot.
The first example is the 1/64 Remnant Grave in Sown Grave: its saved opened state can
release exactly one Buried Remnant, whose emergence is a warning rather than an
instant hit. The feature itself is not a survival loot container and cannot reset.

Suture Mire's ponds and the large Gloam Sea use Gloamwater rather than vanilla water.
Threadkelp, Veilweed, Drowned Roots and Bladderpod support Rotfin, Veilfin and
Rootskimmer while preserving the strict no-vanilla-fluid world contract. Fish select
submerged targets with surrounding water and depth preferences instead of receiving
constant upward buoyancy, preventing surface clustering and tight target-orbits.
Gloamwater remains a separate registry family but deliberately follows vanilla-water
flow distances, source conversion, tick pacing and translucent rendering so swimming,
shorelines and buckets behave predictably. It reuses the vanilla 1.21.1 still/flow
animation sprites with a Gravesown tint and fog. In every lake, pond and ocean, any
reviewed natural regional block directly below Gloamwater is converted into deterministic
organic Gloam Sand and Gloam Muck shelves, so the aquatic bed cannot expose an unrelated
biome surface. The bed boundary comes from coordinate-warped multi-scale value noise,
not a visible chunk or 4x4 checker. Every water-bearing chunk then evaluates the same
chunk-aligned 4x4
ecology grid. Each cell probes all sixteen columns in a deterministic shuffled order
and places at most one eligible Threadkelp, Veilweed, Drowned Roots, Bladderpod or softly
luminous Lumen Kelp candidate. This keeps irregular shores and large lakes uniformly
sampled without carpet density, random empty climate-border chunks or any per-tick
world scan. Low-weight Veilfin and Rootskimmer groups are eligible in Gloamwater across
every region; the Gloam Sea keeps larger schools and the Mire/Sea retain their dangerous
Rotfin niche.

Early farming stays inside the conversion economy. A Gravesown hoe turns Ashen Sod or
Grave Loam into Gloam Farmland; only nearby Gloamwater maintains moisture. Ashgrain and
Mirebean provide the first renewable crops, while Gravebloom Dust accelerates their
server-owned growth. Ribroot stairs, slabs, fences, gates, doors and trapdoors plus a
Tallow Lantern form the first complete shelter vocabulary.

Seven wood species now extend the building palette without fragmenting progression:
Ribroot, Emberbark, Palevine, Cairnwood, Suturewood, Mosswake and Sunveil. Every tree
family ships as stem, planks, cut planks, stairs, slab, fence, gate, door, trapdoor,
foliage and shoot. Functional recipes may combine any reviewed Gravesown planks; a
decorative colored family always keeps its own wood species. Every foliage block owns
the shared client-only falling-leaf behavior, so future tree families inherit a
species-textured canopy effect without a server tick scan.

The Sawmill is the first dedicated finishing station. Its server-owned one-input
recipe converts one ordinary plank into one cut plank of the same species; it never
changes wood color and never creates extra material. Gloam Sand enters a separate
two-stage glass loop: ordinary Gravesown Glass is smelted first, then reheated into
Tempered Glass. Ordinary glass requires Silk Touch to recover; tempered glass is
three times harder and survives ordinary harvesting as itself.

Sticky regional surfaces may slow horizontal travel but may not delete the player's
basic traversal vocabulary. When a player jumps from a Gravesown block whose jump
factor is below one, common-side movement restores the normal vertical jump impulse
while leaving the block's horizontal speed factor untouched. Integrated-server and
dedicated-server movement therefore follow the same one-block-jump contract.

Rare procedural ruined shelters create the first exploration cache. Their 36-slot
Reliquary Crate uses Gravesown-only loot and a dedicated menu. The Field Kitchen turns
three ordered ingredients plus persistent Cleaver and Hook utensils into hot meals;
the server consumes ingredients, returns the empty Gloamwater bucket and applies tool
wear. Fishing currently yields the small Needle Sprat rather than vanilla treasure or
fish, keeping the food loop inside the conversion economy.

Позднее Узлы Тишины создают локальные заражённые области. Их состояние влияет на
спавн, туман, растения, звуки и агрессивность. Игрок очищает отдельные территории,
а не просто ждёт единого глобального таймера эволюции.

## First-hour tools

Стартовая цепочка не использует ванильные палки, кремень или верстак. Игрок
перерабатывает Рёберник в щепы, Нитяную траву — в обвязку и в сетке 2x2 создаёт
Грубый ручной кайл. Он добывает обычный Камень тишины, но не глубинный слой.
Камень тишины разбирается на обратимо собираемые осколки; осколок, щепа и
обвязка дают Обвязанный нож. Это постоянный первый порог прогрессии.

## Quietskin silhouette

Quietskin is scavenged field equipment, not a sealed monster costume. Its hood frames
and exposes the player's face while four slot-specific volumetric models build layered
hide plates, bone fasteners, sinew seams, straps, wraps and mirrored raised fittings
across the coat and boots. The open face preserves player identity and avoids a
featureless black mask; concealment remains a server-side Dead Scent mechanic rather
than a visual promise of complete enclosure.

## Non-goals for the first playable build

- Полная замена всех измерений.
- Финальные боссы.
- Сложные машины и GUI.
- Фотореалистичный gore.
- Копирование системы стадий, существ или эстетики конкретного существующего мода.
