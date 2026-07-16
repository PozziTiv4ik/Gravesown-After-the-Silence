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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/** A peaceful bottom fish that noses through drowned roots and silt. */
public final class Rootskimmer extends GloamwaterFish {
    public Rootskimmer(EntityType<? extends Rootskimmer> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.56D)
                .add(Attributes.FOLLOW_RANGE, 8.0D);
    }

    public static boolean checkSpawnRules(
            EntityType<Rootskimmer> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return ModFluids.GLOAMWATER.get().isSame(level.getFluidState(pos).getType())
                && ModFluids.GLOAMWATER.get().isSame(level.getFluidState(pos.below()).getType());
    }

    @Override
    protected boolean prefersBottomWater() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }
}
