package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.HollowGrazer;
import dev.gravesown.entity.BuriedRemnant;
import dev.gravesown.entity.Ribspring;
import dev.gravesown.entity.Stitchtusk;
import dev.gravesown.entity.Woundscent;
import dev.gravesown.entity.Rotfin;
import dev.gravesown.entity.Rootskimmer;
import dev.gravesown.entity.Veilfin;
import dev.gravesown.entity.GloamSkiff;
import dev.gravesown.entity.NativeFauna;
import dev.gravesown.entity.NativeFaunaSpecies;
import dev.gravesown.entity.SiltRay;
import dev.gravesown.entity.ThrownSpear;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public static final DeferredHolder<EntityType<?>, EntityType<Ribspring>> RIBSPRING =
            ENTITY_TYPES.register("ribspring", registryName -> EntityType.Builder
                    .of(Ribspring::new, MobCategory.CREATURE)
                    .sized(0.80F, 1.15F)
                    .eyeHeight(0.90F)
                    .clientTrackingRange(10)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Stitchtusk>> STITCHTUSK =
            ENTITY_TYPES.register("stitchtusk", registryName -> EntityType.Builder
                    .of(Stitchtusk::new, MobCategory.CREATURE)
                    .sized(2.05F, 1.75F)
                    .eyeHeight(1.30F)
                    .clientTrackingRange(10)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Woundscent>> WOUNDSCENT =
            ENTITY_TYPES.register("woundscent", registryName -> EntityType.Builder
                    .of(Woundscent::new, MobCategory.MONSTER)
                    .sized(0.95F, 1.35F)
                    .eyeHeight(1.05F)
                    .clientTrackingRange(10)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BuriedRemnant>> BURIED_REMNANT =
            ENTITY_TYPES.register("buried_remnant", registryName -> EntityType.Builder
                    .of(BuriedRemnant::new, MobCategory.MONSTER)
                    .sized(0.72F, 1.95F)
                    .eyeHeight(1.66F)
                    .clientTrackingRange(10)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Rotfin>> ROTFIN =
            ENTITY_TYPES.register("rotfin", registryName -> EntityType.Builder
                    .of(Rotfin::new, MobCategory.WATER_CREATURE)
                    .sized(0.72F, 0.42F)
                    .eyeHeight(0.24F)
                    .clientTrackingRange(8)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Veilfin>> VEILFIN =
            ENTITY_TYPES.register("veilfin", registryName -> EntityType.Builder
                    .of(Veilfin::new, MobCategory.WATER_AMBIENT)
                    .sized(0.58F, 0.34F)
                    .eyeHeight(0.20F)
                    .clientTrackingRange(8)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Rootskimmer>> ROOTSKIMMER =
            ENTITY_TYPES.register("rootskimmer", registryName -> EntityType.Builder
                    .of(Rootskimmer::new, MobCategory.WATER_AMBIENT)
                    .sized(0.78F, 0.32F)
                    .eyeHeight(0.18F)
                    .clientTrackingRange(8)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GloamSkiff>> GLOAM_SKIFF =
            ENTITY_TYPES.register("gloam_skiff", registryName -> EntityType.Builder
                    .of(GloamSkiff::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10)
                    .build(registryName.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownSpear>> THROWN_SPEAR =
            ENTITY_TYPES.register("thrown_spear", registryName -> EntityType.Builder
                    .<ThrownSpear>of(ThrownSpear::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(10)
                    .build(registryName.toString()));

    private static final List<NativeFaunaRegistration> MUTABLE_NATIVE_FAUNA = new ArrayList<>();
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> ASH_HOPPER =
            registerNative(NativeFaunaSpecies.ASH_HOPPER);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> GRAVEWING =
            registerNative(NativeFaunaSpecies.GRAVEWING);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> ROOTBACK =
            registerNative(NativeFaunaSpecies.ROOTBACK);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> BARK_MARTEN =
            registerNative(NativeFaunaSpecies.BARK_MARTEN);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> CRAG_RAM =
            registerNative(NativeFaunaSpecies.CRAG_RAM);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> RIFT_PUMA =
            registerNative(NativeFaunaSpecies.RIFT_PUMA);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> MIRE_TOAD =
            registerNative(NativeFaunaSpecies.MIRE_TOAD);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> REED_LYNX =
            registerNative(NativeFaunaSpecies.REED_LYNX);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> EMBER_FOX =
            registerNative(NativeFaunaSpecies.EMBER_FOX);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> CINDER_FOWL =
            registerNative(NativeFaunaSpecies.CINDER_FOWL);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> PALLID_HART =
            registerNative(NativeFaunaSpecies.PALLID_HART);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> MOSSBOAR =
            registerNative(NativeFaunaSpecies.MOSSBOAR);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> AMBER_JAY =
            registerNative(NativeFaunaSpecies.AMBER_JAY);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeFauna>> SUNHORN =
            registerNative(NativeFaunaSpecies.SUNHORN);
    public static final DeferredHolder<EntityType<?>, EntityType<SiltRay>> SILT_RAY =
            ENTITY_TYPES.register("silt_ray", registryName -> EntityType.Builder
                    .of(SiltRay::new, MobCategory.WATER_AMBIENT)
                    .sized(1.15F, 0.30F)
                    .eyeHeight(0.16F)
                    .clientTrackingRange(8)
                    .build(registryName.toString()));

    private ModEntities() {
    }

    private static DeferredHolder<EntityType<?>, EntityType<NativeFauna>> registerNative(
            NativeFaunaSpecies species
    ) {
        DeferredHolder<EntityType<?>, EntityType<NativeFauna>> holder = ENTITY_TYPES.register(
                species.id(),
                registryName -> EntityType.Builder
                        .<NativeFauna>of((type, level) -> new NativeFauna(type, level, species), MobCategory.CREATURE)
                        .sized(species.width(), species.height())
                        .eyeHeight(species.eyeHeight())
                        .clientTrackingRange(10)
                        .build(registryName.toString())
        );
        MUTABLE_NATIVE_FAUNA.add(new NativeFaunaRegistration(species, holder));
        return holder;
    }

    public static List<NativeFaunaRegistration> nativeFauna() {
        return Collections.unmodifiableList(MUTABLE_NATIVE_FAUNA);
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
        event.put(RIBSPRING.get(), Ribspring.createAttributes().build());
        event.put(STITCHTUSK.get(), Stitchtusk.createAttributes().build());
        event.put(WOUNDSCENT.get(), Woundscent.createAttributes().build());
        event.put(BURIED_REMNANT.get(), BuriedRemnant.createAttributes().build());
        event.put(ROTFIN.get(), Rotfin.createAttributes().build());
        event.put(VEILFIN.get(), Veilfin.createAttributes().build());
        event.put(ROOTSKIMMER.get(), Rootskimmer.createAttributes().build());
        event.put(SILT_RAY.get(), SiltRay.createAttributes().build());
        for (NativeFaunaRegistration registration : MUTABLE_NATIVE_FAUNA) {
            event.put(registration.type().get(), NativeFauna.createAttributes(registration.species()).build());
        }
    }

    private static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                HOLLOW_GRAZER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HollowGrazer::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                RIBSPRING.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Ribspring::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                STITCHTUSK.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Stitchtusk::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                WOUNDSCENT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Woundscent::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                BURIED_REMNANT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BuriedRemnant::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                ROTFIN.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Rotfin::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                VEILFIN.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Veilfin::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                ROOTSKIMMER.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Rootskimmer::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                SILT_RAY.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SiltRay::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        for (NativeFaunaRegistration registration : MUTABLE_NATIVE_FAUNA) {
            event.register(
                    registration.type().get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    NativeFauna::checkSpawnRules,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );
        }
    }

    public record NativeFaunaRegistration(
            NativeFaunaSpecies species,
            DeferredHolder<EntityType<?>, EntityType<NativeFauna>> type
    ) {
    }
}
