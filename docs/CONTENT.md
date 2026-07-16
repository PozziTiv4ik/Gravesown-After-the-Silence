# Content tracker

“Implemented” means code/data exists and the named automated checks pass. Entity and
equipment rows explicitly retain visual or pacing checks until they are observed by a
player; file presence alone is never treated as final art acceptance.

## Creatures

| ID | Concept and biome | Core behavior/stats | Model/texture | Loot/test path | Status |
|---|---|---|---|---|---|
| `hollow_grazer` | Wounded-prey grazer; Sown Grave | 28 health; daytime blood scent, night aggression, Dead Scent interaction | Rich layered geometry, new jaw/lunge/idle/locomotion accents and fully redrawn 128x128 opaque atlas; 24 dedicated detail islands; lineup tracking/capture PASS | Five original resources; spawn egg, summon, GameTests PASS | Implemented alpha; natural motion/density pending |
| `ribspring` | Skittish prey; Ribroot Groves | 16 health; no attack attribute; flees players, Grazers and monsters | Richer silhouette/appendages, new spring/twitch/locomotion accents and fully redrawn 128x128 opaque atlas; 29 dedicated detail islands; lineup tracking/capture PASS | Sinew/tainted meat; spawn egg, EN/RU, server spawn/tick GameTest PASS | TC4b implemented; natural motion/density pending |
| `stitchtusk` | Territorial heavy beast; Marrow Rifts | 42 health, 8 damage; 24-tick warning then straight 18-tick rush | Richer layered anatomy, new synced charge/telegraph/locomotion accents and fully redrawn 128x128 opaque atlas; 19 dedicated detail islands; lineup tracking/capture PASS | Hide/sinew/shard; spawn egg, EN/RU, server spawn/tick GameTest PASS | TC4b implemented; charge motion/feel pending |
| `woundscent` | Blind scent predator; Suture Mire | 28 health, 5 damage; healthy scent 12 blocks, wounded scent up to 28; pursuit pauses | Richer sensory silhouette, new scent-scan/pause/locomotion accents and fully redrawn 128x128 opaque atlas; 29 dedicated detail islands; lineup tracking/capture PASS | Sinew/tallow/hide; spawn egg, EN/RU, server spawn/tick GameTest PASS | TC4b implemented; pursuit motion/feel pending |
| `buried_remnant` | One-shot grave guardian; Sown Grave | 26 health, 5.5 damage; persistent 108-tick server-owned emergence disables AI/movement/damage, then melee hunt | Symmetric layered model, synced emergence pose, deterministic 128x128 texture and renderer | Gravesown-only entity loot; egg, summon, EN/RU and three GameTests PASS | Implemented; emergence visuals/natural rarity pending |
| `rotfin` | Aquatic hunter; Gloamwater ponds in Suture Mire | 10 health, 3 damage; water-creature AI, 16 follow range | Layered bilateral 128x128 model with paired gills/fins, feelers and segmented tail; deterministic texture/renderer | Rotfin Flesh; egg, summon, EN/RU and aquatic GameTests PASS | Implemented; in-pond motion/natural density pending |
| `veilfin` | Peaceful open-water fish; all Gloamwater, denser Gloam Sea schools | 8 health; submerged wander and avoidance | Bilateral 128x128 schooling-fish model with paired fins and segmented tail; deterministic texture/renderer | Veilfin Fillet; egg, summon, EN/RU and aquatic GameTests PASS | Implemented; natural school motion/density pending |
| `rootskimmer` | Peaceful bottom fish; all Gloamwater, denser Gloam Sea schools | 9 health; lower-band submerged targets | Broad bilateral 128x128 bottom-fish model with barbels, paired fins and segmented tail; deterministic texture/renderer | Rootskimmer Meat; egg, summon, EN/RU and aquatic GameTests PASS | Implemented; natural school motion/density pending |
| `ash_hopper` | Small prey; Sown Grave | 8 health; fast player/monster avoidance | Bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `gravewing` | Small gliding bird; Sown Grave | 6 health; panic/avoidance, hopping flight and slow fall | Bilateral winged native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `rootback` | Neutral browser; Ribroot Groves | 30 health, 5 retaliation damage | Layered bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `bark_marten` | Small grove predator; Ribroot Groves | 12 health, 3 damage; hunts native prey, avoids routine player aggression | Layered bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; food-web pacing pending |
| `crag_ram` | Neutral cliff herbivore; Marrow Rifts | 28 health, 5 retaliation damage | Horned bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `rift_puma` | Large Rifts predator | 34 health, 7 damage; hunts native prey and players at low spawn weight | Layered bilateral predator model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; combat/pacing pending |
| `mire_toad` | Small prey; Suture Mire | 7 health; evasive ground wander | Broad bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `reed_lynx` | Mire predator; Suture Mire | 25 health, 6 damage; hunts native prey and players at low spawn weight | Layered bilateral predator model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; combat/pacing pending |
| `silt_ray` | Peaceful bottom swimmer; Gloam Sea | 12 health; submerged bottom-band wander | Dedicated paired-fin ray model and original 128x128 atlas | Original loot; egg, summon, EN/RU and aquatic GameTests PASS | Implemented alpha; natural school motion pending |
| `ember_fox` | Small predator; Ember Thicket | 14 health, 4 damage; hunts native prey without routine player aggression | Layered bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; food-web pacing pending |
| `cinder_fowl` | Small ground bird; Ember Thicket | 8 health; panic/avoidance, hopping flight and slow fall | Bilateral winged native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `pallid_hart` | Woodland prey; Pallid Weald | 26 health; fast player/monster avoidance | Antlered bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `mossboar` | Neutral browser; Mosswake Woods | 32 health, 6 retaliation damage | Layered bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `amber_jay` | Small bird; Amberquiet Grove | 6 health; panic/avoidance, hopping flight and slow fall | Bilateral winged native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `sunhorn` | Grove prey; Amberquiet Grove | 30 health; player/monster avoidance | Horned bilateral native-fauna model and original 128x128 atlas | Original loot; egg, summon, EN/RU and NativeFauna GameTests PASS | Implemented alpha; natural pacing pending |
| `bellbeak` | Carrion bird that steals food and calls others | Planned | Planned | Planned | Not implemented |
| `hushstep` | Grove hunter that freezes under a direct gaze | Planned | Planned | Planned | Not implemented |
| `choirmaw` | Rare ecosystem coordinator | Planned | Planned | Planned | Not implemented |

