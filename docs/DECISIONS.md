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
