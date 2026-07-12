# Testing

## One-click path

1. Run setup.cmd once.
2. Run doctor.cmd.
3. Run test.cmd. It starts a headless NeoForge GameTest server and exits by itself.
4. Run play.cmd.
5. Use a disposable Creative test world with cheats, never an important survival world.

## Gradle commands

- Fast compile: gradlew.bat compileJava
- Project checks: gradlew.bat check
- Release build: gradlew.bat clean build
- Development client: gradlew.bat runClient
- Dedicated server: gradlew.bat runServer
- GameTests: gradlew.bat runGameTestServer
- Data generation: gradlew.bat runData

The root scripts automatically select the project-local Java 21.

## Automated GameTest

`test.cmd` currently verifies that `gravesown:hollow_grazer`:

- is registered under the stable expected id;
- can be created and added to a logical server level;
- receives at least five server ticks and remains alive;
- has 28 maximum health and uses the CREATURE spawn category.

Success means exit code 0 and `All 1 required tests passed` in the output. Gradle
`check` does not run GameTests, so gameplay changes require the separate button.

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
