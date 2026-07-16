package dev.gravesown.menu;

import dev.gravesown.recipe.SawmillRecipe;
import dev.gravesown.recipe.SawmillRecipeInput;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import java.util.Optional;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/** Server-authoritative one-input Sawmill menu. */
public final class SawmillMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int RESULT_SLOT = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 38;

    private final SimpleContainer input = new SimpleContainer(1);
    private final SimpleContainer result = new SimpleContainer(1);
    private final ContainerLevelAccess access;
    private final Player player;
    private long lastSoundTime;

    public SawmillMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public SawmillMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.SAWMILL.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        this.input.addListener(this::slotsChanged);

        this.addSlot(new Slot(this.input, 0, 44, 35));
        this.addSlot(new Slot(this.result, 0, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                SawmillMenu.this.consumeOneInput();
                SawmillMenu.this.playCraftSound();
                super.onTake(player, stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> updateResult(level));
    }

    private void updateResult(Level level) {
        if (level.isClientSide || !(this.player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack output = ItemStack.EMPTY;
        Optional<RecipeHolder<SawmillRecipe>> match = level.getRecipeManager().getRecipeFor(
                ModRecipes.SAWMILL_TYPE.get(),
                new SawmillRecipeInput(this.input.getItem(0)),
                level
        );
        if (match.isPresent()) {
            ItemStack assembled = match.get().value().assemble(
                    new SawmillRecipeInput(this.input.getItem(0)),
                    level.registryAccess()
            );
            if (assembled.isItemEnabled(level.enabledFeatures())) {
                output = assembled;
            }
        }
        this.result.setItem(0, output);
        this.setRemoteSlot(RESULT_SLOT, output);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                this.containerId,
                this.incrementStateId(),
                RESULT_SLOT,
                output
        ));
    }

    private void consumeOneInput() {
        this.access.execute((level, pos) -> {
            if (level.isClientSide) {
                return;
            }
            Optional<RecipeHolder<SawmillRecipe>> match = level.getRecipeManager().getRecipeFor(
                    ModRecipes.SAWMILL_TYPE.get(),
                    new SawmillRecipeInput(this.input.getItem(0)),
                    level
            );
            if (match.isPresent()) {
                this.input.removeItem(INPUT_SLOT, 1);
                updateResult(level);
            }
        });
    }

    private void playCraftSound() {
        this.access.execute((level, pos) -> {
            if (level.isClientSide) {
                return;
            }
            long gameTime = level.getGameTime();
            if (this.lastSoundTime != gameTime) {
                level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 0.92F);
                this.lastSoundTime = gameTime;
            }
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.input));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.SAWMILL.get());
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
        }
        else if (slotIndex >= INVENTORY_START) {
            if (!this.moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        }
        else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }
}
