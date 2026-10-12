package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

/** Blockbench geometry with its own stride, alternating slashes, airborne and landing poses. */
public final class CorruptedZombieModel extends CorruptedHumanoidModel<Zombie> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_zombie"), "main");
    public CorruptedZombieModel(ModelPart root) { super(root); }

    @Override protected float restingSideLean() { return -0.105F; }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(64, 17).addBox(-1.3F, -3.0F, -0.7F, 2.5F, 3.0F, 2.1F, new CubeDeformation(0.0F))
        .texOffs(77, 17).addBox(2.5F, -2.2F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(82, 17).addBox(1.15F, -2.2F, -4.15F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(87, 17).addBox(-0.2F, -2.2F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(92, 17).addBox(-1.55F, -2.2F, -4.15F, 0.7F, 0.85F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(97, 17).addBox(-2.9F, -2.2F, -4.15F, 0.7F, 1.15F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(80, 34).addBox(3.7F, -6.5F, -1.2F, 0.9F, 4.0F, 2.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(33, 0).addBox(-3.8F, -0.2F, -5.5F, 7.6F, 1.4F, 7.4F, new CubeDeformation(0.0F))
        .texOffs(102, 17).addBox(2.15F, -0.85F, -5.6F, 0.65F, 0.8F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(107, 17).addBox(0.5F, -1.05F, -5.6F, 0.65F, 1.0F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(112, 17).addBox(-1.15F, -0.85F, -5.6F, 0.65F, 0.8F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(117, 17).addBox(-2.8F, -1.05F, -5.6F, 0.65F, 1.0F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.5F, 0.2094F, 0.0F, -0.1222F));

        PartDefinition right_head_sickles = head.addOrReplaceChild("right_head_sickles", CubeListBuilder.create().texOffs(46, 80).addBox(-1.5F, -1.8F, -1.6F, 2.4F, 2.6F, 3.3F, new CubeDeformation(0.0F))
        .texOffs(61, 80).addBox(-3.2F, -2.4F, -1.2F, 2.6F, 2.0F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -6.4F, 1.8F, -0.1222F, 0.0F, -0.1047F));

        PartDefinition right_head_blade_0 = right_head_sickles.addOrReplaceChild("right_head_blade_0", CubeListBuilder.create().texOffs(119, 80).addBox(-0.8F, -0.6F, -0.715F, 1.6F, 1.8F, 1.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.7F, -0.6F, 0.0F, 0.1396F, 0.0F));

        PartDefinition right_head_blade_0_segment_3_r1 = right_head_blade_0.addOrReplaceChild("right_head_blade_0_segment_3_r1", CubeListBuilder.create().texOffs(23, 90).addBox(-0.0765F, -1.7045F, -0.55F, 0.153F, 1.9245F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.995F, -3.23F, 0.0F, 0.0F, 0.0F, -1.9832F));

        PartDefinition right_head_blade_0_segment_2_r1 = right_head_blade_0.addOrReplaceChild("right_head_blade_0_segment_2_r1", CubeListBuilder.create().texOffs(16, 90).addBox(-0.2337F, -1.855F, -0.55F, 0.4675F, 2.075F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.38F, -2.975F, 0.0F, 0.0F, 0.0F, -1.4142F));

        PartDefinition right_head_blade_0_segment_1_r1 = right_head_blade_0.addOrReplaceChild("right_head_blade_0_segment_1_r1", CubeListBuilder.create().texOffs(9, 90).addBox(-0.4037F, -1.9723F, -0.55F, 0.8075F, 2.1923F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.02F, -1.87F, 0.0F, 0.0F, 0.0F, -0.8885F));

        PartDefinition right_head_blade_0_segment_0_r1 = right_head_blade_0.addOrReplaceChild("right_head_blade_0_segment_0_r1", CubeListBuilder.create().texOffs(0, 90).addBox(-0.5312F, -2.3501F, -0.55F, 1.0625F, 2.5701F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4993F));

        PartDefinition right_head_blade_1 = right_head_sickles.addOrReplaceChild("right_head_blade_1", CubeListBuilder.create().texOffs(73, 90).addBox(-0.8F, -0.6F, -0.715F, 1.6F, 1.8F, 1.43F, new CubeDeformation(0.0F)), PartPose.offset(-1.55F, -1.7F, 0.25F));

        PartDefinition right_head_blade_1_segment_3_r1 = right_head_blade_1.addOrReplaceChild("right_head_blade_1_segment_3_r1", CubeListBuilder.create().texOffs(105, 90).addBox(-0.09F, -1.9664F, -0.55F, 0.18F, 2.1864F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7F, -3.8F, 0.0F, 0.0F, 0.0F, -1.9832F));

        PartDefinition right_head_blade_1_segment_2_r1 = right_head_blade_1.addOrReplaceChild("right_head_blade_1_segment_2_r1", CubeListBuilder.create().texOffs(98, 90).addBox(-0.275F, -2.1435F, -0.55F, 0.55F, 2.3635F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, -3.5F, 0.0F, 0.0F, 0.0F, -1.4142F));

        PartDefinition right_head_blade_1_segment_1_r1 = right_head_blade_1.addOrReplaceChild("right_head_blade_1_segment_1_r1", CubeListBuilder.create().texOffs(91, 90).addBox(-0.475F, -2.2816F, -0.55F, 0.95F, 2.5016F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -2.2F, 0.0F, 0.0F, 0.0F, -0.8885F));

        PartDefinition right_head_blade_1_segment_0_r1 = right_head_blade_1.addOrReplaceChild("right_head_blade_1_segment_0_r1", CubeListBuilder.create().texOffs(82, 90).addBox(-0.625F, -2.726F, -0.55F, 1.25F, 2.946F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4993F));

        PartDefinition right_head_blade_2 = right_head_sickles.addOrReplaceChild("right_head_blade_2", CubeListBuilder.create().texOffs(32, 99).addBox(-0.8F, -0.6F, -0.715F, 1.6F, 1.8F, 1.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6F, -1.7F, 1.1F, 0.0F, -0.1396F, 0.0F));

        PartDefinition right_head_blade_2_segment_3_r1 = right_head_blade_2.addOrReplaceChild("right_head_blade_2_segment_3_r1", CubeListBuilder.create().texOffs(64, 99).addBox(-0.081F, -1.7918F, -0.55F, 0.162F, 2.0118F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.23F, -3.42F, 0.0F, 0.0F, 0.0F, -1.9832F));

        PartDefinition right_head_blade_2_segment_2_r1 = right_head_blade_2.addOrReplaceChild("right_head_blade_2_segment_2_r1", CubeListBuilder.create().texOffs(57, 99).addBox(-0.2475F, -1.9512F, -0.55F, 0.495F, 2.1712F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.52F, -3.15F, 0.0F, 0.0F, 0.0F, -1.4142F));

        PartDefinition right_head_blade_2_segment_1_r1 = right_head_blade_2.addOrReplaceChild("right_head_blade_2_segment_1_r1", CubeListBuilder.create().texOffs(50, 99).addBox(-0.4275F, -2.0754F, -0.55F, 0.855F, 2.2954F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.08F, -1.98F, 0.0F, 0.0F, 0.0F, -0.8885F));

        PartDefinition right_head_blade_2_segment_0_r1 = right_head_blade_2.addOrReplaceChild("right_head_blade_2_segment_0_r1", CubeListBuilder.create().texOffs(41, 99).addBox(-0.5625F, -2.4754F, -0.55F, 1.125F, 2.6954F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4993F));

        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(66, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(34, 17).addBox(-1.1F, 1.5F, -2.65F, 3.0F, 6.5F, 0.85F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rib_right_r1 = body.addOrReplaceChild("rib_right_r1", CubeListBuilder.create().texOffs(57, 17).addBox(-1.7F, -0.4F, -0.75F, 1.9F, 0.9F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 4.0F, -2.0F, 0.0F, 0.0F, 0.3142F));

        PartDefinition rib_left_lower_r1 = body.addOrReplaceChild("rib_left_lower_r1", CubeListBuilder.create().texOffs(50, 17).addBox(-0.1F, -0.3F, -0.75F, 1.7F, 0.8F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 5.0F, -2.0F, 0.0F, 0.0F, -0.2793F));

        PartDefinition rib_left_upper_r1 = body.addOrReplaceChild("rib_left_upper_r1", CubeListBuilder.create().texOffs(43, 17).addBox(0.0F, -0.9F, -0.75F, 2.0F, 0.9F, 0.65F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 3.0F, -2.0F, 0.0F, 0.0F, -0.2094F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(91, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.3F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -0.1396F, 0.0F, -0.1047F));

        PartDefinition shoulder_tendon_r1 = left_arm.addOrReplaceChild("shoulder_tendon_r1", CubeListBuilder.create().texOffs(94, 45).addBox(-0.3F, -1.4F, -0.4F, 0.8F, 5.4F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -1.0F, 1.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition left_elbow = left_arm.addOrReplaceChild("left_elbow", CubeListBuilder.create().texOffs(91, 6).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 4.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition right_claw_inner_r1 = left_elbow.addOrReplaceChild("right_claw_inner_r1", CubeListBuilder.create().texOffs(111, 57).addBox(-0.6F, -0.5F, -0.5F, 0.9F, 1.9F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 6.0F, -2.0F, 0.2618F, 0.0F, 0.1745F));

        PartDefinition right_claw_outer_r1 = left_elbow.addOrReplaceChild("right_claw_outer_r1", CubeListBuilder.create().texOffs(106, 57).addBox(-0.6F, -0.5F, -0.5F, 0.9F, 2.5F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 6.0F, -2.0F, 0.2618F, 0.0F, -0.1222F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(108, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.3F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -0.1396F, 0.0F, 0.1047F));

        PartDefinition right_elbow = right_arm.addOrReplaceChild("right_elbow", CubeListBuilder.create().texOffs(108, 6).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 4.0F, 0.0F, -0.4189F, 0.0F, 0.0F));

        PartDefinition left_claw_2_r1 = right_elbow.addOrReplaceChild("left_claw_2_r1", CubeListBuilder.create().texOffs(101, 57).addBox(-0.7F, -0.5F, -0.4F, 0.7F, 3.1F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6F, 6.0F, -2.0F, 0.2094F, 0.0F, 0.1396F));

        PartDefinition left_claw_1_r1 = right_elbow.addOrReplaceChild("left_claw_1_r1", CubeListBuilder.create().texOffs(96, 57).addBox(-0.7F, -0.5F, -0.4F, 0.7F, 3.8F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 6.0F, -2.0F, 0.2094F, 0.0F, 0.0F));

        PartDefinition left_claw_0_r1 = right_elbow.addOrReplaceChild("left_claw_0_r1", CubeListBuilder.create().texOffs(91, 57).addBox(-0.7F, -0.5F, -0.4F, 0.7F, 3.1F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6F, 6.0F, -2.0F, 0.2094F, 0.0F, -0.1396F));

        PartDefinition right_shoulder_sickles = right_arm.addOrReplaceChild("right_shoulder_sickles", CubeListBuilder.create().texOffs(0, 80).addBox(-3.3F, -1.7F, -2.5F, 4.1F, 2.9F, 5.5F, new CubeDeformation(0.0F))
        .texOffs(23, 80).addBox(-5.7F, -3.0F, -2.1F, 4.1F, 2.4F, 5.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -1.0F, 0.0F, 0.1396F, 0.0F, -0.384F));

        PartDefinition right_shoulder_blade_0 = right_shoulder_sickles.addOrReplaceChild("right_shoulder_blade_0", CubeListBuilder.create().texOffs(76, 80).addBox(-0.8F, -0.6F, -1.04F, 1.6F, 1.8F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7F, -2.5F, -1.0F, 0.0F, -0.2793F, 0.0F));

        PartDefinition right_shoulder_blade_0_segment_3_r1 = right_shoulder_blade_0.addOrReplaceChild("right_shoulder_blade_0_segment_3_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.0984F, -2.2749F, -0.8F, 0.1968F, 2.4949F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.312F, -10.496F, 0.0F, 0.0F, 0.0F, 0.4993F));

        PartDefinition right_shoulder_blade_0_segment_2_r1 = right_shoulder_blade_0.addOrReplaceChild("right_shoulder_blade_0_segment_2_r1", CubeListBuilder.create().texOffs(105, 80).addBox(-0.328F, -3.4043F, -0.8F, 0.656F, 3.6243F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.968F, -7.38F, 0.0F, 0.0F, 0.0F, 0.2075F));

        PartDefinition right_shoulder_blade_0_segment_1_r1 = right_shoulder_blade_0.addOrReplaceChild("right_shoulder_blade_0_segment_1_r1", CubeListBuilder.create().texOffs(96, 80).addBox(-0.5535F, -3.8734F, -0.8F, 1.107F, 4.0934F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.394F, -3.772F, 0.0F, 0.0F, 0.0F, -0.1578F));

        PartDefinition right_shoulder_blade_0_segment_0_r1 = right_shoulder_blade_0.addOrReplaceChild("right_shoulder_blade_0_segment_0_r1", CubeListBuilder.create().texOffs(87, 80).addBox(-0.738F, -4.2413F, -0.8F, 1.476F, 4.4613F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.354F));

        PartDefinition right_shoulder_blade_1 = right_shoulder_sickles.addOrReplaceChild("right_shoulder_blade_1", CubeListBuilder.create().texOffs(30, 90).addBox(-0.8F, -0.6F, -1.04F, 1.6F, 1.8F, 2.08F, new CubeDeformation(0.0F)), PartPose.offset(-3.25F, -2.5F, 0.65F));

        PartDefinition right_shoulder_blade_1_segment_3_r1 = right_shoulder_blade_1.addOrReplaceChild("right_shoulder_blade_1_segment_3_r1", CubeListBuilder.create().texOffs(66, 90).addBox(-0.12F, -2.726F, -0.8F, 0.24F, 2.946F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6F, -12.8F, 0.0F, 0.0F, 0.0F, 0.4993F));

        PartDefinition right_shoulder_blade_1_segment_2_r1 = right_shoulder_blade_1.addOrReplaceChild("right_shoulder_blade_1_segment_2_r1", CubeListBuilder.create().texOffs(59, 90).addBox(-0.4F, -4.1033F, -0.8F, 0.8F, 4.3233F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -9.0F, 0.0F, 0.0F, 0.0F, 0.2075F));

        PartDefinition right_shoulder_blade_1_segment_1_r1 = right_shoulder_blade_1.addOrReplaceChild("right_shoulder_blade_1_segment_1_r1", CubeListBuilder.create().texOffs(50, 90).addBox(-0.675F, -4.6753F, -0.8F, 1.35F, 4.8953F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7F, -4.6F, 0.0F, 0.0F, 0.0F, -0.1578F));

        PartDefinition right_shoulder_blade_1_segment_0_r1 = right_shoulder_blade_1.addOrReplaceChild("right_shoulder_blade_1_segment_0_r1", CubeListBuilder.create().texOffs(41, 90).addBox(-0.9F, -5.1241F, -0.8F, 1.8F, 5.3441F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.354F));

        PartDefinition right_shoulder_blade_2 = right_shoulder_sickles.addOrReplaceChild("right_shoulder_blade_2", CubeListBuilder.create().texOffs(112, 90).addBox(-0.8F, -0.6F, -1.04F, 1.6F, 1.8F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.8F, -2.5F, 2.3F, 0.0F, 0.5411F, 0.0F));

        PartDefinition right_shoulder_blade_2_segment_3_r1 = right_shoulder_blade_2.addOrReplaceChild("right_shoulder_blade_2_segment_3_r1", CubeListBuilder.create().texOffs(25, 99).addBox(-0.1056F, -2.4253F, -0.8F, 0.2112F, 2.6453F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.408F, -11.264F, 0.0F, 0.0F, 0.0F, 0.4993F));

        PartDefinition right_shoulder_blade_2_segment_2_r1 = right_shoulder_blade_2.addOrReplaceChild("right_shoulder_blade_2_segment_2_r1", CubeListBuilder.create().texOffs(18, 99).addBox(-0.352F, -3.6373F, -0.8F, 0.704F, 3.8573F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.112F, -7.92F, 0.0F, 0.0F, 0.0F, 0.2075F));

        PartDefinition right_shoulder_blade_2_segment_1_r1 = right_shoulder_blade_2.addOrReplaceChild("right_shoulder_blade_2_segment_1_r1", CubeListBuilder.create().texOffs(9, 99).addBox(-0.594F, -4.1407F, -0.8F, 1.188F, 4.3607F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.496F, -4.048F, 0.0F, 0.0F, 0.0F, -0.1578F));

        PartDefinition right_shoulder_blade_2_segment_0_r1 = right_shoulder_blade_2.addOrReplaceChild("right_shoulder_blade_2_segment_0_r1", CubeListBuilder.create().texOffs(0, 99).addBox(-0.792F, -4.5356F, -0.8F, 1.584F, 4.7556F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.354F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.3F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, -0.1396F, 0.0F, -0.0349F));

        PartDefinition left_knee = left_leg.addOrReplaceChild("left_knee", CubeListBuilder.create().texOffs(0, 23).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(30, 66).addBox(-1.5F, -1.4F, -2.3F, 3.2F, 2.0F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.2793F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(17, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.3F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, -0.1396F, 0.0F, 0.0349F));

        PartDefinition right_knee = right_leg.addOrReplaceChild("right_knee", CubeListBuilder.create().texOffs(17, 23).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.2793F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
