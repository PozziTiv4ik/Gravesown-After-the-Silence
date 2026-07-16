package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.RotfinModel;
import dev.gravesown.entity.Rotfin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class RotfinRenderer extends MobRenderer<Rotfin, RotfinModel> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/rotfin.png");

    public RotfinRenderer(EntityRendererProvider.Context context) {
        super(context, new RotfinModel(context.bakeLayer(RotfinModel.LAYER_LOCATION)), 0.28F);
    }

    @Override
    public ResourceLocation getTextureLocation(Rotfin entity) {
        return TEXTURE;
    }
}
