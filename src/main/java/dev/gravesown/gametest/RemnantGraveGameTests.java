package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.block.RemnantGraveBlock;
import dev.gravesown.entity.BuriedRemnant;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RemnantGraveGameTests {
    private static final ResourceKey<LootTable> BURIED_REMNANT_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE,
            Gravesown.id("entities/buried_remnant")
    );

    private RemnantGraveGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void remnantContentHasStableIdsAndReviewedAttributes(GameTestHelper helper) {
        BuriedRemnant remnant = helper.spawnWithNoFreeWill(ModEntities.BURIED_REMNANT.get(), 1, 1, 1);

        helper.assertTrue(
                Gravesown.id("remnant_grave").equals(BuiltInRegistries.BLOCK.getKey(ModBlocks.REMNANT_GRAVE.get())),
                "Remnant Grave block id changed"
        );
        helper.assertTrue(ModItems.REMNANT_GRAVE.get().getBlock() == ModBlocks.REMNANT_GRAVE.get(),
                "Remnant Grave must have its matching block item");
        helper.assertTrue(
                ModBlocks.REMNANT_GRAVE.get().getLootTable().equals(BuiltInLootTables.EMPTY),
                "Worldgen graves must not drop a reset closed grave for infinite remnant farming"
        );
        helper.assertTrue(
                Gravesown.id("buried_remnant").equals(BuiltInRegistries.ENTITY_TYPE.getKey(remnant.getType())),
                "Buried Remnant entity id changed"
        );
        helper.assertTrue(remnant.getType().getCategory() == MobCategory.MONSTER,
                "Buried Remnant must use the monster category");
        assertNear(helper, remnant.getMaxHealth(), 26.0D, "max health");
        assertNear(helper, remnant.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.22D, "movement speed");
        assertNear(helper, remnant.getAttributeValue(Attributes.ATTACK_DAMAGE), 5.5D, "attack damage");
        assertNear(helper, remnant.getAttributeValue(Attributes.FOLLOW_RANGE), 24.0D, "follow range");
        helper.assertTrue(remnant.getLootTable().equals(BURIED_REMNANT_LOOT),
                "Buried Remnant must use its own loot table");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 140)
    public static void graveInteractionIsOneShotAndEmergenceIsServerOwned(GameTestHelper helper) {
        BlockPos relativePos = new BlockPos(2, 1, 2);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState closed = ModBlocks.REMNANT_GRAVE.get().defaultBlockState()
                .setValue(RemnantGraveBlock.FACING, Direction.EAST)
                .setValue(RemnantGraveBlock.OPENED, false);
        helper.setBlock(relativePos, closed);
        BlockPos expectedEmergenceBlock = absolutePos.relative(Direction.EAST);
        double expectedSurfaceY = helper.getLevel().getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                expectedEmergenceBlock.getX(),
                expectedEmergenceBlock.getZ()
        );

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        InteractionResult firstResult = closed.useWithoutItem(helper.getLevel(), player, hit);

        helper.assertTrue(firstResult.consumesAction(), "First grave interaction must be handled");
        helper.assertTrue(helper.getBlockState(relativePos).getValue(RemnantGraveBlock.OPENED),
                "Successful interaction must persist OPENED in blockstate");

        AABB emergenceArea = new AABB(expectedEmergenceBlock).inflate(1.0D, 3.0D, 1.0D);
        List<BuriedRemnant> firstSpawn = helper.getLevel().getEntitiesOfClass(
                BuriedRemnant.class,
                emergenceArea
        );
        helper.assertTrue(firstSpawn.size() == 1,
                "First interaction must create exactly one Buried Remnant, found " + firstSpawn.size());
        BuriedRemnant remnant = firstSpawn.getFirst();
        helper.assertTrue(remnant.isEmerging(), "Grave spawn must start its emergence state");
        helper.assertTrue(remnant.getEmergenceTicks() == BuriedRemnant.EMERGENCE_DURATION,
                "Emergence must start at the reviewed duration");
        helper.assertTrue(remnant.isNoAi(), "Emerging remnant must have server AI disabled");
        helper.assertTrue(remnant.isInvulnerable(), "Emerging remnant must be temporarily invulnerable");
        helper.assertTrue(
                remnant.getY() <= expectedSurfaceY - BuriedRemnant.EMERGENCE_DEPTH + 0.01D,
                "Buried Remnant must begin physically below the terrain rather than standing on it"
        );

        BlockState opened = helper.getBlockState(relativePos);
        InteractionResult secondResult = opened.useWithoutItem(helper.getLevel(), player, hit);
        helper.assertTrue(secondResult.consumesAction(), "Opened grave interaction must be consumed");
        helper.assertTrue(
                helper.getLevel().getEntitiesOfClass(BuriedRemnant.class, emergenceArea).size() == 1,
                "Opened grave must never create a duplicate"
        );

        helper.runAtTickTime(BuriedRemnant.EMERGENCE_DURATION + 2, () -> {
            helper.assertTrue(!remnant.isEmerging(), "Emergence counter must finish on the server");
            helper.assertTrue(!remnant.isNoAi(), "AI must activate after emergence");
            helper.assertTrue(!remnant.isInvulnerable(), "Damage must activate after emergence");
            helper.assertTrue(Math.abs(remnant.getY() - expectedSurfaceY) < 0.05D,
                    "Buried Remnant must finish at the terrain surface after rising from below");
            helper.assertTrue(remnant.isAlive(), "Buried Remnant must survive its emergence");
            helper.succeed();
        });
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void buriedRemnantLootUsesOnlyGravesownResources(GameTestHelper helper) {
        BuriedRemnant remnant = helper.spawnWithNoFreeWill(ModEntities.BURIED_REMNANT.get(), 1, 1, 1);
        LootTable table = helper.getLevel().getServer().reloadableRegistries().getLootTable(BURIED_REMNANT_LOOT);
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.THIS_ENTITY, remnant)
                .withParameter(LootContextParams.ORIGIN, remnant.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, helper.getLevel().damageSources().generic())
                .create(LootContextParamSets.ENTITY);

        boolean sawSinew = false;
        boolean sawTallow = false;
        for (long seed = 1; seed <= 192; seed++) {
            List<ItemStack> drops = table.getRandomItems(params, seed);
            helper.assertTrue(
                    drops.stream().filter(stack -> stack.is(ModItems.HUSHSTONE_SHARD.get()))
                            .mapToInt(ItemStack::getCount).sum() >= 1,
                    "Every remnant must yield at least one Hushstone Shard; seed=" + seed
            );
            for (ItemStack stack : drops) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                helper.assertTrue(Gravesown.MOD_ID.equals(id.getNamespace()),
                        "Buried Remnant yielded foreign item " + id + "; seed=" + seed);
            }
            sawSinew |= drops.stream().anyMatch(stack -> stack.is(ModItems.TAUT_SINEW.get()));
            sawTallow |= drops.stream().anyMatch(stack -> stack.is(ModItems.GRAVE_TALLOW.get()));
        }
        helper.assertTrue(sawSinew, "Fixed loot samples must exercise sinew");
        helper.assertTrue(sawTallow, "Fixed loot samples must exercise tallow");
        helper.succeed();
    }

    private static void assertNear(GameTestHelper helper, double actual, double expected, String label) {
        helper.assertTrue(Math.abs(actual - expected) < 1.0E-6D,
                label + ": expected " + expected + ", got " + actual);
    }
}