All 23 implemented creatures have registration, server attributes/AI, a client
renderer/model/original texture, loot, spawn egg, EN/RU names and an easy summon/test
path. Twenty-two use reviewed natural spawn entries across the exact nine-biome
allowlist; Buried Remnant is released only by the grave encounter. The 14 profile-based
land newcomers cover prey, neutral, small-predator, large-predator and bird archetypes;
Silt Ray adds a fourth peaceful aquatic niche. Their goals are bounded, server-owned
and species-specific predators remain a minority. Static art audits enforce atlas and
model bounds. The FHD client smoke tracks a five-entity dry-land lineup; underwater
motion and population feel remain separate manual checks. `gloam_skiff` is a vehicle
and `thrown_spear` is a projectile, not creatures.

## Foundation and regional blocks

| ID | Role/biome | Model/texture | Loot/tool contract | Verification |
|---|---|---|---|---|
| `ashen_sod` | Sown Grave, Mosswake Woods and Amberquiet Grove turf | Art Language V3 benchmark top/side 16x16 | Self-drop; shovel | GameTest/client resource coverage; placed visual pending |
| `grave_loam` | Sown Grave filler | Original 16x16 | Self-drop; shovel | GameTest/client resource PASS; placed visual pending |
| `hushstone` | Common rock | Original 16x16 | Self-drop; wooden pick or better | GameTest PASS |
| `deep_hushstone` | Deep progression rock | Original 16x16 | Self-drop; stone tier; wood/gold rejected | GameTest PASS |
| `gravebed` | Unbreakable bottom | Original 16x16 | No loot; unbreakable | GameTest PASS |
| `rootfelt` | Ribroot Groves surface | Original deterministic 16x16 | Self-drop; shovel | GameTest and Full audit PASS |
| `fibrous_loam` | Groves/Mire filler | Original deterministic 16x16 | Self-drop; shovel | GameTest and Full audit PASS |
| `scar_shale` | Marrow Rifts surface | Original deterministic 16x16 | Self-drop; wooden pick or better | GameTest and Full audit PASS |
| `marrowstone` | Marrow Rifts filler | Original deterministic 16x16 | Self-drop; wooden pick or better | GameTest and Full audit PASS |
| `suture_silt` | Suture Mire surface | Original deterministic 16x16 | Self-drop; shovel; 0.88 speed | GameTest and Full audit PASS |
| `dried_ichor` | Mire channel patch | Original deterministic 16x16 | Self-drop; shovel; 0.65 speed/0.85 jump | GameTest and Full audit PASS |
| `remnant_grave` | One-shot Sown Grave marker | Original closed/open directional models and deterministic 16x16 texture | No survival loot; `OPENED` blockstate persists and cannot reset/release twice | Interaction/emergence/loot GameTests PASS; natural visual pending |
| `gloamwater` | Gravesown aquatic fluid/block | Vanilla 1.21.1 still/flow/overlay sprites with custom translucent tint and fog | Source/flow/bucket family; standard water movement semantics without vanilla registry ids | Registry/source, FHD resource load and strict world-audit contracts PASS |
| `gloam_muck`, `gloam_sand` | Universal Gloamwater floor and shore | Art Language V3 tile-safe 16x16 materials | Every reviewed natural regional floor below new Gloamwater becomes a deterministic Sand-dominant bed with organic coordinate-noise Muck shelves; the material boundary is independent of the 4x4 plant grid | Aquatic distribution seam and strict world-audit path; executed evidence in STATUS |
| `veined_shale`, `splintered_marrowstone`, `cairnstone` | Marrow Rifts geology accents | Three distinct V3 16x16 stone grammars | Self-drop; pickaxe; deterministic placed disks | Registry/data/GameTest coverage; natural composition pending |
| `gravesown_glass` | First transparent building sheet | V3 transparent 16x16 art | Smelt Gloam Sand; hardness 0.3/resistance 1.5; Silk Touch recovery | Recipe/loot/GameTest coverage |
| `tempered_glass` | Recoverable reinforced glass | V3 transparent reinforced-edge art | Resmelt ordinary glass; hardness 0.9/resistance 4.5; self-drop | Recipe/loot/GameTest coverage |
| `threadkelp` | Gloamwater plant | Original deterministic hard-edged cutout | Survives in Gloamwater; exact self-drop | Ecology/feature GameTests PASS; in-world visual pending |
| `veilweed` | Mid-water Gloamwater plant | Original deterministic hard-edged cutout | Survives in Gloamwater; exact self-drop | Dense pond/sea feature GameTests PASS |
| `drowned_roots` | Submerged root cluster | Original deterministic hard-edged cutout | Survives in Gloamwater; exact self-drop | Dense pond/sea feature GameTests PASS |
| `bladderpod` | Pod-bearing aquatic plant | Original deterministic hard-edged cutout | Survives in Gloamwater; exact self-drop | Dense pond/sea feature GameTests PASS |
| `lumen_kelp` | Softly luminous aquatic accent | Original deterministic hard-edged cutout; light 6 | Survives in Gloamwater; exact self-drop | Dense pond/sea feature and survival GameTests PASS |
| `gloam_farmland` | Custom early farmland | Original dry/moist 16x16 states | Hoe-created from Ashen Sod/Grave Loam; only Gloamwater hydrates | Server hydration/tilling/growth GameTests PASS |

