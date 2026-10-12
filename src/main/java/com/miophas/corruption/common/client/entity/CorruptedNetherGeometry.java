package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/** Static meshes exported from editable native-derived Blockbench projects. */
final class CorruptedNetherGeometry {
    static LayerDefinition piglin() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-3.0F, -0.18F, -2.0F, 4.0F, 5.82F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition shoulder_claw = right_arm.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(80, 17).addBox(-1.445F, -1.19F, -1.275F, 2.89F, 2.38F, 2.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -1.4F, 1.0F, 0.22F, -0.22F, -0.35F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(93, 17).addBox(-1.02F, -0.1F, -0.85F, 2.04F, 1.8416F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.425F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0625F, -1.7F, -0.595F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5525F, -4.216F, -2.635F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3825F, -2.4242F, -0.8959F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(104, 17).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7F, -0.595F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.5803F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.304F, -3.315F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(127, 17).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 3.3414F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0498F, -1.1271F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(118, 17).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 3.4514F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0625F, -1.7F, -0.595F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5525F, -4.216F, -2.635F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3825F, -2.4242F, -0.8959F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(104, 17).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(51, 0).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 3.42F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.24F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(85, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(110, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 6.56F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(189, 0).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(200, 0).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(205, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.45F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.45F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(147, 0).addBox(-5.0F, -0.12F, -5.76F, 10.0F, 1.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.44F, 1.76F, 0.045F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(210, 0).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(221, 0).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(232, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -0.18F, -2.0F, 4.0F, 5.82F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(17, 17).addBox(2.7F, -0.05F, -0.75F, 0.6F, 2.9328F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition left_arm_exposed_tendon_r1 = left_arm_lower.addOrReplaceChild("left_arm_exposed_tendon_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.21F, -0.1F, -0.24F, 0.42F, 4.8411F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.2F, 0.0F, 0.0F, 0.0F, -0.0215F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(29, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(46, 17).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 3.42F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(63, 17).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.24F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition piglin_brute() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-3.0F, -0.18F, -2.0F, 4.0F, 5.82F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition shoulder_claw = right_arm.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(80, 17).addBox(-1.785F, -1.47F, -1.575F, 3.57F, 2.94F, 3.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -1.4F, 1.0F, 0.22F, -0.22F, -0.35F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(97, 17).addBox(-1.26F, -0.1F, -1.05F, 2.52F, 2.2279F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.525F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.3125F, -2.1F, -0.735F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(124, 17).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 2.5412F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6825F, -5.208F, -3.255F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(119, 17).addBox(-0.3938F, -0.1F, -0.4725F, 0.7875F, 3.2917F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4725F, -2.9946F, -1.1067F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(110, 17).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 3.4273F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.1F, -0.735F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(119, 17).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 3.1404F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.552F, -4.095F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(138, 17).addBox(-0.3937F, -0.1F, -0.4725F, 0.7875F, 4.0805F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.7674F, -1.3923F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(129, 17).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 4.2164F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.3125F, -2.1F, -0.735F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(124, 17).addBox(-0.126F, -0.1F, -0.1732F, 0.252F, 2.5412F, 0.3465F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6825F, -5.208F, -3.255F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(119, 17).addBox(-0.3937F, -0.1F, -0.4725F, 0.7875F, 3.2917F, 0.945F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4725F, -2.9946F, -1.1067F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(110, 17).addBox(-0.6563F, -0.1F, -0.735F, 1.3125F, 3.4273F, 1.47F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(51, 0).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 3.42F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.24F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(85, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(110, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 6.56F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(189, 0).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(200, 0).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(205, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.45F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.45F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(147, 0).addBox(-5.0F, -0.12F, -5.76F, 10.0F, 1.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.44F, 1.76F, 0.045F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(210, 0).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(221, 0).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(232, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -0.18F, -2.0F, 4.0F, 5.82F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(17, 17).addBox(2.7F, -0.05F, -0.75F, 0.6F, 2.9328F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition left_arm_exposed_tendon_r1 = left_arm_lower.addOrReplaceChild("left_arm_exposed_tendon_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.21F, -0.1F, -0.24F, 0.42F, 4.8411F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.2F, 0.0F, 0.0F, 0.0F, -0.0215F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(29, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(46, 17).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 3.42F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(63, 17).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.24F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition zombified_piglin() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-3.0F, -0.18F, -2.0F, 4.0F, 5.82F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition shoulder_claw = right_arm.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(80, 17).addBox(-1.445F, -1.19F, -1.275F, 2.89F, 2.38F, 2.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -1.4F, 1.0F, 0.22F, -0.22F, -0.35F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(93, 17).addBox(-1.02F, -0.1F, -0.85F, 2.04F, 1.8416F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.425F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0625F, -1.7F, -0.595F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5525F, -4.216F, -2.635F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3825F, -2.4242F, -0.8959F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(104, 17).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7F, -0.595F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.5803F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.304F, -3.315F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(127, 17).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 3.3414F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0498F, -1.1271F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(118, 17).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 3.4514F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0625F, -1.7F, -0.595F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5525F, -4.216F, -2.635F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(113, 17).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3825F, -2.4242F, -0.8959F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(104, 17).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(51, 0).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 3.42F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.24F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(85, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(110, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 6.56F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(189, 0).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(200, 0).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(205, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.45F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.45F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(184, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(147, 0).addBox(-5.0F, -0.12F, -5.76F, 10.0F, 1.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.44F, 1.76F, 0.045F, 0.0F, 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(210, 0).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(221, 0).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition jaw_claw = head.addOrReplaceChild("jaw_claw", CubeListBuilder.create().texOffs(132, 17).addBox(-0.765F, -0.63F, -0.675F, 1.53F, 1.26F, 1.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7F, -0.7F, 2.2F, 0.3F, 0.3F, -0.4F));

        PartDefinition jaw_claw_palm_r1 = jaw_claw.addOrReplaceChild("jaw_claw_palm_r1", CubeListBuilder.create().texOffs(141, 17).addBox(-0.54F, -0.1F, -0.45F, 1.08F, 1.0691F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.225F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition jaw_claw_finger_0 = jaw_claw.addOrReplaceChild("jaw_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5625F, -0.9F, -0.315F, 0.0F, -0.2F, -0.14F));

        PartDefinition jaw_claw_finger_0_sharp_tip_r1 = jaw_claw_finger_0.addOrReplaceChild("jaw_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.054F, -0.1F, -0.0743F, 0.108F, 1.2034F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2925F, -2.232F, -1.395F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition jaw_claw_finger_0_blade_r1 = jaw_claw_finger_0.addOrReplaceChild("jaw_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.525F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2025F, -1.2834F, -0.4743F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition jaw_claw_finger_0_proximal_r1 = jaw_claw_finger_0.addOrReplaceChild("jaw_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.5831F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition jaw_claw_finger_1 = jaw_claw.addOrReplaceChild("jaw_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.9F, -0.315F, 0.09F, 0.0F, 0.0F));

        PartDefinition jaw_claw_finger_1_sharp_tip_r1 = jaw_claw_finger_1.addOrReplaceChild("jaw_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.054F, -0.1F, -0.0743F, 0.108F, 1.4602F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.808F, -1.755F, -1.7955F, 0.0F, 0.0F));

        PartDefinition jaw_claw_finger_1_blade_r1 = jaw_claw_finger_1.addOrReplaceChild("jaw_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.8631F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6146F, -0.5967F, -2.3711F, 0.0F, 0.0F));

        PartDefinition jaw_claw_finger_1_proximal_r1 = jaw_claw_finger_1.addOrReplaceChild("jaw_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.9213F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition jaw_claw_finger_2 = jaw_claw.addOrReplaceChild("jaw_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5625F, -0.9F, -0.315F, 0.18F, 0.2F, 0.14F));

        PartDefinition jaw_claw_finger_2_sharp_tip_r1 = jaw_claw_finger_2.addOrReplaceChild("jaw_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.054F, -0.1F, -0.0743F, 0.108F, 1.2034F, 0.1485F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2925F, -2.232F, -1.395F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition jaw_claw_finger_2_blade_r1 = jaw_claw_finger_2.addOrReplaceChild("jaw_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.1688F, -0.1F, -0.2025F, 0.3375F, 1.525F, 0.405F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2025F, -1.2834F, -0.4743F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition jaw_claw_finger_2_proximal_r1 = jaw_claw_finger_2.addOrReplaceChild("jaw_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(148, 17).addBox(-0.2813F, -0.1F, -0.315F, 0.5625F, 1.5831F, 0.63F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(232, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -0.18F, -2.0F, 4.0F, 5.82F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(17, 17).addBox(2.7F, -0.05F, -0.75F, 0.6F, 2.9328F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition left_arm_exposed_tendon_r1 = left_arm_lower.addOrReplaceChild("left_arm_exposed_tendon_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.21F, -0.1F, -0.24F, 0.42F, 4.8411F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.2F, 0.0F, 0.0F, 0.0F, -0.0215F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(29, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.54F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(46, 17).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 3.42F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(63, 17).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.24F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition hoglin() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 4.92F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, -12.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.35F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1167F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1167F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.35F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(67, 0).addBox(-7.0F, -0.12F, -13.68F, 14.0F, 1.2F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.92F, -5.32F, 0.045F, 0.0F, 0.0F));

        PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(139, 0).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 2.0F, -12.0F));

        PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(148, 0).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 2.0F, -12.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(157, 0).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -2.0F, -3.0F, 0.0F, 0.0F, -0.6981F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(178, 0).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -2.0F, -3.0F, 0.0F, 0.0F, 0.6981F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(199, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, -8.5F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(224, 0).addBox(-2.28F, -0.18F, -2.28F, 4.56F, 3.76F, 4.56F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.42F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -0.18F, -3.0F, 6.0F, 3.18F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.58F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(25, 25).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.01F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 10.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(46, 25).addBox(-1.9F, -0.18F, -1.9F, 3.8F, 2.35F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.83F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(63, 25).addBox(-2.5F, -0.18F, -2.5F, 5.0F, 3.18F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.17F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(84, 25).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.01F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.0F, 10.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(105, 25).addBox(-1.9F, -0.18F, -1.9F, 3.8F, 2.35F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.83F, 0.0F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(122, 25).addBox(-2.5F, -0.18F, -2.5F, 5.0F, 3.18F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.17F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(143, 25).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition mane = body.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(0, 66).addBox(-0.001F, -0.001F, -9.001F, 0.002F, 10.002F, 19.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, -5.0F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(110, 66).addBox(-2.04F, -1.68F, -1.8F, 4.08F, 3.36F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.7F, 3.0F, 0.12F, 0.3F, -0.45F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(129, 66).addBox(-1.44F, -0.1F, -1.2F, 2.88F, 2.5176F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, -2.4F, -0.84F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 66).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 2.8757F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.78F, -5.952F, -3.72F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(151, 66).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 3.7333F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.54F, -3.4224F, -1.2648F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(142, 66).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 3.8884F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.4F, -0.84F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(179, 66).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 3.5605F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.488F, -4.68F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(172, 66).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 4.6349F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.3056F, -1.5912F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(163, 66).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 4.7902F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, -2.4F, -0.84F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 66).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 2.8757F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.78F, -5.952F, -3.72F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(151, 66).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 3.7333F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.54F, -3.4224F, -1.2648F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(142, 66).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 3.8884F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(39, 66).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.0F, -8.5F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(64, 66).addBox(-2.28F, -0.18F, -2.28F, 4.56F, 3.76F, 4.56F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.42F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(85, 66).addBox(-3.0F, -0.18F, -3.0F, 6.0F, 3.18F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.58F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition zoglin() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 4.92F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, -12.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.35F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1167F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1167F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(134, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.35F, 1.74F, -19.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(67, 0).addBox(-7.0F, -0.12F, -13.68F, 14.0F, 1.2F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.92F, -5.32F, 0.045F, 0.0F, 0.0F));

        PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(139, 0).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 2.0F, -12.0F));

        PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(148, 0).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 2.0F, -12.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(157, 0).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -2.0F, -3.0F, 0.0F, 0.0F, -0.6981F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(178, 0).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -2.0F, -3.0F, 0.0F, 0.0F, 0.6981F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(199, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, -8.5F));

        PartDefinition right_front_leg_lower = right_front_leg.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create().texOffs(224, 0).addBox(-2.28F, -0.18F, -2.28F, 4.56F, 3.76F, 4.56F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.42F, 0.0F));

        PartDefinition right_front_leg_foot = right_front_leg_lower.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -0.18F, -3.0F, 6.0F, 3.18F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.58F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(25, 25).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.01F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 10.0F));

        PartDefinition right_hind_leg_lower = right_hind_leg.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create().texOffs(46, 25).addBox(-1.9F, -0.18F, -1.9F, 3.8F, 2.35F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.83F, 0.0F));

        PartDefinition right_hind_leg_foot = right_hind_leg_lower.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create().texOffs(63, 25).addBox(-2.5F, -0.18F, -2.5F, 5.0F, 3.18F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.17F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(84, 25).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 6.01F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.0F, 10.0F));

        PartDefinition left_hind_leg_lower = left_hind_leg.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create().texOffs(105, 25).addBox(-1.9F, -0.18F, -1.9F, 3.8F, 2.35F, 3.8F, new CubeDeformation(0.0F))
        .texOffs(143, 25).addBox(2.2F, -0.05F, -0.75F, 0.6F, 2.6884F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.83F, 0.0F));

        PartDefinition left_hind_leg_exposed_tendon_r1 = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_exposed_tendon_r1", CubeListBuilder.create().texOffs(150, 25).addBox(-0.21F, -0.1F, -0.24F, 0.42F, 4.3712F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3F, 0.2F, 0.0F, 0.0F, 0.0F, -0.024F));

        PartDefinition left_hind_leg_foot = left_hind_leg_lower.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create().texOffs(122, 25).addBox(-2.5F, -0.18F, -2.5F, 5.0F, 3.18F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.17F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(155, 25).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition exposed_rib_3_r1 = body.addOrReplaceChild("exposed_rib_3_r1", CubeListBuilder.create().texOffs(184, 66).addBox(-0.35F, -0.1F, -0.4F, 0.7F, 7.2285F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9F, -4.0F, 3.8F, 0.0285F, 0.0012F, -0.0855F));

        PartDefinition exposed_rib_2_r1 = body.addOrReplaceChild("exposed_rib_2_r1", CubeListBuilder.create().texOffs(184, 66).addBox(-0.35F, -0.1F, -0.4F, 0.7F, 7.2285F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9F, -4.0F, 1.2F, 0.0285F, 0.0012F, -0.0855F));

        PartDefinition exposed_rib_1_r1 = body.addOrReplaceChild("exposed_rib_1_r1", CubeListBuilder.create().texOffs(184, 66).addBox(-0.35F, -0.1F, -0.4F, 0.7F, 7.2285F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9F, -4.0F, -1.4F, 0.0285F, 0.0012F, -0.0855F));

        PartDefinition exposed_rib_0_r1 = body.addOrReplaceChild("exposed_rib_0_r1", CubeListBuilder.create().texOffs(184, 66).addBox(-0.35F, -0.1F, -0.4F, 0.7F, 7.2285F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.9F, -4.0F, -4.0F, 0.0285F, 0.0012F, -0.0855F));

        PartDefinition mane = body.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(0, 66).addBox(-0.001F, -0.001F, -9.001F, 0.002F, 10.002F, 19.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, -5.0F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(110, 66).addBox(-2.04F, -1.68F, -1.8F, 4.08F, 3.36F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.7F, 3.0F, 0.12F, 0.3F, -0.45F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(129, 66).addBox(-1.44F, -0.1F, -1.2F, 2.88F, 2.5176F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, -2.4F, -0.84F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 66).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 2.8757F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.78F, -5.952F, -3.72F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(151, 66).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 3.7333F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.54F, -3.4224F, -1.2648F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(142, 66).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 3.8884F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.4F, -0.84F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(179, 66).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 3.5605F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.488F, -4.68F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(172, 66).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 4.6349F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.3056F, -1.5912F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(163, 66).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 4.7902F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, -2.4F, -0.84F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(158, 66).addBox(-0.144F, -0.1F, -0.198F, 0.288F, 2.8757F, 0.396F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.78F, -5.952F, -3.72F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(151, 66).addBox(-0.45F, -0.1F, -0.54F, 0.9F, 3.7333F, 1.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.54F, -3.4224F, -1.2648F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(142, 66).addBox(-0.75F, -0.1F, -0.84F, 1.5F, 3.8884F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(39, 66).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.0F, -8.5F));

        PartDefinition left_front_leg_lower = left_front_leg.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create().texOffs(64, 66).addBox(-2.28F, -0.18F, -2.28F, 4.56F, 3.76F, 4.56F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.42F, 0.0F));

        PartDefinition left_front_leg_foot = left_front_leg_lower.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create().texOffs(85, 66).addBox(-3.0F, -0.18F, -3.0F, 6.0F, 3.18F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.58F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition ghast() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(63, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(200, 40).addBox(-6.0F, 3.4F, -7.8F, 12.0F, 0.65F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.6F, 0.0F));

        PartDefinition upper_fang_6_r1 = body.addOrReplaceChild("upper_fang_6_r1", CubeListBuilder.create().texOffs(35, 62).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 2.65F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4F, 3.7F, -7.425F, 0.0F, 0.0F, -0.1047F));

        PartDefinition upper_fang_5_r1 = body.addOrReplaceChild("upper_fang_5_r1", CubeListBuilder.create().texOffs(25, 62).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 1.85F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, 3.7F, -7.425F, 0.0F, 0.0F, 0.1047F));

        PartDefinition upper_fang_4_r1 = body.addOrReplaceChild("upper_fang_4_r1", CubeListBuilder.create().texOffs(15, 62).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 2.65F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 3.7F, -7.425F, 0.0F, 0.0F, -0.1047F));

        PartDefinition upper_fang_3_r1 = body.addOrReplaceChild("upper_fang_3_r1", CubeListBuilder.create().texOffs(5, 62).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 1.85F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.7F, -7.425F, 0.0F, 0.0F, 0.1047F));

        PartDefinition upper_fang_2_r1 = body.addOrReplaceChild("upper_fang_2_r1", CubeListBuilder.create().texOffs(249, 40).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 2.65F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, 3.7F, -7.425F, 0.0F, 0.0F, -0.1047F));

        PartDefinition upper_fang_1_r1 = body.addOrReplaceChild("upper_fang_1_r1", CubeListBuilder.create().texOffs(239, 40).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 1.85F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, 3.7F, -7.425F, 0.0F, 0.0F, 0.1047F));

        PartDefinition upper_fang_0_r1 = body.addOrReplaceChild("upper_fang_0_r1", CubeListBuilder.create().texOffs(229, 40).addBox(-0.32F, -0.1F, -0.375F, 0.64F, 2.65F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4F, 3.7F, -7.425F, 0.0F, 0.0F, -0.1047F));

        PartDefinition crown_claw = body.addOrReplaceChild("crown_claw", CubeListBuilder.create().texOffs(164, 0).addBox(-2.754F, -2.268F, -2.43F, 5.508F, 4.536F, 4.86F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -7.3F, 3.8F, 0.1F, -0.4F, 0.45F));

        PartDefinition crown_claw_palm_r1 = crown_claw.addOrReplaceChild("crown_claw_palm_r1", CubeListBuilder.create().texOffs(185, 0).addBox(-1.944F, -0.12F, -1.62F, 3.888F, 3.3687F, 3.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.81F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_0 = crown_claw.addOrReplaceChild("crown_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.025F, -3.24F, -1.134F, 0.0F, -0.2F, -0.14F));

        PartDefinition crown_claw_finger_0_sharp_tip_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(209, 0).addBox(-0.1944F, -0.12F, -0.2673F, 0.3888F, 3.8522F, 0.5346F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.053F, -8.0352F, -5.022F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition crown_claw_finger_0_blade_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(200, 0).addBox(-0.6075F, -0.12F, -0.729F, 1.215F, 5.01F, 1.458F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.729F, -4.6202F, -1.7075F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition crown_claw_finger_0_proximal_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(200, 0).addBox(-1.0125F, -0.12F, -1.134F, 2.025F, 5.2193F, 2.268F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition crown_claw_finger_1 = crown_claw.addOrReplaceChild("crown_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.24F, -1.134F, 0.09F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_sharp_tip_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(209, 0).addBox(-0.1944F, -0.12F, -0.2673F, 0.3888F, 4.7767F, 0.5346F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.1088F, -6.318F, -1.7955F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_blade_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(214, 0).addBox(-0.6075F, -0.12F, -0.729F, 1.215F, 6.2271F, 1.458F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.8126F, -2.1481F, -2.3711F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_proximal_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(214, 0).addBox(-1.0125F, -0.12F, -1.134F, 2.025F, 6.4368F, 2.268F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_2 = crown_claw.addOrReplaceChild("crown_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.025F, -3.24F, -1.134F, 0.18F, 0.2F, 0.14F));

        PartDefinition crown_claw_finger_2_sharp_tip_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(209, 0).addBox(-0.1944F, -0.12F, -0.2673F, 0.3888F, 3.8522F, 0.5346F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.053F, -8.0352F, -5.022F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition crown_claw_finger_2_blade_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(200, 0).addBox(-0.6075F, -0.12F, -0.729F, 1.215F, 5.01F, 1.458F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.729F, -4.6202F, -1.7075F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition crown_claw_finger_2_proximal_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(200, 0).addBox(-1.0125F, -0.12F, -1.134F, 2.025F, 5.2193F, 2.268F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition ghast_jaw = body.addOrReplaceChild("ghast_jaw", CubeListBuilder.create().texOffs(40, 40).addBox(-6.0F, -0.15F, -12.7F, 12.0F, 4.15F, 15.5F, new CubeDeformation(0.0F))
        .texOffs(171, 40).addBox(-5.9F, -0.4F, -12.4F, 11.8F, 0.6F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 5.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition lower_fang_6_r1 = ghast_jaw.addOrReplaceChild("lower_fang_6_r1", CubeListBuilder.create().texOffs(40, 62).addBox(-0.28F, -1.9F, -0.4F, 0.56F, 2.0F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4F, -0.35F, -12.05F, 0.0F, 0.0F, 0.1571F));

        PartDefinition lower_fang_5_r1 = ghast_jaw.addOrReplaceChild("lower_fang_5_r1", CubeListBuilder.create().texOffs(30, 62).addBox(-0.28F, -1.26F, -0.4F, 0.56F, 1.36F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -0.35F, -12.05F, 0.0F, 0.0F, -0.1571F));

        PartDefinition lower_fang_4_r1 = ghast_jaw.addOrReplaceChild("lower_fang_4_r1", CubeListBuilder.create().texOffs(20, 62).addBox(-0.28F, -1.9F, -0.4F, 0.56F, 2.0F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, -0.35F, -12.05F, 0.0F, 0.0F, 0.1571F));

        PartDefinition lower_fang_3_r1 = ghast_jaw.addOrReplaceChild("lower_fang_3_r1", CubeListBuilder.create().texOffs(10, 62).addBox(-0.28F, -1.26F, -0.4F, 0.56F, 1.36F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.35F, -12.05F, 0.0F, 0.0F, -0.1571F));

        PartDefinition lower_fang_2_r1 = ghast_jaw.addOrReplaceChild("lower_fang_2_r1", CubeListBuilder.create().texOffs(0, 62).addBox(-0.28F, -1.9F, -0.4F, 0.56F, 2.0F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, -0.35F, -12.05F, 0.0F, 0.0F, 0.1571F));

        PartDefinition lower_fang_1_r1 = ghast_jaw.addOrReplaceChild("lower_fang_1_r1", CubeListBuilder.create().texOffs(244, 40).addBox(-0.28F, -1.26F, -0.4F, 0.56F, 1.36F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, -0.35F, -12.05F, 0.0F, 0.0F, -0.1571F));

        PartDefinition lower_fang_0_r1 = ghast_jaw.addOrReplaceChild("lower_fang_0_r1", CubeListBuilder.create().texOffs(234, 40).addBox(-0.28F, -1.9F, -0.4F, 0.56F, 2.0F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4F, -0.35F, -12.05F, 0.0F, 0.0F, 0.1571F));

        PartDefinition jaw_right_cheek_r1 = ghast_jaw.addOrReplaceChild("jaw_right_cheek_r1", CubeListBuilder.create().texOffs(134, 40).addBox(-8.0F, -0.2F, -12.2F, 2.2F, 3.9F, 14.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        PartDefinition jaw_left_cheek_r1 = ghast_jaw.addOrReplaceChild("jaw_left_cheek_r1", CubeListBuilder.create().texOffs(97, 40).addBox(5.8F, -0.2F, -11.8F, 2.2F, 3.9F, 14.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition tentacle0 = ghast_jaw.addOrReplaceChild("tentacle0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.02F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.75F, 3.0F, -10.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle0_lower = tentacle0.addOrReplaceChild("tentacle0_lower", CubeListBuilder.create().texOffs(9, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 4.34F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.84F, 0.0F));

        PartDefinition tendril_claw_0 = tentacle0_lower.addOrReplaceChild("tendril_claw_0", CubeListBuilder.create().texOffs(223, 0).addBox(-0.595F, -0.49F, -0.525F, 1.19F, 0.98F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 2.9845F, 0.0F, 0.12F));

        PartDefinition tendril_claw_0_palm_r1 = tendril_claw_0.addOrReplaceChild("tendril_claw_0_palm_r1", CubeListBuilder.create().texOffs(232, 0).addBox(-0.42F, -0.1F, -0.35F, 0.84F, 0.876F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.175F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tendril_claw_0_finger_0 = tendril_claw_0.addOrReplaceChild("tendril_claw_0_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4375F, -0.7F, -0.245F, 0.0F, -0.2F, -0.14F));

        PartDefinition tendril_claw_0_finger_0_sharp_tip_r1 = tendril_claw_0_finger_0.addOrReplaceChild("tendril_claw_0_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 0.9804F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2275F, -1.736F, -1.085F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tendril_claw_0_finger_0_blade_r1 = tendril_claw_0_finger_0.addOrReplaceChild("tendril_claw_0_finger_0_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.2306F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1575F, -0.9982F, -0.3689F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tendril_claw_0_finger_0_proximal_r1 = tendril_claw_0_finger_0.addOrReplaceChild("tendril_claw_0_finger_0_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.2758F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tendril_claw_0_finger_1 = tendril_claw_0.addOrReplaceChild("tendril_claw_0_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.7F, -0.245F, 0.09F, 0.0F, 0.0F));

        PartDefinition tendril_claw_0_finger_1_sharp_tip_r1 = tendril_claw_0_finger_1.addOrReplaceChild("tendril_claw_0_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 1.1801F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.184F, -1.365F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tendril_claw_0_finger_1_blade_r1 = tendril_claw_0_finger_1.addOrReplaceChild("tendril_claw_0_finger_1_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.4935F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2558F, -0.4641F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tendril_claw_0_finger_1_proximal_r1 = tendril_claw_0_finger_1.addOrReplaceChild("tendril_claw_0_finger_1_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.5388F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tendril_claw_0_finger_2 = tendril_claw_0.addOrReplaceChild("tendril_claw_0_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.4375F, -0.7F, -0.245F, 0.18F, 0.2F, 0.14F));

        PartDefinition tendril_claw_0_finger_2_sharp_tip_r1 = tendril_claw_0_finger_2.addOrReplaceChild("tendril_claw_0_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 0.9804F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2275F, -1.736F, -1.085F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tendril_claw_0_finger_2_blade_r1 = tendril_claw_0_finger_2.addOrReplaceChild("tendril_claw_0_finger_2_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.2306F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1575F, -0.9982F, -0.3689F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tendril_claw_0_finger_2_proximal_r1 = tendril_claw_0_finger_2.addOrReplaceChild("tendril_claw_0_finger_2_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.2758F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tentacle1 = ghast_jaw.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.42F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 3.0F, -10.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle1_lower = tentacle1.addOrReplaceChild("tentacle1_lower", CubeListBuilder.create().texOffs(27, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 6.94F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.24F, 0.0F));

        PartDefinition tentacle2 = ghast_jaw.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(45, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.25F, 3.0F, -10.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle2_lower = tentacle2.addOrReplaceChild("tentacle2_lower", CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 4.86F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.32F, 0.0F));

        PartDefinition tentacle3 = ghast_jaw.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(128, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.46F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.25F, 3.0F, -5.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle3_lower = tentacle3.addOrReplaceChild("tentacle3_lower", CubeListBuilder.create().texOffs(137, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 5.9F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.28F, 0.0F));

        PartDefinition tendril_claw_3 = tentacle3_lower.addOrReplaceChild("tendril_claw_3", CubeListBuilder.create().texOffs(223, 0).addBox(-0.595F, -0.49F, -0.525F, 1.19F, 0.98F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 2.9845F, 0.6F, 0.12F));

        PartDefinition tendril_claw_3_palm_r1 = tendril_claw_3.addOrReplaceChild("tendril_claw_3_palm_r1", CubeListBuilder.create().texOffs(232, 0).addBox(-0.42F, -0.1F, -0.35F, 0.84F, 0.876F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.175F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tendril_claw_3_finger_0 = tendril_claw_3.addOrReplaceChild("tendril_claw_3_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4375F, -0.7F, -0.245F, 0.0F, -0.2F, -0.14F));

        PartDefinition tendril_claw_3_finger_0_sharp_tip_r1 = tendril_claw_3_finger_0.addOrReplaceChild("tendril_claw_3_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 0.9804F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2275F, -1.736F, -1.085F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tendril_claw_3_finger_0_blade_r1 = tendril_claw_3_finger_0.addOrReplaceChild("tendril_claw_3_finger_0_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.2306F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1575F, -0.9982F, -0.3689F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tendril_claw_3_finger_0_proximal_r1 = tendril_claw_3_finger_0.addOrReplaceChild("tendril_claw_3_finger_0_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.2758F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tendril_claw_3_finger_1 = tendril_claw_3.addOrReplaceChild("tendril_claw_3_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.7F, -0.245F, 0.09F, 0.0F, 0.0F));

        PartDefinition tendril_claw_3_finger_1_sharp_tip_r1 = tendril_claw_3_finger_1.addOrReplaceChild("tendril_claw_3_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 1.1801F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.184F, -1.365F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tendril_claw_3_finger_1_blade_r1 = tendril_claw_3_finger_1.addOrReplaceChild("tendril_claw_3_finger_1_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1312F, -0.1F, -0.1575F, 0.2625F, 1.4935F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2558F, -0.4641F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tendril_claw_3_finger_1_proximal_r1 = tendril_claw_3_finger_1.addOrReplaceChild("tendril_claw_3_finger_1_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.5388F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tendril_claw_3_finger_2 = tendril_claw_3.addOrReplaceChild("tendril_claw_3_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.4375F, -0.7F, -0.245F, 0.18F, 0.2F, 0.14F));

        PartDefinition tendril_claw_3_finger_2_sharp_tip_r1 = tendril_claw_3_finger_2.addOrReplaceChild("tendril_claw_3_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 0.9804F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2275F, -1.736F, -1.085F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tendril_claw_3_finger_2_blade_r1 = tendril_claw_3_finger_2.addOrReplaceChild("tendril_claw_3_finger_2_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1312F, -0.1F, -0.1575F, 0.2625F, 1.2306F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1575F, -0.9982F, -0.3689F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tendril_claw_3_finger_2_proximal_r1 = tendril_claw_3_finger_2.addOrReplaceChild("tendril_claw_3_finger_2_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.2758F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tentacle4 = ghast_jaw.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(128, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.46F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, 3.0F, -5.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle4_lower = tentacle4.addOrReplaceChild("tentacle4_lower", CubeListBuilder.create().texOffs(137, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 5.9F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.28F, 0.0F));

        PartDefinition tentacle5 = ghast_jaw.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(146, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.98F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.75F, 3.0F, -5.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle5_lower = tentacle5.addOrReplaceChild("tentacle5_lower", CubeListBuilder.create().texOffs(155, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 5.38F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.8F, 0.0F));

        PartDefinition tentacle6 = ghast_jaw.addOrReplaceChild("tentacle6", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.94F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.75F, 3.0F, 0.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle6_lower = tentacle6.addOrReplaceChild("tentacle6_lower", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 6.42F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.76F, 0.0F));

        PartDefinition tendril_claw_6 = tentacle6_lower.addOrReplaceChild("tendril_claw_6", CubeListBuilder.create().texOffs(223, 0).addBox(-0.595F, -0.49F, -0.525F, 1.19F, 0.98F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 2.9845F, 1.2F, 0.12F));

        PartDefinition tendril_claw_6_palm_r1 = tendril_claw_6.addOrReplaceChild("tendril_claw_6_palm_r1", CubeListBuilder.create().texOffs(232, 0).addBox(-0.42F, -0.1F, -0.35F, 0.84F, 0.876F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.175F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tendril_claw_6_finger_0 = tendril_claw_6.addOrReplaceChild("tendril_claw_6_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4375F, -0.7F, -0.245F, 0.0F, -0.2F, -0.14F));

        PartDefinition tendril_claw_6_finger_0_sharp_tip_r1 = tendril_claw_6_finger_0.addOrReplaceChild("tendril_claw_6_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 0.9804F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2275F, -1.736F, -1.085F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tendril_claw_6_finger_0_blade_r1 = tendril_claw_6_finger_0.addOrReplaceChild("tendril_claw_6_finger_0_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.2306F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1575F, -0.9982F, -0.3689F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tendril_claw_6_finger_0_proximal_r1 = tendril_claw_6_finger_0.addOrReplaceChild("tendril_claw_6_finger_0_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.2758F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tendril_claw_6_finger_1 = tendril_claw_6.addOrReplaceChild("tendril_claw_6_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.7F, -0.245F, 0.09F, 0.0F, 0.0F));

        PartDefinition tendril_claw_6_finger_1_sharp_tip_r1 = tendril_claw_6_finger_1.addOrReplaceChild("tendril_claw_6_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 1.1801F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.184F, -1.365F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tendril_claw_6_finger_1_blade_r1 = tendril_claw_6_finger_1.addOrReplaceChild("tendril_claw_6_finger_1_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.4935F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2558F, -0.4641F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tendril_claw_6_finger_1_proximal_r1 = tendril_claw_6_finger_1.addOrReplaceChild("tendril_claw_6_finger_1_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.5388F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tendril_claw_6_finger_2 = tendril_claw_6.addOrReplaceChild("tendril_claw_6_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.4375F, -0.7F, -0.245F, 0.18F, 0.2F, 0.14F));

        PartDefinition tendril_claw_6_finger_2_sharp_tip_r1 = tendril_claw_6_finger_2.addOrReplaceChild("tendril_claw_6_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(242, 0).addBox(-0.042F, -0.1F, -0.0577F, 0.084F, 0.9804F, 0.1155F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2275F, -1.736F, -1.085F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tendril_claw_6_finger_2_blade_r1 = tendril_claw_6_finger_2.addOrReplaceChild("tendril_claw_6_finger_2_blade_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.1313F, -0.1F, -0.1575F, 0.2625F, 1.2306F, 0.315F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1575F, -0.9982F, -0.3689F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tendril_claw_6_finger_2_proximal_r1 = tendril_claw_6_finger_2.addOrReplaceChild("tendril_claw_6_finger_2_proximal_r1", CubeListBuilder.create().texOffs(237, 0).addBox(-0.2188F, -0.1F, -0.245F, 0.4375F, 1.2758F, 0.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tentacle7 = ghast_jaw.addOrReplaceChild("tentacle7", CubeListBuilder.create().texOffs(45, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 3.0F, 0.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle7_lower = tentacle7.addOrReplaceChild("tentacle7_lower", CubeListBuilder.create().texOffs(54, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 4.86F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.32F, 0.0F));

        PartDefinition tentacle8 = ghast_jaw.addOrReplaceChild("tentacle8", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.94F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.25F, 3.0F, 0.0F, -1.309F, 0.0F, 0.0F));

        PartDefinition tentacle8_lower = tentacle8.addOrReplaceChild("tentacle8_lower", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 6.42F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.76F, 0.0F));

        PartDefinition tongue_limb = ghast_jaw.addOrReplaceChild("tongue_limb", CubeListBuilder.create().texOffs(0, 69).addBox(-2.0F, -1.35F, -2.0F, 4.0F, 2.7F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.55F, -1.1F, -1.1781F, 0.0F, 0.0F));

        PartDefinition tongue_upper_ridge_r1 = tongue_limb.addOrReplaceChild("tongue_upper_ridge_r1", CubeListBuilder.create().texOffs(30, 69).addBox(-0.325F, -7.5127F, -0.35F, 0.65F, 7.6327F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.85F, 0.65F, -0.5F, 1.7262F, -0.0316F, 0.0271F));

        PartDefinition tongue_upper_tendon_r1 = tongue_limb.addOrReplaceChild("tongue_upper_tendon_r1", CubeListBuilder.create().texOffs(17, 69).addBox(-1.25F, -8.2095F, -1.35F, 2.5F, 8.3295F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.7197F, 0.0F, 0.0F));

        PartDefinition tongue_limb_middle = tongue_limb.addOrReplaceChild("tongue_limb_middle", CubeListBuilder.create().texOffs(35, 69).addBox(-1.65F, -1.65F, -1.65F, 3.3F, 3.3F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.2F, -8.0F, -0.1047F, 0.0873F, 0.1222F));

        PartDefinition tongue_middle_tendon_r1 = tongue_limb_middle.addOrReplaceChild("tongue_middle_tendon_r1", CubeListBuilder.create().texOffs(52, 69).addBox(-1.025F, -7.5859F, -1.1F, 2.05F, 7.7059F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.9013F, 0.1326F, -0.0947F));

        PartDefinition tongue_limb_tip = tongue_limb_middle.addOrReplaceChild("tongue_limb_tip", CubeListBuilder.create().texOffs(65, 69).addBox(-1.3F, -1.3F, -1.3F, 2.6F, 2.6F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 2.5F, -7.0F, 0.1396F, -0.1222F, -0.1047F));

        PartDefinition tongue_lower_tendon_r1 = tongue_limb_tip.addOrReplaceChild("tongue_lower_tendon_r1", CubeListBuilder.create().texOffs(78, 69).addBox(-0.825F, -6.0369F, -0.95F, 1.65F, 6.1569F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.7527F, -0.0815F, 0.0679F));

        PartDefinition tongue_terminal_claw = tongue_limb_tip.addOrReplaceChild("tongue_terminal_claw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.4F, 1.1F, -5.8F, 0.2793F, 0.0F, 0.0873F));

        PartDefinition tongue_claw_palm_r1 = tongue_terminal_claw.addOrReplaceChild("tongue_claw_palm_r1", CubeListBuilder.create().texOffs(87, 69).addBox(-1.75F, -3.3888F, -1.6F, 3.5F, 3.5088F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, 1.4635F, 0.0F, 0.0F));

        PartDefinition tongue_claw_finger_0 = tongue_terminal_claw.addOrReplaceChild("tongue_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.4F, -0.3F, -2.8F, 0.0F, 0.2269F, -0.1222F));

        PartDefinition tongue_claw_finger_0_tip_r1 = tongue_claw_finger_0.addOrReplaceChild("tongue_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(120, 69).addBox(-0.14F, -2.8734F, -0.19F, 0.28F, 2.9934F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, 4.872F, -6.72F, 2.9137F, 0.3328F, -0.0384F));

        PartDefinition tongue_claw_finger_0_blade_r1 = tongue_claw_finger_0.addOrReplaceChild("tongue_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(113, 69).addBox(-0.475F, -3.6926F, -0.55F, 0.95F, 3.8126F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.352F, -4.2F, 2.3443F, -0.1683F, 0.071F));

        PartDefinition tongue_claw_finger_0_root_r1 = tongue_claw_finger_0.addOrReplaceChild("tongue_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(104, 69).addBox(-0.75F, -4.9596F, -0.85F, 1.5F, 5.0796F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.0629F, -0.1753F, 0.1051F));

        PartDefinition tongue_claw_finger_1 = tongue_terminal_claw.addOrReplaceChild("tongue_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.3F, -2.8F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tongue_claw_finger_1_tip_r1 = tongue_claw_finger_1.addOrReplaceChild("tongue_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(141, 69).addBox(-0.14F, -3.3957F, -0.19F, 0.28F, 3.5157F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.8F, -8.0F, 2.9262F, 0.0F, 0.0F));

        PartDefinition tongue_claw_finger_1_blade_r1 = tongue_claw_finger_1.addOrReplaceChild("tongue_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(134, 69).addBox(-0.475F, -4.3626F, -0.55F, 0.95F, 4.4826F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.8F, -5.0F, 2.3562F, 0.0F, 0.0F));

        PartDefinition tongue_claw_finger_1_root_r1 = tongue_claw_finger_1.addOrReplaceChild("tongue_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(125, 69).addBox(-0.75F, -5.8506F, -0.85F, 1.5F, 5.9706F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.0813F, 0.0F, 0.0F));

        PartDefinition tongue_claw_finger_2 = tongue_terminal_claw.addOrReplaceChild("tongue_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.4F, -0.3F, -2.8F, -0.1745F, -0.2269F, 0.1222F));

        PartDefinition tongue_claw_finger_2_tip_r1 = tongue_claw_finger_2.addOrReplaceChild("tongue_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(162, 69).addBox(-0.14F, -2.8734F, -0.19F, 0.28F, 2.9934F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 4.872F, -6.72F, 2.9137F, -0.3328F, 0.0384F));

        PartDefinition tongue_claw_finger_2_blade_r1 = tongue_claw_finger_2.addOrReplaceChild("tongue_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(155, 69).addBox(-0.475F, -3.6926F, -0.55F, 0.95F, 3.8126F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.352F, -4.2F, 2.3443F, 0.1683F, -0.071F));

        PartDefinition tongue_claw_finger_2_root_r1 = tongue_claw_finger_2.addOrReplaceChild("tongue_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(146, 69).addBox(-0.75F, -4.9596F, -0.85F, 1.5F, 5.0796F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.0629F, 0.1753F, -0.1051F));

        PartDefinition left_jaw_joint = body.addOrReplaceChild("left_jaw_joint", CubeListBuilder.create().texOffs(45, 62).addBox(-1.1F, -1.1F, -1.2F, 2.2F, 2.2F, 2.4F, new CubeDeformation(0.0F)), PartPose.offset(6.4F, 4.0F, 5.0F));

        PartDefinition right_jaw_joint = body.addOrReplaceChild("right_jaw_joint", CubeListBuilder.create().texOffs(58, 62).addBox(-1.1F, -1.1F, -1.2F, 2.2F, 2.2F, 2.4F, new CubeDeformation(0.0F)), PartPose.offset(-6.4F, 4.0F, 5.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition blaze() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition part0 = corruption_root.addOrReplaceChild("part0", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -13.0F, 0.0F));

        PartDefinition rod_barb_0_r1 = part0.addOrReplaceChild("rod_barb_0_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part1 = corruption_root.addOrReplaceChild("part1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.863F, -13.1224F, 7.5731F));

        PartDefinition rod_barb_1_r1 = part1.addOrReplaceChild("rod_barb_1_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part2 = corruption_root.addOrReplaceChild("part2", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.7447F, -13.4597F, 8.1839F));

        PartDefinition rod_barb_2_r1 = part2.addOrReplaceChild("rod_barb_2_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(9, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 6.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(75, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.35F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(75, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1167F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(75, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1167F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(75, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.35F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(42, 0).addBox(-4.0F, -0.12F, -5.76F, 8.0F, 1.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.56F, 1.76F, 0.045F, 0.0F, 0.0F));

        PartDefinition crown_claw = head.addOrReplaceChild("crown_claw", CubeListBuilder.create().texOffs(80, 0).addBox(-1.275F, -1.05F, -1.125F, 2.55F, 2.1F, 2.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, -2.0F, 2.5F, 0.1F, 0.2F, -0.35F));

        PartDefinition crown_claw_palm_r1 = crown_claw.addOrReplaceChild("crown_claw_palm_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.9F, -0.1F, -0.75F, 1.8F, 1.6485F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.375F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_0 = crown_claw.addOrReplaceChild("crown_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9375F, -1.5F, -0.525F, 0.0F, -0.2F, -0.14F));

        PartDefinition crown_claw_finger_0_sharp_tip_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(114, 0).addBox(-0.09F, -0.1F, -0.1238F, 0.18F, 1.8723F, 0.2475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4875F, -3.72F, -2.325F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition crown_claw_finger_0_blade_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.2813F, -0.1F, -0.3375F, 0.5625F, 2.4083F, 0.675F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3375F, -2.139F, -0.7905F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition crown_claw_finger_0_proximal_r1 = crown_claw_finger_0.addOrReplaceChild("crown_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(102, 0).addBox(-0.4688F, -0.1F, -0.525F, 0.9375F, 2.5052F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition crown_claw_finger_1 = crown_claw.addOrReplaceChild("crown_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.5F, -0.525F, 0.09F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_sharp_tip_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.09F, -0.1F, -0.1238F, 0.18F, 2.3003F, 0.2475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.68F, -2.925F, -1.7955F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_blade_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.2813F, -0.1F, -0.3375F, 0.5625F, 2.9718F, 0.675F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.691F, -0.9945F, -2.3711F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_1_proximal_r1 = crown_claw_finger_1.addOrReplaceChild("crown_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(119, 0).addBox(-0.4688F, -0.1F, -0.525F, 0.9375F, 3.0689F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition crown_claw_finger_2 = crown_claw.addOrReplaceChild("crown_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9375F, -1.5F, -0.525F, 0.18F, 0.2F, 0.14F));

        PartDefinition crown_claw_finger_2_sharp_tip_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(114, 0).addBox(-0.09F, -0.1F, -0.1238F, 0.18F, 1.8723F, 0.2475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4875F, -3.72F, -2.325F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition crown_claw_finger_2_blade_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.2813F, -0.1F, -0.3375F, 0.5625F, 2.4083F, 0.675F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3375F, -2.139F, -0.7905F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition crown_claw_finger_2_proximal_r1 = crown_claw_finger_2.addOrReplaceChild("crown_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(102, 0).addBox(-0.4688F, -0.1F, -0.525F, 0.9375F, 2.5052F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition part3 = corruption_root.addOrReplaceChild("part3", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.9099F, -13.9292F, 1.2702F));

        PartDefinition rod_barb_3_r1 = part3.addOrReplaceChild("rod_barb_3_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part11 = corruption_root.addOrReplaceChild("part11", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.7308F, -1.3857F, -1.6184F));

        PartDefinition rod_barb_11_r1 = part11.addOrReplaceChild("rod_barb_11_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part4 = corruption_root.addOrReplaceChild("part4", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.9497F, -10.4161F, 4.9497F));

        PartDefinition rod_barb_4_r1 = part4.addOrReplaceChild("rod_barb_4_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part10 = corruption_root.addOrReplaceChild("part10", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.9177F, -0.6533F, 3.1067F));

        PartDefinition rod_barb_10_r1 = part10.addOrReplaceChild("rod_barb_10_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part5 = corruption_root.addOrReplaceChild("part5", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.4905F, -10.8011F, 6.8395F));

        PartDefinition rod_barb_5_r1 = part5.addOrReplaceChild("rod_barb_5_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part6 = corruption_root.addOrReplaceChild("part6", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5604F, -10.99F, 2.4414F));

        PartDefinition rod_barb_6_r1 = part6.addOrReplaceChild("rod_barb_6_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part7 = corruption_root.addOrReplaceChild("part7", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.5988F, -10.9365F, -4.2016F));

        PartDefinition rod_barb_7_r1 = part7.addOrReplaceChild("rod_barb_7_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part8 = corruption_root.addOrReplaceChild("part8", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.4551F, -0.0398F, 2.2699F));

        PartDefinition rod_barb_8_r1 = part8.addOrReplaceChild("rod_barb_8_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition part9 = corruption_root.addOrReplaceChild("part9", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.4972F, -0.107F, 4.9752F));

        PartDefinition rod_barb_9_r1 = part9.addOrReplaceChild("rod_barb_9_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.35F, -0.1F, -0.375F, 0.7F, 2.9368F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 1.0F, 0.2056F, 0.4627F, -2.316F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition magma_cube() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition cube0 = corruption_root.addOrReplaceChild("cube0", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 16.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition core_claw = cube0.addOrReplaceChild("core_claw", CubeListBuilder.create().texOffs(50, 10).addBox(-0.714F, -0.588F, -0.63F, 1.428F, 1.176F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.1F, 2.2F, 0.3F, 0.3F, -0.28F));

        PartDefinition core_claw_palm_r1 = core_claw.addOrReplaceChild("core_claw_palm_r1", CubeListBuilder.create().texOffs(59, 10).addBox(-0.504F, -0.1F, -0.42F, 1.008F, 1.0112F, 0.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.21F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition core_claw_finger_0 = core_claw.addOrReplaceChild("core_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.525F, -0.84F, -0.294F, 0.0F, -0.2F, -0.14F));

        PartDefinition core_claw_finger_0_sharp_tip_r1 = core_claw_finger_0.addOrReplaceChild("core_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.0504F, -0.1F, -0.0693F, 0.1008F, 1.1365F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.273F, -2.0832F, -1.302F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition core_claw_finger_0_blade_r1 = core_claw_finger_0.addOrReplaceChild("core_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.1575F, -0.1F, -0.189F, 0.315F, 1.4367F, 0.378F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.189F, -1.1978F, -0.4427F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition core_claw_finger_0_proximal_r1 = core_claw_finger_0.addOrReplaceChild("core_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.2625F, -0.1F, -0.294F, 0.525F, 1.4909F, 0.588F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition core_claw_finger_1 = core_claw.addOrReplaceChild("core_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.84F, -0.294F, 0.09F, 0.0F, 0.0F));

        PartDefinition core_claw_finger_1_sharp_tip_r1 = core_claw_finger_1.addOrReplaceChild("core_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.0504F, -0.1F, -0.0693F, 0.1008F, 1.3762F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6208F, -1.638F, -1.7955F, 0.0F, 0.0F));

        PartDefinition core_claw_finger_1_blade_r1 = core_claw_finger_1.addOrReplaceChild("core_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.1575F, -0.1F, -0.189F, 0.315F, 1.7522F, 0.378F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.507F, -0.5569F, -2.3711F, 0.0F, 0.0F));

        PartDefinition core_claw_finger_1_proximal_r1 = core_claw_finger_1.addOrReplaceChild("core_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.2625F, -0.1F, -0.294F, 0.525F, 1.8066F, 0.588F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition core_claw_finger_2 = core_claw.addOrReplaceChild("core_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.525F, -0.84F, -0.294F, 0.18F, 0.2F, 0.14F));

        PartDefinition core_claw_finger_2_sharp_tip_r1 = core_claw_finger_2.addOrReplaceChild("core_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.0504F, -0.1F, -0.0693F, 0.1008F, 1.1365F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.273F, -2.0832F, -1.302F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition core_claw_finger_2_blade_r1 = core_claw_finger_2.addOrReplaceChild("core_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.1575F, -0.1F, -0.189F, 0.315F, 1.4367F, 0.378F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.189F, -1.1978F, -0.4427F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition core_claw_finger_2_proximal_r1 = core_claw_finger_2.addOrReplaceChild("core_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(66, 10).addBox(-0.2625F, -0.1F, -0.294F, 0.525F, 1.4909F, 0.588F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition cube2 = corruption_root.addOrReplaceChild("cube2", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, 18.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition cube1 = corruption_root.addOrReplaceChild("cube1", CubeListBuilder.create().texOffs(66, 0).addBox(-4.0F, 17.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition cube4 = corruption_root.addOrReplaceChild("cube4", CubeListBuilder.create().texOffs(99, 0).addBox(-4.0F, 20.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition cube3 = corruption_root.addOrReplaceChild("cube3", CubeListBuilder.create().texOffs(132, 0).addBox(-4.0F, 19.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition cube6 = corruption_root.addOrReplaceChild("cube6", CubeListBuilder.create().texOffs(165, 0).addBox(-4.0F, 22.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition cube5 = corruption_root.addOrReplaceChild("cube5", CubeListBuilder.create().texOffs(198, 0).addBox(-4.0F, 21.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition cube7 = corruption_root.addOrReplaceChild("cube7", CubeListBuilder.create().texOffs(0, 10).addBox(-4.0F, 23.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition inside_cube = corruption_root.addOrReplaceChild("inside_cube", CubeListBuilder.create().texOffs(33, 10).addBox(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition strider() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.66F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -4.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(17, 0).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 5.3F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.48F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(34, 0).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.12F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(51, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.66F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -4.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(68, 0).addBox(-1.52F, -0.18F, -1.52F, 3.04F, 5.3F, 3.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.48F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(85, 0).addBox(-2.0F, -0.18F, -2.0F, 4.0F, 2.58F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.12F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(102, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition mouth_fang_2_r1 = body.addOrReplaceChild("mouth_fang_2_r1", CubeListBuilder.create().texOffs(159, 31).addBox(-0.225F, -0.1F, -0.275F, 0.45F, 2.2591F, 0.55F, new CubeDeformation(0.0F))
        .texOffs(159, 31).addBox(-2.225F, -0.1F, -0.275F, 0.45F, 2.2591F, 0.55F, new CubeDeformation(0.0F))
        .texOffs(159, 31).addBox(-4.225F, -0.1F, -0.275F, 0.45F, 2.2591F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 6.0F, -8.0F, -0.5071F, 0.0F, 0.0F));

        PartDefinition right_top_bristle = body.addOrReplaceChild("right_top_bristle", CubeListBuilder.create().texOffs(167, 0).mirror().addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.8727F));

        PartDefinition right_bottom_bristle = body.addOrReplaceChild("right_bottom_bristle", CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217F));

        PartDefinition left_top_bristle = body.addOrReplaceChild("left_top_bristle", CubeListBuilder.create().texOffs(167, 0).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.8727F));

        PartDefinition left_bottom_bristle = body.addOrReplaceChild("left_bottom_bristle", CubeListBuilder.create().texOffs(0, 31).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217F));

        PartDefinition right_middle_bristle = body.addOrReplaceChild("right_middle_bristle", CubeListBuilder.create().texOffs(57, 31).mirror().addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.1345F));

        PartDefinition left_middle_bristle = body.addOrReplaceChild("left_middle_bristle", CubeListBuilder.create().texOffs(57, 31).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.1345F));

        PartDefinition back_claw = body.addOrReplaceChild("back_claw", CubeListBuilder.create().texOffs(114, 31).addBox(-1.615F, -1.33F, -1.425F, 3.23F, 2.66F, 2.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -5.0F, 5.6F, 0.18F, -0.32F, -0.3F));

        PartDefinition back_claw_palm_r1 = back_claw.addOrReplaceChild("back_claw_palm_r1", CubeListBuilder.create().texOffs(129, 31).addBox(-1.14F, -0.1F, -0.95F, 2.28F, 2.0348F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.475F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_0 = back_claw.addOrReplaceChild("back_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.1875F, -1.9F, -0.665F, 0.0F, -0.2F, -0.14F));

        PartDefinition back_claw_finger_0_sharp_tip_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(149, 31).addBox(-0.114F, -0.1F, -0.1567F, 0.228F, 2.3183F, 0.3135F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6175F, -4.712F, -2.945F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition back_claw_finger_0_blade_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(149, 31).addBox(-0.3563F, -0.1F, -0.4275F, 0.7125F, 2.9972F, 0.855F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4275F, -2.7094F, -1.0013F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition back_claw_finger_0_proximal_r1 = back_claw_finger_0.addOrReplaceChild("back_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(140, 31).addBox(-0.5938F, -0.1F, -0.665F, 1.1875F, 3.12F, 1.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition back_claw_finger_1 = back_claw.addOrReplaceChild("back_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.9F, -0.665F, 0.09F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_sharp_tip_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(149, 31).addBox(-0.114F, -0.1F, -0.1567F, 0.228F, 2.8604F, 0.3135F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.928F, -3.705F, -1.7955F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_blade_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(154, 31).addBox(-0.3563F, -0.1F, -0.4275F, 0.7125F, 3.711F, 0.855F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.4086F, -1.2597F, -2.3711F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_1_proximal_r1 = back_claw_finger_1.addOrReplaceChild("back_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(140, 31).addBox(-0.5938F, -0.1F, -0.665F, 1.1875F, 3.8339F, 1.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition back_claw_finger_2 = back_claw.addOrReplaceChild("back_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.1875F, -1.9F, -0.665F, 0.18F, 0.2F, 0.14F));

        PartDefinition back_claw_finger_2_sharp_tip_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(149, 31).addBox(-0.114F, -0.1F, -0.1567F, 0.228F, 2.3183F, 0.3135F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6175F, -4.712F, -2.945F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition back_claw_finger_2_blade_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(149, 31).addBox(-0.3563F, -0.1F, -0.4275F, 0.7125F, 2.9972F, 0.855F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4275F, -2.7094F, -1.0013F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition back_claw_finger_2_proximal_r1 = back_claw_finger_2.addOrReplaceChild("back_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(140, 31).addBox(-0.5938F, -0.1F, -0.665F, 1.1875F, 3.12F, 1.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition wither_skeleton() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1167F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1167F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.35F, -1.62F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -0.12F, -5.76F, 8.0F, 1.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.44F, 1.76F, 0.045F, 0.0F, 0.0F));

        PartDefinition skull_claw = head.addOrReplaceChild("skull_claw", CubeListBuilder.create().texOffs(195, 0).addBox(-0.884F, -0.728F, -0.78F, 1.768F, 1.456F, 1.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -5.8F, 2.2F, 0.1F, -0.3F, 0.5F));

        PartDefinition skull_claw_palm_r1 = skull_claw.addOrReplaceChild("skull_claw_palm_r1", CubeListBuilder.create().texOffs(195, 0).addBox(-0.624F, -0.1F, -0.52F, 1.248F, 1.2043F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.26F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_0 = skull_claw.addOrReplaceChild("skull_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.65F, -1.04F, -0.364F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_claw_finger_0_sharp_tip_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.3595F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.338F, -2.5792F, -1.612F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_claw_finger_0_blade_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 1.7311F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.234F, -1.483F, -0.5481F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_claw_finger_0_proximal_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 1.7983F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_claw_finger_1 = skull_claw.addOrReplaceChild("skull_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.04F, -0.364F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_sharp_tip_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.6562F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2448F, -2.028F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_blade_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(181, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 2.1218F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.8658F, -0.6895F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_proximal_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(181, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 2.1891F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_2 = skull_claw.addOrReplaceChild("skull_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, -1.04F, -0.364F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_claw_finger_2_sharp_tip_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.0624F, -0.1F, -0.0858F, 0.1248F, 1.3595F, 0.1716F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.338F, -2.5792F, -1.612F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_claw_finger_2_blade_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.195F, -0.1F, -0.234F, 0.39F, 1.7311F, 0.468F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.234F, -1.483F, -0.5481F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_claw_finger_2_proximal_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(204, 0).addBox(-0.325F, -0.1F, -0.364F, 0.65F, 1.7983F, 0.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition right_arm = corruption_root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(71, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.54F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(80, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 5.82F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition left_leg = corruption_root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(89, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.54F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition left_leg_lower = left_leg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(98, 0).mirror().addBox(-0.76F, -0.18F, -0.76F, 1.52F, 4.62F, 1.52F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition left_leg_foot = left_leg_lower.addOrReplaceChild("left_leg_foot", CubeListBuilder.create().texOffs(107, 0).mirror().addBox(-1.0F, -0.18F, -1.0F, 2.0F, 1.38F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.44F, 0.0F));

        PartDefinition left_arm = corruption_root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(71, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.54F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(80, 0).mirror().addBox(-1.0F, -0.18F, -1.0F, 2.0F, 5.82F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.36F, 0.0F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 4.74F, 0.0F));

        PartDefinition right_leg = corruption_root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(89, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.54F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        PartDefinition right_leg_lower = right_leg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(98, 0).addBox(-0.76F, -0.18F, -0.76F, 1.52F, 4.62F, 1.52F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.36F, 0.0F));

        PartDefinition right_leg_foot = right_leg_lower.addOrReplaceChild("right_leg_foot", CubeListBuilder.create().texOffs(107, 0).addBox(-1.0F, -0.18F, -1.0F, 2.0F, 1.38F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.44F, 0.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(116, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition rib_claw = body.addOrReplaceChild("rib_claw", CubeListBuilder.create().texOffs(141, 0).addBox(-1.7F, -1.4F, -1.5F, 3.4F, 2.8F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 3.0F, 1.3F, 0.18F, 0.35F, -0.42F));

        PartDefinition rib_claw_palm_r1 = rib_claw.addOrReplaceChild("rib_claw_palm_r1", CubeListBuilder.create().texOffs(156, 0).addBox(-1.2F, -0.1F, -1.0F, 2.4F, 2.1313F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_0 = rib_claw.addOrReplaceChild("rib_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.25F, -2.0F, -0.7F, 0.0F, -0.2F, -0.14F));

        PartDefinition rib_claw_finger_0_sharp_tip_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(181, 0).addBox(-0.12F, -0.1F, -0.165F, 0.24F, 2.4298F, 0.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.65F, -4.96F, -3.1F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition rib_claw_finger_0_blade_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(176, 0).addBox(-0.375F, -0.1F, -0.45F, 0.75F, 3.1444F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.45F, -2.852F, -1.054F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition rib_claw_finger_0_proximal_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.625F, -0.1F, -0.7F, 1.25F, 3.2736F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition rib_claw_finger_1 = rib_claw.addOrReplaceChild("rib_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, -0.7F, 0.09F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_sharp_tip_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(181, 0).addBox(-0.12F, -0.1F, -0.165F, 0.24F, 3.0004F, 0.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.24F, -3.9F, -1.7955F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_blade_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(176, 0).addBox(-0.375F, -0.1F, -0.45F, 0.75F, 3.8958F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.588F, -1.326F, -2.3711F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_proximal_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(186, 0).addBox(-0.625F, -0.1F, -0.7F, 1.25F, 4.0252F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_2 = rib_claw.addOrReplaceChild("rib_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.25F, -2.0F, -0.7F, 0.18F, 0.2F, 0.14F));

        PartDefinition rib_claw_finger_2_sharp_tip_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(181, 0).addBox(-0.12F, -0.1F, -0.165F, 0.24F, 2.4298F, 0.33F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.65F, -4.96F, -3.1F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition rib_claw_finger_2_blade_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(176, 0).addBox(-0.375F, -0.1F, -0.45F, 0.75F, 3.1444F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.45F, -2.852F, -1.054F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition rib_claw_finger_2_proximal_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.625F, -0.1F, -0.7F, 1.25F, 3.2736F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition wither() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition shoulders = corruption_root.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition shoulder_claw = shoulders.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(0, 16).addBox(-1.87F, -1.54F, -1.65F, 3.74F, 3.08F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 4.0F, 1.0F, 0.2F, -0.2F, 0.35F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(17, 16).addBox(-1.32F, -0.1F, -1.1F, 2.64F, 2.3245F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.55F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.375F, -2.2F, -0.77F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.715F, -5.456F, -3.41F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(39, 16).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.495F, -3.1372F, -1.1594F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(30, 16).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.2F, -0.77F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(39, 16).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 3.2804F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.864F, -4.29F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(58, 16).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 4.2653F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9468F, -1.4586F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(49, 16).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 4.4077F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.375F, -2.2F, -0.77F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.132F, -0.1F, -0.1815F, 0.264F, 2.6527F, 0.363F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.715F, -5.456F, -3.41F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(39, 16).addBox(-0.4125F, -0.1F, -0.495F, 0.825F, 3.4389F, 0.99F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.495F, -3.1372F, -1.1594F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(30, 16).addBox(-0.6875F, -0.1F, -0.77F, 1.375F, 3.581F, 1.54F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition ribcage = corruption_root.addOrReplaceChild("ribcage", CubeListBuilder.create().texOffs(47, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(60, 0).addBox(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(60, 0).addBox(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(60, 0).addBox(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -5.1F, -0.5F, 0.2042F, 0.0F, 0.0F));

        PartDefinition rib_claw = ribcage.addOrReplaceChild("rib_claw", CubeListBuilder.create().texOffs(63, 16).addBox(-1.53F, -1.26F, -1.35F, 3.06F, 2.52F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 4.0F, 1.0F, 0.2F, 0.4F, -0.35F));

        PartDefinition rib_claw_palm_r1 = rib_claw.addOrReplaceChild("rib_claw_palm_r1", CubeListBuilder.create().texOffs(78, 16).addBox(-1.08F, -0.1F, -0.9F, 2.16F, 1.9382F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.45F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_0 = rib_claw.addOrReplaceChild("rib_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.125F, -1.8F, -0.63F, 0.0F, -0.2F, -0.14F));

        PartDefinition rib_claw_finger_0_sharp_tip_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.585F, -4.464F, -2.79F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition rib_claw_finger_0_blade_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.405F, -2.5668F, -0.9486F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition rib_claw_finger_0_proximal_r1 = rib_claw_finger_0.addOrReplaceChild("rib_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(89, 16).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition rib_claw_finger_1 = rib_claw.addOrReplaceChild("rib_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.8F, -0.63F, 0.09F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_sharp_tip_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.7204F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.616F, -3.51F, -1.7955F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_blade_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(39, 16).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 3.5262F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2292F, -1.1934F, -2.3711F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_1_proximal_r1 = rib_claw_finger_1.addOrReplaceChild("rib_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(30, 16).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 3.6427F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition rib_claw_finger_2 = rib_claw.addOrReplaceChild("rib_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.125F, -1.8F, -0.63F, 0.18F, 0.2F, 0.14F));

        PartDefinition rib_claw_finger_2_sharp_tip_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.108F, -0.1F, -0.1485F, 0.216F, 2.2068F, 0.297F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.585F, -4.464F, -2.79F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition rib_claw_finger_2_blade_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-0.3375F, -0.1F, -0.405F, 0.675F, 2.85F, 0.81F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.405F, -2.5668F, -0.9486F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition rib_claw_finger_2_proximal_r1 = rib_claw_finger_2.addOrReplaceChild("rib_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(89, 16).addBox(-0.5625F, -0.1F, -0.63F, 1.125F, 2.9663F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail = corruption_root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(87, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.6924F, 1.527F, 0.8325F, 0.0F, 0.0F));

        PartDefinition left_head = corruption_root.addOrReplaceChild("left_head", CubeListBuilder.create().texOffs(100, 0).addBox(-4.0F, -4.0F, -4.0F, 6.0F, 4.92F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, -8.0F, 0.0F));

        PartDefinition left_head_upper_fang_3_r1 = left_head.addOrReplaceChild("left_head_upper_fang_3_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition left_head_upper_fang_2_r1 = left_head.addOrReplaceChild("left_head_upper_fang_2_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2167F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition left_head_upper_fang_1_r1 = left_head.addOrReplaceChild("left_head_upper_fang_1_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7833F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition left_head_upper_fang_0_r1 = left_head.addOrReplaceChild("left_head_upper_fang_0_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.35F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition left_head_jaw = left_head.addOrReplaceChild("left_head_jaw", CubeListBuilder.create().texOffs(125, 0).addBox(-4.0F, -0.12F, -4.32F, 6.0F, 1.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.92F, 0.32F, 0.045F, 0.0F, 0.0F));

        PartDefinition right_head = corruption_root.addOrReplaceChild("right_head", CubeListBuilder.create().texOffs(155, 0).addBox(-4.0F, -4.0F, -4.0F, 6.0F, 4.92F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -8.0F, 0.0F));

        PartDefinition right_head_upper_fang_3_r1 = right_head.addOrReplaceChild("right_head_upper_fang_3_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition right_head_upper_fang_2_r1 = right_head.addOrReplaceChild("right_head_upper_fang_2_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2167F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition right_head_upper_fang_1_r1 = right_head.addOrReplaceChild("right_head_upper_fang_1_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7833F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition right_head_upper_fang_0_r1 = right_head.addOrReplaceChild("right_head_upper_fang_0_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.35F, 0.74F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition right_head_jaw = right_head.addOrReplaceChild("right_head_jaw", CubeListBuilder.create().texOffs(125, 0).addBox(-4.0F, -0.12F, -4.32F, 6.0F, 1.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.92F, 0.32F, 0.045F, 0.0F, 0.0F));

        PartDefinition center_head = corruption_root.addOrReplaceChild("center_head", CubeListBuilder.create().texOffs(180, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 6.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition center_head_upper_fang_3_r1 = center_head.addOrReplaceChild("center_head_upper_fang_3_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.35F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition center_head_upper_fang_2_r1 = center_head.addOrReplaceChild("center_head_upper_fang_2_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1167F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition center_head_upper_fang_1_r1 = center_head.addOrReplaceChild("center_head_upper_fang_1_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1167F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition center_head_upper_fang_0_r1 = center_head.addOrReplaceChild("center_head_upper_fang_0_r1", CubeListBuilder.create().texOffs(150, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.35F, 2.38F, -4.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition center_head_jaw = center_head.addOrReplaceChild("center_head_jaw", CubeListBuilder.create().texOffs(213, 0).addBox(-4.0F, -0.12F, -5.76F, 8.0F, 1.56F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.56F, 1.76F, 0.045F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition endermite() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition segment2 = corruption_root.addOrReplaceChild("segment2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 3.0F));

        PartDefinition segment1 = corruption_root.addOrReplaceChild("segment1", CubeListBuilder.create().texOffs(9, 0).addBox(-3.0F, 0.0F, -2.5F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition insect_hook_1_2_r1 = segment1.addOrReplaceChild("insect_hook_1_2_r1", CubeListBuilder.create().texOffs(65, 0).addBox(-0.15F, -0.1F, -0.19F, 0.3F, 2.3424F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 2.0F, 1.7F, -0.1407F, -0.0577F, -0.7772F));

        PartDefinition insect_hook_1_1_r1 = segment1.addOrReplaceChild("insect_hook_1_1_r1", CubeListBuilder.create().texOffs(65, 0).addBox(-0.15F, -0.1F, -0.19F, 0.3F, 2.3424F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 2.0F, 0.0F, -0.1407F, -0.0577F, -0.7772F));

        PartDefinition insect_hook_1_0_r1 = segment1.addOrReplaceChild("insect_hook_1_0_r1", CubeListBuilder.create().texOffs(65, 0).addBox(-0.15F, -0.1F, -0.19F, 0.3F, 2.3424F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 2.0F, -1.7F, -0.1407F, -0.0577F, -0.7772F));

        PartDefinition insect_hook_m1_2_r1 = segment1.addOrReplaceChild("insect_hook_m1_2_r1", CubeListBuilder.create().texOffs(65, 0).addBox(-0.15F, -0.1F, -0.19F, 0.3F, 2.3424F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, 2.0F, 1.7F, -0.1407F, 0.0577F, 0.7772F));

        PartDefinition insect_hook_m1_1_r1 = segment1.addOrReplaceChild("insect_hook_m1_1_r1", CubeListBuilder.create().texOffs(65, 0).addBox(-0.15F, -0.1F, -0.19F, 0.3F, 2.3424F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, 2.0F, 0.0F, -0.1407F, 0.0577F, 0.7772F));

        PartDefinition insect_hook_m1_0_r1 = segment1.addOrReplaceChild("insect_hook_m1_0_r1", CubeListBuilder.create().texOffs(65, 0).addBox(-0.15F, -0.1F, -0.19F, 0.3F, 2.3424F, 0.38F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, 2.0F, -1.7F, -0.1407F, 0.0577F, 0.7772F));

        PartDefinition carapace_claw = segment1.addOrReplaceChild("carapace_claw", CubeListBuilder.create().texOffs(50, 0).addBox(-0.374F, -0.308F, -0.33F, 0.748F, 0.616F, 0.66F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -0.2F, 0.0F, 0.25F, 0.2F, -0.4F));

        PartDefinition carapace_claw_palm_r1 = carapace_claw.addOrReplaceChild("carapace_claw_palm_r1", CubeListBuilder.create().texOffs(50, 0).addBox(-0.264F, -0.1F, -0.22F, 0.528F, 0.6249F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.11F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_0 = carapace_claw.addOrReplaceChild("carapace_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.275F, -0.44F, -0.154F, 0.0F, -0.2F, -0.14F));

        PartDefinition carapace_claw_finger_0_sharp_tip_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.6905F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.143F, -1.0912F, -0.682F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition carapace_claw_finger_0_blade_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 0.8478F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.099F, -0.6274F, -0.2319F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition carapace_claw_finger_0_proximal_r1 = carapace_claw_finger_0.addOrReplaceChild("carapace_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 0.8762F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition carapace_claw_finger_1 = carapace_claw.addOrReplaceChild("carapace_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.44F, -0.154F, 0.09F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_sharp_tip_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.8161F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3728F, -0.858F, -1.7955F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_blade_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 1.0131F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7894F, -0.2917F, -2.3711F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_1_proximal_r1 = carapace_claw_finger_1.addOrReplaceChild("carapace_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(60, 0).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 1.0415F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition carapace_claw_finger_2 = carapace_claw.addOrReplaceChild("carapace_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.275F, -0.44F, -0.154F, 0.18F, 0.2F, 0.14F));

        PartDefinition carapace_claw_finger_2_sharp_tip_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.0264F, -0.1F, -0.0363F, 0.0528F, 0.6905F, 0.0726F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.143F, -1.0912F, -0.682F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition carapace_claw_finger_2_blade_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.0825F, -0.1F, -0.099F, 0.165F, 0.8478F, 0.198F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.099F, -0.6274F, -0.2319F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition carapace_claw_finger_2_proximal_r1 = carapace_claw_finger_2.addOrReplaceChild("carapace_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.1375F, -0.1F, -0.154F, 0.275F, 0.8762F, 0.308F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition segment0 = corruption_root.addOrReplaceChild("segment0", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, -3.5F));

        PartDefinition segment3 = corruption_root.addOrReplaceChild("segment3", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 4.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    static LayerDefinition ender_dragon() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, -36.0F, 0.0F));

        PartDefinition right_front_leg = corruption_root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(316, 240).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 27.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -5.0F, 2.0F, 0.15F, 0.0F, 0.0F));

        PartDefinition right_front_leg_tip = right_front_leg.addOrReplaceChild("right_front_leg_tip", CubeListBuilder.create().texOffs(350, 240).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.25F, 0.0F, 0.0F));

        PartDefinition right_front_foot = right_front_leg_tip.addOrReplaceChild("right_front_foot", CubeListBuilder.create().texOffs(376, 240).addBox(-4.0F, -2.0F, -12.0F, 8.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));

        PartDefinition right_hind_leg = corruption_root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(426, 240).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 38.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-16.0F, -9.0F, 42.0F, 0.15F, 0.0F, 0.0F));

        PartDefinition right_hind_leg_tip = right_hind_leg.addOrReplaceChild("right_hind_leg_tip", CubeListBuilder.create().texOffs(0, 296).addBox(-6.0F, -4.0F, -2.0F, 12.0F, 37.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 32.0F, -4.0F, -0.2F, 0.0F, 0.0F));

        PartDefinition right_hind_foot = right_hind_leg_tip.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(54, 296).addBox(-9.0F, -2.0F, -20.0F, 18.0F, 8.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 4.0F));

        PartDefinition right_wing = corruption_root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(306, 0).addBox(-56.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(0, 49).addBox(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.0F, -20.0F, 2.0F));

        PartDefinition right_wing_tip = right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(225, 49).addBox(-56.0F, -2.0F, -2.0F, 56.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(0, 106).addBox(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.0F)), PartPose.offset(-56.0F, 0.0F, 0.0F));

        PartDefinition left_wing = corruption_root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(306, 0).mirror().addBox(0.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(0, 49).mirror().addBox(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(12.0F, -20.0F, 2.0F));

        PartDefinition left_wing_tip = left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(225, 49).mirror().addBox(0.0F, -2.0F, -2.0F, 56.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(0, 106).mirror().addBox(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(56.0F, 0.0F, 0.0F));

        PartDefinition left_hind_leg = corruption_root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(110, 240).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 38.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.0F, -9.0F, 42.0F, 0.15F, 0.0F, 0.0F));

        PartDefinition left_hind_leg_tip = left_hind_leg.addOrReplaceChild("left_hind_leg_tip", CubeListBuilder.create().texOffs(176, 240).addBox(-6.0F, -4.0F, -2.0F, 12.0F, 37.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 32.0F, -4.0F, -0.2F, 0.0F, 0.0F));

        PartDefinition left_hind_foot = left_hind_leg_tip.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(230, 240).addBox(-9.0F, -2.0F, -20.0F, 18.0F, 8.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 4.0F));

        PartDefinition body = corruption_root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(225, 106).addBox(-12.0F, 0.0F, -16.0F, 24.0F, 24.0F, 64.0F, new CubeDeformation(0.0F))
        .texOffs(402, 106).addBox(-1.0F, -6.0F, -10.0F, 2.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(402, 106).addBox(-1.0F, -6.0F, 10.0F, 2.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(402, 106).addBox(-1.0F, -6.0F, 30.0F, 2.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.0F, 8.0F));

        PartDefinition shoulder_claw = body.addOrReplaceChild("shoulder_claw", CubeListBuilder.create().texOffs(209, 195).addBox(-4.76F, -3.92F, -4.2F, 9.52F, 7.84F, 8.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 2.0F, -7.0F, 0.16F, 0.35F, -0.42F));

        PartDefinition shoulder_claw_palm_r1 = shoulder_claw.addOrReplaceChild("shoulder_claw_palm_r1", CubeListBuilder.create().texOffs(248, 195).addBox(-3.36F, -0.1F, -2.8F, 6.72F, 5.6077F, 5.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_0 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.5F, -5.6F, -1.96F, 0.0F, -0.2F, -0.14F));

        PartDefinition shoulder_claw_finger_0_sharp_tip_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(305, 195).addBox(-0.336F, -0.1F, -0.462F, 0.672F, 6.4433F, 0.924F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.82F, -13.888F, -8.68F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shoulder_claw_finger_0_blade_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(292, 195).addBox(-1.05F, -0.1F, -1.26F, 2.1F, 8.4445F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.26F, -7.9856F, -2.9512F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shoulder_claw_finger_0_proximal_r1 = shoulder_claw_finger_0.addOrReplaceChild("shoulder_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(275, 195).addBox(-1.75F, -0.1F, -1.96F, 3.5F, 8.8062F, 3.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shoulder_claw_finger_1 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.6F, -1.96F, 0.09F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_sharp_tip_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(340, 195).addBox(-0.336F, -0.1F, -0.462F, 0.672F, 8.0411F, 0.924F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.472F, -10.92F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_blade_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(327, 195).addBox(-1.05F, -0.1F, -1.26F, 2.1F, 10.5481F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0464F, -3.7128F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_1_proximal_r1 = shoulder_claw_finger_1.addOrReplaceChild("shoulder_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(310, 195).addBox(-1.75F, -0.1F, -1.96F, 3.5F, 10.9105F, 3.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shoulder_claw_finger_2 = shoulder_claw.addOrReplaceChild("shoulder_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(3.5F, -5.6F, -1.96F, 0.18F, 0.2F, 0.14F));

        PartDefinition shoulder_claw_finger_2_sharp_tip_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(305, 195).addBox(-0.336F, -0.1F, -0.462F, 0.672F, 6.4433F, 0.924F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.82F, -13.888F, -8.68F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shoulder_claw_finger_2_blade_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(292, 195).addBox(-1.05F, -0.1F, -1.26F, 2.1F, 8.4445F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.26F, -7.9856F, -2.9512F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shoulder_claw_finger_2_proximal_r1 = shoulder_claw_finger_2.addOrReplaceChild("shoulder_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(275, 195).addBox(-1.75F, -0.1F, -1.96F, 3.5F, 8.8062F, 3.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition left_front_leg = corruption_root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 240).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 27.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -5.0F, 2.0F, 0.15F, 0.0F, 0.0F));

        PartDefinition left_front_leg_tip = left_front_leg.addOrReplaceChild("left_front_leg_tip", CubeListBuilder.create().texOffs(34, 240).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.25F, 0.0F, 0.0F));

        PartDefinition left_front_foot = left_front_leg_tip.addOrReplaceChild("left_front_foot", CubeListBuilder.create().texOffs(60, 240).addBox(-4.0F, -2.0F, -12.0F, 8.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));

        PartDefinition neck_0 = corruption_root.addOrReplaceChild("neck_0", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -12.0F));

        PartDefinition neck_1 = neck_0.addOrReplaceChild("neck_1", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -10.0F));

        PartDefinition neck_2 = neck_1.addOrReplaceChild("neck_2", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -10.0F));

        PartDefinition neck_3 = neck_2.addOrReplaceChild("neck_3", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -10.0F));

        PartDefinition neck_4 = neck_3.addOrReplaceChild("neck_4", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -10.0F));

        PartDefinition head = neck_4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 195).addBox(-6.0F, -1.0F, -24.0F, 12.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(57, 195).addBox(-8.0F, -8.0F, -10.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(122, 195).mirror().addBox(-5.0F, -12.0F, -4.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(139, 195).mirror().addBox(-5.0F, -3.0F, -22.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(122, 195).mirror().addBox(3.0F, -12.0F, -4.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(139, 195).mirror().addBox(3.0F, -3.0F, -22.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, -10.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(152, 195).addBox(-6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, -8.0F));

        PartDefinition skull_claw = head.addOrReplaceChild("skull_claw", CubeListBuilder.create().texOffs(345, 195).addBox(-2.38F, -1.96F, -2.1F, 4.76F, 3.92F, 4.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -5.0F, 3.0F, 0.2F, -0.4F, 0.5F));

        PartDefinition skull_claw_palm_r1 = skull_claw.addOrReplaceChild("skull_claw_palm_r1", CubeListBuilder.create().texOffs(366, 195).addBox(-1.68F, -0.1F, -1.4F, 3.36F, 2.9038F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_0 = skull_claw.addOrReplaceChild("skull_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.75F, -2.8F, -0.98F, 0.0F, -0.2F, -0.14F));

        PartDefinition skull_claw_finger_0_sharp_tip_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(390, 195).addBox(-0.168F, -0.1F, -0.231F, 0.336F, 3.3217F, 0.462F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.91F, -6.944F, -4.34F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition skull_claw_finger_0_blade_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(381, 195).addBox(-0.525F, -0.1F, -0.63F, 1.05F, 4.3222F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.63F, -3.9928F, -1.4756F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition skull_claw_finger_0_proximal_r1 = skull_claw_finger_0.addOrReplaceChild("skull_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(381, 195).addBox(-0.875F, -0.1F, -0.98F, 1.75F, 4.5031F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition skull_claw_finger_1 = skull_claw.addOrReplaceChild("skull_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.8F, -0.98F, 0.09F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_sharp_tip_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(404, 195).addBox(-0.168F, -0.1F, -0.231F, 0.336F, 4.1206F, 0.462F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.736F, -5.46F, -1.7955F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_blade_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(395, 195).addBox(-0.525F, -0.1F, -0.63F, 1.05F, 5.374F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0232F, -1.8564F, -2.3711F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_1_proximal_r1 = skull_claw_finger_1.addOrReplaceChild("skull_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(395, 195).addBox(-0.875F, -0.1F, -0.98F, 1.75F, 5.5553F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition skull_claw_finger_2 = skull_claw.addOrReplaceChild("skull_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, -2.8F, -0.98F, 0.18F, 0.2F, 0.14F));

        PartDefinition skull_claw_finger_2_sharp_tip_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(390, 195).addBox(-0.168F, -0.1F, -0.231F, 0.336F, 3.3217F, 0.462F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.91F, -6.944F, -4.34F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition skull_claw_finger_2_blade_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(381, 195).addBox(-0.525F, -0.1F, -0.63F, 1.05F, 4.3222F, 1.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.63F, -3.9928F, -1.4756F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition skull_claw_finger_2_proximal_r1 = skull_claw_finger_2.addOrReplaceChild("skull_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(381, 195).addBox(-0.875F, -0.1F, -0.98F, 1.75F, 4.5031F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail_0 = corruption_root.addOrReplaceChild("tail_0", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 60.0F));

        PartDefinition tail_1 = tail_0.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_2 = tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_3 = tail_2.addOrReplaceChild("tail_3", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_4 = tail_3.addOrReplaceChild("tail_4", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_5 = tail_4.addOrReplaceChild("tail_5", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_6 = tail_5.addOrReplaceChild("tail_6", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_7 = tail_6.addOrReplaceChild("tail_7", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_8 = tail_7.addOrReplaceChild("tail_8", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_9 = tail_8.addOrReplaceChild("tail_9", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_10 = tail_9.addOrReplaceChild("tail_10", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_11 = tail_10.addOrReplaceChild("tail_11", CubeListBuilder.create().texOffs(431, 106).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
        .texOffs(472, 106).addBox(-1.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition tail_claw_9 = tail_9.addOrReplaceChild("tail_claw_9", CubeListBuilder.create().texOffs(409, 195).addBox(-1.445F, -1.19F, -1.275F, 2.89F, 2.38F, 2.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.1F, 0.3F, -0.25F));

        PartDefinition tail_claw_9_palm_r1 = tail_claw_9.addOrReplaceChild("tail_claw_9_palm_r1", CubeListBuilder.create().texOffs(422, 195).addBox(-1.02F, -0.1F, -0.85F, 2.04F, 1.8416F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.425F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_9_finger_0 = tail_claw_9.addOrReplaceChild("tail_claw_9_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0625F, -1.7F, -0.595F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_9_finger_0_sharp_tip_r1 = tail_claw_9_finger_0.addOrReplaceChild("tail_claw_9_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5525F, -4.216F, -2.635F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_9_finger_0_blade_r1 = tail_claw_9_finger_0.addOrReplaceChild("tail_claw_9_finger_0_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3825F, -2.4242F, -0.8959F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_9_finger_0_proximal_r1 = tail_claw_9_finger_0.addOrReplaceChild("tail_claw_9_finger_0_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_9_finger_1 = tail_claw_9.addOrReplaceChild("tail_claw_9_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7F, -0.595F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_9_finger_1_sharp_tip_r1 = tail_claw_9_finger_1.addOrReplaceChild("tail_claw_9_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.5803F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.304F, -3.315F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_9_finger_1_blade_r1 = tail_claw_9_finger_1.addOrReplaceChild("tail_claw_9_finger_1_blade_r1", CubeListBuilder.create().texOffs(390, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 3.3414F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0498F, -1.1271F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_9_finger_1_proximal_r1 = tail_claw_9_finger_1.addOrReplaceChild("tail_claw_9_finger_1_proximal_r1", CubeListBuilder.create().texOffs(447, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 3.4514F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_9_finger_2 = tail_claw_9.addOrReplaceChild("tail_claw_9_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0625F, -1.7F, -0.595F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_9_finger_2_sharp_tip_r1 = tail_claw_9_finger_2.addOrReplaceChild("tail_claw_9_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5525F, -4.216F, -2.635F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_9_finger_2_blade_r1 = tail_claw_9_finger_2.addOrReplaceChild("tail_claw_9_finger_2_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3188F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3825F, -2.4242F, -0.8959F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_9_finger_2_proximal_r1 = tail_claw_9_finger_2.addOrReplaceChild("tail_claw_9_finger_2_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail_claw_6 = tail_6.addOrReplaceChild("tail_claw_6", CubeListBuilder.create().texOffs(409, 195).addBox(-1.445F, -1.19F, -1.275F, 2.89F, 2.38F, 2.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.1F, 0.3F, -0.25F));

        PartDefinition tail_claw_6_palm_r1 = tail_claw_6.addOrReplaceChild("tail_claw_6_palm_r1", CubeListBuilder.create().texOffs(422, 195).addBox(-1.02F, -0.1F, -0.85F, 2.04F, 1.8416F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.425F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_6_finger_0 = tail_claw_6.addOrReplaceChild("tail_claw_6_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0625F, -1.7F, -0.595F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_6_finger_0_sharp_tip_r1 = tail_claw_6_finger_0.addOrReplaceChild("tail_claw_6_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5525F, -4.216F, -2.635F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_6_finger_0_blade_r1 = tail_claw_6_finger_0.addOrReplaceChild("tail_claw_6_finger_0_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3825F, -2.4242F, -0.8959F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_6_finger_0_proximal_r1 = tail_claw_6_finger_0.addOrReplaceChild("tail_claw_6_finger_0_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_6_finger_1 = tail_claw_6.addOrReplaceChild("tail_claw_6_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7F, -0.595F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_6_finger_1_sharp_tip_r1 = tail_claw_6_finger_1.addOrReplaceChild("tail_claw_6_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.5803F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.304F, -3.315F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_6_finger_1_blade_r1 = tail_claw_6_finger_1.addOrReplaceChild("tail_claw_6_finger_1_blade_r1", CubeListBuilder.create().texOffs(390, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 3.3414F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0498F, -1.1271F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_6_finger_1_proximal_r1 = tail_claw_6_finger_1.addOrReplaceChild("tail_claw_6_finger_1_proximal_r1", CubeListBuilder.create().texOffs(447, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 3.4514F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_6_finger_2 = tail_claw_6.addOrReplaceChild("tail_claw_6_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0625F, -1.7F, -0.595F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_6_finger_2_sharp_tip_r1 = tail_claw_6_finger_2.addOrReplaceChild("tail_claw_6_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5525F, -4.216F, -2.635F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_6_finger_2_blade_r1 = tail_claw_6_finger_2.addOrReplaceChild("tail_claw_6_finger_2_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3188F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3825F, -2.4242F, -0.8959F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_6_finger_2_proximal_r1 = tail_claw_6_finger_2.addOrReplaceChild("tail_claw_6_finger_2_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail_claw_3 = tail_3.addOrReplaceChild("tail_claw_3", CubeListBuilder.create().texOffs(409, 195).addBox(-1.445F, -1.19F, -1.275F, 2.89F, 2.38F, 2.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.1F, 0.3F, -0.25F));

        PartDefinition tail_claw_3_palm_r1 = tail_claw_3.addOrReplaceChild("tail_claw_3_palm_r1", CubeListBuilder.create().texOffs(422, 195).addBox(-1.02F, -0.1F, -0.85F, 2.04F, 1.8416F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.425F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_3_finger_0 = tail_claw_3.addOrReplaceChild("tail_claw_3_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0625F, -1.7F, -0.595F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_3_finger_0_sharp_tip_r1 = tail_claw_3_finger_0.addOrReplaceChild("tail_claw_3_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5525F, -4.216F, -2.635F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_3_finger_0_blade_r1 = tail_claw_3_finger_0.addOrReplaceChild("tail_claw_3_finger_0_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3825F, -2.4242F, -0.8959F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_3_finger_0_proximal_r1 = tail_claw_3_finger_0.addOrReplaceChild("tail_claw_3_finger_0_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_3_finger_1 = tail_claw_3.addOrReplaceChild("tail_claw_3_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7F, -0.595F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_3_finger_1_sharp_tip_r1 = tail_claw_3_finger_1.addOrReplaceChild("tail_claw_3_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.5803F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.304F, -3.315F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_3_finger_1_blade_r1 = tail_claw_3_finger_1.addOrReplaceChild("tail_claw_3_finger_1_blade_r1", CubeListBuilder.create().texOffs(390, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 3.3414F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0498F, -1.1271F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_3_finger_1_proximal_r1 = tail_claw_3_finger_1.addOrReplaceChild("tail_claw_3_finger_1_proximal_r1", CubeListBuilder.create().texOffs(447, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 3.4514F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_3_finger_2 = tail_claw_3.addOrReplaceChild("tail_claw_3_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0625F, -1.7F, -0.595F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_3_finger_2_sharp_tip_r1 = tail_claw_3_finger_2.addOrReplaceChild("tail_claw_3_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1402F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5525F, -4.216F, -2.635F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_3_finger_2_blade_r1 = tail_claw_3_finger_2.addOrReplaceChild("tail_claw_3_finger_2_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3188F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3825F, -2.4242F, -0.8959F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_3_finger_2_proximal_r1 = tail_claw_3_finger_2.addOrReplaceChild("tail_claw_3_finger_2_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition tail_claw_0 = tail_0.addOrReplaceChild("tail_claw_0", CubeListBuilder.create().texOffs(409, 195).addBox(-1.445F, -1.19F, -1.275F, 2.89F, 2.38F, 2.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.1F, 0.3F, -0.25F));

        PartDefinition tail_claw_0_palm_r1 = tail_claw_0.addOrReplaceChild("tail_claw_0_palm_r1", CubeListBuilder.create().texOffs(422, 195).addBox(-1.02F, -0.1F, -0.85F, 2.04F, 1.8416F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.425F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition tail_claw_0_finger_0 = tail_claw_0.addOrReplaceChild("tail_claw_0_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0625F, -1.7F, -0.595F, 0.0F, -0.2F, -0.14F));

        PartDefinition tail_claw_0_finger_0_sharp_tip_r1 = tail_claw_0_finger_0.addOrReplaceChild("tail_claw_0_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5525F, -4.216F, -2.635F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition tail_claw_0_finger_0_blade_r1 = tail_claw_0_finger_0.addOrReplaceChild("tail_claw_0_finger_0_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3825F, -2.4242F, -0.8959F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition tail_claw_0_finger_0_proximal_r1 = tail_claw_0_finger_0.addOrReplaceChild("tail_claw_0_finger_0_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition tail_claw_0_finger_1 = tail_claw_0.addOrReplaceChild("tail_claw_0_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7F, -0.595F, 0.09F, 0.0F, 0.0F));

        PartDefinition tail_claw_0_finger_1_sharp_tip_r1 = tail_claw_0_finger_1.addOrReplaceChild("tail_claw_0_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.5803F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.304F, -3.315F, -1.7955F, 0.0F, 0.0F));

        PartDefinition tail_claw_0_finger_1_blade_r1 = tail_claw_0_finger_1.addOrReplaceChild("tail_claw_0_finger_1_blade_r1", CubeListBuilder.create().texOffs(390, 195).addBox(-0.3187F, -0.1F, -0.3825F, 0.6375F, 3.3414F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0498F, -1.1271F, -2.3711F, 0.0F, 0.0F));

        PartDefinition tail_claw_0_finger_1_proximal_r1 = tail_claw_0_finger_1.addOrReplaceChild("tail_claw_0_finger_1_proximal_r1", CubeListBuilder.create().texOffs(447, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 3.4514F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition tail_claw_0_finger_2 = tail_claw_0.addOrReplaceChild("tail_claw_0_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0625F, -1.7F, -0.595F, 0.18F, 0.2F, 0.14F));

        PartDefinition tail_claw_0_finger_2_sharp_tip_r1 = tail_claw_0_finger_2.addOrReplaceChild("tail_claw_0_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.102F, -0.1F, -0.1403F, 0.204F, 2.0953F, 0.2805F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5525F, -4.216F, -2.635F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition tail_claw_0_finger_2_blade_r1 = tail_claw_0_finger_2.addOrReplaceChild("tail_claw_0_finger_2_blade_r1", CubeListBuilder.create().texOffs(442, 195).addBox(-0.3188F, -0.1F, -0.3825F, 0.6375F, 2.7028F, 0.765F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3825F, -2.4242F, -0.8959F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition tail_claw_0_finger_2_proximal_r1 = tail_claw_0_finger_2.addOrReplaceChild("tail_claw_0_finger_2_proximal_r1", CubeListBuilder.create().texOffs(433, 195).addBox(-0.5313F, -0.1F, -0.595F, 1.0625F, 2.8126F, 1.19F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static LayerDefinition shulker() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition corruption_root = partdefinition.addOrReplaceChild("corruption_root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition lid = corruption_root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -4.0F, -15.0F, 16.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 7.0F, -0.5585F, 0.0F, 0.0F));

        PartDefinition shell_ridge_2_r1 = lid.addOrReplaceChild("shell_ridge_2_r1", CubeListBuilder.create().texOffs(236, 0).addBox(-0.275F, -0.1F, -0.35F, 0.55F, 2.2928F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.9F, -1.0F, 0.6F, 0.3472F, 0.1849F, -0.9726F));

        PartDefinition shell_ridge_1_r1 = lid.addOrReplaceChild("shell_ridge_1_r1", CubeListBuilder.create().texOffs(236, 0).addBox(-0.275F, -0.1F, -0.35F, 0.55F, 2.2928F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.9F, -3.0F, 0.6F, 0.3472F, 0.1849F, -0.9726F));

        PartDefinition shell_ridge_0_r1 = lid.addOrReplaceChild("shell_ridge_0_r1", CubeListBuilder.create().texOffs(236, 0).addBox(-0.275F, -0.1F, -0.35F, 0.55F, 2.2928F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.9F, -5.0F, 0.6F, 0.3472F, 0.1849F, -0.9726F));

        PartDefinition shell_claw = lid.addOrReplaceChild("shell_claw", CubeListBuilder.create().texOffs(185, 0).addBox(-1.105F, -0.91F, -0.975F, 2.21F, 1.82F, 1.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -4.0F, -1.5F, 0.2F, -0.25F, 0.5F));

        PartDefinition shell_claw_palm_r1 = shell_claw.addOrReplaceChild("shell_claw_palm_r1", CubeListBuilder.create().texOffs(196, 0).addBox(-0.78F, -0.1F, -0.65F, 1.56F, 1.4554F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.325F, 0.0F, -2.7707F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_0 = shell_claw.addOrReplaceChild("shell_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.8125F, -1.3F, -0.455F, 0.0F, -0.2F, -0.14F));

        PartDefinition shell_claw_finger_0_sharp_tip_r1 = shell_claw_finger_0.addOrReplaceChild("shell_claw_finger_0_sharp_tip_r1", CubeListBuilder.create().texOffs(210, 0).addBox(-0.078F, -0.1F, -0.1072F, 0.156F, 1.6493F, 0.2145F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4225F, -3.224F, -2.015F, -1.7912F, -0.073F, -0.0585F));

        PartDefinition shell_claw_finger_0_blade_r1 = shell_claw_finger_0.addOrReplaceChild("shell_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.2437F, -0.1F, -0.2925F, 0.4875F, 2.1139F, 0.585F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2925F, -1.8538F, -0.6851F, -2.3596F, 0.1669F, 0.0689F));

        PartDefinition shell_claw_finger_0_proximal_r1 = shell_claw_finger_0.addOrReplaceChild("shell_claw_finger_0_proximal_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.4063F, -0.1F, -0.455F, 0.8125F, 2.1979F, 0.91F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, 0.7701F, 0.2054F));

        PartDefinition shell_claw_finger_1 = shell_claw.addOrReplaceChild("shell_claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.3F, -0.455F, 0.09F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_1_sharp_tip_r1 = shell_claw_finger_1.addOrReplaceChild("shell_claw_finger_1_sharp_tip_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.078F, -0.1F, -0.1072F, 0.156F, 2.0203F, 0.2145F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.056F, -2.535F, -1.7955F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_1_blade_r1 = shell_claw_finger_1.addOrReplaceChild("shell_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.2438F, -0.1F, -0.2925F, 0.4875F, 2.6022F, 0.585F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.3322F, -0.8619F, -2.3711F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_1_proximal_r1 = shell_claw_finger_1.addOrReplaceChild("shell_claw_finger_1_proximal_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.4063F, -0.1F, -0.455F, 0.8125F, 2.6864F, 0.91F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7876F, 0.0F, 0.0F));

        PartDefinition shell_claw_finger_2 = shell_claw.addOrReplaceChild("shell_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.8125F, -1.3F, -0.455F, 0.18F, 0.2F, 0.14F));

        PartDefinition shell_claw_finger_2_sharp_tip_r1 = shell_claw_finger_2.addOrReplaceChild("shell_claw_finger_2_sharp_tip_r1", CubeListBuilder.create().texOffs(210, 0).addBox(-0.078F, -0.1F, -0.1072F, 0.156F, 1.6493F, 0.2145F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4225F, -3.224F, -2.015F, -1.7912F, 0.073F, 0.0585F));

        PartDefinition shell_claw_finger_2_blade_r1 = shell_claw_finger_2.addOrReplaceChild("shell_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.2438F, -0.1F, -0.2925F, 0.4875F, 2.1139F, 0.585F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2925F, -1.8538F, -0.6851F, -2.3596F, -0.1669F, -0.0689F));

        PartDefinition shell_claw_finger_2_proximal_r1 = shell_claw_finger_2.addOrReplaceChild("shell_claw_finger_2_proximal_r1", CubeListBuilder.create().texOffs(205, 0).addBox(-0.4063F, -0.1F, -0.455F, 0.8125F, 2.1979F, 0.91F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6436F, -0.7701F, -0.2054F));

        PartDefinition maw_lid_left_claw = lid.addOrReplaceChild("maw_lid_left_claw", CubeListBuilder.create().texOffs(102, 270).addBox(-1.3125F, -0.8312F, -1.05F, 2.625F, 1.6625F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, 3.5F, -12.3F, -3.1067F, -0.2443F, 0.1745F));

        PartDefinition maw_lid_left_claw_palm_r1 = maw_lid_left_claw.addOrReplaceChild("maw_lid_left_claw_palm_r1", CubeListBuilder.create().texOffs(116, 270).addBox(-0.9625F, -1.5684F, -0.7656F, 1.925F, 1.7784F, 1.5313F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_lid_left_claw_finger_0 = maw_lid_left_claw.addOrReplaceChild("maw_lid_left_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9188F, -1.0938F, -0.35F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_lid_left_claw_finger_0_tip_r1 = maw_lid_left_claw_finger_0.addOrReplaceChild("maw_lid_left_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(140, 270).addBox(-0.0787F, -1.8816F, -0.1094F, 0.1575F, 2.0916F, 0.2188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2975F, -3.3863F, -1.3125F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_lid_left_claw_finger_0_blade_r1 = maw_lid_left_claw_finger_0.addOrReplaceChild("maw_lid_left_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(134, 270).addBox(-0.2275F, -1.9018F, -0.3063F, 0.455F, 2.1118F, 0.6125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.175F, -2.0317F, -0.3062F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_lid_left_claw_finger_0_root_r1 = maw_lid_left_claw_finger_0.addOrReplaceChild("maw_lid_left_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(126, 270).addBox(-0.4287F, -2.2721F, -0.5031F, 0.8575F, 2.4821F, 1.0062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_lid_left_claw_finger_1 = maw_lid_left_claw.addOrReplaceChild("maw_lid_left_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0938F, -0.35F));

        PartDefinition maw_lid_left_claw_finger_1_tip_r1 = maw_lid_left_claw_finger_1.addOrReplaceChild("maw_lid_left_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(160, 270).addBox(-0.0788F, -1.8988F, -0.1094F, 0.1575F, 2.1088F, 0.2188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9375F, -1.3125F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_lid_left_claw_finger_1_blade_r1 = maw_lid_left_claw_finger_1.addOrReplaceChild("maw_lid_left_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(154, 270).addBox(-0.2275F, -2.079F, -0.3063F, 0.455F, 2.289F, 0.6125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.3625F, -0.3062F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_lid_left_claw_finger_1_root_r1 = maw_lid_left_claw_finger_1.addOrReplaceChild("maw_lid_left_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(146, 270).addBox(-0.4287F, -2.5923F, -0.5031F, 0.8575F, 2.8023F, 1.0062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_lid_left_claw_finger_2 = maw_lid_left_claw.addOrReplaceChild("maw_lid_left_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9188F, -1.0938F, -0.35F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_lid_left_claw_finger_2_tip_r1 = maw_lid_left_claw_finger_2.addOrReplaceChild("maw_lid_left_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(180, 270).addBox(-0.0788F, -1.8816F, -0.1094F, 0.1575F, 2.0916F, 0.2188F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2975F, -3.3863F, -1.3125F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_lid_left_claw_finger_2_blade_r1 = maw_lid_left_claw_finger_2.addOrReplaceChild("maw_lid_left_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(174, 270).addBox(-0.2275F, -1.9018F, -0.3063F, 0.455F, 2.1118F, 0.6125F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.175F, -2.0317F, -0.3062F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_lid_left_claw_finger_2_root_r1 = maw_lid_left_claw_finger_2.addOrReplaceChild("maw_lid_left_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(166, 270).addBox(-0.4287F, -2.2721F, -0.5031F, 0.8575F, 2.4821F, 1.0062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition maw_lid_right_claw = lid.addOrReplaceChild("maw_lid_right_claw", CubeListBuilder.create().texOffs(186, 270).addBox(-1.4437F, -0.9144F, -1.155F, 2.8875F, 1.8288F, 2.31F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 3.5F, -12.3F, -3.2114F, 0.2618F, -0.2094F));

        PartDefinition maw_lid_right_claw_palm_r1 = maw_lid_right_claw.addOrReplaceChild("maw_lid_right_claw_palm_r1", CubeListBuilder.create().texOffs(200, 270).addBox(-1.0587F, -1.7042F, -0.8422F, 2.1175F, 1.9142F, 1.6844F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_lid_right_claw_finger_0 = maw_lid_right_claw.addOrReplaceChild("maw_lid_right_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0106F, -1.2031F, -0.385F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_lid_right_claw_finger_0_tip_r1 = maw_lid_right_claw_finger_0.addOrReplaceChild("maw_lid_right_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(226, 270).addBox(-0.0866F, -2.0488F, -0.1203F, 0.1733F, 2.2588F, 0.2406F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3273F, -3.7249F, -1.4438F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_lid_right_claw_finger_0_blade_r1 = maw_lid_right_claw_finger_0.addOrReplaceChild("maw_lid_right_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(220, 270).addBox(-0.2502F, -2.071F, -0.3369F, 0.5005F, 2.281F, 0.6738F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1925F, -2.2349F, -0.3369F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_lid_right_claw_finger_0_root_r1 = maw_lid_right_claw_finger_0.addOrReplaceChild("maw_lid_right_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(212, 270).addBox(-0.4716F, -2.4783F, -0.5534F, 0.9432F, 2.6883F, 1.1069F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_lid_right_claw_finger_1 = maw_lid_right_claw.addOrReplaceChild("maw_lid_right_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.2031F, -0.385F));

        PartDefinition maw_lid_right_claw_finger_1_tip_r1 = maw_lid_right_claw_finger_1.addOrReplaceChild("maw_lid_right_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(246, 270).addBox(-0.0866F, -2.0677F, -0.1203F, 0.1733F, 2.2776F, 0.2406F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.3312F, -1.4438F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_lid_right_claw_finger_1_blade_r1 = maw_lid_right_claw_finger_1.addOrReplaceChild("maw_lid_right_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(240, 270).addBox(-0.2503F, -2.2659F, -0.3369F, 0.5005F, 2.4759F, 0.6738F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5988F, -0.3369F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_lid_right_claw_finger_1_root_r1 = maw_lid_right_claw_finger_1.addOrReplaceChild("maw_lid_right_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(232, 270).addBox(-0.4716F, -2.8305F, -0.5534F, 0.9432F, 3.0405F, 1.1069F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_lid_right_claw_finger_2 = maw_lid_right_claw.addOrReplaceChild("maw_lid_right_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0106F, -1.2031F, -0.385F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_lid_right_claw_finger_2_tip_r1 = maw_lid_right_claw_finger_2.addOrReplaceChild("maw_lid_right_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(266, 270).addBox(-0.0866F, -2.0488F, -0.1203F, 0.1733F, 2.2588F, 0.2406F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3273F, -3.7249F, -1.4438F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_lid_right_claw_finger_2_blade_r1 = maw_lid_right_claw_finger_2.addOrReplaceChild("maw_lid_right_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(260, 270).addBox(-0.2502F, -2.071F, -0.3369F, 0.5005F, 2.281F, 0.6738F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1925F, -2.2349F, -0.3369F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_lid_right_claw_finger_2_root_r1 = maw_lid_right_claw_finger_2.addOrReplaceChild("maw_lid_right_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(252, 270).addBox(-0.4716F, -2.4783F, -0.5534F, 0.9432F, 2.6883F, 1.1069F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition base = corruption_root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(65, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(215, 0).addBox(-2.4F, -11.0F, -2.4F, 4.8F, 4.1F, 4.8F, new CubeDeformation(0.0F))
        .texOffs(0, 40).addBox(-5.8F, -8.6F, -5.8F, 11.6F, 2.9F, 11.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition lid_hinge_flesh = base.addOrReplaceChild("lid_hinge_flesh", CubeListBuilder.create().texOffs(49, 40).addBox(-5.0F, -1.2F, -0.8F, 10.0F, 2.4F, 2.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 6.5F));

        PartDefinition maw_center_claw = base.addOrReplaceChild("maw_center_claw", CubeListBuilder.create().texOffs(104, 260).addBox(-2.2313F, -1.4131F, -1.785F, 4.4625F, 2.8262F, 3.57F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -6.2F, -0.1396F, 0.0F, 0.0F));

        PartDefinition maw_center_claw_palm_r1 = maw_center_claw.addOrReplaceChild("maw_center_claw_palm_r1", CubeListBuilder.create().texOffs(124, 260).addBox(-1.6362F, -2.5192F, -1.3016F, 3.2725F, 2.7292F, 2.6031F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_center_claw_finger_0 = maw_center_claw.addOrReplaceChild("maw_center_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5619F, -1.8594F, -0.595F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_center_claw_finger_0_tip_r1 = maw_center_claw_finger_0.addOrReplaceChild("maw_center_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(158, 260).addBox(-0.1339F, -3.0518F, -0.1859F, 0.2677F, 3.2618F, 0.3719F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5057F, -5.7566F, -2.2312F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_center_claw_finger_0_blade_r1 = maw_center_claw_finger_0.addOrReplaceChild("maw_center_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(150, 260).addBox(-0.3867F, -3.0861F, -0.5206F, 0.7735F, 3.2961F, 1.0412F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2975F, -3.454F, -0.5206F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_center_claw_finger_0_root_r1 = maw_center_claw_finger_0.addOrReplaceChild("maw_center_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(140, 260).addBox(-0.7289F, -3.7156F, -0.8553F, 1.4578F, 3.9256F, 1.7106F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_center_claw_finger_1 = maw_center_claw.addOrReplaceChild("maw_center_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.8594F, -0.595F));

        PartDefinition maw_center_claw_finger_1_tip_r1 = maw_center_claw_finger_1.addOrReplaceChild("maw_center_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(182, 260).addBox(-0.1339F, -3.0809F, -0.1859F, 0.2677F, 3.2909F, 0.3719F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.6937F, -2.2312F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_center_claw_finger_1_blade_r1 = maw_center_claw_finger_1.addOrReplaceChild("maw_center_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(174, 260).addBox(-0.3867F, -3.3873F, -0.5206F, 0.7735F, 3.5973F, 1.0412F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0162F, -0.5206F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_center_claw_finger_1_root_r1 = maw_center_claw_finger_1.addOrReplaceChild("maw_center_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(164, 260).addBox(-0.7289F, -4.2599F, -0.8553F, 1.4577F, 4.4698F, 1.7106F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_center_claw_finger_2 = maw_center_claw.addOrReplaceChild("maw_center_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5619F, -1.8594F, -0.595F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_center_claw_finger_2_tip_r1 = maw_center_claw_finger_2.addOrReplaceChild("maw_center_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(206, 260).addBox(-0.1339F, -3.0518F, -0.1859F, 0.2677F, 3.2618F, 0.3719F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5057F, -5.7566F, -2.2312F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_center_claw_finger_2_blade_r1 = maw_center_claw_finger_2.addOrReplaceChild("maw_center_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(198, 260).addBox(-0.3867F, -3.0861F, -0.5206F, 0.7735F, 3.2961F, 1.0412F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2975F, -3.454F, -0.5206F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_center_claw_finger_2_root_r1 = maw_center_claw_finger_2.addOrReplaceChild("maw_center_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(188, 260).addBox(-0.7289F, -3.7156F, -0.8553F, 1.4578F, 3.9256F, 1.7106F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition maw_left_claw = base.addOrReplaceChild("maw_left_claw", CubeListBuilder.create().texOffs(212, 260).addBox(-2.0475F, -1.2967F, -1.638F, 4.095F, 2.5935F, 3.276F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8F, -8.0F, -5.0F, -0.2094F, -0.3491F, -0.3142F));

        PartDefinition maw_left_claw_palm_r1 = maw_left_claw.addOrReplaceChild("maw_left_claw_palm_r1", CubeListBuilder.create().texOffs(232, 260).addBox(-1.5015F, -2.3291F, -1.1944F, 3.003F, 2.5391F, 2.3888F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_left_claw_finger_0 = maw_left_claw.addOrReplaceChild("maw_left_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.4333F, -1.7063F, -0.546F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_left_claw_finger_0_tip_r1 = maw_left_claw_finger_0.addOrReplaceChild("maw_left_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(264, 260).addBox(-0.1229F, -2.8178F, -0.1706F, 0.2457F, 3.0278F, 0.3413F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4641F, -5.2825F, -2.0475F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_left_claw_finger_0_blade_r1 = maw_left_claw_finger_0.addOrReplaceChild("maw_left_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(258, 260).addBox(-0.3549F, -2.8492F, -0.4778F, 0.7098F, 3.0592F, 0.9555F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.273F, -3.1695F, -0.4777F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_left_claw_finger_0_root_r1 = maw_left_claw_finger_0.addOrReplaceChild("maw_left_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(248, 260).addBox(-0.6688F, -3.4269F, -0.7849F, 1.3377F, 3.6369F, 1.5698F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_left_claw_finger_1 = maw_left_claw.addOrReplaceChild("maw_left_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.7063F, -0.546F));

        PartDefinition maw_left_claw_finger_1_tip_r1 = maw_left_claw_finger_1.addOrReplaceChild("maw_left_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(286, 260).addBox(-0.1228F, -2.8445F, -0.1706F, 0.2457F, 3.0545F, 0.3413F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.1425F, -2.0475F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_left_claw_finger_1_blade_r1 = maw_left_claw_finger_1.addOrReplaceChild("maw_left_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(280, 260).addBox(-0.3549F, -3.1256F, -0.4778F, 0.7098F, 3.3356F, 0.9555F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6855F, -0.4777F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_left_claw_finger_1_root_r1 = maw_left_claw_finger_1.addOrReplaceChild("maw_left_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(270, 260).addBox(-0.6688F, -3.9263F, -0.7849F, 1.3377F, 4.1363F, 1.5698F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_left_claw_finger_2 = maw_left_claw.addOrReplaceChild("maw_left_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.4332F, -1.7063F, -0.546F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_left_claw_finger_2_tip_r1 = maw_left_claw_finger_2.addOrReplaceChild("maw_left_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(308, 260).addBox(-0.1228F, -2.8178F, -0.1706F, 0.2457F, 3.0278F, 0.3413F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4641F, -5.2825F, -2.0475F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_left_claw_finger_2_blade_r1 = maw_left_claw_finger_2.addOrReplaceChild("maw_left_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(302, 260).addBox(-0.3549F, -2.8492F, -0.4778F, 0.7098F, 3.0592F, 0.9555F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.273F, -3.1695F, -0.4777F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_left_claw_finger_2_root_r1 = maw_left_claw_finger_2.addOrReplaceChild("maw_left_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(292, 260).addBox(-0.6689F, -3.4269F, -0.7849F, 1.3377F, 3.6369F, 1.5698F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition maw_right_claw = base.addOrReplaceChild("maw_right_claw", CubeListBuilder.create().texOffs(314, 260).addBox(-2.2575F, -1.4298F, -1.806F, 4.515F, 2.8595F, 3.612F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -8.0F, -5.0F, -0.1222F, 0.384F, 0.3142F));

        PartDefinition maw_right_claw_palm_r1 = maw_right_claw.addOrReplaceChild("maw_right_claw_palm_r1", CubeListBuilder.create().texOffs(334, 260).addBox(-1.6555F, -2.5464F, -1.3169F, 3.311F, 2.7564F, 2.6337F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_right_claw_finger_0 = maw_right_claw.addOrReplaceChild("maw_right_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5802F, -1.8812F, -0.602F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_right_claw_finger_0_tip_r1 = maw_right_claw_finger_0.addOrReplaceChild("maw_right_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(368, 260).addBox(-0.1354F, -3.0852F, -0.1881F, 0.2709F, 3.2952F, 0.3762F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5117F, -5.8244F, -2.2575F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_right_claw_finger_0_blade_r1 = maw_right_claw_finger_0.addOrReplaceChild("maw_right_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(360, 260).addBox(-0.3913F, -3.1199F, -0.5267F, 0.7826F, 3.3299F, 1.0535F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.301F, -3.4946F, -0.5267F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_right_claw_finger_0_root_r1 = maw_right_claw_finger_0.addOrReplaceChild("maw_right_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(350, 260).addBox(-0.7374F, -3.7569F, -0.8654F, 1.4749F, 3.9669F, 1.7307F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_right_claw_finger_1 = maw_right_claw.addOrReplaceChild("maw_right_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.8812F, -0.602F));

        PartDefinition maw_right_claw_finger_1_tip_r1 = maw_right_claw_finger_1.addOrReplaceChild("maw_right_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(392, 260).addBox(-0.1355F, -3.1147F, -0.1881F, 0.2709F, 3.3247F, 0.3762F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.7725F, -2.2575F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_right_claw_finger_1_blade_r1 = maw_right_claw_finger_1.addOrReplaceChild("maw_right_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(384, 260).addBox(-0.3913F, -3.4247F, -0.5267F, 0.7826F, 3.6347F, 1.0535F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0635F, -0.5267F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_right_claw_finger_1_root_r1 = maw_right_claw_finger_1.addOrReplaceChild("maw_right_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(374, 260).addBox(-0.7374F, -4.3075F, -0.8654F, 1.4749F, 4.5175F, 1.7307F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_right_claw_finger_2 = maw_right_claw.addOrReplaceChild("maw_right_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5803F, -1.8812F, -0.602F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_right_claw_finger_2_tip_r1 = maw_right_claw_finger_2.addOrReplaceChild("maw_right_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(416, 260).addBox(-0.1355F, -3.0852F, -0.1881F, 0.2709F, 3.2952F, 0.3762F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5117F, -5.8244F, -2.2575F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_right_claw_finger_2_blade_r1 = maw_right_claw_finger_2.addOrReplaceChild("maw_right_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(408, 260).addBox(-0.3913F, -3.1199F, -0.5267F, 0.7826F, 3.3299F, 1.0535F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.301F, -3.4946F, -0.5267F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_right_claw_finger_2_root_r1 = maw_right_claw_finger_2.addOrReplaceChild("maw_right_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(398, 260).addBox(-0.7374F, -3.7569F, -0.8654F, 1.4749F, 3.9669F, 1.7307F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition maw_left_outer_claw = base.addOrReplaceChild("maw_left_outer_claw", CubeListBuilder.create().texOffs(422, 260).addBox(-1.68F, -1.064F, -1.344F, 3.36F, 2.128F, 2.688F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, -7.5F, -3.5F, -0.2618F, 0.5236F, 0.4887F));

        PartDefinition maw_left_outer_claw_palm_r1 = maw_left_outer_claw.addOrReplaceChild("maw_left_outer_claw_palm_r1", CubeListBuilder.create().texOffs(438, 260).addBox(-1.232F, -1.9487F, -0.98F, 2.464F, 2.1587F, 1.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_left_outer_claw_finger_0 = maw_left_outer_claw.addOrReplaceChild("maw_left_outer_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.176F, -1.4F, -0.448F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_left_outer_claw_finger_0_tip_r1 = maw_left_outer_claw_finger_0.addOrReplaceChild("maw_left_outer_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(466, 260).addBox(-0.1008F, -2.3497F, -0.14F, 0.2016F, 2.5597F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3808F, -4.3344F, -1.68F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_left_outer_claw_finger_0_blade_r1 = maw_left_outer_claw_finger_0.addOrReplaceChild("maw_left_outer_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(460, 260).addBox(-0.2912F, -2.3755F, -0.392F, 0.5824F, 2.5855F, 0.784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.224F, -2.6006F, -0.392F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_left_outer_claw_finger_0_root_r1 = maw_left_outer_claw_finger_0.addOrReplaceChild("maw_left_outer_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(450, 260).addBox(-0.5488F, -2.8495F, -0.644F, 1.0976F, 3.0595F, 1.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_left_outer_claw_finger_1 = maw_left_outer_claw.addOrReplaceChild("maw_left_outer_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.4F, -0.448F));

        PartDefinition maw_left_outer_claw_finger_1_tip_r1 = maw_left_outer_claw_finger_1.addOrReplaceChild("maw_left_outer_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(488, 260).addBox(-0.1008F, -2.3716F, -0.14F, 0.2016F, 2.5816F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.04F, -1.68F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_left_outer_claw_finger_1_blade_r1 = maw_left_outer_claw_finger_1.addOrReplaceChild("maw_left_outer_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(482, 260).addBox(-0.2912F, -2.6023F, -0.392F, 0.5824F, 2.8123F, 0.784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.024F, -0.392F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_left_outer_claw_finger_1_root_r1 = maw_left_outer_claw_finger_1.addOrReplaceChild("maw_left_outer_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(472, 260).addBox(-0.5488F, -3.2593F, -0.644F, 1.0976F, 3.4693F, 1.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_left_outer_claw_finger_2 = maw_left_outer_claw.addOrReplaceChild("maw_left_outer_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.176F, -1.4F, -0.448F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_left_outer_claw_finger_2_tip_r1 = maw_left_outer_claw_finger_2.addOrReplaceChild("maw_left_outer_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(0, 270).addBox(-0.1008F, -2.3497F, -0.14F, 0.2016F, 2.5597F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3808F, -4.3344F, -1.68F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_left_outer_claw_finger_2_blade_r1 = maw_left_outer_claw_finger_2.addOrReplaceChild("maw_left_outer_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(504, 260).addBox(-0.2912F, -2.3755F, -0.392F, 0.5824F, 2.5855F, 0.784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.224F, -2.6006F, -0.392F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_left_outer_claw_finger_2_root_r1 = maw_left_outer_claw_finger_2.addOrReplaceChild("maw_left_outer_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(494, 260).addBox(-0.5488F, -2.8495F, -0.644F, 1.0976F, 3.0595F, 1.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition maw_right_outer_claw = base.addOrReplaceChild("maw_right_outer_claw", CubeListBuilder.create().texOffs(6, 270).addBox(-1.8375F, -1.1638F, -1.47F, 3.675F, 2.3275F, 2.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, -7.5F, -3.5F, -0.1745F, -0.4887F, -0.4887F));

        PartDefinition maw_right_outer_claw_palm_r1 = maw_right_outer_claw.addOrReplaceChild("maw_right_outer_claw_palm_r1", CubeListBuilder.create().texOffs(22, 270).addBox(-1.3475F, -2.1117F, -1.0719F, 2.695F, 2.3217F, 2.1437F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2606F, 0.0F, 0.0F));

        PartDefinition maw_right_outer_claw_finger_0 = maw_right_outer_claw.addOrReplaceChild("maw_right_outer_claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(1.2862F, -1.5313F, -0.49F, 0.0698F, 0.1571F, -0.1047F));

        PartDefinition maw_right_outer_claw_finger_0_tip_r1 = maw_right_outer_claw_finger_0.addOrReplaceChild("maw_right_outer_claw_finger_0_tip_r1", CubeListBuilder.create().texOffs(52, 270).addBox(-0.1102F, -2.5503F, -0.1531F, 0.2205F, 2.7603F, 0.3063F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4165F, -4.7408F, -1.8375F, 1.3197F, 0.0244F, -0.0314F));

        PartDefinition maw_right_outer_claw_finger_0_blade_r1 = maw_right_outer_claw_finger_0.addOrReplaceChild("maw_right_outer_claw_finger_0_blade_r1", CubeListBuilder.create().texOffs(46, 270).addBox(-0.3185F, -2.5785F, -0.4287F, 0.637F, 2.7885F, 0.8575F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.245F, -2.8445F, -0.4287F, 0.6372F, -0.0239F, 0.0725F));

        PartDefinition maw_right_outer_claw_finger_0_root_r1 = maw_right_outer_claw_finger_0.addOrReplaceChild("maw_right_outer_claw_finger_0_root_r1", CubeListBuilder.create().texOffs(36, 270).addBox(-0.6002F, -3.097F, -0.7044F, 1.2005F, 3.307F, 1.4087F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, -0.0063F, 0.085F));

        PartDefinition maw_right_outer_claw_finger_1 = maw_right_outer_claw.addOrReplaceChild("maw_right_outer_claw_finger_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5313F, -0.49F));

        PartDefinition maw_right_outer_claw_finger_1_tip_r1 = maw_right_outer_claw_finger_1.addOrReplaceChild("maw_right_outer_claw_finger_1_tip_r1", CubeListBuilder.create().texOffs(74, 270).addBox(-0.1102F, -2.5743F, -0.1531F, 0.2205F, 2.7843F, 0.3063F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5125F, -1.8375F, 1.2818F, 0.0F, 0.0F));

        PartDefinition maw_right_outer_claw_finger_1_blade_r1 = maw_right_outer_claw_finger_1.addOrReplaceChild("maw_right_outer_claw_finger_1_blade_r1", CubeListBuilder.create().texOffs(68, 270).addBox(-0.3185F, -2.8266F, -0.4287F, 0.637F, 3.0366F, 0.8575F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.3075F, -0.4287F, 0.5685F, 0.0F, 0.0F));

        PartDefinition maw_right_outer_claw_finger_1_root_r1 = maw_right_outer_claw_finger_1.addOrReplaceChild("maw_right_outer_claw_finger_1_root_r1", CubeListBuilder.create().texOffs(58, 270).addBox(-0.6002F, -3.5452F, -0.7044F, 1.2005F, 3.7552F, 1.4087F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1289F, 0.0F, 0.0F));

        PartDefinition maw_right_outer_claw_finger_2 = maw_right_outer_claw.addOrReplaceChild("maw_right_outer_claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.2862F, -1.5313F, -0.49F, -0.0698F, -0.1571F, 0.1047F));

        PartDefinition maw_right_outer_claw_finger_2_tip_r1 = maw_right_outer_claw_finger_2.addOrReplaceChild("maw_right_outer_claw_finger_2_tip_r1", CubeListBuilder.create().texOffs(96, 270).addBox(-0.1103F, -2.5503F, -0.1531F, 0.2205F, 2.7603F, 0.3063F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4165F, -4.7408F, -1.8375F, 1.3197F, -0.0244F, 0.0314F));

        PartDefinition maw_right_outer_claw_finger_2_blade_r1 = maw_right_outer_claw_finger_2.addOrReplaceChild("maw_right_outer_claw_finger_2_blade_r1", CubeListBuilder.create().texOffs(90, 270).addBox(-0.3185F, -2.5785F, -0.4287F, 0.637F, 2.7885F, 0.8575F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.245F, -2.8445F, -0.4287F, 0.6372F, 0.0239F, -0.0725F));

        PartDefinition maw_right_outer_claw_finger_2_root_r1 = maw_right_outer_claw_finger_2.addOrReplaceChild("maw_right_outer_claw_finger_2_root_r1", CubeListBuilder.create().texOffs(80, 270).addBox(-0.6002F, -3.097F, -0.7044F, 1.2005F, 3.307F, 1.4087F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1491F, 0.0063F, -0.085F));

        PartDefinition head = corruption_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(130, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.92F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head_upper_fang_3_r1 = head.addOrReplaceChild("head_upper_fang_3_r1", CubeListBuilder.create().texOffs(180, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.35F, 4.74F, -3.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_2_r1 = head.addOrReplaceChild("head_upper_fang_2_r1", CubeListBuilder.create().texOffs(180, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7833F, 4.74F, -3.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_1_r1 = head.addOrReplaceChild("head_upper_fang_1_r1", CubeListBuilder.create().texOffs(180, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7833F, 4.74F, -3.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_upper_fang_0_r1 = head.addOrReplaceChild("head_upper_fang_0_r1", CubeListBuilder.create().texOffs(180, 0).addBox(-0.175F, -0.1F, -0.225F, 0.35F, 0.9727F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.35F, 4.74F, -3.08F, -0.3295F, -0.0086F, -0.0518F));

        PartDefinition head_jaw = head.addOrReplaceChild("head_jaw", CubeListBuilder.create().texOffs(155, 0).addBox(-3.0F, -0.12F, -4.32F, 6.0F, 1.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.92F, 1.32F, 0.045F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    static java.util.Map<String,net.minecraft.client.model.geom.ModelPart> parts(net.minecraft.client.model.geom.ModelPart root,String kind) {
        String[] paths=switch(kind) {
            case "piglin" -> new String[]{"corruption_root","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/right_arm/shoulder_claw","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_0","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_1","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/body","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot"};
            case "piglin_brute" -> new String[]{"corruption_root","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/right_arm/shoulder_claw","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_0","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_1","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/body","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot"};
            case "zombified_piglin" -> new String[]{"corruption_root","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/right_arm/shoulder_claw","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_0","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_1","corruption_root/right_arm/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/body","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/head/jaw_claw","corruption_root/head/jaw_claw/jaw_claw_finger_0","corruption_root/head/jaw_claw/jaw_claw_finger_1","corruption_root/head/jaw_claw/jaw_claw_finger_2","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot"};
            case "hoglin" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/right_horn","corruption_root/head/left_horn","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/mane","corruption_root/body/back_claw","corruption_root/body/back_claw/back_claw_finger_0","corruption_root/body/back_claw/back_claw_finger_1","corruption_root/body/back_claw/back_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "zoglin" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/right_horn","corruption_root/head/left_horn","corruption_root/head/right_ear","corruption_root/head/left_ear","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_lower","corruption_root/right_front_leg/right_front_leg_lower/right_front_leg_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_lower","corruption_root/right_hind_leg/right_hind_leg_lower/right_hind_leg_foot","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_lower","corruption_root/left_hind_leg/left_hind_leg_lower/left_hind_leg_foot","corruption_root/body","corruption_root/body/mane","corruption_root/body/back_claw","corruption_root/body/back_claw/back_claw_finger_0","corruption_root/body/back_claw/back_claw_finger_1","corruption_root/body/back_claw/back_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_lower","corruption_root/left_front_leg/left_front_leg_lower/left_front_leg_foot"};
            case "ghast" -> new String[]{"corruption_root","corruption_root/body","corruption_root/body/crown_claw","corruption_root/body/crown_claw/crown_claw_finger_0","corruption_root/body/crown_claw/crown_claw_finger_1","corruption_root/body/crown_claw/crown_claw_finger_2","corruption_root/body/ghast_jaw","corruption_root/body/ghast_jaw/tentacle0","corruption_root/body/ghast_jaw/tentacle0/tentacle0_lower","corruption_root/body/ghast_jaw/tentacle0/tentacle0_lower/tendril_claw_0","corruption_root/body/ghast_jaw/tentacle0/tentacle0_lower/tendril_claw_0/tendril_claw_0_finger_0","corruption_root/body/ghast_jaw/tentacle0/tentacle0_lower/tendril_claw_0/tendril_claw_0_finger_1","corruption_root/body/ghast_jaw/tentacle0/tentacle0_lower/tendril_claw_0/tendril_claw_0_finger_2","corruption_root/body/ghast_jaw/tentacle1","corruption_root/body/ghast_jaw/tentacle1/tentacle1_lower","corruption_root/body/ghast_jaw/tentacle2","corruption_root/body/ghast_jaw/tentacle2/tentacle2_lower","corruption_root/body/ghast_jaw/tentacle3","corruption_root/body/ghast_jaw/tentacle3/tentacle3_lower","corruption_root/body/ghast_jaw/tentacle3/tentacle3_lower/tendril_claw_3","corruption_root/body/ghast_jaw/tentacle3/tentacle3_lower/tendril_claw_3/tendril_claw_3_finger_0","corruption_root/body/ghast_jaw/tentacle3/tentacle3_lower/tendril_claw_3/tendril_claw_3_finger_1","corruption_root/body/ghast_jaw/tentacle3/tentacle3_lower/tendril_claw_3/tendril_claw_3_finger_2","corruption_root/body/ghast_jaw/tentacle4","corruption_root/body/ghast_jaw/tentacle4/tentacle4_lower","corruption_root/body/ghast_jaw/tentacle5","corruption_root/body/ghast_jaw/tentacle5/tentacle5_lower","corruption_root/body/ghast_jaw/tentacle6","corruption_root/body/ghast_jaw/tentacle6/tentacle6_lower","corruption_root/body/ghast_jaw/tentacle6/tentacle6_lower/tendril_claw_6","corruption_root/body/ghast_jaw/tentacle6/tentacle6_lower/tendril_claw_6/tendril_claw_6_finger_0","corruption_root/body/ghast_jaw/tentacle6/tentacle6_lower/tendril_claw_6/tendril_claw_6_finger_1","corruption_root/body/ghast_jaw/tentacle6/tentacle6_lower/tendril_claw_6/tendril_claw_6_finger_2","corruption_root/body/ghast_jaw/tentacle7","corruption_root/body/ghast_jaw/tentacle7/tentacle7_lower","corruption_root/body/ghast_jaw/tentacle8","corruption_root/body/ghast_jaw/tentacle8/tentacle8_lower","corruption_root/body/ghast_jaw/tongue_limb","corruption_root/body/ghast_jaw/tongue_limb/tongue_limb_middle","corruption_root/body/ghast_jaw/tongue_limb/tongue_limb_middle/tongue_limb_tip","corruption_root/body/ghast_jaw/tongue_limb/tongue_limb_middle/tongue_limb_tip/tongue_terminal_claw","corruption_root/body/ghast_jaw/tongue_limb/tongue_limb_middle/tongue_limb_tip/tongue_terminal_claw/tongue_claw_finger_0","corruption_root/body/ghast_jaw/tongue_limb/tongue_limb_middle/tongue_limb_tip/tongue_terminal_claw/tongue_claw_finger_1","corruption_root/body/ghast_jaw/tongue_limb/tongue_limb_middle/tongue_limb_tip/tongue_terminal_claw/tongue_claw_finger_2","corruption_root/body/left_jaw_joint","corruption_root/body/right_jaw_joint"};
            case "blaze" -> new String[]{"corruption_root","corruption_root/part0","corruption_root/part1","corruption_root/part2","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/crown_claw","corruption_root/head/crown_claw/crown_claw_finger_0","corruption_root/head/crown_claw/crown_claw_finger_1","corruption_root/head/crown_claw/crown_claw_finger_2","corruption_root/part3","corruption_root/part11","corruption_root/part4","corruption_root/part10","corruption_root/part5","corruption_root/part6","corruption_root/part7","corruption_root/part8","corruption_root/part9"};
            case "magma_cube" -> new String[]{"corruption_root","corruption_root/cube0","corruption_root/cube0/core_claw","corruption_root/cube0/core_claw/core_claw_finger_0","corruption_root/cube0/core_claw/core_claw_finger_1","corruption_root/cube0/core_claw/core_claw_finger_2","corruption_root/cube2","corruption_root/cube1","corruption_root/cube4","corruption_root/cube3","corruption_root/cube6","corruption_root/cube5","corruption_root/cube7","corruption_root/inside_cube"};
            case "strider" -> new String[]{"corruption_root","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/right_top_bristle","corruption_root/body/right_bottom_bristle","corruption_root/body/left_top_bristle","corruption_root/body/left_bottom_bristle","corruption_root/body/right_middle_bristle","corruption_root/body/left_middle_bristle","corruption_root/body/back_claw","corruption_root/body/back_claw/back_claw_finger_0","corruption_root/body/back_claw/back_claw_finger_1","corruption_root/body/back_claw/back_claw_finger_2"};
            case "wither_skeleton" -> new String[]{"corruption_root","corruption_root/head","corruption_root/head/head_jaw","corruption_root/head/skull_claw","corruption_root/head/skull_claw/skull_claw_finger_0","corruption_root/head/skull_claw/skull_claw_finger_1","corruption_root/head/skull_claw/skull_claw_finger_2","corruption_root/right_arm","corruption_root/right_arm/right_arm_lower","corruption_root/right_arm/right_arm_lower/right_arm_hand","corruption_root/left_leg","corruption_root/left_leg/left_leg_lower","corruption_root/left_leg/left_leg_lower/left_leg_foot","corruption_root/left_arm","corruption_root/left_arm/left_arm_lower","corruption_root/left_arm/left_arm_lower/left_arm_hand","corruption_root/right_leg","corruption_root/right_leg/right_leg_lower","corruption_root/right_leg/right_leg_lower/right_leg_foot","corruption_root/body","corruption_root/body/rib_claw","corruption_root/body/rib_claw/rib_claw_finger_0","corruption_root/body/rib_claw/rib_claw_finger_1","corruption_root/body/rib_claw/rib_claw_finger_2"};
            case "wither" -> new String[]{"corruption_root","corruption_root/shoulders","corruption_root/shoulders/shoulder_claw","corruption_root/shoulders/shoulder_claw/shoulder_claw_finger_0","corruption_root/shoulders/shoulder_claw/shoulder_claw_finger_1","corruption_root/shoulders/shoulder_claw/shoulder_claw_finger_2","corruption_root/ribcage","corruption_root/ribcage/rib_claw","corruption_root/ribcage/rib_claw/rib_claw_finger_0","corruption_root/ribcage/rib_claw/rib_claw_finger_1","corruption_root/ribcage/rib_claw/rib_claw_finger_2","corruption_root/tail","corruption_root/left_head","corruption_root/left_head/left_head_jaw","corruption_root/right_head","corruption_root/right_head/right_head_jaw","corruption_root/center_head","corruption_root/center_head/center_head_jaw"};
            case "endermite" -> new String[]{"corruption_root","corruption_root/segment2","corruption_root/segment1","corruption_root/segment1/carapace_claw","corruption_root/segment1/carapace_claw/carapace_claw_finger_0","corruption_root/segment1/carapace_claw/carapace_claw_finger_1","corruption_root/segment1/carapace_claw/carapace_claw_finger_2","corruption_root/segment0","corruption_root/segment3"};
            case "ender_dragon" -> new String[]{"corruption_root","corruption_root/right_front_leg","corruption_root/right_front_leg/right_front_leg_tip","corruption_root/right_front_leg/right_front_leg_tip/right_front_foot","corruption_root/right_hind_leg","corruption_root/right_hind_leg/right_hind_leg_tip","corruption_root/right_hind_leg/right_hind_leg_tip/right_hind_foot","corruption_root/right_wing","corruption_root/right_wing/right_wing_tip","corruption_root/left_wing","corruption_root/left_wing/left_wing_tip","corruption_root/left_hind_leg","corruption_root/left_hind_leg/left_hind_leg_tip","corruption_root/left_hind_leg/left_hind_leg_tip/left_hind_foot","corruption_root/body","corruption_root/body/shoulder_claw","corruption_root/body/shoulder_claw/shoulder_claw_finger_0","corruption_root/body/shoulder_claw/shoulder_claw_finger_1","corruption_root/body/shoulder_claw/shoulder_claw_finger_2","corruption_root/left_front_leg","corruption_root/left_front_leg/left_front_leg_tip","corruption_root/left_front_leg/left_front_leg_tip/left_front_foot","corruption_root/neck_0","corruption_root/neck_0/neck_1","corruption_root/neck_0/neck_1/neck_2","corruption_root/neck_0/neck_1/neck_2/neck_3","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4/head","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4/head/jaw","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4/head/skull_claw","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4/head/skull_claw/skull_claw_finger_0","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4/head/skull_claw/skull_claw_finger_1","corruption_root/neck_0/neck_1/neck_2/neck_3/neck_4/head/skull_claw/skull_claw_finger_2","corruption_root/tail_0","corruption_root/tail_0/tail_1","corruption_root/tail_0/tail_1/tail_2","corruption_root/tail_0/tail_1/tail_2/tail_3","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9/tail_10","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9/tail_10/tail_11","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9/tail_claw_9","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9/tail_claw_9/tail_claw_9_finger_0","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9/tail_claw_9/tail_claw_9_finger_1","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_7/tail_8/tail_9/tail_claw_9/tail_claw_9_finger_2","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_claw_6","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_claw_6/tail_claw_6_finger_0","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_claw_6/tail_claw_6_finger_1","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_4/tail_5/tail_6/tail_claw_6/tail_claw_6_finger_2","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_claw_3","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_claw_3/tail_claw_3_finger_0","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_claw_3/tail_claw_3_finger_1","corruption_root/tail_0/tail_1/tail_2/tail_3/tail_claw_3/tail_claw_3_finger_2","corruption_root/tail_0/tail_claw_0","corruption_root/tail_0/tail_claw_0/tail_claw_0_finger_0","corruption_root/tail_0/tail_claw_0/tail_claw_0_finger_1","corruption_root/tail_0/tail_claw_0/tail_claw_0_finger_2"};
            case "shulker" -> new String[]{"corruption_root","corruption_root/lid","corruption_root/lid/shell_claw","corruption_root/lid/shell_claw/shell_claw_finger_0","corruption_root/lid/shell_claw/shell_claw_finger_1","corruption_root/lid/shell_claw/shell_claw_finger_2","corruption_root/lid/maw_lid_left_claw","corruption_root/lid/maw_lid_left_claw/maw_lid_left_claw_finger_0","corruption_root/lid/maw_lid_left_claw/maw_lid_left_claw_finger_1","corruption_root/lid/maw_lid_left_claw/maw_lid_left_claw_finger_2","corruption_root/lid/maw_lid_right_claw","corruption_root/lid/maw_lid_right_claw/maw_lid_right_claw_finger_0","corruption_root/lid/maw_lid_right_claw/maw_lid_right_claw_finger_1","corruption_root/lid/maw_lid_right_claw/maw_lid_right_claw_finger_2","corruption_root/base","corruption_root/base/lid_hinge_flesh","corruption_root/base/maw_center_claw","corruption_root/base/maw_center_claw/maw_center_claw_finger_0","corruption_root/base/maw_center_claw/maw_center_claw_finger_1","corruption_root/base/maw_center_claw/maw_center_claw_finger_2","corruption_root/base/maw_left_claw","corruption_root/base/maw_left_claw/maw_left_claw_finger_0","corruption_root/base/maw_left_claw/maw_left_claw_finger_1","corruption_root/base/maw_left_claw/maw_left_claw_finger_2","corruption_root/base/maw_right_claw","corruption_root/base/maw_right_claw/maw_right_claw_finger_0","corruption_root/base/maw_right_claw/maw_right_claw_finger_1","corruption_root/base/maw_right_claw/maw_right_claw_finger_2","corruption_root/base/maw_left_outer_claw","corruption_root/base/maw_left_outer_claw/maw_left_outer_claw_finger_0","corruption_root/base/maw_left_outer_claw/maw_left_outer_claw_finger_1","corruption_root/base/maw_left_outer_claw/maw_left_outer_claw_finger_2","corruption_root/base/maw_right_outer_claw","corruption_root/base/maw_right_outer_claw/maw_right_outer_claw_finger_0","corruption_root/base/maw_right_outer_claw/maw_right_outer_claw_finger_1","corruption_root/base/maw_right_outer_claw/maw_right_outer_claw_finger_2","corruption_root/head","corruption_root/head/head_jaw"};
            default -> throw new IllegalArgumentException(kind);
        };
        var result=new java.util.HashMap<String,net.minecraft.client.model.geom.ModelPart>();
        for(String path:paths) {var part=root;String[] names=path.split("/");for(String name:names)part=part.getChild(name);result.put(names[names.length-1],part);}
        return result;
    }

    private CorruptedNetherGeometry() {}
}
