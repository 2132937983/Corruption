package com.miophas.corruption.common.client.entity;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
final class GreedGeometry {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition stage_0 = partdefinition.addOrReplaceChild("stage_0", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_0_root_3_b_r1 = stage_0.addOrReplaceChild("stage_0_root_3_b_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.4F, -4.0791F, -0.4F, 0.8F, 4.2791F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6675F, -2.08F, -3.293F, 1.9594F, 0.2802F, -0.1887F));

        PartDefinition stage_0_root_3_a_r1 = stage_0.addOrReplaceChild("stage_0_root_3_a_r1", CubeListBuilder.create().texOffs(31, 0).addBox(-0.52F, -3.5941F, -0.52F, 1.04F, 3.7941F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.56F, 0.0F, 1.6698F, 0.2241F, -0.2031F));

        PartDefinition stage_0_root_2_b_r1 = stage_0.addOrReplaceChild("stage_0_root_2_b_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.4F, -4.0791F, -0.4F, 0.8F, 4.2791F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.293F, -2.08F, -0.6675F, 0.1887F, -0.2802F, 1.9594F));

        PartDefinition stage_0_root_2_a_r1 = stage_0.addOrReplaceChild("stage_0_root_2_a_r1", CubeListBuilder.create().texOffs(31, 0).addBox(-0.52F, -3.5941F, -0.52F, 1.04F, 3.7941F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.56F, 0.0F, 0.2031F, -0.2241F, 1.6698F));

        PartDefinition stage_0_root_1_b_r1 = stage_0.addOrReplaceChild("stage_0_root_1_b_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.4F, -4.0791F, -0.4F, 0.8F, 4.2791F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6675F, -2.08F, 3.293F, -1.9594F, 0.2802F, 0.1887F));

        PartDefinition stage_0_root_1_a_r1 = stage_0.addOrReplaceChild("stage_0_root_1_a_r1", CubeListBuilder.create().texOffs(31, 0).addBox(-0.52F, -3.5941F, -0.52F, 1.04F, 3.7941F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.56F, 0.0F, -1.6698F, 0.2241F, 0.2031F));

        PartDefinition stage_0_root_0_b_r1 = stage_0.addOrReplaceChild("stage_0_root_0_b_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.4F, -4.0791F, -0.4F, 0.8F, 4.2791F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.293F, -2.08F, 0.6675F, -0.1887F, -0.2802F, -1.9594F));

        PartDefinition stage_0_root_0_a_r1 = stage_0.addOrReplaceChild("stage_0_root_0_a_r1", CubeListBuilder.create().texOffs(31, 0).addBox(-0.52F, -3.5941F, -0.52F, 1.04F, 3.7941F, 1.04F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.56F, 0.0F, -0.2031F, -0.2241F, -1.6698F));

        PartDefinition stage_0_trunk_2_r1 = stage_0.addOrReplaceChild("stage_0_trunk_2_r1", CubeListBuilder.create().texOffs(22, 0).addBox(-0.792F, -5.3598F, -0.7616F, 1.584F, 5.5598F, 1.5232F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.64F, -13.76F, 0.0F, 0.0F, 0.0F, -0.1244F));

        PartDefinition stage_0_trunk_1_r1 = stage_0.addOrReplaceChild("stage_0_trunk_1_r1", CubeListBuilder.create().texOffs(13, 0).addBox(-0.996F, -7.0972F, -0.9408F, 1.992F, 7.2972F, 1.8816F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.88F, -7.04F, 0.32F, 0.0464F, -0.0052F, 0.2222F));

        PartDefinition stage_0_trunk_0_r1 = stage_0.addOrReplaceChild("stage_0_trunk_0_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.2F, -7.0047F, -1.12F, 2.4F, 7.2047F, 2.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 0.0F, -0.047F, -0.0031F, -0.1297F));

        PartDefinition stage_0_branch_0 = stage_0.addOrReplaceChild("stage_0_branch_0", CubeListBuilder.create(), PartPose.offset(0.0F, -18.88F, 0.0F));

        PartDefinition stage_0_neck_0 = stage_0_branch_0.addOrReplaceChild("stage_0_neck_0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_0_neck_0_section_0_r1 = stage_0_neck_0.addOrReplaceChild("stage_0_neck_0_section_0_r1", CubeListBuilder.create().texOffs(45, 0).addBox(-0.84F, -2.5278F, -0.8F, 1.68F, 2.7278F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1648F, 0.0F, 0.0F));

        PartDefinition stage_0_neck_0_joint_1 = stage_0_neck_0.addOrReplaceChild("stage_0_neck_0_joint_1", CubeListBuilder.create(), PartPose.offset(0.0F, -2.2962F, -0.3818F));

        PartDefinition stage_0_neck_0_section_1_r1 = stage_0_neck_0_joint_1.addOrReplaceChild("stage_0_neck_0_section_1_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-0.84F, -2.7214F, -0.6F, 1.68F, 2.9214F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7389F, 0.0F, 0.0F));

        PartDefinition stage_0_head_0 = stage_0_neck_0_joint_1.addOrReplaceChild("stage_0_head_0", CubeListBuilder.create().texOffs(63, 0).addBox(-1.52F, -2.048F, -1.2F, 3.04F, 2.464F, 2.48F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(0.4F, -1.28F, -1.568F, 0.928F, 0.576F, 0.4F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(0.576F, -1.088F, -1.664F, 0.576F, 0.256F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(-1.328F, -1.28F, -1.568F, 0.928F, 0.576F, 0.4F, new CubeDeformation(0.0F))
        .texOffs(114, 0).addBox(-1.152F, -1.088F, -1.664F, 0.576F, 0.256F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(131, 0).addBox(-0.432F, -1.024F, -1.968F, 0.864F, 0.96F, 0.976F, new CubeDeformation(0.0F))
        .texOffs(136, 0).addBox(-0.544F, -0.352F, -2.704F, 1.088F, 0.544F, 0.368F, new CubeDeformation(0.0F))
        .texOffs(143, 0).addBox(-1.2F, 0.8F, -2.288F, 2.4F, 0.448F, 1.328F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.8638F, -1.6982F));

        PartDefinition stage_0_head_0_crown_horn_1_1_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_crown_horn_1_1_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.144F, -1.1092F, -0.128F, 0.288F, 1.3092F, 0.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.472F, -3.104F, 1.024F, -0.689F, -0.0834F, -0.2316F));

        PartDefinition stage_0_head_0_crown_horn_1_0_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_crown_horn_1_0_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.208F, -1.7017F, -0.192F, 0.416F, 1.9017F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.04F, -1.76F, 0.512F, -0.3484F, -0.0518F, -0.2922F));

        PartDefinition stage_0_head_0_crown_horn_left_1_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_crown_horn_left_1_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.144F, -1.1092F, -0.128F, 0.288F, 1.3092F, 0.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.472F, -3.104F, 1.024F, -0.689F, 0.0834F, 0.2316F));

        PartDefinition stage_0_head_0_crown_horn_left_0_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_crown_horn_left_0_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.208F, -1.7017F, -0.192F, 0.416F, 1.9017F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.04F, -1.76F, 0.512F, -0.3484F, 0.0518F, 0.2922F));

        PartDefinition stage_0_head_0_mane_7_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_7_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.184F, -0.9429F, -0.192F, 0.368F, 1.1429F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5274F, 0.7403F, 0.288F, -2.2917F, -0.6856F, -0.3202F));

        PartDefinition stage_0_head_0_mane_7_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_7_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.2207F, -0.36F, 0.784F, 1.4207F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8825F, -0.0346F, 0.448F, 0.1728F, 0.4237F, -2.3758F));

