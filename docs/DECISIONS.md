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

## ADR-0012: Four-biome branded survival build supersedes the uniform alpha target

Date: 2026-07-12
Status: Accepted
Supersedes: the final one-biome target in ADR-0006; TC4a remains valid evidence

Context: In-game review proved the minimal generator technically clean but too flat
and empty, and the alpha Quietskin art did not meet visual quality. The project also
needs first-session guidance and a recognizable Windows entry point to behave like
a complete modpack rather than a developer-only mod.

Decision: Keep `gravesown:after_the_silence`, but migrate its Overworld to four
Gravesown-owned biomes: Sown Grave, Ribroot Groves, Marrow Rifts and Suture Mire.
Each requires a distinct surface palette, terrain silhouette, natural features and
native creature. The audit changes from one required biome to an exact reviewed
allowlist plus required-presence checks across Full seeds.

Quietskin UVs and icons are replaced, not patched around. A first-join field guide
and a Gravesown advancement tree teach only implemented survival steps. A branded
Java 21 Windows launcher may start the already cached development client and bundle
its own launcher runtime, but it may not bypass Microsoft authentication, distribute
Minecraft binaries or silently download unreviewed executables. The main menu and
launcher share one original background illustration.

Consequences: TC4a saves remain disposable and will show intentional chunk borders
after the biome-source migration. Entity definition of done still applies separately
to all three new creatures. The launcher is a project client front-end, not an
unofficial authentication service. Strict world verification must prove all four
biomes without weakening the no-vanilla block/fluid contract.

The shared 1672x941 launcher/title illustration is an approved ImageGen use because
it is large mood artwork, not a UV-constrained game texture. Exact block, item,
armor and entity sheets remain deterministic pixel art generated by project scripts.

## ADR-0013: Filter vanilla advancements with a built-in top data pack

Date: 2026-07-12
Status: Accepted

Context: The Survivor's Codex and Gravesown progression tree must be the only
first-session route presented by the total conversion. Overriding individual vanilla
advancement files would be brittle and incomplete, while a screen Mixin would couple
the mod to client implementation details and would not change server authority.

Decision: Register an always-enabled built-in server data pack at top priority through
NeoForge's `AddPackFindersEvent`. Its standard pack filter blocks lower resources in
the `minecraft` namespace whose path matches `advancement/.*`; the seven Gravesown
advancements remain ordinary `gravesown` data. Do not delete registries, patch the
advancement screen or replace dozens of vanilla JSON files.

Consequences: Integrated and dedicated data reloads expose only the reviewed seven-
step Gravesown tree from this project, and the server owns progress. The filter also
rejects any third-party advancement deliberately published into the `minecraft`
namespace, so compatibility work must use a separate later decision rather than
silently weakening this total-conversion rule. A GameTest must prove vanilla
`story/root` is absent and Gravesown `survival/root` remains; startup logs must load
seven advancements without data errors.

## ADR-0014: Open the custom Codex through the vanilla written-book protocol

Date: 2026-07-13
Status: Superseded by ADR-0016

Context: Giving an item with `WrittenBookContent` was not sufficient to make a custom
`WrittenBookItem` subclass open reliably on right-click. The onboarding grant also
missed players who entered a world in Creative and only later changed to Survival.
Both behaviors must be server-authoritative, dedicated-server safe and duplicate-
free without replacing the vanilla book GUI.

Decision: `SurvivorCodexItem.use` resolves the stack's written-book components on the
server, broadcasts any resulting inventory change and sends the standard
`ClientboundOpenBookPacket` for the used hand. Keep the vanilla written-book screen;
do not add a client-only Codex GUI or a Mixin. Run the same persistent exactly-once
grant path on both player login and a game-mode transition whose new mode is
Survival. Set the attachment only after the Codex entered the inventory or was safely
dropped for the player.

Consequences: First-login and Creative-to-Survival onboarding behave identically on
an integrated or dedicated server, reconnects and repeated mode changes do not create
duplicates, and localized pages open in a familiar vanilla UI. GameTests must assert
both grant paths, persistence and the outbound packet; integrated client smoke must
capture the actual open 11-page screen from the one stack auto-granted during a real
fresh Survival login. The smoke harness may not create its own Codex.

## ADR-0015: Give every added entity detail its own UV footprint

Date: 2026-07-13
Status: Accepted

Context: Richer model geometry does not improve the visual result when small cubes
sample one-pixel color swatches or overlap unrelated UV regions. Such shortcuts hide
material variation, make later repainting unsafe and can produce transparent faces
even though a renderer-instantiation smoke test passes.

Decision: Standardize the four current creature atlases at 128x128. Every added
detail cube receives a dedicated, non-overlapping, full-footprint UV island; base
geometry may retain intentional established layout. Fill otherwise unused pixels with
an opaque multi-tone safety underlay rather than transparency, and paint dedicated
islands with material-appropriate bone, tissue, sinew or accent values. Keep a static
audit that checks every cube against atlas bounds and checks the added-detail islands
for footprint size and overlap.

Consequences: The current audit covers 143 total cubes and 101 added detail cubes:
24 Hollow Grazer, 29 Ribspring, 19 Stitchtusk and 29 Woundscent. Future model detail
must satisfy the same ownership rule before visual acceptance. Opaque underlay is a
safety net, not final art: visible model faces still require intentional island art
and in-client review. Texture generators remain deterministic and must pass two
consecutive hash-stable regeneration runs after atlas changes.

