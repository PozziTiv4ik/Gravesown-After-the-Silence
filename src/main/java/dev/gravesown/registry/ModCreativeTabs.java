package dev.gravesown.registry;

import dev.gravesown.Gravesown;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
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
                        output.accept(ModItems.ROOTFELT.get());
                        output.accept(ModItems.FIBROUS_LOAM.get());
                        output.accept(ModItems.SCAR_SHALE.get());
                        output.accept(ModItems.MARROWSTONE.get());
                        output.accept(ModItems.SUTURE_SILT.get());
                        output.accept(ModItems.DRIED_ICHOR.get());
                        output.accept(ModItems.ABYSSAL_SILT.get());
                        output.accept(ModItems.BRINEBONE.get());
                        output.accept(ModItems.GLOAM_MUCK.get());
                        output.accept(ModItems.GLOAM_SAND.get());
                        output.accept(ModItems.VEINED_SHALE.get());
                        output.accept(ModItems.SPLINTERED_MARROWSTONE.get());
                        output.accept(ModItems.CAIRNSTONE.get());
                        output.accept(ModItems.SAWMILL.get());
                        output.accept(ModItems.RIBROOT_CUT_PLANKS.get());
                        output.accept(ModItems.EMBERBARK_CUT_PLANKS.get());
                        output.accept(ModItems.PALEVINE_CUT_PLANKS.get());
                        output.accept(ModItems.GRAVESOWN_GLASS.get());
                        output.accept(ModItems.TEMPERED_GLASS.get());
                        output.accept(ModItems.GRAVEWORK_BENCH.get());
                        output.accept(ModItems.PITCH_KILN.get());
                        output.accept(ModItems.RELIQUARY_CRATE.get());
                        output.accept(ModItems.FIELD_KITCHEN.get());
                        output.accept(ModItems.REMNANT_GRAVE.get());
                        output.accept(ModItems.RIBROOT_STEM.get());
                        output.accept(ModItems.RIBROOT_PLANKS.get());
                        output.accept(ModItems.RIBROOT_STAIRS.get());
                        output.accept(ModItems.RIBROOT_SLAB.get());
                        output.accept(ModItems.RIBROOT_FENCE.get());
                        output.accept(ModItems.RIBROOT_FENCE_GATE.get());
                        output.accept(ModItems.RIBROOT_DOOR.get());
                        output.accept(ModItems.RIBROOT_TRAPDOOR.get());
                        output.accept(ModItems.EMBERBARK_STEM.get());
                        output.accept(ModItems.EMBERBARK_PLANKS.get());
                        output.accept(ModItems.EMBERBARK_STAIRS.get());
                        output.accept(ModItems.EMBERBARK_SLAB.get());
                        output.accept(ModItems.EMBERBARK_FENCE.get());
                        output.accept(ModItems.EMBERBARK_FENCE_GATE.get());
                        output.accept(ModItems.EMBERBARK_DOOR.get());
                        output.accept(ModItems.EMBERBARK_TRAPDOOR.get());
                        output.accept(ModItems.EMBERBARK_FOLIAGE.get());
                        output.accept(ModItems.EMBERBARK_SHOOT.get());
                        output.accept(ModItems.PALEVINE_STEM.get());
                        output.accept(ModItems.PALEVINE_PLANKS.get());
                        output.accept(ModItems.PALEVINE_STAIRS.get());
                        output.accept(ModItems.PALEVINE_SLAB.get());
                        output.accept(ModItems.PALEVINE_FENCE.get());
                        output.accept(ModItems.PALEVINE_FENCE_GATE.get());
                        output.accept(ModItems.PALEVINE_DOOR.get());
                        output.accept(ModItems.PALEVINE_TRAPDOOR.get());
                        output.accept(ModItems.PALEVINE_FOLIAGE.get());
                        output.accept(ModItems.PALEVINE_SHOOT.get());
                        addWoodFamily(output, ModItems.CAIRNWOOD);
                        addWoodFamily(output, ModItems.SUTUREWOOD);
                        addWoodFamily(output, ModItems.MOSSWAKE);
                        addWoodFamily(output, ModItems.SUNVEIL);
                        output.accept(ModItems.TALLOW_LANTERN.get());
                        output.accept(ModItems.GLOAM_FARMLAND.get());
                        output.accept(ModItems.VEIL_FOLIAGE.get());
                        output.accept(ModItems.THREADGRASS.get());
                        output.accept(ModItems.RIBROOT_SHOOT.get());
                        output.accept(ModItems.PALLID_BULB.get());
                        output.accept(ModItems.CINDER_BLOOM.get());
                        output.accept(ModItems.RIFT_THORN.get());
                        output.accept(ModItems.MIRE_FROND.get());
                        output.accept(ModItems.MOSSVEIL.get());
                        output.accept(ModItems.AMBER_BLOOM.get());
                        output.accept(ModItems.SINEW_FERN.get());
                        output.accept(ModItems.MARROW_REED.get());
                        output.accept(ModItems.THREADKELP.get());
                        output.accept(ModItems.VEILWEED.get());
                        output.accept(ModItems.DROWNED_ROOTS.get());
                        output.accept(ModItems.BLADDERPOD.get());
                        output.accept(ModItems.LUMEN_KELP.get());
                        output.accept(ModItems.GLOAMWATER_BUCKET.get());
                        output.accept(ModItems.RIBROOT_SPLINT.get());
                        output.accept(ModItems.THREAD_BINDING.get());
                        output.accept(ModItems.HUSHSTONE_SHARD.get());
                        output.accept(ModItems.ASHGRAIN_SEEDS.get());
                        output.accept(ModItems.MIREBEAN_SEEDS.get());
                        output.accept(ModItems.ASHGRAIN.get());
                        output.accept(ModItems.MIREBEAN.get());
                        output.accept(ModItems.ASHGRAIN_LOAF.get());
                        output.accept(ModItems.FIELD_RATION.get());
                        output.accept(ModItems.BONE_CLEAVER.get());
                        output.accept(ModItems.STIRRING_HOOK.get());
                        output.accept(ModItems.MIREBEAN_STEW.get());
                        output.accept(ModItems.CHARRED_MARROW_POT.get());
                        output.accept(ModItems.GLOAM_CHOWDER.get());
                        output.accept(ModItems.NEEDLE_SPRAT.get());
                        output.accept(ModItems.GRAVEBLOOM_DUST.get());
                        output.accept(ModItems.SURVIVOR_CODEX.get());
                        output.accept(ModItems.CRUDE_HANDPICK.get());
                        output.accept(ModItems.BOUND_KNIFE.get());
                        output.accept(ModItems.HUSHSTONE_PICKAXE.get());
                        output.accept(ModItems.HUSHSTONE_SHOVEL.get());
                        output.accept(ModItems.HUSHSTONE_AXE.get());
                        output.accept(ModItems.HUSHSTONE_HOE.get());
                        output.accept(ModItems.GLOAMLINE_ROD.get());
                        output.accept(ModItems.GLOAM_SKIFF.get());
                        output.accept(ModItems.HUSHSTONE_SPEAR.get());
                        output.accept(ModItems.RAGGED_GRAZER_HIDE.get());
                        output.accept(ModItems.TAUT_SINEW.get());
                        output.accept(ModItems.GRAVE_TALLOW.get());
                        output.accept(ModItems.TAINTED_GRAZER_MEAT.get());
                        output.accept(ModItems.CHARRED_GRAZER_MEAT.get());
                        output.accept(ModItems.SMOKED_ROTFIN.get());
                        output.accept(ModItems.HOLLOW_JAW.get());
                        output.accept(ModItems.ROTFIN_FLESH.get());
                        output.accept(ModItems.VEILFIN_FILLET.get());
                        output.accept(ModItems.ROOTSKIMMER_MEAT.get());
                        output.accept(ModItems.QUIETSKIN_HOOD.get());
                        output.accept(ModItems.QUIETSKIN_COAT.get());
                        output.accept(ModItems.QUIETSKIN_LEGWRAPS.get());
                        output.accept(ModItems.QUIETSKIN_BOOTS.get());
                        ModItems.spawnEggs().forEach(egg -> output.accept(egg.get()));
                    })
                    .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FAUNA = TABS.register(
            "fauna",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gravesown.fauna"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModItems.ASH_HOPPER_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters, output) ->
                            ModItems.spawnEggs().forEach(egg -> output.accept(egg.get())))
                    .build()
    );

    private ModCreativeTabs() {
    }

    private static void addWoodFamily(CreativeModeTab.Output output, ModItems.WoodFamilyItems family) {
        output.accept(family.stem().get());
        output.accept(family.planks().get());
        output.accept(family.cutPlanks().get());
        output.accept(family.stairs().get());
        output.accept(family.slab().get());
        output.accept(family.fence().get());
        output.accept(family.fenceGate().get());
        output.accept(family.door().get());
        output.accept(family.trapdoor().get());
        output.accept(family.foliage().get());
        output.accept(family.shoot().get());
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
        modEventBus.addListener(ModCreativeTabs::addSpawnEggsToVanillaTab);
    }

    private static void addSpawnEggsToVanillaTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            ModItems.spawnEggs().forEach(egg -> event.accept(egg.get()));
        }
    }
}
