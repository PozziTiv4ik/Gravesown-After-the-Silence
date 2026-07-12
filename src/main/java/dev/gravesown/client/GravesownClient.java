package dev.gravesown.client;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.HollowGrazerModel;
import dev.gravesown.client.renderer.HollowGrazerRenderer;
import dev.gravesown.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Gravesown.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class GravesownClient {
    public GravesownClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HOLLOW_GRAZER.get(), HollowGrazerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HollowGrazerModel.LAYER_LOCATION, HollowGrazerModel::createBodyLayer);
    }
}
