package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.blockentity.PitchKilnBlockEntity;
import dev.gravesown.blockentity.ReliquaryCrateBlockEntity;
import dev.gravesown.blockentity.FieldKitchenBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Gravesown.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PitchKilnBlockEntity>> PITCH_KILN =
            BLOCK_ENTITIES.register("pitch_kiln", () -> BlockEntityType.Builder.of(
                    PitchKilnBlockEntity::new,
                    ModBlocks.PITCH_KILN.get()
            ).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReliquaryCrateBlockEntity>> RELIQUARY_CRATE =
            BLOCK_ENTITIES.register("reliquary_crate", () -> BlockEntityType.Builder.of(
                    ReliquaryCrateBlockEntity::new,
                    ModBlocks.RELIQUARY_CRATE.get()
            ).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FieldKitchenBlockEntity>> FIELD_KITCHEN =
            BLOCK_ENTITIES.register("field_kitchen", () -> BlockEntityType.Builder.of(
                    FieldKitchenBlockEntity::new,
                    ModBlocks.FIELD_KITCHEN.get()
            ).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
