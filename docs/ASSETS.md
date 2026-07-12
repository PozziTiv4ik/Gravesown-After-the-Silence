# Asset pipeline

## Visual target

- Vanilla-readable Minecraft proportions.
- Base item/block resolution: 16 by 16.
- Entity sheets use the exact UV dimensions required by their models.
- Dark soil, bruised desaturated flesh, old bone and restrained sickly accents.
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

- `textures/entity/hollow_grazer.png` — original 128 by 64 controlled pixel sheet,
  generated deterministically by `scripts/generate-dev-art.ps1`; visual QA pending.
- `gravesown.png` — original 64 by 64 pixel mod icon from the same script.
- Nine original 16 by 16 item icons cover the five Hollow Grazer resources and
  four Quietskin armor pieces.
- `textures/models/armor/quietskin_layer_1.png` and `quietskin_layer_2.png` are
  original 64 by 32 controlled UV sheets for Minecraft 1.21.1 armor rendering;
  resource loading passed and worn-model visual QA remains open.
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
- `art/generation-prompts/hollow_grazer_concept.md` — preserved ImageGen concept
  prompt. Image generation is intentionally not used as the final UV texture.

All current shipped pixel assets are reproducible by running
`scripts/generate-dev-art.ps1`; the script writes fixed pixels rather than asking
an image model to guess exact Minecraft UV coordinates.
