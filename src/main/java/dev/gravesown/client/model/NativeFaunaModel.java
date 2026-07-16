package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.NativeFauna;
import dev.gravesown.entity.NativeFaunaSpecies;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * One stable bilateral rig with species-owned optional anatomy.  The shared rig
 * keeps animation and dedicated-client registration maintainable while each
 * animal still receives a readable silhouette and its own texture atlas.
 */
public final class NativeFaunaModel extends QuadrupedModel<NativeFauna> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("native_fauna"), "main");

    private final ModelPart modelRoot;
    private final ModelPart snout;
    private final ModelPart jaw;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart leftHorn;
    private final ModelPart rightHorn;
    private final ModelPart leftAntler;
    private final ModelPart rightAntler;
    private final ModelPart leftTusk;
    private final ModelPart rightTusk;
    private final ModelPart beak;
    private final ModelPart crest;
    private final ModelPart shell;
    private final ModelPart hump;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart tail;
    private final ModelPart tailTip;

    public NativeFaunaModel(ModelPart root) {
        super(root, false, 6.0F, 2.0F, 1.8F, 1.8F, 20);
        this.modelRoot = root;
        this.snout = this.head.getChild("snout");
        this.jaw = this.head.getChild("jaw");
        this.leftEar = this.head.getChild("left_ear");
        this.rightEar = this.head.getChild("right_ear");
        this.leftHorn = this.head.getChild("left_horn");
        this.rightHorn = this.head.getChild("right_horn");
        this.leftAntler = this.head.getChild("left_antler");
        this.rightAntler = this.head.getChild("right_antler");
        this.leftTusk = this.head.getChild("left_tusk");
        this.rightTusk = this.head.getChild("right_tusk");
        this.beak = this.head.getChild("beak");
        this.crest = this.head.getChild("crest");
        this.shell = this.body.getChild("shell");
        this.hump = this.body.getChild("hump");
        this.leftWing = this.body.getChild("left_wing");
        this.rightWing = this.body.getChild("right_wing");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -4.0F, -5.0F, 6.0F, 6.0F, 6.0F)
                        .texOffs(26, 0).addBox(-2.25F, -4.75F, -3.5F, 4.5F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 11.0F, -6.5F)
        );
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(0, 14)
                .addBox(-2.25F, -1.5F, -4.0F, 4.5F, 3.0F, 4.0F), PartPose.offset(0.0F, -0.25F, -4.0F));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(20, 14)
                .addBox(-2.0F, -0.25F, -3.5F, 4.0F, 1.5F, 3.5F), PartPose.offset(0.0F, 1.25F, -4.0F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(38, 0)
                .addBox(-0.75F, -4.5F, -0.75F, 1.5F, 5.0F, 1.5F), PartPose.offsetAndRotation(2.25F, -3.5F, -1.5F, -0.18F, 0.0F, 0.18F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().mirror().texOffs(38, 0)
                .addBox(-0.75F, -4.5F, -0.75F, 1.5F, 5.0F, 1.5F), PartPose.offsetAndRotation(-2.25F, -3.5F, -1.5F, -0.18F, 0.0F, -0.18F));
        head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(46, 0)
                .addBox(-0.6F, -5.5F, -0.6F, 1.2F, 6.0F, 1.2F), PartPose.offsetAndRotation(2.0F, -3.0F, -1.0F, -0.35F, 0.0F, 0.25F));
        head.addOrReplaceChild("right_horn", CubeListBuilder.create().mirror().texOffs(46, 0)
                .addBox(-0.6F, -5.5F, -0.6F, 1.2F, 6.0F, 1.2F), PartPose.offsetAndRotation(-2.0F, -3.0F, -1.0F, -0.35F, 0.0F, -0.25F));
        head.addOrReplaceChild("left_antler", CubeListBuilder.create().texOffs(52, 0)
                .addBox(-0.5F, -6.5F, -0.5F, 1.0F, 7.0F, 1.0F)
                .addBox(0.0F, -5.5F, -0.5F, 3.0F, 1.0F, 1.0F)
                .addBox(1.75F, -7.5F, -0.5F, 1.0F, 3.0F, 1.0F), PartPose.offset(2.0F, -3.25F, -0.75F));
        head.addOrReplaceChild("right_antler", CubeListBuilder.create().mirror().texOffs(52, 0)
                .addBox(-0.5F, -6.5F, -0.5F, 1.0F, 7.0F, 1.0F)
                .addBox(-3.0F, -5.5F, -0.5F, 3.0F, 1.0F, 1.0F)
                .addBox(-2.75F, -7.5F, -0.5F, 1.0F, 3.0F, 1.0F), PartPose.offset(-2.0F, -3.25F, -0.75F));
        head.addOrReplaceChild("left_tusk", CubeListBuilder.create().texOffs(76, 0)
                .addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F), PartPose.offsetAndRotation(1.75F, 0.75F, -4.5F, -0.2F, -0.12F, 0.0F));
        head.addOrReplaceChild("right_tusk", CubeListBuilder.create().mirror().texOffs(76, 0)
                .addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F), PartPose.offsetAndRotation(-1.75F, 0.75F, -4.5F, -0.2F, 0.12F, 0.0F));
        head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(86, 0)
                .addBox(-1.5F, -1.0F, -4.0F, 3.0F, 2.0F, 4.0F), PartPose.offset(0.0F, -0.5F, -4.0F));
        head.addOrReplaceChild("crest", CubeListBuilder.create().texOffs(102, 0)
                .addBox(-0.5F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F), PartPose.offset(0.0F, -4.0F, -1.0F));

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-4.0F, -6.0F, -5.5F, 8.0F, 12.0F, 9.0F)
                        .texOffs(36, 24).addBox(-3.25F, -7.0F, -4.5F, 6.5F, 2.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 13.0F, 1.0F, Mth.HALF_PI, 0.0F, 0.0F)
        );
        body.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(66, 20)
                .addBox(-5.0F, -6.5F, -4.5F, 10.0F, 8.0F, 9.0F), PartPose.ZERO);
        body.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(66, 40)
                .addBox(-3.5F, -4.0F, -3.5F, 7.0F, 5.0F, 7.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 50)
                .addBox(0.0F, -0.75F, -2.0F, 8.0F, 1.5F, 9.0F), PartPose.offsetAndRotation(3.5F, -1.0F, -1.5F, 0.0F, 0.0F, 0.28F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().mirror().texOffs(0, 50)
                .addBox(-8.0F, -0.75F, -2.0F, 8.0F, 1.5F, 9.0F), PartPose.offsetAndRotation(-3.5F, -1.0F, -1.5F, 0.0F, 0.0F, -0.28F));
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(40, 52)
                .addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 7.0F), PartPose.offsetAndRotation(0.0F, 4.0F, 2.0F, -0.45F, 0.0F, 0.0F));
        tail.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(60, 54)
                .addBox(-1.75F, -1.75F, 0.0F, 3.5F, 3.5F, 5.0F), PartPose.offset(0.0F, 0.0F, 6.0F));

        addLeg(root, "right_hind_leg", -2.75F, 15.0F, 5.0F, false, 0);
        addLeg(root, "left_hind_leg", 2.75F, 15.0F, 5.0F, true, 0);
        addLeg(root, "right_front_leg", -2.75F, 15.0F, -4.0F, false, 16);
        addLeg(root, "left_front_leg", 2.75F, 15.0F, -4.0F, true, 16);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addLeg(PartDefinition root, String name, float x, float y, float z, boolean mirror, int u) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) builder = builder.mirror();
        root.addOrReplaceChild(name, builder.texOffs(u, 66)
                .addBox(-1.25F, 0.0F, -1.25F, 2.5F, 8.0F, 2.5F)
                .texOffs(u + 10, 66).addBox(-1.75F, 7.0F, -2.25F, 3.5F, 1.5F, 4.0F), PartPose.offset(x, y, z));
    }

    @Override
    public void setupAnim(NativeFauna entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
        this.modelRoot.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        hideAnatomy();
        NativeFaunaSpecies species = entity.species();
        configureSpecies(species);

        float breath = Mth.sin(ageInTicks * 0.12F);
        float stride = Mth.sin(limbSwing * 0.6662F) * Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        this.body.y += breath * 0.06F;
        this.head.zRot = -stride * 0.035F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.18F + limbSwing * 0.35F) * 0.18F;
        this.tailTip.yRot = -this.tail.yRot * 1.35F;

        if (species.archetype() == NativeFaunaSpecies.Archetype.FLYER) {
            float flap = Mth.sin(ageInTicks * (entity.onGround() ? 0.22F : 0.75F)) * (entity.onGround() ? 0.12F : 0.72F);
            this.leftWing.zRot = 0.28F + flap;
            this.rightWing.zRot = -0.28F - flap;
            this.body.xRot = Mth.HALF_PI - 0.08F;
        }
    }

    private void hideAnatomy() {
        this.snout.visible = true;
        this.jaw.visible = true;
        this.leftEar.visible = false;
        this.rightEar.visible = false;
        this.leftHorn.visible = false;
        this.rightHorn.visible = false;
        this.leftAntler.visible = false;
        this.rightAntler.visible = false;
        this.leftTusk.visible = false;
        this.rightTusk.visible = false;
        this.beak.visible = false;
        this.crest.visible = false;
        this.shell.visible = false;
        this.hump.visible = false;
        this.leftWing.visible = false;
        this.rightWing.visible = false;
        this.tail.visible = false;
    }

    private void configureSpecies(NativeFaunaSpecies species) {
        switch (species) {
            case ASH_HOPPER -> {
                ears(); tail();
                this.body.xScale = 0.76F; this.body.zScale = 0.88F;
                this.rightHindLeg.xScale = this.leftHindLeg.xScale = 1.35F;
            }
            case GRAVEWING, CINDER_FOWL, AMBER_JAY -> {
                wings(); this.beak.visible = true; this.snout.visible = false; this.jaw.visible = false;
                this.crest.visible = species != NativeFaunaSpecies.GRAVEWING;
                this.body.xScale = 0.76F; this.body.zScale = 0.78F;
            }
            case ROOTBACK -> { this.shell.visible = true; horns(); tail(); this.head.xScale = 0.9F; }
            case BARK_MARTEN, RIFT_PUMA, REED_LYNX, EMBER_FOX -> {
                ears(); tail();
                this.body.xScale = species == NativeFaunaSpecies.RIFT_PUMA ? 1.08F : 0.82F;
                this.body.zScale = 1.12F;
            }
            case CRAG_RAM -> { horns(); this.hump.visible = true; tail(); }
            case MIRE_TOAD -> {
                this.head.xScale = 1.35F; this.head.yScale = 0.72F;
                this.body.xScale = 1.28F; this.body.yScale = 0.62F;
                this.rightFrontLeg.yScale = this.leftFrontLeg.yScale = 0.48F;
                this.rightHindLeg.yScale = this.leftHindLeg.yScale = 0.48F;
            }
            case PALLID_HART -> { antlers(); ears(); tail(); this.body.zScale = 1.12F; }
            case MOSSBOAR -> { tusks(); this.hump.visible = true; tail(); this.head.yScale = 0.88F; }
            case SUNHORN -> { horns(); ears(); tail(); this.leftHorn.yScale = this.rightHorn.yScale = 1.35F; }
        }
    }

    private void ears() { this.leftEar.visible = true; this.rightEar.visible = true; }
    private void horns() { this.leftHorn.visible = true; this.rightHorn.visible = true; }
    private void antlers() { this.leftAntler.visible = true; this.rightAntler.visible = true; }
    private void tusks() { this.leftTusk.visible = true; this.rightTusk.visible = true; }
    private void wings() { this.leftWing.visible = true; this.rightWing.visible = true; }
    private void tail() { this.tail.visible = true; }
}
