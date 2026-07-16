package dev.gravesown;

import com.mojang.logging.LogUtils;
import dev.gravesown.audit.WorldAuditRunner;
import dev.gravesown.config.GravesownConfig;
import dev.gravesown.event.WorldSpawnEvents;
import dev.gravesown.event.SurvivalGuideEvents;
import dev.gravesown.event.AdvancementPackEvents;
import dev.gravesown.event.FarmingEvents;
import dev.gravesown.event.StickyMovementEvents;
import dev.gravesown.registry.ModArmorMaterials;
import dev.gravesown.registry.ModAttachments;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModBlockEntities;
import dev.gravesown.registry.ModCreativeTabs;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModFeatures;
import dev.gravesown.registry.ModFluids;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Gravesown.MOD_ID)
public final class Gravesown {
    public static final String MOD_ID = "gravesown";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Gravesown(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.register(modEventBus);
        ModMenus.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModEntities.registerEventListeners(modEventBus);
        modEventBus.addListener(AdvancementPackEvents::addPackFinders);
        NeoForge.EVENT_BUS.register(new WorldSpawnEvents());
        NeoForge.EVENT_BUS.register(new SurvivalGuideEvents());
        NeoForge.EVENT_BUS.register(new FarmingEvents());
        NeoForge.EVENT_BUS.register(new StickyMovementEvents());
        NeoForge.EVENT_BUS.register(new WorldAuditRunner());

        modContainer.registerConfig(ModConfig.Type.COMMON, GravesownConfig.SPEC);
        LOGGER.info("Gravesown is preparing the world after the Silence.");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
