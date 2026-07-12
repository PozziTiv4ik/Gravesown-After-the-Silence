# Instructions for all coding agents

## Project

Gravesown: After the Silence is an original Minecraft Java Edition 1.21.1
total-conversion horror mod. It uses NeoForge 21.1.235, ModDevGradle 2.0.141,
Gradle 9.2.1, Java 21, Mojang mappings plus the Parchment version pinned in
gradle.properties, and the mod id gravesown.

Primary play is singleplayer, but all gameplay logic must be correct on a
dedicated server. The server is authoritative.

The project is inspired by the broad infection-apocalypse genre. Never copy
names, code, lore, progression, textures, sounds, models, animations, or
distinctive creatures from Scape and Run: Parasites or any other mod.

## Start every session

1. Read this file completely.
2. Read docs/STATUS.md, docs/DESIGN.md, docs/TOTAL_CONVERSION_PLAN.md,
   docs/ROADMAP.md, docs/DECISIONS.md, and docs/TESTING.md.
3. Inspect git status --short and all relevant diffs. Never discard unknown
   changes.
4. Run doctor.cmd.
5. Continue the exact **Next action** from docs/STATUS.md unless the user
   explicitly changes priorities.
6. Before editing, briefly state what will change and how it will be verified.

## Source of truth

- Code and tests describe current reality.
- docs/DECISIONS.md records accepted architectural decisions.
- docs/DESIGN.md records stable game design.
- docs/TOTAL_CONVERSION_PLAN.md is the editable long-term implementation plan.
- docs/CONTENT.md tracks every content asset.
- docs/ROADMAP.md contains milestones and acceptance criteria.
- docs/STATUS.md is the short operational handoff.

If a document disagrees with verified code, fix the document in the same task.
Chat history is not project memory.

## Hard technical constraints

- Use only Minecraft 1.21.1 and NeoForge 21.1.x APIs present in this project.
- Do not add Fabric API, Minecraft Forge, Yarn mappings, or code copied from a
  different Minecraft version.
- Do not change Java, Gradle, NeoForge, ModDevGradle, Parchment, or mapping
  versions unless the user explicitly requests an upgrade and the decision is
  recorded.
- Prefer NeoForge events, registries, data attachments, configs, and data-driven
  resources. Use Mixins only when an accepted ADR explains why no stable API
  works.
- Keep net.minecraft.client.* references inside dev.gravesown.client.
- Spawning, AI, damage, mutations, progression, loot, and saved state must run
  on the logical server.
- Never scan every entity or every chunk every tick.
- World-audit scans run only in the isolated `runWorldTest` development profile;
  they must never activate during ordinary client or server play.
- Use lowercase snake_case registry ids. Do not casually rename released ids.
- All player-visible text must exist in both en_us.json and ru_ru.json.
- Final game textures are original Minecraft-style pixel art with hard pixel
  edges and no filtering blur.
- Never commit secrets, downloaded JDKs, Gradle caches, compiled output, logs,
  crash reports, or test worlds.
- Never push, publish, accept a server EULA, or delete user worlds without
  explicit user authorization.

## Entity definition of done

An entity is not complete until it has registration, attributes, server-side
AI, spawn or replacement rules, renderer, model, original texture, English and
Russian names, loot table, an easy summon/test path, dedicated-server safety,
and a recorded smoke test.

## General definition of done

1. Implement code and resources for the requested vertical slice.
2. Run gradlew.bat compileJava while iterating.
3. Run test.cmd after changes to entity registration, attributes, spawning, or
   server-side behavior.
4. Run gradlew.bat check and gradlew.bat build before declaring it done.
5. Run the relevant client, GameTest, or dedicated-server smoke test.
6. Run worldtest.cmd after worldgen, biome, placed-feature, structure, fluid, or
   foundation-block changes. Use strict mode when the current milestone claims
   the total-conversion world contract should pass.
7. Record only tests that were actually executed.
8. Update docs/STATUS.md and relevant content/design documents.
9. Append architectural changes to docs/DECISIONS.md.
10. Add user-visible changes to CHANGELOG.md.

## End every session

Update docs/STATUS.md with:

- what is implemented and verified;
- exact verification commands and PASS/FAIL results;
- current known problems;
- important changed areas;
- exactly one concrete **Next action** with its completion criterion.

Keep STATUS concise. Git and CHANGELOG hold history.