## Bootstrap flora and placed features

| ID | Role | Texture/behavior | Natural generation | Verification |
|---|---|---|---|---|
| `ribroot_stem` | First log | Axis-aware original 16x16 side/end; log/axe tags | `ribroot_tree`; lonely in Sown Grave, sparse in Groves | GameTest and Full audit observed |
| `ribroot_planks` | First building wood | Original 16x16; plank/axe tags | Crafted from Stem | GameTest PASS |
| `ribroot_stairs`, `ribroot_slab` | First shaped shelter blocks | Shared Ribroot grain and vanilla-compatible geometry | Gravework 4x4 recipes; axe tools; self/double-slab loot | Recipe/resource GameTests and FHD load PASS |
| `ribroot_fence`, `ribroot_fence_gate` | First perimeter family | Connected vanilla-compatible Ribroot geometry | Gravework 4x4 recipes; wooden tags/behavior | Recipe/resource GameTests PASS |
| `ribroot_door`, `ribroot_trapdoor` | First shelter closures | Shared panel/window/hinge door grammar with a readable item silhouette | Gravework 4x4 recipes; wooden tags/behavior | Recipe/resource GameTests PASS |
| `emberbark_*` | Ember Thicket wood family | Warm brown/rust V3 stem, ordinary/cut planks, foliage and coherent construction shapes | Trees natural; shoots player-planted only; shared functional-plank tag; species falling leaves | Recipe/resource coverage and Full biome audit path |
| `palevine_*` | Pallid Weald wood family | Warm ivory/khaki V3 stem, ordinary/cut planks and olive foliage; coherent construction shapes | Trees natural; shoots player-planted only; species falling leaves; no Ribroot fallback | Recipe/resource/art coverage and Full biome audit path |
| `cairnwood_*` | Marrow Rifts shrub wood family | Weathered gray-brown V3 stem, ordinary/cut planks, foliage and complete construction set | Bounded low Cairnwood shrubs; player-propagated shoot; species falling leaves | Family/resource/tree GameTest coverage; natural composition pending |
| `suturewood_*` | Suture Mire rooted wood family | Dark wet-umber V3 stem, ordinary/cut planks, foliage and complete construction set | Four horizontal roots converge on a central trunk; player-propagated shoot; species falling leaves | Family/resource/tree GameTest coverage; natural composition pending |
| `mosswake_*` | Mosswake Woods wood family | Mossy-oak V3 stem, ordinary/cut planks, foliage and complete construction set | Broad natural crowns over Ashen Sod; player-propagated shoot; species falling leaves | Family/resource/tree GameTest coverage; natural composition pending |
| `sunveil_*` | Amberquiet Grove wood family | Warm honey/ochre V3 stem, ordinary/cut planks, foliage and complete construction set | Tall natural crowns over Ashen Sod; player-propagated shoot; species falling leaves | Family/resource/tree GameTest coverage; natural composition pending |
| `*_cut_planks`, `sawmill` | Seven-species finishing loop | Cut planks use calmer Minecraft-adjacent grain; Sawmill has a readable workbench/blade silhouette | One ordinary plank -> one cut plank of the same species through a custom server recipe | Seven JSON recipes, custom menu/serializer and GameTest coverage |
| `tallow_lantern` | First crafted shelter light | Original deterministic lantern model/texture; light 13 | Gravework 4x4 from Ribroot, Tallow and Hushstone | Recipe/resource GameTests PASS |
| `veil_foliage` | Ribroot membrane crown | Original cutout 16x16; leaf decay plus species-textured client falling leaves | Ribroot tree crown | GameTest and Full audit observed |
| `threadgrass` | First binding fiber | Original cutout 16x16 | Patches in Sown Grave, Groves and Mire | GameTest and Full audit observed |
| `ribroot_shoot` | Ribroot propagule | Original cutout 16x16; sapling tag | Patches in Groves | GameTest and Full audit observed |
| `pallid_bulb` | Dim guide plant | Original cutout 16x16; light 3 | Reviewed patches in all four biomes | GameTest and Full audit observed |
| `cinder_bloom` | Warm rust flower | Original cutout 16x16 | Sown Grave and Marrow Rifts patches | GameTest and Full audit contract PASS |
| `sinew_fern` | Broad green understory | Original cutout 16x16 | Ribroot Groves and Suture Mire patches | GameTest and Full audit contract PASS |
| `marrow_reed` | Ivory-rust mire reed | Original cutout 16x16 | Marrow Rifts and Suture Mire patches | GameTest and Full audit contract PASS |
| `rift_thorn` | Marrow Rifts understory | Original hard-edged V3 cutout | Cairnwood/Rifts patches only | Registry/feature/resource coverage |
| `mire_frond` | Suture Mire understory | Original hard-edged V3 cutout | Suturewood mire patches only | Registry/feature/resource coverage |
| `mossveil` | Mosswake understory | Original hard-edged V3 cutout | Mosswake Woods patches only | Registry/feature/resource coverage |
| `amber_bloom` | Amberquiet understory | Original hard-edged V3 cutout | Amberquiet Grove patches only | Registry/feature/resource coverage |
| `gravebloom_dust` | Gravesown fertilizer | Original 16x16 item; grows Ribroot or spreads reviewed flora on valid soil | Crafted 1 Pallid Bulb to 2 | Server GameTests PASS |
| `dried_ichor` | Mire channel material | Regional block above | Dedicated underground/surface patch feature in Mire | Full audit observed |
| `marrow_outcrop` | Rifts landmark | Marrowstone forest-rock formation | One reviewed surface outcrop attempt per Rifts chunk | GameTest registry contract PASS; Full audit rerun recorded in STATUS |
| `remnant_graves_rare` | Rare Sown Grave encounter | Asymmetrical grave made from reviewed Gravesown terrain plus `remnant_grave` | 1/64 placed-feature attempt | Biome/feature contract and interaction GameTests PASS; natural pacing pending |
| `gloamwater_ponds_rare` | Small Suture Mire lake | Custom pond carver creates the fluid/bed shape; the shared chunk ecology pass alone supplies its plants | 1/14 placed-feature attempt; universal chunk-aligned 4x4 growth grid also covers cross-biome lakes and oceans without double seeding | Production-carver stability and exhaustive 16-column-per-cell aquatic GameTests PASS |
| `ruined_shelters_rare` | Three partial shelter variants | Procedural Gravesown timber/stone shell, optional Tallow Lantern and Reliquary Crate | 1/48 placed-feature attempt in six land biomes | Strict audit observes generated parts/crate block entities; loot/menu GameTests PASS |
| `abandoned_camps_very_rare` | Three intact-but-aged survivor camp variants | Biome-matched wood palette, fenced footprint, food/material props and a Reliquary Crate using the camp loot table | One deterministic 1/192 attempt in every reviewed biome; bounded variant selection | All variants, palettes, crate loot assignment and accessible fence footprint GameTests PASS; natural rarity manual |

