# Testing

## One-click path

1. On a fresh GitHub download, run launcher.cmd. It invokes setup.cmd only when
   the external setup marker is absent, then opens the branded launcher. play.cmd
   provides the same bootstrap with a direct client launch.
2. For development, run doctor.cmd after setup.
3. Run artcheck.cmd after texture, model-reference or art-generator changes. It audits
   native texture dimensions/references and writes comparison contact sheets without
   launching Minecraft. Art Language V3 also writes its final-ownership coverage table.
4. Run test.cmd. It starts a headless NeoForge GameTest server and exits by itself.
5. Run worldtest.cmd. It audits an isolated generated world and exits by itself.
6. Run clienttest.cmd only for a visual/client milestone or release candidate. It
   opens the isolated audit save in a client and exits by itself; routine code/content
   iteration should prefer compile, GameTests and the world audit to save time/RAM.
   Visual acceptance uses one final 1920x1080 run. The 854x480 layout is an optional
   responsive regression only and is never the sole visual acceptance capture.
7. Run github-ready.cmd before a commit intended for GitHub.
8. Run package-release.cmd for a release-candidate JAR and Windows source/launcher ZIP.
9. Use a disposable fresh Survival/Creative test world, never an important personal world.

## Gradle commands

- Fast compile: `gradle-offline.cmd compileJava`
- Project checks: `gradle-offline.cmd check`
- Release build: `gradle-offline.cmd clean build`
- Development client: `gradle-offline.cmd runClient`
- Dedicated server: `gradle-offline.cmd runServer`
- GameTests: `gradle-offline.cmd runGameTestServer`
- Real generated-chunk audit server: `gradle-offline.cmd runWorldTest`
- Data generation: `gradle-offline.cmd runData`

The root scripts select Java 21 from `%LOCALAPPDATA%\Gravesown\runtime`, use the
shared `%USERPROFILE%\.gradle` dependency cache, keep a path-keyed cache for each
working copy under `%LOCALAPPDATA%\Gravesown\cache\projects`, and apply offline
single-use Gradle execution.
Client, server and test runs live under `%LOCALAPPDATA%\Gravesown\runs`.
`setup.cmd` is the only normal path allowed to download a missing JDK, wrapper
distribution or dependency; an offline failure means setup must be run explicitly
rather than silently consuming network during a test or game launch.

## Automated art audit

`artcheck.cmd` is the fast presentation gate. It checks all shipped Gravesown PNGs
against the native-size contract (16x16 terrain, reviewed 16/32 items and exact
entity/armor UV dimensions), resolves every Gravesown block/item texture referenced by
model JSON and writes these nearest-neighbor contact sheets:

- `build/reports/gravesown/art/terrain-contact-sheet.png`;
- `build/reports/gravesown/art/entity-atlas-contact-sheet.png`;
- `build/reports/gravesown/art/block-catalog-contact-sheet.png`;
- `build/reports/gravesown/art/item-catalog-contact-sheet.png`.

The final generator stage writes
`build/reports/gravesown/art/art-language-v3-coverage.tsv`. Every shipped texture must
have one deliberate V3 owner or an explicitly reviewed UV-safe grading path; a missing
row or accidental earlier-generator overwrite fails the release art gate.

Review the sheets for cross-family palette collisions, repeated masonry-like rails on
natural surfaces, seams and accidental cyan/pink/gray grading. A release-candidate art
pass also runs `scripts/generate-all-art.ps1` twice and compares hashes of every shipped
texture; both runs must be identical before the final 1920x1080 client smoke.

## Automated GameTest

`test.cmd` runs the complete registered logical-server suite. Do not hard-code its test
count here: every added gameplay contract must register its own required GameTest, and
the runner must finish with zero required failures. The suite covers:

- Hollow Grazer registration, 28 health, server ticks, five-resource deterministic
  loot and the absence of leather/rotten flesh;
- all four Quietskin slots, defense/durability/repair, 10-point full set, exact Dead
  Scent multipliers and cancellation of an active daytime scent hunt;
