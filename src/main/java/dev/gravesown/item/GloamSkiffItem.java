package dev.gravesown.item;

import dev.gravesown.entity.GloamSkiff;
import dev.gravesown.registry.ModEntities;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Places the Gravesown skiff while preserving vanilla collision and anti-clipping checks. */
public final class GloamSkiffItem extends Item {
    private static final Predicate<Entity> PICKABLE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public GloamSkiffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 view = player.getViewVector(1.0F);
        List<Entity> entities = level.getEntities(
                player,
                player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D),
                PICKABLE
        );
        Vec3 eye = player.getEyePosition();
        for (Entity entity : entities) {
            if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(eye)) {
                return InteractionResultHolder.pass(stack);
            }
        }

        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 location = hit.getLocation();
        GloamSkiff skiff = ModEntities.GLOAM_SKIFF.get().create(level);
        if (skiff == null) {
            return InteractionResultHolder.fail(stack);
        }
        skiff.setPos(location.x, location.y, location.z);
        skiff.setYRot(player.getYRot());
        if (level instanceof ServerLevel serverLevel) {
            EntityType.createDefaultStackConfig(serverLevel, stack, player).accept(skiff);
        }
        if (!level.noCollision(skiff, skiff.getBoundingBox())) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide()) {
            level.addFreshEntity(skiff);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, location);
            stack.consume(1, player);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
