package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Gravesown.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gravesown.main"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModItems.HOLLOW_GRAZER_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters, output) ->
                            output.accept(ModItems.HOLLOW_GRAZER_SPAWN_EGG.get()))
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
