package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Registry ownership for the complete Gloamwater fluid family. */
public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Gravesown.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Gravesown.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> GLOAMWATER_TYPE = FLUID_TYPES.register(
            "gloamwater",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid_type.gravesown.gloamwater")
                    .density(1000)
                    .viscosity(1000)
                    .temperature(283)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> GLOAMWATER = FLUIDS.register(
            "gloamwater",
            () -> new BaseFlowingFluid.Source(properties())
    );
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_GLOAMWATER = FLUIDS.register(
            "flowing_gloamwater",
            () -> new BaseFlowingFluid.Flowing(properties())
    );

    private ModFluids() {
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(GLOAMWATER_TYPE, GLOAMWATER, FLOWING_GLOAMWATER)
                .bucket(ModItems.GLOAMWATER_BUCKET)
                .block(ModBlocks.GLOAMWATER)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .tickRate(5)
                .explosionResistance(100.0F);
    }
}
