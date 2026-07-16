package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.Stitchtusk;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public final class StitchtuskModel extends HierarchicalModel<Stitchtusk> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("stitchtusk"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightTusk;
    private final ModelPart leftTusk;
    private final ModelPart rightTuskTip;
    private final ModelPart leftTuskTip;
    private final ModelPart body;
    private final ModelPart stitchedRidge;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public StitchtuskModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.rightTusk = this.head.getChild("right_tusk");
        this.leftTusk = this.head.getChild("left_tusk");
        this.rightTuskTip = this.rightTusk.getChild("tip");
        this.leftTuskTip = this.leftTusk.getChild("tip");
        this.body = root.getChild("body");
        this.stitchedRidge = this.body.getChild("stitched_ridge");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -5.0F, -9.0F, 10.0F, 9.0F, 10.0F)
                        .texOffs(0, 22)
                        .addBox(-4.0F, -1.5F, -13.0F, 8.0F, 5.0F, 5.0F)
                        .texOffs(42, 0)
                        .addBox(-8.0F, -4.5F, -4.0F, 4.0F, 1.0F, 5.0F)
                        .texOffs(62, 0)
                        .addBox(4.0F, -4.5F, -4.0F, 4.0F, 1.0F, 5.0F)
                        .texOffs(48, 20)
                        .addBox(-6.0F, -6.25F, -5.5F, 4.0F, 3.0F, 6.0F)
                        .mirror().texOffs(48, 20)
                        .addBox(2.0F, -6.25F, -5.5F, 4.0F, 3.0F, 6.0F)
                        .texOffs(28, 22)
                        .addBox(-1.0F, -6.75F, -8.0F, 2.0F, 2.0F, 5.0F),
                PartPose.offset(0.0F, 8.0F, -10.5F)
        );
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-4.5F, 0.0F, -11.5F, 9.0F, 3.0F, 8.0F)
                        .texOffs(88, 20)
                        .addBox(-4.75F, 1.5F, -8.0F, 3.0F, 2.0F, 5.0F),
                PartPose.offset(0.0F, 2.5F, -0.5F)
        );
        PartDefinition rightTusk = head.addOrReplaceChild(
                "right_tusk",
                CubeListBuilder.create()
                        .texOffs(28, 22)
                        .addBox(-1.25F, -1.0F, -1.25F, 2.5F, 6.0F, 2.5F)
                        .texOffs(38, 22)
                        .addBox(-1.0F, 3.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(-4.0F, 1.0F, -11.5F, -0.48F, 0.0F, 0.14F)
        );
        rightTusk.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(28, 22)
                        .addBox(-0.75F, 0.0F, -0.75F, 1.5F, 5.0F, 1.5F),
                PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.42F, 0.0F, 0.14F)
        );
        PartDefinition leftTusk = head.addOrReplaceChild(
                "left_tusk",
                CubeListBuilder.create().mirror()
                        .texOffs(28, 22)
                        .addBox(-1.25F, -1.0F, -1.25F, 2.5F, 6.0F, 2.5F)
                        .texOffs(38, 22)
                        .addBox(-1.0F, 3.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(4.0F, 1.0F, -11.5F, -0.48F, 0.0F, -0.14F)
        );
        leftTusk.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().mirror()
                        .texOffs(28, 22)
                        .addBox(-0.75F, 0.0F, -0.75F, 1.5F, 5.0F, 1.5F),
                PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.42F, 0.0F, -0.14F)
        );

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 40)
                        .addBox(-8.0F, -11.0F, -9.0F, 16.0F, 21.0F, 18.0F)
                        .texOffs(74, 40)
                        .addBox(-5.0F, -13.0F, -9.5F, 10.0F, 5.0F, 11.0F)
                        .texOffs(82, 0)
                        .addBox(-9.75F, -7.5F, -5.0F, 3.0F, 8.0F, 10.0F)
                        .mirror().texOffs(82, 0)
                        .addBox(6.75F, -7.5F, -5.0F, 3.0F, 8.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 8.0F, 1.5F, Mth.HALF_PI, 0.0F, 0.0F)
        );
        body.addOrReplaceChild(
                "stitched_ridge",
                CubeListBuilder.create()
                        .texOffs(0, 104)
                        .addBox(-2.0F, -13.0F, -7.0F, 4.0F, 5.0F, 18.0F)
                        .texOffs(106, 20)
                        .addBox(-1.0F, -16.0F, -5.0F, 2.0F, 4.0F, 3.0F)
                        .texOffs(118, 20)
                        .addBox(-0.75F, -15.0F, 1.0F, 1.5F, 3.0F, 3.0F)
                        .texOffs(106, 29)
                        .addBox(-1.0F, -15.5F, 7.0F, 2.0F, 3.5F, 3.0F),
                PartPose.ZERO
        );
        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(46, 104)
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 8.0F)
                        .texOffs(72, 104)
                        .addBox(-2.5F, -2.5F, 5.0F, 5.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 6.0F, 8.0F, -0.28F, 0.0F, 0.0F)
        );
        tail.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(90, 104)
                        .addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.3F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(0, 82).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F)
                        .texOffs(110, 104).addBox(-3.25F, 3.0F, -3.0F, 3.0F, 6.0F, 6.0F)
                        .texOffs(46, 118).addBox(-3.0F, 10.0F, -4.25F, 6.0F, 2.0F, 7.0F),
                PartPose.offset(-5.5F, 12.0F, -6.0F)
        );
        root.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 82).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F)
                        .texOffs(110, 104).addBox(0.25F, 3.0F, -3.0F, 3.0F, 6.0F, 6.0F)
                        .texOffs(46, 118).addBox(-3.0F, 10.0F, -4.25F, 6.0F, 2.0F, 7.0F),
                PartPose.offset(5.5F, 12.0F, -6.0F)
        );
        root.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(44, 82).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 10.0F, 6.0F)
                        .texOffs(74, 66).addBox(-3.5F, 8.0F, -4.0F, 7.0F, 2.0F, 7.0F),
                PartPose.offset(-5.5F, 14.0F, 7.0F)
        );
        root.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(44, 82).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 10.0F, 6.0F)
                        .texOffs(74, 66).addBox(-3.5F, 8.0F, -4.0F, 7.0F, 2.0F, 7.0F),
                PartPose.offset(5.5F, 14.0F, 7.0F)
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(
            Stitchtusk entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        float headYaw = netHeadYaw * Mth.DEG_TO_RAD;
        float headPitchRadians = headPitch * Mth.DEG_TO_RAD;
        float strideRate = entity.isRushing() ? 1.9F : 0.75F;
        float stride = Mth.cos(limbSwing * strideRate) * 1.05F * limbSwingAmount;
        float movement = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float breath = Mth.sin(ageInTicks * 0.095F);
        float attack = Mth.sin(Mth.sqrt(this.attackTime) * Mth.PI);

        this.rightFrontLeg.xRot = stride;
        this.leftHindLeg.xRot = stride;
        this.leftFrontLeg.xRot = -stride;
        this.rightHindLeg.xRot = -stride;
        this.head.yRot = headYaw;
        this.head.zRot = 0.0F;
        this.body.y = 8.0F + Mth.abs(Mth.sin(limbSwing * 0.75F)) * movement * 0.35F + breath * 0.06F;
        this.body.zRot = Mth.sin(limbSwing * 0.35F) * 0.045F * movement;
        this.body.xScale = 1.0F + breath * 0.012F;
        this.stitchedRidge.yScale = 1.0F + breath * 0.02F;
        this.jaw.xRot = 0.06F + attack * 0.46F;
        this.rightTusk.zRot = 0.14F + breath * 0.012F;
        this.leftTusk.zRot = -0.14F - breath * 0.01F;
        this.rightTuskTip.xRot = -0.42F + breath * 0.015F;
        this.leftTuskTip.xRot = this.rightTuskTip.xRot;
        this.tail.xRot = -0.28F + Mth.sin(ageInTicks * 0.16F) * 0.045F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.13F + limbSwing * 0.4F) * 0.1F;
        this.tailTip.xRot = 0.3F + Mth.sin(ageInTicks * 0.18F) * 0.075F;
        this.tailTip.yRot = -this.tail.yRot * 1.2F;

        if (entity.isTelegraphingCharge()) {
            float tremor = Mth.sin(ageInTicks * 1.55F);
            this.head.xRot = 0.42F + tremor * 0.055F;
            this.head.zRot = tremor * 0.045F;
            this.body.xRot = Mth.HALF_PI + 0.08F;
            this.body.zRot = -tremor * 0.025F;
            this.rightFrontLeg.xRot = -0.28F;
            this.leftFrontLeg.xRot = -0.28F;
            this.rightFrontLeg.zRot = 0.13F;
            this.leftFrontLeg.zRot = -0.13F;
            this.rightHindLeg.xRot = 0.18F;
            this.leftHindLeg.xRot = 0.18F;
            this.rightHindLeg.zRot = 0.1F;
            this.leftHindLeg.zRot = -0.1F;
            this.jaw.xRot = 0.2F + Mth.abs(tremor) * 0.13F;
            this.tail.xRot = -0.48F;
        }
        else if (entity.isRushing()) {
            this.head.xRot = 0.72F;
            this.head.yRot *= 0.25F;
            this.body.xRot = Mth.HALF_PI + 0.14F;
            this.body.zRot *= 0.35F;
            this.rightFrontLeg.xRot *= 1.35F;
            this.leftFrontLeg.xRot *= 1.35F;
            this.rightHindLeg.xRot *= 1.2F;
            this.leftHindLeg.xRot *= 1.2F;
            this.jaw.xRot = 0.28F + attack * 0.28F;
            this.tail.xRot = -0.65F;
            this.tailTip.xRot = 0.08F;
        }
        else {
            this.head.xRot = headPitchRadians * 0.6F + breath * 0.018F - attack * 0.14F;
            this.rightFrontLeg.zRot = 0.035F;
            this.leftFrontLeg.zRot = -0.035F;
            this.rightHindLeg.zRot = 0.035F;
            this.leftHindLeg.zRot = -0.035F;
        }
    }
}