- the strict/baseline world-audit contract and exact nine-biome allowlist semantics;
- five foundation blocks and six regional surface blocks: stable block/item ids,
  placement, mining tags/tiers, block properties and exact loot;
- Ribroot wood, Veil Foliage, six land plants and Gravebloom Dust: axis/collision/light,
  ecosystem tags, Gravesown-only support soils and exact drops;
- five first-tool items and seven 2x2 recipes, including Hushstone/Deep Hushstone
  gating, repair materials and the complete Splint/Binding/Handpick/Shard/Knife path;
- Ribspring, Stitchtusk, Woundscent, Buried Remnant and Rotfin registration, spawn
  category, server ticks and reviewed health/movement/follow/damage attributes;
- all nine biome registry contracts: no precipitation/carvers/foreign spawns, exact
  native creature, exact low-density ambient fish weights and exact Gravesown
  placed-feature set;
- air-default noise settings with no aquifers/ore veins, variable warmth/wetness,
  variable terrain density and the exact nine-biome `MultiNoiseBiomeSource` preset;
- the custom Gloamwater type/source/flow/block/bucket family, five aquatic plants,
  conversion of every reviewed natural underwater floor to Gloam Sand/Muck, organic
  multi-scale material boundaries independent from the deterministic 4x4 ecology grid,
  all-column water probing, bounded plant coverage and all three fish attributes/
  real-fluid survival;
- the fourteen-node Survival Hub condition/claim chain, locked/ready/claimed transitions,
  claim persistence/no double claim, persistent exactly-once first-Survival grant and
  pending grant when a player first changes from Creative to Survival;
- the custom Codex use path refreshes server conditions and sends the versioned
  condition/claimed masks; the serverbound Claim payload is revalidated authoritatively;
- all four Quietskin pieces are real strict Gravework 4x4 recipes, alongside the seven
  existing tool/station/water recipes; the test constructs every exact server input;
- Remnant Grave stable ids/attributes, successful one-shot interaction, saved/synced
  108-tick server emergence and Gravesown-only Buried Remnant loot;
- the top-priority advancement filter: both vanilla and Gravesown ordinary progression
  are absent; quest progress exists only in the Survival Hub attachments;
- the Quietskin condition requires all four pieces in their equipment slots: merely
  carrying the complete set is insufficient;
- hoe tilling, Gloamwater-only farmland hydration, rejection of vanilla water,
  Gravebloom Dust growth for both crops and the field/shelter recipes;
- complete Emberbark/Palevine family recipes and tags, 36-slot Reliquary Crate storage,
  all three ordered Field Kitchen recipes, bucket return and exact utensil wear;
- all seven complete wood families, their species-preserving decorative recipes,
  distinct ordinary/cut plank resources and client-only falling-leaf block contract;
- Sawmill registration/menu/serializer and one-to-one ordinary-plank to same-species
  cut-plank recipes for every wood family;
- Gravesown Glass and Tempered Glass smelting progression, relative strength and their
  different Silk Touch/self-drop loot contracts;
- Mosswake Woods and Amberquiet Grove registry/feature contracts, all four new tree
  silhouettes, Marrow Rifts accent stones/shrubs and Suture Mire rooted trees/puddles;
- sticky Gravesown ground retaining horizontal drag while restoring the normal
  server-observed player jump impulse.

Success means exit code 0 and a summary showing that every registered required test
passed with zero failures. Gradle `check` does not run GameTests, so gameplay changes
require the separate button.

## TC3a foundation-block visual acceptance

In a disposable Creative world, obtain the five blocks:

```mcfunction
/give @s gravesown:ashen_sod 64
/give @s gravesown:grave_loam 64
/give @s gravesown:hushstone 64
/give @s gravesown:deep_hushstone 64
/give @s gravesown:gravebed 64
```

- Build a 5x5 sample of each block in daylight and darkness; check for purple-black
  missing assets, obvious 16-block tiling seams and unreadable value collisions.
- Confirm Ashen Sod uses its top on top, loam underneath and the rooted side laterally.
- In Survival, compare hand, shovel, wooden/gold/stone pickaxes and verify Deep
  Hushstone drops only at stone tier or better.
