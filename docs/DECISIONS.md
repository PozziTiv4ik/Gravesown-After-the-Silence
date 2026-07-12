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
