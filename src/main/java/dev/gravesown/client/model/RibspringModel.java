package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.Ribspring;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public final class RibspringModel extends QuadrupedModel<Ribspring> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("ribspring"), "main");

    private final ModelPart modelRoot;
    private final ModelPart leftRoot;
    private final ModelPart rightRoot;
    private final ModelPart leftRootTip;
    private final ModelPart rightRootTip;
    private final ModelPart throatFan;
    private final ModelPart ribRack;
    private final ModelPart tail;
    private final ModelPart tailTip;

    public RibspringModel(ModelPart root) {
        super(root, false, 6.0F, 2.0F, 1.8F, 1.8F, 20);
        this.modelRoot = root;
        this.leftRoot = this.head.getChild("left_root");
        this.rightRoot = this.head.getChild("right_root");
        this.leftRootTip = this.leftRoot.getChild("tip");
        this.rightRootTip = this.rightRoot.getChild("tip");
        this.throatFan = this.head.getChild("throat_fan");
        this.ribRack = this.body.getChild("rib_rack");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -4.0F, -5.0F, 6.0F, 5.0F, 7.0F)
                        .texOffs(0, 14)
                        .addBox(-2.0F, -1.5F, -7.0F, 4.0F, 3.0F, 3.0F)
                        .texOffs(66, 0)
                        .addBox(-4.0F, -4.0F, -2.5F, 2.0F, 3.0F, 4.0F)
                        .mirror().texOffs(66, 0)
                        .addBox(2.0F, -4.0F, -2.5F, 2.0F, 3.0F, 4.0F)
                        .texOffs(92, 0)
                        .addBox(-0.75F, -2.5F, -8.25F, 1.5F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 11.0F, -6.0F)
        );
        PartDefinition leftRoot = head.addOrReplaceChild(
                "left_root",
                CubeListBuilder.create()
                        .texOffs(101, 0)
                        .addBox(-0.5F, -5.5F, -0.5F, 1.0F, 6.0F, 2.0F)
                        .texOffs(109, 0)
                        .addBox(-0.5F, -6.5F, 0.5F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(2.0F, -3.5F, -0.5F)
        );
        leftRoot.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(118, 0)
                        .addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F)
                        .texOffs(66, 10)
                        .addBox(-0.5F, -3.5F, -2.0F, 1.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -5.5F, 2.5F, -0.35F, 0.0F, 0.22F)
        );
        PartDefinition rightRoot = head.addOrReplaceChild(
                "right_root",
                CubeListBuilder.create().mirror()
                        .texOffs(101, 0)
                        .addBox(-0.5F, -5.5F, -0.5F, 1.0F, 6.0F, 2.0F)
                        .texOffs(109, 0)
                        .addBox(-0.5F, -6.5F, 0.5F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(-2.0F, -3.5F, -0.5F)
        );
        rightRoot.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().mirror()
                        .texOffs(118, 0)
                        .addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F)
                        .texOffs(66, 10)
                        .addBox(-0.5F, -3.5F, -2.0F, 1.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -5.5F, 2.5F, -0.35F, 0.0F, -0.22F)
        );
        head.addOrReplaceChild(
                "throat_fan",
                CubeListBuilder.create()
                        .texOffs(108, 9)
                        .addBox(-2.5F, 0.0F, -1.0F, 5.0F, 5.0F, 1.0F)
                        .texOffs(66, 18)
                        .addBox(-1.5F, 4.0F, -0.75F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, -4.0F, -0.2F, 0.0F, 0.0F)
        );

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-4.0F, -7.0F, -5.0F, 8.0F, 12.0F, 8.0F)
                        .texOffs(76, 18)
                        .addBox(-4.5F, -5.0F, -3.0F, 1.0F, 8.0F, 6.0F)
                        .texOffs(92, 18)
                        .addBox(3.5F, -5.0F, -3.0F, 1.0F, 8.0F, 6.0F)
                        .texOffs(108, 18)
                        .addBox(-2.5F, -8.0F, -3.5F, 5.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 13.0F, 1.0F, Mth.HALF_PI, 0.0F, 0.0F)
        );
        body.addOrReplaceChild(
                "rib_rack",
                CubeListBuilder.create()
                        .texOffs(66, 34).addBox(-5.25F, -5.5F, -3.5F, 1.0F, 2.0F, 7.0F)
                        .texOffs(84, 34).addBox(-5.0F, -2.0F, -3.0F, 1.0F, 2.0F, 6.0F)
                        .texOffs(100, 34).addBox(-4.75F, 1.5F, -2.5F, 1.0F, 2.0F, 5.0F)
                        .mirror().texOffs(66, 34).addBox(4.25F, -5.5F, -3.5F, 1.0F, 2.0F, 7.0F)
                        .mirror().texOffs(84, 34).addBox(4.0F, -2.0F, -3.0F, 1.0F, 2.0F, 6.0F)
                        .mirror().texOffs(100, 34).addBox(3.75F, 1.5F, -2.5F, 1.0F, 2.0F, 5.0F),
                PartPose.ZERO
        );
        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(98, 45)
                        .addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F)
                        .texOffs(114, 45)
                        .addBox(-1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 2.0F, -0.45F, 0.0F, 0.0F)
        );
        tail.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(66, 56)
                        .addBox(-0.75F, -0.75F, 0.0F, 1.5F, 1.5F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.5F, 0.25F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(0, 44)
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 5.0F)
                        .texOffs(18, 44)
                        .addBox(-1.0F, 4.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(81, 56)
                        .addBox(-1.5F, 8.0F, -2.5F, 3.0F, 1.5F, 3.5F),
                PartPose.offset(-2.5F, 15.0F, 5.0F)
        );
        root.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 44)
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 5.0F)
                        .texOffs(18, 44)
                        .addBox(-1.0F, 4.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(81, 56)
                        .addBox(-1.5F, 8.0F, -2.5F, 3.0F, 1.5F, 3.5F),
                PartPose.offset(2.5F, 15.0F, 5.0F)
        );
        root.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(34, 44)
                        .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                        .texOffs(111, 56)
                        .addBox(-1.25F, 7.0F, -2.25F, 2.5F, 1.5F, 3.0F),
                PartPose.offset(-2.25F, 16.0F, -4.0F)
        );
        root.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(34, 44)
                        .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                        .texOffs(111, 56)
                        .addBox(-1.25F, 7.0F, -2.25F, 2.5F, 1.5F, 3.0F),
                PartPose.offset(2.25F, 16.0F, -4.0F)
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(
            Ribspring entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.modelRoot.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float sprint = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float gait = Mth.sin(limbSwing * 0.6662F);
        float leap = Mth.abs(gait) * sprint;
        float nervousTwitch = Mth.sin(ageInTicks * 0.73F) * 0.055F;
        float breath = Mth.sin(ageInTicks * 0.15F);

        this.body.y = 13.0F - leap * 1.35F + breath * 0.05F;
        this.body.xRot = Mth.HALF_PI - sprint * 0.09F;
        this.body.zRot = gait * sprint * 0.075F;
        this.body.xScale = 1.0F + breath * 0.018F;
        this.ribRack.xScale = 1.0F + breath * 0.032F;
        this.ribRack.zRot = -this.body.zRot * 0.55F;

        this.head.y = 11.0F - leap * 0.45F;
        this.head.xRot += sprint * 0.12F + Mth.sin(ageInTicks * 0.19F) * (1.0F - sprint) * 0.035F;
        this.head.zRot = -gait * sprint * 0.055F + nervousTwitch * (1.0F - sprint);
        this.rightHindLeg.xRot *= 1.35F;
        this.leftHindLeg.xRot *= 1.35F;
        this.rightFrontLeg.xRot *= 0.9F;
        this.leftFrontLeg.xRot *= 0.9F;
        this.rightHindLeg.zRot = 0.08F;
        this.leftHindLeg.zRot = -0.08F;
        this.rightFrontLeg.zRot = -0.035F;
        this.leftFrontLeg.zRot = 0.035F;

        this.leftRoot.zRot = -0.08F + nervousTwitch + gait * sprint * 0.08F;
        this.rightRoot.zRot = 0.08F - nervousTwitch - gait * sprint * 0.07F;
        this.leftRoot.yRot = Mth.sin(ageInTicks * 0.21F) * 0.04F;
        this.rightRoot.yRot = -this.leftRoot.yRot;
        this.leftRootTip.zRot = 0.24F + nervousTwitch * 1.6F;
        this.rightRootTip.zRot = -this.leftRootTip.zRot;
        this.throatFan.xRot = -0.2F + breath * 0.06F + sprint * 0.12F;
        this.throatFan.yScale = 1.0F + breath * 0.025F;

        this.tail.xRot = -0.45F + Mth.sin(ageInTicks * 0.25F) * 0.1F - sprint * 0.18F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.31F + limbSwing) * 0.13F;
        this.tailTip.xRot = 0.25F + Mth.sin(ageInTicks * 0.38F) * 0.14F;
        this.tailTip.yRot = -this.tail.yRot * 1.25F;
    }
}
