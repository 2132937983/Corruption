package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Mob;

/** Vanilla armor textures split at the elbows and knees. */
public class CorruptedHumanoidArmorModel<T extends Mob> extends HumanoidArmorModel<T> {
    private final ModelPart rightElbow, leftElbow, rightKnee, leftKnee;

    protected CorruptedHumanoidArmorModel(ModelPart root) {
        super(root);
        rightElbow = rightArm.getChild("right_elbow");
        leftElbow = leftArm.getChild("left_elbow");
        rightKnee = rightLeg.getChild("right_knee");
        leftKnee = leftLeg.getChild("left_knee");
    }

    void copyJoints(ModelPart rightElbow, ModelPart leftElbow, ModelPart rightKnee, ModelPart leftKnee) {
        this.rightElbow.copyFrom(rightElbow);
        this.leftElbow.copyFrom(leftElbow);
        this.rightKnee.copyFrom(rightKnee);
        this.leftKnee.copyFrom(leftKnee);
    }

    public static LayerDefinition createLayer(float inflation, boolean thin) {
        var deformation = new CubeDeformation(inflation);
        var mesh = HumanoidModel.createMesh(deformation, 0);
        var root = mesh.getRoot();
        for (boolean right : new boolean[] {true, false}) {
            String side = right ? "right" : "left";
            float armX = thin ? -2 : (right ? -3 : -1);
            var arm = root.addOrReplaceChild(side + "_arm",
                    CubeListBuilder.create().texOffs(40, 16).mirror(!right)
                            .addBox(armX, -2, -2, 4, 6.3F, 4, deformation),
                    PartPose.offset(right ? -5 : 5, 2, 0));
            arm.addOrReplaceChild(side + "_elbow",
                    CubeListBuilder.create().texOffs(40, 22).mirror(!right)
                            .addBox(-2, 0, -2, 4, 6, 4, deformation),
                    PartPose.offset(right ? -1 : 1, 4, 0));
            var leg = root.addOrReplaceChild(side + "_leg",
                    CubeListBuilder.create().texOffs(0, 16).mirror(!right)
                            .addBox(-2, 0, -2, 4, 6.3F, 4, deformation),
                    PartPose.offset(right ? -1.9F : 1.9F, 12, 0));
            leg.addOrReplaceChild(side + "_knee",
                    CubeListBuilder.create().texOffs(0, 22).mirror(!right)
                            .addBox(-2, 0, -2, 4, 6, 4, deformation), PartPose.offset(0, 6, 0));
        }
        return LayerDefinition.create(mesh, 64, 32);
    }
}
