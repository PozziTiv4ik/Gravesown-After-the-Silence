package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.WoundscentModel;
import dev.gravesown.entity.Woundscent;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class WoundscentRenderer extends MobRenderer<Woundscent, WoundscentModel> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/woundscent.png");

    public WoundscentRenderer(EntityRendererProvider.Context context) {
        super(context, new WoundscentModel(context.bakeLayer(WoundscentModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Woundscent entity) {
        return TEXTURE;
    }
}
