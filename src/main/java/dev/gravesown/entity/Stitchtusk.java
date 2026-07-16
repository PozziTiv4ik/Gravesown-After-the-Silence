package dev.gravesown.entity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * A slow territorial Marrow Rifts animal whose straight-line charge is decided
 * and executed by the logical server. The synced state is visual information
 * only: clients never select targets, apply damage or steer the charge.
 */
public final class Stitchtusk extends PathfinderMob {
    public static final int CHARGE_STATE_IDLE = 0;
    public static final int CHARGE_STATE_TELEGRAPH = 1;
    public static final int CHARGE_STATE_RUSHING = 2;
    public static final int TELEGRAPH_TICKS = 24;
    public static final int RUSH_TICKS = 18;

    private static final double MIN_CHARGE_DISTANCE_SQR = 25.0D;
    private static final double MAX_CHARGE_DISTANCE_SQR = 256.0D;
    private static final double CHARGE_SPEED = 1.05D;
    private static final EntityDataAccessor<Integer> DATA_CHARGE_STATE =
            SynchedEntityData.defineId(Stitchtusk.class, EntityDataSerializers.INT);

    private int chargeCooldownTicks;

    public Stitchtusk(EntityType<? extends Stitchtusk> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 42.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.19D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 22.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.55D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.35D);
    }

    public static boolean checkSpawnRules(
            EntityType<Stitchtusk> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Mob.checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CHARGE_STATE, CHARGE_STATE_IDLE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TelegraphChargeGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.72D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                true,
                false,
                target -> target instanceof Player player && !player.isCreative() && !player.isSpectator()
        ));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.chargeCooldownTicks > 0) {
            --this.chargeCooldownTicks;
        }
    }

    public int getChargeState() {
        return this.entityData.get(DATA_CHARGE_STATE);
    }

    public boolean isTelegraphingCharge() {
        return this.getChargeState() == CHARGE_STATE_TELEGRAPH;
    }

    public boolean isRushing() {
        return this.getChargeState() == CHARGE_STATE_RUSHING;
    }

    public int getChargeCooldownTicks() {
        return this.chargeCooldownTicks;
    }

    private void setChargeState(int state) {
        this.entityData.set(DATA_CHARGE_STATE, state);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.35F, 0.72F);
    }

    private final class TelegraphChargeGoal extends Goal {
        private int telegraphTicks;
        private int rushTicks;
        private boolean struckTarget;
        private Vec3 rushDirection = Vec3.ZERO;

        private TelegraphChargeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Stitchtusk.this.getTarget();
            if (target == null
                    || !target.isAlive()
                    || Stitchtusk.this.chargeCooldownTicks > 0
                    || !Stitchtusk.this.onGround()
                    || !Stitchtusk.this.getSensing().hasLineOfSight(target)) {
                return false;
            }

            double distanceSqr = Stitchtusk.this.distanceToSqr(target);
            return distanceSqr >= MIN_CHARGE_DISTANCE_SQR && distanceSqr <= MAX_CHARGE_DISTANCE_SQR;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = Stitchtusk.this.getTarget();
            return target != null
                    && target.isAlive()
                    && (this.telegraphTicks > 0 || this.rushTicks > 0);
        }

        @Override
        public void start() {
            this.telegraphTicks = TELEGRAPH_TICKS;
            this.rushTicks = RUSH_TICKS;
            this.struckTarget = false;
            this.rushDirection = Vec3.ZERO;
            Stitchtusk.this.getNavigation().stop();
            Stitchtusk.this.setAggressive(true);
            Stitchtusk.this.setChargeState(CHARGE_STATE_TELEGRAPH);
            Stitchtusk.this.playSound(SoundEvents.RAVAGER_ROAR, 0.8F, 0.68F);
        }

        @Override
        public void stop() {
            Stitchtusk.this.setChargeState(CHARGE_STATE_IDLE);
            Stitchtusk.this.chargeCooldownTicks = 70 + Stitchtusk.this.getRandom().nextInt(41);
            Stitchtusk.this.setAggressive(Stitchtusk.this.getTarget() != null);
            this.telegraphTicks = 0;
            this.rushTicks = 0;
            this.rushDirection = Vec3.ZERO;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = Stitchtusk.this.getTarget();
            if (target == null) {
                return;
            }

            if (this.telegraphTicks > 0) {
                Stitchtusk.this.getNavigation().stop();
                Stitchtusk.this.setDeltaMovement(0.0D, Stitchtusk.this.getDeltaMovement().y, 0.0D);
                Stitchtusk.this.getLookControl().setLookAt(target, 30.0F, 20.0F);
                --this.telegraphTicks;
                if (this.telegraphTicks == 0) {
                    Vec3 horizontalAim = new Vec3(
                            target.getX() - Stitchtusk.this.getX(),
                            0.0D,
                            target.getZ() - Stitchtusk.this.getZ()
                    );
                    if (horizontalAim.lengthSqr() < 0.01D) {
                        this.rushTicks = 0;
                        return;
                    }

                    this.rushDirection = horizontalAim.normalize();
                    Stitchtusk.this.setChargeState(CHARGE_STATE_RUSHING);
                    Stitchtusk.this.playSound(SoundEvents.RAVAGER_ATTACK, 0.9F, 0.78F);
                }
                return;
            }

            if (this.rushTicks <= 0) {
                return;
            }

            Stitchtusk.this.setDeltaMovement(
                    this.rushDirection.x * CHARGE_SPEED,
                    Stitchtusk.this.getDeltaMovement().y,
                    this.rushDirection.z * CHARGE_SPEED
            );
            Stitchtusk.this.hasImpulse = true;
            Stitchtusk.this.setYRot((float) (Mth.atan2(this.rushDirection.z, this.rushDirection.x)
                    * (180.0D / Math.PI)) - 90.0F);
            Stitchtusk.this.yBodyRot = Stitchtusk.this.getYRot();

            if (!this.struckTarget
                    && Stitchtusk.this.getBoundingBox().inflate(0.6D).intersects(target.getBoundingBox())) {
                this.struckTarget = Stitchtusk.this.doHurtTarget(target);
                if (this.struckTarget) {
                    target.knockback(
                            1.6D,
                            Stitchtusk.this.getX() - target.getX(),
                            Stitchtusk.this.getZ() - target.getZ()
                    );
                }
            }

            --this.rushTicks;
            if (Stitchtusk.this.horizontalCollision) {
                this.rushTicks = 0;
            }
        }
    }
}
