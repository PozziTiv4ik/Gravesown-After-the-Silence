package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.item.QuietskinArmorItem;
import dev.gravesown.item.GraveTallowItem;
import dev.gravesown.item.SurvivorCodexItem;
import dev.gravesown.item.GloamSkiffItem;
import dev.gravesown.item.GravebloomDustItem;
import dev.gravesown.item.HushstoneSpearItem;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gravesown.MOD_ID);
    private static final int QUIETSKIN_DURABILITY = 10;

    public static final DeferredItem<BlockItem> ASHEN_SOD = ITEMS.registerSimpleBlockItem(ModBlocks.ASHEN_SOD);
    public static final DeferredItem<BlockItem> GRAVE_LOAM = ITEMS.registerSimpleBlockItem(ModBlocks.GRAVE_LOAM);
    public static final DeferredItem<BlockItem> HUSHSTONE = ITEMS.registerSimpleBlockItem(ModBlocks.HUSHSTONE);
    public static final DeferredItem<BlockItem> DEEP_HUSHSTONE = ITEMS.registerSimpleBlockItem(ModBlocks.DEEP_HUSHSTONE);
    public static final DeferredItem<BlockItem> GRAVEBED = ITEMS.registerSimpleBlockItem(ModBlocks.GRAVEBED);
    public static final DeferredItem<BlockItem> ROOTFELT = ITEMS.registerSimpleBlockItem(ModBlocks.ROOTFELT);
    public static final DeferredItem<BlockItem> FIBROUS_LOAM = ITEMS.registerSimpleBlockItem(ModBlocks.FIBROUS_LOAM);
    public static final DeferredItem<BlockItem> SCAR_SHALE = ITEMS.registerSimpleBlockItem(ModBlocks.SCAR_SHALE);
    public static final DeferredItem<BlockItem> MARROWSTONE = ITEMS.registerSimpleBlockItem(ModBlocks.MARROWSTONE);
    public static final DeferredItem<BlockItem> SUTURE_SILT = ITEMS.registerSimpleBlockItem(ModBlocks.SUTURE_SILT);
    public static final DeferredItem<BlockItem> DRIED_ICHOR = ITEMS.registerSimpleBlockItem(ModBlocks.DRIED_ICHOR);
    public static final DeferredItem<BlockItem> ABYSSAL_SILT = ITEMS.registerSimpleBlockItem(ModBlocks.ABYSSAL_SILT);
    public static final DeferredItem<BlockItem> BRINEBONE = ITEMS.registerSimpleBlockItem(ModBlocks.BRINEBONE);
    public static final DeferredItem<BlockItem> GLOAM_MUCK = ITEMS.registerSimpleBlockItem(ModBlocks.GLOAM_MUCK);
    public static final DeferredItem<BlockItem> GLOAM_SAND = ITEMS.registerSimpleBlockItem(ModBlocks.GLOAM_SAND);
    public static final DeferredItem<BlockItem> VEINED_SHALE = ITEMS.registerSimpleBlockItem(ModBlocks.VEINED_SHALE);
    public static final DeferredItem<BlockItem> SPLINTERED_MARROWSTONE =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPLINTERED_MARROWSTONE);
    public static final DeferredItem<BlockItem> CAIRNSTONE = ITEMS.registerSimpleBlockItem(ModBlocks.CAIRNSTONE);
    public static final DeferredItem<BlockItem> SAWMILL = ITEMS.registerSimpleBlockItem(ModBlocks.SAWMILL);
    public static final DeferredItem<BlockItem> RIBROOT_CUT_PLANKS =
            ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_CUT_PLANKS);
    public static final DeferredItem<BlockItem> EMBERBARK_CUT_PLANKS =
            ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_CUT_PLANKS);
    public static final DeferredItem<BlockItem> PALEVINE_CUT_PLANKS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_CUT_PLANKS);
    public static final DeferredItem<BlockItem> GRAVESOWN_GLASS =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAVESOWN_GLASS);
    public static final DeferredItem<BlockItem> TEMPERED_GLASS =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEMPERED_GLASS);
    public static final DeferredItem<BlockItem> GRAVEWORK_BENCH = ITEMS.registerSimpleBlockItem(ModBlocks.GRAVEWORK_BENCH);
    public static final DeferredItem<BlockItem> PITCH_KILN = ITEMS.registerSimpleBlockItem(ModBlocks.PITCH_KILN);
    public static final DeferredItem<BlockItem> RELIQUARY_CRATE = ITEMS.registerSimpleBlockItem(ModBlocks.RELIQUARY_CRATE);
    public static final DeferredItem<BlockItem> FIELD_KITCHEN = ITEMS.registerSimpleBlockItem(ModBlocks.FIELD_KITCHEN);
    public static final DeferredItem<BlockItem> REMNANT_GRAVE = ITEMS.registerSimpleBlockItem(ModBlocks.REMNANT_GRAVE);
    public static final DeferredItem<BlockItem> RIBROOT_STEM = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_STEM);
    public static final DeferredItem<BlockItem> RIBROOT_PLANKS = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_PLANKS);
    public static final DeferredItem<BlockItem> RIBROOT_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_STAIRS);
    public static final DeferredItem<BlockItem> RIBROOT_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_SLAB);
    public static final DeferredItem<BlockItem> RIBROOT_FENCE = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_FENCE);
    public static final DeferredItem<BlockItem> RIBROOT_FENCE_GATE = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_FENCE_GATE);
    public static final DeferredItem<DoubleHighBlockItem> RIBROOT_DOOR = ITEMS.registerItem(
            "ribroot_door",
            properties -> new DoubleHighBlockItem(ModBlocks.RIBROOT_DOOR.get(), properties)
    );
    public static final DeferredItem<BlockItem> RIBROOT_TRAPDOOR = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_TRAPDOOR);
    public static final DeferredItem<BlockItem> EMBERBARK_STEM = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_STEM);
    public static final DeferredItem<BlockItem> EMBERBARK_PLANKS = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_PLANKS);
    public static final DeferredItem<BlockItem> EMBERBARK_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_STAIRS);
    public static final DeferredItem<BlockItem> EMBERBARK_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_SLAB);
    public static final DeferredItem<BlockItem> EMBERBARK_FENCE = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_FENCE);
    public static final DeferredItem<BlockItem> EMBERBARK_FENCE_GATE = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_FENCE_GATE);
    public static final DeferredItem<DoubleHighBlockItem> EMBERBARK_DOOR = ITEMS.registerItem(
            "emberbark_door", properties -> new DoubleHighBlockItem(ModBlocks.EMBERBARK_DOOR.get(), properties)
    );
    public static final DeferredItem<BlockItem> EMBERBARK_TRAPDOOR = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_TRAPDOOR);
    public static final DeferredItem<BlockItem> EMBERBARK_FOLIAGE = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_FOLIAGE);
    public static final DeferredItem<BlockItem> EMBERBARK_SHOOT = ITEMS.registerSimpleBlockItem(ModBlocks.EMBERBARK_SHOOT);
    public static final DeferredItem<BlockItem> PALEVINE_STEM = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_STEM);
    public static final DeferredItem<BlockItem> PALEVINE_PLANKS = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_PLANKS);
    public static final DeferredItem<BlockItem> PALEVINE_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_STAIRS);
    public static final DeferredItem<BlockItem> PALEVINE_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_SLAB);
    public static final DeferredItem<BlockItem> PALEVINE_FENCE = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_FENCE);
    public static final DeferredItem<BlockItem> PALEVINE_FENCE_GATE = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_FENCE_GATE);
    public static final DeferredItem<DoubleHighBlockItem> PALEVINE_DOOR = ITEMS.registerItem(
            "palevine_door", properties -> new DoubleHighBlockItem(ModBlocks.PALEVINE_DOOR.get(), properties)
    );
    public static final DeferredItem<BlockItem> PALEVINE_TRAPDOOR = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_TRAPDOOR);
    public static final DeferredItem<BlockItem> PALEVINE_FOLIAGE = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_FOLIAGE);
    public static final DeferredItem<BlockItem> PALEVINE_SHOOT = ITEMS.registerSimpleBlockItem(ModBlocks.PALEVINE_SHOOT);
    public static final WoodFamilyItems CAIRNWOOD = registerWoodFamilyItems("cairnwood", ModBlocks.CAIRNWOOD);
    public static final DeferredItem<BlockItem> CAIRNWOOD_STEM = CAIRNWOOD.stem();
    public static final DeferredItem<BlockItem> CAIRNWOOD_PLANKS = CAIRNWOOD.planks();
    public static final DeferredItem<BlockItem> CAIRNWOOD_CUT_PLANKS = CAIRNWOOD.cutPlanks();
    public static final DeferredItem<BlockItem> CAIRNWOOD_STAIRS = CAIRNWOOD.stairs();
    public static final DeferredItem<BlockItem> CAIRNWOOD_SLAB = CAIRNWOOD.slab();
    public static final DeferredItem<BlockItem> CAIRNWOOD_FENCE = CAIRNWOOD.fence();
    public static final DeferredItem<BlockItem> CAIRNWOOD_FENCE_GATE = CAIRNWOOD.fenceGate();
    public static final DeferredItem<DoubleHighBlockItem> CAIRNWOOD_DOOR = CAIRNWOOD.door();
    public static final DeferredItem<BlockItem> CAIRNWOOD_TRAPDOOR = CAIRNWOOD.trapdoor();
    public static final DeferredItem<BlockItem> CAIRNWOOD_FOLIAGE = CAIRNWOOD.foliage();
    public static final DeferredItem<BlockItem> CAIRNWOOD_SHOOT = CAIRNWOOD.shoot();

    public static final WoodFamilyItems SUTUREWOOD = registerWoodFamilyItems("suturewood", ModBlocks.SUTUREWOOD);
    public static final DeferredItem<BlockItem> SUTUREWOOD_STEM = SUTUREWOOD.stem();
    public static final DeferredItem<BlockItem> SUTUREWOOD_PLANKS = SUTUREWOOD.planks();
    public static final DeferredItem<BlockItem> SUTUREWOOD_CUT_PLANKS = SUTUREWOOD.cutPlanks();
    public static final DeferredItem<BlockItem> SUTUREWOOD_STAIRS = SUTUREWOOD.stairs();
    public static final DeferredItem<BlockItem> SUTUREWOOD_SLAB = SUTUREWOOD.slab();
    public static final DeferredItem<BlockItem> SUTUREWOOD_FENCE = SUTUREWOOD.fence();
    public static final DeferredItem<BlockItem> SUTUREWOOD_FENCE_GATE = SUTUREWOOD.fenceGate();
    public static final DeferredItem<DoubleHighBlockItem> SUTUREWOOD_DOOR = SUTUREWOOD.door();
    public static final DeferredItem<BlockItem> SUTUREWOOD_TRAPDOOR = SUTUREWOOD.trapdoor();
    public static final DeferredItem<BlockItem> SUTUREWOOD_FOLIAGE = SUTUREWOOD.foliage();
    public static final DeferredItem<BlockItem> SUTUREWOOD_SHOOT = SUTUREWOOD.shoot();

    public static final WoodFamilyItems MOSSWAKE = registerWoodFamilyItems("mosswake", ModBlocks.MOSSWAKE);
    public static final DeferredItem<BlockItem> MOSSWAKE_STEM = MOSSWAKE.stem();
    public static final DeferredItem<BlockItem> MOSSWAKE_PLANKS = MOSSWAKE.planks();
    public static final DeferredItem<BlockItem> MOSSWAKE_CUT_PLANKS = MOSSWAKE.cutPlanks();
    public static final DeferredItem<BlockItem> MOSSWAKE_STAIRS = MOSSWAKE.stairs();
    public static final DeferredItem<BlockItem> MOSSWAKE_SLAB = MOSSWAKE.slab();
    public static final DeferredItem<BlockItem> MOSSWAKE_FENCE = MOSSWAKE.fence();
    public static final DeferredItem<BlockItem> MOSSWAKE_FENCE_GATE = MOSSWAKE.fenceGate();
    public static final DeferredItem<DoubleHighBlockItem> MOSSWAKE_DOOR = MOSSWAKE.door();
    public static final DeferredItem<BlockItem> MOSSWAKE_TRAPDOOR = MOSSWAKE.trapdoor();
    public static final DeferredItem<BlockItem> MOSSWAKE_FOLIAGE = MOSSWAKE.foliage();
    public static final DeferredItem<BlockItem> MOSSWAKE_SHOOT = MOSSWAKE.shoot();

    public static final WoodFamilyItems SUNVEIL = registerWoodFamilyItems("sunveil", ModBlocks.SUNVEIL);
    public static final DeferredItem<BlockItem> SUNVEIL_STEM = SUNVEIL.stem();
    public static final DeferredItem<BlockItem> SUNVEIL_PLANKS = SUNVEIL.planks();
    public static final DeferredItem<BlockItem> SUNVEIL_CUT_PLANKS = SUNVEIL.cutPlanks();
    public static final DeferredItem<BlockItem> SUNVEIL_STAIRS = SUNVEIL.stairs();
    public static final DeferredItem<BlockItem> SUNVEIL_SLAB = SUNVEIL.slab();
    public static final DeferredItem<BlockItem> SUNVEIL_FENCE = SUNVEIL.fence();
    public static final DeferredItem<BlockItem> SUNVEIL_FENCE_GATE = SUNVEIL.fenceGate();
    public static final DeferredItem<DoubleHighBlockItem> SUNVEIL_DOOR = SUNVEIL.door();
    public static final DeferredItem<BlockItem> SUNVEIL_TRAPDOOR = SUNVEIL.trapdoor();
    public static final DeferredItem<BlockItem> SUNVEIL_FOLIAGE = SUNVEIL.foliage();
    public static final DeferredItem<BlockItem> SUNVEIL_SHOOT = SUNVEIL.shoot();
    public static final DeferredItem<BlockItem> TALLOW_LANTERN = ITEMS.registerSimpleBlockItem(ModBlocks.TALLOW_LANTERN);
    public static final DeferredItem<BlockItem> GLOAM_FARMLAND = ITEMS.registerSimpleBlockItem(ModBlocks.GLOAM_FARMLAND);
    public static final DeferredItem<BlockItem> VEIL_FOLIAGE = ITEMS.registerSimpleBlockItem(ModBlocks.VEIL_FOLIAGE);
    public static final DeferredItem<BlockItem> THREADGRASS = ITEMS.registerSimpleBlockItem(ModBlocks.THREADGRASS);
    public static final DeferredItem<BlockItem> RIBROOT_SHOOT = ITEMS.registerSimpleBlockItem(ModBlocks.RIBROOT_SHOOT);
    public static final DeferredItem<BlockItem> PALLID_BULB = ITEMS.registerSimpleBlockItem(ModBlocks.PALLID_BULB);
    public static final DeferredItem<BlockItem> CINDER_BLOOM = ITEMS.registerSimpleBlockItem(ModBlocks.CINDER_BLOOM);
    public static final DeferredItem<BlockItem> RIFT_THORN = ITEMS.registerSimpleBlockItem(ModBlocks.RIFT_THORN);
    public static final DeferredItem<BlockItem> MIRE_FROND = ITEMS.registerSimpleBlockItem(ModBlocks.MIRE_FROND);
    public static final DeferredItem<BlockItem> MOSSVEIL = ITEMS.registerSimpleBlockItem(ModBlocks.MOSSVEIL);
    public static final DeferredItem<BlockItem> AMBER_BLOOM = ITEMS.registerSimpleBlockItem(ModBlocks.AMBER_BLOOM);
    public static final DeferredItem<BlockItem> SINEW_FERN = ITEMS.registerSimpleBlockItem(ModBlocks.SINEW_FERN);
    public static final DeferredItem<BlockItem> MARROW_REED = ITEMS.registerSimpleBlockItem(ModBlocks.MARROW_REED);
    public static final DeferredItem<BlockItem> THREADKELP = ITEMS.registerSimpleBlockItem(ModBlocks.THREADKELP);
    public static final DeferredItem<BlockItem> VEILWEED = ITEMS.registerSimpleBlockItem(ModBlocks.VEILWEED);
    public static final DeferredItem<BlockItem> DROWNED_ROOTS = ITEMS.registerSimpleBlockItem(ModBlocks.DROWNED_ROOTS);
    public static final DeferredItem<BlockItem> BLADDERPOD = ITEMS.registerSimpleBlockItem(ModBlocks.BLADDERPOD);
    public static final DeferredItem<BlockItem> LUMEN_KELP = ITEMS.registerSimpleBlockItem(ModBlocks.LUMEN_KELP);
    public static final DeferredItem<BucketItem> GLOAMWATER_BUCKET = ITEMS.registerItem(
            "gloamwater_bucket",
            properties -> new BucketItem(
                    ModFluids.GLOAMWATER.get(),
                    properties.stacksTo(1).craftRemainder(Items.BUCKET)
            )
    );

    public static final DeferredItem<Item> RIBROOT_SPLINT = ITEMS.registerSimpleItem("ribroot_splint");
    public static final DeferredItem<Item> THREAD_BINDING = ITEMS.registerSimpleItem("thread_binding");
    public static final DeferredItem<Item> HUSHSTONE_SHARD = ITEMS.registerSimpleItem("hushstone_shard");
    public static final DeferredItem<ItemNameBlockItem> ASHGRAIN_SEEDS = ITEMS.registerItem(
            "ashgrain_seeds",
            properties -> new ItemNameBlockItem(ModBlocks.ASHGRAIN_CROP.get(), properties)
    );
    public static final DeferredItem<ItemNameBlockItem> MIREBEAN_SEEDS = ITEMS.registerItem(
            "mirebean_seeds",
            properties -> new ItemNameBlockItem(ModBlocks.MIREBEAN_CROP.get(), properties)
    );
    public static final DeferredItem<Item> ASHGRAIN = ITEMS.registerSimpleItem(
            "ashgrain",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build())
    );
    public static final DeferredItem<Item> MIREBEAN = ITEMS.registerSimpleItem(
            "mirebean",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2F).build())
    );
    public static final DeferredItem<Item> ASHGRAIN_LOAF = ITEMS.registerSimpleItem(
            "ashgrain_loaf",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.65F).build())
    );
    public static final DeferredItem<Item> FIELD_RATION = ITEMS.registerSimpleItem(
            "field_ration",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationModifier(0.85F).build())
    );
    public static final DeferredItem<Item> BONE_CLEAVER = ITEMS.registerSimpleItem(
            "bone_cleaver", new Item.Properties().durability(128)
    );
    public static final DeferredItem<Item> STIRRING_HOOK = ITEMS.registerSimpleItem(
            "stirring_hook", new Item.Properties().durability(160)
    );
    public static final DeferredItem<Item> MIREBEAN_STEW = ITEMS.registerSimpleItem(
            "mirebean_stew",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.72F).build())
    );
    public static final DeferredItem<Item> CHARRED_MARROW_POT = ITEMS.registerSimpleItem(
            "charred_marrow_pot",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(9).saturationModifier(0.85F).build())
    );
    public static final DeferredItem<Item> GLOAM_CHOWDER = ITEMS.registerSimpleItem(
            "gloam_chowder",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.95F).build())
    );
    public static final DeferredItem<Item> NEEDLE_SPRAT = ITEMS.registerSimpleItem(
            "needle_sprat",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.18F).build())
    );
    public static final DeferredItem<GravebloomDustItem> GRAVEBLOOM_DUST = ITEMS.registerItem(
            "gravebloom_dust",
            GravebloomDustItem::new
    );
    public static final DeferredItem<SurvivorCodexItem> SURVIVOR_CODEX = ITEMS.registerItem(
            "survivor_codex",
            SurvivorCodexItem::new
    );
    public static final DeferredItem<PickaxeItem> CRUDE_HANDPICK = ITEMS.registerItem(
            "crude_handpick",
            properties -> new PickaxeItem(
                    ModToolTiers.CRUDE,
                    properties.attributes(PickaxeItem.createAttributes(ModToolTiers.CRUDE, 1.0F, -2.8F))
            )
    );
    public static final DeferredItem<SwordItem> BOUND_KNIFE = ITEMS.registerItem(
            "bound_knife",
            properties -> new SwordItem(
                    ModToolTiers.BOUND_HUSHSTONE,
                    properties.attributes(SwordItem.createAttributes(ModToolTiers.BOUND_HUSHSTONE, 2.0F, -2.0F))
            )
    );
    public static final DeferredItem<PickaxeItem> HUSHSTONE_PICKAXE = ITEMS.registerItem(
            "hushstone_pickaxe",
            properties -> new PickaxeItem(
                    ModToolTiers.GRAVEWORK_HUSHSTONE,
                    properties.attributes(PickaxeItem.createAttributes(ModToolTiers.GRAVEWORK_HUSHSTONE, 1.0F, -2.8F))
            )
    );
    public static final DeferredItem<ShovelItem> HUSHSTONE_SHOVEL = ITEMS.registerItem(
            "hushstone_shovel",
            properties -> new ShovelItem(
                    ModToolTiers.GRAVEWORK_HUSHSTONE,
                    properties.attributes(ShovelItem.createAttributes(ModToolTiers.GRAVEWORK_HUSHSTONE, 1.5F, -3.0F))
            )
    );
    public static final DeferredItem<AxeItem> HUSHSTONE_AXE = ITEMS.registerItem(
            "hushstone_axe",
            properties -> new AxeItem(
                    ModToolTiers.GRAVEWORK_HUSHSTONE,
                    properties.attributes(AxeItem.createAttributes(ModToolTiers.GRAVEWORK_HUSHSTONE, 5.0F, -3.1F))
            )
    );
    public static final DeferredItem<HoeItem> HUSHSTONE_HOE = ITEMS.registerItem(
            "hushstone_hoe",
            properties -> new HoeItem(
                    ModToolTiers.GRAVEWORK_HUSHSTONE,
                    properties.attributes(HoeItem.createAttributes(ModToolTiers.GRAVEWORK_HUSHSTONE, -1.0F, -2.0F))
            )
    );
    public static final DeferredItem<FishingRodItem> GLOAMLINE_ROD = ITEMS.registerItem(
            "gloamline_rod",
            properties -> new FishingRodItem(properties.durability(96))
    );
    public static final DeferredItem<GloamSkiffItem> GLOAM_SKIFF = ITEMS.registerItem(
            "gloam_skiff",
            properties -> new GloamSkiffItem(properties.stacksTo(1))
    );
    public static final DeferredItem<HushstoneSpearItem> HUSHSTONE_SPEAR = ITEMS.registerItem(
            "hushstone_spear",
            properties -> new HushstoneSpearItem(
                    properties
                            .durability(192)
                            .attributes(HushstoneSpearItem.createAttributes())
                            .component(DataComponents.TOOL, HushstoneSpearItem.createToolProperties())
            )
    );

    public static final DeferredItem<Item> RAGGED_GRAZER_HIDE = ITEMS.registerSimpleItem("ragged_grazer_hide");
    public static final DeferredItem<Item> TAUT_SINEW = ITEMS.registerSimpleItem("taut_sinew");
    public static final DeferredItem<GraveTallowItem> GRAVE_TALLOW = ITEMS.registerItem(
            "grave_tallow",
            GraveTallowItem::new
    );
    public static final DeferredItem<Item> CHARRED_GRAZER_MEAT = ITEMS.registerSimpleItem(
            "charred_grazer_meat",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.65F).build())
    );
    public static final DeferredItem<Item> SMOKED_ROTFIN = ITEMS.registerSimpleItem(
            "smoked_rotfin",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.5F).build())
    );
    public static final DeferredItem<Item> TAINTED_GRAZER_MEAT = ITEMS.registerSimpleItem(
            "tainted_grazer_meat",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.15F)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F)
                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 0), 0.25F)
                    .build())
    );
    public static final DeferredItem<Item> HOLLOW_JAW = ITEMS.registerSimpleItem(
            "hollow_jaw",
            new Item.Properties().stacksTo(16)
    );
    public static final DeferredItem<Item> ROTFIN_FLESH = ITEMS.registerSimpleItem(
            "rotfin_flesh",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.1F)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0), 0.65F)
                    .build())
    );
    public static final DeferredItem<Item> VEILFIN_FILLET = ITEMS.registerSimpleItem(
            "veilfin_fillet",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2F).build())
    );
    public static final DeferredItem<Item> ROOTSKIMMER_MEAT = ITEMS.registerSimpleItem(
            "rootskimmer_meat",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.25F).build())
    );

    public static final DeferredItem<ArmorItem> QUIETSKIN_HOOD = registerQuietskin(
            "quietskin_hood",
            ArmorItem.Type.HELMET
    );
    public static final DeferredItem<ArmorItem> QUIETSKIN_COAT = registerQuietskin(
            "quietskin_coat",
            ArmorItem.Type.CHESTPLATE
    );
    public static final DeferredItem<ArmorItem> QUIETSKIN_LEGWRAPS = registerQuietskin(
            "quietskin_legwraps",
            ArmorItem.Type.LEGGINGS
    );
    public static final DeferredItem<ArmorItem> QUIETSKIN_BOOTS = registerQuietskin(
            "quietskin_boots",
            ArmorItem.Type.BOOTS
    );

    public static final DeferredItem<DeferredSpawnEggItem> HOLLOW_GRAZER_SPAWN_EGG = ITEMS.registerItem(
            "hollow_grazer_spawn_egg",
            properties -> new DeferredSpawnEggItem(
                    ModEntities.HOLLOW_GRAZER,
                    0x292522,
                    0x8A9A4A,
                    properties
            )
    );
    public static final DeferredItem<DeferredSpawnEggItem> RIBSPRING_SPAWN_EGG = ITEMS.registerItem(
            "ribspring_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.RIBSPRING, 0x49352F, 0xB4A887, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> STITCHTUSK_SPAWN_EGG = ITEMS.registerItem(
            "stitchtusk_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.STITCHTUSK, 0x3C2528, 0xD1C1A0, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> WOUNDSCENT_SPAWN_EGG = ITEMS.registerItem(
            "woundscent_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.WOUNDSCENT, 0x211B1D, 0x8A3E42, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> BURIED_REMNANT_SPAWN_EGG = ITEMS.registerItem(
            "buried_remnant_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.BURIED_REMNANT, 0x282521, 0xB8AA83, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> ROTFIN_SPAWN_EGG = ITEMS.registerItem(
            "rotfin_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.ROTFIN, 0x17211F, 0x8B4B46, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> VEILFIN_SPAWN_EGG = ITEMS.registerItem(
            "veilfin_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.VEILFIN, 0x172925, 0x9DAA70, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> ROOTSKIMMER_SPAWN_EGG = ITEMS.registerItem(
            "rootskimmer_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.ROOTSKIMMER, 0x261F1B, 0xA98A69, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> ASH_HOPPER_SPAWN_EGG = ITEMS.registerItem(
            "ash_hopper_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.ASH_HOPPER, 0x514B42, 0xD6B45E, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> GRAVEWING_SPAWN_EGG = ITEMS.registerItem(
            "gravewing_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.GRAVEWING, 0x282D36, 0x89919C, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> ROOTBACK_SPAWN_EGG = ITEMS.registerItem(
            "rootback_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.ROOTBACK, 0x2F3A2E, 0x839172, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> BARK_MARTEN_SPAWN_EGG = ITEMS.registerItem(
            "bark_marten_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.BARK_MARTEN, 0x3C2C25, 0xC1A06D, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> CRAG_RAM_SPAWN_EGG = ITEMS.registerItem(
            "crag_ram_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.CRAG_RAM, 0x393B3C, 0xA7A29A, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> RIFT_PUMA_SPAWN_EGG = ITEMS.registerItem(
            "rift_puma_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.RIFT_PUMA, 0x3B3133, 0xA28279, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> MIRE_TOAD_SPAWN_EGG = ITEMS.registerItem(
            "mire_toad_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.MIRE_TOAD, 0x344331, 0x8EA57C, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> REED_LYNX_SPAWN_EGG = ITEMS.registerItem(
            "reed_lynx_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.REED_LYNX, 0x494033, 0xB3A47B, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> SILT_RAY_SPAWN_EGG = ITEMS.registerItem(
            "silt_ray_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.SILT_RAY, 0x243D43, 0x77969A, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> EMBER_FOX_SPAWN_EGG = ITEMS.registerItem(
            "ember_fox_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.EMBER_FOX, 0x523124, 0xD0A15E, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> CINDER_FOWL_SPAWN_EGG = ITEMS.registerItem(
            "cinder_fowl_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.CINDER_FOWL, 0x3D2928, 0xC68A43, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> PALLID_HART_SPAWN_EGG = ITEMS.registerItem(
            "pallid_hart_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.PALLID_HART, 0x5C5B51, 0xCDC5A9, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> MOSSBOAR_SPAWN_EGG = ITEMS.registerItem(
            "mossboar_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.MOSSBOAR, 0x303A2D, 0x899676, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> AMBER_JAY_SPAWN_EGG = ITEMS.registerItem(
            "amber_jay_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.AMBER_JAY, 0x5E452B, 0xDFAA5C, properties)
    );
    public static final DeferredItem<DeferredSpawnEggItem> SUNHORN_SPAWN_EGG = ITEMS.registerItem(
            "sunhorn_spawn_egg",
            properties -> new DeferredSpawnEggItem(ModEntities.SUNHORN, 0x604B32, 0xEFE08A, properties)
    );

    public static List<DeferredItem<DeferredSpawnEggItem>> spawnEggs() {
        return List.of(
                HOLLOW_GRAZER_SPAWN_EGG,
                RIBSPRING_SPAWN_EGG,
                STITCHTUSK_SPAWN_EGG,
                WOUNDSCENT_SPAWN_EGG,
                BURIED_REMNANT_SPAWN_EGG,
                ROTFIN_SPAWN_EGG,
                VEILFIN_SPAWN_EGG,
                ROOTSKIMMER_SPAWN_EGG,
                ASH_HOPPER_SPAWN_EGG,
                GRAVEWING_SPAWN_EGG,
                ROOTBACK_SPAWN_EGG,
                BARK_MARTEN_SPAWN_EGG,
                CRAG_RAM_SPAWN_EGG,
                RIFT_PUMA_SPAWN_EGG,
                MIRE_TOAD_SPAWN_EGG,
                REED_LYNX_SPAWN_EGG,
                SILT_RAY_SPAWN_EGG,
                EMBER_FOX_SPAWN_EGG,
                CINDER_FOWL_SPAWN_EGG,
                PALLID_HART_SPAWN_EGG,
                MOSSBOAR_SPAWN_EGG,
                AMBER_JAY_SPAWN_EGG,
                SUNHORN_SPAWN_EGG
        );
    }

    private static WoodFamilyItems registerWoodFamilyItems(String prefix, ModBlocks.WoodFamily family) {
        DeferredItem<BlockItem> stem = ITEMS.registerSimpleBlockItem(family.stem());
        DeferredItem<BlockItem> planks = ITEMS.registerSimpleBlockItem(family.planks());
        DeferredItem<BlockItem> cutPlanks = ITEMS.registerSimpleBlockItem(family.cutPlanks());
        DeferredItem<BlockItem> stairs = ITEMS.registerSimpleBlockItem(family.stairs());
        DeferredItem<BlockItem> slab = ITEMS.registerSimpleBlockItem(family.slab());
        DeferredItem<BlockItem> fence = ITEMS.registerSimpleBlockItem(family.fence());
        DeferredItem<BlockItem> fenceGate = ITEMS.registerSimpleBlockItem(family.fenceGate());
        DeferredItem<DoubleHighBlockItem> door = ITEMS.registerItem(
                prefix + "_door",
                properties -> new DoubleHighBlockItem(family.door().get(), properties)
        );
        DeferredItem<BlockItem> trapdoor = ITEMS.registerSimpleBlockItem(family.trapdoor());
        DeferredItem<BlockItem> foliage = ITEMS.registerSimpleBlockItem(family.foliage());
        DeferredItem<BlockItem> shoot = ITEMS.registerSimpleBlockItem(family.shoot());
        return new WoodFamilyItems(
                stem, planks, cutPlanks, stairs, slab, fence, fenceGate, door, trapdoor, foliage, shoot
        );
    }

    public record WoodFamilyItems(
            DeferredItem<BlockItem> stem,
            DeferredItem<BlockItem> planks,
            DeferredItem<BlockItem> cutPlanks,
            DeferredItem<BlockItem> stairs,
            DeferredItem<BlockItem> slab,
            DeferredItem<BlockItem> fence,
            DeferredItem<BlockItem> fenceGate,
            DeferredItem<DoubleHighBlockItem> door,
            DeferredItem<BlockItem> trapdoor,
            DeferredItem<BlockItem> foliage,
            DeferredItem<BlockItem> shoot
    ) {
    }

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<ArmorItem> registerQuietskin(String id, ArmorItem.Type type) {
        return ITEMS.registerItem(
                id,
                properties -> new QuietskinArmorItem(
                        ModArmorMaterials.QUIETSKIN,
                        type,
                        properties.durability(type.getDurability(QUIETSKIN_DURABILITY))
                )
        );
    }
}
