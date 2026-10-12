package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/** Static geometry exported from the six editable Blockbench projects. */
final class CorruptedLivestockGeometry {
    static LayerDefinition pig() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.8F, -4.0F, -7.0F, 6.8F, 8.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(91, 0).addBox(-2.0F, -3.0F, -2.2F, 1.1F, 6.2F, 6.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition exposed_rib_2_taper_r1 = body.addOrReplaceChild("exposed_rib_2_taper_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.325F, -3.7403F, -0.325F, 0.65F, 3.9603F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, 2.43F, -0.045F, 0.3244F, 2.8685F));

        PartDefinition exposed_rib_2_upper_r1 = body.addOrReplaceChild("exposed_rib_2_upper_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.475F, -3.746F, -0.475F, 0.95F, 3.966F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.0F, 2.28F, -0.0427F, -0.0809F, -2.1711F));

        PartDefinition exposed_rib_1_taper_r1 = body.addOrReplaceChild("exposed_rib_1_taper_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.325F, -3.7403F, -0.325F, 0.65F, 3.9603F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, 0.91F, -0.045F, 0.3244F, 2.8685F));

        PartDefinition exposed_rib_1_upper_r1 = body.addOrReplaceChild("exposed_rib_1_upper_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.475F, -3.746F, -0.475F, 0.95F, 3.966F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.0F, 0.76F, -0.0427F, -0.0809F, -2.1711F));

        PartDefinition exposed_rib_0_taper_r1 = body.addOrReplaceChild("exposed_rib_0_taper_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.325F, -3.7403F, -0.325F, 0.65F, 3.9603F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, -0.61F, -0.045F, 0.3244F, 2.8685F));

        PartDefinition exposed_rib_0_upper_r1 = body.addOrReplaceChild("exposed_rib_0_upper_r1", CubeListBuilder.create().texOffs(110, 0).addBox(-0.475F, -3.746F, -0.475F, 0.95F, 3.966F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.0F, -0.76F, -0.0427F, -0.0809F, -2.1711F));

        PartDefinition uneven_haunch_r1 = body.addOrReplaceChild("uneven_haunch_r1", CubeListBuilder.create().texOffs(70, 0).addBox(-3.3F, -3.7F, -3.6F, 3.8F, 7.4F, 5.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, 7.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition rounded_shoulder_r1 = body.addOrReplaceChild("rounded_shoulder_r1", CubeListBuilder.create().texOffs(47, 0).addBox(-3.5F, -4.25F, -2.05F, 4.05F, 7.85F, 5.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, -5.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -6.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 6.75F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(76, 25).addBox(-2.0F, -0.64F, -9.1F, 4.0F, 2.1F, 1.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.24F, -1.271F, -0.23F, 0.48F, 1.491F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, 2.43F, -7.82F, 2.7337F, 0.286F, -0.0595F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, 2.43F, -7.82F, 2.5606F, -0.2741F, 0.0824F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, 2.43F, -7.82F, 2.5606F, 0.2741F, -0.0824F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, 2.43F, -7.82F, 2.5606F, -0.2741F, 0.0824F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, 2.43F, -7.82F, 2.5606F, 0.2741F, -0.0824F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.24F, -1.271F, -0.23F, 0.48F, 1.491F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, 2.43F, -7.82F, 2.7337F, -0.286F, 0.0595F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(64, 25).addBox(2.75F, -0.45F, -0.8F, 0.6F, 1.55F, 1.5F, new CubeDeformation(0.0F))
        .texOffs(64, 25).addBox(-3.35F, -0.45F, -0.8F, 0.6F, 1.55F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, -0.8F, -0.0524F, 0.0F, 0.0524F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.75F, 0.53F, -6.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -1.1392F, -0.22F, 0.38F, 1.3592F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3125F, 0.53F, -6.9F, 0.5116F, 0.0142F, -0.0544F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.125F, 0.53F, -6.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -1.1392F, -0.22F, 0.38F, 1.3592F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5625F, 0.53F, -6.9F, 0.5116F, 0.0142F, -0.0544F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(71, 25).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.53F, -6.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition broken_lower_jaw_r1 = jaw.addOrReplaceChild("broken_lower_jaw_r1", CubeListBuilder.create().texOffs(33, 25).addBox(-3.2F, 0.07F, -7.05F, 6.8F, 1.18F, 7.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-3.0F, 2.8F, -5.0F));

        PartDefinition leg_0_upper_skin_r1 = leg_0.addOrReplaceChild("leg_0_upper_skin_r1", CubeListBuilder.create().texOffs(89, 25).addBox(-1.85F, -4.2218F, -1.739F, 3.7F, 4.4418F, 3.478F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9411F, -0.4989F, -0.0512F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 3.936F, 0.7F));

        PartDefinition leg_0_torn_tendon_r1 = leg_0_lower.addOrReplaceChild("leg_0_torn_tendon_r1", CubeListBuilder.create().texOffs(13, 41).addBox(-0.4625F, -1.7831F, -0.4625F, 0.925F, 2.0031F, 0.925F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5124F, 1.9745F, -0.513F, 2.4551F, 1.1695F, -0.4647F));

        PartDefinition leg_0_remaining_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_remaining_skin_r1", CubeListBuilder.create().texOffs(0, 41).addBox(-1.332F, -2.2612F, -1.258F, 2.664F, 2.4812F, 2.516F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8785F, 0.2607F, -0.0347F));

        PartDefinition leg_0_exposed_shin_r1 = leg_0_lower.addOrReplaceChild("leg_0_exposed_shin_r1", CubeListBuilder.create().texOffs(119, 25).addBox(-0.6475F, -3.801F, -0.6845F, 1.295F, 4.021F, 1.369F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8785F, 0.2607F, -0.0347F));

        PartDefinition leg_0_asymmetric_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_asymmetric_joint_r1", CubeListBuilder.create().texOffs(106, 25).addBox(-1.554F, -0.75F, -1.332F, 2.96F, 1.4F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 3.464F, -0.9F));

        PartDefinition leg_0_split_hoof_1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(-1.48F, -0.45F, -1.776F, 1.4F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_0_split_hoof_m1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_m1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(0.06F, -0.45F, -1.776F, 1.42F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(-3.0F, 2.8F, 7.0F));

        PartDefinition leg_1_upper_skin_r1 = leg_1.addOrReplaceChild("leg_1_upper_skin_r1", CubeListBuilder.create().texOffs(89, 25).addBox(-1.85F, -4.2616F, -1.739F, 3.7F, 4.4816F, 3.478F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8985F, 0.3894F, -0.0482F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 3.936F, -0.9F));

        PartDefinition leg_1_lower_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_lower_skin_r1", CubeListBuilder.create().texOffs(44, 41).addBox(-1.295F, -3.8564F, -1.332F, 2.59F, 4.0764F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.827F, -0.2121F, -0.0338F));

        PartDefinition leg_1_asymmetric_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_asymmetric_joint_r1", CubeListBuilder.create().texOffs(31, 41).addBox(-1.554F, -0.75F, -1.332F, 2.96F, 1.4F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 3.464F, 1.1F));

        PartDefinition leg_1_split_hoof_1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(-1.48F, -0.45F, -1.776F, 1.4F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_1_split_hoof_m1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_m1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(0.06F, -0.45F, -1.776F, 1.42F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_2 = body.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(3.0F, 2.8F, -5.0F));

        PartDefinition leg_2_upper_skin_r1 = leg_2.addOrReplaceChild("leg_2_upper_skin_r1", CubeListBuilder.create().texOffs(89, 25).addBox(-1.85F, -4.2218F, -1.739F, 3.7F, 4.4418F, 3.478F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9411F, 0.4989F, 0.0512F));

        PartDefinition leg_2_lower = leg_2.addOrReplaceChild("leg_2_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 3.936F, 0.7F));

        PartDefinition leg_2_lower_skin_r1 = leg_2_lower.addOrReplaceChild("leg_2_lower_skin_r1", CubeListBuilder.create().texOffs(44, 41).addBox(-1.295F, -3.801F, -1.332F, 2.59F, 4.021F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8785F, -0.2607F, 0.0347F));

        PartDefinition leg_2_asymmetric_joint_r1 = leg_2_lower.addOrReplaceChild("leg_2_asymmetric_joint_r1", CubeListBuilder.create().texOffs(31, 41).addBox(-1.554F, -0.75F, -1.332F, 2.96F, 1.4F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_2_foot = leg_2_lower.addOrReplaceChild("leg_2_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 3.464F, -0.9F));

        PartDefinition leg_2_split_hoof_1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(-1.48F, -0.45F, -1.776F, 1.4F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_2_split_hoof_m1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_m1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(0.06F, -0.45F, -1.776F, 1.42F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_3 = body.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(3.0F, 2.8F, 7.0F));

        PartDefinition leg_3_upper_skin_r1 = leg_3.addOrReplaceChild("leg_3_upper_skin_r1", CubeListBuilder.create().texOffs(89, 25).addBox(-1.85F, -4.2616F, -1.739F, 3.7F, 4.4816F, 3.478F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8985F, -0.3894F, 0.0482F));

        PartDefinition leg_3_lower = leg_3.addOrReplaceChild("leg_3_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 3.936F, -0.9F));

        PartDefinition leg_3_lower_skin_r1 = leg_3_lower.addOrReplaceChild("leg_3_lower_skin_r1", CubeListBuilder.create().texOffs(44, 41).addBox(-1.295F, -3.8564F, -1.332F, 2.59F, 4.0764F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.827F, 0.2121F, 0.0338F));

        PartDefinition leg_3_asymmetric_joint_r1 = leg_3_lower.addOrReplaceChild("leg_3_asymmetric_joint_r1", CubeListBuilder.create().texOffs(31, 41).addBox(-1.554F, -0.75F, -1.332F, 2.96F, 1.4F, 2.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_3_foot = leg_3_lower.addOrReplaceChild("leg_3_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 3.464F, 1.1F));

        PartDefinition leg_3_split_hoof_1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(-1.48F, -0.45F, -1.776F, 1.4F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_3_split_hoof_m1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_m1_r1", CubeListBuilder.create().texOffs(18, 41).addBox(0.06F, -0.45F, -1.776F, 1.42F, 1.25F, 3.0784F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 8.5F, -0.1571F, 0.0F, 0.0F));

        PartDefinition torn_tail_tip_r1 = tail.addOrReplaceChild("torn_tail_tip_r1", CubeListBuilder.create().texOffs(62, 41).addBox(-0.275F, -2.5801F, -0.3F, 0.55F, 2.8001F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 1.9F, -2.6872F, -0.6883F, -0.1653F));

        PartDefinition tail_root_r1 = tail.addOrReplaceChild("tail_root_r1", CubeListBuilder.create().texOffs(57, 41).addBox(-0.45F, -3.0923F, -0.45F, 0.9F, 3.3123F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1F, -2.2792F, -0.4106F, -0.191F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(82, 41).addBox(-1.25F, -2.5F, -0.95F, 2.5F, 1.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -3.7F, 5.16F, 0.192F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(67, 41).addBox(-1.5F, -1.3F, -1.3F, 3.1F, 2.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(93, 41).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -2.1F, 0.1F, -0.0698F, 0.1222F, 0.0873F));

        PartDefinition sickle_0_point_r1 = finger_0.addOrReplaceChild("sickle_0_point_r1", CubeListBuilder.create().texOffs(62, 41).addBox(-0.12F, -1.9043F, -0.13F, 0.24F, 2.1243F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.1328F, -1.9152F, 0.9951F, -0.0485F, 0.0893F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(62, 41).addBox(-0.35F, -2.313F, -0.35F, 0.7F, 2.533F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -2.52F, -0.6048F, 0.6772F, -0.0422F, 0.1198F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(102, 41).addBox(-0.525F, -2.8236F, -0.525F, 1.05F, 3.0436F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2345F, -0.0113F, 0.0962F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(93, 41).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 0.1F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_point_r1 = finger_1.addOrReplaceChild("sickle_1_point_r1", CubeListBuilder.create().texOffs(62, 41).addBox(-0.12F, -2.3171F, -0.13F, 0.24F, 2.5371F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.166F, -2.394F, 0.9995F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(111, 41).addBox(-0.35F, -2.8176F, -0.35F, 0.7F, 3.0376F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.15F, -0.756F, 0.6823F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(102, 41).addBox(-0.525F, -3.4594F, -0.525F, 1.05F, 3.6794F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2355F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(93, 41).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, -2.1F, 0.1F, 0.0349F, -0.1222F, -0.0873F));

        PartDefinition sickle_2_point_r1 = finger_2.addOrReplaceChild("sickle_2_point_r1", CubeListBuilder.create().texOffs(62, 41).addBox(-0.12F, -1.9043F, -0.13F, 0.24F, 2.1243F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -4.1328F, -1.9152F, 0.9951F, 0.0485F, -0.0893F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(62, 41).addBox(-0.35F, -2.313F, -0.35F, 0.7F, 2.533F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -2.52F, -0.6048F, 0.6772F, 0.0422F, -0.1198F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(102, 41).addBox(-0.525F, -2.8236F, -0.525F, 1.05F, 3.0436F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2345F, 0.0113F, -0.0962F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition cow() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.16F, -5.0F, -8.0F, 8.16F, 10.0F, 18.0F, new CubeDeformation(0.0F))
        .texOffs(105, 0).addBox(-2.4F, -4.0F, -2.6F, 1.32F, 8.2F, 7.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition udder_r1 = body.addOrReplaceChild("udder_r1", CubeListBuilder.create().texOffs(106, 29).addBox(-1.6F, 4.0F, 3.0F, 3.2F, 1.8F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition exposed_rib_2_taper_r1 = body.addOrReplaceChild("exposed_rib_2_taper_r1", CubeListBuilder.create().texOffs(5, 29).addBox(-0.325F, -4.7531F, -0.325F, 0.65F, 4.9731F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.28F, -1.0F, 2.59F, -0.0344F, 0.2719F, 2.8917F));

        PartDefinition exposed_rib_2_upper_r1 = body.addOrReplaceChild("exposed_rib_2_upper_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-0.475F, -4.8171F, -0.475F, 0.95F, 5.0371F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, -4.0F, 2.44F, -0.0327F, -0.0712F, -2.2799F));

        PartDefinition exposed_rib_1_taper_r1 = body.addOrReplaceChild("exposed_rib_1_taper_r1", CubeListBuilder.create().texOffs(5, 29).addBox(-0.325F, -4.7531F, -0.325F, 0.65F, 4.9731F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.28F, -1.0F, 0.88F, -0.0344F, 0.2719F, 2.8917F));

        PartDefinition exposed_rib_1_upper_r1 = body.addOrReplaceChild("exposed_rib_1_upper_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-0.475F, -4.8171F, -0.475F, 0.95F, 5.0371F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, -4.0F, 0.73F, -0.0327F, -0.0712F, -2.2799F));

        PartDefinition exposed_rib_0_taper_r1 = body.addOrReplaceChild("exposed_rib_0_taper_r1", CubeListBuilder.create().texOffs(5, 29).addBox(-0.325F, -4.7531F, -0.325F, 0.65F, 4.9731F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.28F, -1.0F, -0.83F, -0.0344F, 0.2719F, 2.8917F));

        PartDefinition exposed_rib_0_upper_r1 = body.addOrReplaceChild("exposed_rib_0_upper_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-0.475F, -4.8171F, -0.475F, 0.95F, 5.0371F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, -4.0F, -0.98F, -0.0327F, -0.0712F, -2.2799F));

        PartDefinition uneven_haunch_r1 = body.addOrReplaceChild("uneven_haunch_r1", CubeListBuilder.create().texOffs(80, 0).addBox(-3.96F, -4.7F, -4.3F, 4.56F, 9.4F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 0.0F, 8.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition rounded_shoulder_r1 = body.addOrReplaceChild("rounded_shoulder_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-4.2F, -5.25F, -2.05F, 4.86F, 9.85F, 6.17F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 0.0F, -6.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -8.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(10, 29).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 6.75F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cow_ear_1_r1 = head.addOrReplaceChild("cow_ear_1_r1", CubeListBuilder.create().texOffs(92, 29).addBox(-0.75F, -2.726F, -0.75F, 1.5F, 2.946F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4F, -3.0F, -1.5F, -0.1637F, -0.2024F, -1.7826F));

        PartDefinition sharp_horn_r1 = head.addOrReplaceChild("sharp_horn_r1", CubeListBuilder.create().texOffs(101, 29).addBox(-0.3F, -2.7871F, -0.3F, 0.6F, 3.0071F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7F, -5.4F, -3.3F, 0.1171F, -0.0115F, 0.196F));

        PartDefinition cow_horn_root_1_r1 = head.addOrReplaceChild("cow_horn_root_1_r1", CubeListBuilder.create().texOffs(78, 29).addBox(-0.65F, -2.4359F, -0.65F, 1.3F, 2.6559F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -3.5F, -3.0F, 0.1359F, 0.0362F, -0.5198F));

        PartDefinition cow_ear_m1_r1 = head.addOrReplaceChild("cow_ear_m1_r1", CubeListBuilder.create().texOffs(92, 29).addBox(-0.75F, -2.726F, -0.75F, 1.5F, 2.946F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4F, -3.0F, -1.5F, -0.1637F, 0.2024F, 1.7826F));

        PartDefinition snapped_horn_r1 = head.addOrReplaceChild("snapped_horn_r1", CubeListBuilder.create().texOffs(87, 29).addBox(-0.5F, -0.8757F, -0.5F, 1.0F, 1.0957F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7F, -5.4F, -3.3F, 0.4906F, 0.2417F, -0.9032F));

        PartDefinition cow_horn_root_m1_r1 = head.addOrReplaceChild("cow_horn_root_m1_r1", CubeListBuilder.create().texOffs(78, 29).addBox(-0.65F, -2.4359F, -0.65F, 1.3F, 2.6559F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, -3.5F, -3.0F, 0.1359F, -0.0362F, 0.5198F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.24F, -1.271F, -0.23F, 0.48F, 1.491F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, 2.43F, -5.82F, 2.7337F, 0.286F, -0.0595F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.86F, 2.43F, -5.82F, 2.5606F, -0.2741F, 0.0824F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.62F, 2.43F, -5.82F, 2.5606F, 0.2741F, -0.0824F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.62F, 2.43F, -5.82F, 2.5606F, -0.2741F, 0.0824F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.86F, 2.43F, -5.82F, 2.5606F, 0.2741F, -0.0824F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.24F, -1.271F, -0.23F, 0.48F, 1.491F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, 2.43F, -5.82F, 2.7337F, -0.286F, 0.0595F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(66, 29).addBox(2.75F, -0.45F, -0.8F, 0.6F, 1.55F, 1.5F, new CubeDeformation(0.0F))
        .texOffs(66, 29).addBox(-3.35F, -0.45F, -0.8F, 0.6F, 1.55F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, -0.8F, -0.0524F, 0.0F, -0.0349F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.75F, 0.53F, -4.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -1.1392F, -0.22F, 0.38F, 1.3592F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3125F, 0.53F, -4.9F, 0.5116F, 0.0142F, -0.0544F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.125F, 0.53F, -4.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -1.1392F, -0.22F, 0.38F, 1.3592F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5625F, 0.53F, -4.9F, 0.5116F, 0.0142F, -0.0544F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(73, 29).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.53F, -4.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition broken_lower_jaw_r1 = jaw.addOrReplaceChild("broken_lower_jaw_r1", CubeListBuilder.create().texOffs(39, 29).addBox(-3.2F, 0.07F, -5.05F, 6.8F, 1.18F, 5.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-4.0F, 3.5F, -6.0F));

        PartDefinition leg_0_upper_skin_r1 = leg_0.addOrReplaceChild("leg_0_upper_skin_r1", CubeListBuilder.create().texOffs(0, 43).addBox(-1.9F, -6.2634F, -1.786F, 3.8F, 6.4834F, 3.572F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0091F, -0.5014F, -0.034F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 6.0F, 0.7F));

        PartDefinition leg_0_torn_tendon_r1 = leg_0_lower.addOrReplaceChild("leg_0_torn_tendon_r1", CubeListBuilder.create().texOffs(39, 43).addBox(-0.475F, -2.7171F, -0.475F, 0.95F, 2.9371F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5244F, 3.249F, -0.513F, 2.6674F, 1.2245F, -0.3362F));

        PartDefinition leg_0_remaining_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_remaining_skin_r1", CubeListBuilder.create().texOffs(26, 43).addBox(-1.368F, -3.51F, -1.292F, 2.736F, 3.73F, 2.584F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9794F, 0.2634F, -0.0215F));

        PartDefinition leg_0_exposed_shin_r1 = leg_0_lower.addOrReplaceChild("leg_0_exposed_shin_r1", CubeListBuilder.create().texOffs(17, 43).addBox(-0.665F, -5.9919F, -0.703F, 1.33F, 6.2119F, 1.406F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9794F, 0.2634F, -0.0215F));

        PartDefinition leg_0_asymmetric_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_asymmetric_joint_r1", CubeListBuilder.create().texOffs(106, 29).addBox(-1.596F, -0.75F, -1.368F, 3.04F, 1.4F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 5.7F, -0.9F));

        PartDefinition leg_0_split_hoof_1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(-1.52F, -0.45F, -1.824F, 1.44F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_0_split_hoof_m1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_m1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(0.06F, -0.45F, -1.824F, 1.46F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(-4.0F, 3.5F, 7.0F));

        PartDefinition leg_1_upper_skin_r1 = leg_1.addOrReplaceChild("leg_1_upper_skin_r1", CubeListBuilder.create().texOffs(0, 43).addBox(-1.9F, -6.2898F, -1.786F, 3.8F, 6.5098F, 3.572F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9804F, 0.3924F, -0.0321F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 6.0F, -0.9F));

        PartDefinition leg_1_lower_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_lower_skin_r1", CubeListBuilder.create().texOffs(72, 43).addBox(-1.33F, -6.0264F, -1.368F, 2.66F, 6.2464F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9464F, -0.2153F, -0.0212F));

        PartDefinition leg_1_asymmetric_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_asymmetric_joint_r1", CubeListBuilder.create().texOffs(57, 43).addBox(-1.596F, -0.75F, -1.368F, 3.04F, 1.4F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 5.7F, 1.1F));

        PartDefinition leg_1_split_hoof_1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(-1.52F, -0.45F, -1.824F, 1.44F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_1_split_hoof_m1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_m1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(0.06F, -0.45F, -1.824F, 1.46F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_2 = body.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(4.0F, 3.5F, -6.0F));

        PartDefinition leg_2_upper_skin_r1 = leg_2.addOrReplaceChild("leg_2_upper_skin_r1", CubeListBuilder.create().texOffs(0, 43).addBox(-1.9F, -6.2634F, -1.786F, 3.8F, 6.4834F, 3.572F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0091F, 0.5014F, 0.034F));

        PartDefinition leg_2_lower = leg_2.addOrReplaceChild("leg_2_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 6.0F, 0.7F));

        PartDefinition leg_2_lower_skin_r1 = leg_2_lower.addOrReplaceChild("leg_2_lower_skin_r1", CubeListBuilder.create().texOffs(72, 43).addBox(-1.33F, -5.9919F, -1.368F, 2.66F, 6.2119F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9794F, -0.2634F, 0.0215F));

        PartDefinition leg_2_asymmetric_joint_r1 = leg_2_lower.addOrReplaceChild("leg_2_asymmetric_joint_r1", CubeListBuilder.create().texOffs(57, 43).addBox(-1.596F, -0.75F, -1.368F, 3.04F, 1.4F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_2_foot = leg_2_lower.addOrReplaceChild("leg_2_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 5.7F, -0.9F));

        PartDefinition leg_2_split_hoof_1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(-1.52F, -0.45F, -1.824F, 1.44F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_2_split_hoof_m1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_m1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(0.06F, -0.45F, -1.824F, 1.46F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_3 = body.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(4.0F, 3.5F, 7.0F));

        PartDefinition leg_3_upper_skin_r1 = leg_3.addOrReplaceChild("leg_3_upper_skin_r1", CubeListBuilder.create().texOffs(0, 43).addBox(-1.9F, -6.2898F, -1.786F, 3.8F, 6.5098F, 3.572F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9804F, -0.3924F, 0.0321F));

        PartDefinition leg_3_lower = leg_3.addOrReplaceChild("leg_3_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 6.0F, -0.9F));

        PartDefinition leg_3_lower_skin_r1 = leg_3_lower.addOrReplaceChild("leg_3_lower_skin_r1", CubeListBuilder.create().texOffs(72, 43).addBox(-1.33F, -6.0264F, -1.368F, 2.66F, 6.2464F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9464F, 0.2153F, 0.0212F));

        PartDefinition leg_3_asymmetric_joint_r1 = leg_3_lower.addOrReplaceChild("leg_3_asymmetric_joint_r1", CubeListBuilder.create().texOffs(57, 43).addBox(-1.596F, -0.75F, -1.368F, 3.04F, 1.4F, 2.736F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_3_foot = leg_3_lower.addOrReplaceChild("leg_3_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 5.7F, 1.1F));

        PartDefinition leg_3_split_hoof_1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(-1.52F, -0.45F, -1.824F, 1.44F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_3_split_hoof_m1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_m1_r1", CubeListBuilder.create().texOffs(44, 43).addBox(0.06F, -0.45F, -1.824F, 1.46F, 1.25F, 3.1616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 9.5F, -0.1571F, 0.0F, 0.0F));

        PartDefinition torn_tail_tip_r1 = tail.addOrReplaceChild("torn_tail_tip_r1", CubeListBuilder.create().texOffs(90, 43).addBox(-0.275F, -2.5801F, -0.3F, 0.55F, 2.8001F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 1.9F, -2.6872F, -0.6883F, -0.1653F));

        PartDefinition tail_root_r1 = tail.addOrReplaceChild("tail_root_r1", CubeListBuilder.create().texOffs(85, 43).addBox(-0.45F, -3.0923F, -0.45F, 0.9F, 3.3123F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1F, -2.2792F, -0.4106F, -0.191F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(110, 43).addBox(-1.25F, -2.5F, -0.95F, 2.5F, 1.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -4.7F, 5.68F, 0.192F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(95, 43).addBox(-1.5F, -1.3F, -1.3F, 3.1F, 2.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(0, 55).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -2.1F, 0.1F, -0.0698F, 0.1222F, 0.0873F));

        PartDefinition sickle_0_point_r1 = finger_0.addOrReplaceChild("sickle_0_point_r1", CubeListBuilder.create().texOffs(90, 43).addBox(-0.12F, -2.5682F, -0.13F, 0.24F, 2.7882F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -5.7728F, -2.6752F, 0.9972F, -0.0348F, 0.064F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(101, 29).addBox(-0.35F, -3.1334F, -0.35F, 0.7F, 3.3534F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -3.52F, -0.8448F, 0.6797F, -0.0304F, 0.086F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(9, 55).addBox(-0.525F, -3.8486F, -0.525F, 1.05F, 4.0686F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.235F, -0.0081F, 0.069F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(0, 55).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 0.1F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_point_r1 = finger_1.addOrReplaceChild("sickle_1_point_r1", CubeListBuilder.create().texOffs(101, 29).addBox(-0.12F, -3.1492F, -0.13F, 0.24F, 3.3692F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.216F, -3.344F, 0.9995F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(18, 55).addBox(-0.35F, -3.8483F, -0.35F, 0.7F, 4.0683F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, -1.056F, 0.6823F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(9, 55).addBox(-0.525F, -4.7449F, -0.525F, 1.05F, 4.9649F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2355F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(0, 55).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, -2.1F, 0.1F, 0.0349F, -0.1222F, -0.0873F));

        PartDefinition sickle_2_point_r1 = finger_2.addOrReplaceChild("sickle_2_point_r1", CubeListBuilder.create().texOffs(90, 43).addBox(-0.12F, -2.5682F, -0.13F, 0.24F, 2.7882F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -5.7728F, -2.6752F, 0.9972F, 0.0348F, -0.064F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(101, 29).addBox(-0.35F, -3.1334F, -0.35F, 0.7F, 3.3534F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -3.52F, -0.8448F, 0.6797F, 0.0304F, -0.086F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(9, 55).addBox(-0.525F, -3.8486F, -0.525F, 1.05F, 4.0686F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.235F, 0.0081F, -0.069F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition sheep() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.44F, -3.0F, -8.0F, 5.44F, 6.0F, 16.0F, new CubeDeformation(0.0F))
        .texOffs(87, 0).addBox(-1.6F, -2.0F, -3.2F, 0.88F, 4.2F, 6.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition exposed_rib_2_taper_r1 = body.addOrReplaceChild("exposed_rib_2_taper_r1", CubeListBuilder.create().texOffs(104, 0).addBox(-0.325F, -2.7302F, -0.325F, 0.65F, 2.9502F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.52F, -1.0F, 1.43F, -0.065F, 0.4013F, 2.8247F));

        PartDefinition exposed_rib_2_upper_r1 = body.addOrReplaceChild("exposed_rib_2_upper_r1", CubeListBuilder.create().texOffs(104, 0).addBox(-0.475F, -2.7508F, -0.475F, 0.95F, 2.9708F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -2.0F, 1.28F, -0.0595F, -0.0899F, -1.9724F));

        PartDefinition exposed_rib_1_taper_r1 = body.addOrReplaceChild("exposed_rib_1_taper_r1", CubeListBuilder.create().texOffs(104, 0).addBox(-0.325F, -2.7302F, -0.325F, 0.65F, 2.9502F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.52F, -1.0F, -0.09F, -0.065F, 0.4013F, 2.8247F));

        PartDefinition exposed_rib_1_upper_r1 = body.addOrReplaceChild("exposed_rib_1_upper_r1", CubeListBuilder.create().texOffs(104, 0).addBox(-0.475F, -2.7508F, -0.475F, 0.95F, 2.9708F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -2.0F, -0.24F, -0.0595F, -0.0899F, -1.9724F));

        PartDefinition exposed_rib_0_taper_r1 = body.addOrReplaceChild("exposed_rib_0_taper_r1", CubeListBuilder.create().texOffs(104, 0).addBox(-0.325F, -2.7302F, -0.325F, 0.65F, 2.9502F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.52F, -1.0F, -1.61F, -0.065F, 0.4013F, 2.8247F));

        PartDefinition exposed_rib_0_upper_r1 = body.addOrReplaceChild("exposed_rib_0_upper_r1", CubeListBuilder.create().texOffs(104, 0).addBox(-0.475F, -2.7508F, -0.475F, 0.95F, 2.9708F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -2.0F, -1.76F, -0.0595F, -0.0899F, -1.9724F));

        PartDefinition uneven_haunch_r1 = body.addOrReplaceChild("uneven_haunch_r1", CubeListBuilder.create().texOffs(66, 0).addBox(-2.64F, -2.7F, -3.6F, 3.04F, 5.4F, 5.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.0F, 6.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition rounded_shoulder_r1 = body.addOrReplaceChild("rounded_shoulder_r1", CubeListBuilder.create().texOffs(45, 0).addBox(-2.8F, -3.25F, -2.05F, 3.24F, 5.85F, 5.49F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.0F, -6.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -8.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 23).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 5.1F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition sheep_ear_1_r1 = head.addOrReplaceChild("sheep_ear_1_r1", CubeListBuilder.create().texOffs(68, 23).addBox(-0.65F, -2.1847F, -0.75F, 1.3F, 2.4047F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -3.0F, 0.0F, -0.156F, -0.1865F, -1.7491F));

        PartDefinition sheep_ear_m1_r1 = head.addOrReplaceChild("sheep_ear_m1_r1", CubeListBuilder.create().texOffs(68, 23).addBox(-0.65F, -2.1847F, -0.75F, 1.3F, 2.4047F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4F, -3.0F, 0.0F, -0.156F, 0.1865F, 1.7491F));

        PartDefinition upper_tooth_5_r1 = head.addOrReplaceChild("upper_tooth_5_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.24F, -1.271F, -0.23F, 0.48F, 1.491F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1F, 0.78F, -5.82F, 2.7337F, 0.286F, -0.0595F));

        PartDefinition upper_tooth_4_r1 = head.addOrReplaceChild("upper_tooth_4_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.26F, 0.78F, -5.82F, 2.5606F, -0.2741F, 0.0824F));

        PartDefinition upper_tooth_3_r1 = head.addOrReplaceChild("upper_tooth_3_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.42F, 0.78F, -5.82F, 2.5606F, 0.2741F, -0.0824F));

        PartDefinition upper_tooth_2_r1 = head.addOrReplaceChild("upper_tooth_2_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.42F, 0.78F, -5.82F, 2.5606F, -0.2741F, 0.0824F));

        PartDefinition upper_tooth_1_r1 = head.addOrReplaceChild("upper_tooth_1_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -0.9771F, -0.23F, 0.38F, 1.1971F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.26F, 0.78F, -5.82F, 2.5606F, 0.2741F, -0.0824F));

        PartDefinition upper_tooth_0_r1 = head.addOrReplaceChild("upper_tooth_0_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.24F, -1.271F, -0.23F, 0.48F, 1.491F, 0.46F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1F, 0.78F, -5.82F, 2.7337F, -0.286F, 0.0595F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(56, 23).addBox(1.75F, -0.45F, -0.8F, 0.6F, 1.2F, 1.5F, new CubeDeformation(0.0F))
        .texOffs(56, 23).addBox(-2.35F, -0.45F, -0.8F, 0.6F, 1.2F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.85F, 1.2F, -0.0524F, 0.0F, -0.0349F));

        PartDefinition lower_tooth_4_r1 = jaw.addOrReplaceChild("lower_tooth_4_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, 0.53F, -6.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition lower_tooth_3_r1 = jaw.addOrReplaceChild("lower_tooth_3_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -1.1392F, -0.22F, 0.38F, 1.3592F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8125F, 0.53F, -6.9F, 0.5116F, 0.0142F, -0.0544F));

        PartDefinition lower_tooth_2_r1 = jaw.addOrReplaceChild("lower_tooth_2_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.125F, 0.53F, -6.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition lower_tooth_1_r1 = jaw.addOrReplaceChild("lower_tooth_1_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -1.1392F, -0.22F, 0.38F, 1.3592F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0625F, 0.53F, -6.9F, 0.5116F, 0.0142F, -0.0544F));

        PartDefinition lower_tooth_0_r1 = jaw.addOrReplaceChild("lower_tooth_0_r1", CubeListBuilder.create().texOffs(63, 23).addBox(-0.19F, -1.0537F, -0.22F, 0.38F, 1.2737F, 0.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.53F, -6.9F, 0.5703F, 0.0176F, -0.06F));

        PartDefinition broken_lower_jaw_r1 = jaw.addOrReplaceChild("broken_lower_jaw_r1", CubeListBuilder.create().texOffs(29, 23).addBox(-2.2F, 0.07F, -7.05F, 4.8F, 0.83F, 7.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition head_wool = head.addOrReplaceChild("head_wool", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition ragged_head_fleece_r1 = head_wool.addOrReplaceChild("ragged_head_fleece_r1", CubeListBuilder.create().texOffs(67, 41).addBox(-3.2F, -4.8F, -2.0F, 6.5F, 1.4F, 4.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0698F));

        PartDefinition wool = body.addOrReplaceChild("wool", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition ragged_fleece_edge_2_r1 = wool.addOrReplaceChild("ragged_fleece_edge_2_r1", CubeListBuilder.create().texOffs(58, 41).addBox(-2.2F, -3.5F, -1.0F, 1.4F, 1.2F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3316F));

        PartDefinition ragged_fleece_edge_1_r1 = wool.addOrReplaceChild("ragged_fleece_edge_1_r1", CubeListBuilder.create().texOffs(58, 41).addBox(-2.5F, -3.5F, -3.0F, 1.7F, 1.2F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2443F));

        PartDefinition ragged_fleece_edge_0_r1 = wool.addOrReplaceChild("ragged_fleece_edge_0_r1", CubeListBuilder.create().texOffs(58, 41).addBox(-2.2F, -3.5F, -5.0F, 1.4F, 1.2F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition rear_fleece_lobe_r1 = wool.addOrReplaceChild("rear_fleece_lobe_r1", CubeListBuilder.create().texOffs(37, 41).addBox(-4.7F, -4.2F, 3.8F, 4.18F, 6.8F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1047F, 0.0F, 0.0873F));

        PartDefinition uneven_left_fleece_r1 = wool.addOrReplaceChild("uneven_left_fleece_r1", CubeListBuilder.create().texOffs(0, 41).addBox(3.4F, -3.3F, -7.7F, 1.7F, 6.7F, 15.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0698F));

        PartDefinition torn_fleece_back_r1 = wool.addOrReplaceChild("torn_fleece_back_r1", CubeListBuilder.create().texOffs(77, 23).addBox(-2.4F, -4.6F, -7.0F, 7.3F, 2.1F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0349F, 0.0F, 0.0F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-3.0F, 2.6F, -5.0F));

        PartDefinition leg_0_upper_skin_r1 = leg_0.addOrReplaceChild("leg_0_upper_skin_r1", CubeListBuilder.create().texOffs(92, 41).addBox(-1.2F, -7.1697F, -1.128F, 2.4F, 7.3897F, 2.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0264F, -0.5019F, -0.0295F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 6.912F, 0.7F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(114, 41).addBox(-0.84F, -6.9694F, -0.864F, 1.68F, 7.1894F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.003F, 0.2639F, -0.0184F));

        PartDefinition leg_0_asymmetric_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_asymmetric_joint_r1", CubeListBuilder.create().texOffs(105, 41).addBox(-1.008F, -0.75F, -0.864F, 1.92F, 1.4F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 6.688F, -0.9F));

        PartDefinition leg_0_split_hoof_1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(-0.96F, -0.45F, -1.152F, 0.88F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_0_split_hoof_m1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_m1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(0.06F, -0.45F, -1.152F, 0.9F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(-3.0F, 2.6F, 6.0F));

        PartDefinition leg_1_upper_skin_r1 = leg_1.addOrReplaceChild("leg_1_upper_skin_r1", CubeListBuilder.create().texOffs(92, 41).addBox(-1.2F, -7.1927F, -1.128F, 2.4F, 7.4127F, 2.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0014F, 0.393F, -0.0279F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 6.912F, -0.9F));

        PartDefinition leg_1_lower_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_lower_skin_r1", CubeListBuilder.create().texOffs(114, 41).addBox(-0.84F, -6.9989F, -0.864F, 1.68F, 7.2189F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9747F, -0.2158F, -0.0181F));

        PartDefinition leg_1_asymmetric_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_asymmetric_joint_r1", CubeListBuilder.create().texOffs(105, 41).addBox(-1.008F, -0.75F, -0.864F, 1.92F, 1.4F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 6.688F, 1.1F));

        PartDefinition leg_1_split_hoof_1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(-0.96F, -0.45F, -1.152F, 0.88F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_1_split_hoof_m1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_m1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(0.06F, -0.45F, -1.152F, 0.9F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_2 = body.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(3.0F, 2.6F, -5.0F));

        PartDefinition leg_2_upper_skin_r1 = leg_2.addOrReplaceChild("leg_2_upper_skin_r1", CubeListBuilder.create().texOffs(92, 41).addBox(-1.2F, -7.1697F, -1.128F, 2.4F, 7.3897F, 2.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.0264F, 0.5019F, 0.0295F));

        PartDefinition leg_2_lower = leg_2.addOrReplaceChild("leg_2_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 6.912F, 0.7F));

        PartDefinition leg_2_torn_tendon_r1 = leg_2_lower.addOrReplaceChild("leg_2_torn_tendon_r1", CubeListBuilder.create().texOffs(30, 65).addBox(-0.3F, -3.1232F, -0.3F, 0.6F, 3.3432F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2196F, 3.8122F, -0.513F, 2.9918F, 0.4664F, -0.0356F));

        PartDefinition leg_2_remaining_skin_r1 = leg_2_lower.addOrReplaceChild("leg_2_remaining_skin_r1", CubeListBuilder.create().texOffs(21, 65).addBox(-0.864F, -4.0671F, -0.816F, 1.728F, 4.2871F, 1.632F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.003F, -0.2639F, 0.0184F));

        PartDefinition leg_2_exposed_shin_r1 = leg_2_lower.addOrReplaceChild("leg_2_exposed_shin_r1", CubeListBuilder.create().texOffs(16, 65).addBox(-0.42F, -6.9694F, -0.444F, 0.84F, 7.1894F, 0.888F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.003F, -0.2639F, 0.0184F));

        PartDefinition leg_2_asymmetric_joint_r1 = leg_2_lower.addOrReplaceChild("leg_2_asymmetric_joint_r1", CubeListBuilder.create().texOffs(7, 65).addBox(-1.008F, -0.75F, -0.864F, 1.92F, 1.4F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_2_foot = leg_2_lower.addOrReplaceChild("leg_2_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 6.688F, -0.9F));

        PartDefinition leg_2_split_hoof_1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(-0.96F, -0.45F, -1.152F, 0.88F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_2_split_hoof_m1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_m1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(0.06F, -0.45F, -1.152F, 0.9F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_3 = body.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(3.0F, 2.6F, 6.0F));

        PartDefinition leg_3_upper_skin_r1 = leg_3.addOrReplaceChild("leg_3_upper_skin_r1", CubeListBuilder.create().texOffs(92, 41).addBox(-1.2F, -7.1927F, -1.128F, 2.4F, 7.4127F, 2.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.0014F, -0.393F, 0.0279F));

        PartDefinition leg_3_lower = leg_3.addOrReplaceChild("leg_3_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 6.912F, -0.9F));

        PartDefinition leg_3_lower_skin_r1 = leg_3_lower.addOrReplaceChild("leg_3_lower_skin_r1", CubeListBuilder.create().texOffs(114, 41).addBox(-0.84F, -6.9989F, -0.864F, 1.68F, 7.2189F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9747F, 0.2158F, 0.0181F));

        PartDefinition leg_3_asymmetric_joint_r1 = leg_3_lower.addOrReplaceChild("leg_3_asymmetric_joint_r1", CubeListBuilder.create().texOffs(105, 41).addBox(-1.008F, -0.75F, -0.864F, 1.92F, 1.4F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_3_foot = leg_3_lower.addOrReplaceChild("leg_3_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 6.688F, 1.1F));

        PartDefinition leg_3_split_hoof_1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(-0.96F, -0.45F, -1.152F, 0.88F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_3_split_hoof_m1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_m1_r1", CubeListBuilder.create().texOffs(0, 65).addBox(0.06F, -0.45F, -1.152F, 0.9F, 1.25F, 1.9968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 7.5F, -0.1571F, 0.0F, 0.0F));

        PartDefinition torn_tail_tip_r1 = tail.addOrReplaceChild("torn_tail_tip_r1", CubeListBuilder.create().texOffs(40, 65).addBox(-0.275F, -2.5801F, -0.3F, 0.55F, 2.8001F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 1.9F, -2.6872F, -0.6883F, -0.1653F));

        PartDefinition tail_root_r1 = tail.addOrReplaceChild("tail_root_r1", CubeListBuilder.create().texOffs(35, 65).addBox(-0.45F, -3.0923F, -0.45F, 0.9F, 3.3123F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1F, -2.2792F, -0.4106F, -0.191F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(60, 65).addBox(-1.25F, -2.5F, -0.95F, 2.5F, 1.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6F, -2.7F, 4.16F, 0.192F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(45, 65).addBox(-1.5F, -1.3F, -1.3F, 3.1F, 2.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(7, 65).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -2.1F, 0.1F, -0.0698F, 0.1222F, 0.0873F));

        PartDefinition sickle_0_point_r1 = finger_0.addOrReplaceChild("sickle_0_point_r1", CubeListBuilder.create().texOffs(40, 65).addBox(-0.12F, -2.1697F, -0.13F, 0.24F, 2.3897F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.7888F, -2.2192F, 0.9962F, -0.0419F, 0.0771F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(40, 65).addBox(-0.35F, -2.6408F, -0.35F, 0.7F, 2.8608F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -2.92F, -0.7008F, 0.6785F, -0.0366F, 0.1035F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(71, 65).addBox(-0.525F, -3.2333F, -0.525F, 1.05F, 3.4533F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2347F, -0.0098F, 0.0831F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(7, 65).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 0.1F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_point_r1 = finger_1.addOrReplaceChild("sickle_1_point_r1", CubeListBuilder.create().texOffs(40, 65).addBox(-0.12F, -2.6499F, -0.13F, 0.24F, 2.8699F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.986F, -2.774F, 0.9995F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(89, 65).addBox(-0.35F, -3.2299F, -0.35F, 0.7F, 3.4499F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.65F, -0.876F, 0.6823F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(80, 65).addBox(-0.525F, -3.9736F, -0.525F, 1.05F, 4.1936F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2355F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(7, 65).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, -2.1F, 0.1F, 0.0349F, -0.1222F, -0.0873F));

        PartDefinition sickle_2_point_r1 = finger_2.addOrReplaceChild("sickle_2_point_r1", CubeListBuilder.create().texOffs(40, 65).addBox(-0.12F, -2.1697F, -0.13F, 0.24F, 2.3897F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -4.7888F, -2.2192F, 0.9962F, 0.0419F, -0.0771F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(40, 65).addBox(-0.35F, -2.6408F, -0.35F, 0.7F, 2.8608F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -2.92F, -0.7008F, 0.6785F, 0.0366F, -0.1035F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(71, 65).addBox(-0.525F, -3.2333F, -0.525F, 1.05F, 3.4533F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2347F, 0.0098F, -0.0831F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    static LayerDefinition chicken() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.08F, -3.0F, -4.0F, 4.08F, 6.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(53, 0).addBox(-1.2F, -2.0F, -1.6F, 0.66F, 4.2F, 3.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition exposed_rib_1_taper_r1 = body.addOrReplaceChild("exposed_rib_1_taper_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.225F, -2.6846F, -0.225F, 0.45F, 2.9046F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.64F, -1.0F, 0.03F, -0.0708F, 0.534F, 2.8842F));

        PartDefinition exposed_rib_1_upper_r1 = body.addOrReplaceChild("exposed_rib_1_upper_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.325F, -2.2325F, -0.325F, 0.65F, 2.4525F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, -2.0F, -0.12F, -0.0752F, -0.1284F, -2.0828F));

        PartDefinition exposed_rib_0_taper_r1 = body.addOrReplaceChild("exposed_rib_0_taper_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.225F, -2.6846F, -0.225F, 0.45F, 2.9046F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.64F, -1.0F, -0.73F, -0.0708F, 0.534F, 2.8842F));

        PartDefinition exposed_rib_0_upper_r1 = body.addOrReplaceChild("exposed_rib_0_upper_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.325F, -2.2325F, -0.325F, 0.65F, 2.4525F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, -2.0F, -0.88F, -0.0752F, -0.1284F, -2.0828F));

        PartDefinition uneven_haunch_r1 = body.addOrReplaceChild("uneven_haunch_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-1.98F, -2.7F, -0.8F, 2.28F, 5.4F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, 0.0F, 2.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition rounded_shoulder_r1 = body.addOrReplaceChild("rounded_shoulder_r1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.1F, -3.25F, -2.05F, 2.43F, 5.85F, 2.77F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, 0.0F, -2.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -3.4F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(69, 0).addBox(-2.0F, -4.0F, -2.6F, 4.0F, 4.02F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(97, 0).addBox(-2.0F, -1.78F, -4.6F, 4.0F, 1.7F, 2.3F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.23F, -0.4F, -0.0524F, 0.0F, -0.0349F));

        PartDefinition torn_wattle_r1 = jaw.addOrReplaceChild("torn_wattle_r1", CubeListBuilder.create().texOffs(112, 0).addBox(-0.55F, -3.0729F, -0.45F, 1.1F, 3.2929F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.4F, -2.9F, -2.871F, -1.1665F, -0.1792F));

        PartDefinition broken_lower_jaw_r1 = jaw.addOrReplaceChild("broken_lower_jaw_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-1.2F, -0.35F, -2.05F, 2.8F, 2.33F, 2.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(2.8F, -2.0F, 0.1F, 0.0F, 0.0873F, 0.1571F));

        PartDefinition separated_feather_m1_2_r1 = left_wing.addOrReplaceChild("separated_feather_m1_2_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-0.55F, -4.6722F, -0.65F, 1.1F, 4.8922F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.85F, 1.3F, 1.6F, -2.6053F, 0.3945F, 0.1097F));

        PartDefinition separated_feather_m1_1_r1 = left_wing.addOrReplaceChild("separated_feather_m1_1_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-0.55F, -4.5815F, -0.65F, 1.1F, 4.8015F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.85F, 1.3F, 1.0F, -2.6396F, 0.4387F, 0.1142F));

        PartDefinition separated_feather_m1_0_r1 = left_wing.addOrReplaceChild("separated_feather_m1_0_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-0.55F, -4.4981F, -0.65F, 1.1F, 4.7181F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.85F, 1.3F, 0.4F, -2.6737F, 0.4926F, 0.1197F));

        PartDefinition wing_root_m1_r1 = left_wing.addOrReplaceChild("wing_root_m1_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.9F, -3.1679F, -1.65F, 1.8F, 3.3879F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.626F, 1.0751F, 1.0272F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.8F, -2.0F, 0.1F, 0.0F, -0.0873F, -0.1571F));

        PartDefinition separated_feather_1_2_r1 = right_wing.addOrReplaceChild("separated_feather_1_2_r1", CubeListBuilder.create().texOffs(29, 15).addBox(-0.325F, -3.0061F, -0.65F, 0.65F, 3.2261F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.85F, 1.3F, 1.6F, -1.8678F, -0.6628F, -0.4985F));

        PartDefinition separated_feather_1_1_r1 = right_wing.addOrReplaceChild("separated_feather_1_1_r1", CubeListBuilder.create().texOffs(29, 15).addBox(-0.325F, -3.2403F, -0.65F, 0.65F, 3.4603F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.85F, 1.3F, 1.0F, -2.0141F, -0.8005F, -0.5227F));

        PartDefinition separated_feather_1_0_r1 = right_wing.addOrReplaceChild("separated_feather_1_0_r1", CubeListBuilder.create().texOffs(22, 15).addBox(-0.325F, -3.5789F, -0.65F, 0.65F, 3.7989F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.85F, 1.3F, 0.4F, -2.1142F, -0.9382F, -0.557F));

        PartDefinition wing_root_1_r1 = right_wing.addOrReplaceChild("wing_root_1_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.9F, -3.1679F, -1.65F, 1.8F, 3.3879F, 3.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.626F, -1.0751F, -1.0272F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-1.6F, 2.3F, 1.0F));

        PartDefinition leg_0_upper_skin_r1 = leg_0.addOrReplaceChild("leg_0_upper_skin_r1", CubeListBuilder.create().texOffs(36, 15).addBox(-0.6F, -3.0499F, -0.564F, 1.2F, 3.2699F, 1.128F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8568F, -0.4943F, -0.0723F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 2.736F, 0.7F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(50, 15).addBox(-0.42F, -2.5668F, -0.432F, 0.84F, 2.7868F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7341F, 0.2545F, -0.0529F));

        PartDefinition leg_0_asymmetric_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_asymmetric_joint_r1", CubeListBuilder.create().texOffs(45, 15).addBox(-0.504F, -0.75F, -0.432F, 0.96F, 1.4F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create().texOffs(55, 15).addBox(-0.6F, -0.3F, -0.5F, 1.2F, 0.8F, 1.1F, new CubeDeformation(0.0F)), PartPose.offset(-0.12F, 2.164F, -0.9F));

        PartDefinition leg_0_toe_2_r1 = leg_0_foot.addOrReplaceChild("leg_0_toe_2_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.175F, -2.0953F, -0.21F, 0.35F, 2.3153F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3F, 0.15F, 0.0F, 1.597F, 0.4351F, -0.4242F));

        PartDefinition leg_0_toe_1_r1 = leg_0_foot.addOrReplaceChild("leg_0_toe_1_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.175F, -2.4509F, -0.21F, 0.35F, 2.6709F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.15F, 0.0F, 1.7374F, 0.0F, 0.0F));

        PartDefinition leg_0_toe_0_r1 = leg_0_foot.addOrReplaceChild("leg_0_toe_0_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.175F, -2.0953F, -0.21F, 0.35F, 2.3153F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, 0.15F, 0.0F, 1.597F, -0.4351F, 0.4242F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(1.6F, 2.3F, 1.0F));

        PartDefinition leg_1_upper_skin_r1 = leg_1.addOrReplaceChild("leg_1_upper_skin_r1", CubeListBuilder.create().texOffs(36, 15).addBox(-0.6F, -3.0499F, -0.564F, 1.2F, 3.2699F, 1.128F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8568F, 0.4943F, 0.0723F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 2.736F, 0.7F));

        PartDefinition leg_1_torn_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_tendon_r1", CubeListBuilder.create().texOffs(69, 15).addBox(-0.15F, -1.228F, -0.15F, 0.3F, 1.448F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0756F, 1.2335F, -0.513F, 2.7454F, 0.1013F, -0.0203F));

        PartDefinition leg_1_remaining_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_remaining_skin_r1", CubeListBuilder.create().texOffs(45, 15).addBox(-0.432F, -1.5577F, -0.408F, 0.864F, 1.7777F, 0.816F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7341F, -0.2545F, 0.0529F));

        PartDefinition leg_1_exposed_shin_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_shin_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.21F, -2.5668F, -0.222F, 0.42F, 2.7868F, 0.444F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7341F, -0.2545F, 0.0529F));

        PartDefinition leg_1_asymmetric_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_asymmetric_joint_r1", CubeListBuilder.create().texOffs(69, 15).addBox(-0.504F, -0.75F, -0.432F, 0.96F, 1.4F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create().texOffs(55, 15).addBox(-0.6F, -0.3F, -0.5F, 1.2F, 0.8F, 1.1F, new CubeDeformation(0.0F)), PartPose.offset(0.12F, 2.164F, -0.9F));

        PartDefinition leg_1_toe_2_r1 = leg_1_foot.addOrReplaceChild("leg_1_toe_2_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.175F, -2.0953F, -0.21F, 0.35F, 2.3153F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3F, 0.15F, 0.0F, 1.597F, 0.4351F, -0.4242F));

        PartDefinition leg_1_toe_1_r1 = leg_1_foot.addOrReplaceChild("leg_1_toe_1_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.175F, -2.4509F, -0.21F, 0.35F, 2.6709F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.15F, 0.0F, 1.7374F, 0.0F, 0.0F));

        PartDefinition leg_1_toe_0_r1 = leg_1_foot.addOrReplaceChild("leg_1_toe_0_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.175F, -2.0953F, -0.21F, 0.35F, 2.3153F, 0.42F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, 0.15F, 0.0F, 1.597F, -0.4351F, 0.4242F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 3.5F, -0.1571F, 0.0F, 0.0F));

        PartDefinition ragged_tail_feather_2_r1 = tail.addOrReplaceChild("ragged_tail_feather_2_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-0.55F, -4.6211F, -0.7F, 1.1F, 4.8411F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.65F, 0.0F, -0.5F, -0.9225F, -0.0682F, -0.1371F));

        PartDefinition ragged_tail_feather_1_r1 = tail.addOrReplaceChild("ragged_tail_feather_1_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-0.55F, -4.4081F, -0.7F, 1.1F, 4.6281F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, -0.9894F, 0.0F, 0.0F));

        PartDefinition ragged_tail_feather_0_r1 = tail.addOrReplaceChild("ragged_tail_feather_0_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-0.55F, -4.2955F, -0.7F, 1.1F, 4.5155F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.65F, 0.0F, -0.5F, -1.039F, 0.0849F, 0.1483F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(89, 15).addBox(-1.25F, -2.5F, -0.95F, 2.5F, 1.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -2.7F, 2.08F, 0.192F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(74, 15).addBox(-1.5F, -1.3F, -1.3F, 3.1F, 2.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(100, 15).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -2.1F, 0.1F, -0.0698F, 0.1222F, 0.0873F));

        PartDefinition sickle_0_point_r1 = finger_0.addOrReplaceChild("sickle_0_point_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.12F, -1.164F, -0.13F, 0.24F, 1.384F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -2.296F, -1.064F, 0.9856F, -0.0861F, 0.1602F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.225F, -1.4012F, -0.225F, 0.45F, 1.6212F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -1.4F, -0.336F, 0.6662F, -0.0742F, 0.2139F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.35F, -1.6813F, -0.35F, 0.7F, 1.9013F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2321F, -0.0201F, 0.172F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(100, 15).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 0.1F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_point_r1 = finger_1.addOrReplaceChild("sickle_1_point_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.12F, -1.385F, -0.13F, 0.24F, 1.605F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.87F, -1.33F, 0.9995F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.225F, -1.6631F, -0.225F, 0.45F, 1.8831F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, -0.42F, 0.6823F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-0.35F, -2.0197F, -0.35F, 0.7F, 2.2397F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2355F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(100, 15).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, -2.1F, 0.1F, 0.0349F, -0.1222F, -0.0873F));

        PartDefinition sickle_2_point_r1 = finger_2.addOrReplaceChild("sickle_2_point_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.12F, -1.164F, -0.13F, 0.24F, 1.384F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -2.296F, -1.064F, 0.9856F, 0.0861F, -0.1602F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.225F, -1.4012F, -0.225F, 0.45F, 1.6212F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -1.4F, -0.336F, 0.6662F, 0.0742F, -0.2139F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(109, 15).addBox(-0.35F, -1.6813F, -0.35F, 0.7F, 1.9013F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2321F, 0.0201F, -0.172F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    static LayerDefinition horse() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.8F, -5.0F, -12.0F, 6.8F, 10.0F, 22.0F, new CubeDeformation(0.0F))
        .texOffs(0, 33).addBox(-2.0F, -4.0F, -5.4F, 1.1F, 8.2F, 8.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition exposed_rib_2_taper_r1 = body.addOrReplaceChild("exposed_rib_2_taper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.325F, -4.7136F, -0.325F, 0.65F, 4.9336F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, 0.91F, -0.0353F, 0.3267F, 2.9285F));

        PartDefinition exposed_rib_2_upper_r1 = body.addOrReplaceChild("exposed_rib_2_upper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.475F, -4.3952F, -0.475F, 0.95F, 4.6152F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, 0.76F, -0.0361F, -0.0888F, -2.3699F));

        PartDefinition exposed_rib_1_taper_r1 = body.addOrReplaceChild("exposed_rib_1_taper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.325F, -4.7136F, -0.325F, 0.65F, 4.9336F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, -1.18F, -0.0353F, 0.3267F, 2.9285F));

        PartDefinition exposed_rib_1_upper_r1 = body.addOrReplaceChild("exposed_rib_1_upper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.475F, -4.3952F, -0.475F, 0.95F, 4.6152F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, -1.33F, -0.0361F, -0.0888F, -2.3699F));

        PartDefinition exposed_rib_0_taper_r1 = body.addOrReplaceChild("exposed_rib_0_taper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.325F, -4.7136F, -0.325F, 0.65F, 4.9336F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, -3.27F, -0.0353F, 0.3267F, 2.9285F));

        PartDefinition exposed_rib_0_upper_r1 = body.addOrReplaceChild("exposed_rib_0_upper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.475F, -4.3952F, -0.475F, 0.95F, 4.6152F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, -3.42F, -0.0361F, -0.0888F, -2.3699F));

        PartDefinition uneven_haunch_r1 = body.addOrReplaceChild("uneven_haunch_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-3.3F, -4.7F, -5.7F, 3.8F, 9.4F, 7.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, 8.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition rounded_shoulder_r1 = body.addOrReplaceChild("rounded_shoulder_r1", CubeListBuilder.create().texOffs(59, 0).addBox(-3.5F, -5.25F, -2.05F, 4.05F, 9.85F, 7.53F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, -10.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -10.0F));

        PartDefinition broken_mane_3_r1 = neck.addOrReplaceChild("broken_mane_3_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.1F, 0.55F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition broken_mane_2_r1 = neck.addOrReplaceChild("broken_mane_2_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, 1.2F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition broken_mane_1_r1 = neck.addOrReplaceChild("broken_mane_1_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7F, 1.85F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition broken_mane_0_r1 = neck.addOrReplaceChild("broken_mane_0_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.5F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition slanted_neck_r1 = neck.addOrReplaceChild("slanted_neck_r1", CubeListBuilder.create().texOffs(28, 33).addBox(-2.3F, -8.577F, -2.75F, 4.6F, 8.797F, 5.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3F, 0.0F, 0.3672F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 33).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 4.02F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(0, 52).addBox(-2.0F, -2.3F, -9.2F, 4.0F, 3.42F, 4.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -2.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.77F, 1.2F, -0.0524F, 0.0F, -0.0349F));

        PartDefinition split_muzzle_jaw_r1 = jaw.addOrReplaceChild("split_muzzle_jaw_r1", CubeListBuilder.create().texOffs(19, 52).addBox(-1.5F, -0.05F, -10.1F, 3.3F, 2.08F, 4.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition uneven_tooth_2_r1 = jaw.addOrReplaceChild("uneven_tooth_2_r1", CubeListBuilder.create().texOffs(112, 33).addBox(-0.25F, -0.9942F, -0.3F, 0.5F, 1.2142F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.35F, -6.0F, 0.195F, 0.0153F, -0.1556F));

        PartDefinition uneven_tooth_1_r1 = jaw.addOrReplaceChild("uneven_tooth_1_r1", CubeListBuilder.create().texOffs(112, 33).addBox(-0.25F, -1.2383F, -0.3F, 0.5F, 1.4583F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.35F, -6.0F, 0.1479F, 0.0088F, -0.1181F));

        PartDefinition uneven_tooth_0_r1 = jaw.addOrReplaceChild("uneven_tooth_0_r1", CubeListBuilder.create().texOffs(112, 33).addBox(-0.25F, -0.9942F, -0.3F, 0.5F, 1.2142F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.35F, -6.0F, 0.195F, 0.0153F, -0.1556F));

        PartDefinition broken_lower_jaw_r1 = jaw.addOrReplaceChild("broken_lower_jaw_r1", CubeListBuilder.create().texOffs(87, 33).addBox(-2.2F, -0.35F, -6.05F, 4.8F, 2.33F, 6.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offsetAndRotation(1.8F, -2.7F, 0.5F, -0.1396F, 0.0F, -0.2443F));

        PartDefinition torn_ear_top_m1_r1 = left_ear.addOrReplaceChild("torn_ear_top_m1_r1", CubeListBuilder.create().texOffs(47, 52).addBox(-0.425F, -1.4017F, -0.325F, 0.85F, 1.6217F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2F, -2.145F, 0.3F, 0.1273F, -0.0109F, 0.1701F));

        PartDefinition long_ear_base_m1_r1 = left_ear.addOrReplaceChild("long_ear_base_m1_r1", CubeListBuilder.create().texOffs(38, 52).addBox(-0.7F, -2.3951F, -0.625F, 1.4F, 2.6151F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1384F, 0.0064F, 0.0921F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.8F, -2.7F, 0.5F, 0.0873F, 0.0F, 0.2443F));

        PartDefinition torn_ear_top_1_r1 = right_ear.addOrReplaceChild("torn_ear_top_1_r1", CubeListBuilder.create().texOffs(52, 52).addBox(-0.325F, -1.0191F, -0.325F, 0.65F, 1.2391F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, -2.145F, 0.3F, 0.1889F, 0.0241F, -0.253F));

        PartDefinition long_ear_base_1_r1 = right_ear.addOrReplaceChild("long_ear_base_1_r1", CubeListBuilder.create().texOffs(38, 52).addBox(-0.7F, -2.3951F, -0.625F, 1.4F, 2.6151F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1384F, -0.0064F, -0.0921F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-3.2F, 3.7F, -10.0F));

        PartDefinition leg_0_upper_skin_r1 = leg_0.addOrReplaceChild("leg_0_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.6F, -6.1681F, -1.504F, 3.2F, 6.3881F, 3.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.007F, -0.5013F, -0.0345F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 5.904F, 0.7F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(87, 52).addBox(-1.12F, -5.8892F, -1.152F, 2.24F, 6.1092F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9764F, 0.2634F, -0.0219F));

        PartDefinition leg_0_asymmetric_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_asymmetric_joint_r1", CubeListBuilder.create().texOffs(74, 52).addBox(-1.344F, -0.75F, -1.152F, 2.56F, 1.4F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 5.596F, -0.9F));

        PartDefinition leg_0_split_hoof_1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(-1.28F, -0.45F, -1.536F, 1.2F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_0_split_hoof_m1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_m1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(0.06F, -0.45F, -1.536F, 1.22F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(-3.2F, 3.7F, 7.5F));

        PartDefinition leg_1_upper_skin_r1 = leg_1.addOrReplaceChild("leg_1_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.6F, -6.1949F, -1.504F, 3.2F, 6.4149F, 3.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9778F, 0.3923F, -0.0326F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 5.904F, -0.9F));

        PartDefinition leg_1_lower_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_lower_skin_r1", CubeListBuilder.create().texOffs(87, 52).addBox(-1.12F, -5.9244F, -1.152F, 2.24F, 6.1444F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9429F, -0.2152F, -0.0215F));

        PartDefinition leg_1_asymmetric_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_asymmetric_joint_r1", CubeListBuilder.create().texOffs(74, 52).addBox(-1.344F, -0.75F, -1.152F, 2.56F, 1.4F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 5.596F, 1.1F));

        PartDefinition leg_1_split_hoof_1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(-1.28F, -0.45F, -1.536F, 1.2F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_1_split_hoof_m1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_m1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(0.06F, -0.45F, -1.536F, 1.22F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_2 = body.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(3.2F, 3.7F, -10.0F));

        PartDefinition leg_2_upper_skin_r1 = leg_2.addOrReplaceChild("leg_2_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.6F, -6.1681F, -1.504F, 3.2F, 6.3881F, 3.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.007F, 0.5013F, 0.0345F));

        PartDefinition leg_2_lower = leg_2.addOrReplaceChild("leg_2_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 5.904F, 0.7F));

        PartDefinition leg_2_torn_tendon_r1 = leg_2_lower.addOrReplaceChild("leg_2_torn_tendon_r1", CubeListBuilder.create().texOffs(22, 64).addBox(-0.4F, -2.6612F, -0.4F, 0.8F, 2.8812F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3156F, 3.1897F, -0.513F, 2.9346F, 0.6901F, -0.0746F));

        PartDefinition leg_2_remaining_skin_r1 = leg_2_lower.addOrReplaceChild("leg_2_remaining_skin_r1", CubeListBuilder.create().texOffs(9, 64).addBox(-1.152F, -3.4514F, -1.088F, 2.304F, 3.6714F, 2.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9764F, -0.2634F, 0.0219F));

        PartDefinition leg_2_exposed_shin_r1 = leg_2_lower.addOrReplaceChild("leg_2_exposed_shin_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.56F, -5.8892F, -0.592F, 1.12F, 6.1092F, 1.184F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9764F, -0.2634F, 0.0219F));

        PartDefinition leg_2_asymmetric_joint_r1 = leg_2_lower.addOrReplaceChild("leg_2_asymmetric_joint_r1", CubeListBuilder.create().texOffs(111, 52).addBox(-1.344F, -0.75F, -1.152F, 2.56F, 1.4F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_2_foot = leg_2_lower.addOrReplaceChild("leg_2_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 5.596F, -0.9F));

        PartDefinition leg_2_split_hoof_1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(-1.28F, -0.45F, -1.536F, 1.2F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_2_split_hoof_m1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_m1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(0.06F, -0.45F, -1.536F, 1.22F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_3 = body.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(3.2F, 3.7F, 7.5F));

        PartDefinition leg_3_upper_skin_r1 = leg_3.addOrReplaceChild("leg_3_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.6F, -6.1949F, -1.504F, 3.2F, 6.4149F, 3.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9778F, -0.3923F, 0.0326F));

        PartDefinition leg_3_lower = leg_3.addOrReplaceChild("leg_3_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 5.904F, -0.9F));

        PartDefinition leg_3_lower_skin_r1 = leg_3_lower.addOrReplaceChild("leg_3_lower_skin_r1", CubeListBuilder.create().texOffs(87, 52).addBox(-1.12F, -5.9244F, -1.152F, 2.24F, 6.1444F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9429F, 0.2152F, 0.0215F));

        PartDefinition leg_3_asymmetric_joint_r1 = leg_3_lower.addOrReplaceChild("leg_3_asymmetric_joint_r1", CubeListBuilder.create().texOffs(74, 52).addBox(-1.344F, -0.75F, -1.152F, 2.56F, 1.4F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_3_foot = leg_3_lower.addOrReplaceChild("leg_3_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 5.596F, 1.1F));

        PartDefinition leg_3_split_hoof_1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(-1.28F, -0.45F, -1.536F, 1.2F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_3_split_hoof_m1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_m1_r1", CubeListBuilder.create().texOffs(100, 52).addBox(0.06F, -0.45F, -1.536F, 1.22F, 1.25F, 2.6624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 9.5F, -0.1571F, 0.0F, 0.0F));

        PartDefinition torn_tail_tip_r1 = tail.addOrReplaceChild("torn_tail_tip_r1", CubeListBuilder.create().texOffs(36, 64).addBox(-0.5F, -12.2504F, -0.55F, 1.0F, 12.4704F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 1.9F, -3.0533F, -0.7164F, -0.0331F));

        PartDefinition tail_root_r1 = tail.addOrReplaceChild("tail_root_r1", CubeListBuilder.create().texOffs(27, 64).addBox(-0.65F, -3.0923F, -0.65F, 1.3F, 3.3123F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1F, -2.2792F, -0.4106F, -0.191F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(58, 64).addBox(-1.25F, -2.5F, -0.95F, 2.5F, 1.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.7F, 4.72F, 0.192F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(43, 64).addBox(-1.5F, -1.3F, -1.3F, 3.1F, 2.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(69, 64).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -2.1F, 0.1F, -0.0698F, 0.1222F, 0.0873F));

        PartDefinition sickle_0_point_r1 = finger_0.addOrReplaceChild("sickle_0_point_r1", CubeListBuilder.create().texOffs(92, 64).addBox(-0.12F, -2.3025F, -0.13F, 0.24F, 2.5225F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -5.1168F, -2.3712F, 0.9966F, -0.0393F, 0.0721F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(87, 64).addBox(-0.35F, -2.8049F, -0.35F, 0.7F, 3.0249F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -3.12F, -0.7488F, 0.679F, -0.0343F, 0.0969F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(78, 64).addBox(-0.525F, -3.4383F, -0.525F, 1.05F, 3.6583F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2348F, -0.0092F, 0.0778F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(69, 64).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 0.1F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_point_r1 = finger_1.addOrReplaceChild("sickle_1_point_r1", CubeListBuilder.create().texOffs(87, 64).addBox(-0.12F, -2.8164F, -0.13F, 0.24F, 3.0364F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.396F, -2.964F, 0.9995F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(87, 64).addBox(-0.35F, -3.436F, -0.35F, 0.7F, 3.656F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9F, -0.936F, 0.6823F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(97, 64).addBox(-0.525F, -4.2307F, -0.525F, 1.05F, 4.4507F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2355F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(69, 64).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, -2.1F, 0.1F, 0.0349F, -0.1222F, -0.0873F));

        PartDefinition sickle_2_point_r1 = finger_2.addOrReplaceChild("sickle_2_point_r1", CubeListBuilder.create().texOffs(92, 64).addBox(-0.12F, -2.3025F, -0.13F, 0.24F, 2.5225F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -5.1168F, -2.3712F, 0.9966F, 0.0393F, -0.0721F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(87, 64).addBox(-0.35F, -2.8049F, -0.35F, 0.7F, 3.0249F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -3.12F, -0.7488F, 0.679F, 0.0343F, -0.0969F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(78, 64).addBox(-0.525F, -3.4383F, -0.525F, 1.05F, 3.6583F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2348F, 0.0092F, -0.0778F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    static LayerDefinition donkey() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.8F, -5.0F, -12.0F, 6.8F, 10.0F, 22.0F, new CubeDeformation(0.0F))
        .texOffs(0, 33).addBox(-2.0F, -4.0F, -5.4F, 1.1F, 8.2F, 8.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition exposed_rib_2_taper_r1 = body.addOrReplaceChild("exposed_rib_2_taper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.325F, -4.7136F, -0.325F, 0.65F, 4.9336F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, 0.91F, -0.0353F, 0.3267F, 2.9285F));

        PartDefinition exposed_rib_2_upper_r1 = body.addOrReplaceChild("exposed_rib_2_upper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.475F, -4.3952F, -0.475F, 0.95F, 4.6152F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, 0.76F, -0.0361F, -0.0888F, -2.3699F));

        PartDefinition exposed_rib_1_taper_r1 = body.addOrReplaceChild("exposed_rib_1_taper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.325F, -4.7136F, -0.325F, 0.65F, 4.9336F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, -1.18F, -0.0353F, 0.3267F, 2.9285F));

        PartDefinition exposed_rib_1_upper_r1 = body.addOrReplaceChild("exposed_rib_1_upper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.475F, -4.3952F, -0.475F, 0.95F, 4.6152F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, -1.33F, -0.0361F, -0.0888F, -2.3699F));

        PartDefinition exposed_rib_0_taper_r1 = body.addOrReplaceChild("exposed_rib_0_taper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.325F, -4.7136F, -0.325F, 0.65F, 4.9336F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.0F, -3.27F, -0.0353F, 0.3267F, 2.9285F));

        PartDefinition exposed_rib_0_upper_r1 = body.addOrReplaceChild("exposed_rib_0_upper_r1", CubeListBuilder.create().texOffs(23, 33).addBox(-0.475F, -4.3952F, -0.475F, 0.95F, 4.6152F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, -3.42F, -0.0361F, -0.0888F, -2.3699F));

        PartDefinition uneven_haunch_r1 = body.addOrReplaceChild("uneven_haunch_r1", CubeListBuilder.create().texOffs(86, 0).addBox(-3.3F, -4.7F, -5.7F, 3.8F, 9.4F, 7.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, 8.0F, 0.0524F, 0.0F, 0.0524F));

        PartDefinition rounded_shoulder_r1 = body.addOrReplaceChild("rounded_shoulder_r1", CubeListBuilder.create().texOffs(59, 0).addBox(-3.5F, -5.25F, -2.05F, 4.05F, 9.85F, 7.53F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, -10.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -10.0F));

        PartDefinition broken_mane_3_r1 = neck.addOrReplaceChild("broken_mane_3_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.1F, 0.55F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition broken_mane_2_r1 = neck.addOrReplaceChild("broken_mane_2_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.4F, 1.2F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition broken_mane_1_r1 = neck.addOrReplaceChild("broken_mane_1_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.7F, 1.85F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition broken_mane_0_r1 = neck.addOrReplaceChild("broken_mane_0_r1", CubeListBuilder.create().texOffs(51, 33).addBox(-0.8F, -1.7142F, -0.7F, 1.6F, 1.9342F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.5F, -0.8285F, -0.0443F, -0.1007F));

        PartDefinition slanted_neck_r1 = neck.addOrReplaceChild("slanted_neck_r1", CubeListBuilder.create().texOffs(28, 33).addBox(-2.3F, -8.577F, -2.75F, 4.6F, 8.797F, 5.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3F, 0.0F, 0.3672F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 33).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 4.02F, 7.0F, new CubeDeformation(0.0F))
        .texOffs(0, 52).addBox(-2.0F, -2.3F, -9.2F, 4.0F, 3.42F, 4.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -2.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.77F, 1.2F, -0.0524F, 0.0F, -0.0349F));

        PartDefinition split_muzzle_jaw_r1 = jaw.addOrReplaceChild("split_muzzle_jaw_r1", CubeListBuilder.create().texOffs(19, 52).addBox(-1.5F, -0.05F, -10.1F, 3.3F, 2.08F, 4.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition uneven_tooth_2_r1 = jaw.addOrReplaceChild("uneven_tooth_2_r1", CubeListBuilder.create().texOffs(112, 33).addBox(-0.25F, -0.9942F, -0.3F, 0.5F, 1.2142F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.35F, -6.0F, 0.195F, 0.0153F, -0.1556F));

        PartDefinition uneven_tooth_1_r1 = jaw.addOrReplaceChild("uneven_tooth_1_r1", CubeListBuilder.create().texOffs(112, 33).addBox(-0.25F, -1.2383F, -0.3F, 0.5F, 1.4583F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.35F, -6.0F, 0.1479F, 0.0088F, -0.1181F));

        PartDefinition uneven_tooth_0_r1 = jaw.addOrReplaceChild("uneven_tooth_0_r1", CubeListBuilder.create().texOffs(112, 33).addBox(-0.25F, -0.9942F, -0.3F, 0.5F, 1.2142F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.35F, -6.0F, 0.195F, 0.0153F, -0.1556F));

        PartDefinition broken_lower_jaw_r1 = jaw.addOrReplaceChild("broken_lower_jaw_r1", CubeListBuilder.create().texOffs(87, 33).addBox(-2.2F, -0.35F, -6.05F, 4.8F, 2.33F, 6.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0524F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offsetAndRotation(1.8F, -2.7F, 0.5F, -0.1396F, 0.0F, -0.1396F));

        PartDefinition torn_ear_top_m1_r1 = left_ear.addOrReplaceChild("torn_ear_top_m1_r1", CubeListBuilder.create().texOffs(47, 52).addBox(-0.425F, -2.4739F, -0.325F, 0.85F, 2.6939F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2F, -4.16F, 0.3F, 0.0666F, -0.003F, 0.0889F));

        PartDefinition long_ear_base_m1_r1 = left_ear.addOrReplaceChild("long_ear_base_m1_r1", CubeListBuilder.create().texOffs(38, 52).addBox(-0.7F, -4.3956F, -0.625F, 1.4F, 4.6156F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0719F, 0.0017F, 0.0479F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.8F, -2.7F, 0.5F, 0.0873F, 0.0F, 0.1396F));

        PartDefinition torn_ear_top_1_r1 = right_ear.addOrReplaceChild("torn_ear_top_1_r1", CubeListBuilder.create().texOffs(52, 52).addBox(-0.325F, -1.7131F, -0.325F, 0.65F, 1.9331F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, -4.16F, 0.3F, 0.1006F, 0.0068F, -0.1344F));

        PartDefinition long_ear_base_1_r1 = right_ear.addOrReplaceChild("long_ear_base_1_r1", CubeListBuilder.create().texOffs(38, 52).addBox(-0.7F, -4.3956F, -0.625F, 1.4F, 4.6156F, 1.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0719F, -0.0017F, -0.0479F));

        PartDefinition leg_0 = body.addOrReplaceChild("leg_0", CubeListBuilder.create(), PartPose.offset(-3.2F, 3.7F, -10.0F));

        PartDefinition leg_0_upper_skin_r1 = leg_0.addOrReplaceChild("leg_0_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.55F, -6.1681F, -1.457F, 3.1F, 6.3881F, 2.914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.007F, -0.5013F, -0.0345F));

        PartDefinition leg_0_lower = leg_0.addOrReplaceChild("leg_0_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 5.904F, 0.7F));

        PartDefinition leg_0_lower_skin_r1 = leg_0_lower.addOrReplaceChild("leg_0_lower_skin_r1", CubeListBuilder.create().texOffs(85, 52).addBox(-1.085F, -5.8892F, -1.116F, 2.17F, 6.1092F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9764F, 0.2634F, -0.0219F));

        PartDefinition leg_0_asymmetric_joint_r1 = leg_0_lower.addOrReplaceChild("leg_0_asymmetric_joint_r1", CubeListBuilder.create().texOffs(72, 52).addBox(-1.302F, -0.75F, -1.116F, 2.48F, 1.4F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_0_foot = leg_0_lower.addOrReplaceChild("leg_0_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 5.596F, -0.9F));

        PartDefinition leg_0_split_hoof_1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(-1.24F, -0.45F, -1.488F, 1.16F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_0_split_hoof_m1_r1 = leg_0_foot.addOrReplaceChild("leg_0_split_hoof_m1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(0.06F, -0.45F, -1.488F, 1.18F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_1 = body.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(-3.2F, 3.7F, 7.5F));

        PartDefinition leg_1_upper_skin_r1 = leg_1.addOrReplaceChild("leg_1_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.55F, -6.1949F, -1.457F, 3.1F, 6.4149F, 2.914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9778F, 0.3923F, -0.0326F));

        PartDefinition leg_1_lower = leg_1.addOrReplaceChild("leg_1_lower", CubeListBuilder.create(), PartPose.offset(-0.18F, 5.904F, -0.9F));

        PartDefinition leg_1_torn_tendon_r1 = leg_1_lower.addOrReplaceChild("leg_1_torn_tendon_r1", CubeListBuilder.create().texOffs(22, 63).addBox(-0.3875F, -2.6838F, -0.3875F, 0.775F, 2.9038F, 0.775F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4404F, 3.1897F, 0.627F, -2.8211F, -0.9155F, -0.1589F));

        PartDefinition leg_1_remaining_skin_r1 = leg_1_lower.addOrReplaceChild("leg_1_remaining_skin_r1", CubeListBuilder.create().texOffs(9, 63).addBox(-1.116F, -3.4715F, -1.054F, 2.232F, 3.6915F, 2.108F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9429F, -0.2152F, -0.0215F));

        PartDefinition leg_1_exposed_shin_r1 = leg_1_lower.addOrReplaceChild("leg_1_exposed_shin_r1", CubeListBuilder.create().texOffs(0, 63).addBox(-0.5425F, -5.9244F, -0.5735F, 1.085F, 6.1444F, 1.147F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9429F, -0.2152F, -0.0215F));

        PartDefinition leg_1_asymmetric_joint_r1 = leg_1_lower.addOrReplaceChild("leg_1_asymmetric_joint_r1", CubeListBuilder.create().texOffs(109, 52).addBox(-1.302F, -0.75F, -1.116F, 2.48F, 1.4F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition leg_1_foot = leg_1_lower.addOrReplaceChild("leg_1_foot", CubeListBuilder.create(), PartPose.offset(-0.12F, 5.596F, 1.1F));

        PartDefinition leg_1_split_hoof_1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(-1.24F, -0.45F, -1.488F, 1.16F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_1_split_hoof_m1_r1 = leg_1_foot.addOrReplaceChild("leg_1_split_hoof_m1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(0.06F, -0.45F, -1.488F, 1.18F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_2 = body.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(3.2F, 3.7F, -10.0F));

        PartDefinition leg_2_upper_skin_r1 = leg_2.addOrReplaceChild("leg_2_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.55F, -6.1681F, -1.457F, 3.1F, 6.3881F, 2.914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.007F, 0.5013F, 0.0345F));

        PartDefinition leg_2_lower = leg_2.addOrReplaceChild("leg_2_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 5.904F, 0.7F));

        PartDefinition leg_2_lower_skin_r1 = leg_2_lower.addOrReplaceChild("leg_2_lower_skin_r1", CubeListBuilder.create().texOffs(85, 52).addBox(-1.085F, -5.8892F, -1.116F, 2.17F, 6.1092F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9764F, -0.2634F, 0.0219F));

        PartDefinition leg_2_asymmetric_joint_r1 = leg_2_lower.addOrReplaceChild("leg_2_asymmetric_joint_r1", CubeListBuilder.create().texOffs(72, 52).addBox(-1.302F, -0.75F, -1.116F, 2.48F, 1.4F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_2_foot = leg_2_lower.addOrReplaceChild("leg_2_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 5.596F, -0.9F));

        PartDefinition leg_2_split_hoof_1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(-1.24F, -0.45F, -1.488F, 1.16F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_2_split_hoof_m1_r1 = leg_2_foot.addOrReplaceChild("leg_2_split_hoof_m1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(0.06F, -0.45F, -1.488F, 1.18F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition leg_3 = body.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(3.2F, 3.7F, 7.5F));

        PartDefinition leg_3_upper_skin_r1 = leg_3.addOrReplaceChild("leg_3_upper_skin_r1", CubeListBuilder.create().texOffs(57, 52).addBox(-1.55F, -6.1949F, -1.457F, 3.1F, 6.4149F, 2.914F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9778F, -0.3923F, 0.0326F));

        PartDefinition leg_3_lower = leg_3.addOrReplaceChild("leg_3_lower", CubeListBuilder.create(), PartPose.offset(0.18F, 5.904F, -0.9F));

        PartDefinition leg_3_lower_skin_r1 = leg_3_lower.addOrReplaceChild("leg_3_lower_skin_r1", CubeListBuilder.create().texOffs(85, 52).addBox(-1.085F, -5.9244F, -1.116F, 2.17F, 6.1444F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9429F, 0.2152F, 0.0215F));

        PartDefinition leg_3_asymmetric_joint_r1 = leg_3_lower.addOrReplaceChild("leg_3_asymmetric_joint_r1", CubeListBuilder.create().texOffs(72, 52).addBox(-1.302F, -0.75F, -1.116F, 2.48F, 1.4F, 2.232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition leg_3_foot = leg_3_lower.addOrReplaceChild("leg_3_foot", CubeListBuilder.create(), PartPose.offset(0.12F, 5.596F, 1.1F));

        PartDefinition leg_3_split_hoof_1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(-1.24F, -0.45F, -1.488F, 1.16F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0698F, 0.0524F));

        PartDefinition leg_3_split_hoof_m1_r1 = leg_3_foot.addOrReplaceChild("leg_3_split_hoof_m1_r1", CubeListBuilder.create().texOffs(98, 52).addBox(0.06F, -0.45F, -1.488F, 1.18F, 1.25F, 2.5792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0698F, -0.0524F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 9.5F, -0.1571F, 0.0F, 0.0F));

        PartDefinition torn_tail_tip_r1 = tail.addOrReplaceChild("torn_tail_tip_r1", CubeListBuilder.create().texOffs(36, 63).addBox(-0.5F, -12.2504F, -0.55F, 1.0F, 12.4704F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 1.9F, -3.0533F, -0.7164F, -0.0331F));

        PartDefinition tail_root_r1 = tail.addOrReplaceChild("tail_root_r1", CubeListBuilder.create().texOffs(27, 63).addBox(-0.65F, -3.0923F, -0.65F, 1.3F, 3.3123F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1F, -2.2792F, -0.4106F, -0.191F));

        PartDefinition ornament = body.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(58, 63).addBox(-1.25F, -2.5F, -0.95F, 2.5F, 1.95F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.7F, 4.72F, 0.192F, 0.0F, 0.0873F));

        PartDefinition embedded_claw_root_r1 = ornament.addOrReplaceChild("embedded_claw_root_r1", CubeListBuilder.create().texOffs(43, 63).addBox(-1.5F, -1.3F, -1.3F, 3.1F, 2.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, -0.1222F, -0.1047F));

        PartDefinition finger_0 = ornament.addOrReplaceChild("finger_0", CubeListBuilder.create().texOffs(69, 63).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -2.1F, 0.1F, -0.0698F, 0.1222F, 0.0873F));

        PartDefinition sickle_0_point_r1 = finger_0.addOrReplaceChild("sickle_0_point_r1", CubeListBuilder.create().texOffs(87, 63).addBox(-0.12F, -2.0901F, -0.13F, 0.24F, 2.3101F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.592F, -2.128F, 0.9959F, -0.0437F, 0.0804F));

        PartDefinition sickle_0_edge_r1 = finger_0.addOrReplaceChild("sickle_0_edge_r1", CubeListBuilder.create().texOffs(87, 63).addBox(-0.35F, -2.5424F, -0.35F, 0.7F, 2.7624F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -2.8F, -0.672F, 0.6782F, -0.0381F, 0.1079F));

        PartDefinition sickle_0_base_r1 = finger_0.addOrReplaceChild("sickle_0_base_r1", CubeListBuilder.create().texOffs(78, 63).addBox(-0.525F, -3.1103F, -0.525F, 1.05F, 3.3303F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2347F, -0.0102F, 0.0866F));

        PartDefinition finger_1 = ornament.addOrReplaceChild("finger_1", CubeListBuilder.create().texOffs(69, 63).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, 0.1F, -0.0175F, 0.0F, 0.0F));

        PartDefinition sickle_1_point_r1 = finger_1.addOrReplaceChild("sickle_1_point_r1", CubeListBuilder.create().texOffs(87, 63).addBox(-0.12F, -2.5501F, -0.13F, 0.24F, 2.7701F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.74F, -2.66F, 0.9995F, 0.0F, 0.0F));

        PartDefinition sickle_1_edge_r1 = finger_1.addOrReplaceChild("sickle_1_edge_r1", CubeListBuilder.create().texOffs(101, 63).addBox(-0.35F, -3.1062F, -0.35F, 0.7F, 3.3262F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, -0.84F, 0.6823F, 0.0F, 0.0F));

        PartDefinition sickle_1_base_r1 = finger_1.addOrReplaceChild("sickle_1_base_r1", CubeListBuilder.create().texOffs(92, 63).addBox(-0.525F, -3.8194F, -0.525F, 1.05F, 4.0394F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2355F, 0.0F, 0.0F));

        PartDefinition finger_2 = ornament.addOrReplaceChild("finger_2", CubeListBuilder.create().texOffs(69, 63).addBox(-0.55F, -0.6F, -0.5F, 1.1F, 1.05F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, -2.1F, 0.1F, 0.0349F, -0.1222F, -0.0873F));

        PartDefinition sickle_2_point_r1 = finger_2.addOrReplaceChild("sickle_2_point_r1", CubeListBuilder.create().texOffs(87, 63).addBox(-0.12F, -2.0901F, -0.13F, 0.24F, 2.3101F, 0.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -4.592F, -2.128F, 0.9959F, 0.0437F, -0.0804F));

        PartDefinition sickle_2_edge_r1 = finger_2.addOrReplaceChild("sickle_2_edge_r1", CubeListBuilder.create().texOffs(87, 63).addBox(-0.35F, -2.5424F, -0.35F, 0.7F, 2.7624F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -2.8F, -0.672F, 0.6782F, 0.0381F, -0.1079F));

        PartDefinition sickle_2_base_r1 = finger_2.addOrReplaceChild("sickle_2_base_r1", CubeListBuilder.create().texOffs(78, 63).addBox(-0.525F, -3.1103F, -0.525F, 1.05F, 3.3303F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2347F, 0.0102F, -0.0866F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private CorruptedLivestockGeometry() {}
}
