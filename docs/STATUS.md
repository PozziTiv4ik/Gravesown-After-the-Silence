# Current status

Updated: 2026-07-16 Europe/Zurich
Branch: main; published at `PozziTiv4ik/Gravesown-After-the-Silence`
Playable state: nine-biome Survival alpha with 23 native creatures, one vehicle,
early crafting/cooking/farming stations, aquatic life, ruins, camps, a Windows
launcher and a verified GitHub source/release distribution

## Current milestone

The current alpha is published to GitHub. Every runtime and cache that previously
inflated the repository now lives outside Git, a fresh source checkout or Windows
release ZIP has a one-command bootstrap, and automated publication gates reject
private/generated state and oversized files. The clean Windows GitHub runner has
verified setup, offline checks, launcher packaging and artifact upload. The existing
native-ecosystem, survival, launcher and presentation slices remain unchanged and
server-authoritative.

## Implemented and verified

- Added 15 complete endemic species: Ash Hopper, Gloamwing, Rootback, Bark Marten,
  Crag Ram, Rift Puma, Mire Toad, Reed Lynx, Silt Ray, Ember Fox, Cinder Fowl,
  Pallid Hart, Mossboar, Amber Jay and Sunhorn.
- Each new species has its own id, attributes, server AI/spawn rules, renderer/model,
  original 128x128 texture, loot, spawn egg, EN/RU localization and summon/test path.
- Shared land-animal profiles cover bounded prey, flyer, neutral and predator behavior.
  Only Rift Puma and Reed Lynx routinely hunt players; predator weights remain below
  local prey/neutral populations. Silt Ray uses a separate bottom-swimming controller.
- Added the Hushstone Spear: seven melee/thrown damage, recoverable exact durability
  stack and Slowness V for 200 ticks, with the held-stack duplication bug eliminated.
- Reliquary Crate and Sawmill now have opaque undersides, four horizontal facings and
  appropriate open/close or craft audio feedback.
- Added a 1/192 biome-aware abandoned camp with three bounded layouts, local wood,
  accessible fencing and a Reliquary Crate using the dedicated camp loot table.
- Halved the rejected over-broad biome width with the 0.175 climate scale and expanded
  every tree family to multiple deterministic silhouettes.
- Routine Gradle compile/test/build/client/server scripts now use `--offline
  --no-daemon`; only explicit first-time `setup.cmd` may download dependencies.
- Added `clean-storage.cmd`, which safely removes duplicate project-local Gradle
  caches and every disposable development world while preserving the local JDK,
  source, launcher, release JAR, screenshots and verification reports. The first
  cleanup reduced the workspace from 4.37 GiB to 0.52 GiB.
- Moved Java, client/server/test instances, logs and setup state to
  `%LOCALAPPDATA%\Gravesown`; shared dependencies use `%USERPROFILE%\.gradle`,
  while each working copy receives a path-keyed external Gradle project cache.
- `launcher.cmd` and `play.cmd` now bootstrap setup only when needed. A GitHub source
  checkout can build its launcher automatically; the release ZIP includes the
  packaged app-image and release JAR but deliberately excludes Minecraft binaries.
- Added `github-ready.cmd`, `package-release.cmd`, release-bundle re-extraction and
  privacy/hash/EXE verification, Windows CI, tagged GitHub Release automation,
  expanded ignore rules and VS Code publication tasks.

## Verification evidence

- `doctor.cmd` — PASS on 2026-07-16: Java 21, pinned Minecraft 1.21.1,
  NeoForge 21.1.235 and Gradle 9.2.1; offline `compileJava` PASS.
- `test.cmd` — PASS on 2026-07-16: all 66 required NeoForge GameTests.
- `gradlew.bat check build --offline --no-daemon` — PASS on 2026-07-16.
- `clean-storage.cmd` — PASS on 2026-07-16: removed 17 ordinary test saves,
  the dedicated-server test world, client smoke world, world-audit world and both
  duplicate project-local Gradle caches; approximately 3.84 GiB freed.
