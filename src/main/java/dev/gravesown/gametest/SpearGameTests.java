package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.HollowGrazer;
import dev.gravesown.entity.ThrownSpear;
import dev.gravesown.item.HushstoneSpearItem;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.GameType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpearGameTests {
    private SpearGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void survivalThrowTransfersExactlyOneDamagedSpearToTheProjectile(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        BlockPos origin = helper.absolutePos(new BlockPos(2, 2, 2));
        player.setPos(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D);
        ItemStack held = new ItemStack(ModItems.HUSHSTONE_SPEAR.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, held);

        HushstoneSpearItem item = ModItems.HUSHSTONE_SPEAR.get();
        item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        item.releaseUsing(
                held,
                helper.getLevel(),
                player,
                item.getUseDuration(held, player) - HushstoneSpearItem.THROW_THRESHOLD_TICKS
        );

        AABB search = new AABB(origin).inflate(16.0D);
        List<ThrownSpear> projectiles = helper.getLevel().getEntitiesOfClass(ThrownSpear.class, search);
        helper.assertTrue(player.getMainHandItem().isEmpty(),
                "A survival throw must remove the spear stack from the player's hand");
        helper.assertTrue(projectiles.size() == 1,
                "A survival throw must create exactly one server-owned spear projectile");
        ThrownSpear projectile = projectiles.getFirst();
        helper.assertTrue(projectile.pickup == AbstractArrow.Pickup.ALLOWED,
                "The survival projectile must be recoverable by its owner");
        helper.assertTrue(projectile.getWeaponItem().is(ModItems.HUSHSTONE_SPEAR.get())
                        && projectile.getWeaponItem().getCount() == 1
                        && projectile.getWeaponItem().getDamageValue() == 1,
                "The recoverable projectile must retain exactly one spear with one throw durability spent");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 60)
    public static void thrownSpearImpactDealsDamageAndAppliesTenSecondHeavySlow(GameTestHelper helper) {
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        owner.setGameMode(GameType.SURVIVAL);
        BlockPos start = helper.absolutePos(new BlockPos(2, 2, 2));
        owner.setPos(start.getX() + 0.5D, start.getY(), start.getZ() + 0.5D);
        HollowGrazer target = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 6, 2, 2);
        float startingHealth = target.getHealth();

        ThrownSpear spear = new ThrownSpear(helper.getLevel(), owner,
                new ItemStack(ModItems.HUSHSTONE_SPEAR.get()));
        Vec3 targetCenter = target.getBoundingBox().getCenter();
        spear.setPos(targetCenter.x - 1.0D, targetCenter.y, targetCenter.z);
        spear.shoot(1.0D, 0.0D, 0.0D, 1.5F, 0.0F);
        helper.getLevel().addFreshEntity(spear);

        helper.succeedWhen(() -> {
            MobEffectInstance slow = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            helper.assertTrue(target.getHealth() < startingHealth,
                    "A thrown Hushstone Spear must deal server-side impact damage");
            helper.assertTrue(slow != null, "A successful spear hit must apply movement slowdown");
            helper.assertTrue(ThrownSpear.SLOW_DURATION_TICKS == 200,
                    "The reviewed slowdown duration must remain exactly ten seconds");
            helper.assertTrue(slow.getAmplifier() == ThrownSpear.SLOW_AMPLIFIER
                            && slow.getDuration() >= 190 && slow.getDuration() <= 200,
                    "The target must receive the reviewed heavy slowdown at impact");
        });
    }
}
