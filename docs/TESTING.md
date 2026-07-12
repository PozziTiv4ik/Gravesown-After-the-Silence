# Testing

## One-click path

1. Run setup.cmd once.
2. Run doctor.cmd.
3. Run test.cmd. It starts a headless NeoForge GameTest server and exits by itself.
4. Run worldtest.cmd. It audits an isolated generated world and exits by itself.
5. Run clienttest.cmd. It opens the isolated audit save in a client and exits by itself.
6. Run play.cmd.
7. Use a disposable Creative test world with cheats, never an important survival world.

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

`test.cmd` currently runs fifteen required server tests. Together they verify that:

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
- all five foundation blocks/items keep stable ids, place correctly and use the
  intended hardness/no-loot properties;
- soil/rock mining tags, Deep Hushstone tier restrictions and all four self-drop
  loot tables work on the logical server.
- Ribroot wood, Veil Foliage and all three bootstrap plants retain stable block/item
  ids, axis/collision/light behavior, standard ecosystem tags and exact self-drops;
- Threadgrass, Ribroot Shoot and Pallid Bulb survive on both Gravesown soils while
  deliberately rejecting vanilla dirt.
- all five first-tool items retain stable ids, exact durability, repair materials,
  pickaxe/sword/custom tags and the intended Hushstone versus Deep Hushstone gate;
- all seven bootstrap recipes fit the player's 2x2 grid, resolve only Gravesown
  ingredients and execute the complete Ribroot/Threadgrass → Handpick → Shard → Knife chain.
- the Sown Grave dynamic registries retain one fixed biome, no carvers/features or
  foreign spawn entries, fluidless noise settings and the reviewed world preset.

Success means exit code 0 and `All 15 required tests passed` in the output. Gradle
`check` does not run GameTests, so gameplay changes require the separate button.

## TC3a foundation-block visual acceptance

In a disposable Creative world, obtain the five blocks:

```mcfunction
/give @s gravesown:ashen_sod 64
/give @s gravesown:grave_loam 64
/give @s gravesown:hushstone 64
/give @s gravesown:deep_hushstone 64
/give @s gravesown:gravebed 64
```

- Build a 5x5 sample of each block in daylight and darkness; check for purple-black
  missing assets, obvious 16-block tiling seams and unreadable value collisions.
- Confirm Ashen Sod uses its top on top, loam underneath and the rooted side laterally.
- In Survival, compare hand, shovel, wooden/gold/stone pickaxes and verify Deep
  Hushstone drops only at stone tier or better.
- Confirm Gravebed cannot be broken in Survival and produces no item if removed in Creative.
- Switch `en_us`/`ru_ru`, reload resources with F3+T and inspect all inventory names/models.

## TC3b Ribroot/flora visual acceptance

In the same disposable Creative world, obtain the six new blocks:

```mcfunction
/give @s gravesown:ribroot_stem 64
/give @s gravesown:ribroot_planks 64
/give @s gravesown:veil_foliage 64
/give @s gravesown:threadgrass 16
/give @s gravesown:ribroot_shoot 16
/give @s gravesown:pallid_bulb 16
```

- Place Ribroot Stem vertically and sideways; confirm the end grain follows the axis.
- Build a dense Veil Foliage canopy and confirm holes use hard cutout edges without
  purple-black pixels, sorting halos or missing internal silhouettes.
- Place all plants on Ashen Sod and Grave Loam, then remove their support and confirm
  they break. They must not remain on vanilla dirt.
- View the plants from both sides, near/far and fast/fancy graphics. Confirm there is
  no opaque black rectangle and that Pallid Bulb emits only a faint light level 3.
- Inspect all six inventory models and names in both `en_us` and `ru_ru`.

## TC3c first-tool acceptance

In Survival, start with one Ribroot Stem, three Threadgrass and no vanilla items:

```mcfunction
/clear @s
/give @s gravesown:ribroot_stem 1
/give @s gravesown:threadgrass 3
```

- In the player 2x2 grid, convert the Stem to four Planks, two Planks to four
  Splints and three Threadgrass to two Bindings.
- Craft a Crude Handpick from two Splints and one Binding. Confirm it mines and
  drops Hushstone, but cannot make Deep Hushstone drop.
- Convert one mined Hushstone into four Shards; craft a Bound Knife from one Shard,
  one Splint and one Binding. Rebuild four Shards into Hushstone as a separate
  reversibility check.
- Confirm the Handpick repairs with Splints but not sticks, and the Knife repairs
  with Shards but not flint. Inspect both icons in hand and inventory in en/ru.

## Real generated-chunk world audit

`worldtest.cmd` is different from a GameTest template. It starts a real isolated
dedicated world with `gravesown:after_the_silence`, obtains FULL chunks, scans them,
writes reports and then uses Minecraft's normal save/stop path.

Useful commands:

```text
worldtest.cmd
worldtest.cmd -Profile Full
worldtest.cmd -Baseline -WorldPreset minecraft:normal
verify-all.cmd
verify-all.cmd -FullWorld
clienttest.cmd
```

- Default Smoke mode scans one fixed seed, 5x5 chunks, all 384 build-height blocks,
  biome quart cells, fluid states and block entities.
- Full mode scans three fixed seeds and 17x17 chunks per seed. It is deliberately
  slower and is intended for worldgen milestones and release candidates.
- Strict enforcement is the default and exits nonzero unless the only biome is
  `gravesown:sown_grave` and every generated block/fluid/block entity satisfies the
  Gravesown namespace plus the explicit technical allowlist.
- `-Baseline` is an explicit diagnostic escape hatch for auditing a known-incomplete
  generator such as `minecraft:normal`; it never claims total-conversion acceptance.
- Reports are stored under `build/reports/gravesown/world-audit/` as JSON and text.

Safety rules:

- The only disposable world root is `run-worldtest/`, protected by
  `.gravesown-worldtest-root`; `run/world` is never touched.
- The script verifies the resolved absolute path and sentinel before recursive deletion.
- It never accepts the Minecraft EULA and never scans or deletes a personal world.
- `-ReuseWorld` is restricted to the single-seed Smoke profile and validates the
  saved world's actual seed before accepting a report.
- `clienttest.cmd` first generates the fixed-seed strict Smoke world, then uses only
  `run-clienttest/`, protected by its own sentinel. It copies the isolated audit
  save, checks the same seed and biome, then saves and exits.

## TC4a world-preset acceptance

- Run `worldtest.cmd`; strict Smoke must report zero violations.
- Run `worldtest.cmd -Profile Full`; all three seeds and all 867 FULL chunks must pass.
- Run `clienttest.cmd`; it must log `GRAVESOWN_CLIENT_SMOKE_RESULT status=PASS`,
  disconnect normally and save the integrated world.
- For visual QA, run `play.cmd`, create a disposable world and cycle World Type to
  **After the Silence / После Тишины**. Confirm low hills, an Ashen Sod surface,
  no water/vanilla vegetation/structures and a safe solid spawn.
- The TC4a terrain is intentionally barren. Natural Ribroot and bootstrap plants
  are the separate TC4b milestone, not a missing-resource error.

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
