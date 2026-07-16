package dev.gravesown.blockentity;

import dev.gravesown.registry.ModBlockEntities;
import dev.gravesown.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ReliquaryCrateBlockEntity extends RandomizableContainerBlockEntity {
    public static final int ROWS = 4;
    public static final int SIZE = ROWS * 9;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playContainerSound(level, pos, SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playContainerSound(level, pos, SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
            level.blockEvent(pos, state.getBlock(), 1, newCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof ChestMenu chestMenu
                    && chestMenu.getContainer() == ReliquaryCrateBlockEntity.this;
        }
    };

    public ReliquaryCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RELIQUARY_CRATE.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.gravesown.reliquary_crate");
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
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ChestMenu(ModMenus.RELIQUARY_CRATE.get(), containerId, inventory, this, ROWS);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.level != null && !this.level.isClientSide) {
            this.openersCounter.incrementOpeners(player, this.level, this.worldPosition, this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.level != null && !this.level.isClientSide) {
            this.openersCounter.decrementOpeners(player, this.level, this.worldPosition, this.getBlockState());
        }
    }

    public void recheckOpeners() {
        if (!this.remove && this.level != null && !this.level.isClientSide) {
            this.openersCounter.recheckOpeners(this.level, this.worldPosition, this.getBlockState());
        }
    }

    public int getOpenerCount() {
        return this.openersCounter.getOpenerCount();
    }

    private static void playContainerSound(Level level, BlockPos pos, net.minecraft.sounds.SoundEvent sound) {
        level.playSound(
                null,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                sound,
                SoundSource.BLOCKS,
                0.5F,
                level.random.nextFloat() * 0.1F + 0.9F
        );
    }
}
