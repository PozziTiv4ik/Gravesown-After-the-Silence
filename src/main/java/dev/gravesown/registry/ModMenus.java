package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.menu.GraveworkMenu;
import dev.gravesown.menu.PitchKilnMenu;
import dev.gravesown.menu.FieldKitchenMenu;
import dev.gravesown.menu.SawmillMenu;
import dev.gravesown.blockentity.ReliquaryCrateBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Gravesown.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<GraveworkMenu>> GRAVEWORK = MENUS.register(
            "gravework",
            () -> new MenuType<>(GraveworkMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<PitchKilnMenu>> PITCH_KILN = MENUS.register(
            "pitch_kiln",
            () -> new MenuType<>(PitchKilnMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<FieldKitchenMenu>> FIELD_KITCHEN = MENUS.register(
            "field_kitchen",
            () -> new MenuType<>(FieldKitchenMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<SawmillMenu>> SAWMILL = MENUS.register(
            "sawmill",
            () -> new MenuType<>(SawmillMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<ChestMenu>> RELIQUARY_CRATE = MENUS.register(
            "reliquary_crate",
            () -> new MenuType<>(ModMenus::createReliquaryCrateClientMenu, FeatureFlags.DEFAULT_FLAGS)
    );

    private ModMenus() {
    }

    private static ChestMenu createReliquaryCrateClientMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory) {
        return new ChestMenu(
                RELIQUARY_CRATE.get(),
                containerId,
                inventory,
                new SimpleContainer(ReliquaryCrateBlockEntity.SIZE),
                ReliquaryCrateBlockEntity.ROWS
        );
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
