package dev.gravesown.entity;

import dev.gravesown.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A small carrion hunter whose rigid jaw and exposed fin-spines telegraph its
 * short rush. All targeting and damage resolution remain server-owned.
 */
public final class Rotfin extends GloamwaterFish {
    public Rotfin(EntityType<? extends Rotfin> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.82D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public static boolean checkSpawnRules(
            EntityType<Rotfin> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return ModFluids.GLOAMWATER.get().isSame(level.getFluidState(pos).getType())
                && ModFluids.GLOAMWATER.get().isSame(level.getFluidState(pos.below()).getType())
                && random.nextInt(3) == 0;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                true,
                false,
                living -> living instanceof Player player
                        && !player.isCreative()
                        && !player.isSpectator()
                        && isGloamwater(player.level().getFluidState(player.blockPosition()))
        ));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SALMON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SALMON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SALMON_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

}
