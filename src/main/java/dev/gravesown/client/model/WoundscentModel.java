package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.Woundscent;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public final class WoundscentModel extends QuadrupedModel<Woundscent> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("woundscent"), "main");

    private final ModelPart modelRoot;
    private final ModelPart jaw;
    private final ModelPart leftScentLobe;
    private final ModelPart rightScentLobe;
    private final ModelPart leftScentTip;
    private final ModelPart rightScentTip;
    private final ModelPart spine;
    private final ModelPart tail;
    private final ModelPart tailTip;

    public WoundscentModel(ModelPart root) {
        super(root, false, 7.0F, 2.0F, 2.0F, 2.0F, 21);
        this.modelRoot = root;
        this.jaw = this.head.getChild("jaw");
        this.leftScentLobe = this.head.getChild("left_scent_lobe");
        this.rightScentLobe = this.head.getChild("right_scent_lobe");
        this.leftScentTip = this.leftScentLobe.getChild("tip");
        this.rightScentTip = this.rightScentLobe.getChild("tip");
        this.spine = this.body.getChild("spine");
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
                        .addBox(-4.0F, -4.0F, -6.0F, 8.0F, 6.0F, 7.0F)
                        .texOffs(0, 14)
                        .addBox(-3.0F, -1.5F, -9.0F, 6.0F, 3.0F, 4.0F)
                        .texOffs(0, 64)
                        .addBox(-4.5F, -5.0F, -5.5F, 9.0F, 2.0F, 3.0F)
                        .texOffs(24, 64)
                        .addBox(-5.0F, -3.25F, -4.5F, 2.5F, 3.5F, 4.0F)
                        .mirror().texOffs(24, 64)
                        .addBox(2.5F, -3.25F, -4.5F, 2.5F, 3.5F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 10.0F, -7.5F, 0.2F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(0, 14)
                        .addBox(-3.5F, 0.0F, -8.0F, 7.0F, 2.5F, 6.0F)
                        .texOffs(49, 64)
                        .addBox(-2.5F, 1.0F, -8.75F, 1.0F, 3.0F, 1.0F)
                        .texOffs(53, 64)
                        .addBox(1.5F, 1.0F, -8.75F, 1.0F, 3.0F, 1.0F),
                PartPose.offset(0.0F, 1.0F, -0.5F)
        );
        PartDefinition leftLobe = head.addOrReplaceChild(
                "left_scent_lobe",
                CubeListBuilder.create()
                        .texOffs(57, 64)
                        .addBox(-0.5F, -1.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(63, 64)
                        .addBox(-0.25F, 3.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(3.0F, -2.5F, -4.0F, 0.2F, 0.0F, -0.25F)
        );
        leftLobe.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(71, 64)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F)
                        .texOffs(75, 64)
                        .addBox(-0.5F, 3.0F, -2.0F, 1.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(1.0F, 5.0F, 0.0F, 0.0F, 0.0F, -0.32F)
        );
        PartDefinition rightLobe = head.addOrReplaceChild(
                "right_scent_lobe",
                CubeListBuilder.create().mirror()
                        .texOffs(57, 64)
                        .addBox(-1.5F, -1.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(63, 64)
                        .addBox(-1.75F, 3.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(-3.0F, -2.5F, -4.0F, 0.2F, 0.0F, 0.25F)
        );
        rightLobe.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().mirror()
                        .texOffs(71, 64)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F)
                        .texOffs(75, 64)
                        .addBox(-0.5F, 3.0F, -2.0F, 1.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(-1.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.32F)
        );

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-5.0F, -8.0F, -6.0F, 10.0F, 15.0F, 9.0F)
                        .texOffs(0, 73)
                        .addBox(-1.0F, -9.5F, -5.0F, 2.0F, 3.0F, 11.0F)
                        .texOffs(26, 73)
                        .addBox(-6.0F, -6.5F, -3.5F, 2.5F, 6.0F, 7.0F)
                        .mirror().texOffs(26, 73)
                        .addBox(3.5F, -6.5F, -3.5F, 2.5F, 6.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 11.0F, 1.0F, 1.35F, 0.0F, 0.0F)
        );
        body.addOrReplaceChild(
                "spine",
                CubeListBuilder.create()
                        .texOffs(61, 73)
                        .addBox(-1.0F, -10.5F, -5.0F, 2.0F, 3.0F, 11.0F)
                        .texOffs(87, 73)
                        .addBox(-0.5F, -12.5F, -3.5F, 1.0F, 2.0F, 2.0F)
                        .texOffs(93, 73)
                        .addBox(-0.5F, -12.0F, 1.0F, 1.0F, 2.0F, 2.0F)
                        .texOffs(99, 73)
                        .addBox(-0.5F, -11.5F, 4.5F, 1.0F, 1.5F, 2.0F),
                PartPose.ZERO
        );
        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(105, 73)
                        .addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(0, 88)
                        .addBox(-1.75F, -1.75F, 4.0F, 3.5F, 3.5F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 2.0F, -0.5F, 0.0F, 0.0F)
        );
        tail.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(14, 88)
                        .addBox(-0.65F, -0.65F, 0.0F, 1.3F, 1.3F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.28F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(24, 48).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F)
                        .texOffs(29, 88).addBox(-2.0F, 7.5F, -2.75F, 4.0F, 1.5F, 4.0F),
                PartPose.offset(-3.5F, 15.0F, 5.0F)
        );
        root.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(24, 48).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F)
                        .texOffs(29, 88).addBox(-2.0F, 7.5F, -2.75F, 4.0F, 1.5F, 4.0F),
                PartPose.offset(3.5F, 15.0F, 5.0F)
        );
        root.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(0, 48).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F)
                        .texOffs(72, 88).addBox(-2.0F, 3.0F, -2.0F, 2.0F, 5.0F, 4.0F)
                        .texOffs(84, 88).addBox(-2.0F, 9.5F, -3.0F, 4.0F, 1.5F, 4.5F),
                PartPose.offset(-3.5F, 13.0F, -5.0F)
        );
        root.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 48).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F)
                        .texOffs(72, 88).addBox(0.0F, 3.0F, -2.0F, 2.0F, 5.0F, 4.0F)
                        .texOffs(84, 88).addBox(-2.0F, 9.5F, -3.0F, 4.0F, 1.5F, 4.5F),
                PartPose.offset(3.5F, 13.0F, -5.0F)
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(
            Woundscent entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.modelRoot.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float movement = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        boolean tracking = entity.isAggressive();
        boolean scentPause = tracking && movement < 0.08F;
        float sniffRate = tracking ? (scentPause ? 0.72F : 0.45F) : 0.2F;
        float sniff = Mth.sin(ageInTicks * sniffRate);
        float breath = Mth.sin(ageInTicks * 0.1F);
        float attack = Mth.sin(Mth.sqrt(this.attackTime) * Mth.PI);
        float gait = Mth.sin(limbSwing * 0.6662F);

        this.head.xRot += tracking ? 0.16F + sniff * 0.075F : sniff * 0.03F;
        this.head.xRot -= attack * 0.2F;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.45F
                + (scentPause ? Mth.sin(ageInTicks * 0.12F) * 0.42F : 0.0F);
        this.head.zRot = gait * movement * 0.045F + sniff * (scentPause ? 0.025F : 0.01F);
        this.jaw.xRot = 0.08F + (tracking ? Mth.abs(sniff) * 0.1F : 0.0F) + attack * 0.5F;

        float flare = scentPause ? 0.2F : tracking ? 0.12F : 0.06F;
        this.leftScentLobe.zRot = -0.25F + sniff * flare;
        this.rightScentLobe.zRot = -this.leftScentLobe.zRot;
        this.leftScentLobe.xRot = 0.2F + Mth.abs(sniff) * (scentPause ? 0.12F : 0.05F);
        this.rightScentLobe.xRot = this.leftScentLobe.xRot;
        this.leftScentTip.zRot = -0.32F - sniff * flare * 0.8F;
        this.rightScentTip.zRot = -this.leftScentTip.zRot;

        this.body.y = 11.0F + Mth.abs(gait) * movement * 0.45F + breath * 0.06F;
        this.body.zRot = Mth.sin(limbSwing * 0.35F) * movement * 0.065F;
        this.body.xRot = 1.35F + (tracking ? 0.05F : 0.0F);
        this.body.xScale = 1.0F + breath * 0.018F;
        this.spine.zRot = -this.body.zRot * 0.65F;
        this.spine.yScale = 1.0F + breath * 0.025F;

        this.tail.xRot = -0.5F + Mth.sin(limbSwing * 0.45F) * movement * 0.12F
                - (tracking ? 0.12F : 0.0F);
        this.tail.yRot = Mth.sin(ageInTicks * 0.19F + limbSwing * 0.25F) * (tracking ? 0.13F : 0.08F);
        this.tailTip.xRot = 0.28F + Mth.sin(ageInTicks * 0.23F) * 0.09F;
        this.tailTip.yRot = -this.tail.yRot * 1.35F;

        this.rightFrontLeg.zRot = -0.04F;
        this.leftFrontLeg.zRot = 0.04F;
        this.rightHindLeg.zRot = 0.04F;
        this.leftHindLeg.zRot = -0.04F;
    }
}
