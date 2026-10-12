package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/** Static meshes exported from editable native-derived Blockbench projects. */
final class CorruptedWildlifeGeometry {
    static LayerDefinition allay() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition root = corruption_root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 4.2F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.99F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(21, 0).addBox(-2.5F, -0.2F, -3.75F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, 1.25F, 0.05F, 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(47, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(58, 0).addBox(-1.3F, 0.2F, -0.8F, 2.6F, 4.6F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(123, 0).addBox(0.15F, 0.5F, 0.35F, 0.9F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(123, 0).addBox(-1.05F, 0.5F, 0.35F, 0.9F, 1.5F, 0.9F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(69, 0).addBox(-0.74F, -0.49F, -0.99F, 0.98F, 2.31F, 1.98F, new CubeDeformation(0.0F)), PartPose.offset(-1.75F, 0.5F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(69, 0).addBox(-0.74F, -0.24F, -0.99F, 0.98F, 2.15F, 1.98F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.58F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 1.42F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(76, 0).addBox(-0.24F, -0.49F, -0.99F, 0.98F, 2.31F, 1.98F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.5F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(76, 0).addBox(-0.24F, -0.24F, -0.99F, 0.98F, 2.15F, 1.98F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.58F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 1.42F, 0.0F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(83, 0).addBox(-0.04F, 1.0F, 0.0F, 0.08F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.6F));

        PartDefinition right_wing_tendon_r1 = right_wing.addOrReplaceChild("right_wing_tendon_r1", CubeListBuilder.create().texOffs(128, 0).addBox(-0.14F, -0.1F, -0.16F, 0.28F, 7.7107F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.8F, 0.0F, 1.5175F, 0.0F, 0.0F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(83, 0).addBox(-0.04F, 1.0F, 0.0F, 0.08F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.6F));

        PartDefinition left_wing_tendon_r1 = left_wing.addOrReplaceChild("left_wing_tendon_r1", CubeListBuilder.create().texOffs(128, 0).addBox(-0.14F, -0.1F, -0.16F, 0.28F, 7.7107F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.8F, 0.0F, 1.5175F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(102, 0).addBox(-0.714F, -0.588F, -0.63F, 1.428F, 1.176F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, 1.4F, 0.5F, 0.1F, 0.3F, -0.55F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(111, 0).addBox(-0.504F, -0.1F, -0.42F, 1.008F, 1.0112F, 0.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.21F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.525F, -0.84F, -0.294F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.0504F, -0.1F, -0.0693F, 0.1008F, 1.1365F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.273F, -2.0832F, -1.302F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.1575F, -0.1F, -0.189F, 0.315F, 1.4367F, 0.378F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.189F, -1.1978F, -0.4427F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.2625F, -0.1F, -0.294F, 0.525F, 1.4909F, 0.588F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.84F, -0.294F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.0504F, -0.1F, -0.0693F, 0.1008F, 1.3762F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6208F, -1.638F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.1575F, -0.1F, -0.189F, 0.315F, 1.7522F, 0.378F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.507F, -0.5569F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.2625F, -0.1F, -0.294F, 0.525F, 1.8066F, 0.588F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.525F, -0.84F, -0.294F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.0504F, -0.1F, -0.0693F, 0.1008F, 1.1365F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.273F, -2.0832F, -1.302F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.1575F, -0.1F, -0.189F, 0.315F, 1.4367F, 0.378F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.189F, -1.1978F, -0.4427F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.2625F, -0.1F, -0.294F, 0.525F, 1.4909F, 0.588F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition armadillo() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.81F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 9.0F, -4.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 1.33F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.56F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(9, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.61F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.08F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.81F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 9.0F, 4.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 1.33F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.56F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(27, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.61F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.08F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.81F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 9.0F, 4.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 1.33F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.56F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(45, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.61F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.08F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(54, 0).addBox(-4.3F, -7.3F, -10.3F, 8.6F, 8.6F, 12.6F, new CubeDeformation(0.0F))
        .texOffs(95, 0).addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(223, 0).addBox(3.8F, -4.8F, -6.0F, 0.7F, 3.2F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 4.0F));

        PartDefinition carapace_tendon_r1 = body.addOrReplaceChild("carapace_tendon_r1", CubeListBuilder.create().texOffs(234, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 4.021F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, -4.6F, -5.6F, 0.7484F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -11.0F));

        PartDefinition head_cube = head.addOrReplaceChild("head_cube", CubeListBuilder.create().texOffs(136, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, 0.0F));

        PartDefinition right_ear_cube = right_ear.addOrReplaceChild("right_ear_cube", CubeListBuilder.create().texOffs(147, 0).addBox(-2.0F, -3.0F, -0.04F, 2.0F, 5.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(1.0F, -2.0F, 0.0F));

        PartDefinition left_ear_cube = left_ear.addOrReplaceChild("left_ear_cube", CubeListBuilder.create().texOffs(154, 0).addBox(0.0F, -3.0F, -0.04F, 2.0F, 5.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(161, 0).addBox(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F));

        PartDefinition carapace_claw = body.addOrReplaceChild("carapace_claw", CubeListBuilder.create().texOffs(184, 0).addBox(-1.224F, -1.008F, -1.08F, 2.448F, 2.016F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, -6.3F, -1.5F, 0.16F, -0.35F, -0.42F));

        PartDefinition carapace_claw_palm_r1 = carapace_claw.addOrReplaceChild("carapace_claw_palm_r1", CubeListBuilder.create().texOffs(197, 0).addBox(-0.864F, -0.1F, -0.72F, 1.728F, 1.5906F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.36F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_0 = carapace_claw.addOrReplaceChild("carapace_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9F, -1.44F, -0.504F, 0.0F, -0.2F, -0.14F));

        PartDefinition carapace_claw_finger_0_sharp_tip_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 1.8054F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.468F, -3.5712F, -2.232F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition carapace_claw_finger_0_blade_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(213, 0).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.32F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.324F, -2.0534F, -0.7589F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition carapace_claw_finger_0_proximal_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.413F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition carapace_claw_finger_1 = carapace_claw.addOrReplaceChild("carapace_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.44F, -0.504F, 0.09F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_sharp_tip_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(213, 0).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 2.2163F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4928F, -2.808F, -1.7955F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_blade_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(213, 0).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.8609F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5834F, -0.9547F, -2.3711F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_proximal_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.9541F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_2 = carapace_claw.addOrReplaceChild("carapace_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, -1.44F, -0.504F, 0.18F, 0.2F, 0.14F));

        PartDefinition carapace_claw_finger_2_sharp_tip_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 1.8054F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.468F, -3.5712F, -2.232F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition carapace_claw_finger_2_blade_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(213, 0).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.32F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.324F, -2.0534F, -0.7589F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition carapace_claw_finger_2_proximal_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.413F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(166, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.81F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 9.0F, -4.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(166, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 1.33F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.56F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(175, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.61F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.08F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition axolotl() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 1.0F));

        PartDefinition tail_fleshy_root_r1 = body.addOrReplaceChild("tail_fleshy_root_r1", CubeListBuilder.create().texOffs(255, 18).addBox(-0.85F, -0.1F, -0.95F, 1.7F, 4.7F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.0F, 8.5F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.001F, -3.001F, -5.001F, 8.002F, 4.002F, 5.002F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.0F, -10.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(27, 0).addBox(-4.001F, -0.201F, -3.751F, 8.002F, 1.202F, 5.002F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.25F, 0.05F, 0.0F, 0.0F));

        PartDefinition top_gills = head.addOrReplaceChild("top_gills", CubeListBuilder.create().texOffs(59, 0).addBox(-4.001F, -3.001F, -0.041F, 8.002F, 3.002F, 0.082F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -1.0F));

        PartDefinition left_gills = head.addOrReplaceChild("left_gills", CubeListBuilder.create().texOffs(78, 0).addBox(-3.001F, -5.001F, -0.041F, 3.002F, 7.002F, 0.082F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, -1.0F));

        PartDefinition right_gills = head.addOrReplaceChild("right_gills", CubeListBuilder.create().texOffs(87, 0).addBox(-0.001F, -5.001F, -0.041F, 3.002F, 7.002F, 0.082F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, -1.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(96, 0).addBox(-0.04F, -3.0F, 0.0F, 0.08F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.0F, 11.0F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(123, 0).addBox(-1.001F, -0.001F, -0.351F, 3.002F, 5.002F, 0.702F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, -6.0F));

        PartDefinition left_front_leg_digit_2_r1 = left_front_leg.addOrReplaceChild("left_front_leg_digit_2_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 3.1787F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 2.6F, 0.65F, 0.0841F, 0.0458F, -0.9976F));

        PartDefinition left_front_leg_digit_1_r1 = left_front_leg.addOrReplaceChild("left_front_leg_digit_1_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.9203F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 2.6F, 0.0F, 0.0F, 0.0F, -0.942F));

        PartDefinition left_front_leg_digit_0_r1 = left_front_leg.addOrReplaceChild("left_front_leg_digit_0_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.6965F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 2.6F, -0.65F, -0.1004F, -0.0465F, -0.8662F));

        PartDefinition left_front_leg_fleshy_joint_r1 = left_front_leg.addOrReplaceChild("left_front_leg_fleshy_joint_r1", CubeListBuilder.create().texOffs(264, 18).addBox(-0.8F, -0.1F, -0.675F, 1.6F, 3.8688F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, -0.3045F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(123, 0).addBox(-1.001F, -0.001F, -0.351F, 3.002F, 5.002F, 0.702F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 5.0F));

        PartDefinition left_hind_leg_digit_2_r1 = left_hind_leg.addOrReplaceChild("left_hind_leg_digit_2_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 3.1787F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 2.6F, 0.65F, 0.0841F, 0.0458F, -0.9976F));

        PartDefinition left_hind_leg_digit_1_r1 = left_hind_leg.addOrReplaceChild("left_hind_leg_digit_1_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.9203F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 2.6F, 0.0F, 0.0F, 0.0F, -0.942F));

        PartDefinition left_hind_leg_digit_0_r1 = left_hind_leg.addOrReplaceChild("left_hind_leg_digit_0_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.6965F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 2.6F, -0.65F, -0.1004F, -0.0465F, -0.8662F));

        PartDefinition left_hind_leg_fleshy_joint_r1 = left_hind_leg.addOrReplaceChild("left_hind_leg_fleshy_joint_r1", CubeListBuilder.create().texOffs(264, 18).addBox(-0.8F, -0.1F, -0.675F, 1.6F, 3.8688F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, -0.3045F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(123, 0).addBox(-2.001F, -0.001F, -0.351F, 3.002F, 5.002F, 0.702F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, -6.0F));

        PartDefinition right_front_leg_digit_2_r1 = right_front_leg.addOrReplaceChild("right_front_leg_digit_2_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 3.1787F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.6F, 0.65F, 0.0841F, -0.0458F, 0.9976F));

        PartDefinition right_front_leg_digit_1_r1 = right_front_leg.addOrReplaceChild("right_front_leg_digit_1_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.9203F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.6F, 0.0F, 0.0F, 0.0F, 0.942F));

        PartDefinition right_front_leg_digit_0_r1 = right_front_leg.addOrReplaceChild("right_front_leg_digit_0_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.6965F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.6F, -0.65F, -0.1004F, 0.0465F, 0.8662F));

        PartDefinition right_front_leg_fleshy_joint_r1 = right_front_leg.addOrReplaceChild("right_front_leg_fleshy_joint_r1", CubeListBuilder.create().texOffs(264, 18).addBox(-0.8F, -0.1F, -0.675F, 1.6F, 3.8688F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.3045F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(123, 0).addBox(-2.001F, -0.001F, -0.351F, 3.002F, 5.002F, 0.702F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 5.0F));

        PartDefinition right_hind_leg_digit_2_r1 = right_hind_leg.addOrReplaceChild("right_hind_leg_digit_2_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 3.1787F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.6F, 0.65F, 0.0841F, -0.0458F, 0.9976F));

        PartDefinition right_hind_leg_digit_1_r1 = right_hind_leg.addOrReplaceChild("right_hind_leg_digit_1_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.9203F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.6F, 0.0F, 0.0F, 0.0F, 0.942F));

        PartDefinition right_hind_leg_digit_0_r1 = right_hind_leg.addOrReplaceChild("right_hind_leg_digit_0_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.2F, -0.1F, -0.23F, 0.4F, 2.6965F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.6F, -0.65F, -0.1004F, 0.0465F, 0.8662F));

        PartDefinition right_hind_leg_fleshy_joint_r1 = right_hind_leg.addOrReplaceChild("right_hind_leg_fleshy_joint_r1", CubeListBuilder.create().texOffs(264, 18).addBox(-0.8F, -0.1F, -0.675F, 1.6F, 3.8688F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.3045F));

        PartDefinition flesh_core = body.addOrReplaceChild("flesh_core", CubeListBuilder.create().texOffs(132, 0).addBox(-3.6949F, -5.04F, -2.9021F, 7.3897F, 2.08F, 5.8043F, new CubeDeformation(0.0F))
        .texOffs(161, 0).addBox(-2.6603F, -5.04F, -4.0307F, 5.3206F, 2.08F, 8.0615F, new CubeDeformation(0.0F))
        .texOffs(192, 0).addBox(-5.1089F, -3.04F, -4.0128F, 10.2177F, 2.08F, 8.0256F, new CubeDeformation(0.0F))
        .texOffs(233, 0).addBox(-3.6784F, -3.04F, -5.5733F, 7.3568F, 2.08F, 11.1466F, new CubeDeformation(0.0F))
        .texOffs(192, 0).addBox(-5.5F, -1.04F, -4.32F, 11.0F, 2.08F, 8.64F, new CubeDeformation(0.0F))
        .texOffs(233, 0).addBox(-3.96F, -1.04F, -6.0F, 7.92F, 2.08F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(192, 0).addBox(-5.1089F, 0.96F, -4.0128F, 10.2177F, 2.08F, 8.0256F, new CubeDeformation(0.0F))
        .texOffs(233, 0).addBox(-3.6784F, 0.96F, -5.5733F, 7.3568F, 2.08F, 11.1466F, new CubeDeformation(0.0F))
        .texOffs(132, 0).addBox(-3.6949F, 2.96F, -2.9021F, 7.3897F, 2.08F, 5.8043F, new CubeDeformation(0.0F))
        .texOffs(161, 0).addBox(-2.6603F, 2.96F, -4.0307F, 5.3206F, 2.08F, 8.0615F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.03F, 0.0F, -0.04F));

        PartDefinition flesh_front = body.addOrReplaceChild("flesh_front", CubeListBuilder.create().texOffs(274, 0).addBox(-3.359F, -4.54F, -2.4184F, 6.7179F, 1.88F, 4.8369F, new CubeDeformation(0.0F))
        .texOffs(299, 0).addBox(-2.4184F, -4.54F, -3.359F, 4.8369F, 1.88F, 6.7179F, new CubeDeformation(0.0F))
        .texOffs(324, 0).addBox(-4.6444F, -2.74F, -3.344F, 9.2888F, 1.88F, 6.688F, new CubeDeformation(0.0F))
        .texOffs(359, 0).addBox(-3.344F, -2.74F, -4.6444F, 6.688F, 1.88F, 9.2888F, new CubeDeformation(0.0F))
        .texOffs(394, 0).addBox(-5.0F, -0.94F, -3.6F, 10.0F, 1.88F, 7.2F, new CubeDeformation(0.0F))
        .texOffs(431, 0).addBox(-3.6F, -0.94F, -5.0F, 7.2F, 1.88F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(324, 0).addBox(-4.6444F, 0.86F, -3.344F, 9.2888F, 1.88F, 6.688F, new CubeDeformation(0.0F))
        .texOffs(359, 0).addBox(-3.344F, 0.86F, -4.6444F, 6.688F, 1.88F, 9.2888F, new CubeDeformation(0.0F))
        .texOffs(274, 0).addBox(-3.359F, 2.66F, -2.4184F, 6.7179F, 1.88F, 4.8369F, new CubeDeformation(0.0F))
        .texOffs(299, 0).addBox(-2.4184F, 2.66F, -3.359F, 4.8369F, 1.88F, 6.7179F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -3.0F, -7.0F, -0.07F, -0.1F, -0.09F));

        PartDefinition flesh_rear = body.addOrReplaceChild("flesh_rear", CubeListBuilder.create().texOffs(468, 0).addBox(-3.359F, -5.04F, -2.4184F, 6.7179F, 2.08F, 4.8369F, new CubeDeformation(0.0F))
        .texOffs(0, 18).addBox(-2.4184F, -5.04F, -3.359F, 4.8369F, 2.08F, 6.7179F, new CubeDeformation(0.0F))
        .texOffs(25, 18).addBox(-4.6444F, -3.04F, -3.344F, 9.2888F, 2.08F, 6.688F, new CubeDeformation(0.0F))
        .texOffs(60, 18).addBox(-3.344F, -3.04F, -4.6444F, 6.688F, 2.08F, 9.2888F, new CubeDeformation(0.0F))
        .texOffs(95, 18).addBox(-5.0F, -1.04F, -3.6F, 10.0F, 2.08F, 7.2F, new CubeDeformation(0.0F))
        .texOffs(132, 18).addBox(-3.6F, -1.04F, -5.0F, 7.2F, 2.08F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(25, 18).addBox(-4.6444F, 0.96F, -3.344F, 9.2888F, 2.08F, 6.688F, new CubeDeformation(0.0F))
        .texOffs(60, 18).addBox(-3.344F, 0.96F, -4.6444F, 6.688F, 2.08F, 9.2888F, new CubeDeformation(0.0F))
        .texOffs(468, 0).addBox(-3.359F, 2.96F, -2.4184F, 6.7179F, 2.08F, 4.8369F, new CubeDeformation(0.0F))
        .texOffs(0, 18).addBox(-2.4184F, 2.96F, -3.359F, 4.8369F, 2.08F, 6.7179F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.0F, 6.0F, 0.04F, 0.09F, 0.1F));

        PartDefinition flank_claw = flesh_rear.addOrReplaceChild("flank_claw", CubeListBuilder.create().texOffs(186, 18).addBox(-1.36F, -1.12F, -1.2F, 2.72F, 2.24F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.0F, 2.0F, 0.25F, -0.4F, 0.6F));

        PartDefinition flank_claw_palm_r1 = flank_claw.addOrReplaceChild("flank_claw_palm_r1", CubeListBuilder.create().texOffs(227, 18).addBox(-0.96F, -0.1F, -0.8F, 1.92F, 1.7451F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_0 = flank_claw.addOrReplaceChild("flank_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, -1.6F, -0.56F, 0.0F, -0.2F, -0.14F));

        PartDefinition flank_claw_finger_0_sharp_tip_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(243, 18).addBox(-0.096F, -0.1F, -0.132F, 0.192F, 1.9838F, 0.264F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.52F, -3.968F, -2.48F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition flank_claw_finger_0_blade_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.3F, -0.1F, -0.36F, 0.6F, 2.5556F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.36F, -2.2816F, -0.8432F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition flank_claw_finger_0_proximal_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(236, 18).addBox(-0.5F, -0.1F, -0.56F, 1.0F, 2.6589F, 1.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition flank_claw_finger_1 = flank_claw.addOrReplaceChild("flank_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.6F, -0.56F, 0.09F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_sharp_tip_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.096F, -0.1F, -0.132F, 0.192F, 2.4403F, 0.264F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.992F, -3.12F, -1.7955F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_blade_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.3F, -0.1F, -0.36F, 0.6F, 3.1566F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.8704F, -1.0608F, -2.3711F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_proximal_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(248, 18).addBox(-0.5F, -0.1F, -0.56F, 1.0F, 3.2601F, 1.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_2 = flank_claw.addOrReplaceChild("flank_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -1.6F, -0.56F, 0.18F, 0.2F, 0.14F));

        PartDefinition flank_claw_finger_2_sharp_tip_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(243, 18).addBox(-0.096F, -0.1F, -0.132F, 0.192F, 1.9838F, 0.264F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.52F, -3.968F, -2.48F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition flank_claw_finger_2_blade_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.3F, -0.1F, -0.36F, 0.6F, 2.5556F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.36F, -2.2816F, -0.8432F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition flank_claw_finger_2_proximal_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(236, 18).addBox(-0.5F, -0.1F, -0.56F, 1.0F, 2.6589F, 1.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition flesh_crown = body.addOrReplaceChild("flesh_crown", CubeListBuilder.create().texOffs(274, 0).addBox(-3.359F, -4.54F, -2.4184F, 6.7179F, 1.88F, 4.8369F, new CubeDeformation(0.0F))
        .texOffs(299, 0).addBox(-2.4184F, -4.54F, -3.359F, 4.8369F, 1.88F, 6.7179F, new CubeDeformation(0.0F))
        .texOffs(324, 0).addBox(-4.6444F, -2.74F, -3.344F, 9.2888F, 1.88F, 6.688F, new CubeDeformation(0.0F))
        .texOffs(359, 0).addBox(-3.344F, -2.74F, -4.6444F, 6.688F, 1.88F, 9.2888F, new CubeDeformation(0.0F))
        .texOffs(394, 0).addBox(-5.0F, -0.94F, -3.6F, 10.0F, 1.88F, 7.2F, new CubeDeformation(0.0F))
        .texOffs(431, 0).addBox(-3.6F, -0.94F, -5.0F, 7.2F, 1.88F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(324, 0).addBox(-4.6444F, 0.86F, -3.344F, 9.2888F, 1.88F, 6.688F, new CubeDeformation(0.0F))
        .texOffs(359, 0).addBox(-3.344F, 0.86F, -4.6444F, 6.688F, 1.88F, 9.2888F, new CubeDeformation(0.0F))
        .texOffs(274, 0).addBox(-3.359F, 2.66F, -2.4184F, 6.7179F, 1.88F, 4.8369F, new CubeDeformation(0.0F))
        .texOffs(299, 0).addBox(-2.4184F, 2.66F, -3.359F, 4.8369F, 1.88F, 6.7179F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, -1.0F, 0.06F, -0.1F, 0.07F));

        PartDefinition crown_claw = flesh_crown.addOrReplaceChild("crown_claw", CubeListBuilder.create().texOffs(169, 18).addBox(-1.734F, -1.428F, -1.53F, 3.468F, 2.856F, 3.06F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -3.4F, 0.8F, 0.16F, 0.2F, -0.45F));

        PartDefinition crown_claw_palm_r1 = crown_claw.addOrReplaceChild("crown_claw_palm_r1", CubeListBuilder.create().texOffs(186, 18).addBox(-1.224F, -0.1F, -1.02F, 2.448F, 2.17F, 2.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.51F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_0 = crown_claw.addOrReplaceChild("crown_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.275F, -2.04F, -0.714F, 0.0F, -0.2F, -0.14F));

        PartDefinition crown_claw_finger_0_sharp_tip_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.1224F, -0.1F, -0.1683F, 0.2448F, 2.4744F, 0.3366F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.663F, -5.0592F, -3.162F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition crown_claw_finger_0_blade_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.3825F, -0.1F, -0.459F, 0.765F, 3.2033F, 0.918F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.459F, -2.909F, -1.0751F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition crown_claw_finger_0_proximal_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(199, 18).addBox(-0.6375F, -0.1F, -0.714F, 1.275F, 3.3351F, 1.428F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition crown_claw_finger_1 = crown_claw.addOrReplaceChild("crown_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.04F, -0.714F, 0.09F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_sharp_tip_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.1224F, -0.1F, -0.1683F, 0.2448F, 3.0564F, 0.3366F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.3648F, -3.978F, -1.7955F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_blade_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.3825F, -0.1F, -0.459F, 0.765F, 3.9697F, 0.918F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6598F, -1.3525F, -2.3711F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_proximal_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(218, 18).addBox(-0.6375F, -0.1F, -0.714F, 1.275F, 4.1017F, 1.428F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_2 = crown_claw.addOrReplaceChild("crown_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.275F, -2.04F, -0.714F, 0.18F, 0.2F, 0.14F));

        PartDefinition crown_claw_finger_2_sharp_tip_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(213, 18).addBox(-0.1224F, -0.1F, -0.1683F, 0.2448F, 2.4744F, 0.3366F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.663F, -5.0592F, -3.162F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition crown_claw_finger_2_blade_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(208, 18).addBox(-0.3825F, -0.1F, -0.459F, 0.765F, 3.2033F, 0.918F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.459F, -2.909F, -1.0751F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition crown_claw_finger_2_proximal_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(199, 18).addBox(-0.6375F, -0.1F, -0.714F, 1.275F, 3.3351F, 1.428F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition bat() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 2.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(26, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -0.8F, -1.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(26, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, -1.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(26, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.8F, -1.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(13, 0).addBox(-2.0F, -0.2F, -1.5F, 4.0F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, 0.5F, 0.05F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(31, 0).addBox(-2.5F, -4.0F, -0.04F, 3.0F, 5.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -2.0F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(40, 0).addBox(-0.1F, -3.0F, -0.04F, 3.0F, 5.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(1.1F, -3.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(49, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition feet = body.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, 0.0F, -0.04F, 3.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(69, 0).addBox(-2.0F, -2.0F, -0.04F, 2.0F, 7.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.0F, 0.0F));

        PartDefinition right_wing_spar_r1 = right_wing.addOrReplaceChild("right_wing_spar_r1", CubeListBuilder.create().texOffs(135, 0).addBox(-0.15F, -0.1F, -0.175F, 0.3F, 2.2F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition right_wing_tip = right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(76, 0).addBox(-6.0F, -2.0F, -0.04F, 6.0F, 8.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition right_wing_hook_r1 = right_wing_tip.addOrReplaceChild("right_wing_hook_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.13F, -0.1F, -0.15F, 0.26F, 2.2322F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -0.4F, 0.0F, -0.482F, 1.0113F, 2.306F));

        PartDefinition right_wing_tip_spar_r1 = right_wing_tip.addOrReplaceChild("right_wing_tip_spar_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.135F, -0.1F, -0.15F, 0.27F, 6.0215F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.6568F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(91, 0).addBox(0.0F, -2.0F, -0.04F, 2.0F, 7.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.0F, 0.0F));

        PartDefinition left_wing_spar_r1 = left_wing.addOrReplaceChild("left_wing_spar_r1", CubeListBuilder.create().texOffs(135, 0).addBox(-0.15F, -0.1F, -0.175F, 0.3F, 2.2F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        PartDefinition left_wing_tip = left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(98, 0).addBox(0.0F, -2.0F, -0.04F, 6.0F, 8.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition left_wing_hook_r1 = left_wing_tip.addOrReplaceChild("left_wing_hook_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.13F, -0.1F, -0.15F, 0.26F, 2.2322F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4F, -0.4F, 0.0F, -0.482F, -1.0113F, -2.306F));

        PartDefinition left_wing_tip_spar_r1 = left_wing_tip.addOrReplaceChild("left_wing_tip_spar_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.135F, -0.1F, -0.15F, 0.27F, 6.0215F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.6568F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(113, 0).addBox(-0.51F, -0.42F, -0.45F, 1.02F, 0.84F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3F, 1.0F, 0.0F, 0.1F, -0.2F, 0.6F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(120, 0).addBox(-0.36F, -0.1F, -0.3F, 0.72F, 0.7794F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.15F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.375F, -0.6F, -0.21F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(130, 0).addBox(-0.036F, -0.1F, -0.0495F, 0.072F, 0.8689F, 0.099F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.195F, -1.488F, -0.93F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.1125F, -0.1F, -0.135F, 0.225F, 1.0833F, 0.27F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.135F, -0.8556F, -0.3162F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.1875F, -0.1F, -0.21F, 0.375F, 1.1221F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.6F, -0.21F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.036F, -0.1F, -0.0495F, 0.072F, 1.0401F, 0.099F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.872F, -1.17F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.1125F, -0.1F, -0.135F, 0.225F, 1.3087F, 0.27F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0764F, -0.3978F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.1875F, -0.1F, -0.21F, 0.375F, 1.3476F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.375F, -0.6F, -0.21F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(130, 0).addBox(-0.036F, -0.1F, -0.0495F, 0.072F, 0.8689F, 0.099F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.195F, -1.488F, -0.93F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.1125F, -0.1F, -0.135F, 0.225F, 1.0833F, 0.27F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.135F, -0.8556F, -0.3162F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(125, 0).addBox(-0.1875F, -0.1F, -0.21F, 0.375F, 1.1221F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition bee() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition bone = corruption_root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition front_legs = bone.addOrReplaceChild("front_legs", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -0.04F, 7.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, -2.0F));

        PartDefinition right_wing = bone.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(17, 0).addBox(-9.001F, -0.041F, -0.001F, 9.002F, 0.082F, 6.002F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F));

        PartDefinition right_wing_spar_r1 = right_wing.addOrReplaceChild("right_wing_spar_r1", CubeListBuilder.create().texOffs(183, 0).addBox(-0.15F, -0.1F, -0.175F, 0.3F, 8.2F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 1.5708F));

        PartDefinition left_wing = bone.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-0.001F, -0.041F, -0.001F, 9.002F, 0.082F, 6.002F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F));

        PartDefinition left_wing_spar_r1 = left_wing.addOrReplaceChild("left_wing_spar_r1", CubeListBuilder.create().texOffs(183, 0).addBox(-0.15F, -0.1F, -0.175F, 0.3F, 8.2F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.0F, 0.0F, -1.5708F));

        PartDefinition middle_legs = bone.addOrReplaceChild("middle_legs", CubeListBuilder.create().texOffs(48, 0).addBox(-5.0F, 0.0F, -0.04F, 7.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, 0.0F));

        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(65, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_antenna = body.addOrReplaceChild("right_antenna", CubeListBuilder.create().texOffs(100, 0).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

        PartDefinition stinger = body.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(109, 0).addBox(-0.04F, -1.0F, 5.0F, 0.08F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_antenna = body.addOrReplaceChild("left_antenna", CubeListBuilder.create().texOffs(116, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

        PartDefinition stinger_claw = body.addOrReplaceChild("stinger_claw", CubeListBuilder.create().texOffs(142, 0).addBox(-0.986F, -0.812F, -0.87F, 1.972F, 1.624F, 1.74F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 4.7F, -1.5708F, 0.1F, 0.1F));

        PartDefinition stinger_claw_palm_r1 = stinger_claw.addOrReplaceChild("stinger_claw_palm_r1", CubeListBuilder.create().texOffs(142, 0).addBox(-0.696F, -0.1F, -0.58F, 1.392F, 1.3202F, 1.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.29F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition stinger_claw_finger_0 = stinger_claw.addOrReplaceChild("stinger_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.725F, -1.16F, -0.406F, 0.0F, -0.2F, -0.14F));

        PartDefinition stinger_claw_finger_0_sharp_tip_r1 = stinger_claw_finger_0.addOrReplaceChild("stinger_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.0696F, -0.1F, -0.0957F, 0.1392F, 1.4933F, 0.1914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.377F, -2.8768F, -1.798F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition stinger_claw_finger_0_blade_r1 = stinger_claw_finger_0.addOrReplaceChild("stinger_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.2175F, -0.1F, -0.261F, 0.435F, 1.9078F, 0.522F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.261F, -1.6542F, -0.6113F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition stinger_claw_finger_0_proximal_r1 = stinger_claw_finger_0.addOrReplaceChild("stinger_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.3625F, -0.1F, -0.406F, 0.725F, 1.9827F, 0.812F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition stinger_claw_finger_1 = stinger_claw.addOrReplaceChild("stinger_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.16F, -0.406F, 0.09F, 0.0F, 0.0F));

        PartDefinition stinger_claw_finger_1_sharp_tip_r1 = stinger_claw_finger_1.addOrReplaceChild("stinger_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.0696F, -0.1F, -0.0957F, 0.1392F, 1.8242F, 0.1914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6192F, -2.262F, -1.7955F, 0.0F, 0.0F));

        PartDefinition stinger_claw_finger_1_blade_r1 = stinger_claw_finger_1.addOrReplaceChild("stinger_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(156, 0).addBox(-0.2175F, -0.1F, -0.261F, 0.435F, 2.3435F, 0.522F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.081F, -0.7691F, -2.3711F, 0.0F, 0.0F));

        PartDefinition stinger_claw_finger_1_proximal_r1 = stinger_claw_finger_1.addOrReplaceChild("stinger_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(156, 0).addBox(-0.3625F, -0.1F, -0.406F, 0.725F, 2.4186F, 0.812F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition stinger_claw_finger_2 = stinger_claw.addOrReplaceChild("stinger_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.725F, -1.16F, -0.406F, 0.18F, 0.2F, 0.14F));

        PartDefinition stinger_claw_finger_2_sharp_tip_r1 = stinger_claw_finger_2.addOrReplaceChild("stinger_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.0696F, -0.1F, -0.0957F, 0.1392F, 1.4933F, 0.1914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.377F, -2.8768F, -1.798F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition stinger_claw_finger_2_blade_r1 = stinger_claw_finger_2.addOrReplaceChild("stinger_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.2175F, -0.1F, -0.261F, 0.435F, 1.9078F, 0.522F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.261F, -1.6542F, -0.6113F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition stinger_claw_finger_2_proximal_r1 = stinger_claw_finger_2.addOrReplaceChild("stinger_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(151, 0).addBox(-0.3625F, -0.1F, -0.406F, 0.725F, 1.9827F, 0.812F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition head_jaw = body.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(161, 0).addBox(-2.6F, -0.2F, -1.3F, 5.2F, 1.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -4.0F, 0.1F, 0.0F, 0.0F));

        PartDefinition back_legs = bone.addOrReplaceChild("back_legs", CubeListBuilder.create().texOffs(125, 0).addBox(-5.0F, 0.0F, -0.04F, 7.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition bogged() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.5F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_4_r1 = head.addOrReplaceChild("head_upper_fang_4_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -1.7F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, -1.7F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.7F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -1.7F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.7F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -0.2F, -6.0F, 8.0F, 1.7F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, 2.0F, 0.05F, 0.0F, 0.0F));

        PartDefinition skull_claw = head.addOrReplaceChild("skull_claw", CubeListBuilder.create().texOffs(132, 0).addBox(-1.02F, -0.84F, -0.9F, 2.04F, 1.68F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, -6.6F, 2.3F, 0.18F, -0.32F, 0.55F));

        PartDefinition skull_claw_palm_r1 = skull_claw.addOrReplaceChild("skull_claw_palm_r1", CubeListBuilder.create().texOffs(143, 0).addBox(-0.72F, -0.1F, -0.6F, 1.44F, 1.3588F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_0 = skull_claw.addOrReplaceChild("skull_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.75F, -1.2F, -0.42F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_claw_finger_0_sharp_tip_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.072F, -0.1F, -0.099F, 0.144F, 1.5378F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.39F, -2.976F, -1.86F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_claw_finger_0_blade_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.225F, -0.1F, -0.27F, 0.45F, 1.9667F, 0.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.27F, -1.7112F, -0.6324F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_claw_finger_0_proximal_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.375F, -0.1F, -0.42F, 0.75F, 2.0442F, 0.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_claw_finger_1 = skull_claw.addOrReplaceChild("skull_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.2F, -0.42F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_sharp_tip_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.072F, -0.1F, -0.099F, 0.144F, 1.8802F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.744F, -2.34F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_blade_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.225F, -0.1F, -0.27F, 0.45F, 2.4174F, 0.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1528F, -0.7956F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_proximal_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.375F, -0.1F, -0.42F, 0.75F, 2.4951F, 0.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_2 = skull_claw.addOrReplaceChild("skull_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.75F, -1.2F, -0.42F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_claw_finger_2_sharp_tip_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.072F, -0.1F, -0.099F, 0.144F, 1.5378F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.39F, -2.976F, -1.86F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_claw_finger_2_blade_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.225F, -0.1F, -0.27F, 0.45F, 1.9667F, 0.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.27F, -1.7112F, -0.6324F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_claw_finger_2_proximal_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.375F, -0.1F, -0.42F, 0.75F, 2.0442F, 0.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(71, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(71, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 6.01F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(80, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(89, 0).mirror().addBox(-1.0F, -0.25F, -1.0F, 2.0F, 5.31F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(98, 0).mirror().addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(71, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(71, 0).mirror().addBox(-1.0F, -0.25F, -1.0F, 2.0F, 6.01F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(80, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(89, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 5.31F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(98, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(107, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(189, 0).addBox(-3.5F, 1.3F, 1.7F, 0.65F, 7.3F, 0.75F, new CubeDeformation(0.0F))
        .texOffs(189, 0).addBox(-0.7F, 1.3F, 1.7F, 0.65F, 7.3F, 0.75F, new CubeDeformation(0.0F))
        .texOffs(189, 0).addBox(2.1F, 1.3F, 1.7F, 0.65F, 7.3F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.1F, 0.0F, 0.0F));

        PartDefinition rib_claw = body.addOrReplaceChild("rib_claw", CubeListBuilder.create().texOffs(162, 0).addBox(-1.292F, -1.064F, -1.14F, 2.584F, 2.128F, 2.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, 4.2F, 1.6F, 0.18F, 0.3F, -0.45F));

        PartDefinition rib_claw_palm_r1 = rib_claw.addOrReplaceChild("rib_claw_palm_r1", CubeListBuilder.create().texOffs(143, 0).addBox(-0.912F, -0.1F, -0.76F, 1.824F, 1.6678F, 1.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.38F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_0 = rib_claw.addOrReplaceChild("rib_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.95F, -1.52F, -0.532F, 0.0F, -0.2F, -0.14F));

        PartDefinition rib_claw_finger_0_sharp_tip_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.494F, -3.7696F, -2.356F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition rib_claw_finger_0_blade_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.342F, -2.1675F, -0.801F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition rib_claw_finger_0_proximal_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition rib_claw_finger_1 = rib_claw.addOrReplaceChild("rib_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.52F, -0.532F, 0.09F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_sharp_tip_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 2.3283F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.7424F, -2.964F, -1.7955F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_blade_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 3.0088F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7269F, -1.0078F, -2.3711F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_proximal_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(182, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 3.1071F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_2 = rib_claw.addOrReplaceChild("rib_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.95F, -1.52F, -0.532F, 0.18F, 0.2F, 0.14F));

        PartDefinition rib_claw_finger_2_sharp_tip_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(157, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.494F, -3.7696F, -2.356F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition rib_claw_finger_2_blade_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(152, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.342F, -2.1675F, -0.801F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition rib_claw_finger_2_proximal_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition breeze() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(29, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition crown_claw = head.addOrReplaceChild("crown_claw", CubeListBuilder.create().texOffs(143, 19).addBox(-1.224F, -1.008F, -1.08F, 2.448F, 2.016F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -6.3F, 1.2F, 0.14F, 0.2F, -0.48F));

        PartDefinition crown_claw_palm_r1 = crown_claw.addOrReplaceChild("crown_claw_palm_r1", CubeListBuilder.create().texOffs(156, 19).addBox(-0.864F, -0.1F, -0.72F, 1.728F, 1.5906F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.36F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_0 = crown_claw.addOrReplaceChild("crown_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9F, -1.44F, -0.504F, 0.0F, -0.2F, -0.14F));

        PartDefinition crown_claw_finger_0_sharp_tip_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(177, 19).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 1.8054F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.468F, -3.5712F, -2.232F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition crown_claw_finger_0_blade_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(172, 19).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.32F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.324F, -2.0534F, -0.7589F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition crown_claw_finger_0_proximal_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(165, 19).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.413F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition crown_claw_finger_1 = crown_claw.addOrReplaceChild("crown_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.44F, -0.504F, 0.09F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_sharp_tip_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(172, 19).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 2.2163F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4928F, -2.808F, -1.7955F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_blade_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(172, 19).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.8609F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5834F, -0.9547F, -2.3711F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_proximal_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(165, 19).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.9541F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_2 = crown_claw.addOrReplaceChild("crown_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, -1.44F, -0.504F, 0.18F, 0.2F, 0.14F));

        PartDefinition crown_claw_finger_2_sharp_tip_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(177, 19).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 1.8054F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.468F, -3.5712F, -2.232F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition crown_claw_finger_2_blade_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(172, 19).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.32F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.324F, -2.0534F, -0.7589F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition crown_claw_finger_2_proximal_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(165, 19).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.413F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition rods = body.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition rod_2 = rods.addOrReplaceChild("rod_2", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(182, 19).addBox(-1.15F, -0.35F, -3.15F, 2.3F, 1.5F, 2.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F));

        PartDefinition rod_3 = rods.addOrReplaceChild("rod_3", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(182, 19).addBox(-1.15F, -0.35F, -3.15F, 2.3F, 1.5F, 2.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition rod_1 = rods.addOrReplaceChild("rod_1", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(182, 19).addBox(-1.15F, -0.35F, -3.15F, 2.3F, 1.5F, 2.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F));

        PartDefinition wind_body = corruption_root.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition wind_bottom = wind_body.addOrReplaceChild("wind_bottom", CubeListBuilder.create().texOffs(71, 0).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition wind_mid = wind_bottom.addOrReplaceChild("wind_mid", CubeListBuilder.create().texOffs(92, 0).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(141, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(174, 0).addBox(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition wind_top = wind_mid.addOrReplaceChild("wind_top", CubeListBuilder.create().texOffs(0, 19).addBox(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new CubeDeformation(0.0F))
        .texOffs(73, 19).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(122, 19).addBox(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition camel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 11.17F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, -11.0F, -10.5F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(21, 0).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 9.63F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.92F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(42, 0).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 0.95F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.38F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(63, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 11.17F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, -11.0F, 9.5F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 9.63F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.92F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(105, 0).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 0.95F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.38F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(126, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 11.17F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.9F, -11.0F, 9.5F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(147, 0).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 9.63F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.92F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(168, 0).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 0.95F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.38F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, new CubeDeformation(0.0F))
        .texOffs(160, 58).addBox(6.9F, -8.5F, -16.0F, 0.9F, 4.8F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 9.5F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(85, 18).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, new CubeDeformation(0.0F))
        .texOffs(138, 18).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(167, 18).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -19.5F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(190, 18).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -21.0F, -9.5F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(201, 18).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -21.0F, -9.5F));

        PartDefinition neck_claw = head.addOrReplaceChild("neck_claw", CubeListBuilder.create().texOffs(135, 58).addBox(-1.054F, -0.868F, -0.93F, 2.108F, 1.736F, 1.86F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -13.0F, -10.0F, 0.2F, -0.3F, 0.48F));

        PartDefinition neck_claw_palm_r1 = neck_claw.addOrReplaceChild("neck_claw_palm_r1", CubeListBuilder.create().texOffs(146, 58).addBox(-0.744F, -0.1F, -0.62F, 1.488F, 1.3974F, 1.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.31F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_0 = neck_claw.addOrReplaceChild("neck_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.775F, -1.24F, -0.434F, 0.0F, -0.2F, -0.14F));

        PartDefinition neck_claw_finger_0_sharp_tip_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(155, 58).addBox(-0.0744F, -0.1F, -0.1023F, 0.1488F, 1.5824F, 0.2046F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.403F, -3.0752F, -1.922F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition neck_claw_finger_0_blade_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.2325F, -0.1F, -0.279F, 0.465F, 2.0256F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.279F, -1.7682F, -0.6535F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition neck_claw_finger_0_proximal_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.3875F, -0.1F, -0.434F, 0.775F, 2.1057F, 0.868F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition neck_claw_finger_1 = neck_claw.addOrReplaceChild("neck_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.24F, -0.434F, 0.09F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_sharp_tip_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(155, 58).addBox(-0.0744F, -0.1F, -0.1023F, 0.1488F, 1.9363F, 0.2046F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8688F, -2.418F, -1.7955F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_blade_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.2325F, -0.1F, -0.279F, 0.465F, 2.4914F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2246F, -0.8221F, -2.3711F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_proximal_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.3875F, -0.1F, -0.434F, 0.775F, 2.5716F, 0.868F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_2 = neck_claw.addOrReplaceChild("neck_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.775F, -1.24F, -0.434F, 0.18F, 0.2F, 0.14F));

        PartDefinition neck_claw_finger_2_sharp_tip_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(155, 58).addBox(-0.0744F, -0.1F, -0.1023F, 0.1488F, 1.5824F, 0.2046F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.403F, -3.0752F, -1.922F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition neck_claw_finger_2_blade_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.2325F, -0.1F, -0.279F, 0.465F, 2.0256F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.279F, -1.7682F, -0.6535F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition neck_claw_finger_2_proximal_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.3875F, -0.1F, -0.434F, 0.775F, 2.1057F, 0.868F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(177, 58).addBox(-2.4F, -0.15F, -4.5F, 4.8F, 1.1F, 5.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, -16.5F, 0.06F, 0.0F, 0.0F));

        PartDefinition hump = body.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(212, 18).addBox(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -10.0F));

        PartDefinition hump_claw = hump.addOrReplaceChild("hump_claw", CubeListBuilder.create().texOffs(72, 58).addBox(-1.87F, -1.54F, -1.65F, 3.74F, 3.08F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -3.0F, -0.5F, 0.12F, 0.1F, -0.38F));

        PartDefinition hump_claw_palm_r1 = hump_claw.addOrReplaceChild("hump_claw_palm_r1", CubeListBuilder.create().texOffs(89, 58).addBox(-1.32F, -0.1F, -1.1F, 2.64F, 2.3245F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.55F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition hump_claw_finger_0 = hump_claw.addOrReplaceChild("hump_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.375F, -2.2F, -0.77F, 0.0F, -0.2F, -0.14F));

        PartDefinition hump_claw_finger_0_sharp_tip_r1 = hump_claw_finger_0.addOrReplaceChild("hump_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.715F, -5.456F, -3.41F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition hump_claw_finger_0_blade_r1 = hump_claw_finger_0.addOrReplaceChild("hump_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(111, 58).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.495F, -3.1372F, -1.1594F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition hump_claw_finger_0_proximal_r1 = hump_claw_finger_0.addOrReplaceChild("hump_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(102, 58).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition hump_claw_finger_1 = hump_claw.addOrReplaceChild("hump_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.2F, -0.77F, 0.09F, 0.0F, 0.0F));

        PartDefinition hump_claw_finger_1_sharp_tip_r1 = hump_claw_finger_1.addOrReplaceChild("hump_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(111, 58).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 3.2804F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.864F, -4.29F, -1.7955F, 0.0F, 0.0F));

        PartDefinition hump_claw_finger_1_blade_r1 = hump_claw_finger_1.addOrReplaceChild("hump_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(130, 58).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 4.2653F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9468F, -1.4586F, -2.3711F, 0.0F, 0.0F));

        PartDefinition hump_claw_finger_1_proximal_r1 = hump_claw_finger_1.addOrReplaceChild("hump_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(121, 58).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 4.4077F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition hump_claw_finger_2 = hump_claw.addOrReplaceChild("hump_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.375F, -2.2F, -0.77F, 0.18F, 0.2F, 0.14F));

        PartDefinition hump_claw_finger_2_sharp_tip_r1 = hump_claw_finger_2.addOrReplaceChild("hump_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.715F, -5.456F, -3.41F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition hump_claw_finger_2_blade_r1 = hump_claw_finger_2.addOrReplaceChild("hump_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(111, 58).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.495F, -3.1372F, -1.1594F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition hump_claw_finger_2_proximal_r1 = hump_claw_finger_2.addOrReplaceChild("hump_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(102, 58).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 58).addBox(-1.5F, 0.0F, -0.04F, 3.0F, 14.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 3.5F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(9, 58).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 11.17F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.9F, -11.0F, -10.5F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(30, 58).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 9.63F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.92F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(51, 58).addBox(-2.5F, -0.25F, -2.5F, 5.0F, 0.95F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.38F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition cat() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
        .texOffs(21, 0).addBox(-1.5F, -0.001F, -4.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(32, 0).addBox(-2.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(39, 0).addBox(1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -9.0F));

        PartDefinition tail1 = corruption_root.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(46, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 8.0F, 0.9F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(51, 0).addBox(-0.48F, -0.18F, 0.02F, 0.96F, 8.16F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.9F, 0.0F, 0.0F));

        PartDefinition tail_torn_tip_r1 = tail2.addOrReplaceChild("tail_torn_tip_r1", CubeListBuilder.create().texOffs(161, 0).addBox(-0.25F, -0.1F, -0.275F, 0.5F, 2.1423F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.7F, 0.0F, -0.1812F, -0.0094F, -0.1032F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.45F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.2F, 2.1F, -5.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(65, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 4.35F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(74, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(83, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 3.37F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.1F, 6.0F, 5.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(92, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 2.43F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.12F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(83, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 3.37F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.1F, 6.0F, 5.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(92, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 2.43F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.12F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(110, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(152, 0).addBox(1.6F, 9.0F, -6.0F, 0.6F, 3.1F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(131, 0).addBox(-0.765F, -0.63F, -0.675F, 1.53F, 1.26F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6F, 5.0F, -6.2F, -1.5708F, 0.18F, 0.4F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.54F, -0.1F, -0.45F, 1.08F, 1.0691F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.225F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5625F, -0.9F, -0.315F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.054F, -0.1F, -0.0742F, 0.108F, 1.2034F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2925F, -2.232F, -1.395F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.525F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2025F, -1.2834F, -0.4743F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.5831F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.9F, -0.315F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.054F, -0.1F, -0.0742F, 0.108F, 1.4602F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.808F, -1.755F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.1687F, -0.1F, -0.2025F, 0.3375F, 1.8631F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6146F, -0.5967F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.9213F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5625F, -0.9F, -0.315F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.054F, -0.1F, -0.0742F, 0.108F, 1.2034F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2925F, -2.232F, -1.395F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.525F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2025F, -1.2834F, -0.4743F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.5831F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.45F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.2F, 2.1F, -5.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(65, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 4.35F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(74, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition cave_spider() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -3.0F));

        PartDefinition cave_mandible_left_r1 = head.addOrReplaceChild("cave_mandible_left_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.5F, -0.1F, -0.55F, 1.0F, 3.6496F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2F, 2.0F, -7.0F, -0.8136F, 0.0757F, 0.1753F));

        PartDefinition cave_mandible_right_r1 = head.addOrReplaceChild("cave_mandible_right_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.5F, -0.1F, -0.55F, 1.0F, 3.6496F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 2.0F, -7.0F, -0.8136F, -0.0757F, -0.1753F));

        PartDefinition body0 = corruption_root.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(33, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition body1 = corruption_root.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(58, 0).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 9.0F));

        PartDefinition abdomen_claw = body1.addOrReplaceChild("abdomen_claw", CubeListBuilder.create().texOffs(125, 0).addBox(-1.462F, -1.204F, -1.29F, 2.924F, 2.408F, 2.58F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.8F, 2.0F, 0.2F, 0.25F, -0.5F));

        PartDefinition abdomen_claw_palm_r1 = abdomen_claw.addOrReplaceChild("abdomen_claw_palm_r1", CubeListBuilder.create().texOffs(138, 0).addBox(-1.032F, -0.1F, -0.86F, 2.064F, 1.8609F, 1.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.43F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition abdomen_claw_finger_0 = abdomen_claw.addOrReplaceChild("abdomen_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.075F, -1.72F, -0.602F, 0.0F, -0.2F, -0.14F));

        PartDefinition abdomen_claw_finger_0_sharp_tip_r1 = abdomen_claw_finger_0.addOrReplaceChild("abdomen_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 0).addBox(-0.1032F, -0.1F, -0.1419F, 0.2064F, 2.1176F, 0.2838F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.559F, -4.2656F, -2.666F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition abdomen_claw_finger_0_blade_r1 = abdomen_claw_finger_0.addOrReplaceChild("abdomen_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(158, 0).addBox(-0.3225F, -0.1F, -0.387F, 0.645F, 2.7322F, 0.774F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.387F, -2.4527F, -0.9064F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition abdomen_claw_finger_0_proximal_r1 = abdomen_claw_finger_0.addOrReplaceChild("abdomen_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(149, 0).addBox(-0.5375F, -0.1F, -0.602F, 1.075F, 2.8433F, 1.204F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition abdomen_claw_finger_1 = abdomen_claw.addOrReplaceChild("abdomen_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.72F, -0.602F, 0.09F, 0.0F, 0.0F));

        PartDefinition abdomen_claw_finger_1_sharp_tip_r1 = abdomen_claw_finger_1.addOrReplaceChild("abdomen_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 0).addBox(-0.1032F, -0.1F, -0.1419F, 0.2064F, 2.6083F, 0.2838F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.3664F, -3.354F, -1.7955F, 0.0F, 0.0F));

        PartDefinition abdomen_claw_finger_1_blade_r1 = abdomen_claw_finger_1.addOrReplaceChild("abdomen_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.3225F, -0.1F, -0.387F, 0.645F, 3.3783F, 0.774F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0857F, -1.1404F, -2.3711F, 0.0F, 0.0F));

        PartDefinition abdomen_claw_finger_1_proximal_r1 = abdomen_claw_finger_1.addOrReplaceChild("abdomen_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(163, 0).addBox(-0.5375F, -0.1F, -0.602F, 1.075F, 3.4897F, 1.204F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition abdomen_claw_finger_2 = abdomen_claw.addOrReplaceChild("abdomen_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.075F, -1.72F, -0.602F, 0.18F, 0.2F, 0.14F));

        PartDefinition abdomen_claw_finger_2_sharp_tip_r1 = abdomen_claw_finger_2.addOrReplaceChild("abdomen_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 0).addBox(-0.1032F, -0.1F, -0.1419F, 0.2064F, 2.1176F, 0.2838F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.559F, -4.2656F, -2.666F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition abdomen_claw_finger_2_blade_r1 = abdomen_claw_finger_2.addOrReplaceChild("abdomen_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(158, 0).addBox(-0.3225F, -0.1F, -0.387F, 0.645F, 2.7322F, 0.774F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.387F, -2.4527F, -0.9064F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition abdomen_claw_finger_2_proximal_r1 = abdomen_claw_finger_2.addOrReplaceChild("abdomen_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(149, 0).addBox(-0.5375F, -0.1F, -0.602F, 1.075F, 2.8433F, 1.204F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 3.0F, 2.0F, 0.0F, 0.72F, 0.0F));

        PartDefinition right_hind_leg_native_upper_r1 = right_hind_leg.addOrReplaceChild("right_hind_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.9296F));

        PartDefinition right_hind_leg_knee = right_hind_leg.addOrReplaceChild("right_hind_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, 0.0F));

        PartDefinition right_hind_leg_native_lower_r1 = right_hind_leg_knee.addOrReplaceChild("right_hind_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3948F));

        PartDefinition right_middle_hind_leg = corruption_root.addOrReplaceChild("right_middle_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 3.0F, 1.0F, 0.0F, 0.24F, 0.0F));

        PartDefinition right_middle_hind_leg_native_upper_r1 = right_middle_hind_leg.addOrReplaceChild("right_middle_hind_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.9296F));

        PartDefinition right_middle_hind_leg_knee = right_middle_hind_leg.addOrReplaceChild("right_middle_hind_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, 0.0F));

        PartDefinition right_middle_hind_leg_native_lower_r1 = right_middle_hind_leg_knee.addOrReplaceChild("right_middle_hind_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3948F));

        PartDefinition right_middle_front_leg = corruption_root.addOrReplaceChild("right_middle_front_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 3.0F, 0.0F, 0.0F, -0.24F, 0.0F));

        PartDefinition right_middle_front_leg_native_upper_r1 = right_middle_front_leg.addOrReplaceChild("right_middle_front_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.9296F));

        PartDefinition right_middle_front_leg_knee = right_middle_front_leg.addOrReplaceChild("right_middle_front_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, 0.0F));

        PartDefinition right_middle_front_leg_native_lower_r1 = right_middle_front_leg_knee.addOrReplaceChild("right_middle_front_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3948F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 3.0F, -1.0F, 0.0F, -0.72F, 0.0F));

        PartDefinition right_front_leg_native_upper_r1 = right_front_leg.addOrReplaceChild("right_front_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.9296F));

        PartDefinition right_front_leg_knee = right_front_leg.addOrReplaceChild("right_front_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, 0.0F));

        PartDefinition right_front_leg_native_lower_r1 = right_front_leg_knee.addOrReplaceChild("right_front_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3948F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 3.0F, 2.0F, 0.0F, 0.72F, 0.0F));

        PartDefinition left_hind_leg_native_upper_r1 = left_hind_leg.addOrReplaceChild("left_hind_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.9296F));

        PartDefinition left_hind_leg_knee = left_hind_leg.addOrReplaceChild("left_hind_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, 0.0F));

        PartDefinition left_hind_leg_native_lower_r1 = left_hind_leg_knee.addOrReplaceChild("left_hind_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3948F));

        PartDefinition left_middle_hind_leg = corruption_root.addOrReplaceChild("left_middle_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 3.0F, 1.0F, 0.0F, 0.24F, 0.0F));

        PartDefinition left_middle_hind_leg_native_upper_r1 = left_middle_hind_leg.addOrReplaceChild("left_middle_hind_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.9296F));

        PartDefinition left_middle_hind_leg_knee = left_middle_hind_leg.addOrReplaceChild("left_middle_hind_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, 0.0F));

        PartDefinition left_middle_hind_leg_native_lower_r1 = left_middle_hind_leg_knee.addOrReplaceChild("left_middle_hind_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3948F));

        PartDefinition left_middle_front_leg = corruption_root.addOrReplaceChild("left_middle_front_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 3.0F, 0.0F, 0.0F, -0.24F, 0.0F));

        PartDefinition left_middle_front_leg_native_upper_r1 = left_middle_front_leg.addOrReplaceChild("left_middle_front_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.9296F));

        PartDefinition left_middle_front_leg_knee = left_middle_front_leg.addOrReplaceChild("left_middle_front_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, 0.0F));

        PartDefinition left_middle_front_leg_native_lower_r1 = left_middle_front_leg_knee.addOrReplaceChild("left_middle_front_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3948F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 3.0F, -1.0F, 0.0F, -0.72F, 0.0F));

        PartDefinition left_front_leg_native_upper_r1 = left_front_leg.addOrReplaceChild("left_front_leg_native_upper_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-1.05F, -0.1F, -1.1F, 2.1F, 8.744F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.9296F));

        PartDefinition left_front_leg_knee = left_front_leg.addOrReplaceChild("left_front_leg_knee", CubeListBuilder.create().texOffs(125, 0).addBox(-1.35F, -1.2F, -1.3F, 2.7F, 2.4F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, 0.0F));

        PartDefinition left_front_leg_native_lower_r1 = left_front_leg_knee.addOrReplaceChild("left_front_leg_native_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.9F, -0.1F, -0.95F, 1.8F, 13.2F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3948F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition cod() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition nose = corruption_root.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.0F));

        PartDefinition left_fin = corruption_root.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(18, 0).addBox(0.0F, -0.04F, -1.0F, 2.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition top_fin = corruption_root.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(27, 0).addBox(-0.04F, -1.0F, -1.0F, 0.08F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition right_fin = corruption_root.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(42, 0).addBox(-2.0F, -0.04F, -1.0F, 2.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 11.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(51, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(96, 0).addBox(-1.08F, -0.6F, 0.4F, 0.2F, 1.8F, 1.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition dorsal_claw = body.addOrReplaceChild("dorsal_claw", CubeListBuilder.create().texOffs(81, 0).addBox(-0.374F, -0.308F, -0.33F, 0.748F, 0.616F, 0.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.3F, 3.5F, 0.15F, 0.25F, -0.4F));

        PartDefinition dorsal_claw_palm_r1 = dorsal_claw.addOrReplaceChild("dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(81, 0).addBox(-0.264F, -0.1F, -0.22F, 0.528F, 0.6249F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.11F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_0 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.275F, -0.44F, -0.154F, 0.0F, -0.2F, -0.14F));

        PartDefinition dorsal_claw_finger_0_sharp_tip_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.6905F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.143F, -1.0912F, -0.682F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition dorsal_claw_finger_0_blade_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 0.8478F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.099F, -0.6274F, -0.2319F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition dorsal_claw_finger_0_proximal_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 0.8762F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition dorsal_claw_finger_1 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.44F, -0.154F, 0.09F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_sharp_tip_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.8161F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3728F, -0.858F, -1.7955F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_blade_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 1.0131F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7894F, -0.2917F, -2.3711F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_proximal_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 1.0415F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_2 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.275F, -0.44F, -0.154F, 0.18F, 0.2F, 0.14F));

        PartDefinition dorsal_claw_finger_2_sharp_tip_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.6905F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.143F, -1.0912F, -0.682F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition dorsal_claw_finger_2_blade_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 0.8478F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.099F, -0.6274F, -0.2319F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition dorsal_claw_finger_2_proximal_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 0.8762F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail_fin = corruption_root.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(70, 0).addBox(-0.04F, -2.0F, 0.0F, 0.08F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 7.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition dolphin() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F, new CubeDeformation(0.0F))
        .texOffs(209, 0).addBox(-4.15F, -4.0F, 3.0F, 0.45F, 2.8F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -5.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(43, 0).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -3.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(222, 0).addBox(-0.95F, 1.0F, -5.0F, 1.9F, 0.8F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.06F, 0.0F, 0.0F));

        PartDefinition left_fin = body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(85, 0).mirror().addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -2.0F, 4.0F, 1.0472F, 0.0F, 2.0944F));

        PartDefinition right_fin = body.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(85, 0).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, 4.0F, 1.0472F, 0.0F, -2.0944F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(102, 0).addBox(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5F, 11.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition tail_fin = tail.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(133, 0).addBox(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 9.0F));

        PartDefinition back_fin = body.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(166, 0).addBox(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition dorsal_claw = body.addOrReplaceChild("dorsal_claw", CubeListBuilder.create().texOffs(179, 0).addBox(-1.071F, -0.882F, -0.945F, 2.142F, 1.764F, 1.89F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, -5.5F, 6.0F, 0.18F, 0.18F, -0.48F));

        PartDefinition dorsal_claw_palm_r1 = dorsal_claw.addOrReplaceChild("dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(190, 0).addBox(-0.756F, -0.1F, -0.63F, 1.512F, 1.4167F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.315F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_0 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.7875F, -1.26F, -0.441F, 0.0F, -0.2F, -0.14F));

        PartDefinition dorsal_claw_finger_0_sharp_tip_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.0756F, -0.1F, -0.1039F, 0.1512F, 1.6047F, 0.2079F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4095F, -3.1248F, -1.953F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition dorsal_claw_finger_0_blade_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(199, 0).addBox(-0.2363F, -0.1F, -0.2835F, 0.4725F, 2.055F, 0.567F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2835F, -1.7968F, -0.664F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition dorsal_claw_finger_0_proximal_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(199, 0).addBox(-0.3938F, -0.1F, -0.441F, 0.7875F, 2.1364F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition dorsal_claw_finger_1 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.26F, -0.441F, 0.09F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_sharp_tip_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.0756F, -0.1F, -0.104F, 0.1512F, 1.9643F, 0.2079F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9312F, -2.457F, -1.7955F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_blade_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(199, 0).addBox(-0.2363F, -0.1F, -0.2835F, 0.4725F, 2.5283F, 0.567F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2604F, -0.8354F, -2.3711F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_proximal_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(199, 0).addBox(-0.3937F, -0.1F, -0.441F, 0.7875F, 2.6099F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_2 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.7875F, -1.26F, -0.441F, 0.18F, 0.2F, 0.14F));

        PartDefinition dorsal_claw_finger_2_sharp_tip_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.0756F, -0.1F, -0.1039F, 0.1512F, 1.6047F, 0.2079F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4095F, -3.1248F, -1.953F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition dorsal_claw_finger_2_blade_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(199, 0).addBox(-0.2363F, -0.1F, -0.2835F, 0.4725F, 2.055F, 0.567F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2835F, -1.7968F, -0.664F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition dorsal_claw_finger_2_proximal_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(199, 0).addBox(-0.3937F, -0.1F, -0.441F, 0.7875F, 2.1364F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition drowned() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 7.2F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(0, 29).addBox(-0.65F, -1.0F, 2.8F, 1.3F, 1.2F, 1.05F, new CubeDeformation(0.0F))
        .texOffs(10, 29).addBox(2.5F, -1.0F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(16, 29).addBox(1.156F, -1.0F, -4.15F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(22, 29).addBox(-0.188F, -1.0F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(28, 29).addBox(-1.532F, -1.0F, -4.15F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(34, 29).addBox(-2.876F, -1.0F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, 0.8F, -6.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(40, 29).addBox(2.15F, 0.0F, -6.1F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(46, 29).addBox(0.502F, -0.2F, -6.1F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(52, 29).addBox(-1.146F, 0.0F, -6.1F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(58, 29).addBox(-2.794F, -0.2F, -6.1F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, 2.0F, 0.05F, 0.0F, 0.0F));

        PartDefinition gill_claw = head.addOrReplaceChild("gill_claw", CubeListBuilder.create().texOffs(46, 17).addBox(-1.19F, -0.98F, -1.05F, 2.38F, 1.96F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -3.7F, 2.5F, 0.0873F, 0.1396F, -0.3142F));

        PartDefinition gill_claw_palm_r1 = gill_claw.addOrReplaceChild("gill_claw_palm_r1", CubeListBuilder.create().texOffs(59, 17).addBox(-0.84F, -0.1F, -0.7F, 1.68F, 1.5519F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.35F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition gill_claw_finger_0 = gill_claw.addOrReplaceChild("gill_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.875F, -1.4F, -0.49F, 0.0F, -0.2F, -0.14F));

        PartDefinition gill_claw_finger_0_sharp_tip_r1 = gill_claw_finger_0.addOrReplaceChild("gill_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(68, 17).addBox(-0.084F, -0.1F, -0.1155F, 0.168F, 1.7608F, 0.231F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.455F, -3.472F, -2.17F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition gill_claw_finger_0_blade_r1 = gill_claw_finger_0.addOrReplaceChild("gill_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.2625F, -0.1F, -0.315F, 0.525F, 2.2611F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.315F, -1.9964F, -0.7378F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition gill_claw_finger_0_proximal_r1 = gill_claw_finger_0.addOrReplaceChild("gill_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.4375F, -0.1F, -0.49F, 0.875F, 2.3515F, 0.98F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition gill_claw_finger_1 = gill_claw.addOrReplaceChild("gill_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.4F, -0.49F, 0.09F, 0.0F, 0.0F));

        PartDefinition gill_claw_finger_1_sharp_tip_r1 = gill_claw_finger_1.addOrReplaceChild("gill_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.084F, -0.1F, -0.1155F, 0.168F, 2.1603F, 0.231F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.368F, -2.73F, -1.7955F, 0.0F, 0.0F));

        PartDefinition gill_claw_finger_1_blade_r1 = gill_claw_finger_1.addOrReplaceChild("gill_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.2625F, -0.1F, -0.315F, 0.525F, 2.787F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5116F, -0.9282F, -2.3711F, 0.0F, 0.0F));

        PartDefinition gill_claw_finger_1_proximal_r1 = gill_claw_finger_1.addOrReplaceChild("gill_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.4375F, -0.1F, -0.49F, 0.875F, 2.8776F, 0.98F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition gill_claw_finger_2 = gill_claw.addOrReplaceChild("gill_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.875F, -1.4F, -0.49F, 0.18F, 0.2F, 0.14F));

        PartDefinition gill_claw_finger_2_sharp_tip_r1 = gill_claw_finger_2.addOrReplaceChild("gill_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(68, 17).addBox(-0.084F, -0.1F, -0.1155F, 0.168F, 1.7608F, 0.231F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.455F, -3.472F, -2.17F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition gill_claw_finger_2_blade_r1 = gill_claw_finger_2.addOrReplaceChild("gill_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.2625F, -0.1F, -0.315F, 0.525F, 2.2611F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.315F, -1.9964F, -0.7378F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition gill_claw_finger_2_proximal_r1 = gill_claw_finger_2.addOrReplaceChild("gill_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.4375F, -0.1F, -0.49F, 0.875F, 2.3515F, 0.98F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(71, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(71, 0).addBox(-3.0F, -0.25F, -2.0F, 4.0F, 6.01F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(88, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(105, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(122, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(139, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(139, 0).addBox(-1.0F, -0.25F, -2.0F, 4.0F, 6.01F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(156, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(173, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(190, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(207, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(73, 17).addBox(-4.25F, 3.4F, -1.7F, 1.1F, 5.5F, 3.3F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(232, 0).addBox(-2.125F, -1.75F, -1.875F, 4.25F, 3.5F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, 1.3F, 2.2F, 0.0873F, -0.1047F, 0.3142F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, -0.1F, -1.25F, 3.0F, 2.6142F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.625F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5625F, -2.5F, -0.875F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.15F, -0.1F, -0.2062F, 0.3F, 2.9872F, 0.4125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8125F, -6.2F, -3.875F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(22, 17).addBox(-0.4688F, -0.1F, -0.5625F, 0.9375F, 3.8806F, 1.125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5625F, -3.565F, -1.3175F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(13, 17).addBox(-0.7812F, -0.1F, -0.875F, 1.5625F, 4.0421F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.5F, -0.875F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(41, 17).addBox(-0.15F, -0.1F, -0.2062F, 0.3F, 3.7005F, 0.4125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.8F, -4.875F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.4688F, -0.1F, -0.5625F, 0.9375F, 4.8197F, 1.125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.485F, -1.6575F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(13, 17).addBox(-0.7813F, -0.1F, -0.875F, 1.5625F, 4.9815F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5625F, -2.5F, -0.875F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.15F, -0.1F, -0.2062F, 0.3F, 2.9872F, 0.4125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8125F, -6.2F, -3.875F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(22, 17).addBox(-0.4688F, -0.1F, -0.5625F, 0.9375F, 3.8806F, 1.125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5625F, -3.565F, -1.3175F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(13, 17).addBox(-0.7813F, -0.1F, -0.875F, 1.5625F, 4.0421F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition elder_guardian() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -8.0F, 12.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(57, 0).addBox(-8.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(57, 0).mirror().addBox(6.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(86, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(86, 0).addBox(-6.0F, 6.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(376, 0).addBox(-2.4F, -2.5F, -8.08F, 4.8F, 4.8F, 0.24F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition spike10 = head.addOrReplaceChild("spike10", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9329F, 7.9329F, 0.0F, 0.0F, 0.0F, 2.3562F));

        PartDefinition spike11 = head.addOrReplaceChild("spike11", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0004F, 8.0004F, 0.0F, 0.0F, 0.0F, 3.927F));

        PartDefinition spike6 = head.addOrReplaceChild("spike6", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0768F, 0.0F, 8.0768F, 1.5708F, 3.927F, 0.0F));

        PartDefinition spike7 = head.addOrReplaceChild("spike7", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0603F, 0.0F, 8.0603F, 1.5708F, 2.3562F, 0.0F));

        PartDefinition spike8 = head.addOrReplaceChild("spike8", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.9884F, 7.9884F, 3.927F, 0.0F, 0.0F));

        PartDefinition spike9 = head.addOrReplaceChild("spike9", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.9271F, -7.9271F, 2.3562F, 0.0F, 0.0F));

        PartDefinition tail0 = head.addOrReplaceChild("tail0", CubeListBuilder.create().texOffs(148, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

        PartDefinition tail1 = tail0.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(173, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(194, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(211, 0).addBox(0.0F, -4.5F, 3.0F, 1.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));

        PartDefinition tail_claw = tail1.addOrReplaceChild("tail_claw", CubeListBuilder.create().texOffs(362, 0).addBox(-0.935F, -0.77F, -0.825F, 1.87F, 1.54F, 1.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, -0.8F, 3.6F, 0.1F, 0.2F, -0.55F));

        PartDefinition tail_claw_palm_r1 = tail_claw.addOrReplaceChild("tail_claw_palm_r1", CubeListBuilder.create().texOffs(362, 0).addBox(-0.66F, -0.1F, -0.55F, 1.32F, 1.2622F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.275F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_0 = tail_claw.addOrReplaceChild("tail_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.6875F, -1.1F, -0.385F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_finger_0_sharp_tip_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.066F, -0.1F, -0.0907F, 0.132F, 1.4264F, 0.1815F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3575F, -2.728F, -1.705F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_finger_0_blade_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.2063F, -0.1F, -0.2475F, 0.4125F, 1.8194F, 0.495F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2475F, -1.5686F, -0.5797F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_finger_0_proximal_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.3438F, -0.1F, -0.385F, 0.6875F, 1.8905F, 0.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_finger_1 = tail_claw.addOrReplaceChild("tail_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, -0.385F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_sharp_tip_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.066F, -0.1F, -0.0907F, 0.132F, 1.7402F, 0.1815F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.432F, -2.145F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_blade_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(357, 0).addBox(-0.2063F, -0.1F, -0.2475F, 0.4125F, 2.2327F, 0.495F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.9734F, -0.7293F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_proximal_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(357, 0).addBox(-0.3438F, -0.1F, -0.385F, 0.6875F, 2.3039F, 0.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_2 = tail_claw.addOrReplaceChild("tail_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.6875F, -1.1F, -0.385F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_finger_2_sharp_tip_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.066F, -0.1F, -0.0907F, 0.132F, 1.4264F, 0.1815F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3575F, -2.728F, -1.705F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_finger_2_blade_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.2063F, -0.1F, -0.2475F, 0.4125F, 1.8194F, 0.495F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2475F, -1.5686F, -0.5797F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_finger_2_proximal_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(371, 0).addBox(-0.3438F, -0.1F, -0.385F, 0.6875F, 1.8905F, 0.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition spike0 = head.addOrReplaceChild("spike0", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.08F, 8.08F, 5.4978F, 0.0F, 0.0F));

        PartDefinition spike1 = head.addOrReplaceChild("spike1", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0432F, -8.0432F, 0.7854F, 0.0F, 0.0F));

        PartDefinition spike2 = head.addOrReplaceChild("spike2", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9667F, -7.9667F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition spike3 = head.addOrReplaceChild("spike3", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9208F, -7.9208F, 0.0F, 0.0F, 0.0F, 5.4978F));

        PartDefinition spike4 = head.addOrReplaceChild("spike4", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9477F, 0.0F, -7.9477F, 1.5708F, 0.7854F, 0.0F));

        PartDefinition spike5 = head.addOrReplaceChild("spike5", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0227F, 0.0F, -8.0227F, 1.5708F, 5.4978F, 0.0F));

        PartDefinition dorsal_claw = head.addOrReplaceChild("dorsal_claw", CubeListBuilder.create().texOffs(232, 0).addBox(-2.465F, -2.03F, -2.175F, 4.93F, 4.06F, 4.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -6.0F, 2.2F, 0.14F, 0.18F, -0.48F));

        PartDefinition dorsal_claw_palm_r1 = dorsal_claw.addOrReplaceChild("dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(253, 0).addBox(-1.74F, -0.1F, -1.45F, 3.48F, 3.0004F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.725F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_0 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.8125F, -2.9F, -1.015F, 0.0F, -0.2F, -0.14F));

        PartDefinition dorsal_claw_finger_0_sharp_tip_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(288, 0).addBox(-0.174F, -0.1F, -0.2393F, 0.348F, 3.4331F, 0.4785F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9425F, -7.192F, -4.495F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition dorsal_claw_finger_0_blade_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(279, 0).addBox(-0.5437F, -0.1F, -0.6525F, 1.0875F, 4.4695F, 1.305F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6525F, -4.1354F, -1.5283F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition dorsal_claw_finger_0_proximal_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(268, 0).addBox(-0.9063F, -0.1F, -1.015F, 1.8125F, 4.6568F, 2.03F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition dorsal_claw_finger_1 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.9F, -1.015F, 0.09F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_sharp_tip_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(313, 0).addBox(-0.174F, -0.1F, -0.2393F, 0.348F, 4.2606F, 0.4785F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.048F, -5.655F, -1.7955F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_blade_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(304, 0).addBox(-0.5437F, -0.1F, -0.6525F, 1.0875F, 5.5588F, 1.305F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.2026F, -1.9227F, -2.3711F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_proximal_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(293, 0).addBox(-0.9063F, -0.1F, -1.015F, 1.8125F, 5.7465F, 2.03F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_2 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.8125F, -2.9F, -1.015F, 0.18F, 0.2F, 0.14F));

        PartDefinition dorsal_claw_finger_2_sharp_tip_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(288, 0).addBox(-0.174F, -0.1F, -0.2393F, 0.348F, 3.4331F, 0.4785F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9425F, -7.192F, -4.495F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition dorsal_claw_finger_2_blade_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(279, 0).addBox(-0.5438F, -0.1F, -0.6525F, 1.0875F, 4.4695F, 1.305F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6525F, -4.1354F, -1.5283F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition dorsal_claw_finger_2_proximal_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(268, 0).addBox(-0.9063F, -0.1F, -1.015F, 1.8125F, 4.6568F, 2.03F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition flank_claw = head.addOrReplaceChild("flank_claw", CubeListBuilder.create().texOffs(318, 0).addBox(-1.802F, -1.484F, -1.59F, 3.604F, 2.968F, 3.18F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -0.5F, -0.2F, 0.28F, -0.22F, 0.9F));

        PartDefinition flank_claw_palm_r1 = flank_claw.addOrReplaceChild("flank_claw_palm_r1", CubeListBuilder.create().texOffs(335, 0).addBox(-1.272F, -0.1F, -1.06F, 2.544F, 2.2472F, 2.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.53F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_0 = flank_claw.addOrReplaceChild("flank_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.325F, -2.12F, -0.742F, 0.0F, -0.2F, -0.14F));

        PartDefinition flank_claw_finger_0_sharp_tip_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(357, 0).addBox(-0.1272F, -0.1F, -0.1749F, 0.2544F, 2.5635F, 0.3498F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.689F, -5.2576F, -3.286F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition flank_claw_finger_0_blade_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(288, 0).addBox(-0.3975F, -0.1F, -0.477F, 0.795F, 3.3211F, 0.954F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.477F, -3.0231F, -1.1172F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition flank_claw_finger_0_proximal_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(348, 0).addBox(-0.6625F, -0.1F, -0.742F, 1.325F, 3.4581F, 1.484F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition flank_claw_finger_1 = flank_claw.addOrReplaceChild("flank_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.12F, -0.742F, 0.09F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_sharp_tip_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(288, 0).addBox(-0.1272F, -0.1F, -0.1749F, 0.2544F, 3.1684F, 0.3498F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.6144F, -4.134F, -1.7955F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_blade_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(313, 0).addBox(-0.3975F, -0.1F, -0.477F, 0.795F, 4.1175F, 0.954F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8033F, -1.4056F, -2.3711F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_proximal_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(279, 0).addBox(-0.6625F, -0.1F, -0.742F, 1.325F, 4.2547F, 1.484F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_2 = flank_claw.addOrReplaceChild("flank_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.325F, -2.12F, -0.742F, 0.18F, 0.2F, 0.14F));

        PartDefinition flank_claw_finger_2_sharp_tip_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(357, 0).addBox(-0.1272F, -0.1F, -0.1749F, 0.2544F, 2.5635F, 0.3498F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.689F, -5.2576F, -3.286F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition flank_claw_finger_2_blade_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(288, 0).addBox(-0.3975F, -0.1F, -0.477F, 0.795F, 3.3211F, 0.954F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.477F, -3.0231F, -1.1172F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition flank_claw_finger_2_proximal_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(348, 0).addBox(-0.6625F, -0.1F, -0.742F, 1.325F, 3.4581F, 1.484F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition fox() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 4.5F, -3.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(73, 0).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(80, 0).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(87, 0).addBox(1.999F, 0.499F, -1.001F, 2.002F, 3.372F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 5.5F, 0.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(96, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 2.432F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(105, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 0.952F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(87, 0).addBox(1.999F, 0.499F, -1.001F, 2.002F, 3.372F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 5.5F, 7.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(96, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 2.432F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(105, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 0.952F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(114, 0).addBox(1.999F, 0.499F, -1.001F, 2.002F, 3.372F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 5.5F, 7.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(123, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 2.432F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(132, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 0.952F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(141, 0).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(215, 0).addBox(2.6F, 8.0F, -2.8F, 0.8F, 3.2F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -6.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(166, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.0524F, 0.0F, 0.0F));

        PartDefinition fox_tail_torn_tip_r1 = tail.addOrReplaceChild("fox_tail_torn_tip_r1", CubeListBuilder.create().texOffs(226, 0).addBox(-0.35F, -0.1F, -0.4F, 0.7F, 3.7637F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4F, 7.0F, -0.3F, -0.1692F, -0.0072F, -0.0843F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(185, 0).addBox(-1.105F, -0.91F, -0.975F, 2.21F, 1.82F, 1.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6F, 5.0F, -2.8F, -1.5708F, 0.18F, 0.45F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(196, 0).addBox(-0.78F, -0.1F, -0.65F, 1.56F, 1.4554F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.325F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.8125F, -1.3F, -0.455F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(210, 0).addBox(-0.078F, -0.1F, -0.1073F, 0.156F, 1.6493F, 0.2145F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4225F, -3.224F, -2.015F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.2437F, -0.1F, -0.2925F, 0.4875F, 2.1139F, 0.585F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2925F, -1.8538F, -0.6851F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.4063F, -0.1F, -0.455F, 0.8125F, 2.1979F, 0.91F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.3F, -0.455F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.078F, -0.1F, -0.1073F, 0.156F, 2.0203F, 0.2145F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.056F, -2.535F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.2437F, -0.1F, -0.2925F, 0.4875F, 2.6022F, 0.585F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.3322F, -0.8619F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.4063F, -0.1F, -0.455F, 0.8125F, 2.6864F, 0.91F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.8125F, -1.3F, -0.455F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(210, 0).addBox(-0.078F, -0.1F, -0.1073F, 0.156F, 1.6493F, 0.2145F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4225F, -3.224F, -2.015F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.2437F, -0.1F, -0.2925F, 0.4875F, 2.1139F, 0.585F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2925F, -1.8538F, -0.6851F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.4063F, -0.1F, -0.455F, 0.8125F, 2.1979F, 0.91F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(114, 0).addBox(1.999F, 0.499F, -1.001F, 2.002F, 3.372F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 5.5F, 0.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(123, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 2.432F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(132, 0).addBox(1.999F, -0.251F, -1.001F, 2.002F, 0.952F, 2.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition frog() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition root = corruption_root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(172, 13).addBox(-1.1F, -0.25F, -1.0F, 2.2F, 1.6F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -3.0F, 4.0F));

        PartDefinition left_foot = left_leg.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(15, 0).addBox(-4.0F, -0.03F, -4.0F, 8.0F, 0.08F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 3.0F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(172, 13).addBox(-1.1F, -0.25F, -1.0F, 2.2F, 1.6F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -3.0F, 4.0F));

        PartDefinition right_foot = right_leg.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(63, 0).addBox(-4.0F, -0.03F, -4.0F, 8.0F, 0.08F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 3.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, -2.0F, -8.0F, 7.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(129, 0).addBox(-3.5F, -1.04F, -8.0F, 7.0F, 0.08F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 4.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(162, 0).addBox(-3.5F, -1.04F, -7.0F, 7.0F, 0.08F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(195, 0).addBox(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -1.0F));

        PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.0F, 2.0F));

        PartDefinition right_eye = eyes.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(228, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -3.0F, -6.5F));

        PartDefinition left_eye = eyes.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(241, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -3.0F, -6.5F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -1.0F, -6.5F));

        PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(11, 13).addBox(-4.0F, -0.03F, -5.0F, 8.0F, 0.08F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition tongue = body.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(44, 13).addBox(-2.0F, -0.04F, -7.1F, 4.0F, 0.08F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.01F, 1.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(67, 13).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -1.0F, -6.5F));

        PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(78, 13).addBox(-4.0F, -0.03F, -4.0F, 8.0F, 0.08F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -1.0F));

        PartDefinition croaking_body = body.addOrReplaceChild("croaking_body", CubeListBuilder.create().texOffs(111, 13).addBox(-3.4F, 0.0F, -2.8F, 6.8F, 1.8F, 2.8F, new CubeDeformation(0.0F))
        .texOffs(153, 13).addBox(-2.8F, -0.3F, -2.8F, 5.6F, 1.5F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -5.0F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(132, 13).addBox(-0.782F, -0.644F, -0.69F, 1.564F, 1.288F, 1.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2F, -1.4F, -0.7F, 0.18F, 0.25F, -0.58F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(141, 13).addBox(-0.552F, -0.1F, -0.46F, 1.104F, 1.0884F, 0.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.23F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.575F, -0.92F, -0.322F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.0552F, -0.1F, -0.0759F, 0.1104F, 1.2257F, 0.1518F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.299F, -2.2816F, -1.426F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.1725F, -0.1F, -0.207F, 0.345F, 1.5544F, 0.414F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.207F, -1.3119F, -0.4848F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.2875F, -0.1F, -0.322F, 0.575F, 1.6139F, 0.644F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.92F, -0.322F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.0552F, -0.1F, -0.0759F, 0.1104F, 1.4882F, 0.1518F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.8704F, -1.794F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.1725F, -0.1F, -0.207F, 0.345F, 1.9F, 0.414F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6505F, -0.61F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.2875F, -0.1F, -0.322F, 0.575F, 1.9596F, 0.644F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.575F, -0.92F, -0.322F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.0552F, -0.1F, -0.0759F, 0.1104F, 1.2257F, 0.1518F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.299F, -2.2816F, -1.426F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.1725F, -0.1F, -0.207F, 0.345F, 1.5544F, 0.414F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.207F, -1.3119F, -0.4848F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(148, 13).addBox(-0.2875F, -0.1F, -0.322F, 0.575F, 1.6139F, 0.644F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition glow_squid() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition tentacle0 = corruption_root.addOrReplaceChild("tentacle0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition tentacle0_middle = tentacle0.addOrReplaceChild("tentacle0_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle0_tip = tentacle0_middle.addOrReplaceChild("tentacle0_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle1 = corruption_root.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5355F, 3.0F, 3.5355F, 0.0F, 0.7854F, 0.0F));

        PartDefinition tentacle1_middle = tentacle1.addOrReplaceChild("tentacle1_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle1_tip = tentacle1_middle.addOrReplaceChild("tentacle1_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle6 = corruption_root.addOrReplaceChild("tentacle6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -5.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition tentacle6_middle = tentacle6.addOrReplaceChild("tentacle6_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle6_tip = tentacle6_middle.addOrReplaceChild("tentacle6_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle7 = corruption_root.addOrReplaceChild("tentacle7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5355F, 3.0F, -3.5355F, 0.0F, -3.927F, 0.0F));

        PartDefinition tentacle7_middle = tentacle7.addOrReplaceChild("tentacle7_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle7_tip = tentacle7_middle.addOrReplaceChild("tentacle7_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(9, 0).addBox(-6.02F, -8.02F, -6.02F, 12.04F, 16.04F, 12.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition mantle_claw = body.addOrReplaceChild("mantle_claw", CubeListBuilder.create().texOffs(58, 0).addBox(-1.496F, -1.232F, -1.32F, 2.992F, 2.464F, 2.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2F, -3.9F, 2.2F, 0.12F, 0.2F, -0.65F));

        PartDefinition mantle_claw_palm_r1 = mantle_claw.addOrReplaceChild("mantle_claw_palm_r1", CubeListBuilder.create().texOffs(71, 0).addBox(-1.056F, -0.1F, -0.88F, 2.112F, 1.8996F, 1.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.44F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_0 = mantle_claw.addOrReplaceChild("mantle_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.1F, -1.76F, -0.616F, 0.0F, -0.2F, -0.14F));

        PartDefinition mantle_claw_finger_0_sharp_tip_r1 = mantle_claw_finger_0.addOrReplaceChild("mantle_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.1056F, -0.1F, -0.1452F, 0.2112F, 2.1622F, 0.2904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.572F, -4.3648F, -2.728F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition mantle_claw_finger_0_blade_r1 = mantle_claw_finger_0.addOrReplaceChild("mantle_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.33F, -0.1F, -0.396F, 0.66F, 2.7911F, 0.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.396F, -2.5098F, -0.9275F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition mantle_claw_finger_0_proximal_r1 = mantle_claw_finger_0.addOrReplaceChild("mantle_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(82, 0).addBox(-0.55F, -0.1F, -0.616F, 1.1F, 2.9048F, 1.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition mantle_claw_finger_1 = mantle_claw.addOrReplaceChild("mantle_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.76F, -0.616F, 0.09F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_1_sharp_tip_r1 = mantle_claw_finger_1.addOrReplaceChild("mantle_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.1056F, -0.1F, -0.1452F, 0.2112F, 2.6644F, 0.2904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.4912F, -3.432F, -1.7955F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_1_blade_r1 = mantle_claw_finger_1.addOrReplaceChild("mantle_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(105, 0).addBox(-0.33F, -0.1F, -0.396F, 0.66F, 3.4523F, 0.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.1574F, -1.1669F, -2.3711F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_1_proximal_r1 = mantle_claw_finger_1.addOrReplaceChild("mantle_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.55F, -0.1F, -0.616F, 1.1F, 3.5662F, 1.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_2 = mantle_claw.addOrReplaceChild("mantle_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.1F, -1.76F, -0.616F, 0.18F, 0.2F, 0.14F));

        PartDefinition mantle_claw_finger_2_sharp_tip_r1 = mantle_claw_finger_2.addOrReplaceChild("mantle_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.1056F, -0.1F, -0.1452F, 0.2112F, 2.1622F, 0.2904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.572F, -4.3648F, -2.728F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition mantle_claw_finger_2_blade_r1 = mantle_claw_finger_2.addOrReplaceChild("mantle_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.33F, -0.1F, -0.396F, 0.66F, 2.7911F, 0.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.396F, -2.5098F, -0.9275F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition mantle_claw_finger_2_proximal_r1 = mantle_claw_finger_2.addOrReplaceChild("mantle_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(82, 0).addBox(-0.55F, -0.1F, -0.616F, 1.1F, 2.9048F, 1.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition lower_mantle_claw = body.addOrReplaceChild("lower_mantle_claw", CubeListBuilder.create().texOffs(58, 0).addBox(-1.292F, -1.064F, -1.14F, 2.584F, 2.128F, 2.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.8F, 4.0F, 2.8F, 0.35F, -0.25F, 0.9F));

        PartDefinition lower_mantle_claw_palm_r1 = lower_mantle_claw.addOrReplaceChild("lower_mantle_claw_palm_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.912F, -0.1F, -0.76F, 1.824F, 1.6678F, 1.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.38F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition lower_mantle_claw_finger_0 = lower_mantle_claw.addOrReplaceChild("lower_mantle_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.95F, -1.52F, -0.532F, 0.0F, -0.2F, -0.14F));

        PartDefinition lower_mantle_claw_finger_0_sharp_tip_r1 = lower_mantle_claw_finger_0.addOrReplaceChild("lower_mantle_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.494F, -3.7696F, -2.356F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition lower_mantle_claw_finger_0_blade_r1 = lower_mantle_claw_finger_0.addOrReplaceChild("lower_mantle_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.342F, -2.1675F, -0.801F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition lower_mantle_claw_finger_0_proximal_r1 = lower_mantle_claw_finger_0.addOrReplaceChild("lower_mantle_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(119, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition lower_mantle_claw_finger_1 = lower_mantle_claw.addOrReplaceChild("lower_mantle_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.52F, -0.532F, 0.09F, 0.0F, 0.0F));

        PartDefinition lower_mantle_claw_finger_1_sharp_tip_r1 = lower_mantle_claw_finger_1.addOrReplaceChild("lower_mantle_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 2.3283F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.7424F, -2.964F, -1.7955F, 0.0F, 0.0F));

        PartDefinition lower_mantle_claw_finger_1_blade_r1 = lower_mantle_claw_finger_1.addOrReplaceChild("lower_mantle_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 3.0088F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7269F, -1.0078F, -2.3711F, 0.0F, 0.0F));

        PartDefinition lower_mantle_claw_finger_1_proximal_r1 = lower_mantle_claw_finger_1.addOrReplaceChild("lower_mantle_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 3.1071F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition lower_mantle_claw_finger_2 = lower_mantle_claw.addOrReplaceChild("lower_mantle_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.95F, -1.52F, -0.532F, 0.18F, 0.2F, 0.14F));

        PartDefinition lower_mantle_claw_finger_2_sharp_tip_r1 = lower_mantle_claw_finger_2.addOrReplaceChild("lower_mantle_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.494F, -3.7696F, -2.356F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition lower_mantle_claw_finger_2_blade_r1 = lower_mantle_claw_finger_2.addOrReplaceChild("lower_mantle_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.342F, -2.1675F, -0.801F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition lower_mantle_claw_finger_2_proximal_r1 = lower_mantle_claw_finger_2.addOrReplaceChild("lower_mantle_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(119, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tentacle4 = corruption_root.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition tentacle4_middle = tentacle4.addOrReplaceChild("tentacle4_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle4_tip = tentacle4_middle.addOrReplaceChild("tentacle4_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle5 = corruption_root.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5355F, 3.0F, -3.5355F, 0.0F, -2.3562F, 0.0F));

        PartDefinition tentacle5_middle = tentacle5.addOrReplaceChild("tentacle5_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle5_tip = tentacle5_middle.addOrReplaceChild("tentacle5_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle2 = corruption_root.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 5.0F));

        PartDefinition tentacle2_middle = tentacle2.addOrReplaceChild("tentacle2_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle2_tip = tentacle2_middle.addOrReplaceChild("tentacle2_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle3 = corruption_root.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5355F, 3.0F, 3.5355F, 0.0F, -0.7854F, 0.0F));

        PartDefinition tentacle3_middle = tentacle3.addOrReplaceChild("tentacle3_middle", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(138, 0).addBox(-1.18F, -0.8F, -1.18F, 2.36F, 1.6F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition tentacle3_tip = tentacle3_middle.addOrReplaceChild("tentacle3_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.4F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition goat() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -11.0F, -10.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(0, 0).mirror().addBox(2.0F, -11.0F, -10.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(9, 0).mirror().addBox(-0.54F, -3.0F, -14.0F, 0.08F, 7.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, 2.0F, 0.0F));

        PartDefinition goat_beard_tendon_r1 = head.addOrReplaceChild("goat_beard_tendon_r1", CubeListBuilder.create().texOffs(149, 28).addBox(-0.3F, -0.1F, -0.325F, 0.6F, 6.2836F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -11.0F, -0.1651F, -0.0014F, -0.0164F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(22, 0).addBox(-3.0F, -4.0F, -8.0F, 5.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -8.0F, 0.9599F, 0.0F, 0.0F));

        PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(53, 0).addBox(-2.99F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(53, 0).addBox(-0.01F, -16.0F, -10.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(62, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 5.45F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 2.0F, -6.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(75, 0).addBox(0.0F, -0.25F, 0.0F, 3.0F, 4.35F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(88, 0).addBox(0.0F, -0.25F, 0.0F, 3.0F, 0.95F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(101, 0).addBox(0.0F, 4.0F, 0.0F, 3.0F, 3.37F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 2.0F, 4.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(114, 0).addBox(0.0F, -0.25F, 0.0F, 3.0F, 2.43F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.12F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(127, 0).addBox(0.0F, -0.25F, 0.0F, 3.0F, 0.95F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(140, 0).addBox(0.0F, 4.0F, 0.0F, 3.0F, 3.37F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 2.0F, 4.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(153, 0).addBox(0.0F, -0.25F, 0.0F, 3.0F, 2.43F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.12F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(166, 0).addBox(0.0F, -0.25F, 0.0F, 3.0F, 0.95F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(179, 0).addBox(-4.0F, -17.0F, -7.0F, 9.0F, 11.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(0, 28).addBox(-5.0F, -18.0F, -8.0F, 11.0F, 14.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(134, 28).addBox(-4.6F, -12.0F, 1.0F, 1.1F, 3.5F, 4.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(84, 28).addBox(-1.411F, -1.162F, -1.245F, 2.822F, 2.324F, 2.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7F, -16.4F, -3.0F, 0.12F, 0.18F, -0.45F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(97, 28).addBox(-0.996F, -0.1F, -0.83F, 1.992F, 1.803F, 1.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.415F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0375F, -1.66F, -0.581F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(115, 28).addBox(-0.0996F, -0.1F, -0.1369F, 0.1992F, 2.0507F, 0.2739F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5395F, -4.1168F, -2.573F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(115, 28).addBox(-0.3113F, -0.1F, -0.3735F, 0.6225F, 2.6439F, 0.747F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3735F, -2.3672F, -0.8748F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(106, 28).addBox(-0.5187F, -0.1F, -0.581F, 1.0375F, 2.7511F, 1.162F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.66F, -0.581F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(115, 28).addBox(-0.0996F, -0.1F, -0.1369F, 0.1992F, 2.5243F, 0.2739F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.1792F, -3.237F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(129, 28).addBox(-0.3112F, -0.1F, -0.3735F, 0.6225F, 3.2675F, 0.747F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.978F, -1.1006F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(120, 28).addBox(-0.5187F, -0.1F, -0.581F, 1.0375F, 3.3749F, 1.162F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0375F, -1.66F, -0.581F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(115, 28).addBox(-0.0996F, -0.1F, -0.1369F, 0.1992F, 2.0507F, 0.2739F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5395F, -4.1168F, -2.573F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(115, 28).addBox(-0.3113F, -0.1F, -0.3735F, 0.6225F, 2.6439F, 0.747F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3735F, -2.3672F, -0.8748F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(106, 28).addBox(-0.5187F, -0.1F, -0.581F, 1.0375F, 2.7511F, 1.162F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(45, 28).addBox(0.0F, 0.0F, 0.0F, 3.0F, 5.45F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 2.0F, -6.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(58, 28).addBox(0.0F, -0.25F, 0.0F, 3.0F, 4.35F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(71, 28).addBox(0.0F, -0.25F, 0.0F, 3.0F, 0.95F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition guardian() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -8.0F, 12.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(57, 0).addBox(-8.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(57, 0).mirror().addBox(6.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(86, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(86, 0).addBox(-6.0F, 6.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(0, 31).addBox(2.5F, 4.6F, -8.55F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(6, 31).addBox(1.156F, 4.6F, -8.55F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(12, 31).addBox(-0.188F, 4.6F, -8.55F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(18, 31).addBox(-1.532F, 4.6F, -8.55F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(24, 31).addBox(-2.876F, 4.6F, -8.55F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition spike10 = head.addOrReplaceChild("spike10", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9329F, 7.9329F, 0.0F, 0.0F, 0.0F, 2.3562F));

        PartDefinition spike11 = head.addOrReplaceChild("spike11", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0003F, 8.0003F, 0.0F, 0.0F, 0.0F, 3.927F));

        PartDefinition spike6 = head.addOrReplaceChild("spike6", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0768F, 0.0F, 8.0768F, 1.5708F, 3.927F, 0.0F));

        PartDefinition spike7 = head.addOrReplaceChild("spike7", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0603F, 0.0F, 8.0603F, 1.5708F, 2.3562F, 0.0F));

        PartDefinition spike8 = head.addOrReplaceChild("spike8", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.9884F, 7.9884F, 3.927F, 0.0F, 0.0F));

        PartDefinition spike9 = head.addOrReplaceChild("spike9", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.9271F, -7.9271F, 2.3562F, 0.0F, 0.0F));

        PartDefinition eye = head.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(148, 0).addBox(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, -8.25F));

        PartDefinition tail0 = head.addOrReplaceChild("tail0", CubeListBuilder.create().texOffs(155, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

        PartDefinition tail1 = tail0.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(180, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(201, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(218, 0).addBox(0.0F, -4.5F, 3.0F, 1.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));

        PartDefinition tail_claw = tail1.addOrReplaceChild("tail_claw", CubeListBuilder.create().texOffs(335, 0).addBox(-0.935F, -0.77F, -0.825F, 1.87F, 1.54F, 1.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, -0.8F, 3.6F, 0.1F, 0.2F, -0.55F));

        PartDefinition tail_claw_palm_r1 = tail_claw.addOrReplaceChild("tail_claw_palm_r1", CubeListBuilder.create().texOffs(335, 0).addBox(-0.66F, -0.1F, -0.55F, 1.32F, 1.2622F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.275F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_0 = tail_claw.addOrReplaceChild("tail_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.6875F, -1.1F, -0.385F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_finger_0_sharp_tip_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.066F, -0.1F, -0.0907F, 0.132F, 1.4264F, 0.1815F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3575F, -2.728F, -1.705F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_finger_0_blade_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.2063F, -0.1F, -0.2475F, 0.4125F, 1.8194F, 0.495F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2475F, -1.5686F, -0.5797F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_finger_0_proximal_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.3438F, -0.1F, -0.385F, 0.6875F, 1.8905F, 0.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_finger_1 = tail_claw.addOrReplaceChild("tail_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, -0.385F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_sharp_tip_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.066F, -0.1F, -0.0907F, 0.132F, 1.7402F, 0.1815F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.432F, -2.145F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_blade_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-0.2063F, -0.1F, -0.2475F, 0.4125F, 2.2327F, 0.495F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.9734F, -0.7293F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_proximal_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-0.3438F, -0.1F, -0.385F, 0.6875F, 2.3038F, 0.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_2 = tail_claw.addOrReplaceChild("tail_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.6875F, -1.1F, -0.385F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_finger_2_sharp_tip_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.066F, -0.1F, -0.0907F, 0.132F, 1.4264F, 0.1815F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3575F, -2.728F, -1.705F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_finger_2_blade_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.2063F, -0.1F, -0.2475F, 0.4125F, 1.8194F, 0.495F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2475F, -1.5686F, -0.5797F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_finger_2_proximal_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(344, 0).addBox(-0.3438F, -0.1F, -0.385F, 0.6875F, 1.8905F, 0.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition spike0 = head.addOrReplaceChild("spike0", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.08F, 8.08F, 5.4978F, 0.0F, 0.0F));

        PartDefinition spike1 = head.addOrReplaceChild("spike1", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0432F, -8.0432F, 0.7854F, 0.0F, 0.0F));

        PartDefinition spike2 = head.addOrReplaceChild("spike2", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9667F, -7.9667F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition spike3 = head.addOrReplaceChild("spike3", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9208F, -7.9208F, 0.0F, 0.0F, 0.0F, 5.4978F));

        PartDefinition spike4 = head.addOrReplaceChild("spike4", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9477F, 0.0F, -7.9477F, 1.5708F, 0.7854F, 0.0F));

        PartDefinition spike5 = head.addOrReplaceChild("spike5", CubeListBuilder.create().texOffs(135, 0).addBox(-1.25F, -4.5F, -1.25F, 2.5F, 9.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0227F, 0.0F, -8.0227F, 1.5708F, 5.4978F, 0.0F));

        PartDefinition dorsal_claw = head.addOrReplaceChild("dorsal_claw", CubeListBuilder.create().texOffs(239, 0).addBox(-2.074F, -1.708F, -1.83F, 4.148F, 3.416F, 3.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -6.0F, 2.2F, 0.14F, 0.18F, -0.48F));

        PartDefinition dorsal_claw_palm_r1 = dorsal_claw.addOrReplaceChild("dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(258, 0).addBox(-1.464F, -0.1F, -1.22F, 2.928F, 2.5562F, 2.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.61F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_0 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.525F, -2.44F, -0.854F, 0.0F, -0.2F, -0.14F));

        PartDefinition dorsal_claw_finger_0_sharp_tip_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-0.1464F, -0.1F, -0.2013F, 0.2928F, 2.9203F, 0.4026F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.793F, -6.0512F, -3.782F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition dorsal_claw_finger_0_blade_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(280, 0).addBox(-0.4575F, -0.1F, -0.549F, 0.915F, 3.7922F, 1.098F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.549F, -3.4794F, -1.2859F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition dorsal_claw_finger_0_proximal_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(271, 0).addBox(-0.7625F, -0.1F, -0.854F, 1.525F, 3.9498F, 1.708F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition dorsal_claw_finger_1 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.44F, -0.854F, 0.09F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_sharp_tip_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(308, 0).addBox(-0.1464F, -0.1F, -0.2013F, 0.2928F, 3.6165F, 0.4026F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.6128F, -4.758F, -1.7955F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_blade_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(301, 0).addBox(-0.4575F, -0.1F, -0.549F, 0.915F, 4.7088F, 1.098F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.3774F, -1.6177F, -2.3711F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_proximal_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(292, 0).addBox(-0.7625F, -0.1F, -0.854F, 1.525F, 4.8667F, 1.708F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_2 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.525F, -2.44F, -0.854F, 0.18F, 0.2F, 0.14F));

        PartDefinition dorsal_claw_finger_2_sharp_tip_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-0.1464F, -0.1F, -0.2013F, 0.2928F, 2.9203F, 0.4026F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.793F, -6.0512F, -3.782F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition dorsal_claw_finger_2_blade_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(280, 0).addBox(-0.4575F, -0.1F, -0.549F, 0.915F, 3.7922F, 1.098F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.549F, -3.4794F, -1.2859F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition dorsal_claw_finger_2_proximal_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(271, 0).addBox(-0.7625F, -0.1F, -0.854F, 1.525F, 3.9498F, 1.708F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition flank_claw = head.addOrReplaceChild("flank_claw", CubeListBuilder.create().texOffs(313, 0).addBox(-1.802F, -1.484F, -1.59F, 3.604F, 2.968F, 3.18F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -0.5F, -0.2F, 0.28F, -0.22F, 0.9F));

        PartDefinition flank_claw_palm_r1 = flank_claw.addOrReplaceChild("flank_claw_palm_r1", CubeListBuilder.create().texOffs(258, 0).addBox(-1.272F, -0.1F, -1.06F, 2.544F, 2.2472F, 2.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.53F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_0 = flank_claw.addOrReplaceChild("flank_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.325F, -2.12F, -0.742F, 0.0F, -0.2F, -0.14F));

        PartDefinition flank_claw_finger_0_sharp_tip_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-0.1272F, -0.1F, -0.1749F, 0.2544F, 2.5635F, 0.3498F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.689F, -5.2576F, -3.286F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition flank_claw_finger_0_blade_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(308, 0).addBox(-0.3975F, -0.1F, -0.477F, 0.795F, 3.3211F, 0.954F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.477F, -3.0231F, -1.1172F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition flank_claw_finger_0_proximal_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(271, 0).addBox(-0.6625F, -0.1F, -0.742F, 1.325F, 3.4581F, 1.484F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition flank_claw_finger_1 = flank_claw.addOrReplaceChild("flank_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.12F, -0.742F, 0.09F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_sharp_tip_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(308, 0).addBox(-0.1272F, -0.1F, -0.1749F, 0.2544F, 3.1684F, 0.3498F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.6144F, -4.134F, -1.7955F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_blade_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(330, 0).addBox(-0.3975F, -0.1F, -0.477F, 0.795F, 4.1175F, 0.954F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8033F, -1.4056F, -2.3711F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_proximal_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(292, 0).addBox(-0.6625F, -0.1F, -0.742F, 1.325F, 4.2547F, 1.484F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_2 = flank_claw.addOrReplaceChild("flank_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.325F, -2.12F, -0.742F, 0.18F, 0.2F, 0.14F));

        PartDefinition flank_claw_finger_2_sharp_tip_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-0.1272F, -0.1F, -0.1749F, 0.2544F, 2.5635F, 0.3498F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.689F, -5.2576F, -3.286F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition flank_claw_finger_2_blade_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(308, 0).addBox(-0.3975F, -0.1F, -0.477F, 0.795F, 3.3211F, 0.954F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.477F, -3.0231F, -1.1172F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition flank_claw_finger_2_proximal_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(271, 0).addBox(-0.6625F, -0.1F, -0.742F, 1.325F, 3.4581F, 1.484F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(349, 0).addBox(-5.2F, -0.1F, -13.4F, 10.4F, 2.4F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(30, 31).addBox(2.15F, -0.9F, -13.5F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(36, 31).addBox(0.502F, -1.1F, -13.5F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(42, 31).addBox(-1.146F, -0.9F, -13.5F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(48, 31).addBox(-2.794F, -1.1F, -13.5F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.5F, 5.0F, 0.2F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition husk() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 2.8F, 6.8F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(0, 0).addBox(1.3F, -8.0F, -4.0F, 2.7F, 6.8F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(23, 0).addBox(-1.2F, -8.0F, -4.0F, 2.5F, 2.6F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(46, 0).addBox(-1.35F, -5.5F, -1.0F, 2.8F, 5.0F, 4.5F, new CubeDeformation(0.0F))
        .texOffs(0, 19).addBox(-0.65F, -1.4F, 2.8F, 1.3F, 1.2F, 1.05F, new CubeDeformation(0.0F))
        .texOffs(10, 19).addBox(-3.4F, -1.5F, -3.75F, 6.8F, 0.35F, 0.85F, new CubeDeformation(0.0F))
        .texOffs(28, 19).addBox(2.5F, -1.4F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(34, 19).addBox(1.156F, -1.4F, -4.15F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(40, 19).addBox(-0.188F, -1.4F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(46, 19).addBox(-1.532F, -1.4F, -4.15F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(52, 19).addBox(-2.876F, -1.4F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(63, 0).addBox(-4.0F, 0.8F, -6.5F, 8.0F, 1.3F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(58, 19).addBox(2.15F, 0.0F, -6.6F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(64, 19).addBox(0.502F, -0.2F, -6.6F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(70, 19).addBox(-1.146F, 0.0F, -6.6F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(76, 19).addBox(-2.794F, -0.2F, -6.6F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 2.5F, 0.22F, 0.0F, 0.0F));

        PartDefinition skull_crown_claw = head.addOrReplaceChild("skull_crown_claw", CubeListBuilder.create().texOffs(206, 0).addBox(-2.38F, -1.96F, -2.1F, 4.76F, 3.92F, 4.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -5.5F, 1.4F, 0.18F, -0.1F, 0.2F));

        PartDefinition skull_crown_claw_palm_r1 = skull_crown_claw.addOrReplaceChild("skull_crown_claw_palm_r1", CubeListBuilder.create().texOffs(227, 0).addBox(-1.68F, -0.1F, -1.4F, 3.36F, 2.9039F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_crown_claw_finger_0 = skull_crown_claw.addOrReplaceChild("skull_crown_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.75F, -2.8F, -0.98F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_crown_claw_finger_0_sharp_tip_r1 = skull_crown_claw_finger_0.addOrReplaceChild("skull_crown_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.168F, -0.1F, -0.231F, 0.336F, 3.3217F, 0.462F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.91F, -6.944F, -4.34F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_crown_claw_finger_0_blade_r1 = skull_crown_claw_finger_0.addOrReplaceChild("skull_crown_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.525F, -0.1F, -0.63F, 1.05F, 4.3222F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.63F, -3.9928F, -1.4756F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_crown_claw_finger_0_proximal_r1 = skull_crown_claw_finger_0.addOrReplaceChild("skull_crown_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.875F, -0.1F, -0.98F, 1.75F, 4.5031F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_crown_claw_finger_1 = skull_crown_claw.addOrReplaceChild("skull_crown_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.8F, -0.98F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_crown_claw_finger_1_sharp_tip_r1 = skull_crown_claw_finger_1.addOrReplaceChild("skull_crown_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(265, 0).addBox(-0.168F, -0.1F, -0.231F, 0.336F, 4.1206F, 0.462F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.736F, -5.46F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_crown_claw_finger_1_blade_r1 = skull_crown_claw_finger_1.addOrReplaceChild("skull_crown_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(256, 0).addBox(-0.525F, -0.1F, -0.63F, 1.05F, 5.3741F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0232F, -1.8564F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_crown_claw_finger_1_proximal_r1 = skull_crown_claw_finger_1.addOrReplaceChild("skull_crown_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(256, 0).addBox(-0.875F, -0.1F, -0.98F, 1.75F, 5.5553F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_crown_claw_finger_2 = skull_crown_claw.addOrReplaceChild("skull_crown_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, -2.8F, -0.98F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_crown_claw_finger_2_sharp_tip_r1 = skull_crown_claw_finger_2.addOrReplaceChild("skull_crown_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.168F, -0.1F, -0.231F, 0.336F, 3.3217F, 0.462F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.91F, -6.944F, -4.34F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_crown_claw_finger_2_blade_r1 = skull_crown_claw_finger_2.addOrReplaceChild("skull_crown_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.525F, -0.1F, -0.63F, 1.05F, 4.3222F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.63F, -3.9928F, -1.4756F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_crown_claw_finger_2_proximal_r1 = skull_crown_claw_finger_2.addOrReplaceChild("skull_crown_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.875F, -0.1F, -0.98F, 1.75F, 4.5031F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition skull_left_claw = head.addOrReplaceChild("skull_left_claw", CubeListBuilder.create().texOffs(270, 0).addBox(-1.87F, -1.54F, -1.65F, 3.74F, 3.08F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -4.2F, 0.6F, 0.1F, -0.35F, 0.95F));

        PartDefinition skull_left_claw_palm_r1 = skull_left_claw.addOrReplaceChild("skull_left_claw_palm_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-1.32F, -0.1F, -1.1F, 2.64F, 2.3245F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.55F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_left_claw_finger_0 = skull_left_claw.addOrReplaceChild("skull_left_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.375F, -2.2F, -0.77F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_left_claw_finger_0_sharp_tip_r1 = skull_left_claw_finger_0.addOrReplaceChild("skull_left_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.715F, -5.456F, -3.41F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_left_claw_finger_0_blade_r1 = skull_left_claw_finger_0.addOrReplaceChild("skull_left_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.495F, -3.1372F, -1.1594F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_left_claw_finger_0_proximal_r1 = skull_left_claw_finger_0.addOrReplaceChild("skull_left_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_left_claw_finger_1 = skull_left_claw.addOrReplaceChild("skull_left_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.2F, -0.77F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_left_claw_finger_1_sharp_tip_r1 = skull_left_claw_finger_1.addOrReplaceChild("skull_left_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 3.2804F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.864F, -4.29F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_left_claw_finger_1_blade_r1 = skull_left_claw_finger_1.addOrReplaceChild("skull_left_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(265, 0).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 4.2653F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9468F, -1.4586F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_left_claw_finger_1_proximal_r1 = skull_left_claw_finger_1.addOrReplaceChild("skull_left_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 4.4077F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_left_claw_finger_2 = skull_left_claw.addOrReplaceChild("skull_left_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.375F, -2.2F, -0.77F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_left_claw_finger_2_sharp_tip_r1 = skull_left_claw_finger_2.addOrReplaceChild("skull_left_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.715F, -5.456F, -3.41F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_left_claw_finger_2_blade_r1 = skull_left_claw_finger_2.addOrReplaceChild("skull_left_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.495F, -3.1372F, -1.1594F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_left_claw_finger_2_proximal_r1 = skull_left_claw_finger_2.addOrReplaceChild("skull_left_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition skull_front_claw = head.addOrReplaceChild("skull_front_claw", CubeListBuilder.create().texOffs(314, 0).addBox(-1.785F, -1.47F, -1.575F, 3.57F, 2.94F, 3.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, -4.1F, -1.2F, 0.75F, 0.3F, -0.28F));

        PartDefinition skull_front_claw_palm_r1 = skull_front_claw.addOrReplaceChild("skull_front_claw_palm_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-1.26F, -0.1F, -1.05F, 2.52F, 2.2279F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.525F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_front_claw_finger_0 = skull_front_claw.addOrReplaceChild("skull_front_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.3125F, -2.1F, -0.735F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_front_claw_finger_0_sharp_tip_r1 = skull_front_claw_finger_0.addOrReplaceChild("skull_front_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 2.5412F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6825F, -5.208F, -3.255F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_front_claw_finger_0_blade_r1 = skull_front_claw_finger_0.addOrReplaceChild("skull_front_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.3937F, -0.1F, -0.4725F, 0.7875F, 3.2917F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4725F, -2.9946F, -1.1067F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_front_claw_finger_0_proximal_r1 = skull_front_claw_finger_0.addOrReplaceChild("skull_front_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 3.4273F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_front_claw_finger_1 = skull_front_claw.addOrReplaceChild("skull_front_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.1F, -0.735F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_front_claw_finger_1_sharp_tip_r1 = skull_front_claw_finger_1.addOrReplaceChild("skull_front_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 3.1404F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.552F, -4.095F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_front_claw_finger_1_blade_r1 = skull_front_claw_finger_1.addOrReplaceChild("skull_front_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(265, 0).addBox(-0.3938F, -0.1F, -0.4725F, 0.7875F, 4.0805F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.7674F, -1.3923F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_front_claw_finger_1_proximal_r1 = skull_front_claw_finger_1.addOrReplaceChild("skull_front_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 4.2164F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_front_claw_finger_2 = skull_front_claw.addOrReplaceChild("skull_front_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.3125F, -2.1F, -0.735F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_front_claw_finger_2_sharp_tip_r1 = skull_front_claw_finger_2.addOrReplaceChild("skull_front_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 2.5412F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6825F, -5.208F, -3.255F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_front_claw_finger_2_blade_r1 = skull_front_claw_finger_2.addOrReplaceChild("skull_front_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.3938F, -0.1F, -0.4725F, 0.7875F, 3.2917F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4725F, -2.9946F, -1.1067F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_front_claw_finger_2_proximal_r1 = skull_front_claw_finger_2.addOrReplaceChild("skull_front_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 3.4273F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(96, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 5.69F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(96, 0).addBox(-3.0F, 0.55F, -2.0F, 4.0F, 5.21F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(331, 0).addBox(-2.0F, -1.5F, -1.25F, 2.0F, 3.0F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition elbow_claw = right_arm_lower.addOrReplaceChild("elbow_claw", CubeListBuilder.create().texOffs(287, 0).addBox(-1.394F, -1.148F, -1.23F, 2.788F, 2.296F, 2.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 1.3F, 0.15F, 0.2F, 0.55F));

        PartDefinition elbow_claw_palm_r1 = elbow_claw.addOrReplaceChild("elbow_claw_palm_r1", CubeListBuilder.create().texOffs(342, 0).addBox(-0.984F, -0.1F, -0.82F, 1.968F, 1.7837F, 1.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.41F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition elbow_claw_finger_0 = elbow_claw.addOrReplaceChild("elbow_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.025F, -1.64F, -0.574F, 0.0F, -0.2F, -0.14F));

        PartDefinition elbow_claw_finger_0_sharp_tip_r1 = elbow_claw_finger_0.addOrReplaceChild("elbow_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.0984F, -0.1F, -0.1353F, 0.1968F, 2.0284F, 0.2706F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.533F, -4.0672F, -2.542F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition elbow_claw_finger_0_blade_r1 = elbow_claw_finger_0.addOrReplaceChild("elbow_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.3075F, -0.1F, -0.369F, 0.615F, 2.6145F, 0.738F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.369F, -2.3386F, -0.8643F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition elbow_claw_finger_0_proximal_r1 = elbow_claw_finger_0.addOrReplaceChild("elbow_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(351, 0).addBox(-0.5125F, -0.1F, -0.574F, 1.025F, 2.7204F, 1.148F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition elbow_claw_finger_1 = elbow_claw.addOrReplaceChild("elbow_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.64F, -0.574F, 0.09F, 0.0F, 0.0F));

        PartDefinition elbow_claw_finger_1_sharp_tip_r1 = elbow_claw_finger_1.addOrReplaceChild("elbow_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.0984F, -0.1F, -0.1353F, 0.1968F, 2.4963F, 0.2706F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.1168F, -3.198F, -1.7955F, 0.0F, 0.0F));

        PartDefinition elbow_claw_finger_1_blade_r1 = elbow_claw_finger_1.addOrReplaceChild("elbow_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.3075F, -0.1F, -0.369F, 0.615F, 3.2305F, 0.738F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.9422F, -1.0873F, -2.3711F, 0.0F, 0.0F));

        PartDefinition elbow_claw_finger_1_proximal_r1 = elbow_claw_finger_1.addOrReplaceChild("elbow_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.5125F, -0.1F, -0.574F, 1.025F, 3.3366F, 1.148F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition elbow_claw_finger_2 = elbow_claw.addOrReplaceChild("elbow_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.025F, -1.64F, -0.574F, 0.18F, 0.2F, 0.14F));

        PartDefinition elbow_claw_finger_2_sharp_tip_r1 = elbow_claw_finger_2.addOrReplaceChild("elbow_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.0984F, -0.1F, -0.1353F, 0.1968F, 2.0284F, 0.2706F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.533F, -4.0672F, -2.542F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition elbow_claw_finger_2_blade_r1 = elbow_claw_finger_2.addOrReplaceChild("elbow_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.3075F, -0.1F, -0.369F, 0.615F, 2.6145F, 0.738F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.369F, -2.3386F, -0.8643F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition elbow_claw_finger_2_proximal_r1 = elbow_claw_finger_2.addOrReplaceChild("elbow_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(351, 0).addBox(-0.5125F, -0.1F, -0.574F, 1.025F, 2.7204F, 1.148F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(113, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.9F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(130, 0).mirror().addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(147, 0).mirror().addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(164, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(164, 0).mirror().addBox(-1.0F, -0.25F, -2.0F, 4.0F, 6.01F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(113, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(130, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(147, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(181, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(360, 0).addBox(-2.006F, -1.652F, -1.77F, 4.012F, 3.304F, 3.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, 1.5F, 1.3F, 0.15F, 0.3F, -0.48F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-1.416F, -0.1F, -1.18F, 2.832F, 2.479F, 2.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.59F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.475F, -2.36F, -0.826F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.1416F, -0.1F, -0.1947F, 0.2832F, 2.8311F, 0.3894F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.767F, -5.8528F, -3.658F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(379, 0).addBox(-0.4425F, -0.1F, -0.531F, 0.885F, 3.6745F, 1.062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.531F, -3.3654F, -1.2437F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.7375F, -0.1F, -0.826F, 1.475F, 3.8269F, 1.652F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.36F, -0.826F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(251, 0).addBox(-0.1416F, -0.1F, -0.1947F, 0.2832F, 3.5045F, 0.3894F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.3632F, -4.602F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(386, 0).addBox(-0.4425F, -0.1F, -0.531F, 0.885F, 4.561F, 1.062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2338F, -1.5647F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.7375F, -0.1F, -0.826F, 1.475F, 4.7137F, 1.652F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.475F, -2.36F, -0.826F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.1416F, -0.1F, -0.1947F, 0.2832F, 2.8311F, 0.3894F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.767F, -5.8528F, -3.658F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(379, 0).addBox(-0.4425F, -0.1F, -0.531F, 0.885F, 3.6745F, 1.062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.531F, -3.3654F, -1.2437F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(300, 0).addBox(-0.7375F, -0.1F, -0.826F, 1.475F, 3.8269F, 1.652F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition iron_golem() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 9.2F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(71, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(0, 43).addBox(-0.65F, -3.0F, 1.3F, 1.3F, 1.2F, 1.05F, new CubeDeformation(0.0F))
        .texOffs(10, 43).addBox(2.5F, -3.0F, -5.65F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(16, 43).addBox(1.156F, -3.0F, -5.65F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(22, 43).addBox(-0.188F, -3.0F, -5.65F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(28, 43).addBox(-1.532F, -3.0F, -5.65F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(34, 43).addBox(-2.876F, -3.0F, -5.65F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.0F, -2.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, 0.8F, -6.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(40, 43).addBox(2.15F, 0.0F, -6.1F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(46, 43).addBox(0.502F, -0.2F, -6.1F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(52, 43).addBox(-1.146F, 0.0F, -6.1F, 0.65F, 0.95F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(58, 43).addBox(-2.794F, -0.2F, -6.1F, 0.65F, 1.15F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.8F, 0.5F, 0.05F, 0.0F, 0.0F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 43).addBox(-3.8F, -2.5F, -3.8F, 7.6F, 15.85F, 7.6F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, -19.0F, 0.0F));

        PartDefinition tyrant_right_arm_tip_3_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_tip_3_r1", CubeListBuilder.create().texOffs(58, 88).addBox(-0.19F, -2.7351F, -0.23F, 0.38F, 2.8651F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7838F, 11.0F, 6.3948F, -1.9309F, -0.7598F, -0.5392F));

        PartDefinition tyrant_right_arm_spur_3_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_spur_3_r1", CubeListBuilder.create().texOffs(48, 88).addBox(-0.8F, -3.732F, -0.775F, 1.6F, 3.862F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9763F, 9.2F, 3.3811F, -2.016F, -0.3843F, -0.2442F));

        PartDefinition tyrant_right_arm_tip_2_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_tip_2_r1", CubeListBuilder.create().texOffs(42, 88).addBox(-0.19F, -3.1747F, -0.23F, 0.38F, 3.3047F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.4232F, 5.44F, -1.6952F, -0.0356F, -0.0214F, -1.0808F));

        PartDefinition tyrant_right_arm_spur_2_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_spur_2_r1", CubeListBuilder.create().texOffs(32, 88).addBox(-0.98F, -4.5729F, -0.775F, 1.96F, 4.7029F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3037F, 7.0F, -1.1163F, 0.1312F, 0.0895F, -1.197F));

        PartDefinition tyrant_right_arm_tip_1_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_tip_1_r1", CubeListBuilder.create().texOffs(26, 88).addBox(-0.19F, -2.1637F, -0.23F, 0.38F, 2.2937F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7783F, 5.72F, 4.8677F, -0.7489F, -0.7465F, -1.5673F));

        PartDefinition tyrant_right_arm_spur_1_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_spur_1_r1", CubeListBuilder.create().texOffs(16, 88).addBox(-0.89F, -3.1053F, -0.775F, 1.78F, 3.2353F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.908F, 4.7F, 2.7906F, -1.2163F, -0.7312F, -1.0055F));

        PartDefinition tyrant_right_arm_tip_0_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_tip_0_r1", CubeListBuilder.create().texOffs(10, 88).addBox(-0.19F, -3.5252F, -0.23F, 0.38F, 3.6552F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2403F, -1.18F, -6.4689F, 0.5252F, 0.1521F, -0.5526F));

        PartDefinition tyrant_right_arm_spur_0_r1 = right_arm.addOrReplaceChild("tyrant_right_arm_spur_0_r1", CubeListBuilder.create().texOffs(0, 88).addBox(-0.8F, -4.6983F, -0.775F, 1.6F, 4.8283F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0243F, 1.4F, -3.4189F, 0.7511F, 0.2085F, -0.5188F));

        PartDefinition tyrant_upper_lobe_2_r1 = right_arm.addOrReplaceChild("tyrant_upper_lobe_2_r1", CubeListBuilder.create().texOffs(218, 43).addBox(-4.6F, -3.8F, -4.9F, 9.2F, 5.8F, 9.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.0349F));

        PartDefinition tyrant_upper_lobe_1_r1 = right_arm.addOrReplaceChild("tyrant_upper_lobe_1_r1", CubeListBuilder.create().texOffs(172, 43).addBox(-5.3F, -4.8F, -5.4F, 10.6F, 6.8F, 10.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition tyrant_upper_lobe_0_r1 = right_arm.addOrReplaceChild("tyrant_upper_lobe_0_r1", CubeListBuilder.create().texOffs(134, 43).addBox(-4.2F, -3.8F, -4.4F, 8.4F, 5.8F, 8.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0349F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(98, 43).addBox(-4.0F, -0.25F, -4.2F, 8.0F, 14.65F, 8.4F, new CubeDeformation(0.0F))
        .texOffs(340, 69).addBox(-2.0F, -2.3F, -5.0F, 4.0F, 3.1F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.1F, 0.0F));

        PartDefinition tyrant_right_arm_lower_tip_5_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_tip_5_r1", CubeListBuilder.create().texOffs(154, 88).addBox(-0.19F, -2.3683F, -0.23F, 0.38F, 2.4983F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8616F, 10.94F, -1.5214F, 0.2914F, 0.1246F, -0.804F));

        PartDefinition tyrant_right_arm_lower_spur_5_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_spur_5_r1", CubeListBuilder.create().texOffs(144, 88).addBox(-0.98F, -3.218F, -0.775F, 1.96F, 3.348F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2225F, 12.5F, -1.1505F, 0.1207F, 0.0682F, -1.0287F));

        PartDefinition tyrant_right_arm_lower_tip_4_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_tip_4_r1", CubeListBuilder.create().texOffs(138, 88).addBox(-0.19F, -2.7201F, -0.23F, 0.38F, 2.8501F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4452F, 11.16F, 7.3264F, -1.6743F, -0.5508F, -0.4988F));

        PartDefinition tyrant_right_arm_lower_spur_4_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_spur_4_r1", CubeListBuilder.create().texOffs(128, 88).addBox(-0.89F, -4.0203F, -0.775F, 1.78F, 4.1503F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2293F, 10.2F, 4.2764F, -1.3403F, -0.6346F, -0.7857F));

        PartDefinition tyrant_right_arm_lower_tip_3_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_tip_3_r1", CubeListBuilder.create().texOffs(122, 88).addBox(-0.19F, -3.1685F, -0.23F, 0.38F, 3.2985F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0135F, 5.98F, -8.4103F, 0.6825F, 0.0572F, -0.1609F));

        PartDefinition tyrant_right_arm_lower_spur_3_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_spur_3_r1", CubeListBuilder.create().texOffs(112, 88).addBox(-0.8F, -4.0902F, -0.775F, 1.6F, 4.2202F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5793F, 8.5F, -5.7129F, 0.7601F, 0.1513F, -0.3751F));

        PartDefinition tyrant_right_arm_lower_tip_2_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_tip_2_r1", CubeListBuilder.create().texOffs(106, 88).addBox(-0.19F, -3.0613F, -0.23F, 0.38F, 3.1913F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.4288F, 4.88F, 2.6903F, -0.5102F, -0.3295F, -1.135F));

        PartDefinition tyrant_right_arm_lower_spur_2_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_spur_2_r1", CubeListBuilder.create().texOffs(96, 88).addBox(-0.98F, -4.6083F, -0.775F, 1.96F, 4.7383F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2251F, 5.6F, 1.3244F, -0.3202F, -0.2492F, -1.3196F));

        PartDefinition tyrant_right_arm_lower_tip_1_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_tip_1_r1", CubeListBuilder.create().texOffs(90, 88).addBox(-0.19F, -2.409F, -0.23F, 0.38F, 2.539F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.9092F, 5.78F, 7.1567F, -1.7424F, -0.9183F, -0.7886F));

        PartDefinition tyrant_right_arm_lower_spur_1_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_spur_1_r1", CubeListBuilder.create().texOffs(80, 88).addBox(-0.89F, -3.2255F, -0.775F, 1.78F, 3.3555F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9352F, 4.1F, 4.746F, -1.9675F, -0.5655F, -0.3819F));

        PartDefinition tyrant_right_arm_lower_tip_0_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_tip_0_r1", CubeListBuilder.create().texOffs(74, 88).addBox(-0.19F, -3.1048F, -0.23F, 0.38F, 3.2348F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.2893F, -0.8F, -5.2622F, 0.3402F, 0.1383F, -0.7666F));

        PartDefinition tyrant_right_arm_lower_spur_0_r1 = right_arm_lower.addOrReplaceChild("tyrant_right_arm_lower_spur_0_r1", CubeListBuilder.create().texOffs(64, 88).addBox(-0.8F, -4.2202F, -0.775F, 1.6F, 4.3502F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4496F, 1.3F, -3.1991F, 0.5451F, 0.2335F, -0.7946F));

        PartDefinition tyrant_forearm_lobe_3_r1 = right_arm_lower.addOrReplaceChild("tyrant_forearm_lobe_3_r1", CubeListBuilder.create().texOffs(424, 43).addBox(-5.2F, -2.0F, -6.2F, 10.4F, 4.0F, 11.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.4F, 0.0F, -0.0349F, 0.0F, -0.0524F));

        PartDefinition tyrant_forearm_lobe_2_r1 = right_arm_lower.addOrReplaceChild("tyrant_forearm_lobe_2_r1", CubeListBuilder.create().texOffs(364, 43).addBox(-6.7F, -3.5F, -7.7F, 13.4F, 5.5F, 14.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.4F, 0.0F, 0.0349F, 0.0F, 0.0524F));

        PartDefinition tyrant_forearm_lobe_1_r1 = right_arm_lower.addOrReplaceChild("tyrant_forearm_lobe_1_r1", CubeListBuilder.create().texOffs(308, 43).addBox(-6.3F, -4.0F, -7.4F, 12.6F, 6.0F, 13.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.9F, 0.0F, -0.0349F, 0.0F, -0.0524F));

        PartDefinition tyrant_forearm_lobe_0_r1 = right_arm_lower.addOrReplaceChild("tyrant_forearm_lobe_0_r1", CubeListBuilder.create().texOffs(260, 43).addBox(-5.2F, -3.6F, -6.1F, 10.4F, 5.6F, 11.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.9F, 0.0F, 0.0349F, 0.0F, 0.0524F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(0, 69).addBox(-5.8F, -3.3F, -6.3F, 11.6F, 4.2F, 11.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.9F, 0.0F));

        PartDefinition tyrant_finger_0 = right_arm_hand.addOrReplaceChild("tyrant_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -1.0F, -4.8F, 0.0F, 0.2094F, -0.0698F));

        PartDefinition tyrant_finger_0_sharp_tip_r1 = tyrant_finger_0.addOrReplaceChild("tyrant_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(70, 69).addBox(-0.175F, -4.6414F, -0.21F, 0.35F, 4.7714F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.5F, -12.75F, 1.9073F, 0.0626F, -0.0444F));

        PartDefinition tyrant_finger_0_blade_r1 = tyrant_finger_0.addOrReplaceChild("tyrant_finger_0_blade_r1", CubeListBuilder.create().texOffs(60, 69).addBox(-0.55F, -6.2865F, -0.625F, 1.1F, 6.4165F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9F, -1.0F, -6.8F, 1.8092F, -0.104F, 0.0817F));

        PartDefinition tyrant_finger_0_root_r1 = tyrant_finger_0.addOrReplaceChild("tyrant_finger_0_root_r1", CubeListBuilder.create().texOffs(50, 69).addBox(-0.9F, -7.0618F, -1.0F, 1.8F, 7.1918F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4101F, -0.1115F, 0.131F));

        PartDefinition tyrant_finger_1 = right_arm_hand.addOrReplaceChild("tyrant_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -1.0F, -4.8F, 0.0F, 0.1047F, -0.0349F));

        PartDefinition tyrant_finger_1_sharp_tip_r1 = tyrant_finger_1.addOrReplaceChild("tyrant_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(96, 69).addBox(-0.175F, -4.1701F, -0.21F, 0.35F, 4.3001F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, 0.5F, -11.25F, 1.9504F, 0.0365F, -0.0248F));

        PartDefinition tyrant_finger_1_blade_r1 = tyrant_finger_1.addOrReplaceChild("tyrant_finger_1_blade_r1", CubeListBuilder.create().texOffs(86, 69).addBox(-0.55F, -5.5958F, -0.625F, 1.1F, 5.7258F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.45F, -1.0F, -6.0F, 1.8463F, -0.0606F, 0.0458F));

        PartDefinition tyrant_finger_1_root_r1 = tyrant_finger_1.addOrReplaceChild("tyrant_finger_1_root_r1", CubeListBuilder.create().texOffs(76, 69).addBox(-0.9F, -6.2294F, -1.0F, 1.8F, 6.3594F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.401F, -0.0624F, 0.074F));

        PartDefinition tyrant_finger_2 = right_arm_hand.addOrReplaceChild("tyrant_finger_2", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -4.8F));

        PartDefinition tyrant_finger_2_sharp_tip_r1 = tyrant_finger_2.addOrReplaceChild("tyrant_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(122, 69).addBox(-0.175F, -5.1112F, -0.21F, 0.35F, 5.2412F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -14.25F, 1.8767F, 0.0F, 0.0F));

        PartDefinition tyrant_finger_2_blade_r1 = tyrant_finger_2.addOrReplaceChild("tyrant_finger_2_blade_r1", CubeListBuilder.create().texOffs(112, 69).addBox(-0.55F, -6.9471F, -0.625F, 1.1F, 7.0771F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -7.6F, 1.7926F, 0.0F, 0.0F));

        PartDefinition tyrant_finger_2_root_r1 = tyrant_finger_2.addOrReplaceChild("tyrant_finger_2_root_r1", CubeListBuilder.create().texOffs(102, 69).addBox(-0.9F, -7.7955F, -1.0F, 1.8F, 7.9255F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.44F, 0.0F, 0.0F));

        PartDefinition tyrant_finger_3 = right_arm_hand.addOrReplaceChild("tyrant_finger_3", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -1.0F, -4.8F, 0.0F, -0.1047F, 0.0349F));

        PartDefinition tyrant_finger_3_sharp_tip_r1 = tyrant_finger_3.addOrReplaceChild("tyrant_finger_3_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 69).addBox(-0.175F, -4.1701F, -0.21F, 0.35F, 4.3001F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 0.5F, -11.25F, 1.9504F, -0.0365F, 0.0248F));

        PartDefinition tyrant_finger_3_blade_r1 = tyrant_finger_3.addOrReplaceChild("tyrant_finger_3_blade_r1", CubeListBuilder.create().texOffs(138, 69).addBox(-0.55F, -5.5958F, -0.625F, 1.1F, 5.7258F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.45F, -1.0F, -6.0F, 1.8463F, 0.0606F, -0.0458F));

        PartDefinition tyrant_finger_3_root_r1 = tyrant_finger_3.addOrReplaceChild("tyrant_finger_3_root_r1", CubeListBuilder.create().texOffs(128, 69).addBox(-0.9F, -6.2294F, -1.0F, 1.8F, 6.3594F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.401F, 0.0624F, -0.074F));

        PartDefinition tyrant_finger_4 = right_arm_hand.addOrReplaceChild("tyrant_finger_4", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -1.0F, -4.8F, 0.0F, -0.2094F, 0.0698F));

        PartDefinition tyrant_finger_4_sharp_tip_r1 = tyrant_finger_4.addOrReplaceChild("tyrant_finger_4_sharp_tip_r1", CubeListBuilder.create().texOffs(174, 69).addBox(-0.175F, -4.6414F, -0.21F, 0.35F, 4.7714F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, 0.5F, -12.75F, 1.9073F, -0.0626F, 0.0444F));

        PartDefinition tyrant_finger_4_blade_r1 = tyrant_finger_4.addOrReplaceChild("tyrant_finger_4_blade_r1", CubeListBuilder.create().texOffs(164, 69).addBox(-0.55F, -6.2865F, -0.625F, 1.1F, 6.4165F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, -1.0F, -6.8F, 1.8092F, 0.104F, -0.0817F));

        PartDefinition tyrant_finger_4_root_r1 = tyrant_finger_4.addOrReplaceChild("tyrant_finger_4_root_r1", CubeListBuilder.create().texOffs(154, 69).addBox(-0.9F, -7.0618F, -1.0F, 1.8F, 7.1918F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4101F, 0.1115F, -0.131F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(122, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 8.57F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -1.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(145, 0).mirror().addBox(-3.5F, -0.25F, -3.0F, 6.0F, 7.23F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.32F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(168, 0).mirror().addBox(-3.5F, -0.25F, -3.0F, 6.0F, 0.95F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.98F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(191, 0).addBox(-2.0F, -2.5F, -3.0F, 4.0F, 15.85F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(11.0F, -19.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(212, 0).addBox(-2.0F, -0.25F, -3.0F, 4.0F, 14.65F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.1F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 13.9F, 0.0F));

        PartDefinition forearm_claw = left_arm_lower.addOrReplaceChild("forearm_claw", CubeListBuilder.create().texOffs(0, 24).addBox(-2.346F, -1.932F, -2.07F, 4.692F, 3.864F, 4.14F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, 7.5F, 2.5F, -0.4F, 0.1F, -1.2F));

        PartDefinition forearm_claw_palm_r1 = forearm_claw.addOrReplaceChild("forearm_claw_palm_r1", CubeListBuilder.create().texOffs(21, 24).addBox(-1.656F, -0.1F, -1.38F, 3.312F, 2.8652F, 2.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.69F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_0 = forearm_claw.addOrReplaceChild("forearm_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.725F, -2.76F, -0.966F, 0.0F, -0.2F, -0.14F));

        PartDefinition forearm_claw_finger_0_sharp_tip_r1 = forearm_claw_finger_0.addOrReplaceChild("forearm_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(45, 24).addBox(-0.1656F, -0.1F, -0.2277F, 0.3312F, 3.2771F, 0.4554F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.897F, -6.8448F, -4.278F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition forearm_claw_finger_0_blade_r1 = forearm_claw_finger_0.addOrReplaceChild("forearm_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(36, 24).addBox(-0.5175F, -0.1F, -0.621F, 1.035F, 4.2633F, 1.242F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.621F, -3.9358F, -1.4545F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition forearm_claw_finger_0_proximal_r1 = forearm_claw_finger_0.addOrReplaceChild("forearm_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(36, 24).addBox(-0.8625F, -0.1F, -0.966F, 1.725F, 4.4416F, 1.932F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition forearm_claw_finger_1 = forearm_claw.addOrReplaceChild("forearm_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.76F, -0.966F, 0.09F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_1_sharp_tip_r1 = forearm_claw_finger_1.addOrReplaceChild("forearm_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(460, 0).addBox(-0.1656F, -0.1F, -0.2277F, 0.3312F, 4.0646F, 0.4554F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.6112F, -5.382F, -1.7955F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_1_blade_r1 = forearm_claw_finger_1.addOrReplaceChild("forearm_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(451, 0).addBox(-0.5175F, -0.1F, -0.621F, 1.035F, 5.3001F, 1.242F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.9514F, -1.8299F, -2.3711F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_1_proximal_r1 = forearm_claw_finger_1.addOrReplaceChild("forearm_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(451, 0).addBox(-0.8625F, -0.1F, -0.966F, 1.725F, 5.4787F, 1.932F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_2 = forearm_claw.addOrReplaceChild("forearm_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.725F, -2.76F, -0.966F, 0.18F, 0.2F, 0.14F));

        PartDefinition forearm_claw_finger_2_sharp_tip_r1 = forearm_claw_finger_2.addOrReplaceChild("forearm_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(45, 24).addBox(-0.1656F, -0.1F, -0.2277F, 0.3312F, 3.2771F, 0.4554F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.897F, -6.8448F, -4.278F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition forearm_claw_finger_2_blade_r1 = forearm_claw_finger_2.addOrReplaceChild("forearm_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(36, 24).addBox(-0.5175F, -0.1F, -0.621F, 1.035F, 4.2633F, 1.242F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.621F, -3.9358F, -1.4545F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition forearm_claw_finger_2_proximal_r1 = forearm_claw_finger_2.addOrReplaceChild("forearm_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(36, 24).addBox(-0.8625F, -0.1F, -0.966F, 1.725F, 4.4416F, 1.932F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(233, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 8.57F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -1.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(256, 0).addBox(-3.5F, -0.25F, -3.0F, 6.0F, 7.23F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.32F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(279, 0).addBox(-3.5F, -0.25F, -3.0F, 6.0F, 0.95F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.98F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(302, 0).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(361, 0).addBox(-5.0F, 9.5F, -3.5F, 10.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(50, 24).addBox(-9.2F, 0.6F, -4.4F, 1.2F, 7.0F, 8.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.0F, 0.0F));

        PartDefinition chest_broken_rib_2_r1 = body.addOrReplaceChild("chest_broken_rib_2_r1", CubeListBuilder.create().texOffs(73, 24).addBox(-0.36F, -0.1F, -0.4F, 0.72F, 8.2994F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 5.0F, -6.0F, -0.0495F, -0.0425F, -1.4198F));

        PartDefinition chest_broken_rib_1_r1 = body.addOrReplaceChild("chest_broken_rib_1_r1", CubeListBuilder.create().texOffs(73, 24).addBox(-0.36F, -0.1F, -0.4F, 0.72F, 8.2994F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 3.0F, -6.0F, -0.0495F, -0.0425F, -1.4198F));

        PartDefinition chest_broken_rib_0_r1 = body.addOrReplaceChild("chest_broken_rib_0_r1", CubeListBuilder.create().texOffs(73, 24).addBox(-0.36F, -0.1F, -0.4F, 0.72F, 8.2994F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 1.0F, -6.0F, -0.0495F, -0.0425F, -1.4198F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(392, 0).addBox(-3.315F, -2.73F, -2.925F, 6.63F, 5.46F, 5.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.2F, 1.5F, 5.8F, -0.1222F, -0.1745F, 0.7505F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(419, 0).addBox(-2.34F, -0.1F, -1.95F, 4.68F, 3.9661F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.975F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.4375F, -3.9F, -1.365F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(460, 0).addBox(-0.234F, -0.1F, -0.3218F, 0.468F, 4.548F, 0.6435F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2675F, -9.672F, -6.045F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(451, 0).addBox(-0.7313F, -0.1F, -0.8775F, 1.4625F, 5.9417F, 1.755F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8775F, -5.5614F, -2.0553F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(438, 0).addBox(-1.2188F, -0.1F, -1.365F, 2.4375F, 6.1936F, 2.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.9F, -1.365F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(487, 0).addBox(-0.234F, -0.1F, -0.3217F, 0.468F, 5.6608F, 0.6435F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.168F, -7.605F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(478, 0).addBox(-0.7312F, -0.1F, -0.8775F, 1.4625F, 7.4067F, 1.755F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.9966F, -2.5857F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(465, 0).addBox(-1.2188F, -0.1F, -1.365F, 2.4375F, 7.6591F, 2.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.4375F, -3.9F, -1.365F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(460, 0).addBox(-0.234F, -0.1F, -0.3218F, 0.468F, 4.548F, 0.6435F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2675F, -9.672F, -6.045F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(451, 0).addBox(-0.7312F, -0.1F, -0.8775F, 1.4625F, 5.9417F, 1.755F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8775F, -5.5614F, -2.0553F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(438, 0).addBox(-1.2188F, -0.1F, -1.365F, 2.4375F, 6.1936F, 2.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition llama() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(27, 0).addBox(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(56, 0).addBox(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(56, 0).addBox(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -6.0F));

        PartDefinition neck_claw = head.addOrReplaceChild("neck_claw", CubeListBuilder.create().texOffs(208, 0).addBox(-1.156F, -0.952F, -1.02F, 2.312F, 1.904F, 2.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2F, -8.0F, -2.6F, 0.16F, 0.2F, -0.62F));

        PartDefinition neck_claw_palm_r1 = neck_claw.addOrReplaceChild("neck_claw_palm_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.816F, -0.1F, -0.68F, 1.632F, 1.5133F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.34F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_0 = neck_claw.addOrReplaceChild("neck_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.85F, -1.36F, -0.476F, 0.0F, -0.2F, -0.14F));

        PartDefinition neck_claw_finger_0_sharp_tip_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.442F, -3.3728F, -2.108F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition neck_claw_finger_0_blade_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.306F, -1.9394F, -0.7167F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition neck_claw_finger_0_proximal_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition neck_claw_finger_1 = neck_claw.addOrReplaceChild("neck_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.36F, -0.476F, 0.09F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_sharp_tip_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 2.1043F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2432F, -2.652F, -1.7955F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_blade_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.7131F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.4398F, -0.9017F, -2.3711F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_proximal_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.8011F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_2 = neck_claw.addOrReplaceChild("neck_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.85F, -1.36F, -0.476F, 0.18F, 0.2F, 0.14F));

        PartDefinition neck_claw_finger_2_sharp_tip_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.442F, -3.3728F, -2.108F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition neck_claw_finger_2_blade_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.306F, -1.9394F, -0.7167F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition neck_claw_finger_2_proximal_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -2.0F, -5.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -2.0F, 6.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -2.0F, 6.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(118, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(235, 0).addBox(5.7F, -1.0F, -5.5F, 0.75F, 4.8F, 4.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(163, 0).addBox(-1.615F, -1.33F, -1.425F, 3.23F, 2.66F, 2.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7F, -5.5F, -5.8F, -1.5708F, 0.2F, 0.45F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(178, 0).addBox(-1.14F, -0.1F, -0.95F, 2.28F, 2.0348F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.475F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.1875F, -1.9F, -0.665F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.114F, -0.1F, -0.1567F, 0.228F, 2.3183F, 0.3135F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6175F, -4.712F, -2.945F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.3563F, -0.1F, -0.4275F, 0.7125F, 2.9972F, 0.855F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4275F, -2.7094F, -1.0013F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(189, 0).addBox(-0.5938F, -0.1F, -0.665F, 1.1875F, 3.12F, 1.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.9F, -0.665F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.114F, -0.1F, -0.1568F, 0.228F, 2.8604F, 0.3135F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.928F, -3.705F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(203, 0).addBox(-0.3563F, -0.1F, -0.4275F, 0.7125F, 3.711F, 0.855F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.4086F, -1.2597F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(189, 0).addBox(-0.5938F, -0.1F, -0.665F, 1.1875F, 3.8339F, 1.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.1875F, -1.9F, -0.665F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.114F, -0.1F, -0.1567F, 0.228F, 2.3183F, 0.3135F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6175F, -4.712F, -2.945F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.3563F, -0.1F, -0.4275F, 0.7125F, 2.9972F, 0.855F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4275F, -2.7094F, -1.0013F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(189, 0).addBox(-0.5938F, -0.1F, -0.665F, 1.1875F, 3.12F, 1.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -2.0F, -5.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition mooshroom() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 7.2F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(63, 0).addBox(-5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(63, 0).addBox(4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -8.0F));

        PartDefinition head_upper_fang_4_r1 = head.addOrReplaceChild("head_upper_fang_4_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 3.0F, -6.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, 3.0F, -6.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -6.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, 3.0F, -6.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 3.0F, -6.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(29, 0).addBox(-4.0F, -0.2F, -4.5F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.2F, -1.5F, 0.05F, 0.0F, 0.0F));

        PartDefinition skull_claw = head.addOrReplaceChild("skull_claw", CubeListBuilder.create().texOffs(226, 0).addBox(-1.156F, -0.952F, -1.02F, 2.312F, 1.904F, 2.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -3.8F, -1.0F, 0.12F, 0.16F, -0.52F));

        PartDefinition skull_claw_palm_r1 = skull_claw.addOrReplaceChild("skull_claw_palm_r1", CubeListBuilder.create().texOffs(207, 0).addBox(-0.816F, -0.1F, -0.68F, 1.632F, 1.5133F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.34F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_0 = skull_claw.addOrReplaceChild("skull_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.85F, -1.36F, -0.476F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_claw_finger_0_sharp_tip_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.442F, -3.3728F, -2.108F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_claw_finger_0_blade_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.306F, -1.9394F, -0.7167F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_claw_finger_0_proximal_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_claw_finger_1 = skull_claw.addOrReplaceChild("skull_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.36F, -0.476F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_sharp_tip_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 2.1043F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2432F, -2.652F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_blade_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.7131F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.4398F, -0.9017F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_proximal_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.8011F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_2 = skull_claw.addOrReplaceChild("skull_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.85F, -1.36F, -0.476F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_claw_finger_2_sharp_tip_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.442F, -3.3728F, -2.108F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_claw_finger_2_blade_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.306F, -1.9394F, -0.7167F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_claw_finger_2_proximal_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, -6.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(85, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(102, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, 7.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(85, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(102, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 7.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(85, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(102, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(119, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(164, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(239, 0).addBox(-6.35F, -3.2F, -4.0F, 0.8F, 4.8F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_0 = body.addOrReplaceChild("corrupt_mushroom_0", CubeListBuilder.create().texOffs(184, 0).addBox(-2.9F, -4.5F, -2.5F, 5.8F, 1.4F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, -5.0F, 3.0F, -1.5708F, 0.18F, 0.08F));

        PartDefinition corrupt_mushroom_0_stalk_r1 = corrupt_mushroom_0.addOrReplaceChild("corrupt_mushroom_0_stalk_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.6F, -0.1F, -0.7F, 1.2F, 4.0F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 3.1416F));

        PartDefinition corrupt_mushroom_0_claw = corrupt_mushroom_0.addOrReplaceChild("corrupt_mushroom_0_claw", CubeListBuilder.create().texOffs(207, 0).addBox(-0.884F, -0.728F, -0.78F, 1.768F, 1.456F, 1.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.2F, 0.2F, 0.16F, 0.18F, -0.3F));

        PartDefinition corrupt_mushroom_0_claw_palm_r1 = corrupt_mushroom_0_claw.addOrReplaceChild("corrupt_mushroom_0_claw_palm_r1", CubeListBuilder.create().texOffs(207, 0).addBox(-0.624F, -0.1F, -0.52F, 1.248F, 1.2043F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.26F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_0_claw_finger_0 = corrupt_mushroom_0_claw.addOrReplaceChild("corrupt_mushroom_0_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.65F, -1.04F, -0.364F, 0.0F, -0.2F, -0.14F));

        PartDefinition corrupt_mushroom_0_claw_finger_0_sharp_tip_r1 = corrupt_mushroom_0_claw_finger_0.addOrReplaceChild("corrupt_mushroom_0_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.3595F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.338F, -2.5792F, -1.612F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition corrupt_mushroom_0_claw_finger_0_blade_r1 = corrupt_mushroom_0_claw_finger_0.addOrReplaceChild("corrupt_mushroom_0_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 1.7311F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.234F, -1.483F, -0.5481F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition corrupt_mushroom_0_claw_finger_0_proximal_r1 = corrupt_mushroom_0_claw_finger_0.addOrReplaceChild("corrupt_mushroom_0_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 1.7983F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition corrupt_mushroom_0_claw_finger_1 = corrupt_mushroom_0_claw.addOrReplaceChild("corrupt_mushroom_0_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.04F, -0.364F, 0.09F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_0_claw_finger_1_sharp_tip_r1 = corrupt_mushroom_0_claw_finger_1.addOrReplaceChild("corrupt_mushroom_0_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.6562F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2448F, -2.028F, -1.7955F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_0_claw_finger_1_blade_r1 = corrupt_mushroom_0_claw_finger_1.addOrReplaceChild("corrupt_mushroom_0_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 2.1218F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.8658F, -0.6895F, -2.3711F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_0_claw_finger_1_proximal_r1 = corrupt_mushroom_0_claw_finger_1.addOrReplaceChild("corrupt_mushroom_0_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 2.1891F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_0_claw_finger_2 = corrupt_mushroom_0_claw.addOrReplaceChild("corrupt_mushroom_0_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, -1.04F, -0.364F, 0.18F, 0.2F, 0.14F));

        PartDefinition corrupt_mushroom_0_claw_finger_2_sharp_tip_r1 = corrupt_mushroom_0_claw_finger_2.addOrReplaceChild("corrupt_mushroom_0_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.3595F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.338F, -2.5792F, -1.612F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition corrupt_mushroom_0_claw_finger_2_blade_r1 = corrupt_mushroom_0_claw_finger_2.addOrReplaceChild("corrupt_mushroom_0_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 1.7311F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.234F, -1.483F, -0.5481F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition corrupt_mushroom_0_claw_finger_2_proximal_r1 = corrupt_mushroom_0_claw_finger_2.addOrReplaceChild("corrupt_mushroom_0_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 1.7983F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition corrupt_mushroom_1 = body.addOrReplaceChild("corrupt_mushroom_1", CubeListBuilder.create().texOffs(184, 0).addBox(-2.9F, -4.5F, -2.5F, 5.8F, 1.4F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 4.0F, 3.0F, -1.5708F, -0.12F, -0.07F));

        PartDefinition corrupt_mushroom_1_stalk_r1 = corrupt_mushroom_1.addOrReplaceChild("corrupt_mushroom_1_stalk_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.6F, -0.1F, -0.7F, 1.2F, 4.0F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 3.1416F));

        PartDefinition corrupt_mushroom_1_claw = corrupt_mushroom_1.addOrReplaceChild("corrupt_mushroom_1_claw", CubeListBuilder.create().texOffs(207, 0).addBox(-0.884F, -0.728F, -0.78F, 1.768F, 1.456F, 1.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.2F, 0.2F, 0.16F, 0.18F, -0.3F));

        PartDefinition corrupt_mushroom_1_claw_palm_r1 = corrupt_mushroom_1_claw.addOrReplaceChild("corrupt_mushroom_1_claw_palm_r1", CubeListBuilder.create().texOffs(207, 0).addBox(-0.624F, -0.1F, -0.52F, 1.248F, 1.2043F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.26F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_1_claw_finger_0 = corrupt_mushroom_1_claw.addOrReplaceChild("corrupt_mushroom_1_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.65F, -1.04F, -0.364F, 0.0F, -0.2F, -0.14F));

        PartDefinition corrupt_mushroom_1_claw_finger_0_sharp_tip_r1 = corrupt_mushroom_1_claw_finger_0.addOrReplaceChild("corrupt_mushroom_1_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.3595F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.338F, -2.5792F, -1.612F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition corrupt_mushroom_1_claw_finger_0_blade_r1 = corrupt_mushroom_1_claw_finger_0.addOrReplaceChild("corrupt_mushroom_1_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 1.7311F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.234F, -1.483F, -0.5481F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition corrupt_mushroom_1_claw_finger_0_proximal_r1 = corrupt_mushroom_1_claw_finger_0.addOrReplaceChild("corrupt_mushroom_1_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 1.7983F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition corrupt_mushroom_1_claw_finger_1 = corrupt_mushroom_1_claw.addOrReplaceChild("corrupt_mushroom_1_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.04F, -0.364F, 0.09F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_1_claw_finger_1_sharp_tip_r1 = corrupt_mushroom_1_claw_finger_1.addOrReplaceChild("corrupt_mushroom_1_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.6562F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2448F, -2.028F, -1.7955F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_1_claw_finger_1_blade_r1 = corrupt_mushroom_1_claw_finger_1.addOrReplaceChild("corrupt_mushroom_1_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 2.1218F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.8658F, -0.6895F, -2.3711F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_1_claw_finger_1_proximal_r1 = corrupt_mushroom_1_claw_finger_1.addOrReplaceChild("corrupt_mushroom_1_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(221, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 2.1891F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition corrupt_mushroom_1_claw_finger_2 = corrupt_mushroom_1_claw.addOrReplaceChild("corrupt_mushroom_1_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, -1.04F, -0.364F, 0.18F, 0.2F, 0.14F));

        PartDefinition corrupt_mushroom_1_claw_finger_2_sharp_tip_r1 = corrupt_mushroom_1_claw_finger_2.addOrReplaceChild("corrupt_mushroom_1_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.3595F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.338F, -2.5792F, -1.612F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition corrupt_mushroom_1_claw_finger_2_blade_r1 = corrupt_mushroom_1_claw_finger_2.addOrReplaceChild("corrupt_mushroom_1_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 1.7311F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.234F, -1.483F, -0.5481F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition corrupt_mushroom_1_claw_finger_2_proximal_r1 = corrupt_mushroom_1_claw_finger_2.addOrReplaceChild("corrupt_mushroom_1_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 1.7983F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, -6.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(85, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(102, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition mule() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 2.0F, -12.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, -0.25F, -1.9F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition mule_torn_ankle_r1 = right_front_leg_lower.addOrReplaceChild("mule_torn_ankle_r1", CubeListBuilder.create().texOffs(66, 33).addBox(-0.5F, -0.1F, -0.55F, 1.0F, 3.016F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.1067F, 0.0F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -0.25F, -1.9F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 2.0F, 7.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, -0.25F, -1.0F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -0.25F, -1.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition head_parts = corruption_root.addOrReplaceChild("head_parts", CubeListBuilder.create().texOffs(51, 0).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -12.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition head = head_parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(74, 0).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(101, 0).addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, -10.0F, 4.0F, 0.2618F, 0.0F, -0.2618F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(101, 0).addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -10.0F, 4.0F, 0.2618F, 0.0F, 0.2618F));

        PartDefinition mane = head_parts.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(108, 0).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition upper_mouth = head_parts.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mane_claw = head_parts.addOrReplaceChild("mane_claw", CubeListBuilder.create().texOffs(24, 33).addBox(-1.156F, -0.952F, -1.02F, 2.312F, 1.904F, 2.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, -4.0F, 5.4F, -0.18F, 0.15F, -0.45F));

        PartDefinition mane_claw_palm_r1 = mane_claw.addOrReplaceChild("mane_claw_palm_r1", CubeListBuilder.create().texOffs(37, 33).addBox(-0.816F, -0.1F, -0.68F, 1.632F, 1.5133F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.34F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_0 = mane_claw.addOrReplaceChild("mane_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.85F, -1.36F, -0.476F, 0.0F, -0.2F, -0.14F));

        PartDefinition mane_claw_finger_0_sharp_tip_r1 = mane_claw_finger_0.addOrReplaceChild("mane_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(46, 33).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.442F, -3.3728F, -2.108F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition mane_claw_finger_0_blade_r1 = mane_claw_finger_0.addOrReplaceChild("mane_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.306F, -1.9394F, -0.7167F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition mane_claw_finger_0_proximal_r1 = mane_claw_finger_0.addOrReplaceChild("mane_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition mane_claw_finger_1 = mane_claw.addOrReplaceChild("mane_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.36F, -0.476F, 0.09F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_1_sharp_tip_r1 = mane_claw_finger_1.addOrReplaceChild("mane_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 2.1043F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2432F, -2.652F, -1.7955F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_1_blade_r1 = mane_claw_finger_1.addOrReplaceChild("mane_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.7131F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.4398F, -0.9017F, -2.3711F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_1_proximal_r1 = mane_claw_finger_1.addOrReplaceChild("mane_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.8011F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_2 = mane_claw.addOrReplaceChild("mane_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.85F, -1.36F, -0.476F, 0.18F, 0.2F, 0.14F));

        PartDefinition mane_claw_finger_2_sharp_tip_r1 = mane_claw_finger_2.addOrReplaceChild("mane_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(46, 33).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.442F, -3.3728F, -2.108F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition mane_claw_finger_2_blade_r1 = mane_claw_finger_2.addOrReplaceChild("mane_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.306F, -1.9394F, -0.7167F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition mane_claw_finger_2_proximal_r1 = mane_claw_finger_2.addOrReplaceChild("mane_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 2.0F, 7.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-3.0F, -0.25F, -1.0F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(-3.0F, -0.25F, -1.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(136, 0).addBox(-5.05F, -8.05F, -17.05F, 10.1F, 10.1F, 22.1F, new CubeDeformation(0.0F))
        .texOffs(51, 33).addBox(4.6F, -5.0F, -3.0F, 0.9F, 4.2F, 5.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 5.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(201, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(216, 0).addBox(-1.785F, -1.47F, -1.575F, 3.57F, 2.94F, 3.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -7.0F, -10.0F, 0.0F, 0.25F, 0.58F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(233, 0).addBox(-1.26F, -0.1F, -1.05F, 2.52F, 2.2279F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.525F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.3125F, -2.1F, -0.735F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 2.5412F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6825F, -5.208F, -3.255F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.3937F, -0.1F, -0.4725F, 0.7875F, 3.2917F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4725F, -2.9946F, -1.1067F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 3.4273F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.1F, -0.735F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 3.1404F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.552F, -4.095F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(19, 33).addBox(-0.3937F, -0.1F, -0.4725F, 0.7875F, 4.0805F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.7674F, -1.3923F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(10, 33).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 4.2164F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.3125F, -2.1F, -0.735F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 2.5412F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6825F, -5.208F, -3.255F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.3937F, -0.1F, -0.4725F, 0.7875F, 3.2917F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4725F, -2.9946F, -1.1067F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 3.4273F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 2.0F, -12.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-3.0F, -0.25F, -1.9F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(-3.0F, -0.25F, -1.9F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.58F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition ocelot() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
        .texOffs(21, 0).addBox(-1.5F, -0.001F, -4.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(32, 0).addBox(-2.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(39, 0).addBox(1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -9.0F));

        PartDefinition tail1 = corruption_root.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(46, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 8.0F, 0.9F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(51, 0).addBox(-0.48F, -0.18F, 0.02F, 0.96F, 8.16F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.9F, 0.0F, 0.0F));

        PartDefinition tail_torn_tip_r1 = tail2.addOrReplaceChild("tail_torn_tip_r1", CubeListBuilder.create().texOffs(161, 0).addBox(-0.25F, -0.1F, -0.275F, 0.5F, 2.1423F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.7F, 0.0F, -0.1812F, -0.0094F, -0.1032F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.45F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.2F, 2.1F, -5.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(65, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 4.35F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(74, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(83, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 3.37F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.1F, 6.0F, 5.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(92, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 2.43F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.12F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(83, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 3.37F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.1F, 6.0F, 5.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(92, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 2.43F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.12F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-1.0F, -0.25F, 1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.18F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(110, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(152, 0).addBox(1.6F, 9.0F, -6.0F, 0.6F, 3.1F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(131, 0).addBox(-0.765F, -0.63F, -0.675F, 1.53F, 1.26F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6F, 5.0F, -6.2F, -1.5708F, 0.18F, 0.4F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.54F, -0.1F, -0.45F, 1.08F, 1.0691F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.225F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5625F, -0.9F, -0.315F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.054F, -0.1F, -0.0742F, 0.108F, 1.2034F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2925F, -2.232F, -1.395F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.525F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2025F, -1.2834F, -0.4743F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.5831F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.9F, -0.315F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.054F, -0.1F, -0.0742F, 0.108F, 1.4602F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.808F, -1.755F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.1687F, -0.1F, -0.2025F, 0.3375F, 1.8631F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6146F, -0.5967F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.9213F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5625F, -0.9F, -0.315F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.054F, -0.1F, -0.0742F, 0.108F, 1.2034F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2925F, -2.232F, -1.395F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.525F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2025F, -1.2834F, -0.4743F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(147, 0).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.5831F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.45F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.2F, 2.1F, -5.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(65, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 4.35F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(74, 0).addBox(-1.0F, -0.25F, 0.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition panda() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -5.0F, -4.0F, 13.0F, 9.2F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(95, 0).addBox(-3.5F, 0.0F, -6.0F, 7.0F, 4.2F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(133, 0).addBox(3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(133, 0).addBox(-8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -17.0F));

        PartDefinition head_upper_fang_4_r1 = head.addOrReplaceChild("head_upper_fang_4_r1", CubeListBuilder.create().texOffs(90, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 4.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(90, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 4.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(90, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(90, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(90, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 4.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(45, 0).addBox(-6.5F, -0.2F, -6.75F, 13.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-3.5F, -0.2F, -8.75F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.2F, 2.75F, 0.05F, 0.0F, 0.0F));

        PartDefinition panda_muzzle_fang_4_r1 = head_jaw.addOrReplaceChild("panda_muzzle_fang_4_r1", CubeListBuilder.create().texOffs(90, 0).addBox(-0.14F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(90, 0).addBox(-1.54F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(90, 0).addBox(-2.94F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(90, 0).addBox(-4.34F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(90, 0).addBox(-5.74F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.0F, -8.85F, -2.9889F, 0.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(146, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.93F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.5F, 3.0F, -9.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(171, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 3.87F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.68F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(196, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition forearm_claw = right_front_leg_lower.addOrReplaceChild("forearm_claw", CubeListBuilder.create().texOffs(130, 20).addBox(-1.156F, -0.952F, -1.02F, 2.312F, 1.904F, 2.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7F, 1.0F, -1.5F, 0.6F, 0.18F, 0.48F));

        PartDefinition forearm_claw_palm_r1 = forearm_claw.addOrReplaceChild("forearm_claw_palm_r1", CubeListBuilder.create().texOffs(143, 20).addBox(-0.816F, -0.1F, -0.68F, 1.632F, 1.5133F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.34F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_0 = forearm_claw.addOrReplaceChild("forearm_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.85F, -1.36F, -0.476F, 0.0F, -0.2F, -0.14F));

        PartDefinition forearm_claw_finger_0_sharp_tip_r1 = forearm_claw_finger_0.addOrReplaceChild("forearm_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(152, 20).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.442F, -3.3728F, -2.108F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition forearm_claw_finger_0_blade_r1 = forearm_claw_finger_0.addOrReplaceChild("forearm_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.306F, -1.9394F, -0.7167F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition forearm_claw_finger_0_proximal_r1 = forearm_claw_finger_0.addOrReplaceChild("forearm_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition forearm_claw_finger_1 = forearm_claw.addOrReplaceChild("forearm_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.36F, -0.476F, 0.09F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_1_sharp_tip_r1 = forearm_claw_finger_1.addOrReplaceChild("forearm_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 2.1043F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2432F, -2.652F, -1.7955F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_1_blade_r1 = forearm_claw_finger_1.addOrReplaceChild("forearm_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.7131F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.4398F, -0.9017F, -2.3711F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_1_proximal_r1 = forearm_claw_finger_1.addOrReplaceChild("forearm_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.8011F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition forearm_claw_finger_2 = forearm_claw.addOrReplaceChild("forearm_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.85F, -1.36F, -0.476F, 0.18F, 0.2F, 0.14F));

        PartDefinition forearm_claw_finger_2_sharp_tip_r1 = forearm_claw_finger_2.addOrReplaceChild("forearm_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(152, 20).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.442F, -3.3728F, -2.108F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition forearm_claw_finger_2_blade_r1 = forearm_claw_finger_2.addOrReplaceChild("forearm_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.306F, -1.9394F, -0.7167F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition forearm_claw_finger_2_proximal_r1 = forearm_claw_finger_2.addOrReplaceChild("forearm_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(146, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.93F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.5F, 3.0F, 9.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(171, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 3.87F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.68F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(196, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(146, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.93F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, 3.0F, 9.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(171, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 3.87F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.68F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(196, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F, new CubeDeformation(0.0F))
        .texOffs(157, 20).addBox(8.9F, -4.5F, -4.0F, 1.1F, 6.2F, 5.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(65, 20).addBox(-2.125F, -1.75F, -1.875F, 4.25F, 3.5F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.8F, -9.0F, 4.8F, -1.5708F, 0.2F, 0.52F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(84, 20).addBox(-1.5F, -0.1F, -1.25F, 3.0F, 2.6142F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.625F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5625F, -2.5F, -0.875F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.15F, -0.1F, -0.2063F, 0.3F, 2.9872F, 0.4125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8125F, -6.2F, -3.875F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(106, 20).addBox(-0.4688F, -0.1F, -0.5625F, 0.9375F, 3.8806F, 1.125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5625F, -3.565F, -1.3175F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(97, 20).addBox(-0.7813F, -0.1F, -0.875F, 1.5625F, 4.0421F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.5F, -0.875F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(125, 20).addBox(-0.15F, -0.1F, -0.2063F, 0.3F, 3.7005F, 0.4125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.8F, -4.875F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(118, 20).addBox(-0.4688F, -0.1F, -0.5625F, 0.9375F, 4.8197F, 1.125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.485F, -1.6575F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(97, 20).addBox(-0.7813F, -0.1F, -0.875F, 1.5625F, 4.9815F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5625F, -2.5F, -0.875F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.15F, -0.1F, -0.2063F, 0.3F, 2.9872F, 0.4125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8125F, -6.2F, -3.875F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(106, 20).addBox(-0.4688F, -0.1F, -0.5625F, 0.9375F, 3.8806F, 1.125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5625F, -3.565F, -1.3175F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(97, 20).addBox(-0.7813F, -0.1F, -0.875F, 1.5625F, 4.0421F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(146, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.93F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, 3.0F, -9.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(171, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 3.87F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.68F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(196, 0).addBox(-3.0F, -0.25F, -3.0F, 6.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.62F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition parrot() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.69F, -2.76F));

        PartDefinition beak1 = head.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(9, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -1.5F));

        PartDefinition beak2 = head.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(14, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.75F, -2.45F));

        PartDefinition feather = head.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(19, 0).addBox(-0.04F, -4.0F, -2.0F, 0.08F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.15F, 0.15F));

        PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(30, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -1.0F));

        PartDefinition crest_claw = head.addOrReplaceChild("crest_claw", CubeListBuilder.create().texOffs(84, 0).addBox(-0.646F, -0.532F, -0.57F, 1.292F, 1.064F, 1.14F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.65F, -2.1F, 0.7F, 0.12F, 0.22F, 0.55F));

        PartDefinition crest_claw_palm_r1 = crest_claw.addOrReplaceChild("crest_claw_palm_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.456F, -0.1F, -0.38F, 0.912F, 0.9339F, 0.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.19F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition crest_claw_finger_0 = crest_claw.addOrReplaceChild("crest_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.475F, -0.76F, -0.266F, 0.0F, -0.2F, -0.14F));

        PartDefinition crest_claw_finger_0_sharp_tip_r1 = crest_claw_finger_0.addOrReplaceChild("crest_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.0456F, -0.1F, -0.0627F, 0.0912F, 1.0473F, 0.1254F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.247F, -1.8848F, -1.178F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition crest_claw_finger_0_blade_r1 = crest_claw_finger_0.addOrReplaceChild("crest_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.1425F, -0.1F, -0.171F, 0.285F, 1.3189F, 0.342F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.171F, -1.0838F, -0.4005F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition crest_claw_finger_0_proximal_r1 = crest_claw_finger_0.addOrReplaceChild("crest_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.2375F, -0.1F, -0.266F, 0.475F, 1.368F, 0.532F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition crest_claw_finger_1 = crest_claw.addOrReplaceChild("crest_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.76F, -0.266F, 0.09F, 0.0F, 0.0F));

        PartDefinition crest_claw_finger_1_sharp_tip_r1 = crest_claw_finger_1.addOrReplaceChild("crest_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.0456F, -0.1F, -0.0627F, 0.0912F, 1.2642F, 0.1254F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.3712F, -1.482F, -1.7955F, 0.0F, 0.0F));

        PartDefinition crest_claw_finger_1_blade_r1 = crest_claw_finger_1.addOrReplaceChild("crest_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.1425F, -0.1F, -0.171F, 0.285F, 1.6044F, 0.342F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3634F, -0.5039F, -2.3711F, 0.0F, 0.0F));

        PartDefinition crest_claw_finger_1_proximal_r1 = crest_claw_finger_1.addOrReplaceChild("crest_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.2375F, -0.1F, -0.266F, 0.475F, 1.6536F, 0.532F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition crest_claw_finger_2 = crest_claw.addOrReplaceChild("crest_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.475F, -0.76F, -0.266F, 0.18F, 0.2F, 0.14F));

        PartDefinition crest_claw_finger_2_sharp_tip_r1 = crest_claw_finger_2.addOrReplaceChild("crest_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.0456F, -0.1F, -0.0627F, 0.0912F, 1.0473F, 0.1254F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.247F, -1.8848F, -1.178F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition crest_claw_finger_2_blade_r1 = crest_claw_finger_2.addOrReplaceChild("crest_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.1425F, -0.1F, -0.171F, 0.285F, 1.3189F, 0.342F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.171F, -1.0838F, -0.4005F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition crest_claw_finger_2_proximal_r1 = crest_claw_finger_2.addOrReplaceChild("crest_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.2375F, -0.1F, -0.266F, 0.475F, 1.368F, 0.532F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition beak_jaw = head.addOrReplaceChild("beak_jaw", CubeListBuilder.create().texOffs(108, 0).addBox(-0.46F, 0.0F, -1.2F, 0.92F, 0.75F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, -1.4F, 0.12F, 0.0F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(43, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.29F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 10.0F, -1.05F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(48, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 0.97F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.04F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(48, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 0.49F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.72F, 0.0F));

        PartDefinition right_wing = corruption_root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(53, 0).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 4.94F, -2.76F));

        PartDefinition right_wing_torn_hook_r1 = right_wing.addOrReplaceChild("right_wing_torn_hook_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 1.9F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -0.7F, -0.49F, 0.0F, 0.0F));

        PartDefinition right_wing_tendon_r1 = right_wing.addOrReplaceChild("right_wing_tendon_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 4.419F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.1F, 0.0F, 0.095F, 0.0F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(43, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.29F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 10.0F, -1.05F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(48, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 0.97F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.04F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(48, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 0.49F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.72F, 0.0F));

        PartDefinition tail = corruption_root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(62, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.07F, 1.16F));

        PartDefinition left_wing = corruption_root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(53, 0).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 4.94F, -2.76F));

        PartDefinition left_wing_torn_hook_r1 = left_wing.addOrReplaceChild("left_wing_torn_hook_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 1.9F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -0.7F, -0.49F, 0.0F, 0.0F));

        PartDefinition left_wing_tendon_r1 = left_wing.addOrReplaceChild("left_wing_tendon_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 4.419F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.1F, 0.0F, 0.095F, 0.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(71, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.5F, -3.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition phantom() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.1F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(29, 0).addBox(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -7.0F, 0.2F, 0.0F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(220, 0).addBox(-3.5F, -0.2F, -4.4F, 6.0F, 0.8F, 4.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.8F, -0.7F, 0.12F, 0.0F, 0.0F));

        PartDefinition phantom_fang_4_r1 = head_jaw.addOrReplaceChild("phantom_fang_4_r1", CubeListBuilder.create().texOffs(243, 0).addBox(-0.125F, -0.1F, -0.16F, 0.25F, 0.9762F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(243, 0).addBox(-1.325F, -0.1F, -0.16F, 0.25F, 0.9762F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(243, 0).addBox(-2.525F, -0.1F, -0.16F, 0.25F, 0.9762F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(243, 0).addBox(-3.725F, -0.1F, -0.16F, 0.25F, 0.9762F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(243, 0).addBox(-4.925F, -0.1F, -0.16F, 0.25F, 0.9762F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -4.35F, -2.881F, 0.0F, 0.0F));

        PartDefinition right_wing_base = body.addOrReplaceChild("right_wing_base", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -2.0F, -8.0F, 0.0F, 0.0F, -0.1F));

        PartDefinition right_wing_tip = right_wing_base.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(85, 0).mirror().addBox(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F));

        PartDefinition right_wing_tip_edge_hook_2_r1 = right_wing_tip.addOrReplaceChild("right_wing_tip_edge_hook_2_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.24F, -0.1F, -0.275F, 0.48F, 3.369F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.8F, 0.25F, 7.7F, 0.5341F, -0.1284F, 0.4617F));

        PartDefinition right_wing_tip_edge_hook_1_r1 = right_wing_tip.addOrReplaceChild("right_wing_tip_edge_hook_1_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.24F, -0.1F, -0.275F, 0.48F, 3.3261F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4F, 0.25F, 7.7F, 0.5418F, -0.1218F, 0.4323F));

        PartDefinition right_wing_tip_edge_hook_0_r1 = right_wing_tip.addOrReplaceChild("right_wing_tip_edge_hook_0_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.24F, -0.1F, -0.275F, 0.48F, 3.2859F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.25F, 7.7F, 0.5491F, -0.1147F, 0.4022F));

        PartDefinition right_wing_tip_leading_tendon_r1 = right_wing_tip.addOrReplaceChild("right_wing_tip_leading_tendon_r1", CubeListBuilder.create().texOffs(210, 0).addBox(-0.325F, -0.1F, -0.375F, 0.65F, 11.7035F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.2F, 0.0174F, -0.0177F, 1.5879F));

        PartDefinition tail_base = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(130, 0).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 1.0F));

        PartDefinition tail_tip = tail_base.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(149, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 6.0F));

        PartDefinition left_wing_base = body.addOrReplaceChild("left_wing_base", CubeListBuilder.create().texOffs(54, 0).addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -2.0F, -8.0F, 0.0F, 0.0F, 0.1F));

        PartDefinition left_wing_tip = left_wing_base.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(85, 0).addBox(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F));

        PartDefinition left_wing_tip_edge_hook_2_r1 = left_wing_tip.addOrReplaceChild("left_wing_tip_edge_hook_2_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.24F, -0.1F, -0.275F, 0.48F, 3.369F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.8F, 0.25F, 7.7F, 0.5341F, 0.1284F, -0.4617F));

        PartDefinition left_wing_tip_edge_hook_1_r1 = left_wing_tip.addOrReplaceChild("left_wing_tip_edge_hook_1_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.24F, -0.1F, -0.275F, 0.48F, 3.3261F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.4F, 0.25F, 7.7F, 0.5418F, 0.1218F, -0.4323F));

        PartDefinition left_wing_tip_edge_hook_0_r1 = left_wing_tip.addOrReplaceChild("left_wing_tip_edge_hook_0_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.24F, -0.1F, -0.275F, 0.48F, 3.2859F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.25F, 7.7F, 0.5491F, 0.1147F, -0.4022F));

        PartDefinition left_wing_tip_leading_tendon_r1 = left_wing_tip.addOrReplaceChild("left_wing_tip_leading_tendon_r1", CubeListBuilder.create().texOffs(210, 0).addBox(-0.325F, -0.1F, -0.375F, 0.65F, 11.7035F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.2F, 0.0174F, 0.0177F, -1.5879F));

        PartDefinition dorsal_claw = body.addOrReplaceChild("dorsal_claw", CubeListBuilder.create().texOffs(164, 0).addBox(-1.292F, -1.064F, -1.14F, 2.584F, 2.128F, 2.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, -2.0F, -3.0F, 0.1F, 0.14F, -0.45F));

        PartDefinition dorsal_claw_palm_r1 = dorsal_claw.addOrReplaceChild("dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.912F, -0.1F, -0.76F, 1.824F, 1.6678F, 1.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.38F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_0 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.95F, -1.52F, -0.532F, 0.0F, -0.2F, -0.14F));

        PartDefinition dorsal_claw_finger_0_sharp_tip_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.494F, -3.7696F, -2.356F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition dorsal_claw_finger_0_blade_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(193, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.342F, -2.1675F, -0.801F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition dorsal_claw_finger_0_proximal_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(186, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition dorsal_claw_finger_1 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.52F, -0.532F, 0.09F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_sharp_tip_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(193, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 2.3283F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.7424F, -2.964F, -1.7955F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_blade_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(193, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 3.0088F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7269F, -1.0078F, -2.3711F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_proximal_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(203, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 3.1071F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_2 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.95F, -1.52F, -0.532F, 0.18F, 0.2F, 0.14F));

        PartDefinition dorsal_claw_finger_2_sharp_tip_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(198, 0).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.494F, -3.7696F, -2.356F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition dorsal_claw_finger_2_blade_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(193, 0).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.342F, -2.1675F, -0.801F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition dorsal_claw_finger_2_proximal_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(186, 0).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition polar_bear() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 6.2F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(63, 0).addBox(-2.5F, 1.0F, -6.0F, 5.0F, 2.2F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(97, 0).addBox(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(97, 0).mirror().addBox(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -2.0F, -16.0F));

        PartDefinition head_upper_fang_4_r1 = head.addOrReplaceChild("head_upper_fang_4_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(29, 0).addBox(-3.5F, -0.2F, -5.25F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(80, 0).addBox(-2.5F, -0.2F, -8.25F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.2F, 2.25F, 0.05F, 0.0F, 0.0F));

        PartDefinition polar_bear_muzzle_fang_4_r1 = head_jaw.addOrReplaceChild("polar_bear_muzzle_fang_4_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-0.14F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(58, 0).addBox(-1.14F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(58, 0).addBox(-2.14F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(58, 0).addBox(-3.14F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F))
        .texOffs(58, 0).addBox(-4.14F, -0.1F, -0.18F, 0.28F, 0.8576F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -8.35F, -2.9889F, 0.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(104, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.45F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 2.0F, -8.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(125, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 4.35F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(203, 15).addBox(-2.2F, 0.4F, -1.7F, 0.6F, 2.7F, 3.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(146, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition right_paw_claw_tip_2_r1 = right_front_leg_foot.addOrReplaceChild("right_paw_claw_tip_2_r1", CubeListBuilder.create().texOffs(214, 15).addBox(-0.08F, -0.1F, -0.11F, 0.16F, 1.1827F, 0.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.21F, -0.65F, -3.7F, -0.9677F, 0.1516F, 0.287F));

        PartDefinition right_paw_claw_2_r1 = right_front_leg_foot.addOrReplaceChild("right_paw_claw_2_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 2.2574F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, -0.18F, -1.7F, -1.798F, -0.0674F, -0.0536F));

        PartDefinition right_paw_claw_tip_1_r1 = right_front_leg_foot.addOrReplaceChild("right_paw_claw_tip_1_r1", CubeListBuilder.create().texOffs(214, 15).addBox(-0.08F, -0.1F, -0.11F, 0.16F, 1.1434F, 0.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.65F, -3.7F, -1.0122F, 0.0F, 0.0F));

        PartDefinition right_paw_claw_1_r1 = right_front_leg_foot.addOrReplaceChild("right_paw_claw_1_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 2.2545F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.18F, -1.7F, -1.8016F, 0.0F, 0.0F));

        PartDefinition right_paw_claw_tip_0_r1 = right_front_leg_foot.addOrReplaceChild("right_paw_claw_tip_0_r1", CubeListBuilder.create().texOffs(214, 15).addBox(-0.08F, -0.1F, -0.11F, 0.16F, 1.1827F, 0.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.21F, -0.65F, -3.7F, -0.9677F, -0.1516F, -0.287F));

        PartDefinition right_paw_claw_0_r1 = right_front_leg_foot.addOrReplaceChild("right_paw_claw_0_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 2.2574F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -0.18F, -1.7F, -1.798F, 0.0674F, 0.0536F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(167, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 2.0F, 6.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(192, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 4.35F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(217, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(167, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 2.0F, 6.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(192, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 4.35F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(217, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(51, 15).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(190, 15).addBox(-4.3F, -21.0F, -4.0F, 0.85F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -3.0F, 12.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(96, 15).addBox(-2.04F, -1.68F, -1.8F, 4.08F, 3.36F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -18.0F, 2.9F, -1.5708F, 0.16F, 0.5F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(115, 15).addBox(-1.44F, -0.1F, -1.2F, 2.88F, 2.5176F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, -2.4F, -0.84F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 2.8757F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.78F, -5.952F, -3.72F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(137, 15).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 3.7333F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.54F, -3.4224F, -1.2648F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(128, 15).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 3.8884F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.4F, -0.84F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(165, 15).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 3.5605F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.488F, -4.68F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(158, 15).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 4.6349F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.3056F, -1.5912F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(149, 15).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 4.7902F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, -2.4F, -0.84F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 2.8757F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.78F, -5.952F, -3.72F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(137, 15).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 3.7333F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.54F, -3.4224F, -1.2648F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(128, 15).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 3.8884F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition flank_claw = body.addOrReplaceChild("flank_claw", CubeListBuilder.create().texOffs(115, 15).addBox(-1.428F, -1.176F, -1.26F, 2.856F, 2.352F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.4F, -4.0F, 2.6F, -1.5708F, -0.15F, -0.5F));

        PartDefinition flank_claw_palm_r1 = flank_claw.addOrReplaceChild("flank_claw_palm_r1", CubeListBuilder.create().texOffs(170, 15).addBox(-1.008F, -0.1F, -0.84F, 2.016F, 1.8223F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.42F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_0 = flank_claw.addOrReplaceChild("flank_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.05F, -1.68F, -0.588F, 0.0F, -0.2F, -0.14F));

        PartDefinition flank_claw_finger_0_sharp_tip_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.1008F, -0.1F, -0.1386F, 0.2016F, 2.073F, 0.2772F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.546F, -4.1664F, -2.604F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition flank_claw_finger_0_blade_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.315F, -0.1F, -0.378F, 0.63F, 2.6733F, 0.756F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.378F, -2.3957F, -0.8854F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition flank_claw_finger_0_proximal_r1 = flank_claw_finger_0.addOrReplaceChild("flank_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(181, 15).addBox(-0.525F, -0.1F, -0.588F, 1.05F, 2.7819F, 1.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition flank_claw_finger_1 = flank_claw.addOrReplaceChild("flank_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.68F, -0.588F, 0.09F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_sharp_tip_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.1008F, -0.1F, -0.1386F, 0.2016F, 2.5523F, 0.2772F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.2416F, -3.276F, -1.7955F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_blade_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(165, 15).addBox(-0.315F, -0.1F, -0.378F, 0.63F, 3.3044F, 0.756F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0139F, -1.1138F, -2.3711F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_1_proximal_r1 = flank_claw_finger_1.addOrReplaceChild("flank_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(128, 15).addBox(-0.525F, -0.1F, -0.588F, 1.05F, 3.4132F, 1.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition flank_claw_finger_2 = flank_claw.addOrReplaceChild("flank_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.05F, -1.68F, -0.588F, 0.18F, 0.2F, 0.14F));

        PartDefinition flank_claw_finger_2_sharp_tip_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.1008F, -0.1F, -0.1386F, 0.2016F, 2.073F, 0.2772F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.546F, -4.1664F, -2.604F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition flank_claw_finger_2_blade_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.315F, -0.1F, -0.378F, 0.63F, 2.6733F, 0.756F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.378F, -2.3957F, -0.8854F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition flank_claw_finger_2_proximal_r1 = flank_claw_finger_2.addOrReplaceChild("flank_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(181, 15).addBox(-0.525F, -0.1F, -0.588F, 1.05F, 2.7819F, 1.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(104, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.45F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 2.0F, -8.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(125, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 4.35F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(146, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition left_paw_claw_tip_2_r1 = left_front_leg_foot.addOrReplaceChild("left_paw_claw_tip_2_r1", CubeListBuilder.create().texOffs(214, 15).addBox(-0.08F, -0.1F, -0.11F, 0.16F, 1.1827F, 0.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.21F, -0.65F, -3.7F, -0.9677F, 0.1516F, 0.287F));

        PartDefinition left_paw_claw_2_r1 = left_front_leg_foot.addOrReplaceChild("left_paw_claw_2_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 2.2574F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, -0.18F, -1.7F, -1.798F, -0.0674F, -0.0536F));

        PartDefinition left_paw_claw_tip_1_r1 = left_front_leg_foot.addOrReplaceChild("left_paw_claw_tip_1_r1", CubeListBuilder.create().texOffs(214, 15).addBox(-0.08F, -0.1F, -0.11F, 0.16F, 1.1434F, 0.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.65F, -3.7F, -1.0122F, 0.0F, 0.0F));

        PartDefinition left_paw_claw_1_r1 = left_front_leg_foot.addOrReplaceChild("left_paw_claw_1_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 2.2545F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.18F, -1.7F, -1.8016F, 0.0F, 0.0F));

        PartDefinition left_paw_claw_tip_0_r1 = left_front_leg_foot.addOrReplaceChild("left_paw_claw_tip_0_r1", CubeListBuilder.create().texOffs(214, 15).addBox(-0.08F, -0.1F, -0.11F, 0.16F, 1.1827F, 0.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.21F, -0.65F, -3.7F, -0.9677F, -0.1516F, -0.287F));

        PartDefinition left_paw_claw_0_r1 = left_front_leg_foot.addOrReplaceChild("left_paw_claw_0_r1", CubeListBuilder.create().texOffs(144, 15).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 2.2574F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -0.18F, -1.7F, -1.798F, 0.0674F, 0.0536F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition pufferfish() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition puffer_state_0 = corruption_root.addOrReplaceChild("puffer_state_0", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition puffer_0_right_eye = puffer_state_0.addOrReplaceChild("puffer_0_right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition puffer_0_left_fin = puffer_state_0.addOrReplaceChild("puffer_0_left_fin", CubeListBuilder.create().texOffs(5, 0).addBox(0.0F, -0.04F, 0.0F, 1.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 22.0F, -1.5F));

        PartDefinition puffer_0_right_fin = puffer_state_0.addOrReplaceChild("puffer_0_right_fin", CubeListBuilder.create().texOffs(5, 0).addBox(-1.0F, -0.04F, 0.0F, 1.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 22.0F, -1.5F));

        PartDefinition puffer_0_left_eye = puffer_state_0.addOrReplaceChild("puffer_0_left_eye", CubeListBuilder.create().texOffs(12, 0).addBox(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition puffer_0_body = puffer_state_0.addOrReplaceChild("puffer_0_body", CubeListBuilder.create().texOffs(17, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(44, 17).addBox(-1.6F, -1.3F, -0.75F, 0.23F, 0.84F, 1.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));

        PartDefinition puffer_0_dorsal_claw = puffer_0_body.addOrReplaceChild("puffer_0_dorsal_claw", CubeListBuilder.create().texOffs(29, 17).addBox(-0.374F, -0.308F, -0.33F, 0.748F, 0.616F, 0.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.675F, -1.75F, 0.15F, 0.15F, 0.22F, -0.45F));

        PartDefinition puffer_0_dorsal_claw_palm_r1 = puffer_0_dorsal_claw.addOrReplaceChild("puffer_0_dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.264F, -0.1F, -0.22F, 0.528F, 0.6249F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.11F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition puffer_0_dorsal_claw_finger_0 = puffer_0_dorsal_claw.addOrReplaceChild("puffer_0_dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.275F, -0.44F, -0.154F, 0.0F, -0.2F, -0.14F));

        PartDefinition puffer_0_dorsal_claw_finger_0_sharp_tip_r1 = puffer_0_dorsal_claw_finger_0.addOrReplaceChild("puffer_0_dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.6905F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.143F, -1.0912F, -0.682F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition puffer_0_dorsal_claw_finger_0_blade_r1 = puffer_0_dorsal_claw_finger_0.addOrReplaceChild("puffer_0_dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 0.8478F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.099F, -0.6274F, -0.2319F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition puffer_0_dorsal_claw_finger_0_proximal_r1 = puffer_0_dorsal_claw_finger_0.addOrReplaceChild("puffer_0_dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 0.8762F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition puffer_0_dorsal_claw_finger_1 = puffer_0_dorsal_claw.addOrReplaceChild("puffer_0_dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.44F, -0.154F, 0.09F, 0.0F, 0.0F));

        PartDefinition puffer_0_dorsal_claw_finger_1_sharp_tip_r1 = puffer_0_dorsal_claw_finger_1.addOrReplaceChild("puffer_0_dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.8161F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3728F, -0.858F, -1.7955F, 0.0F, 0.0F));

        PartDefinition puffer_0_dorsal_claw_finger_1_blade_r1 = puffer_0_dorsal_claw_finger_1.addOrReplaceChild("puffer_0_dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 1.0131F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7894F, -0.2917F, -2.3711F, 0.0F, 0.0F));

        PartDefinition puffer_0_dorsal_claw_finger_1_proximal_r1 = puffer_0_dorsal_claw_finger_1.addOrReplaceChild("puffer_0_dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 1.0415F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition puffer_0_dorsal_claw_finger_2 = puffer_0_dorsal_claw.addOrReplaceChild("puffer_0_dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.275F, -0.44F, -0.154F, 0.18F, 0.2F, 0.14F));

        PartDefinition puffer_0_dorsal_claw_finger_2_sharp_tip_r1 = puffer_0_dorsal_claw_finger_2.addOrReplaceChild("puffer_0_dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.6905F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.143F, -1.0912F, -0.682F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition puffer_0_dorsal_claw_finger_2_blade_r1 = puffer_0_dorsal_claw_finger_2.addOrReplaceChild("puffer_0_dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 0.8478F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.099F, -0.6274F, -0.2319F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition puffer_0_dorsal_claw_finger_2_proximal_r1 = puffer_0_dorsal_claw_finger_2.addOrReplaceChild("puffer_0_dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 0.8762F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition puffer_0_back_fin = puffer_state_0.addOrReplaceChild("puffer_0_back_fin", CubeListBuilder.create().texOffs(30, 0).addBox(-1.5F, -0.04F, 0.0F, 3.0F, 0.08F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 1.5F));

        PartDefinition puffer_state_1 = corruption_root.addOrReplaceChild("puffer_state_1", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition puffer_1_left_blue_fin = puffer_state_1.addOrReplaceChild("puffer_1_left_blue_fin", CubeListBuilder.create().texOffs(43, 0).addBox(0.0F, -0.04F, 0.0F, 2.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 17.0F, -1.5F));

        PartDefinition puffer_1_top_back_fin = puffer_state_1.addOrReplaceChild("puffer_1_top_back_fin", CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.0F, 2.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_1_left_back_fin = puffer_state_1.addOrReplaceChild("puffer_1_left_back_fin", CubeListBuilder.create().texOffs(65, 0).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 22.0F, 2.5F, 0.0F, -0.7854F, 0.0F));

        PartDefinition puffer_1_left_front_fin = puffer_state_1.addOrReplaceChild("puffer_1_left_front_fin", CubeListBuilder.create().texOffs(70, 0).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 22.0F, -2.5F, 0.0F, 0.7854F, 0.0F));

        PartDefinition puffer_1_bottom_front_fin = puffer_state_1.addOrReplaceChild("puffer_1_bottom_front_fin", CubeListBuilder.create().texOffs(75, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -2.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_1_right_front_fin = puffer_state_1.addOrReplaceChild("puffer_1_right_front_fin", CubeListBuilder.create().texOffs(88, 0).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 22.0F, -2.5F, 0.0F, -0.7854F, 0.0F));

        PartDefinition puffer_1_right_back_fin = puffer_state_1.addOrReplaceChild("puffer_1_right_back_fin", CubeListBuilder.create().texOffs(88, 0).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 22.0F, 2.5F, 0.0F, 0.7854F, 0.0F));

        PartDefinition puffer_1_body = puffer_state_1.addOrReplaceChild("puffer_1_body", CubeListBuilder.create().texOffs(93, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
        .texOffs(60, 17).addBox(-2.6F, -3.25F, -1.25F, 0.23F, 2.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition puffer_1_flank_hook_1_2_r1 = puffer_1_body.addOrReplaceChild("puffer_1_flank_hook_1_2_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.126F, -0.1F, -0.144F, 0.252F, 1.84F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.35F, -3.45F, 0.25F, -1.0117F, -0.628F, -1.0605F));

        PartDefinition puffer_1_flank_hook_1_1_r1 = puffer_1_body.addOrReplaceChild("puffer_1_flank_hook_1_1_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.126F, -0.1F, -0.144F, 0.252F, 1.84F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.35F, -2.35F, 0.25F, -1.0117F, -0.628F, -1.0605F));

        PartDefinition puffer_1_flank_hook_1_0_r1 = puffer_1_body.addOrReplaceChild("puffer_1_flank_hook_1_0_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.126F, -0.1F, -0.144F, 0.252F, 1.84F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.35F, -1.25F, 0.25F, -1.0117F, -0.628F, -1.0605F));

        PartDefinition puffer_1_flank_hook_m1_2_r1 = puffer_1_body.addOrReplaceChild("puffer_1_flank_hook_m1_2_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.126F, -0.1F, -0.144F, 0.252F, 1.84F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.35F, -3.45F, 0.25F, -1.0117F, 0.628F, 1.0605F));

        PartDefinition puffer_1_flank_hook_m1_1_r1 = puffer_1_body.addOrReplaceChild("puffer_1_flank_hook_m1_1_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.126F, -0.1F, -0.144F, 0.252F, 1.84F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.35F, -2.35F, 0.25F, -1.0117F, 0.628F, 1.0605F));

        PartDefinition puffer_1_flank_hook_m1_0_r1 = puffer_1_body.addOrReplaceChild("puffer_1_flank_hook_m1_0_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.126F, -0.1F, -0.144F, 0.252F, 1.84F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.35F, -1.25F, 0.25F, -1.0117F, 0.628F, 1.0605F));

        PartDefinition puffer_1_dorsal_claw = puffer_1_body.addOrReplaceChild("puffer_1_dorsal_claw", CubeListBuilder.create().texOffs(51, 17).addBox(-0.612F, -0.504F, -0.54F, 1.224F, 1.008F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.125F, -4.75F, 0.25F, 0.15F, 0.22F, -0.45F));

        PartDefinition puffer_1_dorsal_claw_palm_r1 = puffer_1_dorsal_claw.addOrReplaceChild("puffer_1_dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(29, 17).addBox(-0.432F, -0.1F, -0.36F, 0.864F, 0.8953F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.18F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition puffer_1_dorsal_claw_finger_0 = puffer_1_dorsal_claw.addOrReplaceChild("puffer_1_dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.45F, -0.72F, -0.252F, 0.0F, -0.2F, -0.14F));

        PartDefinition puffer_1_dorsal_claw_finger_0_sharp_tip_r1 = puffer_1_dorsal_claw_finger_0.addOrReplaceChild("puffer_1_dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0432F, -0.1F, -0.0594F, 0.0864F, 1.0027F, 0.1188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.234F, -1.7856F, -1.116F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition puffer_1_dorsal_claw_finger_0_blade_r1 = puffer_1_dorsal_claw_finger_0.addOrReplaceChild("puffer_1_dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.135F, -0.1F, -0.162F, 0.27F, 1.26F, 0.324F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.162F, -1.0267F, -0.3794F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition puffer_1_dorsal_claw_finger_0_proximal_r1 = puffer_1_dorsal_claw_finger_0.addOrReplaceChild("puffer_1_dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.225F, -0.1F, -0.252F, 0.45F, 1.3065F, 0.504F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition puffer_1_dorsal_claw_finger_1 = puffer_1_dorsal_claw.addOrReplaceChild("puffer_1_dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.72F, -0.252F, 0.09F, 0.0F, 0.0F));

        PartDefinition puffer_1_dorsal_claw_finger_1_sharp_tip_r1 = puffer_1_dorsal_claw_finger_1.addOrReplaceChild("puffer_1_dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.0432F, -0.1F, -0.0594F, 0.0864F, 1.2081F, 0.1188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2464F, -1.404F, -1.7955F, 0.0F, 0.0F));

        PartDefinition puffer_1_dorsal_claw_finger_1_blade_r1 = puffer_1_dorsal_claw_finger_1.addOrReplaceChild("puffer_1_dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.135F, -0.1F, -0.162F, 0.27F, 1.5305F, 0.324F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2917F, -0.4774F, -2.3711F, 0.0F, 0.0F));

        PartDefinition puffer_1_dorsal_claw_finger_1_proximal_r1 = puffer_1_dorsal_claw_finger_1.addOrReplaceChild("puffer_1_dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.225F, -0.1F, -0.252F, 0.45F, 1.5771F, 0.504F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition puffer_1_dorsal_claw_finger_2 = puffer_1_dorsal_claw.addOrReplaceChild("puffer_1_dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.45F, -0.72F, -0.252F, 0.18F, 0.2F, 0.14F));

        PartDefinition puffer_1_dorsal_claw_finger_2_sharp_tip_r1 = puffer_1_dorsal_claw_finger_2.addOrReplaceChild("puffer_1_dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-0.0432F, -0.1F, -0.0594F, 0.0864F, 1.0027F, 0.1188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.234F, -1.7856F, -1.116F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition puffer_1_dorsal_claw_finger_2_blade_r1 = puffer_1_dorsal_claw_finger_2.addOrReplaceChild("puffer_1_dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.135F, -0.1F, -0.162F, 0.27F, 1.26F, 0.324F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.162F, -1.0267F, -0.3794F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition puffer_1_dorsal_claw_finger_2_proximal_r1 = puffer_1_dorsal_claw_finger_2.addOrReplaceChild("puffer_1_dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.225F, -0.1F, -0.252F, 0.45F, 1.3065F, 0.504F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition puffer_1_top_front_fin = puffer_state_1.addOrReplaceChild("puffer_1_top_front_fin", CubeListBuilder.create().texOffs(114, 0).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.0F, -2.5F, 0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_1_bottom_back_fin = puffer_state_1.addOrReplaceChild("puffer_1_bottom_back_fin", CubeListBuilder.create().texOffs(127, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 22.0F, 2.5F, 0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_1_right_blue_fin = puffer_state_1.addOrReplaceChild("puffer_1_right_blue_fin", CubeListBuilder.create().texOffs(132, 0).addBox(-2.0F, -0.04F, 0.0F, 2.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 17.0F, -1.5F));

        PartDefinition puffer_state_2 = corruption_root.addOrReplaceChild("puffer_state_2", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition puffer_2_left_blue_fin = puffer_state_2.addOrReplaceChild("puffer_2_left_blue_fin", CubeListBuilder.create().texOffs(141, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 15.0F, -2.0F));

        PartDefinition puffer_2_top_back_fin = puffer_state_2.addOrReplaceChild("puffer_2_top_back_fin", CubeListBuilder.create().texOffs(150, 0).addBox(-4.0F, -1.0F, -0.04F, 8.0F, 1.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_2_top_middle_fin = puffer_state_2.addOrReplaceChild("puffer_2_top_middle_fin", CubeListBuilder.create().texOffs(169, 0).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition puffer_2_bottom_front_fin = puffer_state_2.addOrReplaceChild("puffer_2_bottom_front_fin", CubeListBuilder.create().texOffs(188, 0).addBox(-4.0F, 0.0F, -0.04F, 8.0F, 1.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_2_right_front_fin = puffer_state_2.addOrReplaceChild("puffer_2_right_front_fin", CubeListBuilder.create().texOffs(207, 0).addBox(-1.0F, -8.0F, -0.04F, 1.0F, 8.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 22.0F, -4.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition puffer_2_body = puffer_state_2.addOrReplaceChild("puffer_2_body", CubeListBuilder.create().texOffs(212, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(72, 17).addBox(-4.1F, -5.2F, -2.0F, 0.23F, 3.36F, 3.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition puffer_2_flank_hook_1_2_r1 = puffer_2_body.addOrReplaceChild("puffer_2_flank_hook_1_2_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1855F, -0.1F, -0.212F, 0.371F, 2.6677F, 0.424F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.85F, -5.52F, 0.4F, -1.115F, -0.6226F, -0.9529F));

        PartDefinition puffer_2_flank_hook_1_1_r1 = puffer_2_body.addOrReplaceChild("puffer_2_flank_hook_1_1_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1855F, -0.1F, -0.212F, 0.371F, 2.6677F, 0.424F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.85F, -3.76F, 0.4F, -1.115F, -0.6226F, -0.9529F));

        PartDefinition puffer_2_flank_hook_1_0_r1 = puffer_2_body.addOrReplaceChild("puffer_2_flank_hook_1_0_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1855F, -0.1F, -0.212F, 0.371F, 2.6677F, 0.424F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.85F, -2.0F, 0.4F, -1.115F, -0.6226F, -0.9529F));

        PartDefinition puffer_2_flank_hook_m1_2_r1 = puffer_2_body.addOrReplaceChild("puffer_2_flank_hook_m1_2_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1855F, -0.1F, -0.212F, 0.371F, 2.6677F, 0.424F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.85F, -5.52F, 0.4F, -1.115F, 0.6226F, 0.9529F));

        PartDefinition puffer_2_flank_hook_m1_1_r1 = puffer_2_body.addOrReplaceChild("puffer_2_flank_hook_m1_1_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1855F, -0.1F, -0.212F, 0.371F, 2.6677F, 0.424F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.85F, -3.76F, 0.4F, -1.115F, 0.6226F, 0.9529F));

        PartDefinition puffer_2_flank_hook_m1_0_r1 = puffer_2_body.addOrReplaceChild("puffer_2_flank_hook_m1_0_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1855F, -0.1F, -0.212F, 0.371F, 2.6677F, 0.424F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.85F, -2.0F, 0.4F, -1.115F, 0.6226F, 0.9529F));

        PartDefinition puffer_2_dorsal_claw = puffer_2_body.addOrReplaceChild("puffer_2_dorsal_claw", CubeListBuilder.create().texOffs(51, 17).addBox(-0.901F, -0.742F, -0.795F, 1.802F, 1.484F, 1.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, -7.75F, 0.4F, 0.15F, 0.22F, -0.45F));

        PartDefinition puffer_2_dorsal_claw_palm_r1 = puffer_2_dorsal_claw.addOrReplaceChild("puffer_2_dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(51, 17).addBox(-0.636F, -0.1F, -0.53F, 1.272F, 1.2236F, 1.06F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.265F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition puffer_2_dorsal_claw_finger_0 = puffer_2_dorsal_claw.addOrReplaceChild("puffer_2_dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.6625F, -1.06F, -0.371F, 0.0F, -0.2F, -0.14F));

        PartDefinition puffer_2_dorsal_claw_finger_0_sharp_tip_r1 = puffer_2_dorsal_claw_finger_0.addOrReplaceChild("puffer_2_dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.0636F, -0.1F, -0.0875F, 0.1272F, 1.3818F, 0.1749F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3445F, -2.6288F, -1.643F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition puffer_2_dorsal_claw_finger_0_blade_r1 = puffer_2_dorsal_claw_finger_0.addOrReplaceChild("puffer_2_dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.1987F, -0.1F, -0.2385F, 0.3975F, 1.7606F, 0.477F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2385F, -1.5116F, -0.5586F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition puffer_2_dorsal_claw_finger_0_proximal_r1 = puffer_2_dorsal_claw_finger_0.addOrReplaceChild("puffer_2_dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.3313F, -0.1F, -0.371F, 0.6625F, 1.829F, 0.742F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition puffer_2_dorsal_claw_finger_1 = puffer_2_dorsal_claw.addOrReplaceChild("puffer_2_dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.06F, -0.371F, 0.09F, 0.0F, 0.0F));

        PartDefinition puffer_2_dorsal_claw_finger_1_sharp_tip_r1 = puffer_2_dorsal_claw_finger_1.addOrReplaceChild("puffer_2_dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.0636F, -0.1F, -0.0875F, 0.1272F, 1.6842F, 0.1749F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.3072F, -2.067F, -1.7955F, 0.0F, 0.0F));

        PartDefinition puffer_2_dorsal_claw_finger_1_blade_r1 = puffer_2_dorsal_claw_finger_1.addOrReplaceChild("puffer_2_dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.1987F, -0.1F, -0.2385F, 0.3975F, 2.1587F, 0.477F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.9016F, -0.7028F, -2.3711F, 0.0F, 0.0F));

        PartDefinition puffer_2_dorsal_claw_finger_1_proximal_r1 = puffer_2_dorsal_claw_finger_1.addOrReplaceChild("puffer_2_dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(67, 17).addBox(-0.3313F, -0.1F, -0.371F, 0.6625F, 2.2273F, 0.742F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition puffer_2_dorsal_claw_finger_2 = puffer_2_dorsal_claw.addOrReplaceChild("puffer_2_dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.6625F, -1.06F, -0.371F, 0.18F, 0.2F, 0.14F));

        PartDefinition puffer_2_dorsal_claw_finger_2_sharp_tip_r1 = puffer_2_dorsal_claw_finger_2.addOrReplaceChild("puffer_2_dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.0636F, -0.1F, -0.0875F, 0.1272F, 1.3818F, 0.1749F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3445F, -2.6288F, -1.643F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition puffer_2_dorsal_claw_finger_2_blade_r1 = puffer_2_dorsal_claw_finger_2.addOrReplaceChild("puffer_2_dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.1987F, -0.1F, -0.2385F, 0.3975F, 1.7606F, 0.477F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2385F, -1.5116F, -0.5586F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition puffer_2_dorsal_claw_finger_2_proximal_r1 = puffer_2_dorsal_claw_finger_2.addOrReplaceChild("puffer_2_dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(39, 17).addBox(-0.3312F, -0.1F, -0.371F, 0.6625F, 1.829F, 0.742F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition puffer_2_bottom_back_fin = puffer_state_2.addOrReplaceChild("puffer_2_bottom_back_fin", CubeListBuilder.create().texOffs(188, 0).addBox(-4.0F, 0.0F, -0.04F, 8.0F, 1.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, 4.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition puffer_2_right_blue_fin = puffer_state_2.addOrReplaceChild("puffer_2_right_blue_fin", CubeListBuilder.create().texOffs(245, 0).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 15.0F, -2.0F));

        PartDefinition puffer_2_left_front_fin = puffer_state_2.addOrReplaceChild("puffer_2_left_front_fin", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -8.0F, -0.04F, 1.0F, 8.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 22.0F, -4.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition puffer_2_bottom_middle_fin = puffer_state_2.addOrReplaceChild("puffer_2_bottom_middle_fin", CubeListBuilder.create().texOffs(188, 0).addBox(-4.0F, 0.0F, -0.04F, 8.0F, 1.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition puffer_2_left_back_fin = puffer_state_2.addOrReplaceChild("puffer_2_left_back_fin", CubeListBuilder.create().texOffs(5, 17).addBox(0.0F, -8.0F, -0.04F, 1.0F, 8.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 22.0F, 4.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition puffer_2_right_back_fin = puffer_state_2.addOrReplaceChild("puffer_2_right_back_fin", CubeListBuilder.create().texOffs(5, 17).addBox(-1.0F, -8.0F, -0.04F, 1.0F, 8.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 22.0F, 4.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition puffer_2_top_front_fin = puffer_state_2.addOrReplaceChild("puffer_2_top_front_fin", CubeListBuilder.create().texOffs(10, 17).addBox(-4.0F, -1.0F, -0.04F, 8.0F, 1.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, -4.0F, 0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition rabbit() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, -1.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(21, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(28, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(35, 0).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -0.1F, -3.8F, 3.0F, 0.6F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.35F, -1.0F, 0.06F, 0.0F, 0.0F));

        PartDefinition rabbit_incisor_1_r1 = head_jaw.addOrReplaceChild("rabbit_incisor_1_r1", CubeListBuilder.create().texOffs(15, 16).addBox(-0.15F, -0.1F, -0.16F, 0.3F, 0.8671F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(15, 16).addBox(-0.99F, -0.1F, -0.16F, 0.3F, 0.8671F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.42F, 0.0F, -3.95F, -2.9148F, 0.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.89F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(49, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 2.91F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.64F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.66F, 0.0F));

        PartDefinition tail = corruption_root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(67, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 7.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition left_haunch = corruption_root.addOrReplaceChild("left_haunch", CubeListBuilder.create().texOffs(78, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 5.5F, 3.7F, -0.3491F, 0.0F, 0.0F));

        PartDefinition left_hind_tendon_r1 = left_haunch.addOrReplaceChild("left_hind_tendon_r1", CubeListBuilder.create().texOffs(236, 0).addBox(-0.5F, -0.1F, -0.575F, 1.0F, 2.7F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.4F, 1.5F, -0.2838F, 0.0F, 0.0F));

        PartDefinition left_hind_foot = left_haunch.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(93, 0).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition right_haunch = corruption_root.addOrReplaceChild("right_haunch", CubeListBuilder.create().texOffs(112, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.5F, 3.7F, -0.3491F, 0.0F, 0.0F));

        PartDefinition right_hind_tendon_r1 = right_haunch.addOrReplaceChild("right_hind_tendon_r1", CubeListBuilder.create().texOffs(236, 0).addBox(-0.5F, -0.1F, -0.575F, 1.0F, 2.7F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.4F, 1.5F, -0.2838F, 0.0F, 0.0F));

        PartDefinition right_hind_foot = right_haunch.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(127, 0).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(146, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(225, 0).addBox(2.6F, -0.8F, -4.5F, 0.65F, 3.0F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0F, 8.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(206, 0).addBox(-0.986F, -0.812F, -0.87F, 1.972F, 1.624F, 1.74F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -1.6F, -7.4F, 0.12F, -0.2F, 0.46F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.696F, -0.1F, -0.58F, 1.392F, 1.3202F, 1.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.29F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.725F, -1.16F, -0.406F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.0696F, -0.1F, -0.0957F, 0.1392F, 1.4933F, 0.1914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.377F, -2.8768F, -1.798F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.2175F, -0.1F, -0.261F, 0.435F, 1.9078F, 0.522F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.261F, -1.6542F, -0.6113F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.3625F, -0.1F, -0.406F, 0.725F, 1.9827F, 0.812F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.16F, -0.406F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.0696F, -0.1F, -0.0957F, 0.1392F, 1.8242F, 0.1914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6192F, -2.262F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(220, 0).addBox(-0.2175F, -0.1F, -0.261F, 0.435F, 2.3435F, 0.522F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.081F, -0.7691F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(220, 0).addBox(-0.3625F, -0.1F, -0.406F, 0.725F, 2.4186F, 0.812F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.725F, -1.16F, -0.406F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.0696F, -0.1F, -0.0957F, 0.1392F, 1.4933F, 0.1914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.377F, -2.8768F, -1.798F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.2175F, -0.1F, -0.261F, 0.435F, 1.9078F, 0.522F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.261F, -1.6542F, -0.6113F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(215, 0).addBox(-0.3625F, -0.1F, -0.406F, 0.725F, 1.9827F, 0.812F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(179, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.89F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 5.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(188, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 2.91F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.64F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(197, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.66F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition salmon() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body_front = corruption_root.addOrReplaceChild("body_front", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(132, 0).addBox(-1.68F, -1.3F, 0.7F, 0.28F, 2.7F, 1.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition top_front_fin = body_front.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(23, 0).addBox(-0.04F, 0.0F, 0.0F, 0.08F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 5.0F));

        PartDefinition body_back = body_front.addOrReplaceChild("body_back", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.0F));

        PartDefinition salmon_tail_torn_edge_r1 = body_back.addOrReplaceChild("salmon_tail_torn_edge_r1", CubeListBuilder.create().texOffs(139, 0).addBox(-0.125F, -0.1F, -0.175F, 0.25F, 3.1275F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.8F, 7.6F, 1.7079F, 0.0F, 0.0F));

        PartDefinition top_back_fin = body_back.addOrReplaceChild("top_back_fin", CubeListBuilder.create().texOffs(55, 0).addBox(-0.04F, 0.0F, 0.0F, 0.08F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, -1.0F));

        PartDefinition back_fin = body_back.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(66, 0).addBox(-0.04F, -2.5F, 0.0F, 0.08F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.0F));

        PartDefinition head = body_front.addOrReplaceChild("head", CubeListBuilder.create().texOffs(81, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_fin = body_front.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(92, 0).addBox(0.0F, -0.04F, 0.0F, 2.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 1.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition right_fin = body_front.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.04F, 0.0F, 2.0F, 0.08F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 1.5F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition dorsal_claw = body_front.addOrReplaceChild("dorsal_claw", CubeListBuilder.create().texOffs(110, 0).addBox(-0.527F, -0.434F, -0.465F, 1.054F, 0.868F, 0.93F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -2.2F, 3.8F, 0.18F, 0.18F, -0.45F));

        PartDefinition dorsal_claw_palm_r1 = dorsal_claw.addOrReplaceChild("dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(117, 0).addBox(-0.372F, -0.1F, -0.31F, 0.744F, 0.7987F, 0.62F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.155F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_0 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.3875F, -0.62F, -0.217F, 0.0F, -0.2F, -0.14F));

        PartDefinition dorsal_claw_finger_0_sharp_tip_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(127, 0).addBox(-0.0372F, -0.1F, -0.0511F, 0.0744F, 0.8912F, 0.1023F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2015F, -1.5376F, -0.961F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition dorsal_claw_finger_0_blade_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.1162F, -0.1F, -0.1395F, 0.2325F, 1.1128F, 0.279F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1395F, -0.8841F, -0.3267F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition dorsal_claw_finger_0_proximal_r1 = dorsal_claw_finger_0.addOrReplaceChild("dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.1938F, -0.1F, -0.217F, 0.3875F, 1.1528F, 0.434F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition dorsal_claw_finger_1 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.62F, -0.217F, 0.09F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_sharp_tip_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.0372F, -0.1F, -0.0511F, 0.0744F, 1.0681F, 0.1023F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.9344F, -1.209F, -1.7955F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_blade_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.1162F, -0.1F, -0.1395F, 0.2325F, 1.3457F, 0.279F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.1123F, -0.4111F, -2.3711F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_1_proximal_r1 = dorsal_claw_finger_1.addOrReplaceChild("dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.1938F, -0.1F, -0.217F, 0.3875F, 1.3858F, 0.434F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition dorsal_claw_finger_2 = dorsal_claw.addOrReplaceChild("dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3875F, -0.62F, -0.217F, 0.18F, 0.2F, 0.14F));

        PartDefinition dorsal_claw_finger_2_sharp_tip_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(127, 0).addBox(-0.0372F, -0.1F, -0.0511F, 0.0744F, 0.8912F, 0.1023F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2015F, -1.5376F, -0.961F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition dorsal_claw_finger_2_blade_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.1162F, -0.1F, -0.1395F, 0.2325F, 1.1128F, 0.279F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1395F, -0.8841F, -0.3267F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition dorsal_claw_finger_2_proximal_r1 = dorsal_claw_finger_2.addOrReplaceChild("dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.1937F, -0.1F, -0.217F, 0.3875F, 1.1528F, 0.434F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition silverfish() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition segment0 = corruption_root.addOrReplaceChild("segment0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.5F));

        PartDefinition segment1 = segment0.addOrReplaceChild("segment1", CubeListBuilder.create().texOffs(11, 0).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(177, 0).addBox(-0.55F, 0.2F, -1.7F, 1.1F, 0.85F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 2.0F));

        PartDefinition silverfish_1_leg_tip_1_r1 = segment1.addOrReplaceChild("silverfish_1_leg_tip_1_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.05F, 1.2F, 0.2F, -0.6591F, -0.1401F, -0.4046F));

        PartDefinition silverfish_1_leg_1_r1 = segment1.addOrReplaceChild("silverfish_1_leg_1_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, 0.5F, 0.0F, 0.1392F, 0.0806F, -1.049F));

        PartDefinition silverfish_m1_leg_tip_1_r1 = segment1.addOrReplaceChild("silverfish_m1_leg_tip_1_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.05F, 1.2F, 0.2F, -0.6591F, 0.1401F, 0.4046F));

        PartDefinition silverfish_m1_leg_1_r1 = segment1.addOrReplaceChild("silverfish_m1_leg_1_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 0.5F, 0.0F, 0.1392F, -0.0806F, 1.049F));

        PartDefinition segment2 = segment1.addOrReplaceChild("segment2", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, 0.0F, -1.5F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(177, 0).addBox(-0.55F, 0.2F, -1.7F, 1.1F, 0.85F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 2.5F));

        PartDefinition silverfish_1_leg_tip_2_r1 = segment2.addOrReplaceChild("silverfish_1_leg_tip_2_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.05F, 1.2F, 0.2F, -0.6591F, -0.1401F, -0.4046F));

        PartDefinition silverfish_1_leg_2_r1 = segment2.addOrReplaceChild("silverfish_1_leg_2_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.5F, 0.0F, 0.1392F, 0.0806F, -1.049F));

        PartDefinition silverfish_m1_leg_tip_2_r1 = segment2.addOrReplaceChild("silverfish_m1_leg_tip_2_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.05F, 1.2F, 0.2F, -0.6591F, 0.1401F, 0.4046F));

        PartDefinition silverfish_m1_leg_2_r1 = segment2.addOrReplaceChild("silverfish_m1_leg_2_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 0.5F, 0.0F, 0.1392F, -0.0806F, 1.049F));

        PartDefinition segment3 = segment2.addOrReplaceChild("segment3", CubeListBuilder.create().texOffs(43, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(177, 0).addBox(-0.55F, 0.2F, -1.7F, 1.1F, 0.85F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 3.0F));

        PartDefinition silverfish_1_leg_tip_3_r1 = segment3.addOrReplaceChild("silverfish_1_leg_tip_3_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.65F, 1.2F, 0.2F, -0.6591F, -0.1401F, -0.4046F));

        PartDefinition silverfish_1_leg_3_r1 = segment3.addOrReplaceChild("silverfish_1_leg_3_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.5F, 0.0F, 0.1392F, 0.0806F, -1.049F));

        PartDefinition silverfish_m1_leg_tip_3_r1 = segment3.addOrReplaceChild("silverfish_m1_leg_tip_3_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.65F, 1.2F, 0.2F, -0.6591F, 0.1401F, 0.4046F));

        PartDefinition silverfish_m1_leg_3_r1 = segment3.addOrReplaceChild("silverfish_m1_leg_3_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, 0.5F, 0.0F, 0.1392F, -0.0806F, 1.049F));

        PartDefinition segment4 = segment3.addOrReplaceChild("segment4", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(177, 0).addBox(-0.55F, 0.2F, -1.7F, 1.1F, 0.85F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 3.0F));

        PartDefinition silverfish_1_leg_tip_4_r1 = segment4.addOrReplaceChild("silverfish_1_leg_tip_4_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.15F, 1.2F, 0.2F, -0.6591F, -0.1401F, -0.4046F));

        PartDefinition silverfish_1_leg_4_r1 = segment4.addOrReplaceChild("silverfish_1_leg_4_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9F, 0.5F, 0.0F, 0.1392F, 0.0806F, -1.049F));

        PartDefinition silverfish_m1_leg_tip_4_r1 = segment4.addOrReplaceChild("silverfish_m1_leg_tip_4_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.09F, -0.1F, -0.12F, 0.18F, 1.3543F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.15F, 1.2F, 0.2F, -0.6591F, 0.1401F, 0.4046F));

        PartDefinition silverfish_m1_leg_4_r1 = segment4.addOrReplaceChild("silverfish_m1_leg_4_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.21F, 0.35F, 1.6465F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, 0.5F, 0.0F, 0.1392F, -0.0806F, 1.049F));

        PartDefinition segment5 = segment4.addOrReplaceChild("segment5", CubeListBuilder.create().texOffs(67, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(177, 0).addBox(-0.55F, 0.2F, -1.7F, 1.1F, 0.85F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 2.5F));

        PartDefinition segment6 = segment5.addOrReplaceChild("segment6", CubeListBuilder.create().texOffs(76, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

        PartDefinition tail_claw = segment5.addOrReplaceChild("tail_claw", CubeListBuilder.create().texOffs(167, 0).addBox(-0.425F, -0.35F, -0.375F, 0.85F, 0.7F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, 0.2F, 0.2F, 0.25F, -0.3F, 0.5F));

        PartDefinition tail_claw_palm_r1 = tail_claw.addOrReplaceChild("tail_claw_palm_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.3F, -0.1F, -0.25F, 0.6F, 0.6828F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.125F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_0 = tail_claw.addOrReplaceChild("tail_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.3125F, -0.5F, -0.175F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_finger_0_sharp_tip_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.03F, -0.1F, -0.0412F, 0.06F, 0.7574F, 0.0825F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1625F, -1.24F, -0.775F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_finger_0_blade_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.0938F, -0.1F, -0.1125F, 0.1875F, 0.9361F, 0.225F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1125F, -0.713F, -0.2635F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_finger_0_proximal_r1 = tail_claw_finger_0.addOrReplaceChild("tail_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.1563F, -0.1F, -0.175F, 0.3125F, 0.9684F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_finger_1 = tail_claw.addOrReplaceChild("tail_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, -0.175F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_sharp_tip_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.03F, -0.1F, -0.0412F, 0.06F, 0.9001F, 0.0825F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.56F, -0.975F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_blade_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.0938F, -0.1F, -0.1125F, 0.1875F, 1.1239F, 0.225F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.897F, -0.3315F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_1_proximal_r1 = tail_claw_finger_1.addOrReplaceChild("tail_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.1563F, -0.1F, -0.175F, 0.3125F, 1.1563F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_finger_2 = tail_claw.addOrReplaceChild("tail_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3125F, -0.5F, -0.175F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_finger_2_sharp_tip_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.03F, -0.1F, -0.0412F, 0.06F, 0.7574F, 0.0825F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1625F, -1.24F, -0.775F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_finger_2_blade_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.0938F, -0.1F, -0.1125F, 0.1875F, 0.9361F, 0.225F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1125F, -0.713F, -0.2635F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_finger_2_proximal_r1 = tail_claw_finger_2.addOrReplaceChild("tail_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.1563F, -0.1F, -0.175F, 0.3125F, 0.9684F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition layer1 = segment4.addOrReplaceChild("layer1", CubeListBuilder.create().texOffs(83, 0).addBox(-3.0F, 0.0F, -1.5F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition layer0 = segment2.addOrReplaceChild("layer0", CubeListBuilder.create().texOffs(102, 0).addBox(-5.0F, 0.0F, -1.5F, 10.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition carapace_claw = segment2.addOrReplaceChild("carapace_claw", CubeListBuilder.create().texOffs(146, 0).addBox(-0.731F, -0.602F, -0.645F, 1.462F, 1.204F, 1.29F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2F, 0.3F, 0.2F, 0.15F, 0.18F, -0.58F));

        PartDefinition carapace_claw_palm_r1 = carapace_claw.addOrReplaceChild("carapace_claw_palm_r1", CubeListBuilder.create().texOffs(155, 0).addBox(-0.516F, -0.1F, -0.43F, 1.032F, 1.0305F, 0.86F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.215F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_0 = carapace_claw.addOrReplaceChild("carapace_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5375F, -0.86F, -0.301F, 0.0F, -0.2F, -0.14F));

        PartDefinition carapace_claw_finger_0_sharp_tip_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.0516F, -0.1F, -0.0709F, 0.1032F, 1.1588F, 0.1419F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2795F, -2.1328F, -1.333F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition carapace_claw_finger_0_blade_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.1613F, -0.1F, -0.1935F, 0.3225F, 1.4661F, 0.387F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1935F, -1.2264F, -0.4532F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition carapace_claw_finger_0_proximal_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.2687F, -0.1F, -0.301F, 0.5375F, 1.5217F, 0.602F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition carapace_claw_finger_1 = carapace_claw.addOrReplaceChild("carapace_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.86F, -0.301F, 0.09F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_sharp_tip_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.0516F, -0.1F, -0.0709F, 0.1032F, 1.4042F, 0.1419F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6832F, -1.677F, -1.7955F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_blade_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.1613F, -0.1F, -0.1935F, 0.3225F, 1.7892F, 0.387F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5428F, -0.5702F, -2.3711F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_proximal_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.2688F, -0.1F, -0.301F, 0.5375F, 1.8448F, 0.602F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_2 = carapace_claw.addOrReplaceChild("carapace_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5375F, -0.86F, -0.301F, 0.18F, 0.2F, 0.14F));

        PartDefinition carapace_claw_finger_2_sharp_tip_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.0516F, -0.1F, -0.0709F, 0.1032F, 1.1588F, 0.1419F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2795F, -2.1328F, -1.333F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition carapace_claw_finger_2_blade_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.1613F, -0.1F, -0.1935F, 0.3225F, 1.4661F, 0.387F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1935F, -1.2264F, -0.4532F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition carapace_claw_finger_2_proximal_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(162, 0).addBox(-0.2688F, -0.1F, -0.301F, 0.5375F, 1.5217F, 0.602F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition layer2 = segment1.addOrReplaceChild("layer2", CubeListBuilder.create().texOffs(129, 0).addBox(-3.0F, 0.0F, -1.5F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition skeleton_horse() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 2.0F, -12.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, -0.25F, -1.9F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -0.25F, -1.9F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 2.0F, 7.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, -0.25F, -1.0F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -0.25F, -1.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition head_parts = corruption_root.addOrReplaceChild("head_parts", CubeListBuilder.create().texOffs(51, 0).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(45, 33).addBox(-1.6F, -6.7F, -2.3F, 3.2F, 2.0F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -12.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition head = head_parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(74, 0).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 3.3F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(101, 0).addBox(-2.549F, -12.999F, 4.001F, 1.998F, 2.998F, 0.998F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(101, 0).addBox(0.551F, -12.999F, 4.001F, 1.998F, 2.998F, 0.998F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mane = head_parts.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(108, 0).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition upper_mouth = head_parts.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 3.3F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition horse_upper_fang_3_r1 = upper_mouth.addOrReplaceChild("horse_upper_fang_3_r1", CubeListBuilder.create().texOffs(60, 33).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 1.205F, 0.3F, new CubeDeformation(0.0F))
        .texOffs(60, 33).addBox(-1.025F, -0.1F, -0.15F, 0.25F, 1.205F, 0.3F, new CubeDeformation(0.0F))
        .texOffs(60, 33).addBox(-1.925F, -0.1F, -0.15F, 0.25F, 1.205F, 0.3F, new CubeDeformation(0.0F))
        .texOffs(60, 33).addBox(-2.825F, -0.1F, -0.15F, 0.25F, 1.205F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, -7.8F, -6.8F, -0.0997F, 0.0F, 0.0F));

        PartDefinition spinal_claw = head_parts.addOrReplaceChild("spinal_claw", CubeListBuilder.create().texOffs(233, 0).addBox(-1.224F, -1.008F, -1.08F, 2.448F, 2.016F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2F, -3.0F, 5.4F, -0.18F, 0.16F, -0.5F));

        PartDefinition spinal_claw_palm_r1 = spinal_claw.addOrReplaceChild("spinal_claw_palm_r1", CubeListBuilder.create().texOffs(24, 33).addBox(-0.864F, -0.1F, -0.72F, 1.728F, 1.5906F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.36F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition spinal_claw_finger_0 = spinal_claw.addOrReplaceChild("spinal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9F, -1.44F, -0.504F, 0.0F, -0.2F, -0.14F));

        PartDefinition spinal_claw_finger_0_sharp_tip_r1 = spinal_claw_finger_0.addOrReplaceChild("spinal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(40, 33).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 1.8054F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.468F, -3.5712F, -2.232F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition spinal_claw_finger_0_blade_r1 = spinal_claw_finger_0.addOrReplaceChild("spinal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.32F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.324F, -2.0534F, -0.7589F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition spinal_claw_finger_0_proximal_r1 = spinal_claw_finger_0.addOrReplaceChild("spinal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(33, 33).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.413F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition spinal_claw_finger_1 = spinal_claw.addOrReplaceChild("spinal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.44F, -0.504F, 0.09F, 0.0F, 0.0F));

        PartDefinition spinal_claw_finger_1_sharp_tip_r1 = spinal_claw_finger_1.addOrReplaceChild("spinal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 2.2163F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4928F, -2.808F, -1.7955F, 0.0F, 0.0F));

        PartDefinition spinal_claw_finger_1_blade_r1 = spinal_claw_finger_1.addOrReplaceChild("spinal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.8609F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5834F, -0.9547F, -2.3711F, 0.0F, 0.0F));

        PartDefinition spinal_claw_finger_1_proximal_r1 = spinal_claw_finger_1.addOrReplaceChild("spinal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(33, 33).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.9541F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition spinal_claw_finger_2 = spinal_claw.addOrReplaceChild("spinal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9F, -1.44F, -0.504F, 0.18F, 0.2F, 0.14F));

        PartDefinition spinal_claw_finger_2_sharp_tip_r1 = spinal_claw_finger_2.addOrReplaceChild("spinal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(40, 33).addBox(-0.0864F, -0.1F, -0.1188F, 0.1728F, 1.8054F, 0.2376F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.468F, -3.5712F, -2.232F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition spinal_claw_finger_2_blade_r1 = spinal_claw_finger_2.addOrReplaceChild("spinal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.27F, -0.1F, -0.324F, 0.54F, 2.32F, 0.648F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.324F, -2.0534F, -0.7589F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition spinal_claw_finger_2_proximal_r1 = spinal_claw_finger_2.addOrReplaceChild("spinal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(33, 33).addBox(-0.45F, -0.1F, -0.504F, 0.9F, 2.413F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 2.0F, 7.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-3.0F, -0.25F, -1.0F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(-3.0F, -0.25F, -1.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(136, 0).addBox(-5.05F, -8.05F, -17.05F, 10.1F, 10.1F, 22.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 5.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(201, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(216, 0).addBox(-1.87F, -1.54F, -1.65F, 3.74F, 3.08F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -6.6F, -10.0F, 0.06F, 0.24F, 0.55F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(233, 0).addBox(-1.32F, -0.1F, -1.1F, 2.64F, 2.3245F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.55F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.375F, -2.2F, -0.77F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.715F, -5.456F, -3.41F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.495F, -3.1372F, -1.1594F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.2F, -0.77F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 3.2804F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.864F, -4.29F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(19, 33).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 4.2653F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9468F, -1.4586F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(10, 33).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 4.4077F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.375F, -2.2F, -0.77F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 33).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.715F, -5.456F, -3.41F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.495F, -3.1372F, -1.1594F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 2.0F, -12.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-3.0F, -0.25F, -1.9F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(-3.0F, -0.25F, -1.9F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.58F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition slime() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition cube = corruption_root.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.2F, 0.05F, -3.7F, 8.4F, 0.85F, 7.4F, new CubeDeformation(0.0F))
        .texOffs(35, 0).addBox(-5.75F, 0.25F, -3.9F, 5.5F, 0.65F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(56, 0).addBox(0.45F, 0.3F, 0.25F, 4.7F, 0.6F, 4.5F, new CubeDeformation(0.0F))
        .texOffs(77, 0).addBox(1.2F, 0.45F, -4.35F, 4.2F, 0.45F, 3.7F, new CubeDeformation(0.0F))
        .texOffs(77, 0).addBox(-4.6F, 0.4F, 1.1F, 4.6F, 0.5F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.1F, 0.0F));

        PartDefinition blood_claw_0 = cube.addOrReplaceChild("blood_claw_0", CubeListBuilder.create().texOffs(96, 0).addBox(-1.53F, -1.26F, -1.35F, 3.06F, 2.24F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6F, -0.08F, 0.0F, 0.13F, 0.0F, 0.28F));

        PartDefinition blood_claw_0_palm_r1 = blood_claw_0.addOrReplaceChild("blood_claw_0_palm_r1", CubeListBuilder.create().texOffs(111, 0).addBox(-1.08F, -0.1F, -0.9F, 2.16F, 1.9382F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.45F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition blood_claw_0_finger_0 = blood_claw_0.addOrReplaceChild("blood_claw_0_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.125F, -1.8F, -0.63F, 0.0F, -0.2F, -0.14F));

        PartDefinition blood_claw_0_finger_0_sharp_tip_r1 = blood_claw_0_finger_0.addOrReplaceChild("blood_claw_0_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.585F, -4.464F, -2.79F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition blood_claw_0_finger_0_blade_r1 = blood_claw_0_finger_0.addOrReplaceChild("blood_claw_0_finger_0_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.405F, -2.5668F, -0.9486F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition blood_claw_0_finger_0_proximal_r1 = blood_claw_0_finger_0.addOrReplaceChild("blood_claw_0_finger_0_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition blood_claw_0_finger_1 = blood_claw_0.addOrReplaceChild("blood_claw_0_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.8F, -0.63F, 0.09F, 0.0F, 0.0F));

        PartDefinition blood_claw_0_finger_1_sharp_tip_r1 = blood_claw_0_finger_1.addOrReplaceChild("blood_claw_0_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.7204F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.616F, -3.51F, -1.7955F, 0.0F, 0.0F));

        PartDefinition blood_claw_0_finger_1_blade_r1 = blood_claw_0_finger_1.addOrReplaceChild("blood_claw_0_finger_1_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 3.5262F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2292F, -1.1934F, -2.3711F, 0.0F, 0.0F));

        PartDefinition blood_claw_0_finger_1_proximal_r1 = blood_claw_0_finger_1.addOrReplaceChild("blood_claw_0_finger_1_proximal_r1", CubeListBuilder.create().texOffs(136, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 3.6427F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition blood_claw_0_finger_2 = blood_claw_0.addOrReplaceChild("blood_claw_0_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.125F, -1.8F, -0.63F, 0.18F, 0.2F, 0.14F));

        PartDefinition blood_claw_0_finger_2_sharp_tip_r1 = blood_claw_0_finger_2.addOrReplaceChild("blood_claw_0_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.585F, -4.464F, -2.79F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition blood_claw_0_finger_2_blade_r1 = blood_claw_0_finger_2.addOrReplaceChild("blood_claw_0_finger_2_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.405F, -2.5668F, -0.9486F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition blood_claw_0_finger_2_proximal_r1 = blood_claw_0_finger_2.addOrReplaceChild("blood_claw_0_finger_2_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition blood_claw_1 = cube.addOrReplaceChild("blood_claw_1", CubeListBuilder.create().texOffs(150, 0).addBox(-1.258F, -1.036F, -1.11F, 2.516F, 2.016F, 2.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8034F, -0.08F, 2.1874F, 0.3202F, 0.8168F, 0.0865F));

        PartDefinition blood_claw_1_palm_r1 = blood_claw_1.addOrReplaceChild("blood_claw_1_palm_r1", CubeListBuilder.create().texOffs(163, 0).addBox(-0.888F, -0.1F, -0.74F, 1.776F, 1.6292F, 1.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.37F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition blood_claw_1_finger_0 = blood_claw_1.addOrReplaceChild("blood_claw_1_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.925F, -1.48F, -0.518F, 0.0F, -0.2F, -0.14F));

        PartDefinition blood_claw_1_finger_0_sharp_tip_r1 = blood_claw_1_finger_0.addOrReplaceChild("blood_claw_1_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(179, 0).addBox(-0.0888F, -0.1F, -0.1221F, 0.1776F, 1.85F, 0.2442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.481F, -3.6704F, -2.294F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition blood_claw_1_finger_0_blade_r1 = blood_claw_1_finger_0.addOrReplaceChild("blood_claw_1_finger_0_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.2775F, -0.1F, -0.333F, 0.555F, 2.3789F, 0.666F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.333F, -2.1105F, -0.78F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition blood_claw_1_finger_0_proximal_r1 = blood_claw_1_finger_0.addOrReplaceChild("blood_claw_1_finger_0_proximal_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.4625F, -0.1F, -0.518F, 0.925F, 2.4745F, 1.036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition blood_claw_1_finger_1 = blood_claw_1.addOrReplaceChild("blood_claw_1_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.48F, -0.518F, 0.09F, 0.0F, 0.0F));

        PartDefinition blood_claw_1_finger_1_sharp_tip_r1 = blood_claw_1_finger_1.addOrReplaceChild("blood_claw_1_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.0888F, -0.1F, -0.1221F, 0.1776F, 2.2723F, 0.2442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6176F, -2.886F, -1.7955F, 0.0F, 0.0F));

        PartDefinition blood_claw_1_finger_1_blade_r1 = blood_claw_1_finger_1.addOrReplaceChild("blood_claw_1_finger_1_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.2775F, -0.1F, -0.333F, 0.555F, 2.9349F, 0.666F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6551F, -0.9812F, -2.3711F, 0.0F, 0.0F));

        PartDefinition blood_claw_1_finger_1_proximal_r1 = blood_claw_1_finger_1.addOrReplaceChild("blood_claw_1_finger_1_proximal_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.4625F, -0.1F, -0.518F, 0.925F, 3.0306F, 1.036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition blood_claw_1_finger_2 = blood_claw_1.addOrReplaceChild("blood_claw_1_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.925F, -1.48F, -0.518F, 0.18F, 0.2F, 0.14F));

        PartDefinition blood_claw_1_finger_2_sharp_tip_r1 = blood_claw_1_finger_2.addOrReplaceChild("blood_claw_1_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(179, 0).addBox(-0.0888F, -0.1F, -0.1221F, 0.1776F, 1.85F, 0.2442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.481F, -3.6704F, -2.294F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition blood_claw_1_finger_2_blade_r1 = blood_claw_1_finger_2.addOrReplaceChild("blood_claw_1_finger_2_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.2775F, -0.1F, -0.333F, 0.555F, 2.3789F, 0.666F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.333F, -2.1105F, -0.78F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition blood_claw_1_finger_2_proximal_r1 = blood_claw_1_finger_2.addOrReplaceChild("blood_claw_1_finger_2_proximal_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.4625F, -0.1F, -0.518F, 0.925F, 2.4745F, 1.036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition blood_claw_2 = cube.addOrReplaceChild("blood_claw_2", CubeListBuilder.create().texOffs(96, 0).addBox(-1.53F, -1.26F, -1.35F, 3.06F, 2.24F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1034F, -0.08F, 1.3519F, 0.2476F, 1.6336F, -0.2265F));

        PartDefinition blood_claw_2_palm_r1 = blood_claw_2.addOrReplaceChild("blood_claw_2_palm_r1", CubeListBuilder.create().texOffs(111, 0).addBox(-1.08F, -0.1F, -0.9F, 2.16F, 1.9382F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.45F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition blood_claw_2_finger_0 = blood_claw_2.addOrReplaceChild("blood_claw_2_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.125F, -1.8F, -0.63F, 0.0F, -0.2F, -0.14F));

        PartDefinition blood_claw_2_finger_0_sharp_tip_r1 = blood_claw_2_finger_0.addOrReplaceChild("blood_claw_2_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.585F, -4.464F, -2.79F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition blood_claw_2_finger_0_blade_r1 = blood_claw_2_finger_0.addOrReplaceChild("blood_claw_2_finger_0_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.405F, -2.5668F, -0.9486F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition blood_claw_2_finger_0_proximal_r1 = blood_claw_2_finger_0.addOrReplaceChild("blood_claw_2_finger_0_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition blood_claw_2_finger_1 = blood_claw_2.addOrReplaceChild("blood_claw_2_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.8F, -0.63F, 0.09F, 0.0F, 0.0F));

        PartDefinition blood_claw_2_finger_1_sharp_tip_r1 = blood_claw_2_finger_1.addOrReplaceChild("blood_claw_2_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.7204F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.616F, -3.51F, -1.7955F, 0.0F, 0.0F));

        PartDefinition blood_claw_2_finger_1_blade_r1 = blood_claw_2_finger_1.addOrReplaceChild("blood_claw_2_finger_1_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 3.5262F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2292F, -1.1934F, -2.3711F, 0.0F, 0.0F));

        PartDefinition blood_claw_2_finger_1_proximal_r1 = blood_claw_2_finger_1.addOrReplaceChild("blood_claw_2_finger_1_proximal_r1", CubeListBuilder.create().texOffs(136, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 3.6427F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition blood_claw_2_finger_2 = blood_claw_2.addOrReplaceChild("blood_claw_2_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.125F, -1.8F, -0.63F, 0.18F, 0.2F, 0.14F));

        PartDefinition blood_claw_2_finger_2_sharp_tip_r1 = blood_claw_2_finger_2.addOrReplaceChild("blood_claw_2_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.585F, -4.464F, -2.79F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition blood_claw_2_finger_2_blade_r1 = blood_claw_2_finger_2.addOrReplaceChild("blood_claw_2_finger_2_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.405F, -2.5668F, -0.9486F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition blood_claw_2_finger_2_proximal_r1 = blood_claw_2_finger_2.addOrReplaceChild("blood_claw_2_finger_2_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition blood_claw_3 = cube.addOrReplaceChild("blood_claw_3", CubeListBuilder.create().texOffs(150, 0).addBox(-1.258F, -1.036F, -1.11F, 2.516F, 2.016F, 2.22F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1034F, -0.08F, -1.3519F, 0.0124F, 2.4504F, -0.2265F));

        PartDefinition blood_claw_3_palm_r1 = blood_claw_3.addOrReplaceChild("blood_claw_3_palm_r1", CubeListBuilder.create().texOffs(163, 0).addBox(-0.888F, -0.1F, -0.74F, 1.776F, 1.6292F, 1.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.37F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition blood_claw_3_finger_0 = blood_claw_3.addOrReplaceChild("blood_claw_3_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.925F, -1.48F, -0.518F, 0.0F, -0.2F, -0.14F));

        PartDefinition blood_claw_3_finger_0_sharp_tip_r1 = blood_claw_3_finger_0.addOrReplaceChild("blood_claw_3_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(179, 0).addBox(-0.0888F, -0.1F, -0.1221F, 0.1776F, 1.85F, 0.2442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.481F, -3.6704F, -2.294F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition blood_claw_3_finger_0_blade_r1 = blood_claw_3_finger_0.addOrReplaceChild("blood_claw_3_finger_0_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.2775F, -0.1F, -0.333F, 0.555F, 2.3789F, 0.666F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.333F, -2.1105F, -0.78F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition blood_claw_3_finger_0_proximal_r1 = blood_claw_3_finger_0.addOrReplaceChild("blood_claw_3_finger_0_proximal_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.4625F, -0.1F, -0.518F, 0.925F, 2.4745F, 1.036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition blood_claw_3_finger_1 = blood_claw_3.addOrReplaceChild("blood_claw_3_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.48F, -0.518F, 0.09F, 0.0F, 0.0F));

        PartDefinition blood_claw_3_finger_1_sharp_tip_r1 = blood_claw_3_finger_1.addOrReplaceChild("blood_claw_3_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.0888F, -0.1F, -0.1221F, 0.1776F, 2.2723F, 0.2442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6176F, -2.886F, -1.7955F, 0.0F, 0.0F));

        PartDefinition blood_claw_3_finger_1_blade_r1 = blood_claw_3_finger_1.addOrReplaceChild("blood_claw_3_finger_1_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.2775F, -0.1F, -0.333F, 0.555F, 2.9349F, 0.666F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6551F, -0.9812F, -2.3711F, 0.0F, 0.0F));

        PartDefinition blood_claw_3_finger_1_proximal_r1 = blood_claw_3_finger_1.addOrReplaceChild("blood_claw_3_finger_1_proximal_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.4625F, -0.1F, -0.518F, 0.925F, 3.0306F, 1.036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition blood_claw_3_finger_2 = blood_claw_3.addOrReplaceChild("blood_claw_3_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.925F, -1.48F, -0.518F, 0.18F, 0.2F, 0.14F));

        PartDefinition blood_claw_3_finger_2_sharp_tip_r1 = blood_claw_3_finger_2.addOrReplaceChild("blood_claw_3_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(179, 0).addBox(-0.0888F, -0.1F, -0.1221F, 0.1776F, 1.85F, 0.2442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.481F, -3.6704F, -2.294F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition blood_claw_3_finger_2_blade_r1 = blood_claw_3_finger_2.addOrReplaceChild("blood_claw_3_finger_2_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.2775F, -0.1F, -0.333F, 0.555F, 2.3789F, 0.666F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.333F, -2.1105F, -0.78F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition blood_claw_3_finger_2_proximal_r1 = blood_claw_3_finger_2.addOrReplaceChild("blood_claw_3_finger_2_proximal_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.4625F, -0.1F, -0.518F, 0.925F, 2.4745F, 1.036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition blood_claw_4 = cube.addOrReplaceChild("blood_claw_4", CubeListBuilder.create().texOffs(96, 0).addBox(-1.53F, -1.26F, -1.35F, 3.06F, 2.24F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8034F, -0.08F, -2.1874F, -0.0602F, 3.2673F, 0.0865F));

        PartDefinition blood_claw_4_palm_r1 = blood_claw_4.addOrReplaceChild("blood_claw_4_palm_r1", CubeListBuilder.create().texOffs(111, 0).addBox(-1.08F, -0.1F, -0.9F, 2.16F, 1.9382F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.45F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition blood_claw_4_finger_0 = blood_claw_4.addOrReplaceChild("blood_claw_4_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.125F, -1.8F, -0.63F, 0.0F, -0.2F, -0.14F));

        PartDefinition blood_claw_4_finger_0_sharp_tip_r1 = blood_claw_4_finger_0.addOrReplaceChild("blood_claw_4_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.585F, -4.464F, -2.79F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition blood_claw_4_finger_0_blade_r1 = blood_claw_4_finger_0.addOrReplaceChild("blood_claw_4_finger_0_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.405F, -2.5668F, -0.9486F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition blood_claw_4_finger_0_proximal_r1 = blood_claw_4_finger_0.addOrReplaceChild("blood_claw_4_finger_0_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition blood_claw_4_finger_1 = blood_claw_4.addOrReplaceChild("blood_claw_4_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.8F, -0.63F, 0.09F, 0.0F, 0.0F));

        PartDefinition blood_claw_4_finger_1_sharp_tip_r1 = blood_claw_4_finger_1.addOrReplaceChild("blood_claw_4_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.7204F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.616F, -3.51F, -1.7955F, 0.0F, 0.0F));

        PartDefinition blood_claw_4_finger_1_blade_r1 = blood_claw_4_finger_1.addOrReplaceChild("blood_claw_4_finger_1_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 3.5262F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2292F, -1.1934F, -2.3711F, 0.0F, 0.0F));

        PartDefinition blood_claw_4_finger_1_proximal_r1 = blood_claw_4_finger_1.addOrReplaceChild("blood_claw_4_finger_1_proximal_r1", CubeListBuilder.create().texOffs(136, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 3.6427F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition blood_claw_4_finger_2 = blood_claw_4.addOrReplaceChild("blood_claw_4_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.125F, -1.8F, -0.63F, 0.18F, 0.2F, 0.14F));

        PartDefinition blood_claw_4_finger_2_sharp_tip_r1 = blood_claw_4_finger_2.addOrReplaceChild("blood_claw_4_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.585F, -4.464F, -2.79F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition blood_claw_4_finger_2_blade_r1 = blood_claw_4_finger_2.addOrReplaceChild("blood_claw_4_finger_2_blade_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.405F, -2.5668F, -0.9486F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition blood_claw_4_finger_2_proximal_r1 = blood_claw_4_finger_2.addOrReplaceChild("blood_claw_4_finger_2_proximal_r1", CubeListBuilder.create().texOffs(122, 0).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition blood_claw_center = cube.addOrReplaceChild("blood_claw_center", CubeListBuilder.create().texOffs(191, 0).addBox(-1.751F, -1.442F, -1.545F, 3.502F, 2.884F, 3.09F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.18F, 0.0F, 0.1F, 0.25F, -0.16F));

        PartDefinition blood_claw_center_palm_r1 = blood_claw_center.addOrReplaceChild("blood_claw_center_palm_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-1.236F, -0.1F, -1.03F, 2.472F, 2.1893F, 2.06F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.515F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition blood_claw_center_finger_0 = blood_claw_center.addOrReplaceChild("blood_claw_center_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.2875F, -2.06F, -0.721F, 0.0F, -0.2F, -0.14F));

        PartDefinition blood_claw_center_finger_0_sharp_tip_r1 = blood_claw_center_finger_0.addOrReplaceChild("blood_claw_center_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.1236F, -0.1F, -0.1699F, 0.2472F, 2.4966F, 0.3399F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6695F, -5.1088F, -3.193F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition blood_claw_center_finger_0_blade_r1 = blood_claw_center_finger_0.addOrReplaceChild("blood_claw_center_finger_0_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.3862F, -0.1F, -0.4635F, 0.7725F, 3.2328F, 0.927F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4635F, -2.9376F, -1.0856F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition blood_claw_center_finger_0_proximal_r1 = blood_claw_center_finger_0.addOrReplaceChild("blood_claw_center_finger_0_proximal_r1", CubeListBuilder.create().texOffs(136, 0).addBox(-0.6438F, -0.1F, -0.721F, 1.2875F, 3.3659F, 1.442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition blood_claw_center_finger_1 = blood_claw_center.addOrReplaceChild("blood_claw_center_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.06F, -0.721F, 0.09F, 0.0F, 0.0F));

        PartDefinition blood_claw_center_finger_1_sharp_tip_r1 = blood_claw_center_finger_1.addOrReplaceChild("blood_claw_center_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.1236F, -0.1F, -0.17F, 0.2472F, 3.0844F, 0.3399F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.4272F, -4.017F, -1.7955F, 0.0F, 0.0F));

        PartDefinition blood_claw_center_finger_1_blade_r1 = blood_claw_center_finger_1.addOrReplaceChild("blood_claw_center_finger_1_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.3862F, -0.1F, -0.4635F, 0.7725F, 4.0066F, 0.927F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6956F, -1.3658F, -2.3711F, 0.0F, 0.0F));

        PartDefinition blood_claw_center_finger_1_proximal_r1 = blood_claw_center_finger_1.addOrReplaceChild("blood_claw_center_finger_1_proximal_r1", CubeListBuilder.create().texOffs(208, 0).addBox(-0.6438F, -0.1F, -0.721F, 1.2875F, 4.1399F, 1.442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition blood_claw_center_finger_2 = blood_claw_center.addOrReplaceChild("blood_claw_center_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.2875F, -2.06F, -0.721F, 0.18F, 0.2F, 0.14F));

        PartDefinition blood_claw_center_finger_2_sharp_tip_r1 = blood_claw_center_finger_2.addOrReplaceChild("blood_claw_center_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(131, 0).addBox(-0.1236F, -0.1F, -0.1699F, 0.2472F, 2.4966F, 0.3399F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6695F, -5.1088F, -3.193F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition blood_claw_center_finger_2_blade_r1 = blood_claw_center_finger_2.addOrReplaceChild("blood_claw_center_finger_2_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.3862F, -0.1F, -0.4635F, 0.7725F, 3.2328F, 0.927F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4635F, -2.9376F, -1.0856F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition blood_claw_center_finger_2_proximal_r1 = blood_claw_center_finger_2.addOrReplaceChild("blood_claw_center_finger_2_proximal_r1", CubeListBuilder.create().texOffs(136, 0).addBox(-0.6438F, -0.1F, -0.721F, 1.2875F, 3.3659F, 1.442F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition sniffer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 3.0F, -15.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(31, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 4.35F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(217, 135).addBox(-3.55F, -0.65F, -4.05F, 7.1F, 1.3F, 8.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.2F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(62, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(93, 0).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 3.0F, 15.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(124, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 4.35F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(217, 135).addBox(-3.55F, -0.65F, -4.05F, 7.1F, 1.3F, 8.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.2F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(155, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition right_mid_leg = corruption_root.addOrReplaceChild("right_mid_leg", CubeListBuilder.create().texOffs(186, 0).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 3.0F, 0.0F));

        PartDefinition right_mid_leg_lower = right_mid_leg.addOrReplaceChild("right_mid_leg_lower", CubeListBuilder.create().texOffs(217, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 4.35F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(217, 135).addBox(-3.55F, -0.65F, -4.05F, 7.1F, 1.3F, 8.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.2F, 0.0F));

        PartDefinition right_mid_leg_foot = right_mid_leg_lower.addOrReplaceChild("right_mid_leg_foot", CubeListBuilder.create().texOffs(248, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(279, 0).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 3.0F, 15.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(310, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 4.35F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(217, 135).addBox(-3.55F, -0.65F, -4.05F, 7.1F, 1.3F, 8.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.2F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(341, 0).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(372, 0).addBox(-12.5F, -14.0F, -20.0F, 25.0F, 29.0F, 40.0F, new CubeDeformation(0.0F))
        .texOffs(0, 70).addBox(-12.5F, -14.0F, -20.0F, 25.0F, 24.0F, 40.0F, new CubeDeformation(0.5F))
        .texOffs(131, 70).addBox(-12.5F, 11.96F, -20.0F, 25.0F, 0.08F, 40.0F, new CubeDeformation(0.0F))
        .texOffs(186, 135).addBox(-4.0F, 0.0F, -23.0F, 8.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(436, 135).addBox(-12.9F, -6.0F, -14.0F, 1.1F, 8.5F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(262, 70).addBox(-6.5F, -7.5F, -11.5F, 13.0F, 18.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(311, 70).addBox(-6.5F, 7.46F, -11.5F, 13.0F, 0.08F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.5F, -19.48F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(360, 70).addBox(-6.5F, -2.0F, -9.0F, 13.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, -11.5F));

        PartDefinition lower_beak = head.addOrReplaceChild("lower_beak", CubeListBuilder.create().texOffs(405, 70).addBox(-6.5F, -7.0F, -8.0F, 13.0F, 12.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.5F, -12.5F));

        PartDefinition sniffer_beak_fang_4_r1 = lower_beak.addOrReplaceChild("sniffer_beak_fang_4_r1", CubeListBuilder.create().texOffs(459, 135).addBox(-0.21F, -0.1F, -0.25F, 0.42F, 1.6036F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(459, 135).addBox(-2.61F, -0.1F, -0.25F, 0.42F, 1.6036F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(459, 135).addBox(-5.01F, -0.1F, -0.25F, 0.42F, 1.6036F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(459, 135).addBox(-7.41F, -0.1F, -0.25F, 0.42F, 1.6036F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(459, 135).addBox(-9.81F, -0.1F, -0.25F, 0.42F, 1.6036F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8F, -5.8F, -8.0F, -3.0703F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(450, 70).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 19.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.51F, -7.5F, -4.51F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(467, 70).addBox(0.0F, 0.0F, -3.0F, 1.0F, 19.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(6.51F, -7.5F, -4.51F));

        PartDefinition snout_claw = head.addOrReplaceChild("snout_claw", CubeListBuilder.create().texOffs(390, 135).addBox(-1.292F, -1.064F, -1.14F, 2.584F, 2.128F, 2.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4F, -5.3F, -8.5F, 0.18F, 0.25F, -0.6F));

        PartDefinition snout_claw_palm_r1 = snout_claw.addOrReplaceChild("snout_claw_palm_r1", CubeListBuilder.create().texOffs(403, 135).addBox(-0.912F, -0.1F, -0.76F, 1.824F, 1.6678F, 1.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.38F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition snout_claw_finger_0 = snout_claw.addOrReplaceChild("snout_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.95F, -1.52F, -0.532F, 0.0F, -0.2F, -0.14F));

        PartDefinition snout_claw_finger_0_sharp_tip_r1 = snout_claw_finger_0.addOrReplaceChild("snout_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(424, 135).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.494F, -3.7696F, -2.356F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition snout_claw_finger_0_blade_r1 = snout_claw_finger_0.addOrReplaceChild("snout_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(419, 135).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.342F, -2.1675F, -0.801F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition snout_claw_finger_0_proximal_r1 = snout_claw_finger_0.addOrReplaceChild("snout_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(412, 135).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition snout_claw_finger_1 = snout_claw.addOrReplaceChild("snout_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.52F, -0.532F, 0.09F, 0.0F, 0.0F));

        PartDefinition snout_claw_finger_1_sharp_tip_r1 = snout_claw_finger_1.addOrReplaceChild("snout_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(419, 135).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 2.3283F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.7424F, -2.964F, -1.7955F, 0.0F, 0.0F));

        PartDefinition snout_claw_finger_1_blade_r1 = snout_claw_finger_1.addOrReplaceChild("snout_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(419, 135).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 3.0088F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7269F, -1.0078F, -2.3711F, 0.0F, 0.0F));

        PartDefinition snout_claw_finger_1_proximal_r1 = snout_claw_finger_1.addOrReplaceChild("snout_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(429, 135).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 3.1071F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition snout_claw_finger_2 = snout_claw.addOrReplaceChild("snout_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.95F, -1.52F, -0.532F, 0.18F, 0.2F, 0.14F));

        PartDefinition snout_claw_finger_2_sharp_tip_r1 = snout_claw_finger_2.addOrReplaceChild("snout_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(424, 135).addBox(-0.0912F, -0.1F, -0.1254F, 0.1824F, 1.8946F, 0.2508F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.494F, -3.7696F, -2.356F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition snout_claw_finger_2_blade_r1 = snout_claw_finger_2.addOrReplaceChild("snout_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(419, 135).addBox(-0.285F, -0.1F, -0.342F, 0.57F, 2.4378F, 0.684F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.342F, -2.1675F, -0.801F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition snout_claw_finger_2_proximal_r1 = snout_claw_finger_2.addOrReplaceChild("snout_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(412, 135).addBox(-0.475F, -0.1F, -0.532F, 0.95F, 2.536F, 1.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition mantle_claw = body.addOrReplaceChild("mantle_claw", CubeListBuilder.create().texOffs(252, 135).addBox(-2.635F, -2.17F, -2.325F, 5.27F, 4.34F, 4.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.2F, -12.8F, -7.0F, 0.12F, 0.2F, 0.46F));

        PartDefinition mantle_claw_palm_r1 = mantle_claw.addOrReplaceChild("mantle_claw_palm_r1", CubeListBuilder.create().texOffs(275, 135).addBox(-1.86F, -0.1F, -1.55F, 3.72F, 3.1935F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.775F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_0 = mantle_claw.addOrReplaceChild("mantle_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.9375F, -3.1F, -1.085F, 0.0F, -0.2F, -0.14F));

        PartDefinition mantle_claw_finger_0_sharp_tip_r1 = mantle_claw_finger_0.addOrReplaceChild("mantle_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(312, 135).addBox(-0.186F, -0.1F, -0.2558F, 0.372F, 3.6561F, 0.5115F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0075F, -7.688F, -4.805F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition mantle_claw_finger_0_blade_r1 = mantle_claw_finger_0.addOrReplaceChild("mantle_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(303, 135).addBox(-0.5813F, -0.1F, -0.6975F, 1.1625F, 4.7639F, 1.395F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6975F, -4.4206F, -1.6337F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition mantle_claw_finger_0_proximal_r1 = mantle_claw_finger_0.addOrReplaceChild("mantle_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(292, 135).addBox(-0.9688F, -0.1F, -1.085F, 1.9375F, 4.9642F, 2.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition mantle_claw_finger_1 = mantle_claw.addOrReplaceChild("mantle_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.1F, -1.085F, 0.09F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_1_sharp_tip_r1 = mantle_claw_finger_1.addOrReplaceChild("mantle_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(337, 135).addBox(-0.186F, -0.1F, -0.2558F, 0.372F, 4.5406F, 0.5115F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.672F, -6.045F, -1.7955F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_1_blade_r1 = mantle_claw_finger_1.addOrReplaceChild("mantle_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(328, 135).addBox(-0.5813F, -0.1F, -0.6975F, 1.1625F, 5.9284F, 1.395F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5614F, -2.0553F, -2.3711F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_1_proximal_r1 = mantle_claw_finger_1.addOrReplaceChild("mantle_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(317, 135).addBox(-0.9688F, -0.1F, -1.085F, 1.9375F, 6.129F, 2.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition mantle_claw_finger_2 = mantle_claw.addOrReplaceChild("mantle_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.9375F, -3.1F, -1.085F, 0.18F, 0.2F, 0.14F));

        PartDefinition mantle_claw_finger_2_sharp_tip_r1 = mantle_claw_finger_2.addOrReplaceChild("mantle_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(312, 135).addBox(-0.186F, -0.1F, -0.2558F, 0.372F, 3.6561F, 0.5115F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0075F, -7.688F, -4.805F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition mantle_claw_finger_2_blade_r1 = mantle_claw_finger_2.addOrReplaceChild("mantle_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(303, 135).addBox(-0.5813F, -0.1F, -0.6975F, 1.1625F, 4.7639F, 1.395F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6975F, -4.4206F, -1.6337F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition mantle_claw_finger_2_proximal_r1 = mantle_claw_finger_2.addOrReplaceChild("mantle_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(292, 135).addBox(-0.9688F, -0.1F, -1.085F, 1.9375F, 4.9642F, 2.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition rear_mantle_claw = body.addOrReplaceChild("rear_mantle_claw", CubeListBuilder.create().texOffs(342, 135).addBox(-2.244F, -1.848F, -1.98F, 4.488F, 3.696F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.5F, -11.7F, 12.0F, 0.2F, -0.2F, -0.5F));

        PartDefinition rear_mantle_claw_palm_r1 = rear_mantle_claw.addOrReplaceChild("rear_mantle_claw_palm_r1", CubeListBuilder.create().texOffs(361, 135).addBox(-1.584F, -0.1F, -1.32F, 3.168F, 2.7493F, 2.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.66F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition rear_mantle_claw_finger_0 = rear_mantle_claw.addOrReplaceChild("rear_mantle_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.65F, -2.64F, -0.924F, 0.0F, -0.2F, -0.14F));

        PartDefinition rear_mantle_claw_finger_0_sharp_tip_r1 = rear_mantle_claw_finger_0.addOrReplaceChild("rear_mantle_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(312, 135).addBox(-0.1584F, -0.1F, -0.2178F, 0.3168F, 3.1433F, 0.4356F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.858F, -6.5472F, -4.092F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition rear_mantle_claw_finger_0_blade_r1 = rear_mantle_claw_finger_0.addOrReplaceChild("rear_mantle_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(376, 135).addBox(-0.495F, -0.1F, -0.594F, 0.99F, 4.0867F, 1.188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.594F, -3.7646F, -1.3913F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition rear_mantle_claw_finger_0_proximal_r1 = rear_mantle_claw_finger_0.addOrReplaceChild("rear_mantle_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(303, 135).addBox(-0.825F, -0.1F, -0.924F, 1.65F, 4.2572F, 1.848F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition rear_mantle_claw_finger_1 = rear_mantle_claw.addOrReplaceChild("rear_mantle_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.64F, -0.924F, 0.09F, 0.0F, 0.0F));

        PartDefinition rear_mantle_claw_finger_1_sharp_tip_r1 = rear_mantle_claw_finger_1.addOrReplaceChild("rear_mantle_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(312, 135).addBox(-0.1584F, -0.1F, -0.2178F, 0.3168F, 3.8965F, 0.4356F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.2368F, -5.148F, -1.7955F, 0.0F, 0.0F));

        PartDefinition rear_mantle_claw_finger_1_blade_r1 = rear_mantle_claw_finger_1.addOrReplaceChild("rear_mantle_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(383, 135).addBox(-0.495F, -0.1F, -0.594F, 0.99F, 5.0784F, 1.188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.7362F, -1.7503F, -2.3711F, 0.0F, 0.0F));

        PartDefinition rear_mantle_claw_finger_1_proximal_r1 = rear_mantle_claw_finger_1.addOrReplaceChild("rear_mantle_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(328, 135).addBox(-0.825F, -0.1F, -0.924F, 1.65F, 5.2492F, 1.848F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition rear_mantle_claw_finger_2 = rear_mantle_claw.addOrReplaceChild("rear_mantle_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.65F, -2.64F, -0.924F, 0.18F, 0.2F, 0.14F));

        PartDefinition rear_mantle_claw_finger_2_sharp_tip_r1 = rear_mantle_claw_finger_2.addOrReplaceChild("rear_mantle_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(312, 135).addBox(-0.1584F, -0.1F, -0.2178F, 0.3168F, 3.1433F, 0.4356F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.858F, -6.5472F, -4.092F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition rear_mantle_claw_finger_2_blade_r1 = rear_mantle_claw_finger_2.addOrReplaceChild("rear_mantle_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(376, 135).addBox(-0.495F, -0.1F, -0.594F, 0.99F, 4.0867F, 1.188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.594F, -3.7646F, -1.3913F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition rear_mantle_claw_finger_2_proximal_r1 = rear_mantle_claw_finger_2.addOrReplaceChild("rear_mantle_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(303, 135).addBox(-0.825F, -0.1F, -0.924F, 1.65F, 4.2572F, 1.848F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_mid_leg = corruption_root.addOrReplaceChild("left_mid_leg", CubeListBuilder.create().texOffs(0, 135).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 3.0F, 0.0F));

        PartDefinition left_mid_leg_lower = left_mid_leg.addOrReplaceChild("left_mid_leg_lower", CubeListBuilder.create().texOffs(31, 135).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 4.35F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(217, 135).addBox(-3.55F, -0.65F, -4.05F, 7.1F, 1.3F, 8.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.2F, 0.0F));

        PartDefinition left_mid_leg_foot = left_mid_leg_lower.addOrReplaceChild("left_mid_leg_foot", CubeListBuilder.create().texOffs(62, 135).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(93, 135).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 5.45F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 3.0F, -15.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(124, 135).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 4.35F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(217, 135).addBox(-3.55F, -0.65F, -4.05F, 7.1F, 1.3F, 8.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.2F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(155, 135).addBox(-3.5F, -0.25F, -4.0F, 7.0F, 0.95F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.1F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition snow_golem() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition upper_body = corruption_root.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.5F))
        .texOffs(152, 0).addBox(-1.6F, -11.3F, -1.6F, 3.2F, 2.9F, 3.2F, new CubeDeformation(0.0F))
        .texOffs(198, 0).addBox(-5.9F, -8.2F, -2.2F, 2.4F, 2.4F, 2.4F, new CubeDeformation(0.0F))
        .texOffs(198, 0).addBox(3.5F, -8.2F, -0.2F, 2.4F, 2.4F, 2.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition pumpkin_left_half_attachment_r1 = upper_body.addOrReplaceChild("pumpkin_left_half_attachment_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.7F, -0.1F, -0.8F, 1.4F, 2.5108F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4F, -5.2F, -3.8F, -1.5201F, -0.0827F, -0.087F));

        PartDefinition pumpkin_right_half_attachment_r1 = upper_body.addOrReplaceChild("pumpkin_right_half_attachment_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.7F, -0.1F, -0.8F, 1.4F, 2.5108F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -5.2F, -3.8F, -1.5201F, 0.0827F, 0.087F));

        PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(41, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, -9.0F, 0.0F));

        PartDefinition right_arm = upper_body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(74, 0).addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(-5.0F, -7.0F, -1.0F, 0.0F, 3.1416F, -1.0F));

        PartDefinition left_arm = upper_body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(74, 0).addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(5.0F, -7.0F, 1.0F, 0.0F, 0.0F, 1.0F));

        PartDefinition pumpkin_right_half = upper_body.addOrReplaceChild("pumpkin_right_half", CubeListBuilder.create().texOffs(211, 0).addBox(-2.0F, -3.3F, -2.0F, 4.0F, 6.6F, 3.2F, new CubeDeformation(0.0F))
        .texOffs(228, 0).addBox(1.6F, -3.1F, -1.9F, 1.0F, 1.1F, 2.8F, new CubeDeformation(0.0F))
        .texOffs(228, 0).addBox(1.6F, -1.0F, -1.9F, 1.0F, 1.22F, 2.8F, new CubeDeformation(0.0F))
        .texOffs(228, 0).addBox(1.6F, 1.1F, -1.9F, 1.0F, 1.34F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -5.1F, -4.6F, 0.03F, -0.22F, -0.11F));

        PartDefinition pumpkin_left_half = upper_body.addOrReplaceChild("pumpkin_left_half", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -3.3F, -2.0F, 4.0F, 6.6F, 3.2F, new CubeDeformation(0.0F))
        .texOffs(228, 0).addBox(-2.6F, -3.1F, -1.9F, 1.0F, 1.1F, 2.8F, new CubeDeformation(0.0F))
        .texOffs(228, 0).addBox(-2.6F, -1.0F, -1.9F, 1.0F, 1.22F, 2.8F, new CubeDeformation(0.0F))
        .texOffs(228, 0).addBox(-2.6F, 1.1F, -1.9F, 1.0F, 1.34F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -5.1F, -4.6F, 0.03F, 0.22F, 0.11F));

        PartDefinition snow_shoulder_claw = upper_body.addOrReplaceChild("snow_shoulder_claw", CubeListBuilder.create().texOffs(17, 25).addBox(-1.564F, -1.288F, -1.38F, 3.128F, 2.576F, 2.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1F, -8.0F, 1.6F, 0.14F, -0.2F, 0.55F));

        PartDefinition snow_shoulder_claw_palm_r1 = snow_shoulder_claw.addOrReplaceChild("snow_shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(32, 25).addBox(-1.104F, -0.1F, -0.92F, 2.208F, 1.9768F, 1.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.46F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition snow_shoulder_claw_finger_0 = snow_shoulder_claw.addOrReplaceChild("snow_shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.15F, -1.84F, -0.644F, 0.0F, -0.2F, -0.14F));

        PartDefinition snow_shoulder_claw_finger_0_sharp_tip_r1 = snow_shoulder_claw_finger_0.addOrReplaceChild("snow_shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(52, 25).addBox(-0.1104F, -0.1F, -0.1518F, 0.2208F, 2.2514F, 0.3036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.598F, -4.5632F, -2.852F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition snow_shoulder_claw_finger_0_blade_r1 = snow_shoulder_claw_finger_0.addOrReplaceChild("snow_shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(52, 25).addBox(-0.345F, -0.1F, -0.414F, 0.69F, 2.9089F, 0.828F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.414F, -2.6238F, -0.9697F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition snow_shoulder_claw_finger_0_proximal_r1 = snow_shoulder_claw_finger_0.addOrReplaceChild("snow_shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(43, 25).addBox(-0.575F, -0.1F, -0.644F, 1.15F, 3.0278F, 1.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition snow_shoulder_claw_finger_1 = snow_shoulder_claw.addOrReplaceChild("snow_shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.84F, -0.644F, 0.09F, 0.0F, 0.0F));

        PartDefinition snow_shoulder_claw_finger_1_sharp_tip_r1 = snow_shoulder_claw_finger_1.addOrReplaceChild("snow_shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(52, 25).addBox(-0.1104F, -0.1F, -0.1518F, 0.2208F, 2.7764F, 0.3036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.7408F, -3.588F, -1.7955F, 0.0F, 0.0F));

        PartDefinition snow_shoulder_claw_finger_1_blade_r1 = snow_shoulder_claw_finger_1.addOrReplaceChild("snow_shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(57, 25).addBox(-0.345F, -0.1F, -0.414F, 0.69F, 3.6001F, 0.828F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.301F, -1.2199F, -2.3711F, 0.0F, 0.0F));

        PartDefinition snow_shoulder_claw_finger_1_proximal_r1 = snow_shoulder_claw_finger_1.addOrReplaceChild("snow_shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(43, 25).addBox(-0.575F, -0.1F, -0.644F, 1.15F, 3.7192F, 1.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition snow_shoulder_claw_finger_2 = snow_shoulder_claw.addOrReplaceChild("snow_shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.15F, -1.84F, -0.644F, 0.18F, 0.2F, 0.14F));

        PartDefinition snow_shoulder_claw_finger_2_sharp_tip_r1 = snow_shoulder_claw_finger_2.addOrReplaceChild("snow_shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(52, 25).addBox(-0.1104F, -0.1F, -0.1518F, 0.2208F, 2.2514F, 0.3036F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.598F, -4.5632F, -2.852F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition snow_shoulder_claw_finger_2_blade_r1 = snow_shoulder_claw_finger_2.addOrReplaceChild("snow_shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(52, 25).addBox(-0.345F, -0.1F, -0.414F, 0.69F, 2.9089F, 0.828F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.414F, -2.6238F, -0.9697F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition snow_shoulder_claw_finger_2_proximal_r1 = snow_shoulder_claw_finger_2.addOrReplaceChild("snow_shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(43, 25).addBox(-0.575F, -0.1F, -0.644F, 1.15F, 3.0278F, 1.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition lower_body = corruption_root.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(103, 0).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(-0.5F))
        .texOffs(169, 0).addBox(-3.2F, -13.8F, -3.2F, 6.4F, 4.0F, 6.4F, new CubeDeformation(0.0F))
        .texOffs(62, 25).addBox(5.05F, -7.0F, -3.0F, 0.8F, 4.2F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition squid() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition tentacle0 = corruption_root.addOrReplaceChild("tentacle0", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition tentacle0_middle = tentacle0.addOrReplaceChild("tentacle0_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle0_tip = tentacle0_middle.addOrReplaceChild("tentacle0_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle1 = corruption_root.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5355F, 3.0F, 3.5355F, 0.0F, 0.7854F, 0.0F));

        PartDefinition tentacle1_middle = tentacle1.addOrReplaceChild("tentacle1_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle1_tip = tentacle1_middle.addOrReplaceChild("tentacle1_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle6 = corruption_root.addOrReplaceChild("tentacle6", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -5.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition tentacle6_middle = tentacle6.addOrReplaceChild("tentacle6_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle6_tip = tentacle6_middle.addOrReplaceChild("tentacle6_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle7 = corruption_root.addOrReplaceChild("tentacle7", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5355F, 3.0F, -3.5355F, 0.0F, -3.927F, 0.0F));

        PartDefinition tentacle7_middle = tentacle7.addOrReplaceChild("tentacle7_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle7_tip = tentacle7_middle.addOrReplaceChild("tentacle7_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(14, 0).addBox(-6.02F, -8.02F, -6.02F, 12.04F, 16.04F, 12.04F, new CubeDeformation(0.0F))
        .texOffs(101, 0).addBox(-6.2F, -1.5F, -3.0F, 0.6F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition crown_spine = body.addOrReplaceChild("crown_spine", CubeListBuilder.create().texOffs(63, 0).addBox(-1.5F, -1.1F, -1.5F, 3.0F, 2.2F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -7.4F, 1.0F, 0.12F, 0.18F, -0.24F));

        PartDefinition crown_spine_sharp_tip_r1 = crown_spine.addOrReplaceChild("crown_spine_sharp_tip_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.125F, -0.1F, -0.175F, 0.25F, 4.9676F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, -14.0F, -4.8F, -2.2484F, -0.0879F, -0.0421F));

        PartDefinition crown_spine_blade_r1 = crown_spine.addOrReplaceChild("crown_spine_blade_r1", CubeListBuilder.create().texOffs(87, 0).addBox(-0.575F, -0.1F, -0.65F, 1.15F, 7.0593F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, -8.0F, -1.5F, -2.6253F, -0.2259F, -0.0599F));

        PartDefinition crown_spine_proximal_r1 = crown_spine.addOrReplaceChild("crown_spine_proximal_r1", CubeListBuilder.create().texOffs(76, 0).addBox(-1.0F, -0.1F, -1.1F, 2.0F, 7.8544F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, -2.928F, -0.3906F, -0.0424F));

        PartDefinition tentacle4 = corruption_root.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition tentacle4_middle = tentacle4.addOrReplaceChild("tentacle4_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle4_tip = tentacle4_middle.addOrReplaceChild("tentacle4_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle5 = corruption_root.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5355F, 3.0F, -3.5355F, 0.0F, -2.3562F, 0.0F));

        PartDefinition tentacle5_middle = tentacle5.addOrReplaceChild("tentacle5_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle5_tip = tentacle5_middle.addOrReplaceChild("tentacle5_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle2 = corruption_root.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 5.0F));

        PartDefinition tentacle2_middle = tentacle2.addOrReplaceChild("tentacle2_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle2_tip = tentacle2_middle.addOrReplaceChild("tentacle2_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle3 = corruption_root.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.65F, 0.0F, -0.65F, 1.3F, 9.2F, 1.3F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5355F, 3.0F, 3.5355F, 0.0F, -0.7854F, 0.0F));

        PartDefinition tentacle3_middle = tentacle3.addOrReplaceChild("tentacle3_middle", CubeListBuilder.create().texOffs(9, 0).addBox(-0.48F, -0.2F, -0.48F, 0.96F, 9.4F, 0.96F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition tentacle3_tip = tentacle3_middle.addOrReplaceChild("tentacle3_tip", CubeListBuilder.create().texOffs(9, 0).addBox(-0.3F, -0.2F, -0.3F, 0.6F, 9.2F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition stray() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(148, 0).addBox(-1.3F, -2.5F, 0.2F, 2.6F, 3.4F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition stray_neck_tendon_1_r1 = head.addOrReplaceChild("stray_neck_tendon_1_r1", CubeListBuilder.create().texOffs(161, 0).addBox(-0.325F, -0.1F, -0.35F, 0.65F, 3.1766F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, -0.3F, 0.8F, 0.514F, 0.7203F, -1.9229F));

        PartDefinition stray_neck_tendon_m1_r1 = head.addOrReplaceChild("stray_neck_tendon_m1_r1", CubeListBuilder.create().texOffs(161, 0).addBox(-0.325F, -0.1F, -0.35F, 0.65F, 3.1766F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, -0.3F, 0.8F, 0.514F, -0.7203F, 1.9229F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(122, 0).addBox(-3.8F, -0.6F, -5.3F, 7.6F, 1.35F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 1.3F, 0.12F, 0.0F, 0.0F));

        PartDefinition stray_lower_fang_5_r1 = head_jaw.addOrReplaceChild("stray_lower_fang_5_r1", CubeListBuilder.create().texOffs(166, 0).addBox(-0.15F, -0.1F, -0.175F, 0.3F, 1.3045F, 0.35F, new CubeDeformation(0.0F))
        .texOffs(166, 0).addBox(-1.35F, -0.1F, -0.175F, 0.3F, 1.3045F, 0.35F, new CubeDeformation(0.0F))
        .texOffs(166, 0).addBox(-2.55F, -0.1F, -0.175F, 0.3F, 1.3045F, 0.35F, new CubeDeformation(0.0F))
        .texOffs(166, 0).addBox(-3.75F, -0.1F, -0.175F, 0.3F, 1.3045F, 0.35F, new CubeDeformation(0.0F))
        .texOffs(166, 0).addBox(-4.95F, -0.1F, -0.175F, 0.3F, 1.3045F, 0.35F, new CubeDeformation(0.0F))
        .texOffs(166, 0).addBox(-6.15F, -0.1F, -0.175F, 0.3F, 1.3045F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -0.6F, -5.2F, -3.0509F, 0.0F, 0.0F));

        PartDefinition stray_mandible_side_1_r1 = head_jaw.addOrReplaceChild("stray_mandible_side_1_r1", CubeListBuilder.create().texOffs(143, 0).addBox(-0.5F, -0.1F, -0.5F, 1.0F, 5.6415F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4F, -0.2F, -4.8F, 1.5037F, -0.1039F, 0.1111F));

        PartDefinition stray_mandible_side_m1_r1 = head_jaw.addOrReplaceChild("stray_mandible_side_m1_r1", CubeListBuilder.create().texOffs(143, 0).addBox(-0.5F, -0.1F, -0.5F, 1.0F, 5.6415F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4F, -0.2F, -4.8F, 1.5037F, 0.1039F, -0.1111F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(9, 0).addBox(-1.18F, -2.18F, -1.18F, 2.36F, 6.85F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 6.01F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(9, 0).addBox(-1.18F, -0.43F, -1.18F, 2.36F, 6.37F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(45, 0).addBox(-1.18F, -0.18F, -1.18F, 2.36F, 6.85F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(27, 0).mirror().addBox(-1.0F, -0.25F, -1.0F, 2.0F, 5.31F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(54, 0).addBox(-1.18F, -0.43F, -1.18F, 2.36F, 5.67F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(36, 0).mirror().addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(63, 0).addBox(-1.18F, -0.43F, -1.18F, 2.36F, 1.31F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(9, 0).addBox(-1.18F, -2.18F, -1.18F, 2.36F, 6.85F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -0.25F, -1.0F, 2.0F, 6.01F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(9, 0).addBox(-1.18F, -0.43F, -1.18F, 2.36F, 6.37F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.49F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(45, 0).addBox(-1.18F, -0.18F, -1.18F, 2.36F, 6.85F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(27, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 5.31F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(54, 0).addBox(-1.18F, -0.43F, -1.18F, 2.36F, 5.67F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(63, 0).addBox(-1.18F, -0.43F, -1.18F, 2.36F, 1.31F, 2.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(97, 0).addBox(-4.18F, -0.18F, -2.18F, 8.36F, 12.36F, 4.36F, new CubeDeformation(0.0F))
        .texOffs(216, 0).addBox(3.5F, 4.2F, -1.4F, 0.75F, 4.2F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.12F, 0.0F, 0.0F));

        PartDefinition rib_claw = body.addOrReplaceChild("rib_claw", CubeListBuilder.create().texOffs(171, 0).addBox(-1.666F, -1.372F, -1.47F, 3.332F, 2.744F, 2.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 3.3F, 1.6F, 0.15F, -0.2F, 0.55F));

        PartDefinition rib_claw_palm_r1 = rib_claw.addOrReplaceChild("rib_claw_palm_r1", CubeListBuilder.create().texOffs(186, 0).addBox(-1.176F, -0.1F, -0.98F, 2.352F, 2.0927F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.49F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_0 = rib_claw.addOrReplaceChild("rib_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.225F, -1.96F, -0.686F, 0.0F, -0.2F, -0.14F));

        PartDefinition rib_claw_finger_0_sharp_tip_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(211, 0).addBox(-0.1176F, -0.1F, -0.1617F, 0.2352F, 2.3852F, 0.3234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.637F, -4.8608F, -3.038F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition rib_claw_finger_0_blade_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.3675F, -0.1F, -0.441F, 0.735F, 3.0856F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.441F, -2.795F, -1.0329F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition rib_claw_finger_0_proximal_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(197, 0).addBox(-0.6125F, -0.1F, -0.686F, 1.225F, 3.2122F, 1.372F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition rib_claw_finger_1 = rib_claw.addOrReplaceChild("rib_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.96F, -0.686F, 0.09F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_sharp_tip_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(211, 0).addBox(-0.1176F, -0.1F, -0.1617F, 0.2352F, 2.9444F, 0.3234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.1152F, -3.822F, -1.7955F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_blade_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.3675F, -0.1F, -0.441F, 0.735F, 3.8218F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5162F, -1.2995F, -2.3711F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_proximal_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(197, 0).addBox(-0.6125F, -0.1F, -0.686F, 1.225F, 3.9487F, 1.372F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_2 = rib_claw.addOrReplaceChild("rib_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.225F, -1.96F, -0.686F, 0.18F, 0.2F, 0.14F));

        PartDefinition rib_claw_finger_2_sharp_tip_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(211, 0).addBox(-0.1176F, -0.1F, -0.1617F, 0.2352F, 2.3852F, 0.3234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.637F, -4.8608F, -3.038F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition rib_claw_finger_2_blade_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(206, 0).addBox(-0.3675F, -0.1F, -0.441F, 0.735F, 3.0856F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.441F, -2.795F, -1.0329F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition rib_claw_finger_2_proximal_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(197, 0).addBox(-0.6125F, -0.1F, -0.686F, 1.225F, 3.2122F, 1.372F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition tadpole() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(45, 0).addBox(-1.6F, -0.6F, 0.6F, 0.28F, 1.1F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(13, 0).addBox(-0.04F, -1.0F, 0.0F, 0.08F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(30, 0).addBox(-0.408F, -0.336F, -0.36F, 0.816F, 0.672F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, -0.65F, 1.8F, 0.12F, 0.18F, -0.4F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(30, 0).addBox(-0.288F, -0.1F, -0.24F, 0.576F, 0.6635F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.12F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.3F, -0.48F, -0.168F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.0288F, -0.1F, -0.0396F, 0.0576F, 0.7351F, 0.0792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.156F, -1.1904F, -0.744F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.09F, -0.1F, -0.108F, 0.18F, 0.9067F, 0.216F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.108F, -0.6845F, -0.253F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.15F, -0.1F, -0.168F, 0.3F, 0.9377F, 0.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.48F, -0.168F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.0288F, -0.1F, -0.0396F, 0.0576F, 0.8721F, 0.0792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4976F, -0.936F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.09F, -0.1F, -0.108F, 0.18F, 1.087F, 0.216F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8611F, -0.3182F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.15F, -0.1F, -0.168F, 0.3F, 1.118F, 0.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, -0.48F, -0.168F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.0288F, -0.1F, -0.0396F, 0.0576F, 0.7351F, 0.0792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.156F, -1.1904F, -0.744F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.09F, -0.1F, -0.108F, 0.18F, 0.9067F, 0.216F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.108F, -0.6845F, -0.253F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-0.15F, -0.1F, -0.168F, 0.3F, 0.9377F, 0.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition trader_llama() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(27, 0).addBox(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(56, 0).addBox(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(56, 0).addBox(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -6.0F));

        PartDefinition neck_claw = head.addOrReplaceChild("neck_claw", CubeListBuilder.create().texOffs(0, 29).addBox(-1.088F, -0.896F, -0.96F, 2.176F, 1.792F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -7.0F, -2.7F, 0.15F, 0.22F, -0.55F));

        PartDefinition neck_claw_palm_r1 = neck_claw.addOrReplaceChild("neck_claw_palm_r1", CubeListBuilder.create().texOffs(11, 29).addBox(-0.768F, -0.1F, -0.64F, 1.536F, 1.436F, 1.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.32F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_0 = neck_claw.addOrReplaceChild("neck_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.8F, -1.28F, -0.448F, 0.0F, -0.2F, -0.14F));

        PartDefinition neck_claw_finger_0_sharp_tip_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(20, 29).addBox(-0.0768F, -0.1F, -0.1056F, 0.1536F, 1.627F, 0.2112F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.416F, -3.1744F, -1.984F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition neck_claw_finger_0_blade_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.24F, -0.1F, -0.288F, 0.48F, 2.0844F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.288F, -1.8253F, -0.6746F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition neck_claw_finger_0_proximal_r1 = neck_claw_finger_0.addOrReplaceChild("neck_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.4F, -0.1F, -0.448F, 0.8F, 2.1671F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition neck_claw_finger_1 = neck_claw.addOrReplaceChild("neck_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.28F, -0.448F, 0.09F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_sharp_tip_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(20, 29).addBox(-0.0768F, -0.1F, -0.1056F, 0.1536F, 1.9923F, 0.2112F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9936F, -2.496F, -1.7955F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_blade_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.24F, -0.1F, -0.288F, 0.48F, 2.5653F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2963F, -0.8486F, -2.3711F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_1_proximal_r1 = neck_claw_finger_1.addOrReplaceChild("neck_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.4F, -0.1F, -0.448F, 0.8F, 2.6481F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition neck_claw_finger_2 = neck_claw.addOrReplaceChild("neck_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.8F, -1.28F, -0.448F, 0.18F, 0.2F, 0.14F));

        PartDefinition neck_claw_finger_2_sharp_tip_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(20, 29).addBox(-0.0768F, -0.1F, -0.1056F, 0.1536F, 1.627F, 0.2112F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.416F, -3.1744F, -1.984F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition neck_claw_finger_2_blade_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.24F, -0.1F, -0.288F, 0.48F, 2.0844F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.288F, -1.8253F, -0.6746F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition neck_claw_finger_2_proximal_r1 = neck_claw_finger_2.addOrReplaceChild("neck_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.4F, -0.1F, -0.448F, 0.8F, 2.1671F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -2.0F, -5.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -2.0F, 6.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -2.0F, 6.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(118, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(163, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, new CubeDeformation(0.32F))
        .texOffs(25, 29).addBox(5.7F, -2.0F, -5.0F, 0.95F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(208, 0).addBox(-1.666F, -1.372F, -1.47F, 3.332F, 2.744F, 2.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -6.0F, -5.0F, -1.5708F, 0.22F, 0.42F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(223, 0).addBox(-1.176F, -0.1F, -0.98F, 2.352F, 2.0927F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.49F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.225F, -1.96F, -0.686F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.1176F, -0.1F, -0.1617F, 0.2352F, 2.3852F, 0.3234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.637F, -4.8608F, -3.038F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(243, 0).addBox(-0.3675F, -0.1F, -0.441F, 0.735F, 3.0856F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.441F, -2.795F, -1.0329F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(234, 0).addBox(-0.6125F, -0.1F, -0.686F, 1.225F, 3.2122F, 1.372F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.96F, -0.686F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.1176F, -0.1F, -0.1617F, 0.2352F, 2.9444F, 0.3234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.1152F, -3.822F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(243, 0).addBox(-0.3675F, -0.1F, -0.441F, 0.735F, 3.8218F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5162F, -1.2995F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(234, 0).addBox(-0.6125F, -0.1F, -0.686F, 1.225F, 3.9487F, 1.372F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.225F, -1.96F, -0.686F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(248, 0).addBox(-0.1176F, -0.1F, -0.1617F, 0.2352F, 2.3852F, 0.3234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.637F, -4.8608F, -3.038F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(243, 0).addBox(-0.3675F, -0.1F, -0.441F, 0.735F, 3.0856F, 0.882F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.441F, -2.795F, -1.0329F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(234, 0).addBox(-0.6125F, -0.1F, -0.686F, 1.225F, 3.2122F, 1.372F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.53F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -2.0F, -5.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 6.27F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.28F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(101, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.02F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition tropical_fish() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition fish_state_0 = corruption_root.addOrReplaceChild("fish_state_0", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition fish_0_left_fin = fish_state_0.addOrReplaceChild("fish_0_left_fin", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -0.04F, 2.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 22.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition fish_0_top_fin = fish_state_0.addOrReplaceChild("fish_0_top_fin", CubeListBuilder.create().texOffs(7, 0).addBox(-0.04F, -3.0F, 0.0F, 0.08F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.5F, -3.0F));

        PartDefinition fish_0_tail = fish_state_0.addOrReplaceChild("fish_0_tail", CubeListBuilder.create().texOffs(22, 0).addBox(-0.04F, -1.5F, 0.0F, 0.08F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 3.0F));

        PartDefinition fish_0_right_fin = fish_state_0.addOrReplaceChild("fish_0_right_fin", CubeListBuilder.create().texOffs(37, 0).addBox(-2.0F, -1.0F, -0.04F, 2.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 22.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition fish_0_body = fish_state_0.addOrReplaceChild("fish_0_body", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(150, 0).addBox(-1.15F, -0.7F, -2.7F, 0.35F, 1.4F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition fish_0_dorsal_claw = fish_0_body.addOrReplaceChild("fish_0_dorsal_claw", CubeListBuilder.create().texOffs(135, 0).addBox(-0.442F, -0.364F, -0.39F, 0.884F, 0.728F, 0.78F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -1.2F, 0.3F, 0.12F, 0.24F, -0.4F));

        PartDefinition fish_0_dorsal_claw_palm_r1 = fish_0_dorsal_claw.addOrReplaceChild("fish_0_dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(135, 0).addBox(-0.312F, -0.1F, -0.26F, 0.624F, 0.7021F, 0.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.13F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition fish_0_dorsal_claw_finger_0 = fish_0_dorsal_claw.addOrReplaceChild("fish_0_dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.325F, -0.52F, -0.182F, 0.0F, -0.2F, -0.14F));

        PartDefinition fish_0_dorsal_claw_finger_0_sharp_tip_r1 = fish_0_dorsal_claw_finger_0.addOrReplaceChild("fish_0_dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.0312F, -0.1F, -0.0429F, 0.0624F, 0.7797F, 0.0858F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.169F, -1.2896F, -0.806F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition fish_0_dorsal_claw_finger_0_blade_r1 = fish_0_dorsal_claw_finger_0.addOrReplaceChild("fish_0_dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.0975F, -0.1F, -0.117F, 0.195F, 0.9656F, 0.234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.117F, -0.7415F, -0.274F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition fish_0_dorsal_claw_finger_0_proximal_r1 = fish_0_dorsal_claw_finger_0.addOrReplaceChild("fish_0_dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.1625F, -0.1F, -0.182F, 0.325F, 0.9991F, 0.364F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition fish_0_dorsal_claw_finger_1 = fish_0_dorsal_claw.addOrReplaceChild("fish_0_dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.52F, -0.182F, 0.09F, 0.0F, 0.0F));

        PartDefinition fish_0_dorsal_claw_finger_1_sharp_tip_r1 = fish_0_dorsal_claw_finger_1.addOrReplaceChild("fish_0_dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.0312F, -0.1F, -0.0429F, 0.0624F, 0.9281F, 0.0858F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6224F, -1.014F, -1.7955F, 0.0F, 0.0F));

        PartDefinition fish_0_dorsal_claw_finger_1_blade_r1 = fish_0_dorsal_claw_finger_1.addOrReplaceChild("fish_0_dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.0975F, -0.1F, -0.117F, 0.195F, 1.1609F, 0.234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9329F, -0.3448F, -2.3711F, 0.0F, 0.0F));

        PartDefinition fish_0_dorsal_claw_finger_1_proximal_r1 = fish_0_dorsal_claw_finger_1.addOrReplaceChild("fish_0_dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.1625F, -0.1F, -0.182F, 0.325F, 1.1945F, 0.364F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition fish_0_dorsal_claw_finger_2 = fish_0_dorsal_claw.addOrReplaceChild("fish_0_dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.325F, -0.52F, -0.182F, 0.18F, 0.2F, 0.14F));

        PartDefinition fish_0_dorsal_claw_finger_2_sharp_tip_r1 = fish_0_dorsal_claw_finger_2.addOrReplaceChild("fish_0_dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.0312F, -0.1F, -0.0429F, 0.0624F, 0.7797F, 0.0858F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.169F, -1.2896F, -0.806F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition fish_0_dorsal_claw_finger_2_blade_r1 = fish_0_dorsal_claw_finger_2.addOrReplaceChild("fish_0_dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.0975F, -0.1F, -0.117F, 0.195F, 0.9656F, 0.234F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.117F, -0.7415F, -0.274F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition fish_0_dorsal_claw_finger_2_proximal_r1 = fish_0_dorsal_claw_finger_2.addOrReplaceChild("fish_0_dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(140, 0).addBox(-0.1625F, -0.1F, -0.182F, 0.325F, 0.9991F, 0.364F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition fish_state_1 = corruption_root.addOrReplaceChild("fish_state_1", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition fish_1_left_fin = fish_state_1.addOrReplaceChild("fish_1_left_fin", CubeListBuilder.create().texOffs(61, 0).addBox(0.0F, 0.0F, -0.04F, 2.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 20.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition fish_1_top_fin = fish_state_1.addOrReplaceChild("fish_1_top_fin", CubeListBuilder.create().texOffs(68, 0).addBox(-0.04F, -4.0F, 0.0F, 0.08F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, -3.0F));

        PartDefinition fish_1_tail = fish_state_1.addOrReplaceChild("fish_1_tail", CubeListBuilder.create().texOffs(83, 0).addBox(-0.04F, -3.0F, 0.0F, 0.08F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 3.0F));

        PartDefinition fish_1_right_fin = fish_state_1.addOrReplaceChild("fish_1_right_fin", CubeListBuilder.create().texOffs(96, 0).addBox(-2.0F, 0.0F, -0.04F, 2.0F, 2.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 20.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition fish_1_body = fish_state_1.addOrReplaceChild("fish_1_body", CubeListBuilder.create().texOffs(103, 0).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(166, 0).addBox(-1.15F, -2.1F, -2.7F, 0.35F, 3.6F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));

        PartDefinition fish_1_dorsal_claw = fish_1_body.addOrReplaceChild("fish_1_dorsal_claw", CubeListBuilder.create().texOffs(157, 0).addBox(-0.68F, -0.56F, -0.6F, 1.36F, 1.12F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -2.7F, 0.3F, 0.12F, 0.24F, -0.4F));

        PartDefinition fish_1_dorsal_claw_palm_r1 = fish_1_dorsal_claw.addOrReplaceChild("fish_1_dorsal_claw_palm_r1", CubeListBuilder.create().texOffs(135, 0).addBox(-0.48F, -0.1F, -0.4F, 0.96F, 0.9725F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition fish_1_dorsal_claw_finger_0 = fish_1_dorsal_claw.addOrReplaceChild("fish_1_dorsal_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, -0.8F, -0.28F, 0.0F, -0.2F, -0.14F));

        PartDefinition fish_1_dorsal_claw_finger_0_sharp_tip_r1 = fish_1_dorsal_claw_finger_0.addOrReplaceChild("fish_1_dorsal_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.048F, -0.1F, -0.066F, 0.096F, 1.0919F, 0.132F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.26F, -1.984F, -1.24F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition fish_1_dorsal_claw_finger_0_blade_r1 = fish_1_dorsal_claw_finger_0.addOrReplaceChild("fish_1_dorsal_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.15F, -0.1F, -0.18F, 0.3F, 1.3778F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.18F, -1.1408F, -0.4216F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition fish_1_dorsal_claw_finger_0_proximal_r1 = fish_1_dorsal_claw_finger_0.addOrReplaceChild("fish_1_dorsal_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.25F, -0.1F, -0.28F, 0.5F, 1.4295F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition fish_1_dorsal_claw_finger_1 = fish_1_dorsal_claw.addOrReplaceChild("fish_1_dorsal_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.8F, -0.28F, 0.09F, 0.0F, 0.0F));

        PartDefinition fish_1_dorsal_claw_finger_1_sharp_tip_r1 = fish_1_dorsal_claw_finger_1.addOrReplaceChild("fish_1_dorsal_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.048F, -0.1F, -0.066F, 0.096F, 1.3202F, 0.132F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.496F, -1.56F, -1.7955F, 0.0F, 0.0F));

        PartDefinition fish_1_dorsal_claw_finger_1_blade_r1 = fish_1_dorsal_claw_finger_1.addOrReplaceChild("fish_1_dorsal_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.15F, -0.1F, -0.18F, 0.3F, 1.6783F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4352F, -0.5304F, -2.3711F, 0.0F, 0.0F));

        PartDefinition fish_1_dorsal_claw_finger_1_proximal_r1 = fish_1_dorsal_claw_finger_1.addOrReplaceChild("fish_1_dorsal_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.25F, -0.1F, -0.28F, 0.5F, 1.7301F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition fish_1_dorsal_claw_finger_2 = fish_1_dorsal_claw.addOrReplaceChild("fish_1_dorsal_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -0.8F, -0.28F, 0.18F, 0.2F, 0.14F));

        PartDefinition fish_1_dorsal_claw_finger_2_sharp_tip_r1 = fish_1_dorsal_claw_finger_2.addOrReplaceChild("fish_1_dorsal_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.048F, -0.1F, -0.066F, 0.096F, 1.0919F, 0.132F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.26F, -1.984F, -1.24F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition fish_1_dorsal_claw_finger_2_blade_r1 = fish_1_dorsal_claw_finger_2.addOrReplaceChild("fish_1_dorsal_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.15F, -0.1F, -0.18F, 0.3F, 1.3778F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.18F, -1.1408F, -0.4216F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition fish_1_dorsal_claw_finger_2_proximal_r1 = fish_1_dorsal_claw_finger_2.addOrReplaceChild("fish_1_dorsal_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(145, 0).addBox(-0.25F, -0.1F, -0.28F, 0.5F, 1.4295F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition fish_1_bottom_fin = fish_state_1.addOrReplaceChild("fish_1_bottom_fin", CubeListBuilder.create().texOffs(120, 0).addBox(-0.04F, 0.0F, 0.0F, 0.08F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, -3.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition turtle() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 4.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -10.0F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(50, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(50, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(50, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 3.0F, -3.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(25, 0).addBox(-3.0F, -0.2F, -4.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.2F, 1.5F, 0.05F, 0.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(55, 0).addBox(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 9.0F, -4.0F));

        PartDefinition right_front_leg_claw_tip_r1 = right_front_leg.addOrReplaceChild("right_front_leg_claw_tip_r1", CubeListBuilder.create().texOffs(100, 27).addBox(-0.1F, -0.1F, -0.125F, 0.2F, 2.0193F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.5F, 0.6F, -2.1F, -0.5493F, 0.3257F, 1.0556F));

        PartDefinition right_front_leg_torn_edge_r1 = right_front_leg.addOrReplaceChild("right_front_leg_torn_edge_r1", CubeListBuilder.create().texOffs(116, 27).addBox(-0.225F, -0.1F, -0.25F, 0.45F, 4.8422F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 0.3F, -1.0F, -0.2451F, 0.2175F, 1.4503F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(92, 0).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 10.0F, 11.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(121, 0).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 10.0F, 11.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(150, 0).addBox(-9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(201, 0).addBox(-5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(105, 27).addBox(-9.8F, 7.0F, -9.0F, 0.8F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -10.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shell_claw = body.addOrReplaceChild("shell_claw", CubeListBuilder.create().texOffs(37, 27).addBox(-1.547F, -1.274F, -1.365F, 3.094F, 2.548F, 2.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.7F, 9.0F, -6.5F, -1.5708F, 0.18F, 0.5F));

        PartDefinition shell_claw_palm_r1 = shell_claw.addOrReplaceChild("shell_claw_palm_r1", CubeListBuilder.create().texOffs(52, 27).addBox(-1.092F, -0.1F, -0.91F, 2.184F, 1.9575F, 1.82F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.455F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_0 = shell_claw.addOrReplaceChild("shell_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.1375F, -1.82F, -0.637F, 0.0F, -0.2F, -0.14F));

        PartDefinition shell_claw_finger_0_sharp_tip_r1 = shell_claw_finger_0.addOrReplaceChild("shell_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.1092F, -0.1F, -0.1502F, 0.2184F, 2.2291F, 0.3003F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5915F, -4.5136F, -2.821F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shell_claw_finger_0_blade_r1 = shell_claw_finger_0.addOrReplaceChild("shell_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.3413F, -0.1F, -0.4095F, 0.6825F, 2.8794F, 0.819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4095F, -2.5953F, -0.9591F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shell_claw_finger_0_proximal_r1 = shell_claw_finger_0.addOrReplaceChild("shell_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(63, 27).addBox(-0.5687F, -0.1F, -0.637F, 1.1375F, 2.997F, 1.274F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shell_claw_finger_1 = shell_claw.addOrReplaceChild("shell_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.82F, -0.637F, 0.09F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_1_sharp_tip_r1 = shell_claw_finger_1.addOrReplaceChild("shell_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.1092F, -0.1F, -0.1502F, 0.2184F, 2.7484F, 0.3003F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.6784F, -3.549F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_1_blade_r1 = shell_claw_finger_1.addOrReplaceChild("shell_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(86, 27).addBox(-0.3412F, -0.1F, -0.4095F, 0.6825F, 3.5631F, 0.819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2651F, -1.2067F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_1_proximal_r1 = shell_claw_finger_1.addOrReplaceChild("shell_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(77, 27).addBox(-0.5687F, -0.1F, -0.637F, 1.1375F, 3.6809F, 1.274F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_2 = shell_claw.addOrReplaceChild("shell_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.1375F, -1.82F, -0.637F, 0.18F, 0.2F, 0.14F));

        PartDefinition shell_claw_finger_2_sharp_tip_r1 = shell_claw_finger_2.addOrReplaceChild("shell_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.1092F, -0.1F, -0.1502F, 0.2184F, 2.2291F, 0.3003F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5915F, -4.5136F, -2.821F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shell_claw_finger_2_blade_r1 = shell_claw_finger_2.addOrReplaceChild("shell_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.3412F, -0.1F, -0.4095F, 0.6825F, 2.8794F, 0.819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4095F, -2.5953F, -0.9591F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shell_claw_finger_2_proximal_r1 = shell_claw_finger_2.addOrReplaceChild("shell_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(63, 27).addBox(-0.5687F, -0.1F, -0.637F, 1.1375F, 2.997F, 1.274F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition rear_shell_claw = body.addOrReplaceChild("rear_shell_claw", CubeListBuilder.create().texOffs(52, 27).addBox(-1.122F, -0.924F, -0.99F, 2.244F, 1.848F, 1.98F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.3F, 17.0F, -6.3F, -1.5708F, -0.22F, -0.48F));

        PartDefinition rear_shell_claw_palm_r1 = rear_shell_claw.addOrReplaceChild("rear_shell_claw_palm_r1", CubeListBuilder.create().texOffs(91, 27).addBox(-0.792F, -0.1F, -0.66F, 1.584F, 1.4747F, 1.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.33F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition rear_shell_claw_finger_0 = rear_shell_claw.addOrReplaceChild("rear_shell_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.825F, -1.32F, -0.462F, 0.0F, -0.2F, -0.14F));

        PartDefinition rear_shell_claw_finger_0_sharp_tip_r1 = rear_shell_claw_finger_0.addOrReplaceChild("rear_shell_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(100, 27).addBox(-0.0792F, -0.1F, -0.1089F, 0.1584F, 1.6716F, 0.2178F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.429F, -3.2736F, -2.046F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition rear_shell_claw_finger_0_blade_r1 = rear_shell_claw_finger_0.addOrReplaceChild("rear_shell_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.2475F, -0.1F, -0.297F, 0.495F, 2.1433F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.297F, -1.8823F, -0.6956F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition rear_shell_claw_finger_0_proximal_r1 = rear_shell_claw_finger_0.addOrReplaceChild("rear_shell_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.4125F, -0.1F, -0.462F, 0.825F, 2.2286F, 0.924F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition rear_shell_claw_finger_1 = rear_shell_claw.addOrReplaceChild("rear_shell_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.32F, -0.462F, 0.09F, 0.0F, 0.0F));

        PartDefinition rear_shell_claw_finger_1_sharp_tip_r1 = rear_shell_claw_finger_1.addOrReplaceChild("rear_shell_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.0792F, -0.1F, -0.1089F, 0.1584F, 2.0483F, 0.2178F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.1184F, -2.574F, -1.7955F, 0.0F, 0.0F));

        PartDefinition rear_shell_claw_finger_1_blade_r1 = rear_shell_claw_finger_1.addOrReplaceChild("rear_shell_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.2475F, -0.1F, -0.297F, 0.495F, 2.6392F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.3681F, -0.8752F, -2.3711F, 0.0F, 0.0F));

        PartDefinition rear_shell_claw_finger_1_proximal_r1 = rear_shell_claw_finger_1.addOrReplaceChild("rear_shell_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.4125F, -0.1F, -0.462F, 0.825F, 2.7246F, 0.924F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition rear_shell_claw_finger_2 = rear_shell_claw.addOrReplaceChild("rear_shell_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.825F, -1.32F, -0.462F, 0.18F, 0.2F, 0.14F));

        PartDefinition rear_shell_claw_finger_2_sharp_tip_r1 = rear_shell_claw_finger_2.addOrReplaceChild("rear_shell_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(100, 27).addBox(-0.0792F, -0.1F, -0.1089F, 0.1584F, 1.6716F, 0.2178F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.429F, -3.2736F, -2.046F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition rear_shell_claw_finger_2_blade_r1 = rear_shell_claw_finger_2.addOrReplaceChild("rear_shell_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.2475F, -0.1F, -0.297F, 0.495F, 2.1433F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.297F, -1.8823F, -0.6956F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition rear_shell_claw_finger_2_proximal_r1 = rear_shell_claw_finger_2.addOrReplaceChild("rear_shell_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(72, 27).addBox(-0.4125F, -0.1F, -0.462F, 0.825F, 2.2286F, 0.924F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 27).addBox(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 9.0F, -4.0F));

        PartDefinition left_front_leg_claw_tip_r1 = left_front_leg.addOrReplaceChild("left_front_leg_claw_tip_r1", CubeListBuilder.create().texOffs(100, 27).addBox(-0.1F, -0.1F, -0.125F, 0.2F, 2.0193F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.5F, 0.6F, -2.1F, -0.5493F, -0.3257F, -1.0556F));

        PartDefinition left_front_leg_torn_edge_r1 = left_front_leg.addOrReplaceChild("left_front_leg_torn_edge_r1", CubeListBuilder.create().texOffs(116, 27).addBox(-0.225F, -0.1F, -0.25F, 0.45F, 4.8422F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.3F, -1.0F, -0.2451F, -0.2175F, -1.4503F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition vex() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition root = corruption_root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, -14.5F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 4.2F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(21, 0).addBox(-2.5F, -0.2F, -3.75F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, 1.25F, 0.05F, 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(42, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(53, 0).addBox(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 0).addBox(-1.25F, -0.5F, -1.0F, 2.0F, 2.33F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(-1.75F, 0.25F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(64, 0).addBox(-1.25F, -0.25F, -1.0F, 2.0F, 2.17F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 1.58F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 1.42F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(73, 0).addBox(-0.75F, -0.5F, -1.0F, 2.0F, 2.33F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(1.75F, 0.25F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(73, 0).addBox(-0.75F, -0.25F, -1.0F, 2.0F, 2.17F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 1.58F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 1.42F, 0.0F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(82, 0).addBox(-0.04F, 0.0F, 0.0F, 0.08F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.0F, 1.0F));

        PartDefinition right_wing_hook_r1 = right_wing.addOrReplaceChild("right_wing_hook_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 1.9748F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.8F, 7.2F, 0.6725F, -0.101F, 0.2871F));

        PartDefinition right_wing_tendon_r1 = right_wing.addOrReplaceChild("right_wing_tendon_r1", CubeListBuilder.create().texOffs(120, 0).addBox(-0.12F, -0.1F, -0.14F, 0.24F, 8.6095F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2F, 0.0F, 1.1284F, 0.0F, 0.0F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(82, 0).mirror().addBox(-0.04F, 0.0F, 0.0F, 0.08F, 5.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.5F, 1.0F, 1.0F));

        PartDefinition left_wing_hook_r1 = left_wing.addOrReplaceChild("left_wing_hook_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.125F, -0.1F, -0.15F, 0.25F, 1.9748F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.8F, 7.2F, 0.6725F, 0.101F, -0.2871F));

        PartDefinition left_wing_tendon_r1 = left_wing.addOrReplaceChild("left_wing_tendon_r1", CubeListBuilder.create().texOffs(120, 0).addBox(-0.12F, -0.1F, -0.14F, 0.24F, 8.6095F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2F, 0.0F, 1.1284F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(101, 0).addBox(-0.68F, -0.56F, -0.6F, 1.36F, 1.12F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, 0.5F, 0.4F, 0.1F, 0.2F, -0.48F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.48F, -0.1F, -0.4F, 0.96F, 0.9725F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, -0.8F, -0.28F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.048F, -0.1F, -0.066F, 0.096F, 1.0919F, 0.132F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.26F, -1.984F, -1.24F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.15F, -0.1F, -0.18F, 0.3F, 1.3778F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.18F, -1.1408F, -0.4216F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.25F, -0.1F, -0.28F, 0.5F, 1.4295F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.8F, -0.28F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.048F, -0.1F, -0.066F, 0.096F, 1.3202F, 0.132F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.496F, -1.56F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.15F, -0.1F, -0.18F, 0.3F, 1.6783F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4352F, -0.5304F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.25F, -0.1F, -0.28F, 0.5F, 1.7301F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -0.8F, -0.28F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.048F, -0.1F, -0.066F, 0.096F, 1.0919F, 0.132F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.26F, -1.984F, -1.24F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.15F, -0.1F, -0.18F, 0.3F, 1.3778F, 0.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.18F, -1.1408F, -0.4216F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(115, 0).addBox(-0.25F, -0.1F, -0.28F, 0.5F, 1.4295F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition wandering_trader() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 9.2F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_4_r1 = head.addOrReplaceChild("head_upper_fang_4_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -0.2F, -6.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, 2.0F, 0.05F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(71, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(80, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition hood_claw = head.addOrReplaceChild("hood_claw", CubeListBuilder.create().texOffs(235, 0).addBox(-1.088F, -0.896F, -0.96F, 2.176F, 1.792F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -7.5F, 1.8F, 0.12F, -0.22F, 0.48F));

        PartDefinition hood_claw_palm_r1 = hood_claw.addOrReplaceChild("hood_claw_palm_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.768F, -0.1F, -0.64F, 1.536F, 1.4361F, 1.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.32F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition hood_claw_finger_0 = hood_claw.addOrReplaceChild("hood_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.8F, -1.28F, -0.448F, 0.0F, -0.2F, -0.14F));

        PartDefinition hood_claw_finger_0_sharp_tip_r1 = hood_claw_finger_0.addOrReplaceChild("hood_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 27).addBox(-0.0768F, -0.1F, -0.1056F, 0.1536F, 1.627F, 0.2112F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.416F, -3.1744F, -1.984F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition hood_claw_finger_0_blade_r1 = hood_claw_finger_0.addOrReplaceChild("hood_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.24F, -0.1F, -0.288F, 0.48F, 2.0845F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.288F, -1.8253F, -0.6746F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition hood_claw_finger_0_proximal_r1 = hood_claw_finger_0.addOrReplaceChild("hood_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.4F, -0.1F, -0.448F, 0.8F, 2.1671F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition hood_claw_finger_1 = hood_claw.addOrReplaceChild("hood_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.28F, -0.448F, 0.09F, 0.0F, 0.0F));

        PartDefinition hood_claw_finger_1_sharp_tip_r1 = hood_claw_finger_1.addOrReplaceChild("hood_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 27).addBox(-0.0768F, -0.1F, -0.1056F, 0.1536F, 1.9923F, 0.2112F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9936F, -2.496F, -1.7955F, 0.0F, 0.0F));

        PartDefinition hood_claw_finger_1_blade_r1 = hood_claw_finger_1.addOrReplaceChild("hood_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.24F, -0.1F, -0.288F, 0.48F, 2.5653F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2963F, -0.8486F, -2.3711F, 0.0F, 0.0F));

        PartDefinition hood_claw_finger_1_proximal_r1 = hood_claw_finger_1.addOrReplaceChild("hood_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.4F, -0.1F, -0.448F, 0.8F, 2.6481F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition hood_claw_finger_2 = hood_claw.addOrReplaceChild("hood_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.8F, -1.28F, -0.448F, 0.18F, 0.2F, 0.14F));

        PartDefinition hood_claw_finger_2_sharp_tip_r1 = hood_claw_finger_2.addOrReplaceChild("hood_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(5, 27).addBox(-0.0768F, -0.1F, -0.1056F, 0.1536F, 1.627F, 0.2112F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.416F, -3.1744F, -1.984F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition hood_claw_finger_2_blade_r1 = hood_claw_finger_2.addOrReplaceChild("hood_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.24F, -0.1F, -0.288F, 0.48F, 2.0845F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.288F, -1.8253F, -0.6746F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition hood_claw_finger_2_proximal_r1 = hood_claw_finger_2.addOrReplaceChild("hood_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.4F, -0.1F, -0.448F, 0.8F, 2.1671F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(113, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(130, 0).mirror().addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(147, 0).mirror().addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(113, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(130, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(147, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(164, 0).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(222, 0).addBox(-4.5F, 1.7F, -1.9F, 1.5F, 3.2F, 3.8F, new CubeDeformation(0.0F))
        .texOffs(222, 0).addBox(3.0F, 1.7F, -1.9F, 1.5F, 3.2F, 3.8F, new CubeDeformation(0.0F))
        .texOffs(46, 27).addBox(-4.7F, 12.0F, -2.5F, 1.0F, 5.5F, 4.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(193, 0).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(10, 27).addBox(-1.496F, -1.232F, -1.32F, 2.992F, 2.464F, 2.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, 5.0F, 2.8F, 0.18F, 0.2F, -0.45F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(235, 0).addBox(-1.056F, -0.1F, -0.88F, 2.112F, 1.8996F, 1.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.44F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.1F, -1.76F, -0.616F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.1056F, -0.1F, -0.1452F, 0.2112F, 2.1622F, 0.2904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.572F, -4.3648F, -2.728F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.33F, -0.1F, -0.396F, 0.66F, 2.7911F, 0.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.396F, -2.5098F, -0.9275F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(23, 27).addBox(-0.55F, -0.1F, -0.616F, 1.1F, 2.9048F, 1.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.76F, -0.616F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.1056F, -0.1F, -0.1452F, 0.2112F, 2.6644F, 0.2904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.4912F, -3.432F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(41, 27).addBox(-0.33F, -0.1F, -0.396F, 0.66F, 3.4523F, 0.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.1574F, -1.1669F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(32, 27).addBox(-0.55F, -0.1F, -0.616F, 1.1F, 3.5662F, 1.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.1F, -1.76F, -0.616F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.1056F, -0.1F, -0.1452F, 0.2112F, 2.1622F, 0.2904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.572F, -4.3648F, -2.728F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.33F, -0.1F, -0.396F, 0.66F, 2.7911F, 0.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.396F, -2.5098F, -0.9275F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(23, 27).addBox(-0.55F, -0.1F, -0.616F, 1.1F, 2.9048F, 1.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition warden() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.9F, 0.0F, -3.0F, 6.0F, 7.01F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.9F, -1.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(25, 0).addBox(-2.9F, -0.25F, -3.0F, 6.0F, 5.79F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.76F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(50, 0).addBox(-2.9F, -0.25F, -3.0F, 6.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.54F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(75, 0).addBox(-3.1F, 0.0F, -3.0F, 6.0F, 7.01F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.9F, -1.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(100, 0).addBox(-3.1F, -0.25F, -3.0F, 6.0F, 5.79F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.76F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(125, 0).addBox(-3.1F, -0.25F, -3.0F, 6.0F, 0.95F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.54F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(150, 0).addBox(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(436, 33).addBox(-4.2F, -10.2F, -4.3F, 8.4F, 12.5F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(209, 0).addBox(-8.0F, -16.0F, -5.0F, 16.0F, 15.2F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

        PartDefinition right_tendril = head.addOrReplaceChild("right_tendril", CubeListBuilder.create().texOffs(267, 0).addBox(-16.0F, -13.0F, -0.04F, 16.0F, 16.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -12.0F, 0.0F));

        PartDefinition left_tendril = head.addOrReplaceChild("left_tendril", CubeListBuilder.create().texOffs(302, 0).addBox(0.0F, -13.0F, -0.04F, 16.0F, 16.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -12.0F, 0.0F));

        PartDefinition left_ribcage = body.addOrReplaceChild("left_ribcage", CubeListBuilder.create().texOffs(337, 0).mirror().addBox(-7.0F, -11.0F, -0.14F, 9.0F, 21.0F, 0.08F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(7.0F, -2.0F, -4.0F));

        PartDefinition right_ribcage = body.addOrReplaceChild("right_ribcage", CubeListBuilder.create().texOffs(337, 0).addBox(-2.0F, -11.0F, -0.14F, 9.0F, 21.0F, 0.08F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -2.0F, -4.0F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(372, 33).addBox(-2.21F, -1.82F, -1.95F, 4.42F, 3.64F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.7F, -8.0F, 5.4F, 0.12F, 0.2F, -0.45F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(391, 33).addBox(-1.56F, -0.1F, -1.3F, 3.12F, 2.7107F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.65F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.625F, -2.6F, -0.91F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(422, 33).addBox(-0.156F, -0.1F, -0.2145F, 0.312F, 3.0987F, 0.429F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.845F, -6.448F, -4.03F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(415, 33).addBox(-0.4875F, -0.1F, -0.585F, 0.975F, 4.0278F, 1.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.585F, -3.7076F, -1.3702F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(406, 33).addBox(-0.8125F, -0.1F, -0.91F, 1.625F, 4.1957F, 1.82F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.6F, -0.91F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(422, 33).addBox(-0.156F, -0.1F, -0.2145F, 0.312F, 3.8405F, 0.429F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.112F, -5.07F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(415, 33).addBox(-0.4875F, -0.1F, -0.585F, 0.975F, 5.0045F, 1.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6644F, -1.7238F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(427, 33).addBox(-0.8125F, -0.1F, -0.91F, 1.625F, 5.1727F, 1.82F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.625F, -2.6F, -0.91F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(422, 33).addBox(-0.156F, -0.1F, -0.2145F, 0.312F, 3.0987F, 0.429F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.845F, -6.448F, -4.03F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(415, 33).addBox(-0.4875F, -0.1F, -0.585F, 0.975F, 4.0278F, 1.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.585F, -3.7076F, -1.3702F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(406, 33).addBox(-0.8125F, -0.1F, -0.91F, 1.625F, 4.1957F, 1.82F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition spine_claw_0 = body.addOrReplaceChild("spine_claw_0", CubeListBuilder.create().texOffs(173, 112).addBox(-1.265F, -4.6F, -1.035F, 2.53F, 4.6F, 2.07F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 7.0F, -1.3614F, 0.0F, 0.0F));

        PartDefinition spine_claw_0_mid = spine_claw_0.addOrReplaceChild("spine_claw_0_mid", CubeListBuilder.create().texOffs(187, 112).addBox(-0.8625F, -4.6F, -0.69F, 1.725F, 4.6F, 1.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition spine_claw_0_tipgrp = spine_claw_0_mid.addOrReplaceChild("spine_claw_0_tipgrp", CubeListBuilder.create().texOffs(197, 112).addBox(-0.4025F, -4.6F, -0.345F, 0.805F, 4.6F, 0.69F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6F, 0.0F, -0.384F, 0.0F, 0.0F));

        PartDefinition spine_claw_1 = body.addOrReplaceChild("spine_claw_1", CubeListBuilder.create().texOffs(203, 112).addBox(-1.155F, -4.2F, -0.945F, 2.31F, 4.2F, 1.89F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 7.0F, -1.4661F, 0.0F, 0.0F));

        PartDefinition spine_claw_1_mid = spine_claw_1.addOrReplaceChild("spine_claw_1_mid", CubeListBuilder.create().texOffs(215, 112).addBox(-0.7875F, -4.2F, -0.63F, 1.575F, 4.2F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition spine_claw_1_tipgrp = spine_claw_1_mid.addOrReplaceChild("spine_claw_1_tipgrp", CubeListBuilder.create().texOffs(225, 112).addBox(-0.3675F, -4.2F, -0.315F, 0.735F, 4.2F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2F, 0.0F, -0.384F, 0.0F, 0.0F));

        PartDefinition spine_claw_2 = body.addOrReplaceChild("spine_claw_2", CubeListBuilder.create().texOffs(231, 112).addBox(-1.045F, -3.8F, -0.855F, 2.09F, 3.8F, 1.71F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 7.0F, -1.6057F, 0.0F, 0.0F));

        PartDefinition spine_claw_2_mid = spine_claw_2.addOrReplaceChild("spine_claw_2_mid", CubeListBuilder.create().texOffs(243, 112).addBox(-0.7125F, -3.8F, -0.57F, 1.425F, 3.8F, 1.14F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition spine_claw_2_tipgrp = spine_claw_2_mid.addOrReplaceChild("spine_claw_2_tipgrp", CubeListBuilder.create().texOffs(253, 112).addBox(-0.3325F, -3.8F, -0.285F, 0.665F, 3.8F, 0.57F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8F, 0.0F, -0.384F, 0.0F, 0.0F));

        PartDefinition left_flank_claw_0 = body.addOrReplaceChild("left_flank_claw_0", CubeListBuilder.create().texOffs(259, 112).addBox(-0.99F, -3.6F, -0.81F, 1.98F, 3.6F, 1.62F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -7.0F, 1.0F, 0.0F, 0.0F, 1.3963F));

        PartDefinition left_flank_claw_0_mid = left_flank_claw_0.addOrReplaceChild("left_flank_claw_0_mid", CubeListBuilder.create().texOffs(269, 112).addBox(-0.675F, -3.6F, -0.54F, 1.35F, 3.6F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition left_flank_claw_0_tipgrp = left_flank_claw_0_mid.addOrReplaceChild("left_flank_claw_0_tipgrp", CubeListBuilder.create().texOffs(279, 112).addBox(-0.315F, -3.6F, -0.27F, 0.63F, 3.6F, 0.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition left_flank_claw_1 = body.addOrReplaceChild("left_flank_claw_1", CubeListBuilder.create().texOffs(285, 112).addBox(-0.88F, -3.2F, -0.72F, 1.76F, 3.2F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.6057F));

        PartDefinition left_flank_claw_1_mid = left_flank_claw_1.addOrReplaceChild("left_flank_claw_1_mid", CubeListBuilder.create().texOffs(295, 112).addBox(-0.6F, -3.2F, -0.48F, 1.2F, 3.2F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition left_flank_claw_1_tipgrp = left_flank_claw_1_mid.addOrReplaceChild("left_flank_claw_1_tipgrp", CubeListBuilder.create().texOffs(303, 112).addBox(-0.28F, -3.2F, -0.24F, 0.56F, 3.2F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition right_flank_claw_0 = body.addOrReplaceChild("right_flank_claw_0", CubeListBuilder.create().texOffs(309, 112).addBox(-0.99F, -3.6F, -0.81F, 1.98F, 3.6F, 1.62F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -7.0F, 1.0F, 0.0F, 0.0F, -1.3963F));

        PartDefinition right_flank_claw_0_mid = right_flank_claw_0.addOrReplaceChild("right_flank_claw_0_mid", CubeListBuilder.create().texOffs(319, 112).addBox(-0.675F, -3.6F, -0.54F, 1.35F, 3.6F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition right_flank_claw_0_tipgrp = right_flank_claw_0_mid.addOrReplaceChild("right_flank_claw_0_tipgrp", CubeListBuilder.create().texOffs(329, 112).addBox(-0.315F, -3.6F, -0.27F, 0.63F, 3.6F, 0.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition right_flank_claw_1 = body.addOrReplaceChild("right_flank_claw_1", CubeListBuilder.create().texOffs(335, 112).addBox(-0.88F, -3.2F, -0.72F, 1.76F, 3.2F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.6057F));

        PartDefinition right_flank_claw_1_mid = right_flank_claw_1.addOrReplaceChild("right_flank_claw_1_mid", CubeListBuilder.create().texOffs(345, 112).addBox(-0.6F, -3.2F, -0.48F, 1.2F, 3.2F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition right_flank_claw_1_tipgrp = right_flank_claw_1_mid.addOrReplaceChild("right_flank_claw_1_tipgrp", CubeListBuilder.create().texOffs(353, 112).addBox(-0.28F, -3.2F, -0.24F, 0.56F, 3.2F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(358, 0).addBox(-6.0F, 0.0F, -5.5F, 12.0F, 17.93F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(147, 33).addBox(-6.6F, -1.2F, -5.9F, 13.2F, 6.4F, 11.8F, new CubeDeformation(0.0F)), PartPose.offset(13.0F, -22.0F, 1.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(405, 0).addBox(-6.0F, -0.25F, -5.5F, 12.0F, 16.57F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(47, 33).addBox(-6.8F, 1.0F, -6.0F, 13.6F, 10.5F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(100, 33).addBox(-5.8F, 10.0F, -5.3F, 11.6F, 6.1F, 10.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.68F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 15.82F, 0.0F));

        PartDefinition left_dragging_hand_claw = left_arm_hand.addOrReplaceChild("left_dragging_hand_claw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.3F, -0.8F, 0.0F, 1.5708F, 0.0F));

        PartDefinition left_dragging_hand_claw_finger_0 = left_dragging_hand_claw.addOrReplaceChild("left_dragging_hand_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.7425F, -0.155F, 3.35F, -0.0366F, 0.1336F, 0.0453F));

        PartDefinition left_dragging_hand_claw_finger_0_sharp_tip_r1 = left_dragging_hand_claw_finger_0.addOrReplaceChild("left_dragging_hand_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(53, 91).addBox(-0.217F, -0.155F, -0.279F, 0.434F, 8.1191F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8525F, 31.6898F, -8.3777F, -0.9162F, 0.0294F, 0.0596F));

        PartDefinition left_dragging_hand_claw_finger_0_edge_r1 = left_dragging_hand_claw_finger_0.addOrReplaceChild("left_dragging_hand_claw_finger_0_edge_r1", CubeListBuilder.create().texOffs(43, 91).addBox(-0.93F, -0.155F, -0.744F, 1.86F, 9.6193F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0075F, 23.6763F, -3.6425F, -0.5336F, 0.0046F, 0.0167F));

        PartDefinition left_dragging_hand_claw_finger_0_blade_r1 = left_dragging_hand_claw_finger_0.addOrReplaceChild("left_dragging_hand_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(25, 91).addBox(-1.86F, -0.155F, -1.55F, 3.72F, 12.9628F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5425F, 11.2918F, -1.0927F, -0.2029F, -0.0037F, -0.0368F));

        PartDefinition left_dragging_hand_claw_finger_0_proximal_r1 = left_dragging_hand_claw_finger_0.addOrReplaceChild("left_dragging_hand_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(1, 91).addBox(-2.8287F, -0.155F, -2.4025F, 5.6575F, 11.6675F, 4.805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0964F, -0.0023F, -0.0478F));

        PartDefinition left_dragging_hand_claw_finger_1 = left_dragging_hand_claw.addOrReplaceChild("left_dragging_hand_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.155F, -2.65F, -3.0793F, -0.0436F, -3.1405F));

        PartDefinition left_dragging_hand_claw_finger_1_sharp_tip_r1 = left_dragging_hand_claw_finger_1.addOrReplaceChild("left_dragging_hand_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(111, 91).addBox(-0.217F, -0.155F, -0.279F, 0.434F, 8.9346F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 35.061F, -9.269F, -0.9179F, 0.0F, 0.0F));

        PartDefinition left_dragging_hand_claw_finger_1_edge_r1 = left_dragging_hand_claw_finger_1.addOrReplaceChild("left_dragging_hand_claw_finger_1_edge_r1", CubeListBuilder.create().texOffs(101, 91).addBox(-0.93F, -0.155F, -0.744F, 1.86F, 10.6082F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 26.195F, -4.03F, -0.5337F, 0.0F, 0.0F));

        PartDefinition left_dragging_hand_claw_finger_1_blade_r1 = left_dragging_hand_claw_finger_1.addOrReplaceChild("left_dragging_hand_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(83, 91).addBox(-1.86F, -0.155F, -1.55F, 3.72F, 14.2994F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.493F, -1.209F, -0.203F, 0.0F, 0.0F));

        PartDefinition left_dragging_hand_claw_finger_1_proximal_r1 = left_dragging_hand_claw_finger_1.addOrReplaceChild("left_dragging_hand_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(59, 91).addBox(-2.8287F, -0.155F, -2.4025F, 5.6575F, 12.8614F, 4.805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0965F, 0.0F, 0.0F));

        PartDefinition left_dragging_hand_claw_finger_2 = left_dragging_hand_claw.addOrReplaceChild("left_dragging_hand_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.7425F, -0.155F, 3.35F, 0.0007F, -0.2644F, -0.0527F));

        PartDefinition left_dragging_hand_claw_finger_2_sharp_tip_r1 = left_dragging_hand_claw_finger_2.addOrReplaceChild("left_dragging_hand_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(169, 91).addBox(-0.217F, -0.155F, -0.279F, 0.434F, 8.1191F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8525F, 31.6898F, -8.3777F, -0.9162F, -0.0294F, -0.0596F));

        PartDefinition left_dragging_hand_claw_finger_2_edge_r1 = left_dragging_hand_claw_finger_2.addOrReplaceChild("left_dragging_hand_claw_finger_2_edge_r1", CubeListBuilder.create().texOffs(159, 91).addBox(-0.93F, -0.155F, -0.744F, 1.86F, 9.6193F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0075F, 23.6763F, -3.6425F, -0.5336F, -0.0046F, -0.0167F));

        PartDefinition left_dragging_hand_claw_finger_2_blade_r1 = left_dragging_hand_claw_finger_2.addOrReplaceChild("left_dragging_hand_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(141, 91).addBox(-1.86F, -0.155F, -1.55F, 3.72F, 12.9628F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5425F, 11.2918F, -1.0927F, -0.2029F, 0.0037F, 0.0368F));

        PartDefinition left_dragging_hand_claw_finger_2_proximal_r1 = left_dragging_hand_claw_finger_2.addOrReplaceChild("left_dragging_hand_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(117, 91).addBox(-2.8287F, -0.155F, -2.4025F, 5.6575F, 11.6675F, 4.805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0964F, 0.0023F, 0.0478F));

        PartDefinition left_shoulder_claw_0 = left_arm.addOrReplaceChild("left_shoulder_claw_0", CubeListBuilder.create().texOffs(1, 112).addBox(-1.21F, -4.4F, -0.99F, 2.42F, 4.4F, 1.98F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.2F, -3.0F, 0.0F, 0.0F, 0.4887F));

        PartDefinition left_shoulder_claw_0_mid = left_shoulder_claw_0.addOrReplaceChild("left_shoulder_claw_0_mid", CubeListBuilder.create().texOffs(13, 112).addBox(-0.825F, -4.4F, -0.66F, 1.65F, 4.4F, 1.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition left_shoulder_claw_0_tipgrp = left_shoulder_claw_0_mid.addOrReplaceChild("left_shoulder_claw_0_tipgrp", CubeListBuilder.create().texOffs(23, 112).addBox(-0.385F, -4.4F, -0.33F, 0.77F, 4.4F, 0.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition left_shoulder_claw_1 = left_arm.addOrReplaceChild("left_shoulder_claw_1", CubeListBuilder.create().texOffs(29, 112).addBox(-1.375F, -5.0F, -1.125F, 2.75F, 5.0F, 2.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -1.2F, 1.5F, 0.0F, 0.0F, 0.733F));

        PartDefinition left_shoulder_claw_1_mid = left_shoulder_claw_1.addOrReplaceChild("left_shoulder_claw_1_mid", CubeListBuilder.create().texOffs(43, 112).addBox(-0.9375F, -5.0F, -0.75F, 1.875F, 5.0F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition left_shoulder_claw_1_tipgrp = left_shoulder_claw_1_mid.addOrReplaceChild("left_shoulder_claw_1_tipgrp", CubeListBuilder.create().texOffs(53, 112).addBox(-0.4375F, -5.0F, -0.375F, 0.875F, 5.0F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition left_shoulder_claw_2 = left_arm.addOrReplaceChild("left_shoulder_claw_2", CubeListBuilder.create().texOffs(59, 112).addBox(-1.1F, -4.0F, -0.9F, 2.2F, 4.0F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 2.0F, 5.9F, -1.309F, 0.0F, 0.3142F));

        PartDefinition left_shoulder_claw_2_mid = left_shoulder_claw_2.addOrReplaceChild("left_shoulder_claw_2_mid", CubeListBuilder.create().texOffs(71, 112).addBox(-0.75F, -4.0F, -0.6F, 1.5F, 4.0F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition left_shoulder_claw_2_tipgrp = left_shoulder_claw_2_mid.addOrReplaceChild("left_shoulder_claw_2_tipgrp", CubeListBuilder.create().texOffs(81, 112).addBox(-0.35F, -4.0F, -0.3F, 0.7F, 4.0F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.384F, 0.0F, 0.0F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(452, 0).addBox(-6.0F, 0.0F, -5.5F, 12.0F, 17.93F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(147, 33).addBox(-6.6F, -1.2F, -5.9F, 13.2F, 6.4F, 11.8F, new CubeDeformation(0.0F)), PartPose.offset(-13.0F, -22.0F, 1.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(0, 33).addBox(-6.0F, -0.25F, -5.5F, 12.0F, 16.57F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(47, 33).addBox(-6.8F, 1.0F, -6.0F, 13.6F, 10.5F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(100, 33).addBox(-5.8F, 10.0F, -5.3F, 11.6F, 6.1F, 10.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.68F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 15.82F, 0.0F));

        PartDefinition right_dragging_hand_claw = right_arm_hand.addOrReplaceChild("right_dragging_hand_claw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.3F, -0.8F, 0.0F, -1.5708F, 0.0F));

        PartDefinition right_dragging_hand_claw_finger_0 = right_dragging_hand_claw.addOrReplaceChild("right_dragging_hand_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.7425F, -0.155F, 3.35F, -0.0366F, -0.1336F, -0.0453F));

        PartDefinition right_dragging_hand_claw_finger_0_sharp_tip_r1 = right_dragging_hand_claw_finger_0.addOrReplaceChild("right_dragging_hand_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(211, 64).addBox(-0.217F, -0.155F, -0.279F, 0.434F, 8.1191F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8525F, 31.6898F, -8.3777F, -0.9162F, -0.0294F, -0.0596F));

        PartDefinition right_dragging_hand_claw_finger_0_edge_r1 = right_dragging_hand_claw_finger_0.addOrReplaceChild("right_dragging_hand_claw_finger_0_edge_r1", CubeListBuilder.create().texOffs(201, 64).addBox(-0.93F, -0.155F, -0.744F, 1.86F, 9.6193F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0075F, 23.6763F, -3.6425F, -0.5336F, -0.0046F, -0.0167F));

        PartDefinition right_dragging_hand_claw_finger_0_blade_r1 = right_dragging_hand_claw_finger_0.addOrReplaceChild("right_dragging_hand_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(183, 64).addBox(-1.86F, -0.155F, -1.55F, 3.72F, 12.9628F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5425F, 11.2918F, -1.0927F, -0.2029F, 0.0037F, 0.0368F));

        PartDefinition right_dragging_hand_claw_finger_0_proximal_r1 = right_dragging_hand_claw_finger_0.addOrReplaceChild("right_dragging_hand_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(159, 64).addBox(-2.8287F, -0.155F, -2.4025F, 5.6575F, 11.6675F, 4.805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0964F, 0.0023F, 0.0478F));

        PartDefinition right_dragging_hand_claw_finger_1 = right_dragging_hand_claw.addOrReplaceChild("right_dragging_hand_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.155F, -2.65F, -3.0793F, 0.0436F, 3.1405F));

        PartDefinition right_dragging_hand_claw_finger_1_sharp_tip_r1 = right_dragging_hand_claw_finger_1.addOrReplaceChild("right_dragging_hand_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(269, 64).addBox(-0.217F, -0.155F, -0.279F, 0.434F, 8.9346F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 35.061F, -9.269F, -0.9179F, 0.0F, 0.0F));

        PartDefinition right_dragging_hand_claw_finger_1_edge_r1 = right_dragging_hand_claw_finger_1.addOrReplaceChild("right_dragging_hand_claw_finger_1_edge_r1", CubeListBuilder.create().texOffs(259, 64).addBox(-0.93F, -0.155F, -0.744F, 1.86F, 10.6082F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 26.195F, -4.03F, -0.5337F, 0.0F, 0.0F));

        PartDefinition right_dragging_hand_claw_finger_1_blade_r1 = right_dragging_hand_claw_finger_1.addOrReplaceChild("right_dragging_hand_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(241, 64).addBox(-1.86F, -0.155F, -1.55F, 3.72F, 14.2994F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.493F, -1.209F, -0.203F, 0.0F, 0.0F));

        PartDefinition right_dragging_hand_claw_finger_1_proximal_r1 = right_dragging_hand_claw_finger_1.addOrReplaceChild("right_dragging_hand_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(217, 64).addBox(-2.8287F, -0.155F, -2.4025F, 5.6575F, 12.8614F, 4.805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0965F, 0.0F, 0.0F));

        PartDefinition right_dragging_hand_claw_finger_2 = right_dragging_hand_claw.addOrReplaceChild("right_dragging_hand_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(3.7425F, -0.155F, 3.35F, 0.0007F, 0.2644F, 0.0527F));

        PartDefinition right_dragging_hand_claw_finger_2_sharp_tip_r1 = right_dragging_hand_claw_finger_2.addOrReplaceChild("right_dragging_hand_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(327, 64).addBox(-0.217F, -0.155F, -0.279F, 0.434F, 8.1191F, 0.558F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8525F, 31.6898F, -8.3777F, -0.9162F, 0.0294F, 0.0596F));

        PartDefinition right_dragging_hand_claw_finger_2_edge_r1 = right_dragging_hand_claw_finger_2.addOrReplaceChild("right_dragging_hand_claw_finger_2_edge_r1", CubeListBuilder.create().texOffs(317, 64).addBox(-0.93F, -0.155F, -0.744F, 1.86F, 9.6193F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0075F, 23.6763F, -3.6425F, -0.5336F, 0.0046F, 0.0167F));

        PartDefinition right_dragging_hand_claw_finger_2_blade_r1 = right_dragging_hand_claw_finger_2.addOrReplaceChild("right_dragging_hand_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(299, 64).addBox(-1.86F, -0.155F, -1.55F, 3.72F, 12.9628F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5425F, 11.2918F, -1.0927F, -0.2029F, -0.0037F, -0.0368F));

        PartDefinition right_dragging_hand_claw_finger_2_proximal_r1 = right_dragging_hand_claw_finger_2.addOrReplaceChild("right_dragging_hand_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(275, 64).addBox(-2.8287F, -0.155F, -2.4025F, 5.6575F, 11.6675F, 4.805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0964F, -0.0023F, -0.0478F));

        PartDefinition right_shoulder_claw_0 = right_arm.addOrReplaceChild("right_shoulder_claw_0", CubeListBuilder.create().texOffs(87, 112).addBox(-1.21F, -4.4F, -0.99F, 2.42F, 4.4F, 1.98F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.2F, -3.0F, 0.0F, 0.0F, -0.4887F));

        PartDefinition right_shoulder_claw_0_mid = right_shoulder_claw_0.addOrReplaceChild("right_shoulder_claw_0_mid", CubeListBuilder.create().texOffs(99, 112).addBox(-0.825F, -4.4F, -0.66F, 1.65F, 4.4F, 1.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition right_shoulder_claw_0_tipgrp = right_shoulder_claw_0_mid.addOrReplaceChild("right_shoulder_claw_0_tipgrp", CubeListBuilder.create().texOffs(109, 112).addBox(-0.385F, -4.4F, -0.33F, 0.77F, 4.4F, 0.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition right_shoulder_claw_1 = right_arm.addOrReplaceChild("right_shoulder_claw_1", CubeListBuilder.create().texOffs(115, 112).addBox(-1.375F, -5.0F, -1.125F, 2.75F, 5.0F, 2.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -1.2F, 1.5F, 0.0F, 0.0F, -0.733F));

        PartDefinition right_shoulder_claw_1_mid = right_shoulder_claw_1.addOrReplaceChild("right_shoulder_claw_1_mid", CubeListBuilder.create().texOffs(129, 112).addBox(-0.9375F, -5.0F, -0.75F, 1.875F, 5.0F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition right_shoulder_claw_1_tipgrp = right_shoulder_claw_1_mid.addOrReplaceChild("right_shoulder_claw_1_tipgrp", CubeListBuilder.create().texOffs(139, 112).addBox(-0.4375F, -5.0F, -0.375F, 0.875F, 5.0F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.384F, 0.0F, 0.0F));

        PartDefinition right_shoulder_claw_2 = right_arm.addOrReplaceChild("right_shoulder_claw_2", CubeListBuilder.create().texOffs(145, 112).addBox(-1.1F, -4.0F, -0.9F, 2.2F, 4.0F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, 5.9F, -1.309F, 0.0F, -0.3142F));

        PartDefinition right_shoulder_claw_2_mid = right_shoulder_claw_2.addOrReplaceChild("right_shoulder_claw_2_mid", CubeListBuilder.create().texOffs(157, 112).addBox(-0.75F, -4.0F, -0.6F, 1.5F, 4.0F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition right_shoulder_claw_2_tipgrp = right_shoulder_claw_2_mid.addOrReplaceChild("right_shoulder_claw_2_tipgrp", CubeListBuilder.create().texOffs(167, 112).addBox(-0.35F, -4.0F, -0.3F, 0.7F, 4.0F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.384F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition witch() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 9.2F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_4_r1 = head.addOrReplaceChild("head_upper_fang_4_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.12F, -0.1F, -0.15F, 0.24F, 0.8636F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.0F, -4.04F, -0.1972F, -0.0045F, -0.0452F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -0.2F, -6.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, 2.0F, 0.05F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(71, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition mole = nose.addOrReplaceChild("mole", CubeListBuilder.create().texOffs(80, 0).addBox(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(85, 0).addBox(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0313F, -5.0F));

        PartDefinition hat_rim = hat.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(126, 0).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition hat2 = hat.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(161, 0).addBox(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.0524F, 0.0F, 0.0262F));

        PartDefinition hat3 = hat2.addOrReplaceChild("hat3", CubeListBuilder.create().texOffs(190, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.1047F, 0.0F, 0.0524F));

        PartDefinition hat4 = hat3.addOrReplaceChild("hat4", CubeListBuilder.create().texOffs(207, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.75F, -2.0F, 2.0F, -0.2094F, 0.0F, 0.1047F));

        PartDefinition hat_claw = hat2.addOrReplaceChild("hat_claw", CubeListBuilder.create().texOffs(92, 19).addBox(-1.156F, -0.952F, -1.02F, 2.312F, 1.904F, 2.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8F, -1.0F, 4.4F, 0.1F, 0.2F, -0.4F));

        PartDefinition hat_claw_palm_r1 = hat_claw.addOrReplaceChild("hat_claw_palm_r1", CubeListBuilder.create().texOffs(105, 19).addBox(-0.816F, -0.1F, -0.68F, 1.632F, 1.5133F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.34F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition hat_claw_finger_0 = hat_claw.addOrReplaceChild("hat_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.85F, -1.36F, -0.476F, 0.0F, -0.2F, -0.14F));

        PartDefinition hat_claw_finger_0_sharp_tip_r1 = hat_claw_finger_0.addOrReplaceChild("hat_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(119, 19).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.442F, -3.3728F, -2.108F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition hat_claw_finger_0_blade_r1 = hat_claw_finger_0.addOrReplaceChild("hat_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.306F, -1.9394F, -0.7167F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition hat_claw_finger_0_proximal_r1 = hat_claw_finger_0.addOrReplaceChild("hat_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition hat_claw_finger_1 = hat_claw.addOrReplaceChild("hat_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.36F, -0.476F, 0.09F, 0.0F, 0.0F));

        PartDefinition hat_claw_finger_1_sharp_tip_r1 = hat_claw_finger_1.addOrReplaceChild("hat_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 2.1043F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.2432F, -2.652F, -1.7955F, 0.0F, 0.0F));

        PartDefinition hat_claw_finger_1_blade_r1 = hat_claw_finger_1.addOrReplaceChild("hat_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.7131F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.4398F, -0.9017F, -2.3711F, 0.0F, 0.0F));

        PartDefinition hat_claw_finger_1_proximal_r1 = hat_claw_finger_1.addOrReplaceChild("hat_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.8011F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition hat_claw_finger_2 = hat_claw.addOrReplaceChild("hat_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.85F, -1.36F, -0.476F, 0.18F, 0.2F, 0.14F));

        PartDefinition hat_claw_finger_2_sharp_tip_r1 = hat_claw_finger_2.addOrReplaceChild("hat_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(119, 19).addBox(-0.0816F, -0.1F, -0.1122F, 0.1632F, 1.7162F, 0.2244F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.442F, -3.3728F, -2.108F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition hat_claw_finger_2_blade_r1 = hat_claw_finger_2.addOrReplaceChild("hat_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.255F, -0.1F, -0.306F, 0.51F, 2.2022F, 0.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.306F, -1.9394F, -0.7167F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition hat_claw_finger_2_proximal_r1 = hat_claw_finger_2.addOrReplaceChild("hat_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.425F, -0.1F, -0.476F, 0.85F, 2.2901F, 0.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(212, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(229, 0).mirror().addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(212, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(229, 0).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 5.31F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, -0.25F, -2.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.06F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(17, 19).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(164, 19).addBox(-4.8F, 10.0F, -2.6F, 1.0F, 5.8F, 4.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(46, 19).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(124, 19).addBox(-1.598F, -1.316F, -1.41F, 3.196F, 2.632F, 2.82F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8F, 2.1F, 1.5F, 0.15F, 0.24F, -0.5F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(139, 19).addBox(-1.128F, -0.1F, -0.94F, 2.256F, 2.0154F, 1.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.47F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.175F, -1.88F, -0.658F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.1128F, -0.1F, -0.1551F, 0.2256F, 2.296F, 0.3102F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.611F, -4.6624F, -2.914F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.3525F, -0.1F, -0.423F, 0.705F, 2.9678F, 0.846F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.423F, -2.6809F, -0.9908F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(150, 19).addBox(-0.5875F, -0.1F, -0.658F, 1.175F, 3.0892F, 1.316F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.88F, -0.658F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.1128F, -0.1F, -0.1551F, 0.2256F, 2.8324F, 0.3102F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.8656F, -3.666F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(159, 19).addBox(-0.3525F, -0.1F, -0.423F, 0.705F, 3.674F, 0.846F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.3727F, -1.2464F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(150, 19).addBox(-0.5875F, -0.1F, -0.658F, 1.175F, 3.7957F, 1.316F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.175F, -1.88F, -0.658F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.1128F, -0.1F, -0.1551F, 0.2256F, 2.296F, 0.3102F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.611F, -4.6624F, -2.914F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(114, 19).addBox(-0.3525F, -0.1F, -0.423F, 0.705F, 2.9678F, 0.846F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.423F, -2.6809F, -0.9908F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(150, 19).addBox(-0.5875F, -0.1F, -0.658F, 1.175F, 3.0892F, 1.316F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(75, 19).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.08F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(75, 19).addBox(-3.0F, -0.25F, -2.0F, 4.0F, 6.01F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(177, 19).addBox(-2.5F, -0.7F, -1.6F, 3.0F, 1.6F, 3.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(75, 19).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.49F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.08F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(75, 19).mirror().addBox(-1.0F, -0.25F, -2.0F, 4.0F, 6.01F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.24F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 5.26F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition wolf() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(156, 0).addBox(-1.0F, 0.0F, -1.0F, 4.0F, 3.1F, 3.3F, new CubeDeformation(0.0F))
        .texOffs(173, 0).addBox(-1.0F, 1.5F, -4.0F, 4.0F, 1.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 1.5F, -7.0F));

        PartDefinition wolf_upper_jaw = head.addOrReplaceChild("wolf_upper_jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 2.2F, 1.0F, -1.1216F, -0.1F, -0.2F));

        PartDefinition real_head = wolf_upper_jaw.addOrReplaceChild("real_head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 6.0F, 5.1F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(21, 0).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(21, 0).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(28, 0).addBox(-0.5F, -0.001F, -5.0F, 3.0F, 1.5F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -2.2F, -1.0F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(135, 0).addBox(-1.7F, -0.1F, -6.2F, 3.4F, 1.0F, 5.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.2F, 0.7F, 0.06F, 0.0F, 0.0F));

        PartDefinition mouth_right_claw = head.addOrReplaceChild("mouth_right_claw", CubeListBuilder.create().texOffs(190, 0).addBox(-1.7F, -1.4F, -1.5F, 3.4F, 2.8F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, 2.0F, -2.0F, 0.95F, -0.2F, 0.15F));

        PartDefinition mouth_right_claw_palm_r1 = mouth_right_claw.addOrReplaceChild("mouth_right_claw_palm_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-1.2F, -0.1F, -1.0F, 2.4F, 2.1313F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition mouth_right_claw_finger_0 = mouth_right_claw.addOrReplaceChild("mouth_right_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.25F, -2.0F, -0.7F, 0.0F, -0.2F, -0.14F));

        PartDefinition mouth_right_claw_finger_0_sharp_tip_r1 = mouth_right_claw_finger_0.addOrReplaceChild("mouth_right_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.12F, -0.1F, -0.165F, 0.24F, 2.4298F, 0.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.65F, -4.96F, -3.1F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition mouth_right_claw_finger_0_blade_r1 = mouth_right_claw_finger_0.addOrReplaceChild("mouth_right_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(225, 0).addBox(-0.375F, -0.1F, -0.45F, 0.75F, 3.1444F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.45F, -2.852F, -1.054F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition mouth_right_claw_finger_0_proximal_r1 = mouth_right_claw_finger_0.addOrReplaceChild("mouth_right_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.625F, -0.1F, -0.7F, 1.25F, 3.2736F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition mouth_right_claw_finger_1 = mouth_right_claw.addOrReplaceChild("mouth_right_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, -0.7F, 0.09F, 0.0F, 0.0F));

        PartDefinition mouth_right_claw_finger_1_sharp_tip_r1 = mouth_right_claw_finger_1.addOrReplaceChild("mouth_right_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.12F, -0.1F, -0.165F, 0.24F, 3.0004F, 0.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.24F, -3.9F, -1.7955F, 0.0F, 0.0F));

        PartDefinition mouth_right_claw_finger_1_blade_r1 = mouth_right_claw_finger_1.addOrReplaceChild("mouth_right_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(225, 0).addBox(-0.375F, -0.1F, -0.45F, 0.75F, 3.8958F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.588F, -1.326F, -2.3711F, 0.0F, 0.0F));

        PartDefinition mouth_right_claw_finger_1_proximal_r1 = mouth_right_claw_finger_1.addOrReplaceChild("mouth_right_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(235, 0).addBox(-0.625F, -0.1F, -0.7F, 1.25F, 4.0252F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition mouth_right_claw_finger_2 = mouth_right_claw.addOrReplaceChild("mouth_right_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.25F, -2.0F, -0.7F, 0.18F, 0.2F, 0.14F));

        PartDefinition mouth_right_claw_finger_2_sharp_tip_r1 = mouth_right_claw_finger_2.addOrReplaceChild("mouth_right_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.12F, -0.1F, -0.165F, 0.24F, 2.4298F, 0.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.65F, -4.96F, -3.1F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition mouth_right_claw_finger_2_blade_r1 = mouth_right_claw_finger_2.addOrReplaceChild("mouth_right_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(225, 0).addBox(-0.375F, -0.1F, -0.45F, 0.75F, 3.1444F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.45F, -2.852F, -1.054F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition mouth_right_claw_finger_2_proximal_r1 = mouth_right_claw_finger_2.addOrReplaceChild("mouth_right_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.625F, -0.1F, -0.7F, 1.25F, 3.2736F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition mouth_left_claw = head.addOrReplaceChild("mouth_left_claw", CubeListBuilder.create().texOffs(156, 0).addBox(-1.87F, -1.54F, -1.65F, 3.74F, 3.08F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 2.0F, -2.1F, 1.0F, 0.18F, -0.12F));

        PartDefinition mouth_left_claw_palm_r1 = mouth_left_claw.addOrReplaceChild("mouth_left_claw_palm_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.32F, -0.1F, -1.1F, 2.64F, 2.3245F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.55F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition mouth_left_claw_finger_0 = mouth_left_claw.addOrReplaceChild("mouth_left_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.375F, -2.2F, -0.77F, 0.0F, -0.2F, -0.14F));

        PartDefinition mouth_left_claw_finger_0_sharp_tip_r1 = mouth_left_claw_finger_0.addOrReplaceChild("mouth_left_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.715F, -5.456F, -3.41F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition mouth_left_claw_finger_0_blade_r1 = mouth_left_claw_finger_0.addOrReplaceChild("mouth_left_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(225, 0).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.495F, -3.1372F, -1.1594F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition mouth_left_claw_finger_0_proximal_r1 = mouth_left_claw_finger_0.addOrReplaceChild("mouth_left_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition mouth_left_claw_finger_1 = mouth_left_claw.addOrReplaceChild("mouth_left_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.2F, -0.77F, 0.09F, 0.0F, 0.0F));

        PartDefinition mouth_left_claw_finger_1_sharp_tip_r1 = mouth_left_claw_finger_1.addOrReplaceChild("mouth_left_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(225, 0).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 3.2805F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.864F, -4.29F, -1.7955F, 0.0F, 0.0F));

        PartDefinition mouth_left_claw_finger_1_blade_r1 = mouth_left_claw_finger_1.addOrReplaceChild("mouth_left_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(13, 16).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 4.2653F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9468F, -1.4586F, -2.3711F, 0.0F, 0.0F));

        PartDefinition mouth_left_claw_finger_1_proximal_r1 = mouth_left_claw_finger_1.addOrReplaceChild("mouth_left_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(235, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 4.4077F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition mouth_left_claw_finger_2 = mouth_left_claw.addOrReplaceChild("mouth_left_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.375F, -2.2F, -0.77F, 0.18F, 0.2F, 0.14F));

        PartDefinition mouth_left_claw_finger_2_sharp_tip_r1 = mouth_left_claw_finger_2.addOrReplaceChild("mouth_left_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.715F, -5.456F, -3.41F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition mouth_left_claw_finger_2_blade_r1 = mouth_left_claw_finger_2.addOrReplaceChild("mouth_left_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(225, 0).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.495F, -3.1372F, -1.1594F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition mouth_left_claw_finger_2_proximal_r1 = mouth_left_claw_finger_2.addOrReplaceChild("mouth_left_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(216, 0).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(43, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 4.41F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 4.0F, -4.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(52, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 3.39F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.16F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(61, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.14F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(43, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 4.41F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 4.0F, 7.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(52, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 3.39F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.16F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(61, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.14F, 0.0F));

        PartDefinition upper_body = corruption_root.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(70, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.0F, -3.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = upper_body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(0, 16).addBox(-1.241F, -1.022F, -1.095F, 2.482F, 2.044F, 2.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, -1.0F, -1.7F, 0.12F, -0.2F, 0.45F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(18, 16).addBox(-0.876F, -0.1F, -0.73F, 1.752F, 1.6099F, 1.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.365F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9125F, -1.46F, -0.511F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 16).addBox(-0.0876F, -0.1F, -0.1204F, 0.1752F, 1.8277F, 0.2409F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4745F, -3.6208F, -2.263F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.2737F, -0.1F, -0.3285F, 0.5475F, 2.3494F, 0.657F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3285F, -2.082F, -0.7694F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(27, 16).addBox(-0.4562F, -0.1F, -0.511F, 0.9125F, 2.4438F, 1.022F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.46F, -0.511F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.0876F, -0.1F, -0.1204F, 0.1752F, 2.2443F, 0.2409F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5552F, -2.847F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.2738F, -0.1F, -0.3285F, 0.5475F, 2.8979F, 0.657F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6192F, -0.968F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(27, 16).addBox(-0.4562F, -0.1F, -0.511F, 0.9125F, 2.9924F, 1.022F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9125F, -1.46F, -0.511F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(34, 16).addBox(-0.0876F, -0.1F, -0.1204F, 0.1752F, 1.8277F, 0.2409F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4745F, -3.6208F, -2.263F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-0.2737F, -0.1F, -0.3285F, 0.5475F, 2.3494F, 0.657F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3285F, -2.082F, -0.7694F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(27, 16).addBox(-0.4562F, -0.1F, -0.511F, 0.9125F, 2.4438F, 1.022F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail = corruption_root.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 0.0F, 8.0F, 0.6283F, 0.0F, 0.0F));

        PartDefinition real_tail = tail.addOrReplaceChild("real_tail", CubeListBuilder.create().texOffs(101, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(43, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 4.41F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 4.0F, 7.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(52, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 3.39F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.16F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(61, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.14F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(110, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(43, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 4.41F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 4.0F, -4.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(52, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 3.39F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.16F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(61, 0).addBox(0.0F, -0.25F, -1.0F, 2.0F, 0.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.14F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition zombie_horse() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 2.0F, -12.0F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, -0.25F, -1.9F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -0.25F, -1.9F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 2.0F, 7.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, -0.25F, -1.0F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -0.25F, -1.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition head_parts = corruption_root.addOrReplaceChild("head_parts", CubeListBuilder.create().texOffs(51, 0).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -12.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition head = head_parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(74, 0).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(101, 0).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(101, 0).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mane = head_parts.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(108, 0).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition upper_mouth = head_parts.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(117, 0).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mane_claw = head_parts.addOrReplaceChild("mane_claw", CubeListBuilder.create().texOffs(233, 0).addBox(-1.326F, -1.092F, -1.17F, 2.652F, 2.184F, 2.34F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, -4.0F, 5.3F, -0.16F, 0.15F, -0.46F));

        PartDefinition mane_claw_palm_r1 = mane_claw.addOrReplaceChild("mane_claw_palm_r1", CubeListBuilder.create().texOffs(33, 33).addBox(-0.936F, -0.1F, -0.78F, 1.872F, 1.7064F, 1.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.39F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_0 = mane_claw.addOrReplaceChild("mane_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.975F, -1.56F, -0.546F, 0.0F, -0.2F, -0.14F));

        PartDefinition mane_claw_finger_0_sharp_tip_r1 = mane_claw_finger_0.addOrReplaceChild("mane_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(49, 33).addBox(-0.0936F, -0.1F, -0.1287F, 0.1872F, 1.9392F, 0.2574F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.507F, -3.8688F, -2.418F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition mane_claw_finger_0_blade_r1 = mane_claw_finger_0.addOrReplaceChild("mane_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(7, 33).addBox(-0.2925F, -0.1F, -0.351F, 0.585F, 2.4967F, 0.702F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.351F, -2.2246F, -0.8221F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition mane_claw_finger_0_proximal_r1 = mane_claw_finger_0.addOrReplaceChild("mane_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(42, 33).addBox(-0.4875F, -0.1F, -0.546F, 0.975F, 2.5974F, 1.092F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition mane_claw_finger_1 = mane_claw.addOrReplaceChild("mane_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.56F, -0.546F, 0.09F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_1_sharp_tip_r1 = mane_claw_finger_1.addOrReplaceChild("mane_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(7, 33).addBox(-0.0936F, -0.1F, -0.1287F, 0.1872F, 2.3843F, 0.2574F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.8672F, -3.042F, -1.7955F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_1_blade_r1 = mane_claw_finger_1.addOrReplaceChild("mane_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(28, 33).addBox(-0.2925F, -0.1F, -0.351F, 0.585F, 3.0827F, 0.702F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7986F, -1.0343F, -2.3711F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_1_proximal_r1 = mane_claw_finger_1.addOrReplaceChild("mane_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.4875F, -0.1F, -0.546F, 0.975F, 3.1836F, 1.092F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition mane_claw_finger_2 = mane_claw.addOrReplaceChild("mane_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.975F, -1.56F, -0.546F, 0.18F, 0.2F, 0.14F));

        PartDefinition mane_claw_finger_2_sharp_tip_r1 = mane_claw_finger_2.addOrReplaceChild("mane_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(49, 33).addBox(-0.0936F, -0.1F, -0.1287F, 0.1872F, 1.9392F, 0.2574F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.507F, -3.8688F, -2.418F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition mane_claw_finger_2_blade_r1 = mane_claw_finger_2.addOrReplaceChild("mane_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(7, 33).addBox(-0.2925F, -0.1F, -0.351F, 0.585F, 2.4967F, 0.702F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.351F, -2.2246F, -0.8221F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition mane_claw_finger_2_proximal_r1 = mane_claw_finger_2.addOrReplaceChild("mane_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(42, 33).addBox(-0.4875F, -0.1F, -0.546F, 0.975F, 2.5974F, 1.092F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition head_jaw = head_parts.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(69, 33).addBox(-1.9F, -0.2F, -5.3F, 3.8F, 1.2F, 5.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.9F, -1.9F, 0.13F, 0.0F, 0.0F));

        PartDefinition horse_lower_fang_3_r1 = head_jaw.addOrReplaceChild("horse_lower_fang_3_r1", CubeListBuilder.create().texOffs(90, 33).addBox(-0.13F, -0.1F, -0.16F, 0.26F, 0.8576F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(90, 33).addBox(-1.03F, -0.1F, -0.16F, 0.26F, 0.8576F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(90, 33).addBox(-1.93F, -0.1F, -0.16F, 0.26F, 0.8576F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(90, 33).addBox(-2.83F, -0.1F, -0.16F, 0.26F, 0.8576F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, -0.2F, -5.25F, -2.9889F, 0.0F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 2.0F, 7.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-3.0F, -0.25F, -1.0F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(-3.0F, -0.25F, -1.0F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.58F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(136, 0).addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new CubeDeformation(0.05F))
        .texOffs(54, 33).addBox(4.6F, -5.0F, -3.8F, 1.0F, 4.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 5.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(201, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(216, 0).addBox(-1.955F, -1.61F, -1.725F, 3.91F, 3.22F, 3.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6F, -7.0F, -10.0F, 0.1F, 0.2F, 0.48F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(233, 0).addBox(-1.38F, -0.1F, -1.15F, 2.76F, 2.421F, 2.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.575F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.4375F, -2.3F, -0.805F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(7, 33).addBox(-0.138F, -0.1F, -0.1898F, 0.276F, 2.7642F, 0.3795F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7475F, -5.704F, -3.565F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.4313F, -0.1F, -0.5175F, 0.8625F, 3.5861F, 1.035F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5175F, -3.2798F, -1.2121F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.7188F, -0.1F, -0.805F, 1.4375F, 3.7347F, 1.61F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.3F, -0.805F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(28, 33).addBox(-0.138F, -0.1F, -0.1898F, 0.276F, 3.4205F, 0.3795F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.176F, -4.485F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(21, 33).addBox(-0.4313F, -0.1F, -0.5175F, 0.8625F, 4.4501F, 1.035F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.1262F, -1.5249F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(12, 33).addBox(-0.7188F, -0.1F, -0.805F, 1.4375F, 4.599F, 1.61F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.4375F, -2.3F, -0.805F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(7, 33).addBox(-0.138F, -0.1F, -0.1898F, 0.276F, 2.7642F, 0.3795F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7475F, -5.704F, -3.565F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-0.4312F, -0.1F, -0.5175F, 0.8625F, 3.5861F, 1.035F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5175F, -3.2798F, -1.2121F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(246, 0).addBox(-0.7188F, -0.1F, -0.805F, 1.4375F, 3.7347F, 1.61F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 5.97F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 2.0F, -12.0F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(-3.0F, -0.25F, -1.9F, 4.0F, 4.83F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.71F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(34, 0).mirror().addBox(-3.0F, -0.25F, -1.9F, 4.0F, 0.95F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.58F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static java.util.Map<String,net.minecraft.client.model.geom.ModelPart> parts(net.minecraft.client.model.geom.ModelPart root,String kind) {
        String[] paths=switch(kind) {
            case "allay" -> new String[]{"corruption_root","corruption_root/root","corruption_root/root/head","corruption_root/root/head/head_jaw","corruption_root/root/body","corruption_root/root/body/right_arm","corruption_root/root/body/right_arm/right_arm_lower","corruption_root/root/body/right_arm/right_arm_lower/right_arm_hand","corruption_root/root/body/left_arm","corruption_root/root/body/left_arm/left_arm_lower","corruption_root/root/body/left_arm/left_arm_lower/left_arm_hand","corruption_root/root/body/right_wing","corruption_root/root/body/left_wing","corruption_root/root/body/shoulder_claw","corruption_root/root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/root/body/shoulder_claw/shoulder_claw_finger_2"};
            case "armadillo" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/head","corruption_root/body/head/head_cube","corruption_root/body/head/right_ear","corruption_root/body/head/right_ear/right_ear_cube","corruption_root/body/head/left_ear","corruption_root/body/head/left_ear/left_ear_cube","corruption_root/body/tail","corruption_root/body/carapace_claw","corruption_root/body/carapace_claw/carapace_claw_finger_0","corruption_root/body/carapace_claw/carapace_claw_finger_1","corruption_root/body/carapace_claw/carapace_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "axolotl" -> new String[]{"corruption_root","corruption_root/body","corruption_root/body/head","corruption_root/body/head/head_jaw","corruption_root/body/head/top_gills","corruption_root/body/head/left_gills","corruption_root/body/head/right_gills","corruption_root/body/tail","corruption_root/body/left_front_leg","corruption_root/body/left_hind_leg","corruption_root/body/right_front_leg","corruption_root/body/right_hind_leg","corruption_root/body/flesh_core","corruption_root/body/flesh_front","corruption_root/body/flesh_rear","corruption_root/body/flesh_rear/flank_claw","corruption_root/body/flesh_rear/flank_claw/flank_claw_finger_0","corruption_root/body/flesh_rear/flank_claw/flank_claw_finger_1","corruption_root/body/flesh_rear/flank_claw/flank_claw_finger_2","corruption_root/body/flesh_crown","corruption_root/body/flesh_crown/crown_claw","corruption_root/body/flesh_crown/crown_claw/crown_claw_finger_0","corruption_root/body/flesh_crown/crown_claw/crown_claw_finger_1","corruption_root/body/flesh_crown/crown_claw/crown_claw_finger_2"};
            case "bat" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/body","corruption_root/body/feet","corruption_root/body/right_wing","corruption_root/body/right_wing/right_wing_tip","corruption_root/body/left_wing","corruption_root/body/left_wing/left_wing_tip","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2"};
            case "bee" -> new String[]{"corruption_root","corruption_root/bone","corruption_root/bone/front_legs","corruption_root/bone/right_wing","corruption_root/bone/left_wing","corruption_root/bone/middle_legs","corruption_root/bone/body","corruption_root/bone/body/right_antenna","corruption_root/bone/body/stinger","corruption_root/bone/body/left_antenna","corruption_root/bone/body/stinger_claw","corruption_root/bone/body/stinger_claw/stinger_claw_finger_0","corruption_root/bone/body/stinger_claw/stinger_claw_finger_1","corruption_root/bone/body/stinger_claw/stinger_claw_finger_2","corruption_root/bone/body/head_jaw","corruption_root/bone/back_legs"};
            case "bogged" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/skull_claw","corruption_root/head/skull_claw/skull_claw_finger_0","corruption_root/head/skull_claw/skull_claw_finger_1","corruption_root/head/skull_claw/skull_claw_finger_2","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/rib_claw","corruption_root/body/rib_claw/rib_claw_finger_0","corruption_root/body/rib_claw/rib_claw_finger_1","corruption_root/body/rib_claw/rib_claw_finger_2"};
            case "breeze" -> new String[]{"corruption_root","corruption_root/body","corruption_root/body/head","corruption_root/body/head/crown_claw","corruption_root/body/head/crown_claw/crown_claw_finger_0","corruption_root/body/head/crown_claw/crown_claw_finger_1","corruption_root/body/head/crown_claw/crown_claw_finger_2","corruption_root/body/rods","corruption_root/body/rods/rod_2","corruption_root/body/rods/rod_3","corruption_root/body/rods/rod_1","corruption_root/wind_body","corruption_root/wind_body/wind_bottom","corruption_root/wind_body/wind_bottom/wind_mid","corruption_root/wind_body/wind_bottom/wind_mid/wind_top"};
            case "camel" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/head","corruption_root/body/head/right_ear","corruption_root/body/head/left_ear","corruption_root/body/head/neck_claw","corruption_root/body/head/neck_claw/neck_claw_finger_0","corruption_root/body/head/neck_claw/neck_claw_finger_1","corruption_root/body/head/neck_claw/neck_claw_finger_2","corruption_root/body/head/head_jaw","corruption_root/body/hump","corruption_root/body/hump/hump_claw","corruption_root/body/hump/hump_claw/hump_claw_finger_0","corruption_root/body/hump/hump_claw/hump_claw_finger_1","corruption_root/body/hump/hump_claw/hump_claw_finger_2","corruption_root/body/tail","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "cat" -> new String[]{"corruption_root","corruption_root/head","corruption_root/tail1","corruption_root/tail1/tail2","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "cave_spider" -> new String[]{"corruption_root","corruption_root/head","corruption_root/body0","corruption_root/body1","corruption_root/body1/abdomen_claw","corruption_root/body1/abdomen_claw/abdomen_claw_finger_0","corruption_root/body1/abdomen_claw/abdomen_claw_finger_1","corruption_root/body1/abdomen_claw/abdomen_claw_finger_2","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_knee","corruption_root/right_middle_hind_leg","corruption_root/right_middle_hind_leg/right_middle_hind_leg_knee","corruption_root/right_middle_front_leg","corruption_root/right_middle_front_leg/right_middle_front_leg_knee","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_knee","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_knee","corruption_root/left_middle_hind_leg","corruption_root/left_middle_hind_leg/left_middle_hind_leg_knee","corruption_root/left_middle_front_leg","corruption_root/left_middle_front_leg/left_middle_front_leg_knee","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_knee"};
            case "cod" -> new String[]{"corruption_root","corruption_root/head","corruption_root/nose","corruption_root/left_fin","corruption_root/top_fin","corruption_root/right_fin","corruption_root/body","corruption_root/body/dorsal_claw","corruption_root/body/dorsal_claw/dorsal_claw_finger_0","corruption_root/body/dorsal_claw/dorsal_claw_finger_1","corruption_root/body/dorsal_claw/dorsal_claw_finger_2","corruption_root/tail_fin"};
            case "dolphin" -> new String[]{"corruption_root","corruption_root/body","corruption_root/body/head","corruption_root/body/head/nose","corruption_root/body/head/head_jaw","corruption_root/body/left_fin","corruption_root/body/right_fin","corruption_root/body/tail","corruption_root/body/tail/tail_fin","corruption_root/body/back_fin","corruption_root/body/dorsal_claw","corruption_root/body/dorsal_claw/dorsal_claw_finger_0","corruption_root/body/dorsal_claw/dorsal_claw_finger_1","corruption_root/body/dorsal_claw/dorsal_claw_finger_2"};
            case "drowned" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/gill_claw","corruption_root/head/gill_claw/gill_claw_finger_0","corruption_root/head/gill_claw/gill_claw_finger_1","corruption_root/head/gill_claw/gill_claw_finger_2","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2"};
            case "elder_guardian" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/spike10","corruption_root/head/spike11","corruption_root/head/spike6","corruption_root/head/spike7","corruption_root/head/spike8","corruption_root/head/spike9","corruption_root/head/tail0","corruption_root/head/tail0/tail1","corruption_root/head/tail0/tail1/tail2","corruption_root/head/tail0/tail1/tail_claw","corruption_root/head/tail0/tail1/tail_claw/tail_claw_finger_0","corruption_root/head/tail0/tail1/tail_claw/tail_claw_finger_1","corruption_root/head/tail0/tail1/tail_claw/tail_claw_finger_2","corruption_root/head/spike0","corruption_root/head/spike1","corruption_root/head/spike2","corruption_root/head/spike3","corruption_root/head/spike4","corruption_root/head/spike5","corruption_root/head/dorsal_claw","corruption_root/head/dorsal_claw/dorsal_claw_finger_0","corruption_root/head/dorsal_claw/dorsal_claw_finger_1","corruption_root/head/dorsal_claw/dorsal_claw_finger_2","corruption_root/head/flank_claw","corruption_root/head/flank_claw/flank_claw_finger_0","corruption_root/head/flank_claw/flank_claw_finger_1","corruption_root/head/flank_claw/flank_claw_finger_2"};
            case "fox" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/nose","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/tail","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "frog" -> new String[]{"corruption_root","corruption_root/root","corruption_root/root/left_leg","corruption_root/root/left_leg/left_foot","corruption_root/root/right_leg","corruption_root/root/right_leg/right_foot","corruption_root/root/body","corruption_root/root/body/head","corruption_root/root/body/head/eyes","corruption_root/root/body/head/eyes/right_eye","corruption_root/root/body/head/eyes/left_eye","corruption_root/root/body/right_arm","corruption_root/root/body/right_arm/right_hand","corruption_root/root/body/tongue","corruption_root/root/body/left_arm","corruption_root/root/body/left_arm/left_hand","corruption_root/root/body/croaking_body","corruption_root/root/body/back_claw","corruption_root/root/body/back_claw/back_claw_finger_0","corruption_root/root/body/back_claw/back_claw_finger_1","corruption_root/root/body/back_claw/back_claw_finger_2"};
            case "glow_squid" -> new String[]{"corruption_root","corruption_root/tentacle0","corruption_root/tentacle0/tentacle0_middle","corruption_root/tentacle0/tentacle0_middle/tentacle0_tip","corruption_root/tentacle1","corruption_root/tentacle1/tentacle1_middle","corruption_root/tentacle1/tentacle1_middle/tentacle1_tip","corruption_root/tentacle6","corruption_root/tentacle6/tentacle6_middle","corruption_root/tentacle6/tentacle6_middle/tentacle6_tip","corruption_root/tentacle7","corruption_root/tentacle7/tentacle7_middle","corruption_root/tentacle7/tentacle7_middle/tentacle7_tip","corruption_root/body","corruption_root/body/mantle_claw","corruption_root/body/mantle_claw/mantle_claw_finger_0","corruption_root/body/mantle_claw/mantle_claw_finger_1","corruption_root/body/mantle_claw/mantle_claw_finger_2","corruption_root/body/lower_mantle_claw","corruption_root/body/lower_mantle_claw/lower_mantle_claw_finger_0","corruption_root/body/lower_mantle_claw/lower_mantle_claw_finger_1","corruption_root/body/lower_mantle_claw/lower_mantle_claw_finger_2","corruption_root/tentacle4","corruption_root/tentacle4/tentacle4_middle","corruption_root/tentacle4/tentacle4_middle/tentacle4_tip","corruption_root/tentacle5","corruption_root/tentacle5/tentacle5_middle","corruption_root/tentacle5/tentacle5_middle/tentacle5_tip","corruption_root/tentacle2","corruption_root/tentacle2/tentacle2_middle","corruption_root/tentacle2/tentacle2_middle/tentacle2_tip","corruption_root/tentacle3","corruption_root/tentacle3/tentacle3_middle","corruption_root/tentacle3/tentacle3_middle/tentacle3_tip"};
            case "goat" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/nose","corruption_root/head/right_horn","corruption_root/head/left_horn","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "guardian" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/spike10","corruption_root/head/spike11","corruption_root/head/spike6","corruption_root/head/spike7","corruption_root/head/spike8","corruption_root/head/spike9","corruption_root/head/eye","corruption_root/head/tail0","corruption_root/head/tail0/tail1","corruption_root/head/tail0/tail1/tail2","corruption_root/head/tail0/tail1/tail_claw","corruption_root/head/tail0/tail1/tail_claw/tail_claw_finger_0","corruption_root/head/tail0/tail1/tail_claw/tail_claw_finger_1","corruption_root/head/tail0/tail1/tail_claw/tail_claw_finger_2","corruption_root/head/spike0","corruption_root/head/spike1","corruption_root/head/spike2","corruption_root/head/spike3","corruption_root/head/spike4","corruption_root/head/spike5","corruption_root/head/dorsal_claw","corruption_root/head/dorsal_claw/dorsal_claw_finger_0","corruption_root/head/dorsal_claw/dorsal_claw_finger_1","corruption_root/head/dorsal_claw/dorsal_claw_finger_2","corruption_root/head/flank_claw","corruption_root/head/flank_claw/flank_claw_finger_0","corruption_root/head/flank_claw/flank_claw_finger_1","corruption_root/head/flank_claw/flank_claw_finger_2","corruption_root/head/head_jaw"};
            case "husk" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/skull_crown_claw","corruption_root/head/skull_crown_claw/skull_crown_claw_finger_0","corruption_root/head/skull_crown_claw/skull_crown_claw_finger_1","corruption_root/head/skull_crown_claw/skull_crown_claw_finger_2","corruption_root/head/skull_left_claw","corruption_root/head/skull_left_claw/skull_left_claw_finger_0","corruption_root/head/skull_left_claw/skull_left_claw_finger_1","corruption_root/head/skull_left_claw/skull_left_claw_finger_2","corruption_root/head/skull_front_claw","corruption_root/head/skull_front_claw/skull_front_claw_finger_0","corruption_root/head/skull_front_claw/skull_front_claw_finger_1","corruption_root/head/skull_front_claw/skull_front_claw_finger_2","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/right_arm/right_arm_lower/elbow_claw","corruption_root/right_arm/right_arm_lower/elbow_claw/elbow_claw_finger_0","corruption_root/right_arm/right_arm_lower/elbow_claw/elbow_claw_finger_1","corruption_root/right_arm/right_arm_lower/elbow_claw/elbow_claw_finger_2","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2"};
            case "iron_golem" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/right_arm/right_arm_lower/right_arm_hand/tyrant_finger_0","corruption_root/right_arm/right_arm_lower/right_arm_hand/tyrant_finger_1","corruption_root/right_arm/right_arm_lower/right_arm_hand/tyrant_finger_2","corruption_root/right_arm/right_arm_lower/right_arm_hand/tyrant_finger_3","corruption_root/right_arm/right_arm_lower/right_arm_hand/tyrant_finger_4","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/left_arm/left_arm_lower/forearm_claw","corruption_root/left_arm/left_arm_lower/forearm_claw/forearm_claw_finger_0","corruption_root/left_arm/left_arm_lower/forearm_claw/forearm_claw_finger_1","corruption_root/left_arm/left_arm_lower/forearm_claw/forearm_claw_finger_2","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2"};
            case "llama" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/neck_claw","corruption_root/head/neck_claw/neck_claw_finger_0","corruption_root/head/neck_claw/neck_claw_finger_1","corruption_root/head/neck_claw/neck_claw_finger_2","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "mooshroom" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/skull_claw","corruption_root/head/skull_claw/skull_claw_finger_0","corruption_root/head/skull_claw/skull_claw_finger_1","corruption_root/head/skull_claw/skull_claw_finger_2","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/corrupt_mushroom_0","corruption_root/body/corrupt_mushroom_0/corrupt_mushroom_0_claw","corruption_root/body/corrupt_mushroom_0/corrupt_mushroom_0_claw/corrupt_mushroom_0_claw_finger_0","corruption_root/body/corrupt_mushroom_0/corrupt_mushroom_0_claw/corrupt_mushroom_0_claw_finger_1","corruption_root/body/corrupt_mushroom_0/corrupt_mushroom_0_claw/corrupt_mushroom_0_claw_finger_2","corruption_root/body/corrupt_mushroom_1","corruption_root/body/corrupt_mushroom_1/corrupt_mushroom_1_claw","corruption_root/body/corrupt_mushroom_1/corrupt_mushroom_1_claw/corrupt_mushroom_1_claw_finger_0","corruption_root/body/corrupt_mushroom_1/corrupt_mushroom_1_claw/corrupt_mushroom_1_claw_finger_1","corruption_root/body/corrupt_mushroom_1/corrupt_mushroom_1_claw/corrupt_mushroom_1_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "mule" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/head_parts","corruption_root/head_parts/head","corruption_root/head_parts/head/right_ear","corruption_root/head_parts/head/left_ear","corruption_root/head_parts/mane","corruption_root/head_parts/upper_mouth","corruption_root/head_parts/mane_claw","corruption_root/head_parts/mane_claw/mane_claw_finger_0","corruption_root/head_parts/mane_claw/mane_claw_finger_1","corruption_root/head_parts/mane_claw/mane_claw_finger_2","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/tail","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "ocelot" -> new String[]{"corruption_root","corruption_root/head","corruption_root/tail1","corruption_root/tail1/tail2","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "panda" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_front_leg/right_front_leg_lower/forearm_claw","corruption_root/right_front_leg/right_front_leg_lower/forearm_claw/forearm_claw_finger_0","corruption_root/right_front_leg/right_front_leg_lower/forearm_claw/forearm_claw_finger_1","corruption_root/right_front_leg/right_front_leg_lower/forearm_claw/forearm_claw_finger_2","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "parrot" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/beak1","corruption_root/head/beak2","corruption_root/head/feather","corruption_root/head/head2","corruption_root/head/crest_claw","corruption_root/head/crest_claw/crest_claw_finger_0","corruption_root/head/crest_claw/crest_claw_finger_1","corruption_root/head/crest_claw/crest_claw_finger_2","corruption_root/head/beak_jaw","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/right_wing","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/tail","corruption_root/left_wing","corruption_root/body"};
            case "phantom" -> new String[]{"corruption_root","corruption_root/body","corruption_root/body/head","corruption_root/body/head/head_jaw","corruption_root/body/right_wing_base","corruption_root/body/right_wing_base/right_wing_tip","corruption_root/body/tail_base","corruption_root/body/tail_base/tail_tip","corruption_root/body/left_wing_base","corruption_root/body/left_wing_base/left_wing_tip","corruption_root/body/dorsal_claw","corruption_root/body/dorsal_claw/dorsal_claw_finger_0","corruption_root/body/dorsal_claw/dorsal_claw_finger_1","corruption_root/body/dorsal_claw/dorsal_claw_finger_2"};
            case "polar_bear" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/body/flank_claw","corruption_root/body/flank_claw/flank_claw_finger_0","corruption_root/body/flank_claw/flank_claw_finger_1","corruption_root/body/flank_claw/flank_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "pufferfish" -> new String[]{"corruption_root","corruption_root/puffer_state_0","corruption_root/puffer_state_0/puffer_0_right_eye","corruption_root/puffer_state_0/puffer_0_left_fin","corruption_root/puffer_state_0/puffer_0_right_fin","corruption_root/puffer_state_0/puffer_0_left_eye","corruption_root/puffer_state_0/puffer_0_body","corruption_root/puffer_state_0/puffer_0_body/puffer_0_dorsal_claw","corruption_root/puffer_state_0/puffer_0_body/puffer_0_dorsal_claw/puffer_0_dorsal_claw_finger_0","corruption_root/puffer_state_0/puffer_0_body/puffer_0_dorsal_claw/puffer_0_dorsal_claw_finger_1","corruption_root/puffer_state_0/puffer_0_body/puffer_0_dorsal_claw/puffer_0_dorsal_claw_finger_2","corruption_root/puffer_state_0/puffer_0_back_fin","corruption_root/puffer_state_1","corruption_root/puffer_state_1/puffer_1_left_blue_fin","corruption_root/puffer_state_1/puffer_1_top_back_fin","corruption_root/puffer_state_1/puffer_1_left_back_fin","corruption_root/puffer_state_1/puffer_1_left_front_fin","corruption_root/puffer_state_1/puffer_1_bottom_front_fin","corruption_root/puffer_state_1/puffer_1_right_front_fin","corruption_root/puffer_state_1/puffer_1_right_back_fin","corruption_root/puffer_state_1/puffer_1_body","corruption_root/puffer_state_1/puffer_1_body/puffer_1_dorsal_claw","corruption_root/puffer_state_1/puffer_1_body/puffer_1_dorsal_claw/puffer_1_dorsal_claw_finger_0","corruption_root/puffer_state_1/puffer_1_body/puffer_1_dorsal_claw/puffer_1_dorsal_claw_finger_1","corruption_root/puffer_state_1/puffer_1_body/puffer_1_dorsal_claw/puffer_1_dorsal_claw_finger_2","corruption_root/puffer_state_1/puffer_1_top_front_fin","corruption_root/puffer_state_1/puffer_1_bottom_back_fin","corruption_root/puffer_state_1/puffer_1_right_blue_fin","corruption_root/puffer_state_2","corruption_root/puffer_state_2/puffer_2_left_blue_fin","corruption_root/puffer_state_2/puffer_2_top_back_fin","corruption_root/puffer_state_2/puffer_2_top_middle_fin","corruption_root/puffer_state_2/puffer_2_bottom_front_fin","corruption_root/puffer_state_2/puffer_2_right_front_fin","corruption_root/puffer_state_2/puffer_2_body","corruption_root/puffer_state_2/puffer_2_body/puffer_2_dorsal_claw","corruption_root/puffer_state_2/puffer_2_body/puffer_2_dorsal_claw/puffer_2_dorsal_claw_finger_0","corruption_root/puffer_state_2/puffer_2_body/puffer_2_dorsal_claw/puffer_2_dorsal_claw_finger_1","corruption_root/puffer_state_2/puffer_2_body/puffer_2_dorsal_claw/puffer_2_dorsal_claw_finger_2","corruption_root/puffer_state_2/puffer_2_bottom_back_fin","corruption_root/puffer_state_2/puffer_2_right_blue_fin","corruption_root/puffer_state_2/puffer_2_left_front_fin","corruption_root/puffer_state_2/puffer_2_bottom_middle_fin","corruption_root/puffer_state_2/puffer_2_left_back_fin","corruption_root/puffer_state_2/puffer_2_right_back_fin","corruption_root/puffer_state_2/puffer_2_top_front_fin"};
            case "rabbit" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/head/nose","corruption_root/head/head_jaw","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/tail","corruption_root/left_haunch","corruption_root/left_haunch/left_hind_foot","corruption_root/right_haunch","corruption_root/right_haunch/right_hind_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "salmon" -> new String[]{"corruption_root","corruption_root/body_front","corruption_root/body_front/top_front_fin","corruption_root/body_front/body_back","corruption_root/body_front/body_back/top_back_fin","corruption_root/body_front/body_back/back_fin","corruption_root/body_front/head","corruption_root/body_front/left_fin","corruption_root/body_front/right_fin","corruption_root/body_front/dorsal_claw","corruption_root/body_front/dorsal_claw/dorsal_claw_finger_0","corruption_root/body_front/dorsal_claw/dorsal_claw_finger_1","corruption_root/body_front/dorsal_claw/dorsal_claw_finger_2"};
            case "silverfish" -> new String[]{"corruption_root","corruption_root/segment0","corruption_root/segment0/segment1","corruption_root/segment0/segment1/segment2","corruption_root/segment0/segment1/segment2/segment3","corruption_root/segment0/segment1/segment2/segment3/segment4","corruption_root/segment0/segment1/segment2/segment3/segment4/segment5","corruption_root/segment0/segment1/segment2/segment3/segment4/segment5/segment6","corruption_root/segment0/segment1/segment2/segment3/segment4/segment5/tail_claw","corruption_root/segment0/segment1/segment2/segment3/segment4/segment5/tail_claw/tail_claw_finger_0","corruption_root/segment0/segment1/segment2/segment3/segment4/segment5/tail_claw/tail_claw_finger_1","corruption_root/segment0/segment1/segment2/segment3/segment4/segment5/tail_claw/tail_claw_finger_2","corruption_root/segment0/segment1/segment2/segment3/segment4/layer1","corruption_root/segment0/segment1/segment2/layer0","corruption_root/segment0/segment1/segment2/carapace_claw","corruption_root/segment0/segment1/segment2/carapace_claw/carapace_claw_finger_0","corruption_root/segment0/segment1/segment2/carapace_claw/carapace_claw_finger_1","corruption_root/segment0/segment1/segment2/carapace_claw/carapace_claw_finger_2","corruption_root/segment0/segment1/layer2"};
            case "skeleton_horse" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/head_parts","corruption_root/head_parts/head","corruption_root/head_parts/head/right_ear","corruption_root/head_parts/head/left_ear","corruption_root/head_parts/mane","corruption_root/head_parts/upper_mouth","corruption_root/head_parts/spinal_claw","corruption_root/head_parts/spinal_claw/spinal_claw_finger_0","corruption_root/head_parts/spinal_claw/spinal_claw_finger_1","corruption_root/head_parts/spinal_claw/spinal_claw_finger_2","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/tail","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "slime" -> new String[]{"corruption_root","corruption_root/cube","corruption_root/cube/blood_claw_0","corruption_root/cube/blood_claw_0/blood_claw_0_finger_0","corruption_root/cube/blood_claw_0/blood_claw_0_finger_1","corruption_root/cube/blood_claw_0/blood_claw_0_finger_2","corruption_root/cube/blood_claw_1","corruption_root/cube/blood_claw_1/blood_claw_1_finger_0","corruption_root/cube/blood_claw_1/blood_claw_1_finger_1","corruption_root/cube/blood_claw_1/blood_claw_1_finger_2","corruption_root/cube/blood_claw_2","corruption_root/cube/blood_claw_2/blood_claw_2_finger_0","corruption_root/cube/blood_claw_2/blood_claw_2_finger_1","corruption_root/cube/blood_claw_2/blood_claw_2_finger_2","corruption_root/cube/blood_claw_3","corruption_root/cube/blood_claw_3/blood_claw_3_finger_0","corruption_root/cube/blood_claw_3/blood_claw_3_finger_1","corruption_root/cube/blood_claw_3/blood_claw_3_finger_2","corruption_root/cube/blood_claw_4","corruption_root/cube/blood_claw_4/blood_claw_4_finger_0","corruption_root/cube/blood_claw_4/blood_claw_4_finger_1","corruption_root/cube/blood_claw_4/blood_claw_4_finger_2","corruption_root/cube/blood_claw_center","corruption_root/cube/blood_claw_center/blood_claw_center_finger_0","corruption_root/cube/blood_claw_center/blood_claw_center_finger_1","corruption_root/cube/blood_claw_center/blood_claw_center_finger_2"};
            case "sniffer" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/right_mid_leg","corruption_root/right_mid_leg/right_mid_leg_lower","corruption_root/right_mid_leg/right_mid_leg_lower/right_mid_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/head","corruption_root/body/head/nose","corruption_root/body/head/lower_beak","corruption_root/body/head/right_ear","corruption_root/body/head/left_ear","corruption_root/body/head/snout_claw","corruption_root/body/head/snout_claw/snout_claw_finger_0","corruption_root/body/head/snout_claw/snout_claw_finger_1","corruption_root/body/head/snout_claw/snout_claw_finger_2","corruption_root/body/mantle_claw","corruption_root/body/mantle_claw/mantle_claw_finger_0","corruption_root/body/mantle_claw/mantle_claw_finger_1","corruption_root/body/mantle_claw/mantle_claw_finger_2","corruption_root/body/rear_mantle_claw","corruption_root/body/rear_mantle_claw/rear_mantle_claw_finger_0","corruption_root/body/rear_mantle_claw/rear_mantle_claw_finger_1","corruption_root/body/rear_mantle_claw/rear_mantle_claw_finger_2","corruption_root/left_mid_leg","corruption_root/left_mid_leg/left_mid_leg_lower","corruption_root/left_mid_leg/left_mid_leg_lower/left_mid_leg_foot","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "snow_golem" -> new String[]{"corruption_root","corruption_root/upper_body","corruption_root/upper_body/head","corruption_root/upper_body/right_arm","corruption_root/upper_body/left_arm","corruption_root/upper_body/pumpkin_right_half","corruption_root/upper_body/pumpkin_left_half","corruption_root/upper_body/snow_shoulder_claw","corruption_root/upper_body/snow_shoulder_claw/snow_shoulder_claw_finger_0","corruption_root/upper_body/snow_shoulder_claw/snow_shoulder_claw_finger_1","corruption_root/upper_body/snow_shoulder_claw/snow_shoulder_claw_finger_2","corruption_root/lower_body"};
            case "squid" -> new String[]{"corruption_root","corruption_root/tentacle0","corruption_root/tentacle0/tentacle0_middle","corruption_root/tentacle0/tentacle0_middle/tentacle0_tip","corruption_root/tentacle1","corruption_root/tentacle1/tentacle1_middle","corruption_root/tentacle1/tentacle1_middle/tentacle1_tip","corruption_root/tentacle6","corruption_root/tentacle6/tentacle6_middle","corruption_root/tentacle6/tentacle6_middle/tentacle6_tip","corruption_root/tentacle7","corruption_root/tentacle7/tentacle7_middle","corruption_root/tentacle7/tentacle7_middle/tentacle7_tip","corruption_root/body","corruption_root/body/crown_spine","corruption_root/tentacle4","corruption_root/tentacle4/tentacle4_middle","corruption_root/tentacle4/tentacle4_middle/tentacle4_tip","corruption_root/tentacle5","corruption_root/tentacle5/tentacle5_middle","corruption_root/tentacle5/tentacle5_middle/tentacle5_tip","corruption_root/tentacle2","corruption_root/tentacle2/tentacle2_middle","corruption_root/tentacle2/tentacle2_middle/tentacle2_tip","corruption_root/tentacle3","corruption_root/tentacle3/tentacle3_middle","corruption_root/tentacle3/tentacle3_middle/tentacle3_tip"};
            case "stray" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/rib_claw","corruption_root/body/rib_claw/rib_claw_finger_0","corruption_root/body/rib_claw/rib_claw_finger_1","corruption_root/body/rib_claw/rib_claw_finger_2"};
            case "tadpole" -> new String[]{"corruption_root","corruption_root/body","corruption_root/body/tail","corruption_root/body/back_claw","corruption_root/body/back_claw/back_claw_finger_0","corruption_root/body/back_claw/back_claw_finger_1","corruption_root/body/back_claw/back_claw_finger_2"};
            case "trader_llama" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/neck_claw","corruption_root/head/neck_claw/neck_claw_finger_0","corruption_root/head/neck_claw/neck_claw_finger_1","corruption_root/head/neck_claw/neck_claw_finger_2","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "tropical_fish" -> new String[]{"corruption_root","corruption_root/fish_state_0","corruption_root/fish_state_0/fish_0_left_fin","corruption_root/fish_state_0/fish_0_top_fin","corruption_root/fish_state_0/fish_0_tail","corruption_root/fish_state_0/fish_0_right_fin","corruption_root/fish_state_0/fish_0_body","corruption_root/fish_state_0/fish_0_body/fish_0_dorsal_claw","corruption_root/fish_state_0/fish_0_body/fish_0_dorsal_claw/fish_0_dorsal_claw_finger_0","corruption_root/fish_state_0/fish_0_body/fish_0_dorsal_claw/fish_0_dorsal_claw_finger_1","corruption_root/fish_state_0/fish_0_body/fish_0_dorsal_claw/fish_0_dorsal_claw_finger_2","corruption_root/fish_state_1","corruption_root/fish_state_1/fish_1_left_fin","corruption_root/fish_state_1/fish_1_top_fin","corruption_root/fish_state_1/fish_1_tail","corruption_root/fish_state_1/fish_1_right_fin","corruption_root/fish_state_1/fish_1_body","corruption_root/fish_state_1/fish_1_body/fish_1_dorsal_claw","corruption_root/fish_state_1/fish_1_body/fish_1_dorsal_claw/fish_1_dorsal_claw_finger_0","corruption_root/fish_state_1/fish_1_body/fish_1_dorsal_claw/fish_1_dorsal_claw_finger_1","corruption_root/fish_state_1/fish_1_body/fish_1_dorsal_claw/fish_1_dorsal_claw_finger_2","corruption_root/fish_state_1/fish_1_bottom_fin"};
            case "turtle" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/right_front_leg","corruption_root/right_hind_leg","corruption_root/left_hind_leg","corruption_root/body","corruption_root/body/shell_claw","corruption_root/body/shell_claw/shell_claw_finger_0","corruption_root/body/shell_claw/shell_claw_finger_1","corruption_root/body/shell_claw/shell_claw_finger_2","corruption_root/body/rear_shell_claw","corruption_root/body/rear_shell_claw/rear_shell_claw_finger_0","corruption_root/body/rear_shell_claw/rear_shell_claw_finger_1","corruption_root/body/rear_shell_claw/rear_shell_claw_finger_2","corruption_root/left_front_leg"};
            case "vex" -> new String[]{"corruption_root","corruption_root/root","corruption_root/root/head","corruption_root/root/head/head_jaw","corruption_root/root/body","corruption_root/root/body/right_arm","corruption_root/root/body/right_arm/right_arm_lower","corruption_root/root/body/right_arm/right_arm_lower/right_arm_hand","corruption_root/root/body/left_arm","corruption_root/root/body/left_arm/left_arm_lower","corruption_root/root/body/left_arm/left_arm_lower/left_arm_hand","corruption_root/root/body/right_wing","corruption_root/root/body/left_wing","corruption_root/root/body/shoulder_claw","corruption_root/root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/root/body/shoulder_claw/shoulder_claw_finger_2"};
            case "wandering_trader" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/nose","corruption_root/head/hat","corruption_root/head/hood_claw","corruption_root/head/hood_claw/hood_claw_finger_0","corruption_root/head/hood_claw/hood_claw_finger_1","corruption_root/head/hood_claw/hood_claw_finger_2","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/jacket","corruption_root/body/back_claw","corruption_root/body/back_claw/back_claw_finger_0","corruption_root/body/back_claw/back_claw_finger_1","corruption_root/body/back_claw/back_claw_finger_2"};
            case "warden" -> new String[]{"corruption_root","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/head","corruption_root/body/head/right_tendril","corruption_root/body/head/left_tendril","corruption_root/body/left_ribcage","corruption_root/body/right_ribcage","corruption_root/body/back_claw","corruption_root/body/back_claw/back_claw_finger_0","corruption_root/body/back_claw/back_claw_finger_1","corruption_root/body/back_claw/back_claw_finger_2","corruption_root/body/spine_claw_0","corruption_root/body/spine_claw_0/spine_claw_0_mid","corruption_root/body/spine_claw_0/spine_claw_0_mid/spine_claw_0_tipgrp","corruption_root/body/spine_claw_1","corruption_root/body/spine_claw_1/spine_claw_1_mid","corruption_root/body/spine_claw_1/spine_claw_1_mid/spine_claw_1_tipgrp","corruption_root/body/spine_claw_2","corruption_root/body/spine_claw_2/spine_claw_2_mid","corruption_root/body/spine_claw_2/spine_claw_2_mid/spine_claw_2_tipgrp","corruption_root/body/left_flank_claw_0","corruption_root/body/left_flank_claw_0/left_flank_claw_0_mid","corruption_root/body/left_flank_claw_0/left_flank_claw_0_mid/left_flank_claw_0_tipgrp","corruption_root/body/left_flank_claw_1","corruption_root/body/left_flank_claw_1/left_flank_claw_1_mid","corruption_root/body/left_flank_claw_1/left_flank_claw_1_mid/left_flank_claw_1_tipgrp","corruption_root/body/right_flank_claw_0","corruption_root/body/right_flank_claw_0/right_flank_claw_0_mid","corruption_root/body/right_flank_claw_0/right_flank_claw_0_mid/right_flank_claw_0_tipgrp","corruption_root/body/right_flank_claw_1","corruption_root/body/right_flank_claw_1/right_flank_claw_1_mid","corruption_root/body/right_flank_claw_1/right_flank_claw_1_mid/right_flank_claw_1_tipgrp","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/left_arm/left_arm_lower/left_arm_hand/left_dragging_hand_claw","corruption_root/left_arm/left_arm_lower/left_arm_hand/left_dragging_hand_claw/left_dragging_hand_claw_finger_0","corruption_root/left_arm/left_arm_lower/left_arm_hand/left_dragging_hand_claw/left_dragging_hand_claw_finger_1","corruption_root/left_arm/left_arm_lower/left_arm_hand/left_dragging_hand_claw/left_dragging_hand_claw_finger_2","corruption_root/left_arm/left_shoulder_claw_0","corruption_root/left_arm/left_shoulder_claw_0/left_shoulder_claw_0_mid","corruption_root/left_arm/left_shoulder_claw_0/left_shoulder_claw_0_mid/left_shoulder_claw_0_tipgrp","corruption_root/left_arm/left_shoulder_claw_1","corruption_root/left_arm/left_shoulder_claw_1/left_shoulder_claw_1_mid","corruption_root/left_arm/left_shoulder_claw_1/left_shoulder_claw_1_mid/left_shoulder_claw_1_tipgrp","corruption_root/left_arm/left_shoulder_claw_2","corruption_root/left_arm/left_shoulder_claw_2/left_shoulder_claw_2_mid","corruption_root/left_arm/left_shoulder_claw_2/left_shoulder_claw_2_mid/left_shoulder_claw_2_tipgrp","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/right_arm/right_arm_lower/right_arm_hand/right_dragging_hand_claw","corruption_root/right_arm/right_arm_lower/right_arm_hand/right_dragging_hand_claw/right_dragging_hand_claw_finger_0","corruption_root/right_arm/right_arm_lower/right_arm_hand/right_dragging_hand_claw/right_dragging_hand_claw_finger_1","corruption_root/right_arm/right_arm_lower/right_arm_hand/right_dragging_hand_claw/right_dragging_hand_claw_finger_2","corruption_root/right_arm/right_shoulder_claw_0","corruption_root/right_arm/right_shoulder_claw_0/right_shoulder_claw_0_mid","corruption_root/right_arm/right_shoulder_claw_0/right_shoulder_claw_0_mid/right_shoulder_claw_0_tipgrp","corruption_root/right_arm/right_shoulder_claw_1","corruption_root/right_arm/right_shoulder_claw_1/right_shoulder_claw_1_mid","corruption_root/right_arm/right_shoulder_claw_1/right_shoulder_claw_1_mid/right_shoulder_claw_1_tipgrp","corruption_root/right_arm/right_shoulder_claw_2","corruption_root/right_arm/right_shoulder_claw_2/right_shoulder_claw_2_mid","corruption_root/right_arm/right_shoulder_claw_2/right_shoulder_claw_2_mid/right_shoulder_claw_2_tipgrp"};
            case "witch" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/nose","corruption_root/head/nose/mole","corruption_root/head/hat","corruption_root/head/hat/hat_rim","corruption_root/head/hat/hat2","corruption_root/head/hat/hat2/hat3","corruption_root/head/hat/hat2/hat3/hat4","corruption_root/head/hat/hat2/hat_claw","corruption_root/head/hat/hat2/hat_claw/hat_claw_finger_0","corruption_root/head/hat/hat2/hat_claw/hat_claw_finger_1","corruption_root/head/hat/hat2/hat_claw/hat_claw_finger_2","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/jacket","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand"};
            case "wolf" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/wolf_upper_jaw","corruption_root/head/wolf_upper_jaw/real_head","corruption_root/head/head_jaw","corruption_root/head/mouth_right_claw","corruption_root/head/mouth_right_claw/mouth_right_claw_finger_0","corruption_root/head/mouth_right_claw/mouth_right_claw_finger_1","corruption_root/head/mouth_right_claw/mouth_right_claw_finger_2","corruption_root/head/mouth_left_claw","corruption_root/head/mouth_left_claw/mouth_left_claw_finger_0","corruption_root/head/mouth_left_claw/mouth_left_claw_finger_1","corruption_root/head/mouth_left_claw/mouth_left_claw_finger_2","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/upper_body","corruption_root/upper_body/shoulder_claw","corruption_root/upper_body/shoulder_claw/shoulder_claw_finger_0","corruption_root/upper_body/shoulder_claw/shoulder_claw_finger_1","corruption_root/upper_body/shoulder_claw/shoulder_claw_finger_2","corruption_root/tail","corruption_root/tail/real_tail","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "zombie_horse" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/head_parts","corruption_root/head_parts/head","corruption_root/head_parts/head/right_ear","corruption_root/head_parts/head/left_ear","corruption_root/head_parts/mane","corruption_root/head_parts/upper_mouth","corruption_root/head_parts/mane_claw","corruption_root/head_parts/mane_claw/mane_claw_finger_0","corruption_root/head_parts/mane_claw/mane_claw_finger_1","corruption_root/head_parts/mane_claw/mane_claw_finger_2","corruption_root/head_parts/head_jaw","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/tail","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            default -> throw new IllegalArgumentException(kind);
        };
        var result=new java.util.HashMap<String,net.minecraft.client.model.geom.ModelPart>();
        for(String path:paths) {var part=root;String[] names=path.split("/");for(String name:names)part=part.getChild(name);result.put(names[names.length-1],part);}
        return result;
    }

    private CorruptedWildlifeGeometry() {}
}
