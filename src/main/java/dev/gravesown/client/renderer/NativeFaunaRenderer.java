package dev.gravesown.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gravesown.Gravesown;
import dev.gravesown.client.model.NativeFaunaModel;
import dev.gravesown.entity.NativeFauna;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class NativeFaunaRenderer extends MobRenderer<NativeFauna, NativeFaunaModel> {
    public NativeFaunaRenderer(EntityRendererProvider.Context context) {
        super(context, new NativeFaunaModel(context.bakeLayer(NativeFaunaModel.LAYER_LOCATION)), 0.36F);
    }

    @Override
    public ResourceLocation getTextureLocation(NativeFauna entity) {
        return Gravesown.id("textures/entity/" + entity.species().id() + ".png");
    }

    @Override
    protected void scale(NativeFauna entity, PoseStack poseStack, float partialTickTime) {
        float scale = switch (entity.species()) {
            case ASH_HOPPER, MIRE_TOAD -> 0.56F;
            case GRAVEWING, CINDER_FOWL -> 0.58F;
            case AMBER_JAY -> 0.50F;
            case BARK_MARTEN, EMBER_FOX -> 0.72F;
            case REED_LYNX -> 0.88F;
            case CRAG_RAM -> 0.94F;
            case ROOTBACK, RIFT_PUMA, MOSSBOAR, PALLID_HART -> 1.04F;
            case SUNHORN -> 1.10F;
        };
        poseStack.scale(scale, scale, scale);
    }
}
