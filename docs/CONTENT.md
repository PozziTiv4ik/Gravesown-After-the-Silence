# Content tracker

| ID | Type | Concept | Stage | Biome | AI | Model | Texture | Loot | Code | Test |
|---|---|---|---|---|---|---|---|---|---|---|
| hollow_grazer | Creature | Neutral grazer that hunts wounded players and becomes aggressive at night | TC1 | Overworld | Alpha + Dead Scent | Alpha; visual QA pending | Original alpha; visual QA pending | Five original drops | Implemented | 5/5 GameTests PASS; in-world pending |
| bellbeak | Creature | Carrion bird that steals dropped food and calls others | M2 | Open biomes | Planned | Planned | Planned | Planned | Planned | Not run |
| stitchtusk | Creature | Burrowing charge beast | M2 | Plains/forest | Planned | Planned | Planned | Planned | Planned | Not run |
| ribspring | Creature | Skittish skeletal prey animal | M2 | Plains | Planned | Planned | Planned | Planned | Planned | Not run |
| rotfin | Creature | Aquatic blood-and-motion hunter | M2 | Water | Planned | Planned | Planned | Planned | Planned | Not run |

Update a row whenever any listed part changes. “Done” requires verification,
not merely the presence of a file.

## Foundation blocks

| ID | Role | Stage | Model | Texture | Loot | Tool contract | Test |
|---|---|---|---|---|---|---|---|
| ashen_sod | Surface layer | TC3a | Implemented | Original 16x16 top/side; client loaded | Self-drop | Shovel-mineable | GameTest PASS; placed visual pending |
| grave_loam | Soil/filler | TC3a | Implemented | Original 16x16 | Self-drop | Shovel-mineable | GameTest PASS; placed visual pending |
| hushstone | Primary rock | TC3a | Implemented | Original 16x16 | Self-drop | Wooden pickaxe or better | GameTest PASS; placed visual pending |
| deep_hushstone | Deep rock | TC3a | Implemented | Original 16x16 | Self-drop | Stone pickaxe or better; wood/gold rejected | GameTest PASS; placed visual pending |
| gravebed | Unbreakable bottom | TC3a | Implemented | Original 16x16 | No loot table | Unbreakable | GameTest PASS; placed visual pending |
