package dev.gravesown.menu;

import dev.gravesown.blockentity.FieldKitchenBlockEntity;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class FieldKitchenMenu extends AbstractContainerMenu {
    private final Container container;

    public FieldKitchenMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(FieldKitchenBlockEntity.SIZE));
    }

    public FieldKitchenMenu(int containerId, Inventory inventory, Container container) {
        super(ModMenus.FIELD_KITCHEN.get(), containerId);
        checkContainerSize(container, FieldKitchenBlockEntity.SIZE);
        this.container = container;
        container.startOpen(inventory.player);

        this.addSlot(new Slot(container, 0, 26, 27));
        this.addSlot(new Slot(container, 1, 48, 27));
        this.addSlot(new Slot(container, 2, 70, 27));
        this.addSlot(new Slot(container, 3, 37, 58));
        this.addSlot(new Slot(container, 4, 59, 58));
        this.addSlot(new Slot(container, FieldKitchenBlockEntity.RESULT_SLOT, 134, 43) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if (FieldKitchenMenu.this.container instanceof FieldKitchenBlockEntity kitchen) {
                    kitchen.takeResult(player);
                }
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 104 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 162));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container instanceof FieldKitchenBlockEntity kitchen) {
            kitchen.updateResult();
            this.broadcastChanges();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index == FieldKitchenBlockEntity.RESULT_SLOT) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (index < FieldKitchenBlockEntity.SIZE) {
            if (!this.moveItemStackTo(original, FieldKitchenBlockEntity.SIZE, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }
        else if (original.is(ModItems.BONE_CLEAVER.get())) {
            if (!this.moveItemStackTo(original, 3, 4, false)) return ItemStack.EMPTY;
        }
        else if (original.is(ModItems.STIRRING_HOOK.get())) {
            if (!this.moveItemStackTo(original, 4, 5, false)) return ItemStack.EMPTY;
        }
        else if (!this.moveItemStackTo(original, 0, 3, false)) {
            return ItemStack.EMPTY;
        }
        if (original.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
