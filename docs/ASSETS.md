# Asset pipeline

## Visual target

- Vanilla-readable Minecraft proportions.
- Base item/block resolution: 16 by 16.
- Entity sheets use the exact UV dimensions required by their models.
- Familiar daylight values and natural local biome palettes; no global gray/pink/cyan
  grade and no repeated brick/stripe template on organic terrain.
- The cold navy/steel/cyan palette is presentation-only: GUI, HUD, menu backgrounds
  and launcher. It is never applied to gameplay textures or biome effects.
- Dark soil, bruised flesh, old bone and sickly accents remain local materials rather
  than a desaturation filter over every asset.
- Horror through silhouette and implication, not photorealistic gore.

## Image generation policy

Image generation is good for:

- mood and palette exploration;
- creature silhouette variants;
- broad anatomy ideas;
- splash art and larger promotional concepts.

It is weak for:

- exact 16 by 16 pixels;
- stable UV placement;
- seamless tiles;
- matching animation frames;
- reproducible tiny details.

Therefore the approved workflow is hybrid:

1. Save the prompt under art/generation-prompts/.
2. Save generated concepts under art/concepts/.
3. Select only an original idea, never another mod's asset.
4. Rebuild the final texture on a controlled pixel grid.
5. Check dimensions, alpha and nearest-neighbor scaling.
6. Test the PNG on the real model in game.

## Paths

- Editable sources: art/source/
- Generated concepts: art/concepts/
- Generation records: art/generation-prompts/
- Final shipped textures: src/main/resources/assets/gravesown/textures/
- Models and definitions: src/main/resources/assets/gravesown/models/

## Naming

Use lowercase snake_case. Entity texture example:
textures/entity/hollow_grazer.png

Do not overwrite a concept without retaining its prompt or source record. Do not
ship watermarks, third-party copyrighted assets or generated text artifacts.

## Current alpha assets

- `textures/entity/hollow_grazer.png` — fully redrawn original 128 by 128 controlled
  pixel sheet generated deterministically by `scripts/generate-dev-art.ps1`; the
  historical four-creature client lineup tracking/capture passes.
- `gravesown.png` — original 64 by 64 pixel mod icon from the same script.
- Nine original 16 by 16 item icons cover the five Hollow Grazer resources and the
  four fully redrawn Quietskin armor silhouettes.
- `textures/models/armor/quietskin_layer_1.png` and `quietskin_layer_2.png` are
  fully redrawn original 128 by 128 controlled UV sheets for Minecraft 1.21.1 armor
  rendering. Four slot-specific client model layers add 49 volumetric cubes of
  scavenged plates, straps, wraps and fittings while deliberately leaving the face
  open. Deterministic resource/model checks pass; default/slim, front/back, daylight,
  darkness and hurt-flash QA remains open.
- Six original 16 by 16 block tiles cover the first five foundation blocks:
  Ashen Sod has separate top/side textures and Grave Loam doubles as its bottom.
  Hushstone, Deep Hushstone and Gravebed use distinct value ranges so underground
  layers remain readable without bright fantasy colors.
- Seven original 16 by 16 tiles cover Ribroot Stem sides/end, Ribroot Planks,
  Veil Foliage, Threadgrass, Ribroot Shoot and Pallid Bulb. Foliage and plants use
  audited hard-edged alpha silhouettes; their block models select NeoForge's named
  `cutout_mipped`/`cutout` render types directly in JSON.
- Five original 16 by 16 item silhouettes cover Ribroot Splint, Thread Binding,
  Hushstone Shard, Crude Handpick and Bound Knife. The two tools use vanilla-readable
  handheld perspective while keeping the Gravesown wood/stone/membrane palette.
- Six deterministic 16 by 16 regional tiles cover Rootfelt, Fibrous Loam, Scar
  Shale, Marrowstone, Suture Silt and Dried Ichor.
- Hollow Grazer, Ribspring, Stitchtusk and Woundscent use fully redrawn original
  128x128 hard-edged entity UV sheets with fully opaque multi-tone safety underlays.
  All 143 model cubes fit their declared atlas bounds. The 101 added detail cubes use
  dedicated, non-overlapping, full-footprint UV islands: 24 for Hollow Grazer, 29 for
  Ribspring, 19 for Stitchtusk and 29 for Woundscent; their distinct bone, tissue,
  sinew and accent materials are not collapsed into shared one-pixel swatches. Their
  procedural animation code now adds species-specific idle, locomotion, sensory and
  attack/telegraph accents. Resource/UV checks and exact client
  tracking plus the historical automated four-creature lineup capture pass, while natural-speed
  animation/daylight/hurt-flash review remains manual.
