package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.HollowGrazer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Gravesown.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<HollowGrazer>> HOLLOW_GRAZER =
            ENTITY_TYPES.register("hollow_grazer", registryName -> EntityType.Builder
                    .of(HollowGrazer::new, MobCategory.CREATURE)
                    .sized(1.15F, 1.45F)
                    .eyeHeight(1.15F)
                    .clientTrackingRange(10)
                    .build(registryName.toString()));

    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }

    public static void registerEventListeners(IEventBus modEventBus) {
        modEventBus.addListener(ModEntities::onEntityAttributes);
        modEventBus.addListener(ModEntities::onRegisterSpawnPlacements);
    }

    private static void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(HOLLOW_GRAZER.get(), HollowGrazer.createAttributes().build());
    }

    private static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                HOLLOW_GRAZER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HollowGrazer::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }
}