## ADR-0016: Open the Codex from a server-owned advancement snapshot

Date: 2026-07-13
Status: Accepted
Supersedes: ADR-0014 presentation and packet choice; its exactly-once grant rule remains

Context: The vanilla written-book presentation was functional but could not provide
the requested two-page quest map, card states or a coherent total-conversion visual
language. The seven advancements are server-owned, so a client must not infer or
write progress when opening the guide.

Decision: Keep the persistent exactly-once first-Survival grant from ADR-0014. On
item use, compute a seven-bit completion mask from the player's server advancement
state and send `gravesown:open_survivor_codex` as a play-to-client NeoForge payload
under protocol version `1`. The client opens `SurvivorCodexScreen`, which renders two
chapters and seven ordered cards as complete, current or locked. No progress mutation
is accepted from the client and no Mixin replaces a vanilla screen.

Consequences: Dedicated and integrated servers remain authoritative, packet data is
small and versioned, and the visual screen can evolve independently of advancement
storage. EN/RU text and card ordering must stay aligned with the seven server ids.
GameTests assert the mask and outbound custom payload; final layout and navigation
still require manual client QA.

## ADR-0017: Render Quietskin through slot-specific client armor extensions

Date: 2026-07-13
Status: Accepted

Context: Repainting the two flat worn UV sheets could not create the layered,
scavenged silhouette requested for Quietskin. Gameplay attributes and Dead Scent are
server concerns, while armor geometry is presentation-only.

Decision: Register client item extensions through the pinned NeoForge
`RegisterClientExtensionsEvent`. Supply four slot-specific `HumanoidModel` layers
for hood, coat, legwraps and boots, totaling 49 cubes parented to normal humanoid
bones. Keep the face open and use original layered hide, plate, strap, wrap and raised
fitting forms rather than copying a named film or armor design.

Consequences: Standard player posing still drives the equipment, common gameplay
code stays free of client imports, and each slot can hide unrelated model parts.
Default and slim player models, front/back views, daylight, darkness and hurt flash
are required manual acceptance cases after deterministic model/UV checks pass.

## ADR-0018: Make the first grave a persistent one-shot feature encounter

Date: 2026-07-13
Status: Accepted; emergence timing refined by ADR-0024

Context: A rare grave should create a readable ambush without becoming a renewable
loot container, reset exploit or ticking block entity. The emerging creature must
not attack invisibly before its animation reaches the surface.

Decision: Place `remnant_graves_rare` in Sown Grave at a 1/64 attempt rate through a
custom feature made only from reviewed Gravesown terrain blocks and one
`remnant_grave`. Store interaction state in its `OPENED` block property. First
server-side use creates exactly one persistent Buried Remnant; only a successful
entity insertion marks the grave opened, and later use consumes without loot or
reset. A synced, saved 36-tick emergence counter disables AI, horizontal movement,
targeting, damage and pushing on the server while the client model rises and
straightens.

Consequences: No block entity or per-tick world scan is needed. The saved block and
entity state survive restart, dedicated-server rules match singleplayer, and the
feature cannot be farmed by repeated clicks. Registration/interaction/emergence,
loot and placement contracts require GameTests; natural rarity and animation feel
remain manual checks.

## ADR-0019: Introduce aquatic ecology with a Gravesown-owned placed fluid

Date: 2026-07-13
Status: Accepted

Context: The fluidless foundation proved the total-conversion contract but left the
world without lakes or aquatic life. Vanilla water would violate the world identity
and strict namespace audit, while re-enabling aquifers would add uncontrolled
providers across every biome.

Decision: Register the `gloamwater` fluid type, source, `flowing_gloamwater`, liquid
block and bucket. Tag the fluid family for standard water movement semantics while
keeping every registry id and texture Gravesown-owned. A custom production pond
feature places small source basins only in Suture Mire at a 1/14 attempt rate and
adds the custom Threadkelp plant. Rotfin is a complete `WATER_CREATURE` vertical
slice with server AI, attributes, biome spawn, renderer/model/original texture, loot,
egg, EN/RU and summon/test paths.

Consequences: The world audit continues to allow technical air and Gravesown fluids,
not vanilla water. Ponds remain controlled features rather than aquifers. A direct
production-carver GameTest proves a pond independently of natural rarity, while
strict world audit checks namespace purity and manual play checks frequency, visual
edges, plant density and Rotfin movement.

## ADR-0020: Theme selected vanilla screens through isolated client presentation hooks

Date: 2026-07-13
Status: Accepted

Context: The title screen and launcher alone did not make the client feel cohesive,
but replacing each vanilla screen or mixing into UI implementation would duplicate
navigation, harm accessibility and couple gameplay to fragile client internals.

Decision: Override the standard widget/menu sprites with deterministic hard-edged
resources and apply the original 1672x941 infected-lake background only to a reviewed
allowlist of non-gameplay screens. Use the pinned, deprecated NeoForge
`ScreenEvent.BackgroundRendered` in one `dev.gravesown.client.GravesownScreenTheme`
subscriber; do not alter inventory/HUD screens or vanilla screen behavior. Keep this
hook presentation-only and isolated as the single migration point for a future API
upgrade. Large background art may use ImageGen under ADR-0003; exact widgets remain
deterministic pixel art.

