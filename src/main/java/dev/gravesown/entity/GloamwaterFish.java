package dev.gravesown.entity;

import dev.gravesown.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

/**
 * Shared server-owned swimming and breathing contract for Gravesown fish.
 * It checks the registered fluid family directly so a data-pack tag reload
 * cannot make a fish stand still or drown inside visible Gloamwater.
 */
public abstract class GloamwaterFish extends WaterAnimal {
    protected GloamwaterFish(EntityType<? extends GloamwaterFish> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new GloamwaterMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new GloamwaterWanderGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public final boolean isInGloamwater() {
        BlockPos feet = this.blockPosition();
        BlockPos eyes = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
        return isGloamwater(this.level().getFluidState(feet))
                || isGloamwater(this.level().getFluidState(eyes));
    }

    public static boolean isGloamwater(FluidState state) {
        return ModFluids.GLOAMWATER.get().isSame(state.getType());
    }

    @Override
    protected void handleAirSupply(int previousAir) {
        if (this.isAlive() && !this.isInGloamwater()) {
            this.setAirSupply(previousAir - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInGloamwater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.88D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.001D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void aiStep() {
        if (!this.isInGloamwater() && this.onGround() && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add(
                    (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F,
                    0.36D,
                    (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F
            ));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(SoundEvents.COD_FLOP, 0.7F, 0.7F);
        }
        super.aiStep();
    }

    private static final class GloamwaterMoveControl extends MoveControl {
        private final GloamwaterFish fish;

        private GloamwaterMoveControl(GloamwaterFish fish) {
            super(fish);
            this.fish = fish;
        }

        @Override
        public void tick() {
            if (this.fish.isInGloamwater()) {
                BlockPos position = this.fish.blockPosition();
                boolean waterAbove = isGloamwater(this.fish.level().getFluidState(position.above()));
                boolean waterBelow = isGloamwater(this.fish.level().getFluidState(position.below()));
                // Surface water used to receive unconditional buoyancy, making every
                // fish climb and circle. Keep them inside a depth band instead.
                if (!waterAbove) {
                    this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0D, -0.018D, 0.0D));
                } else if (!waterBelow) {
                    this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0D, 0.006D, 0.0D));
                }
            }

            if (this.operation != Operation.MOVE_TO) {
                this.fish.setSpeed(0.0F);
                this.fish.setXxa(0.0F);
                this.fish.setYya(0.0F);
                this.fish.setZza(0.0F);
                return;
            }

            double dx = this.wantedX - this.fish.getX();
            double dy = this.wantedY - this.fish.getY();
            double dz = this.wantedZ - this.fish.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            if (dx * dx + dy * dy + dz * dz < 1.65D) {
                this.operation = Operation.WAIT;
                this.fish.setSpeed(0.0F);
                this.fish.setZza(0.0F);
                return;
            }
            if (horizontal < 1.0E-5D && Math.abs(dy) < 1.0E-5D) {
                this.fish.setZza(0.0F);
                return;
            }

            float wantedYaw = (float)(Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
            this.fish.setYRot(this.rotlerp(this.fish.getYRot(), wantedYaw, 35.0F));
            this.fish.yBodyRot = this.fish.getYRot();
            this.fish.yHeadRot = this.fish.getYRot();

            float speed = (float)(this.speedModifier * this.fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.fish.setSpeed(Mth.lerp(0.16F, this.fish.getSpeed(), speed * 0.24F));
            float pitch = -((float)(Mth.atan2(dy, Math.max(horizontal, 1.0E-5D)) * Mth.RAD_TO_DEG));
            pitch = Mth.clamp(Mth.wrapDegrees(pitch), -30.0F, 30.0F);
            this.fish.setXRot(this.rotlerp(this.fish.getXRot(), pitch, 7.0F));

            float pitchCos = Mth.cos(this.fish.getXRot() * Mth.DEG_TO_RAD);
            float pitchSin = Mth.sin(this.fish.getXRot() * Mth.DEG_TO_RAD);
            this.fish.setZza(pitchCos * speed);
            this.fish.setYya(-pitchSin * speed);
        }
    }

    /** Picks only submerged destinations and therefore cannot path a fish onto a pond bank. */
    private static final class GloamwaterWanderGoal extends Goal {
        private final GloamwaterFish fish;
        private Vec3 target;
        private int remainingTicks;

        private GloamwaterWanderGoal(GloamwaterFish fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.fish.getTarget() != null || this.fish.getRandom().nextInt(10) != 0) {
                return false;
            }
            this.target = this.findTarget();
            return this.target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                    && this.remainingTicks-- > 0
                    && this.fish.isInGloamwater()
                    && this.fish.distanceToSqr(this.target) > 1.65D;
        }

        @Override
        public void start() {
            this.remainingTicks = 56;
            this.fish.getMoveControl().setWantedPosition(this.target.x, this.target.y, this.target.z, 1.0D);
        }

        @Override
        public void stop() {
            this.target = null;
        }

        private Vec3 findTarget() {
            BlockPos origin = this.fish.blockPosition();
            Vec3 bottomFallback = null;
            for (int attempt = 0; attempt < 28; attempt++) {
                BlockPos candidate = origin.offset(
                        this.fish.getRandom().nextInt(13) - 6,
                        this.fish.getRandom().nextInt(7) - 3,
                        this.fish.getRandom().nextInt(13) - 6
                );
                if (!isGloamwater(this.fish.level().getFluidState(candidate))) {
                    continue;
                }
                boolean aboveWet = isGloamwater(this.fish.level().getFluidState(candidate.above()));
                boolean belowWet = isGloamwater(this.fish.level().getFluidState(candidate.below()));
                if (!aboveWet) {
                    continue;
                }
                int wetNeighbors = 0;
                for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    if (isGloamwater(this.fish.level().getFluidState(candidate.relative(direction)))) {
                        wetNeighbors++;
                    }
                }
                if (wetNeighbors >= 3) {
                    Vec3 target = Vec3.atCenterOf(candidate);
                    if (!belowWet) {
                        bottomFallback = target;
                        if (this.fish.prefersBottomWater()) {
                            return target;
                        }
                    } else if (!this.fish.prefersBottomWater()) {
                        return target;
                    }
                }
            }
            return bottomFallback;
        }
    }

    /** Bottom species override this so their targets stay one block above the floor. */
    protected boolean prefersBottomWater() {
        return false;
    }
}
