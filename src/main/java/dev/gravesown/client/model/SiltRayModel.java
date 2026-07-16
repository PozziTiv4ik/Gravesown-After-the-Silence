package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.SiltRay;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Broad, bilaterally symmetric bottom-swimmer rig for the Gloam Sea. */
public final class SiltRayModel extends HierarchicalModel<SiltRay> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Gravesown.id("silt_ray"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart tail;
    private final ModelPart tailTip;

    public SiltRayModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.leftFin = this.body.getChild("left_fin");
        this.rightFin = this.body.getChild("right_fin");
        this.tail = this.body.getChild("tail");
        this.tailTip = this.tail.getChild("tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -1.5F, -5.0F, 10.0F, 3.0F, 11.0F)
                .texOffs(0, 16).addBox(-3.5F, -2.5F, -3.5F, 7.0F, 1.0F, 7.0F)
                .texOffs(40, 0).addBox(-2.0F, -0.75F, -7.0F, 4.0F, 1.5F, 3.0F), PartPose.offset(0.0F, 21.0F, 0.0F));
        body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(0, 28)
                .addBox(0.0F, -0.75F, -4.0F, 9.0F, 1.5F, 10.0F), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.12F));
        body.addOrReplaceChild("right_fin", CubeListBuilder.create().mirror().texOffs(0, 28)
                .addBox(-9.0F, -0.75F, -4.0F, 9.0F, 1.5F, 10.0F), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.12F));
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 16)
                .addBox(-1.25F, -1.0F, 0.0F, 2.5F, 2.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 5.0F));
        tail.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(54, 28)
                .addBox(-0.75F, -0.75F, 0.0F, 1.5F, 1.5F, 8.0F)
                .texOffs(66, 16).addBox(-0.5F, -3.0F, 5.0F, 1.0F, 6.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 7.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() { return this.root; }

    @Override
    public void setupAnim(SiltRay entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        float water = entity.isInWater() ? 1.0F : 0.25F;
        float wave = Mth.sin(ageInTicks * 0.16F) * water;
        this.leftFin.zRot = 0.12F + wave * 0.18F;
        this.rightFin.zRot = -0.12F - wave * 0.18F;
        this.body.yRot = wave * 0.035F;
        this.tail.yRot = wave * 0.28F;
        this.tailTip.yRot = -wave * 0.46F;
    }
}
