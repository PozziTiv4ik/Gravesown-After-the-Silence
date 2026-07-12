# Current status

Updated: 2026-07-12 20:26 Europe/Zurich
Branch: main; clean local baseline commit exists
Playable state: first alpha vertical slice; automated server test passes, manual in-world visual QA pending
Last build: PASS — scripts/build.ps1
Last verified command: scripts/run-gametests.ps1 — PASS, 1/1 required tests

## Current milestone

M1 — first living Gravesown creature and Overworld replacement loop.

## Implemented and verified

- Official NeoForge 1.21.1 ModDevGradle template imported.
- Project identity pinned to Gravesown / gravesown / dev.gravesown.
- Root handoff contract and permanent project documentation created.
- Portable Microsoft OpenJDK 21.0.11 setup, doctor and one-click commands work.
- `clean check build` produces `dist/gravesown-0.1.0-alpha.1.jar`.
- Development client reaches the main menu with the mod and resources loaded;
  no Gravesown missing-model, missing-texture, ERROR or FATAL marker was found.
- Dedicated development server reaches `Done` on NeoForge 21.1.235 without
  loading a client-only class.
- Hollow Grazer registration, attributes, AI, renderer, model, spawn egg,
  translations, temporary loot table and original 128x64 pixel texture exist.
- Server-authoritative Overworld events suppress ordinary natural vanilla mobs,
  replace eligible creature spawns with Hollow Grazers and optionally remove
  loaded vanilla mobs. Developer command/spawn-egg entities remain testable.
- Automated NeoForge GameTest creates a Hollow Grazer, verifies its stable id,
  attributes and spawn category, and confirms that it survives five server ticks.

## In progress

- Manual disposable-world check of the renderer, UV layout, animations and AI.
- Natural spawn replacement and save/reload smoke test inside a real world.
- First proper survival drops; the alpha loot table still uses rotten flesh and leather.

## Known issues

- In-world rendering has not been visually verified. The Windows UI-control
  connector was unavailable, so no claim is made about final UV alignment.
- The dedicated server startup was verified and then deliberately terminated;
  reconnect and graceful save/restart have not yet been tested.
- The current M1 replacement scope is the Overworld. Nether and End ecosystem
  replacement belongs to later milestones.

## Open questions

- Final public author name and distribution license can be chosen before release.
- Exact gore intensity will be confirmed after the first visual concept.

## Next action

Run play.cmd, create a disposable Creative world and complete the Hollow Grazer
manual checklist in docs/TESTING.md: summon, daylight/night aggression, texture,
animation, natural replacement and save/reload. Completion criterion: record the
observed result here and fix every Gravesown ERROR or missing asset found in latest.log.

## Verification evidence

- `scripts/setup.ps1` — PASS — portable Microsoft OpenJDK 21.0.11 installed and verified by SHA-256.
- `doctor.cmd` — PASS — Java, pinned versions, wrapper and compileJava.
- `scripts/build.ps1` — PASS — clean/check/build and release JAR copy.
- `gradlew.bat runClient` — PASS to main menu; deliberately stopped there, so the Gradle task ended nonzero.
- `gradlew.bat runServer` — PASS to `Done (8.834s)`; deliberately stopped after startup verification.
- `scripts/run-gametests.ps1` — PASS — `All 1 required tests passed`.
