package dev.gravesown.network;

import dev.gravesown.Gravesown;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClaimCodexQuestPayload(int questIndex) implements CustomPacketPayload {
    public static final Type<ClaimCodexQuestPayload> TYPE =
            new Type<>(Gravesown.id("claim_codex_quest"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimCodexQuestPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, ClaimCodexQuestPayload::questIndex, ClaimCodexQuestPayload::new);

    @Override
    public Type<ClaimCodexQuestPayload> type() {
        return TYPE;
    }
}
