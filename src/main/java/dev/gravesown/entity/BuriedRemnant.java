package dev.gravesown.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A soil-and-bone cadaver assembled beneath old Gravesown markers. Grave
 * emergence is server-owned; the synced counter only drives its client pose.
 */
public final class BuriedRemnant extends Monster {
    public static final int EMERGENCE_DURATION = 108;
    public static final double EMERGENCE_DEPTH = 1.8D;
    private static final String EMERGENCE_TAG = "EmergenceTicks";
    private static final String EMERGENCE_TARGET_Y_TAG = "EmergenceTargetY";
    private static final EntityDataAccessor<Integer> DATA_EMERGENCE_TICKS =
            SynchedEntityData.defineId(BuriedRemnant.class, EntityDataSerializers.INT);
    private double emergenceTargetY;

    public BuriedRemnant(EntityType<? extends BuriedRemnant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 5.5D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.22D);
    }

    public static boolean checkSpawnRules(
            EntityType<BuriedRemnant> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_EMERGENCE_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.12D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.72D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 9.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(BuriedRemnant.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                true,
                false,
                target -> target instanceof Player player && !player.isCreative() && !player.isSpectator()
        ));
    }

    public void beginEmergence() {
        this.beginEmergence(this.getY());
    }

    public void beginEmergence(double targetY) {
        this.emergenceTargetY = targetY;
        this.setEmergenceTicks(EMERGENCE_DURATION);
        this.setNoAi(true);
        this.setInvulnerable(true);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setPos(this.getX(), targetY - EMERGENCE_DEPTH, this.getZ());
        this.setAggressive(false);
        this.setTarget(null);
        this.getNavigation().stop();
    }

    public int getEmergenceTicks() {
        return this.entityData.get(DATA_EMERGENCE_TICKS);
    }

    public boolean isEmerging() {
        return this.getEmergenceTicks() > 0;
    }

    public float getEmergenceProgress(float partialTick) {
        float remaining = Math.max(0.0F, this.getEmergenceTicks() - partialTick);
        return 1.0F - remaining / EMERGENCE_DURATION;
    }

    private void setEmergenceTicks(int ticks) {
        this.entityData.set(DATA_EMERGENCE_TICKS, Math.max(0, ticks));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() || !this.isEmerging()) {
            return;
        }

        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        int remaining = this.getEmergenceTicks() - 1;
        this.setEmergenceTicks(remaining);
        double progress = 1.0D - (double)remaining / (double)EMERGENCE_DURATION;
        double eased = progress * progress * (3.0D - 2.0D * progress);
        this.setPos(this.getX(), this.emergenceTargetY - EMERGENCE_DEPTH * (1.0D - eased), this.getZ());
        this.setDeltaMovement(Vec3.ZERO);
        if (remaining == 0) {
            this.setPos(this.getX(), this.emergenceTargetY, this.getZ());
            this.setNoAi(false);
            this.setInvulnerable(false);
            this.setNoGravity(false);
            this.noPhysics = false;
            this.playSound(SoundEvents.HUSK_AMBIENT, 0.85F, 0.72F);
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isEmerging() && super.isPushable();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(EMERGENCE_TAG, this.getEmergenceTicks());
        compound.putDouble(EMERGENCE_TARGET_Y_TAG, this.emergenceTargetY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        int ticks = Math.max(0, compound.getInt(EMERGENCE_TAG));
        this.emergenceTargetY = compound.contains(EMERGENCE_TARGET_Y_TAG)
                ? compound.getDouble(EMERGENCE_TARGET_Y_TAG)
                : this.getY();
        this.setEmergenceTicks(ticks);
        this.setNoAi(ticks > 0);
        this.setInvulnerable(ticks > 0);
        this.setNoGravity(ticks > 0);
        this.noPhysics = ticks > 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.HUSK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ROOTED_DIRT_STEP, 0.22F, 0.72F);
    }
}
