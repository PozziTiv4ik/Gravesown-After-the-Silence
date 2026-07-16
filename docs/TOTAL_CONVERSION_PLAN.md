# Total conversion plan

Status: Living document
Accepted direction: 2026-07-12

This file is the long-term implementation plan for the Gravesown total
conversion. Future agents may refine, reorder or remove entries when verified
implementation teaches us something new. Stable released registry ids must not
be renamed casually.

## Product contract

- The protagonist is a crash survivor on a wild alien planet; Gravesown fauna is a
  native ecosystem rather than infected or zombified Earth life.
- The planet provides no usable technology. Progression starts with hand survival and
  grows into original Gravesown mechanical processing, metallurgy, power and automation.
- The playable world uses the dedicated preset `gravesown:after_the_silence`.
- Its Overworld uses exactly nine Gravesown biomes: `sown_grave`,
  `mosswake_woods`, `amberquiet_grove`, `ribroot_groves`, `marrow_rifts`,
  `suture_mire`, `gloam_sea`, `ember_thicket` and `pallid_weald`.
- Newly generated playable Overworld chunks contain no visible or obtainable
  vanilla blocks, fluids, ores, vegetation or structures.
- Vanilla registries remain intact for engine stability and development tools.
- The initial strict technical allowlist is only `minecraft:air` and
  `minecraft:cave_air`; all non-empty fluids in generated chunks must be
  Gravesown-owned. Any temporary exception must be named and tracked.
- Existing vanilla/M1 worlds are never converted in place. Worldgen development
  is new-world-only until the schema is declared stable.
- Nether and End survival access stays unavailable until each dimension receives
  an explicit Gravesown replacement milestone.

## The nine-biome Overworld

TC4a proved a minimal one-biome generator. The user review found it too flat and
empty, so the shipping target is now nine registry biomes with strong silhouettes:

1. **The Sown Grave / Засеянная могила** — open Ashskin flats, low grave mounds
   and Hollow Grazer herds; this keeps the existing stable biome id.
2. **Ribroot Groves / Рощи рёберника** — dense rib-like trunks, membrane crowns,
   root arches and a skittish native prey species.
3. **Marrow Rifts / Костномозговые разломы** — high ridges, ravines, exposed
   marrow rock and a heavy territorial burrower.
4. **Suture Mire / Шовная топь** — low dark silt basins, dried ichor channels,
   pallid lights and a blind scent predator.

5. **Gloam Sea** — a large Gloamwater ocean with Abyssal Silt/Brinebone floor,
   layered aquatic vegetation and three fish depth niches.
6. **Ember Thicket** — warm dry Dried Ichor/Scar Shale hills, Emberbark stands,
   cinder flora and Grazer/Woundscent pressure.
7. **Pallid Weald** — cool Fibrous Loam/Marrowstone woodland, Palevine crowns,
   pale bulbs and fleeing Ribspring herds.
8. **Mosswake Woods** — Ashen Sod woodland with broad Mosswake crowns, Mossveil
   understory and Ribspring movement through readable green-brown turf.
9. **Amberquiet Grove** — warmer Ashen Sod woodland with tall Sunveil crowns,
   Amber Bloom accents and sparse Hollow Grazer groups.

Climate parameters select the registry biome while custom density functions,
surface rules and Gravesown-only placed features create the terrain. No biome may
reuse a vanilla surface provider, tree, ore, structure or fluid as final content.
The reviewed climate functions now target one-half of the prototype X/Z frequency,
making public regions roughly twice the prototype width and half the previous
over-broad four-times width. Emberbark and Palevine shoots remain
player-planted propagation items and are deliberately absent from natural decoration.
Mosswake Woods and Amberquiet Grove reuse the Ashen Sod/Grave Loam surface contract;
their identity comes from trees, understory and local color rather than a gratuitous
new foundation block.

Later internal subzones such as Hush Hollows and Cairnspines may vary terrain and
features without increasing the reviewed public biome count.

## World foundation palette