Consequences: World selection, options and loading/connect screens retain vanilla
controls and accessibility while sharing the Gravesown composition. The deprecation
is consciously contained and does not justify a Mixin. Common code and dedicated
servers do not load the theme. Offscreen/static checks cover assets, but common
window sizes and every allowlisted screen require manual client acceptance.

## ADR-0021: Store Survival Hub progression as explicit server conditions and claims

Date: 2026-07-13
Status: Accepted; supersedes ADR-0016 for progression storage and presentation

Context: Ordinary advancements completed immediately and could not express the
requested ready-then-Claim flow. The two-page Codex also constrained navigation and
mixed quest guidance with recipes.

Decision: Persist separate seven-bit condition and claimed masks as copy-on-death
player attachments. Inventory/equipment checks and server events may mark conditions;
only a serverbound Claim payload may request completion. The server refreshes the
condition, validates the predecessor and rejects duplicate claims before persisting
and returning the versioned state. Present it through one full-screen, pannable and
zoomable Survival Hub. Keep both vanilla and Gravesown progression out of the
ordinary advancement tree.

Consequences: A client can neither forge a condition nor skip order. Quest state is
dedicated-server safe and independent of UI layout. New quests require a stable index,
condition source, EN/RU text, claim test and recipe-guide entry when relevant.

## ADR-0022: Give early containers one code-rendered Gravesown skeleton

Date: 2026-07-13
Status: Accepted; narrows the inventory restriction in ADR-0020

Context: Decorating unrelated vanilla bitmaps did not produce a coherent inventory,
4x4 bench and kiln. Pitch Kiln shared the vanilla furnace menu id, preventing a custom
screen registration without replacing every furnace screen.

Decision: Keep vanilla slot behavior but render panels, complete borders and slots
through `GravesownContainerStyle`. Replace only the ordinary Survival inventory at
`ScreenEvent.Opening`; Creative remains untouched. Give Pitch Kiln a dedicated
`PitchKilnMenu` id while retaining `AbstractFurnaceMenu` server behavior and the
smelting recipe book. Gravework remains its custom server-authoritative 4x4 menu.

Consequences: Common/server code has no client imports, standard item movement and
recipe logic remain intact, and all three screens share one visual source. Client
layout still needs a consolidated normal-scale visual pass.

## ADR-0023: Use the loader-supported dark bootstrap and a reduced launcher

Date: 2026-07-13
Status: Accepted

Context: The bright red window in the report is created by FML before mod classes or
resources exist, so a normal mod cannot safely brand it. The previous launcher also
spent most of its area on chapter rails and build-status cards.

Decision: Set `FML_EARLY_WINDOW_DARK=1` in project client scripts, using FML's own
supported early-window scheme until Gravesown resources load. Do not ship a loader
replacement or Mixin. Reduce the launcher to centered title, animated Play, Verify,
Logs and bottom console while retaining cached ModDev startup and process guarding.

Consequences: The unsupported red bootstrap is removed from project launches without
coupling Gravesown to loader internals. The earliest window is black rather than fully
branded; original Gravesown artwork begins at the first resource-controlled screen.

## ADR-0024: Slow grave emergence and keep aquatic targets inside depth bands

Date: 2026-07-13
Status: Accepted

Context: A 36-tick grave rise read as a jump rather than a creature digging upward.
The first shared fish controller also applied constant upward velocity and accepted
targets at exposed fluid surfaces, producing surface crowding and tight circles.

Decision: Extend the saved, synced Buried Remnant emergence to 108 server ticks while
preserving the one-shot grave and all server-side invulnerability/AI locks. Remove
artificial fish buoyancy. Choose wander targets only inside connected Gloamwater with
horizontal water neighbors and a wider arrival radius; bias Rootskimmer toward the
lower water band without forcing a vertical velocity.

Consequences: Grave pacing is three times slower without moving authority to the
client. Fish still use bounded server AI but no longer seek the air boundary by
construction. GameTests cover real-fluid survival/displacement and emergence locks;
natural schooling and animation feel remain player-observed acceptance cases.

## ADR-0025: Derive the Survival Hub craft guide from synchronized recipes

Date: 2026-07-13
Status: Accepted

Context: A hand-maintained recipe-card list could drift from JSON, omitted the reverse
Hushstone recipe, and could describe an item that the active server could not actually
craft. The guide also needed exact layouts, text search and prerequisite navigation
without moving recipe authority to the client.

Decision: When the Hub initializes, build its Gravesown-only catalog from the current
client connection's synchronized `RecipeManager`. Normalize vanilla shaped/shapeless,
custom Gravework and cooking recipes into presentation entries while retaining output
counts and exact station grids. Search localized output names and registry ids. Build a
client-only, cycle-safe prerequisite path from recipe outputs, treating mined Hushstone
as the acquisition root of its reversible shard conversion. Keep crafting and all
inventory mutation in the existing server-authoritative menus.

Consequences: New data-driven Gravesown recipes appear without a second GUI registry,
and a multiplayer client documents the recipes supplied by its server. Alternative
tag ingredients currently display the first synchronized choice. Integrated-client
smoke asserts the reviewed 33-entry catalog, localized search, exact Handpick grid and
six-node path; manual acceptance still covers pointer/keyboard feel and long names.

