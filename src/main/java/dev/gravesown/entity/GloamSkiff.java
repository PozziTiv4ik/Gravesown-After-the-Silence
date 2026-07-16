package dev.gravesown.entity;

import dev.gravesown.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A ribroot-and-hide skiff that reuses Minecraft's proven server-authoritative boat controls. */
public final class GloamSkiff extends Boat {
    public GloamSkiff(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
        this.setVariant(Type.OAK);
    }

    @Override
    public Item getDropItem() {
        return ModItems.GLOAM_SKIFF.get();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.GLOAM_SKIFF.get());
    }
}
