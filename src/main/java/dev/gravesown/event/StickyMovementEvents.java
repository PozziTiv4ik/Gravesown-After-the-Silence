package dev.gravesown.event;

import dev.gravesown.Gravesown;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/** Keeps sticky Gravesown terrain slow without reducing a player's full-block jump. */
public final class StickyMovementEvents {
    private static final float DEFAULT_JUMP_FACTOR = 1.0F;

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            restoreUnrestrictedJump(player);
        }
    }

    /**
     * Testable common-side rule used after vanilla has applied the supporting
     * block's jump factor. The logical server enforces the same rule mirrored by
     * local movement prediction; horizontal stickiness deliberately remains intact.
     */
    public static boolean restoreUnrestrictedJump(Player player) {
        BlockState movementState = player.level().getBlockState(player.blockPosition());
        if (movementState.getBlock().getJumpFactor() == DEFAULT_JUMP_FACTOR) {
            movementState = player.level().getBlockState(player.getBlockPosBelowThatAffectsMyMovement());
        }

        Block movementBlock = movementState.getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(movementBlock);
        if (blockId == null
                || !Gravesown.MOD_ID.equals(blockId.getNamespace())
                || movementBlock.getJumpFactor() >= DEFAULT_JUMP_FACTOR) {
            return false;
        }

        double unrestrictedJump = player.getAttributeValue(Attributes.JUMP_STRENGTH) + player.getJumpBoostPower();
        Vec3 movement = player.getDeltaMovement();
        if (movement.y + 1.0E-6D >= unrestrictedJump) {
            return false;
        }

        player.setDeltaMovement(movement.x, unrestrictedJump, movement.z);
        player.hasImpulse = true;
        return true;
    }
}