- Confirm Gravebed cannot be broken in Survival and produces no item if removed in Creative.
- Switch `en_us`/`ru_ru`, reload resources with F3+T and inspect all inventory names/models.

## TC3b Ribroot/flora visual acceptance

In the same disposable Creative world, obtain the six new blocks:

```mcfunction
/give @s gravesown:ribroot_stem 64
/give @s gravesown:ribroot_planks 64
/give @s gravesown:veil_foliage 64
/give @s gravesown:threadgrass 16
/give @s gravesown:ribroot_shoot 16
/give @s gravesown:pallid_bulb 16
```

- Place Ribroot Stem vertically and sideways; confirm the end grain follows the axis.
- Build a dense Veil Foliage canopy and confirm holes use hard cutout edges without
  purple-black pixels, sorting halos or missing internal silhouettes.
- Place all plants on Ashen Sod and Grave Loam, then remove their support and confirm
  they break. They must not remain on vanilla dirt.
- View the plants from both sides, near/far and fast/fancy graphics. Confirm there is
  no opaque black rectangle and that Pallid Bulb emits only a faint light level 3.
- Inspect all six inventory models and names in both `en_us` and `ru_ru`.

## TC3c first-tool acceptance

In Survival, start with one Ribroot Stem, three Threadgrass and no vanilla items:

```mcfunction
/clear @s
/give @s gravesown:ribroot_stem 1
/give @s gravesown:threadgrass 3
```

- In the player 2x2 grid, convert the Stem to four Planks, two Planks to four
  Splints and three Threadgrass to two Bindings.
- Craft a Crude Handpick from two Splints and one Binding. Confirm it mines and
  drops Hushstone, but cannot make Deep Hushstone drop.
- Convert one mined Hushstone into four Shards; craft a Bound Knife from one Shard,
  one Splint and one Binding. Rebuild four Shards into Hushstone as a separate
  reversibility check.
- Confirm the Handpick repairs with Splints but not sticks, and the Knife repairs
  with Shards but not flint. Inspect both icons in hand and inventory in en/ru.

## Real generated-chunk world audit

`worldtest.cmd` is different from a GameTest template. It starts a real isolated
dedicated world with `gravesown:after_the_silence`, obtains FULL chunks, scans them,
writes reports and then uses Minecraft's normal save/stop path.

Useful commands:

```text
worldtest.cmd
worldtest.cmd -Profile Full
worldtest.cmd -Baseline -WorldPreset minecraft:normal
verify-all.cmd
verify-all.cmd -FullWorld
clienttest.cmd
```

- Default Smoke mode uses Survival and scans one fixed seed, 5x5 chunks, all 384
  build-height blocks, biome quart cells, fluid states and block entities. The
  Survival default is intentional so the copied client world exercises a real
  first-login onboarding path.
- Full mode scans three fixed seeds and 17x17 chunks per seed. It is deliberately
  slower and is intended for worldgen milestones and release candidates. It also
  samples the uncached biome source on a 4-chunk grid across +/-128 chunks; those
  4225 cheap climate probes per seed do not load or generate additional chunks.
- Strict enforcement is the default. Every sampled biome must belong to the exact
  nine-biome Gravesown allowlist, and every block/fluid/block entity must satisfy the
  Gravesown namespace plus the explicit technical allowlist. Technical air remains
  allowed; non-empty fluid must be Gravesown-owned (currently Gloamwater). Smoke may
  sample only a subset; Full succeeds only when its deep and wide samples contain
  every expected biome while block/fluid truth still comes only from FULL chunks.
- `-Baseline` is an explicit diagnostic escape hatch for auditing a known-incomplete
  generator such as `minecraft:normal`; it never claims total-conversion acceptance.
- Reports are stored under `build/reports/gravesown/world-audit/` as JSON and text.

Safety rules:

- The only disposable world-audit root is
  `%LOCALAPPDATA%\Gravesown\runs\tests\worldtest`, protected by
  `.gravesown-worldtest-root`; the player client and its saves are never touched.
