package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.BuriedRemnant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** A symmetric soil-and-bone sentinel, deliberately distinct from vanilla zombies. */
public final class BuriedRemnantModel extends HumanoidModel<BuriedRemnant> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("buried_remnant"), "main");

    private final ModelPart modelRoot;
    private final ModelPart jaw;
    private final ModelPart faceCage;
    private final ModelPart spine;
    private final ModelPart sternum;
    private final ModelPart rightRibGuard;
    private final ModelPart leftRibGuard;
    private final ModelPart pelvis;
    private final ModelPart rightShoulderCairn;
    private final ModelPart leftShoulderCairn;
    private final ModelPart backRoots;
    private final ModelPart rightForearmClod;
    private final ModelPart leftForearmClod;

    public BuriedRemnantModel(ModelPart root) {
        super(root);
        this.modelRoot = root;
        this.jaw = this.head.getChild("jaw");
        this.faceCage = this.head.getChild("face_cage");
        this.spine = this.body.getChild("spine");
        this.sternum = this.body.getChild("sternum");
        this.rightRibGuard = this.body.getChild("right_rib_guard");
        this.leftRibGuard = this.body.getChild("left_rib_guard");
        this.pelvis = this.body.getChild("pelvis");
        this.rightShoulderCairn = this.body.getChild("right_shoulder_cairn");
        this.leftShoulderCairn = this.body.getChild("left_shoulder_cairn");
        this.backRoots = this.body.getChild("back_roots");
        this.rightForearmClod = this.rightArm.getChild("forearm_clod");
        this.leftForearmClod = this.leftArm.getChild("forearm_clod");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(32, 0).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 3.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -0.75F, 0.08F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -1.0F, -4.5F, 6.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.06F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "face_cage",
                CubeListBuilder.create()
                        .texOffs(64, 64).addBox(-3.75F, -5.5F, -4.75F, 1.0F, 5.0F, 1.0F)
                        .mirror().texOffs(64, 64).addBox(2.75F, -5.5F, -4.75F, 1.0F, 5.0F, 1.0F)
                        .texOffs(70, 64).addBox(-3.0F, -6.0F, -4.8F, 6.0F, 1.0F, 1.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(24, 16).addBox(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F)
                        .texOffs(92, 16).addBox(-4.0F, 2.0F, -3.25F, 8.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.08F, 0.0F, 0.0F)
        );
        body.addOrReplaceChild(
                "spine",
                CubeListBuilder.create()
                        .texOffs(52, 16).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 12.0F, 2.0F)
                        .texOffs(34, 52).addBox(-3.0F, 7.0F, 2.0F, 6.0F, 3.0F, 3.0F),
                PartPose.ZERO
        );
        body.addOrReplaceChild(
                "sternum",
                CubeListBuilder.create().texOffs(86, 64).addBox(-1.0F, 1.25F, -3.75F, 2.0F, 9.0F, 1.0F),
                PartPose.ZERO
        );
        body.addOrReplaceChild(
                "right_rib_guard",
                CubeListBuilder.create().texOffs(92, 64).addBox(-4.75F, 2.0F, -3.5F, 1.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.08F)
        );
        body.addOrReplaceChild(
                "left_rib_guard",
                CubeListBuilder.create().mirror().texOffs(92, 64).addBox(3.75F, 2.0F, -3.5F, 1.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.08F)
        );
        body.addOrReplaceChild(
                "pelvis",
                CubeListBuilder.create().texOffs(0, 72).addBox(-4.0F, 9.5F, -2.75F, 8.0F, 3.0F, 5.5F),
                PartPose.ZERO
        );
        body.addOrReplaceChild(
                "right_shoulder_cairn",
                CubeListBuilder.create().texOffs(60, 16).addBox(-5.5F, -2.5F, -3.0F, 4.0F, 4.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.08F)
        );
        body.addOrReplaceChild(
                "left_shoulder_cairn",
                CubeListBuilder.create().mirror().texOffs(60, 16).addBox(1.5F, -2.5F, -3.0F, 4.0F, 4.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.08F)
        );
        body.addOrReplaceChild(
                "back_roots",
                CubeListBuilder.create()
                        .texOffs(52, 52).addBox(-3.5F, 6.0F, 3.0F, 1.0F, 7.0F, 1.0F)
                        .mirror().texOffs(52, 52).addBox(2.5F, 6.0F, 3.0F, 1.0F, 7.0F, 1.0F)
                        .texOffs(56, 52).addBox(-0.5F, 5.0F, 3.0F, 1.0F, 8.0F, 1.0F),
                PartPose.ZERO
        );

        PartDefinition rightArm = root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -0.08F, 0.0F, 0.11F)
        );
        rightArm.addOrReplaceChild(
                "forearm_clod",
                CubeListBuilder.create()
                        .texOffs(28, 72).addBox(-2.75F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F)
                        .texOffs(50, 72).addBox(-1.75F, 4.5F, -0.5F, 1.0F, 5.0F, 1.0F),
                PartPose.offset(-1.75F, 5.5F, 0.0F)
        );
        PartDefinition leftArm = root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 32).addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -0.08F, 0.0F, -0.11F)
        );
        leftArm.addOrReplaceChild(
                "forearm_clod",
                CubeListBuilder.create().mirror()
                        .texOffs(28, 72).addBox(-2.25F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F)
                        .texOffs(50, 72).addBox(0.75F, 4.5F, -0.5F, 1.0F, 5.0F, 1.0F),
                PartPose.offset(1.75F, 5.5F, 0.0F)
        );
        PartDefinition rightLeg = root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .texOffs(72, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(-2.1F, 12.0F, 0.0F, 0.0F, 0.0F, 0.04F)
        );
        rightLeg.addOrReplaceChild(
                "shin_guard",
                CubeListBuilder.create()
                        .texOffs(60, 72).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F)
                        .texOffs(82, 72).addBox(-2.5F, 4.0F, -4.0F, 5.0F, 3.0F, 7.0F),
                PartPose.offset(0.0F, 6.0F, 0.0F)
        );
        PartDefinition leftLeg = root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().mirror()
                        .texOffs(72, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(2.1F, 12.0F, 0.0F, 0.0F, 0.0F, -0.04F)
        );
        leftLeg.addOrReplaceChild(
                "shin_guard",
                CubeListBuilder.create().mirror()
                        .texOffs(60, 72).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F)
                        .texOffs(82, 72).addBox(-2.5F, 4.0F, -4.0F, 5.0F, 3.0F, 7.0F),
                PartPose.offset(0.0F, 6.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(
            BuriedRemnant entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.modelRoot.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        float movement = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float gait = Mth.sin(limbSwing * 0.6662F);
        float rasp = Mth.sin(ageInTicks * 0.13F);
        float attack = Mth.sin(Mth.sqrt(this.attackTime) * Mth.PI);
        this.rightArm.xRot *= 0.68F;
        this.leftArm.xRot *= 0.68F;
        this.rightLeg.xRot *= 0.72F;
        this.leftLeg.xRot *= 0.72F;
        this.body.y = Mth.abs(gait) * movement * 0.18F;
        this.body.zRot = Mth.sin(limbSwing * 0.32F) * movement * 0.055F;
        this.spine.zRot = -this.body.zRot * 0.65F;
        this.sternum.yScale = 1.0F + rasp * 0.012F;
        this.rightRibGuard.zRot = -0.08F - rasp * 0.012F;
        this.leftRibGuard.zRot = -this.rightRibGuard.zRot;
        this.pelvis.zRot = -this.body.zRot * 0.35F;
        this.jaw.xRot = 0.06F + Mth.abs(rasp) * 0.055F + attack * 0.48F;
        this.faceCage.xRot = -attack * 0.035F;
        this.rightShoulderCairn.zRot = -0.08F + rasp * 0.018F;
        this.leftShoulderCairn.zRot = -this.rightShoulderCairn.zRot;
        this.backRoots.zRot = Mth.sin(ageInTicks * 0.09F) * 0.035F;
        this.rightArm.zRot += 0.11F;
        this.leftArm.zRot -= 0.11F;
        this.rightForearmClod.xRot = Math.max(0.0F, gait) * movement * 0.08F;
        this.leftForearmClod.xRot = Math.max(0.0F, -gait) * movement * 0.08F;

        if (entity.isEmerging()) {
            float progress = Mth.clamp(entity.getEmergenceProgress(0.0F), 0.0F, 1.0F);
            float eased = progress * progress * (3.0F - 2.0F * progress);
            // The entity itself now rises from 1.8 blocks below the terrain.
            // Keep only a small settling offset in the model so collision,
            // particles and the visible body all share the same emergence.
            this.modelRoot.y = (1.0F - eased) * 3.0F;
            this.body.xRot = 0.85F * (1.0F - eased);
            this.head.xRot += 0.72F * (1.0F - eased);
            this.rightArm.xRot = -1.02F + eased * 0.7F;
            this.leftArm.xRot = this.rightArm.xRot;
            this.rightForearmClod.xRot = -0.42F * (1.0F - eased);
            this.leftForearmClod.xRot = this.rightForearmClod.xRot;
            this.rightLeg.xRot = -0.32F * (1.0F - eased);
            this.leftLeg.xRot = this.rightLeg.xRot;
            this.jaw.xRot += Mth.sin(progress * Mth.PI * 4.0F) * 0.08F;
        }
    }
}
