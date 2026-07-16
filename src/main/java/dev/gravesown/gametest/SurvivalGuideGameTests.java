package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.event.SurvivalGuideEvents;
import dev.gravesown.network.OpenSurvivorCodexPayload;
import dev.gravesown.network.SurvivorCodexProgress;
import dev.gravesown.registry.ModAttachments;
import dev.gravesown.registry.ModItems;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SurvivalGuideGameTests {
    private SurvivalGuideGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void survivorCodexUsesDedicatedFourteenQuestContract(GameTestHelper helper) {
        ItemStack codex = new ItemStack(ModItems.SURVIVOR_CODEX.get());
        helper.assertTrue(
                Gravesown.id("survivor_codex").equals(BuiltInRegistries.ITEM.getKey(codex.getItem())),
                "Survivor's Codex registry id must remain stable"
        );
        helper.assertTrue(codex.getMaxStackSize() == 1, "Survivor's Codex must be non-stackable");
        helper.assertTrue(
                codex.get(DataComponents.WRITTEN_BOOK_CONTENT) == null,
                "Survivor's Codex must open the dedicated hub instead of vanilla book pages"
        );
        helper.assertTrue(
                SurvivorCodexProgress.QUEST_IDS.size() == 14,
                "The server quest contract must retain the fourteen reviewed survival goals"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void survivalGrantIsServerAuthoritativeExactlyOnceAndUnlocksRoot(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().clearContent();
        reset(player);

        player.setGameMode(GameType.CREATIVE);
        helper.assertTrue(!SurvivalGuideEvents.giveCodexIfEligible(player),
                "Creative login must not consume the Survival grant");
        helper.assertTrue(countCodices(player) == 0, "Creative login must not grant the hub item");

        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        reset(player);
        helper.assertTrue(SurvivalGuideEvents.giveCodexIfEligible(player),
                "First Survival login must grant the hub item");
        helper.assertTrue(countCodices(player) == 1, "First grant must create exactly one hub item");
        helper.assertTrue(
                SurvivorCodexProgress.isSet(
                        SurvivorCodexProgress.refreshConditions(player), SurvivorCodexProgress.AWAKENING),
                "Successful grant must satisfy the first custom quest condition"
        );
        helper.assertTrue(!SurvivalGuideEvents.giveCodexIfEligible(player),
                "Repeated login processing must not duplicate the item");
        helper.assertTrue(countCodices(player) == 1, "Repeated grant processing must leave one item");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void enteringSurvivalTriggersPendingOneTimeGrant(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().clearContent();
        reset(player);
        player.setGameMode(GameType.CREATIVE);

        SurvivalGuideEvents events = new SurvivalGuideEvents();
        events.onPlayerChangeGameMode(
                new PlayerEvent.PlayerChangeGameModeEvent(player, GameType.CREATIVE, GameType.SURVIVAL)
        );
        helper.assertTrue(countCodices(player) == 1, "Entering Survival must grant exactly one hub item");
        events.onPlayerChangeGameMode(
                new PlayerEvent.PlayerChangeGameModeEvent(player, GameType.CREATIVE, GameType.SURVIVAL)
        );
        helper.assertTrue(countCodices(player) == 1, "Repeated Survival transitions must not duplicate it");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void rightClickSendsConditionAndClaimSnapshots(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        reset(player);
        player.setData(ModAttachments.RECEIVED_SURVIVOR_CODEX, true);
        SurvivorCodexProgress.markCondition(player, SurvivorCodexProgress.GRAZER);
        helper.assertTrue(SurvivorCodexProgress.claim(player, SurvivorCodexProgress.AWAKENING),
                "Root must be claimable after the grant condition");

        ItemStack codex = new ItemStack(ModItems.SURVIVOR_CODEX.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, codex);
        EmbeddedChannel channel = (EmbeddedChannel) player.connection.getConnection().channel();
        NetworkRegistry.configureMockConnection(player.connection.getConnection());
        while (channel.readOutbound() != null) {
            // Discard join packets.
        }

        codex.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        Object outbound = channel.readOutbound();
        helper.assertTrue(
                outbound instanceof ClientboundCustomPayloadPacket packet
                        && packet.payload() instanceof OpenSurvivorCodexPayload payload
                        && SurvivorCodexProgress.isSet(payload.conditionMask(), SurvivorCodexProgress.AWAKENING)
                        && SurvivorCodexProgress.isSet(payload.conditionMask(), SurvivorCodexProgress.GRAZER)
                        && payload.claimedMask() == 1
                        && payload.newlyClaimedQuest() == -1,
                "Right-click must send both server-owned masks without a false completion toast"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void questsRequireConditionsPredecessorsAndCannotDoubleClaim(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        reset(player);
        player.setData(ModAttachments.RECEIVED_SURVIVOR_CODEX, true);
        player.getInventory().add(new ItemStack(ModItems.RIBROOT_STEM.get()));
        player.getInventory().add(new ItemStack(ModItems.THREADGRASS.get()));

        helper.assertTrue(!SurvivorCodexProgress.claim(player, SurvivorCodexProgress.GATHER),
                "A satisfied quest must remain locked until its predecessor is claimed");
        helper.assertTrue(SurvivorCodexProgress.claim(player, SurvivorCodexProgress.AWAKENING),
                "The satisfied root must claim once");
        helper.assertTrue(!SurvivorCodexProgress.claim(player, SurvivorCodexProgress.AWAKENING),
                "A claimed quest must reject a duplicate claim");
        helper.assertTrue(SurvivorCodexProgress.claim(player, SurvivorCodexProgress.GATHER),
                "The next satisfied quest must claim after its predecessor");
        helper.assertTrue(SurvivorCodexProgress.claimedMask(player) == 3,
                "Claims must persist as a monotonic two-bit mask");
        helper.assertTrue(!SurvivorCodexProgress.claim(player, SurvivorCodexProgress.HANDPICK),
                "An unmet item condition must not claim");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void quietskinConditionRequiresWearingCompleteSet(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        reset(player);
        player.getInventory().add(new ItemStack(ModItems.QUIETSKIN_HOOD.get()));
        player.getInventory().add(new ItemStack(ModItems.QUIETSKIN_COAT.get()));
        player.getInventory().add(new ItemStack(ModItems.QUIETSKIN_LEGWRAPS.get()));
        player.getInventory().add(new ItemStack(ModItems.QUIETSKIN_BOOTS.get()));
        helper.assertTrue(
                !SurvivorCodexProgress.isSet(
                        SurvivorCodexProgress.refreshConditions(player), SurvivorCodexProgress.QUIETSKIN),
                "Carrying the four pieces must not satisfy the wear condition"
        );
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.QUIETSKIN_HOOD.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.QUIETSKIN_COAT.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.QUIETSKIN_LEGWRAPS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.QUIETSKIN_BOOTS.get()));
        helper.assertTrue(
                SurvivorCodexProgress.isSet(
                        SurvivorCodexProgress.refreshConditions(player), SurvivorCodexProgress.QUIETSKIN),
                "Wearing all four pieces must satisfy the server-owned condition"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void expandedStoryConditionsAreServerOwnedAndPersistAfterObservation(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        reset(player);
        player.getInventory().add(new ItemStack(ModItems.RIBROOT_PLANKS.get()));
        player.getInventory().add(new ItemStack(ModItems.EMBERBARK_PLANKS.get()));
        player.getInventory().add(new ItemStack(ModItems.PALEVINE_PLANKS.get()));
        player.getInventory().add(new ItemStack(ModItems.FIELD_KITCHEN.get()));
        player.getInventory().add(new ItemStack(ModItems.BONE_CLEAVER.get()));
        player.getInventory().add(new ItemStack(ModItems.STIRRING_HOOK.get()));
        player.getInventory().add(new ItemStack(ModItems.GLOAM_CHOWDER.get()));
        player.getInventory().add(new ItemStack(ModItems.NEEDLE_SPRAT.get()));
        player.getInventory().add(new ItemStack(ModItems.RELIQUARY_CRATE.get()));

        int conditions = SurvivorCodexProgress.refreshConditions(player);
        for (int quest : new int[]{
                SurvivorCodexProgress.THREE_WOODS,
                SurvivorCodexProgress.FIELD_KITCHEN,
                SurvivorCodexProgress.UTENSILS,
                SurvivorCodexProgress.HOT_MEAL,
                SurvivorCodexProgress.ANGLER,
                SurvivorCodexProgress.RUIN_CACHE
        }) {
            helper.assertTrue(SurvivorCodexProgress.isSet(conditions, quest),
                    "Expanded quest condition " + quest + " must be computed on the logical server");
        }
        player.getInventory().clearContent();
        int persisted = SurvivorCodexProgress.refreshConditions(player);
        helper.assertTrue(SurvivorCodexProgress.isSet(persisted, SurvivorCodexProgress.RUIN_CACHE),
                "Observed story conditions must remain recorded after the item leaves inventory");
        helper.succeed();
    }

    private static void reset(ServerPlayer player) {
        player.setData(ModAttachments.RECEIVED_SURVIVOR_CODEX, false);
        player.setData(ModAttachments.CODEX_CONDITION_MASK, 0);
        player.setData(ModAttachments.CODEX_CLAIMED_MASK, 0);
    }

    private static int countCodices(ServerPlayer player) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(ModItems.SURVIVOR_CODEX.get())) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