Priority-zero blocks:

- `ashen_sod` — surface;
- `grave_loam` — soil;
- `hushstone` — primary rock;
- `deep_hushstone` — deep rock;
- `fractured_hushstone` — early building and processing material;
- `clotted_marl` — noisy surface patches;
- `gravebed` — unbreakable bottom replacement;
- `rootfelt`, `fibrous_loam` — Ribroot Groves surface and filler;
- `scar_shale`, `marrowstone` — Marrow Rifts surface and filler;
- `suture_silt`, `dried_ichor` — Suture Mire surface and slow channel material;
- `ribroot_stem`, `ribroot_planks`, `veil_foliage` — first wood family;
- `emberbark_*`, `palevine_*`, `cairnwood_*`, `suturewood_*`, `mosswake_*`,
  `sunveil_*` — six additional complete regional wood/decor families;
- `*_cut_planks` — one Sawmill-finished 1:1 plank variant for every one of the seven
  wood species;
- `veined_shale`, `splintered_marrowstone`, `cairnstone` — Rifts geology accents;
- `rift_thorn`, `mire_frond`, `mossveil`, `amber_bloom` — regional understory;
- `gloam_muck`, `gloam_sand` — aquatic floor and shore materials;
- `threadgrass`, `ribroot_shoot`, `pallid_bulb` — bootstrap vegetation.

Bootstrap tool materials and tools:

- `ribroot_splint` — local handle material from Ribroot Planks;
- `thread_binding` — binding made only from Threadgrass;
- `hushstone_shard` — reversible Hushstone cutting edge;
- `crude_handpick` — 48-use first pick that harvests Hushstone but not Deep Hushstone;
- `bound_knife` — 96-use first cutting/combat tool.

Early utility blocks:

- `gravework_bench` — crafting station;
- `pitch_kiln` — cooking and smelting;
- `hide_rack` — hide curing;
- `tallow_lamp` — early light;
- `sinew_crate` — storage.
- `sawmill` — server-authoritative one-input wood finishing station;
- `gravesown_glass`, `tempered_glass` — two-stage Gloam Sand glass loop.

Implemented landmark/aquatic content:

- `remnant_grave` — no-loot, one-shot interactive Sown Grave marker;
- `gloamwater`, `flowing_gloamwater` and `gloamwater_bucket` — custom fluid family;
- `threadkelp` — Gloamwater vegetation;
- Buried Remnant and Rotfin — complete grave/aquatic entity vertical slices.

No vanilla default feature may be copied without auditing every block provider.
Aquifers and vanilla ore veins remain disabled. Rare Suture Mire ponds are now a
reviewed placed feature using the custom Gloamwater source/flow/block/bucket family,
with Threadkelp and Rotfin forming the first aquatic ecology slice.

## Resource progression

Each deposit must unlock a distinct use rather than being a cosmetic ore:

1. `grave_pitch_ore` / Grave Pitch / Могильная смола — fuel and light.
2. `ferric_marrow_ore` / Ferric Marrow / Железистый мозг — first metal tier.
3. `gloam_salt_deposit` / Gloam Salt / Сумеречная соль — food preservation and hides.
4. `mute_crystal_ore` / Mute Crystal / Немой кристалл — first progression resource.

Later candidates: Veinmetal, Nightglass and dimension-specific materials.

## Hollow Grazer economy

The temporary leather and rotten-flesh loot is replaced by:

- `ragged_grazer_hide` — main armor material;
- `taut_sinew` — binding and thread;
- `grave_tallow` — fuel and lamps;
- `tainted_grazer_meat` — unsafe until processed;
- `hollow_jaw` — rare progression trophy.

Gloam Salt and the Hide Rack later turn raw hide into
`cured_grazer_hide`. Before worldgen exists, the alpha recipe may use the raw
hide and sinew directly so the vertical slice remains testable.

## Quietskin armor

The first set is **Quietskin / Тихошкура**:

- `quietskin_hood`;
- `quietskin_coat`;
- `quietskin_legwraps`;
- `quietskin_boots`.

Protection sits between leather and chain with no toughness. Its identity is
the **Dead Scent / Мёртвый запах** mechanic:

- each equipped piece reduces blood-scent detection range;
- a full set can suppress new daytime blood-scent targeting after the player
  crouches without moving for three seconds;
- taking or dealing damage breaks concealment for ten seconds;
- night aggression, retaliation and creatures without scent ignore it.

The first implementation may ship the deterministic per-piece range reduction
before adding the timed full-set concealment state.

## First-hour survival loop

1. Gather Ribroot and Threadgrass by hand.
2. Convert planks into Splints, grass into Thread Binding and craft a Crude Handpick
   in the 2x2 grid.
3. Mine Hushstone, split it into reversible Shards and craft a Bound Knife.
4. Build the Gravework Bench, Pitch Kiln, shelter and Tallow Lamp.
5. Hunt Hollow Grazers during daytime and process food/hides.
6. Mine Ferric Marrow and craft the first metal tool.
7. Assemble Quietskin and enter a Marrow Rift.
8. Recover the first Mute Crystal and craft the future Hush Needle.

The survival gate is strict: a new player with an empty inventory must obtain
food, light, shelter and a mining tool without commands or vanilla resources.

## Implementation sequence

### TC1 — Hollow Grazer economy

Implementation status: code/data complete and the original economy tests pass.
Quietskin has four slot-specific volumetric client models with 49 total cubes,
layered scavenged plates/fittings and an open-face hood. Default/slim fit plus
front/back/daylight/hurt-flash appearance and no-command acquisition remain manual
acceptance checks.

- custom drops and loot table;
- Quietskin armor, recipes, textures and Dead Scent integration;
- GameTests for ids, equipment, armor values and scent reduction.

### TC2 — World audit harness

Implementation status: complete. Smoke baseline and strict negative control are
verified against 25 real FULL chunks; three-seed Full mode is available for later
worldgen milestones.

- `worldtest.cmd` for true chunk generation and block/biome/fluid audit;
- `verify-all.cmd` for doctor, GameTests, world audit and build;
- reports under `build/reports/gravesown/`.

### TC3 — World foundation content

Implementation status: foundation palette, bootstrap flora and first-tool slices are complete.
The first five terrain blocks plus `ribroot_stem`, `ribroot_planks`, `veil_foliage`,
`threadgrass`, `ribroot_shoot` and `pallid_bulb` have stable ids, block items,
original textures/models, loot/tool/ecology tags and automated tests. The local
Splint/Binding/Shard chain crafts a Crude Handpick and Bound Knife entirely in 2x2;
early utility blocks remain.

- priority-zero block palette and pixel textures;
- mining/replaceable/tool tags, loot tables and building variants;
- bootstrap items and utility blocks.

### TC4 — World preset and biome expansion

Implementation status: TC4a remains the historical clean foundation, and TC4b is
implemented. `after_the_silence` now uses a nine-entry multi-noise source and
spatial warmth/wetness functions. A safe Y28–112 gradient plus macro/detail density
creates broad hills and local variation. Biome surface rules select the reviewed
regional foundations, while original tree, plant, stone, dried-ichor, rare Remnant
Grave and Gloamwater placed features provide Gravesown-only natural content. Aquifers,
vanilla ore veins, carvers and vanilla structures remain absent. Dedicated strict
Smoke/Full evidence exists; the current slice reran strict Smoke without a client.

- [x] data-driven dynamic registries;
- [x] nine-biome multi-noise source, custom noise settings and surface rules;
- [x] empty reviewed carver sets and safe solid spawn terrain;
- [x] no vanilla structures, aquifers or ore veins;
- [x] stronger macro/detail terrain variation;
- [x] broad 0.175-scale climate regions plus player-only Emberbark/Palevine shoots;
- [x] custom Ribroot, bootstrap-flora, dried-ichor, rare grave and rare pond placed
  features;
