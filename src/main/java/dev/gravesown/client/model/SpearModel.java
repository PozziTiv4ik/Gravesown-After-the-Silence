package dev.gravesown.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.gravesown.Gravesown;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

/** A single-point, long-shaft spear silhouette distinct from vanilla's trident. */
public final class SpearModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("hushstone_spear"), "main");

    private final ModelPart root;

    public SpearModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition spear = root.addOrReplaceChild(
                "spear",
                CubeListBuilder.create()
                        .texOffs(0, 8).addBox(-0.5F, 3.0F, -0.5F, 1.0F, 25.0F, 1.0F)
                        .texOffs(4, 8).addBox(-1.5F, 1.0F, -0.5F, 3.0F, 2.0F, 1.0F)
                        .texOffs(0, 0).addBox(-1.5F, -5.0F, -0.5F, 3.0F, 6.0F, 1.0F),
                PartPose.ZERO
        );
        spear.addOrReplaceChild(
                "butt",
                CubeListBuilder.create().texOffs(8, 8).addBox(-1.0F, 28.0F, -0.5F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color) {
        this.root.render(poseStack, consumer, light, overlay, color);
    }
}
