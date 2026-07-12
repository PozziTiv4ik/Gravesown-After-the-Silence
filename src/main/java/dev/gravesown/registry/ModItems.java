package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gravesown.MOD_ID);

    public static final DeferredItem<DeferredSpawnEggItem> HOLLOW_GRAZER_SPAWN_EGG = ITEMS.registerItem(
            "hollow_grazer_spawn_egg",
            properties -> new DeferredSpawnEggItem(
                    ModEntities.HOLLOW_GRAZER,
                    0x292522,
                    0x8A9A4A,
                    properties
            )
    );

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
