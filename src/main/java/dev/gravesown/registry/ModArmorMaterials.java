package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Gravesown.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> QUIETSKIN = ARMOR_MATERIALS.register(
            "quietskin",
            () -> new ArmorMaterial(
                    createQuietskinDefense(),
                    13,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(ModItems.RAGGED_GRAZER_HIDE.get()),
                    List.of(new ArmorMaterial.Layer(Gravesown.id("quietskin"))),
                    0.0F,
                    0.0F
            )
    );

    private ModArmorMaterials() {
    }

    public static void register(IEventBus modEventBus) {
        ARMOR_MATERIALS.register(modEventBus);
    }

    private static EnumMap<ArmorItem.Type, Integer> createQuietskinDefense() {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, 1);
        defense.put(ArmorItem.Type.LEGGINGS, 3);
        defense.put(ArmorItem.Type.CHESTPLATE, 4);
        defense.put(ArmorItem.Type.HELMET, 2);
        defense.put(ArmorItem.Type.BODY, 4);
        return defense;
    }
}