## ADR-0026: Present creation dependencies as a separate branching graph

Date: 2026-07-13
Status: Accepted; refines ADR-0025

Context: A single horizontal prerequisite strip falsely implied that unrelated natural
materials belonged to one linear process and could not scale to deep recipes. Players
also needed a direct recipe lookup from ordinary container screens.

Decision: Add `Chain` as the third Survival Hub mode. Derive a client-only, cycle-safe
dependency DAG from the server-synchronized recipe catalog, retain independent roots,
auto-fit it on open and support bounded pan/zoom. Craftable nodes switch to their exact
`Crafts` entry. Register `R` as a GUI key; hovering a container item sends an empty
serverbound request, the server verifies the player owns the Codex and returns the
current authoritative masks, and the client opens the requested graph. Advance the
payload protocol to 3.

Consequences: Recipe and progression authority remain on the server while navigation
scales to recipes with multiple branches. The integrated-client harness verifies the
Handpick's six nodes, five edges and server request path; arbitrary deep graphs still
need player-driven readability review as content grows.

## ADR-0027: Filter every vanilla recipe in total-conversion play

Date: 2026-07-13
Status: Accepted

Context: Removing vanilla blocks and fauna while retaining vanilla crafting allowed
unreviewed recipes to leak back into progression and the recipe book.

Decision: Extend the existing always-on high-priority built-in data-pack filter from
`minecraft:advancement/.*` to `minecraft:recipe/.*`. Keep all intended survival recipes
under the `gravesown` namespace and avoid a recipe-manager Mixin.

Consequences: The active catalog contains the reviewed 33 Gravesown recipes and zero
vanilla recipes. GameTest asserts that `minecraft:stick` is absent while
`gravesown:crude_handpick` remains available. Any future vanilla-derived behavior must
be expressed by an explicit Gravesown recipe rather than an exception to the filter.

## ADR-0028: Keep Gloamwater distinct but match vanilla movement semantics

Date: 2026-07-13
Status: Accepted

Context: The custom fluid was opaque, flowed too slowly and used a solid render layer,
making ponds read as dark floor tiles rather than water.

Decision: Keep separate Gravesown source, flowing, block and bucket ids, but use
vanilla-water density, viscosity, source conversion, slope distance, level loss and
five-tick pacing. Register both fluid variants as translucent on the client and generate
hard-edged alpha-bearing still/flow sheets at vanilla animation cadence.

Consequences: Server flow and shoreline behavior are familiar without weakening the
no-vanilla-fluid world contract. Transparency remains an original Gravesown tint and
texture. Strict world audit and aquatic GameTests cover ids/behavior; final appearance
in natural deep water remains a manual check.

## ADR-0029: Theme vanilla GUI through deterministic sprites and accept at FHD

Date: 2026-07-13
Status: Accepted; extends ADR-0022

Context: Vanilla options, recipe-book and Creative screens exposed gray controls and
icons, while nine-slice edge ornaments produced dangling vertical fragments and gaps
around slider handles. Repeated 854x480 launches distorted design decisions and spent
unnecessary time and memory.

Decision: Override the exact vanilla GUI sprite ids for continuous widget rails,
full slider handles, custom locks, recipe-book controls/panel and Creative backgrounds,
tabs and scrollers. Generate all assets deterministically with hard pixel edges. Use
automated checks during iteration and at most one final 1920x1080 client acceptance run;
854x480 is only an explicit compact-layout regression.

Consequences: Unmodified vanilla screen logic receives the Gravesown visual language
without a Mixin. Resource dimensions and JSON are cheap to audit; interactive hover,
narration and unusual modded screens remain player-observed acceptance cases.

## ADR-0030: Separate Codex browsing into four stable modes

Date: 2026-07-13
Status: Accepted; extends ADR-0021, ADR-0025 and ADR-0026

Context: Recipe search, long dependency graphs and explanatory survival guidance have
different navigation needs. Combining them in one page produced empty space and made
future progression harder to browse.

Decision: Keep the server-owned quest state and synchronized recipe catalog, but render
four client presentation modes: `Story`, `Crafts`, `Chain` and `Guide`. Crafts owns six
local presentation filters. Chain and container-hover `R` always reset that filter to
All before selecting the target recipe. Guide owns scrollable, localized topic cards
and does not grant items or mutate progression.

Consequences: The Codex can grow without duplicating recipe authority or making guide
text part of saved state. Every new recipe needs a category; every new guide topic needs
EN/RU text. The FHD smoke validates all four modes, category reset and the current five
Guide topics.

## ADR-0031: Enforce the total-conversion new-world contract through public screen state

Date: 2026-07-13
Status: Accepted

Context: Exposing vanilla presets, a bonus chest or disabled structures can create a
world that violates the total-conversion contract before gameplay starts.

Decision: Tag only After the Silence as the normal/extended selectable preset. During
Create World screen initialization, select that preset and enforce structures enabled
and bonus chest disabled through the public UI state/listener. Remove or disable the
irrelevant controls through NeoForge `ScreenEvent` widget access. Do not use a Mixin.

Consequences: Ordinary client creation has one supported world path while dedicated
servers and existing saves remain data-driven. Automated state checks cover the preset
contract; final localized pointer/narration behavior remains a player acceptance case.

## ADR-0032: Make early farming depend on Gloamwater server-side

