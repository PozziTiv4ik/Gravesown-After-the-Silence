package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.RibspringModel;
import dev.gravesown.entity.Ribspring;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class RibspringRenderer extends MobRenderer<Ribspring, RibspringModel> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/ribspring.png");

    public RibspringRenderer(EntityRendererProvider.Context context) {
        super(context, new RibspringModel(context.bakeLayer(RibspringModel.LAYER_LOCATION)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(Ribspring entity) {
        return TEXTURE;
    }
}
