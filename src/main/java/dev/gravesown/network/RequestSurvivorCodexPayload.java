package dev.gravesown.network;

import dev.gravesown.Gravesown;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Requests a fresh server-owned Codex snapshot for client-side navigation. */
public record RequestSurvivorCodexPayload() implements CustomPacketPayload {
    public static final RequestSurvivorCodexPayload INSTANCE = new RequestSurvivorCodexPayload();
    public static final Type<RequestSurvivorCodexPayload> TYPE =
            new Type<>(Gravesown.id("request_survivor_codex"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSurvivorCodexPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<RequestSurvivorCodexPayload> type() {
        return TYPE;
    }
}
