# Roadmap

Галочка ставится только после указанной проверки.

## M0 — Foundation

- [x] Official NeoForge 1.21.1 ModDevGradle scaffold.
- [x] Stable project identity and handoff documentation.
- [x] Portable Java 21 setup succeeds on a machine without Java.
- [x] compileJava and build pass.
- [x] Development client reaches the main menu.
- [x] Dedicated server reaches `Done` without client-class crashes.

Acceptance: setup.cmd followed by build.cmd succeeds from a clean checkout.

## M1 — First living build

- [x] Register Hollow Grazer with attributes and initial AI; server GameTest passes.
- [x] Original model, renderer, texture, spawn egg and translations are implemented;
  the historical controlled four-creature lineup capture passes, while natural motion remains
  player-reviewed.
- [x] Server-authoritative config for vanilla mob suppression/replacement loads.
- [ ] Eligible Overworld replacement path is implemented; natural-spawn world test pending.
- [x] Original Hollow Grazer resource loot replaces the temporary vanilla drops.
- [x] Client and dedicated-server startup smoke tests.

Acceptance: a new world contains a functioning Hollow Grazer, regular eligible
vanilla spawns are suppressed according to config, and save/reload works.

## TC1 — Hollow Grazer economy

- [x] Five original Hollow Grazer drops replace vanilla loot.
- [x] Quietskin armor set, recipes and repair material.
- [x] Dead Scent integration with blood-sensing AI.
- [x] Original item and worn-armor pixel textures fully redrawn; the hood leaves the
  player face open and the front/night capture passes, while back/daylight/hurt-flash
  visual QA remains.
- [x] Deterministic GameTests for loot, equipment and scent lifecycle (5/5 PASS).

Acceptance: the complete set can be obtained and tested without vanilla leather
or rotten flesh, and all automated server tests pass.

## TC2 — World audit harness

- [x] `worldtest.cmd` scans real generated FULL chunks in an isolated world.
- [x] `verify-all.cmd` provides one-button full verification.
- [x] Strict biome, block, fluid and block-entity allowlists.
- [x] Machine-readable JSON and human-readable text reports.

Acceptance: a deliberately inserted vanilla block or fluid fails the audit with
its registry id and coordinates. Verified against the current vanilla generator:
baseline records violations, while strict mode exits nonzero with exact samples.

## TC3 — World foundation

- [x] Custom surface, soil, rock, deep rock and bottom blocks (TC3a; 8/8 tests PASS).
- [x] Ribroot wood family and bootstrap vegetation (TC3b; 11/11 tests PASS).
- [x] Vanilla-free 2x2 first-tool crafting chain (TC3c; 14/14 tests PASS).
- [ ] Early light, crafting station, processing and storage.
- [x] Mining, replaceable and tool tags plus loot tables for all current foundation content.

Acceptance: every foundation block has assets, drops, tags and a tested survival use.

## TC4a — Minimal Sown Grave foundation

- [x] `gravesown:after_the_silence` world preset.
- [x] Exactly one `gravesown:sown_grave` Overworld biome.
- [x] Custom noise settings and surface rules; the reviewed TC4a carver list is empty.
- [x] No vanilla aquifers, ore veins, structures or visible generated blocks.

Acceptance: PASS. Smoke and three-seed Full strict audits report zero violations;
the fixed seed also loads in an isolated integrated client with the same biome.

## TC4b — Four-biome living world

- [x] Replace the fixed source with Sown Grave, Ribroot Groves, Marrow Rifts and Suture Mire.
- [x] Give every biome a distinct surface palette and reviewed natural features;
  macro/detail terrain removes the old flat-field profile.
- [x] Add one complete original native creature for each of the three new biomes.
- [x] Make Ribroot and first-tool fiber generate naturally near the reviewed spawn seed.
- [x] Preserve the zero-vanilla strict block/fluid/structure contract.

Acceptance: all four biomes occur across the fixed Full seeds, each required native
block/feature/creature is observed, a fresh Survival player can start without
commands, and the strict Full audit remains at zero forbidden content.

Automated acceptance: PASS — 34/34 GameTests, strict Smoke 25 chunks/0 violations,
strict Full 867 chunks/all four biomes/0 violations and integrated-client smoke PASS.
Natural population pacing and the no-command first-hour route remain manual review.

## TC4C — Gloam Sea and first stations

- [x] Add the fifth `gloam_sea` multi-noise biome with a large Gravesown-owned
  Gloamwater body, custom floor materials and five aquatic plant families.
