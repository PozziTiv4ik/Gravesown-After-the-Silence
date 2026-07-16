package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.NativeFauna;
import dev.gravesown.entity.NativeFaunaSpecies;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Species-owned native fauna geometry.
 *
 * <p>The original expansion put fourteen animals on one quadruped mesh and only
 * toggled ears, horns or wings. That made technically different entities read as
 * palette swaps. Every species now bakes its own proportions and silhouette while
 * retaining a small common part vocabulary for stable animation code.</p>
 */
public final class NativeFaunaModel extends HierarchicalModel<NativeFauna> {
    private static final float DEG_TO_RAD = Mth.PI / 180.0F;

    private final NativeFaunaSpecies species;
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart leftFeature;
    private final ModelPart rightFeature;
    private final ModelPart backFeature;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public NativeFaunaModel(ModelPart root, NativeFaunaSpecies species) {
        this.species = species;
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
        this.leftFeature = this.head.getChild("left_feature");
        this.rightFeature = this.head.getChild("right_feature");
        this.backFeature = this.body.getChild("back_feature");
        this.leftWing = this.body.getChild("left_wing");
        this.rightWing = this.body.getChild("right_wing");
    }

    public static ModelLayerLocation layer(NativeFaunaSpecies species) {
        return new ModelLayerLocation(Gravesown.id(species.id()), "main");
    }

    public static LayerDefinition createBodyLayer(NativeFaunaSpecies species) {
        return switch (species) {
            case ASH_HOPPER -> createAshHopperLayer();
            case GRAVEWING -> createGravewingLayer();
            case ROOTBACK -> createRootbackLayer();
            case BARK_MARTEN -> createBarkMartenLayer();
            case CRAG_RAM -> createCragRamLayer();
            case RIFT_PUMA -> createRiftPumaLayer();
            case MIRE_TOAD -> createMireToadLayer();
            case REED_LYNX -> createReedLynxLayer();
            case EMBER_FOX -> createEmberFoxLayer();
            case CINDER_FOWL -> createCinderFowlLayer();
            case PALLID_HART -> createPallidHartLayer();
            case MOSSBOAR -> createMossboarLayer();
            case AMBER_JAY -> createAmberJayLayer();
            case SUNHORN -> createSunhornLayer();
        };
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(
            NativeFauna entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        float movement = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float walk = Mth.cos(limbSwing * 0.6662F);
        float opposite = Mth.cos(limbSwing * 0.6662F + Mth.PI);
        float breath = Mth.sin(ageInTicks * 0.11F);

        this.head.yRot = Mth.clamp(netHeadYaw * DEG_TO_RAD, -0.75F, 0.75F);
        this.head.xRot = Mth.clamp(headPitch * DEG_TO_RAD, -0.55F, 0.65F);
        this.rightHindLeg.xRot = walk * 1.15F * movement;
        this.leftFrontLeg.xRot = walk * 1.15F * movement;
        this.leftHindLeg.xRot = opposite * 1.15F * movement;
        this.rightFrontLeg.xRot = opposite * 1.15F * movement;
        this.body.y += breath * 0.04F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.15F + limbSwing * 0.22F) * 0.13F;
        this.tailTip.yRot = -this.tail.yRot * 1.25F;

        switch (this.species) {
            case ASH_HOPPER -> animateAshHopper(limbSwing, movement, ageInTicks);
            case GRAVEWING -> animateBird(limbSwing, movement, ageInTicks, entity.onGround(), 0.78F);
            case ROOTBACK -> animateRootback(limbSwing, movement, ageInTicks);
            case BARK_MARTEN -> animateMarten(limbSwing, movement, ageInTicks);
            case CRAG_RAM -> animateRam(limbSwing, movement, ageInTicks);
            case RIFT_PUMA -> animateCat(limbSwing, movement, ageInTicks, 1.0F);
            case MIRE_TOAD -> animateToad(limbSwing, movement, ageInTicks);
            case REED_LYNX -> animateCat(limbSwing, movement, ageInTicks, 0.72F);
            case EMBER_FOX -> animateFox(limbSwing, movement, ageInTicks);
            case CINDER_FOWL -> animateBird(limbSwing, movement, ageInTicks, entity.onGround(), 0.52F);
            case PALLID_HART -> animateUngulate(limbSwing, movement, ageInTicks, 0.76F);
            case MOSSBOAR -> animateBoar(limbSwing, movement, ageInTicks);
            case AMBER_JAY -> animateBird(limbSwing, movement, ageInTicks, entity.onGround(), 0.92F);
            case SUNHORN -> animateUngulate(limbSwing, movement, ageInTicks, 0.95F);
        }
    }

    private void animateAshHopper(float limbSwing, float movement, float ageInTicks) {
        float leap = Mth.abs(Mth.sin(limbSwing * 0.6662F)) * movement;
        this.body.y -= leap * 1.35F;
        this.body.xRot = -0.08F + leap * 0.16F;
        this.head.y -= leap * 0.75F;
        this.rightHindLeg.xRot *= 1.65F;
        this.leftHindLeg.xRot *= 1.65F;
        this.rightFrontLeg.xRot *= 0.58F;
        this.leftFrontLeg.xRot *= 0.58F;
        float ear = Mth.sin(ageInTicks * 0.31F) * 0.07F;
        this.leftFeature.zRot = 0.16F + ear;
        this.rightFeature.zRot = -0.16F - ear;
        this.tail.xRot = -0.42F - leap * 0.2F;
    }