- Buried Remnant has an original deterministic entity sheet, while
  `remnant_grave.png` supports the closed/open directional block models. Its emergence
  pose is code-driven and must be reviewed in the client.
- Gloamwater is rendered through the translucent layer with Minecraft 1.21.1's
  still/flow/overlay sprites, while its tint, fog and registry logic remain custom.
  Gloam Muck/Sand, Threadkelp, Veilweed, Drowned Roots, Bladderpod and softly luminous
  Lumen Kelp use original hard-edged assets; the three fish keep original entity sheets
  and deterministic item icons. They are generated by `scripts/generate-aquatic-art.ps1`.
- Survivor's Codex has a deterministic 16 by 16 icon from
  `scripts/generate-guide-art.ps1`; its full-screen
  `Story / Crafts / Chain / Guide` Hub is drawn by client GUI code so exact translated
  text is never baked into a raster.
- Ribroot shelter shapes, Tallow Lantern, custom farmland states, both crop stages,
  seeds and food icons are deterministic 16x16 assets produced by
  `scripts/generate-farming-decor-art.ps1`.
- Complete Emberbark and Palevine wood families are deterministic 16x16 assets from
  `scripts/generate-wood-family-art.ps1`; every related model is audited to reference
  its own family. Ribroot uses dark walnut/moss, Emberbark warm brown/rust and Palevine
  warm ivory/khaki with olive foliage rather than cyan. Field Kitchen, utensils, three meals and Needle Sprat come from
  `scripts/generate-kitchen-art.ps1`.
- Deterministic vanilla-id HUD overrides provide Gravesown heart, hunger and XP art;
  Creative and container slot wells use their exact 1.21.1 coordinates with no
  one-pixel up-left shadow offset.
- Custom hard-edged sun and moon discs are generated with the shared GUI art pipeline;
  they replace only the celestial sprites and do not change the light engine.
- `launcher/assets/launcher_background_source.png` is the immutable approved
  1672 by 941 giant-tree composition master. `generate-presentation-background.ps1`
  derives the navy/steel/fog-blue `launcher_background.png` non-cumulatively and the
  same generated bitmap is copied to `title_background.png` and
  `screen_background.png`. These are deliberate large splash-art uses, not sources
  for tiny game UVs.
- Deterministic `assets/minecraft/textures/gui` overrides use exact presentation
  colors `#071423`, `#10243B`, `#294764`, `#3F6385`, `#8FAFC8`, `#4CA8E8` and
  `#D7E6F2`. They provide Gravesown
  menu/list backgrounds, continuous button/slider/text-field rails, custom lock and
  recipe-book controls, the recipe-book panel, and complete Creative tab/inventory
  families. Their `.mcmeta` scaling metadata preserves vanilla widget dimensions;
  no generated lettering is baked into them.
- `art/generation-prompts/hollow_grazer_concept.md` — preserved ImageGen concept
  prompt. Image generation is intentionally not used as the final UV texture.

## Deterministic ownership and verification

Current shipped pixel assets are reproducible through `scripts/generate-all-art.ps1`.
It uses a reviewed explicit manifest rather than alphabetical discovery:

1. immutable presentation-background derivation, then bootstrap dev, aquatic,
   farming/decor, flora, guide, kitchen, remnant, Ribspring,
   Stitchtusk, survival-station, symmetric-Quietskin, wood-family and UI passes;
2. `generate-cohesive-world-art.ps1` as the final owner of natural terrain surfaces;
3. `generate-cohesive-creature-art.ps1` as the final owner of the reviewed dry-land
   creature atlases;
4. `generate-art-language-v3.ps1` as the ownership/audit pass: world art keeps its
   authored local colors, while exact UI and presentation assets are protected from
   a second grade.

The order is part of the asset contract: legacy/bootstrap scripts may emit an earlier
version, but may not win over the final family owner. The generators write fixed pixels
rather than asking an image model to guess exact Minecraft UV coordinates.

Run `artcheck.cmd` after changing art or model texture references. It validates native
dimensions and every Gravesown block/item texture referenced by model JSON, and writes
terrain/entity contact sheets under `build/reports/gravesown/art/` for fast comparison.
Release-candidate art also requires two consecutive complete regenerations with identical
hashes for shipped textures.
