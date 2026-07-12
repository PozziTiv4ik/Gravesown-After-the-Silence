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
- [ ] Temporary loot table exists; original survival resources are not implemented yet.
- [ ] Client and dedicated-server smoke tests.

Acceptance: a new world contains a functioning Hollow Grazer, regular eligible
vanilla spawns are suppressed according to config, and save/reload works.

## M2 — Basic ecosystem

- [ ] Bellbeak.
- [ ] Stitchtusk.
- [ ] Ribspring.
- [ ] Rotfin.
- [ ] Biome-aware spawn tables and predator/prey interactions.

Acceptance: Overworld land and water have a stable basic food chain.

## M3 — Regional infection

- [ ] Silence Nodes.
- [ ] Region state and persistence.
- [ ] Spawn/visual/audio changes by local infection.
- [ ] Temporary cleansing loop.

Acceptance: two regions can maintain different infection states across restart.

## M4 — Survival progression

- [ ] Food, hide, string and common mob-drop replacements.
- [ ] Weapons and protective equipment.
- [ ] Nether and End progression resources.
- [ ] Configurable balance and migration.

Acceptance: a fresh survival world can reach vanilla endgame without enabling
ordinary vanilla mob spawns.

## M5 — Polish

- [ ] Final models, animations, sounds, particles and UI.
- [ ] Accessibility and gore controls.
- [ ] Performance profiling and spawn caps.

## M6 — Release

- [ ] Dedicated multiplayer test with two players.
- [ ] Upgrade/config migration test.
- [ ] Clean profile and compatibility smoke tests.
- [ ] License, credits, changelog and distribution metadata.
