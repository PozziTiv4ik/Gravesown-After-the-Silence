# Architecture decision record

This file is append-only. A later ADR may supersede an older one.

## ADR-0001: NeoForge 1.21.1 and Java 21

Date: 2026-07-12
Status: Accepted

Context: The user explicitly selected NeoForge for a large server-capable content
mod and wants AI-driven development with stable high-level APIs.

Decision: Pin Minecraft 1.21.1, NeoForge 21.1.235, ModDevGradle 2.0.141,
Gradle 9.2.1, Java 21 and the Parchment versions in gradle.properties.

Consequences: Code must use only APIs visible in this workspace. Version upgrades
are separate migration tasks. Fabric, Forge and Yarn examples are invalid here.

## ADR-0002: Original necrotic ecosystem

Date: 2026-07-12
Status: Accepted

Context: The requested mood is inspired by infection-apocalypse mods.

Decision: Build an original setting based on the Silence, Gravesown wildlife and
regional Silence Nodes. Do not reproduce distinctive content from existing mods.

Consequences: Every creature needs an original silhouette, behavior, name, texture,
model and lore. Similarity checks belong in content review.

## ADR-0003: Hybrid concept-to-pixel art workflow

Date: 2026-07-12
Status: Accepted

Context: Image generation is fast for visual exploration but unreliable for exact
Minecraft UV layouts, tiny pixel grids, repeatability and animation frames.

Decision: Use image generation for mood boards, silhouette exploration and selected
source concepts. Convert approved ideas into controlled pixel art, verify every
pixel/UV in game, and keep prompts and sources under art/.

Consequences: Generated concept art is never dropped directly into the JAR as an
unreviewed entity texture. Final PNGs use exact dimensions, nearest-neighbor scaling
and original project palettes.

## ADR-0004: Server-authoritative world replacement

Date: 2026-07-12
Status: Accepted

Context: Singleplayer still runs an integrated server, and the mod must also work
on a dedicated server.

Decision: Spawning, removal, AI, damage, loot and infection state live on the logical
server. Client code only renders and presents synced state.

Consequences: Every milestone includes a dedicated-server safety check. No
net.minecraft.client imports are allowed in common gameplay packages.

## ADR-0005: Headless GameTests for server content

Date: 2026-07-12
Status: Accepted

Context: Manual Minecraft UI checks are slow and do not reliably catch broken
entity registration, attributes or dedicated logical-server behavior.

Decision: Keep small NeoForge GameTests in the main mod source set, register them
with `@GameTestHolder(gravesown)`, and expose `test.cmd` as the one-click runner.
Every server-side vertical slice gets a deterministic fixture and at least one
spawn/tick assertion. Manual client checks remain mandatory for rendering and UVs.

Consequences: `check` alone is not sufficient. Agents run `test.cmd` for gameplay
changes and keep GameTest structures under `data/gravesown/structure/`.

## ADR-0006: Dedicated one-biome total-conversion preset

Date: 2026-07-12
Status: Accepted

Context: The user wants a world with one dark biome and no standard Minecraft
blocks, while the engine and development tools still depend on vanilla registries.

Decision: Keep vanilla registries intact and create the dedicated
`gravesown:after_the_silence` preset. Its Overworld uses only
`gravesown:sown_grave`; generated player-facing blocks and fluids must be
Gravesown-owned, with a narrow technical air allowlist. Development is
new-world-only until the schema stabilizes.

Consequences: A real chunk audit is required before worldgen milestones can be
called complete. Nether and End survival access remains unavailable until their
own replacement milestones. The editable detail plan lives in
docs/TOTAL_CONVERSION_PLAN.md.

## ADR-0007: Isolated real-chunk world audit

Date: 2026-07-12
Status: Accepted

Context: GameTest structures prove content mechanics but do not prove what the
normal noise/feature/structure pipeline generated across complete chunks.

Decision: Add an isolated ModDev dedicated-server run named `worldTest`. On
`ServerStartedEvent`, and only when that run's explicit system property is present,
the server loads real Overworld chunks to FULL status, scans every block and fluid
position, biome quart cell and block entity, writes JSON/text reports, and calls the
normal server halt path. The wrapper owns only sentinel-protected `run-worldtest/`.

Baseline enforcement records violations while TC3–TC4 are unfinished. Strict
enforcement is the release gate: content must use the Gravesown namespace, the only
biome must be `gravesown:sown_grave`, and the technical allowlist is limited to air,
cave air and the empty fluid state.

Consequences: `worldtest.cmd` is required for worldgen changes; `verify-all.cmd`
provides the vibe-coder one-button path. A normal client or server cannot trigger
the scanner. Reports under `build/reports/gravesown/world-audit/` are generated
evidence and are not committed. Test worlds are disposable and never user worlds.

