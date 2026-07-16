package dev.gravesown.menu;

import dev.gravesown.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;

/** Dedicated menu id so the Pitch Kiln can use the shared Gravesown container skin. */
public final class PitchKilnMenu extends AbstractFurnaceMenu {
    public PitchKilnMenu(int containerId, Inventory inventory) {
        super(ModMenus.PITCH_KILN.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, containerId, inventory);
    }

    public PitchKilnMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.PITCH_KILN.get(), RecipeType.SMELTING, RecipeBookType.FURNACE,
                containerId, inventory, container, data);
    }
}
