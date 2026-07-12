# Current status

Updated: 2026-07-12 22:38 Europe/Zurich
Branch: main; TC4a implementation and verification complete, checkpoint pending
Playable state: creature/equipment alpha plus a barren total-conversion Overworld
Last build: `verify-all.cmd -FullWorld` — PASS; release JAR copied to `dist/`
Last verified command: `verify-all.cmd -FullWorld` — PASS

## Current milestone

TC4a — the minimal one-biome total-conversion preset is implemented. Fresh dedicated
and integrated-client worlds load the same seed, and strict Smoke/Full audits pass.

## Implemented and verified

- `gravesown:after_the_silence` defines an Overworld-only preset with a fixed
  `gravesown:sown_grave` biome. English and Russian UI names are present.
- A custom density function creates low hills around Y64. Surface rules produce one
  Ashen Sod layer, three Grave Loam layers, Hushstone, Deep Hushstone and one
  unbreakable Gravebed floor across the full -64..319 build range.
- The biome has no precipitation, carvers or placed features. Aquifers, fluids,
  vanilla ore veins and structures are absent; Hollow Grazer is its only natural
  spawn entry and can spawn on both Gravesown soils.
- The fifteenth GameTest locks the biome, noise-settings and world-preset dynamic
  registry contracts, including the fixed biome source and empty feature lists.
- `worldtest.cmd` and `verify-all.cmd` now use the Gravesown preset and strict mode
  by default. Known-incomplete generators require explicit `-Baseline`.
- New `clienttest.cmd` safely prepares and copies a fixed-seed audit save under
  `run-clienttest/`, quick-loads it, validates seed/biome, saves and exits normally.
- Three-seed Full audit scanned 867 FULL chunks, 85,229,568 block positions and the
  same number of fluid positions. Every report contains only Sown Grave, the five
  reviewed terrain blocks, technical air/empty fluid and zero violations.

## Known issues and open acceptance checks

- TC4a terrain is intentionally barren: Ribroot, Threadgrass, Pallid Bulb, deposits,
  caves and subzones are not generated naturally yet.
- The preset intentionally has no Nether or End stems until TC8 provides complete
  replacements; existing vanilla worlds are not converted.
- The world-creation preset label and terrain composition still need a player-driven
  visual pass; automated quick-play bypasses the creation screen.
- Natural Hollow Grazer population density needs an in-world observation after TC4b
  provides enough terrain interest for a meaningful Survival test.
- TC3a/TC3b block composition, first-tool Survival flow and Quietskin worn-model
  visual QA remain manual checks documented in `docs/TESTING.md`.

## Next action

Implement TC4b natural bootstrap features: an original Ribroot tree plus controlled
Threadgrass and Pallid Bulb patches, all using only Gravesown blocks in Sown Grave.
Completion criterion: a fixed-seed fresh player can obtain at least one Ribroot Stem
and three Threadgrass within the Smoke audit region, all 15+ GameTests pass,
`clienttest.cmd` passes, and the three-seed strict Full audit remains at zero violations.

## Verification evidence

- `gradlew.bat compileJava processResources` — PASS.
- TC4 resource/script audit — PASS — seven new JSON resources parsed, three changed
  PowerShell scripts parsed and forbidden Fabric/Forge API scan returned empty.
- `test.cmd` — PASS — `All 15 required tests passed`.
- `worldtest.cmd` — PASS — strict Smoke, 25 FULL chunks, 2,457,600 block positions,
  one biome and zero violations.
- `clienttest.cmd` — PASS — client resources loaded, player joined seed
  `-7046029254386353131` in Sown Grave, then disconnected and saved normally; no
  missing texture/model, registry or datapack errors were found in the client log.
- `worldtest.cmd -Profile Full` — PASS — all three seeds, 867 FULL chunks and zero
  biome/block/fluid/block-entity violations.
- `verify-all.cmd -FullWorld` — PASS — doctor, 15/15 GameTests, clean build/JAR and
  the complete strict Full world audit.