## ADR-0008: Stable foundation palette and mining tiers

Date: 2026-07-12
Status: Accepted

Context: The custom world preset needs a complete non-vanilla vertical column before
noise and surface rules can safely replace the Overworld.

Decision: Reserve stable ids for `ashen_sod`, `grave_loam`, `hushstone`,
`deep_hushstone` and `gravebed`. Ashen Sod and Grave Loam are shovel-mineable and
hand-accessible. Hushstone requires a pickaxe but permits the lowest tier. Deep
Hushstone rejects wooden and gold tools and begins at stone tier. Gravebed is the
unbreakable/no-loot bottom layer. All five use simple common-side blocks with no
client-only or ticking behavior.

Consequences: Future surface/noise rules may depend on these ids without renaming.
Custom tools must honor the same block tags. Gravebed never becomes a progression
resource. Final block art is deterministic 16x16 pixel work generated by the project
script; ImageGen remains a concept tool, not a tiny seamless-tile generator.

## ADR-0009: Ribroot bootstrap flora before world generation

Date: 2026-07-12
Status: Accepted

Context: The single-biome preset needs a complete original surface vocabulary and
the first-hour loop needs wood and gatherable plants before trees/features exist.

Decision: Reserve stable ids for `ribroot_stem`, `ribroot_planks`, `veil_foliage`,
`threadgrass`, `ribroot_shoot` and `pallid_bulb`. Ribroot uses vanilla-compatible
log/plank/leaf tag contracts so leaf distance and future recipes work without custom
tick scans. All three ground plants share a no-collision server-safe block that only
survives on Ashen Sod or Grave Loam. The Shoot is tagged as a sapling but cannot grow
until TC4b supplies the reviewed configured feature. Pallid Bulb emits light level 3,
which is atmospheric rather than a replacement for the planned Tallow Lamp.

Client transparency is selected through the 1.21.1 model JSON `render_type` field;
no deprecated render-layer registration or common-side client reference is added.

Consequences: TC4b can place the six ids directly and later attach deterministic
Ribroot growth without renaming content. Plants cannot leak vanilla tall grass or
trees through bonemeal. The temporary self-drop economy keeps every block testable;
rarer foliage/seed drops may replace it when renewable world generation exists.

## ADR-0010: Vanilla-free 2x2 first-tool gate

Date: 2026-07-12
Status: Accepted

Context: A total-conversion spawn must provide a mining tool without a crafting
table, vanilla sticks, flint or pre-mined stone, while still preserving a meaningful
deep-rock progression gate.

Decision: Ribroot Planks become `ribroot_splint`; three Threadgrass become two
`thread_binding`. Two Splints and one Binding make a 48-use `crude_handpick` in the
player 2x2 grid. Its wood-level incorrect-block contract harvests Hushstone but not
Deep Hushstone. Mined Hushstone converts reversibly to four `hushstone_shard`; one
Shard, Splint and Binding make a 96-use `bound_knife`. Repair inputs are the exact
local material, not broad vanilla tags. Every recipe fits 2x2 and accepts only
Gravesown-owned ingredients.

Consequences: TC4 spawn placement only needs reachable Ribroot and Threadgrass to
unlock the first mining tier. Deep Hushstone remains reserved for the next tool tier.
The Knife uses standard sword behavior/tags and the Handpick standard pickaxe
behavior/tags, preserving mod compatibility without exposing vanilla acquisition.

## ADR-0011: Minimal one-biome preset before natural features

Date: 2026-07-12
Status: Accepted

Context: TC4 needs a save-loadable total-conversion foundation whose absence of
vanilla content can be proven before custom trees, deposits, caves and subzones add
many independent failure paths.

Decision: `gravesown:after_the_silence` initially defines only an Overworld using
a fixed `gravesown:sown_grave` biome and `gravesown:sown_grave` noise settings. A
small custom density function forms low hills. Surface rules produce Ashen Sod,
three Grave Loam layers, Hushstone, Deep Hushstone and one Gravebed floor. The
default fluid is technical air; aquifers, ore veins, carvers, placed features and
structures are empty or disabled. Nether and End stems remain absent until their
explicit TC8 replacements. Hollow Grazer is the biome's sole natural spawn entry.

`worldtest.cmd` and `verify-all.cmd` now default to this preset and strict
enforcement; baseline mode is explicit. `clienttest.cmd` copies only the sentinel-
protected audit save into a separate sentinel-protected client directory, quick-
loads it with the same seed, validates the biome and exits normally.

Consequences: TC4a terrain is intentionally barren but safe and mechanically
auditable. TC4b must add only Gravesown-owned Ribroot and bootstrap-flora features.
Existing saves are never converted. The preset, biome, density/noise and terrain
block ids are save-facing and must not be renamed casually.
