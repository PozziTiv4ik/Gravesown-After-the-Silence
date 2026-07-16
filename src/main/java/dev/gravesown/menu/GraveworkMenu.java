package dev.gravesown.menu;

import dev.gravesown.recipe.GraveworkRecipe;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import java.util.Optional;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/** Server-authoritative 4x4 crafting menu without the vanilla 3x3 recipe book. */
public final class GraveworkMenu extends AbstractContainerMenu {
    private static final int RESULT_SLOT = 0;
    private static final int GRID_START = 1;
    private static final int GRID_END = 17;
    private static final int INVENTORY_START = 17;
    private static final int INVENTORY_END = 53;

    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 4, 4);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public GraveworkMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public GraveworkMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.GRAVEWORK.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 137, 45));

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                this.addSlot(new Slot(this.craftSlots, column + row * 4, 18 + column * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 108 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 166));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> updateResult(this, level, this.player, this.craftSlots, this.resultSlots));
    }

    private static void updateResult(
            GraveworkMenu menu,
            Level level,
            Player player,
            CraftingContainer craftSlots,
            ResultContainer resultSlots
    ) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        CraftingInput input = craftSlots.asCraftInput();
        ItemStack result = ItemStack.EMPTY;
        Optional<RecipeHolder<GraveworkRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipes.GRAVEWORK_TYPE.get(), input, level);
        if (match.isPresent() && resultSlots.setRecipeUsed(level, serverPlayer, match.get())) {
            ItemStack assembled = match.get().value().assemble(input, level.registryAccess());
            if (assembled.isItemEnabled(level.enabledFeatures())) {
                result = assembled;
            }
        }
        resultSlots.setItem(0, result);
        menu.setRemoteSlot(0, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                menu.containerId,
                menu.incrementStateId(),
                RESULT_SLOT,
                result
        ));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.craftSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.GRAVEWORK_BENCH.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (slotIndex == RESULT_SLOT) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else if (slotIndex >= INVENTORY_START) {
            if (!this.moveItemStackTo(stack, GRID_START, GRID_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }
}
