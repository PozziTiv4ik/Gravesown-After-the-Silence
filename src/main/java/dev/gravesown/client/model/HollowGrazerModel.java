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

    private final ModelPart modelRoot;
    private final ModelPart jaw;
    private final ModelPart spine;
    private final ModelPart rightShoulder;
    private final ModelPart leftShoulder;
    private final ModelPart tailStump;

    public HollowGrazerModel(ModelPart root) {
        super(root, false, 8.0F, 3.0F, 2.0F, 2.0F, 22);
        this.modelRoot = root;
        this.jaw = this.head.getChild("jaw");
        this.spine = this.body.getChild("spine");
        this.rightShoulder = this.body.getChild("right_shoulder");
        this.leftShoulder = this.body.getChild("left_shoulder");
        this.tailStump = this.body.getChild("tail_stump");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5F, -4.0F, -7.0F, 7.0F, 7.0F, 7.0F)
                        .texOffs(0, 64)
                        .addBox(-5.0F, -4.5F, -2.5F, 2.0F, 4.0F, 4.0F)
                        .mirror().texOffs(0, 64)
                        .addBox(3.0F, -4.5F, -2.5F, 2.0F, 4.0F, 4.0F)
                        .texOffs(24, 64)
                        .addBox(-2.5F, -5.5F, -6.5F, 5.0F, 2.0F, 5.0F),
                PartPose.offset(0.0F, 6.0F, -9.0F)
        );
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(0, 14)
                        .addBox(-4.0F, 0.0F, -7.5F, 8.0F, 3.0F, 8.0F)
                        .texOffs(50, 64)
                        .addBox(-3.25F, 2.0F, -8.0F, 1.0F, 3.0F, 1.0F)
                        .texOffs(54, 64)
                        .addBox(2.25F, 2.0F, -8.0F, 1.0F, 3.0F, 1.0F)
                        .texOffs(58, 64)
                        .addBox(-1.0F, 2.5F, -7.75F, 2.0F, 1.0F, 3.0F),
                PartPose.offset(0.0F, 2.0F, 0.0F)
        );

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-6.0F, -9.0F, -7.0F, 12.0F, 18.0F, 10.0F)
                        .texOffs(68, 64)
                        .addBox(-4.0F, -7.5F, 2.0F, 8.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 7.0F, 2.0F, Mth.HALF_PI, 0.0F, 0.0F)
        );
        body.addOrReplaceChild(
                "spine",
                CubeListBuilder.create()
                        .texOffs(92, 64)
                        .addBox(-1.0F, -10.5F, -5.0F, 2.0F, 3.0F, 12.0F)
                        .texOffs(0, 80)
                        .addBox(-0.5F, -13.0F, -3.0F, 1.0F, 3.0F, 2.0F)
                        .texOffs(6, 80)
                        .addBox(-0.5F, -12.0F, 1.0F, 1.0F, 2.0F, 2.0F)
                        .texOffs(12, 80)
                        .addBox(-0.5F, -11.5F, 5.0F, 1.0F, 2.0F, 2.0F),
                PartPose.ZERO
        );
        body.addOrReplaceChild(
                "right_shoulder",
                CubeListBuilder.create()
                        .texOffs(18, 80)
                        .addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F)
                        .texOffs(40, 80)
                        .addBox(-3.5F, -1.0F, -1.5F, 2.0F, 4.0F, 3.0F),
                PartPose.offset(-5.25F, -5.0F, -3.5F)
        );
        body.addOrReplaceChild(
                "left_shoulder",
                CubeListBuilder.create().mirror()
                        .texOffs(18, 80)
                        .addBox(-2.5F, -3.0F, -3.0F, 5.0F, 6.0F, 6.0F)
                        .texOffs(40, 80)
                        .addBox(1.5F, -1.0F, -1.5F, 2.0F, 4.0F, 3.0F),
                PartPose.offset(5.25F, -5.0F, -3.5F)
        );
        body.addOrReplaceChild(
                "tail_stump",
                CubeListBuilder.create()
                        .texOffs(76, 80)
                        .addBox(-2.0F, -1.5F, 0.0F, 4.0F, 3.0F, 5.0F)
                        .texOffs(94, 80)
                        .addBox(-1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, -0.35F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(0, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F)
                        .texOffs(0, 92).addBox(-2.25F, 9.5F, -3.5F, 4.0F, 2.0F, 5.0F),
                PartPose.offset(-4.0F, 13.0F, 7.0F)
        );
        root.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F)
                        .texOffs(0, 92).addBox(-1.75F, 9.5F, -3.5F, 4.0F, 2.0F, 5.0F),
                PartPose.offset(4.0F, 13.0F, 7.0F)
        );
        root.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(28, 43).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F)
                        .texOffs(34, 92).addBox(-1.75F, 11.0F, -3.25F, 3.5F, 2.0F, 4.0F),
                PartPose.offset(-4.0F, 11.0F, -6.0F)
        );
        root.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(28, 43).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F)
                        .texOffs(34, 92).addBox(-1.75F, 11.0F, -3.25F, 3.5F, 2.0F, 4.0F),
                PartPose.offset(4.0F, 11.0F, -6.0F)
        );

        return LayerDefinition.create(mesh, 128, 128);
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
        this.modelRoot.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        float movement = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float breath = Mth.sin(ageInTicks * 0.11F);
        float attack = Mth.sin(Mth.sqrt(this.attackTime) * Mth.PI);
        float aggression = entity.isAggressive() ? 1.0F : 0.0F;
        float gait = Mth.sin(limbSwing * 0.6662F);

        this.head.xRot += aggression * 0.12F - attack * 0.28F;
        this.head.zRot = gait * movement * 0.035F + breath * 0.012F;
        this.head.y += Mth.abs(gait) * movement * 0.35F;
        this.jaw.xRot = 0.07F
                + aggression * (0.42F + Mth.sin(ageInTicks * 0.31F) * 0.07F)
                + attack * 0.42F;

        this.body.y = 7.0F + Mth.abs(gait) * movement * 0.45F + breath * 0.08F;
        this.body.zRot = Mth.sin(limbSwing * 0.35F) * 0.055F * movement;
        this.spine.yRot = -this.body.zRot * 0.8F;
        this.spine.yScale = 1.0F + breath * 0.018F;
        this.rightShoulder.zRot = -0.055F + gait * movement * 0.03F;
        this.leftShoulder.zRot = 0.055F - gait * movement * 0.03F;
        this.tailStump.xRot = -0.35F + breath * 0.035F + aggression * 0.16F;
        this.tailStump.yRot = Mth.sin(ageInTicks * 0.17F) * 0.08F;

        this.rightHindLeg.zRot = 0.035F;
        this.leftHindLeg.zRot = -0.035F;
        this.rightFrontLeg.zRot = -0.035F;
        this.leftFrontLeg.zRot = 0.035F;
    }
}
