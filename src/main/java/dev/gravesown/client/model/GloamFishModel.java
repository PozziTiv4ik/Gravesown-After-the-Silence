package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.GloamwaterFish;
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

/**
 * Shared bilateral rig for the two peaceful Gloamwater fish. The species keep
 * different proportions, but both use the same readable anatomy: skull, jaw,
 * paired gill shields, paired fins, a plated torso and a two-joint tail.
 */
public final class GloamFishModel<T extends GloamwaterFish> extends HierarchicalModel<T> {
    public static final ModelLayerLocation VEILFIN_LAYER =
            new ModelLayerLocation(Gravesown.id("veilfin"), "main");
    public static final ModelLayerLocation ROOTSKIMMER_LAYER =
            new ModelLayerLocation(Gravesown.id("rootskimmer"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftFlankPlate;
    private final ModelPart rightFlankPlate;
    private final ModelPart leftGill;
    private final ModelPart rightGill;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart leftFinTip;
    private final ModelPart rightFinTip;
    private final ModelPart dorsal;
    private final ModelPart dorsalTip;

    public GloamFishModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leftFlankPlate = this.body.getChild("left_flank_plate");
        this.rightFlankPlate = this.body.getChild("right_flank_plate");
        this.leftGill = this.head.getChild("left_gill");
        this.rightGill = this.head.getChild("right_gill");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
        this.leftFin = this.body.getChild("left_fin");
        this.rightFin = this.body.getChild("right_fin");
        this.leftFinTip = this.leftFin.getChild("tip");
        this.rightFinTip = this.rightFin.getChild("tip");
        this.dorsal = this.body.getChild("dorsal");
        this.dorsalTip = this.dorsal.getChild("tip");
    }

    public static LayerDefinition createVeilfinLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -2.5F, -4.5F, 8.0F, 5.0F, 9.0F)
                        .texOffs(0, 16).addBox(-3.0F, 1.5F, -3.5F, 6.0F, 2.0F, 7.0F)
                        .texOffs(30, 16).addBox(-2.0F, -3.5F, -2.5F, 4.0F, 1.0F, 6.0F),
                PartPose.offset(0.0F, 20.5F, 0.0F)
        );
        addSwimmingRig(body, false);
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createRootskimmerLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -2.0F, -5.5F, 10.0F, 4.0F, 11.0F)
                        .texOffs(0, 16).addBox(-4.5F, 1.25F, -4.0F, 9.0F, 1.5F, 8.0F)
                        .texOffs(36, 16).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 1.0F, 7.0F),
                PartPose.offset(0.0F, 21.0F, 0.0F)
        );
        addSwimmingRig(body, true);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addSwimmingRig(PartDefinition body, boolean rootskimmer) {
        float headZ = rootskimmer ? -4.5F : -3.75F;
        CubeListBuilder headBuilder = rootskimmer
                ? CubeListBuilder.create()
                        .texOffs(44, 0).addBox(-4.0F, -1.75F, -4.0F, 8.0F, 3.5F, 5.5F)
                        .texOffs(72, 0).addBox(-2.5F, -1.0F, -6.0F, 5.0F, 2.0F, 3.0F)
                        .texOffs(90, 0).addBox(-3.0F, -2.5F, -3.25F, 6.0F, 1.0F, 4.0F)
                : CubeListBuilder.create()
                        .texOffs(40, 0).addBox(-3.5F, -2.0F, -4.0F, 7.0F, 4.0F, 5.0F)
                        .texOffs(66, 0).addBox(-2.5F, -1.0F, -5.75F, 5.0F, 2.0F, 3.0F)
                        .texOffs(84, 0).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 1.0F, 4.0F);
        PartDefinition head = body.addOrReplaceChild("head", headBuilder, PartPose.offset(0.0F, 0.0F, headZ));
        head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(66, 8)
                        .addBox(-2.5F, -0.25F, -3.25F, 5.0F, 1.5F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 1.25F, -2.25F, 0.03F, 0.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "left_gill",
                CubeListBuilder.create().texOffs(84, 8).addBox(0.0F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(rootskimmer ? 3.75F : 3.25F, 0.0F, -0.25F, 0.0F, 0.0F, 0.08F)
        );
        head.addOrReplaceChild(
                "right_gill",
                CubeListBuilder.create().mirror().texOffs(84, 8).addBox(-1.0F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(rootskimmer ? -3.75F : -3.25F, 0.0F, -0.25F, 0.0F, 0.0F, -0.08F)
        );
        head.addOrReplaceChild(
                "left_sensor",
                CubeListBuilder.create().texOffs(94, 8).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(2.0F, -1.1F, -4.1F)
        );
        head.addOrReplaceChild(
                "right_sensor",
                CubeListBuilder.create().mirror().texOffs(94, 8).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(-2.0F, -1.1F, -4.1F)
        );
        if (rootskimmer) {
            head.addOrReplaceChild(
                    "left_barbel",
                    CubeListBuilder.create().texOffs(60, 44).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 5.0F),
                    PartPose.offsetAndRotation(1.5F, 1.0F, -5.25F, 0.15F, 0.18F, 0.0F)
            );
            head.addOrReplaceChild(
                    "right_barbel",
                    CubeListBuilder.create().mirror().texOffs(60, 44).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 5.0F),
                    PartPose.offsetAndRotation(-1.5F, 1.0F, -5.25F, 0.15F, -0.18F, 0.0F)
            );
        }

        float side = rootskimmer ? 4.75F : 3.75F;
        float finLength = rootskimmer ? 4.0F : 5.0F;
        PartDefinition leftFin = body.addOrReplaceChild(
                "left_fin",
                CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, -0.5F, -1.0F, finLength, 1.0F, 5.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(side, 0.75F, -1.0F, 0.0F, 0.0F, rootskimmer ? 0.24F : 0.38F)
        );
        leftFin.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().texOffs(22, 30).addBox(0.0F, -0.45F, -0.75F, 4.0F, 0.9F, 4.0F,
                        new CubeDeformation(-0.08F)),
                PartPose.offsetAndRotation(finLength - 0.5F, 0.0F, 0.35F, 0.0F, 0.0F, 0.12F)
        );
        PartDefinition rightFin = body.addOrReplaceChild(
                "right_fin",
                CubeListBuilder.create().mirror().texOffs(0, 30).addBox(-finLength, -0.5F, -1.0F, finLength, 1.0F, 5.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(-side, 0.75F, -1.0F, 0.0F, 0.0F, rootskimmer ? -0.24F : -0.38F)
        );
        rightFin.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().mirror().texOffs(22, 30).addBox(-4.0F, -0.45F, -0.75F, 4.0F, 0.9F, 4.0F,
                        new CubeDeformation(-0.08F)),
                PartPose.offsetAndRotation(-finLength + 0.5F, 0.0F, 0.35F, 0.0F, 0.0F, -0.12F)
        );
        body.addOrReplaceChild(
                "left_pelvic_fin",
                CubeListBuilder.create().texOffs(40, 30).addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 4.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(1.25F, 2.0F, 0.5F, -0.15F, 0.0F, 0.16F)
        );
        body.addOrReplaceChild(
                "right_pelvic_fin",
                CubeListBuilder.create().mirror().texOffs(40, 30).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 1.0F, 4.0F,
                        new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(-1.25F, 2.0F, 0.5F, -0.15F, 0.0F, -0.16F)
        );
        body.addOrReplaceChild(
                "left_flank_plate",
                CubeListBuilder.create().texOffs(72, 44).addBox(0.0F, -1.5F, -2.5F, 1.0F, 3.0F, 5.0F),
                PartPose.offset(side - 0.25F, -0.25F, 0.5F)
        );
        body.addOrReplaceChild(
                "right_flank_plate",
                CubeListBuilder.create().mirror().texOffs(72, 44).addBox(-1.0F, -1.5F, -2.5F, 1.0F, 3.0F, 5.0F),
                PartPose.offset(-side + 0.25F, -0.25F, 0.5F)
        );

        PartDefinition dorsal = body.addOrReplaceChild(
                "dorsal",
                CubeListBuilder.create().texOffs(56, 30).addBox(-0.5F, -4.0F, -2.5F, 1.0F, 4.0F, 6.0F),
                PartPose.offset(0.0F, rootskimmer ? -2.0F : -2.5F, 0.0F)
        );
        dorsal.addOrReplaceChild(
                "tip",
                CubeListBuilder.create().texOffs(72, 30).addBox(-0.45F, -4.0F, -1.75F, 0.9F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.25F, 0.08F, 0.0F, 0.0F)
        );

        float tailStart = rootskimmer ? 4.5F : 3.75F;
        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 44).addBox(-2.0F, -1.75F, 0.0F, 4.0F, 3.5F, 5.0F),
                PartPose.offset(0.0F, 0.0F, tailStart)
        );
        tail.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(20, 44).addBox(-1.5F, -1.25F, 0.0F, 3.0F, 2.5F, 5.0F)
                        .texOffs(38, 44).addBox(-0.5F, -4.0F, 3.5F, 1.0F, 8.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 4.5F)
        );
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        float water = entity.isInWater() ? 1.0F : 0.3F;
        float speed = Mth.clamp(limbSwingAmount * 2.2F, 0.08F, 1.0F);
        float phase = ageInTicks * (0.09F + speed * 0.11F);
        float wave = Mth.sin(phase);
        float breath = Mth.sin(ageInTicks * 0.075F);

        this.body.yRot = wave * 0.045F * water;
        this.body.zRot = Mth.sin(ageInTicks * 0.065F) * 0.022F * water;
        this.head.yRot = -wave * 0.055F * water;
        this.jaw.xRot = 0.03F + Math.max(0.0F, breath) * 0.035F;
        this.leftGill.xScale = 1.0F + breath * 0.02F;
        this.rightGill.xScale = this.leftGill.xScale;
        this.leftFlankPlate.zRot = 0.025F + breath * 0.008F;
        this.rightFlankPlate.zRot = -this.leftFlankPlate.zRot;

        this.tail.yRot = wave * 0.3F * water;
        this.tailTip.yRot = -wave * 0.46F * water;
        this.leftFin.zRot += wave * 0.055F;
        this.rightFin.zRot -= wave * 0.055F;
        this.leftFinTip.zRot = 0.12F - wave * 0.08F;
        this.rightFinTip.zRot = -this.leftFinTip.zRot;
        this.dorsal.xRot = breath * 0.012F;
        this.dorsalTip.xRot = 0.08F - wave * 0.025F;
    }
}