- The script verifies the resolved absolute path and sentinel before recursive deletion.
- It never accepts the Minecraft EULA and never scans or deletes a personal world.
- `-ReuseWorld` is restricted to the single-seed Smoke profile and validates the
  saved world's actual seed before accepting a report.
- `clienttest.cmd` first generates the fixed-seed strict Smoke world in Survival,
  then uses only `%LOCALAPPDATA%\Gravesown\runs\tests\client-smoke`, protected
  by its own sentinel. It copies the isolated audit
  save, checks the same seed, an allowed Gravesown biome and the packaged 1672x941
  title background, and does not manufacture a guide item. On fresh login it asserts
  exactly one server-granted Codex plus the persistent attachment, uses that exact
  inventory stack and verifies it remains stored. It then requests the Handpick through
  the same serverbound path used by container-hover `R`, builds every entry from the
  synchronized recipe manager, validates search, categories, Guide, its exact grid and
  six-node/five-edge branching dependency graph, the dedicated Field Kitchen 3x2
  layout/localization and high-zoom Story edge reachability, auto-fits the graph, and captures
  Codex Chain plus Guide at 1920x1080.
  Nearby natural mobs are cleared;
  the current harness inserts and tracks the four dry-land biome natives plus Buried
  Remnant by UUID, network id and type before capturing the lineup. It also selects the
  real Creative Survival Inventory tab and captures its exact layout. It writes Codex,
  Guide, Creative inventory, five-entity lineup and armor PNGs under
  `%LOCALAPPDATA%\Gravesown\runs\tests\client-smoke\screenshots`, then saves
  and exits. The three aquatic creatures remain outside this dry-land capture.

## TC4a world-preset acceptance

- Run `worldtest.cmd`; strict Smoke must report zero violations.
- Run `worldtest.cmd -Profile Full`; all three seeds and all 867 FULL chunks must pass.
- Run `clienttest.cmd`; it must log `GRAVESOWN_CLIENT_SMOKE_RESULT status=PASS`,
  disconnect normally and save the integrated world.
- For visual QA, run `play.cmd`, create a disposable world and cycle World Type to
  **After the Silence / После Тишины**. Confirm low hills, an Ashen Sod surface,
  no water/vanilla vegetation/structures and a safe solid spawn.
- The TC4a terrain is intentionally barren. Natural Ribroot and bootstrap plants
  are the separate TC4b milestone, not a missing-resource error.

TC4a is retained as historical foundation evidence. New visual and Survival checks
must use fresh TC4b worlds because old saves can show expected chunk borders.

## TC4 world acceptance

- Run `test.cmd`; every required server test must pass and startup must report
  `Loaded 0 advancements` plus a non-empty Gravesown-only recipe catalog with no data-pack
  error. The recipe contract must find no `minecraft:stick` recipe while retaining
  `gravesown:crude_handpick`.
- Run `worldtest.cmd`; strict Smoke must scan 25 FULL chunks and report zero
  violations. It is valid for this small region to contain fewer than nine biomes.
- Run `worldtest.cmd -Profile Full`; the union of three reports must contain exactly
  Sown Grave, Ribroot Groves, Marrow Rifts, Suture Mire, Gloam Sea, Ember Thicket,
  Pallid Weald, Mosswake Woods and Amberquiet Grove, with zero missing expected biomes
  and zero forbidden blocks, fluids or block entities.
- Current `clienttest.cmd` evidence joins an allowed biome, validates the title
  background, proves the real fresh-Survival auto-grant and persistent no-duplicate
  flag, verifies the complete synchronized recipe catalog plus search/categories/Guide/exact-grid/
  branching-chain/`R` behavior, captures the corrected Creative Survival inventory,
  opens/captures the responsive Hub, tracks a five-entity dry-land lineup
  including Buried Remnant and captures the Quietskin front view. Rotfin, Veilfin and
  Rootskimmer still need separate manual underwater views.
- Run `play.cmd` in a disposable fresh Survival world. Visit all nine regions and
  compare their silhouettes/surfaces: ash/loam, Rootfelt/Fibrous Loam, Scar
  Shale/Marrowstone and Suture Silt/Dried Ichor. Look for abrupt walls, unsafe spawn,
  long featureless flats and visually repetitive macro shapes.