## World generation

| ID | Type | Contract/content | Verification |
|---|---|---|---|
| `after_the_silence` | World preset | Overworld-only until TC8; exact nine-entry multi-noise source; only exposed new-world preset | Registry/GameTest/audit allowlist coverage; executed result in STATUS |
| `sown_grave` | Biome | Ashen Sod/Grave Loam; lonely Ribroot, plants, Hollow Grazer, Ash Hopper, Gloamwing and rare Remnant Grave | Full audit observed; native/grave density manual |
| `mosswake_woods` | Biome | Ashen Sod/Grave Loam; broad Mosswake trees, Mossveil, Mossboar and Ribspring | Registry/feature/audit coverage; natural density manual |
| `amberquiet_grove` | Biome | Ashen Sod/Grave Loam; tall Sunveil trees, Amber Bloom, Amber Jay, Sunhorn and Hollow Grazer | Registry/feature/audit coverage; natural density manual |
| `ribroot_groves` | Biome | Rootfelt/Fibrous Loam; varied Ribroot silhouettes, Rootback, Bark Marten and Ribspring | Full audit observed; native density manual |
| `marrow_rifts` | Biome | Scar Shale/Marrowstone, three stone disks and Cairnwood shrubs; Stitchtusk, Crag Ram and rare Rift Puma | Registry/feature/audit coverage; native/composition manual |
| `suture_mire` | Biome | Suture Silt/Fibrous Loam/Dried Ichor; rooted Suturewood and Gloamwater; Woundscent, Rotfin, Mire Toad and rare Reed Lynx | Registry/feature/audit coverage; pond/native density manual |
| `gloam_sea` | Biome | Large Gloamwater ocean; Abyssal Silt/Brinebone floor; five aquatic plants plus Rotfin, Veilfin, Rootskimmer and Silt Ray | Full biome evidence, dense-growth contract and strict namespace audit PASS; visual density manual |
| `ember_thicket` | Biome | Dried Ichor/Scar Shale warmth, varied Emberbark trees, Ember Fox, Cinder Fowl and Hollow Grazer | Full audit observed; natural density manual |
| `pallid_weald` | Biome | Fibrous Loam/Marrowstone cold woodland, varied Palevine trees, Pallid Hart and Ribspring | Full audit observed; natural density manual |
| `sown_terrain` | Density | Safe Y28–112 gradient plus cached macro/detail noise; 0.175 climate scale halves the over-broad regional width; no aquifers/ore veins | Strict Full: three seeds × 289 chunks, all nine biomes and zero violations |

