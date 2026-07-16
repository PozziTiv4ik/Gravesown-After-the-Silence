package dev.gravesown.client.model;

import dev.gravesown.Gravesown;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * Symmetric, open-faced field armor built from a few readable layered masses.
 * Mirrored limbs share UV islands so the set cannot drift back into accidental
 * left/right noise while still remaining visibly thicker than a vanilla skin.
 */
public final class QuietskinArmorModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation HOOD_LAYER =
            new ModelLayerLocation(Gravesown.id("quietskin_hood"), "armor");
    public static final ModelLayerLocation COAT_LAYER =
            new ModelLayerLocation(Gravesown.id("quietskin_coat"), "armor");
    public static final ModelLayerLocation LEGWRAPS_LAYER =
            new ModelLayerLocation(Gravesown.id("quietskin_legwraps"), "armor");
    public static final ModelLayerLocation BOOTS_LAYER =
            new ModelLayerLocation(Gravesown.id("quietskin_boots"), "armor");

    public QuietskinArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createHoodLayer() {
        MeshDefinition mesh = emptyHumanoidMesh();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        // Crown and rear curtain protect the skull while the whole
                        // face below the brow remains open and readable.
                        .texOffs(0, 0).addBox(-5.0F, -9.2F, -5.0F, 10.0F, 2.0F, 10.0F)
                        .texOffs(40, 0).addBox(-5.0F, -7.2F, 3.2F, 10.0F, 8.0F, 2.0F)
                        .texOffs(64, 0).addBox(-5.1F, -7.2F, -4.0F, 2.0F, 7.0F, 7.0F)
                        .mirror().texOffs(64, 0).addBox(3.1F, -7.2F, -4.0F, 2.0F, 7.0F, 7.0F)
                        .texOffs(82, 0).addBox(-4.0F, -7.35F, -5.35F, 8.0F, 1.0F, 1.0F)
                        .texOffs(100, 0).addBox(-5.4F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F)
                        .mirror().texOffs(100, 0).addBox(3.4F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createCoatLayer() {
        MeshDefinition mesh = emptyHumanoidMesh();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-4.4F, 0.4F, -2.9F, 8.8F, 10.0F, 1.2F)
                        .texOffs(20, 24).addBox(-4.9F, 0.8F, -2.0F, 1.2F, 9.5F, 4.0F)
                        .mirror().texOffs(20, 24).addBox(3.7F, 0.8F, -2.0F, 1.2F, 9.5F, 4.0F)
                        .texOffs(30, 24).addBox(-4.4F, 0.4F, 1.8F, 8.8F, 10.0F, 1.2F)
                        .texOffs(50, 24).addBox(-1.0F, 1.0F, -3.25F, 2.0F, 8.5F, 1.0F)
                        .texOffs(70, 24).addBox(-4.7F, 9.2F, -2.65F, 9.4F, 2.2F, 5.3F)
                        .texOffs(90, 24).addBox(-4.6F, -0.4F, -2.5F, 9.2F, 2.2F, 5.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "right_arm",
                arm(false),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "left_arm",
                arm(true),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createLegwrapsLayer() {
        MeshDefinition mesh = emptyHumanoidMesh();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.7F, 9.3F, -2.7F, 9.4F, 3.0F, 5.4F),
                PartPose.ZERO
        );
        root.addOrReplaceChild("right_leg", legwrap(false), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", legwrap(true), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createBootsLayer() {
        MeshDefinition mesh = emptyHumanoidMesh();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("right_leg", boot(false), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", boot(true), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static CubeListBuilder arm(boolean mirror) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) {
            builder = builder.mirror();
        }
        return builder
                .texOffs(0, 48).addBox(mirror ? -1.3F : -3.7F, -3.0F, -3.0F, 5.0F, 4.2F, 6.0F)
                .texOffs(24, 48).addBox(mirror ? -1.5F : -3.5F, 5.2F, -2.7F, 5.0F, 5.7F, 5.4F)
                .texOffs(46, 48).addBox(mirror ? -1.6F : -3.6F, 2.1F, -2.75F, 5.2F, 1.2F, 5.5F)
                .texOffs(68, 48).addBox(mirror ? 2.4F : -3.4F, -0.7F, -2.4F, 1.0F, 6.0F, 4.8F);
    }

    private static CubeListBuilder legwrap(boolean mirror) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) {
            builder = builder.mirror();
        }
        return builder
                .texOffs(30, 0).addBox(mirror ? -2.3F : -2.7F, 0.0F, -2.7F, 5.0F, 7.0F, 1.2F)
                .texOffs(44, 0).addBox(mirror ? 1.5F : -2.7F, 0.5F, -2.5F, 1.2F, 7.0F, 5.0F)
                .texOffs(58, 0).addBox(mirror ? -2.3F : -2.7F, 6.0F, -3.0F, 5.0F, 3.0F, 2.0F)
                .texOffs(74, 0).addBox(mirror ? -2.3F : -2.7F, 1.2F, -2.8F, 5.0F, 1.2F, 5.6F)
                .texOffs(96, 0).addBox(mirror ? -2.3F : -2.7F, 9.2F, -2.8F, 5.0F, 1.2F, 5.6F);
    }

    private static CubeListBuilder boot(boolean mirror) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) {
            builder = builder.mirror();
        }
        return builder
                .texOffs(0, 72).addBox(mirror ? -2.3F : -2.7F, 5.2F, -2.7F, 5.0F, 6.5F, 5.4F)
                .texOffs(22, 72).addBox(mirror ? -2.3F : -2.7F, 9.0F, -4.3F, 5.0F, 3.0F, 7.0F)
                .texOffs(48, 72).addBox(mirror ? -2.35F : -2.65F, 5.0F, -3.05F, 5.0F, 5.0F, 1.2F)
                .texOffs(62, 72).addBox(mirror ? -2.8F : -3.2F, 7.2F, -3.0F, 6.0F, 2.0F, 6.0F);
    }

    private static MeshDefinition emptyHumanoidMesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        return mesh;
    }
}