- Confirm Ribroot, Threadgrass and Pallid Bulb are reachable near the opening route;
  Groves also contain Ribroot Shoots, and Mire contains Dried Ichor patches.
- In Suture Mire, find a rare Gloamwater pond and confirm it contains no vanilla
  water, Threadkelp is rooted below the surface, and Rotfin swims rather than strands
  or clips. The fixed Smoke seed may legitimately miss the 1/14 feature.
- In Sown Grave, find a rare Remnant Grave, use it once and confirm the Buried Remnant
  spends the full readable emergence window rising before it can move or deal/take
  damage. Reuse must not spawn or drop another reward. The fixed Smoke seed may
  legitimately miss the 1/64 feature.
- Observe at least ten minutes in each region. Record whether Hollow Grazer,
  Ribspring, Stitchtusk and Woundscent appear naturally at readable but non-spammy
  density. Automated biome tables prove eligibility, not actual pacing.

## TC4V onboarding and branded-client acceptance

- Run `launcher.cmd`; confirm the Windows app opens, the shared background crops
  cleanly at and above 1100x700, the centered animated Play action and bottom console
  remain readable in RU and EN, Play starts the cached ModDev client, Verify runs the project doctor, the
  log control opens a useful folder and a second game process cannot be started while
  one is active. The launcher must not ask for alternate credentials or download a
  Minecraft binary itself. For low-cost layout QA, use `--render-preview` before a
  live window run.
- At the Minecraft title screen, use 1920x1080 for the primary visual pass; optionally
  test 854x480 and 1280x720 only as responsive regressions. The
  menu buttons must remain usable and the Gravesown brand/subtitle readable. Open
  world selection, options, graphics and a loading screen; themed backgrounds and
  global hard-edged widget sprites must not cover text, sliders or narration focus.
- Enter a fresh Survival world with an empty inventory. Exactly one Survivor's Codex
  must be granted and open the full-screen `Story / Crafts / Chain / Guide` Hub on
  right-click. Also create a
  player in Creative, switch once to Survival and confirm the pending grant occurs.
  Reconnect, switch modes again and die/respawn; no duplicate may appear.
- Inspect all fourteen nodes in both EN/RU. Pan and zoom Story. In Crafts, type localized
  letters and an English registry-id fragment, click results and compare every displayed
  2x2/4x4/kiln layout to the real station. Exercise All, Tools, Building, Food,
  Equipment and Materials; Chain navigation must reset the filter to All. Open Chain
  and confirm independent natural
  resources remain separate until their first common recipe, the complete graph auto-fits,
  pan/zoom works, and craftable nodes jump to Crafts. Hover an item in a container and
  press `R`; the Hub must open directly on that item's Chain. Confirm empty searches
  recover cleanly and long names fit.
  Pin a difficult Chain, inspect intermediate recipe nodes and confirm the pinned graph
  does not change until unpinned or replaced through `R`. Open Guide and pan/scroll
  through Basics, Farming, Food, Water, Building, Cooking and Exploration in both
  languages; Guide is explanatory only and must not mutate inventory or quest state.
  Locked/ready/claimed states must follow server order. A ready node remains unclaimed
  until Claim is pressed; verify sound and slide-out notice. Vanilla and Gravesown
  ordinary advancement tabs must not be loaded, and no vanilla recipe may be available.
- Open options, Creative inventory and the vanilla recipe-book panel. Buttons, slider
  rails, text fields and panel borders must be continuous; no dangling right edge or
  gap may appear around a handle. The lock and recipe-book icons must use Gravesown art,
  and Creative tabs/slots must match the common cold navy/steel/cyan interface
  family. This check applies only to presentation; nearby world art must retain its
  local natural material colors.
- Place and bucket Gloamwater beside a shoreline. Confirm it is turbid but visibly
  translucent, changes coherently without flicker, converts sources and descends/spreads like vanilla
  water while retaining only Gravesown fluid/block ids.
