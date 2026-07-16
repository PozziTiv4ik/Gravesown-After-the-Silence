package dev.gravesown.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModRecipes;
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

/** Exact one-input/one-output cutting recipe used only by the Gravesown Sawmill. */
public final class SawmillRecipe implements Recipe<SawmillRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack result;

    public SawmillRecipe(Ingredient ingredient, ItemStack result) {
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(SawmillRecipeInput input, Level level) {
        return this.ingredient.test(input.stack());
    }

    @Override
    public ItemStack assemble(SawmillRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.ingredient);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.SAWMILL.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAWMILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SAWMILL_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<SawmillRecipe> {
        private static final MapCodec<SawmillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, SawmillRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                },
                buffer -> new SawmillRecipe(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        ItemStack.STREAM_CODEC.decode(buffer)
                )
        );

        @Override
        public MapCodec<SawmillRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
