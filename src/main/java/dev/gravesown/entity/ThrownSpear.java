package dev.gravesown.entity;

import dev.gravesown.item.HushstoneSpearItem;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Server-authoritative recoverable spear projectile. */
public final class ThrownSpear extends AbstractArrow {
    public static final int SLOW_DURATION_TICKS = 10 * 20;
    public static final int SLOW_AMPLIFIER = 4;

    private static final EntityDataAccessor<Boolean> FOIL =
            SynchedEntityData.defineId(ThrownSpear.class, EntityDataSerializers.BOOLEAN);

    private boolean dealtDamage;

    public ThrownSpear(EntityType<? extends ThrownSpear> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownSpear(Level level, LivingEntity shooter, ItemStack pickupStack) {
        super(ModEntities.THROWN_SPEAR.get(), shooter, level, pickupStack.copyWithCount(1), pickupStack);
        this.entityData.set(FOIL, pickupStack.hasFoil());
    }

    public ThrownSpear(Level level, double x, double y, double z, ItemStack pickupStack) {
        super(ModEntities.THROWN_SPEAR.get(), x, y, z, level, pickupStack.copyWithCount(1), pickupStack);
        this.entityData.set(FOIL, pickupStack.hasFoil());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FOIL, false);
    }

    public boolean isFoil() {
        return this.entityData.get(FOIL);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 start, Vec3 end) {
        return this.dealtDamage ? null : super.findHitEntity(start, end);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().trident(this, owner == null ? this : owner);
        float damage = HushstoneSpearItem.THROWN_DAMAGE;
        if (this.level() instanceof ServerLevel serverLevel) {
            damage = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), target, source, damage);
        }

        this.dealtDamage = true;
        if (target.hurt(source, damage)) {
            if (this.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, this.getWeaponItem());
            }
            if (target instanceof LivingEntity livingTarget) {
                livingTarget.addEffect(
                        new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION_TICKS, SLOW_AMPLIFIER),
                        owner
                );
                this.doKnockback(livingTarget, source);
                this.doPostHurtEffects(livingTarget);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 0.82F);
    }

    @Override
    protected void hitBlockEnchantmentEffects(ServerLevel level, BlockHitResult hitResult, ItemStack stack) {
        Vec3 location = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
        EnchantmentHelper.onHitBlock(
                level,
                stack,
                this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null,
                this,
                null,
                location,
                level.getBlockState(hitResult.getBlockPos()),
                ignored -> this.kill()
        );
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.HUSHSTONE_SPEAR.get());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.dealtDamage = tag.getBoolean("DealtDamage");
        this.entityData.set(FOIL, this.getPickupItemStackOrigin().hasFoil());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
