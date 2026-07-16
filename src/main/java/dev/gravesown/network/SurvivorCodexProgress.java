package dev.gravesown.network;

import dev.gravesown.registry.ModAttachments;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

/** Server-owned snapshot of the reviewed survival progression goals. */
public final class SurvivorCodexProgress {
    public static final int AWAKENING = 0;
    public static final int GATHER = 1;
    public static final int HANDPICK = 2;
    public static final int SHARD = 3;
    public static final int KNIFE = 4;
    public static final int GRAZER = 5;
    public static final int QUIETSKIN = 6;
    public static final int GRAVEWORK = 7;
    public static final int THREE_WOODS = 8;
    public static final int FIELD_KITCHEN = 9;
    public static final int UTENSILS = 10;
    public static final int HOT_MEAL = 11;
    public static final int ANGLER = 12;
    public static final int RUIN_CACHE = 13;

    public static final List<String> QUEST_IDS = List.of(
            "awakening",
            "gather_root_and_thread",
            "craft_crude_handpick",
            "split_hushstone",
            "craft_bound_knife",
            "hunt_hollow_grazer",
            "wear_quietskin",
            "build_gravework",
            "discover_three_woods",
            "build_field_kitchen",
            "craft_kitchen_utensils",
            "prepare_hot_meal",
            "catch_needle_sprat",
            "recover_ruin_cache"
    );
    public static final int ALL_QUEST_BITS = (1 << QUEST_IDS.size()) - 1;

    private SurvivorCodexProgress() {
    }

    public static int refreshConditions(ServerPlayer player) {
        int mask = player.getData(ModAttachments.CODEX_CONDITION_MASK) & ALL_QUEST_BITS;
        if (player.getData(ModAttachments.RECEIVED_SURVIVOR_CODEX)) {
            mask |= bit(AWAKENING);
        }
        if (has(player, ModBlocks.RIBROOT_STEM.asItem()) && has(player, ModBlocks.THREADGRASS.asItem())) {
            mask |= bit(GATHER);
        }
        if (has(player, ModItems.CRUDE_HANDPICK.get())) {
            mask |= bit(HANDPICK);
        }
        if (has(player, ModItems.HUSHSTONE_SHARD.get())) {
            mask |= bit(SHARD);
        }
        if (has(player, ModItems.BOUND_KNIFE.get())) {
            mask |= bit(KNIFE);
        }
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.QUIETSKIN_HOOD.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.QUIETSKIN_COAT.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.QUIETSKIN_LEGWRAPS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.QUIETSKIN_BOOTS.get())) {
            mask |= bit(QUIETSKIN);
        }
        if (has(player, ModItems.GRAVEWORK_BENCH.get())) {
            mask |= bit(GRAVEWORK);
        }
        if (has(player, ModItems.RIBROOT_PLANKS.get())
                && has(player, ModItems.EMBERBARK_PLANKS.get())
                && has(player, ModItems.PALEVINE_PLANKS.get())) {
            mask |= bit(THREE_WOODS);
        }
        if (has(player, ModItems.FIELD_KITCHEN.get())) {
            mask |= bit(FIELD_KITCHEN);
        }
        if (has(player, ModItems.BONE_CLEAVER.get()) && has(player, ModItems.STIRRING_HOOK.get())) {
            mask |= bit(UTENSILS);
        }
        if (has(player, ModItems.MIREBEAN_STEW.get())
                || has(player, ModItems.CHARRED_MARROW_POT.get())
                || has(player, ModItems.GLOAM_CHOWDER.get())) {
            mask |= bit(HOT_MEAL);
        }
        if (has(player, ModItems.NEEDLE_SPRAT.get())) {
            mask |= bit(ANGLER);
        }
        if (has(player, ModItems.RELIQUARY_CRATE.get())) {
            mask |= bit(RUIN_CACHE);
        }
        player.setData(ModAttachments.CODEX_CONDITION_MASK, mask);
        return mask;
    }

    public static int claimedMask(ServerPlayer player) {
        return player.getData(ModAttachments.CODEX_CLAIMED_MASK) & ALL_QUEST_BITS;
    }

    public static void markCondition(ServerPlayer player, int questIndex) {
        if (validIndex(questIndex)) {
            player.setData(
                    ModAttachments.CODEX_CONDITION_MASK,
                    (player.getData(ModAttachments.CODEX_CONDITION_MASK) | bit(questIndex)) & ALL_QUEST_BITS
            );
        }
    }

    public static boolean canClaim(ServerPlayer player, int questIndex) {
        if (!validIndex(questIndex)) {
            return false;
        }
        int conditions = refreshConditions(player);
        int claimed = claimedMask(player);
        return (conditions & bit(questIndex)) != 0
                && (claimed & bit(questIndex)) == 0
                && (questIndex == 0 || (claimed & bit(questIndex - 1)) != 0);
    }

    public static boolean claim(ServerPlayer player, int questIndex) {
        if (!canClaim(player, questIndex)) {
            return false;
        }
        player.setData(ModAttachments.CODEX_CLAIMED_MASK, claimedMask(player) | bit(questIndex));
        return true;
    }

    public static boolean isSet(int mask, int questIndex) {
        return (mask & 1 << questIndex) != 0;
    }

    private static int bit(int questIndex) {
        return 1 << questIndex;
    }

    private static boolean validIndex(int questIndex) {
        return questIndex >= 0 && questIndex < QUEST_IDS.size();
    }

    private static boolean has(ServerPlayer player, Item item) {
        return player.getInventory().contains(item.getDefaultInstance());
    }
}
