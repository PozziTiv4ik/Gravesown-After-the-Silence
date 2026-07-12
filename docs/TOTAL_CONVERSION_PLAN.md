# Total conversion plan

Status: Living document
Accepted direction: 2026-07-12

This file is the long-term implementation plan for the Gravesown total
conversion. Future agents may refine, reorder or remove entries when verified
implementation teaches us something new. Stable released registry ids must not
be renamed casually.

## Product contract

- The playable world uses the dedicated preset `gravesown:after_the_silence`.
- Its Overworld uses exactly one biome: `gravesown:sown_grave`.
- Newly generated playable Overworld chunks contain no visible or obtainable
  vanilla blocks, fluids, ores, vegetation or structures.
- Vanilla registries remain intact for engine stability and development tools.
- The initial strict technical allowlist is only `minecraft:air` and
  `minecraft:cave_air`. Any temporary exception must be named and tracked.
- Existing vanilla/M1 worlds are never converted in place. Worldgen development
  is new-world-only until the schema is declared stable.
- Nether and End survival access stays unavailable until each dimension receives
  an explicit Gravesown replacement milestone.

## The single biome

Working name: **The Sown Grave / Засеянная могила**.

It remains one registry biome while terrain noise, surface rules and placed
features create different visual subzones:

1. **Ashskin Flats / Пеплокожие равнины** — open grazing land and Hollow Grazers.
2. **Ribroot Groves / Рощи рёберника** — membrane-crowned custom trees.
3. **Marrow Rifts / Костномозговые разломы** — cliffs, caves and exposed deposits.
4. **Suture Mire / Шовная топь** — dark silt and later custom ichor ponds.
5. **Hush Hollows / Безмолвные впадины** — fog and dangerous night populations.
6. **Cairnspines / Курганные хребты** — rare ridges and late resources.

The first worldgen release only needs Flats, Groves and Rifts.

## World foundation palette

Priority-zero blocks:

- `ashen_sod` — surface;
- `grave_loam` — soil;
- `hushstone` — primary rock;
- `deep_hushstone` — deep rock;
- `fractured_hushstone` — early building and processing material;
- `clotted_marl` — noisy surface patches;
- `gravebed` — unbreakable bottom replacement;
- `ribroot_stem`, `ribroot_planks`, `veil_foliage` — first wood family;
- `threadgrass`, `ribroot_shoot`, `pallid_bulb` — bootstrap vegetation.

Early utility blocks:

- `gravework_bench` — crafting station;
- `pitch_kiln` — cooking and smelting;
- `hide_rack` — hide curing;
- `tallow_lamp` — early light;
- `sinew_crate` — storage.

No vanilla default feature may be copied without auditing every block provider.
Aquifers and vanilla ore veins remain disabled in the first preset. Custom ponds
are placed features; a full custom fluid system comes later.

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

1. Gather loose Hushstone, Threadgrass and Ribroot shoots by hand.
2. Craft a Bound Knife and Crude Handpick in the 2x2 grid.
3. Obtain Ribroot and Hushstone.
4. Build the Gravework Bench, Pitch Kiln, shelter and Tallow Lamp.
5. Hunt Hollow Grazers during daytime and process food/hides.
6. Mine Ferric Marrow and craft the first metal tool.
7. Assemble Quietskin and enter a Marrow Rift.
8. Recover the first Mute Crystal and craft the future Hush Needle.

The survival gate is strict: a new player with an empty inventory must obtain
food, light, shelter and a mining tool without commands or vanilla resources.

## Implementation sequence

### TC1 — Hollow Grazer economy

Implementation status: code/data complete and 5/5 automated tests pass; worn-armor
visual inspection and no-command acquisition remain manual acceptance checks.

- custom drops and loot table;
- Quietskin armor, recipes, textures and Dead Scent integration;
- GameTests for ids, equipment, armor values and scent reduction.

### TC2 — World audit harness

- `worldtest.cmd` for true chunk generation and block/biome/fluid audit;
- `verify-all.cmd` for doctor, GameTests, world audit and build;
- reports under `build/reports/gravesown/`.

### TC3 — World foundation content

- priority-zero block palette and pixel textures;
- mining/replaceable/tool tags, loot tables and building variants;
- bootstrap items and utility blocks.

### TC4 — One-biome world preset

- dynamic registry datagen;
- fixed biome source, custom noise settings and surface rules;
- custom carvers and safe spawn;
- no vanilla structures, aquifers or ore veins.

### TC5 — Deposits and subzones

- four functional deposits and tool progression;
- Flats, Groves and Rifts;
- reproducible density and height validation.

### TC6 — Survival completeness

- processing stations, food, lighting, storage and building families;
- one-hour no-command playthrough;
- dedicated-server save/reload.

### TC7 — Ecosystem and atmosphere

- additional creatures, predator/prey interactions and spawn costs;
- remaining subzones, Gravesown ruins, particles and original sounds.

### TC8 — Other dimensions

- independent Gravesown Nether replacement;
- independent Gravesown End replacement;
- full progression and bosses without vanilla-world access.

## Verification contract

- `test.cmd` covers deterministic content mechanics.
- `worldtest.cmd` scans real FULL chunks, not GameTest templates.
- Smoke audit: one fixed seed and 5x5 chunks through the entire build height.
- Full audit: three fixed seeds and at least 17x17 chunks each.
- Every biome palette entry must be `gravesown:sown_grave`.
- Every block and fluid id must be Gravesown or explicitly allowlisted.
- No vanilla block entity, structure or bonus chest may enter generated chunks.
- Ore counts and min/max Y are reported and bounded.
- A save/reload pass repeats the audit and checks persistent custom content.
- Dedicated server and client must generate the same seed consistently.

Worldgen ids become permanent once a public build creates save files. Changes to
worldgen are expected to create chunk borders; development worlds are disposable.
