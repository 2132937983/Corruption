package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/** Static geometry exported from the seven editable Blockbench projects. */
final class CorruptedVillageGeometry {
    static LayerDefinition villager() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.25F, -6.0F, -3.0F, 6.25F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(65, 0).addBox(-2.8F, -2.2F, -2.7F, 0.85F, 5.5F, 5.4F, new CubeDeformation(0.0F))
        .texOffs(80, 0).addBox(-1.5F, -9.3F, -1.5F, 3.0F, 4.3F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition left_arm_socket_r1 = body.addOrReplaceChild("left_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-2.15F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -4.0F, 0.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition right_arm_socket_r1 = body.addOrReplaceChild("right_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-0.65F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.1047F));

        PartDefinition rib_return_1_r1 = body.addOrReplaceChild("rib_return_1_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.2F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_1_r1 = body.addOrReplaceChild("rib_upper_1_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 0.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition rib_return_0_r1 = body.addOrReplaceChild("rib_return_0_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -0.8F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_0_r1 = body.addOrReplaceChild("rib_upper_0_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -2.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition torn_right_waist_r1 = body.addOrReplaceChild("torn_right_waist_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-2.0F, -1.2F, -3.0F, 2.05F, 3.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition torn_right_shoulder_r1 = body.addOrReplaceChild("torn_right_shoulder_r1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.2F, -2.2F, -3.0F, 2.25F, 4.4F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(93, 5).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(80, 9).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition cheek_tendon_r1 = head.addOrReplaceChild("cheek_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.115F, -2.2602F, -0.14F, 0.23F, 2.5102F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.92F, -4.1F, -3.2F, 3.0374F, 0.297F, -0.0156F));

        PartDefinition torn_cheek_edge_r1 = head.addOrReplaceChild("torn_cheek_edge_r1", CubeListBuilder.create().texOffs(113, 0).addBox(-0.32F, -0.1F, -0.65F, 0.37F, 1.3F, 0.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -3.0F, -3.2F, 0.0F, 0.1396F, -0.1571F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -2.32F, -3.88F, 2.7979F, 0.2232F, -0.0389F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -2.32F, -3.88F, 2.7979F, -0.2232F, 0.0389F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(27, 12).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.0F));

        PartDefinition exposed_jaw_tendon_r1 = jaw.addOrReplaceChild("exposed_jaw_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.18F, -2.0528F, -0.215F, 0.36F, 2.3028F, 0.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -0.3F, -5.8F, -3.0861F, 0.0F, 0.0F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition ornament = head.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(52, 29).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6F, -8.5F, 2.5F, 0.1571F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(75, 13).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.1F, -2.1723F, -0.115F, 0.2F, 2.4223F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -4.352F, -2.176F, 0.9638F, -0.0409F, 0.0782F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.27F, -2.4507F, -0.295F, 0.54F, 2.7007F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -2.72F, -0.7072F, 0.731F, -0.0261F, 0.0682F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.425F, -3.0715F, -0.45F, 0.85F, 3.3215F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2534F, -0.0113F, 0.0887F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(75, 13).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.1F, -2.6455F, -0.115F, 0.2F, 2.8955F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.44F, -2.72F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.27F, -2.9945F, -0.295F, 0.54F, 3.2445F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.4F, -0.884F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(111, 28).addBox(-0.425F, -3.763F, -0.45F, 0.85F, 4.013F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(75, 13).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.1F, -2.1723F, -0.115F, 0.2F, 2.4223F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -4.352F, -2.176F, 0.9638F, 0.0409F, -0.0782F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.27F, -2.4507F, -0.295F, 0.54F, 2.7007F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -2.72F, -0.7072F, 0.731F, 0.0261F, -0.0682F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.425F, -3.0715F, -0.45F, 0.85F, 3.3215F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2534F, 0.0113F, -0.0887F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.5F, -4.0F, 0.0F));

        PartDefinition right_arm_sleeve_r1 = right_arm.addOrReplaceChild("right_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, 1.0797F, -0.053F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(0, 19).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 6.0F, -0.25F));

        PartDefinition right_arm_cuff_seam_r1 = right_arm_lower.addOrReplaceChild("right_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition right_arm_torn_cuff_r1 = right_arm_lower.addOrReplaceChild("right_arm_torn_cuff_r1", CubeListBuilder.create().texOffs(60, 21).addBox(-1.2F, -3.2604F, -1.325F, 2.4F, 3.5104F, 2.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9068F, -1.2811F, 0.1753F));

        PartDefinition right_arm_exposed_forearm_r1 = right_arm_lower.addOrReplaceChild("right_arm_exposed_forearm_r1", CubeListBuilder.create().texOffs(13, 19).addBox(-0.6F, -5.1564F, -0.65F, 1.2F, 5.4064F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, -1.2848F, 0.1086F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 4.9F, -0.2F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.5F, -4.0F, 0.0F));

        PartDefinition left_arm_sleeve_r1 = left_arm.addOrReplaceChild("left_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, -1.0797F, 0.053F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(105, 22).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 6.0F, -0.25F));

        PartDefinition left_arm_cuff_seam_r1 = left_arm_lower.addOrReplaceChild("left_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition left_arm_forearm_r1 = left_arm_lower.addOrReplaceChild("left_arm_forearm_r1", CubeListBuilder.create().texOffs(22, 23).addBox(-1.6F, -5.1564F, -1.625F, 3.2F, 5.4064F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, 1.2848F, -0.1086F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 4.9F, -0.2F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-2.0F, 6.0F, 0.0F));

        PartDefinition leg_0_upper_r1 = leg_0.addOrReplaceChild("leg_0_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, -0.3206F, -0.0236F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 5.76F, 0.8F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(39, 23).addBox(-1.332F, -5.7731F, -1.404F, 2.664F, 6.0231F, 2.808F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, 0.1875F, -0.0166F));

        PartDefinition leg_0_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_joint_r1", CubeListBuilder.create().texOffs(105, 22).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.09F, 5.44F, -0.95F));

        PartDefinition leg_0_sole_r1 = leg_0_foot.addOrReplaceChild("leg_0_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0349F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(2.0F, 6.0F, 0.0F));

        PartDefinition leg_1_upper_r1 = leg_1.addOrReplaceChild("leg_1_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, 0.3206F, 0.0236F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 5.76F, 0.8F));

        PartDefinition leg_1_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.36F, -2.1831F, -0.45F, 0.72F, 2.4331F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6615F, 3.536F, -0.6175F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_torn_trouser_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_trouser_r1", CubeListBuilder.create().texOffs(73, 28).addBox(-1.26F, -3.84F, -1.26F, 2.52F, 4.09F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_exposed_bone_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_bone_r1", CubeListBuilder.create().texOffs(118, 22).addBox(-0.648F, -5.7731F, -0.72F, 1.296F, 6.0231F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_joint_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(0.09F, 5.44F, -0.95F));

        PartDefinition leg_1_sole_r1 = leg_1_foot.addOrReplaceChild("leg_1_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0349F));

        PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition robe_torn_edge_1_r1 = robe.addOrReplaceChild("robe_torn_edge_1_r1", CubeListBuilder.create().texOffs(123, 0).addBox(-0.35F, -1.9646F, -0.35F, 0.7F, 2.2146F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, 4.0F, -3.0F, 0.0966F, -0.9216F, 2.9474F));

        PartDefinition robe_torn_edge_0_r1 = robe.addOrReplaceChild("robe_torn_edge_0_r1", CubeListBuilder.create().texOffs(123, 0).addBox(-0.35F, -2.4613F, -0.35F, 0.7F, 2.7113F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 4.0F, -3.0F, 0.0751F, -0.9239F, 2.991F));

        PartDefinition torn_right_robe_r1 = robe.addOrReplaceChild("torn_right_robe_r1", CubeListBuilder.create().texOffs(17, 34).addBox(-3.8F, -1.6F, -3.3F, 3.75F, 5.3F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, -0.0524F));

        PartDefinition uneven_left_robe_r1 = robe.addOrReplaceChild("uneven_left_robe_r1", CubeListBuilder.create().texOffs(86, 28).addBox(0.2F, -2.5F, -3.3F, 4.05F, 7.7F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, 0.0349F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition pillager() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.25F, -6.0F, -3.0F, 6.25F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(65, 0).addBox(-2.8F, -2.2F, -2.7F, 0.85F, 5.5F, 5.4F, new CubeDeformation(0.0F))
        .texOffs(80, 0).addBox(-1.5F, -9.3F, -1.5F, 3.0F, 4.3F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(93, 28).addBox(-0.65F, 3.3F, -3.5F, 1.3F, 1.2F, 0.55F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition torn_harness_back_r1 = body.addOrReplaceChild("torn_harness_back_r1", CubeListBuilder.create().texOffs(100, 28).addBox(-3.1F, -0.2F, -0.2F, 6.4F, 0.8F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition crossed_harness_right_r1 = body.addOrReplaceChild("crossed_harness_right_r1", CubeListBuilder.create().texOffs(86, 28).addBox(-0.525F, -10.5233F, -0.35F, 1.05F, 10.7733F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, -5.8F, -3.15F, 0.0097F, -0.035F, 2.5992F));

        PartDefinition crossed_harness_left_r1 = body.addOrReplaceChild("crossed_harness_left_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.525F, -10.472F, -0.35F, 1.05F, 10.722F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2F, -5.8F, -3.15F, 0.0098F, 0.0358F, -2.6075F));

        PartDefinition left_arm_socket_r1 = body.addOrReplaceChild("left_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-2.15F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -4.0F, 0.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition right_arm_socket_r1 = body.addOrReplaceChild("right_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-0.65F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.1047F));

        PartDefinition rib_return_1_r1 = body.addOrReplaceChild("rib_return_1_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.2F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_1_r1 = body.addOrReplaceChild("rib_upper_1_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 0.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition rib_return_0_r1 = body.addOrReplaceChild("rib_return_0_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -0.8F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_0_r1 = body.addOrReplaceChild("rib_upper_0_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -2.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition torn_right_waist_r1 = body.addOrReplaceChild("torn_right_waist_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-2.0F, -1.2F, -3.0F, 2.05F, 3.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition torn_right_shoulder_r1 = body.addOrReplaceChild("torn_right_shoulder_r1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.2F, -2.2F, -3.0F, 2.25F, 4.4F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(93, 5).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(80, 9).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition cheek_tendon_r1 = head.addOrReplaceChild("cheek_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.115F, -2.2602F, -0.14F, 0.23F, 2.5102F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.92F, -4.1F, -3.2F, 3.0374F, 0.297F, -0.0156F));

        PartDefinition torn_cheek_edge_r1 = head.addOrReplaceChild("torn_cheek_edge_r1", CubeListBuilder.create().texOffs(113, 0).addBox(-0.32F, -0.1F, -0.65F, 0.37F, 1.3F, 0.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -3.0F, -3.2F, 0.0F, 0.1396F, -0.1571F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -2.32F, -3.88F, 2.7979F, 0.2232F, -0.0389F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -2.32F, -3.88F, 2.7979F, -0.2232F, 0.0389F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(27, 12).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.0F));

        PartDefinition exposed_jaw_tendon_r1 = jaw.addOrReplaceChild("exposed_jaw_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.18F, -2.0528F, -0.215F, 0.36F, 2.3028F, 0.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -0.3F, -5.8F, -3.0861F, 0.0F, 0.0F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.5F, -4.0F, 0.0F));

        PartDefinition right_arm_sleeve_r1 = right_arm.addOrReplaceChild("right_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, 1.0797F, -0.053F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(0, 19).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 6.0F, -0.25F));

        PartDefinition right_arm_cuff_seam_r1 = right_arm_lower.addOrReplaceChild("right_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition right_arm_torn_cuff_r1 = right_arm_lower.addOrReplaceChild("right_arm_torn_cuff_r1", CubeListBuilder.create().texOffs(60, 21).addBox(-1.2F, -3.2604F, -1.325F, 2.4F, 3.5104F, 2.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9068F, -1.2811F, 0.1753F));

        PartDefinition right_arm_exposed_forearm_r1 = right_arm_lower.addOrReplaceChild("right_arm_exposed_forearm_r1", CubeListBuilder.create().texOffs(13, 19).addBox(-0.6F, -5.1564F, -0.65F, 1.2F, 5.4064F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, -1.2848F, 0.1086F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 4.9F, -0.2F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.5F, -4.0F, 0.0F));

        PartDefinition left_arm_sleeve_r1 = left_arm.addOrReplaceChild("left_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, -1.0797F, 0.053F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(105, 22).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 6.0F, -0.25F));

        PartDefinition left_arm_cuff_seam_r1 = left_arm_lower.addOrReplaceChild("left_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition left_arm_forearm_r1 = left_arm_lower.addOrReplaceChild("left_arm_forearm_r1", CubeListBuilder.create().texOffs(22, 23).addBox(-1.6F, -5.1564F, -1.625F, 3.2F, 5.4064F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, 1.2848F, -0.1086F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 4.9F, -0.2F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-2.0F, 6.0F, 0.0F));

        PartDefinition leg_0_upper_r1 = leg_0.addOrReplaceChild("leg_0_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, -0.3206F, -0.0236F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 5.76F, 0.8F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(39, 23).addBox(-1.332F, -5.7731F, -1.404F, 2.664F, 6.0231F, 2.808F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, 0.1875F, -0.0166F));

        PartDefinition leg_0_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_joint_r1", CubeListBuilder.create().texOffs(105, 22).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.09F, 5.44F, -0.95F));

        PartDefinition leg_0_sole_r1 = leg_0_foot.addOrReplaceChild("leg_0_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0349F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(2.0F, 6.0F, 0.0F));

        PartDefinition leg_1_upper_r1 = leg_1.addOrReplaceChild("leg_1_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, 0.3206F, 0.0236F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 5.76F, 0.8F));

        PartDefinition leg_1_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.36F, -2.1831F, -0.45F, 0.72F, 2.4331F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6615F, 3.536F, -0.6175F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_torn_trouser_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_trouser_r1", CubeListBuilder.create().texOffs(73, 28).addBox(-1.26F, -3.84F, -1.26F, 2.52F, 4.09F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_exposed_bone_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_bone_r1", CubeListBuilder.create().texOffs(118, 22).addBox(-0.648F, -5.7731F, -0.72F, 1.296F, 6.0231F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_joint_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(0.09F, 5.44F, -0.95F));

        PartDefinition leg_1_sole_r1 = leg_1_foot.addOrReplaceChild("leg_1_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0349F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(59, 29).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -5.1F, 2.7F, 0.1571F, 1.1345F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(123, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(100, 31).addBox(-0.1F, -2.1723F, -0.115F, 0.2F, 2.4223F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -4.352F, -2.176F, 0.9638F, -0.0409F, 0.0782F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(100, 31).addBox(-0.27F, -2.4507F, -0.295F, 0.54F, 2.7007F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -2.72F, -0.7072F, 0.731F, -0.0261F, 0.0682F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.425F, -3.0715F, -0.45F, 0.85F, 3.3215F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2534F, -0.0113F, 0.0887F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(123, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(100, 31).addBox(-0.1F, -2.6455F, -0.115F, 0.2F, 2.8955F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.44F, -2.72F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.27F, -2.9945F, -0.295F, 0.54F, 3.2445F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.4F, -0.884F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(105, 31).addBox(-0.425F, -3.763F, -0.45F, 0.85F, 4.013F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(123, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(100, 31).addBox(-0.1F, -2.1723F, -0.115F, 0.2F, 2.4223F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -4.352F, -2.176F, 0.9638F, 0.0409F, -0.0782F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(100, 31).addBox(-0.27F, -2.4507F, -0.295F, 0.54F, 2.7007F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -2.72F, -0.7072F, 0.731F, 0.0261F, -0.0682F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.425F, -3.0715F, -0.45F, 0.85F, 3.3215F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2534F, 0.0113F, -0.0887F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition vindicator() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.25F, -6.0F, -3.0F, 6.25F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(65, 0).addBox(-2.8F, -2.2F, -2.7F, 0.85F, 5.5F, 5.4F, new CubeDeformation(0.0F))
        .texOffs(80, 0).addBox(-1.5F, -9.3F, -1.5F, 3.0F, 4.3F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition jacket_hem_left_r1 = body.addOrReplaceChild("jacket_hem_left_r1", CubeListBuilder.create().texOffs(93, 28).addBox(0.4F, -1.5F, -3.2F, 3.8F, 2.8F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.0524F));

        PartDefinition vindicator_right_lapel_r1 = body.addOrReplaceChild("vindicator_right_lapel_r1", CubeListBuilder.create().texOffs(86, 28).addBox(-0.725F, -4.9118F, -0.4F, 1.45F, 5.1618F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.9F, -6.0F, -3.2F, 0.0107F, -0.0534F, 2.7446F));

        PartDefinition vindicator_left_lapel_r1 = body.addOrReplaceChild("vindicator_left_lapel_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.8F, -5.9747F, -0.4F, 1.6F, 6.2247F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -6.0F, -3.2F, 0.0087F, 0.0511F, -2.8028F));

        PartDefinition left_arm_socket_r1 = body.addOrReplaceChild("left_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-2.15F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -4.0F, 0.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition right_arm_socket_r1 = body.addOrReplaceChild("right_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-0.65F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.1047F));

        PartDefinition rib_return_1_r1 = body.addOrReplaceChild("rib_return_1_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.2F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_1_r1 = body.addOrReplaceChild("rib_upper_1_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 0.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition rib_return_0_r1 = body.addOrReplaceChild("rib_return_0_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -0.8F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_0_r1 = body.addOrReplaceChild("rib_upper_0_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -2.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition torn_right_waist_r1 = body.addOrReplaceChild("torn_right_waist_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-2.0F, -1.2F, -3.0F, 2.05F, 3.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition torn_right_shoulder_r1 = body.addOrReplaceChild("torn_right_shoulder_r1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.2F, -2.2F, -3.0F, 2.25F, 4.4F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(93, 5).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(80, 9).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition cheek_tendon_r1 = head.addOrReplaceChild("cheek_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.115F, -2.2602F, -0.14F, 0.23F, 2.5102F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.92F, -4.1F, -3.2F, 3.0374F, 0.297F, -0.0156F));

        PartDefinition torn_cheek_edge_r1 = head.addOrReplaceChild("torn_cheek_edge_r1", CubeListBuilder.create().texOffs(113, 0).addBox(-0.32F, -0.1F, -0.65F, 0.37F, 1.3F, 0.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -3.0F, -3.2F, 0.0F, 0.1396F, -0.1571F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -2.32F, -3.88F, 2.7979F, 0.2232F, -0.0389F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -2.32F, -3.88F, 2.7979F, -0.2232F, 0.0389F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(27, 12).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.0F));

        PartDefinition exposed_jaw_tendon_r1 = jaw.addOrReplaceChild("exposed_jaw_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.18F, -2.0528F, -0.215F, 0.36F, 2.3028F, 0.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -0.3F, -5.8F, -3.0861F, 0.0F, 0.0F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.5F, -4.0F, 0.0F));

        PartDefinition right_arm_sleeve_r1 = right_arm.addOrReplaceChild("right_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, 1.0797F, -0.053F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(0, 19).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 6.0F, -0.25F));

        PartDefinition right_arm_cuff_seam_r1 = right_arm_lower.addOrReplaceChild("right_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition right_arm_torn_cuff_r1 = right_arm_lower.addOrReplaceChild("right_arm_torn_cuff_r1", CubeListBuilder.create().texOffs(60, 21).addBox(-1.2F, -3.2604F, -1.325F, 2.4F, 3.5104F, 2.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9068F, -1.2811F, 0.1753F));

        PartDefinition right_arm_exposed_forearm_r1 = right_arm_lower.addOrReplaceChild("right_arm_exposed_forearm_r1", CubeListBuilder.create().texOffs(13, 19).addBox(-0.6F, -5.1564F, -0.65F, 1.2F, 5.4064F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, -1.2848F, 0.1086F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 4.9F, -0.2F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.5F, -4.0F, 0.0F));

        PartDefinition left_arm_sleeve_r1 = left_arm.addOrReplaceChild("left_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, -1.0797F, 0.053F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(105, 22).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 6.0F, -0.25F));

        PartDefinition left_arm_cuff_seam_r1 = left_arm_lower.addOrReplaceChild("left_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition left_arm_forearm_r1 = left_arm_lower.addOrReplaceChild("left_arm_forearm_r1", CubeListBuilder.create().texOffs(22, 23).addBox(-1.6F, -5.1564F, -1.625F, 3.2F, 5.4064F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, 1.2848F, -0.1086F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 4.9F, -0.2F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-2.0F, 6.0F, 0.0F));

        PartDefinition leg_0_upper_r1 = leg_0.addOrReplaceChild("leg_0_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, -0.3206F, -0.0236F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 5.76F, 0.8F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(39, 23).addBox(-1.332F, -5.7731F, -1.404F, 2.664F, 6.0231F, 2.808F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, 0.1875F, -0.0166F));

        PartDefinition leg_0_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_joint_r1", CubeListBuilder.create().texOffs(105, 22).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.09F, 5.44F, -0.95F));

        PartDefinition leg_0_sole_r1 = leg_0_foot.addOrReplaceChild("leg_0_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0349F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(2.0F, 6.0F, 0.0F));

        PartDefinition leg_1_upper_r1 = leg_1.addOrReplaceChild("leg_1_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, 0.3206F, 0.0236F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 5.76F, 0.8F));

        PartDefinition leg_1_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.36F, -2.1831F, -0.45F, 0.72F, 2.4331F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6615F, 3.536F, -0.6175F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_torn_trouser_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_trouser_r1", CubeListBuilder.create().texOffs(73, 28).addBox(-1.26F, -3.84F, -1.26F, 2.52F, 4.09F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_exposed_bone_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_bone_r1", CubeListBuilder.create().texOffs(118, 22).addBox(-0.648F, -5.7731F, -0.72F, 1.296F, 6.0231F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_joint_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(0.09F, 5.44F, -0.95F));

        PartDefinition leg_1_sole_r1 = leg_1_foot.addOrReplaceChild("leg_1_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0349F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(59, 29).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -5.1F, 2.7F, 0.1571F, 1.1345F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(123, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(52, 32).addBox(-0.1F, -2.1723F, -0.115F, 0.2F, 2.4223F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -4.352F, -2.176F, 0.9638F, -0.0409F, 0.0782F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(52, 32).addBox(-0.27F, -2.4507F, -0.295F, 0.54F, 2.7007F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -2.72F, -0.7072F, 0.731F, -0.0261F, 0.0682F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.425F, -3.0715F, -0.45F, 0.85F, 3.3215F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2534F, -0.0113F, 0.0887F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(123, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(52, 32).addBox(-0.1F, -2.6455F, -0.115F, 0.2F, 2.8955F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.44F, -2.72F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.27F, -2.9945F, -0.295F, 0.54F, 3.2445F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.4F, -0.884F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(116, 32).addBox(-0.425F, -3.763F, -0.45F, 0.85F, 4.013F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(123, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(52, 32).addBox(-0.1F, -2.1723F, -0.115F, 0.2F, 2.4223F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -4.352F, -2.176F, 0.9638F, 0.0409F, -0.0782F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(52, 32).addBox(-0.27F, -2.4507F, -0.295F, 0.54F, 2.7007F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -2.72F, -0.7072F, 0.731F, 0.0261F, -0.0682F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.425F, -3.0715F, -0.45F, 0.85F, 3.3215F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2534F, 0.0113F, -0.0887F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition evoker() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.25F, -6.0F, -3.0F, 6.25F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(65, 0).addBox(-2.8F, -2.2F, -2.7F, 0.85F, 5.5F, 5.4F, new CubeDeformation(0.0F))
        .texOffs(80, 0).addBox(-1.5F, -9.3F, -1.5F, 3.0F, 4.3F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(48, 16).addBox(-0.8F, -3.8F, -3.5F, 1.6F, 1.5F, 0.55F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition raised_collar_1_r1 = body.addOrReplaceChild("raised_collar_1_r1", CubeListBuilder.create().texOffs(57, 15).addBox(-0.4F, -2.3971F, -0.325F, 0.8F, 2.6471F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1F, -6.0F, -2.7F, 0.7156F, -1.1308F, 2.0766F));

        PartDefinition raised_collar_m1_r1 = body.addOrReplaceChild("raised_collar_m1_r1", CubeListBuilder.create().texOffs(57, 15).addBox(-0.4F, -2.3971F, -0.325F, 0.8F, 2.6471F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1F, -6.0F, -2.7F, 0.7156F, 1.1308F, -2.0766F));

        PartDefinition ceremonial_trim_1_r1 = body.addOrReplaceChild("ceremonial_trim_1_r1", CubeListBuilder.create().texOffs(121, 21).addBox(-0.45F, -9.4429F, -0.35F, 0.9F, 9.6929F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -5.8F, -3.2F, 0.011F, -0.1528F, 2.998F));

        PartDefinition ceremonial_trim_m1_r1 = body.addOrReplaceChild("ceremonial_trim_m1_r1", CubeListBuilder.create().texOffs(121, 21).addBox(-0.45F, -9.4429F, -0.35F, 0.9F, 9.6929F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -5.8F, -3.2F, 0.011F, 0.1528F, -2.998F));

        PartDefinition left_arm_socket_r1 = body.addOrReplaceChild("left_arm_socket_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-2.15F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -4.0F, 0.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition right_arm_socket_r1 = body.addOrReplaceChild("right_arm_socket_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-0.65F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.1047F));

        PartDefinition rib_return_1_r1 = body.addOrReplaceChild("rib_return_1_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.2F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_1_r1 = body.addOrReplaceChild("rib_upper_1_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 0.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition rib_return_0_r1 = body.addOrReplaceChild("rib_return_0_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -0.8F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_0_r1 = body.addOrReplaceChild("rib_upper_0_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -2.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition torn_right_waist_r1 = body.addOrReplaceChild("torn_right_waist_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-2.0F, -1.2F, -3.0F, 2.05F, 3.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition torn_right_shoulder_r1 = body.addOrReplaceChild("torn_right_shoulder_r1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.2F, -2.2F, -3.0F, 2.25F, 4.4F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(93, 5).addBox(-4.0F, -10.0F, -2.5F, 8.0F, 8.0F, 6.5F, new CubeDeformation(0.0F))
        .texOffs(27, 12).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 5.2F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(103, 0).addBox(-4.0F, -2.8F, -4.0F, 8.0F, 0.8F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(80, 9).addBox(3.0F, -4.8F, -4.0F, 1.0F, 2.0F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(48, 11).addBox(-1.0F, -4.8F, -4.0F, 2.0F, 2.0F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(80, 9).addBox(-4.0F, -4.8F, -4.0F, 1.0F, 2.0F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(57, 11).addBox(1.0F, -4.8F, -2.62F, 2.0F, 2.0F, 0.18F, new CubeDeformation(0.0F))
        .texOffs(57, 11).addBox(-3.0F, -4.8F, -2.62F, 2.0F, 2.0F, 0.18F, new CubeDeformation(0.0F))
        .texOffs(64, 13).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition cheek_tendon_r1 = head.addOrReplaceChild("cheek_tendon_r1", CubeListBuilder.create().texOffs(73, 13).addBox(-0.115F, -2.2602F, -0.14F, 0.23F, 2.5102F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.92F, -4.1F, -3.2F, 3.0374F, 0.297F, -0.0156F));

        PartDefinition torn_cheek_edge_r1 = head.addOrReplaceChild("torn_cheek_edge_r1", CubeListBuilder.create().texOffs(87, 13).addBox(-0.32F, -0.1F, -0.65F, 0.37F, 1.3F, 0.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -3.0F, -3.2F, 0.0F, 0.1396F, -0.1571F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -2.32F, -3.88F, 2.7979F, 0.2232F, -0.0389F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -2.32F, -3.88F, 2.7979F, -0.2232F, 0.0389F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(48, 20).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.0F));

        PartDefinition exposed_jaw_tendon_r1 = jaw.addOrReplaceChild("exposed_jaw_tendon_r1", CubeListBuilder.create().texOffs(73, 13).addBox(-0.18F, -2.0528F, -0.215F, 0.36F, 2.3028F, 0.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -0.3F, -5.8F, -3.0861F, 0.0F, 0.0F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(87, 9).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.5F, -4.0F, 0.0F));

        PartDefinition right_arm_sleeve_r1 = right_arm.addOrReplaceChild("right_arm_sleeve_r1", CubeListBuilder.create().texOffs(15, 21).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, 1.0797F, -0.053F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(32, 21).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 6.0F, -0.25F));

        PartDefinition right_arm_cuff_seam_r1 = right_arm_lower.addOrReplaceChild("right_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(57, 15).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition right_arm_torn_cuff_r1 = right_arm_lower.addOrReplaceChild("right_arm_torn_cuff_r1", CubeListBuilder.create().texOffs(90, 21).addBox(-1.2F, -3.2604F, -1.325F, 2.4F, 3.5104F, 2.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9068F, -1.2811F, 0.1753F));

        PartDefinition right_arm_exposed_forearm_r1 = right_arm_lower.addOrReplaceChild("right_arm_exposed_forearm_r1", CubeListBuilder.create().texOffs(81, 17).addBox(-0.6F, -5.1564F, -0.65F, 1.2F, 5.4064F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, -1.2848F, 0.1086F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(103, 21).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 4.9F, -0.2F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.5F, -4.0F, 0.0F));

        PartDefinition left_arm_sleeve_r1 = left_arm.addOrReplaceChild("left_arm_sleeve_r1", CubeListBuilder.create().texOffs(15, 21).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, -1.0797F, 0.053F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(0, 27).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 6.0F, -0.25F));

        PartDefinition left_arm_cuff_seam_r1 = left_arm_lower.addOrReplaceChild("left_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(57, 15).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition left_arm_forearm_r1 = left_arm_lower.addOrReplaceChild("left_arm_forearm_r1", CubeListBuilder.create().texOffs(103, 27).addBox(-1.6F, -5.1564F, -1.625F, 3.2F, 5.4064F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, 1.2848F, -0.1086F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create().texOffs(103, 21).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 4.9F, -0.2F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-2.0F, 6.0F, 0.0F));

        PartDefinition leg_0_upper_r1 = leg_0.addOrReplaceChild("leg_0_upper_r1", CubeListBuilder.create().texOffs(15, 21).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, -0.3206F, -0.0236F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 5.76F, 0.8F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(32, 27).addBox(-1.332F, -5.7731F, -1.404F, 2.664F, 6.0231F, 2.808F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, 0.1875F, -0.0166F));

        PartDefinition leg_0_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_joint_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.09F, 5.44F, -0.95F));

        PartDefinition leg_0_sole_r1 = leg_0_foot.addOrReplaceChild("leg_0_sole_r1", CubeListBuilder.create().texOffs(81, 29).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0349F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(2.0F, 6.0F, 0.0F));

        PartDefinition leg_1_upper_r1 = leg_1.addOrReplaceChild("leg_1_upper_r1", CubeListBuilder.create().texOffs(15, 21).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, 0.3206F, 0.0236F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 5.76F, 0.8F));

        PartDefinition leg_1_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_tendon_r1", CubeListBuilder.create().texOffs(73, 13).addBox(-0.36F, -2.1831F, -0.45F, 0.72F, 2.4331F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6615F, 3.536F, -0.6175F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_torn_trouser_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_trouser_r1", CubeListBuilder.create().texOffs(54, 31).addBox(-1.26F, -3.84F, -1.26F, 2.52F, 4.09F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_exposed_bone_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_bone_r1", CubeListBuilder.create().texOffs(45, 31).addBox(-0.648F, -5.7731F, -0.72F, 1.296F, 6.0231F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_joint_r1", CubeListBuilder.create().texOffs(32, 21).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(0.09F, 5.44F, -0.95F));

        PartDefinition leg_1_sole_r1 = leg_1_foot.addOrReplaceChild("leg_1_sole_r1", CubeListBuilder.create().texOffs(81, 29).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0349F));

        PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition robe_torn_edge_1_r1 = robe.addOrReplaceChild("robe_torn_edge_1_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.35F, -1.9646F, -0.35F, 0.7F, 2.2146F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, 4.0F, -3.0F, 0.0966F, -0.9216F, 2.9474F));

        PartDefinition robe_torn_edge_0_r1 = robe.addOrReplaceChild("robe_torn_edge_0_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.35F, -2.4613F, -0.35F, 0.7F, 2.7113F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 4.0F, -3.0F, 0.0751F, -0.9239F, 2.991F));

        PartDefinition torn_right_robe_r1 = robe.addOrReplaceChild("torn_right_robe_r1", CubeListBuilder.create().texOffs(67, 36).addBox(-3.8F, -1.6F, -3.3F, 3.75F, 5.3F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, -0.0524F));

        PartDefinition uneven_left_robe_r1 = robe.addOrReplaceChild("uneven_left_robe_r1", CubeListBuilder.create().texOffs(0, 33).addBox(0.2F, -2.5F, -3.3F, 4.05F, 7.7F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, 0.0349F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(67, 31).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -5.1F, 2.7F, 0.1571F, 1.1345F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(32, 21).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(78, 14).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(25, 33).addBox(-0.1F, -2.7908F, -0.115F, 0.2F, 3.0408F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -5.76F, -2.88F, 0.9652F, -0.031F, 0.0591F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(25, 33).addBox(-0.27F, -3.1599F, -0.295F, 0.54F, 3.4099F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -3.6F, -0.936F, 0.7318F, -0.0198F, 0.0516F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(98, 29).addBox(-0.425F, -3.9781F, -0.45F, 0.85F, 4.2281F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2538F, -0.0086F, 0.0671F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(78, 14).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(25, 33).addBox(-0.1F, -3.4205F, -0.115F, 0.2F, 3.6705F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.2F, -3.6F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(98, 29).addBox(-0.27F, -3.8825F, -0.295F, 0.54F, 4.1325F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, -1.17F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(120, 33).addBox(-0.425F, -4.8996F, -0.45F, 0.85F, 5.1496F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(78, 14).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(25, 33).addBox(-0.1F, -2.7908F, -0.115F, 0.2F, 3.0408F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -5.76F, -2.88F, 0.9652F, 0.031F, -0.0591F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(25, 33).addBox(-0.27F, -3.1599F, -0.295F, 0.54F, 3.4099F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -3.6F, -0.936F, 0.7318F, 0.0198F, -0.0516F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(98, 29).addBox(-0.425F, -3.9781F, -0.45F, 0.85F, 4.2281F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2538F, 0.0086F, -0.0671F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition illusioner() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.25F, -6.0F, -3.0F, 6.25F, 12.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(65, 0).addBox(-2.8F, -2.2F, -2.7F, 0.85F, 5.5F, 5.4F, new CubeDeformation(0.0F))
        .texOffs(80, 0).addBox(-1.5F, -9.3F, -1.5F, 3.0F, 4.3F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(111, 28).addBox(-0.8F, -3.8F, -3.5F, 1.6F, 1.5F, 0.55F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition raised_collar_1_r1 = body.addOrReplaceChild("raised_collar_1_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.4F, -2.3971F, -0.325F, 0.8F, 2.6471F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1F, -6.0F, -2.7F, 0.7156F, -1.1308F, 2.0766F));

        PartDefinition raised_collar_m1_r1 = body.addOrReplaceChild("raised_collar_m1_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.4F, -2.3971F, -0.325F, 0.8F, 2.6471F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1F, -6.0F, -2.7F, 0.7156F, 1.1308F, -2.0766F));

        PartDefinition ceremonial_trim_1_r1 = body.addOrReplaceChild("ceremonial_trim_1_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.45F, -9.4429F, -0.35F, 0.9F, 9.6929F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -5.8F, -3.2F, 0.011F, -0.1528F, 2.998F));

        PartDefinition ceremonial_trim_m1_r1 = body.addOrReplaceChild("ceremonial_trim_m1_r1", CubeListBuilder.create().texOffs(52, 23).addBox(-0.45F, -9.4429F, -0.35F, 0.9F, 9.6929F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -5.8F, -3.2F, 0.011F, 0.1528F, -2.998F));

        PartDefinition left_arm_socket_r1 = body.addOrReplaceChild("left_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-2.15F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -4.0F, 0.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition right_arm_socket_r1 = body.addOrReplaceChild("right_arm_socket_r1", CubeListBuilder.create().texOffs(60, 13).addBox(-0.65F, -1.5F, -1.7F, 2.8F, 2.8F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.1047F));

        PartDefinition rib_return_1_r1 = body.addOrReplaceChild("rib_return_1_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.2F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_1_r1 = body.addOrReplaceChild("rib_upper_1_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 0.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition rib_return_0_r1 = body.addOrReplaceChild("rib_return_0_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-0.25F, -1.6464F, -0.275F, 0.5F, 1.8964F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -0.8F, -2.9F, -0.0772F, 0.3804F, 2.7457F));

        PartDefinition rib_upper_0_r1 = body.addOrReplaceChild("rib_upper_0_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.325F, -2.4226F, -0.35F, 0.65F, 2.6726F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -2.0F, -2.7F, 0.0936F, 0.1712F, -2.1428F));

        PartDefinition torn_right_waist_r1 = body.addOrReplaceChild("torn_right_waist_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-2.0F, -1.2F, -3.0F, 2.05F, 3.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition torn_right_shoulder_r1 = body.addOrReplaceChild("torn_right_shoulder_r1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.2F, -2.2F, -3.0F, 2.25F, 4.4F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(93, 5).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(80, 9).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition cheek_tendon_r1 = head.addOrReplaceChild("cheek_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.115F, -2.2602F, -0.14F, 0.23F, 2.5102F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.92F, -4.1F, -3.2F, 3.0374F, 0.297F, -0.0156F));

        PartDefinition torn_cheek_edge_r1 = head.addOrReplaceChild("torn_cheek_edge_r1", CubeListBuilder.create().texOffs(113, 0).addBox(-0.32F, -0.1F, -0.65F, 0.37F, 1.3F, 0.73F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -3.0F, -3.2F, 0.0F, 0.1396F, -0.1571F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -2.32F, -3.88F, 2.7979F, 0.2232F, -0.0389F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, -2.32F, -3.88F, 2.6447F, -0.2161F, 0.055F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.175F, -1.0877F, -0.225F, 0.35F, 1.3377F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, -2.32F, -3.88F, 2.6447F, 0.2161F, -0.055F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.21F, -1.4368F, -0.225F, 0.42F, 1.6868F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -2.32F, -3.88F, 2.7979F, -0.2232F, 0.0389F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(27, 12).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.0F));

        PartDefinition exposed_jaw_tendon_r1 = jaw.addOrReplaceChild("exposed_jaw_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.18F, -2.0528F, -0.215F, 0.36F, 2.3028F, 0.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -0.3F, -5.8F, -3.0861F, 0.0F, 0.0F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.2392F, -0.22F, 0.34F, 1.4892F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 0.4F, -6.85F, 0.3725F, 0.0095F, -0.0506F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -1.1469F, -0.22F, 0.34F, 1.3969F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 0.4F, -6.85F, 0.413F, 0.0117F, -0.0558F));

        PartDefinition hood = head.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(40, 35).addBox(-4.3F, -3.7F, 1.6F, 8.6F, 6.0F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 1.3F));

        PartDefinition torn_hood_side_1_r1 = hood.addOrReplaceChild("torn_hood_side_1_r1", CubeListBuilder.create().texOffs(77, 44).addBox(-4.6F, -3.3F, -5.0F, 0.9F, 5.9F, 7.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0524F));

        PartDefinition torn_hood_side_m1_r1 = hood.addOrReplaceChild("torn_hood_side_m1_r1", CubeListBuilder.create().texOffs(77, 44).addBox(3.7F, -3.3F, -5.0F, 0.9F, 5.9F, 7.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition hood_top_r1 = hood.addOrReplaceChild("hood_top_r1", CubeListBuilder.create().texOffs(40, 44).addBox(-4.3F, -4.2F, -5.5F, 8.7F, 1.4F, 8.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0349F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.5F, -4.0F, 0.0F));

        PartDefinition right_arm_sleeve_r1 = right_arm.addOrReplaceChild("right_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, 1.0797F, -0.053F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(0, 19).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 6.0F, -0.25F));

        PartDefinition right_arm_cuff_seam_r1 = right_arm_lower.addOrReplaceChild("right_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition right_arm_torn_cuff_r1 = right_arm_lower.addOrReplaceChild("right_arm_torn_cuff_r1", CubeListBuilder.create().texOffs(60, 21).addBox(-1.2F, -3.2604F, -1.325F, 2.4F, 3.5104F, 2.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9068F, -1.2811F, 0.1753F));

        PartDefinition right_arm_exposed_forearm_r1 = right_arm_lower.addOrReplaceChild("right_arm_exposed_forearm_r1", CubeListBuilder.create().texOffs(13, 19).addBox(-0.6F, -5.1564F, -0.65F, 1.2F, 5.4064F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, -1.2848F, 0.1086F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 4.9F, -0.2F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.5F, -4.0F, 0.0F));

        PartDefinition left_arm_sleeve_r1 = left_arm.addOrReplaceChild("left_arm_sleeve_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.85F, -6.2571F, -1.875F, 3.7F, 6.5071F, 3.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0532F, -1.0797F, 0.053F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(105, 22).addBox(-1.4F, -0.7F, -1.4F, 2.8F, 1.3F, 2.8F, new CubeDeformation(0.0F)), PartPose.offset(0.15F, 6.0F, -0.25F));

        PartDefinition left_arm_cuff_seam_r1 = left_arm_lower.addOrReplaceChild("left_arm_cuff_seam_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.16F, -1.9128F, -0.2F, 0.32F, 2.1628F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.1F, -1.4F, -0.0308F, -0.2171F, -2.8608F));

        PartDefinition left_arm_forearm_r1 = left_arm_lower.addOrReplaceChild("left_arm_forearm_r1", CubeListBuilder.create().texOffs(22, 23).addBox(-1.6F, -5.1564F, -1.625F, 3.2F, 5.4064F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9966F, 1.2848F, -0.1086F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create().texOffs(92, 22).addBox(-1.35F, -1.1F, -1.3F, 2.7F, 2.0F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(-0.15F, 4.9F, -0.2F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-2.0F, 6.0F, 0.0F));

        PartDefinition leg_0_upper_r1 = leg_0.addOrReplaceChild("leg_0_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, -0.3206F, -0.0236F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 5.76F, 0.8F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(39, 23).addBox(-1.332F, -5.7731F, -1.404F, 2.664F, 6.0231F, 2.808F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, 0.1875F, -0.0166F));

        PartDefinition leg_0_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_joint_r1", CubeListBuilder.create().texOffs(105, 22).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.09F, 5.44F, -0.95F));

        PartDefinition leg_0_sole_r1 = leg_0_foot.addOrReplaceChild("leg_0_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0349F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(2.0F, 6.0F, 0.0F));

        PartDefinition leg_1_upper_r1 = leg_1.addOrReplaceChild("leg_1_upper_r1", CubeListBuilder.create().texOffs(75, 16).addBox(-1.8F, -6.0667F, -1.656F, 3.6F, 6.3167F, 3.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9962F, 0.3206F, 0.0236F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 5.76F, 0.8F));

        PartDefinition leg_1_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_tendon_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-0.36F, -2.1831F, -0.45F, 0.72F, 2.4331F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6615F, 3.536F, -0.6175F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_torn_trouser_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_trouser_r1", CubeListBuilder.create().texOffs(73, 28).addBox(-1.26F, -3.84F, -1.26F, 2.52F, 4.09F, 2.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_exposed_bone_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_bone_r1", CubeListBuilder.create().texOffs(118, 22).addBox(-0.648F, -5.7731F, -0.72F, 1.296F, 6.0231F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9656F, -0.1875F, 0.0166F));

        PartDefinition leg_1_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_joint_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.44F, -0.8F, -1.44F, 2.88F, 1.5F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(0.09F, 5.44F, -0.95F));

        PartDefinition leg_1_sole_r1 = leg_1_foot.addOrReplaceChild("leg_1_sole_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-1.512F, -0.4F, -2.34F, 3.024F, 1.2F, 3.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0349F));

        PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition robe_torn_edge_1_r1 = robe.addOrReplaceChild("robe_torn_edge_1_r1", CubeListBuilder.create().texOffs(123, 0).addBox(-0.35F, -1.9646F, -0.35F, 0.7F, 2.2146F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, 4.0F, -3.0F, 0.0966F, -0.9216F, 2.9474F));

        PartDefinition robe_torn_edge_0_r1 = robe.addOrReplaceChild("robe_torn_edge_0_r1", CubeListBuilder.create().texOffs(123, 0).addBox(-0.35F, -2.4613F, -0.35F, 0.7F, 2.7113F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 4.0F, -3.0F, 0.0751F, -0.9239F, 2.991F));

        PartDefinition torn_right_robe_r1 = robe.addOrReplaceChild("torn_right_robe_r1", CubeListBuilder.create().texOffs(17, 34).addBox(-3.8F, -1.6F, -3.3F, 3.75F, 5.3F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, -0.0524F));

        PartDefinition uneven_left_robe_r1 = robe.addOrReplaceChild("uneven_left_robe_r1", CubeListBuilder.create().texOffs(86, 28).addBox(0.2F, -2.5F, -3.3F, 4.05F, 7.7F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, 0.0349F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(57, 29).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -5.1F, 2.7F, 0.1571F, 1.1345F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(75, 13).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(68, 29).addBox(-0.1F, -2.369F, -0.115F, 0.2F, 2.619F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -4.8F, -2.4F, 0.9644F, -0.0371F, 0.0709F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(68, 29).addBox(-0.27F, -2.6763F, -0.295F, 0.54F, 2.9263F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -3.0F, -0.78F, 0.7313F, -0.0237F, 0.0619F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.425F, -3.3598F, -0.45F, 0.85F, 3.6098F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2535F, -0.0103F, 0.0805F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(75, 13).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.1F, -2.8921F, -0.115F, 0.2F, 3.1421F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -3.0F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.27F, -3.2771F, -0.295F, 0.54F, 3.5271F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.75F, -0.975F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(111, 32).addBox(-0.425F, -4.1247F, -0.45F, 0.85F, 4.3747F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(75, 13).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(68, 29).addBox(-0.1F, -2.369F, -0.115F, 0.2F, 2.619F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -4.8F, -2.4F, 0.9644F, 0.0371F, -0.0709F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(68, 29).addBox(-0.27F, -2.6763F, -0.295F, 0.54F, 2.9263F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -3.0F, -0.78F, 0.7313F, 0.0237F, -0.0619F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(17, 28).addBox(-0.425F, -3.3598F, -0.45F, 0.85F, 3.6098F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2535F, 0.0103F, -0.0805F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition vex() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 4.4F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(68, 0).addBox(-2.35F, -2.1F, -0.8F, 1.25F, 1.3F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(68, 0).addBox(1.1F, -2.1F, -0.8F, 1.25F, 1.3F, 1.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.0F));

        PartDefinition uneven_ghost_tail_2_r1 = body.addOrReplaceChild("uneven_ghost_tail_2_r1", CubeListBuilder.create().texOffs(63, 0).addBox(-0.325F, -2.4294F, -0.375F, 0.65F, 2.6794F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, 2.0F, 0.0F, -2.6645F, -1.0477F, -0.2791F));

        PartDefinition uneven_ghost_tail_1_r1 = body.addOrReplaceChild("uneven_ghost_tail_1_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-0.625F, -4.1819F, -0.375F, 1.25F, 4.4319F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -3.0141F, 0.0F, 0.0F));

        PartDefinition uneven_ghost_tail_0_r1 = body.addOrReplaceChild("uneven_ghost_tail_0_r1", CubeListBuilder.create().texOffs(51, 0).addBox(-0.325F, -2.9146F, -0.375F, 0.65F, 3.1646F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8F, 2.0F, 0.0F, -2.7487F, 1.0586F, 0.2318F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(11, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 0).addBox(-1.6F, -0.2F, -4.1F, 3.5F, 1.3F, 4.3F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.9F, 1.8F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-2.0F, -1.5F, 0.0F));

        PartDefinition right_arm_upper_r1 = right_arm.addOrReplaceChild("right_arm_upper_r1", CubeListBuilder.create().texOffs(77, 0).addBox(-0.85F, -2.5635F, -0.825F, 1.7F, 2.8135F, 1.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2259F, 1.2771F, -2.8383F));

        PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create(), PartPose.offset(-0.2F, 2.3F, -0.15F));

        PartDefinition right_arm_forearm_r1 = right_arm_lower.addOrReplaceChild("right_arm_forearm_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-0.375F, -2.268F, -0.375F, 0.75F, 2.518F, 0.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9704F, -0.7568F, 0.0682F));

        PartDefinition right_arm_hand = right_arm_lower.addOrReplaceChild("right_arm_hand", CubeListBuilder.create().texOffs(68, 0).addBox(-0.7F, -0.65F, -0.65F, 1.4F, 1.15F, 1.3F, new CubeDeformation(0.0F)), PartPose.offset(0.1F, 2.0F, -0.25F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-1.1F, -0.6F, 0.95F));

        PartDefinition right_wing_outer_spar_r1 = right_wing.addOrReplaceChild("right_wing_outer_spar_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.14F, -4.61F, -0.175F, 0.28F, 4.86F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -1.7F, 1.45F, -0.8609F, -0.8429F, -1.5469F));

        PartDefinition right_wing_inner_spar_r1 = right_wing.addOrReplaceChild("right_wing_inner_spar_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -4.2752F, -0.21F, 0.34F, 4.5252F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.4707F, -0.8806F, -0.9604F));

        PartDefinition right_wing_ragged_membrane_2_r1 = right_wing.addOrReplaceChild("right_wing_ragged_membrane_2_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.4F, -4.5605F, -0.775F, 0.8F, 4.8105F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -1.7F, 1.55F, -0.8093F, -0.8324F, -1.6024F));

        PartDefinition right_wing_ragged_membrane_1_r1 = right_wing.addOrReplaceChild("right_wing_ragged_membrane_1_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.4F, -4.3071F, -0.775F, 0.8F, 4.5571F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3F, -1.1F, 1.0F, -1.1146F, -0.9563F, -1.3874F));

        PartDefinition right_wing_ragged_membrane_0_r1 = right_wing.addOrReplaceChild("right_wing_ragged_membrane_0_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.4F, -4.141F, -0.775F, 0.8F, 4.391F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -0.5F, 0.45F, -1.6426F, -0.9991F, -0.9398F));

        PartDefinition right_wing_leading_r1 = right_wing.addOrReplaceChild("right_wing_leading_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.375F, -6.4534F, -0.4F, 0.75F, 6.7034F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4698F, -0.2045F, -0.8101F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(2.0F, -1.5F, 0.0F));

        PartDefinition left_arm_upper_r1 = left_arm.addOrReplaceChild("left_arm_upper_r1", CubeListBuilder.create().texOffs(77, 0).addBox(-0.85F, -2.5635F, -0.825F, 1.7F, 2.8135F, 1.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2259F, -1.2771F, 2.8383F));

        PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create(), PartPose.offset(0.2F, 2.3F, -0.15F));

        PartDefinition left_arm_forearm_r1 = left_arm_lower.addOrReplaceChild("left_arm_forearm_r1", CubeListBuilder.create().texOffs(77, 0).addBox(-0.675F, -2.268F, -0.65F, 1.35F, 2.518F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9704F, 0.7568F, -0.0682F));

        PartDefinition left_arm_hand = left_arm_lower.addOrReplaceChild("left_arm_hand", CubeListBuilder.create().texOffs(68, 0).addBox(-0.7F, -0.65F, -0.65F, 1.4F, 1.15F, 1.3F, new CubeDeformation(0.0F)), PartPose.offset(-0.1F, 2.0F, -0.25F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(1.1F, -0.6F, 0.95F));

        PartDefinition left_wing_outer_spar_r1 = left_wing.addOrReplaceChild("left_wing_outer_spar_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.14F, -4.61F, -0.175F, 0.28F, 4.86F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2F, -1.7F, 1.45F, -0.8609F, 0.8429F, 1.5469F));

        PartDefinition left_wing_inner_spar_r1 = left_wing.addOrReplaceChild("left_wing_inner_spar_r1", CubeListBuilder.create().texOffs(103, 0).addBox(-0.17F, -4.2752F, -0.21F, 0.34F, 4.5252F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.4707F, 0.8806F, 0.9604F));

        PartDefinition left_wing_ragged_membrane_2_r1 = left_wing.addOrReplaceChild("left_wing_ragged_membrane_2_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.4F, -4.5605F, -0.775F, 0.8F, 4.8105F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2F, -1.7F, 1.55F, -0.8093F, 0.8324F, 1.6024F));

        PartDefinition left_wing_ragged_membrane_1_r1 = left_wing.addOrReplaceChild("left_wing_ragged_membrane_1_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.4F, -4.3071F, -0.775F, 0.8F, 4.5571F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, -1.1F, 1.0F, -1.1146F, 0.9563F, 1.3874F));

        PartDefinition left_wing_ragged_membrane_0_r1 = left_wing.addOrReplaceChild("left_wing_ragged_membrane_0_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.4F, -4.141F, -0.775F, 0.8F, 4.391F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -0.5F, 0.45F, -1.6426F, 0.9991F, 0.9398F));

        PartDefinition left_wing_leading_r1 = left_wing.addOrReplaceChild("left_wing_leading_r1", CubeListBuilder.create().texOffs(91, 0).addBox(-0.375F, -6.4534F, -0.4F, 0.75F, 6.7034F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4698F, 0.2045F, 0.8101F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(63, 5).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3F, -1.6F, 0.8F, 0.1571F, 1.1345F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(121, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(86, 5).addBox(-0.1F, -1.527F, -0.115F, 0.2F, 1.777F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -2.88F, -1.44F, 0.9598F, -0.0614F, 0.118F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(86, 5).addBox(-0.27F, -1.7107F, -0.295F, 0.54F, 1.9607F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -1.8F, -0.468F, 0.7288F, -0.0393F, 0.103F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(121, 3).addBox(-0.425F, -2.1266F, -0.45F, 0.85F, 2.3766F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2521F, -0.017F, 0.1336F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(121, 3).addBox(-0.1F, -1.8353F, -0.115F, 0.2F, 2.0853F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, -1.8F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(121, 3).addBox(-0.27F, -2.0662F, -0.295F, 0.54F, 2.3162F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.25F, -0.585F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(121, 3).addBox(-0.425F, -2.5748F, -0.45F, 0.85F, 2.8248F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(121, 0).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(86, 5).addBox(-0.1F, -1.527F, -0.115F, 0.2F, 1.777F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -2.88F, -1.44F, 0.9598F, 0.0614F, -0.118F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(86, 5).addBox(-0.27F, -1.7107F, -0.295F, 0.54F, 1.9607F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -1.8F, -0.468F, 0.7288F, 0.0393F, -0.103F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(121, 3).addBox(-0.425F, -2.1266F, -0.45F, 0.85F, 2.3766F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2521F, 0.017F, -0.1336F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    static LayerDefinition ravager() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.6F, -10.0F, -12.0F, 9.6F, 20.0F, 29.0F, new CubeDeformation(0.0F))
        .texOffs(137, 0).addBox(-3.1F, -7.0F, -3.5F, 1.0F, 15.0F, 12.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 4.0F));

        PartDefinition ravager_hip_socket_3_r1 = body.addOrReplaceChild("ravager_hip_socket_3_r1", CubeListBuilder.create().texOffs(212, 29).addBox(-3.0F, -1.3F, -3.3F, 6.0F, 3.6F, 6.6F, new CubeDeformation(0.0F))
        .texOffs(212, 29).addBox(-3.0F, -1.3F, -26.3F, 6.0F, 3.6F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -11.0F, 14.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition ravager_hip_socket_1_r1 = body.addOrReplaceChild("ravager_hip_socket_1_r1", CubeListBuilder.create().texOffs(212, 29).addBox(-3.0F, -1.3F, -3.3F, 6.0F, 3.6F, 6.6F, new CubeDeformation(0.0F))
        .texOffs(212, 29).addBox(-3.0F, -1.3F, -26.3F, 6.0F, 3.6F, 6.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -11.0F, 14.0F, 0.0F, 0.0F, -0.0698F));

        PartDefinition ravager_rib_tip_2_r1 = body.addOrReplaceChild("ravager_rib_tip_2_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.575F, -8.3061F, -0.6F, 1.15F, 8.5561F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -2.0F, 5.2F, -0.0465F, 0.6409F, 3.0018F));

        PartDefinition ravager_rib_upper_2_r1 = body.addOrReplaceChild("ravager_rib_upper_2_r1", CubeListBuilder.create().texOffs(166, 0).addBox(-0.825F, -6.9133F, -0.75F, 1.65F, 7.1633F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -7.0F, 5.0F, -0.0301F, -0.0795F, -2.4175F));

        PartDefinition ravager_rib_tip_1_r1 = body.addOrReplaceChild("ravager_rib_tip_1_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.575F, -8.3061F, -0.6F, 1.15F, 8.5561F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -2.0F, 2.2F, -0.0465F, 0.6409F, 3.0018F));

        PartDefinition ravager_rib_upper_1_r1 = body.addOrReplaceChild("ravager_rib_upper_1_r1", CubeListBuilder.create().texOffs(166, 0).addBox(-0.825F, -6.9133F, -0.75F, 1.65F, 7.1633F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -7.0F, 2.0F, -0.0301F, -0.0795F, -2.4175F));

        PartDefinition ravager_rib_tip_0_r1 = body.addOrReplaceChild("ravager_rib_tip_0_r1", CubeListBuilder.create().texOffs(175, 0).addBox(-0.575F, -8.3061F, -0.6F, 1.15F, 8.5561F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -2.0F, -0.8F, -0.0465F, 0.6409F, 3.0018F));

        PartDefinition ravager_rib_upper_0_r1 = body.addOrReplaceChild("ravager_rib_upper_0_r1", CubeListBuilder.create().texOffs(166, 0).addBox(-0.825F, -6.9133F, -0.75F, 1.65F, 7.1633F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -7.0F, -1.0F, -0.0301F, -0.0795F, -2.4175F));

        PartDefinition right_haunch_r1 = body.addOrReplaceChild("right_haunch_r1", CubeListBuilder.create().texOffs(108, 0).addBox(-4.7F, -8.0F, -4.0F, 5.0F, 18.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3F, -1.0F, 12.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition right_shoulder_r1 = body.addOrReplaceChild("right_shoulder_r1", CubeListBuilder.create().texOffs(79, 0).addBox(-4.6F, -9.0F, -6.0F, 4.8F, 19.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -1.0F, -6.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(184, 0).addBox(-5.0F, -5.0F, -4.5F, 10.0F, 10.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -12.0F));

        PartDefinition neck_fold_1_r1 = neck.addOrReplaceChild("neck_fold_1_r1", CubeListBuilder.create().texOffs(250, 55).addBox(-0.4F, -6.9589F, -0.45F, 0.8F, 7.2089F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -2.0F, -2.0F, -2.677F, -0.0631F, -0.0149F));

        PartDefinition neck_fold_m1_r1 = neck.addOrReplaceChild("neck_fold_m1_r1", CubeListBuilder.create().texOffs(250, 55).addBox(-0.4F, -6.9589F, -0.45F, 0.8F, 7.2089F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4F, -2.0F, -2.0F, -2.677F, 0.0631F, 0.0149F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(79, 29).addBox(-8.0F, -9.0F, -13.5F, 16.0F, 20.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(166, 12).addBox(-2.0F, 5.0F, -17.5F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -4.0F));

        PartDefinition jaw_tendon_1_r1 = head.addOrReplaceChild("jaw_tendon_1_r1", CubeListBuilder.create().texOffs(190, 56).addBox(-0.275F, -9.7368F, -0.325F, 0.55F, 9.9868F, 0.65F, new CubeDeformation(0.0F))
        .texOffs(190, 56).addBox(14.325F, -9.7368F, -0.325F, 0.55F, 9.9868F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.3F, 7.0F, 1.0F, 1.8925F, 0.0F, 0.0F));

        PartDefinition horn_sharp_tip_r1 = head.addOrReplaceChild("horn_sharp_tip_r1", CubeListBuilder.create().texOffs(207, 29).addBox(-0.21F, -2.3971F, -0.35F, 0.42F, 2.6471F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.2F, -9.0F, -18.5F, 1.2629F, -0.2127F, 0.2899F));

        PartDefinition horn_sickle_r1 = head.addOrReplaceChild("horn_sickle_r1", CubeListBuilder.create().texOffs(241, 20).addBox(-0.75F, -6.4814F, -1.25F, 1.5F, 6.7314F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.5F, -6.5F, -12.8F, 1.156F, -0.0314F, 0.0482F));

        PartDefinition horn_root_1_r1 = head.addOrReplaceChild("horn_root_1_r1", CubeListBuilder.create().texOffs(241, 0).addBox(-1.0F, -6.621F, -2.0F, 2.0F, 6.871F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -3.0F, -7.5F, 0.9838F, 0.0422F, -0.0786F));

        PartDefinition broken_horn_r1 = head.addOrReplaceChild("broken_horn_r1", CubeListBuilder.create().texOffs(241, 12).addBox(-0.9F, -2.1761F, -1.6F, 1.8F, 2.4261F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.5F, -6.5F, -12.8F, 1.0823F, 0.0312F, -0.052F));

        PartDefinition horn_root_m1_r1 = head.addOrReplaceChild("horn_root_m1_r1", CubeListBuilder.create().texOffs(241, 0).addBox(-1.0F, -6.621F, -2.0F, 2.0F, 6.871F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -3.0F, -7.5F, 0.9838F, -0.0422F, 0.0786F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 2.0F));

        PartDefinition broken_ravager_jaw_r1 = jaw.addOrReplaceChild("broken_ravager_jaw_r1", CubeListBuilder.create().texOffs(144, 29).addBox(-7.2F, -0.6F, -15.3F, 15.0F, 4.1F, 15.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0524F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-8.0F, -11.0F, -9.0F));

        PartDefinition leg_0_upper_r1 = leg_0.addOrReplaceChild("leg_0_upper_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-4.0F, -18.0285F, -4.0F, 8.0F, 18.2785F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0941F, -0.322F, -0.0077F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 17.76F, 0.8F));

        PartDefinition leg_0_tendon_r1 = leg_0_lower.addOrReplaceChild("leg_0_tendon_r1", CubeListBuilder.create().texOffs(66, 50).addBox(-0.8F, -6.7126F, -1.0F, 1.6F, 6.9626F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6585F, 11.986F, -0.6175F, 3.0892F, 0.1888F, -0.005F));

        PartDefinition leg_0_torn_trouser_r1 = leg_0_lower.addOrReplaceChild("leg_0_torn_trouser_r1", CubeListBuilder.create().texOffs(33, 50).addBox(-3.68F, -12.252F, -3.68F, 7.36F, 12.502F, 7.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0892F, 0.1888F, -0.005F));

        PartDefinition leg_0_exposed_bone_r1 = leg_0_lower.addOrReplaceChild("leg_0_exposed_bone_r1", CubeListBuilder.create().texOffs(240, 31).addBox(-1.44F, -18.7147F, -1.6F, 2.88F, 18.9647F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0892F, 0.1888F, -0.005F));

        PartDefinition leg_0_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_joint_r1", CubeListBuilder.create().texOffs(0, 50).addBox(-3.84F, -0.8F, -3.84F, 7.68F, 1.5F, 7.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create().texOffs(144, 51).addBox(-4.0F, -0.4F, -4.0F, 8.0F, 1.2F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.09F, 18.44F, -0.95F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(-8.0F, -11.0F, 14.0F));

        PartDefinition leg_1_upper_r1 = leg_1.addOrReplaceChild("leg_1_upper_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-4.0F, -18.0445F, -4.0F, 8.0F, 18.2945F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.078F, 0.235F, -0.0075F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(-0.13F, 17.76F, -1.1F));

        PartDefinition leg_1_lower_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_lower_skin_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-3.8F, -18.7433F, -3.8F, 7.6F, 18.9933F, 7.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0652F, -0.1282F, -0.0049F));

        PartDefinition leg_1_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_joint_r1", CubeListBuilder.create().texOffs(0, 61).addBox(-3.84F, -0.8F, -3.84F, 7.68F, 1.5F, 7.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create().texOffs(144, 51).addBox(-4.0F, -0.4F, -4.0F, 8.0F, 1.2F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.09F, 18.44F, 1.4F));

        PartDefinition leg_2 = body.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(8.0F, -11.0F, -9.0F));

        PartDefinition leg_2_upper_r1 = leg_2.addOrReplaceChild("leg_2_upper_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-4.0F, -18.0285F, -4.0F, 8.0F, 18.2785F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0941F, 0.322F, 0.0077F));

        PartDefinition leg_2_lower = leg_2.addOrReplaceChild("leg_2_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 17.76F, 0.8F));

        PartDefinition leg_2_lower_skin_r1 = leg_2_lower.addOrReplaceChild("leg_2_lower_skin_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-3.8F, -18.7147F, -3.8F, 7.6F, 18.9647F, 7.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0892F, -0.1888F, 0.005F));

        PartDefinition leg_2_joint_r1 = leg_2_lower.addOrReplaceChild("leg_2_joint_r1", CubeListBuilder.create().texOffs(0, 61).addBox(-3.84F, -0.8F, -3.84F, 7.68F, 1.5F, 7.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_2_foot = leg_2_lower.addOrReplaceChild("leg_2_foot", CubeListBuilder.create().texOffs(144, 51).addBox(-4.0F, -0.4F, -4.0F, 8.0F, 1.2F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.09F, 18.44F, -0.95F));

        PartDefinition leg_3 = body.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(8.0F, -11.0F, 14.0F));

        PartDefinition leg_3_upper_r1 = leg_3.addOrReplaceChild("leg_3_upper_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-4.0F, -18.0445F, -4.0F, 8.0F, 18.2945F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.078F, -0.235F, 0.0075F));

        PartDefinition leg_3_lower = leg_3.addOrReplaceChild("leg_3_lower", CubeListBuilder.create(), PartPose.offset(0.13F, 17.76F, -1.1F));

        PartDefinition leg_3_lower_skin_r1 = leg_3_lower.addOrReplaceChild("leg_3_lower_skin_r1", CubeListBuilder.create().texOffs(207, 41).addBox(-3.8F, -18.7433F, -3.8F, 7.6F, 18.9933F, 7.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0652F, 0.1282F, 0.0049F));

        PartDefinition leg_3_joint_r1 = leg_3_lower.addOrReplaceChild("leg_3_joint_r1", CubeListBuilder.create().texOffs(0, 61).addBox(-3.84F, -0.8F, -3.84F, 7.68F, 1.5F, 7.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leg_3_foot = leg_3_lower.addOrReplaceChild("leg_3_foot", CubeListBuilder.create().texOffs(144, 51).addBox(-4.0F, -0.4F, -4.0F, 8.0F, 1.2F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.09F, 18.44F, 1.4F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(190, 51).addBox(-1.05F, -2.0F, -0.9F, 2.1F, 1.6F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4F, -9.5F, 10.0F, 0.1571F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(177, 51).addBox(-1.25F, -1.1F, -1.1F, 2.5F, 2.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(166, 25).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.66F, -1.75F, 0.0F, -0.0698F, 0.1396F, 0.0873F));

        PartDefinition sickle_0_tip_r1 = finger_0.addOrReplaceChild("sickle_0_tip_r1", CubeListBuilder.create().texOffs(240, 55).addBox(-0.1F, -3.3537F, -0.115F, 0.2F, 3.6037F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -7.04F, -3.52F, 0.9658F, -0.0254F, 0.0484F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(207, 34).addBox(-0.27F, -3.8049F, -0.295F, 0.54F, 4.0549F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -4.4F, -1.144F, 0.7321F, -0.0162F, 0.0422F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(201, 51).addBox(-0.425F, -4.8032F, -0.45F, 0.85F, 5.0532F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.254F, -0.007F, 0.0549F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(166, 25).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_tip_r1 = finger_1.addOrReplaceChild("sickle_1_tip_r1", CubeListBuilder.create().texOffs(207, 34).addBox(-0.1F, -4.1251F, -0.115F, 0.2F, 4.3751F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.8F, -4.4F, 0.967F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(207, 34).addBox(-0.27F, -4.6897F, -0.295F, 0.54F, 4.9397F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5F, -1.43F, 0.7328F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(245, 55).addBox(-0.425F, -5.9329F, -0.45F, 0.85F, 6.1829F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2544F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(166, 25).addBox(-0.48F, -0.5F, -0.46F, 0.96F, 0.92F, 0.94F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.66F, -1.75F, 0.0F, 0.0349F, -0.1396F, -0.0873F));

        PartDefinition sickle_2_tip_r1 = finger_2.addOrReplaceChild("sickle_2_tip_r1", CubeListBuilder.create().texOffs(240, 55).addBox(-0.1F, -3.3537F, -0.115F, 0.2F, 3.6037F, 0.23F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, -7.04F, -3.52F, 0.9658F, 0.0254F, -0.0484F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(207, 34).addBox(-0.27F, -3.8049F, -0.295F, 0.54F, 4.0549F, 0.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -4.4F, -1.144F, 0.7321F, 0.0162F, -0.0422F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(201, 51).addBox(-0.425F, -4.8032F, -0.45F, 0.85F, 5.0532F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.254F, 0.007F, -0.0549F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    private CorruptedVillageGeometry() {}
}
