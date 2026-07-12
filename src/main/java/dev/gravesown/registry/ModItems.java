package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.item.QuietskinArmorItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gravesown.MOD_ID);
    private static final int QUIETSKIN_DURABILITY = 10;

    public static final DeferredItem<Item> RAGGED_GRAZER_HIDE = ITEMS.registerSimpleItem("ragged_grazer_hide");
    public static final DeferredItem<Item> TAUT_SINEW = ITEMS.registerSimpleItem("taut_sinew");
    public static final DeferredItem<Item> GRAVE_TALLOW = ITEMS.registerSimpleItem("grave_tallow");
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
