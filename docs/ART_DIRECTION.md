# Art direction

This file is the binding visual contract for Gravesown. Every new or reworked
model, texture, GUI and launcher surface must be checked against it. Horror comes
from anatomy, posture and behavior, not random asymmetry or unreadable darkness.

## Art Language V3: daylight-readable Minecraft material horror

- Preserve Minecraft's block scale, hard pixel edges, square silhouettes and
  nearest-neighbor sampling. Never use filtering blur, photographic detail or
  voxel-noise confetti.
- Terrain stays on the native 16x16 grid. Raise fidelity through controlled material
  clusters, shading and geometry; 32x32 terrain is not a substitute for a coherent
  palette or pattern language. Larger sheets are reserved for entity and armor UV
  ownership where the model actually needs the space, and may not make pixels read
  smaller than Minecraft at ordinary play distance.
- Start from familiar Minecraft daylight and value separation, then give each biome
  a local natural palette. Ashen Sod is the reference turf: green-brown top clusters,
  rooted side, clear soil bottom and readable contrast under neutral daylight. Forest
  and wetland surface blocks follow that top/side/bottom grammar when they represent
  living turf; raw stone and loose sediment do not receive fake grass sides.
- Never apply one global gray, pink, purple or cyan wash to the whole world. Grave
  green, warm earth, weathered ochre, marrow ivory, dried-rust red and restrained teal
  are accents and material families, not a full-screen grade. The V3 finish is lighter
  and more chromatic than V2 without becoming candy-bright or photographic.
- Use three value groups per material: broad shadow, dominant midtone and sparse
  highlight. Reserve the darkest color for cavities and contact shadows.
- Fog is a local atmosphere layer: it may make a Mire, Sea or subzone distinct, but
  must preserve terrain silhouettes, navigation contrast and a readable daylight
  midtone. Global desaturation is not horror direction.

## Creature geometry

- New creatures are native animals of a living alien planet, not universal undead or
  infection variants. Regional palettes may use fur, hide, scale, feather, shell and
  membrane colors derived from their biome. Bone/tissue accents are species-specific,
  not a mandatory global material language.
- Primary anatomy is centered and mirrored. At least 80% of the visible mass must
  be bilaterally symmetric; deliberate damage may break symmetry only after the
  base silhouette reads clearly.
- Build from large forms first: torso, pelvis, head, wings, limbs or fins. Secondary
  forms are species-readable ears, tails, beaks, horns, plates, ribs, jaw layers or
  muscle bands. Tiny one-pixel cubes are
  forbidden unless they remain readable at normal FOV.
- Sharing a server behavior profile never permits sharing a finished silhouette.
  Every public species must remain recognizable without its texture through distinct
  body proportions and at least two anatomy cues such as gait, tail, wing, horn,
  shell, ear, beak or leg construction.
- Prefer 20–45 meaningful model parts over clouds of micro-cubes. Adjacent detail
  must share a material family and a clear attachment point.
- Use 128x128 sheets for major land creatures and 64x64 or 128x128 for small fauna.
  Every visible cube owns a non-overlapping UV region; mirrored pairs may use
  intentionally mirrored UV only when both sides are materially identical.
- Animation communicates weight: planted feet, restrained idle breathing, readable
  anticipation and recovery. No instant popping, frantic universal oscillation or
  parts moving without an anatomical cause.
- Aquatic animals obey the same construction standard. A fish needs a centered body,
  paired fins/gills/sensors and a segmented tail with depth-specific posture; a flat
  box with one oscillating fin is not a finished creature model.

## Armor geometry

- Armor is a symmetric, wearable silhouette rather than body paint. Shoulder,
  chest, waist, knee and shin layers must have visible depth and clear attachment.
- The hood remains open. Face, eyes and skin stay readable. Asymmetry is limited to
  removable props such as one pouch or repaired strap, never the core plates.
- Keep the player body proportions and animation pivots intact on both default and
  slim skins.

## Block and item textures

- Terrain remains 16x16 so it belongs beside Minecraft. Use clustered 2–5 pixel
  shapes, restrained contrast and tile-safe edges; never fill a tile with repeated
  isolated dashes. Soil, stone, bark, foliage and other natural surfaces may not use
  repeated horizontal brick rails or evenly spaced stripe bands: clusters, cracks,
  roots and grain must follow the material rather than a shared masonry template.
- Items may use 16x16 or 32x32 source art but must preserve Minecraft icon scale,
  one-pixel outline logic and a single dominant silhouette.
- A block family shares grain direction, highlight hue and crack language. Biomes
  differentiate through material families, not unrelated noise patterns.
- Every public wood species uses one shared grain/value grammar across stem, planks,
  cut planks, stairs, slab, fence, gate, door and trapdoor, and every model must
  reference its own family textures. The reviewed families are Ribroot, Emberbark,
  Palevine, Cairnwood, Suturewood, Mosswake and Sunveil. Ribroot is dark
  walnut/moss, Emberbark warm brown/rust, Palevine warm ivory/khaki, Cairnwood
  weathered gray-brown, Suturewood dark wet umber, Mosswake mossy oak and Sunveil
  warm honey/ochre. Shape changes may not invent a new palette or silently fall back
  to another species.
