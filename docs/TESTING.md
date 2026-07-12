# Testing

## One-click path

1. Run setup.cmd once.
2. Run doctor.cmd.
3. Run test.cmd. It starts a headless NeoForge GameTest server and exits by itself.
4. Run worldtest.cmd. It audits an isolated generated world and exits by itself.
5. Run play.cmd.
6. Use a disposable Creative test world with cheats, never an important survival world.

## Gradle commands

- Fast compile: gradlew.bat compileJava
- Project checks: gradlew.bat check
- Release build: gradlew.bat clean build
- Development client: gradlew.bat runClient
- Dedicated server: gradlew.bat runServer
- GameTests: gradlew.bat runGameTestServer
- Real generated-chunk audit server: gradlew.bat runWorldTest
- Data generation: gradlew.bat runData

The root scripts automatically select the project-local Java 21.

## Automated GameTest

`test.cmd` currently runs six required server tests. Together they verify that:

- is registered under the stable expected id;
- can be created and added to a logical server level;
- receives at least five server ticks and remains alive;
- has 28 maximum health and uses the CREATURE spawn category.
- its deterministic loot rolls contain only Gravesown resources, always provide
  the required core materials, expose both optional drops across fixed seeds and
  never contain leather or rotten flesh;
- every Quietskin piece uses the intended slot, defense, durability and repair item,
  and the equipped full set produces 10 armor points;
- Dead Scent produces exact range multipliers of 100%, 87.5%, 75%, 62.5% and 50%,
  including a server-side targeting boundary test.
- an already active daytime blood-scent hunt stops after the player equips enough
  Quietskin or heals, while night aggression continues.
- the world-audit contract permits only reviewed technical air/empty-fluid states
  and Gravesown-owned content, and strict/baseline status semantics cannot be mixed up.

Success means exit code 0 and `All 6 required tests passed` in the output. Gradle
`check` does not run GameTests, so gameplay changes require the separate button.

## Real generated-chunk world audit

`worldtest.cmd` is different from a GameTest template. It starts a real isolated
dedicated world, obtains FULL chunks from the normal world generator, scans them,
writes reports and then uses Minecraft's normal save/stop path.

Useful commands:

```text
worldtest.cmd
worldtest.cmd -Strict
worldtest.cmd -Profile Full
verify-all.cmd
verify-all.cmd -StrictWorld
```

- Default Smoke mode scans one fixed seed, 5x5 chunks, all 384 build-height blocks,
  biome quart cells, fluid states and block entities.
- Full mode scans three fixed seeds and 17x17 chunks per seed. It is deliberately
  slower and is intended for worldgen milestones and release candidates.
- Baseline enforcement exits successfully after recording violations. This is the
  default only while the custom preset does not exist.
- Strict enforcement exits nonzero unless the only biome is
  `gravesown:sown_grave` and every generated block/fluid/block entity satisfies the
  Gravesown namespace plus the explicit technical allowlist.
- Reports are stored under `build/reports/gravesown/world-audit/` as JSON and text.

Safety rules:

- The only disposable world root is `run-worldtest/`, protected by
  `.gravesown-worldtest-root`; `run/world` is never touched.
- The script verifies the resolved absolute path and sentinel before recursive deletion.
- It never accepts the Minecraft EULA and never scans or deletes a personal world.
- `-ReuseWorld` is restricted to the single-seed Smoke profile and validates the
  saved world's actual seed before accepting a report.

## TC1 manual acceptance

In a disposable world with cheats, obtain the complete set quickly:

```mcfunction
/give @s gravesown:quietskin_hood
/give @s gravesown:quietskin_coat
/give @s gravesown:quietskin_legwraps
/give @s gravesown:quietskin_boots
/give @s gravesown:ragged_grazer_hide 16
/give @s gravesown:taut_sinew 16
```

- Inspect all icons, names and the Dead Scent tooltip in both `en_us` and `ru_ru`.
- Equip every piece and inspect front/back/arms/legs in third person, daylight,
  darkness and hurt flash; purple-black pixels or displaced UV regions fail the check.
- Kill at least 20 Hollow Grazers without Looting. Confirm the five custom resources
  and the absence of leather and rotten flesh.
- Craft and repair every armor piece in Survival using the documented recipes.
- During daytime, become wounded below half health and compare blood-sense targeting
  just inside and outside 24, 21, 18, 15 and 12 blocks as pieces are equipped.
- At night, confirm that a complete set does not suppress ordinary night aggression.

## Client smoke test

- Reach the main menu without loader errors.
- Create a new Creative test world.
- Open the Gravesown creative tab or use:

  ```mcfunction
  /give @s gravesown:hollow_grazer_spawn_egg
  /summon gravesown:hollow_grazer ~ ~ ~
  ```

- Run `/time set day`; confirm a healthy Survival player is not selected as prey.
- Run `/time set night`; confirm a nearby Survival player is selected as prey.
- At daytime, lower the player below half health and confirm blood-sense aggression.
- Let eligible vanilla mobs spawn naturally and confirm the configured Overworld
  suppression/replacement behavior. Command- or egg-spawned vanilla mobs should
  remain available for development tests.
- Check idle movement, navigation, target selection, damage, death and loot.
- Leave the world, restart the client and reload it.
- Confirm English and Russian names.
- Inspect latest.log for errors, missing textures and missing models.

## Entity visual test

- Inventory/spawn egg icon is readable.
- Model has no detached cubes or inverted UVs.
- Texture is correct in daylight, darkness and hurt flash.
- Adult/child size is correct if supported.
- Animation does not slide badly at normal movement speed.
- No purple-black missing texture appears.

## Dedicated server test

Run server.cmd. Some development runtimes start directly; others may create
run/eula.txt and stop. The script never accepts the Minecraft EULA automatically.
If an EULA file is created, read it and change its value only if you personally
accept it, then run server.cmd again. Type `stop` in the server console for a
graceful shutdown.

Check:

- server starts without loading client-only classes;
- world generation completes;
- entities spawn and tick;
- save and restart preserve required state;
- a client can connect through localhost;
- client disconnect/reconnect does not duplicate entities or state.

Use online-mode=false only for an isolated localhost development server. Never
expose such a server to the internet.

## Release JAR test

build.cmd places the non-sources JAR in dist/. Test it in a separate launcher
profile containing exactly Minecraft 1.21.1, the matching NeoForge version and
the Gravesown JAR.

## Reporting a failure

Run diagnostics.cmd and provide the generated ZIP plus:

- exact steps;
- expected behavior;
- actual behavior;
- screenshot or short video for visual problems.

The diagnostic archive intentionally excludes worlds, authentication data,
options, resource packs and unrelated personal files.
