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
- `art/generation-prompts/hollow_grazer_concept.md` — preserved ImageGen concept
  prompt. Image generation is intentionally not used as the final UV texture.
