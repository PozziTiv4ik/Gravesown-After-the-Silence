package dev.gravesown.item;

import dev.gravesown.network.OpenSurvivorCodexPayload;
import dev.gravesown.network.SurvivorCodexProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SurvivorCodexItem extends Item {
    public SurvivorCodexItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new OpenSurvivorCodexPayload(
                            SurvivorCodexProgress.refreshConditions(serverPlayer),
                            SurvivorCodexProgress.claimedMask(serverPlayer),
                            -1
                    )
            );
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.gravesown.survivor_codex");
    }
}
