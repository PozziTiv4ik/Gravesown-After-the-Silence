# Current status

Updated: 2026-07-12 21:35 Europe/Zurich
Branch: main; TC2 implementation complete, final checkpoint commit pending
Playable state: alpha creature/equipment slice; custom world foundation is next
Last build: PASS — `scripts/build.ps1`; release JAR copied to `dist/`
Last verified command: `scripts/run-worldtest.ps1 -ReuseWorld` — baseline PASS

## Current milestone

TC2 — real generated-chunk world audit harness is implemented. Smoke baseline,
strict negative-control behavior and the report schema are verified. The current
vanilla world is expected to fail strict enforcement until TC3–TC4 replace it.

## Implemented and verified

- TC1 remains complete: five Hollow Grazer resources, Quietskin recipes/repair,
  original pixel assets and per-piece Dead Scent behavior.
- Dead Scent tests no longer share mutable world time, removing parallel GameTest
  flakiness while production AI still reads the real server day/night state.
- New isolated ModDev run `runWorldTest` uses `run-worldtest/`, never `run/world`.
- `worldtest.cmd` creates a sentinel-protected disposable world, generates real
  `LevelChunk` instances to FULL status and stops through the normal server save path.
- Smoke mode scans 25 chunks and every block/fluid position from Y -64 through 319,
  every biome quart cell and every block entity.
- Strict contract requires `gravesown:sown_grave`, allows Gravesown content plus
  only `minecraft:air`, `minecraft:cave_air` and the empty fluid state.
- JSON and text reports include versions, seed, region, histograms, totals, duration,
  violation counts and up to 50 samples per violation kind with world coordinates.
- Baseline mode records expected pre-worldgen violations but exits successfully;
  strict mode exits nonzero whenever any forbidden content exists.
- `verify-all.cmd` chains doctor, six GameTests, clean build and world audit.
- Full mode is implemented for three fixed seeds and 17x17 chunks per seed.

## Known issues and open acceptance checks

- Strict smoke currently fails as designed: the vanilla generator produced 778,724
  violations in the verified seed. TC3–TC4 must drive that count to zero.
- Full three-seed/17x17 execution is implemented but not yet run; smoke and strict
  negative-control modes are the executed evidence for TC2.
- Quietskin worn-model visual QA, no-command acquisition and multiplayer reconnect
  remain manual checks from TC1.
- Nether and End replacement remains outside the current Overworld milestone.

## Next action

Begin TC3 by registering the five priority-zero foundation blocks — `ashen_sod`,
`grave_loam`, `hushstone`, `deep_hushstone` and `gravebed` — with original 16x16
textures, block/item models, loot, mining/tool tags, translations and deterministic
GameTests. Completion criterion: all five are usable building/mining content on
client and dedicated server, all automated tests and build pass, and worldtest
still produces a valid baseline report ready for TC4 placement.

## Verification evidence

- `scripts/run-worldtest.ps1` — PASS — baseline scan of 25 real FULL chunks,
  2,457,600 block/fluid positions and 38,400 biome samples.
- `scripts/run-worldtest.ps1 -Strict -ReuseWorld` — expected FAIL — correctly
  rejected 778,724 vanilla biome/block/fluid/block-entity violations with samples.
- `scripts/run-gametests.ps1` — PASS — `All 6 required tests passed`.
- `scripts/verify-all.ps1` — PASS — doctor, 6/6 GameTests, clean build and
  baseline real-chunk audit completed through the one-button workflow.
- `scripts/build.ps1` — PASS — final clean/check/build and release JAR copy.
- Earlier client and dedicated-server smoke tests remain PASS for the TC1 build.