- [x] biome-specific surface blocks and one complete creature per added biome;
- [x] custom Gloamwater fluid family, Threadkelp and Rotfin aquatic slice.

### TC4V — Visual identity, onboarding and Windows client

- [x] replace the alpha Quietskin shell with readable original pixel art and four
  slot-specific volumetric armor models (49 cubes) using layered scavenged plates and
  an open-face hood; automated resource/model checks pass, while default/slim,
  back/daylight/hurt-flash appearance remains manual;
- [x] rebuild Hollow Grazer, Ribspring, Stitchtusk and Woundscent with richer layered
  model geometry, procedural motion accents and fully redrawn deterministic texture
  sheets; all four atlases are 128x128 with opaque multi-tone safety underlays, all
  143 cubes pass bounds and all 101 added detail cubes own non-overlapping full-
  footprint UV islands; exact client tracking and the lineup capture pass;
- [x] branded title-screen and selected vanilla menu/options/loading presentation;
  deterministic widget overrides preserve the underlying screen behavior, while one
  isolated client theme subscriber owns the pinned deprecated background event;
- [x] a Windows launcher app-image with a real `.exe`, centered animated Play button,
  Verify/Logs controls, compact bottom console, RU/EN locale path, offscreen preview and cached project client
  startup; it must not bypass Microsoft authentication or redistribute Minecraft
  binaries;
- [x] full-screen pannable/zoomable Survival Hub with `Story`, categorized `Crafts`,
  branching `Chain` and scrollable `Guide`, a fourteen-step
  server-owned condition/claim tree, manual Claim validation, sound and custom
  completion notice; grant its Codex item on first Survival login or first later
  Creative-to-Survival transition, persist all state and synchronize through protocol 3;
- [x] make `Crafts` an interactive server-synchronized recipe browser with EN/RU and
  registry-id search, six filters and exact station grids; make `Chain` a separate auto-fitted,
  pannable/zoomable branching dependency graph whose craftable nodes jump back to
  `Crafts`, with `R` over a container item opening its graph through a server request;
- [x] vanilla advancements and recipes are filtered through an always-on high-priority
  built-in server data pack; no fragile Mixin is used;
- [x] Gloamwater remains a distinct custom fluid while matching vanilla-water source,
  flow-distance and tick semantics and reusing vanilla water animation sprites with
  Gravesown tint, fog, floor materials and ecology;
- [x] chunk-aligned pond/sea growth covers every 4x4 bed cell with one candidate from
  five custom plant families including softly luminous Lumen Kelp, while no vanilla
  fluid or aquatic vegetation enters the world;
- [x] low-density peaceful fish spawn tables cover Gloamwater in all nine climates,
  with larger schools and predators remaining regional ecology differences;
- [x] add the first complete field/shelter slice: custom-fluid-only farmland, two crops,
  food, six Ribroot construction shapes and a Tallow Lantern;
- [x] add Ember Thicket and Pallid Weald with complete Emberbark/Palevine families;
- [x] add procedural ruined shelters, a 36-slot Reliquary Crate, Field Kitchen,
  persistent utensils, three hot meals and Gravesown-only fishing loot;
- [x] extend the server-owned Story to 14 goals and Guide to seven topics; allow a
  creation chain to be pinned while inspecting intermediate crafts;
- [x] force new worlds to the After the Silence preset, structures enabled and bonus
  chest disabled through public NeoForge/vanilla screen state rather than a Mixin;

Large branded illustration work may use ImageGen and must still receive visual
review. Exact 16x16 items/blocks, 64x32 armor UVs and 128x128 entity sheets remain
deterministic hard-edged pixel art generated by project scripts.

### TC4E — Woodland, material and Art Language V3 expansion

Implementation status: code/data and deterministic asset ownership exist; final
command evidence and FHD visual acceptance are recorded only in `docs/STATUS.md`.

