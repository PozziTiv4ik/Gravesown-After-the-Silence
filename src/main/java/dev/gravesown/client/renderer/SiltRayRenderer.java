package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.SiltRayModel;
import dev.gravesown.entity.SiltRay;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class SiltRayRenderer extends MobRenderer<SiltRay, SiltRayModel> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/silt_ray.png");

    public SiltRayRenderer(EntityRendererProvider.Context context) {
        super(context, new SiltRayModel(context.bakeLayer(SiltRayModel.LAYER_LOCATION)), 0.48F);
    }

    @Override
    public ResourceLocation getTextureLocation(SiltRay entity) { return TEXTURE; }
}