- In a fresh created world, confirm After the Silence is the only preset, structures
  stay enabled and the bonus chest is absent/disabled. Till Ashen Sod and Grave Loam,
  then prove nearby Gloamwater hydrates the field while ordinary water does not. Grow
  both crops, craft both foods and build with every new Ribroot shape plus the lantern.
- Complete Ribroot/Threadgrass → Handpick → Hushstone → Knife without commands, then
  hunt a Hollow Grazer and assemble Quietskin. Record missing instructions, pacing
  dead ends and any unobtainable ingredient.
- Manually inspect the volumetric open-face Quietskin on both default and slim player
  models from front/back, in daylight/darkness and during hurt flash; look for hidden
  limbs, detached plates or clipping during crouch and arm swing. Also review all nine
  creatures' motion at natural speed, with all three fish viewed underwater.
- Open Survival inventory, Gravework and Pitch Kiln. Their outer frame, slots, player
  inventory and labels must use the same palette without dangling lines. Start from
  `play.cmd` and confirm FML uses its dark early window before Gravesown resources load.

## TC4D exploration and cooking acceptance

- Visit Ember Thicket and Pallid Weald in a fresh world. Confirm each has its own
  surfaces, trees and vegetation, and build every Emberbark/Palevine decorative shape.
  Functional station recipes must accept mixed reviewed planks; decorative outputs
  must keep the selected species.
- Inspect at least three natural water bodies, including one medium/large lake or ocean
  and one irregular shoreline across a biome boundary. Gloamwater must animate at
  vanilla cadence and remain custom/tinted. Every visible natural floor directly below
  it must be Gloam Sand/Muck rather than a regional surface block. Each chunk-aligned
  4x4 ecology cell must consider all sixteen columns for at most one plant candidate,
  independently of the organic multi-scale Sand/Muck material noise. All five aquatic
  families should occur over a broad route, with no empty climate-border chunks,
  checkerboard shore pattern or carpet-like multi-plant cell.
- Find a natural ruined shelter without commands. Open its Reliquary Crate, verify four
  rows and Gravesown-only loot, then save/reload and confirm contents persist.
- Craft the Field Kitchen, Bone Cleaver and Stirring Hook. Prepare all three meals with
  the exact ordered inputs; each craft must consume food, return an empty bucket and
  damage both utensils by one without consuming them.
- Fish with the Gloamline Rod until Needle Sprat appears. Confirm no vanilla fish or
  treasure enters the conversion loop and judge bite timing, sound and food value.

## TC4E woodland, workshop and Art Language V3 acceptance

- Run `scripts/generate-all-art.ps1` twice, hash every shipped Gravesown PNG after each
  complete run and require identical hash sets. Run `artcheck.cmd` after the second run;
  review all four contact sheets and the V3 ownership table before launching a client.
- In a fresh world, visit Mosswake Woods and Amberquiet Grove. Both must use Ashen Sod/
  Grave Loam without looking like copies: compare their broad Mosswake and tall Sunveil
  tree silhouettes, species foliage particles, understory plants and local mob pacing.
- In Marrow Rifts, find Veined Shale, Splintered Marrowstone, Cairnstone, Cairnwood shrubs
  and Rift Thorn as bounded accents rather than carpets. In Suture Mire, verify each
  Suturewood tree has four readable low roots converging on one trunk and that puddles
  remain small, custom-fluid pockets.
- Inspect all seven wood species in daylight and shade. Stems, foliage, ordinary planks,
  cut planks, stairs, slabs, fences, gates, doors and trapdoors must retain a distinct
  species palette. Falling leaves are visual-only, sparse and use the source foliage's
  texture; they must not create items, blocks or server scans.
- Craft and open a Sawmill. Each recipe must accept exactly one ordinary plank and return
  exactly one cut plank of the same species; mixed or cross-species substitution must not
  recolor the result. Verify its title/menu in EN and RU and inspect its functional-block
  silhouette against the V3 palette.
- Smelt Gloam Sand into Gravesown Glass, then resmelt that glass into Tempered Glass.
  Ordinary glass must require Silk Touch to recover, while tempered glass self-drops and
  takes approximately three times as long to break under equivalent conditions.
