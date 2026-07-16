package dev.gravesown.entity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Server-authoritative behavior shared by the native land animals. The four
 * archetypes have deliberately different goals: prey flee, gliders hop and
 * slow-fall, neutral animals retaliate, and predators stalk bounded targets.
 */
public final class NativeFauna extends Animal {
    private final NativeFaunaSpecies species;

    public NativeFauna(
            EntityType<? extends NativeFauna> entityType,
            Level level,
            NativeFaunaSpecies species
    ) {
        super(entityType, level);
        this.species = species;
        this.xpReward = species.archetype() == NativeFaunaSpecies.Archetype.PREDATOR ? 5 : 2;
        // Mob invokes the overridable goal hook from its superclass constructor,
        // before this immutable biological profile can be assigned. The guarded
        // first call is intentionally empty; install the reviewed profile now.
        this.registerGoals();
    }

    public NativeFaunaSpecies species() {
        return this.species;
    }

    public static AttributeSupplier.Builder createAttributes(NativeFaunaSpecies species) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, species.health())
                .add(Attributes.MOVEMENT_SPEED, species.movementSpeed())
                .add(Attributes.FOLLOW_RANGE, species.followRange())
                .add(Attributes.ATTACK_DAMAGE, species.attackDamage());
    }

    public static boolean checkSpawnRules(
            EntityType<NativeFauna> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void registerGoals() {
        if (this.species == null) {
            return;
        }
        this.goalSelector.addGoal(0, new FloatGoal(this));
        switch (this.species.archetype()) {
            case PREY -> this.registerPreyGoals();
            case FLYER -> this.registerFlyerGoals();
            case NEUTRAL -> this.registerNeutralGoals();
            case PREDATOR -> this.registerPredatorGoals();
        }
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    private void registerPreyGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.55D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 9.0F, 1.2D, 1.55D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 10.0F, 1.25D, 1.6D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.85D));
    }

    private void registerFlyerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.45D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.15D, 1.45D));
        this.goalSelector.addGoal(4, new FlutterHopGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.95D));
    }

    private void registerNeutralGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.08D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.78D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    private void registerPredatorGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.12D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.82D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        if (this.species.aggressiveToPlayers()) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                    this,
                    Player.class,
                    10,
                    true,
                    false,
                    target -> target instanceof Player player && !player.isCreative() && !player.isSpectator()
            ));
        }
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this,
                NativeFauna.class,
                10,
                true,
                false,
                target -> target instanceof NativeFauna fauna && fauna.species.isPrey()
        ));
    }

    @Override
    public void aiStep() {
        if (this.species.archetype() == NativeFaunaSpecies.Archetype.FLYER
                && !this.onGround()
                && this.getDeltaMovement().y < -0.08D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.58D, 0.98D));
        }
        super.aiStep();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    @Nullable
    public NativeFauna getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        Entity offspring = this.getType().create(level);
        return offspring instanceof NativeFauna fauna ? fauna : null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return switch (this.species.archetype()) {
            case FLYER -> SoundEvents.PARROT_AMBIENT;
            case PREDATOR -> SoundEvents.FOX_AMBIENT;
            case NEUTRAL -> SoundEvents.GOAT_AMBIENT;
            case PREY -> SoundEvents.RABBIT_AMBIENT;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (this.species.archetype()) {
            case FLYER -> SoundEvents.PARROT_HURT;
            case PREDATOR -> SoundEvents.FOX_HURT;
            case NEUTRAL -> SoundEvents.GOAT_HURT;
            case PREY -> SoundEvents.RABBIT_HURT;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return switch (this.species.archetype()) {
            case FLYER -> SoundEvents.PARROT_DEATH;
            case PREDATOR -> SoundEvents.FOX_DEATH;
            case NEUTRAL -> SoundEvents.GOAT_DEATH;
            case PREY -> SoundEvents.RABBIT_DEATH;
        };
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.GRASS_STEP, 0.11F, this.species.archetype() == NativeFaunaSpecies.Archetype.FLYER ? 1.35F : 0.9F);
    }

    private static final class FlutterHopGoal extends Goal {
        private final NativeFauna bird;
        private int cooldown;

        private FlutterHopGoal(NativeFauna bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.bird.onGround()
                    && this.bird.getTarget() == null
                    && this.cooldown-- <= 0
                    && this.bird.getRandom().nextInt(12) == 0;
        }

        @Override
        public void start() {
            double angle = this.bird.getRandom().nextDouble() * Math.PI * 2.0D;
            this.bird.setDeltaMovement(Math.cos(angle) * 0.22D, 0.42D, Math.sin(angle) * 0.22D);
            this.bird.hasImpulse = true;
            this.cooldown = 35 + this.bird.getRandom().nextInt(45);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}
