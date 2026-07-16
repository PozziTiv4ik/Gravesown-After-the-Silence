package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.StitchtuskModel;
import dev.gravesown.entity.Stitchtusk;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class StitchtuskRenderer extends MobRenderer<Stitchtusk, StitchtuskModel> {
    private static final ResourceLocation TEXTURE =
            Gravesown.id("textures/entity/stitchtusk.png");

    public StitchtuskRenderer(EntityRendererProvider.Context context) {
        super(context, new StitchtuskModel(context.bakeLayer(StitchtuskModel.LAYER_LOCATION)), 0.95F);
    }

    @Override
    public ResourceLocation getTextureLocation(Stitchtusk entity) {
        return TEXTURE;
    }
}