The historical seven-biome strict Full evidence covers 867 FULL chunks and
85,229,568 block positions. Current nine-biome command evidence is recorded in
`docs/STATUS.md`; this tracker does not promote registry presence to audit success.

## First-tool survival chain

| ID | Role | Acquisition/contract | Verification |
|---|---|---|---|
| `ribroot_splint` | Local handle | 2 Ribroot Planks → 4 | Recipe/GameTest PASS |
| `thread_binding` | Local binding | 3 Threadgrass → 2 | Recipe/GameTest PASS |
| `hushstone_shard` | First cutting edge | Hushstone → 4; four rebuild block | Recipe/GameTest PASS |
| `crude_handpick` | First mining tool | 2 Splints + Binding in 2x2; 48 uses; Hushstone yes, Deep no | GameTest PASS; first-hour feel pending |
| `bound_knife` | First weapon/tool | Shard + Splint + Binding in 2x2; 96 uses | GameTest PASS; first-hour feel pending |
| `hushstone_spear` | First reach/thrown weapon | Gravework 4x4 recipe; 7 melee/thrown damage; charged throw is recoverable and applies Slowness V for exactly 200 ticks | Recipe, exact stack recovery, no-duplication and hit-effect GameTests PASS; combat feel pending |

## Equipment and onboarding

