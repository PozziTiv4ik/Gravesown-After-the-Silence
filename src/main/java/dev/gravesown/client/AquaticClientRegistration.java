package dev.gravesown.client;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.RotfinModel;
import dev.gravesown.client.model.GloamFishModel;
import dev.gravesown.client.renderer.RootskimmerRenderer;
import dev.gravesown.client.renderer.RotfinRenderer;
import dev.gravesown.client.renderer.VeilfinRenderer;
import dev.gravesown.client.renderer.GloamSkiffRenderer;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class AquaticClientRegistration {
    // Gloamwater remains a separate fluid with server-owned rules, but deliberately
    // shares vanilla's proven water animation so adjacent source/flow blocks read
    // as one continuous surface instead of a flickering custom flipbook.
    private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");
    private static final ResourceLocation OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");

    private AquaticClientRegistration() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ROTFIN.get(), RotfinRenderer::new);
        event.registerEntityRenderer(ModEntities.VEILFIN.get(), VeilfinRenderer::new);
        event.registerEntityRenderer(ModEntities.ROOTSKIMMER.get(), RootskimmerRenderer::new);
        event.registerEntityRenderer(ModEntities.GLOAM_SKIFF.get(), GloamSkiffRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RotfinModel.LAYER_LOCATION, RotfinModel::createBodyLayer);
        event.registerLayerDefinition(GloamFishModel.VEILFIN_LAYER, GloamFishModel::createVeilfinLayer);
        event.registerLayerDefinition(GloamFishModel.ROOTSKIMMER_LAYER, GloamFishModel::createRootskimmerLayer);
    }

    @SubscribeEvent
    public static void registerFluidClientExtension(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return OVERLAY;
            }

            @Override
            public int getTintColor() {
                return 0xC4CBDDD2;
            }

            @Override
            public Vector3f modifyFogColor(
                    net.minecraft.client.Camera camera,
                    float partialTick,
                    net.minecraft.client.multiplayer.ClientLevel level,
                    int renderDistance,
                    float darkenWorldAmount,
                    Vector3f fluidFogColor
            ) {
                return new Vector3f(0.035F, 0.095F, 0.075F);
            }
        }, ModFluids.GLOAMWATER_TYPE.get());
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void configureFluidRenderLayer(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.GLOAMWATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_GLOAMWATER.get(), RenderType.translucent());
        });
    }
}
