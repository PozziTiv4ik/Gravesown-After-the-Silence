package dev.gravesown.gameplay;

import dev.gravesown.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class QuietskinEffects {
    public static final double SCENT_REDUCTION_PER_PIECE = 0.125D;
    public static final int FULL_SET_SIZE = 4;

    private QuietskinEffects() {
    }

    public static int equippedPieces(LivingEntity entity) {
        int pieces = 0;
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.QUIETSKIN_HOOD.get())) {
            pieces++;
        }
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.QUIETSKIN_COAT.get())) {
            pieces++;
        }
        if (entity.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.QUIETSKIN_LEGWRAPS.get())) {
            pieces++;
        }
        if (entity.getItemBySlot(EquipmentSlot.FEET).is(ModItems.QUIETSKIN_BOOTS.get())) {
            pieces++;
        }
        return pieces;
    }

    public static boolean hasFullSet(LivingEntity entity) {
        return equippedPieces(entity) == FULL_SET_SIZE;
    }

    public static double scentDetectionMultiplier(LivingEntity entity) {
        return 1.0D - equippedPieces(entity) * SCENT_REDUCTION_PER_PIECE;
    }

    public static double modifyBloodScentRange(LivingEntity entity, double baseRange) {
        return baseRange * scentDetectionMultiplier(entity);
    }

    public static boolean isQuietskinPiece(ItemStack stack) {
        return stack.is(ModItems.QUIETSKIN_HOOD.get())
                || stack.is(ModItems.QUIETSKIN_COAT.get())
                || stack.is(ModItems.QUIETSKIN_LEGWRAPS.get())
                || stack.is(ModItems.QUIETSKIN_BOOTS.get());
    }
}
