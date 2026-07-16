package dev.gravesown.blockentity;

import dev.gravesown.registry.ModBlockEntities;
import dev.gravesown.menu.PitchKilnMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PitchKilnBlockEntity extends AbstractFurnaceBlockEntity {
    public PitchKilnBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PITCH_KILN.get(), pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.gravesown.pitch_kiln");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PitchKilnMenu(containerId, inventory, this, this.dataAccess);
    }
}
