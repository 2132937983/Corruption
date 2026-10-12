package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.monster.Creeper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Recognizable creeper head, jointed pointed legs and an attacking alien belly hand. */
public final class CorruptedCreeperModel extends HierarchicalModel<Creeper> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_creeper"), "main");
    private final ModelPart root, body, upperBody, head, claw, clawElbow, palm;
    private final Vector3f[] restUpper = new Vector3f[4], restLower = new Vector3f[4];
    private final ModelPart[] legs = new ModelPart[4], knees = new ModelPart[4], feet = new ModelPart[4], fingers = new ModelPart[3], parts;
    private final Map<Creeper, Motion> motions = new WeakHashMap<>();
    private static final class Motion {
        float age, air, previousAttack, landed = Float.NEGATIVE_INFINITY;
        int attackIndex;
        boolean ground;
        Motion(Creeper entity, float age) { this.age = age; ground = entity.onGround(); }
        void update(Creeper entity, float now, boolean airborne, float attack) {
            float elapsed = Mth.clamp(now - age, 0, 4);
            if (entity.onGround() && !ground && air > 0.1F) landed = now;
            ground = entity.onGround();
            air += Mth.clamp((airborne ? 1 : 0) - air, -elapsed / 3, elapsed / 3);
            age = now;
            if (attack > 0 && previousAttack <= 0) attackIndex++;
            previousAttack = attack;
        }
    }
    public CorruptedCreeperModel(ModelPart root) {
        this.root = root; body = root.getChild("body"); upperBody = body.getChild("upper_body"); head = upperBody.getChild("head");
        claw = upperBody.getChild("belly_claw"); clawElbow = claw.getChild("claw_elbow"); palm = clawElbow.getChild("claw_palm");
        for (int i = 0; i < 3; i++) fingers[i] = palm.getChild("claw_finger_" + i);
        for (int i = 0; i < 4; i++) {
            String name = (i < 2 ? "right" : "left") + (i % 2 == 0 ? "_front_leg" : "_hind_leg");
            legs[i] = body.getChild(name); knees[i] = legs[i].getChild(name + "_knee"); feet[i] = knees[i].getChild(name + "_foot");
            restUpper[i] = new Vector3f(knees[i].x, knees[i].y, knees[i].z);
            restLower[i] = new Vector3f(feet[i].x, feet[i].y, feet[i].z);
        }
        parts = root.getAllParts().toArray(ModelPart[]::new);
    }
    @Override public ModelPart root() { return root; }
    @Override public void setupAnim(Creeper entity, float swing, float amount, float age, float yaw, float pitch) {
        for (ModelPart part : parts) part.resetPose();
        Motion motion = motions.computeIfAbsent(entity, key -> new Motion(key, age));
        boolean fluid = RemnantAnimationState.fluid(entity), seated = RemnantAnimationState.riding(entity, riding);
        boolean crouched = RemnantAnimationState.crouching(entity, false);
        boolean airborne = !entity.onGround() && !seated && !fluid
                && (Math.abs(entity.getDeltaMovement().y) > 0.015 || motion.air > 0);
        float stride = Mth.clamp(amount, 0, 1), attack = Mth.clamp(attackTime, 0, 1);
        motion.update(entity, age, airborne, attack);
        float t = age + Math.floorMod(entity.getUUID().hashCode(), 41), breath = Mth.sin(t * 0.065F);
        float windup = attack > 0 && attack < 0.28F ? smooth(attack / 0.28F) : 0;
        float strike = attack >= 0.28F && attack < 0.6F ? smooth((attack - 0.28F) / 0.32F)
                : attack >= 0.6F ? 1 - smooth((attack - 0.6F) / 0.4F) : 0;
        if (attack >= 0.28F && attack < 0.6F) windup = 1-strike;
        float landing = 1 - Mth.clamp((age - motion.landed) / 7, 0, 1);
        float idle = (1-smooth(Mth.clamp(stride*2,0,1))) * (attack>0?0:1) * (1-motion.air);
        if (fluid || seated || crouched) idle=0;
        float shift = Mth.sin(t*0.036F+0.8F), side=motion.attackIndex%2==0?1:-1;
        float idleLean = (.5F + .5F * Mth.sin(t * .05F)) * idle * .36F;
        body.y += breath * idle * 0.30F + stride * 0.20F + landing * 1.3F + (crouched?0.7F:0);
        body.x += shift*idle*.26F;
        upperBody.xRot = -0.105F - idleLean - windup*.21F + strike*.24F + landing*.05F;
        upperBody.yRot = Mth.sin(swing*.85F)*stride*.13F + shift*idle*.055F + side*(-windup*.22F+strike*.28F);
        head.yRot = Mth.clamp(yaw * Mth.DEG_TO_RAD, -0.9F, 0.9F) - upperBody.yRot;
        head.xRot = Mth.clamp(pitch * Mth.DEG_TO_RAD, -0.5F, 0.5F);
        head.yRot += idle*Mth.sin(t*.04F+.7F)*.085F;
        head.xRot += idleLean * .35F + idle*Mth.sin(t*.046F+1.2F)*.055F;
        claw.xRot += breath*idle*.13F - windup*.62F + strike*.40F - motion.air*.20F;
        claw.yRot += side*(windup*.25F-strike*.28F)+shift*idle*.035F;
        clawElbow.xRot += -windup*.75F+strike*.55F+idle*Mth.sin(t*.065F-.5F)*.15F;
        palm.xRot += -strike*.25F+idle*Mth.sin(t*.065F-.8F)*.16F;
        for (int i = 0; i < 3; i++) {
            fingers[i].yRot += (i-1)*(windup*.09F-strike*.14F+motion.air*.06F)
                    +idle*Mth.sin(t*.065F+i*.6F)*.055F;
            fingers[i].xRot += idle*Mth.sin(t*.065F+i*.45F-.5F)*.03F+windup*.08F;
        }
        Matrix4f inverse = new Matrix4f().translation(body.x, body.y, body.z)
                .rotateZYX(body.zRot, body.yRot, body.xRot).invert();
        for (int i = 0; i < 4; i++) {
            float phase = swing * 0.85F + (i == 0 || i == 3 ? 0 : Mth.PI);
            float lift = Math.max(0, Mth.sin(phase)) * stride * 2.0F;
            float z = (i % 2 == 0 ? -5.2F : 5.2F) + Mth.cos(phase) * stride * 2.2F;
            float airborneLift = motion.air * (entity.getDeltaMovement().y > 0.02 ? 3.5F : 1.05F);
            if (fluid) { lift=1.1F+Mth.sin(t*.16F+i)*.5F; z+=Mth.cos(t*.16F+i)*1.4F; }
            if (seated) { lift=2.5F; z=i%2==0?-4:4; }
            Vector3f target = inverse.transformPosition(new Vector3f(i < 2 ? -4.8F : 4.8F, 23.7F - lift - airborneLift, z))
                    .sub(legs[i].x, legs[i].y, legs[i].z);
            poseLeg(i, target);
        }
    }
    private static float smooth(float value) { return value * value * (3 - 2 * value); }
    private void poseLeg(int index, Vector3f target) {
        float upper = restUpper[index].length(), lower = restLower[index].length();
        float distance = Mth.clamp(target.length(), Math.abs(upper - lower) + 0.01F, upper + lower - 0.01F);
        Vector3f direction = new Vector3f(target).normalize();
        float along = (upper * upper - lower * lower + distance * distance) / (2 * distance);
        float out = (float) Math.sqrt(Math.max(0, upper * upper - along * along));
        Vector3f pole = new Vector3f(index < 2 ? -1 : 1, 0, 0);
        pole.sub(new Vector3f(direction).mul(pole.dot(direction))).normalize();
        Vector3f joint = new Vector3f(direction).mul(along).add(pole.mul(out));
        Quaternionf upperRotation = new Quaternionf().rotationTo(new Vector3f(restUpper[index]).normalize(), new Vector3f(joint).normalize());
        Vector3f localLower = new Quaternionf(upperRotation).conjugate().transform(new Vector3f(target).sub(joint)).normalize();
        Quaternionf lowerRotation = new Quaternionf().rotationTo(new Vector3f(restLower[index]).normalize(), localLower);
        setRotation(legs[index], upperRotation); setRotation(knees[index], lowerRotation);
    }
    private static void setRotation(ModelPart part, Quaternionf rotation) {
        Vector3f angles = rotation.getEulerAnglesZYX(new Vector3f());
        part.xRot = angles.x; part.yRot = angles.y; part.zRot = angles.z;
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -7.15F, -2.0F, 8.0F, 7.15F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition upper_body = body.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -7.0F, -2.0F, 8.0F, 7.15F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition belly_claw = upper_body.addOrReplaceChild("belly_claw", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -1.8F));

        PartDefinition claw_upper_arm_r1 = belly_claw.addOrReplaceChild("claw_upper_arm_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-0.825F, -2.5087F, -0.75F, 1.65F, 2.7087F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, -2.2F, 1.2627F, 0.0F, 0.0F));

        PartDefinition claw_root_tendon_r1 = belly_claw.addOrReplaceChild("claw_root_tendon_r1", CubeListBuilder.create().texOffs(98, 0).addBox(-1.0F, -2.577F, -0.85F, 2.0F, 2.777F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 1.3156F, 0.0F, 0.0F));

        PartDefinition claw_elbow = belly_claw.addOrReplaceChild("claw_elbow", CubeListBuilder.create(), PartPose.offset(0.0F, -1.3F, -4.4F));

        PartDefinition claw_wrist_r1 = claw_elbow.addOrReplaceChild("claw_wrist_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.575F, -1.656F, -0.55F, 1.15F, 1.856F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2F, -1.5F, 1.8491F, 0.0F, 0.0F));

        PartDefinition claw_forearm_r1 = claw_elbow.addOrReplaceChild("claw_forearm_r1", CubeListBuilder.create().texOffs(107, 0).addBox(-0.725F, -1.7133F, -0.65F, 1.45F, 1.9133F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4382F, 0.0F, 0.0F));

        PartDefinition claw_palm = claw_elbow.addOrReplaceChild("claw_palm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.2F, -2.9F));

        PartDefinition claw_palm_tendon_2_r1 = claw_palm.addOrReplaceChild("claw_palm_tendon_2_r1", CubeListBuilder.create().texOffs(0, 17).addBox(-0.575F, -3.0671F, -0.475F, 1.15F, 3.2671F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5753F, 0.4569F, -1.3321F));

        PartDefinition claw_palm_tendon_1_r1 = claw_palm.addOrReplaceChild("claw_palm_tendon_1_r1", CubeListBuilder.create().texOffs(0, 17).addBox(-0.575F, -2.8575F, -0.475F, 1.15F, 3.0575F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3623F, 0.0F, 0.0F));

        PartDefinition claw_palm_tendon_0_r1 = claw_palm.addOrReplaceChild("claw_palm_tendon_0_r1", CubeListBuilder.create().texOffs(0, 17).addBox(-0.575F, -3.2871F, -0.475F, 1.15F, 3.4871F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7083F, -0.4595F, 1.1279F));

        PartDefinition claw_finger_0 = claw_palm.addOrReplaceChild("claw_finger_0", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -0.2F, -1.8F, -0.0524F, 0.1571F, -0.1222F));

        PartDefinition claw_finger_0_segment_6_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_6_r1", CubeListBuilder.create().texOffs(19, 17).addBox(-0.0287F, -1.4926F, -0.035F, 0.0574F, 1.6926F, 0.07F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1736F, 4.346F, -6.068F, -2.3951F, -0.3648F, -0.1442F));

        PartDefinition claw_finger_0_segment_5_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_5_r1", CubeListBuilder.create().texOffs(19, 17).addBox(-0.123F, -1.5421F, -0.14F, 0.246F, 1.7421F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3127F, 3.034F, -6.314F, -2.789F, -1.0114F, -0.1967F));

        PartDefinition claw_finger_0_segment_4_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_4_r1", CubeListBuilder.create().texOffs(19, 17).addBox(-0.246F, -1.7856F, -0.275F, 0.492F, 1.9856F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.39F, 1.558F, -5.74F, 2.7578F, 0.2581F, -0.0504F));

        PartDefinition claw_finger_0_segment_3_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_3_r1", CubeListBuilder.create().texOffs(14, 17).addBox(-0.3772F, -1.8825F, -0.425F, 0.7544F, 2.0825F, 0.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.39F, 0.328F, -4.592F, 2.3907F, 0.0F, 0.0F));

        PartDefinition claw_finger_0_segment_2_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_2_r1", CubeListBuilder.create().texOffs(7, 17).addBox(-0.4838F, -1.9085F, -0.55F, 0.9676F, 2.1085F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3127F, -0.369F, -3.034F, 1.9883F, -0.0697F, 0.0453F));

        PartDefinition claw_finger_0_segment_1_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_1_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.574F, -1.769F, -0.65F, 1.148F, 1.969F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1736F, -0.492F, -1.476F, 1.641F, -0.0957F, 0.0892F));

        PartDefinition claw_finger_0_segment_0_r1 = claw_finger_0.addOrReplaceChild("claw_finger_0_segment_0_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.5535F, -1.7655F, -0.575F, 1.107F, 1.9655F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.2401F, -0.0796F, 0.1114F));

        PartDefinition claw_finger_1 = claw_palm.addOrReplaceChild("claw_finger_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.55F, -2.6F, 0.0349F, 0.0F, 0.0349F));

        PartDefinition claw_finger_1_segment_6_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_6_r1", CubeListBuilder.create().texOffs(19, 17).addBox(-0.035F, -1.762F, -0.035F, 0.07F, 1.962F, 0.07F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.3F, -7.4F, -2.4469F, 0.0F, 0.0F));

        PartDefinition claw_finger_1_segment_5_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_5_r1", CubeListBuilder.create().texOffs(14, 17).addBox(-0.15F, -1.8279F, -0.14F, 0.3F, 2.0279F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.7F, -7.7F, -2.9562F, 0.0F, 0.0F));

        PartDefinition claw_finger_1_segment_4_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_4_r1", CubeListBuilder.create().texOffs(14, 17).addBox(-0.3F, -2.1313F, -0.275F, 0.6F, 2.3313F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9F, -7.0F, 2.7707F, 0.0F, 0.0F));

        PartDefinition claw_finger_1_segment_3_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_3_r1", CubeListBuilder.create().texOffs(14, 17).addBox(-0.46F, -2.2518F, -0.425F, 0.92F, 2.4518F, 0.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.4F, -5.6F, 2.3907F, 0.0F, 0.0F));

        PartDefinition claw_finger_1_segment_2_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_2_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.59F, -2.2815F, -0.55F, 1.18F, 2.4815F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.45F, -3.7F, 1.9915F, 0.0F, 0.0F));

        PartDefinition claw_finger_1_segment_1_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_1_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.7F, -2.1059F, -0.65F, 1.4F, 2.3059F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, -1.8F, 1.6496F, 0.0F, 0.0F));

        PartDefinition claw_finger_1_segment_0_r1 = claw_finger_1.addOrReplaceChild("claw_finger_1_segment_0_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.675F, -2.0974F, -0.575F, 1.35F, 2.2974F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.249F, 0.0F, 0.0F));

        PartDefinition claw_finger_2 = claw_palm.addOrReplaceChild("claw_finger_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, 0.1F, -1.4F, -0.1396F, -0.1571F, 0.1571F));

        PartDefinition claw_finger_2_segment_6_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_6_r1", CubeListBuilder.create().texOffs(19, 17).addBox(-0.0315F, -1.6165F, -0.035F, 0.063F, 1.8165F, 0.07F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1736F, 4.77F, -6.66F, -2.404F, 0.3338F, 0.13F));

        PartDefinition claw_finger_2_segment_5_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_5_r1", CubeListBuilder.create().texOffs(19, 17).addBox(-0.135F, -1.6717F, -0.14F, 0.27F, 1.8717F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3127F, 3.33F, -6.93F, -2.8266F, 0.9369F, 0.1604F));

        PartDefinition claw_finger_2_segment_4_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_4_r1", CubeListBuilder.create().texOffs(14, 17).addBox(-0.27F, -1.9399F, -0.275F, 0.54F, 2.1399F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.39F, 1.71F, -6.3F, 2.76F, -0.2354F, 0.0457F));

        PartDefinition claw_finger_2_segment_3_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_3_r1", CubeListBuilder.create().texOffs(14, 17).addBox(-0.414F, -2.0466F, -0.425F, 0.828F, 2.2466F, 0.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.39F, 0.36F, -5.04F, 2.3907F, 0.0F, 0.0F));

        PartDefinition claw_finger_2_segment_2_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_2_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.531F, -2.0749F, -0.55F, 1.062F, 2.2749F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3127F, -0.405F, -3.33F, 1.9888F, 0.0635F, -0.0413F));

        PartDefinition claw_finger_2_segment_1_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_1_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.63F, -1.921F, -0.65F, 1.26F, 2.121F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1736F, -0.54F, -1.62F, 1.6425F, 0.0873F, -0.0813F));

        PartDefinition claw_finger_2_segment_0_r1 = claw_finger_2.addOrReplaceChild("claw_finger_2_segment_0_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-0.6075F, -1.9164F, -0.575F, 1.215F, 2.1164F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.2416F, 0.0727F, -0.1016F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(-2.4F, 0.0F, -2.5F));

        PartDefinition right_front_leg_muscle_r1 = right_front_leg.addOrReplaceChild("right_front_leg_muscle_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-1.55F, -5.6736F, -1.4F, 3.1F, 5.8736F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2056F, 0.4627F, -2.316F));

        PartDefinition right_front_leg_knee = right_front_leg.addOrReplaceChild("right_front_leg_knee", CubeListBuilder.create(), PartPose.offset(-3.6F, 4.0F, -1.0F));

        PartDefinition right_front_leg_shin_2_r1 = right_front_leg_knee.addOrReplaceChild("right_front_leg_shin_2_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.175F, -1.6563F, -0.175F, 0.35F, 1.8563F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.912F, 4.332F, -1.292F, 2.381F, -1.1523F, 0.5082F));

        PartDefinition right_front_leg_shin_1_r1 = right_front_leg_knee.addOrReplaceChild("right_front_leg_shin_1_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-0.65F, -2.3845F, -0.575F, 1.3F, 2.5845F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.48F, 2.28F, -0.68F, 2.381F, -1.1523F, 0.5082F));

        PartDefinition right_front_leg_shin_0_r1 = right_front_leg_knee.addOrReplaceChild("right_front_leg_shin_0_r1", CubeListBuilder.create().texOffs(73, 0).addBox(-1.1F, -2.6272F, -1.0F, 2.2F, 2.8272F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.381F, -1.1523F, 0.5082F));

        PartDefinition right_front_leg_foot = right_front_leg_knee.addOrReplaceChild("right_front_leg_foot", CubeListBuilder.create(), PartPose.offset(1.2F, 5.7F, -1.7F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offset(-2.4F, 0.0F, 2.5F));

        PartDefinition right_hind_leg_muscle_r1 = right_hind_leg.addOrReplaceChild("right_hind_leg_muscle_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-1.55F, -5.6736F, -1.4F, 3.1F, 5.8736F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2056F, -0.4627F, -2.316F));

        PartDefinition right_hind_leg_knee = right_hind_leg.addOrReplaceChild("right_hind_leg_knee", CubeListBuilder.create(), PartPose.offset(-3.6F, 4.0F, 1.0F));

        PartDefinition right_hind_leg_shin_2_r1 = right_hind_leg_knee.addOrReplaceChild("right_hind_leg_shin_2_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.175F, -1.6563F, -0.175F, 0.35F, 1.8563F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.912F, 4.332F, 1.292F, -2.381F, 1.1523F, 0.5082F));

        PartDefinition right_hind_leg_shin_1_r1 = right_hind_leg_knee.addOrReplaceChild("right_hind_leg_shin_1_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-0.65F, -2.3845F, -0.575F, 1.3F, 2.5845F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.48F, 2.28F, 0.68F, -2.381F, 1.1523F, 0.5082F));

        PartDefinition right_hind_leg_shin_0_r1 = right_hind_leg_knee.addOrReplaceChild("right_hind_leg_shin_0_r1", CubeListBuilder.create().texOffs(73, 0).addBox(-1.1F, -2.6272F, -1.0F, 2.2F, 2.8272F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.381F, 1.1523F, 0.5082F));

        PartDefinition right_hind_leg_foot = right_hind_leg_knee.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create(), PartPose.offset(1.2F, 5.7F, 1.7F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(2.4F, 0.0F, -2.5F));

        PartDefinition left_front_leg_muscle_r1 = left_front_leg.addOrReplaceChild("left_front_leg_muscle_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-1.55F, -5.6736F, -1.4F, 3.1F, 5.8736F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2056F, -0.4627F, 2.316F));

        PartDefinition left_front_leg_knee = left_front_leg.addOrReplaceChild("left_front_leg_knee", CubeListBuilder.create(), PartPose.offset(3.6F, 4.0F, -1.0F));

        PartDefinition left_front_leg_shin_2_r1 = left_front_leg_knee.addOrReplaceChild("left_front_leg_shin_2_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.175F, -1.6563F, -0.175F, 0.35F, 1.8563F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.912F, 4.332F, -1.292F, 2.381F, 1.1523F, -0.5082F));

        PartDefinition left_front_leg_shin_1_r1 = left_front_leg_knee.addOrReplaceChild("left_front_leg_shin_1_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-0.65F, -2.3845F, -0.575F, 1.3F, 2.5845F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.48F, 2.28F, -0.68F, 2.381F, 1.1523F, -0.5082F));

        PartDefinition left_front_leg_shin_0_r1 = left_front_leg_knee.addOrReplaceChild("left_front_leg_shin_0_r1", CubeListBuilder.create().texOffs(73, 0).addBox(-1.1F, -2.6272F, -1.0F, 2.2F, 2.8272F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.381F, 1.1523F, -0.5082F));

        PartDefinition left_front_leg_foot = left_front_leg_knee.addOrReplaceChild("left_front_leg_foot", CubeListBuilder.create(), PartPose.offset(-1.2F, 5.7F, -1.7F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(2.4F, 0.0F, 2.5F));

        PartDefinition left_hind_leg_muscle_r1 = left_hind_leg.addOrReplaceChild("left_hind_leg_muscle_r1", CubeListBuilder.create().texOffs(58, 0).addBox(-1.55F, -5.6736F, -1.4F, 3.1F, 5.8736F, 2.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2056F, 0.4627F, 2.316F));

        PartDefinition left_hind_leg_knee = left_hind_leg.addOrReplaceChild("left_hind_leg_knee", CubeListBuilder.create(), PartPose.offset(3.6F, 4.0F, 1.0F));

        PartDefinition left_hind_leg_shin_2_r1 = left_hind_leg_knee.addOrReplaceChild("left_hind_leg_shin_2_r1", CubeListBuilder.create().texOffs(93, 0).addBox(-0.175F, -1.6563F, -0.175F, 0.35F, 1.8563F, 0.35F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.912F, 4.332F, 1.292F, -2.381F, -1.1523F, -0.5082F));

        PartDefinition left_hind_leg_shin_1_r1 = left_hind_leg_knee.addOrReplaceChild("left_hind_leg_shin_1_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-0.65F, -2.3845F, -0.575F, 1.3F, 2.5845F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.48F, 2.28F, 0.68F, -2.381F, -1.1523F, -0.5082F));

        PartDefinition left_hind_leg_shin_0_r1 = left_hind_leg_knee.addOrReplaceChild("left_hind_leg_shin_0_r1", CubeListBuilder.create().texOffs(73, 0).addBox(-1.1F, -2.6272F, -1.0F, 2.2F, 2.8272F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.381F, -1.1523F, -0.5082F));

        PartDefinition left_hind_leg_foot = left_hind_leg_knee.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create(), PartPose.offset(-1.2F, 5.7F, 1.7F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
