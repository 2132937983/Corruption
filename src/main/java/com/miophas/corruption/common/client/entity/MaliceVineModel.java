package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.MaliceVine;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Five distinct anchored growth stages, ending in a faceless crown with six hanging tendrils. */
public final class MaliceVineModel extends HierarchicalModel<MaliceVine> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "malice_vine"), "main");
    private final ModelPart root;
    private final ModelPart[] plants = new ModelPart[5], crowns = new ModelPart[5], parts;
    private final ModelPart[][] tendrils = new ModelPart[5][];
    public MaliceVineModel(ModelPart root) {
        this.root = root;
        for (int stage = 0; stage < 5; stage++) {
            String name = stage == 4 ? "plant" : "stage_" + stage;
            plants[stage] = root.getChild(name);
            crowns[stage] = plants[stage].getChild(stage == 4 ? "crown" : name + "_crown");
            tendrils[stage] = new ModelPart[stage + 2];
            for (int i = 0; i < tendrils[stage].length; i++)
                tendrils[stage][i] = crowns[stage].getChild((stage == 4 ? "" : name + "_") + "tendril_" + i);
        }
        parts = root.getAllParts().toArray(ModelPart[]::new);
    }
    @Override public ModelPart root() { return root; }
    @Override public void setupAnim(MaliceVine entity, float swing, float amount, float age, float yaw, float pitch) {
        for (ModelPart part : parts) part.resetPose();
        int stage = entity.stage();
        for (int i = 0; i < plants.length; i++) plants[i].visible = i == stage;
        float t = age + Math.floorMod(entity.getUUID().hashCode(), 41);
        ModelPart plant = plants[stage], crown = crowns[stage];
        // Sway from the anchor so the stalk and crown remain attached in both orientations.
        plant.xRot += Mth.sin(t * .035F) * .018F;
        plant.zRot += Mth.sin(t * .028F + .9F) * .022F;
        crown.xRot += Mth.sin(t * .046F + .5F) * .065F;
        crown.yRot += Mth.sin(t * .032F) * .055F;
        crown.zRot += Mth.sin(t * .039F + 1.1F) * .035F;
        crown.xScale = crown.zScale = 1 + Mth.sin(t * .065F) * .035F;
        for (int i = 0; i < tendrils[stage].length; i++) {
            tendrils[stage][i].xRot += Mth.sin(t * .052F + i * .85F) * .08F;
            tendrils[stage][i].zRot += Mth.sin(t * .047F + i * .85F + .6F) * .10F;
        }
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition plant = partdefinition.addOrReplaceChild("plant", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition root_3_r1 = plant.addOrReplaceChild("root_3_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-1.0F, -8.1557F, -1.0F, 2.0F, 8.4057F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -1.5F, 1.8925F, 0.0F, 0.0F));

        PartDefinition root_2_r1 = plant.addOrReplaceChild("root_2_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-1.0F, -8.1557F, -1.0F, 2.0F, 8.4057F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.0F, 0.0F, 0.0F, 0.0F, 1.8925F));

        PartDefinition root_1_r1 = plant.addOrReplaceChild("root_1_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-1.0F, -8.1557F, -1.0F, 2.0F, 8.4057F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 1.5F, -1.8925F, 0.0F, 0.0F));

        PartDefinition root_0_r1 = plant.addOrReplaceChild("root_0_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-1.0F, -8.1557F, -1.0F, 2.0F, 8.4057F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.0F, 0.0F, 0.0F, 0.0F, -1.8925F));

        PartDefinition anchored_stalk_2_r1 = plant.addOrReplaceChild("anchored_stalk_2_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-2.25F, -18.3124F, -2.25F, 4.5F, 18.5624F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -35.0F, 0.0F, 0.0F, 0.0F, -0.0831F));

        PartDefinition anchored_stalk_1_r1 = plant.addOrReplaceChild("anchored_stalk_1_r1", CubeListBuilder.create().texOffs(25, 0).addBox(-2.5F, -19.5438F, -2.25F, 5.0F, 19.7938F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, -16.0F, 0.6F, 0.0311F, -0.0027F, 0.1719F));

        PartDefinition anchored_stalk_0_r1 = plant.addOrReplaceChild("anchored_stalk_0_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -16.3621F, -3.0F, 6.0F, 16.6121F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0372F, -0.0021F, -0.112F));

        PartDefinition crown = plant.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(76, 0).addBox(-8.0F, -17.0F, -7.0F, 16.0F, 19.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(176, 0).addBox(-5.5F, -3.0F, -5.5F, 11.0F, 8.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(221, 0).addBox(-1.5F, -13.0F, -7.4F, 3.0F, 12.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -53.0F, 0.0F));

        PartDefinition crown_trunk_3_r1 = crown.addOrReplaceChild("crown_trunk_3_r1", CubeListBuilder.create().texOffs(67, 34).addBox(-0.15F, -3.7141F, -0.15F, 0.3F, 3.9641F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -41.0F, -5.0F, 0.6319F, 0.2129F, -0.6319F));

        PartDefinition crown_trunk_2_r1 = crown.addOrReplaceChild("crown_trunk_2_r1", CubeListBuilder.create().texOffs(54, 34).addBox(-1.5F, -5.9946F, -1.25F, 3.0F, 6.2446F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -36.0F, -3.0F, 0.3564F, 0.0649F, -0.3564F));

        PartDefinition crown_trunk_1_r1 = crown.addOrReplaceChild("crown_trunk_1_r1", CubeListBuilder.create().texOffs(31, 34).addBox(-2.75F, -9.5236F, -2.5F, 5.5F, 9.7736F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -27.0F, -1.0F, 0.2174F, 0.0118F, -0.1081F));

        PartDefinition crown_trunk_0_r1 = crown.addOrReplaceChild("crown_trunk_0_r1", CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, -12.2916F, -3.5F, 8.0F, 12.5416F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 0.0831F, 0.0F, 0.0F));

        PartDefinition mouth_tooth_3_r1 = crown.addOrReplaceChild("mouth_tooth_3_r1", CubeListBuilder.create().texOffs(235, 0).addBox(0.5F, -1.0F, -0.3F, 1.1F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.8F, -7.3F, 0.0F, 0.0F, -0.2094F));

        PartDefinition mouth_tooth_2_r1 = crown.addOrReplaceChild("mouth_tooth_2_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-1.65F, -1.0F, -0.3F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.2F, -7.3F, 0.0F, 0.0F, 0.2094F));

        PartDefinition mouth_tooth_1_r1 = crown.addOrReplaceChild("mouth_tooth_1_r1", CubeListBuilder.create().texOffs(235, 0).addBox(0.5F, -1.0F, -0.3F, 1.1F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.6F, -7.3F, 0.0F, 0.0F, -0.2094F));

        PartDefinition mouth_tooth_0_r1 = crown.addOrReplaceChild("mouth_tooth_0_r1", CubeListBuilder.create().texOffs(230, 0).addBox(-1.65F, -1.0F, -0.3F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -7.3F, 0.0F, 0.0F, 0.2094F));

        PartDefinition swollen_rear_r1 = crown.addOrReplaceChild("swollen_rear_r1", CubeListBuilder.create().texOffs(137, 0).addBox(-6.0F, -12.0F, -1.0F, 12.0F, 14.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 4.0F, -0.1396F, 0.0F, 0.0F));

        PartDefinition tendril_0 = crown.addOrReplaceChild("tendril_0", CubeListBuilder.create(), PartPose.offset(-5.9031F, -8.0F, 0.9847F));

        PartDefinition tendril_0_part_2_r1 = tendril_0.addOrReplaceChild("tendril_0_part_2_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.6518F, -0.175F, 0.35F, 11.9018F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.8546F, 18.0F, 1.5218F, 0.0502F, -0.3534F, 2.8621F));

        PartDefinition tendril_0_part_1_r1 = tendril_0.addOrReplaceChild("tendril_0_part_1_r1", CubeListBuilder.create().texOffs(85, 34).addBox(-0.85F, -12.6193F, -0.75F, 1.7F, 12.8693F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.9031F, 6.0F, 0.9847F, -0.0463F, -0.3544F, -2.8843F));

        PartDefinition tendril_0_part_0_r1 = tendril_0.addOrReplaceChild("tendril_0_part_0_r1", CubeListBuilder.create().texOffs(72, 34).addBox(-1.4F, -8.7244F, -1.3F, 2.8F, 8.9744F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1212F, -0.2809F, -2.3306F));

        PartDefinition tendril_1 = crown.addOrReplaceChild("tendril_1", CubeListBuilder.create(), PartPose.offset(-2.0213F, -10.0F, 5.1785F));

        PartDefinition tendril_1_part_2_r1 = tendril_1.addOrReplaceChild("tendril_1_part_2_r1", CubeListBuilder.create().texOffs(112, 34).addBox(-0.175F, -7.8658F, -0.175F, 0.35F, 8.1158F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0319F, 20.0F, 8.0032F, 2.6551F, -0.6545F, 0.1681F));

        PartDefinition tendril_1_part_1_r1 = tendril_1.addOrReplaceChild("tendril_1_part_1_r1", CubeListBuilder.create().texOffs(85, 34).addBox(-0.85F, -12.6193F, -0.75F, 1.7F, 12.8693F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0213F, 8.0F, 5.1785F, -2.8447F, -0.675F, -0.1048F));

        PartDefinition tendril_1_part_0_r1 = tendril_1.addOrReplaceChild("tendril_1_part_0_r1", CubeListBuilder.create().texOffs(99, 34).addBox(-1.4F, -9.9918F, -1.3F, 2.8F, 10.2418F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.4001F, -0.6648F, -0.2668F));

        PartDefinition tendril_2 = crown.addOrReplaceChild("tendril_2", CubeListBuilder.create(), PartPose.offset(3.8818F, -8.0F, 4.1939F));

        PartDefinition tendril_2_part_2_r1 = tendril_2.addOrReplaceChild("tendril_2_part_2_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.6518F, -0.175F, 0.35F, 11.9018F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.8227F, 18.0F, 6.4814F, 2.188F, 1.3222F, -0.7642F));

        PartDefinition tendril_2_part_1_r1 = tendril_2.addOrReplaceChild("tendril_2_part_1_r1", CubeListBuilder.create().texOffs(85, 34).addBox(-0.85F, -12.6193F, -0.75F, 1.7F, 12.8693F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8818F, 6.0F, 4.1939F, -2.2374F, 1.3332F, 0.7299F));

        PartDefinition tendril_2_part_0_r1 = tendril_2.addOrReplaceChild("tendril_2_part_0_r1", CubeListBuilder.create().texOffs(72, 34).addBox(-1.4F, -8.5359F, -1.3F, 2.8F, 8.7859F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.4306F, 1.0343F, 1.1593F));

        PartDefinition tendril_3 = crown.addOrReplaceChild("tendril_3", CubeListBuilder.create(), PartPose.offset(5.9031F, -10.0F, -0.9847F));

        PartDefinition tendril_3_part_2_r1 = tendril_3.addOrReplaceChild("tendril_3_part_2_r1", CubeListBuilder.create().texOffs(112, 34).addBox(-0.175F, -7.8658F, -0.175F, 0.35F, 8.1158F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.8546F, 20.0F, -1.5218F, -0.075F, -0.3448F, -2.7172F));

        PartDefinition tendril_3_part_1_r1 = tendril_3.addOrReplaceChild("tendril_3_part_1_r1", CubeListBuilder.create().texOffs(85, 34).addBox(-0.85F, -12.6193F, -0.75F, 1.7F, 12.8693F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.9031F, 8.0F, -0.9847F, 0.0463F, -0.3544F, 2.8843F));

        PartDefinition tendril_3_part_0_r1 = tendril_3.addOrReplaceChild("tendril_3_part_0_r1", CubeListBuilder.create().texOffs(99, 34).addBox(-1.4F, -10.2408F, -1.3F, 2.8F, 10.4908F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1032F, -0.2966F, 2.4756F));

        PartDefinition tendril_4 = crown.addOrReplaceChild("tendril_4", CubeListBuilder.create(), PartPose.offset(2.0213F, -8.0F, -5.1785F));

        PartDefinition tendril_4_part_2_r1 = tendril_4.addOrReplaceChild("tendril_4_part_2_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.6518F, -0.175F, 0.35F, 11.9018F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0319F, 18.0F, -8.0032F, -2.8193F, -0.6728F, -0.1136F));

        PartDefinition tendril_4_part_1_r1 = tendril_4.addOrReplaceChild("tendril_4_part_1_r1", CubeListBuilder.create().texOffs(85, 34).addBox(-0.85F, -12.6193F, -0.75F, 1.7F, 12.8693F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0213F, 6.0F, -5.1785F, 2.8447F, -0.675F, 0.1048F));

        PartDefinition tendril_4_part_0_r1 = tendril_4.addOrReplaceChild("tendril_4_part_0_r1", CubeListBuilder.create().texOffs(72, 34).addBox(-1.4F, -8.4294F, -1.3F, 2.8F, 8.6794F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.2435F, -0.6276F, 0.3102F));

        PartDefinition tendril_5 = crown.addOrReplaceChild("tendril_5", CubeListBuilder.create(), PartPose.offset(-3.8818F, -10.0F, -4.1939F));

        PartDefinition tendril_5_part_2_r1 = tendril_5.addOrReplaceChild("tendril_5_part_2_r1", CubeListBuilder.create().texOffs(112, 34).addBox(-0.175F, -7.8658F, -0.175F, 0.35F, 8.1158F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.8227F, 20.0F, -6.4814F, -1.9389F, 1.243F, 0.9134F));

        PartDefinition tendril_5_part_1_r1 = tendril_5.addOrReplaceChild("tendril_5_part_1_r1", CubeListBuilder.create().texOffs(85, 34).addBox(-0.85F, -12.6193F, -0.75F, 1.7F, 12.8693F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8818F, 8.0F, -4.1939F, 2.2374F, 1.3332F, -0.7299F));

        PartDefinition tendril_5_part_0_r1 = tendril_5.addOrReplaceChild("tendril_5_part_0_r1", CubeListBuilder.create().texOffs(99, 34).addBox(-1.4F, -10.0814F, -1.3F, 2.8F, 10.3314F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5166F, 1.1294F, -1.179F));

        PartDefinition stage_0 = partdefinition.addOrReplaceChild("stage_0", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_0_root_3_r1 = stage_0.addOrReplaceChild("stage_0_root_3_r1", CubeListBuilder.create().texOffs(17, 55).addBox(-0.8F, -4.522F, -0.8F, 1.6F, 4.772F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 1.9296F, 0.0F, 0.0F));

        PartDefinition stage_0_root_2_r1 = stage_0.addOrReplaceChild("stage_0_root_2_r1", CubeListBuilder.create().texOffs(17, 55).addBox(-0.8F, -4.522F, -0.8F, 1.6F, 4.772F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.9296F));

        PartDefinition stage_0_root_1_r1 = stage_0.addOrReplaceChild("stage_0_root_1_r1", CubeListBuilder.create().texOffs(17, 55).addBox(-0.8F, -4.522F, -0.8F, 1.6F, 4.772F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, -1.9296F, 0.0F, 0.0F));

        PartDefinition stage_0_root_0_r1 = stage_0.addOrReplaceChild("stage_0_root_0_r1", CubeListBuilder.create().texOffs(17, 55).addBox(-0.8F, -4.522F, -0.8F, 1.6F, 4.772F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.9296F));

        PartDefinition stage_0_stem_1_r1 = stage_0.addOrReplaceChild("stage_0_stem_1_r1", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, -5.5688F, -1.75F, 4.0F, 5.8188F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -4.8F, 0.5F, 0.0941F, 0.0089F, -0.1891F));

        PartDefinition stage_0_stem_0_r1 = stage_0.addOrReplaceChild("stage_0_stem_0_r1", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, -5.1785F, -1.75F, 4.0F, 5.4285F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1016F, 0.0104F, 0.2043F));

        PartDefinition stage_0_crown = stage_0.addOrReplaceChild("stage_0_crown", CubeListBuilder.create().texOffs(26, 55).addBox(-4.5F, -7.0F, -3.9F, 9.0F, 9.0F, 7.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition stage_0_crest_1_r1 = stage_0_crown.addOrReplaceChild("stage_0_crest_1_r1", CubeListBuilder.create().texOffs(128, 55).addBox(-1.25F, -4.6375F, -1.0F, 2.5F, 4.8875F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -18.0F, -0.5F, 0.3492F, 0.0408F, -0.2301F));

        PartDefinition stage_0_crest_0_r1 = stage_0_crown.addOrReplaceChild("stage_0_crest_0_r1", CubeListBuilder.create().texOffs(113, 55).addBox(-1.8F, -6.2915F, -1.4F, 3.6F, 6.5415F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0829F, 0.0034F, -0.0829F));

        PartDefinition closed_bud_lobe_right_r1 = stage_0_crown.addOrReplaceChild("closed_bud_lobe_right_r1", CubeListBuilder.create().texOffs(88, 55).addBox(-4.5F, -9.0F, -3.45F, 5.0F, 9.0F, 6.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition closed_bud_lobe_left_r1 = stage_0_crown.addOrReplaceChild("closed_bud_lobe_left_r1", CubeListBuilder.create().texOffs(61, 55).addBox(-0.5F, -9.0F, -3.7F, 5.0F, 9.0F, 7.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0698F));

        PartDefinition stage_0_tendril_0 = stage_0_crown.addOrReplaceChild("stage_0_tendril_0", CubeListBuilder.create(), PartPose.offset(-3.7258F, -4.0F, 0.8866F));

        PartDefinition stage_0_tendril_0_part_1_r1 = stage_0_tendril_0.addOrReplaceChild("stage_0_tendril_0_part_1_r1", CubeListBuilder.create().texOffs(139, 55).addBox(-0.175F, -7.3211F, -0.175F, 0.35F, 7.5711F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4392F, 2.0F, 1.0343F, -0.0505F, -0.5966F, -2.9775F));

        PartDefinition stage_0_tendril_0_part_0_r1 = stage_0_tendril_0.addOrReplaceChild("stage_0_tendril_0_part_0_r1", CubeListBuilder.create().texOffs(17, 55).addBox(-0.9F, -4.3607F, -0.8F, 1.8F, 4.6107F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2795F, -0.4224F, -1.9804F));

        PartDefinition stage_0_tendril_1 = stage_0_crown.addOrReplaceChild("stage_0_tendril_1", CubeListBuilder.create(), PartPose.offset(3.7258F, -6.0F, -0.8866F));

        PartDefinition stage_0_tendril_1_part_1_r1 = stage_0_tendril_1.addOrReplaceChild("stage_0_tendril_1_part_1_r1", CubeListBuilder.create().texOffs(139, 55).addBox(-0.175F, -7.3211F, -0.175F, 0.35F, 7.5711F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4392F, 4.0F, -1.0343F, 0.0505F, -0.5966F, 2.9775F));

        PartDefinition stage_0_tendril_1_part_0_r1 = stage_0_tendril_1.addOrReplaceChild("stage_0_tendril_1_part_0_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-0.9F, -5.6257F, -0.8F, 1.8F, 5.8757F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2213F, -0.5018F, 2.3236F));

        PartDefinition stage_1 = partdefinition.addOrReplaceChild("stage_1", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_1_root_3_r1 = stage_1.addOrReplaceChild("stage_1_root_3_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-0.8F, -5.4702F, -0.8F, 1.6F, 5.7202F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 1.8623F, 0.0F, 0.0F));

        PartDefinition stage_1_root_2_r1 = stage_1.addOrReplaceChild("stage_1_root_2_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-0.8F, -5.4702F, -0.8F, 1.6F, 5.7202F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.8623F));

        PartDefinition stage_1_root_1_r1 = stage_1.addOrReplaceChild("stage_1_root_1_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-0.8F, -5.4702F, -0.8F, 1.6F, 5.7202F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, -1.8623F, 0.0F, 0.0F));

        PartDefinition stage_1_root_0_r1 = stage_1.addOrReplaceChild("stage_1_root_0_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-0.8F, -5.4702F, -0.8F, 1.6F, 5.7202F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.8623F));

        PartDefinition stage_1_stem_1_r1 = stage_1.addOrReplaceChild("stage_1_stem_1_r1", CubeListBuilder.create().texOffs(170, 55).addBox(-1.875F, -11.2471F, -1.75F, 3.75F, 11.4971F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -10.08F, 0.5F, 0.0455F, -0.0025F, 0.1093F));

        PartDefinition stage_1_stem_0_r1 = stage_1.addOrReplaceChild("stage_1_stem_0_r1", CubeListBuilder.create().texOffs(153, 55).addBox(-1.875F, -10.4135F, -1.75F, 3.75F, 10.6635F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0492F, -0.0029F, -0.1183F));

        PartDefinition stage_1_crown = stage_1.addOrReplaceChild("stage_1_crown", CubeListBuilder.create().texOffs(187, 55).addBox(-4.0F, -11.0F, -3.8F, 8.0F, 13.0F, 7.6F, new CubeDeformation(0.0F))
        .texOffs(249, 55).addBox(-0.6F, -10.0F, -4.0F, 1.2F, 6.0F, 0.3F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.0F, 0.0F));

        PartDefinition stage_1_crest_2_r1 = stage_1_crown.addOrReplaceChild("stage_1_crest_2_r1", CubeListBuilder.create().texOffs(30, 78).addBox(-0.125F, -5.149F, -0.125F, 0.25F, 5.399F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -23.0F, -2.0F, 0.4224F, 0.0919F, -0.4224F));

        PartDefinition stage_1_crest_1_r1 = stage_1_crown.addOrReplaceChild("stage_1_crest_1_r1", CubeListBuilder.create().texOffs(19, 78).addBox(-1.25F, -6.515F, -1.0F, 2.5F, 6.765F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, -0.5F, 0.2418F, 0.0195F, -0.1603F));

        PartDefinition stage_1_crest_0_r1 = stage_1_crown.addOrReplaceChild("stage_1_crest_0_r1", CubeListBuilder.create().texOffs(0, 78).addBox(-2.2F, -9.2639F, -1.8F, 4.4F, 9.5139F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0555F, 0.0F, 0.0F));

        PartDefinition opening_bud_right_r1 = stage_1_crown.addOrReplaceChild("opening_bud_right_r1", CubeListBuilder.create().texOffs(220, 55).addBox(-3.5F, -13.0F, -4.5F, 5.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition opening_bud_left_r1 = stage_1_crown.addOrReplaceChild("opening_bud_left_r1", CubeListBuilder.create().texOffs(220, 55).addBox(-1.5F, -13.0F, -4.5F, 5.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        PartDefinition stage_1_tendril_0 = stage_1_crown.addOrReplaceChild("stage_1_tendril_0", CubeListBuilder.create(), PartPose.offset(-4.6811F, -4.0F, 1.1821F));

        PartDefinition stage_1_tendril_0_part_1_r1 = stage_1_tendril_0.addOrReplaceChild("stage_1_tendril_0_part_1_r1", CubeListBuilder.create().texOffs(35, 78).addBox(-0.175F, -9.3054F, -0.175F, 0.35F, 9.5554F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3945F, 2.0F, 1.3298F, -0.0395F, -0.5979F, -3.0136F));

        PartDefinition stage_1_tendril_0_part_0_r1 = stage_1_tendril_0.addOrReplaceChild("stage_1_tendril_0_part_0_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-1.0F, -5.258F, -0.9F, 2.0F, 5.508F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2922F, -0.3984F, -1.8817F));

        PartDefinition stage_1_tendril_1 = stage_1_crown.addOrReplaceChild("stage_1_tendril_1", CubeListBuilder.create(), PartPose.offset(3.5946F, -6.0F, 2.7183F));

        PartDefinition stage_1_tendril_1_part_1_r1 = stage_1_tendril_1.addOrReplaceChild("stage_1_tendril_1_part_1_r1", CubeListBuilder.create().texOffs(35, 78).addBox(-0.175F, -9.3054F, -0.175F, 0.35F, 9.5554F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3745F, 4.0F, 3.0581F, -0.7588F, 1.4615F, 2.3045F));

        PartDefinition stage_1_tendril_1_part_0_r1 = stage_1_tendril_1.addOrReplaceChild("stage_1_tendril_1_part_0_r1", CubeListBuilder.create().texOffs(40, 78).addBox(-1.0F, -6.3113F, -0.9F, 2.0F, 6.5613F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.1084F, 0.9719F, 1.413F));

        PartDefinition stage_1_tendril_2 = stage_1_crown.addOrReplaceChild("stage_1_tendril_2", CubeListBuilder.create(), PartPose.offset(1.0865F, -4.0F, -3.9004F));

        PartDefinition stage_1_tendril_2_part_1_r1 = stage_1_tendril_2.addOrReplaceChild("stage_1_tendril_2_part_1_r1", CubeListBuilder.create().texOffs(35, 78).addBox(-0.175F, -9.3054F, -0.175F, 0.35F, 9.5554F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.02F, 2.0F, -4.388F, 3.022F, -0.4457F, 0.0271F));

        PartDefinition stage_1_tendril_2_part_0_r1 = stage_1_tendril_2.addOrReplaceChild("stage_1_tendril_2_part_0_r1", CubeListBuilder.create().texOffs(144, 55).addBox(-1.0F, -5.179F, -0.9F, 2.0F, 5.429F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.9294F, -0.3152F, 0.2194F));

        PartDefinition stage_2 = partdefinition.addOrReplaceChild("stage_2", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_2_root_3_r1 = stage_2.addOrReplaceChild("stage_2_root_3_r1", CubeListBuilder.create().texOffs(40, 78).addBox(-0.8F, -6.4347F, -0.8F, 1.6F, 6.6847F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 1.8158F, 0.0F, 0.0F));

        PartDefinition stage_2_root_2_r1 = stage_2.addOrReplaceChild("stage_2_root_2_r1", CubeListBuilder.create().texOffs(40, 78).addBox(-0.8F, -6.4347F, -0.8F, 1.6F, 6.6847F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.8158F));

        PartDefinition stage_2_root_1_r1 = stage_2.addOrReplaceChild("stage_2_root_1_r1", CubeListBuilder.create().texOffs(40, 78).addBox(-0.8F, -6.4347F, -0.8F, 1.6F, 6.6847F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, -1.8158F, 0.0F, 0.0F));

        PartDefinition stage_2_root_0_r1 = stage_2.addOrReplaceChild("stage_2_root_0_r1", CubeListBuilder.create().texOffs(40, 78).addBox(-0.8F, -6.4347F, -0.8F, 1.6F, 6.6847F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.8158F));

        PartDefinition stage_2_stem_1_r1 = stage_2.addOrReplaceChild("stage_2_stem_1_r1", CubeListBuilder.create().texOffs(66, 78).addBox(-1.75F, -16.9275F, -1.75F, 3.5F, 17.1775F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -15.36F, 0.5F, 0.03F, 0.0009F, -0.06F));

        PartDefinition stage_2_stem_0_r1 = stage_2.addOrReplaceChild("stage_2_stem_0_r1", CubeListBuilder.create().texOffs(49, 78).addBox(-1.75F, -15.6506F, -1.75F, 3.5F, 15.9006F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0325F, 0.0011F, 0.065F));

        PartDefinition stage_2_crown = stage_2.addOrReplaceChild("stage_2_crown", CubeListBuilder.create().texOffs(83, 78).addBox(-6.5F, -17.0F, -5.5F, 13.0F, 19.0F, 11.0F, new CubeDeformation(0.0F))
        .texOffs(132, 78).addBox(-4.68F, -4.0F, -3.96F, 9.36F, 8.0F, 7.92F, new CubeDeformation(0.0F))
        .texOffs(169, 78).addBox(-1.0F, -14.0F, -5.8F, 2.0F, 12.0F, 0.45F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -32.0F, 0.0F));

        PartDefinition stage_2_crest_2_r1 = stage_2_crown.addOrReplaceChild("stage_2_crest_2_r1", CubeListBuilder.create().texOffs(30, 78).addBox(-0.125F, -5.149F, -0.125F, 0.25F, 5.399F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -28.0F, -2.0F, 0.4224F, 0.0919F, -0.4224F));

        PartDefinition stage_2_crest_1_r1 = stage_2_crown.addOrReplaceChild("stage_2_crest_1_r1", CubeListBuilder.create().texOffs(19, 78).addBox(-1.25F, -6.515F, -1.0F, 2.5F, 6.765F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, -0.5F, 0.2418F, 0.0195F, -0.1603F));

        PartDefinition stage_2_crest_0_r1 = stage_2_crown.addOrReplaceChild("stage_2_crest_0_r1", CubeListBuilder.create().texOffs(176, 78).addBox(-2.6F, -7.2678F, -2.2F, 5.2F, 7.5178F, 4.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 0.0713F, 0.0F, 0.0F));

        PartDefinition stage_2_tendril_0 = stage_2_crown.addOrReplaceChild("stage_2_tendril_0", CubeListBuilder.create(), PartPose.offset(-5.6365F, -4.0F, 1.4776F));

        PartDefinition stage_2_tendril_0_part_1_r1 = stage_2_tendril_0.addOrReplaceChild("stage_2_tendril_0_part_1_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.2954F, -0.175F, 0.35F, 11.5454F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3499F, 2.0F, 1.6254F, -0.0324F, -0.5986F, -3.0367F));

        PartDefinition stage_2_tendril_0_part_0_r1 = stage_2_tendril_0.addOrReplaceChild("stage_2_tendril_0_part_0_r1", CubeListBuilder.create().texOffs(19, 78).addBox(-1.1F, -6.1883F, -1.0F, 2.2F, 6.4383F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2993F, -0.381F, -1.8143F));

        PartDefinition stage_2_tendril_1 = stage_2_crown.addOrReplaceChild("stage_2_tendril_1", CubeListBuilder.create(), PartPose.offset(1.7436F, -6.0F, 4.7767F));

        PartDefinition stage_2_tendril_1_part_1_r1 = stage_2_tendril_1.addOrReplaceChild("stage_2_tendril_1_part_1_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.2954F, -0.175F, 0.35F, 11.5454F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6549F, 4.0F, 5.2544F, -3.0367F, 0.5986F, 0.0324F));

        PartDefinition stage_2_tendril_1_part_0_r1 = stage_2_tendril_1.addOrReplaceChild("stage_2_tendril_1_part_0_r1", CubeListBuilder.create().texOffs(199, 78).addBox(-1.1F, -7.0579F, -1.0F, 2.2F, 7.3079F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.0931F, 0.4723F, 0.2765F));

        PartDefinition stage_2_tendril_2 = stage_2_crown.addOrReplaceChild("stage_2_tendril_2", CubeListBuilder.create(), PartPose.offset(5.6365F, -4.0F, -1.4776F));

        PartDefinition stage_2_tendril_2_part_1_r1 = stage_2_tendril_2.addOrReplaceChild("stage_2_tendril_2_part_1_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.2954F, -0.175F, 0.35F, 11.5454F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.3499F, 2.0F, -1.6254F, 0.0324F, -0.5986F, 3.0367F));

        PartDefinition stage_2_tendril_2_part_0_r1 = stage_2_tendril_2.addOrReplaceChild("stage_2_tendril_2_part_0_r1", CubeListBuilder.create().texOffs(19, 78).addBox(-1.1F, -6.1883F, -1.0F, 2.2F, 6.4383F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2993F, -0.381F, 1.8143F));

        PartDefinition stage_2_tendril_3 = stage_2_crown.addOrReplaceChild("stage_2_tendril_3", CubeListBuilder.create(), PartPose.offset(-1.7436F, -6.0F, -4.7767F));

        PartDefinition stage_2_tendril_3_part_1_r1 = stage_2_tendril_3.addOrReplaceChild("stage_2_tendril_3_part_1_r1", CubeListBuilder.create().texOffs(94, 34).addBox(-0.175F, -11.2954F, -0.175F, 0.35F, 11.5454F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6549F, 4.0F, -5.2544F, 3.0367F, 0.5986F, -0.0324F));

        PartDefinition stage_2_tendril_3_part_0_r1 = stage_2_tendril_3.addOrReplaceChild("stage_2_tendril_3_part_0_r1", CubeListBuilder.create().texOffs(199, 78).addBox(-1.1F, -7.0579F, -1.0F, 2.2F, 7.3079F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.0931F, 0.4723F, -0.2765F));

        PartDefinition stage_3 = partdefinition.addOrReplaceChild("stage_3", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stage_3_root_3_r1 = stage_3.addOrReplaceChild("stage_3_root_3_r1", CubeListBuilder.create().texOffs(244, 78).addBox(-0.8F, -7.4089F, -0.8F, 1.6F, 7.6589F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 1.7819F, 0.0F, 0.0F));

        PartDefinition stage_3_root_2_r1 = stage_3.addOrReplaceChild("stage_3_root_2_r1", CubeListBuilder.create().texOffs(244, 78).addBox(-0.8F, -7.4089F, -0.8F, 1.6F, 7.6589F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.7819F));

        PartDefinition stage_3_root_1_r1 = stage_3.addOrReplaceChild("stage_3_root_1_r1", CubeListBuilder.create().texOffs(244, 78).addBox(-0.8F, -7.4089F, -0.8F, 1.6F, 7.6589F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, -1.7819F, 0.0F, 0.0F));

        PartDefinition stage_3_root_0_r1 = stage_3.addOrReplaceChild("stage_3_root_0_r1", CubeListBuilder.create().texOffs(244, 78).addBox(-0.8F, -7.4089F, -0.8F, 1.6F, 7.6589F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.7819F));

        PartDefinition stage_3_stem_1_r1 = stage_3.addOrReplaceChild("stage_3_stem_1_r1", CubeListBuilder.create().texOffs(227, 78).addBox(-1.625F, -22.1287F, -1.75F, 3.25F, 22.3787F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -20.16F, 0.5F, 0.0229F, -0.0006F, 0.0549F));

        PartDefinition stage_3_stem_0_r1 = stage_3.addOrReplaceChild("stage_3_stem_0_r1", CubeListBuilder.create().texOffs(210, 78).addBox(-1.625F, -20.4519F, -1.75F, 3.25F, 20.7019F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0248F, -0.0007F, -0.0594F));

        PartDefinition stage_3_crown = stage_3.addOrReplaceChild("stage_3_crown", CubeListBuilder.create().texOffs(0, 109).addBox(-7.5F, -19.0F, -6.5F, 15.0F, 21.0F, 13.0F, new CubeDeformation(0.0F))
        .texOffs(57, 109).addBox(-5.4F, -4.0F, -4.68F, 10.8F, 8.0F, 9.36F, new CubeDeformation(0.0F))
        .texOffs(100, 109).addBox(-1.0F, -16.0F, -6.8F, 2.0F, 14.0F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(230, 0).addBox(-1.2F, -4.8F, -6.95F, 1.0F, 0.8F, 0.4F, new CubeDeformation(0.0F))
        .texOffs(230, 0).addBox(0.2F, -7.8F, -6.95F, 1.0F, 0.8F, 0.4F, new CubeDeformation(0.0F))
        .texOffs(230, 0).addBox(-1.2F, -10.8F, -6.95F, 1.0F, 0.8F, 0.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -42.0F, 0.0F));

        PartDefinition stage_3_crest_2_r1 = stage_3_crown.addOrReplaceChild("stage_3_crest_2_r1", CubeListBuilder.create().texOffs(30, 78).addBox(-0.125F, -5.149F, -0.125F, 0.25F, 5.399F, 0.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -34.0F, -2.0F, 0.4224F, 0.0919F, -0.4224F));

        PartDefinition stage_3_crest_1_r1 = stage_3_crown.addOrReplaceChild("stage_3_crest_1_r1", CubeListBuilder.create().texOffs(19, 78).addBox(-1.25F, -6.515F, -1.0F, 2.5F, 6.765F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -28.0F, -0.5F, 0.2418F, 0.0195F, -0.1603F));

        PartDefinition stage_3_crest_0_r1 = stage_3_crown.addOrReplaceChild("stage_3_crest_0_r1", CubeListBuilder.create().texOffs(140, 109).addBox(-3.0F, -11.2614F, -2.6F, 6.0F, 11.5114F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, 0.0F, 0.0454F, 0.0F, 0.0F));

        PartDefinition developing_rear_crown_r1 = stage_3_crown.addOrReplaceChild("developing_rear_crown_r1", CubeListBuilder.create().texOffs(107, 109).addBox(-5.5F, -16.0F, 0.0F, 11.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 3.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition stage_3_tendril_0 = stage_3_crown.addOrReplaceChild("stage_3_tendril_0", CubeListBuilder.create(), PartPose.offset(-6.5918F, -4.0F, 1.7731F));

        PartDefinition stage_3_tendril_0_part_1_r1 = stage_3_tendril_0.addOrReplaceChild("stage_3_tendril_0_part_1_r1", CubeListBuilder.create().texOffs(178, 109).addBox(-0.175F, -13.2884F, -0.175F, 0.35F, 13.5384F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.3052F, 2.0F, 1.9209F, -0.0274F, -0.599F, -3.0528F));

        PartDefinition stage_3_tendril_0_part_0_r1 = stage_3_tendril_0.addOrReplaceChild("stage_3_tendril_0_part_0_r1", CubeListBuilder.create().texOffs(165, 109).addBox(-1.2F, -7.1381F, -1.1F, 2.4F, 7.3881F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3035F, -0.368F, -1.7658F));

        PartDefinition stage_3_tendril_1 = stage_3_crown.addOrReplaceChild("stage_3_tendril_1", CubeListBuilder.create(), PartPose.offset(-0.0977F, -6.0F, 5.9994F));

        PartDefinition stage_3_tendril_1_part_1_r1 = stage_3_tendril_1.addOrReplaceChild("stage_3_tendril_1_part_1_r1", CubeListBuilder.create().texOffs(178, 109).addBox(-0.175F, -13.2884F, -0.175F, 0.35F, 13.5384F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0934F, 4.0F, 6.4993F, -3.0648F, -0.0283F, -0.0011F));

        PartDefinition stage_3_tendril_1_part_0_r1 = stage_3_tendril_1.addOrReplaceChild("stage_3_tendril_1_part_0_r1", CubeListBuilder.create().texOffs(72, 34).addBox(-1.2F, -7.8822F, -1.1F, 2.4F, 8.1322F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.1222F, -0.0219F, -0.0122F));

        PartDefinition stage_3_tendril_2 = stage_3_crown.addOrReplaceChild("stage_3_tendril_2", CubeListBuilder.create(), PartPose.offset(6.5314F, -4.0F, 1.9347F));

        PartDefinition stage_3_tendril_2_part_1_r1 = stage_3_tendril_2.addOrReplaceChild("stage_3_tendril_2_part_1_r1", CubeListBuilder.create().texOffs(178, 109).addBox(-0.175F, -13.2884F, -0.175F, 0.35F, 13.5384F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.2475F, 2.0F, 2.0959F, -0.0312F, 0.6555F, 3.0499F));

        PartDefinition stage_3_tendril_2_part_0_r1 = stage_3_tendril_2.addOrReplaceChild("stage_3_tendril_2_part_0_r1", CubeListBuilder.create().texOffs(165, 109).addBox(-1.2F, -7.1365F, -1.1F, 2.4F, 7.3865F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3367F, 0.3997F, 1.7452F));

        PartDefinition stage_3_tendril_3 = stage_3_crown.addOrReplaceChild("stage_3_tendril_3", CubeListBuilder.create(), PartPose.offset(4.1343F, -6.0F, -4.8037F));

        PartDefinition stage_3_tendril_3_part_1_r1 = stage_3_tendril_3.addOrReplaceChild("stage_3_tendril_3_part_1_r1", CubeListBuilder.create().texOffs(178, 109).addBox(-0.175F, -13.2884F, -0.175F, 0.35F, 13.5384F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9546F, 4.0F, -5.204F, 2.9258F, -1.28F, 0.161F));

        PartDefinition stage_3_tendril_3_part_0_r1 = stage_3_tendril_3.addOrReplaceChild("stage_3_tendril_3_part_0_r1", CubeListBuilder.create().texOffs(72, 34).addBox(-1.2F, -7.9129F, -1.1F, 2.4F, 8.1629F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5191F, -0.823F, 0.8616F));

        PartDefinition stage_3_tendril_4 = stage_3_crown.addOrReplaceChild("stage_3_tendril_4", CubeListBuilder.create(), PartPose.offset(-3.9763F, -4.0F, -4.9035F));

        PartDefinition stage_3_tendril_4_part_1_r1 = stage_3_tendril_4.addOrReplaceChild("stage_3_tendril_4_part_1_r1", CubeListBuilder.create().texOffs(178, 109).addBox(-0.175F, -13.2884F, -0.175F, 0.35F, 13.5384F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8034F, 2.0F, -5.3122F, 2.956F, 1.2242F, -0.1305F));

        PartDefinition stage_3_tendril_4_part_0_r1 = stage_3_tendril_4.addOrReplaceChild("stage_3_tendril_4_part_0_r1", CubeListBuilder.create().texOffs(165, 109).addBox(-1.2F, -7.0826F, -1.1F, 2.4F, 7.3326F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3859F, 0.6584F, -0.7807F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

}