Date: 2026-07-13
Status: Accepted

Context: Vanilla farmland hydration would make ordinary water a valid progression
resource and bypass Gravesown's fluid ecology.

Decision: Till Ashen Sod or Grave Loam through `BlockToolModificationEvent` into
Gloam Farmland. Its server random tick searches only for the registered Gloamwater
fluid family, and both custom crops require that farmland. Gravebloom Dust grows the
custom crops through the public `BonemealableBlock` contract.

Consequences: Farming, hydration and crop growth remain server-authoritative and work
on dedicated servers without world scans. Tests prove Gloamwater hydrates, vanilla
water does not, both crops grow and the new food/shelter recipes remain Gravesown-only.

## ADR-0033: Expand the public Overworld to seven biomes and complete every wood family

Date: 2026-07-14
Status: Accepted; supersedes the five-biome count in earlier TC4 decisions

Context: Five regions still left large stretches visually repetitive, while adding a
tree without its building shapes would create decorative dead ends and repeated work.

Decision: Add Ember Thicket and Pallid Weald to the reviewed multi-noise source. Give
each a unique surface pair, vegetation and creature population. A new public wood type
is complete only when stem, planks, stairs, slab, fence, gate, door, trapdoor, foliage,
shoot, loot, tags, recipes, EN/RU text and generated art exist. Functional recipes may
accept the exact `gravesown:planks` tag; color-specific decoration may not mix species.
All early wood families share Ribroot Splints rather than inventing cosmetic handles.

Consequences: The world preset and strict audit now expect exactly seven Gravesown
biomes. Future tree requests inherit the complete-family requirement automatically.
The fixed three-seed Full audit must observe the union of all seven climates.

## ADR-0034: Keep Gloamwater logic custom while reusing vanilla water animation sprites

Date: 2026-07-14
Status: Accepted; supersedes the texture-sheet part of ADR-0028

Context: Repeated custom wave sheets still flickered and did not read as one continuous
fluid, even though the underlying flow behavior was correct.

Decision: Preserve all Gravesown fluid ids, buckets, server physics, tint and fog, but
bind still, flowing and overlay rendering to Minecraft 1.21.1 water sprites. Shape pond
and sea beds with Gloam Muck and Gloam Sand, and distribute aquatic plants by bounded
grid candidates rather than independent dense random carpets.

Consequences: Motion and transparency inherit the familiar Minecraft cadence without
allowing `minecraft:water` into generated chunks. The strict namespace audit remains
unchanged; final visual identity comes from tint, environment, floor materials and
original ecology rather than a competing water animation.

## ADR-0035: Make ruins, storage and cooking server-authoritative vertical slices

Date: 2026-07-14
Status: Accepted

Context: Empty fields needed low-frequency exploration rewards, and the first food
system needed more interaction than another furnace recipe.

Decision: Generate rare procedural ruined shelters from Gravesown blocks in three
timber variants. Their Reliquary Crate owns a dedicated menu type, 36 slots and a
Gravesown-only loot table. Field Kitchen recipes contain three ordered ingredients,
consume one Gloamwater bucket while returning its container, and damage persistent
Bone Cleaver/Stirring Hook utensils on the logical server. Replace built-in fishing
loot with Needle Sprat for the current conversion slice.

Consequences: Dedicated servers own inventory mutation, tool wear and loot. The crate
must never reuse a vanilla menu registration, and future kitchen recipes remain
data-driven under the custom serializer rather than special-cased screen code.

## ADR-0036: Grow Codex progression monotonically and allow pinned dependency browsing

Date: 2026-07-14
Status: Accepted; extends ADR-0021, ADR-0025 and ADR-0030

Context: Seven onboarding goals ended before the new stations, food and exploration
content. Inspecting a side ingredient also replaced the active dependency graph when
the player wanted to preserve a difficult target.

Decision: Expand the stable server-owned quest indexes to 14 monotonic conditions,
ending at the Reliquary Crate. Add Cooking and Exploration guide topics. Chain keeps a
separate selected target and pin state; while pinned, inspecting a node may open its
Crafts entry but cannot replace the stored graph. `R` explicitly resets the pin and
opens the requested target from authoritative Codex state.

Consequences: Old claimed bits keep their meaning, multiplayer clients cannot forge
completion, and long creation routes remain navigable. New goals require a stable
index, server condition, EN/RU text and a claim GameTest.

## ADR-0037: Separate deep world truth from wide macro-climate coverage

Date: 2026-07-14
Status: Accepted

Context: Expanding the seven public climate regions to roughly four times their
prototype span makes exploration more coherent, but a contiguous 17x17-chunk audit
window can intentionally remain inside one region. Enlarging the deep scan enough to
cross every climate would generate thousands of disposable chunks per seed.

Decision: Set both climate density functions to one-quarter of their former X/Z
frequency. Keep the Full audit's 17x17 FULL-chunk scan as the authority for blocks,
fluids, block entities and generated terrain, then add a separate uncached biome-source
grid across +/-128 chunks at four-chunk intervals. The wide probe must never request a
chunk. Record it separately in world-audit schema 3 and combine only biome coverage.
Remove natural Emberbark/Palevine shoot patches. Run the Gloam Sea growth feature once
per water-bearing biome chunk on its true chunk origin with one candidate per 4x4 cell.
Give every biome identical low-weight Veilfin/Rootskimmer ambient entries, while the
Gloam Sea retains its larger schools and the Mire/Sea retain Rotfin predators.

