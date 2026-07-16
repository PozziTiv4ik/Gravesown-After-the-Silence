package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.BuriedRemnantModel;
import dev.gravesown.entity.BuriedRemnant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class BuriedRemnantRenderer extends MobRenderer<BuriedRemnant, BuriedRemnantModel> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/buried_remnant.png");

    public BuriedRemnantRenderer(EntityRendererProvider.Context context) {
        super(context, new BuriedRemnantModel(context.bakeLayer(BuriedRemnantModel.LAYER_LOCATION)), 0.42F);
    }

    @Override
    public ResourceLocation getTextureLocation(BuriedRemnant entity) {
        return TEXTURE;
    }
}