- Ordinary planks remain rough and visibly regional. Cut planks are the calmer,
  Minecraft-adjacent finish produced 1:1 by the Sawmill; the distinction comes from
  cleaner grain and joinery, not a color swap or a repeated brick grid.
- Doors use the same readable panel/window/hinge construction across Ribroot,
  Emberbark, Palevine, Cairnwood, Suturewood, Mosswake and Sunveil. Their item
  silhouette, upper half, lower half and trapdoor must describe one built object
  rather than cropped plank tiles.
- Every foliage block uses the species' own leaf texture for sparse client-only
  falling-dust particles. This is the default rule for future trees; effects may not
  require a server tick scan or reuse a different species' leaf color.
- Functional blocks read as constructed tools: recognizable work surface, opening,
  bands, handles or moving parts before decorative grime. Reliquary storage uses a
  barrel/chest silhouette with lid, bands and latch, not a featureless full cube.
- Glass keeps open transparent negative space. Ordinary Gravesown Glass is the lighter
  fragile sheet; Tempered Glass uses a restrained reinforced edge language while
  remaining visibly transparent.

## GUI system

- One cold presentation palette is shared by title, quest hub, recipes, inventory,
  Gravework, Pitch Kiln, Sawmill, Field Kitchen, Reliquary, HUD and launcher:
  deep navy `#071423`, dark blue-gray `#10243B`, steel blue `#294764`, selected
  blue `#3F6385`, fog blue `#8FAFC8`, cyan focus `#4CA8E8` and ice text
  `#D7E6F2`. Yellow is a rare state/light accent, never a panel family.
- This presentation palette never recolors world blocks, items, entity/armor UVs,
  biome fog/sky/water or Gloamwater tint. Gameplay art retains the natural local
  Minecraft-style material palettes defined above.
- Panels must separate from both bright and dark world scenes without collapsing
  into indistinguishable black cards. Cyan focus is precise and sparse, not bloom.
- Borders are continuous rectangles or continuous rails. Decorative lines may not
  terminate in an unexplained floating fragment.
- Buttons have one Minecraft-like nine-slice family and clear hover/disabled states.
  Avoid numbered chapter tabs, dense status cards and decorative panels with no
  gameplay purpose.
- The Survivor Codex opens as a full-screen movable canvas. Its top-level modes are
  `Story`, `Crafts`, `Chain` and `Guide`; nodes pan together, selection stays fixed in a
  side panel, and zoom is bounded for readability. `Crafts` keeps filters in a distinct
  right rail. `Chain` uses orthogonal continuous connectors, code-drawn arrowheads and
  separate branches for independent resources. `Guide` uses the same canvas language,
  a restrained topic rail and vertically scrollable cards rather than a fake book.
- Vanilla widgets overridden by the project use continuous rails with no dangling
  right-edge fragments or gaps around slider handles. Locks, recipe-book controls,
  recipe panels and Creative tabs belong to the same Gravesown pixel-art family.
- Container screens share the same slot frame, title treatment, inventory panel and
  player-inventory placement. Station-specific decoration surrounds this common
  skeleton instead of replacing it.
- Creative survival inventory art follows the exact vanilla 1.21.1 slot coordinates;
  selected tab art merges into the container edge and may not leave an underline.
- Gloamwater deliberately uses the vanilla 1.21.1 water still/flow/overlay sprites so
  animation cadence and continuity remain familiar. Its identity comes from the custom
  tint/fog, Gloam Muck/Sand beds and original plant/fish silhouettes; do not reintroduce
  a bespoke flashing wave sheet without a new reviewed ADR.

## Launcher and loading

- Launcher composition is deliberately simple: centered Gravesown title at the top,
  large animated Play button in the center, Verify and Logs directly below, compact
  console at the bottom. No chapter rail, numbered navigation or build-status card.
- Launcher/title mood art uses the approved giant dead-tree composition with a
  deterministic navy/steel/fog-blue grade. It may be painterly at its large native
  resolution, but the left/center control field must remain quiet and readable.
- Loading presentation uses the same cold interface palette and original Gravesown mark. It must
  remain readable at 854x480 and must never fall back to the bright red NeoForge
  bootstrap appearance once resources are available.
- Routine iteration does not launch Minecraft. Visual acceptance uses at most one
  final 1920x1080 client run; 854x480 exists only as an explicit compact regression,
  never as the sole design reference.

## Acceptance gate

Before an asset family is accepted, verify silhouette at 25% scale, symmetry in
front/back views, normal in-game distance, texture tiling or UV ownership, EN/RU GUI
fit and consistency with at least two neighboring assets. Deterministic generation
scripts remain the source for exact pixel resources; large background illustrations
may use ImageGen only as composition input or final non-UV mood art.

`scripts/generate-all-art.ps1` is the single deterministic regeneration entry point.
Its manifest order is explicit because later family passes own the final form of assets
that older bootstrap passes may also emit. `artcheck.cmd` must pass after an art change;
its native-size/model-reference audit and generated terrain/entity contact sheets are
the cheap review gate before the one final FHD client smoke. Release-candidate art also
requires two consecutive regenerations with identical shipped-texture hashes.

Execution evidence belongs in `docs/STATUS.md`. This contract never treats file
presence, an art-generator PASS line or a low-resolution screenshot as visual
acceptance by itself.
