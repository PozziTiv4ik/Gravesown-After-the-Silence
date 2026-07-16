package dev.gravesown.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.gravesown.Gravesown;
import dev.gravesown.client.model.SpearModel;
import dev.gravesown.entity.ThrownSpear;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ThrownSpearRenderer extends EntityRenderer<ThrownSpear> {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/hushstone_spear.png");
    private final SpearModel model;

    public ThrownSpearRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SpearModel(context.bakeLayer(SpearModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            ThrownSpear spear,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, spear.yRotO, spear.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, spear.xRotO, spear.getXRot()) + 90.0F));
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer,
                this.model.renderType(TEXTURE),
                false,
                spear.isFoil()
        );
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(spear, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownSpear entity) {
        return TEXTURE;
    }
}