| ID/system | Content | Automated verification | Manual acceptance |
|---|---|---|---|
| Quietskin | Hood, Coat, Legwraps, Boots; 10 armor total; per-piece Dead Scent; four slot-specific volumetric models/49 cubes/open face | Recipes/repair/stats/scent GameTests and deterministic model/resource audits PASS | Default/slim fit; front/back/daylight/darkness/hurt flash |
| `survivor_codex` | One-time first-Survival item opening the full-screen `Story / Crafts / Chain / Guide` Codex; Crafts adds six categories, Chain adds branching graphs/pinning plus container-hover `R`, and Guide has seven scrollable topics | Persistent no-duplicate state and protocol 3 remain server-owned; catalog is rebuilt from every synchronized Gravesown recipe, including Sawmill layouts | EN/RU mouse/keyboard feel, expanded catalog and no-command route |
| Gravesown quest state | Fourteen-step condition/claim tree from awakening through wood, stations, cooking, fishing and Reliquary storage | Persistent server attachments enforce order and no double claim; ordinary advancement registry loads zero entries; GameTests cover locked/ready/claimed | Check custom sound/slide notice in live client |

## Exploration, storage and cooking

| ID/system | Content | Verification | Open check |
|---|---|---|---|
| `reliquary_crate` | Dedicated 36-slot barrel-like container; one row larger than a normal chest; four horizontal facings, opaque base and open/close audio | Custom menu type/screen, saved inventory, Gravesown-only ruin/camp loot, orientation/base/audio GameTests PASS | Natural loot value/pacing and live lid/barrel read |
| `sawmill` | Directional plank-finishing station; four horizontal facings, opaque base and craft feedback sound | Dedicated menu/serializer; seven exact one-to-one species-preserving recipes; orientation/base/audio GameTests PASS | Live crafting cadence |
| `field_kitchen` | Six-slot station: three ordered food inputs, persistent Cleaver/Hook and output | Real server block-entity recipe consumes ingredients, returns bucket and damages both tools by one; Codex renders a localized dedicated 3x2 layout; GameTests/FHD smoke PASS | Live interaction and screen feel |
| Hot meals | Mirebean Stew, Charred Marrow Pot and Gloam Chowder | Three synchronized custom recipes and item food properties PASS | Hunger balance |
| Fishing | Gloamline Rod catches 1–3 Needle Sprat; no vanilla fishing loot | Built-in fishing table override loads and Codex catalog includes the result | Player timing/feedback |

## Branded client

| Component | Content | Verification | Open check |
|---|---|---|---|
| Minecraft presentation | Branded title plus exact cold navy/steel/cyan widget sprites and allowlisted menu/world/options/loading backgrounds; gameplay art keeps natural local colors | Resource/static paths are client-only; final evidence in STATUS | Visual composition/accessibility at 1920x1080 |
| Windows launcher | Java 21 Swing app-image with `.exe`, centered animated Play, Verify/Logs, compact console and process guard in the cold presentation palette | App-image diagnostic/offscreen preview paths exist | Final FHD composition and live open-and-play pass |
| Art source | Immutable giant-tree background master plus deterministic V3 pixel assets for all UV-constrained art | Background may use ImageGen; exact textures remain script-owned | Human composition review and hash gate |

The consolidated integrated-client smoke records the responsive Hub, symmetric
five-entity dry-land lineup and revised open-face Quietskin front view. Automated
server/static checks and controlled captures do not replace normal-distance motion,
underwater or multi-angle armor review.

ImageGen is reserved for large launcher/title/screen illustrations. Exact blocks,
items, widget sprites, armor UV/model textures and entity sheets are deterministic
hard-edged pixel art from repository scripts.

All shipped PNGs are covered by the explicit 17-pass
`scripts/generate-all-art.ps1` ownership manifest, with Art Language V3 last.
`artcheck.cmd` enforces native dimensions/model references and emits terrain, block,
item and entity contact sheets. Exact regeneration, hash and acceptance results belong
in `docs/STATUS.md`.