- [x] Add Rotfin, Veilfin and Rootskimmer with submerged steering, loot, eggs and
  dedicated-server behavior; add Gloamline Rod and Gloam Skiff.
- [x] Add Gravework 4x4, Pitch Kiln and the first Hushstone tool family.
- [x] Add Gravebloom Dust and three regional plant families.
- [x] Add the first custom-field loop: hoe-created Gloam Farmland hydrated only by
  Gloamwater, two crops, seeds, bread/ration food and Gravebloom Dust growth.
- [x] Add the first shelter family: Ribroot stairs, slab, fence, gate, door and
  trapdoor plus a craftable Tallow Lantern.

Acceptance: all five biomes occur across fixed Full seeds, strict audit remains at
zero forbidden content, and all early crafting/fertilizer/aquatic contracts pass.

## TC4D — Seven-biome exploration and cooking expansion

- [x] Add Ember Thicket and Pallid Weald to the reviewed multi-noise source.
- [x] Expand all climate regions to roughly four times their prototype span and keep
  both new shoot items out of natural biome decoration.
- [x] Ship complete Emberbark and Palevine wood/decor families with mixed-plank
  functional recipes and species-specific decorative recipes.
- [x] Add Gloam Muck/Sand floors and chunk-aligned 4x4 aquatic vegetation placement
  in every water-bearing biome;
- [x] Allow low-weight Veilfin/Rootskimmer groups in Gloamwater across all seven
  climates while keeping Sea schools and Mire/Sea predators denser;
  render custom Gloamwater with familiar vanilla water animation sprites.
- [x] Generate three rare ruined-shelter variants with a dedicated 36-slot Reliquary
  Crate and Gravesown-only loot.
- [x] Add Field Kitchen, persistent utensils, three meals and Needle Sprat fishing.
- [x] Expand Story to 14 server conditions, Guide to seven topics and Chain pinning.

Acceptance: strict Full covers all seven biomes across three fixed seeds with zero
forbidden content; 52 GameTests, clean build and FHD client smoke pass. Natural pacing
and the no-command kitchen route remain the next player acceptance.

## TC4V — Branded client and onboarding

- [x] Replace the rejected Quietskin shell with an open-face hood and four custom
  slot-specific volumetric models (49 cubes). Default/slim, back/daylight/hurt-flash
  visual QA remains.
- [x] Rebuild all four creature models/textures with richer geometry, layered detail
  and procedural motion. All 143 cubes pass 128x128 bounds; all 101 added details own
  non-overlapping full-footprint UV islands, and exact four-entity client tracking plus
  the controlled lineup capture pass.
- [x] Branded launcher plus Minecraft title/menu/options/loading presentation. The
  launcher has a centered animated Play action, Verify/Logs below it, a compact bottom
  console, RU/EN locale selection and an offscreen preview path; menu widgets use
  deterministic resource overrides.
- [x] One-time first-Survival Codex opening the full-screen
  `Story / Crafts / Chain / Guide` Hub; both
  first login and a later Creative-to-Survival transition are covered without
  duplicates. Fourteen server-owned conditions and claims synchronize through protocol 3,
  and only explicit server-validated Claim presses complete quests.
- [x] Searchable, clickable Crafts catalog built from the synchronized recipe manager;
  six filters, exact 2x2/3x3/4x4/kiln layouts, a separate branching auto-fitted Chain
  canvas, pinning and seven scrollable Guide topics are verified. Chain nodes and
  container-hover `R` navigate to the exact recipe while resetting the filter to All.
- [x] Gravesown-only advancement and recipe presentation without a fragile Mixin.
- [x] Common Gravesown widget, lock, recipe-book and Creative inventory sprites use
  continuous rails/borders with no right-edge fragments or slider gaps.
- [x] First rare interactive landmark: 1/64 Remnant Grave with persistent one-shot
  state and a 108-tick server-owned Buried Remnant emergence.
- [x] First aquatic pocket: 1/14 Gloamwater ponds, Threadkelp and complete Rotfin.
- [x] Correct the Creative Survival inventory geometry, selected-tab seam and slot-row
  continuity without replacing vanilla inventory behavior.
- [x] Lock new-world creation to After the Silence with structures on and bonus chest
  off through public screen/state APIs.

Acceptance: the generated Windows launcher `.exe` starts the cached client, the
guide is issued once and survives reconnects, progression works on a dedicated
server, and both Russian and English client visuals are readable.

