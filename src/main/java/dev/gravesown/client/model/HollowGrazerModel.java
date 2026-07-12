package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.HollowGrazer;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public final class HollowGrazerModel extends QuadrupedModel<HollowGrazer> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("hollow_grazer"), "main");

    private final ModelPart jaw;

    public HollowGrazerModel(ModelPart root) {
        super(root, false, 8.0F, 3.0F, 2.0F, 2.0F, 22);
        this.jaw = this.head.getChild("jaw");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5F, -4.0F, -7.0F, 7.0F, 7.0F, 7.0F)
                        .texOffs(0, 28)
                        .addBox(-5.0F, -3.0F, -3.0F, 2.0F, 2.0F, 5.0F)
                        .texOffs(14, 28)
                        .addBox(3.0F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 6.0F, -9.0F)
        );
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(0, 14)
                        .addBox(-4.0F, 0.0F, -7.5F, 8.0F, 3.0F, 8.0F),
                PartPose.offset(0.0F, 2.0F, 0.0F)
        );

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-6.0F, -9.0F, -7.0F, 12.0F, 18.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 7.0F, 2.0F, Mth.HALF_PI, 0.0F, 0.0F)
        );
        body.addOrReplaceChild(
                "spine",
                CubeListBuilder.create()
                        .texOffs(32, 28)
                        .addBox(-1.0F, -10.5F, -5.0F, 2.0F, 3.0F, 12.0F),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create().texOffs(0, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F),
                PartPose.offset(-4.0F, 13.0F, 7.0F)
        );
        root.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create().texOffs(16, 36).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F),
                PartPose.offset(4.0F, 13.0F, 7.0F)
        );
        root.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create().texOffs(28, 43).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 13.0F, 3.0F),
                PartPose.offset(-4.0F, 11.0F, -6.0F)
        );
        root.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create().texOffs(40, 43).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F),
                PartPose.offset(4.0F, 11.0F, -6.0F)
        );

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(
            HollowGrazer entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.jaw.xRot = entity.isAggressive()
                ? 0.55F + Mth.sin(ageInTicks * 0.3F) * 0.08F
                : 0.08F;
        this.body.zRot = Mth.sin(limbSwing * 0.35F) * 0.04F * limbSwingAmount;
    }
}
