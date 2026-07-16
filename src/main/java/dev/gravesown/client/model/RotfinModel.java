package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.Rotfin;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** A low, plated Gloamwater predator with a readable bilateral body plan. */
public final class RotfinModel extends HierarchicalModel<Rotfin> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("rotfin"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftGill;
    private final ModelPart rightGill;
    private final ModelPart dorsalRidge;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart tailBlade;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart leftFinTip;
    private final ModelPart rightFinTip;
    private final ModelPart leftFeeler;
    private final ModelPart rightFeeler;
    private final ModelPart leftFeelerTip;
    private final ModelPart rightFeelerTip;

    public RotfinModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leftGill = this.head.getChild("left_gill");
        this.rightGill = this.head.getChild("right_gill");
        this.dorsalRidge = this.body.getChild("dorsal_ridge");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
        this.tailBlade = this.tailTip.getChild("blade");
        this.leftFin = this.body.getChild("left_fin");
        this.rightFin = this.body.getChild("right_fin");
        this.leftFinTip = this.leftFin.getChild("tip");
        this.rightFinTip = this.rightFin.getChild("tip");
        this.leftFeeler = this.head.getChild("left_feeler");
        this.rightFeeler = this.head.getChild("right_feeler");
        this.leftFeelerTip = this.leftFeeler.getChild("tip");
        this.rightFeelerTip = this.rightFeeler.getChild("tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -3.0F, -4.5F, 10.0F, 6.0F, 9.0F)
                        .texOffs(0, 16).addBox(-4.0F, 2.0F, -3.5F, 8.0F, 2.0F, 7.0F)
                        .texOffs(32, 16).addBox(-3.0F, -4.5F, -2.5F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );

        PartDefinition head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(40, 0).addBox(-4.5F, -2.5F, -4.5F, 9.0F, 5.0F, 6.0F)
                        .texOffs(72, 0).addBox(-3.5F, -1.5F, -7.5F, 7.0F, 3.0F, 4.0F)
                        .texOffs(96, 0).addBox(-3.5F, -3.25F, -4.75F, 7.0F, 1.0F, 3.0F),
                PartPose.offset(0.0F, -0.25F, -3.75F)
        );
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create()
                        .texOffs(72, 10).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 2.0F, 4.0F)
                        .texOffs(96, 10).addBox(-2.75F, 1.0F, -4.75F, 1.0F, 2.0F, 2.0F)
                        .mirror().texOffs(96, 10).addBox(1.75F, 1.0F, -4.75F, 1.0F, 2.0F, 2.0F)
                        .texOffs(104, 10).addBox(-0.5F, 1.2F, -4.5F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 1.5F, -3.0F, 0.05F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "left_gill",
                CubeListBuilder.create().texOffs(110, 10).addBox(0.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(4.25F, 0.25F, -0.25F, 0.0F, 0.0F, 0.1F)
        );
        head.addOrReplaceChild(
                "right_gill",
                CubeListBuilder.create().mirror().texOffs(110, 10).addBox(-1.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(-4.25F, 0.25F, -0.25F, 0.0F, 0.0F, -0.1F)
        );
        head.addOrReplaceChild(
                "left_sensor",
                CubeListBuilder.create().texOffs(122, 10).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(2.2F, -1.5F, -4.6F)
        );
        head.addOrReplaceChild(
                "right_sensor",
                CubeListBuilder.create().mirror().texOffs(122, 10).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(-2.2F, -1.5F, -4.6F)
        );

        PartDefinition leftFeeler = head.addOrReplaceChild(
                "left_feeler",
                CubeListBuilder.create().texOffs(96, 30).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 5.0F),
                PartPose.offsetAndRotation(3.0F, -0.75F, -5.25F, 0.12F, 0.3F, -0.08F)
        );
        leftFeeler.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().texOffs(110, 30).addBox(0.0F, -0.45F, -0.45F, 1.0F, 0.9F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.5F, 0.08F, 0.16F, 0.0F)
        );
        PartDefinition rightFeeler = head.addOrReplaceChild(
                "right_feeler",
                CubeListBuilder.create().mirror().texOffs(96, 30).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 5.0F),
                PartPose.offsetAndRotation(-3.0F, -0.75F, -5.25F, 0.12F, -0.3F, 0.08F)
        );
        rightFeeler.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().mirror().texOffs(110, 30).addBox(-1.0F, -0.45F, -0.45F, 1.0F, 0.9F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.5F, 0.08F, -0.16F, 0.0F)
        );

        body.addOrReplaceChild(
                "left_flank_plate",
                CubeListBuilder.create().texOffs(54, 44).addBox(0.0F, -2.0F, -3.0F, 1.0F, 4.0F, 6.0F),
                PartPose.offset(4.75F, -0.25F, 0.5F)
        );
        body.addOrReplaceChild(
                "right_flank_plate",
                CubeListBuilder.create().mirror().texOffs(54, 44).addBox(-1.0F, -2.0F, -3.0F, 1.0F, 4.0F, 6.0F),
                PartPose.offset(-4.75F, -0.25F, 0.5F)
        );

        PartDefinition dorsal = body.addOrReplaceChild(
                "dorsal_ridge",
                CubeListBuilder.create()
                        .texOffs(0, 30).addBox(-1.0F, -4.0F, -2.5F, 2.0F, 4.0F, 5.0F)
                        .texOffs(16, 30).addBox(-0.5F, -6.0F, -0.75F, 1.0F, 3.0F, 4.0F)
                        .texOffs(28, 30).addBox(-0.5F, -4.5F, 3.0F, 1.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, -3.0F, -0.25F)
        );

        PartDefinition leftFin = body.addOrReplaceChild(
                "left_fin",
                CubeListBuilder.create().texOffs(40, 30).addBox(0.0F, -0.5F, -1.0F, 5.0F, 1.0F, 5.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(4.75F, 1.25F, -0.5F, 0.0F, 0.0F, 0.3F)
        );
        leftFin.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().texOffs(62, 30).addBox(0.0F, -0.45F, -0.75F, 4.0F, 0.9F, 4.0F,
                        new CubeDeformation(-0.08F)),
                PartPose.offsetAndRotation(4.5F, 0.0F, 0.4F, 0.0F, 0.0F, 0.12F)
        );
        PartDefinition rightFin = body.addOrReplaceChild(
                "right_fin",
                CubeListBuilder.create().mirror().texOffs(40, 30).addBox(-5.0F, -0.5F, -1.0F, 5.0F, 1.0F, 5.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(-4.75F, 1.25F, -0.5F, 0.0F, 0.0F, -0.3F)
        );
        rightFin.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().mirror().texOffs(62, 30).addBox(-4.0F, -0.45F, -0.75F, 4.0F, 0.9F, 4.0F,
                        new CubeDeformation(-0.08F)),
                PartPose.offsetAndRotation(-4.5F, 0.0F, 0.4F, 0.0F, 0.0F, -0.12F)
        );
        body.addOrReplaceChild(
                "left_pelvic_fin",
                CubeListBuilder.create().texOffs(80, 30).addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 4.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(1.5F, 2.75F, 1.0F, -0.18F, 0.0F, 0.15F)
        );
        body.addOrReplaceChild(
                "right_pelvic_fin",
                CubeListBuilder.create().mirror().texOffs(80, 30).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 1.0F, 4.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(-1.5F, 2.75F, 1.0F, -0.18F, 0.0F, -0.15F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 44).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 4.0F)
        );
        PartDefinition tailTip = tail.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().texOffs(22, 44).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F),
                PartPose.offset(0.0F, 0.0F, 5.5F)
        );
        tailTip.addOrReplaceChild(
                "blade",
                CubeListBuilder.create().texOffs(40, 44).addBox(-0.5F, -4.5F, 0.0F, 1.0F, 9.0F, 5.0F),
                PartPose.offset(0.0F, 0.0F, 4.0F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(
            Rotfin entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        float water = entity.isInWater() ? 1.0F : 0.3F;
        float speed = Mth.clamp(limbSwingAmount * 2.2F, 0.1F, 1.0F);
        float phase = ageInTicks * (0.1F + speed * 0.15F);
        float wave = Mth.sin(phase);
        float breath = Mth.sin(ageInTicks * 0.085F);
        float attack = Mth.sin(Mth.sqrt(this.attackTime) * Mth.PI);
        float alert = entity.isAggressive() ? 1.0F : 0.0F;

        this.body.yRot = wave * 0.04F * water;
        this.body.zRot = Mth.sin(ageInTicks * 0.07F) * 0.02F * water;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.18F - wave * 0.045F * water;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD * 0.12F - alert * 0.04F;
        this.jaw.xRot = 0.05F + alert * 0.07F + attack * 0.38F + Math.max(0.0F, breath) * 0.025F;
        this.leftGill.xScale = 1.0F + breath * 0.022F;
        this.rightGill.xScale = this.leftGill.xScale;
        this.dorsalRidge.xRot = -alert * 0.035F + breath * 0.01F;

        this.tail.yRot = wave * 0.32F * water;
        this.tailTip.yRot = -wave * 0.48F * water;
        this.tailBlade.yRot = wave * 0.12F * water;
        this.leftFin.zRot = 0.3F + wave * 0.05F;
        this.rightFin.zRot = -0.3F - wave * 0.05F;
        this.leftFinTip.zRot = 0.12F - wave * 0.075F;
        this.rightFinTip.zRot = -this.leftFinTip.zRot;

        float feelerWave = Mth.sin(ageInTicks * 0.075F + limbSwing * 0.15F);
        this.leftFeeler.yRot = 0.3F + feelerWave * 0.055F;
        this.rightFeeler.yRot = -this.leftFeeler.yRot;
        this.leftFeelerTip.yRot = 0.16F - feelerWave * 0.07F;
        this.rightFeelerTip.yRot = -this.leftFeelerTip.yRot;
    }
}
