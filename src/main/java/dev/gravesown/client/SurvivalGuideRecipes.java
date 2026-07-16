package dev.gravesown.client;

import dev.gravesown.Gravesown;
import dev.gravesown.recipe.GraveworkRecipe;
import dev.gravesown.recipe.FieldKitchenRecipe;
import dev.gravesown.recipe.SawmillRecipe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/** Builds the guide from the recipe catalog synchronized by the current server. */
final class SurvivalGuideRecipes {
    static final List<RecipeEntry> ALL = new ArrayList<>();

    private SurvivalGuideRecipes() {
    }

    static void refresh(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.getConnection() == null) {
            ALL.clear();
            return;
        }

        HolderLookup.Provider registries = minecraft.level.registryAccess();
        List<RecipeEntry> loaded = minecraft.getConnection().getRecipeManager().getRecipes().stream()
                .filter(holder -> Gravesown.MOD_ID.equals(holder.id().getNamespace()))
                .sorted(Comparator.comparing(holder -> holder.id().toString()))
                .map(holder -> fromRecipe(holder, registries))
                .filter(entry -> entry != null)
                .toList();
        ALL.clear();
        ALL.addAll(loaded);
    }

    static int indexOfOutput(Item item) {
        for (int index = 0; index < ALL.size(); index++) {
            if (ALL.get(index).output().get().is(item)) {
                return index;
            }
        }
        return -1;
    }

    private static RecipeEntry fromRecipe(RecipeHolder<?> holder, HolderLookup.Provider registries) {
        Recipe<?> recipe = holder.value();
        ItemStack output = recipe.getResultItem(registries).copy();
        if (output.isEmpty()) {
            return null;
        }
        Supplier<ItemStack> result = fixed(output);

        if (recipe instanceof GraveworkRecipe gravework) {
            return fromIngredients("4x4", result, 4, 4, RecipeKind.SHAPED,
                    gravework.getIngredients(), 4);
        }
        if (recipe instanceof FieldKitchenRecipe kitchen) {
            return fromIngredients("kitchen", result, 3, 2, RecipeKind.KITCHEN,
                    kitchen.getIngredients(), 3);
        }
        if (recipe instanceof SawmillRecipe sawmill) {
            return fromIngredients("sawmill", result, 1, 1, RecipeKind.SAWMILL,
                    sawmill.getIngredients(), 1);
        }
        if (recipe instanceof AbstractCookingRecipe cooking) {
            return fromIngredients("kiln", result, 1, 1, RecipeKind.SMELTING,
                    cooking.getIngredients(), 1);
        }
        if (recipe instanceof ShapedRecipe shaped) {
            int gridSize = Math.max(shaped.getWidth(), shaped.getHeight()) <= 2 ? 2 : 3;
            return fromIngredients(gridSize + "x" + gridSize, result, gridSize, gridSize, RecipeKind.SHAPED,
                    shaped.getIngredients(), shaped.getWidth());
        }
        if (recipe instanceof ShapelessRecipe shapeless) {
            int gridSize = shapeless.getIngredients().size() <= 4 ? 2 : 3;
            return fromIngredients(gridSize + "x" + gridSize, result, gridSize, gridSize, RecipeKind.SHAPELESS,
                    shapeless.getIngredients(), gridSize);
        }
        return null;
    }

    private static RecipeEntry fromIngredients(
            String station,
            Supplier<ItemStack> output,
            int gridWidth,
            int gridHeight,
            RecipeKind kind,
            NonNullList<Ingredient> ingredients,
            int sourceWidth
    ) {
        List<Supplier<ItemStack>> cells = emptyGrid(gridWidth, gridHeight);
        for (int index = 0; index < ingredients.size(); index++) {
            int sourceRow = index / sourceWidth;
            int sourceColumn = index % sourceWidth;
            if (sourceRow < gridHeight && sourceColumn < gridWidth) {
                cells.set(sourceRow * gridWidth + sourceColumn, fixed(firstChoice(ingredients.get(index))));
            }
        }
        return new RecipeEntry(station, output, gridWidth, gridHeight, kind, List.copyOf(cells));
    }

    private static ItemStack firstChoice(Ingredient ingredient) {
        ItemStack[] choices = ingredient.getItems();
        return choices.length == 0 ? ItemStack.EMPTY : choices[0].copyWithCount(1);
    }

    private static Supplier<ItemStack> fixed(ItemStack stack) {
        ItemStack snapshot = stack.copy();
        return () -> snapshot.copy();
    }

    private static List<Supplier<ItemStack>> emptyGrid(int width, int height) {
        List<Supplier<ItemStack>> cells = new ArrayList<>(width * height);
        for (int index = 0; index < width * height; index++) {
            cells.add(() -> ItemStack.EMPTY);
        }
        return cells;
    }

    enum RecipeKind {
        SHAPED("screen.gravesown.codex_hub.recipe.shaped"),
        SHAPELESS("screen.gravesown.codex_hub.recipe.shapeless"),
        SMELTING("screen.gravesown.codex_hub.recipe.smelting"),
        KITCHEN("screen.gravesown.codex_hub.recipe.kitchen"),
        SAWMILL("screen.gravesown.codex_hub.recipe.sawmill");

        private final String translationKey;

        RecipeKind(String translationKey) {
            this.translationKey = translationKey;
        }

        String translationKey() {
            return this.translationKey;
        }
    }

    record RecipeEntry(
            String station,
            Supplier<ItemStack> output,
            int gridWidth,
            int gridHeight,
            RecipeKind kind,
            List<Supplier<ItemStack>> cells
    ) {
        ItemStack cell(int column, int row) {
            return this.cells.get(row * this.gridWidth + column).get();
        }

        int gridSize() {
            return Math.max(this.gridWidth, this.gridHeight);
        }

        List<ItemStack> uniqueIngredients() {
            List<ItemStack> ingredients = new ArrayList<>();
            for (Supplier<ItemStack> cell : this.cells) {
                ItemStack stack = cell.get();
                if (!stack.isEmpty() && ingredients.stream().noneMatch(existing -> existing.is(stack.getItem()))) {
                    ingredients.add(stack);
                }
            }
            return ingredients;
        }
    }
}
