package dev.gravesown.registry;

import com.mojang.serialization.Codec;
import dev.gravesown.Gravesown;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Gravesown.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> RECEIVED_SURVIVOR_CODEX = ATTACHMENTS.register(
            "received_survivor_codex",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build()
    );

    /** Monotonic server-owned record of quest conditions the player has satisfied. */
    public static final Supplier<AttachmentType<Integer>> CODEX_CONDITION_MASK = ATTACHMENTS.register(
            "codex_condition_mask",
            () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build()
    );

    /** Server-owned record of quests the player explicitly claimed in the Codex Hub. */
    public static final Supplier<AttachmentType<Integer>> CODEX_CLAIMED_MASK = ATTACHMENTS.register(
            "codex_claimed_mask",
            () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build()
    );

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}
