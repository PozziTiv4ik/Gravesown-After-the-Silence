package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.HollowGrazerModel;
import dev.gravesown.entity.HollowGrazer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class HollowGrazerRenderer extends MobRenderer<HollowGrazer, HollowGrazerModel> {
    private static final ResourceLocation TEXTURE =
            Gravesown.id("textures/entity/hollow_grazer.png");

    public HollowGrazerRenderer(EntityRendererProvider.Context context) {
        super(context, new HollowGrazerModel(context.bakeLayer(HollowGrazerModel.LAYER_LOCATION)), 0.65F);
    }

    @Override
    public ResourceLocation getTextureLocation(HollowGrazer entity) {
        return TEXTURE;
    }
}
