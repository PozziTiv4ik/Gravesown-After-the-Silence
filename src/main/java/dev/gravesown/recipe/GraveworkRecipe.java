package dev.gravesown.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModRecipes;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** Exact 4x4 shaped recipe used only by the Gravework Bench. */
public final class GraveworkRecipe implements Recipe<CraftingInput> {
    private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
        if (pattern.size() != 4 || pattern.stream().anyMatch(row -> row.length() != 4)) {
            return DataResult.error(() -> "Gravework recipes require exactly four rows of four symbols");
        }
        return DataResult.success(List.copyOf(pattern));
    }, Function.identity());

    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final List<String> sourcePattern;
    private final Map<String, Ingredient> sourceKey;

    public GraveworkRecipe(List<String> pattern, Map<String, Ingredient> key, ItemStack result) {
        this.sourcePattern = List.copyOf(pattern);
        this.sourceKey = Map.copyOf(key);
        this.ingredients = NonNullList.withSize(16, Ingredient.EMPTY);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                String symbol = String.valueOf(pattern.get(y).charAt(x));
                if (!" ".equals(symbol)) {
                    Ingredient ingredient = key.get(symbol);
                    if (ingredient == null) {
                        throw new IllegalArgumentException("Undefined Gravework recipe symbol: " + symbol);
                    }
                    this.ingredients.set(x + y * 4, ingredient);
                }
            }
        }
        this.result = result;
    }

    private GraveworkRecipe(NonNullList<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
        this.sourcePattern = List.of("ABCD", "EFGH", "IJKL", "MNOP");
        java.util.LinkedHashMap<String, Ingredient> generatedKey = new java.util.LinkedHashMap<>();
        for (int index = 0; index < 16; index++) {
            generatedKey.put(String.valueOf((char)('A' + index)), ingredients.get(index));
        }
        this.sourceKey = Map.copyOf(generatedKey);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 4 || input.height() != 4) {
            return false;
        }
        for (int index = 0; index < 16; index++) {
            if (!this.ingredients.get(index).test(input.getItem(index))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 4 && height >= 4;
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
        return new ItemStack(ModBlocks.GRAVEWORK_BENCH.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GRAVEWORK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GRAVEWORK_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<GraveworkRecipe> {
        private final MapCodec<GraveworkRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PATTERN_CODEC.fieldOf("pattern").forGetter(recipe -> recipe.sourcePattern),
                Codec.unboundedMap(Codec.STRING, Ingredient.CODEC).fieldOf("key")
                        .forGetter(recipe -> recipe.sourceKey),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, GraveworkRecipe::new));

        private final StreamCodec<RegistryFriendlyByteBuf, GraveworkRecipe> streamCodec = StreamCodec.of(
                (buffer, recipe) -> {
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    }
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                },
                buffer -> {
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(16, Ingredient.EMPTY);
                    for (int index = 0; index < 16; index++) {
                        ingredients.set(index, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    }
                    return new GraveworkRecipe(ingredients, ItemStack.STREAM_CODEC.decode(buffer));
                }
        );

        @Override
        public MapCodec<GraveworkRecipe> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GraveworkRecipe> streamCodec() {
            return this.streamCodec;
        }
    }

}