Consequences: Large regions remain testable without weakening the no-vanilla content
contract or wasting disk/RAM. Reports distinguish generated-chunk evidence from climate
eligibility. Player-grown shoots stay valuable, and lakes crossing biome borders receive
predictable ecology instead of alternating empty chunks.

## ADR-0038: Normalize every Gloamwater bed and probe every aquatic grid cell

Date: 2026-07-14
Status: Accepted; refines ADR-0034 and ADR-0037

Context: A single sampled column per 4x4 cell could hit dry shore even when another
column contained valid water. The old floor whitelist also omitted ordinary regional
surface blocks, leaving large lakes and biome-border water bodies with exposed land
materials and irregular or absent flora.

Decision: Run the Gloam Sea growth feature once per eligible generated chunk, never as
a tick scan. Inspect all 256 columns and convert any reviewed natural regional block
directly beneath Gloamwater into deterministic 4x4 patches of Gloam Sand (dominant) and
Gloam Muck. Define the replaceable floor set through a Gravesown block tag. Then process
all sixteen chunk-aligned 4x4 ecology cells. For each cell, probe all sixteen columns in
a deterministic shuffled permutation and place at most one of the five aquatic plants
when its water/depth/survival rules allow.

Consequences: Ponds, lakes and oceans use one floor/ecology contract regardless of
their biome, size or shoreline shape. An eligible cell no longer fails because its one
random sample landed on shore, while the hard maximum of sixteen plants per fully wet
chunk prevents carpets. Existing chunks are not rewritten. Adding a natural regional
floor requires updating the reviewed tag, and changes require Aquatic GameTests plus a
strict generated-world audit.

## ADR-0039: Adopt Texture Language V2 and explicit art-generator ownership

Date: 2026-07-14
Status: Accepted; refines ADR-0003, ADR-0008, ADR-0015 and ADR-0033

Context: Tester review identified a gray, washed-out global color grade, tiring
pink/cyan regions and repeated brick/stripe patterns across unrelated natural blocks.
Increasing every terrain texture to 32x32 would preserve those design faults while
moving the world away from Minecraft's native material scale. Alphabetical generator
discovery could also let a legacy bootstrap pass silently overwrite a newer family pass.

Decision: Keep terrain and ordinary block-family textures at 16x16. Use 32x32 items or
64x64/128x128 entity and armor sheets only when silhouette or UV ownership needs the
space. Texture Language V2 starts from familiar Minecraft daylight/value separation,
uses local natural biome palettes and forbids one global gray, pink, purple or cyan
wash. Natural soil, stone, bark and foliage use irregular material-driven clusters,
cracks, roots and grain rather than repeated horizontal brick rails. Fog remains a
local readable atmosphere layer, not a global desaturation filter.

Each wood family owns a stable palette and every related model reference is audited:
Ribroot is dark walnut/moss, Emberbark warm brown/rust and Palevine warm ivory/khaki
with olive foliage rather than cyan-blue. `scripts/generate-all-art.ps1` has one explicit
ordered manifest; final cohesive-world and cohesive-creature passes own the reviewed
natural surfaces and dry-land entity atlases after bootstrap generators run.
`artcheck.cmd` enforces native dimensions and model texture resolution and produces
terrain/entity contact sheets. Release-candidate art requires two hash-identical full
regenerations before the final FHD client smoke.

Consequences: Higher resolution is no longer treated as a universal quality switch;
material design, palette and UV ownership are reviewed independently. Generator order
is architecture rather than incidental filename sorting, and duplicate output is safe
only when the declared final owner wins. Exact UV art stays deterministic; ImageGen
remains appropriate for large mood/background work and concept exploration, not final
terrain tiles or entity atlases.

## ADR-0040: Expand to nine biomes and seven complete wood families

Date: 2026-07-14
Status: Accepted; supersedes the seven-biome count in ADR-0033

Context: Four-times-broader climate regions improved exploration scale, but the two
dry low-temperature bands still needed distinct woodland destinations. Marrow Rifts
and Suture Mire also needed native woody silhouettes and local material variety rather
than sharing the same sparse vegetation language.

Decision: Add `mosswake_woods` and `amberquiet_grove` to the exact reviewed
multi-noise allowlist, bringing the public Overworld to nine biomes. Both reuse Ashen
Sod over Grave Loam so their readable turf remains familiar; Mosswake/Sunveil trees,
Mossveil/Amber Bloom and local population tables create their identity. Add bounded
original Cairnwood shrubs to Marrow Rifts and four-root Suturewood trees to Suture
Mire. Marrow Rifts also receive deterministic Veined Shale, Splintered Marrowstone and
Cairnstone disks.

Every public wood family now includes stem, ordinary planks, cut planks, stairs, slab,
fence, gate, door, trapdoor, foliage and shoot plus assets, loot, recipes, tags and
EN/RU. The seven reviewed species are Ribroot, Emberbark, Palevine, Cairnwood,
Suturewood, Mosswake and Sunveil. All foliage uses `FallingLeafBlock`, whose sparse
client `animateTick` particle samples the owning leaf state; future tree families
inherit this behavior without a server scan.

