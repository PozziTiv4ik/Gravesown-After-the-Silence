package dev.gravesown.blockentity;

import dev.gravesown.menu.FieldKitchenMenu;
import dev.gravesown.recipe.FieldKitchenRecipe;
import dev.gravesown.recipe.FieldKitchenRecipeInput;
import dev.gravesown.registry.ModBlockEntities;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModRecipes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FieldKitchenBlockEntity extends BaseContainerBlockEntity {
    public static final int SIZE = 6;
    public static final int RESULT_SLOT = 5;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public FieldKitchenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIELD_KITCHEN.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.gravesown.field_kitchen");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FieldKitchenMenu(containerId, inventory, this);
    }

    public void updateResult() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        RecipeHolder<FieldKitchenRecipe> recipe = currentRecipe();
        this.items.set(RESULT_SLOT, recipe == null
                ? ItemStack.EMPTY
                : recipe.value().assemble(input(), this.level.registryAccess()));
        this.setChanged();
    }

    public void takeResult(Player player) {
        if (this.level == null || this.level.isClientSide || currentRecipe() == null) {
            return;
        }
        for (int slot = 0; slot < 3; slot++) {
            ItemStack ingredient = this.items.get(slot);
            if (ingredient.is(ModItems.GLOAMWATER_BUCKET.get())) {
                this.items.set(slot, new ItemStack(Items.BUCKET));
            }
            else {
                ingredient.shrink(1);
            }
        }
        wearTool(3);
        wearTool(4);
        updateResult();
    }

    private void wearTool(int slot) {
        ItemStack tool = this.items.get(slot);
        if (!tool.isDamageableItem()) {
            return;
        }
        int damage = tool.getDamageValue() + 1;
        if (damage >= tool.getMaxDamage()) {
            tool.shrink(1);
        }
        else {
            tool.setDamageValue(damage);
        }
    }

    private RecipeHolder<FieldKitchenRecipe> currentRecipe() {
        return this.level == null ? null : this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.FIELD_KITCHEN_TYPE.get(), input(), this.level)
                .orElse(null);
    }

    private FieldKitchenRecipeInput input() {
        return new FieldKitchenRecipeInput(List.of(
                this.items.get(0), this.items.get(1), this.items.get(2),
                this.items.get(3), this.items.get(4)
        ));
    }
}
