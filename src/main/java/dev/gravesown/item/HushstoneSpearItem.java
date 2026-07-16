package dev.gravesown.item;

import dev.gravesown.entity.ThrownSpear;
import dev.gravesown.registry.ModEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Recoverable early-game spear with a deliberate wind-up and a server-owned projectile. */
public final class HushstoneSpearItem extends Item implements ProjectileItem {
    public static final int THROW_THRESHOLD_TICKS = 10;
    public static final float THROW_POWER = 2.5F;
    public static final float MELEE_DAMAGE = 7.0F;
    public static final float THROWN_DAMAGE = 7.0F;

    public HushstoneSpearItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, MELEE_DAMAGE - 1.0F,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.7F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72_000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isTooDamagedToUse(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        if (!(user instanceof Player player)) {
            return;
        }
        int chargeTicks = this.getUseDuration(stack, user) - timeLeft;
        if (chargeTicks < THROW_THRESHOLD_TICKS || isTooDamagedToUse(stack)) {
            return;
        }

        if (!level.isClientSide) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(user.getUsedItemHand()));
            ThrownSpear spear = new ThrownSpear(level, player, stack);
            spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, THROW_POWER, 1.0F);
            if (player.hasInfiniteMaterials()) {
                spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(spear);
            level.playSound(null, spear, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 0.9F, 0.82F);
            if (!player.hasInfiniteMaterials()) {
                // The projectile already owns a one-count copy. Shrinking the
                // actual hand stack is deterministic for players and automation
                // and cannot leave a duplicate reference in a selected slot.
                stack.shrink(1);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownSpear spear = new ThrownSpear(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
        spear.pickup = AbstractArrow.Pickup.ALLOWED;
        return spear;
    }

    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }
}