Consequences: Strict world verification expects exactly nine biomes, while the broad
climate frequency remains unchanged. New tree requests automatically include the full
family, cut-plank and falling-leaf contracts. Reusing Ashen Sod is intentional material
continuity, not missing biome content.

## ADR-0041: Separate rough planks, cut planks and glass tempering into server recipes

Date: 2026-07-14
Status: Accepted

Context: A visually calmer Minecraft-adjacent plank needed a meaningful processing
step without duplicating material, and glass needed a local total-conversion path with
a durable recoverable upgrade.

Decision: Register a dedicated one-input Sawmill menu and data-driven recipe type.
Each of the seven recipes consumes one species' ordinary plank and returns exactly one
cut plank of the same species. It may not recolor wood or act as a multiplication
recipe. Smelting Gloam Sand produces ordinary Gravesown Glass; smelting that glass
again for a longer cycle produces Tempered Glass. Ordinary glass uses hardness 0.3 and
requires Silk Touch to drop. Tempered Glass uses hardness 0.9, three times the base,
and drops itself through ordinary block loot.

Consequences: The synchronized Codex catalog can present Sawmill recipes through the
same recipe-manager authority as other stations. Future wood families require their
own 1:1 Sawmill recipe. Glass progression stays inside Gravesown resources and does
not expose vanilla sand or crafting.

## ADR-0042: Sticky surfaces slow travel without removing the normal jump

Date: 2026-07-14
Status: Accepted

Context: Reduced block jump factors made Dried Ichor and other sticky surfaces trap
the player below an ordinary one-block ledge. The intended biome identity was heavy
horizontal movement, not loss of basic traversal.

Decision: Listen for the public common-side living-jump event. When the jumper is a
player supported by a `gravesown` block whose jump factor is below one, restore the
vertical impulse from the player's jump-strength attribute and jump boost. Leave X/Z
velocity and the block speed factor unchanged. The same event path runs for logical
server authority and local prediction; non-Gravesown blocks are not modified.

Consequences: Sticky ground remains perceptibly slow while a normal player can clear a
one-block rise. The rule is bounded to the jump action and never scans entities or
chunks. Tests call the same restoration seam after vanilla jump computation.

## ADR-0043: Use organic coordinate noise for Gloamwater beds, not ecology cells

Date: 2026-07-14
Status: Accepted; refines ADR-0038

Context: Reusing the 4x4 ecology cell as the Sand/Muck material patch produced a
visible green-sand checker even though plant coverage was correctly bounded.

Decision: Keep exhaustive 4x4 ecology-cell probing for at most one plant candidate,
but choose each converted bed column through deterministic coordinate-warped,
multi-scale value noise. Large, medium and edge-detail samples form continuous organic
Muck shelves inside Sand-dominant beds and remain seamless across chunk boundaries.
The algorithm has fixed work per generated chunk and never runs as an ordinary tick.

Consequences: Aquatic density remains bounded and reproducible while floor shapes no
longer reveal the ecology grid or chunk border. Existing generated chunks are not
rewritten. Worldgen changes still require aquatic tests and strict fresh-world audit.

## ADR-0044: Art Language V3 is the final daylight-readable presentation owner

Date: 2026-07-14
Status: Accepted; supersedes the final finish in ADR-0039

Context: Texture Language V2 removed the worst gray/pink wash and masonry stripes,
but stations, UI, launcher and several creature/fish atlases still belonged to
different darkness and geometry languages. Higher resolution alone did not solve
material readability.

Decision: Keep native 16x16 block pixels and make `generate-art-language-v3.ps1` the
final deterministic raster owner after all source-family passes. Ashen Sod is the
surface benchmark: a readable green-brown top, rooted side and soil bottom. Apply the
same top/side/bottom grammar only to genuine regional turf. Use lighter local natural
palettes and three clear value bands across all seven wood families, tools, functional
blocks, containers, HUD and GUI. Code-rendered screens and the Windows launcher use a
shared forest/wood/bone palette with ochre focus, green success and restrained rust
danger; panels may not collapse into near-black cards.

Creature geometry remains centered and primarily bilateral. Aquatic models now use
paired anatomy and segmented tails, while land creatures and Buried Remnant gain
layered masses and restrained weight-bearing animation. Visible cubes retain dedicated
UV ownership and deterministic atlases. Large title/launcher mood art remains the only
shipping bitmap class that may use ImageGen; it must show readable overcast terrain and
leave quiet contrast behind centered controls.

Consequences: `generate-all-art.ps1` must run twice with identical shipped hashes,
then `artcheck.cmd` and one final 1920x1080 smoke gate the release candidate. The V3
coverage manifest must account for every shipped PNG without changing native UV size
or alpha semantics. Visual acceptance remains a human judgment recorded in STATUS,
not an automatic consequence of generator completion.

## ADR-0045: Limit the cold navy palette to presentation surfaces

Date: 2026-07-15
Status: Accepted; supersedes only the GUI/launcher palette clause of ADR-0044

Context: A cold dark-blue concept was approved for menus and the launcher, but applying
the same grade to terrain, items, creatures, armor, water and biome effects erased the
reviewed local material colors and was explicitly rejected.

