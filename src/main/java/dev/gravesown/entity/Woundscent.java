package dev.gravesown.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * A blind Suture Mire predator that follows the scent of living prey. Healthy
 * players are noticed only nearby, while wounded players can be followed across
 * its full vanilla follow range. The pursuit goal pauses periodically to make
 * the creature readable without performing any broad per-tick entity scans.
 */
public final class Woundscent extends Monster {
    private static final float WOUNDED_HEALTH_RATIO = 0.75F;
    private static final double HEALTHY_SCENT_RANGE = 12.0D;

    public Woundscent(EntityType<? extends Woundscent> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1D);
    }

    public static boolean checkSpawnRules(
            EntityType<Woundscent> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PausingPursuitGoal(this, 1.15D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(Woundscent.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                false,
                false,
                this::canFollowScent
        ));
    }

    public boolean canFollowScent(LivingEntity target) {
        if (!(target instanceof Player player) || player.isCreative() || player.isSpectator()) {
            return false;
        }

        double scentRange = player.getHealth() <= player.getMaxHealth() * WOUNDED_HEALTH_RATIO
                ? this.getAttributeValue(Attributes.FOLLOW_RANGE)
                : HEALTHY_SCENT_RANGE;
        return this.distanceToSqr(player) <= scentRange * scentRange;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.HOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 0.7F);
    }

    private static final class PausingPursuitGoal extends MeleeAttackGoal {
        private static final int MIN_CHASE_TICKS = 35;
        private static final int CHASE_TICK_VARIANCE = 25;
        private static final int PAUSE_TICKS = 12;

        private final Woundscent hunter;
        private int chaseTicks;
        private int nextPauseTick;
        private int pauseTicks;

        private PausingPursuitGoal(Woundscent hunter, double speedModifier) {
            super(hunter, speedModifier, true);
            this.hunter = hunter;
        }

        @Override
        public void start() {
            super.start();
            this.chaseTicks = 0;
            this.pauseTicks = 0;
            this.nextPauseTick = MIN_CHASE_TICKS + this.hunter.getRandom().nextInt(CHASE_TICK_VARIANCE);
        }

        @Override
        public void stop() {
            super.stop();
            this.chaseTicks = 0;
            this.pauseTicks = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.hunter.getTarget();
            if (this.pauseTicks > 0 && target != null) {
                this.hunter.getNavigation().stop();
                this.hunter.getLookControl().setLookAt(target, 30.0F, 30.0F);
                this.pauseTicks--;
                return;
            }

            super.tick();
            this.chaseTicks++;
            if (target != null
                    && this.chaseTicks >= this.nextPauseTick
                    && this.hunter.distanceToSqr(target) > 9.0D) {
                this.pauseTicks = PAUSE_TICKS;
                this.chaseTicks = 0;
                this.nextPauseTick = MIN_CHASE_TICKS
                        + this.hunter.getRandom().nextInt(CHASE_TICK_VARIANCE);
            }
        }
    }
}