- Stand on every Gravesown block with reduced movement/jump factors. Walking drag must
  remain, but an ordinary player jump must still clear a one-block ledge without commands
  or flight.
- Inspect the refreshed dry-land creatures, Buried Remnant and all three fish at native
  speed. Require bilateral layered silhouettes, paired aquatic anatomy, segmented tails,
  readable joints and no detached cubes or UV bleed.
- Perform one final 1920x1080 visual run. Compare title, world creation, options,
  Creative/Survival inventory, Codex, Gravework, Kiln, Kitchen, Reliquary, Sawmill and the
  Windows launcher. All presentation surfaces must use the exact cold navy/steel/cyan
  hierarchy with continuous rails, hard pixel edges and readable focus. Terrain,
  blocks, items, creatures, armor and biome effects must remain on their reviewed
  natural local palettes with no global cold grade.

## Native-fauna, station, spear and camp acceptance

- GameTests must instantiate every new entity, verify attributes/loot registration and
  exercise its server AI archetype without loading client classes. Spawn resources must
  map one to three new endemic species to each of the nine biomes, with low predator
  weights and bounded groups.
- A strict three-seed Full world audit must still observe all nine biomes after the
  climate frequency change. The wide biome probe should show materially more region
  transitions than the former four-times-width build without turning them into strips.
- Tree feature tests must exercise more than one deterministic trunk/crown variant for
  every naturally generated wood family. Player-only shoots must remain absent from
  natural decoration.
- Camp tests must force every layout variant, verify biome-appropriate wood selection,
  fence/accessibility, exactly bounded Reliquary loot and deterministic placement. Full
  world audit must prove that ordinary generation remains rare and namespace-clean.
- Place Reliquary Crate and Sawmill facing north/east/south/west over a contrasting
  block. Their bottom is opaque, interaction side matches facing and menus persist
  inventory. One viewer transition produces one open and one close sound; taking a
  Sawmill output produces one crafting sound.
- Throw the spear into a living target, a block and empty terrain. A successful entity
  hit must apply strong movement slowness for exactly 200 ticks, preserve server-owned
  damage/ownership and allow recovery without duplication after save/reload.
- The one final FHD client smoke must capture a representative passive animal, small or
  flying animal, neutral animal and predator in natural daylight, plus the corrected
  station bottoms and orientation. Navy presentation colors may not tint any entity or
  world texture.

## TC1 manual acceptance

In a disposable world with cheats, obtain the complete set quickly:

```mcfunction
/give @s gravesown:quietskin_hood
/give @s gravesown:quietskin_coat
/give @s gravesown:quietskin_legwraps
/give @s gravesown:quietskin_boots
/give @s gravesown:ragged_grazer_hide 16
/give @s gravesown:taut_sinew 16
```

- Inspect all icons, names and the Dead Scent tooltip in both `en_us` and `ru_ru`.
- Equip every piece and inspect front/back/arms/legs in third person, daylight,
  darkness and hurt flash; purple-black pixels or displaced UV regions fail the check.
- Kill at least 20 Hollow Grazers without Looting. Confirm the five custom resources
  and the absence of leather and rotten flesh.
- Craft and repair every armor piece in Survival using the documented recipes.
- During daytime, become wounded below half health and compare blood-sense targeting
  just inside and outside 24, 21, 18, 15 and 12 blocks as pieces are equipped.
- At night, confirm that a complete set does not suppress ordinary night aggression.

## Client smoke test

- Reach the main menu without loader errors.
- Create a new Creative test world.
- Open the Gravesown creative tab or use:

  ```mcfunction
  /give @s gravesown:hollow_grazer_spawn_egg
  /summon gravesown:hollow_grazer ~ ~ ~
  /summon gravesown:ribspring ~3 ~ ~
  /summon gravesown:stitchtusk ~8 ~ ~
  /summon gravesown:woundscent ~12 ~ ~
  /summon gravesown:buried_remnant ~16 ~ ~
  ```