Decision: Use `#071423`, `#10243B`, `#294764`, `#3F6385`, `#8FAFC8`, `#4CA8E8` and
`#D7E6F2` for GUI, HUD, containers, Codex, title/loading backgrounds and the Windows
launcher. Yellow remains a rare state/light accent. Never pass block, item, entity,
armor or biome-effect assets through this presentation grade. Keep their Art Language
V3 natural local palettes and existing Gloamwater tint/fog. Derive the large giant-tree
background from an immutable source so repeated generations cannot compound its grade.

Consequences: The interface can match the supplied cold concept without turning the
playable world monochrome. The final ownership pass records gameplay textures as
source-native and protects exact GUI/background pixels from double processing. Any
future request to recolor the world needs a separate explicit decision and visual gate.

## ADR-0046: Reframe Gravesown as a native alien ecosystem with industrial progression

Date: 2026-07-15
Status: Accepted; supersedes the infection/cleansing premise for future content

Context: The initial concept treated the world as a necrotic post-apocalypse. The
revised product direction is a crash survivor on a living alien planet, with ordinary
regional fauna alongside territorial predators, followed by a long original
industrial progression. Existing infection language would otherwise push every new
animal toward the same hostile horror archetype and make later machines feel unrelated.

Decision: *After the Silence* begins when the protagonist's ship crashes and all
communication is lost. The planet has no usable technological civilization and its
species are native animals, not zombies, parasites or recreated Earth mobs. Every
public biome targets one to three endemic species across prey, neutral fauna, birds or
small animals and a minority of predators. Existing anatomical material names remain
valid alien field terminology. Progression starts with survival stations and advances
through original mechanical processing, metallurgy, power, transport and automation.
The project may use the broad industrial-mod genre as inspiration, but may not copy
IndustrialCraft names, machines, energy units, recipes, code or assets.

Consequences: Future lore, quests, mob behavior and structures use the castaway/native
ecosystem framing. Infection-state and cleansing-loop roadmap work is retired unless a
later explicit decision introduces a different regional hazard. Creature additions
must make food webs more legible rather than merely increasing hostile spawn count;
industrial systems must extend, not skip, the established hand-crafted survival loop.

## ADR-0047: Use bounded shared fauna profiles without erasing species identity

Date: 2026-07-15
Status: Accepted

Context: Populating all nine biomes with at least fifteen additional animals as one
vertical slice would duplicate simple goal wiring across many classes or tempt a single
generic recolored entity. The first choice increases server-maintenance risk; the
second violates the requirement that every species remain visually and materially
distinct. The previously enlarged climate regions also made biome traversal drag even
after the ecosystem was populated.

Decision: Register every animal under its own permanent species id, attributes, spawn
egg, translation, renderer/model/texture and loot table. The 14 new land animals may
share the bounded `NativeFauna` implementation only through explicit prey, flyer,
neutral and predator profiles. Goals execute on the logical server, use ordinary
navigation/target selectors and never perform global entity or chunk scans. Only Rift
Puma and Reed Lynx target players by default; other predators hunt the declared native
prey set and use lower spawn weights. Silt Ray retains a separate aquatic controller.
Keep biome selection at an exact nine-entry allowlist and set warmth/wetness sampling
to 0.175, halving the linear width of the rejected over-broad climate build. Generate
the first abandoned camp through one bounded 1/192 placed-feature attempt with three
biome-paletted variants rather than an ordinary tick process.

Consequences: The expansion adds 15 complete entity vertical slices while preserving
dedicated-server authority and per-species presentation. Shared profiles are an alpha
behavior foundation, not permission to ship palette swaps: future animals still need
distinct silhouettes, materials, loot, localization and test paths. Feeding, nesting,
resting, richer food-web reactions, spawn caps and long-session pacing remain separate
TC6/TC7 work. Climate changes create expected borders only in disposable development
worlds and always require the strict nine-biome Full audit.

## ADR-0048: Keep runtime state outside Git and bootstrap authorized dependencies

Date: 2026-07-16
Status: Accepted

Context: Project-local JDKs, Gradle caches, generated clients and disposable worlds
grew the working directory to several gigabytes, consumed network repeatedly and made
an accidental oversized GitHub upload likely. A convenient release still needs a
branded launcher and a clone-to-play path, but Minecraft game binaries and modded game
distributions may not be copied into this repository or release archive.

Decision: Store Gravesown runtime state under `%LOCALAPPDATA%\Gravesown`: Java in
`runtime`, path-keyed project Gradle state in `cache\projects`,
client/server/test instances in `runs` and installation state in `state`. Reuse the standard external
`%USERPROFILE%\.gradle` dependency cache. Every non-setup Gradle path passes an
external project-cache directory plus `--offline --no-daemon`; only `setup.cmd` may
obtain the pinned Java, wrapper and NeoForge/Minecraft dependencies. `launcher.cmd`
and `play.cmd` use one bootstrap that invokes setup only when its versioned marker is
absent. Git contains original source/assets, wrapper, scripts and launcher source;
GitHub Release assets contain the release JAR plus a Windows source kit with the
packaged launcher, never Minecraft binaries, credentials, caches, logs or worlds.

Consequences: A clone and release archive remain small, one dependency installation
is reused by every working copy, ordinary play/tests cannot silently consume network,
and player saves survive repository cleanup. A genuinely fresh machine needs one
networked setup before offline play. The release ZIP is a convenient legal bootstrap,
not a redistributed standalone Minecraft client. `github-ready.cmd` and
`package-release.cmd` are required publication gates.
