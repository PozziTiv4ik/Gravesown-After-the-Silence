package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.GloamFishModel;
import dev.gravesown.entity.Rootskimmer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class RootskimmerRenderer extends MobRenderer<Rootskimmer, GloamFishModel<Rootskimmer>> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/rootskimmer.png");

    public RootskimmerRenderer(EntityRendererProvider.Context context) {
        super(context, new GloamFishModel<>(context.bakeLayer(GloamFishModel.ROOTSKIMMER_LAYER)), 0.26F);
    }

    @Override
    public ResourceLocation getTextureLocation(Rootskimmer entity) {
        return TEXTURE;
    }
}
