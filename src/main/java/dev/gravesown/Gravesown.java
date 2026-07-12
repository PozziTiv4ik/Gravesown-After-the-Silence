package dev.gravesown;

import com.mojang.logging.LogUtils;
import dev.gravesown.audit.WorldAuditRunner;
import dev.gravesown.config.GravesownConfig;
import dev.gravesown.event.WorldSpawnEvents;
import dev.gravesown.registry.ModArmorMaterials;
import dev.gravesown.registry.ModCreativeTabs;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
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
        ModEntities.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModEntities.registerEventListeners(modEventBus);
        NeoForge.EVENT_BUS.register(new WorldSpawnEvents());
        NeoForge.EVENT_BUS.register(new WorldAuditRunner());

        modContainer.registerConfig(ModConfig.Type.COMMON, GravesownConfig.SPEC);
        LOGGER.info("Gravesown is preparing the world after the Silence.");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
