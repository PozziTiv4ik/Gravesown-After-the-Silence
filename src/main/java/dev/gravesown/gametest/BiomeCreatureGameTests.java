package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.Ribspring;
import dev.gravesown.entity.Stitchtusk;
import dev.gravesown.entity.Woundscent;
import dev.gravesown.registry.ModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BiomeCreatureGameTests {
    private BiomeCreatureGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void ribspringSpawnsWithReviewedCoreAttributesAndTicks(GameTestHelper helper) {
        Ribspring creature = helper.spawnWithNoFreeWill(ModEntities.RIBSPRING.get(), 1, 1, 1);
        helper.succeedOnTickWhen(5, () -> {
            assertCore(helper, creature, "ribspring", MobCategory.CREATURE, 16.0D, 0.32D, 18.0D);
            helper.assertTrue(
                    creature.getAttribute(Attributes.ATTACK_DAMAGE) == null,
                    "Ribspring must not gain an attack-damage attribute"
            );
        });
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void stitchtuskSpawnsWithReviewedCoreAttributesAndTicks(GameTestHelper helper) {
        Stitchtusk creature = helper.spawnWithNoFreeWill(ModEntities.STITCHTUSK.get(), 1, 1, 1);
        helper.succeedOnTickWhen(5, () -> {
            assertCore(helper, creature, "stitchtusk", MobCategory.CREATURE, 42.0D, 0.19D, 22.0D);
            assertNear(helper, creature.getAttributeValue(Attributes.ATTACK_DAMAGE), 8.0D, "Stitchtusk attack damage");
            assertNear(
                    helper,
                    creature.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE),
                    0.55D,
                    "Stitchtusk knockback resistance"
            );
        });
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void woundscentSpawnsWithReviewedCoreAttributesAndTicks(GameTestHelper helper) {
        Woundscent creature = helper.spawnWithNoFreeWill(ModEntities.WOUNDSCENT.get(), 1, 1, 1);
        helper.succeedOnTickWhen(5, () -> {
            assertCore(helper, creature, "woundscent", MobCategory.MONSTER, 28.0D, 0.25D, 28.0D);
            assertNear(helper, creature.getAttributeValue(Attributes.ATTACK_DAMAGE), 5.0D, "Woundscent attack damage");
        });
    }

    private static void assertCore(
            GameTestHelper helper,
            Mob creature,
            String id,
            MobCategory category,
            double maxHealth,
            double movementSpeed,
            double followRange
    ) {
        helper.assertTrue(creature.isAlive(), id + " must remain alive after spawning");
        helper.assertTrue(creature.tickCount >= 5, id + " must receive at least five server ticks");
        helper.assertTrue(creature.getType().getCategory() == category, id + " has the wrong spawn category");
        helper.assertTrue(
                Gravesown.id(id).equals(BuiltInRegistries.ENTITY_TYPE.getKey(creature.getType())),
                id + " registry id must remain stable"
        );
        assertNear(helper, creature.getMaxHealth(), maxHealth, id + " max health");
        assertNear(helper, creature.getAttributeValue(Attributes.MOVEMENT_SPEED), movementSpeed, id + " movement speed");
        assertNear(helper, creature.getAttributeValue(Attributes.FOLLOW_RANGE), followRange, id + " follow range");
    }

    private static void assertNear(GameTestHelper helper, double actual, double expected, String label) {
        helper.assertTrue(
                Math.abs(actual - expected) < 1.0E-6D,
                label + ": expected " + expected + ", got " + actual
        );
    }
}