- Post-clean `doctor.cmd` — PASS on 2026-07-16; post-clean `build.cmd` — PASS
  fully offline in 29 seconds and restored `dist/gravesown-0.1.0-alpha.1.jar`.
- `github-ready.cmd` — PASS on 2026-07-16: external Java/cache check, offline
  compile, clean check/build, Windows app-image build and diagnostic, Git whitespace
  check and repository-content audit. Git candidates: 1,565 files / 7.3 MiB; remote
  `origin` targets `PozziTiv4ik/Gravesown-After-the-Silence`.
- GitHub publication — PASS on 2026-07-16: `main` was pushed without history
  rewriting, the obsolete duplicate online Gradle workflow was removed, and
  `Gravesown CI` run `29532023275` completed successfully on Windows.
- GitHub artifact upload — PASS on 2026-07-16: artifact
  `gravesown-17d009ba373c29e672d246d569d64fe2789e9509` contains the release outputs
  and is 39,175,934 bytes.
- `package-release.cmd` and `scripts/verify-release-bundle.ps1` — PASS on
  2026-07-16: the Windows archive re-extracted with 1,702 files / 85.4 MiB unpacked,
  no runtime/private state, matching JAR hash and passing packaged EXE diagnostic.
- Fresh-copy smoke from the extracted release ZIP — PASS on 2026-07-16:
  `doctor.cmd` selected a distinct path-keyed external project cache, completed an
  offline compile and materialized `Gravesown.class` inside the extracted copy.
- `artcheck.cmd` — PASS on 2026-07-16: 229 Gravesown PNGs, native dimensions and
  model references; four contact sheets regenerated.
- `scripts/generate-all-art.ps1` twice — PASS on 2026-07-15: complete deterministic
  regeneration produced zero shipped-texture hash differences; final coverage tracks
  369 shipped PNGs.
- `worldtest.cmd -Profile Smoke -Strict` — PASS: 25 FULL chunks, 2,457,600 block
  positions and zero violations.
- `worldtest.cmd -Profile Full -Strict` — PASS: three seeds, 867 FULL chunks,
  85,229,568 block positions, 12,675 wide biome probes, all nine expected biomes and
  zero violations.
- `clienttest.cmd` — PASS at 1920x1080: Codex auto-grant, recipe search/categories/
  graph/shortcut, Creative inventory, five tracked creatures and open-face Quietskin;
  all five screenshots are 1920x1080 and the client exited automatically.
- Final artifact: `build/libs/gravesown-0.1.0-alpha.1.jar`, 3.34 MiB,
  SHA-256 `44A6A8CF2BA0210AE63C0C795DB902BC1D006B607E1ECB90039C1D543785EAD2`.

## Known issues

- New shared fauna profiles are release-quality vertical slices but still need a
  player balance pass for density, pursuit pressure and long-session population feel.
- Feeding, resting/nesting and richer cross-species food-web reactions remain planned.
- Normal-distance underwater motion and default/slim multi-angle Quietskin presentation
  retain manual visual checks beyond the automated FHD smoke.
- Industrial progression beyond the current field stations is not implemented yet.
  Nether and End remain intentionally unavailable until TC8.

## Important changed areas

- `NativeFauna`, `NativeFaunaSpecies`, Silt Ray, entity registry/rendering and loot
- all nine biome spawn tables and strict world-audit contracts
- Hushstone Spear item/projectile and server tests
- Reliquary Crate, Sawmill, abandoned camps and their GameTests
- climate density functions, tree configured features and generated art
- offline/no-daemon developer scripts and project agent/testing instructions
- external runtime/cache layout, safe repeatable development-storage cleanup
- GitHub content audit, CI/release workflows, launcher bootstrap and release packaging
- content, design, roadmap, decisions and changelog documentation

## Next action

Run a focused singleplayer native-fauna balance pass across all nine biomes and tune
only evidence-backed density, pursuit and population-pressure issues. Completion
criterion: every biome is observed for at least one full day/night cycle, adjustments
are covered by GameTests/world audit where applicable, FHD acceptance evidence is
recorded, and the offline `check build` gate remains green.