    private void animateBird(
            float limbSwing,
            float movement,
            float ageInTicks,
            boolean onGround,
            float flightAmplitude
    ) {
        float flight = onGround ? 0.0F : 1.0F;
        float flap = Mth.sin(ageInTicks * (onGround ? 0.22F : 0.72F));
        this.leftWing.zRot += flap * (0.10F + flight * flightAmplitude);
        this.rightWing.zRot -= flap * (0.10F + flight * flightAmplitude);
        this.body.y += Mth.sin(ageInTicks * 0.18F) * flight * 0.18F;
        this.body.xRot = flight * -0.16F;
        this.head.xRot += Mth.sin(ageInTicks * 0.24F) * 0.035F;
        this.rightHindLeg.xRot *= 0.55F;
        this.leftHindLeg.xRot *= 0.55F;
        this.rightFrontLeg.xRot = this.leftFrontLeg.xRot = flight * 0.75F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.18F) * 0.08F;
    }

    private void animateRootback(float limbSwing, float movement, float ageInTicks) {
        this.rightHindLeg.xRot *= 0.62F;
        this.leftHindLeg.xRot *= 0.62F;
        this.rightFrontLeg.xRot *= 0.62F;
        this.leftFrontLeg.xRot *= 0.62F;
        this.body.zRot = Mth.sin(limbSwing * 0.333F) * movement * 0.055F;
        this.backFeature.yScale = 1.0F + Mth.sin(ageInTicks * 0.085F) * 0.015F;
        this.head.xRot += 0.12F + Mth.sin(ageInTicks * 0.12F) * 0.025F;
        this.leftFeature.zRot = 0.33F;
        this.rightFeature.zRot = -0.33F;
        this.tail.xRot = -0.18F;
    }

    private void animateMarten(float limbSwing, float movement, float ageInTicks) {
        float bound = Mth.abs(Mth.sin(limbSwing * 0.6662F)) * movement;
        this.body.y -= bound * 0.38F;
        this.body.xRot = -bound * 0.08F;
        this.head.zRot = -Mth.sin(limbSwing * 0.6662F) * movement * 0.05F;
        this.tail.xRot = -0.28F + bound * 0.16F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.21F + limbSwing * 0.35F) * 0.28F;
        this.tailTip.yRot = -this.tail.yRot * 1.4F;
        twitchEars(ageInTicks, 0.22F);
    }

    private void animateRam(float limbSwing, float movement, float ageInTicks) {
        this.body.zRot = Mth.sin(limbSwing * 0.333F) * movement * 0.035F;
        this.head.xRot += 0.08F + Mth.abs(Mth.sin(limbSwing * 0.333F)) * movement * 0.09F;
        this.backFeature.yScale = 1.0F + Mth.sin(ageInTicks * 0.1F) * 0.012F;
        this.tail.xRot = -0.55F;
    }

    private void animateCat(float limbSwing, float movement, float ageInTicks, float tailWeight) {
        float stride = Mth.sin(limbSwing * 0.6662F) * movement;
        this.body.y -= Mth.abs(stride) * 0.25F;
        this.body.zRot = stride * 0.035F;
        this.head.zRot = -stride * 0.025F;
        this.backFeature.xRot = -0.08F + Mth.abs(stride) * 0.06F;
        this.tail.xRot = -0.20F + movement * 0.13F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.18F + limbSwing * 0.42F) * 0.31F * tailWeight;
        this.tailTip.yRot = -this.tail.yRot * 1.45F;
        twitchEars(ageInTicks, 0.16F);
    }

    private void animateToad(float limbSwing, float movement, float ageInTicks) {
        float hop = Mth.abs(Mth.sin(limbSwing * 0.6662F)) * movement;
        this.body.y -= hop * 1.1F;
        this.head.y -= hop * 0.75F;
        this.rightHindLeg.xRot *= 1.8F;
        this.leftHindLeg.xRot *= 1.8F;
        this.rightFrontLeg.xRot *= 0.45F;
        this.leftFrontLeg.xRot *= 0.45F;
        this.leftFeature.yScale = 1.0F + Mth.sin(ageInTicks * 0.13F) * 0.035F;
        this.rightFeature.yScale = this.leftFeature.yScale;
        this.jaw.xRot = 0.06F + Math.max(0.0F, Mth.sin(ageInTicks * 0.1F)) * 0.04F;
    }

    private void animateFox(float limbSwing, float movement, float ageInTicks) {
        float stride = Mth.sin(limbSwing * 0.6662F) * movement;
        this.body.y -= Mth.abs(stride) * 0.3F;
        this.head.zRot = -stride * 0.045F;
        this.tail.xRot = -0.44F + movement * 0.12F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.16F + limbSwing * 0.38F) * 0.34F;
        this.tailTip.yRot = -this.tail.yRot * 1.35F;
        this.tailTip.zRot = Mth.sin(ageInTicks * 0.12F) * 0.06F;
        twitchEars(ageInTicks, 0.24F);
    }

    private void animateUngulate(float limbSwing, float movement, float ageInTicks, float headWeight) {
        float stride = Mth.sin(limbSwing * 0.6662F) * movement;
        this.body.y -= Mth.abs(stride) * 0.22F;
        this.body.zRot = stride * 0.025F;
        this.head.zRot = -stride * 0.025F * headWeight;
        this.leftFeature.zRot += Mth.sin(ageInTicks * 0.08F) * 0.012F;
        this.rightFeature.zRot -= Mth.sin(ageInTicks * 0.08F) * 0.012F;
        this.tail.xRot = -0.62F + Mth.sin(ageInTicks * 0.22F) * 0.08F;
    }

    private void animateBoar(float limbSwing, float movement, float ageInTicks) {
        float stride = Mth.sin(limbSwing * 0.6662F) * movement;
        this.rightHindLeg.xRot *= 0.78F;
        this.leftHindLeg.xRot *= 0.78F;
        this.rightFrontLeg.xRot *= 0.78F;
        this.leftFrontLeg.xRot *= 0.78F;
        this.body.zRot = stride * 0.035F;
        this.head.xRot += 0.16F + Mth.sin(ageInTicks * 0.09F) * 0.035F;
        this.jaw.xRot = 0.03F + Math.max(0.0F, Mth.sin(ageInTicks * 0.1F)) * 0.025F;
        this.backFeature.zRot = -stride * 0.025F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.31F) * 0.12F;
    }

    private void twitchEars(float ageInTicks, float base) {
        float twitch = Mth.sin(ageInTicks * 0.37F) * 0.045F;
        this.leftFeature.zRot = base + twitch;
        this.rightFeature.zRot = -base - twitch;
    }

    private static LayerDefinition createAshHopperLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.5F, -2.5F, -3.5F, 5.0F, 5.0F, 5.0F)
                        .texOffs(0, 12).addBox(-1.75F, -0.75F, -5.0F, 3.5F, 2.0F, 2.5F),
                PartPose.offset(0.0F, 13.0F, -4.5F)
        );
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.65F, -5.5F, -0.6F, 1.3F, 6.0F, 1.2F),
                PartPose.offsetAndRotation(1.7F, -2.0F, -0.8F, -0.18F, 0.0F, 0.16F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.65F, -5.5F, -0.6F, 1.3F, 6.0F, 1.2F),
                PartPose.offsetAndRotation(-1.7F, -2.0F, -0.8F, -0.18F, 0.0F, -0.16F));
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 8.0F)
                        .texOffs(30, 32).addBox(-2.25F, -4.0F, -2.5F, 4.5F, 1.5F, 5.0F),
                PartPose.offset(0.0F, 16.0F, 0.5F)
        );
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 3.5F, -0.42F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 3.0F),
                PartPose.offset(0.0F, 0.0F, 4.5F));
        addLeg(root, "right_hind_leg", 0,
                CubeListBuilder.create().texOffs(0, 72)
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F)
                        .texOffs(0, 84).addBox(-1.5F, 3.5F, -2.5F, 3.0F, 3.5F, 4.0F),
                PartPose.offset(-2.0F, 17.0F, 2.5F));
        addLeg(root, "left_hind_leg", 16,
                CubeListBuilder.create().mirror().texOffs(16, 72)
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F)
                        .texOffs(16, 84).addBox(-1.5F, 3.5F, -2.5F, 3.0F, 3.5F, 4.0F),
                PartPose.offset(2.0F, 17.0F, 2.5F));
        addSmallFrontLegs(root, 18.0F, -2.5F, 1.6F, 5.5F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createGravewingLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.5F, -2.5F, -3.0F, 5.0F, 5.0F, 5.0F)
                        .texOffs(0, 12).addBox(-1.25F, -0.75F, -4.5F, 2.5F, 1.5F, 2.0F),
                PartPose.offset(0.0F, 12.0F, -3.0F)
        );
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(0.0F, -0.5F, -0.5F, 3.5F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(2.0F, -1.2F, -1.0F, 0.0F, 0.2F, -0.12F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-3.5F, -0.5F, -0.5F, 3.5F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-2.0F, -1.2F, -1.0F, 0.0F, -0.2F, 0.12F));
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.0F, -4.0F, -3.5F, 6.0F, 8.0F, 7.0F)
                        .texOffs(28, 32).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 2.0F, 4.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(64, 48)
                .addBox(0.0F, -0.6F, -3.0F, 10.0F, 1.2F, 9.0F)
                .texOffs(64, 60).addBox(5.0F, -0.45F, 5.0F, 7.0F, 0.9F, 5.0F),
                PartPose.offsetAndRotation(2.5F, -1.0F, -1.0F, 0.0F, 0.0F, 0.22F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().mirror().texOffs(64, 48)
                .addBox(-10.0F, -0.6F, -3.0F, 10.0F, 1.2F, 9.0F)
                .texOffs(64, 60).addBox(-12.0F, -0.45F, 5.0F, 7.0F, 0.9F, 5.0F),
                PartPose.offsetAndRotation(-2.5F, -1.0F, -1.0F, 0.0F, 0.0F, -0.22F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 4.0F),
                PartPose.offset(0.0F, 1.5F, 3.0F),
                CubeListBuilder.create().texOffs(80, 72)
                        .addBox(-3.0F, -0.75F, 0.0F, 2.5F, 1.5F, 4.0F)
                        .mirror().addBox(0.5F, -0.75F, 0.0F, 2.5F, 1.5F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 3.5F));
        addBirdLegs(root, 18.0F, 4.5F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createRootbackLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.75F, -2.0F, -3.5F, 5.5F, 4.0F, 5.0F)
                        .texOffs(0, 12).addBox(-2.0F, -1.0F, -5.0F, 4.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 16.0F, -7.0F)
        );
        addRootHorns(head, 0.34F, 4.0F);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-5.5F, -3.0F, -5.5F, 11.0F, 6.0F, 11.0F)
                        .texOffs(46, 32).addBox(-4.5F, 2.0F, -4.0F, 9.0F, 2.0F, 8.0F),
                PartPose.offset(0.0F, 17.0F, 0.0F)
        );
        body.addOrReplaceChild("back_feature", CubeListBuilder.create().texOffs(64, 32)
                .addBox(-6.5F, -4.0F, -5.5F, 13.0F, 4.0F, 11.0F)
                .texOffs(64, 20).addBox(-4.5F, -6.0F, -3.5F, 9.0F, 2.0F, 7.0F),
                PartPose.ZERO);
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.25F, -1.0F, 0.0F, 2.5F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 5.0F, 0.18F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.0F, -0.75F, 0.0F, 2.0F, 1.5F, 3.5F),
                PartPose.offset(0.0F, 0.0F, 3.5F));
        addHeavyLegs(root, 18.0F, 3.8F, 4.0F, 5.0F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createBarkMartenLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.25F, -2.5F, -3.0F, 4.5F, 5.0F, 4.5F)
                        .texOffs(0, 12).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 2.0F, 2.5F),
                PartPose.offset(0.0F, 14.5F, -6.0F)
        );
        addPointedEars(head, 0.25F, 3.0F);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-2.75F, -2.5F, -6.0F, 5.5F, 5.0F, 12.0F)
                        .texOffs(36, 32).addBox(-2.25F, -3.25F, -3.0F, 4.5F, 1.0F, 6.0F),
                PartPose.offset(0.0F, 16.5F, 0.5F)
        );
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -0.25F, 5.5F, -0.28F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, 0.12F, 0.0F, 0.0F));
        addShortPredatorLegs(root, 18.0F, 3.8F, 2.0F, 5.5F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createCragRamLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.25F, -3.5F, -4.0F, 6.5F, 7.0F, 6.0F)
                        .texOffs(0, 16).addBox(-2.25F, -1.0F, -6.0F, 4.5F, 3.0F, 3.0F),
                PartPose.offset(0.0F, 12.0F, -6.5F)
        );
        addCurledHorns(head);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.75F, -5.0F, -5.5F, 9.5F, 10.0F, 11.0F)
                        .texOffs(42, 32).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 2.0F, 7.0F),
                PartPose.offset(0.0F, 14.5F, 0.0F)
        );
        body.addOrReplaceChild("back_feature", CubeListBuilder.create().texOffs(64, 32)
                .addBox(-4.0F, -3.5F, -3.0F, 8.0F, 5.0F, 7.0F),
                PartPose.offset(0.0F, -5.5F, -0.5F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.55F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.75F, -1.75F, 0.0F, 3.5F, 3.5F, 2.5F),
                PartPose.offset(0.0F, 0.0F, 2.5F));
        addHoofLegs(root, 17.0F, 3.25F, 4.0F, 7.0F, 2.4F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createRiftPumaLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.25F, -3.0F, -3.75F, 6.5F, 6.0F, 5.5F)
                        .texOffs(0, 14).addBox(-2.25F, -1.0F, -5.75F, 4.5F, 2.5F, 2.5F),
                PartPose.offset(0.0F, 13.0F, -7.5F)
        );
        addRoundedEars(head, 0.16F);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.25F, -3.5F, -7.0F, 8.5F, 7.0F, 14.0F)
                        .texOffs(46, 32).addBox(-3.5F, -4.5F, -3.5F, 7.0F, 2.0F, 7.0F),
                PartPose.offset(0.0F, 15.5F, 0.0F)
        );
        body.addOrReplaceChild("back_feature", CubeListBuilder.create().texOffs(64, 32)
                .addBox(-4.75F, -2.0F, -2.5F, 2.0F, 4.0F, 5.0F)
                .mirror().addBox(2.75F, -2.0F, -2.5F, 2.0F, 4.0F, 5.0F),
                PartPose.offset(0.0F, -3.5F, -2.0F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 6.5F, -0.2F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.1F, -1.1F, 0.0F, 2.2F, 2.2F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 7.5F));
        addCatLegs(root, 17.0F, 3.25F, 5.0F, 7.0F, 3.5F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createMireToadLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -2.0F, -3.5F, 10.0F, 4.0F, 6.0F)
                        .texOffs(0, 12).addBox(-3.5F, 0.5F, -4.5F, 7.0F, 1.5F, 3.0F),
                PartPose.offset(0.0F, 17.5F, -4.0F)
        );
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 20)
                .addBox(-3.75F, -0.25F, -3.0F, 7.5F, 1.5F, 3.0F),
                PartPose.offset(0.0F, 1.25F, -3.0F));
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-1.25F, -1.25F, -1.25F, 2.5F, 2.5F, 2.5F),
                PartPose.offset(3.25F, -2.0F, -1.5F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-1.25F, -1.25F, -1.25F, 2.5F, 2.5F, 2.5F),
                PartPose.offset(-3.25F, -2.0F, -1.5F));
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-5.5F, -2.25F, -4.0F, 11.0F, 4.5F, 8.0F)
                        .texOffs(40, 32).addBox(-4.0F, -3.0F, -2.5F, 8.0F, 1.0F, 5.0F),
                PartPose.offset(0.0F, 19.0F, 1.0F)
        );
        addSplayedToadLegs(root);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createReedLynxLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -3.0F, -3.25F, 6.0F, 6.0F, 5.0F)
                        .texOffs(0, 14).addBox(-1.75F, -0.75F, -5.0F, 3.5F, 2.0F, 2.5F),
                PartPose.offset(0.0F, 10.5F, -5.5F)
        );
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-1.0F, -3.5F, -0.75F, 2.0F, 4.0F, 1.5F)
                .texOffs(72, 0).addBox(-0.4F, -5.5F, -0.4F, 0.8F, 2.5F, 0.8F),
                PartPose.offsetAndRotation(2.0F, -2.5F, -0.5F, 0.0F, 0.0F, 0.14F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-1.0F, -3.5F, -0.75F, 2.0F, 4.0F, 1.5F)
                .texOffs(72, 0).addBox(-0.4F, -5.5F, -0.4F, 0.8F, 2.5F, 0.8F),
                PartPose.offsetAndRotation(-2.0F, -2.5F, -0.5F, 0.0F, 0.0F, -0.14F));
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 6.0F, 10.0F)
                        .texOffs(36, 32).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 1.5F, 5.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F)
        );
        body.addOrReplaceChild("back_feature", CubeListBuilder.create().texOffs(64, 32)
                .addBox(-4.0F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F)
                .mirror().addBox(3.0F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, -2.5F, -1.5F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 4.5F, -0.25F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.offset(0.0F, 0.0F, 3.5F));
        addCatLegs(root, 15.0F, 2.8F, 3.5F, 9.0F, 2.8F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createEmberFoxLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.75F, -3.0F, -3.0F, 5.5F, 6.0F, 5.0F)
                        .texOffs(0, 14).addBox(-1.75F, -1.0F, -5.75F, 3.5F, 2.5F, 3.0F),
                PartPose.offset(0.0F, 13.0F, -5.75F)
        );
        addPointedEars(head, 0.32F, 4.25F);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 10.0F)
                        .texOffs(34, 32).addBox(-2.5F, -4.0F, -2.5F, 5.0F, 1.5F, 5.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 4.5F, -0.44F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(84, 72).addBox(-2.75F, -2.75F, 0.0F, 5.5F, 5.5F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, 0.18F, 0.0F, 0.0F));
        addShortPredatorLegs(root, 17.0F, 3.5F, 2.3F, 6.5F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createCinderFowlLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.5F, -3.0F, -2.75F, 5.0F, 6.0F, 5.0F)
                        .texOffs(0, 14).addBox(-1.5F, -0.75F, -5.0F, 3.0F, 1.5F, 2.5F),
                PartPose.offset(0.0F, 8.5F, -2.5F)
        );
        head.addOrReplaceChild("back_feature", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.5F, -4.0F, -1.5F, 1.0F, 4.0F, 3.0F),
                PartPose.offsetAndRotation(0.5F, -3.0F, 0.0F, 0.0F, 0.0F, 0.12F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().texOffs(72, 0)
                .addBox(-0.5F, -3.0F, -1.25F, 1.0F, 3.0F, 2.5F),
                PartPose.offsetAndRotation(-0.75F, -2.5F, 0.5F, 0.0F, 0.0F, -0.12F));
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.5F, -4.5F, -3.5F, 7.0F, 9.0F, 7.0F)
                        .texOffs(30, 32).addBox(-2.5F, 3.0F, -2.5F, 5.0F, 2.0F, 5.0F),
                PartPose.offset(0.0F, 14.5F, 0.0F)
        );
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(64, 48)
                .addBox(0.0F, -1.0F, -2.5F, 5.0F, 2.0F, 7.0F),
                PartPose.offsetAndRotation(3.0F, -1.0F, -0.5F, 0.0F, 0.0F, 0.28F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().mirror().texOffs(64, 48)
                .addBox(-5.0F, -1.0F, -2.5F, 5.0F, 2.0F, 7.0F),
                PartPose.offsetAndRotation(-3.0F, -1.0F, -0.5F, 0.0F, 0.0F, -0.28F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, 3.0F, 0.42F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(84, 72).addBox(-3.0F, -0.75F, 0.0F, 6.0F, 1.5F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.5F, 0.25F, 0.0F, 0.0F));
        addBirdLegs(root, 18.0F, 6.0F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createPallidHartLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.75F, -3.5F, -3.5F, 5.5F, 7.0F, 5.0F)
                        .texOffs(0, 16).addBox(-1.75F, -1.0F, -5.5F, 3.5F, 2.5F, 2.5F),
                PartPose.offset(0.0F, 7.5F, -6.5F)
        );
        addBranchingAntlers(head);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.75F, -3.5F, -6.0F, 7.5F, 7.0F, 12.0F)
                        .texOffs(40, 32).addBox(-3.0F, -4.5F, -3.0F, 6.0F, 1.5F, 6.0F),
                PartPose.offset(0.0F, 12.5F, 0.0F)
        );
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 3.5F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 5.5F, -0.62F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.75F, -1.5F, 0.0F, 3.5F, 3.0F, 2.5F),
                PartPose.offset(0.0F, 0.0F, 3.0F));
        addHoofLegs(root, 14.0F, 2.75F, 4.5F, 10.0F, 2.0F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createMossboarLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 6.0F)
                        .texOffs(0, 16).addBox(-3.25F, -1.25F, -6.5F, 6.5F, 3.5F, 3.0F),
                PartPose.offset(0.0F, 14.5F, -7.0F)
        );
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-2.75F, -0.5F, -2.75F, 5.5F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 1.5F, -4.0F));
        addBoarTusks(head);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-5.5F, -4.5F, -6.0F, 11.0F, 9.0F, 12.0F)
                        .texOffs(48, 32).addBox(-4.5F, -5.5F, -3.5F, 9.0F, 2.0F, 7.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );
        body.addOrReplaceChild("back_feature", CubeListBuilder.create().texOffs(64, 32)
                .addBox(-0.75F, -4.5F, -5.5F, 1.5F, 5.0F, 11.0F),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 5.5F, -0.15F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.25F, -1.25F, 0.0F, 2.5F, 2.5F, 2.5F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, 0.0F, 0.6F));
        addHeavyLegs(root, 18.0F, 4.0F, 4.2F, 5.5F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createAmberJayLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.25F, -2.5F, -2.75F, 4.5F, 5.0F, 4.5F)
                        .texOffs(0, 12).addBox(-1.0F, -0.6F, -5.5F, 2.0F, 1.2F, 3.0F),
                PartPose.offset(0.0F, 11.0F, -4.0F)
        );
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.4F, -3.0F, -0.6F, 0.8F, 3.0F, 1.2F),
                PartPose.offsetAndRotation(0.8F, -2.0F, 0.0F, 0.0F, 0.0F, 0.16F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.4F, -3.0F, -0.6F, 0.8F, 3.0F, 1.2F),
                PartPose.offsetAndRotation(-0.8F, -2.0F, 0.0F, 0.0F, 0.0F, -0.16F));
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-2.5F, -2.75F, -4.0F, 5.0F, 5.5F, 8.0F)
                        .texOffs(28, 32).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 14.5F, 0.0F)
        );
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(64, 48)
                .addBox(0.0F, -0.5F, -2.0F, 7.0F, 1.0F, 8.0F),
                PartPose.offsetAndRotation(2.0F, -0.75F, -1.0F, 0.0F, 0.0F, 0.25F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().mirror().texOffs(64, 48)
                .addBox(-7.0F, -0.5F, -2.0F, 7.0F, 1.0F, 8.0F),
                PartPose.offsetAndRotation(-2.0F, -0.75F, -1.0F, 0.0F, 0.0F, -0.25F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.75F, -1.0F, 0.0F, 3.5F, 2.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 3.5F, 0.12F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(84, 72).addBox(-2.25F, -0.75F, 0.0F, 4.5F, 1.5F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 6.5F));
        addBirdLegs(root, 17.0F, 5.0F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createSunhornLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = addHead(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.5F, -3.5F, -3.5F, 5.0F, 7.0F, 5.0F)
                        .texOffs(0, 16).addBox(-1.5F, -1.0F, -5.75F, 3.0F, 2.25F, 2.75F),
                PartPose.offset(0.0F, 7.0F, -6.0F)
        );
        addSweptHorns(head);
        PartDefinition body = addBody(
                root,
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-3.5F, -3.25F, -5.5F, 7.0F, 6.5F, 11.0F)
                        .texOffs(38, 32).addBox(-2.75F, -4.25F, -2.5F, 5.5F, 1.25F, 5.5F),
                PartPose.offset(0.0F, 12.0F, 0.0F)
        );
        body.addOrReplaceChild("back_feature", CubeListBuilder.create().texOffs(64, 32)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 2.5F, -4.0F, -0.16F, 0.0F, 0.0F));
        replaceTail(body,
                CubeListBuilder.create().texOffs(64, 72).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -0.75F, 5.0F, -0.62F, 0.0F, 0.0F),
                CubeListBuilder.create().texOffs(80, 72).addBox(-1.4F, -1.4F, 0.0F, 2.8F, 2.8F, 2.5F),
                PartPose.offset(0.0F, 0.0F, 3.5F));
        addHoofLegs(root, 13.5F, 2.5F, 4.0F, 10.5F, 1.8F);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static PartDefinition addHead(PartDefinition root, CubeListBuilder cubes, PartPose pose) {
        PartDefinition head = root.addOrReplaceChild("head", cubes, pose);
        head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("left_feature", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("right_feature", CubeListBuilder.create(), PartPose.ZERO);
        return head;
    }

    private static PartDefinition addBody(PartDefinition root, CubeListBuilder cubes, PartPose pose) {
        PartDefinition body = root.addOrReplaceChild("body", cubes, pose);
        body.addOrReplaceChild("back_feature", CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.ZERO);
        tail.addOrReplaceChild("tip", CubeListBuilder.create(), PartPose.ZERO);
        return body;
    }

    private static void replaceTail(
            PartDefinition body,
            CubeListBuilder tailCubes,
            PartPose tailPose,
            CubeListBuilder tipCubes,
            PartPose tipPose
    ) {
        PartDefinition tail = body.addOrReplaceChild("tail", tailCubes, tailPose);
        tail.addOrReplaceChild("tip", tipCubes, tipPose);
    }

    private static void addLeg(
            PartDefinition root,
            String name,
            int ignoredTextureOffset,
            CubeListBuilder cubes,
            PartPose pose
    ) {
        root.addOrReplaceChild(name, cubes, pose);
    }

    private static void addSmallFrontLegs(
            PartDefinition root,
            float y,
            float z,
            float x,
            float length
    ) {
        addLeg(root, "right_front_leg", 32, CubeListBuilder.create().texOffs(32, 72)
                .addBox(-0.9F, 0.0F, -0.9F, 1.8F, length, 1.8F)
                .texOffs(32, 82).addBox(-1.2F, length - 1.0F, -1.8F, 2.4F, 1.2F, 2.4F),
                PartPose.offset(-x, y, z));
        addLeg(root, "left_front_leg", 48, CubeListBuilder.create().mirror().texOffs(48, 72)
                .addBox(-0.9F, 0.0F, -0.9F, 1.8F, length, 1.8F)
                .texOffs(48, 82).addBox(-1.2F, length - 1.0F, -1.8F, 2.4F, 1.2F, 2.4F),
                PartPose.offset(x, y, z));
    }

    private static void addBirdLegs(PartDefinition root, float y, float length) {
        addLeg(root, "right_hind_leg", 0, CubeListBuilder.create().texOffs(0, 72)
                .addBox(-0.6F, 0.0F, -0.6F, 1.2F, length, 1.2F)
                .texOffs(0, 82).addBox(-1.25F, length - 0.5F, -2.0F, 2.5F, 0.8F, 2.5F),
                PartPose.offset(-1.4F, y, 1.0F));
        addLeg(root, "left_hind_leg", 16, CubeListBuilder.create().mirror().texOffs(16, 72)
                .addBox(-0.6F, 0.0F, -0.6F, 1.2F, length, 1.2F)
                .texOffs(16, 82).addBox(-1.25F, length - 0.5F, -2.0F, 2.5F, 0.8F, 2.5F),
                PartPose.offset(1.4F, y, 1.0F));
        addLeg(root, "right_front_leg", 32, CubeListBuilder.create(), PartPose.ZERO);
        addLeg(root, "left_front_leg", 48, CubeListBuilder.create(), PartPose.ZERO);
    }

    private static void addHeavyLegs(
            PartDefinition root,
            float y,
            float x,
            float z,
            float length
    ) {
        addFourLegs(root, y, x, z, length, 2.8F, 3.6F);
    }

    private static void addShortPredatorLegs(
            PartDefinition root,
            float y,
            float z,
            float x,
            float length
    ) {
        addFourLegs(root, y, x, z, length, 1.8F, 3.0F);
    }

    private static void addCatLegs(
            PartDefinition root,
            float y,
            float x,
            float z,
            float length,
            float paw
    ) {
        addFourLegs(root, y, x, z, length, 2.0F, paw);
    }

    private static void addHoofLegs(
            PartDefinition root,
            float y,
            float x,
            float z,
            float length,
            float width
    ) {
        addFourLegs(root, y, x, z, length, width, width + 0.5F);
    }

    private static void addFourLegs(
            PartDefinition root,
            float y,
            float x,
            float z,
            float length,
            float width,
            float foot
    ) {
        addStandardLeg(root, "right_hind_leg", 0, -x, y, z, length, width, foot, false);
        addStandardLeg(root, "left_hind_leg", 16, x, y, z, length, width, foot, true);
        addStandardLeg(root, "right_front_leg", 32, -x, y, -z, length, width, foot, false);
        addStandardLeg(root, "left_front_leg", 48, x, y, -z, length, width, foot, true);
    }

    private static void addStandardLeg(
            PartDefinition root,
            String name,
            int u,
            float x,
            float y,
            float z,
            float length,
            float width,
            float foot,
            boolean mirror
    ) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) {
            builder = builder.mirror();
        }
        root.addOrReplaceChild(name, builder.texOffs(u, 72)
                .addBox(-width / 2.0F, 0.0F, -width / 2.0F, width, length, width)
                .texOffs(u, 88).addBox(-foot / 2.0F, length - 1.0F, -foot * 0.7F, foot, 1.5F, foot),
                PartPose.offset(x, y, z));
    }

    private static void addSplayedToadLegs(PartDefinition root) {
        addLeg(root, "right_hind_leg", 0, CubeListBuilder.create().texOffs(0, 72)
                .addBox(-4.0F, -1.0F, -1.5F, 5.0F, 2.5F, 3.0F)
                .texOffs(0, 80).addBox(-6.0F, 0.5F, -2.0F, 4.0F, 1.5F, 4.0F),
                PartPose.offset(-4.5F, 21.0F, 2.5F));
        addLeg(root, "left_hind_leg", 16, CubeListBuilder.create().mirror().texOffs(16, 72)
                .addBox(-1.0F, -1.0F, -1.5F, 5.0F, 2.5F, 3.0F)
                .texOffs(16, 80).addBox(2.0F, 0.5F, -2.0F, 4.0F, 1.5F, 4.0F),
                PartPose.offset(4.5F, 21.0F, 2.5F));
        addLeg(root, "right_front_leg", 32, CubeListBuilder.create().texOffs(32, 72)
                .addBox(-3.5F, -0.75F, -1.0F, 4.0F, 2.0F, 2.0F)
                .texOffs(32, 79).addBox(-5.0F, 0.5F, -2.0F, 3.5F, 1.25F, 3.5F),
                PartPose.offset(-4.0F, 21.0F, -2.5F));
        addLeg(root, "left_front_leg", 48, CubeListBuilder.create().mirror().texOffs(48, 72)
                .addBox(-0.5F, -0.75F, -1.0F, 4.0F, 2.0F, 2.0F)
                .texOffs(48, 79).addBox(1.5F, 0.5F, -2.0F, 3.5F, 1.25F, 3.5F),
                PartPose.offset(4.0F, 21.0F, -2.5F));
    }

    private static void addPointedEars(PartDefinition head, float angle, float height) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-1.0F, -height, -0.75F, 2.0F, height, 1.5F),
                PartPose.offsetAndRotation(1.8F, -2.25F, -0.5F, -0.08F, 0.0F, angle));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-1.0F, -height, -0.75F, 2.0F, height, 1.5F),
                PartPose.offsetAndRotation(-1.8F, -2.25F, -0.5F, -0.08F, 0.0F, -angle));
    }

    private static void addRoundedEars(PartDefinition head, float angle) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-1.25F, -2.25F, -0.75F, 2.5F, 2.5F, 1.5F),
                PartPose.offsetAndRotation(2.0F, -2.25F, -0.25F, 0.0F, 0.0F, angle));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-1.25F, -2.25F, -0.75F, 2.5F, 2.5F, 1.5F),
                PartPose.offsetAndRotation(-2.0F, -2.25F, -0.25F, 0.0F, 0.0F, -angle));
    }

    private static void addRootHorns(PartDefinition head, float angle, float length) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.75F, -length, -0.75F, 1.5F, length, 1.5F)
                .texOffs(72, 0).addBox(-0.5F, -length - 2.0F, -0.5F, 1.0F, 2.5F, 1.0F),
                PartPose.offsetAndRotation(1.75F, -1.5F, -0.25F, -0.45F, 0.0F, angle));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.75F, -length, -0.75F, 1.5F, length, 1.5F)
                .texOffs(72, 0).addBox(-0.5F, -length - 2.0F, -0.5F, 1.0F, 2.5F, 1.0F),
                PartPose.offsetAndRotation(-1.75F, -1.5F, -0.25F, -0.45F, 0.0F, -angle));
    }

    private static void addCurledHorns(PartDefinition head) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.75F, -4.0F, -0.75F, 1.5F, 5.0F, 1.5F)
                .texOffs(72, 0).addBox(0.0F, -4.5F, -0.75F, 3.5F, 1.5F, 1.5F)
                .texOffs(86, 0).addBox(2.25F, -3.5F, -0.75F, 1.5F, 4.0F, 1.5F),
                PartPose.offsetAndRotation(2.25F, -2.25F, -0.5F, -0.24F, 0.0F, 0.2F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.75F, -4.0F, -0.75F, 1.5F, 5.0F, 1.5F)
                .texOffs(72, 0).addBox(-3.5F, -4.5F, -0.75F, 3.5F, 1.5F, 1.5F)
                .texOffs(86, 0).addBox(-3.75F, -3.5F, -0.75F, 1.5F, 4.0F, 1.5F),
                PartPose.offsetAndRotation(-2.25F, -2.25F, -0.5F, -0.24F, 0.0F, -0.2F));
    }

    private static void addBranchingAntlers(PartDefinition head) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.6F, -7.0F, -0.6F, 1.2F, 7.5F, 1.2F)
                .texOffs(72, 0).addBox(0.0F, -5.75F, -0.6F, 4.0F, 1.2F, 1.2F)
                .texOffs(88, 0).addBox(2.8F, -8.5F, -0.6F, 1.2F, 3.5F, 1.2F)
                .texOffs(96, 0).addBox(0.0F, -3.25F, -0.6F, 3.0F, 1.2F, 1.2F)
                .texOffs(108, 0).addBox(1.9F, -5.0F, -0.6F, 1.2F, 2.5F, 1.2F),
                PartPose.offsetAndRotation(1.75F, -3.0F, -0.5F, -0.08F, 0.0F, 0.08F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.6F, -7.0F, -0.6F, 1.2F, 7.5F, 1.2F)
                .texOffs(72, 0).addBox(-4.0F, -5.75F, -0.6F, 4.0F, 1.2F, 1.2F)
                .texOffs(88, 0).addBox(-4.0F, -8.5F, -0.6F, 1.2F, 3.5F, 1.2F)
                .texOffs(96, 0).addBox(-3.0F, -3.25F, -0.6F, 3.0F, 1.2F, 1.2F)
                .texOffs(108, 0).addBox(-3.1F, -5.0F, -0.6F, 1.2F, 2.5F, 1.2F),
                PartPose.offsetAndRotation(-1.75F, -3.0F, -0.5F, -0.08F, 0.0F, -0.08F));
    }

    private static void addSweptHorns(PartDefinition head) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.55F, -8.0F, -0.55F, 1.1F, 8.5F, 1.1F)
                .texOffs(72, 0).addBox(-0.45F, -12.0F, 1.5F, 0.9F, 5.0F, 0.9F),
                PartPose.offsetAndRotation(1.5F, -3.0F, 0.0F, -0.35F, 0.0F, 0.12F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.55F, -8.0F, -0.55F, 1.1F, 8.5F, 1.1F)
                .texOffs(72, 0).addBox(-0.45F, -12.0F, 1.5F, 0.9F, 5.0F, 0.9F),
                PartPose.offsetAndRotation(-1.5F, -3.0F, 0.0F, -0.35F, 0.0F, -0.12F));
    }

    private static void addBoarTusks(PartDefinition head) {
        head.addOrReplaceChild("left_feature", CubeListBuilder.create().texOffs(64, 0)
                .addBox(-0.55F, -0.55F, -4.0F, 1.1F, 1.1F, 4.0F)
                .texOffs(72, 0).addBox(-0.45F, -2.0F, -5.0F, 0.9F, 2.0F, 1.5F),
                PartPose.offsetAndRotation(2.5F, 1.0F, -4.5F, -0.18F, -0.1F, 0.0F));
        head.addOrReplaceChild("right_feature", CubeListBuilder.create().mirror().texOffs(64, 0)
                .addBox(-0.55F, -0.55F, -4.0F, 1.1F, 1.1F, 4.0F)
                .texOffs(72, 0).addBox(-0.45F, -2.0F, -5.0F, 0.9F, 2.0F, 1.5F),
                PartPose.offsetAndRotation(-2.5F, 1.0F, -4.5F, -0.18F, 0.1F, 0.0F));
    }
}
