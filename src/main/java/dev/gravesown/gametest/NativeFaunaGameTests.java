package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.NativeFauna;
import dev.gravesown.entity.NativeFaunaSpecies;
import dev.gravesown.entity.SiltRay;
import dev.gravesown.registry.ModEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NativeFaunaGameTests {
    private NativeFaunaGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 60)
    public static void everyNativeLandSpeciesHasAStableServerProfile(GameTestHelper helper) {
        helper.assertTrue(
                ModEntities.nativeFauna().size() == NativeFaunaSpecies.values().length,
                "Every biological profile must have exactly one registered entity type"
        );

        List<NativeFauna> spawned = new ArrayList<>();
        for (ModEntities.NativeFaunaRegistration registration : ModEntities.nativeFauna()) {
            NativeFaunaSpecies profile = registration.species();
            NativeFauna animal = helper.spawnWithNoFreeWill(registration.type().get(), 1, 1, 1);
            spawned.add(animal);
            ResourceLocation expectedId = Gravesown.id(profile.id());

            helper.assertTrue(animal.species() == profile, profile.id() + " must retain its biological profile");
            helper.assertTrue(animal.getType().getCategory() == MobCategory.CREATURE,
                    profile.id() + " must use the land-creature spawn category");
            helper.assertTrue(expectedId.equals(BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType())),
                    profile.id() + " registry id must stay stable");
            assertNear(helper, animal.getMaxHealth(), profile.health(), profile.id() + " max health");
            assertNear(helper, animal.getAttributeValue(Attributes.MOVEMENT_SPEED),
                    profile.movementSpeed(), profile.id() + " movement speed");
            assertNear(helper, animal.getAttributeValue(Attributes.FOLLOW_RANGE),
                    profile.followRange(), profile.id() + " follow range");
            assertNear(helper, animal.getAttributeValue(Attributes.ATTACK_DAMAGE),
                    profile.attackDamage(), profile.id() + " attack damage");
            assertNear(helper, animal.getBbWidth(), profile.width(), profile.id() + " width");
            assertNear(helper, animal.getBbHeight(), profile.height(), profile.id() + " height");
        }

        helper.succeedOnTickWhen(5, () -> {
            for (NativeFauna animal : spawned) {
                helper.assertTrue(animal.isAlive() && animal.tickCount >= 5,
                        animal.species().id() + " must survive and receive logical-server ticks");
            }
        });
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 80)
    public static void everyNativeLandSpeciesUsesOnlyGravesownLoot(GameTestHelper helper) {
        for (ModEntities.NativeFaunaRegistration registration : ModEntities.nativeFauna()) {
            NativeFauna animal = helper.spawnWithNoFreeWill(registration.type().get(), 1, 1, 1);
            assertOnlyGravesownLoot(helper, animal, registration.species().id());
        }
        SiltRay siltRay = helper.spawnWithNoFreeWill(ModEntities.SILT_RAY.get(), 1, 1, 1);
        assertOnlyGravesownLoot(helper, siltRay, "silt_ray");
        helper.succeed();
    }

    private static void assertOnlyGravesownLoot(GameTestHelper helper, Mob animal, String id) {
        ResourceKey<LootTable> expectedLoot = ResourceKey.create(
                Registries.LOOT_TABLE,
                Gravesown.id("entities/" + id)
        );
        helper.assertTrue(expectedLoot.equals(animal.getLootTable()), id + " must use its own loot table");

        LootTable table = helper.getLevel().getServer().reloadableRegistries().getLootTable(expectedLoot);
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.THIS_ENTITY, animal)
                .withParameter(LootContextParams.ORIGIN, animal.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, helper.getLevel().damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        boolean sawDrop = false;
        for (long seed = 1L; seed <= 64L; seed++) {
            List<ItemStack> drops = table.getRandomItems(params, seed);
            sawDrop |= !drops.isEmpty();
            for (ItemStack stack : drops) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    helper.assertTrue(Gravesown.MOD_ID.equals(itemId.getNamespace()),
                            id + " produced forbidden non-Gravesown drop " + itemId);
                }
            }
        }
        helper.assertTrue(sawDrop, id + " loot table must yield a resource across fixed smoke seeds");
    }

    private static void assertNear(GameTestHelper helper, double actual, double expected, String label) {
        helper.assertTrue(Math.abs(actual - expected) < 1.0E-5D,
                label + ": expected " + expected + ", got " + actual);
    }
}
