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
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ASHEN_SOD.get());
                        output.accept(ModItems.GRAVE_LOAM.get());
                        output.accept(ModItems.HUSHSTONE.get());
                        output.accept(ModItems.DEEP_HUSHSTONE.get());
                        output.accept(ModItems.GRAVEBED.get());
                        output.accept(ModItems.RIBROOT_STEM.get());
                        output.accept(ModItems.RIBROOT_PLANKS.get());
                        output.accept(ModItems.VEIL_FOLIAGE.get());
                        output.accept(ModItems.THREADGRASS.get());
                        output.accept(ModItems.RIBROOT_SHOOT.get());
                        output.accept(ModItems.PALLID_BULB.get());
                        output.accept(ModItems.RAGGED_GRAZER_HIDE.get());
                        output.accept(ModItems.TAUT_SINEW.get());
                        output.accept(ModItems.GRAVE_TALLOW.get());
                        output.accept(ModItems.TAINTED_GRAZER_MEAT.get());
                        output.accept(ModItems.HOLLOW_JAW.get());
                        output.accept(ModItems.QUIETSKIN_HOOD.get());
                        output.accept(ModItems.QUIETSKIN_COAT.get());
                        output.accept(ModItems.QUIETSKIN_LEGWRAPS.get());
                        output.accept(ModItems.QUIETSKIN_BOOTS.get());
                        output.accept(ModItems.HOLLOW_GRAZER_SPAWN_EGG.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
