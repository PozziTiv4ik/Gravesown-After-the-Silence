package dev.gravesown.client.renderer;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.GloamFishModel;
import dev.gravesown.entity.Veilfin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class VeilfinRenderer extends MobRenderer<Veilfin, GloamFishModel<Veilfin>> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/veilfin.png");

    public VeilfinRenderer(EntityRendererProvider.Context context) {
        super(context, new GloamFishModel<>(context.bakeLayer(GloamFishModel.VEILFIN_LAYER)), 0.22F);
    }

    @Override
    public ResourceLocation getTextureLocation(Veilfin entity) {
        return TEXTURE;
    }
}
