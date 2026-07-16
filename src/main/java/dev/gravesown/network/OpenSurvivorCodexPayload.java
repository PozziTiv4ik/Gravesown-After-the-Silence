package dev.gravesown.network;

import dev.gravesown.Gravesown;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenSurvivorCodexPayload(
        int conditionMask,
        int claimedMask,
        int newlyClaimedQuest
) implements CustomPacketPayload {
    public static final Type<OpenSurvivorCodexPayload> TYPE =
            new Type<>(Gravesown.id("open_survivor_codex"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSurvivorCodexPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    OpenSurvivorCodexPayload::conditionMask,
                    ByteBufCodecs.VAR_INT,
                    OpenSurvivorCodexPayload::claimedMask,
                    ByteBufCodecs.VAR_INT,
                    OpenSurvivorCodexPayload::newlyClaimedQuest,
                    OpenSurvivorCodexPayload::new
            );

    public OpenSurvivorCodexPayload {
        conditionMask &= SurvivorCodexProgress.ALL_QUEST_BITS;
        claimedMask &= SurvivorCodexProgress.ALL_QUEST_BITS;
        if (newlyClaimedQuest < -1 || newlyClaimedQuest >= SurvivorCodexProgress.QUEST_IDS.size()) {
            newlyClaimedQuest = -1;
        }
    }

    @Override
    public Type<OpenSurvivorCodexPayload> type() {
        return TYPE;
    }
}
