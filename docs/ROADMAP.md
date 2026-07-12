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
- [ ] Original model, renderer, texture, spawn egg and translations are implemented; visual QA pending.
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
- [ ] Original item and worn-armor pixel textures implemented; worn visual QA pending.
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

## TC4 — The Sown Grave

- [ ] `gravesown:after_the_silence` world preset.
- [ ] Exactly one `gravesown:sown_grave` Overworld biome.
- [ ] Custom noise settings, surface rules and carvers.
- [ ] No vanilla aquifers, ore veins, structures or visible generated blocks.

Acceptance: strict audit passes and the same seed is reproducible on client and server.

## TC5 — Resources and subzones

- [ ] Grave Pitch, Ferric Marrow, Gloam Salt and Mute Crystal.
- [ ] Ashskin Flats, Ribroot Groves and Marrow Rifts.
- [ ] Complete first-hour survival path.

Acceptance: a fresh Survival player reaches Quietskin and Mute Crystal without
commands or vanilla materials.

## TC6 — Ecosystem and regional infection

- [ ] Bellbeak, Stitchtusk, Ribspring and Rotfin.
- [ ] Predator/prey interactions and spawn costs.
- [ ] Silence Nodes, regional state and cleansing loop.
- [ ] Remaining Sown Grave subzones.

Acceptance: ecology and two distinct regional infection states persist across restart.

## TC7 — Polish

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
