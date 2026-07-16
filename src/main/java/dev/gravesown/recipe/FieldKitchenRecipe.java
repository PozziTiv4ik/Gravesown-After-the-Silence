package dev.gravesown.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModRecipes;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** Three ingredients plus two persistent utensils, processed only by the Field Kitchen. */
public final class FieldKitchenRecipe implements Recipe<FieldKitchenRecipeInput> {
    private static final Codec<List<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC.listOf().comapFlatMap(values ->
            values.size() == 3
                    ? DataResult.success(List.copyOf(values))
                    : DataResult.error(() -> "Field Kitchen recipes require exactly three food ingredients"),
            Function.identity()
    );

    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;

    public FieldKitchenRecipe(List<Ingredient> ingredients, Ingredient cleaver, Ingredient hook, ItemStack result) {
        this.ingredients = NonNullList.withSize(5, Ingredient.EMPTY);
        for (int index = 0; index < 3; index++) {
            this.ingredients.set(index, ingredients.get(index));
        }
        this.ingredients.set(3, cleaver);
        this.ingredients.set(4, hook);
        this.result = result;
    }

    private FieldKitchenRecipe(NonNullList<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(FieldKitchenRecipeInput input, Level level) {
        for (int index = 0; index < 5; index++) {
            if (!this.ingredients.get(index).test(input.getItem(index))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(FieldKitchenRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.FIELD_KITCHEN.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FIELD_KITCHEN_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FIELD_KITCHEN_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<FieldKitchenRecipe> {
        private final MapCodec<FieldKitchenRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(recipe -> List.copyOf(recipe.ingredients.subList(0, 3))),
                Ingredient.CODEC.fieldOf("cleaver").forGetter(recipe -> recipe.ingredients.get(3)),
                Ingredient.CODEC.fieldOf("hook").forGetter(recipe -> recipe.ingredients.get(4)),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, FieldKitchenRecipe::new));

        private final StreamCodec<RegistryFriendlyByteBuf, FieldKitchenRecipe> streamCodec = StreamCodec.of(
                (buffer, recipe) -> {
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    }
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                },
                buffer -> {
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(5, Ingredient.EMPTY);
                    for (int index = 0; index < 5; index++) {
                        ingredients.set(index, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    }
                    return new FieldKitchenRecipe(ingredients, ItemStack.STREAM_CODEC.decode(buffer));
                }
        );

        @Override
        public MapCodec<FieldKitchenRecipe> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FieldKitchenRecipe> streamCodec() {
            return this.streamCodec;
        }
    }
}
