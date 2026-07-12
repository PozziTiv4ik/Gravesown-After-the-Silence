# Current status

Updated: 2026-07-12 21:49 Europe/Zurich
Branch: main; TC3a foundation-block implementation and verification complete, checkpoint pending
Playable state: alpha creature/equipment slice plus five placeable world blocks
Last build: `scripts/verify-all.ps1` — PASS; clean release JAR assembled
Last verified command: `scripts/verify-all.ps1` — PASS

## Current milestone

TC3a — the first world-foundation palette is implemented. Code/data, server
behavior and client resource loading pass. A player-driven in-world composition
check remains open because Windows UI control was unavailable.

## Implemented and verified

- `ashen_sod`, `grave_loam`, `hushstone`, `deep_hushstone` and `gravebed` have
  stable block and block-item registrations and appear in the Gravesown tab.
- Ashen Sod/Grave Loam are shovel-mineable; Hushstone uses a pickaxe; Deep
  Hushstone rejects wooden and gold tools but accepts stone tier and above.
- Gravebed has negative destroy time, extreme blast resistance and no loot table.
- Four self-drop block loot tables load and produce exactly one matching block item.
- Five blockstates, five block models, five inventory models and both translations exist.
- Six original deterministic 16x16 textures cover surface top/side/bottom, soil,
  normal/deep rock and the compressed bottom layer.
- The pixel sheets were visually reviewed as a nearest-neighbor contact sheet;
  exact dimensions and the complete asset matrix pass.
- Two new logical-server tests bring the required suite to 8/8 PASS.
- The client rebuilt the block atlas, entered an existing integrated-server world
  and logged no Gravesown missing-model, missing-texture, ERROR or FATAL marker.
- The client and integrated server then saved and shut down normally.

## Known issues and open acceptance checks

- The five blocks were not placed and viewed inside Minecraft. Computer Use failed
  to connect to its native pipe, so no claim is made about final in-world tiling.
  The exact `/give` checklist is in `docs/TESTING.md`.
- These blocks are registered content only; the vanilla generator does not place
  them yet. Strict world audit therefore remains expected to fail until TC4.
- TC3 still lacks Ribroot wood/vegetation, early custom tools and utility blocks.
- Quietskin worn-model visual QA and multiplayer reconnect checks remain open.

## Next action

Implement TC3b bootstrap flora: `ribroot_stem`, `ribroot_planks`, `veil_foliage`,
`threadgrass`, `ribroot_shoot` and `pallid_bulb`, including original textures,
correct render shapes, wood/leaf/plant tags, loot, translations and GameTests.
Completion criterion: all six content ids load on client and dedicated server,
their survival drops/tags pass automated tests, and the release build plus baseline
world audit remain green.

## Verification evidence

- `gradlew.bat compileJava processResources` — PASS.
- `scripts/run-gametests.ps1` — PASS — `All 8 required tests passed`.
- Foundation asset audit — PASS — five complete block/model matrices, 44 JSON
  resources parsed and six block PNGs verified at exactly 16x16.
- `scripts/run-client.ps1` — PASS — resource atlas and existing world loaded;
  no relevant asset/error markers; graceful integrated-server shutdown.
- `scripts/verify-all.ps1` — PASS — doctor, 8/8 required GameTests, clean build
  and baseline real-world audit all passed.
- Baseline audit sampled 25 FULL chunks, 2,457,600 block/fluid positions and
  38,400 biome positions. It recorded 778,724 expected vanilla-world violations
  while TC4 world generation is not implemented, and correctly passed baseline mode.
