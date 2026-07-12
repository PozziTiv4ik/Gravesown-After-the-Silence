# Current status

Updated: 2026-07-12 22:13 Europe/Zurich
Branch: main; TC3c implementation and verification complete, checkpoint pending
Playable state: creature/equipment alpha, eleven world blocks and local first tools
Last build: `scripts/verify-all.ps1` — PASS; release JAR copied to `dist/`
Last verified command: `scripts/verify-all.ps1` — PASS

## Current milestone

TC3c — the vanilla-free player-grid first-tool chain is implemented. Recipes,
server-authoritative tool behavior, client resources and the full verification path pass.

## Implemented and verified

- Added stable ids for `ribroot_splint`, `thread_binding`, `hushstone_shard`,
  `crude_handpick` and `bound_knife`, all visible in the Gravesown creative tab.
- Seven recipes form a complete 2x2 chain: Ribroot Stem → Planks → Splints,
  Threadgrass → Bindings, Handpick, Hushstone ↔ Shards and Bound Knife.
- Automated recipe inspection proves every recipe fits 2x2 and every accepted
  ingredient belongs to the Gravesown namespace.
- Crude Handpick has 48 durability and mining speed 2.5. It harvests Hushstone,
  cannot harvest Deep Hushstone and repairs only with Ribroot Splints.
- Bound Knife has 96 durability, uses standard sword behavior, cuts bootstrap
  plants efficiently and repairs only with Hushstone Shards.
- Standard pickaxe/sword tags and Gravesown handpick/knife/material tags load on
  the logical server. Vanilla sticks and flint are rejected as repair items.
- Five original deterministic 16x16 item textures, five item models and both
  translations pass exact dimension, alpha silhouette and contact-sheet QA.
- Three new server tests bring the required suite to 14/14 PASS.
- Client smoke loaded all resources and recipes, entered the existing integrated
  world, connected the player, then saved and shut down normally with a clean asset log.

## Known issues and open acceptance checks

- The crafting/mining chain is automated-test complete but still needs a player-driven
  Survival playthrough; the exact checklist is in `docs/TESTING.md`.
- TC3a/TC3b block composition and Quietskin worn-model visual QA remain manual.
- Ribroot Shoot does not grow and no Gravesown content is generated naturally yet.
- Strict world audit still fails by design on the vanilla generator; the latest
  baseline records 778,728 pre-TC4 violations.
- Gravework Bench, Pitch Kiln, Hide Rack, Tallow Lamp and Sinew Crate remain planned.

## Next action

Implement TC4a minimal total-conversion world generation: selectable
`gravesown:after_the_silence`, exactly one `gravesown:sown_grave` Overworld biome,
custom noise settings/surface rules for Ashen Sod, Grave Loam, Hushstone, Deep
Hushstone and Gravebed, and no vanilla fluids, aquifers, ores, features or structures.
Completion criterion: new worlds load on client and dedicated server, safe terrain
generates, and strict Smoke world audit reports zero biome/block/fluid/block-entity
violations for the custom preset.

## Verification evidence

- `gradlew.bat compileJava processResources` — PASS without Java warnings.
- TC3c asset audit — PASS — 105 JSON resources parsed, five item matrices/textures,
  seven recipe files and bilingual names checked.
- `scripts/run-gametests.ps1` — PASS — `All 14 required tests passed`.
- `scripts/run-client.ps1` — PASS — recipes/assets and integrated world loaded;
  player login, save and normal shutdown completed with no relevant error markers.
- `scripts/verify-all.ps1` — PASS — doctor, 14/14 GameTests, clean build and
  baseline dedicated-world audit all passed.
- Baseline audit sampled 25 FULL chunks, 2,457,600 block/fluid positions and
  38,400 biome positions; it recorded 778,728 expected pre-TC4 violations in 948 ms.