        PartDefinition stage_0_head_0_mane_6_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_6_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.184F, -1.0534F, -0.192F, 0.368F, 1.2534F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.312F, 0.288F, -2.6108F, 0.0F, 0.0F));

        PartDefinition stage_0_head_0_mane_6_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_6_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.2681F, -0.36F, 0.784F, 1.4681F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.256F, 0.448F, 2.9912F, 0.0F, 0.0F));

        PartDefinition stage_0_head_0_mane_5_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_5_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.184F, -0.9429F, -0.192F, 0.368F, 1.1429F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5274F, 0.7403F, 0.288F, -2.2917F, 0.6856F, 0.3202F));

        PartDefinition stage_0_head_0_mane_5_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_5_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.2207F, -0.36F, 0.784F, 1.4207F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8825F, -0.0346F, 0.448F, 0.1728F, -0.4237F, 2.3758F));

        PartDefinition stage_0_head_0_mane_4_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_4_tip_r1", CubeListBuilder.create().texOffs(182, 0).addBox(-0.184F, -0.7376F, -0.192F, 0.368F, 0.9376F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.16F, -0.64F, 0.288F, -1.5763F, 0.6375F, 0.6343F));

        PartDefinition stage_0_head_0_mane_4_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_4_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.1309F, -0.36F, 0.784F, 1.3309F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.248F, -0.736F, 0.448F, 0.1759F, -0.1889F, 1.6423F));

        PartDefinition stage_0_head_0_mane_3_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_3_tip_r1", CubeListBuilder.create().texOffs(182, 0).addBox(-0.184F, -0.7065F, -0.192F, 0.368F, 0.9065F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5274F, -2.0203F, 0.288F, -1.0645F, 0.2227F, 0.3752F));

        PartDefinition stage_0_head_0_mane_3_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_3_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.0838F, -0.36F, 0.784F, 1.2838F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8825F, -1.4374F, 0.448F, 0.1826F, -0.0797F, 0.8212F));

        PartDefinition stage_0_head_0_mane_2_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_2_tip_r1", CubeListBuilder.create().texOffs(182, 0).addBox(-0.184F, -0.7573F, -0.192F, 0.368F, 0.9573F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.592F, 0.288F, -0.8871F, 0.0F, 0.0F));

        PartDefinition stage_0_head_0_mane_2_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_2_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.0787F, -0.36F, 0.784F, 1.2787F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.728F, 0.448F, 0.1831F, 0.0F, 0.0F));

        PartDefinition stage_0_head_0_mane_1_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_1_tip_r1", CubeListBuilder.create().texOffs(182, 0).addBox(-0.184F, -0.7065F, -0.192F, 0.368F, 0.9065F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5274F, -2.0203F, 0.288F, -1.0645F, -0.2227F, -0.3752F));

        PartDefinition stage_0_head_0_mane_1_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_1_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.0838F, -0.36F, 0.784F, 1.2838F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8825F, -1.4374F, 0.448F, 0.1826F, 0.0797F, -0.8212F));

        PartDefinition stage_0_head_0_mane_0_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_0_tip_r1", CubeListBuilder.create().texOffs(182, 0).addBox(-0.184F, -0.7376F, -0.192F, 0.368F, 0.9376F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.16F, -0.64F, 0.288F, -1.5763F, -0.6375F, -0.6343F));

        PartDefinition stage_0_head_0_mane_0_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_mane_0_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.392F, -1.1309F, -0.36F, 0.784F, 1.3309F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.248F, -0.736F, 0.448F, 0.1759F, 0.1889F, -1.6423F));

        PartDefinition stage_0_head_0_incisor_2_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_incisor_2_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.072F, -0.6999F, -0.08F, 0.144F, 0.8999F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(172, 0).addBox(0.216F, -0.6999F, -0.08F, 0.144F, 0.8999F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(172, 0).addBox(0.504F, -0.6999F, -0.08F, 0.144F, 0.8999F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.288F, 0.896F, -2.144F, 2.4469F, 0.0F, 0.0F));

        PartDefinition stage_0_head_0_canine_1_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_canine_1_tip_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.032F, -0.8674F, -0.04F, 0.064F, 1.0674F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.912F, 1.504F, -2.08F, 2.2398F, -1.1287F, 0.5947F));

        PartDefinition stage_0_head_0_canine_1_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_canine_1_root_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.128F, -0.9963F, -0.144F, 0.256F, 1.1963F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.944F, 0.8F, -1.888F, 2.8647F, -0.2997F, 0.0421F));

        PartDefinition stage_0_head_0_canine_left_tip_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_canine_left_tip_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.032F, -0.8674F, -0.04F, 0.064F, 1.0674F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.912F, 1.504F, -2.08F, 2.2398F, 1.1287F, -0.5947F));

        PartDefinition stage_0_head_0_canine_left_root_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_canine_left_root_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.128F, -0.9963F, -0.144F, 0.256F, 1.1963F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.944F, 0.8F, -1.888F, 2.8647F, 0.2997F, -0.0421F));

        PartDefinition stage_0_head_0_ear_inner_1_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_ear_inner_1_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.24F, -0.928F, -0.176F, 0.48F, 0.576F, 0.128F, new CubeDeformation(0.0F))
        .texOffs(126, 0).addBox(-0.4F, -1.152F, -0.096F, 0.8F, 1.184F, 0.656F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.504F, -1.376F, 0.32F, 0.0F, 0.0F, 0.384F));

        PartDefinition stage_0_head_0_brow_1_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_brow_1_r1", CubeListBuilder.create().texOffs(119, 0).addBox(-0.608F, -0.128F, -0.448F, 1.216F, 0.416F, 0.496F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.864F, -1.44F, -1.28F, 0.0F, 0.0F, -0.2094F));

        PartDefinition stage_0_head_0_muzzle_lobe_1_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_muzzle_lobe_1_r1", CubeListBuilder.create().texOffs(100, 0).addBox(-0.672F, -0.544F, -1.36F, 1.344F, 1.344F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.608F, 0.32F, -1.12F, 0.0F, 0.1396F, 0.0F));

        PartDefinition stage_0_head_0_cheek_1_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_cheek_1_r1", CubeListBuilder.create().texOffs(89, 0).addBox(-0.56F, -0.864F, -1.376F, 1.12F, 1.92F, 2.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, 0.0F, 0.0F, 0.0F, -0.1745F, -0.1571F));

        PartDefinition stage_0_head_0_ear_inner_left_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_ear_inner_left_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.24F, -0.928F, -0.176F, 0.48F, 0.576F, 0.128F, new CubeDeformation(0.0F))
        .texOffs(126, 0).addBox(-0.4F, -1.152F, -0.096F, 0.8F, 1.184F, 0.656F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.504F, -1.376F, 0.32F, 0.0F, 0.0F, -0.384F));

        PartDefinition stage_0_head_0_brow_left_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_brow_left_r1", CubeListBuilder.create().texOffs(119, 0).addBox(-0.608F, -0.128F, -0.448F, 1.216F, 0.416F, 0.496F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.864F, -1.44F, -1.28F, 0.0F, 0.0F, 0.2094F));

        PartDefinition stage_0_head_0_muzzle_lobe_left_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_muzzle_lobe_left_r1", CubeListBuilder.create().texOffs(100, 0).addBox(-0.672F, -0.544F, -1.36F, 1.344F, 1.344F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.608F, 0.32F, -1.12F, 0.0F, -0.1396F, 0.0F));

        PartDefinition stage_0_head_0_cheek_left_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_cheek_left_r1", CubeListBuilder.create().texOffs(89, 0).addBox(-0.56F, -0.864F, -1.376F, 1.12F, 1.92F, 2.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.1571F));

        PartDefinition stage_0_head_0_forehead_r1 = stage_0_head_0.addOrReplaceChild("stage_0_head_0_forehead_r1", CubeListBuilder.create().texOffs(78, 0).addBox(-1.216F, -1.312F, -1.472F, 2.432F, 1.408F, 1.904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.12F, 0.0F, -0.1222F, 0.0F, 0.0F));

        PartDefinition stage_0_jaw_0 = stage_0_head_0.addOrReplaceChild("stage_0_jaw_0", CubeListBuilder.create().texOffs(154, 0).addBox(-1.104F, 0.672F, -2.72F, 2.208F, 0.864F, 2.72F, new CubeDeformation(0.0F))
        .texOffs(143, 0).addBox(-1.024F, 0.544F, -2.592F, 2.048F, 0.224F, 1.472F, new CubeDeformation(0.0F))
        .texOffs(126, 0).addBox(0.592F, -0.896F, -0.32F, 0.416F, 1.984F, 0.672F, new CubeDeformation(0.0F))
        .texOffs(126, 0).addBox(-1.008F, -0.896F, -0.32F, 0.416F, 1.984F, 0.672F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.576F, 0.4F));

        PartDefinition stage_0_head_0_lower_canine_1_r1 = stage_0_jaw_0.addOrReplaceChild("stage_0_head_0_lower_canine_1_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.096F, -0.7887F, -0.096F, 0.192F, 0.9887F, 0.192F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.736F, 0.8F, -2.368F, 0.1914F, -0.0078F, 0.0816F));

        PartDefinition stage_0_head_0_lower_canine_left_r1 = stage_0_jaw_0.addOrReplaceChild("stage_0_head_0_lower_canine_left_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.096F, -0.7887F, -0.096F, 0.192F, 0.9887F, 0.192F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.736F, 0.8F, -2.368F, 0.1914F, 0.0078F, -0.0816F));

        PartDefinition stage_0_branch_1 = stage_0.addOrReplaceChild("stage_0_branch_1", CubeListBuilder.create(), PartPose.offset(0.0F, -18.88F, 0.0F));

        PartDefinition stage_0_neck_1 = stage_0_branch_1.addOrReplaceChild("stage_0_neck_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_0_neck_1_section_0_r1 = stage_0_neck_1.addOrReplaceChild("stage_0_neck_1_section_0_r1", CubeListBuilder.create().texOffs(31, 0).addBox(-0.84F, -3.2304F, -0.8F, 1.68F, 3.4304F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0885F, 0.0577F, 1.156F));

        PartDefinition stage_0_neck_1_joint_1 = stage_0_neck_1.addOrReplaceChild("stage_0_neck_1_joint_1", CubeListBuilder.create(), PartPose.offset(2.7687F, -1.2024F, 0.2673F));

        PartDefinition stage_0_neck_1_section_1_r1 = stage_0_neck_1_joint_1.addOrReplaceChild("stage_0_neck_1_section_1_r1", CubeListBuilder.create().texOffs(192, 0).addBox(-0.6F, -3.212F, -0.6F, 1.2F, 3.412F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5168F, -0.1672F, 0.6141F));

        PartDefinition stage_0_head_1 = stage_0_neck_1_joint_1.addOrReplaceChild("stage_0_head_1", CubeListBuilder.create().texOffs(54, 0).addBox(-0.992F, -2.304F, -0.768F, 1.984F, 2.496F, 1.792F, new CubeDeformation(0.0F))
        .texOffs(100, 0).addBox(-0.784F, 0.832F, -2.624F, 1.568F, 1.12F, 1.264F, new CubeDeformation(0.0F))
        .texOffs(136, 0).addBox(-0.56F, 0.864F, -2.72F, 1.12F, 0.576F, 0.384F, new CubeDeformation(0.0F)), PartPose.offset(1.7113F, -1.9976F, -1.4673F));

        PartDefinition stage_0_head_1_tooth_1_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_tooth_1_r1", CubeListBuilder.create().texOffs(172, 0).addBox(-0.072F, -0.6929F, -0.08F, 0.144F, 0.8929F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(172, 0).addBox(0.696F, -0.6929F, -0.08F, 0.144F, 0.8929F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.384F, 1.44F, -2.24F, 2.9124F, 0.0F, 0.0F));

        PartDefinition stage_0_head_1_ram_horn_1_2_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ram_horn_1_2_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.176F, -1.206F, -0.16F, 0.352F, 1.406F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.08F, -4.192F, 2.016F, -1.7555F, -1.0266F, -0.8757F));

        PartDefinition stage_0_head_1_ram_horn_1_1_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ram_horn_1_1_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.232F, -1.6283F, -0.216F, 0.464F, 1.8283F, 0.432F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.248F, -3.616F, 1.008F, -0.8303F, -0.2973F, -0.6551F));

        PartDefinition stage_0_head_1_ram_horn_1_0_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ram_horn_1_0_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.288F, -1.9534F, -0.272F, 0.576F, 2.1534F, 0.544F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.752F, -2.048F, 0.4F, -0.3546F, -0.0518F, -0.2872F));

        PartDefinition stage_0_head_1_ear_1_tip_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ear_1_tip_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.144F, -0.9871F, -0.12F, 0.288F, 1.1871F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.408F, -0.768F, 0.48F, 0.1257F, 0.2323F, -2.1524F));

        PartDefinition stage_0_head_1_ear_1_base_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ear_1_base_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.312F, -1.045F, -0.208F, 0.624F, 1.245F, 0.416F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.736F, -1.024F, 0.24F, -0.3384F, -0.4214F, -1.7935F));

        PartDefinition stage_0_head_1_brow_1_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_brow_1_r1", CubeListBuilder.create().texOffs(213, 0).addBox(-1.104F, -1.76F, -1.76F, 0.8F, 0.32F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition stage_0_head_1_eye_1_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_eye_1_r1", CubeListBuilder.create().texOffs(114, 0).addBox(-0.944F, -1.184F, -1.792F, 0.448F, 0.16F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(-1.024F, -1.44F, -1.68F, 0.608F, 0.608F, 0.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_0_head_1_ram_horn_left_2_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ram_horn_left_2_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.176F, -1.206F, -0.16F, 0.352F, 1.406F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.08F, -4.192F, 2.016F, -1.7555F, 1.0266F, 0.8757F));

        PartDefinition stage_0_head_1_ram_horn_left_1_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ram_horn_left_1_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.232F, -1.6283F, -0.216F, 0.464F, 1.8283F, 0.432F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.248F, -3.616F, 1.008F, -0.8303F, 0.2973F, 0.6551F));

        PartDefinition stage_0_head_1_ram_horn_left_0_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ram_horn_left_0_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.288F, -1.9534F, -0.272F, 0.576F, 2.1534F, 0.544F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.752F, -2.048F, 0.4F, -0.3546F, 0.0518F, 0.2872F));

        PartDefinition stage_0_head_1_ear_left_tip_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ear_left_tip_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.144F, -0.9871F, -0.12F, 0.288F, 1.1871F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.408F, -0.768F, 0.48F, 0.1257F, -0.2323F, 2.1524F));

        PartDefinition stage_0_head_1_ear_left_base_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_ear_left_base_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.312F, -1.045F, -0.208F, 0.624F, 1.245F, 0.416F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.736F, -1.024F, 0.24F, -0.3384F, 0.4214F, 1.7935F));

        PartDefinition stage_0_head_1_brow_left_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_brow_left_r1", CubeListBuilder.create().texOffs(213, 0).addBox(0.304F, -1.76F, -1.76F, 0.8F, 0.32F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition stage_0_head_1_eye_left_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_eye_left_r1", CubeListBuilder.create().texOffs(114, 0).addBox(0.496F, -1.184F, -1.792F, 0.448F, 0.16F, 0.16F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(0.416F, -1.44F, -1.68F, 0.608F, 0.608F, 0.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_0_head_1_nasal_bridge_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_nasal_bridge_r1", CubeListBuilder.create().texOffs(208, 0).addBox(-0.496F, -0.608F, -0.816F, 0.992F, 2.208F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.576F, -1.088F, -0.2269F, 0.0F, 0.0F));

        PartDefinition stage_0_head_1_long_face_r1 = stage_0_head_1.addOrReplaceChild("stage_0_head_1_long_face_r1", CubeListBuilder.create().texOffs(201, 0).addBox(-0.72F, -0.352F, -0.72F, 1.44F, 3.168F, 0.976F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.28F, -0.72F, -0.2618F, 0.0F, 0.0F));

        PartDefinition stage_0_jaw_1 = stage_0_head_1.addOrReplaceChild("stage_0_jaw_1", CubeListBuilder.create().texOffs(223, 0).addBox(-0.688F, 0.672F, -2.64F, 1.376F, 0.736F, 2.72F, new CubeDeformation(0.0F))
        .texOffs(234, 0).addBox(-0.656F, 0.384F, -2.784F, 1.312F, 0.384F, 1.2F, new CubeDeformation(0.0F))
        .texOffs(208, 0).addBox(0.336F, -1.248F, -0.24F, 0.384F, 2.272F, 0.688F, new CubeDeformation(0.0F))
        .texOffs(208, 0).addBox(-0.72F, -1.248F, -0.24F, 0.384F, 2.272F, 0.688F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.12F, 0.16F));

        PartDefinition stage_0_head_1_beard_tip_r1 = stage_0_jaw_1.addOrReplaceChild("stage_0_head_1_beard_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.072F, -1.1092F, -0.08F, 0.144F, 1.3092F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.144F, -1.968F, -1.7986F, -1.3337F, -1.118F));

        PartDefinition stage_0_head_1_beard_root_r1 = stage_0_jaw_1.addOrReplaceChild("stage_0_head_1_beard_root_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.312F, -1.453F, -0.224F, 0.624F, 1.653F, 0.448F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.96F, -2.08F, -3.0521F, 0.0F, 0.0F));

        PartDefinition stage_0_branch_2 = stage_0.addOrReplaceChild("stage_0_branch_2", CubeListBuilder.create(), PartPose.offset(0.0F, -18.88F, 0.0F));

        PartDefinition stage_0_neck_2 = stage_0_branch_2.addOrReplaceChild("stage_0_neck_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_0_neck_2_section_0_r1 = stage_0_neck_2.addOrReplaceChild("stage_0_neck_2_section_0_r1", CubeListBuilder.create().texOffs(243, 0).addBox(-0.84F, -4.0907F, -0.8F, 1.68F, 4.2907F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3612F, -0.0873F, -0.4696F));

        PartDefinition stage_0_neck_2_joint_1 = stage_0_neck_2.addOrReplaceChild("stage_0_neck_2_joint_1", CubeListBuilder.create(), PartPose.offset(-1.7541F, -3.1913F, 1.3699F));

        PartDefinition stage_0_neck_2_section_1_r1 = stage_0_neck_2_joint_1.addOrReplaceChild("stage_0_neck_2_section_1_r1", CubeListBuilder.create().texOffs(252, 0).addBox(-0.7F, -4.0841F, -0.7F, 1.4F, 4.2841F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2583F, -0.0545F, -0.4135F));

        PartDefinition stage_0_neck_2_joint_2 = stage_0_neck_2_joint_1.addOrReplaceChild("stage_0_neck_2_joint_2", CubeListBuilder.create(), PartPose.offset(-1.5585F, -3.417F, 0.9906F));

        PartDefinition stage_0_head_2_hood_1_edge_r1 = stage_0_neck_2_joint_2.addOrReplaceChild("stage_0_head_2_hood_1_edge_r1", CubeListBuilder.create().texOffs(321, 0).addBox(-0.216F, -1.8317F, -0.176F, 0.432F, 2.0317F, 0.352F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6714F, -1.6477F, -0.2165F, -0.518F, 1.1635F, 2.3757F));

        PartDefinition stage_0_head_2_hood_1_root_r1 = stage_0_neck_2_joint_2.addOrReplaceChild("stage_0_head_2_hood_1_root_r1", CubeListBuilder.create().texOffs(321, 0).addBox(-0.376F, -1.9015F, -0.272F, 0.752F, 2.1015F, 0.544F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4554F, -2.7357F, -0.4405F, -0.1382F, -0.2996F, -2.2816F));

        PartDefinition stage_0_head_2_hood_left_edge_r1 = stage_0_neck_2_joint_2.addOrReplaceChild("stage_0_head_2_hood_left_edge_r1", CubeListBuilder.create().texOffs(321, 0).addBox(-0.216F, -1.8317F, -0.176F, 0.432F, 2.0317F, 0.352F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6566F, -1.6477F, -0.2165F, -0.518F, -1.1635F, -2.3757F));

        PartDefinition stage_0_head_2_hood_left_root_r1 = stage_0_neck_2_joint_2.addOrReplaceChild("stage_0_head_2_hood_left_root_r1", CubeListBuilder.create().texOffs(321, 0).addBox(-0.376F, -1.9015F, -0.272F, 0.752F, 2.1015F, 0.544F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5594F, -2.7357F, -0.4405F, -0.1382F, 0.2996F, 2.2816F));

        PartDefinition stage_0_neck_2_section_2_r1 = stage_0_neck_2_joint_2.addOrReplaceChild("stage_0_neck_2_section_2_r1", CubeListBuilder.create().texOffs(252, 0).addBox(-0.56F, -3.8509F, -0.6F, 1.12F, 4.0509F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3238F, 0.046F, -0.2799F));

        PartDefinition stage_0_head_2 = stage_0_neck_2_joint_2.addOrReplaceChild("stage_0_head_2", CubeListBuilder.create().texOffs(261, 0).addBox(-1.104F, -1.376F, -0.352F, 2.208F, 1.888F, 1.472F, new CubeDeformation(0.0F))
        .texOffs(287, 0).addBox(-0.864F, -1.248F, -2.8F, 1.728F, 0.736F, 1.648F, new CubeDeformation(0.0F))
        .texOffs(296, 0).addBox(-1.088F, 0.224F, -2.864F, 2.176F, 0.288F, 2.576F, new CubeDeformation(0.0F)), PartPose.offset(-1.0074F, -3.3117F, -1.1605F));

        PartDefinition stage_0_head_2_crown_tip_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_crown_tip_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.032F, -0.8376F, -0.032F, 0.064F, 1.0376F, 0.064F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.208F, -2.848F, 0.736F, -1.473F, -0.2903F, -0.3197F));

        PartDefinition stage_0_head_2_crown_crest_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_crown_crest_r1", CubeListBuilder.create().texOffs(321, 0).addBox(-0.24F, -2.2332F, -0.2F, 0.48F, 2.4332F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.024F, 0.4F, -0.19F, -0.0105F, -0.1104F));

        PartDefinition stage_0_head_2_fang_1_tip_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_fang_1_tip_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.04F, -1.1755F, -0.04F, 0.08F, 1.3755F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, 1.216F, -2.224F, -2.705F, 0.8508F, 0.2003F));

        PartDefinition stage_0_head_2_fang_1_root_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_fang_1_root_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.136F, -1.1983F, -0.136F, 0.272F, 1.3983F, 0.272F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.832F, 0.32F, -1.968F, 2.858F, -0.2296F, 0.0329F));

        PartDefinition stage_0_head_2_brow_1_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_brow_1_r1", CubeListBuilder.create().texOffs(316, 0).addBox(-1.36F, -1.632F, -1.84F, 0.864F, 0.352F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition stage_0_head_2_eye_1_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_eye_1_r1", CubeListBuilder.create().texOffs(114, 0).addBox(-1.168F, -1.248F, -1.872F, 0.384F, 0.288F, 0.224F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(-1.28F, -1.408F, -1.76F, 0.608F, 0.576F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_0_head_2_cheek_1_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_cheek_1_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.432F, -1.344F, -1.12F, 0.864F, 1.696F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.992F, 0.0F, 0.0F, 0.0F, -0.2443F, -0.0873F));

        PartDefinition stage_0_head_2_fang_left_tip_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_fang_left_tip_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.04F, -1.1755F, -0.04F, 0.08F, 1.3755F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8F, 1.216F, -2.224F, -2.705F, -0.8508F, -0.2003F));

        PartDefinition stage_0_head_2_fang_left_root_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_fang_left_root_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.136F, -1.1983F, -0.136F, 0.272F, 1.3983F, 0.272F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.832F, 0.32F, -1.968F, 2.858F, 0.2296F, -0.0329F));

        PartDefinition stage_0_head_2_brow_left_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_brow_left_r1", CubeListBuilder.create().texOffs(316, 0).addBox(0.496F, -1.632F, -1.84F, 0.864F, 0.352F, 0.56F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        PartDefinition stage_0_head_2_eye_left_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_eye_left_r1", CubeListBuilder.create().texOffs(114, 0).addBox(0.784F, -1.248F, -1.872F, 0.384F, 0.288F, 0.224F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(0.672F, -1.408F, -1.76F, 0.608F, 0.576F, 0.288F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_0_head_2_cheek_left_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_cheek_left_r1", CubeListBuilder.create().texOffs(309, 0).addBox(-0.432F, -1.344F, -1.12F, 0.864F, 1.696F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.992F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0873F));

        PartDefinition stage_0_head_2_upper_jaw_r1 = stage_0_head_2.addOrReplaceChild("stage_0_head_2_upper_jaw_r1", CubeListBuilder.create().texOffs(272, 0).addBox(-1.328F, -0.864F, -3.2F, 2.656F, 1.152F, 3.168F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.24F, -0.0698F, 0.0F, 0.0F));

        PartDefinition stage_0_jaw_2 = stage_0_head_2.addOrReplaceChild("stage_0_jaw_2", CubeListBuilder.create().texOffs(326, 0).addBox(-1.072F, 0.192F, -3.232F, 2.144F, 0.8F, 3.456F, new CubeDeformation(0.0F))
        .texOffs(341, 0).addBox(-0.944F, -0.032F, -3.024F, 1.888F, 0.288F, 2.544F, new CubeDeformation(0.0F))
        .texOffs(352, 0).addBox(0.592F, -0.736F, -0.288F, 0.352F, 1.376F, 0.512F, new CubeDeformation(0.0F))
        .texOffs(352, 0).addBox(-0.944F, -0.736F, -0.288F, 0.352F, 1.376F, 0.512F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.512F, 0.368F));

        PartDefinition stage_0_head_2_fork_tongue_1_r1 = stage_0_jaw_2.addOrReplaceChild("stage_0_head_2_fork_tongue_1_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.024F, -1.1154F, -0.024F, 0.048F, 1.3154F, 0.048F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.448F, -3.712F, 1.5372F, 0.5414F, -0.559F));

        PartDefinition stage_0_head_2_fork_tongue_left_r1 = stage_0_jaw_2.addOrReplaceChild("stage_0_head_2_fork_tongue_left_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.024F, -1.1154F, -0.024F, 0.048F, 1.3154F, 0.048F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.448F, -3.712F, 1.5372F, -0.5414F, 0.559F));

        PartDefinition stage_0_head_2_tongue_root_r1 = stage_0_jaw_2.addOrReplaceChild("stage_0_head_2_tongue_root_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.08F, -1.6999F, -0.064F, 0.16F, 1.8999F, 0.128F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.192F, -2.288F, 1.764F, 0.0F, 0.0F));

        PartDefinition stage_1 = partdefinition.addOrReplaceChild("stage_1", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_1_thorn_1_tip_r1 = stage_1.addOrReplaceChild("stage_1_thorn_1_tip_r1", CubeListBuilder.create().texOffs(476, 21).addBox(-0.125F, -5.7633F, -0.125F, 0.25F, 5.9633F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.96F, -23.04F, 3.84F, 0.1595F, 0.4387F, -2.4531F));

        PartDefinition stage_1_thorn_1_a_r1 = stage_1.addOrReplaceChild("stage_1_thorn_1_a_r1", CubeListBuilder.create().texOffs(467, 21).addBox(-0.875F, -11.5228F, -0.85F, 1.75F, 11.7228F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.28F, 0.0F, -0.3519F, -0.1788F, -0.9342F));

        PartDefinition stage_1_thorn_0_tip_r1 = stage_1.addOrReplaceChild("stage_1_thorn_0_tip_r1", CubeListBuilder.create().texOffs(476, 21).addBox(-0.125F, -5.7633F, -0.125F, 0.25F, 5.9633F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.96F, -18.56F, -3.84F, 0.1595F, -0.4387F, 2.4531F));

        PartDefinition stage_1_thorn_0_a_r1 = stage_1.addOrReplaceChild("stage_1_thorn_0_a_r1", CubeListBuilder.create().texOffs(467, 21).addBox(-0.875F, -11.5228F, -0.85F, 1.75F, 11.7228F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_1_root_4_b_r1 = stage_1.addOrReplaceChild("stage_1_root_4_b_r1", CubeListBuilder.create().texOffs(412, 0).addBox(-0.525F, -8.1577F, -0.525F, 1.05F, 8.3577F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3049F, -4.16F, -5.8511F, 1.7146F, 0.6788F, -0.5932F));

        PartDefinition stage_1_root_4_a_r1 = stage_1.addOrReplaceChild("stage_1_root_4_a_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.04F, -6.9882F, -1.04F, 2.08F, 7.1882F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, 0.0F, 1.4158F, 0.5107F, -0.5921F));

        PartDefinition stage_1_root_3_b_r1 = stage_1.addOrReplaceChild("stage_1_root_3_b_r1", CubeListBuilder.create().texOffs(412, 0).addBox(-0.525F, -8.1577F, -0.525F, 1.05F, 8.3577F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5435F, -4.16F, -4.9513F, 1.2674F, -0.8189F, 1.067F));

        PartDefinition stage_1_root_3_a_r1 = stage_1.addOrReplaceChild("stage_1_root_3_a_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.04F, -6.9882F, -1.04F, 2.08F, 7.1882F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, 0.0F, 1.0901F, -0.6048F, 0.9504F));

        PartDefinition stage_1_root_2_b_r1 = stage_1.addOrReplaceChild("stage_1_root_2_b_r1", CubeListBuilder.create().texOffs(412, 0).addBox(-0.525F, -8.1577F, -0.525F, 1.05F, 8.3577F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.113F, -4.16F, 2.7911F, -0.4571F, 0.5871F, 1.8299F));

        PartDefinition stage_1_root_2_a_r1 = stage_1.addOrReplaceChild("stage_1_root_2_a_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.04F, -6.9882F, -1.04F, 2.08F, 7.1882F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, 0.0F, -0.4732F, 0.4459F, 1.5093F));

        PartDefinition stage_1_root_1_b_r1 = stage_1.addOrReplaceChild("stage_1_root_1_b_r1", CubeListBuilder.create().texOffs(412, 0).addBox(-0.525F, -8.1577F, -0.525F, 1.05F, 8.3577F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7655F, -4.16F, 6.6763F, -2.0415F, -0.1667F, -0.1024F));

        PartDefinition stage_1_root_1_a_r1 = stage_1.addOrReplaceChild("stage_1_root_1_a_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.04F, -6.9882F, -1.04F, 2.08F, 7.1882F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, 0.0F, -1.6988F, -0.1295F, -0.114F));

        PartDefinition stage_1_root_0_b_r1 = stage_1.addOrReplaceChild("stage_1_root_0_b_r1", CubeListBuilder.create().texOffs(412, 0).addBox(-0.525F, -8.1577F, -0.525F, 1.05F, 8.3577F, 1.05F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.586F, -4.16F, 1.3351F, -0.1844F, -0.2895F, -2.0109F));

        PartDefinition stage_1_root_0_a_r1 = stage_1.addOrReplaceChild("stage_1_root_0_a_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.04F, -6.9882F, -1.04F, 2.08F, 7.1882F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, 0.0F, -0.2031F, -0.2241F, -1.6698F));

        PartDefinition stage_1_trunk_2_r1 = stage_1.addOrReplaceChild("stage_1_trunk_2_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.584F, -10.5197F, -1.5232F, 3.168F, 10.7197F, 3.0464F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.28F, -27.52F, 0.0F, 0.0F, 0.0F, -0.1244F));

        PartDefinition stage_1_trunk_1_r1 = stage_1.addOrReplaceChild("stage_1_trunk_1_r1", CubeListBuilder.create().texOffs(378, 0).addBox(-1.992F, -13.9944F, -1.8816F, 3.984F, 14.1944F, 3.7632F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.76F, -14.08F, 0.64F, 0.0464F, -0.0052F, 0.2222F));

        PartDefinition stage_1_trunk_0_r1 = stage_1.addOrReplaceChild("stage_1_trunk_0_r1", CubeListBuilder.create().texOffs(357, 0).addBox(-2.4F, -14.1067F, -2.24F, 4.8F, 14.3067F, 4.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 0.0F, -0.046F, -0.0029F, -0.1269F));

        PartDefinition stage_1_branch_0 = stage_1.addOrReplaceChild("stage_1_branch_0", CubeListBuilder.create(), PartPose.offset(0.0F, -37.76F, 0.0F));

        PartDefinition stage_1_neck_0 = stage_1_branch_0.addOrReplaceChild("stage_1_neck_0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_1_neck_0_section_0_r1 = stage_1_neck_0.addOrReplaceChild("stage_1_neck_0_section_0_r1", CubeListBuilder.create().texOffs(421, 0).addBox(-1.68F, -3.4323F, -1.6F, 3.36F, 3.6323F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.065F, 0.0F, 0.0F));

        PartDefinition stage_1_neck_0_joint_1 = stage_1_neck_0.addOrReplaceChild("stage_1_neck_0_joint_1", CubeListBuilder.create(), PartPose.offset(0.0F, -3.2254F, 0.2101F));

        PartDefinition stage_1_neck_0_section_1_r1 = stage_1_neck_0_joint_1.addOrReplaceChild("stage_1_neck_0_section_1_r1", CubeListBuilder.create().texOffs(438, 0).addBox(-1.68F, -3.5539F, -1.4F, 3.36F, 3.7539F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6771F, 0.0F, 0.0F));

        PartDefinition stage_1_neck_0_joint_2 = stage_1_neck_0_joint_1.addOrReplaceChild("stage_1_neck_0_joint_2", CubeListBuilder.create(), PartPose.offset(0.0F, -2.6141F, -2.1012F));

        PartDefinition stage_1_neck_0_section_2_r1 = stage_1_neck_0_joint_2.addOrReplaceChild("stage_1_neck_0_section_2_r1", CubeListBuilder.create().texOffs(438, 0).addBox(-1.68F, -3.5617F, -1.2F, 3.36F, 3.7617F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7409F, 0.0F, 0.0F));

        PartDefinition stage_1_head_0 = stage_1_neck_0_joint_2.addOrReplaceChild("stage_1_head_0", CubeListBuilder.create().texOffs(453, 0).addBox(-3.04F, -4.096F, -2.4F, 6.08F, 4.928F, 4.96F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(0.8F, -2.56F, -3.136F, 1.856F, 1.152F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(39, 21).addBox(1.152F, -2.176F, -3.328F, 1.152F, 0.512F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(-2.656F, -2.56F, -3.136F, 1.856F, 1.152F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(39, 21).addBox(-2.304F, -2.176F, -3.328F, 1.152F, 0.512F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(100, 0).addBox(-0.864F, -2.048F, -3.936F, 1.728F, 1.92F, 1.952F, new CubeDeformation(0.0F))
        .texOffs(60, 21).addBox(-1.088F, -0.704F, -5.408F, 2.176F, 1.088F, 0.736F, new CubeDeformation(0.0F))
        .texOffs(69, 21).addBox(-2.4F, 1.6F, -4.576F, 4.8F, 0.896F, 2.656F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.4805F, -2.2689F));

        PartDefinition stage_1_head_0_crown_horn_1_2_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_crown_horn_1_2_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.08F, -1.5805F, -0.08F, 0.16F, 1.7805F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.36F, -7.552F, 3.2F, -1.7161F, 0.4186F, 0.3631F));

        PartDefinition stage_1_head_0_crown_horn_1_1_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_crown_horn_1_1_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.288F, -2.0184F, -0.256F, 0.576F, 2.2184F, 0.512F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.944F, -6.208F, 2.048F, -0.689F, -0.0834F, -0.2316F));

        PartDefinition stage_1_head_0_crown_horn_1_0_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_crown_horn_1_0_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.416F, -3.2034F, -0.384F, 0.832F, 3.4034F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.08F, -3.52F, 1.024F, -0.3484F, -0.0518F, -0.2922F));

        PartDefinition stage_1_head_0_crown_horn_left_2_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_crown_horn_left_2_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.08F, -1.5805F, -0.08F, 0.16F, 1.7805F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.36F, -7.552F, 3.2F, -1.7161F, -0.4186F, -0.3631F));

        PartDefinition stage_1_head_0_crown_horn_left_1_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_crown_horn_left_1_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.288F, -2.0184F, -0.256F, 0.576F, 2.2184F, 0.512F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.944F, -6.208F, 2.048F, -0.689F, 0.0834F, 0.2316F));

        PartDefinition stage_1_head_0_crown_horn_left_0_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_crown_horn_left_0_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.416F, -3.2034F, -0.384F, 0.832F, 3.4034F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.08F, -3.52F, 1.024F, -0.3484F, 0.0518F, 0.2922F));

        PartDefinition stage_1_head_0_mane_9_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_9_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.602F, -0.384F, 0.736F, 1.802F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.495F, 1.0147F, 0.576F, -2.1541F, -0.7402F, -0.4117F));

        PartDefinition stage_1_head_0_mane_9_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_9_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -2.2059F, -0.72F, 1.568F, 2.4059F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0193F, -0.3058F, 0.896F, 0.1707F, 0.3506F, -2.2417F));

        PartDefinition stage_1_head_0_mane_8_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_8_tip_r1", CubeListBuilder.create().texOffs(125, 21).addBox(-0.368F, -1.8686F, -0.384F, 0.736F, 2.0686F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.335F, 2.4329F, 0.576F, -2.5615F, -0.334F, -0.1005F));

        PartDefinition stage_1_head_0_mane_8_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_8_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -2.3196F, -0.72F, 1.568F, 2.5196F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7713F, 0.4149F, 0.896F, 0.2804F, 0.9938F, -2.6324F));

        PartDefinition stage_1_head_0_mane_7_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_7_tip_r1", CubeListBuilder.create().texOffs(125, 21).addBox(-0.368F, -1.8686F, -0.384F, 0.736F, 2.0686F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.335F, 2.4329F, 0.576F, -2.5615F, 0.334F, 0.1005F));

        PartDefinition stage_1_head_0_mane_7_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_7_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -2.3196F, -0.72F, 1.568F, 2.5196F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7713F, 0.4149F, 0.896F, 0.2804F, -0.9938F, 2.6324F));

        PartDefinition stage_1_head_0_mane_6_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_6_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.602F, -0.384F, 0.736F, 1.802F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.495F, 1.0147F, 0.576F, -2.1541F, 0.7402F, 0.4117F));

        PartDefinition stage_1_head_0_mane_6_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_6_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -2.2059F, -0.72F, 1.568F, 2.4059F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0193F, -0.3058F, 0.896F, 0.1707F, -0.3506F, 2.2417F));

        PartDefinition stage_1_head_0_mane_5_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_5_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.2752F, -0.384F, 0.736F, 1.4752F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.32F, -1.28F, 0.576F, -1.5763F, 0.6375F, 0.6343F));

        PartDefinition stage_1_head_0_mane_5_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_5_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -2.0618F, -0.72F, 1.568F, 2.2618F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.496F, -1.472F, 0.896F, 0.1759F, -0.1889F, 1.6423F));

        PartDefinition stage_1_head_0_mane_4_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_4_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.1916F, -0.384F, 0.736F, 1.3916F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.495F, -3.5747F, 0.576F, -1.1438F, 0.2936F, 0.4516F));

        PartDefinition stage_1_head_0_mane_4_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_4_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -1.9768F, -0.72F, 1.568F, 2.1768F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0193F, -2.6382F, 0.896F, 0.182F, -0.0981F, 0.9873F));

        PartDefinition stage_1_head_0_mane_3_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_3_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.2932F, -0.384F, 0.736F, 1.4932F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.335F, -4.9929F, 0.576F, -0.9147F, 0.0718F, 0.1456F));

        PartDefinition stage_1_head_0_mane_3_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_3_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -1.9579F, -0.72F, 1.568F, 2.1579F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7713F, -3.3589F, 0.896F, 0.1831F, -0.0303F, 0.3266F));

        PartDefinition stage_1_head_0_mane_2_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_2_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.2932F, -0.384F, 0.736F, 1.4932F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.335F, -4.9929F, 0.576F, -0.9147F, -0.0718F, -0.1456F));

        PartDefinition stage_1_head_0_mane_2_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_2_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -1.9579F, -0.72F, 1.568F, 2.1579F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7713F, -3.3589F, 0.896F, 0.1831F, 0.0303F, -0.3266F));

        PartDefinition stage_1_head_0_mane_1_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_1_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.1916F, -0.384F, 0.736F, 1.3916F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.495F, -3.5747F, 0.576F, -1.1438F, -0.2936F, -0.4516F));

        PartDefinition stage_1_head_0_mane_1_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_1_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -1.9768F, -0.72F, 1.568F, 2.1768F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0193F, -2.6382F, 0.896F, 0.182F, 0.0981F, -0.9873F));

        PartDefinition stage_1_head_0_mane_0_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_0_tip_r1", CubeListBuilder.create().texOffs(177, 0).addBox(-0.368F, -1.2752F, -0.384F, 0.736F, 1.4752F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.32F, -1.28F, 0.576F, -1.5763F, -0.6375F, -0.6343F));

        PartDefinition stage_1_head_0_mane_0_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_mane_0_root_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.784F, -2.0618F, -0.72F, 1.568F, 2.2618F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.496F, -1.472F, 0.896F, 0.1759F, 0.1889F, -1.6423F));

        PartDefinition stage_1_head_0_incisor_2_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_incisor_2_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.144F, -1.1997F, -0.16F, 0.288F, 1.3997F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(0.432F, -1.1997F, -0.16F, 0.288F, 1.3997F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(1.008F, -1.1997F, -0.16F, 0.288F, 1.3997F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.576F, 1.792F, -4.288F, 2.4469F, 0.0F, 0.0F));

        PartDefinition stage_1_head_0_canine_1_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_canine_1_tip_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.064F, -1.5348F, -0.08F, 0.128F, 1.7348F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.824F, 3.008F, -4.16F, 2.2398F, -1.1287F, 0.5947F));

        PartDefinition stage_1_head_0_canine_1_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_canine_1_root_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.256F, -1.7926F, -0.288F, 0.512F, 1.9926F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.888F, 1.6F, -3.776F, 2.8647F, -0.2997F, 0.0421F));

        PartDefinition stage_1_head_0_canine_left_tip_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_canine_left_tip_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.064F, -1.5348F, -0.08F, 0.128F, 1.7348F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.824F, 3.008F, -4.16F, 2.2398F, 1.1287F, -0.5947F));

        PartDefinition stage_1_head_0_canine_left_root_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_canine_left_root_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.256F, -1.7926F, -0.288F, 0.512F, 1.9926F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.888F, 1.6F, -3.776F, 2.8647F, 0.2997F, -0.0421F));

        PartDefinition stage_1_head_0_ear_inner_1_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_ear_inner_1_r1", CubeListBuilder.create().texOffs(55, 21).addBox(-0.48F, -1.856F, -0.352F, 0.96F, 1.152F, 0.256F, new CubeDeformation(0.0F))
        .texOffs(54, 0).addBox(-0.8F, -2.304F, -0.192F, 1.6F, 2.368F, 1.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.008F, -2.752F, 0.64F, 0.0F, 0.0F, 0.384F));

        PartDefinition stage_1_head_0_brow_1_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_brow_1_r1", CubeListBuilder.create().texOffs(46, 21).addBox(-1.216F, -0.256F, -0.896F, 2.432F, 0.832F, 0.992F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.728F, -2.88F, -2.56F, 0.0F, 0.0F, -0.2094F));

        PartDefinition stage_1_head_0_muzzle_lobe_1_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_muzzle_lobe_1_r1", CubeListBuilder.create().texOffs(17, 21).addBox(-1.344F, -1.088F, -2.72F, 2.688F, 2.688F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.216F, 0.64F, -2.24F, 0.0F, 0.1396F, 0.0F));

        PartDefinition stage_1_head_0_cheek_1_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_cheek_1_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-1.12F, -1.728F, -2.752F, 2.24F, 3.84F, 4.672F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, 0.0F, 0.0F, 0.0F, -0.1745F, -0.1571F));

        PartDefinition stage_1_head_0_ear_inner_left_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_ear_inner_left_r1", CubeListBuilder.create().texOffs(55, 21).addBox(-0.48F, -1.856F, -0.352F, 0.96F, 1.152F, 0.256F, new CubeDeformation(0.0F))
        .texOffs(54, 0).addBox(-0.8F, -2.304F, -0.192F, 1.6F, 2.368F, 1.312F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.008F, -2.752F, 0.64F, 0.0F, 0.0F, -0.384F));

        PartDefinition stage_1_head_0_brow_left_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_brow_left_r1", CubeListBuilder.create().texOffs(46, 21).addBox(-1.216F, -0.256F, -0.896F, 2.432F, 0.832F, 0.992F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.728F, -2.88F, -2.56F, 0.0F, 0.0F, 0.2094F));

        PartDefinition stage_1_head_0_muzzle_lobe_left_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_muzzle_lobe_left_r1", CubeListBuilder.create().texOffs(17, 21).addBox(-1.344F, -1.088F, -2.72F, 2.688F, 2.688F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.216F, 0.64F, -2.24F, 0.0F, -0.1396F, 0.0F));

        PartDefinition stage_1_head_0_cheek_left_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_cheek_left_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-1.12F, -1.728F, -2.752F, 2.24F, 3.84F, 4.672F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.1571F));

        PartDefinition stage_1_head_0_forehead_r1 = stage_1_head_0.addOrReplaceChild("stage_1_head_0_forehead_r1", CubeListBuilder.create().texOffs(478, 0).addBox(-2.432F, -2.624F, -2.944F, 4.864F, 2.816F, 3.808F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.24F, 0.0F, -0.1222F, 0.0F, 0.0F));

        PartDefinition stage_1_jaw_0 = stage_1_head_0.addOrReplaceChild("stage_1_jaw_0", CubeListBuilder.create().texOffs(86, 21).addBox(-2.208F, 1.344F, -5.44F, 4.416F, 1.728F, 5.44F, new CubeDeformation(0.0F))
        .texOffs(69, 21).addBox(-2.048F, 1.088F, -5.184F, 4.096F, 0.448F, 2.944F, new CubeDeformation(0.0F))
        .texOffs(109, 21).addBox(1.184F, -1.792F, -0.64F, 0.832F, 3.968F, 1.344F, new CubeDeformation(0.0F))
        .texOffs(109, 21).addBox(-2.016F, -1.792F, -0.64F, 0.832F, 3.968F, 1.344F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.152F, 0.8F));

        PartDefinition stage_1_head_0_lower_canine_1_r1 = stage_1_jaw_0.addOrReplaceChild("stage_1_head_0_lower_canine_1_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.192F, -1.3775F, -0.192F, 0.384F, 1.5775F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.472F, 1.6F, -4.736F, 0.1914F, -0.0078F, 0.0816F));

        PartDefinition stage_1_head_0_lower_canine_left_r1 = stage_1_jaw_0.addOrReplaceChild("stage_1_head_0_lower_canine_left_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.192F, -1.3775F, -0.192F, 0.384F, 1.5775F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.472F, 1.6F, -4.736F, 0.1914F, 0.0078F, -0.0816F));

        PartDefinition stage_1_branch_1 = stage_1.addOrReplaceChild("stage_1_branch_1", CubeListBuilder.create(), PartPose.offset(0.0F, -37.76F, 0.0F));

        PartDefinition stage_1_neck_1 = stage_1_branch_1.addOrReplaceChild("stage_1_neck_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_1_neck_1_section_0_r1 = stage_1_neck_1.addOrReplaceChild("stage_1_neck_1_section_0_r1", CubeListBuilder.create().texOffs(135, 21).addBox(-1.68F, -4.2607F, -1.6F, 3.36F, 4.4607F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1384F, 0.093F, 1.1826F));

        PartDefinition stage_1_neck_1_joint_1 = stage_1_neck_1.addOrReplaceChild("stage_1_neck_1_joint_1", CubeListBuilder.create(), PartPose.offset(3.7423F, -1.4743F, 0.5577F));

        PartDefinition stage_1_neck_1_section_1_r1 = stage_1_neck_1_joint_1.addOrReplaceChild("stage_1_neck_1_section_1_r1", CubeListBuilder.create().texOffs(152, 21).addBox(-1.44F, -4.1423F, -1.4F, 2.88F, 4.3423F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1905F, -0.0981F, 0.9497F));

        PartDefinition stage_1_neck_1_joint_2 = stage_1_neck_1_joint_1.addOrReplaceChild("stage_1_neck_1_joint_2", CubeListBuilder.create(), PartPose.offset(3.1907F, -2.1931F, -0.7427F));

        PartDefinition stage_1_neck_1_section_2_r1 = stage_1_neck_1_joint_2.addOrReplaceChild("stage_1_neck_1_section_2_r1", CubeListBuilder.create().texOffs(152, 21).addBox(-1.2F, -4.2598F, -1.2F, 2.4F, 4.4598F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5859F, -0.1635F, 0.5306F));

        PartDefinition stage_1_head_1 = stage_1_neck_1_joint_2.addOrReplaceChild("stage_1_head_1", CubeListBuilder.create().texOffs(165, 21).addBox(-1.984F, -4.608F, -1.536F, 3.968F, 4.992F, 3.584F, new CubeDeformation(0.0F))
        .texOffs(63, 0).addBox(-1.568F, 1.664F, -5.248F, 3.136F, 2.24F, 2.528F, new CubeDeformation(0.0F))
        .texOffs(60, 21).addBox(-1.12F, 1.728F, -5.44F, 2.24F, 1.152F, 0.768F, new CubeDeformation(0.0F)), PartPose.offset(2.027F, -2.7326F, -2.215F));

        PartDefinition stage_1_head_1_tooth_1_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_tooth_1_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.144F, -1.1858F, -0.16F, 0.288F, 1.3858F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(1.392F, -1.1858F, -0.16F, 0.288F, 1.3858F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.768F, 2.88F, -4.48F, 2.9124F, 0.0F, 0.0F));

        PartDefinition stage_1_head_1_ram_horn_1_3_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_1_3_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.128F, -2.4143F, -0.128F, 0.256F, 2.6143F, 0.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.96F, -6.848F, 5.056F, 0.0604F, -0.2935F, 2.7382F));

        PartDefinition stage_1_head_1_ram_horn_1_2_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_1_2_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.352F, -2.2119F, -0.32F, 0.704F, 2.4119F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.16F, -8.384F, 4.032F, -1.7555F, -1.0266F, -0.8757F));

        PartDefinition stage_1_head_1_ram_horn_1_1_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_1_1_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.464F, -3.0566F, -0.432F, 0.928F, 3.2566F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.496F, -7.232F, 2.016F, -0.8303F, -0.2973F, -0.6551F));

        PartDefinition stage_1_head_1_ram_horn_1_0_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_1_0_r1", CubeListBuilder.create().texOffs(216, 21).addBox(-0.576F, -3.7067F, -0.544F, 1.152F, 3.9067F, 1.088F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.504F, -4.096F, 0.8F, -0.3546F, -0.0518F, -0.2872F));

        PartDefinition stage_1_head_1_ear_1_tip_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ear_1_tip_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.288F, -1.7742F, -0.24F, 0.576F, 1.9742F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.816F, -1.536F, 0.96F, 0.1257F, 0.2323F, -2.1524F));

        PartDefinition stage_1_head_1_ear_1_base_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ear_1_base_r1", CubeListBuilder.create().texOffs(209, 21).addBox(-0.624F, -1.89F, -0.416F, 1.248F, 2.09F, 0.832F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.472F, -2.048F, 0.48F, -0.3384F, -0.4214F, -1.7935F));

        PartDefinition stage_1_head_1_brow_1_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_brow_1_r1", CubeListBuilder.create().texOffs(202, 21).addBox(-2.208F, -3.52F, -3.52F, 1.6F, 0.64F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition stage_1_head_1_eye_1_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_eye_1_r1", CubeListBuilder.create().texOffs(114, 0).addBox(-1.888F, -2.368F, -3.584F, 0.896F, 0.32F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(-2.048F, -2.88F, -3.36F, 1.216F, 1.216F, 0.672F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_1_head_1_ram_horn_left_3_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_left_3_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.128F, -2.4143F, -0.128F, 0.256F, 2.6143F, 0.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.96F, -6.848F, 5.056F, 0.0604F, 0.2935F, -2.7382F));

        PartDefinition stage_1_head_1_ram_horn_left_2_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_left_2_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.352F, -2.2119F, -0.32F, 0.704F, 2.4119F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.16F, -8.384F, 4.032F, -1.7555F, 1.0266F, 0.8757F));

        PartDefinition stage_1_head_1_ram_horn_left_1_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_left_1_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.464F, -3.0566F, -0.432F, 0.928F, 3.2566F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.496F, -7.232F, 2.016F, -0.8303F, 0.2973F, 0.6551F));

        PartDefinition stage_1_head_1_ram_horn_left_0_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ram_horn_left_0_r1", CubeListBuilder.create().texOffs(216, 21).addBox(-0.576F, -3.7067F, -0.544F, 1.152F, 3.9067F, 1.088F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.504F, -4.096F, 0.8F, -0.3546F, 0.0518F, 0.2872F));

        PartDefinition stage_1_head_1_ear_left_tip_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ear_left_tip_r1", CubeListBuilder.create().texOffs(126, 0).addBox(-0.288F, -1.7742F, -0.24F, 0.576F, 1.9742F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.816F, -1.536F, 0.96F, 0.1257F, -0.2323F, 2.1524F));

        PartDefinition stage_1_head_1_ear_left_base_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_ear_left_base_r1", CubeListBuilder.create().texOffs(209, 21).addBox(-0.624F, -1.89F, -0.416F, 1.248F, 2.09F, 0.832F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.472F, -2.048F, 0.48F, -0.3384F, 0.4214F, 1.7935F));

        PartDefinition stage_1_head_1_brow_left_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_brow_left_r1", CubeListBuilder.create().texOffs(202, 21).addBox(0.608F, -3.52F, -3.52F, 1.6F, 0.64F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition stage_1_head_1_eye_left_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_eye_left_r1", CubeListBuilder.create().texOffs(114, 0).addBox(0.992F, -2.368F, -3.584F, 0.896F, 0.32F, 0.32F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(0.832F, -2.88F, -3.36F, 1.216F, 1.216F, 0.672F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_1_head_1_nasal_bridge_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_nasal_bridge_r1", CubeListBuilder.create().texOffs(193, 21).addBox(-0.992F, -1.216F, -1.632F, 1.984F, 4.416F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.152F, -2.176F, -0.2269F, 0.0F, 0.0F));

        PartDefinition stage_1_head_1_long_face_r1 = stage_1_head_1.addOrReplaceChild("stage_1_head_1_long_face_r1", CubeListBuilder.create().texOffs(182, 21).addBox(-1.44F, -0.704F, -1.44F, 2.88F, 6.336F, 1.952F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.56F, -1.44F, -0.2618F, 0.0F, 0.0F));

        PartDefinition stage_1_jaw_1 = stage_1_head_1.addOrReplaceChild("stage_1_jaw_1", CubeListBuilder.create().texOffs(225, 21).addBox(-1.376F, 1.344F, -5.28F, 2.752F, 1.472F, 5.44F, new CubeDeformation(0.0F))
        .texOffs(296, 0).addBox(-1.312F, 0.768F, -5.568F, 2.624F, 0.768F, 2.4F, new CubeDeformation(0.0F))
        .texOffs(244, 21).addBox(0.672F, -2.496F, -0.48F, 0.768F, 4.544F, 1.376F, new CubeDeformation(0.0F))
        .texOffs(244, 21).addBox(-1.44F, -2.496F, -0.48F, 0.768F, 4.544F, 1.376F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.24F, 0.32F));

        PartDefinition stage_1_head_1_beard_tip_r1 = stage_1_jaw_1.addOrReplaceChild("stage_1_head_1_beard_tip_r1", CubeListBuilder.create().texOffs(125, 21).addBox(-0.144F, -2.0184F, -0.16F, 0.288F, 2.2184F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.288F, -3.936F, -1.7986F, -1.3337F, -1.118F));

        PartDefinition stage_1_head_1_beard_root_r1 = stage_1_jaw_1.addOrReplaceChild("stage_1_head_1_beard_root_r1", CubeListBuilder.create().texOffs(251, 21).addBox(-0.624F, -2.706F, -0.448F, 1.248F, 2.906F, 0.896F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.92F, -4.16F, -3.0521F, 0.0F, 0.0F));

        PartDefinition stage_1_branch_2 = stage_1.addOrReplaceChild("stage_1_branch_2", CubeListBuilder.create(), PartPose.offset(0.0F, -37.76F, 0.0F));

        PartDefinition stage_1_neck_2 = stage_1_branch_2.addOrReplaceChild("stage_1_neck_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_1_neck_2_section_0_r1 = stage_1_neck_2.addOrReplaceChild("stage_1_neck_2_section_0_r1", CubeListBuilder.create().texOffs(258, 21).addBox(-1.68F, -6.0361F, -1.6F, 3.36F, 6.2361F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3634F, -0.088F, -0.4705F));

        PartDefinition stage_1_neck_2_joint_1 = stage_1_neck_2.addOrReplaceChild("stage_1_neck_2_joint_1", CubeListBuilder.create(), PartPose.offset(-2.6354F, -4.7795F, 2.0667F));

        PartDefinition stage_1_neck_2_section_1_r1 = stage_1_neck_2_joint_1.addOrReplaceChild("stage_1_neck_2_section_1_r1", CubeListBuilder.create().texOffs(275, 21).addBox(-1.4933F, -6.0306F, -1.4667F, 2.9867F, 6.2306F, 2.9333F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3151F, -0.0729F, -0.4511F));

        PartDefinition stage_1_neck_2_joint_2 = stage_1_neck_2_joint_1.addOrReplaceChild("stage_1_neck_2_joint_2", CubeListBuilder.create(), PartPose.offset(-2.5351F, -4.9316F, 1.8024F));

        PartDefinition stage_1_neck_2_section_2_r1 = stage_1_neck_2_joint_2.addOrReplaceChild("stage_1_neck_2_section_2_r1", CubeListBuilder.create().texOffs(275, 21).addBox(-1.3067F, -6.0256F, -1.3333F, 2.6133F, 6.2256F, 2.6667F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1861F, -0.0352F, -0.3731F));

        PartDefinition stage_1_neck_2_joint_3 = stage_1_neck_2_joint_2.addOrReplaceChild("stage_1_neck_2_joint_3", CubeListBuilder.create(), PartPose.offset(-2.1221F, -5.3173F, 1.0773F));

        PartDefinition stage_1_head_2_hood_1_edge_r1 = stage_1_neck_2_joint_3.addOrReplaceChild("stage_1_head_2_hood_1_edge_r1", CubeListBuilder.create().texOffs(394, 21).addBox(-0.432F, -3.4634F, -0.352F, 0.864F, 3.6634F, 0.704F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6754F, -1.4836F, -0.6584F, -0.518F, 1.1635F, 2.3757F));

        PartDefinition stage_1_head_2_hood_1_root_r1 = stage_1_neck_2_joint_3.addOrReplaceChild("stage_1_head_2_hood_1_root_r1", CubeListBuilder.create().texOffs(385, 21).addBox(-0.752F, -3.603F, -0.544F, 1.504F, 3.803F, 1.088F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2434F, -3.6596F, -1.1064F, -0.1382F, -0.2996F, -2.2816F));

        PartDefinition stage_1_head_2_hood_left_edge_r1 = stage_1_neck_2_joint_3.addOrReplaceChild("stage_1_head_2_hood_left_edge_r1", CubeListBuilder.create().texOffs(394, 21).addBox(-0.432F, -3.4634F, -0.352F, 0.864F, 3.6634F, 0.704F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9806F, -1.4836F, -0.6584F, -0.518F, -1.1635F, -2.3757F));

        PartDefinition stage_1_head_2_hood_left_root_r1 = stage_1_neck_2_joint_3.addOrReplaceChild("stage_1_head_2_hood_left_root_r1", CubeListBuilder.create().texOffs(385, 21).addBox(-0.752F, -3.603F, -0.544F, 1.504F, 3.803F, 1.088F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4514F, -3.6596F, -1.1064F, -0.1382F, 0.2996F, 2.2816F));

        PartDefinition stage_1_neck_2_section_3_r1 = stage_1_neck_2_joint_3.addOrReplaceChild("stage_1_neck_2_section_3_r1", CubeListBuilder.create().texOffs(275, 21).addBox(-1.12F, -5.8081F, -1.2F, 2.24F, 6.0081F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4722F, 0.0587F, -0.2431F));

        PartDefinition stage_1_head_2 = stage_1_neck_2_joint_3.addOrReplaceChild("stage_1_head_2", CubeListBuilder.create().texOffs(288, 21).addBox(-2.208F, -2.752F, -0.704F, 4.416F, 3.776F, 2.944F, new CubeDeformation(0.0F))
        .texOffs(332, 21).addBox(-1.728F, -2.496F, -5.6F, 3.456F, 1.472F, 3.296F, new CubeDeformation(0.0F))
        .texOffs(349, 21).addBox(-2.176F, 0.448F, -5.728F, 4.352F, 0.576F, 5.152F, new CubeDeformation(0.0F)), PartPose.offset(-1.3474F, -4.8116F, -2.5464F));

        PartDefinition stage_1_head_2_crown_tip_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_crown_tip_r1", CubeListBuilder.create().texOffs(187, 0).addBox(-0.064F, -1.5234F, -0.064F, 0.128F, 1.7234F, 0.128F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.416F, -5.696F, 1.472F, -1.2222F, -0.2119F, -0.3013F));

        PartDefinition stage_1_head_2_crown_crest_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_crown_crest_r1", CubeListBuilder.create().texOffs(462, 21).addBox(-0.48F, -4.2664F, -0.4F, 0.96F, 4.4664F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.048F, 0.8F, -0.19F, -0.0105F, -0.1104F));

        PartDefinition stage_1_head_2_fang_1_tip_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_fang_1_tip_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.08F, -2.151F, -0.08F, 0.16F, 2.351F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6F, 2.432F, -4.448F, -2.705F, 0.8508F, 0.2003F));

        PartDefinition stage_1_head_2_fang_1_root_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_fang_1_root_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.272F, -2.1966F, -0.272F, 0.544F, 2.3966F, 0.544F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.664F, 0.64F, -3.936F, 2.858F, -0.2296F, 0.0329F));

        PartDefinition stage_1_head_2_brow_1_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_brow_1_r1", CubeListBuilder.create().texOffs(287, 0).addBox(-2.72F, -3.264F, -3.68F, 1.728F, 0.704F, 1.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition stage_1_head_2_eye_1_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_eye_1_r1", CubeListBuilder.create().texOffs(114, 0).addBox(-2.336F, -2.496F, -3.744F, 0.768F, 0.576F, 0.448F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(-2.56F, -2.816F, -3.52F, 1.216F, 1.152F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_1_head_2_cheek_1_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_cheek_1_r1", CubeListBuilder.create().texOffs(372, 21).addBox(-0.864F, -2.688F, -2.24F, 1.728F, 3.392F, 3.456F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.984F, 0.0F, 0.0F, 0.0F, -0.2443F, -0.0873F));

        PartDefinition stage_1_head_2_fang_left_tip_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_fang_left_tip_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.08F, -2.151F, -0.08F, 0.16F, 2.351F, 0.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6F, 2.432F, -4.448F, -2.705F, -0.8508F, -0.2003F));

        PartDefinition stage_1_head_2_fang_left_root_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_fang_left_root_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.272F, -2.1966F, -0.272F, 0.544F, 2.3966F, 0.544F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.664F, 0.64F, -3.936F, 2.858F, 0.2296F, -0.0329F));

        PartDefinition stage_1_head_2_brow_left_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_brow_left_r1", CubeListBuilder.create().texOffs(287, 0).addBox(0.992F, -3.264F, -3.68F, 1.728F, 0.704F, 1.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        PartDefinition stage_1_head_2_eye_left_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_eye_left_r1", CubeListBuilder.create().texOffs(114, 0).addBox(1.568F, -2.496F, -3.744F, 0.768F, 0.576F, 0.448F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(1.344F, -2.816F, -3.52F, 1.216F, 1.152F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_1_head_2_cheek_left_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_cheek_left_r1", CubeListBuilder.create().texOffs(372, 21).addBox(-0.864F, -2.688F, -2.24F, 1.728F, 3.392F, 3.456F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.984F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0873F));

        PartDefinition stage_1_head_2_upper_jaw_r1 = stage_1_head_2.addOrReplaceChild("stage_1_head_2_upper_jaw_r1", CubeListBuilder.create().texOffs(305, 21).addBox(-2.656F, -1.728F, -6.4F, 5.312F, 2.304F, 6.336F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.48F, -0.0698F, 0.0F, 0.0F));

        PartDefinition stage_1_jaw_2 = stage_1_head_2.addOrReplaceChild("stage_1_jaw_2", CubeListBuilder.create().texOffs(404, 21).addBox(-2.144F, 0.384F, -6.464F, 4.288F, 1.6F, 6.912F, new CubeDeformation(0.0F))
        .texOffs(429, 21).addBox(-1.888F, -0.064F, -6.048F, 3.776F, 0.576F, 5.088F, new CubeDeformation(0.0F))
        .texOffs(450, 21).addBox(1.184F, -1.472F, -0.576F, 0.704F, 2.752F, 1.024F, new CubeDeformation(0.0F))
        .texOffs(450, 21).addBox(-1.888F, -1.472F, -0.576F, 0.704F, 2.752F, 1.024F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.024F, 0.736F));

        PartDefinition stage_1_head_2_fork_tongue_1_r1 = stage_1_jaw_2.addOrReplaceChild("stage_1_head_2_fork_tongue_1_r1", CubeListBuilder.create().texOffs(208, 0).addBox(-0.048F, -2.0307F, -0.048F, 0.096F, 2.2307F, 0.096F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.896F, -7.424F, 1.5372F, 0.5414F, -0.559F));

        PartDefinition stage_1_head_2_fork_tongue_left_r1 = stage_1_jaw_2.addOrReplaceChild("stage_1_head_2_fork_tongue_left_r1", CubeListBuilder.create().texOffs(208, 0).addBox(-0.048F, -2.0307F, -0.048F, 0.096F, 2.2307F, 0.096F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.896F, -7.424F, 1.5372F, -0.5414F, 0.559F));

        PartDefinition stage_1_head_2_tongue_root_r1 = stage_1_jaw_2.addOrReplaceChild("stage_1_head_2_tongue_root_r1", CubeListBuilder.create().texOffs(457, 21).addBox(-0.16F, -3.1998F, -0.128F, 0.32F, 3.3998F, 0.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.384F, -4.576F, 1.764F, 0.0F, 0.0F));

        PartDefinition stage_2 = partdefinition.addOrReplaceChild("stage_2", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_2_thorn_2_tip_r1 = stage_2.addOrReplaceChild("stage_2_thorn_2_tip_r1", CubeListBuilder.create().texOffs(435, 80).addBox(-0.125F, -8.4969F, -0.125F, 0.25F, 8.6969F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.44F, -41.28F, -5.76F, 0.101F, -0.2979F, 2.4916F));

        PartDefinition stage_2_thorn_2_a_r1 = stage_2.addOrReplaceChild("stage_2_thorn_2_a_r1", CubeListBuilder.create().texOffs(422, 80).addBox(-1.1F, -17.1841F, -1.05F, 2.2F, 17.3841F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -32.64F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_2_thorn_1_tip_r1 = stage_2.addOrReplaceChild("stage_2_thorn_1_tip_r1", CubeListBuilder.create().texOffs(435, 80).addBox(-0.125F, -8.4969F, -0.125F, 0.25F, 8.6969F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.44F, -34.56F, 5.76F, 0.101F, 0.2979F, -2.4916F));

        PartDefinition stage_2_thorn_1_a_r1 = stage_2.addOrReplaceChild("stage_2_thorn_1_a_r1", CubeListBuilder.create().texOffs(422, 80).addBox(-1.1F, -17.1841F, -1.05F, 2.2F, 17.3841F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -25.92F, 0.0F, -0.3519F, -0.1788F, -0.9342F));

        PartDefinition stage_2_thorn_0_tip_r1 = stage_2.addOrReplaceChild("stage_2_thorn_0_tip_r1", CubeListBuilder.create().texOffs(435, 80).addBox(-0.125F, -8.4969F, -0.125F, 0.25F, 8.6969F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.44F, -27.84F, -5.76F, 0.101F, -0.2979F, 2.4916F));

        PartDefinition stage_2_thorn_0_a_r1 = stage_2.addOrReplaceChild("stage_2_thorn_0_a_r1", CubeListBuilder.create().texOffs(422, 80).addBox(-1.1F, -17.1841F, -1.05F, 2.2F, 17.3841F, 2.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.2F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_2_root_5_b_r1 = stage_2.addOrReplaceChild("stage_2_root_5_b_r1", CubeListBuilder.create().texOffs(46, 51).addBox(-0.65F, -12.2432F, -0.65F, 1.3F, 12.4432F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.6738F, -6.24F, -7.5542F, 1.32F, 0.8253F, -1.0271F));

        PartDefinition stage_2_root_5_a_r1 = stage_2.addOrReplaceChild("stage_2_root_5_a_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.56F, -10.3823F, -1.56F, 3.12F, 10.5823F, 3.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.68F, 0.0F, 1.1202F, 0.6021F, -0.9195F));

        PartDefinition stage_2_root_4_b_r1 = stage_2.addOrReplaceChild("stage_2_root_4_b_r1", CubeListBuilder.create().texOffs(46, 51).addBox(-0.65F, -12.2432F, -0.65F, 1.3F, 12.4432F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2052F, -6.24F, -9.5568F, 1.9498F, -0.4628F, 0.3169F));

        PartDefinition stage_2_root_4_a_r1 = stage_2.addOrReplaceChild("stage_2_root_4_a_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.56F, -10.3823F, -1.56F, 3.12F, 10.5823F, 3.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.68F, 0.0F, 1.5985F, -0.3513F, 0.3419F));

        PartDefinition stage_2_root_3_b_r1 = stage_2.addOrReplaceChild("stage_2_root_3_b_r1", CubeListBuilder.create().texOffs(46, 51).addBox(-0.65F, -12.2432F, -0.65F, 1.3F, 12.4432F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.8791F, -6.24F, -2.0026F, 0.1829F, -0.2925F, 2.0276F));

        PartDefinition stage_2_root_3_a_r1 = stage_2.addOrReplaceChild("stage_2_root_3_a_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.56F, -10.3823F, -1.56F, 3.12F, 10.5823F, 3.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.68F, 0.0F, 0.2031F, -0.2241F, 1.6698F));

        PartDefinition stage_2_root_2_b_r1 = stage_2.addOrReplaceChild("stage_2_root_2_b_r1", CubeListBuilder.create().texOffs(46, 51).addBox(-0.65F, -12.2432F, -0.65F, 1.3F, 12.4432F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.6738F, -6.24F, 7.5542F, -1.32F, 0.8253F, 1.0271F));

        PartDefinition stage_2_root_2_a_r1 = stage_2.addOrReplaceChild("stage_2_root_2_a_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.56F, -10.3823F, -1.56F, 3.12F, 10.5823F, 3.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.68F, 0.0F, -1.1202F, 0.6021F, 0.9195F));

        PartDefinition stage_2_root_1_b_r1 = stage_2.addOrReplaceChild("stage_2_root_1_b_r1", CubeListBuilder.create().texOffs(46, 51).addBox(-0.65F, -12.2432F, -0.65F, 1.3F, 12.4432F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2052F, -6.24F, 9.5568F, -1.9498F, -0.4628F, -0.3169F));

        PartDefinition stage_2_root_1_a_r1 = stage_2.addOrReplaceChild("stage_2_root_1_a_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.56F, -10.3823F, -1.56F, 3.12F, 10.5823F, 3.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.68F, 0.0F, -1.5985F, -0.3513F, -0.3419F));

        PartDefinition stage_2_root_0_b_r1 = stage_2.addOrReplaceChild("stage_2_root_0_b_r1", CubeListBuilder.create().texOffs(46, 51).addBox(-0.65F, -12.2432F, -0.65F, 1.3F, 12.4432F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.8791F, -6.24F, 2.0026F, -0.1829F, -0.2925F, -2.0276F));

        PartDefinition stage_2_root_0_a_r1 = stage_2.addOrReplaceChild("stage_2_root_0_a_r1", CubeListBuilder.create().texOffs(395, 0).addBox(-1.56F, -10.3823F, -1.56F, 3.12F, 10.5823F, 3.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.68F, 0.0F, -0.2031F, -0.2241F, -1.6698F));

        PartDefinition stage_2_trunk_2_r1 = stage_2.addOrReplaceChild("stage_2_trunk_2_r1", CubeListBuilder.create().texOffs(25, 51).addBox(-2.376F, -15.6795F, -2.2848F, 4.752F, 15.8795F, 4.5696F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.92F, -41.28F, 0.0F, 0.0F, 0.0F, -0.1244F));

        PartDefinition stage_2_trunk_1_r1 = stage_2.addOrReplaceChild("stage_2_trunk_1_r1", CubeListBuilder.create().texOffs(0, 51).addBox(-2.988F, -20.8916F, -2.8224F, 5.976F, 21.0916F, 5.6448F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.64F, -21.12F, 0.96F, 0.0464F, -0.0052F, 0.2222F));

        PartDefinition stage_2_trunk_0_r1 = stage_2.addOrReplaceChild("stage_2_trunk_0_r1", CubeListBuilder.create().texOffs(481, 21).addBox(-3.6F, -21.2087F, -3.36F, 7.2F, 21.4087F, 6.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 0.0F, -0.0457F, -0.0029F, -0.126F));

        PartDefinition stage_2_branch_0 = stage_2.addOrReplaceChild("stage_2_branch_0", CubeListBuilder.create(), PartPose.offset(0.0F, -56.64F, 0.0F));

        PartDefinition stage_2_neck_0 = stage_2_branch_0.addOrReplaceChild("stage_2_neck_0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_2_neck_0_section_0_r1 = stage_2_neck_0.addOrReplaceChild("stage_2_neck_0_section_0_r1", CubeListBuilder.create().texOffs(55, 51).addBox(-2.52F, -3.9409F, -2.4F, 5.04F, 4.1409F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2043F, 0.0F, 0.0F));

        PartDefinition stage_2_neck_0_joint_1 = stage_2_neck_0.addOrReplaceChild("stage_2_neck_0_joint_1", CubeListBuilder.create(), PartPose.offset(0.0F, -3.6631F, 0.7589F));

        PartDefinition stage_2_neck_0_section_1_r1 = stage_2_neck_0_joint_1.addOrReplaceChild("stage_2_neck_0_section_1_r1", CubeListBuilder.create().texOffs(78, 51).addBox(-2.52F, -3.9458F, -2.2F, 5.04F, 4.1458F, 4.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5333F, 0.0F, 0.0F));

        PartDefinition stage_2_neck_0_joint_2 = stage_2_neck_0_joint_1.addOrReplaceChild("stage_2_neck_0_joint_2", CubeListBuilder.create(), PartPose.offset(0.0F, -3.2256F, -1.9043F));

        PartDefinition stage_2_neck_0_section_2_r1 = stage_2_neck_0_joint_2.addOrReplaceChild("stage_2_neck_0_section_2_r1", CubeListBuilder.create().texOffs(101, 51).addBox(-2.52F, -3.9824F, -2.0F, 5.04F, 4.1824F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7435F, 0.0F, 0.0F));

        PartDefinition stage_2_neck_0_joint_3 = stage_2_neck_0_joint_2.addOrReplaceChild("stage_2_neck_0_joint_3", CubeListBuilder.create(), PartPose.offset(0.0F, -2.7842F, -2.5602F));

        PartDefinition stage_2_neck_0_section_3_r1 = stage_2_neck_0_joint_3.addOrReplaceChild("stage_2_neck_0_section_3_r1", CubeListBuilder.create().texOffs(101, 51).addBox(-2.52F, -3.9818F, -1.8F, 5.04F, 4.1818F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7344F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0 = stage_2_neck_0_joint_3.addOrReplaceChild("stage_2_head_0", CubeListBuilder.create().texOffs(122, 51).addBox(-4.56F, -6.144F, -3.6F, 9.12F, 7.392F, 7.44F, new CubeDeformation(0.0F))
        .texOffs(234, 51).addBox(1.2F, -3.84F, -4.704F, 2.784F, 1.728F, 1.2F, new CubeDeformation(0.0F))
        .texOffs(39, 21).addBox(1.728F, -3.264F, -4.992F, 1.728F, 0.768F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(234, 51).addBox(-3.984F, -3.84F, -4.704F, 2.784F, 1.728F, 1.2F, new CubeDeformation(0.0F))
        .texOffs(39, 21).addBox(-3.456F, -3.264F, -4.992F, 1.728F, 0.768F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(269, 51).addBox(-1.296F, -3.072F, -5.904F, 2.592F, 2.88F, 2.928F, new CubeDeformation(0.0F))
        .texOffs(282, 51).addBox(-1.632F, -1.056F, -8.112F, 3.264F, 1.632F, 1.104F, new CubeDeformation(0.0F))
        .texOffs(295, 51).addBox(-3.6F, 2.4F, -6.864F, 7.2F, 1.344F, 3.984F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.8071F, -2.5343F));

        PartDefinition stage_2_head_0_crown_horn_1_2_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_crown_horn_1_2_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.12F, -2.2707F, -0.12F, 0.24F, 2.4707F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.04F, -11.328F, 4.8F, -1.7161F, 0.4186F, 0.3631F));

        PartDefinition stage_2_head_0_crown_horn_1_1_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_crown_horn_1_1_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.432F, -2.9276F, -0.384F, 0.864F, 3.1276F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.416F, -9.312F, 3.072F, -0.689F, -0.0834F, -0.2316F));

        PartDefinition stage_2_head_0_crown_horn_1_0_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_crown_horn_1_0_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.624F, -4.7051F, -0.576F, 1.248F, 4.9051F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.12F, -5.28F, 1.536F, -0.3484F, -0.0518F, -0.2922F));

        PartDefinition stage_2_head_0_crown_horn_left_2_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_crown_horn_left_2_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.12F, -2.2707F, -0.12F, 0.24F, 2.4707F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.04F, -11.328F, 4.8F, -1.7161F, -0.4186F, -0.3631F));

        PartDefinition stage_2_head_0_crown_horn_left_1_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_crown_horn_left_1_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.432F, -2.9276F, -0.384F, 0.864F, 3.1276F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.416F, -9.312F, 3.072F, -0.689F, 0.0834F, 0.2316F));

        PartDefinition stage_2_head_0_crown_horn_left_0_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_crown_horn_left_0_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.624F, -4.7051F, -0.576F, 1.248F, 4.9051F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.12F, -5.28F, 1.536F, -0.3484F, 0.0518F, 0.2922F));

        PartDefinition stage_2_head_0_mane_11_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_11_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.694F, -0.288F, 0.432F, 3.894F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.277F, 2.4F, 2.16F, -2.676F, 0.4079F, 0.098F));

        PartDefinition stage_2_head_0_mane_11_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_11_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -2.2149F, -0.576F, 1.104F, 2.4149F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6118F, 1.008F, 0.864F, -2.0551F, -0.7572F, -0.4715F));

        PartDefinition stage_2_head_0_mane_11_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_11_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -3.1716F, -1.08F, 2.352F, 3.3716F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2424F, -0.72F, 1.344F, 0.1706F, 0.3128F, -2.1479F));

        PartDefinition stage_2_head_0_mane_10_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_10_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.6846F, -0.288F, 0.432F, 3.8846F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.624F, 5.1408F, 2.16F, -2.7023F, 0.2387F, 0.0535F));

        PartDefinition stage_2_head_0_mane_10_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_10_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -2.6051F, -0.576F, 1.104F, 2.8051F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.24F, 3.1514F, 0.864F, -2.4713F, -0.5209F, -0.1851F));

        PartDefinition stage_2_head_0_mane_10_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_10_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -3.3372F, -1.08F, 2.352F, 3.5372F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.872F, 0.3693F, 1.344F, 0.1906F, 0.6304F, -2.5714F));

        PartDefinition stage_2_head_0_mane_9_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_9_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.6799F, -0.288F, 0.432F, 3.8799F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.144F, 2.16F, -2.715F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0_mane_9_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_9_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -2.7602F, -0.576F, 1.104F, 2.9602F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.936F, 0.864F, -2.6108F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0_mane_9_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_9_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -3.4042F, -1.08F, 2.352F, 3.6042F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.768F, 1.344F, 2.9912F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0_mane_8_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_8_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.6846F, -0.288F, 0.432F, 3.8846F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.624F, 5.1408F, 2.16F, -2.7023F, -0.2387F, -0.0535F));

        PartDefinition stage_2_head_0_mane_8_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_8_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -2.6051F, -0.576F, 1.104F, 2.8051F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.24F, 3.1514F, 0.864F, -2.4713F, 0.5209F, 0.1851F));

        PartDefinition stage_2_head_0_mane_8_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_8_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -3.3372F, -1.08F, 2.352F, 3.5372F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.872F, 0.3693F, 1.344F, 0.1906F, -0.6304F, 2.5714F));

        PartDefinition stage_2_head_0_mane_7_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_7_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.694F, -0.288F, 0.432F, 3.894F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.277F, 2.4F, 2.16F, -2.676F, -0.4079F, -0.098F));

        PartDefinition stage_2_head_0_mane_7_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_7_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -2.2149F, -0.576F, 1.104F, 2.4149F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.6118F, 1.008F, 0.864F, -2.0551F, 0.7572F, 0.4715F));

        PartDefinition stage_2_head_0_mane_7_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_7_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -3.1716F, -1.08F, 2.352F, 3.3716F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2424F, -0.72F, 1.344F, 0.1706F, -0.3128F, 2.1479F));

        PartDefinition stage_2_head_0_mane_6_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_6_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.6987F, -0.288F, 0.432F, 3.8987F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.248F, -1.344F, 2.16F, -2.6623F, -0.4679F, -0.1163F));

        PartDefinition stage_2_head_0_mane_6_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_6_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -1.8128F, -0.576F, 1.104F, 2.0128F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.48F, -1.92F, 0.864F, -1.5763F, 0.6375F, 0.6343F));

        PartDefinition stage_2_head_0_mane_6_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_6_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.9927F, -1.08F, 2.352F, 3.1927F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.744F, -2.208F, 1.344F, 0.1759F, -0.1889F, 1.6423F));

        PartDefinition stage_2_head_0_mane_5_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_5_tip_r1", CubeListBuilder.create().texOffs(407, 51).addBox(-0.552F, -1.6763F, -0.576F, 1.104F, 1.8763F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.6118F, -4.848F, 0.864F, -1.2041F, 0.3471F, 0.4996F));

        PartDefinition stage_2_head_0_mane_5_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_5_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.878F, -1.08F, 2.352F, 3.078F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2424F, -3.696F, 1.344F, 0.1813F, -0.1111F, 1.0979F));

        PartDefinition stage_2_head_0_mane_4_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_4_tip_r1", CubeListBuilder.create().texOffs(407, 51).addBox(-0.552F, -1.79F, -0.576F, 1.104F, 1.99F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.24F, -6.9914F, 0.864F, -0.9648F, 0.1293F, 0.246F));

        PartDefinition stage_2_head_0_mane_4_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_4_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.8399F, -1.08F, 2.352F, 3.0399F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.872F, -4.7853F, 1.344F, 0.1831F, -0.0514F, 0.5456F));

        PartDefinition stage_2_head_0_mane_3_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_3_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -1.8718F, -0.576F, 1.104F, 2.0718F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.776F, 0.864F, -0.8871F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0_mane_3_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_3_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.8361F, -1.08F, 2.352F, 3.0361F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.184F, 1.344F, 0.1831F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0_mane_2_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_2_tip_r1", CubeListBuilder.create().texOffs(407, 51).addBox(-0.552F, -1.79F, -0.576F, 1.104F, 1.99F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.24F, -6.9914F, 0.864F, -0.9648F, -0.1293F, -0.246F));

        PartDefinition stage_2_head_0_mane_2_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_2_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.8399F, -1.08F, 2.352F, 3.0399F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.872F, -4.7853F, 1.344F, 0.1831F, 0.0514F, -0.5456F));

        PartDefinition stage_2_head_0_mane_1_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_1_tip_r1", CubeListBuilder.create().texOffs(407, 51).addBox(-0.552F, -1.6763F, -0.576F, 1.104F, 1.8763F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6118F, -4.848F, 0.864F, -1.2041F, -0.3471F, -0.4996F));

        PartDefinition stage_2_head_0_mane_1_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_1_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.878F, -1.08F, 2.352F, 3.078F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2424F, -3.696F, 1.344F, 0.1813F, 0.1111F, -1.0979F));

        PartDefinition stage_2_head_0_mane_0_tassel_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_0_tassel_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -3.6987F, -0.288F, 0.432F, 3.8987F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.248F, -1.344F, 2.16F, -2.6623F, 0.4679F, 0.1163F));

        PartDefinition stage_2_head_0_mane_0_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_0_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.552F, -1.8128F, -0.576F, 1.104F, 2.0128F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.48F, -1.92F, 0.864F, -1.5763F, -0.6375F, -0.6343F));

        PartDefinition stage_2_head_0_mane_0_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_mane_0_root_r1", CubeListBuilder.create().texOffs(389, 51).addBox(-1.176F, -2.9927F, -1.08F, 2.352F, 3.1927F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.744F, -2.208F, 1.344F, 0.1759F, 0.1889F, -1.6423F));

        PartDefinition stage_2_head_0_incisor_4_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_incisor_4_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.216F, -1.6996F, -0.24F, 0.432F, 1.8996F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(0.648F, -1.6996F, -0.24F, 0.432F, 1.8996F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(1.512F, -1.6996F, -0.24F, 0.432F, 1.8996F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(2.376F, -1.6996F, -0.24F, 0.432F, 1.8996F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(3.24F, -1.6996F, -0.24F, 0.432F, 1.8996F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.728F, 2.688F, -6.432F, 2.4469F, 0.0F, 0.0F));

        PartDefinition stage_2_head_0_canine_1_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_canine_1_tip_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.096F, -2.2022F, -0.12F, 0.192F, 2.4022F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.736F, 4.512F, -6.24F, 2.2398F, -1.1287F, 0.5947F));

        PartDefinition stage_2_head_0_canine_1_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_canine_1_root_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.384F, -2.5889F, -0.432F, 0.768F, 2.7889F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.832F, 2.4F, -5.664F, 2.8647F, -0.2997F, 0.0421F));

        PartDefinition stage_2_head_0_canine_left_tip_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_canine_left_tip_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.096F, -2.2022F, -0.12F, 0.192F, 2.4022F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.736F, 4.512F, -6.24F, 2.2398F, 1.1287F, -0.5947F));

        PartDefinition stage_2_head_0_canine_left_root_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_canine_left_root_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.384F, -2.5889F, -0.432F, 0.768F, 2.7889F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.832F, 2.4F, -5.664F, 2.8647F, 0.2997F, -0.0421F));

        PartDefinition stage_2_head_0_ear_inner_1_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_ear_inner_1_r1", CubeListBuilder.create().texOffs(32, 21).addBox(-0.72F, -2.784F, -0.528F, 1.44F, 1.728F, 0.384F, new CubeDeformation(0.0F))
        .texOffs(258, 51).addBox(-1.2F, -3.456F, -0.288F, 2.4F, 3.552F, 1.968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.512F, -4.128F, 0.96F, 0.0F, 0.0F, 0.384F));

        PartDefinition stage_2_head_0_brow_1_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_brow_1_r1", CubeListBuilder.create().texOffs(245, 51).addBox(-1.824F, -0.384F, -1.344F, 3.648F, 1.248F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.592F, -4.32F, -3.84F, 0.0F, 0.0F, -0.2094F));

        PartDefinition stage_2_head_0_muzzle_lobe_1_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_muzzle_lobe_1_r1", CubeListBuilder.create().texOffs(213, 51).addBox(-2.016F, -1.632F, -4.08F, 4.032F, 4.032F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.824F, 0.96F, -3.36F, 0.0F, 0.1396F, 0.0F));

        PartDefinition stage_2_head_0_cheek_1_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_cheek_1_r1", CubeListBuilder.create().texOffs(188, 51).addBox(-1.68F, -2.592F, -4.128F, 3.36F, 5.76F, 7.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, 0.0F, 0.0F, 0.0F, -0.1745F, -0.1571F));

        PartDefinition stage_2_head_0_ear_inner_left_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_ear_inner_left_r1", CubeListBuilder.create().texOffs(32, 21).addBox(-0.72F, -2.784F, -0.528F, 1.44F, 1.728F, 0.384F, new CubeDeformation(0.0F))
        .texOffs(258, 51).addBox(-1.2F, -3.456F, -0.288F, 2.4F, 3.552F, 1.968F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.512F, -4.128F, 0.96F, 0.0F, 0.0F, -0.384F));

        PartDefinition stage_2_head_0_brow_left_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_brow_left_r1", CubeListBuilder.create().texOffs(245, 51).addBox(-1.824F, -0.384F, -1.344F, 3.648F, 1.248F, 1.488F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.592F, -4.32F, -3.84F, 0.0F, 0.0F, 0.2094F));

        PartDefinition stage_2_head_0_muzzle_lobe_left_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_muzzle_lobe_left_r1", CubeListBuilder.create().texOffs(213, 51).addBox(-2.016F, -1.632F, -4.08F, 4.032F, 4.032F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.824F, 0.96F, -3.36F, 0.0F, -0.1396F, 0.0F));

        PartDefinition stage_2_head_0_cheek_left_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_cheek_left_r1", CubeListBuilder.create().texOffs(188, 51).addBox(-1.68F, -2.592F, -4.128F, 3.36F, 5.76F, 7.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.1571F));

        PartDefinition stage_2_head_0_forehead_r1 = stage_2_head_0.addOrReplaceChild("stage_2_head_0_forehead_r1", CubeListBuilder.create().texOffs(159, 51).addBox(-3.648F, -3.936F, -4.416F, 7.296F, 4.224F, 5.712F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.36F, 0.0F, -0.1222F, 0.0F, 0.0F));

        PartDefinition stage_2_jaw_0 = stage_2_head_0.addOrReplaceChild("stage_2_jaw_0", CubeListBuilder.create().texOffs(320, 51).addBox(-3.312F, 2.016F, -8.16F, 6.624F, 2.592F, 8.16F, new CubeDeformation(0.0F))
        .texOffs(353, 51).addBox(-3.072F, 1.632F, -7.776F, 6.144F, 0.672F, 4.416F, new CubeDeformation(0.0F))
        .texOffs(378, 51).addBox(1.776F, -2.688F, -0.96F, 1.248F, 5.952F, 2.016F, new CubeDeformation(0.0F))
        .texOffs(378, 51).addBox(-3.024F, -2.688F, -0.96F, 1.248F, 5.952F, 2.016F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.728F, 1.2F));

        PartDefinition stage_2_head_0_lower_canine_1_r1 = stage_2_jaw_0.addOrReplaceChild("stage_2_head_0_lower_canine_1_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.288F, -1.9662F, -0.288F, 0.576F, 2.1662F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.208F, 2.4F, -7.104F, 0.1914F, -0.0078F, 0.0816F));

        PartDefinition stage_2_head_0_lower_canine_left_r1 = stage_2_jaw_0.addOrReplaceChild("stage_2_head_0_lower_canine_left_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.288F, -1.9662F, -0.288F, 0.576F, 2.1662F, 0.576F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.208F, 2.4F, -7.104F, 0.1914F, 0.0078F, -0.0816F));

        PartDefinition stage_2_branch_1 = stage_2.addOrReplaceChild("stage_2_branch_1", CubeListBuilder.create(), PartPose.offset(0.0F, -56.64F, 0.0F));

        PartDefinition stage_2_neck_1 = stage_2_branch_1.addOrReplaceChild("stage_2_neck_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_2_neck_1_section_0_r1 = stage_2_neck_1.addOrReplaceChild("stage_2_neck_1_section_0_r1", CubeListBuilder.create().texOffs(55, 51).addBox(-2.52F, -4.7681F, -2.4F, 5.04F, 4.9681F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1416F, 0.0953F, 1.1842F));

        PartDefinition stage_2_neck_1_joint_1 = stage_2_neck_1.addOrReplaceChild("stage_2_neck_1_joint_1", CubeListBuilder.create(), PartPose.offset(4.2117F, -1.6484F, 0.6418F));

        PartDefinition stage_2_neck_1_section_1_r1 = stage_2_neck_1_joint_1.addOrReplaceChild("stage_2_neck_1_section_1_r1", CubeListBuilder.create().texOffs(213, 51).addBox(-2.28F, -4.7417F, -2.2F, 4.56F, 4.9417F, 4.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0353F, 0.0222F, 1.1238F));

        PartDefinition stage_2_neck_1_joint_2 = stage_2_neck_1_joint_1.addOrReplaceChild("stage_2_neck_1_joint_2", CubeListBuilder.create(), PartPose.offset(4.0945F, -1.9588F, 0.1602F));

        PartDefinition stage_2_neck_1_section_2_r1 = stage_2_neck_1_joint_2.addOrReplaceChild("stage_2_neck_1_section_2_r1", CubeListBuilder.create().texOffs(425, 51).addBox(-2.04F, -4.7156F, -2.0F, 4.08F, 4.9156F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4351F, -0.1627F, 0.7066F));

        PartDefinition stage_2_neck_1_joint_3 = stage_2_neck_1_joint_2.addOrReplaceChild("stage_2_neck_1_joint_3", CubeListBuilder.create(), PartPose.offset(2.8931F, -2.9143F, -1.8783F));

        PartDefinition stage_2_neck_1_section_3_r1 = stage_2_neck_1_joint_3.addOrReplaceChild("stage_2_neck_1_section_3_r1", CubeListBuilder.create().texOffs(165, 21).addBox(-1.8F, -4.768F, -1.8F, 3.6F, 4.968F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5942F, -0.1626F, 0.5202F));

        PartDefinition stage_2_head_1 = stage_2_neck_1_joint_3.addOrReplaceChild("stage_2_head_1", CubeListBuilder.create().texOffs(444, 51).addBox(-2.976F, -6.912F, -2.304F, 5.952F, 7.488F, 5.376F, new CubeDeformation(0.0F))
        .texOffs(0, 80).addBox(-2.352F, 2.496F, -7.872F, 4.704F, 3.36F, 3.792F, new CubeDeformation(0.0F))
        .texOffs(282, 51).addBox(-1.68F, 2.592F, -8.16F, 3.36F, 1.728F, 1.152F, new CubeDeformation(0.0F)), PartPose.offset(2.2407F, -3.0785F, -2.5237F));

        PartDefinition stage_2_head_1_tooth_1_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_tooth_1_r1", CubeListBuilder.create().texOffs(167, 0).addBox(-0.216F, -1.6787F, -0.24F, 0.432F, 1.8787F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(167, 0).addBox(2.088F, -1.6787F, -0.24F, 0.432F, 1.8787F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.152F, 4.32F, -6.72F, 2.9124F, 0.0F, 0.0F));

        PartDefinition stage_2_head_1_ram_horn_1_3_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_1_3_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.192F, -3.5214F, -0.192F, 0.384F, 3.7214F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.44F, -10.272F, 7.584F, 0.0604F, -0.2935F, 2.7382F));

        PartDefinition stage_2_head_1_ram_horn_1_2_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_1_2_r1", CubeListBuilder.create().texOffs(48, 80).addBox(-0.528F, -3.2179F, -0.48F, 1.056F, 3.4179F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.24F, -12.576F, 6.048F, -1.7555F, -1.0266F, -0.8757F));

        PartDefinition stage_2_head_1_ram_horn_1_1_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_1_1_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.696F, -4.4849F, -0.648F, 1.392F, 4.6849F, 1.296F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.744F, -10.848F, 3.024F, -0.8303F, -0.2973F, -0.6551F));

        PartDefinition stage_2_head_1_ram_horn_1_0_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_1_0_r1", CubeListBuilder.create().texOffs(39, 80).addBox(-0.864F, -5.4601F, -0.816F, 1.728F, 5.6601F, 1.632F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.256F, -6.144F, 1.2F, -0.3546F, -0.0518F, -0.2872F));

        PartDefinition stage_2_head_1_ear_1_tip_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ear_1_tip_r1", CubeListBuilder.create().texOffs(208, 0).addBox(-0.432F, -2.5613F, -0.36F, 0.864F, 2.7613F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.224F, -2.304F, 1.44F, 0.1257F, 0.2323F, -2.1524F));

        PartDefinition stage_2_head_1_ear_1_base_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ear_1_base_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-0.936F, -2.7349F, -0.624F, 1.872F, 2.9349F, 1.248F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.208F, -3.072F, 0.72F, -0.3384F, -0.4214F, -1.7935F));

        PartDefinition stage_2_head_1_brow_1_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_brow_1_r1", CubeListBuilder.create().texOffs(28, 80).addBox(-3.312F, -5.28F, -5.28F, 2.4F, 0.96F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition stage_2_head_1_eye_1_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_eye_1_r1", CubeListBuilder.create().texOffs(39, 21).addBox(-2.832F, -3.552F, -5.376F, 1.344F, 0.48F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(19, 80).addBox(-3.072F, -4.32F, -5.04F, 1.824F, 1.824F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_2_head_1_ram_horn_left_3_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_left_3_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.192F, -3.5214F, -0.192F, 0.384F, 3.7214F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.44F, -10.272F, 7.584F, 0.0604F, 0.2935F, -2.7382F));

        PartDefinition stage_2_head_1_ram_horn_left_2_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_left_2_r1", CubeListBuilder.create().texOffs(48, 80).addBox(-0.528F, -3.2179F, -0.48F, 1.056F, 3.4179F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.24F, -12.576F, 6.048F, -1.7555F, 1.0266F, 0.8757F));

        PartDefinition stage_2_head_1_ram_horn_left_1_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_left_1_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.696F, -4.4849F, -0.648F, 1.392F, 4.6849F, 1.296F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.744F, -10.848F, 3.024F, -0.8303F, 0.2973F, 0.6551F));

        PartDefinition stage_2_head_1_ram_horn_left_0_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ram_horn_left_0_r1", CubeListBuilder.create().texOffs(39, 80).addBox(-0.864F, -5.4601F, -0.816F, 1.728F, 5.6601F, 1.632F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.256F, -6.144F, 1.2F, -0.3546F, 0.0518F, 0.2872F));

        PartDefinition stage_2_head_1_ear_left_tip_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ear_left_tip_r1", CubeListBuilder.create().texOffs(208, 0).addBox(-0.432F, -2.5613F, -0.36F, 0.864F, 2.7613F, 0.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.224F, -2.304F, 1.44F, 0.1257F, -0.2323F, 2.1524F));

        PartDefinition stage_2_head_1_ear_left_base_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_ear_left_base_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-0.936F, -2.7349F, -0.624F, 1.872F, 2.9349F, 1.248F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.208F, -3.072F, 0.72F, -0.3384F, 0.4214F, 1.7935F));

        PartDefinition stage_2_head_1_brow_left_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_brow_left_r1", CubeListBuilder.create().texOffs(28, 80).addBox(0.912F, -5.28F, -5.28F, 2.4F, 0.96F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition stage_2_head_1_eye_left_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_eye_left_r1", CubeListBuilder.create().texOffs(39, 21).addBox(1.488F, -3.552F, -5.376F, 1.344F, 0.48F, 0.48F, new CubeDeformation(0.0F))
        .texOffs(19, 80).addBox(1.248F, -4.32F, -5.04F, 1.824F, 1.824F, 1.008F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_2_head_1_nasal_bridge_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_nasal_bridge_r1", CubeListBuilder.create().texOffs(486, 51).addBox(-1.488F, -1.824F, -2.448F, 2.976F, 6.624F, 2.304F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.728F, -3.264F, -0.2269F, 0.0F, 0.0F));

        PartDefinition stage_2_head_1_long_face_r1 = stage_2_head_1.addOrReplaceChild("stage_2_head_1_long_face_r1", CubeListBuilder.create().texOffs(469, 51).addBox(-2.16F, -1.056F, -2.16F, 4.32F, 9.504F, 2.928F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.84F, -2.16F, -0.2618F, 0.0F, 0.0F));

        PartDefinition stage_2_jaw_1 = stage_2_head_1.addOrReplaceChild("stage_2_jaw_1", CubeListBuilder.create().texOffs(55, 80).addBox(-2.064F, 2.016F, -7.92F, 4.128F, 2.208F, 8.16F, new CubeDeformation(0.0F))
        .texOffs(84, 80).addBox(-1.968F, 1.152F, -8.352F, 3.936F, 1.152F, 3.6F, new CubeDeformation(0.0F))
        .texOffs(101, 80).addBox(1.008F, -3.744F, -0.72F, 1.152F, 6.816F, 2.064F, new CubeDeformation(0.0F))
        .texOffs(101, 80).addBox(-2.16F, -3.744F, -0.72F, 1.152F, 6.816F, 2.064F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.36F, 0.48F));

        PartDefinition stage_2_head_1_beard_tip_r1 = stage_2_jaw_1.addOrReplaceChild("stage_2_head_1_beard_tip_r1", CubeListBuilder.create().texOffs(402, 51).addBox(-0.216F, -2.9276F, -0.24F, 0.432F, 3.1276F, 0.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.432F, -5.904F, -1.7986F, -1.3337F, -1.118F));

        PartDefinition stage_2_head_1_beard_root_r1 = stage_2_jaw_1.addOrReplaceChild("stage_2_head_1_beard_root_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.936F, -3.959F, -0.672F, 1.872F, 4.159F, 1.344F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.88F, -6.24F, -3.0521F, 0.0F, 0.0F));

        PartDefinition stage_2_branch_2 = stage_2.addOrReplaceChild("stage_2_branch_2", CubeListBuilder.create(), PartPose.offset(0.0F, -56.64F, 0.0F));

        PartDefinition stage_2_neck_2 = stage_2_branch_2.addOrReplaceChild("stage_2_neck_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_2_neck_2_section_0_r1 = stage_2_neck_2.addOrReplaceChild("stage_2_neck_2_section_0_r1", CubeListBuilder.create().texOffs(121, 80).addBox(-2.52F, -7.2033F, -2.4F, 5.04F, 7.4033F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3624F, -0.0877F, -0.4701F));

        PartDefinition stage_2_neck_2_joint_1 = stage_2_neck_2.addOrReplaceChild("stage_2_neck_2_joint_1", CubeListBuilder.create(), PartPose.offset(-3.1601F, -5.7394F, 2.4735F));

        PartDefinition stage_2_neck_2_section_1_r1 = stage_2_neck_2_joint_1.addOrReplaceChild("stage_2_neck_2_section_1_r1", CubeListBuilder.create().texOffs(144, 80).addBox(-2.31F, -7.2023F, -2.25F, 4.62F, 7.4023F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3489F, -0.0834F, -0.4648F));

        PartDefinition stage_2_neck_2_joint_2 = stage_2_neck_2_joint_1.addOrReplaceChild("stage_2_neck_2_joint_2", CubeListBuilder.create(), PartPose.offset(-3.1276F, -5.7931F, 2.3853F));

        PartDefinition stage_2_neck_2_section_2_r1 = stage_2_neck_2_joint_2.addOrReplaceChild("stage_2_neck_2_section_2_r1", CubeListBuilder.create().texOffs(144, 80).addBox(-2.1F, -7.199F, -2.1F, 4.2F, 7.399F, 4.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2539F, -0.0536F, -0.4138F));

        PartDefinition stage_2_neck_2_joint_3 = stage_2_neck_2_joint_2.addOrReplaceChild("stage_2_neck_2_joint_3", CubeListBuilder.create(), PartPose.offset(-2.8102F, -6.165F, 1.7556F));

        PartDefinition stage_2_neck_2_section_3_r1 = stage_2_neck_2_joint_3.addOrReplaceChild("stage_2_neck_2_section_3_r1", CubeListBuilder.create().texOffs(165, 80).addBox(-1.89F, -7.1693F, -1.95F, 3.78F, 7.3693F, 3.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1062F, -0.019F, -0.3532F));

        PartDefinition stage_2_neck_2_joint_4 = stage_2_neck_2_joint_3.addOrReplaceChild("stage_2_neck_2_joint_4", CubeListBuilder.create(), PartPose.offset(-2.4101F, -6.4974F, 0.7389F));

        PartDefinition stage_2_head_2_hood_1_edge_r1 = stage_2_neck_2_joint_4.addOrReplaceChild("stage_2_head_2_hood_1_edge_r1", CubeListBuilder.create().texOffs(328, 80).addBox(-0.648F, -5.0951F, -0.528F, 1.296F, 5.2951F, 1.056F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4439F, -0.5731F, -0.9213F, -0.518F, 1.1635F, 2.3757F));

        PartDefinition stage_2_head_2_hood_1_root_r1 = stage_2_neck_2_joint_4.addOrReplaceChild("stage_2_head_2_hood_1_root_r1", CubeListBuilder.create().texOffs(317, 80).addBox(-1.128F, -5.3045F, -0.816F, 2.256F, 5.5045F, 1.632F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7959F, -3.8371F, -1.5933F, -0.1382F, -0.2996F, -2.2816F));

        PartDefinition stage_2_head_2_hood_left_edge_r1 = stage_2_neck_2_joint_4.addOrReplaceChild("stage_2_head_2_hood_left_edge_r1", CubeListBuilder.create().texOffs(328, 80).addBox(-0.648F, -5.0951F, -0.528F, 1.296F, 5.2951F, 1.056F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5401F, -0.5731F, -0.9213F, -0.518F, -1.1635F, -2.3757F));

        PartDefinition stage_2_head_2_hood_left_root_r1 = stage_2_neck_2_joint_4.addOrReplaceChild("stage_2_head_2_hood_left_root_r1", CubeListBuilder.create().texOffs(317, 80).addBox(-1.128F, -5.3045F, -0.816F, 2.256F, 5.5045F, 1.632F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1079F, -3.8371F, -1.5933F, -0.1382F, 0.2996F, 2.2816F));

        PartDefinition stage_2_neck_2_section_4_r1 = stage_2_neck_2_joint_4.addOrReplaceChild("stage_2_neck_2_section_4_r1", CubeListBuilder.create().texOffs(165, 80).addBox(-1.68F, -7.0677F, -1.8F, 3.36F, 7.2677F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5795F, 0.0639F, -0.2135F));

        PartDefinition stage_2_head_2 = stage_2_neck_2_joint_4.addOrReplaceChild("stage_2_head_2", CubeListBuilder.create().texOffs(182, 80).addBox(-3.312F, -4.128F, -1.056F, 6.624F, 5.664F, 4.416F, new CubeDeformation(0.0F))
        .texOffs(244, 80).addBox(-2.592F, -3.744F, -8.4F, 5.184F, 2.208F, 4.944F, new CubeDeformation(0.0F))
        .texOffs(267, 80).addBox(-3.264F, 0.672F, -8.592F, 6.528F, 0.864F, 7.728F, new CubeDeformation(0.0F)), PartPose.offset(-1.4519F, -5.5651F, -3.7533F));

        PartDefinition stage_2_head_2_crown_tip_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_crown_tip_r1", CubeListBuilder.create().texOffs(218, 0).addBox(-0.096F, -2.2389F, -0.096F, 0.192F, 2.4389F, 0.192F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.624F, -8.544F, 2.208F, -1.1452F, -0.1888F, -0.2917F));

        PartDefinition stage_2_head_2_crown_crest_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_crown_crest_r1", CubeListBuilder.create().texOffs(413, 80).addBox(-0.72F, -6.2996F, -0.6F, 1.44F, 6.4996F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.072F, 1.2F, -0.19F, -0.0105F, -0.1104F));

        PartDefinition stage_2_head_2_fang_1_tip_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_fang_1_tip_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.12F, -3.1264F, -0.12F, 0.24F, 3.3264F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, 3.648F, -6.672F, -2.705F, 0.8508F, 0.2003F));

        PartDefinition stage_2_head_2_fang_1_root_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_fang_1_root_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.408F, -3.1949F, -0.408F, 0.816F, 3.3949F, 0.816F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.496F, 0.96F, -5.904F, 2.858F, -0.2296F, 0.0329F));

        PartDefinition stage_2_head_2_brow_1_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_brow_1_r1", CubeListBuilder.create().texOffs(261, 0).addBox(-4.08F, -4.896F, -5.52F, 2.592F, 1.056F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition stage_2_head_2_eye_1_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_eye_1_r1", CubeListBuilder.create().texOffs(39, 21).addBox(-3.504F, -3.744F, -5.616F, 1.152F, 0.864F, 0.672F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(-3.84F, -4.224F, -5.28F, 1.824F, 1.728F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_2_head_2_cheek_1_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_cheek_1_r1", CubeListBuilder.create().texOffs(298, 80).addBox(-1.296F, -4.032F, -3.36F, 2.592F, 5.088F, 5.184F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.976F, 0.0F, 0.0F, 0.0F, -0.2443F, -0.0873F));

        PartDefinition stage_2_head_2_fang_left_tip_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_fang_left_tip_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.12F, -3.1264F, -0.12F, 0.24F, 3.3264F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4F, 3.648F, -6.672F, -2.705F, -0.8508F, -0.2003F));

        PartDefinition stage_2_head_2_fang_left_root_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_fang_left_root_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.408F, -3.1949F, -0.408F, 0.816F, 3.3949F, 0.816F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.496F, 0.96F, -5.904F, 2.858F, 0.2296F, -0.0329F));

        PartDefinition stage_2_head_2_brow_left_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_brow_left_r1", CubeListBuilder.create().texOffs(261, 0).addBox(1.488F, -4.896F, -5.52F, 2.592F, 1.056F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        PartDefinition stage_2_head_2_eye_left_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_eye_left_r1", CubeListBuilder.create().texOffs(39, 21).addBox(2.352F, -3.744F, -5.616F, 1.152F, 0.864F, 0.672F, new CubeDeformation(0.0F))
        .texOffs(32, 21).addBox(2.016F, -4.224F, -5.28F, 1.824F, 1.728F, 0.864F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_2_head_2_cheek_left_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_cheek_left_r1", CubeListBuilder.create().texOffs(298, 80).addBox(-1.296F, -4.032F, -3.36F, 2.592F, 5.088F, 5.184F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.976F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0873F));

        PartDefinition stage_2_head_2_upper_jaw_r1 = stage_2_head_2.addOrReplaceChild("stage_2_head_2_upper_jaw_r1", CubeListBuilder.create().texOffs(207, 80).addBox(-3.984F, -2.592F, -9.6F, 7.968F, 3.456F, 9.504F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.72F, -0.0698F, 0.0F, 0.0F));

        PartDefinition stage_2_jaw_2 = stage_2_head_2.addOrReplaceChild("stage_2_jaw_2", CubeListBuilder.create().texOffs(342, 80).addBox(-3.216F, 0.576F, -9.696F, 6.432F, 2.4F, 10.368F, new CubeDeformation(0.0F))
        .texOffs(379, 80).addBox(-2.832F, -0.096F, -9.072F, 5.664F, 0.864F, 7.632F, new CubeDeformation(0.0F))
        .texOffs(252, 0).addBox(1.776F, -2.208F, -0.864F, 1.056F, 4.128F, 1.536F, new CubeDeformation(0.0F))
        .texOffs(252, 0).addBox(-2.832F, -2.208F, -0.864F, 1.056F, 4.128F, 1.536F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.536F, 1.104F));

        PartDefinition stage_2_head_2_fork_tongue_1_r1 = stage_2_jaw_2.addOrReplaceChild("stage_2_head_2_fork_tongue_1_r1", CubeListBuilder.create().texOffs(457, 21).addBox(-0.072F, -2.9461F, -0.072F, 0.144F, 3.1461F, 0.144F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.344F, -11.136F, 1.5372F, 0.5414F, -0.559F));

        PartDefinition stage_2_head_2_fork_tongue_left_r1 = stage_2_jaw_2.addOrReplaceChild("stage_2_head_2_fork_tongue_left_r1", CubeListBuilder.create().texOffs(457, 21).addBox(-0.072F, -2.9461F, -0.072F, 0.144F, 3.1461F, 0.144F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.344F, -11.136F, 1.5372F, -0.5414F, 0.559F));

        PartDefinition stage_2_head_2_tongue_root_r1 = stage_2_jaw_2.addOrReplaceChild("stage_2_head_2_tongue_root_r1", CubeListBuilder.create().texOffs(408, 80).addBox(-0.24F, -4.6997F, -0.192F, 0.48F, 4.8997F, 0.384F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.576F, -6.864F, 1.764F, 0.0F, 0.0F));

        PartDefinition stage_3 = partdefinition.addOrReplaceChild("stage_3", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_3_thorn_3_tip_r1 = stage_3.addOrReplaceChild("stage_3_thorn_3_tip_r1", CubeListBuilder.create().texOffs(317, 168).addBox(-0.125F, -11.24F, -0.125F, 0.25F, 11.44F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.92F, -64.0F, 7.68F, 0.0744F, 0.2249F, -2.5047F));

        PartDefinition stage_3_thorn_3_a_r1 = stage_3.addOrReplaceChild("stage_3_thorn_3_a_r1", CubeListBuilder.create().texOffs(304, 168).addBox(-1.325F, -22.8455F, -1.25F, 2.65F, 23.0455F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -52.48F, 0.0F, -0.3519F, -0.1788F, -0.9342F));

        PartDefinition stage_3_thorn_2_tip_r1 = stage_3.addOrReplaceChild("stage_3_thorn_2_tip_r1", CubeListBuilder.create().texOffs(317, 168).addBox(-0.125F, -11.24F, -0.125F, 0.25F, 11.44F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.92F, -55.04F, -7.68F, 0.0744F, -0.2249F, 2.5047F));

        PartDefinition stage_3_thorn_2_a_r1 = stage_3.addOrReplaceChild("stage_3_thorn_2_a_r1", CubeListBuilder.create().texOffs(304, 168).addBox(-1.325F, -22.8455F, -1.25F, 2.65F, 23.0455F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -43.52F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_3_thorn_1_tip_r1 = stage_3.addOrReplaceChild("stage_3_thorn_1_tip_r1", CubeListBuilder.create().texOffs(317, 168).addBox(-0.125F, -11.24F, -0.125F, 0.25F, 11.44F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.92F, -46.08F, 7.68F, 0.0744F, 0.2249F, -2.5047F));

        PartDefinition stage_3_thorn_1_a_r1 = stage_3.addOrReplaceChild("stage_3_thorn_1_a_r1", CubeListBuilder.create().texOffs(304, 168).addBox(-1.325F, -22.8455F, -1.25F, 2.65F, 23.0455F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -34.56F, 0.0F, -0.3519F, -0.1788F, -0.9342F));

        PartDefinition stage_3_thorn_0_tip_r1 = stage_3.addOrReplaceChild("stage_3_thorn_0_tip_r1", CubeListBuilder.create().texOffs(317, 168).addBox(-0.125F, -11.24F, -0.125F, 0.25F, 11.44F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.92F, -37.12F, -7.68F, 0.0744F, -0.2249F, 2.5047F));

        PartDefinition stage_3_thorn_0_a_r1 = stage_3.addOrReplaceChild("stage_3_thorn_0_a_r1", CubeListBuilder.create().texOffs(304, 168).addBox(-1.325F, -22.8455F, -1.25F, 2.65F, 23.0455F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -25.6F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_3_root_6_b_r1 = stage_3.addOrReplaceChild("stage_3_root_6_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.3002F, -8.32F, -8.6336F, 0.9679F, 0.822F, -1.3845F));

        PartDefinition stage_3_root_6_a_r1 = stage_3.addOrReplaceChild("stage_3_root_6_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, 0.8769F, 0.5967F, -1.161F));

        PartDefinition stage_3_root_5_b_r1 = stage_3.addOrReplaceChild("stage_3_root_5_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3279F, -8.32F, -13.436F, 2.0798F, -0.0363F, 0.0213F));

        PartDefinition stage_3_root_5_a_r1 = stage_3.addOrReplaceChild("stage_3_root_5_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, 1.7121F, -0.0278F, 0.0242F));

        PartDefinition stage_3_root_4_b_r1 = stage_3.addOrReplaceChild("stage_3_root_4_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.7091F, -8.32F, -8.1208F, 0.8571F, -0.7986F, 1.4914F));

        PartDefinition stage_3_root_4_a_r1 = stage_3.addOrReplaceChild("stage_3_root_4_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, 0.798F, -0.5818F, 1.235F));

        PartDefinition stage_3_root_3_b_r1 = stage_3.addOrReplaceChild("stage_3_root_3_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.0262F, -8.32F, 3.3095F, -0.232F, 0.363F, 2.0102F));

        PartDefinition stage_3_root_3_a_r1 = stage_3.addOrReplaceChild("stage_3_root_3_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, -0.2561F, 0.2759F, 1.6459F));

        PartDefinition stage_3_root_2_b_r1 = stage_3.addOrReplaceChild("stage_3_root_2_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5342F, -8.32F, 12.2477F, -1.8588F, 0.5924F, 0.4481F));

        PartDefinition stage_3_root_2_a_r1 = stage_3.addOrReplaceChild("stage_3_root_2_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, -1.5131F, 0.4426F, 0.468F));

        PartDefinition stage_3_root_1_b_r1 = stage_3.addOrReplaceChild("stage_3_root_1_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.1251F, -8.32F, 11.9631F, -1.7971F, -0.648F, -0.5224F));

        PartDefinition stage_3_root_1_a_r1 = stage_3.addOrReplaceChild("stage_3_root_1_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, -1.4627F, -0.4814F, -0.534F));

        PartDefinition stage_3_root_0_b_r1 = stage_3.addOrReplaceChild("stage_3_root_0_b_r1", CubeListBuilder.create().texOffs(50, 119).addBox(-0.775F, -16.3302F, -0.775F, 1.55F, 16.5302F, 1.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.1721F, -8.32F, 2.6701F, -0.1822F, -0.2939F, -2.0358F));

        PartDefinition stage_3_root_0_a_r1 = stage_3.addOrReplaceChild("stage_3_root_0_a_r1", CubeListBuilder.create().texOffs(29, 119).addBox(-2.08F, -13.7765F, -2.08F, 4.16F, 13.9765F, 4.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.24F, 0.0F, -0.2031F, -0.2241F, -1.6698F));

        PartDefinition stage_3_trunk_2_r1 = stage_3.addOrReplaceChild("stage_3_trunk_2_r1", CubeListBuilder.create().texOffs(0, 119).addBox(-3.168F, -20.8394F, -3.0464F, 6.336F, 21.0394F, 6.0928F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.56F, -55.04F, 0.0F, 0.0F, 0.0F, -0.1244F));

        PartDefinition stage_3_trunk_1_r1 = stage_3.addOrReplaceChild("stage_3_trunk_1_r1", CubeListBuilder.create().texOffs(479, 80).addBox(-3.984F, -27.7888F, -3.7632F, 7.968F, 27.9888F, 7.5264F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.52F, -28.16F, 1.28F, 0.0464F, -0.0052F, 0.2222F));

        PartDefinition stage_3_trunk_0_r1 = stage_3.addOrReplaceChild("stage_3_trunk_0_r1", CubeListBuilder.create().texOffs(440, 80).addBox(-4.8F, -28.3106F, -4.48F, 9.6F, 28.5106F, 8.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 0.0F, -0.0456F, -0.0029F, -0.1255F));

        PartDefinition stage_3_branch_0 = stage_3.addOrReplaceChild("stage_3_branch_0", CubeListBuilder.create(), PartPose.offset(0.0F, -75.52F, 0.0F));

        PartDefinition stage_3_neck_0 = stage_3_branch_0.addOrReplaceChild("stage_3_neck_0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_0_section_0_r1 = stage_3_neck_0.addOrReplaceChild("stage_3_neck_0_section_0_r1", CubeListBuilder.create().texOffs(59, 119).addBox(-3.36F, -4.2244F, -3.2F, 6.72F, 4.4244F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2689F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_0_joint_1 = stage_3_neck_0.addOrReplaceChild("stage_3_neck_0_joint_1", CubeListBuilder.create(), PartPose.offset(0.0F, -3.8797F, 1.0693F));

        PartDefinition stage_3_neck_0_section_1_r1 = stage_3_neck_0_joint_1.addOrReplaceChild("stage_3_neck_0_section_1_r1", CubeListBuilder.create().texOffs(88, 119).addBox(-3.36F, -4.1534F, -3.0F, 6.72F, 4.3534F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3511F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_0_joint_2 = stage_3_neck_0_joint_1.addOrReplaceChild("stage_3_neck_0_joint_2", CubeListBuilder.create(), PartPose.offset(0.0F, -3.7122F, -1.3596F));

        PartDefinition stage_3_neck_0_section_2_r1 = stage_3_neck_0_joint_2.addOrReplaceChild("stage_3_neck_0_section_2_r1", CubeListBuilder.create().texOffs(88, 119).addBox(-3.36F, -4.2323F, -2.8F, 6.72F, 4.4323F, 5.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6924F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_0_joint_3 = stage_3_neck_0_joint_2.addOrReplaceChild("stage_3_neck_0_joint_3", CubeListBuilder.create(), PartPose.offset(0.0F, -3.1038F, -2.574F));

        PartDefinition stage_3_neck_0_section_3_r1 = stage_3_neck_0_joint_3.addOrReplaceChild("stage_3_neck_0_section_3_r1", CubeListBuilder.create().texOffs(88, 119).addBox(-3.36F, -4.2352F, -2.6F, 6.72F, 4.4352F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7571F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_0_joint_4 = stage_3_neck_0_joint_3.addOrReplaceChild("stage_3_neck_0_joint_4", CubeListBuilder.create(), PartPose.offset(0.0F, -2.9329F, -2.7714F));

        PartDefinition stage_3_neck_0_section_4_r1 = stage_3_neck_0_joint_4.addOrReplaceChild("stage_3_neck_0_section_4_r1", CubeListBuilder.create().texOffs(453, 0).addBox(-3.36F, -4.234F, -2.4F, 6.72F, 4.434F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.728F, 0.0F, 0.0F));

        PartDefinition stage_3_head_0 = stage_3_neck_0_joint_4.addOrReplaceChild("stage_3_head_0", CubeListBuilder.create().texOffs(115, 119).addBox(-6.08F, -8.192F, -4.8F, 12.16F, 9.856F, 9.92F, new CubeDeformation(0.0F))
        .texOffs(257, 119).addBox(1.6F, -5.12F, -6.272F, 3.712F, 2.304F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(270, 119).addBox(2.304F, -4.352F, -6.656F, 2.304F, 1.024F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(257, 119).addBox(-5.312F, -5.12F, -6.272F, 3.712F, 2.304F, 1.6F, new CubeDeformation(0.0F))
        .texOffs(270, 119).addBox(-4.608F, -4.352F, -6.656F, 2.304F, 1.024F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(316, 119).addBox(-1.728F, -4.096F, -7.872F, 3.456F, 3.84F, 3.904F, new CubeDeformation(0.0F))
        .texOffs(333, 119).addBox(-2.176F, -1.408F, -10.816F, 4.352F, 2.176F, 1.472F, new CubeDeformation(0.0F))
        .texOffs(348, 119).addBox(-4.8F, 3.2F, -9.152F, 9.6F, 1.792F, 5.312F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0113F, -2.6843F));

        PartDefinition stage_3_head_0_crown_horn_1_2_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_crown_horn_1_2_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.16F, -2.9609F, -0.16F, 0.32F, 3.1609F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.72F, -15.104F, 6.4F, -1.7161F, 0.4186F, 0.3631F));

        PartDefinition stage_3_head_0_crown_horn_1_1_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_crown_horn_1_1_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.576F, -3.8368F, -0.512F, 1.152F, 4.0368F, 1.024F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.888F, -12.416F, 4.096F, -0.689F, -0.0834F, -0.2316F));

        PartDefinition stage_3_head_0_crown_horn_1_0_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_crown_horn_1_0_r1", CubeListBuilder.create().texOffs(9, 149).addBox(-0.832F, -6.2068F, -0.768F, 1.664F, 6.4068F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.16F, -7.04F, 2.048F, -0.3484F, -0.0518F, -0.2922F));

        PartDefinition stage_3_head_0_crown_horn_left_2_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_crown_horn_left_2_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.16F, -2.9609F, -0.16F, 0.32F, 3.1609F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.72F, -15.104F, 6.4F, -1.7161F, -0.4186F, -0.3631F));

        PartDefinition stage_3_head_0_crown_horn_left_1_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_crown_horn_left_1_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.576F, -3.8368F, -0.512F, 1.152F, 4.0368F, 1.024F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.888F, -12.416F, 4.096F, -0.689F, 0.0834F, 0.2316F));

        PartDefinition stage_3_head_0_crown_horn_left_0_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_crown_horn_left_0_r1", CubeListBuilder.create().texOffs(9, 149).addBox(-0.832F, -6.2068F, -0.768F, 1.664F, 6.4068F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.16F, -7.04F, 2.048F, -0.3484F, 0.0518F, 0.2922F));

        PartDefinition stage_3_head_0_mane_13_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_13_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.8603F, -0.384F, 0.576F, 5.0603F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.707F, 2.5399F, 2.88F, -2.6726F, 0.4237F, 0.1027F));

        PartDefinition stage_3_head_0_mane_13_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_13_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.736F, -2.8016F, -0.768F, 1.472F, 3.0016F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.7844F, 0.8278F, 1.152F, -1.9828F, -0.76F, -0.5112F));

        PartDefinition stage_3_head_0_mane_13_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_13_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -4.1262F, -1.44F, 3.136F, 4.3262F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4976F, -1.2223F, 1.792F, 0.1709F, 0.2896F, -2.0792F));

        PartDefinition stage_3_head_0_mane_12_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_12_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.8497F, -0.384F, 0.576F, 5.0497F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0254F, 6.0138F, 2.88F, -2.6951F, 0.2966F, 0.0678F));

        PartDefinition stage_3_head_0_mane_12_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_12_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.736F, -3.2807F, -0.768F, 1.472F, 3.4807F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.387F, 3.5445F, 1.152F, -2.3773F, -0.6257F, -0.2586F));

        PartDefinition stage_3_head_0_mane_12_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_12_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -4.3291F, -1.44F, 3.136F, 4.5291F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1125F, 0.1583F, 1.792F, 0.177F, 0.4945F, -2.4654F));

        PartDefinition stage_3_head_0_mane_11_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_11_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.8411F, -0.384F, 0.576F, 5.0411F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1504F, 7.9417F, 2.88F, -2.7125F, 0.1068F, 0.0233F));

        PartDefinition stage_3_head_0_mane_11_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_11_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.736F, -3.5744F, -0.768F, 1.472F, 3.7744F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9226F, 5.0522F, 1.152F, -2.5858F, -0.243F, -0.0696F));

        PartDefinition stage_3_head_0_mane_11_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_11_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -4.4552F, -1.44F, 3.136F, 4.6552F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1108F, 0.9245F, 1.792F, 0.5509F, 1.2793F, -2.4156F));

        PartDefinition stage_3_head_0_mane_10_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_10_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.8411F, -0.384F, 0.576F, 5.0411F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1504F, 7.9417F, 2.88F, -2.7125F, -0.1068F, -0.0233F));

        PartDefinition stage_3_head_0_mane_10_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_10_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.736F, -3.5744F, -0.768F, 1.472F, 3.7744F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9226F, 5.0522F, 1.152F, -2.5858F, 0.243F, 0.0696F));

        PartDefinition stage_3_head_0_mane_10_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_10_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -4.4552F, -1.44F, 3.136F, 4.6552F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1108F, 0.9245F, 1.792F, 0.5509F, -1.2793F, 2.4156F));

        PartDefinition stage_3_head_0_mane_9_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_9_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.8497F, -0.384F, 0.576F, 5.0497F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0254F, 6.0138F, 2.88F, -2.6951F, -0.2966F, -0.0678F));

        PartDefinition stage_3_head_0_mane_9_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_9_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.736F, -3.2807F, -0.768F, 1.472F, 3.4807F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.387F, 3.5445F, 1.152F, -2.3773F, 0.6257F, 0.2586F));

        PartDefinition stage_3_head_0_mane_9_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_9_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -4.3291F, -1.44F, 3.136F, 4.5291F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1125F, 0.1583F, 1.792F, 0.177F, -0.4945F, 2.4654F));

        PartDefinition stage_3_head_0_mane_8_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_8_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.8603F, -0.384F, 0.576F, 5.0603F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.707F, 2.5399F, 2.88F, -2.6726F, -0.4237F, -0.1027F));

        PartDefinition stage_3_head_0_mane_8_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_8_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.736F, -2.8016F, -0.768F, 1.472F, 3.0016F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.7844F, 0.8278F, 1.152F, -1.9828F, 0.76F, 0.5112F));

        PartDefinition stage_3_head_0_mane_8_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_8_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -4.1262F, -1.44F, 3.136F, 4.3262F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4976F, -1.2223F, 1.792F, 0.1709F, -0.2896F, 2.0792F));

        PartDefinition stage_3_head_0_mane_7_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_7_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.865F, -0.384F, 0.576F, 5.065F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.664F, -1.792F, 2.88F, -2.6623F, -0.4679F, -0.1163F));

        PartDefinition stage_3_head_0_mane_7_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_7_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.3504F, -0.768F, 1.472F, 2.5504F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.64F, -2.56F, 1.152F, -1.5763F, 0.6375F, 0.6343F));

        PartDefinition stage_3_head_0_mane_7_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_7_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -3.9236F, -1.44F, 3.136F, 4.1236F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.992F, -2.944F, 1.792F, 0.1759F, -0.1889F, 1.6423F));

        PartDefinition stage_3_head_0_mane_6_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_6_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.1668F, -0.768F, 1.472F, 2.3668F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.7844F, -5.9478F, 1.152F, -1.2504F, 0.3879F, 0.5314F));

        PartDefinition stage_3_head_0_mane_6_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_6_root_r1", CubeListBuilder.create().texOffs(493, 119).addBox(-1.568F, -3.7855F, -1.44F, 3.136F, 3.9855F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4976F, -4.6657F, 1.792F, 0.1808F, -0.1208F, 1.1768F));

        PartDefinition stage_3_head_0_mane_5_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_5_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.2651F, -0.768F, 1.472F, 2.4651F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.387F, -8.6645F, 1.152F, -1.0167F, 0.1791F, 0.3196F));

        PartDefinition stage_3_head_0_mane_5_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_5_root_r1", CubeListBuilder.create().texOffs(493, 119).addBox(-1.568F, -3.7268F, -1.44F, 3.136F, 3.9268F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1125F, -6.0463F, 1.792F, 0.1829F, -0.0672F, 0.7028F));

        PartDefinition stage_3_head_0_mane_4_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_4_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.4067F, -0.768F, 1.472F, 2.6067F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9226F, -10.1722F, 1.152F, -0.9011F, 0.0501F, 0.1036F));

        PartDefinition stage_3_head_0_mane_4_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_4_root_r1", CubeListBuilder.create().texOffs(493, 119).addBox(-1.568F, -3.7152F, -1.44F, 3.136F, 3.9152F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1108F, -6.8125F, 1.792F, 0.1831F, -0.0215F, 0.2331F));

        PartDefinition stage_3_head_0_mane_3_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_3_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.4067F, -0.768F, 1.472F, 2.6067F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9226F, -10.1722F, 1.152F, -0.9011F, -0.0501F, -0.1036F));

        PartDefinition stage_3_head_0_mane_3_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_3_root_r1", CubeListBuilder.create().texOffs(493, 119).addBox(-1.568F, -3.7152F, -1.44F, 3.136F, 3.9152F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1108F, -6.8125F, 1.792F, 0.1831F, 0.0215F, -0.2331F));

        PartDefinition stage_3_head_0_mane_2_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_2_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.2651F, -0.768F, 1.472F, 2.4651F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.387F, -8.6645F, 1.152F, -1.0167F, -0.1791F, -0.3196F));

        PartDefinition stage_3_head_0_mane_2_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_2_root_r1", CubeListBuilder.create().texOffs(493, 119).addBox(-1.568F, -3.7268F, -1.44F, 3.136F, 3.9268F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1125F, -6.0463F, 1.792F, 0.1829F, 0.0672F, -0.7028F));

        PartDefinition stage_3_head_0_mane_1_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_1_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.1668F, -0.768F, 1.472F, 2.3668F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.7844F, -5.9478F, 1.152F, -1.2504F, -0.3879F, -0.5314F));

        PartDefinition stage_3_head_0_mane_1_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_1_root_r1", CubeListBuilder.create().texOffs(493, 119).addBox(-1.568F, -3.7855F, -1.44F, 3.136F, 3.9855F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4976F, -4.6657F, 1.792F, 0.1808F, 0.1208F, -1.1768F));

        PartDefinition stage_3_head_0_mane_0_tassel_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_0_tassel_r1", CubeListBuilder.create().texOffs(488, 119).addBox(-0.288F, -4.865F, -0.384F, 0.576F, 5.065F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.664F, -1.792F, 2.88F, -2.6623F, 0.4679F, 0.1163F));

        PartDefinition stage_3_head_0_mane_0_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_0_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.736F, -2.3504F, -0.768F, 1.472F, 2.5504F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.64F, -2.56F, 1.152F, -1.5763F, -0.6375F, -0.6343F));

        PartDefinition stage_3_head_0_mane_0_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_mane_0_root_r1", CubeListBuilder.create().texOffs(473, 119).addBox(-1.568F, -3.9236F, -1.44F, 3.136F, 4.1236F, 2.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.992F, -2.944F, 1.792F, 0.1759F, 0.1889F, -1.6423F));

        PartDefinition stage_3_head_0_incisor_4_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_incisor_4_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.288F, -2.1994F, -0.32F, 0.576F, 2.3994F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(0.864F, -2.1994F, -0.32F, 0.576F, 2.3994F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(2.016F, -2.1994F, -0.32F, 0.576F, 2.3994F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(3.168F, -2.1994F, -0.32F, 0.576F, 2.3994F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(4.32F, -2.1994F, -0.32F, 0.576F, 2.3994F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.304F, 3.584F, -8.576F, 2.4469F, 0.0F, 0.0F));

        PartDefinition stage_3_head_0_canine_1_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_canine_1_tip_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.128F, -2.8697F, -0.16F, 0.256F, 3.0697F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.648F, 6.016F, -8.32F, 2.2398F, -1.1287F, 0.5947F));

        PartDefinition stage_3_head_0_canine_1_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_canine_1_root_r1", CubeListBuilder.create().texOffs(464, 119).addBox(-0.512F, -3.3852F, -0.576F, 1.024F, 3.5852F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.776F, 3.2F, -7.552F, 2.8647F, -0.2997F, 0.0421F));

        PartDefinition stage_3_head_0_canine_left_tip_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_canine_left_tip_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.128F, -2.8697F, -0.16F, 0.256F, 3.0697F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.648F, 6.016F, -8.32F, 2.2398F, 1.1287F, -0.5947F));

        PartDefinition stage_3_head_0_canine_left_root_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_canine_left_root_r1", CubeListBuilder.create().texOffs(464, 119).addBox(-0.512F, -3.3852F, -0.576F, 1.024F, 3.5852F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.776F, 3.2F, -7.552F, 2.8647F, 0.2997F, -0.0421F));

        PartDefinition stage_3_head_0_ear_inner_1_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_ear_inner_1_r1", CubeListBuilder.create().texOffs(309, 119).addBox(-0.96F, -3.712F, -0.704F, 1.92F, 2.304F, 0.512F, new CubeDeformation(0.0F))
        .texOffs(294, 119).addBox(-1.6F, -4.608F, -0.384F, 3.2F, 4.736F, 2.624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.016F, -5.504F, 1.28F, 0.0F, 0.0F, 0.384F));

        PartDefinition stage_3_head_0_brow_1_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_brow_1_r1", CubeListBuilder.create().texOffs(279, 119).addBox(-2.432F, -0.512F, -1.792F, 4.864F, 1.664F, 1.984F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.456F, -5.76F, -5.12F, 0.0F, 0.0F, -0.2094F));

        PartDefinition stage_3_head_0_muzzle_lobe_1_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_muzzle_lobe_1_r1", CubeListBuilder.create().texOffs(230, 119).addBox(-2.688F, -2.176F, -5.44F, 5.376F, 5.376F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.432F, 1.28F, -4.48F, 0.0F, 0.1396F, 0.0F));

        PartDefinition stage_3_head_0_cheek_1_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_cheek_1_r1", CubeListBuilder.create().texOffs(199, 119).addBox(-2.24F, -3.456F, -5.504F, 4.48F, 7.68F, 9.344F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.8F, 0.0F, 0.0F, 0.0F, -0.1745F, -0.1571F));

        PartDefinition stage_3_head_0_ear_inner_left_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_ear_inner_left_r1", CubeListBuilder.create().texOffs(309, 119).addBox(-0.96F, -3.712F, -0.704F, 1.92F, 2.304F, 0.512F, new CubeDeformation(0.0F))
        .texOffs(294, 119).addBox(-1.6F, -4.608F, -0.384F, 3.2F, 4.736F, 2.624F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.016F, -5.504F, 1.28F, 0.0F, 0.0F, -0.384F));

        PartDefinition stage_3_head_0_brow_left_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_brow_left_r1", CubeListBuilder.create().texOffs(279, 119).addBox(-2.432F, -0.512F, -1.792F, 4.864F, 1.664F, 1.984F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.456F, -5.76F, -5.12F, 0.0F, 0.0F, 0.2094F));

        PartDefinition stage_3_head_0_muzzle_lobe_left_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_muzzle_lobe_left_r1", CubeListBuilder.create().texOffs(230, 119).addBox(-2.688F, -2.176F, -5.44F, 5.376F, 5.376F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.432F, 1.28F, -4.48F, 0.0F, -0.1396F, 0.0F));

        PartDefinition stage_3_head_0_cheek_left_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_cheek_left_r1", CubeListBuilder.create().texOffs(199, 119).addBox(-2.24F, -3.456F, -5.504F, 4.48F, 7.68F, 9.344F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.1571F));

        PartDefinition stage_3_head_0_forehead_r1 = stage_3_head_0.addOrReplaceChild("stage_3_head_0_forehead_r1", CubeListBuilder.create().texOffs(162, 119).addBox(-4.864F, -5.248F, -5.888F, 9.728F, 5.632F, 7.616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.48F, 0.0F, -0.1222F, 0.0F, 0.0F));

        PartDefinition stage_3_jaw_0 = stage_3_head_0.addOrReplaceChild("stage_3_jaw_0", CubeListBuilder.create().texOffs(381, 119).addBox(-4.416F, 2.688F, -10.88F, 8.832F, 3.456F, 10.88F, new CubeDeformation(0.0F))
        .texOffs(422, 119).addBox(-4.096F, 2.176F, -10.368F, 8.192F, 0.896F, 5.888F, new CubeDeformation(0.0F))
        .texOffs(453, 119).addBox(2.368F, -3.584F, -1.28F, 1.664F, 7.936F, 2.688F, new CubeDeformation(0.0F))
        .texOffs(453, 119).addBox(-4.032F, -3.584F, -1.28F, 1.664F, 7.936F, 2.688F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.304F, 1.6F));

        PartDefinition stage_3_head_0_lower_canine_1_r1 = stage_3_jaw_0.addOrReplaceChild("stage_3_head_0_lower_canine_1_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.384F, -2.555F, -0.384F, 0.768F, 2.755F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.944F, 3.2F, -9.472F, 0.1914F, -0.0078F, 0.0816F));

        PartDefinition stage_3_head_0_lower_canine_left_r1 = stage_3_jaw_0.addOrReplaceChild("stage_3_head_0_lower_canine_left_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.384F, -2.555F, -0.384F, 0.768F, 2.755F, 0.768F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.944F, 3.2F, -9.472F, 0.1914F, 0.0078F, -0.0816F));

        PartDefinition stage_3_branch_1 = stage_3.addOrReplaceChild("stage_3_branch_1", CubeListBuilder.create(), PartPose.offset(0.0F, -75.52F, 0.0F));

        PartDefinition stage_3_neck_1 = stage_3_branch_1.addOrReplaceChild("stage_3_neck_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_1_section_0_r1 = stage_3_neck_1.addOrReplaceChild("stage_3_neck_1_section_0_r1", CubeListBuilder.create().texOffs(18, 149).addBox(-3.36F, -5.0724F, -3.2F, 6.72F, 5.2724F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1377F, 0.0925F, 1.1822F));

        PartDefinition stage_3_neck_1_joint_1 = stage_3_neck_1.addOrReplaceChild("stage_3_neck_1_joint_1", CubeListBuilder.create(), PartPose.offset(4.4899F, -1.7712F, 0.6661F));

        PartDefinition stage_3_neck_1_section_1_r1 = stage_3_neck_1_joint_1.addOrReplaceChild("stage_3_neck_1_section_1_r1", CubeListBuilder.create().texOffs(47, 149).addBox(-3.12F, -5.0691F, -3.0F, 6.24F, 5.2691F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1148F, 0.0761F, 1.1704F));

        PartDefinition stage_3_neck_1_joint_2 = stage_3_neck_1_joint_1.addOrReplaceChild("stage_3_neck_1_joint_2", CubeListBuilder.create(), PartPose.offset(4.4711F, -1.8463F, 0.5559F));

        PartDefinition stage_3_neck_1_section_2_r1 = stage_3_neck_1_joint_2.addOrReplaceChild("stage_3_neck_1_section_2_r1", CubeListBuilder.create().texOffs(74, 149).addBox(-2.88F, -5.0099F, -2.8F, 5.76F, 5.2099F, 5.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1812F, -0.0943F, 0.9579F));

        PartDefinition stage_3_neck_1_joint_3 = stage_3_neck_1_joint_2.addOrReplaceChild("stage_3_neck_1_joint_3", CubeListBuilder.create(), PartPose.offset(3.9169F, -2.6548F, -0.8631F));

        PartDefinition stage_3_neck_1_section_3_r1 = stage_3_neck_1_joint_3.addOrReplaceChild("stage_3_neck_1_section_3_r1", CubeListBuilder.create().texOffs(74, 149).addBox(-2.64F, -5.0618F, -2.6F, 5.28F, 5.2618F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5424F, -0.1667F, 0.5837F));

        PartDefinition stage_3_neck_1_joint_4 = stage_3_neck_1_joint_3.addOrReplaceChild("stage_3_neck_1_joint_4", CubeListBuilder.create(), PartPose.offset(2.6422F, -3.2451F, -2.475F));

        PartDefinition stage_3_neck_1_section_4_r1 = stage_3_neck_1_joint_4.addOrReplaceChild("stage_3_neck_1_section_4_r1", CubeListBuilder.create().texOffs(99, 149).addBox(-2.4F, -5.0722F, -2.4F, 4.8F, 5.2722F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5923F, -0.1628F, 0.5226F));

        PartDefinition stage_3_head_1 = stage_3_neck_1_joint_4.addOrReplaceChild("stage_3_head_1", CubeListBuilder.create().texOffs(120, 149).addBox(-3.968F, -9.216F, -3.072F, 7.936F, 9.984F, 7.168F, new CubeDeformation(0.0F))
        .texOffs(88, 119).addBox(-3.136F, 3.328F, -10.496F, 6.272F, 4.48F, 5.056F, new CubeDeformation(0.0F))
        .texOffs(333, 119).addBox(-2.24F, 3.456F, -10.88F, 4.48F, 2.304F, 1.536F, new CubeDeformation(0.0F)), PartPose.offset(2.3999F, -3.2825F, -2.684F));

        PartDefinition stage_3_head_1_tooth_1_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_tooth_1_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.288F, -2.1716F, -0.32F, 0.576F, 2.3716F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(2.784F, -2.1716F, -0.32F, 0.576F, 2.3716F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.536F, 5.76F, -8.96F, 2.9124F, 0.0F, 0.0F));

        PartDefinition stage_3_head_1_ram_horn_1_3_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_1_3_r1", CubeListBuilder.create().texOffs(228, 149).addBox(-0.256F, -4.6285F, -0.256F, 0.512F, 4.8285F, 0.512F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.92F, -13.696F, 10.112F, 0.0604F, -0.2935F, 2.7382F));

        PartDefinition stage_3_head_1_ram_horn_1_2_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_1_2_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.704F, -4.2239F, -0.64F, 1.408F, 4.4239F, 1.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.32F, -16.768F, 8.064F, -1.7555F, -1.0266F, -0.8757F));

        PartDefinition stage_3_head_1_ram_horn_1_1_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_1_1_r1", CubeListBuilder.create().texOffs(9, 149).addBox(-0.928F, -5.9132F, -0.864F, 1.856F, 6.1132F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.992F, -14.464F, 4.032F, -0.8303F, -0.2973F, -0.6551F));

        PartDefinition stage_3_head_1_ram_horn_1_0_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_1_0_r1", CubeListBuilder.create().texOffs(215, 149).addBox(-1.152F, -7.2135F, -1.088F, 2.304F, 7.4135F, 2.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.008F, -8.192F, 1.6F, -0.3546F, -0.0518F, -0.2872F));

        PartDefinition stage_3_head_1_ear_1_tip_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ear_1_tip_r1", CubeListBuilder.create().texOffs(201, 0).addBox(-0.576F, -3.3484F, -0.48F, 1.152F, 3.5484F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.632F, -3.072F, 1.92F, 0.1257F, 0.2323F, -2.1524F));

        PartDefinition stage_3_head_1_ear_1_base_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ear_1_base_r1", CubeListBuilder.create().texOffs(258, 51).addBox(-1.248F, -3.5799F, -0.832F, 2.496F, 3.7799F, 1.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.944F, -4.096F, 0.96F, -0.3384F, -0.4214F, -1.7935F));

        PartDefinition stage_3_head_1_brow_1_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_brow_1_r1", CubeListBuilder.create().texOffs(202, 149).addBox(-4.416F, -7.04F, -7.04F, 3.2F, 1.28F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition stage_3_head_1_eye_1_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_eye_1_r1", CubeListBuilder.create().texOffs(39, 21).addBox(-3.776F, -4.736F, -7.168F, 1.792F, 0.64F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(191, 149).addBox(-4.096F, -5.76F, -6.72F, 2.432F, 2.432F, 1.344F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_3_head_1_ram_horn_left_3_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_left_3_r1", CubeListBuilder.create().texOffs(228, 149).addBox(-0.256F, -4.6285F, -0.256F, 0.512F, 4.8285F, 0.512F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.92F, -13.696F, 10.112F, 0.0604F, 0.2935F, -2.7382F));

        PartDefinition stage_3_head_1_ram_horn_left_2_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_left_2_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.704F, -4.2239F, -0.64F, 1.408F, 4.4239F, 1.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.32F, -16.768F, 8.064F, -1.7555F, 1.0266F, 0.8757F));

        PartDefinition stage_3_head_1_ram_horn_left_1_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_left_1_r1", CubeListBuilder.create().texOffs(9, 149).addBox(-0.928F, -5.9132F, -0.864F, 1.856F, 6.1132F, 1.728F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.992F, -14.464F, 4.032F, -0.8303F, 0.2973F, 0.6551F));

        PartDefinition stage_3_head_1_ram_horn_left_0_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ram_horn_left_0_r1", CubeListBuilder.create().texOffs(215, 149).addBox(-1.152F, -7.2135F, -1.088F, 2.304F, 7.4135F, 2.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.008F, -8.192F, 1.6F, -0.3546F, 0.0518F, 0.2872F));

        PartDefinition stage_3_head_1_ear_left_tip_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ear_left_tip_r1", CubeListBuilder.create().texOffs(201, 0).addBox(-0.576F, -3.3484F, -0.48F, 1.152F, 3.5484F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.632F, -3.072F, 1.92F, 0.1257F, -0.2323F, 2.1524F));

        PartDefinition stage_3_head_1_ear_left_base_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_ear_left_base_r1", CubeListBuilder.create().texOffs(258, 51).addBox(-1.248F, -3.5799F, -0.832F, 2.496F, 3.7799F, 1.664F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.944F, -4.096F, 0.96F, -0.3384F, 0.4214F, 1.7935F));

        PartDefinition stage_3_head_1_brow_left_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_brow_left_r1", CubeListBuilder.create().texOffs(202, 149).addBox(1.216F, -7.04F, -7.04F, 3.2F, 1.28F, 1.536F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition stage_3_head_1_eye_left_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_eye_left_r1", CubeListBuilder.create().texOffs(39, 21).addBox(1.984F, -4.736F, -7.168F, 1.792F, 0.64F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(191, 149).addBox(1.664F, -5.76F, -6.72F, 2.432F, 2.432F, 1.344F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_3_head_1_nasal_bridge_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_nasal_bridge_r1", CubeListBuilder.create().texOffs(174, 149).addBox(-1.984F, -2.432F, -3.264F, 3.968F, 8.832F, 3.072F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.304F, -4.352F, -0.2269F, 0.0F, 0.0F));

        PartDefinition stage_3_head_1_long_face_r1 = stage_3_head_1.addOrReplaceChild("stage_3_head_1_long_face_r1", CubeListBuilder.create().texOffs(153, 149).addBox(-2.88F, -1.408F, -2.88F, 5.76F, 12.672F, 3.904F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, -2.88F, -0.2618F, 0.0F, 0.0F));

        PartDefinition stage_3_jaw_1 = stage_3_head_1.addOrReplaceChild("stage_3_jaw_1", CubeListBuilder.create().texOffs(233, 149).addBox(-2.752F, 2.688F, -10.56F, 5.504F, 2.944F, 10.88F, new CubeDeformation(0.0F))
        .texOffs(268, 149).addBox(-2.624F, 1.536F, -11.136F, 5.248F, 1.536F, 4.8F, new CubeDeformation(0.0F))
        .texOffs(291, 149).addBox(1.344F, -4.992F, -0.96F, 1.536F, 9.088F, 2.752F, new CubeDeformation(0.0F))
        .texOffs(291, 149).addBox(-2.88F, -4.992F, -0.96F, 1.536F, 9.088F, 2.752F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.48F, 0.64F));

        PartDefinition stage_3_head_1_beard_tip_r1 = stage_3_jaw_1.addOrReplaceChild("stage_3_head_1_beard_tip_r1", CubeListBuilder.create().texOffs(313, 149).addBox(-0.288F, -3.8368F, -0.32F, 0.576F, 4.0368F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.576F, -7.872F, -1.7986F, -1.3337F, -1.118F));

        PartDefinition stage_3_head_1_beard_root_r1 = stage_3_jaw_1.addOrReplaceChild("stage_3_head_1_beard_root_r1", CubeListBuilder.create().texOffs(302, 149).addBox(-1.248F, -5.2121F, -0.896F, 2.496F, 5.4121F, 1.792F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.84F, -8.32F, -3.0521F, 0.0F, 0.0F));

        PartDefinition stage_3_branch_2 = stage_3.addOrReplaceChild("stage_3_branch_2", CubeListBuilder.create(), PartPose.offset(0.0F, -75.52F, 0.0F));

        PartDefinition stage_3_neck_2 = stage_3_branch_2.addOrReplaceChild("stage_3_neck_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_3_neck_2_section_0_r1 = stage_3_neck_2.addOrReplaceChild("stage_3_neck_2_section_0_r1", CubeListBuilder.create().texOffs(318, 149).addBox(-3.36F, -7.9814F, -3.2F, 6.72F, 8.1814F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3608F, -0.0872F, -0.4695F));

        PartDefinition stage_3_neck_2_joint_1 = stage_3_neck_2.addOrReplaceChild("stage_3_neck_2_joint_1", CubeListBuilder.create(), PartPose.offset(-3.507F, -6.3844F, 2.7367F));

        PartDefinition stage_3_neck_2_section_1_r1 = stage_3_neck_2_joint_1.addOrReplaceChild("stage_3_neck_2_section_1_r1", CubeListBuilder.create().texOffs(347, 149).addBox(-3.136F, -7.9815F, -3.04F, 6.272F, 8.1815F, 6.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3617F, -0.0875F, -0.4698F));

        PartDefinition stage_3_neck_2_joint_2 = stage_3_neck_2_joint_1.addOrReplaceChild("stage_3_neck_2_joint_2", CubeListBuilder.create(), PartPose.offset(-3.5093F, -6.3807F, 2.7429F));

        PartDefinition stage_3_neck_2_section_2_r1 = stage_3_neck_2_joint_2.addOrReplaceChild("stage_3_neck_2_section_2_r1", CubeListBuilder.create().texOffs(376, 149).addBox(-2.912F, -7.9763F, -2.88F, 5.824F, 8.1763F, 5.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2954F, -0.067F, -0.4428F));

        PartDefinition stage_3_neck_2_joint_3 = stage_3_neck_2_joint_2.addOrReplaceChild("stage_3_neck_2_joint_3", CubeListBuilder.create(), PartPose.offset(-3.3247F, -6.657F, 2.2586F));

        PartDefinition stage_3_neck_2_section_3_r1 = stage_3_neck_2_joint_3.addOrReplaceChild("stage_3_neck_2_section_3_r1", CubeListBuilder.create().texOffs(376, 149).addBox(-2.688F, -7.9796F, -2.72F, 5.376F, 8.1796F, 5.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.221F, -0.0431F, -0.3837F));

        PartDefinition stage_3_neck_2_joint_4 = stage_3_neck_2_joint_3.addOrReplaceChild("stage_3_neck_2_joint_4", CubeListBuilder.create(), PartPose.offset(-2.9093F, -7.011F, 1.704F));

        PartDefinition stage_3_neck_2_section_4_r1 = stage_3_neck_2_joint_4.addOrReplaceChild("stage_3_neck_2_section_4_r1", CubeListBuilder.create().texOffs(401, 149).addBox(-2.464F, -7.9081F, -2.56F, 4.928F, 8.1081F, 5.12F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0034F, -0.0006F, -0.3391F));

        PartDefinition stage_3_neck_2_joint_5 = stage_3_neck_2_joint_4.addOrReplaceChild("stage_3_neck_2_joint_5", CubeListBuilder.create(), PartPose.offset(-2.5638F, -7.2692F, 0.0265F));

        PartDefinition stage_3_head_2_hood_1_edge_r1 = stage_3_neck_2_joint_5.addOrReplaceChild("stage_3_head_2_hood_1_edge_r1", CubeListBuilder.create().texOffs(413, 80).addBox(-0.864F, -6.7267F, -0.704F, 1.728F, 6.9267F, 1.408F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.1219F, 0.6783F, -0.8927F, -0.518F, 1.1635F, 2.3757F));

        PartDefinition stage_3_head_2_hood_1_root_r1 = stage_3_neck_2_joint_5.addOrReplaceChild("stage_3_head_2_hood_1_root_r1", CubeListBuilder.create().texOffs(164, 168).addBox(-1.504F, -7.006F, -1.088F, 3.008F, 7.206F, 2.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2579F, -3.6737F, -1.7887F, -0.1382F, -0.2996F, -2.2816F));

        PartDefinition stage_3_head_2_hood_left_edge_r1 = stage_3_neck_2_joint_5.addOrReplaceChild("stage_3_head_2_hood_left_edge_r1", CubeListBuilder.create().texOffs(413, 80).addBox(-0.864F, -6.7267F, -0.704F, 1.728F, 6.9267F, 1.408F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.1901F, 0.6783F, -0.8927F, -0.518F, -1.1635F, -2.3757F));

        PartDefinition stage_3_head_2_hood_left_root_r1 = stage_3_neck_2_joint_5.addOrReplaceChild("stage_3_head_2_hood_left_root_r1", CubeListBuilder.create().texOffs(164, 168).addBox(-1.504F, -7.006F, -1.088F, 3.008F, 7.206F, 2.176F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3261F, -3.6737F, -1.7887F, -0.1382F, 0.2996F, 2.2816F));

        PartDefinition stage_3_neck_2_section_5_r1 = stage_3_neck_2_joint_5.addOrReplaceChild("stage_3_neck_2_section_5_r1", CubeListBuilder.create().texOffs(424, 149).addBox(-2.24F, -7.9251F, -2.4F, 4.48F, 8.1251F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6505F, 0.0647F, -0.1913F));

        PartDefinition stage_3_head_2 = stage_3_neck_2_joint_5.addOrReplaceChild("stage_3_head_2", CubeListBuilder.create().texOffs(445, 149).addBox(-4.416F, -5.504F, -1.408F, 8.832F, 7.552F, 5.888F, new CubeDeformation(0.0F))
        .texOffs(49, 168).addBox(-3.456F, -4.992F, -11.2F, 6.912F, 2.944F, 6.592F, new CubeDeformation(0.0F))
        .texOffs(78, 168).addBox(-4.352F, 0.896F, -11.456F, 8.704F, 1.152F, 10.304F, new CubeDeformation(0.0F)), PartPose.offset(-1.4659F, -5.9777F, -4.6687F));

        PartDefinition stage_3_head_2_crown_tip_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_crown_tip_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.128F, -2.9611F, -0.128F, 0.256F, 3.1611F, 0.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.832F, -11.392F, 2.944F, -1.1083F, -0.1781F, -0.2865F));

        PartDefinition stage_3_head_2_crown_crest_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_crown_crest_r1", CubeListBuilder.create().texOffs(295, 168).addBox(-0.96F, -8.3328F, -0.8F, 1.92F, 8.5328F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.096F, 1.6F, -0.19F, -0.0105F, -0.1104F));

        PartDefinition stage_3_head_2_fang_1_tip_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_fang_1_tip_r1", CubeListBuilder.create().texOffs(188, 168).addBox(-0.16F, -4.1019F, -0.16F, 0.32F, 4.3019F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2F, 4.864F, -8.896F, -2.705F, 0.8508F, 0.2003F));

        PartDefinition stage_3_head_2_fang_1_root_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_fang_1_root_r1", CubeListBuilder.create().texOffs(179, 168).addBox(-0.544F, -4.1932F, -0.544F, 1.088F, 4.3932F, 1.088F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.328F, 1.28F, -7.872F, 2.858F, -0.2296F, 0.0329F));

        PartDefinition stage_3_head_2_brow_1_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_brow_1_r1", CubeListBuilder.create().texOffs(149, 168).addBox(-5.44F, -6.528F, -7.36F, 3.456F, 1.408F, 2.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition stage_3_head_2_eye_1_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_eye_1_r1", CubeListBuilder.create().texOffs(142, 168).addBox(-4.672F, -4.992F, -7.488F, 1.536F, 1.152F, 0.896F, new CubeDeformation(0.0F))
        .texOffs(191, 149).addBox(-5.12F, -5.632F, -7.04F, 2.432F, 2.304F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_3_head_2_cheek_1_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_cheek_1_r1", CubeListBuilder.create().texOffs(119, 168).addBox(-1.728F, -5.376F, -4.48F, 3.456F, 6.784F, 6.912F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.968F, 0.0F, 0.0F, 0.0F, -0.2443F, -0.0873F));

        PartDefinition stage_3_head_2_fang_left_tip_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_fang_left_tip_r1", CubeListBuilder.create().texOffs(188, 168).addBox(-0.16F, -4.1019F, -0.16F, 0.32F, 4.3019F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2F, 4.864F, -8.896F, -2.705F, -0.8508F, -0.2003F));

        PartDefinition stage_3_head_2_fang_left_root_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_fang_left_root_r1", CubeListBuilder.create().texOffs(179, 168).addBox(-0.544F, -4.1932F, -0.544F, 1.088F, 4.3932F, 1.088F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.328F, 1.28F, -7.872F, 2.858F, 0.2296F, -0.0329F));

        PartDefinition stage_3_head_2_brow_left_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_brow_left_r1", CubeListBuilder.create().texOffs(149, 168).addBox(1.984F, -6.528F, -7.36F, 3.456F, 1.408F, 2.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        PartDefinition stage_3_head_2_eye_left_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_eye_left_r1", CubeListBuilder.create().texOffs(142, 168).addBox(3.136F, -4.992F, -7.488F, 1.536F, 1.152F, 0.896F, new CubeDeformation(0.0F))
        .texOffs(191, 149).addBox(2.688F, -5.632F, -7.04F, 2.432F, 2.304F, 1.152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_3_head_2_cheek_left_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_cheek_left_r1", CubeListBuilder.create().texOffs(119, 168).addBox(-1.728F, -5.376F, -4.48F, 3.456F, 6.784F, 6.912F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.968F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0873F));

        PartDefinition stage_3_head_2_upper_jaw_r1 = stage_3_head_2.addOrReplaceChild("stage_3_head_2_upper_jaw_r1", CubeListBuilder.create().texOffs(0, 168).addBox(-5.312F, -3.456F, -12.8F, 10.624F, 4.608F, 12.672F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.96F, -0.0698F, 0.0F, 0.0F));

        PartDefinition stage_3_jaw_2 = stage_3_head_2.addOrReplaceChild("stage_3_jaw_2", CubeListBuilder.create().texOffs(193, 168).addBox(-4.288F, 0.768F, -12.928F, 8.576F, 3.2F, 13.824F, new CubeDeformation(0.0F))
        .texOffs(240, 168).addBox(-3.776F, -0.128F, -12.096F, 7.552F, 1.152F, 10.176F, new CubeDeformation(0.0F))
        .texOffs(279, 168).addBox(2.368F, -2.944F, -1.152F, 1.408F, 5.504F, 2.048F, new CubeDeformation(0.0F))
        .texOffs(279, 168).addBox(-3.776F, -2.944F, -1.152F, 1.408F, 5.504F, 2.048F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.048F, 1.472F));

        PartDefinition stage_3_head_2_fork_tongue_1_r1 = stage_3_jaw_2.addOrReplaceChild("stage_3_head_2_fork_tongue_1_r1", CubeListBuilder.create().texOffs(408, 80).addBox(-0.096F, -3.8614F, -0.096F, 0.192F, 4.0614F, 0.192F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.792F, -14.848F, 1.5372F, 0.5414F, -0.559F));

        PartDefinition stage_3_head_2_fork_tongue_left_r1 = stage_3_jaw_2.addOrReplaceChild("stage_3_head_2_fork_tongue_left_r1", CubeListBuilder.create().texOffs(408, 80).addBox(-0.096F, -3.8614F, -0.096F, 0.192F, 4.0614F, 0.192F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.792F, -14.848F, 1.5372F, -0.5414F, 0.559F));

        PartDefinition stage_3_head_2_tongue_root_r1 = stage_3_jaw_2.addOrReplaceChild("stage_3_head_2_tongue_root_r1", CubeListBuilder.create().texOffs(290, 168).addBox(-0.32F, -6.1996F, -0.256F, 0.64F, 6.3996F, 0.512F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.768F, -9.152F, 1.764F, 0.0F, 0.0F));

        PartDefinition stage_4 = partdefinition.addOrReplaceChild("stage_4", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_4_thorn_4_tip_r1 = stage_4.addOrReplaceChild("stage_4_thorn_4_tip_r1", CubeListBuilder.create().texOffs(464, 267).addBox(-0.125F, -13.987F, -0.125F, 0.25F, 14.187F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(22.4F, -91.2F, -9.6F, 0.059F, -0.1804F, 2.5107F));

        PartDefinition stage_4_thorn_4_a_r1 = stage_4.addOrReplaceChild("stage_4_thorn_4_a_r1", CubeListBuilder.create().texOffs(449, 267).addBox(-1.55F, -28.5069F, -1.45F, 3.1F, 28.7069F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -76.8F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_4_thorn_3_tip_r1 = stage_4.addOrReplaceChild("stage_4_thorn_3_tip_r1", CubeListBuilder.create().texOffs(464, 267).addBox(-0.125F, -13.987F, -0.125F, 0.25F, 14.187F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-22.4F, -80.0F, 9.6F, 0.059F, 0.1804F, -2.5107F));

        PartDefinition stage_4_thorn_3_a_r1 = stage_4.addOrReplaceChild("stage_4_thorn_3_a_r1", CubeListBuilder.create().texOffs(449, 267).addBox(-1.55F, -28.5069F, -1.45F, 3.1F, 28.7069F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -65.6F, 0.0F, -0.3519F, -0.1788F, -0.9342F));

        PartDefinition stage_4_thorn_2_tip_r1 = stage_4.addOrReplaceChild("stage_4_thorn_2_tip_r1", CubeListBuilder.create().texOffs(464, 267).addBox(-0.125F, -13.987F, -0.125F, 0.25F, 14.187F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(22.4F, -68.8F, -9.6F, 0.059F, -0.1804F, 2.5107F));

        PartDefinition stage_4_thorn_2_a_r1 = stage_4.addOrReplaceChild("stage_4_thorn_2_a_r1", CubeListBuilder.create().texOffs(449, 267).addBox(-1.55F, -28.5069F, -1.45F, 3.1F, 28.7069F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -54.4F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_4_thorn_1_tip_r1 = stage_4.addOrReplaceChild("stage_4_thorn_1_tip_r1", CubeListBuilder.create().texOffs(464, 267).addBox(-0.125F, -13.987F, -0.125F, 0.25F, 14.187F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-22.4F, -57.6F, 9.6F, 0.059F, 0.1804F, -2.5107F));

        PartDefinition stage_4_thorn_1_a_r1 = stage_4.addOrReplaceChild("stage_4_thorn_1_a_r1", CubeListBuilder.create().texOffs(449, 267).addBox(-1.55F, -28.5069F, -1.45F, 3.1F, 28.7069F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -43.2F, 0.0F, -0.3519F, -0.1788F, -0.9342F));

        PartDefinition stage_4_thorn_0_tip_r1 = stage_4.addOrReplaceChild("stage_4_thorn_0_tip_r1", CubeListBuilder.create().texOffs(464, 267).addBox(-0.125F, -13.987F, -0.125F, 0.25F, 14.187F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(22.4F, -46.4F, -9.6F, 0.059F, -0.1804F, 2.5107F));

        PartDefinition stage_4_thorn_0_a_r1 = stage_4.addOrReplaceChild("stage_4_thorn_0_a_r1", CubeListBuilder.create().texOffs(449, 267).addBox(-1.55F, -28.5069F, -1.45F, 3.1F, 28.7069F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -32.0F, 0.0F, 0.3519F, -0.1788F, 0.9342F));

        PartDefinition stage_4_root_7_b_r1 = stage_4.addOrReplaceChild("stage_4_root_7_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0027F, -10.4F, -9.2825F, 0.7236F, 0.7576F, -1.6212F));

        PartDefinition stage_4_root_7_a_r1 = stage_4.addOrReplaceChild("stage_4_root_7_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, 0.6984F, 0.5535F, -1.3249F));

        PartDefinition stage_4_root_6_b_r1 = stage_4.addOrReplaceChild("stage_4_root_6_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3376F, -10.4F, -16.4651F, 2.0407F, 0.2948F, -0.1817F));

        PartDefinition stage_4_root_6_a_r1 = stage_4.addOrReplaceChild("stage_4_root_6_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, 1.6698F, 0.2241F, -0.2031F));

        PartDefinition stage_4_root_5_b_r1 = stage_4.addOrReplaceChild("stage_4_root_5_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.2825F, -10.4F, -14.0027F, 1.6212F, -0.7576F, 0.7236F));

        PartDefinition stage_4_root_5_a_r1 = stage_4.addOrReplaceChild("stage_4_root_5_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, 1.3249F, -0.5535F, 0.6984F));

        PartDefinition stage_4_root_4_b_r1 = stage_4.addOrReplaceChild("stage_4_root_4_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.4651F, -10.4F, -3.3376F, 0.1817F, -0.2948F, 2.0407F));

        PartDefinition stage_4_root_4_a_r1 = stage_4.addOrReplaceChild("stage_4_root_4_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, 0.2031F, -0.2241F, 1.6698F));

        PartDefinition stage_4_root_3_b_r1 = stage_4.addOrReplaceChild("stage_4_root_3_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0027F, -10.4F, 9.2825F, -0.7236F, 0.7576F, 1.6212F));

        PartDefinition stage_4_root_3_a_r1 = stage_4.addOrReplaceChild("stage_4_root_3_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, -0.6984F, 0.5535F, 1.3249F));

        PartDefinition stage_4_root_2_b_r1 = stage_4.addOrReplaceChild("stage_4_root_2_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3376F, -10.4F, 16.4651F, -2.0407F, 0.2948F, 0.1817F));

        PartDefinition stage_4_root_2_a_r1 = stage_4.addOrReplaceChild("stage_4_root_2_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, -1.6698F, 0.2241F, 0.2031F));

        PartDefinition stage_4_root_1_b_r1 = stage_4.addOrReplaceChild("stage_4_root_1_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.2825F, -10.4F, 14.0027F, -1.6212F, -0.7576F, -0.7236F));

        PartDefinition stage_4_root_1_a_r1 = stage_4.addOrReplaceChild("stage_4_root_1_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, -1.3249F, -0.5535F, -0.6984F));

        PartDefinition stage_4_root_0_b_r1 = stage_4.addOrReplaceChild("stage_4_root_0_b_r1", CubeListBuilder.create().texOffs(470, 168).addBox(-0.9F, -20.4179F, -0.9F, 1.8F, 20.6179F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-16.4651F, -10.4F, 3.3376F, -0.1817F, -0.2948F, -2.0407F));

        PartDefinition stage_4_root_0_a_r1 = stage_4.addOrReplaceChild("stage_4_root_0_a_r1", CubeListBuilder.create().texOffs(445, 168).addBox(-2.6F, -17.1706F, -2.6F, 5.2F, 17.3706F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.8F, 0.0F, -0.2031F, -0.2241F, -1.6698F));

        PartDefinition stage_4_trunk_2_r1 = stage_4.addOrReplaceChild("stage_4_trunk_2_r1", CubeListBuilder.create().texOffs(412, 168).addBox(-3.96F, -25.9992F, -3.808F, 7.92F, 26.1992F, 7.616F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2F, -68.8F, 0.0F, 0.0F, 0.0F, -0.1244F));

        PartDefinition stage_4_trunk_1_r1 = stage_4.addOrReplaceChild("stage_4_trunk_1_r1", CubeListBuilder.create().texOffs(371, 168).addBox(-4.98F, -34.6859F, -4.704F, 9.96F, 34.8859F, 9.408F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -35.2F, 1.6F, 0.0464F, -0.0052F, 0.2222F));

        PartDefinition stage_4_trunk_0_r1 = stage_4.addOrReplaceChild("stage_4_trunk_0_r1", CubeListBuilder.create().texOffs(322, 168).addBox(-6.0F, -35.4126F, -5.6F, 12.0F, 35.6126F, 11.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 0.0F, -0.0455F, -0.0029F, -0.1253F));

        PartDefinition stage_4_branch_0 = stage_4.addOrReplaceChild("stage_4_branch_0", CubeListBuilder.create(), PartPose.offset(0.0F, -94.4F, 0.0F));

        PartDefinition stage_4_neck_0 = stage_4_branch_0.addOrReplaceChild("stage_4_neck_0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_0_section_0_r1 = stage_4_neck_0.addOrReplaceChild("stage_4_neck_0_section_0_r1", CubeListBuilder.create().texOffs(0, 217).addBox(-4.2F, -4.399F, -4.0F, 8.4F, 4.599F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2949F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_0_joint_1 = stage_4_neck_0.addOrReplaceChild("stage_4_neck_0_joint_1", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0178F, 1.2202F));

        PartDefinition stage_4_neck_0_section_1_r1 = stage_4_neck_0_joint_1.addOrReplaceChild("stage_4_neck_0_section_1_r1", CubeListBuilder.create().texOffs(35, 217).addBox(-4.2F, -4.305F, -3.8F, 8.4F, 4.505F, 7.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1701F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_0_joint_2 = stage_4_neck_0_joint_1.addOrReplaceChild("stage_4_neck_0_joint_2", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0458F, -0.695F));

        PartDefinition stage_4_neck_0_section_2_r1 = stage_4_neck_0_joint_2.addOrReplaceChild("stage_4_neck_0_section_2_r1", CubeListBuilder.create().texOffs(35, 217).addBox(-4.2F, -4.396F, -3.6F, 8.4F, 4.596F, 7.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6189F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_0_joint_3 = stage_4_neck_0_joint_2.addOrReplaceChild("stage_4_neck_0_joint_3", CubeListBuilder.create(), PartPose.offset(0.0F, -3.4177F, -2.4344F));

        PartDefinition stage_4_neck_0_section_3_r1 = stage_4_neck_0_joint_3.addOrReplaceChild("stage_4_neck_0_section_3_r1", CubeListBuilder.create().texOffs(70, 217).addBox(-4.2F, -4.4028F, -3.4F, 8.4F, 4.6028F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7351F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_0_joint_4 = stage_4_neck_0_joint_3.addOrReplaceChild("stage_4_neck_0_joint_4", CubeListBuilder.create(), PartPose.offset(0.0F, -3.1175F, -2.8187F));

        PartDefinition stage_4_neck_0_section_4_r1 = stage_4_neck_0_joint_4.addOrReplaceChild("stage_4_neck_0_section_4_r1", CubeListBuilder.create().texOffs(70, 217).addBox(-4.2F, -4.4033F, -3.2F, 8.4F, 4.6033F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.759F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_0_joint_5 = stage_4_neck_0_joint_4.addOrReplaceChild("stage_4_neck_0_joint_5", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0495F, -2.8928F));

        PartDefinition stage_4_neck_0_section_5_r1 = stage_4_neck_0_joint_5.addOrReplaceChild("stage_4_neck_0_section_5_r1", CubeListBuilder.create().texOffs(103, 217).addBox(-4.2F, -4.4023F, -3.0F, 8.4F, 4.6023F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7227F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0 = stage_4_neck_0_joint_5.addOrReplaceChild("stage_4_head_0", CubeListBuilder.create().texOffs(134, 217).addBox(-7.6F, -10.24F, -6.0F, 15.2F, 12.32F, 12.4F, new CubeDeformation(0.0F))
        .texOffs(308, 217).addBox(2.0F, -6.4F, -7.84F, 4.64F, 2.88F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(270, 119).addBox(2.88F, -5.44F, -8.32F, 2.88F, 1.28F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(308, 217).addBox(-6.64F, -6.4F, -7.84F, 4.64F, 2.88F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(270, 119).addBox(-5.76F, -5.44F, -8.32F, 2.88F, 1.28F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(213, 51).addBox(-2.16F, -5.12F, -9.84F, 4.32F, 4.8F, 4.88F, new CubeDeformation(0.0F))
        .texOffs(370, 217).addBox(-2.72F, -1.76F, -13.52F, 5.44F, 2.72F, 1.84F, new CubeDeformation(0.0F))
        .texOffs(387, 217).addBox(-6.0F, 4.0F, -11.44F, 12.0F, 2.24F, 6.64F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.1518F, -2.7794F));

        PartDefinition stage_4_head_0_crown_horn_1_2_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_crown_horn_1_2_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.2F, -3.6511F, -0.2F, 0.4F, 3.8511F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.4F, -18.88F, 8.0F, -1.7161F, 0.4186F, 0.3631F));

        PartDefinition stage_4_head_0_crown_horn_1_1_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_crown_horn_1_1_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.72F, -4.7459F, -0.64F, 1.44F, 4.9459F, 1.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.36F, -15.52F, 5.12F, -0.689F, -0.0834F, -0.2316F));

        PartDefinition stage_4_head_0_crown_horn_1_0_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_crown_horn_1_0_r1", CubeListBuilder.create().texOffs(93, 244).addBox(-1.04F, -7.7085F, -0.96F, 2.08F, 7.9085F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2F, -8.8F, 2.56F, -0.3484F, -0.0518F, -0.2922F));

        PartDefinition stage_4_head_0_crown_horn_left_2_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_crown_horn_left_2_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.2F, -3.6511F, -0.2F, 0.4F, 3.8511F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.4F, -18.88F, 8.0F, -1.7161F, -0.4186F, -0.3631F));

        PartDefinition stage_4_head_0_crown_horn_left_1_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_crown_horn_left_1_r1", CubeListBuilder.create().texOffs(416, 51).addBox(-0.72F, -4.7459F, -0.64F, 1.44F, 4.9459F, 1.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.36F, -15.52F, 5.12F, -0.689F, 0.0834F, 0.2316F));

        PartDefinition stage_4_head_0_crown_horn_left_0_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_crown_horn_left_0_r1", CubeListBuilder.create().texOffs(93, 244).addBox(-1.04F, -7.7085F, -0.96F, 2.08F, 7.9085F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2F, -8.8F, 2.56F, -0.3484F, 0.0518F, 0.2922F));

        PartDefinition stage_4_head_0_mane_15_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_15_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0266F, -0.48F, 0.72F, 6.2266F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.1605F, 2.5359F, 3.6F, -2.6704F, 0.434F, 0.1058F));

        PartDefinition stage_4_head_0_mane_15_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_15_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -3.373F, -0.96F, 1.84F, 3.573F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.9779F, 0.535F, 1.44F, -1.9286F, -0.7572F, -0.5384F));

        PartDefinition stage_4_head_0_mane_15_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_15_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.0741F, -1.8F, 3.92F, 5.2741F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.765F, -1.7819F, 2.24F, 0.1713F, 0.2738F, -2.0268F));

        PartDefinition stage_4_head_0_mane_14_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_14_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0156F, -0.48F, 0.72F, 6.2156F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5418F, 6.5847F, 3.6F, -2.6893F, 0.3354F, 0.0779F));

        PartDefinition stage_4_head_0_mane_14_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_14_tip_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.92F, -3.9146F, -0.96F, 1.84F, 4.1146F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.6368F, 3.7014F, 1.44F, -2.2917F, -0.6856F, -0.3202F));

        PartDefinition stage_4_head_0_mane_14_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_14_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.3035F, -1.8F, 3.92F, 5.5035F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4123F, -0.1728F, 2.24F, 0.1728F, 0.4237F, -2.3758F));

        PartDefinition stage_4_head_0_mane_13_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_13_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0045F, -0.48F, 0.72F, 6.2045F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6228F, 9.29F, 3.6F, -2.7076F, 0.1832F, 0.0405F));

        PartDefinition stage_4_head_0_mane_13_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_13_tip_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.92F, -4.3191F, -0.96F, 1.84F, 4.5191F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.133F, 5.8171F, 1.44F, -2.5333F, -0.4088F, -0.13F));

        PartDefinition stage_4_head_0_mane_13_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_13_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.4762F, -1.8F, 3.92F, 5.6762F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3879F, 0.9024F, 2.24F, 0.2243F, 0.8208F, -2.635F));

        PartDefinition stage_4_head_0_mane_12_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_12_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -5.9999F, -0.48F, 0.72F, 6.1999F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.24F, 3.6F, -2.715F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0_mane_12_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_12_tip_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.92F, -4.4671F, -0.96F, 1.84F, 4.6671F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.56F, 1.44F, -2.6108F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0_mane_12_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_12_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.5403F, -1.8F, 3.92F, 5.7403F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.28F, 2.24F, 2.9912F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0_mane_11_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_11_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0045F, -0.48F, 0.72F, 6.2045F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.6228F, 9.29F, 3.6F, -2.7076F, -0.1832F, -0.0405F));

        PartDefinition stage_4_head_0_mane_11_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_11_tip_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.92F, -4.3191F, -0.96F, 1.84F, 4.5191F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.133F, 5.8171F, 1.44F, -2.5333F, 0.4088F, 0.13F));

        PartDefinition stage_4_head_0_mane_11_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_11_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.4762F, -1.8F, 3.92F, 5.6762F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3879F, 0.9024F, 2.24F, 0.2243F, -0.8208F, 2.635F));

        PartDefinition stage_4_head_0_mane_10_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_10_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0156F, -0.48F, 0.72F, 6.2156F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.5418F, 6.5847F, 3.6F, -2.6893F, -0.3354F, -0.0779F));

        PartDefinition stage_4_head_0_mane_10_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_10_tip_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.92F, -3.9146F, -0.96F, 1.84F, 4.1146F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.6368F, 3.7014F, 1.44F, -2.2917F, 0.6856F, 0.3202F));

        PartDefinition stage_4_head_0_mane_10_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_10_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.3035F, -1.8F, 3.92F, 5.5035F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4123F, -0.1728F, 2.24F, 0.1728F, -0.4237F, 2.3758F));

        PartDefinition stage_4_head_0_mane_9_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_9_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0266F, -0.48F, 0.72F, 6.2266F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.1605F, 2.5359F, 3.6F, -2.6704F, -0.434F, -0.1058F));

        PartDefinition stage_4_head_0_mane_9_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_9_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -3.373F, -0.96F, 1.84F, 3.573F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.9779F, 0.535F, 1.44F, -1.9286F, 0.7572F, 0.5384F));

        PartDefinition stage_4_head_0_mane_9_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_9_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -5.0741F, -1.8F, 3.92F, 5.2741F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.765F, -1.7819F, 2.24F, 0.1713F, -0.2738F, 2.0268F));

        PartDefinition stage_4_head_0_mane_8_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_8_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0312F, -0.48F, 0.72F, 6.2312F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.08F, -2.24F, 3.6F, -2.6623F, -0.4679F, -0.1163F));

        PartDefinition stage_4_head_0_mane_8_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_8_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -2.888F, -0.96F, 1.84F, 3.088F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.8F, -3.2F, 1.44F, -1.5763F, 0.6375F, 0.6343F));

        PartDefinition stage_4_head_0_mane_8_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_8_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -4.8545F, -1.8F, 3.92F, 5.0545F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.24F, -3.68F, 2.24F, 0.1759F, -0.1889F, 1.6423F));

        PartDefinition stage_4_head_0_mane_7_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_7_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.92F, -2.6639F, -0.96F, 1.84F, 2.8639F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.9779F, -6.935F, 1.44F, -1.2868F, 0.4196F, 0.5534F));

        PartDefinition stage_4_head_0_mane_7_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_7_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.6977F, -1.8F, 3.92F, 4.8977F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.765F, -5.5781F, 2.24F, 0.1803F, -0.1283F, 1.2359F));

        PartDefinition stage_4_head_0_mane_6_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_6_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.92F, -2.7324F, -0.96F, 1.84F, 2.9324F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.6368F, -10.1014F, 1.44F, -1.0645F, 0.2227F, 0.3752F));

        PartDefinition stage_4_head_0_mane_6_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_6_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.6191F, -1.8F, 3.92F, 4.8191F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4123F, -7.1872F, 2.24F, 0.1826F, -0.0797F, 0.8212F));

        PartDefinition stage_4_head_0_mane_5_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_5_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -2.905F, -0.96F, 1.84F, 3.105F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.133F, -12.2171F, 1.44F, -0.9305F, 0.092F, 0.1829F));

        PartDefinition stage_4_head_0_mane_5_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_5_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.5959F, -1.8F, 3.92F, 4.7959F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3879F, -8.2624F, 2.24F, 0.1831F, -0.038F, 0.4085F));

        PartDefinition stage_4_head_0_mane_4_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_4_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -2.9863F, -0.96F, 1.84F, 3.1863F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.96F, 1.44F, -0.8871F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0_mane_4_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_4_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.5934F, -1.8F, 3.92F, 4.7934F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.64F, 2.24F, 0.1831F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0_mane_3_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_3_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -2.905F, -0.96F, 1.84F, 3.105F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.133F, -12.2171F, 1.44F, -0.9305F, -0.092F, -0.1829F));

        PartDefinition stage_4_head_0_mane_3_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_3_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.5959F, -1.8F, 3.92F, 4.7959F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3879F, -8.2624F, 2.24F, 0.1831F, 0.038F, -0.4085F));

        PartDefinition stage_4_head_0_mane_2_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_2_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.92F, -2.7324F, -0.96F, 1.84F, 2.9324F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.6368F, -10.1014F, 1.44F, -1.0645F, -0.2227F, -0.3752F));

        PartDefinition stage_4_head_0_mane_2_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_2_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.6191F, -1.8F, 3.92F, 4.8191F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4123F, -7.1872F, 2.24F, 0.1826F, 0.0797F, -0.8212F));

        PartDefinition stage_4_head_0_mane_1_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_1_tip_r1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.92F, -2.6639F, -0.96F, 1.84F, 2.8639F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.9779F, -6.935F, 1.44F, -1.2868F, -0.4196F, -0.5534F));

        PartDefinition stage_4_head_0_mane_1_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_1_root_r1", CubeListBuilder.create().texOffs(76, 244).addBox(-1.96F, -4.6977F, -1.8F, 3.92F, 4.8977F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.765F, -5.5781F, 2.24F, 0.1803F, 0.1283F, -1.2359F));

        PartDefinition stage_4_head_0_mane_0_tassel_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_0_tassel_r1", CubeListBuilder.create().texOffs(71, 244).addBox(-0.36F, -6.0312F, -0.48F, 0.72F, 6.2312F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.08F, -2.24F, 3.6F, -2.6623F, 0.4679F, 0.1163F));

        PartDefinition stage_4_head_0_mane_0_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_0_tip_r1", CubeListBuilder.create().texOffs(0, 149).addBox(-0.92F, -2.888F, -0.96F, 1.84F, 3.088F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.8F, -3.2F, 1.44F, -1.5763F, -0.6375F, -0.6343F));

        PartDefinition stage_4_head_0_mane_0_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_mane_0_root_r1", CubeListBuilder.create().texOffs(54, 244).addBox(-1.96F, -4.8545F, -1.8F, 3.92F, 5.0545F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.24F, -3.68F, 2.24F, 0.1759F, 0.1889F, -1.6423F));

        PartDefinition stage_4_head_0_incisor_4_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_incisor_4_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.36F, -2.6993F, -0.4F, 0.72F, 2.8993F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(1.08F, -2.6993F, -0.4F, 0.72F, 2.8993F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(2.52F, -2.6993F, -0.4F, 0.72F, 2.8993F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(3.96F, -2.6993F, -0.4F, 0.72F, 2.8993F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(5.4F, -2.6993F, -0.4F, 0.72F, 2.8993F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.88F, 4.48F, -10.72F, 2.4469F, 0.0F, 0.0F));

        PartDefinition stage_4_head_0_canine_1_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_canine_1_tip_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.16F, -3.5371F, -0.2F, 0.32F, 3.7371F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.56F, 7.52F, -10.4F, 2.2398F, -1.1287F, 0.5947F));

        PartDefinition stage_4_head_0_canine_1_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_canine_1_root_r1", CubeListBuilder.create().texOffs(179, 168).addBox(-0.64F, -4.1816F, -0.72F, 1.28F, 4.3816F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.72F, 4.0F, -9.44F, 2.8647F, -0.2997F, 0.0421F));

        PartDefinition stage_4_head_0_canine_left_tip_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_canine_left_tip_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.16F, -3.5371F, -0.2F, 0.32F, 3.7371F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.56F, 7.52F, -10.4F, 2.2398F, 1.1287F, -0.5947F));

        PartDefinition stage_4_head_0_canine_left_root_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_canine_left_root_r1", CubeListBuilder.create().texOffs(179, 168).addBox(-0.64F, -4.1816F, -0.72F, 1.28F, 4.3816F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.72F, 4.0F, -9.44F, 2.8647F, 0.2997F, -0.0421F));

        PartDefinition stage_4_head_0_ear_inner_1_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_ear_inner_1_r1", CubeListBuilder.create().texOffs(361, 217).addBox(-1.2F, -4.64F, -0.88F, 2.4F, 2.88F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(344, 217).addBox(-2.0F, -5.76F, -0.48F, 4.0F, 5.92F, 3.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.52F, -6.88F, 1.6F, 0.0F, 0.0F, 0.384F));

        PartDefinition stage_4_head_0_brow_1_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_brow_1_r1", CubeListBuilder.create().texOffs(323, 217).addBox(-3.04F, -0.64F, -2.24F, 6.08F, 2.08F, 2.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.32F, -7.2F, -6.4F, 0.0F, 0.0F, -0.2094F));

        PartDefinition stage_4_head_0_muzzle_lobe_1_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_muzzle_lobe_1_r1", CubeListBuilder.create().texOffs(277, 217).addBox(-3.36F, -2.72F, -6.8F, 6.72F, 6.72F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.04F, 1.6F, -5.6F, 0.0F, 0.1396F, 0.0F));

        PartDefinition stage_4_head_0_cheek_1_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_cheek_1_r1", CubeListBuilder.create().texOffs(240, 217).addBox(-2.8F, -4.32F, -6.88F, 5.6F, 9.6F, 11.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, -0.1745F, -0.1571F));

        PartDefinition stage_4_head_0_ear_inner_left_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_ear_inner_left_r1", CubeListBuilder.create().texOffs(361, 217).addBox(-1.2F, -4.64F, -0.88F, 2.4F, 2.88F, 0.64F, new CubeDeformation(0.0F))
        .texOffs(344, 217).addBox(-2.0F, -5.76F, -0.48F, 4.0F, 5.92F, 3.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.52F, -6.88F, 1.6F, 0.0F, 0.0F, -0.384F));

        PartDefinition stage_4_head_0_brow_left_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_brow_left_r1", CubeListBuilder.create().texOffs(323, 217).addBox(-3.04F, -0.64F, -2.24F, 6.08F, 2.08F, 2.48F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.32F, -7.2F, -6.4F, 0.0F, 0.0F, 0.2094F));

        PartDefinition stage_4_head_0_muzzle_lobe_left_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_muzzle_lobe_left_r1", CubeListBuilder.create().texOffs(277, 217).addBox(-3.36F, -2.72F, -6.8F, 6.72F, 6.72F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.04F, 1.6F, -5.6F, 0.0F, -0.1396F, 0.0F));

        PartDefinition stage_4_head_0_cheek_left_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_cheek_left_r1", CubeListBuilder.create().texOffs(240, 217).addBox(-2.8F, -4.32F, -6.88F, 5.6F, 9.6F, 11.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.1571F));

        PartDefinition stage_4_head_0_forehead_r1 = stage_4_head_0.addOrReplaceChild("stage_4_head_0_forehead_r1", CubeListBuilder.create().texOffs(193, 217).addBox(-6.08F, -6.56F, -7.36F, 12.16F, 7.04F, 9.52F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.6F, 0.0F, -0.1222F, 0.0F, 0.0F));

        PartDefinition stage_4_jaw_0 = stage_4_head_0.addOrReplaceChild("stage_4_jaw_0", CubeListBuilder.create().texOffs(426, 217).addBox(-5.52F, 3.36F, -13.6F, 11.04F, 4.32F, 13.6F, new CubeDeformation(0.0F))
        .texOffs(0, 244).addBox(-5.12F, 2.72F, -12.96F, 10.24F, 1.12F, 7.36F, new CubeDeformation(0.0F))
        .texOffs(39, 244).addBox(2.96F, -4.48F, -1.6F, 2.08F, 9.92F, 3.36F, new CubeDeformation(0.0F))
        .texOffs(39, 244).addBox(-5.04F, -4.48F, -1.6F, 2.08F, 9.92F, 3.36F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.88F, 2.0F));

        PartDefinition stage_4_head_0_lower_canine_1_r1 = stage_4_jaw_0.addOrReplaceChild("stage_4_head_0_lower_canine_1_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.48F, -3.1437F, -0.48F, 0.96F, 3.3437F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.68F, 4.0F, -11.84F, 0.1914F, -0.0078F, 0.0816F));

        PartDefinition stage_4_head_0_lower_canine_left_r1 = stage_4_jaw_0.addOrReplaceChild("stage_4_head_0_lower_canine_left_r1", CubeListBuilder.create().texOffs(337, 80).addBox(-0.48F, -3.1437F, -0.48F, 0.96F, 3.3437F, 0.96F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.68F, 4.0F, -11.84F, 0.1914F, 0.0078F, -0.0816F));

        PartDefinition stage_4_branch_1 = stage_4.addOrReplaceChild("stage_4_branch_1", CubeListBuilder.create(), PartPose.offset(0.0F, -94.4F, 0.0F));

        PartDefinition stage_4_neck_1 = stage_4_branch_1.addOrReplaceChild("stage_4_neck_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_1_section_0_r1 = stage_4_neck_1.addOrReplaceChild("stage_4_neck_1_section_0_r1", CubeListBuilder.create().texOffs(104, 244).addBox(-4.2F, -5.2755F, -4.0F, 8.4F, 5.4755F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1328F, 0.0889F, 1.1798F));

        PartDefinition stage_4_neck_1_joint_1 = stage_4_neck_1.addOrReplaceChild("stage_4_neck_1_joint_1", CubeListBuilder.create(), PartPose.offset(4.6738F, -1.8623F, 0.6692F));

        PartDefinition stage_4_neck_1_section_1_r1 = stage_4_neck_1_joint_1.addOrReplaceChild("stage_4_neck_1_section_1_r1", CubeListBuilder.create().texOffs(139, 244).addBox(-3.96F, -5.2766F, -3.8F, 7.92F, 5.4766F, 7.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.144F, 0.0971F, 1.1853F));

        PartDefinition stage_4_neck_1_joint_2 = stage_4_neck_1_joint_1.addOrReplaceChild("stage_4_neck_1_joint_2", CubeListBuilder.create(), PartPose.offset(4.6819F, -1.8236F, 0.725F));

        PartDefinition stage_4_neck_1_section_2_r1 = stage_4_neck_1_joint_2.addOrReplaceChild("stage_4_neck_1_section_2_r1", CubeListBuilder.create().texOffs(139, 244).addBox(-3.72F, -5.2553F, -3.6F, 7.44F, 5.4553F, 7.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0114F, -0.0069F, 1.0925F));

        PartDefinition stage_4_neck_1_joint_3 = stage_4_neck_1_joint_2.addOrReplaceChild("stage_4_neck_1_joint_3", CubeListBuilder.create(), PartPose.offset(4.4879F, -2.3263F, -0.0576F));

        PartDefinition stage_4_neck_1_section_3_r1 = stage_4_neck_1_joint_3.addOrReplaceChild("stage_4_neck_1_section_3_r1", CubeListBuilder.create().texOffs(172, 244).addBox(-3.48F, -5.2371F, -3.4F, 6.96F, 5.4371F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3698F, -0.1527F, 0.7765F));

        PartDefinition stage_4_neck_1_joint_4 = stage_4_neck_1_joint_3.addOrReplaceChild("stage_4_neck_1_joint_4", CubeListBuilder.create(), PartPose.offset(3.4889F, -3.1564F, -1.7992F));

        PartDefinition stage_4_neck_1_section_4_r1 = stage_4_neck_1_joint_4.addOrReplaceChild("stage_4_neck_1_section_4_r1", CubeListBuilder.create().texOffs(172, 244).addBox(-3.24F, -5.2745F, -3.2F, 6.48F, 5.4745F, 6.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5836F, -0.1638F, 0.5335F));

        PartDefinition stage_4_neck_1_joint_5 = stage_4_neck_1_joint_4.addOrReplaceChild("stage_4_neck_1_joint_5", CubeListBuilder.create(), PartPose.offset(2.5459F, -3.4143F, -2.7588F));

        PartDefinition stage_4_neck_1_section_5_r1 = stage_4_neck_1_joint_5.addOrReplaceChild("stage_4_neck_1_section_5_r1", CubeListBuilder.create().texOffs(230, 119).addBox(-3.0F, -5.2751F, -3.0F, 6.0F, 5.4751F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5882F, -0.1633F, 0.5277F));

        PartDefinition stage_4_head_1 = stage_4_neck_1_joint_5.addOrReplaceChild("stage_4_head_1", CubeListBuilder.create().texOffs(201, 244).addBox(-4.96F, -11.52F, -3.84F, 9.92F, 12.48F, 8.96F, new CubeDeformation(0.0F))
        .texOffs(286, 244).addBox(-3.92F, 4.16F, -13.12F, 7.84F, 5.6F, 6.32F, new CubeDeformation(0.0F))
        .texOffs(370, 217).addBox(-2.8F, 4.32F, -13.6F, 5.6F, 2.88F, 1.92F, new CubeDeformation(0.0F)), PartPose.offset(2.5216F, -3.4172F, -2.7787F));

        PartDefinition stage_4_head_1_tooth_1_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_tooth_1_r1", CubeListBuilder.create().texOffs(399, 21).addBox(-0.36F, -2.6645F, -0.4F, 0.72F, 2.8645F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(399, 21).addBox(3.48F, -2.6645F, -0.4F, 0.72F, 2.8645F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.92F, 7.2F, -11.2F, 2.9124F, 0.0F, 0.0F));

        PartDefinition stage_4_head_1_ram_horn_1_3_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_1_3_r1", CubeListBuilder.create().texOffs(352, 244).addBox(-0.32F, -5.7356F, -0.32F, 0.64F, 5.9356F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.4F, -17.12F, 12.64F, 0.0604F, -0.2935F, 2.7382F));

        PartDefinition stage_4_head_1_ram_horn_1_2_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_1_2_r1", CubeListBuilder.create().texOffs(39, 80).addBox(-0.88F, -5.2298F, -0.8F, 1.76F, 5.4298F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.4F, -20.96F, 10.08F, -1.7555F, -1.0266F, -0.8757F));

        PartDefinition stage_4_head_1_ram_horn_1_1_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_1_1_r1", CubeListBuilder.create().texOffs(215, 149).addBox(-1.16F, -7.3415F, -1.08F, 2.32F, 7.5415F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.24F, -18.08F, 5.04F, -0.8303F, -0.2973F, -0.6551F));

        PartDefinition stage_4_head_1_ram_horn_1_0_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_1_0_r1", CubeListBuilder.create().texOffs(339, 244).addBox(-1.44F, -8.9668F, -1.36F, 2.88F, 9.1668F, 2.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.76F, -10.24F, 2.0F, -0.3546F, -0.0518F, -0.2872F));

        PartDefinition stage_4_head_1_ear_1_tip_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ear_1_tip_r1", CubeListBuilder.create().texOffs(193, 21).addBox(-0.72F, -4.1355F, -0.6F, 1.44F, 4.3355F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.04F, -3.84F, 2.4F, 0.1257F, 0.2323F, -2.1524F));

        PartDefinition stage_4_head_1_ear_1_base_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ear_1_base_r1", CubeListBuilder.create().texOffs(294, 119).addBox(-1.56F, -4.4249F, -1.04F, 3.12F, 4.6249F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.68F, -5.12F, 1.2F, -0.3384F, -0.4214F, -1.7935F));

        PartDefinition stage_4_head_1_brow_1_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_brow_1_r1", CubeListBuilder.create().texOffs(202, 149).addBox(-5.52F, -8.8F, -8.8F, 4.0F, 1.6F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition stage_4_head_1_eye_1_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_eye_1_r1", CubeListBuilder.create().texOffs(330, 244).addBox(-4.72F, -5.92F, -8.96F, 2.24F, 0.8F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(317, 244).addBox(-5.12F, -7.2F, -8.4F, 3.04F, 3.04F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_4_head_1_ram_horn_left_3_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_left_3_r1", CubeListBuilder.create().texOffs(352, 244).addBox(-0.32F, -5.7356F, -0.32F, 0.64F, 5.9356F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.4F, -17.12F, 12.64F, 0.0604F, 0.2935F, -2.7382F));

        PartDefinition stage_4_head_1_ram_horn_left_2_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_left_2_r1", CubeListBuilder.create().texOffs(39, 80).addBox(-0.88F, -5.2298F, -0.8F, 1.76F, 5.4298F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.4F, -20.96F, 10.08F, -1.7555F, 1.0266F, 0.8757F));

        PartDefinition stage_4_head_1_ram_horn_left_1_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_left_1_r1", CubeListBuilder.create().texOffs(215, 149).addBox(-1.16F, -7.3415F, -1.08F, 2.32F, 7.5415F, 2.16F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.24F, -18.08F, 5.04F, -0.8303F, 0.2973F, 0.6551F));

        PartDefinition stage_4_head_1_ram_horn_left_0_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ram_horn_left_0_r1", CubeListBuilder.create().texOffs(339, 244).addBox(-1.44F, -8.9668F, -1.36F, 2.88F, 9.1668F, 2.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.76F, -10.24F, 2.0F, -0.3546F, 0.0518F, 0.2872F));

        PartDefinition stage_4_head_1_ear_left_tip_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ear_left_tip_r1", CubeListBuilder.create().texOffs(193, 21).addBox(-0.72F, -4.1355F, -0.6F, 1.44F, 4.3355F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.04F, -3.84F, 2.4F, 0.1257F, -0.2323F, 2.1524F));

        PartDefinition stage_4_head_1_ear_left_base_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_ear_left_base_r1", CubeListBuilder.create().texOffs(294, 119).addBox(-1.56F, -4.4249F, -1.04F, 3.12F, 4.6249F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.68F, -5.12F, 1.2F, -0.3384F, 0.4214F, 1.7935F));

        PartDefinition stage_4_head_1_brow_left_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_brow_left_r1", CubeListBuilder.create().texOffs(202, 149).addBox(1.52F, -8.8F, -8.8F, 4.0F, 1.6F, 1.92F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition stage_4_head_1_eye_left_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_eye_left_r1", CubeListBuilder.create().texOffs(330, 244).addBox(2.48F, -5.92F, -8.96F, 2.24F, 0.8F, 0.8F, new CubeDeformation(0.0F))
        .texOffs(317, 244).addBox(2.08F, -7.2F, -8.4F, 3.04F, 3.04F, 1.68F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_4_head_1_nasal_bridge_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_nasal_bridge_r1", CubeListBuilder.create().texOffs(267, 244).addBox(-2.48F, -3.04F, -4.08F, 4.96F, 11.04F, 3.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.88F, -5.44F, -0.2269F, 0.0F, 0.0F));

        PartDefinition stage_4_head_1_long_face_r1 = stage_4_head_1.addOrReplaceChild("stage_4_head_1_long_face_r1", CubeListBuilder.create().texOffs(240, 244).addBox(-3.6F, -1.76F, -3.6F, 7.2F, 15.84F, 4.88F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.4F, -3.6F, -0.2618F, 0.0F, 0.0F));

        PartDefinition stage_4_jaw_1 = stage_4_head_1.addOrReplaceChild("stage_4_jaw_1", CubeListBuilder.create().texOffs(357, 244).addBox(-3.44F, 3.36F, -13.2F, 6.88F, 3.68F, 13.6F, new CubeDeformation(0.0F))
        .texOffs(400, 244).addBox(-3.28F, 1.92F, -13.92F, 6.56F, 1.92F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(429, 244).addBox(1.68F, -6.24F, -1.2F, 1.92F, 11.36F, 3.44F, new CubeDeformation(0.0F))
        .texOffs(429, 244).addBox(-3.6F, -6.24F, -1.2F, 1.92F, 11.36F, 3.44F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.6F, 0.8F));

        PartDefinition stage_4_head_1_beard_tip_r1 = stage_4_jaw_1.addOrReplaceChild("stage_4_head_1_beard_tip_r1", CubeListBuilder.create().texOffs(313, 149).addBox(-0.36F, -4.7459F, -0.4F, 0.72F, 4.9459F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.72F, -9.84F, -1.7986F, -1.3337F, -1.118F));

        PartDefinition stage_4_head_1_beard_root_r1 = stage_4_jaw_1.addOrReplaceChild("stage_4_head_1_beard_root_r1", CubeListBuilder.create().texOffs(442, 244).addBox(-1.56F, -6.4651F, -1.12F, 3.12F, 6.6651F, 2.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.8F, -10.4F, -3.0521F, 0.0F, 0.0F));

        PartDefinition stage_4_branch_2 = stage_4.addOrReplaceChild("stage_4_branch_2", CubeListBuilder.create(), PartPose.offset(0.0F, -94.4F, 0.0F));

        PartDefinition stage_4_neck_2 = stage_4_branch_2.addOrReplaceChild("stage_4_neck_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition stage_4_neck_2_section_0_r1 = stage_4_neck_2.addOrReplaceChild("stage_4_neck_2_section_0_r1", CubeListBuilder.create().texOffs(457, 244).addBox(-4.2F, -8.5372F, -4.0F, 8.4F, 8.7372F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3592F, -0.0867F, -0.4688F));

        PartDefinition stage_4_neck_2_joint_1 = stage_4_neck_2.addOrReplaceChild("stage_4_neck_2_joint_1", CubeListBuilder.create(), PartPose.offset(-3.753F, -6.8483F, 2.9197F));

        PartDefinition stage_4_neck_2_section_1_r1 = stage_4_neck_2_joint_1.addOrReplaceChild("stage_4_neck_2_section_1_r1", CubeListBuilder.create().texOffs(0, 267).addBox(-3.9667F, -8.5377F, -3.8333F, 7.9333F, 8.7377F, 7.6667F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.367F, -0.0892F, -0.4719F));

        PartDefinition stage_4_neck_2_joint_2 = stage_4_neck_2_joint_1.addOrReplaceChild("stage_4_neck_2_joint_2", CubeListBuilder.create(), PartPose.offset(-3.775F, -6.8107F, 2.98F));

        PartDefinition stage_4_neck_2_section_2_r1 = stage_4_neck_2_joint_2.addOrReplaceChild("stage_4_neck_2_section_2_r1", CubeListBuilder.create().texOffs(0, 267).addBox(-3.7333F, -8.5357F, -3.6667F, 7.4667F, 8.7357F, 7.3333F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3299F, -0.0774F, -0.4572F));

        PartDefinition stage_4_neck_2_joint_3 = stage_4_neck_2_joint_2.addOrReplaceChild("stage_4_neck_2_joint_3", CubeListBuilder.create(), PartPose.offset(-3.6685F, -6.9839F, 2.6926F));

        PartDefinition stage_4_neck_2_section_3_r1 = stage_4_neck_2_joint_3.addOrReplaceChild("stage_4_neck_2_section_3_r1", CubeListBuilder.create().texOffs(347, 149).addBox(-3.5F, -8.5347F, -3.5F, 7.0F, 8.7347F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2495F, -0.0526F, -0.4138F));

        PartDefinition stage_4_neck_2_joint_4 = stage_4_neck_2_joint_3.addOrReplaceChild("stage_4_neck_2_joint_4", CubeListBuilder.create(), PartPose.offset(-3.3468F, -7.3514F, 2.0553F));

        PartDefinition stage_4_neck_2_section_4_r1 = stage_4_neck_2_joint_4.addOrReplaceChild("stage_4_neck_2_section_4_r1", CubeListBuilder.create().texOffs(347, 149).addBox(-3.2667F, -8.5321F, -3.3333F, 6.5333F, 8.7321F, 6.6667F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1882F, -0.035F, -0.3665F));

        PartDefinition stage_4_neck_2_joint_5 = stage_4_neck_2_joint_4.addOrReplaceChild("stage_4_neck_2_joint_5", CubeListBuilder.create(), PartPose.offset(-2.9841F, -7.6218F, 1.5581F));

        PartDefinition stage_4_neck_2_section_5_r1 = stage_4_neck_2_joint_5.addOrReplaceChild("stage_4_neck_2_section_5_r1", CubeListBuilder.create().texOffs(347, 149).addBox(-3.0333F, -8.411F, -3.1667F, 6.0667F, 8.611F, 6.3333F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1125F, 0.0183F, -0.3225F));

        PartDefinition stage_4_neck_2_joint_6 = stage_4_neck_2_joint_5.addOrReplaceChild("stage_4_neck_2_joint_6", CubeListBuilder.create(), PartPose.offset(-2.6018F, -7.7332F, -0.9213F));

        PartDefinition stage_4_head_2_hood_1_edge_r1 = stage_4_neck_2_joint_6.addOrReplaceChild("stage_4_head_2_hood_1_edge_r1", CubeListBuilder.create().texOffs(291, 267).addBox(-1.08F, -8.3584F, -0.88F, 2.16F, 8.5584F, 1.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.7909F, 2.0693F, -0.5644F, -0.518F, 1.1635F, 2.3757F));

        PartDefinition stage_4_head_2_hood_1_root_r1 = stage_4_neck_2_joint_6.addOrReplaceChild("stage_4_head_2_hood_1_root_r1", CubeListBuilder.create().texOffs(276, 267).addBox(-1.88F, -8.7075F, -1.36F, 3.76F, 8.9075F, 2.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.7109F, -3.3707F, -1.6844F, -0.1382F, -0.2996F, -2.2816F));

        PartDefinition stage_4_head_2_hood_left_edge_r1 = stage_4_neck_2_joint_6.addOrReplaceChild("stage_4_head_2_hood_left_edge_r1", CubeListBuilder.create().texOffs(291, 267).addBox(-1.08F, -8.3584F, -0.88F, 2.16F, 8.5584F, 1.76F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.8491F, 2.0693F, -0.5644F, -0.518F, -1.1635F, -2.3757F));

        PartDefinition stage_4_head_2_hood_left_root_r1 = stage_4_neck_2_joint_6.addOrReplaceChild("stage_4_head_2_hood_left_root_r1", CubeListBuilder.create().texOffs(276, 267).addBox(-1.88F, -8.7075F, -1.36F, 3.76F, 8.9075F, 2.72F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7691F, -3.3707F, -1.6844F, -0.1382F, 0.2996F, 2.2816F));

        PartDefinition stage_4_neck_2_section_6_r1 = stage_4_neck_2_joint_6.addOrReplaceChild("stage_4_neck_2_section_6_r1", CubeListBuilder.create().texOffs(376, 149).addBox(-2.8F, -8.5162F, -3.0F, 5.6F, 8.7162F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6903F, 0.0642F, -0.1782F));

        PartDefinition stage_4_head_2 = stage_4_neck_2_joint_6.addOrReplaceChild("stage_4_head_2", CubeListBuilder.create().texOffs(33, 267).addBox(-5.52F, -6.88F, -1.76F, 11.04F, 9.44F, 7.36F, new CubeDeformation(0.0F))
        .texOffs(135, 267).addBox(-4.32F, -6.24F, -14.0F, 8.64F, 3.68F, 8.24F, new CubeDeformation(0.0F))
        .texOffs(172, 267).addBox(-5.44F, 1.12F, -14.32F, 10.88F, 1.44F, 12.88F, new CubeDeformation(0.0F)), PartPose.offset(-1.4709F, -6.2507F, -5.2844F));

        PartDefinition stage_4_head_2_crown_tip_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_crown_tip_r1", CubeListBuilder.create().texOffs(130, 21).addBox(-0.16F, -3.6859F, -0.16F, 0.32F, 3.8859F, 0.32F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.04F, -14.24F, 3.68F, -1.0868F, -0.1718F, -0.2833F));

        PartDefinition stage_4_head_2_crown_crest_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_crown_crest_r1", CubeListBuilder.create().texOffs(438, 267).addBox(-1.2F, -10.366F, -1.0F, 2.4F, 10.566F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.12F, 2.0F, -0.19F, -0.0105F, -0.1104F));

        PartDefinition stage_4_head_2_fang_1_tip_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_fang_1_tip_r1", CubeListBuilder.create().texOffs(311, 267).addBox(-0.2F, -5.0774F, -0.2F, 0.4F, 5.2774F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 6.08F, -11.12F, -2.705F, 0.8508F, 0.2003F));

        PartDefinition stage_4_head_2_fang_1_root_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_fang_1_root_r1", CubeListBuilder.create().texOffs(302, 267).addBox(-0.68F, -5.1915F, -0.68F, 1.36F, 5.3915F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.16F, 1.6F, -9.84F, 2.858F, -0.2296F, 0.0329F));

        PartDefinition stage_4_head_2_brow_1_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_brow_1_r1", CubeListBuilder.create().texOffs(259, 267).addBox(-6.8F, -8.16F, -9.2F, 4.32F, 1.76F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition stage_4_head_2_eye_1_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_eye_1_r1", CubeListBuilder.create().texOffs(250, 267).addBox(-5.84F, -6.24F, -9.36F, 1.92F, 1.44F, 1.12F, new CubeDeformation(0.0F))
        .texOffs(257, 119).addBox(-6.4F, -7.04F, -8.8F, 3.04F, 2.88F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2443F, 0.0F));

        PartDefinition stage_4_head_2_cheek_1_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_cheek_1_r1", CubeListBuilder.create().texOffs(221, 267).addBox(-2.16F, -6.72F, -5.6F, 4.32F, 8.48F, 8.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.96F, 0.0F, 0.0F, 0.0F, -0.2443F, -0.0873F));

        PartDefinition stage_4_head_2_fang_left_tip_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_fang_left_tip_r1", CubeListBuilder.create().texOffs(311, 267).addBox(-0.2F, -5.0774F, -0.2F, 0.4F, 5.2774F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 6.08F, -11.12F, -2.705F, -0.8508F, -0.2003F));

        PartDefinition stage_4_head_2_fang_left_root_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_fang_left_root_r1", CubeListBuilder.create().texOffs(302, 267).addBox(-0.68F, -5.1915F, -0.68F, 1.36F, 5.3915F, 1.36F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.16F, 1.6F, -9.84F, 2.858F, 0.2296F, -0.0329F));

        PartDefinition stage_4_head_2_brow_left_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_brow_left_r1", CubeListBuilder.create().texOffs(259, 267).addBox(2.48F, -8.16F, -9.2F, 4.32F, 1.76F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        PartDefinition stage_4_head_2_eye_left_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_eye_left_r1", CubeListBuilder.create().texOffs(250, 267).addBox(3.92F, -6.24F, -9.36F, 1.92F, 1.44F, 1.12F, new CubeDeformation(0.0F))
        .texOffs(257, 119).addBox(3.36F, -7.04F, -8.8F, 3.04F, 2.88F, 1.44F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0F));

        PartDefinition stage_4_head_2_cheek_left_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_cheek_left_r1", CubeListBuilder.create().texOffs(221, 267).addBox(-2.16F, -6.72F, -5.6F, 4.32F, 8.48F, 8.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.96F, 0.0F, 0.0F, 0.0F, 0.2443F, 0.0873F));

        PartDefinition stage_4_head_2_upper_jaw_r1 = stage_4_head_2.addOrReplaceChild("stage_4_head_2_upper_jaw_r1", CubeListBuilder.create().texOffs(74, 267).addBox(-6.64F, -4.32F, -16.0F, 13.28F, 5.76F, 15.84F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.2F, -0.0698F, 0.0F, 0.0F));

        PartDefinition stage_4_jaw_2 = stage_4_head_2.addOrReplaceChild("stage_4_jaw_2", CubeListBuilder.create().texOffs(316, 267).addBox(-5.36F, 0.96F, -16.16F, 10.72F, 4.0F, 17.28F, new CubeDeformation(0.0F))
        .texOffs(375, 267).addBox(-4.72F, -0.16F, -15.12F, 9.44F, 1.44F, 12.72F, new CubeDeformation(0.0F))
        .texOffs(422, 267).addBox(2.96F, -3.68F, -1.44F, 1.76F, 6.88F, 2.56F, new CubeDeformation(0.0F))
        .texOffs(422, 267).addBox(-4.72F, -3.68F, -1.44F, 1.76F, 6.88F, 2.56F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.56F, 1.84F));

        PartDefinition stage_4_head_2_fork_tongue_1_r1 = stage_4_jaw_2.addOrReplaceChild("stage_4_head_2_fork_tongue_1_r1", CubeListBuilder.create().texOffs(408, 80).addBox(-0.12F, -4.7768F, -0.12F, 0.24F, 4.9768F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.24F, -18.56F, 1.5372F, 0.5414F, -0.559F));

        PartDefinition stage_4_head_2_fork_tongue_left_r1 = stage_4_jaw_2.addOrReplaceChild("stage_4_head_2_fork_tongue_left_r1", CubeListBuilder.create().texOffs(408, 80).addBox(-0.12F, -4.7768F, -0.12F, 0.24F, 4.9768F, 0.24F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.24F, -18.56F, 1.5372F, -0.5414F, 0.559F));

        PartDefinition stage_4_head_2_tongue_root_r1 = stage_4_jaw_2.addOrReplaceChild("stage_4_head_2_tongue_root_r1", CubeListBuilder.create().texOffs(433, 267).addBox(-0.4F, -7.6995F, -0.32F, 0.8F, 7.8995F, 0.64F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.96F, -11.44F, 1.764F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

}
