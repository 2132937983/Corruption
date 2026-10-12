package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Spider;

/** Heavy jointed legs use a four-leg support gait instead of the vanilla straight-leg swing. */
public final class CorruptedSpiderModel extends HierarchicalModel<Spider> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_spider"), "main");
    private static final float UPPER = (float) Math.hypot(9.5, 3.5);
    private static final float LOWER = (float) Math.hypot(5.5, 12.3);
    private static final float UPPER_REST = (float) Math.atan2(-3.5, 9.5);
    private static final float BEND_REST = (float) Math.atan2(12.3, 5.5) - UPPER_REST;
    private final ModelPart root, body, head, abdomen, rightFang, leftFang, sickles;
    private final ModelPart[] legs = new ModelPart[8], knees = new ModelPart[8], blades = new ModelPart[3], parts;
    private final Map<Spider, Motion> motions = new WeakHashMap<>();

    private static final class Motion {
        float age, air, landed = Float.NEGATIVE_INFINITY;
        boolean ground;
        Motion(Spider entity, float age) { this.age = age; ground = entity.onGround(); }
        void update(Spider entity, float now, boolean airborne) {
            float elapsed = Mth.clamp(now - age, 0, 4);
            if (entity.onGround() && !ground && air > 0.1F) landed = now;
            ground = entity.onGround();
            air += Mth.clamp((airborne ? 1 : 0) - air, -elapsed / 3, elapsed / 3);
            age = now;
        }
    }

    public CorruptedSpiderModel(ModelPart root) {
        this.root = root;
        body = root.getChild("body"); head = body.getChild("head"); abdomen = body.getChild("abdomen");
        rightFang = head.getChild("right_mandible"); leftFang = head.getChild("left_mandible");
        sickles = abdomen.getChild("dorsal_sickles");
        for (int i = 0; i < 3; i++) blades[i] = sickles.getChild("dorsal_blade_" + i);
        for (int i = 0; i < 8; i++) {
            String name = (i < 4 ? "right" : "left") + "_leg_" + i % 4;
            legs[i] = body.getChild(name); knees[i] = legs[i].getChild(name + "_knee");
        }
        parts = root.getAllParts().toArray(ModelPart[]::new);
    }

    @Override public ModelPart root() { return root; }

    @Override
    public void setupAnim(Spider entity, float swing, float amount, float age, float yaw, float pitch) {
        for (ModelPart part : parts) part.resetPose();
        Motion motion = motions.computeIfAbsent(entity, key -> new Motion(key, age));
        boolean fluid = RemnantAnimationState.fluid(entity) || !entity.level().getFluidState(entity.blockPosition()).isEmpty();
        boolean seated = RemnantAnimationState.riding(entity, riding);
        boolean airborne = !entity.onGround() && !fluid && !seated
                && (Math.abs(entity.getDeltaMovement().y) > 0.015 || motion.air > 0);
        motion.update(entity, age, airborne);
        float stride = Mth.clamp(amount, 0, 1), attack = Mth.clamp(attackTime, 0, 1);
        float t = age + Math.floorMod(entity.getId(), 37) * 7;
        float idle = (1 - stride) * (1 - motion.air) * (1 - Mth.sin(attack * Mth.PI));
        if (fluid || seated) idle=0;
        float breath = Mth.sin(t * 0.065F), windup = 0, strike = 0;
        if (attack > 0 && attack < 0.25F) windup = smooth(attack / 0.25F);
        else if (attack >= 0.25F && attack < 0.6F) {
            strike = smooth((attack - 0.25F) / 0.35F); windup = 1 - strike;
        } else if (attack >= 0.6F) strike = 1 - smooth((attack - 0.6F) / 0.4F);
        float landing = 1 - Mth.clamp((age - motion.landed) / 6, 0, 1);
        landing *= landing;
        body.y += 0.75F + idle * breath * 0.36F + stride * Math.abs(Mth.sin(swing * 1.1F)) * 0.25F
                + landing * 1.8F + windup * 0.75F;
        body.xRot = -0.035F + idle * Mth.sin(t * 0.043F) * 0.038F - windup * 0.22F + strike * 0.17F;
        body.zRot = idle * Mth.sin(t * 0.031F + 0.7F) * 0.038F;
        body.z -= strike * 2.5F;
        head.yRot = Mth.clamp(yaw * Mth.DEG_TO_RAD, -0.65F, 0.65F);
        head.xRot = Mth.clamp(pitch * Mth.DEG_TO_RAD, -0.4F, 0.4F) + windup * 0.23F - strike * 0.27F;
        abdomen.y -= 0.5F;
        abdomen.xRot += 0.24F + idle * breath * 0.055F;
        rightFang.zRot += idle * breath * 0.075F + (windup + strike) * 0.38F;
        leftFang.zRot -= idle * breath * 0.075F + (windup + strike) * 0.38F;
        rightFang.xRot = leftFang.xRot = -strike * 0.38F;
        placeSickles(entity);
        sickles.xRot += idle * Mth.sin(t * 0.065F - 0.5F) * 0.05F + windup * 0.07F;
        for (int i = 0; i < 3; i++) blades[i].zRot += idle * Mth.sin(t * 0.065F + i * 0.5F) * 0.022F;

        for (int i = 0; i < 8; i++) {
            int row = i % 4, sign = i < 4 ? 1 : -1;
            float phase = swing * 1.1F + ((row + (i < 4 ? 0 : 1)) % 2) * Mth.PI;
            float lift = Math.max(0, Mth.sin(phase)) * stride * 4.4F;
            float reach = 15 + Mth.cos(phase) * stride * 0.95F;
            legs[i].yRot += sign * Mth.cos(phase) * stride * 0.23F;
            if (row == 0) {
                lift += windup * 5.0F;
                legs[i].yRot += sign * (windup * 0.22F - strike * 0.27F);
            }
            float x = legs[i].x - sign * reach * Mth.cos(legs[i].yRot);
            float z = legs[i].z + sign * reach * Mth.sin(legs[i].yRot);
            float sx = Mth.sin(body.xRot), cx = Mth.cos(body.xRot), sz = Mth.sin(body.zRot), cz = Mth.cos(body.zRot);
            // Solve ground height after torso rotation so stance feet stay on the floor while
            // the other four legs lift. Body yaw is zero; roll/pitch still contribute to height.
            float down = (23.7F - lift - body.y - sz * x + cz * sx * z) / (cz * cx) - legs[i].y;
            if (motion.air > 0) {
                reach = Mth.lerp(motion.air, reach, entity.getDeltaMovement().y > 0.02 ? 10.3F : 13.0F);
                down = Mth.lerp(motion.air, down, entity.getDeltaMovement().y > 0.02 ? 2.5F : 5.8F);
            }
            if (fluid) { reach=13+Mth.cos(t*.16F+i)*1.1F; down=5.8F+Mth.sin(t*.16F+i)*1.0F; }
            if (seated) { reach=10; down=4; }
            float distance = Mth.clamp((float) Math.hypot(reach, down), LOWER - UPPER + 0.01F, UPPER + LOWER - 0.01F);
            float bend = (float) Math.acos(Mth.clamp((distance * distance - UPPER * UPPER - LOWER * LOWER)
                    / (2 * UPPER * LOWER), -1, 1));
            float upper = (float) Math.atan2(down, reach)
                    - (float) Math.atan2(LOWER * Math.sin(bend), UPPER + LOWER * Math.cos(bend));
            // Spread the whole articulated leg with Y rotation after its planar Z bend.
            // ModelPart stores ZYX Euler angles, so assigning Y/Z independently would
            // change the bend plane and make diagonal stance feet penetrate the floor.
            float spread = legs[i].yRot, roll = -sign * (upper - UPPER_REST);
            float sy = Mth.sin(spread), cy = Mth.cos(spread), sr = Mth.sin(roll), cr = Mth.cos(roll);
            legs[i].xRot = (float) Math.atan2(sy * sr, cy);
            legs[i].yRot = (float) Math.asin(Mth.clamp(sy * cr, -1, 1));
            legs[i].zRot = (float) Math.atan2(sr, cy * cr);
            knees[i].zRot = -sign * (bend - BEND_REST);
        }
    }

    private void placeSickles(Spider entity) {
        long seed = entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits();
        seed = (seed ^ (seed >>> 30)) * 0xbf58476d1ce4e5b9L;
        seed = (seed ^ (seed >>> 27)) * 0x94d049bb133111ebL;
        seed ^= seed >>> 31;
        int socket = (int) seed & 3;
        sickles.x += (socket - 1.5F) * 0.65F;
        sickles.z += (socket % 2 == 0 ? -0.6F : 0.6F);
        sickles.yRot += (socket - 1.5F) * 0.075F;
    }

    private static float smooth(float value) {
        value = Mth.clamp(value, 0, 1); return value * value * (3 - 2 * value);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(33, 0).addBox(-3.5F, -3.0F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -3.0F));

        PartDefinition right_mandible = head.addOrReplaceChild("right_mandible", CubeListBuilder.create(), PartPose.offset(-2.0F, 2.0F, -7.5F));

        PartDefinition right_fang_tip_r1 = right_mandible.addOrReplaceChild("right_fang_tip_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.4F, -2.1368F, -0.35F, 0.8F, 2.3868F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.7F, -1.5F, 1.9777F, -1.0909F, 0.7598F));

        PartDefinition right_fang_base_r1 = right_mandible.addOrReplaceChild("right_fang_base_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.9F, -2.5716F, -0.8F, 1.8F, 2.8216F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.2841F, 0.5465F, -0.2548F));

        PartDefinition left_mandible = head.addOrReplaceChild("left_mandible", CubeListBuilder.create(), PartPose.offset(2.0F, 2.0F, -7.5F));

        PartDefinition left_fang_tip_r1 = left_mandible.addOrReplaceChild("left_fang_tip_r1", CubeListBuilder.create().texOffs(118, 0).addBox(-0.4F, -2.1368F, -0.35F, 0.8F, 2.3868F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.7F, -1.5F, 1.9777F, 1.0909F, -0.7598F));

        PartDefinition left_fang_base_r1 = left_mandible.addOrReplaceChild("left_fang_base_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.9F, -2.5716F, -0.8F, 1.8F, 2.8216F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.2841F, -0.5465F, 0.2548F));

        PartDefinition abdomen = body.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(62, 0).addBox(-5.5F, -4.0F, -6.0F, 11.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 9.0F));

        PartDefinition dorsal_sickles = abdomen.addOrReplaceChild("dorsal_sickles", CubeListBuilder.create().texOffs(80, 21).addBox(-2.4F, -1.1F, -1.4F, 4.8F, 2.3F, 3.4F, new CubeDeformation(0.0F))
        .texOffs(99, 21).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 1.6F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 1.0F, 0.1047F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_0 = dorsal_sickles.addOrReplaceChild("dorsal_blade_0", CubeListBuilder.create().texOffs(116, 21).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.4F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, -1.5F, -0.5F, 0.0873F, 0.1571F, -0.1047F));

        PartDefinition dorsal_blade_0_segment_3_r1 = dorsal_blade_0.addOrReplaceChild("dorsal_blade_0_segment_3_r1", CubeListBuilder.create().texOffs(0, 37).addBox(-0.084F, -1.5648F, -0.55F, 0.168F, 1.8148F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.36F, 3.192F, -2.0344F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_0_segment_2_r1 = dorsal_blade_0.addOrReplaceChild("dorsal_blade_0_segment_2_r1", CubeListBuilder.create().texOffs(7, 37).addBox(-0.273F, -1.7643F, -0.55F, 0.546F, 2.0143F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.856F, 1.764F, -1.2315F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_0_segment_1_r1 = dorsal_blade_0.addOrReplaceChild("dorsal_blade_0_segment_1_r1", CubeListBuilder.create().texOffs(0, 37).addBox(-0.42F, -1.7361F, -0.55F, 0.84F, 1.9861F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.848F, 0.672F, -0.8254F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_0_segment_0_r1 = dorsal_blade_0.addOrReplaceChild("dorsal_blade_0_segment_0_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.546F, -2.2164F, -0.55F, 1.092F, 2.4664F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3488F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_1 = dorsal_sickles.addOrReplaceChild("dorsal_blade_1", CubeListBuilder.create().texOffs(116, 21).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.4F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, 0.4F, -0.0349F, 0.0F, 0.0175F));

        PartDefinition dorsal_blade_1_segment_3_r1 = dorsal_blade_1.addOrReplaceChild("dorsal_blade_1_segment_3_r1", CubeListBuilder.create().texOffs(7, 37).addBox(-0.1F, -1.8152F, -0.55F, 0.2F, 2.0652F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 3.8F, -2.0344F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_1_segment_2_r1 = dorsal_blade_1.addOrReplaceChild("dorsal_blade_1_segment_2_r1", CubeListBuilder.create().texOffs(7, 37).addBox(-0.325F, -2.0528F, -0.55F, 0.65F, 2.3028F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.4F, 2.1F, -1.2315F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_1_segment_1_r1 = dorsal_blade_1.addOrReplaceChild("dorsal_blade_1_segment_1_r1", CubeListBuilder.create().texOffs(7, 37).addBox(-0.5F, -2.0192F, -0.55F, 1.0F, 2.2692F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2F, 0.8F, -0.8254F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_1_segment_0_r1 = dorsal_blade_1.addOrReplaceChild("dorsal_blade_1_segment_0_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.65F, -2.5909F, -0.55F, 1.3F, 2.8409F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3488F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_2 = dorsal_sickles.addOrReplaceChild("dorsal_blade_2", CubeListBuilder.create().texOffs(116, 21).addBox(-0.7F, -0.5F, -0.7F, 1.4F, 1.4F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, -1.5F, 1.3F, -0.1222F, -0.1571F, 0.1047F));

        PartDefinition dorsal_blade_2_segment_3_r1 = dorsal_blade_2.addOrReplaceChild("dorsal_blade_2_segment_3_r1", CubeListBuilder.create().texOffs(0, 37).addBox(-0.09F, -1.6587F, -0.55F, 0.18F, 1.9087F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, 3.42F, -2.0344F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_2_segment_2_r1 = dorsal_blade_2.addOrReplaceChild("dorsal_blade_2_segment_2_r1", CubeListBuilder.create().texOffs(7, 37).addBox(-0.2925F, -1.8725F, -0.55F, 0.585F, 2.1225F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.06F, 1.89F, -1.2315F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_2_segment_1_r1 = dorsal_blade_2.addOrReplaceChild("dorsal_blade_2_segment_1_r1", CubeListBuilder.create().texOffs(7, 37).addBox(-0.45F, -1.8423F, -0.55F, 0.9F, 2.0923F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.98F, 0.72F, -0.8254F, 0.0F, 0.0F));

        PartDefinition dorsal_blade_2_segment_0_r1 = dorsal_blade_2.addOrReplaceChild("dorsal_blade_2_segment_0_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.585F, -2.3568F, -0.55F, 1.17F, 2.6068F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3488F, 0.0F, 0.0F));

        PartDefinition right_leg_0 = body.addOrReplaceChild("right_leg_0", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 0.0F, -2.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition right_leg_0_upper_r1 = right_leg_0.addOrReplaceChild("right_leg_0_upper_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.15F, -10.3742F, -2.0F, 4.3F, 10.6242F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2178F));

        PartDefinition right_leg_0_knee = right_leg_0.addOrReplaceChild("right_leg_0_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.5F, -3.5F, 0.0F));

        PartDefinition right_leg_0_lower_r1 = right_leg_0_knee.addOrReplaceChild("right_leg_0_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.7211F));

        PartDefinition right_leg_0_knee_spur_r1 = right_leg_0_knee.addOrReplaceChild("right_leg_0_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, -0.8F, 0.0F, 0.0F, 0.0F, -0.664F));

        PartDefinition right_leg_0_foot = right_leg_0_knee.addOrReplaceChild("right_leg_0_foot", CubeListBuilder.create(), PartPose.offset(-4.565F, 10.209F, 0.0F));

        PartDefinition right_leg_0_point_r1 = right_leg_0_foot.addOrReplaceChild("right_leg_0_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.535F, 1.541F, 0.0F, 0.0F, 0.0F, -2.5128F));

        PartDefinition right_leg_0_ankle_r1 = right_leg_0_foot.addOrReplaceChild("right_leg_0_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.8074F));

        PartDefinition right_leg_1 = body.addOrReplaceChild("right_leg_1", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 0.0F, -0.6F, 0.0F, -0.3142F, 0.0F));

        PartDefinition right_leg_1_upper_r1 = right_leg_1.addOrReplaceChild("right_leg_1_upper_r1", CubeListBuilder.create().texOffs(63, 21).addBox(-1.95F, -10.3742F, -1.8F, 3.9F, 10.6242F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2178F));

        PartDefinition right_leg_1_knee = right_leg_1.addOrReplaceChild("right_leg_1_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.5F, -3.5F, 0.0F));

        PartDefinition right_leg_1_lower_r1 = right_leg_1_knee.addOrReplaceChild("right_leg_1_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.7211F));

        PartDefinition right_leg_1_knee_spur_r1 = right_leg_1_knee.addOrReplaceChild("right_leg_1_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, -0.8F, 0.0F, 0.0F, 0.0F, -0.664F));

        PartDefinition right_leg_1_foot = right_leg_1_knee.addOrReplaceChild("right_leg_1_foot", CubeListBuilder.create(), PartPose.offset(-4.565F, 10.209F, 0.0F));

        PartDefinition right_leg_1_point_r1 = right_leg_1_foot.addOrReplaceChild("right_leg_1_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.535F, 1.541F, 0.0F, 0.0F, 0.0F, -2.5128F));

        PartDefinition right_leg_1_ankle_r1 = right_leg_1_foot.addOrReplaceChild("right_leg_1_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.8074F));

        PartDefinition right_leg_2 = body.addOrReplaceChild("right_leg_2", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 0.0F, 0.8F, 0.0F, 0.3142F, 0.0F));

        PartDefinition right_leg_2_upper_r1 = right_leg_2.addOrReplaceChild("right_leg_2_upper_r1", CubeListBuilder.create().texOffs(63, 21).addBox(-1.95F, -10.3742F, -1.8F, 3.9F, 10.6242F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2178F));

        PartDefinition right_leg_2_knee = right_leg_2.addOrReplaceChild("right_leg_2_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.5F, -3.5F, 0.0F));

        PartDefinition right_leg_2_lower_r1 = right_leg_2_knee.addOrReplaceChild("right_leg_2_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.7211F));

        PartDefinition right_leg_2_knee_spur_r1 = right_leg_2_knee.addOrReplaceChild("right_leg_2_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, -0.8F, 0.0F, 0.0F, 0.0F, -0.664F));

        PartDefinition right_leg_2_foot = right_leg_2_knee.addOrReplaceChild("right_leg_2_foot", CubeListBuilder.create(), PartPose.offset(-4.565F, 10.209F, 0.0F));

        PartDefinition right_leg_2_point_r1 = right_leg_2_foot.addOrReplaceChild("right_leg_2_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.535F, 1.541F, 0.0F, 0.0F, 0.0F, -2.5128F));

        PartDefinition right_leg_2_ankle_r1 = right_leg_2_foot.addOrReplaceChild("right_leg_2_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.8074F));

        PartDefinition right_leg_3 = body.addOrReplaceChild("right_leg_3", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 0.0F, 2.2F, 0.0F, 0.7854F, 0.0F));

        PartDefinition right_leg_3_upper_r1 = right_leg_3.addOrReplaceChild("right_leg_3_upper_r1", CubeListBuilder.create().texOffs(63, 21).addBox(-1.95F, -10.3742F, -1.8F, 3.9F, 10.6242F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2178F));

        PartDefinition right_leg_3_knee = right_leg_3.addOrReplaceChild("right_leg_3_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.5F, -3.5F, 0.0F));

        PartDefinition right_leg_3_lower_r1 = right_leg_3_knee.addOrReplaceChild("right_leg_3_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.7211F));

        PartDefinition right_leg_3_knee_spur_r1 = right_leg_3_knee.addOrReplaceChild("right_leg_3_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, -0.8F, 0.0F, 0.0F, 0.0F, -0.664F));

        PartDefinition right_leg_3_foot = right_leg_3_knee.addOrReplaceChild("right_leg_3_foot", CubeListBuilder.create(), PartPose.offset(-4.565F, 10.209F, 0.0F));

        PartDefinition right_leg_3_point_r1 = right_leg_3_foot.addOrReplaceChild("right_leg_3_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.535F, 1.541F, 0.0F, 0.0F, 0.0F, -2.5128F));

        PartDefinition right_leg_3_ankle_r1 = right_leg_3_foot.addOrReplaceChild("right_leg_3_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.8074F));

        PartDefinition left_leg_0 = body.addOrReplaceChild("left_leg_0", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.0F, -2.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition left_leg_0_upper_r1 = left_leg_0.addOrReplaceChild("left_leg_0_upper_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.15F, -10.3742F, -2.0F, 4.3F, 10.6242F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2178F));

        PartDefinition left_leg_0_knee = left_leg_0.addOrReplaceChild("left_leg_0_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.5F, -3.5F, 0.0F));

        PartDefinition left_leg_0_lower_r1 = left_leg_0_knee.addOrReplaceChild("left_leg_0_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.7211F));

        PartDefinition left_leg_0_knee_spur_r1 = left_leg_0_knee.addOrReplaceChild("left_leg_0_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, -0.8F, 0.0F, 0.0F, 0.0F, 0.664F));

        PartDefinition left_leg_0_foot = left_leg_0_knee.addOrReplaceChild("left_leg_0_foot", CubeListBuilder.create(), PartPose.offset(4.565F, 10.209F, 0.0F));

        PartDefinition left_leg_0_point_r1 = left_leg_0_foot.addOrReplaceChild("left_leg_0_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.535F, 1.541F, 0.0F, 0.0F, 0.0F, 2.5128F));

        PartDefinition left_leg_0_ankle_r1 = left_leg_0_foot.addOrReplaceChild("left_leg_0_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.8074F));

        PartDefinition left_leg_1 = body.addOrReplaceChild("left_leg_1", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.0F, -0.6F, 0.0F, 0.3142F, 0.0F));

        PartDefinition left_leg_1_upper_r1 = left_leg_1.addOrReplaceChild("left_leg_1_upper_r1", CubeListBuilder.create().texOffs(63, 21).addBox(-1.95F, -10.3742F, -1.8F, 3.9F, 10.6242F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2178F));

        PartDefinition left_leg_1_knee = left_leg_1.addOrReplaceChild("left_leg_1_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.5F, -3.5F, 0.0F));

        PartDefinition left_leg_1_lower_r1 = left_leg_1_knee.addOrReplaceChild("left_leg_1_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.7211F));

        PartDefinition left_leg_1_knee_spur_r1 = left_leg_1_knee.addOrReplaceChild("left_leg_1_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, -0.8F, 0.0F, 0.0F, 0.0F, 0.664F));

        PartDefinition left_leg_1_foot = left_leg_1_knee.addOrReplaceChild("left_leg_1_foot", CubeListBuilder.create(), PartPose.offset(4.565F, 10.209F, 0.0F));

        PartDefinition left_leg_1_point_r1 = left_leg_1_foot.addOrReplaceChild("left_leg_1_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.535F, 1.541F, 0.0F, 0.0F, 0.0F, 2.5128F));

        PartDefinition left_leg_1_ankle_r1 = left_leg_1_foot.addOrReplaceChild("left_leg_1_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.8074F));

        PartDefinition left_leg_2 = body.addOrReplaceChild("left_leg_2", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.0F, 0.8F, 0.0F, -0.3142F, 0.0F));

        PartDefinition left_leg_2_upper_r1 = left_leg_2.addOrReplaceChild("left_leg_2_upper_r1", CubeListBuilder.create().texOffs(63, 21).addBox(-1.95F, -10.3742F, -1.8F, 3.9F, 10.6242F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2178F));

        PartDefinition left_leg_2_knee = left_leg_2.addOrReplaceChild("left_leg_2_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.5F, -3.5F, 0.0F));

        PartDefinition left_leg_2_lower_r1 = left_leg_2_knee.addOrReplaceChild("left_leg_2_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.7211F));

        PartDefinition left_leg_2_knee_spur_r1 = left_leg_2_knee.addOrReplaceChild("left_leg_2_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, -0.8F, 0.0F, 0.0F, 0.0F, 0.664F));

        PartDefinition left_leg_2_foot = left_leg_2_knee.addOrReplaceChild("left_leg_2_foot", CubeListBuilder.create(), PartPose.offset(4.565F, 10.209F, 0.0F));

        PartDefinition left_leg_2_point_r1 = left_leg_2_foot.addOrReplaceChild("left_leg_2_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.535F, 1.541F, 0.0F, 0.0F, 0.0F, 2.5128F));

        PartDefinition left_leg_2_ankle_r1 = left_leg_2_foot.addOrReplaceChild("left_leg_2_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.8074F));

        PartDefinition left_leg_3 = body.addOrReplaceChild("left_leg_3", CubeListBuilder.create().texOffs(19, 21).addBox(-2.1F, -1.7F, -1.7F, 4.2F, 3.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 0.0F, 2.2F, 0.0F, -0.7854F, 0.0F));

        PartDefinition left_leg_3_upper_r1 = left_leg_3.addOrReplaceChild("left_leg_3_upper_r1", CubeListBuilder.create().texOffs(63, 21).addBox(-1.95F, -10.3742F, -1.8F, 3.9F, 10.6242F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2178F));

        PartDefinition left_leg_3_knee = left_leg_3.addOrReplaceChild("left_leg_3_knee", CubeListBuilder.create().texOffs(19, 21).addBox(-2.2F, -1.8F, -2.0F, 4.4F, 3.6F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.5F, -3.5F, 0.0F));

        PartDefinition left_leg_3_lower_r1 = left_leg_3_knee.addOrReplaceChild("left_leg_3_lower_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-1.4F, -11.4332F, -1.3F, 2.8F, 11.6832F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.7211F));

        PartDefinition left_leg_3_knee_spur_r1 = left_leg_3_knee.addOrReplaceChild("left_leg_3_knee_spur_r1", CubeListBuilder.create().texOffs(38, 21).addBox(-0.45F, -3.1706F, -0.55F, 0.9F, 3.4206F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, -0.8F, 0.0F, 0.0F, 0.0F, 0.664F));

        PartDefinition left_leg_3_foot = left_leg_3_knee.addOrReplaceChild("left_leg_3_foot", CubeListBuilder.create(), PartPose.offset(4.565F, 10.209F, 0.0F));

        PartDefinition left_leg_3_point_r1 = left_leg_3_foot.addOrReplaceChild("left_leg_3_point_r1", CubeListBuilder.create().texOffs(58, 21).addBox(-0.225F, -0.9301F, -0.3F, 0.45F, 1.1801F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.535F, 1.541F, 0.0F, 0.0F, 0.0F, 2.5128F));

        PartDefinition left_leg_3_ankle_r1 = left_leg_3_foot.addOrReplaceChild("left_leg_3_ankle_r1", CubeListBuilder.create().texOffs(109, 0).addBox(-0.8F, -1.8812F, -0.75F, 1.6F, 2.1312F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.8074F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
