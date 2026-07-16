package dev.gravesown.item;

import javax.annotation.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

/** Dense corpse tallow is the first compact fuel for the Pitch Kiln. */
public final class GraveTallowItem extends Item {
    public GraveTallowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return 800;
    }
}