- Run `/time set day`; confirm a healthy Survival player is not selected as prey.
- Run `/time set night`; confirm a nearby Survival player is selected as prey.
- At daytime, lower the player below half health and confirm blood-sense aggression.
- Let eligible vanilla mobs spawn naturally and confirm the configured Overworld
  suppression/replacement behavior. Command- or egg-spawned vanilla mobs should
  remain available for development tests.
- Check idle movement, navigation, target selection, damage, death and loot.
- Confirm Ribspring flees and never retaliates. Give Stitchtusk a clear 5–16 block
  lane and verify its roar/still telegraph precedes the straight rush. Compare a
  healthy and below-75%-health player at 12–28 blocks from Woundscent and verify its
  pursuit periodically pauses.
- Place/use `gravesown:remnant_grave` and confirm its one-shot emergence separately
  from command summon. The block has no survival loot table and an opened grave must
  not release a second creature.
- Test Rotfin in a naturally generated or controlled Gloamwater pond rather than the
  dry-land lineup. Confirm Threadkelp remains in Gloamwater and Rotfin swims, targets,
  attacks, dies and drops only its reviewed loot without client-only server crashes.
- Leave the world, restart the client and reload it.
- Confirm English and Russian names.
- Inspect latest.log for errors, missing textures and missing models.

## Entity visual test

- Inventory/spawn egg icon is readable.
- Model has no detached cubes or inverted UVs.
- Bilateral creatures remain visually balanced; fish show paired fins/gills or barbels
  where their species calls for them and a readable segmented tail rather than a flat box.
- Every cube must remain inside the declared atlas bounds. Added detail cubes require
  dedicated, non-overlapping, full-footprint UV islands rather than borrowing a
  one-pixel swatch; opaque multi-tone safety texels must cover otherwise unused atlas
  space so an accidental face cannot become invisible.
- Texture is correct in daylight, darkness and hurt flash.
- Adult/child size is correct if supported.
- Animation does not slide badly at normal movement speed.
- No purple-black missing texture appears.

## Dedicated server test

Run server.cmd. Some development runtimes start directly; others may create
`%LOCALAPPDATA%\Gravesown\runs\server\eula.txt` and stop. The script never
accepts the Minecraft EULA automatically.
If an EULA file is created, read it and change its value only if you personally
accept it, then run server.cmd again. Type `stop` in the server console for a
graceful shutdown.

Check:

- server starts without loading client-only classes;
- world generation completes;
- entities spawn and tick;
- save and restart preserve required state;
- a client can connect through localhost;
- client disconnect/reconnect does not duplicate entities or state.

Use online-mode=false only for an isolated localhost development server. Never
expose such a server to the internet.

## Release JAR test

build.cmd places the non-sources JAR in dist/. Test it in a separate launcher
profile containing exactly Minecraft 1.21.1, the matching NeoForge version and
the Gravesown JAR.

## GitHub and release-bundle test

Run `github-ready.cmd`. It must pass the offline compile/build, packaged launcher
diagnostic, Git whitespace check and repository-content audit. The content audit
rejects caches, test worlds, credentials, generated runtime files and files near
GitHub's 100 MiB per-file limit.

Run `package-release.cmd`. It must create:

- `dist/gravesown-<version>.jar`;
- `release/Gravesown-After-the-Silence-<version>-Windows.zip`.

Extract the ZIP to a new directory and run the packaged launcher diagnostic.
Confirm that `launcher.cmd`, source files, Gradle wrapper, release JAR and
`launcher/dist/Gravesown Launcher/Gravesown Launcher.exe` are present. The
archive must not contain Java, Gradle caches, Minecraft game binaries, account
state, logs or worlds. On a genuinely fresh machine, `launcher.cmd` is expected
to run setup once before opening the launcher. The packaging command performs
this structure/privacy/hash/launcher diagnostic automatically through
`scripts/verify-release-bundle.ps1`.

## Reporting a failure

Run diagnostics.cmd and provide the generated ZIP plus:

- exact steps;
- expected behavior;
- actual behavior;
- screenshot or short video for visual problems.

The diagnostic archive intentionally excludes worlds, authentication data,
options, resource packs and unrelated personal files.
