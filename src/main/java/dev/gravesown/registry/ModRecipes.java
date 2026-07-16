package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.recipe.GraveworkRecipe;
import dev.gravesown.recipe.FieldKitchenRecipe;
import dev.gravesown.recipe.SawmillRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Gravesown.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Gravesown.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GraveworkRecipe>> GRAVEWORK_SERIALIZER =
            SERIALIZERS.register("gravework_shaped", GraveworkRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<GraveworkRecipe>> GRAVEWORK_TYPE =
            TYPES.register("gravework", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Gravesown.MOD_ID + ":gravework";
                }
            });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FieldKitchenRecipe>> FIELD_KITCHEN_SERIALIZER =
            SERIALIZERS.register("field_kitchen", FieldKitchenRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FieldKitchenRecipe>> FIELD_KITCHEN_TYPE =
            TYPES.register("field_kitchen", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Gravesown.MOD_ID + ":field_kitchen";
                }
            });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SawmillRecipe>> SAWMILL_SERIALIZER =
            SERIALIZERS.register("sawmill", SawmillRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SawmillRecipe>> SAWMILL_TYPE =
            TYPES.register("sawmill", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Gravesown.MOD_ID + ":sawmill";
                }
            });

    private ModRecipes() {
    }

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);
    }
}
