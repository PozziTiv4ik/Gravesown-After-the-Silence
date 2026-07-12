# Current status

Updated: 2026-07-12 21:12 Europe/Zurich
Branch: main; TC1 implementation complete, manual visual acceptance pending
Playable state: alpha vertical slice with original Hollow Grazer economy and Quietskin armor
Last build: PASS — `scripts/build.ps1`; release JAR copied to `dist/`
Last verified command: `scripts/run-gametests.ps1` — PASS, 5/5 required tests

## Current milestone

TC1 — Hollow Grazer economy and Quietskin armor. Code, data and automated
verification are complete. The only remaining acceptance item is a player-driven
visual inspection of the worn armor and a no-command survival acquisition pass.

## Implemented and verified

- Official NeoForge 1.21.1 ModDevGradle template with Java 21 and pinned project versions.
- Stable Gravesown identity, root handoff contract and living total-conversion plan.
- One-click setup, doctor, GameTest, play, build, server and diagnostics commands.
- Hollow Grazer registration, attributes, AI, renderer, model, spawn egg and translations.
- Server-authoritative Overworld suppression and replacement of eligible natural vanilla mobs.
- Hollow Grazer loot now contains only five original resources: Ragged Grazer Hide,
  Taut Sinew, Grave Tallow, Tainted Grazer Meat and the rare Hollow Jaw.
- Complete Quietskin set with recipes, correct armor slots and attributes, raw-hide
  repair material, armor tags, item models and original deterministic pixel textures.
- Dead Scent reduces daytime blood-sense range by 12.5% per equipped Quietskin
  piece, reaching 50% for the full set. Night aggression deliberately ignores it.
- Five deterministic server GameTests cover registration/ticking, loot namespace and
  forbidden vanilla drops, equipment/armor/repair behavior, scent-range reduction and
  ending an existing daytime hunt after equipping Quietskin or healing.
- Development client loaded the mod, resources and an existing world without a
  Gravesown ERROR, FATAL, missing-model or missing-texture marker.
- Dedicated development server reached `Done (0.577s)` without loading client-only classes.
- The editable one-biome total-conversion plan lives in
  `docs/TOTAL_CONVERSION_PLAN.md`; future agents must read it through `AGENTS.md`.

## Known issues and open acceptance checks

- Quietskin item textures loaded successfully, but the worn 64x32 UV sheets still
  require an in-game third-person inspection on the player model.
- Natural spawn replacement, full no-command armor acquisition and save/reload remain
  manual world checks; automated real-chunk auditing begins in TC2.
- The dedicated server startup was deliberately terminated after `Done`; localhost
  reconnect and graceful save/restart are not yet verified.
- Current replacement scope is the Overworld. Nether and End are later milestones.

## Next action

Run the short TC1 manual acceptance in `docs/TESTING.md`, then begin TC2: build
`worldtest.cmd`, strict block/biome/fluid allowlists and machine-readable chunk
audit reports before implementing the custom world foundation.

## Verification evidence

- `scripts/setup.ps1` — PASS — portable Microsoft OpenJDK 21.0.11.
- `doctor.cmd` — PASS — Java, pinned versions, wrapper and `compileJava`.
- `scripts/run-gametests.ps1` — PASS — `All 5 required tests passed`.
- `scripts/run-client.ps1` — PASS — main menu and existing world loaded; graceful close.
- `scripts/run-server.ps1` — PASS to `Done (0.577s)`; deliberately stopped afterward.
- `scripts/build.ps1` — PASS — clean/check/build and release JAR copy.
- Release audit — PASS — 83 JAR entries, 21 JSON files, nine 16x16 item
  textures and two 64x32 armor sheets; no Fabric/example-template residue.
