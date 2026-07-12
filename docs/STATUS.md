# Current status

Updated: 2026-07-12 22:02 Europe/Zurich
Branch: main; TC3b implementation and verification complete, checkpoint pending
Playable state: creature/equipment alpha plus eleven placeable Gravesown world blocks
Last build: `scripts/verify-all.ps1` — PASS; release JAR copied to `dist/`
Last verified command: `scripts/verify-all.ps1` — PASS

## Current milestone

TC3b — the first wood family and bootstrap flora are implemented. Common-side
behavior, data resources, dedicated logical-server tests and client asset loading pass.

## Implemented and verified

- Added stable block and block-item ids for `ribroot_stem`, `ribroot_planks`,
  `veil_foliage`, `threadgrass`, `ribroot_shoot` and `pallid_bulb`.
- Ribroot Stem is axis-aware; wood and foliage use standard log/plank/leaf tags
  plus family tags so future recipes and leaf-distance updates remain compatible.
- Three no-collision ground plants only survive on Ashen Sod or Grave Loam and
  reject vanilla dirt. Threadgrass is tree-replaceable, the Shoot is a sapling,
  and Pallid Bulb is a flower with atmospheric light level 3.
- Every TC3b block currently has an exact self-drop loot table and matching
  English/Russian name, blockstate, block model and inventory model.
- Seven original deterministic 16x16 textures cover stem side/end, planks,
  foliage and three plants. Cutout alpha and nearest-neighbor contact-sheet QA pass.
- Three new logical-server tests bring the suite to 11/11 PASS and cover ids,
  items, placement axis, collision, light, soil survival, tags and loot.
- Client main-menu smoke loaded `mod/gravesown`, rebuilt the block atlas without
  missing model/texture or error markers, and shut down normally.

## Known issues and open acceptance checks

- TC3a/TC3b blocks still need player-driven in-world composition QA. Exact `/give`
  checklists are in `docs/TESTING.md`; no placement claim is made yet.
- Ribroot Shoot deliberately does not grow until TC4 provides the audited custom
  configured tree feature. World generation does not place any new content yet.
- Strict world audit remains expected to fail until the custom one-biome preset
  replaces the vanilla generator; the latest baseline records 778,728 violations.
- TC3 still lacks the no-command first-tool recipes and early utility blocks.
- Quietskin worn-model visual QA and multiplayer reconnect checks remain open.

## Next action

Implement TC3c first-tool survival loop: add `ribroot_splint`, `thread_binding`,
`hushstone_shard`, `bound_knife` and `crude_handpick`, the Ribroot plank conversion
and 2x2 crafting chain, original item textures/models/translations, tool tags and
deterministic GameTests. Completion criterion: a fresh-inventory test can craft both
tools without vanilla ingredients, the Handpick harvests Hushstone, and client,
GameTests, release build and baseline world audit remain green.

## Verification evidence

- `gradlew.bat compileJava processResources` — PASS without deprecation warnings.
- TC3b asset audit — PASS — 87 JSON resources parsed, six complete asset matrices,
  seven 16x16 PNGs and bilingual names checked; alpha silhouettes are nonempty.
- `scripts/run-gametests.ps1` — PASS — `All 11 required tests passed`.
- `scripts/run-client.ps1` — PASS — Gravesown resources and block atlas loaded;
  asset/error scan clean; normal client shutdown.
- `scripts/verify-all.ps1` — PASS — doctor, 11/11 GameTests, clean build and
  baseline dedicated-world audit all passed.
- Baseline audit sampled 25 FULL chunks, 2,457,600 block/fluid positions and
  38,400 biome positions; it recorded the expected 778,728 pre-TC4 violations
  in 489 ms.