- [x] add Mosswake Woods and Amberquiet Grove on Ashen Sod/Grave Loam, keeping the
  exact nine-biome allowlist and a 0.175 climate scale: half the linear width of the
  rejected over-broad build while still broader than the original prototype;
- [x] add Cairnwood shrubs and three Rifts stone accents, four-root Suturewood mire
  trees/puddles, broad Mosswake trees and tall Sunveil crowns with region-specific
  understory;
- [x] complete seven public wood families, including species-colored foliage,
  construction shapes, propagation, falling leaves and Sawmill-only cut planks;
- [x] add the server-authoritative Sawmill and exact one-plank-to-one-cut-plank recipes;
- [x] add Gloam Sand -> Glass -> Tempered Glass smelting, with Silk Touch recovery for
  fragile glass and ordinary self-drop for the three-times-harder tempered block;
- [x] replace the visible 4x4 bed mosaic with deterministic coordinate-warped
  multi-scale Sand/Muck shelves while preserving the bounded 4x4 ecology grid;
- [x] keep sticky horizontal slowdown while restoring a normal player jump impulse;
- [x] make Art Language V3 the final raster owner for terrain, all seven wood families,
  tools/stations, HUD/GUI, armor and creature atlases; keep natural local colors on
  gameplay art, use the cold navy/steel/cyan palette only for presentation, and keep
  large mood backgrounds as the only non-UV ImageGen-eligible shipping art.

### TC5 — Deposits and subzones

- four functional deposits and tool progression;
- Flats, Groves and Rifts;
- reproducible density and height validation.

### TC6 — Survival completeness

- processing stations, food, lighting, storage and building families;
- one-hour no-command playthrough;
- dedicated-server save/reload.

### TC7 — Native ecosystem and industrial foundation

- [x] add at least fifteen endemic animals so every public biome has one to three
  local prey, neutral, avian/small-fauna or predator species;
- [ ] deepen predator/prey interactions, feeding/resting loops, spawn costs and
  long-session population pacing;
- [x] first aquatic pocket: Gloamwater, Threadkelp and Rotfin;
- [x] first rare interactive landmark: one-shot Remnant Grave and Buried Remnant;
- [x] first biome-aware rare surface camp with three bounded variants and local wood;
- [ ] remaining subzones, richer camps/ruins, particles and original sounds;
- original manual-to-mechanical processing graph, followed by metallurgy, power
  generation and automation; no copied IndustrialCraft machines or energy rules.

### TC8 — Other dimensions

- independent Gravesown Nether replacement;
- independent Gravesown End replacement;
- full progression and bosses without vanilla-world access.

## Verification contract

- `test.cmd` covers deterministic server/content mechanics, including the Codex
  grant, Survival transition, condition/claim payloads, one-shot grave/emergence,
  fertilizer/tree/crop growth, custom-only hydration, early stations, shelter recipes,
  aquatic contracts, complete wood families, ruined storage, Field Kitchen recipes,
  sticky jump restoration, nine-biome world contracts, station bases/facing/audio,
  Sawmill recipes, glass loot, the recoverable no-duplication spear, all 15 expansion
  fauna profiles and every abandoned-camp variant.
- `worldtest.cmd` scans real FULL chunks, not GameTest templates.
- Smoke audit: one fixed seed and 5x5 chunks through the entire build height.
- Full audit: three fixed seeds and at least 17x17 chunks each, plus a wide sparse
  uncached biome-source probe that does not load or generate additional chunks.
- The preset's possible-biome set must equal the reviewed nine-biome allowlist;
  strict Full verification must observe every expected biome through deep or wide
  samples across its three seeds.
- Every block and fluid id must be Gravesown or explicitly allowlisted.
- No vanilla block entity, structure or bonus chest may enter generated chunks.
- Ore counts and min/max Y are reported and bounded.
- A save/reload pass repeats the audit and checks persistent custom content.
- Dedicated server and client must generate the same seed consistently.

Worldgen ids become permanent once a public build creates save files. Changes to
worldgen are expected to create chunk borders; development worlds are disposable.
