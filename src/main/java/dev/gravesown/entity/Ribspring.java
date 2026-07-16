package dev.gravesown.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A small herd herbivore that survives by noticing danger early and sprinting
 * through Ribroot undergrowth. Ribsprings never acquire attack targets.
 */
public final class Ribspring extends Animal {
    private static final float PLAYER_FLEE_DISTANCE = 12.0F;
    private static final float PREDATOR_FLEE_DISTANCE = 10.0F;

    public Ribspring(EntityType<? extends Ribspring> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 18.0D);
    }

    public static boolean checkSpawnRules(
            EntityType<Ribspring> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.65D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(
                this,
                Player.class,
                PLAYER_FLEE_DISTANCE,
                1.25D,
                1.65D
        ));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                this,
                HollowGrazer.class,
                PREDATOR_FLEE_DISTANCE,
                1.25D,
                1.6D
        ));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(
                this,
                Monster.class,
                PREDATOR_FLEE_DISTANCE,
                1.2D,
                1.55D
        ));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.85D) {
            @Override
            public boolean canUse() {
                return Ribspring.this.level().isDay() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return Ribspring.this.level().isDay() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    @Nullable
    public Ribspring getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        Entity offspring = this.getType().create(level);
        return offspring instanceof Ribspring ribspring ? ribspring : null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.GOAT_STEP, 0.12F, 1.2F);
    }
}