Automated acceptance: 52/52 GameTests, strict Smoke 25 chunks/0 violations, clean
check/build, EN/RU resource parity and final 1100x700 launcher preview PASS. The final
1920x1080 integrated-client smoke proves exact one-time Codex grant, all 54 synchronized
recipes, exact-grid/search/categories/Guide/branching-chain/`R` behavior, corrected
Creative Survival inventory geometry, fourteen tracked profile-based land entities and the Quietskin
front view. Natural pacing,
grave/pond rarity, all three fish underwater and default/slim/back/daylight/hurt-flash
armor views still need a player-driven visual pass.

## TC4E — Nine-biome woodland and Art Language V3 expansion

- [x] Add Mosswake Woods and Amberquiet Grove, both using the readable Ashen Sod/
  Grave Loam foundation with distinct trees, understory and regional populations.
- [x] Add Cairnwood Rifts shrubs, Suturewood rooted mire trees, Mosswake broad crowns
  and Sunveil tall crowns plus their four region-specific plant families.
- [x] Add Veined Shale, Splintered Marrowstone and Cairnstone deposits to Marrow Rifts.
- [x] Complete all seven wood families with cut planks and species-textured falling
  leaves; functional mixed-plank recipes and colored decoration keep separate rules.
- [x] Add the Sawmill with one-to-one species-preserving recipes for all seven cut
  plank variants.
- [x] Add ordinary and tempered glass as a two-stage Gloam Sand smelting loop; the
  tempered block is three times harder and self-drops.
- [x] Replace the Gloamwater bed checker with organic coordinate-warped Sand/Muck
  shelves while keeping the bounded one-candidate-per-4x4-cell ecology rule.
- [x] Preserve sticky horizontal movement while restoring a normal player jump from
  every Gravesown block with a reduced jump factor.
- [x] Make Art Language V3 the final deterministic texture owner. Keep natural local
  gameplay palettes for terrain/items/entities, while stations, containers, Codex,
  vanilla widget overrides, HUD and launcher use the exact cold navy/steel/cyan
  presentation palette.
- [x] Rebuild aquatic and dry-land model geometry around paired anatomy, owned UVs and
  weighted restrained animation rather than flat boxes or frantic motion.

Acceptance: the current required GameTests, art regeneration/hash gate, `artcheck.cmd`,
strict nine-biome Full world audit and clean check/build must pass. One final 1920x1080
client smoke must show the new terrain/woods, all refreshed models, stations, Codex,
Creative inventory and title presentation without missing art; exact executed evidence
belongs in `docs/STATUS.md`.

Acceptance completed 2026-07-15: 66/66 GameTests, two identical full art generations,
229-resource art audit, strict three-seed Full audit with all nine biomes and zero
violations, clean check/build and the five-scene 1920x1080 client smoke all passed.

## TC5 — Resources and subzones

- [ ] Grave Pitch, Ferric Marrow, Gloam Salt and Mute Crystal.
- [ ] Ashskin Flats, Ribroot Groves and Marrow Rifts.
- [ ] Complete first-hour survival path.

Acceptance: a fresh Survival player reaches Quietskin and Mute Crystal without
commands or vanilla materials.

## TC6 — Native planetary ecosystem

- [x] One to three endemic species in every public biome, with at least fifteen new
  passive, neutral, avian/small-fauna and predator entities in the expansion slice.
- [x] All 23 public creatures have discoverable spawn eggs; the fourteen shared-profile
  land species use species-owned geometry and distinct UV-aware atlases rather than one
  recolored quadruped silhouette.
- [x] Rotfin, Gloamwater and Threadkelp aquatic vertical slice.
- [x] Stitchtusk, Ribspring and Woundscent vertical slices.
- [x] Buried Remnant and the one-shot rare Remnant Grave encounter.
- [ ] Predator/prey interactions and spawn costs.
- [ ] Feeding, fleeing, nesting/resting and regional activity loops.
- [ ] Remaining Sown Grave subzones.

Acceptance: every biome has a readable local food web, predators are the minority,
and population/spawn state remains bounded and dedicated-server safe across restart.

## TC7 — Industrial foundation and polish

- [ ] Original local-material processing chain from hand tools to mechanical stations.
- [ ] Metallurgy, power generation, transport and automation design with no copied
  machine names, recipes, energy units or assets from other mods.
- [ ] Final models, animations, sounds, particles and UI.
- [ ] Accessibility and gore controls.
- [ ] Performance profiling and spawn caps.

## TC8 — Other dimensions and release

- [ ] Gravesown Nether and End replacements.
- [ ] Full progression without survival access to vanilla dimensions.
- [ ] Dedicated multiplayer test with two players.
- [ ] Upgrade/config migration test.
- [ ] Clean profile and compatibility smoke tests.
- [ ] License, credits, changelog and distribution metadata.
