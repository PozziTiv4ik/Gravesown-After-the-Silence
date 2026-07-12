package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.HollowGrazer;
import dev.gravesown.gameplay.QuietskinEffects;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HollowGrazerGameTests {
    private static final ResourceLocation HOLLOW_GRAZER_ID =
            ResourceLocation.fromNamespaceAndPath(Gravesown.MOD_ID, "hollow_grazer");
    private static final ResourceKey<LootTable> HOLLOW_GRAZER_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(Gravesown.MOD_ID, "entities/hollow_grazer")
    );

    private HollowGrazerGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void hollowGrazerSpawnsAndTicks(GameTestHelper helper) {
        HollowGrazer grazer = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 1, 1, 1);

        helper.succeedOnTickWhen(5, () -> {
            helper.assertTrue(grazer.isAlive(), "Hollow Grazer must remain alive after spawning");
            helper.assertTrue(grazer.tickCount >= 5, "Hollow Grazer must receive server ticks");
            helper.assertTrue(grazer.getMaxHealth() == 28.0F, "Hollow Grazer max health must be 28");
            helper.assertTrue(
                    grazer.getType().getCategory() == MobCategory.CREATURE,
                    "Hollow Grazer must use the CREATURE spawn category"
            );
            helper.assertTrue(
                    HOLLOW_GRAZER_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(grazer.getType())),
                    "Hollow Grazer registry id must stay stable"
            );
        });
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void hollowGrazerLootUsesOnlyCustomResources(GameTestHelper helper) {
        HollowGrazer grazer = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 1, 1, 1);
        helper.assertTrue(
                HOLLOW_GRAZER_LOOT.equals(grazer.getLootTable()),
                "Hollow Grazer must use its entity loot table"
        );

        LootTable table = helper.getLevel().getServer().reloadableRegistries().getLootTable(HOLLOW_GRAZER_LOOT);
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.THIS_ENTITY, grazer)
                .withParameter(LootContextParams.ORIGIN, grazer.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, helper.getLevel().damageSources().generic())
                .create(LootContextParamSets.ENTITY);

        boolean sawTallow = false;
        boolean sawJaw = false;
        for (long seed = 1L; seed <= 256L; seed++) {
            List<ItemStack> drops = table.getRandomItems(params, seed);
            helper.assertTrue(
                    count(drops, ModItems.RAGGED_GRAZER_HIDE.get()) >= 2,
                    "Every roll must yield mutant hide; seed=" + seed
            );
            helper.assertTrue(
                    count(drops, ModItems.TAUT_SINEW.get()) >= 1,
                    "Every roll must yield sinew; seed=" + seed
            );
            helper.assertTrue(
                    count(drops, ModItems.TAINTED_GRAZER_MEAT.get()) >= 1,
                    "Every roll must yield mutant meat; seed=" + seed
            );
            helper.assertTrue(count(drops, Items.LEATHER) == 0, "Leather is forbidden; seed=" + seed);
            helper.assertTrue(count(drops, Items.ROTTEN_FLESH) == 0, "Rotten flesh is forbidden; seed=" + seed);

            for (ItemStack stack : drops) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    helper.assertTrue(
                            Gravesown.MOD_ID.equals(itemId.getNamespace()),
                            "Non-Gravesown drop " + itemId + "; seed=" + seed
                    );
                }
            }

            sawTallow |= count(drops, ModItems.GRAVE_TALLOW.get()) > 0;
            sawJaw |= count(drops, ModItems.HOLLOW_JAW.get()) > 0;
        }

        helper.assertTrue(sawTallow, "Fixed rolls must exercise the tallow branch");
        helper.assertTrue(sawJaw, "Fixed rolls must exercise the rare jaw branch");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void quietskinArmorEquipsAndAppliesExpectedAttributes(GameTestHelper helper) {
        ArmorItem[] pieces = {
                ModItems.QUIETSKIN_HOOD.get(),
                ModItems.QUIETSKIN_COAT.get(),
                ModItems.QUIETSKIN_LEGWRAPS.get(),
                ModItems.QUIETSKIN_BOOTS.get()
        };
        ArmorItem.Type[] types = {
                ArmorItem.Type.HELMET,
                ArmorItem.Type.CHESTPLATE,
                ArmorItem.Type.LEGGINGS,
                ArmorItem.Type.BOOTS
        };
        int[] defense = {2, 4, 3, 1};
        HollowGrazer wearer = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 1, 1, 1);

        for (int index = 0; index < pieces.length; index++) {
            ArmorItem piece = pieces[index];
            ItemStack stack = new ItemStack(piece);
            helper.assertTrue(piece.getType() == types[index], "Quietskin piece has the wrong armor type");
            helper.assertTrue(piece.getEquipmentSlot() == types[index].getSlot(), "Quietskin piece has the wrong slot");
            helper.assertTrue(piece.getDefense() == defense[index], "Quietskin piece has the wrong defense");
            helper.assertTrue(
                    stack.getMaxDamage() == types[index].getDurability(10),
                    "Quietskin piece has the wrong durability"
            );
            helper.assertTrue(
                    piece.isValidRepairItem(stack, new ItemStack(ModItems.RAGGED_GRAZER_HIDE.get())),
                    "Ragged Grazer Hide must repair Quietskin"
            );
            helper.assertTrue(
                    !piece.isValidRepairItem(stack, new ItemStack(Items.LEATHER)),
                    "Vanilla leather must not repair Quietskin"
            );
            wearer.setItemSlot(types[index].getSlot(), stack);
        }

        helper.succeedWhen(() -> {
            helper.assertTrue(wearer.getArmorValue() == 10, "Full Quietskin must apply 10 armor");
            helper.assertTrue(wearer.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.QUIETSKIN_HOOD.get()), "Hood slot mismatch");
            helper.assertTrue(wearer.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.QUIETSKIN_COAT.get()), "Coat slot mismatch");
            helper.assertTrue(wearer.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.QUIETSKIN_LEGWRAPS.get()), "Legwrap slot mismatch");
            helper.assertTrue(wearer.getItemBySlot(EquipmentSlot.FEET).is(ModItems.QUIETSKIN_BOOTS.get()), "Boot slot mismatch");
        });
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void quietskinDeadScentScalesDeterministically(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(QuietskinEffects.equippedPieces(player) == 0, "Empty player must have zero Quietskin pieces");
        assertNear(helper, QuietskinEffects.scentDetectionMultiplier(player), 1.0D, "No-armor multiplier");

        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.QUIETSKIN_HOOD.get()));
        assertNear(helper, QuietskinEffects.scentDetectionMultiplier(player), 0.875D, "One-piece multiplier");
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.QUIETSKIN_COAT.get()));
        assertNear(helper, QuietskinEffects.scentDetectionMultiplier(player), 0.75D, "Two-piece multiplier");
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.QUIETSKIN_LEGWRAPS.get()));
        assertNear(helper, QuietskinEffects.scentDetectionMultiplier(player), 0.625D, "Three-piece multiplier");
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.QUIETSKIN_BOOTS.get()));
        helper.assertTrue(QuietskinEffects.hasFullSet(player), "Four pieces must form the full set");
        assertNear(helper, QuietskinEffects.scentDetectionMultiplier(player), 0.5D, "Full-set multiplier");

        HollowGrazer grazer = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 1, 1, 1);
        helper.getLevel().setDayTime(1000L);
        player.setHealth(1.0F);
        player.setPos(grazer.getX() + 13.0D, grazer.getY(), grazer.getZ());
        helper.assertTrue(
                !grazer.canDetectByScent(player),
                "Full Quietskin halves the 24-block scent range, so 13 blocks must be hidden"
        );

        player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        helper.assertTrue(!QuietskinEffects.hasFullSet(player), "Removing boots must break the full set");
        helper.assertTrue(
                grazer.canDetectByScent(player),
                "Three pieces leave a 15-block scent range, so 13 blocks must be detected"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void quietskinStopsAnExistingDaytimeScentHunt(GameTestHelper helper) {
        HollowGrazer grazer = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 1, 1, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.getLevel().setDayTime(1000L);
        player.setHealth(1.0F);
        player.setPos(grazer.getX() + 13.0D, grazer.getY(), grazer.getZ());
        grazer.setTarget(player);

        helper.assertTrue(
                grazer.canContinueBloodScentHunt(),
                "An existing daytime scent hunt must continue at 13 blocks without Quietskin"
        );

        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.QUIETSKIN_HOOD.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.QUIETSKIN_COAT.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.QUIETSKIN_LEGWRAPS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.QUIETSKIN_BOOTS.get()));
        helper.assertTrue(
                !grazer.canContinueBloodScentHunt(),
                "Equipping full Quietskin must end an existing daytime scent hunt outside 12 blocks"
        );

        player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        helper.assertTrue(
                grazer.canContinueBloodScentHunt(),
                "Three Quietskin pieces must still allow the existing hunt at 13 blocks"
        );

        player.setHealth(player.getMaxHealth());
        helper.assertTrue(
                !grazer.canContinueBloodScentHunt(),
                "Healing above half health must end an existing daytime scent hunt"
        );

        helper.getLevel().setDayTime(13000L);
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(
                    grazer.canContinueBloodScentHunt(),
                    "Night aggression must continue regardless of health and Quietskin"
            );
            helper.succeed();
        });
    }

    private static int count(List<ItemStack> drops, Item item) {
        return drops.stream()
                .filter(stack -> stack.is(item))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private static void assertNear(GameTestHelper helper, double actual, double expected, String label) {
        helper.assertTrue(
                Math.abs(actual - expected) < 1.0E-9D,
                label + ": expected " + expected + ", got " + actual
        );
    }
}
