package dev.gravesown.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FieldKitchenRecipeInput(List<ItemStack> stacks) implements RecipeInput {
    public FieldKitchenRecipeInput {
        if (stacks.size() != 5) {
            throw new IllegalArgumentException("Field Kitchen input requires five slots");
        }
        stacks = List.copyOf(stacks);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.stacks.get(index);
    }

    @Override
    public int size() {
        return this.stacks.size();
    }
}
