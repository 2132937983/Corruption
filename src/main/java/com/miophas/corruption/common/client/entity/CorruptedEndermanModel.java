package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.EnderMan;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Vanilla enderman silhouette, a severed left arm, and a breathing growth rooted in one eye. */
public final class CorruptedEndermanModel extends HierarchicalModel<EnderMan> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_enderman"), "main");
    private final ModelPart root, body, head, arm, elbow, pustule, hand;
    private final ModelPart[] legs = new ModelPart[2], knees = new ModelPart[2], parts;
    private final Vector3f[] hips = new Vector3f[2], footTargets = new Vector3f[2];
    private final float[] legReach = {28.98F, 28.98F};
    private final Map<EnderMan, Motion> motions = new WeakHashMap<>();

    private static final class Motion {
        float age, air, lastAttack, landed = Float.NEGATIVE_INFINITY;
        boolean ground, hasAttacked, crossSlash;
        Motion(EnderMan entity, float age) { this.age = age; ground = entity.onGround(); }
        void update(EnderMan entity, float now, boolean airborne, float attack) {
            if (attack > 0 && (lastAttack <= 0 || attack < lastAttack - .2F)) {
                if (hasAttacked) crossSlash = !crossSlash;
                hasAttacked = true;
            }
            lastAttack = attack;
            float elapsed = Mth.clamp(now - age, 0, 4);
            if (entity.onGround() && !ground && air > 0.1F) landed = now;
            ground = entity.onGround();
            air += Mth.clamp((airborne ? 1 : 0) - air, -elapsed / 3, elapsed / 3);
            age = now;
        }
    }

    public CorruptedEndermanModel(ModelPart root) {
        this.root = root;
        body = root.getChild("body"); head = body.getChild("head");
        arm = body.getChild("right_arm"); elbow = arm.getChild("right_elbow");
        pustule = head.getChild("eye_pustule");
        hand = elbow.getChild("hand_spurs");
        for (int i = 0; i < 2; i++) {
            String side = i == 0 ? "right" : "left";
            legs[i] = body.getChild(side + "_leg"); knees[i] = legs[i].getChild(side + "_knee");
            hips[i] = new Vector3f(legs[i].x, legs[i].y, legs[i].z); footTargets[i] = new Vector3f();
        }
        parts = root.getAllParts().toArray(ModelPart[]::new);
    }

    @Override public ModelPart root() { return root; }

    @Override public void setupAnim(EnderMan entity, float swing, float amount, float age, float yaw, float pitch) {
        for (ModelPart part : parts) part.resetPose();
        Motion motion = motions.computeIfAbsent(entity, key -> new Motion(key, age));
        boolean fluid = RemnantAnimationState.fluid(entity), seated = RemnantAnimationState.riding(entity, riding);
        boolean crouched = RemnantAnimationState.crouching(entity, false);
        boolean airborne = !entity.onGround() && !seated && !fluid
                && (Math.abs(entity.getDeltaMovement().y) > 0.015 || motion.air > 0);
        float stride = Mth.clamp(amount, 0, 1.25F), attack = Mth.clamp(attackTime, 0, 1);
        motion.update(entity, age, airborne, attack);
        float t = age + Math.floorMod(entity.getUUID().hashCode(), 41);
        float breath = Mth.sin(t * .07F), shift = Mth.sin(t * .036F + .8F);
        float air = smooth(motion.air), step = Mth.sin(swing * .72F);
        float idle = (1 - smooth(stride * 2)) * (attack > 0 ? 0 : 1) * (1 - air);
        if (fluid || seated || crouched) idle = 0;
        boolean carrying = entity.getCarriedBlock() != null;
        float landing = 1 - Mth.clamp((age - motion.landed) / 7, 0, 1);
        landing *= landing;

        // An independent idle cycle remains visible without limb movement or a combat target.
        body.xRot = .035F + stride * .28F + idle * breath * .11F;
        body.yRot = step * stride * .11F + idle * Mth.sin(t * .025F) * .085F;
        body.zRot = step * stride * .045F + idle * shift * .07F;
        body.x += idle * shift * .34F;
        body.y += .18F + (breath + 1) * idle * .23F + stride * .50F;
        arm.xRot += -step * stride * .56F + idle * Mth.sin(t * .07F + .5F) * (carrying ? .05F : .18F);
        arm.yRot += idle * Mth.sin(t * .035F + .6F) * .075F;
        arm.zRot += .035F + idle * breath * .055F;
        elbow.xRot += -.10F - Math.max(0, step) * stride * .28F
                - idle * Mth.sin(t * .07F + .8F) * .14F;
        pustule.xScale = pustule.yScale = pustule.zScale = 1 + breath * .028F;
        hand.xScale = hand.yScale = hand.zScale = 1 + Mth.sin(t * .07F - .6F) * .022F;

        if (fluid) {
            body.xRot = .20F; body.yRot *= .3F; body.zRot *= .3F;
            arm.xRot = -.8F + Mth.sin(t * .16F) * .38F;
            arm.yRot = Mth.cos(t * .16F) * .16F; elbow.xRot = -.65F;
        } else if (seated) {
            body.xRot = .04F; body.yRot *= .3F; body.zRot = 0;
            arm.xRot -= .2F; elbow.xRot -= .25F;
        } else if (air > 0) {
            boolean rising = entity.getDeltaMovement().y > .02;
            body.xRot = Mth.lerp(air, body.xRot, rising ? -.14F : .32F);
            body.zRot *= 1 - air;
            arm.xRot = Mth.lerp(air, arm.xRot, rising ? -1.05F : -.48F);
            arm.zRot += air * .24F;
            elbow.xRot = Mth.lerp(air, elbow.xRot, rising ? -.75F : -.35F);
        }
        body.y += landing * 1.7F;
        body.xRot += landing * .16F;
        elbow.xRot -= landing * .18F;
        if (attack > 0) slash(motion.crossSlash, attack);
        if (carrying) { arm.xRot -= .4F; elbow.xRot -= .5F; }
        if (crouched) {
            body.xRot += .22F; body.y += 1.8F;
            arm.xRot -= .15F; elbow.xRot -= .18F;
        }
        body.xRot -= Mth.sin(entity.hurtTime / 10.0F * Mth.PI) * .10F;
        head.xRot = Mth.clamp(pitch * Mth.DEG_TO_RAD, -.65F, .65F) - body.xRot
                + idle * Mth.sin(t * .046F + 1.2F) * .07F;
        head.yRot = Mth.clamp(yaw * Mth.DEG_TO_RAD, -.9F, .9F) - body.yRot
                + idle * Mth.sin(t * .04F + .7F) * .10F;
        head.zRot = -body.zRot * .65F + idle * Mth.sin(t * .05F + 1.1F) * .035F;
        if (entity.isAggressive() || entity.isCreepy()) {
            head.y -= 1.1F;
            head.zRot += Mth.sin(t * .8F) * .012F;
        }
        for (int i = 0; i < 2; i++) {
            float phase = swing * 0.72F + i * Mth.PI;
            float lift = Math.max(0, Mth.sin(phase)) * stride * 3.2F * (1 - air);
            // Keep the fast stride inside the combined thigh/shin reach as the torso leans.
            float reach = Mth.cos(phase) * Math.min(stride, 1) * 7.0F * (1 - air);
            if (air > 0) {
                boolean rising = entity.getDeltaMovement().y > .02;
                lift += air * (rising ? 5.5F : 1.4F);
                reach += air * (i == 0 ? (rising ? -2.8F : 1) : (rising ? 1.8F : -1));
            }
            if (fluid) { lift=2.0F+Mth.sin(t*.16F+i*Mth.PI)*.8F; reach=Mth.cos(t*.16F+i*Mth.PI)*3; }
            if (seated) { lift=7; reach=-8; }
            footTargets[i].set(i == 0 ? -2 : 2, 23.8F - lift, reach);
        }
        if (!fluid && !seated && air < .05F) RemnantAnimationGrounding.lowerBody(body, hips, footTargets, legReach);
        Matrix4f inverse = new Matrix4f().translation(body.x, body.y, body.z)
                .rotateZYX(body.zRot, body.yRot, body.xRot).invert();
        for (int i = 0; i < 2; i++) {
            Vector3f target = inverse.transformPosition(new Vector3f(footTargets[i])).sub(hips[i]);
            poseLeg(legs[i], knees[i], target);
        }
    }

    private static float smooth(float value) {
        value = Mth.clamp(value, 0, 1);
        return value * value * (3 - 2 * value);
    }

    /** Alternate two right-arm slashes; all three phases meet at the same joint poses. */
    private void slash(boolean cross, float progress) {
        float twist, reach, sweep, spread, fold;
        float startTwist = cross ? .18F : -.25F, endTwist = cross ? -.25F : .20F;
        float startSweep = cross ? .65F : -.40F, endSweep = cross ? -.70F : .30F;
        float endReach = cross ? -1.10F : -1.55F, endFold = cross ? -.20F : .15F;
        float endSpread = cross ? .18F : -.05F;
        if (progress < .25F) {
            float windup = smooth(progress / .25F);
            twist = startTwist * windup; reach = .45F * windup; sweep = startSweep * windup;
            spread = .18F * windup; fold = -.55F * windup;
        } else if (progress < .55F) {
            float strike = smooth((progress - .25F) / .30F);
            twist = Mth.lerp(strike, startTwist, endTwist);
            reach = Mth.lerp(strike, .45F, endReach); sweep = Mth.lerp(strike, startSweep, endSweep);
            spread = Mth.lerp(strike, .18F, endSpread); fold = Mth.lerp(strike, -.55F, endFold);
        } else {
            float recovery = 1 - smooth((progress - .55F) / .45F);
            twist = endTwist * recovery; reach = endReach * recovery; sweep = endSweep * recovery;
            spread = endSpread * recovery; fold = endFold * recovery;
        }
        body.yRot += twist * 1.5F;
        body.xRot += Mth.sin(progress * Mth.PI) * .10F;
        arm.xRot += reach * 1.2F; arm.yRot += sweep * 1.4F; arm.zRot += spread * 1.4F; elbow.xRot += fold * 1.3F;
    }

    private static void poseLeg(ModelPart leg, ModelPart knee, Vector3f target) {
        float distance = Mth.clamp(target.length(), 1.01F, 28.99F);
        Vector3f direction = new Vector3f(target).normalize();
        float along = (225 - 196 + distance * distance) / (2 * distance);
        float out = (float) Math.sqrt(Math.max(0, 225 - along * along));
        Vector3f pole = new Vector3f(0, 0, -1);
        pole.sub(new Vector3f(direction).mul(pole.dot(direction))).normalize();
        Vector3f joint = new Vector3f(direction).mul(along).add(pole.mul(out));
        Quaternionf upper = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), new Vector3f(joint).normalize());
        Vector3f lowerDirection = new Quaternionf(upper).conjugate()
                .transform(new Vector3f(direction).mul(distance).sub(joint)).normalize();
        Quaternionf lower = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), lowerDirection);
        Vector3f angles = upper.getEulerAnglesZYX(new Vector3f());
        leg.xRot = angles.x; leg.yRot = angles.y; leg.zRot = angles.z;
        angles = lower.getEulerAnglesZYX(angles);
        knee.xRot = angles.x; knee.yRot = angles.y; knee.zRot = angles.z;
    }

    /** The surviving right wrist is the only carried-block attachment point. */
    public void translateToRightHand(PoseStack poses) {
        body.translateAndRotate(poses); arm.translateAndRotate(poses); elbow.translateAndRotate(poses);
        poses.translate(0, 14 / 16.0F, 0);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(51, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(76, 0).addBox(-1.3F, -2.0F, -1.1F, 2.6F, 4.0F, 2.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(18, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition cheek_fissure_r1 = head.addOrReplaceChild("cheek_fissure_r1", CubeListBuilder.create().texOffs(40, 20).addBox(-0.05F, -0.15F, -0.13F, 0.65F, 1.85F, 0.28F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.8F, -4.0F, 0.0F, 0.0F, -0.1396F));

        PartDefinition eye_pustule = head.addOrReplaceChild("eye_pustule", CubeListBuilder.create().texOffs(0, 20).addBox(-1.55F, -2.1F, -0.65F, 3.45F, 3.3F, 1.15F, new CubeDeformation(0.0F)), PartPose.offset(2.2F, -4.0F, -4.0F));

        PartDefinition pustule_lower_tear_r1 = eye_pustule.addOrReplaceChild("pustule_lower_tear_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.1F, -0.2F, -0.45F, 0.6F, 0.85F, 0.55F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, 0.9F, -0.6F, -0.0873F, 0.0F, -0.1396F));

        PartDefinition pustule_seeping_fissure_r1 = eye_pustule.addOrReplaceChild("pustule_seeping_fissure_r1", CubeListBuilder.create().texOffs(40, 20).addBox(-0.1F, -1.1F, -1.72F, 0.75F, 1.85F, 0.27F, new CubeDeformation(0.0F))
        .texOffs(13, 20).addBox(-1.1F, -1.65F, -1.6F, 2.4F, 2.65F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1222F));

        PartDefinition pustule_outer_lobe_r1 = eye_pustule.addOrReplaceChild("pustule_outer_lobe_r1", CubeListBuilder.create().texOffs(31, 20).addBox(-0.35F, -1.35F, -0.8F, 1.1F, 1.95F, 1.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.0F, 0.0F, 0.0F, -0.1222F, -0.1396F));

        PartDefinition pustule_upper_lobe_r1 = eye_pustule.addOrReplaceChild("pustule_upper_lobe_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-0.6F, -1.1F, -0.8F, 1.95F, 1.45F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2F, -1.5F, -0.3F, -0.0698F, 0.1396F, -0.0873F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(89, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 16.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -0.0698F, 0.0F, 0.0349F));

        PartDefinition arm_sickle_upper_2_3_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_3_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0693F, -1.7928F, -0.0693F, 0.1386F, 1.9428F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6773F, 15.746F, -3.5511F, -1.3029F, 1.3542F, 1.6236F));

        PartDefinition arm_sickle_upper_2_3_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_3_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3217F, -2.0804F, -0.297F, 0.6435F, 2.2304F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1133F, 13.982F, -3.0065F, 1.306F, 1.2742F, -1.5376F));

        PartDefinition arm_sickle_upper_2_3_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_3_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5692F, -2.7149F, -0.5197F, 1.1384F, 2.8649F, 1.0394F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8444F, 12.12F, -1.7811F, 1.2343F, 1.0401F, -1.358F));

        PartDefinition arm_sickle_upper_2_3_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_3_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8167F, -2.4462F, -0.693F, 1.6334F, 2.5962F, 1.3859F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5755F, 10.65F, -0.5557F, 1.1983F, 0.9607F, -1.3036F));

        PartDefinition arm_sickle_upper_2_2_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_2_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0409F, -0.7233F, -0.0409F, 0.0819F, 0.8733F, 0.0819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.601F, 12.7284F, -1.6579F, -1.6236F, -1.3542F, -1.3029F));

        PartDefinition arm_sickle_upper_2_2_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_2_2_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.1901F, -0.8237F, -0.1754F, 0.3801F, 0.9737F, 0.3509F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.411F, 12.1128F, -1.4611F, 1.5376F, -1.2742F, 1.306F));

        PartDefinition arm_sickle_upper_2_2_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_2_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3363F, -1.0451F, -0.307F, 0.6725F, 1.1951F, 0.614F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9834F, 11.463F, -1.0183F, 1.358F, -1.0401F, 1.2343F));

        PartDefinition arm_sickle_upper_2_2_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_2_0_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.4825F, -0.9513F, -0.4094F, 0.9649F, 1.1013F, 0.8187F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5557F, 10.95F, -0.5755F, 1.3036F, -0.9607F, 1.1983F));

        PartDefinition arm_sickle_upper_2_1_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_1_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0427F, -0.7743F, -0.0427F, 0.0854F, 0.9243F, 0.0854F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7542F, 12.5865F, 1.694F, 1.3029F, 1.3542F, -1.6236F));

        PartDefinition arm_sickle_upper_2_1_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_1_2_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.1983F, -0.8835F, -0.1831F, 0.3967F, 1.0335F, 0.3661F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5398F, 11.9162F, 1.487F, -1.306F, 1.2742F, 1.5376F));

        PartDefinition arm_sickle_upper_2_1_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_1_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3509F, -1.1247F, -0.3204F, 0.7018F, 1.2747F, 0.6408F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0577F, 11.2086F, 1.0214F, -1.2343F, 1.0401F, 1.358F));

        PartDefinition arm_sickle_upper_2_1_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_1_0_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-0.5035F, -1.0226F, -0.4272F, 1.0069F, 1.1726F, 0.8543F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5755F, 10.65F, 0.5557F, -1.1983F, 0.9607F, 1.3036F));

        PartDefinition arm_sickle_upper_2_0_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_0_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0664F, -1.6587F, -0.0664F, 0.1328F, 1.8087F, 0.1328F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3066F, 15.63F, 3.4241F, 1.6236F, -1.3542F, 1.3029F));

        PartDefinition arm_sickle_upper_2_0_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_0_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3083F, -1.9228F, -0.2846F, 0.6166F, 2.0728F, 0.5692F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8064F, 14.01F, 2.9061F, -1.5376F, -1.2742F, -1.306F));

        PartDefinition arm_sickle_upper_2_0_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_0_1_r1", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5455F, -2.5055F, -0.4981F, 1.091F, 2.6555F, 0.9961F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6811F, 12.3F, 1.7408F, -1.358F, -1.0401F, -1.2343F));

        PartDefinition arm_sickle_upper_2_0_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_2_0_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.7827F, -2.2588F, -0.6641F, 1.5653F, 2.4088F, 1.3282F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5557F, 10.95F, 0.5755F, -1.3036F, -0.9607F, -1.1983F));

        PartDefinition arm_sickle_upper_1_3_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_3_3_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.0737F, -2.0107F, -0.0737F, 0.1475F, 2.1607F, 0.1475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2209F, 11.622F, -5.2321F, -2.7453F, 0.7796F, 0.1646F));

        PartDefinition arm_sickle_upper_1_3_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_3_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3424F, -2.3364F, -0.3161F, 0.6848F, 2.4864F, 0.6321F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8739F, 9.624F, -4.4147F, 2.6001F, 0.7592F, -0.2206F));

        PartDefinition arm_sickle_upper_1_3_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_3_1_r1", CubeListBuilder.create().texOffs(17, 31).addBox(-0.6058F, -3.0551F, -0.5531F, 1.2116F, 3.2051F, 1.1062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0933F, 7.515F, -2.5756F, 2.2017F, 0.6697F, -0.3499F));

        PartDefinition arm_sickle_upper_1_3_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_3_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8692F, -2.7508F, -0.7375F, 1.7384F, 2.9008F, 1.475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3126F, 5.85F, -0.7364F, 2.0746F, 0.631F, -0.3809F));

        PartDefinition arm_sickle_upper_1_2_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_2_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0438F, -0.8061F, -0.0438F, 0.0876F, 0.9561F, 0.0876F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3217F, 8.1853F, -0.9855F, -0.1646F, -0.7796F, -2.7453F));

        PartDefinition arm_sickle_upper_1_2_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_2_2_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.2033F, -0.921F, -0.1877F, 0.4067F, 1.071F, 0.3754F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0334F, 7.4808F, -0.8631F, 0.2206F, -0.7592F, 2.6001F));

        PartDefinition arm_sickle_upper_1_2_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_2_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3597F, -1.1744F, -0.3285F, 0.7195F, 1.3244F, 0.6569F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3849F, 6.7371F, -0.5879F, 0.3499F, -0.6697F, 2.2017F));

        PartDefinition arm_sickle_upper_1_2_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_2_0_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-0.5161F, -1.0671F, -0.4379F, 1.0323F, 1.2171F, 0.8759F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7364F, 6.15F, -0.3126F, 0.3809F, -0.631F, 2.0746F));

        PartDefinition arm_sickle_upper_1_1_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_1_3_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.0737F, -2.0107F, -0.0737F, 0.1475F, 2.1607F, 0.1475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2209F, 11.622F, 5.2321F, 2.7453F, 0.7796F, -0.1646F));

        PartDefinition arm_sickle_upper_1_1_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_1_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3424F, -2.3364F, -0.3161F, 0.6848F, 2.4864F, 0.6321F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8739F, 9.624F, 4.4147F, -2.6001F, 0.7592F, 0.2206F));

        PartDefinition arm_sickle_upper_1_1_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_1_1_r1", CubeListBuilder.create().texOffs(17, 31).addBox(-0.6058F, -3.0551F, -0.5531F, 1.2116F, 3.2051F, 1.1062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0933F, 7.515F, 2.5756F, -2.2017F, 0.6697F, 0.3499F));

        PartDefinition arm_sickle_upper_1_1_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_1_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8692F, -2.7508F, -0.7375F, 1.7384F, 2.9008F, 1.475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3126F, 5.85F, 0.7364F, -2.0746F, 0.631F, 0.3809F));

        PartDefinition arm_sickle_upper_1_0_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_0_3_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.071F, -1.8766F, -0.071F, 0.1421F, 2.0266F, 0.1421F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.9081F, 11.506F, 2.0834F, 0.1646F, -0.7796F, 2.7453F));

        PartDefinition arm_sickle_upper_1_0_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_0_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3298F, -2.1789F, -0.3045F, 0.6597F, 2.3289F, 0.6089F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1496F, 9.652F, 1.7614F, -0.2206F, -0.7592F, -2.6001F));

        PartDefinition arm_sickle_upper_1_0_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_0_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5836F, -2.8458F, -0.5328F, 1.1671F, 2.9958F, 1.0656F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.443F, 7.695F, 1.037F, -0.3499F, -0.6697F, -2.2017F));

        PartDefinition arm_sickle_upper_1_0_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_1_0_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8373F, -2.5634F, -0.7104F, 1.6746F, 2.7134F, 1.4208F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7364F, 6.15F, 0.3126F, -0.3809F, -0.631F, -2.0746F));

        PartDefinition arm_sickle_upper_0_3_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_3_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0693F, -1.7928F, -0.0693F, 0.1386F, 1.9428F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.146F, -5.112F, -2.8387F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_3_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_3_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3217F, -2.0804F, -0.297F, 0.6435F, 2.2304F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.382F, -4.328F, 2.7234F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_3_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_3_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5692F, -2.7149F, -0.5197F, 1.1384F, 2.8649F, 1.0394F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.52F, -2.564F, 2.3832F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_3_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_3_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8167F, -2.4462F, -0.693F, 1.6334F, 2.5962F, 1.3859F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.05F, -0.8F, 2.2655F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_2_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_2_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0409F, -0.7233F, -0.0409F, 0.0819F, 0.8733F, 0.0819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3048F, 3.1284F, 0.0F, 0.0F, 0.0F, -2.8387F));

        PartDefinition arm_sickle_upper_0_2_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_2_2_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.1901F, -0.8237F, -0.1754F, 0.3801F, 0.9737F, 0.3509F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0312F, 2.5128F, 0.0F, 0.0F, 0.0F, 2.7234F));

        PartDefinition arm_sickle_upper_0_2_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_2_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3363F, -1.0451F, -0.307F, 0.6725F, 1.1951F, 0.614F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4156F, 1.863F, 0.0F, 0.0F, 0.0F, 2.3832F));

        PartDefinition arm_sickle_upper_0_2_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_2_0_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.4825F, -0.9513F, -0.4094F, 0.9649F, 1.1013F, 0.8187F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8F, 1.35F, 0.0F, 0.0F, 0.0F, 2.2655F));

        PartDefinition arm_sickle_upper_0_1_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_1_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0693F, -1.7928F, -0.0693F, 0.1386F, 1.9428F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.146F, 5.112F, 2.8387F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_1_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_1_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3217F, -2.0804F, -0.297F, 0.6435F, 2.2304F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.382F, 4.328F, -2.7234F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_1_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_1_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5692F, -2.7149F, -0.5197F, 1.1384F, 2.8649F, 1.0394F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.52F, 2.564F, -2.3832F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_1_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_1_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8167F, -2.4462F, -0.693F, 1.6334F, 2.5962F, 1.3859F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.05F, 0.8F, -2.2655F, 0.0F, 0.0F));

        PartDefinition arm_sickle_upper_0_0_3_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_0_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0664F, -1.6587F, -0.0664F, 0.1328F, 1.8087F, 0.1328F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.76F, 6.03F, 0.0F, 0.0F, 0.0F, 2.8387F));

        PartDefinition arm_sickle_upper_0_0_2_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_0_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3083F, -1.9228F, -0.2846F, 0.6166F, 2.0728F, 0.5692F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.04F, 4.41F, 0.0F, 0.0F, 0.0F, -2.7234F));

        PartDefinition arm_sickle_upper_0_0_1_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_0_1_r1", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5455F, -2.5055F, -0.4981F, 1.091F, 2.6555F, 0.9961F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.42F, 2.7F, 0.0F, 0.0F, 0.0F, -2.3832F));

        PartDefinition arm_sickle_upper_0_0_0_r1 = right_arm.addOrReplaceChild("arm_sickle_upper_0_0_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.7827F, -2.2588F, -0.6641F, 1.5653F, 2.4088F, 1.3282F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, 1.35F, 0.0F, 0.0F, 0.0F, -2.2655F));

        PartDefinition right_elbow = right_arm.addOrReplaceChild("right_elbow", CubeListBuilder.create().texOffs(9, 0).addBox(-1.0F, -0.1F, -1.0F, 2.0F, 14.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1396F, 0.0F, 0.0F));

        PartDefinition arm_sickle_fore_2_3_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_3_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0693F, -1.7928F, -0.0693F, 0.1386F, 1.9428F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5946F, 13.646F, -2.241F, -0.2064F, 0.879F, 2.7079F));

        PartDefinition arm_sickle_fore_2_3_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_3_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3217F, -2.0804F, -0.297F, 0.6435F, 2.2304F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.89F, 11.882F, -1.8973F, 0.2745F, 0.8542F, -2.5523F));

        PartDefinition arm_sickle_fore_2_3_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_3_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5692F, -2.7149F, -0.5197F, 1.1384F, 2.8649F, 1.0394F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3045F, 10.02F, -1.124F, 0.4238F, 0.7478F, -2.1386F));

        PartDefinition arm_sickle_fore_2_3_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_3_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8167F, -2.4462F, -0.693F, 1.6334F, 2.5962F, 1.3859F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.719F, 8.55F, -0.3507F, 0.457F, 0.7026F, -2.0107F));

        PartDefinition arm_sickle_fore_2_2_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_2_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0664F, -1.6587F, -0.0664F, 0.1328F, 1.8087F, 0.1328F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0866F, 13.53F, -4.2783F, -2.7079F, -0.879F, -0.2064F));

        PartDefinition arm_sickle_fore_2_2_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_2_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3083F, -1.9228F, -0.2846F, 0.6166F, 2.0728F, 0.5692F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.771F, 11.91F, -3.6311F, 2.5523F, -0.8542F, 0.2745F));

        PartDefinition arm_sickle_fore_2_2_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_2_1_r1", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5455F, -2.5055F, -0.4981F, 1.091F, 2.6555F, 0.9961F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0609F, 10.2F, -2.1751F, 2.1386F, -0.7478F, 0.4238F));

        PartDefinition arm_sickle_fore_2_2_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_2_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.7827F, -2.2588F, -0.6641F, 1.5653F, 2.4088F, 1.3282F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3507F, 8.85F, -0.719F, 2.0107F, -0.7026F, 0.457F));

        PartDefinition arm_sickle_fore_2_1_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_1_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0427F, -0.7743F, -0.0427F, 0.0854F, 0.9243F, 0.0854F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1918F, 10.4865F, 1.069F, 0.2064F, 0.879F, -2.7079F));

        PartDefinition arm_sickle_fore_2_1_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_1_2_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.1983F, -0.8835F, -0.1831F, 0.3967F, 1.0335F, 0.3661F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.924F, 9.8162F, 0.9384F, -0.2745F, 0.8542F, 2.5523F));

        PartDefinition arm_sickle_fore_2_1_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_1_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3509F, -1.1247F, -0.3204F, 0.7018F, 1.2747F, 0.6408F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3215F, 9.1086F, 0.6445F, -0.4238F, 0.7478F, 2.1386F));

        PartDefinition arm_sickle_fore_2_1_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_1_0_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-0.5035F, -1.0226F, -0.4272F, 1.0069F, 1.1726F, 0.8543F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.719F, 8.55F, 0.3507F, -0.457F, 0.7026F, 2.0107F));

        PartDefinition arm_sickle_fore_2_0_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_0_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0664F, -1.6587F, -0.0664F, 0.1328F, 1.8087F, 0.1328F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0866F, 13.53F, 4.2783F, 2.7079F, -0.879F, 0.2064F));

        PartDefinition arm_sickle_fore_2_0_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_0_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3083F, -1.9228F, -0.2846F, 0.6166F, 2.0728F, 0.5692F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.771F, 11.91F, 3.6311F, -2.5523F, -0.8542F, -0.2745F));

        PartDefinition arm_sickle_fore_2_0_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_0_1_r1", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5455F, -2.5055F, -0.4981F, 1.091F, 2.6555F, 0.9961F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0609F, 10.2F, 2.1751F, -2.1386F, -0.7478F, -0.4238F));

        PartDefinition arm_sickle_fore_2_0_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_2_0_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.7827F, -2.2588F, -0.6641F, 1.5653F, 2.4088F, 1.3282F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3507F, 8.85F, 0.719F, -2.0107F, -0.7026F, -0.457F));

        PartDefinition arm_sickle_fore_1_3_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_3_3_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.0737F, -2.0107F, -0.0737F, 0.1475F, 2.1607F, 0.1475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.729F, 11.072F, -4.2898F, -2.0369F, 1.3161F, 0.8892F));

        PartDefinition arm_sickle_fore_1_3_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_3_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3424F, -2.3364F, -0.3161F, 0.6848F, 2.4864F, 0.6321F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1465F, 9.074F, -3.6196F, 1.8566F, 1.2456F, -0.9863F));

        PartDefinition arm_sickle_fore_1_3_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_3_1_r1", CubeListBuilder.create().texOffs(17, 31).addBox(-0.6058F, -3.0551F, -0.5531F, 1.2116F, 3.2051F, 1.1062F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8357F, 6.965F, -2.1117F, 1.5382F, 1.0247F, -1.0528F));

        PartDefinition arm_sickle_fore_1_3_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_3_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8692F, -2.7508F, -0.7375F, 1.7384F, 2.9008F, 1.475F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5248F, 5.3F, -0.6038F, 1.4578F, 0.9478F, -1.0427F));

        PartDefinition arm_sickle_fore_1_2_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_2_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0438F, -0.8061F, -0.0438F, 0.0876F, 0.9561F, 0.0876F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9035F, 7.6353F, -1.6547F, -0.8892F, -1.3161F, -2.0369F));

        PartDefinition arm_sickle_fore_1_2_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_2_2_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.2033F, -0.921F, -0.1877F, 0.4067F, 1.071F, 0.3754F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6672F, 6.9308F, -1.4493F, 0.9863F, -1.2456F, 1.8566F));

        PartDefinition arm_sickle_fore_1_2_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_2_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3597F, -1.1744F, -0.3285F, 0.7195F, 1.3244F, 0.6569F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1355F, 6.1871F, -0.9871F, 1.0528F, -1.0247F, 1.5382F));

        PartDefinition arm_sickle_fore_1_2_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_2_0_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-0.5161F, -1.0671F, -0.4379F, 1.0323F, 1.2171F, 0.8759F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6038F, 5.6F, -0.5248F, 1.0427F, -0.9478F, 1.4578F));

        PartDefinition arm_sickle_fore_1_1_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_1_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0455F, -0.8571F, -0.0455F, 0.0909F, 1.0071F, 0.0909F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7424F, 7.4934F, 2.0044F, 2.0369F, 1.3161F, -0.8892F));

        PartDefinition arm_sickle_fore_1_1_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_1_2_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.2111F, -0.9808F, -0.1948F, 0.4221F, 1.1308F, 0.3897F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5211F, 6.7341F, 1.7498F, -1.8566F, 1.2456F, 0.9863F));

        PartDefinition arm_sickle_fore_1_1_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_1_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3734F, -1.254F, -0.341F, 0.7469F, 1.404F, 0.6819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.023F, 5.9327F, 1.1768F, -1.5382F, 1.0247F, 1.0528F));

        PartDefinition arm_sickle_fore_1_1_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_1_0_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-0.5358F, -1.1383F, -0.4546F, 1.0716F, 1.2883F, 0.9092F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5248F, 5.3F, 0.6038F, -1.4578F, 0.9478F, 1.0427F));

        PartDefinition arm_sickle_fore_1_0_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_0_3_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.071F, -1.8766F, -0.071F, 0.1421F, 2.0266F, 0.1421F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0241F, 10.956F, 3.4981F, 0.8892F, -1.3161F, 2.0369F));

        PartDefinition arm_sickle_fore_1_0_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_0_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3298F, -2.1789F, -0.3045F, 0.6597F, 2.3289F, 0.6089F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4022F, 9.102F, 2.9575F, -0.9863F, -1.2456F, -1.8566F));

        PartDefinition arm_sickle_fore_1_0_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_0_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5836F, -2.8458F, -0.5328F, 1.1671F, 2.9958F, 1.0656F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.003F, 7.145F, 1.7412F, -1.0528F, -1.0247F, -1.5382F));

        PartDefinition arm_sickle_fore_1_0_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_1_0_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8373F, -2.5634F, -0.7104F, 1.6746F, 2.7134F, 1.4208F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6038F, 5.6F, 0.5248F, -1.0427F, -0.9478F, -1.4578F));

        PartDefinition arm_sickle_fore_0_3_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_3_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0693F, -1.7928F, -0.0693F, 0.1386F, 1.9428F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5797F, 7.146F, -4.8618F, -2.7877F, 0.6119F, 0.1128F));

        PartDefinition arm_sickle_fore_0_3_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_3_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3217F, -2.0804F, -0.297F, 0.6435F, 2.2304F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3374F, 5.382F, -4.1162F, 2.6555F, 0.5974F, -0.1524F));

        PartDefinition arm_sickle_fore_0_3_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_3_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5692F, -2.7149F, -0.5197F, 1.1384F, 2.8649F, 1.0394F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7923F, 3.52F, -2.4385F, 2.2799F, 0.532F, -0.2492F));

        PartDefinition arm_sickle_fore_0_3_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_3_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8167F, -2.4462F, -0.693F, 1.6334F, 2.5962F, 1.3859F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2472F, 2.05F, -0.7608F, 2.1555F, 0.503F, -0.2744F));

        PartDefinition arm_sickle_fore_0_2_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_2_3_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.0409F, -0.7233F, -0.0409F, 0.0819F, 0.8733F, 0.0819F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.192F, 4.1284F, -0.7122F, -0.1128F, -0.6119F, -2.7877F));

        PartDefinition arm_sickle_fore_0_2_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_2_2_r1", CubeListBuilder.create().texOffs(12, 31).addBox(-0.1901F, -0.8237F, -0.1754F, 0.3801F, 0.9737F, 0.3509F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9318F, 3.5128F, -0.6277F, 0.1524F, -0.5974F, 2.6555F));

        PartDefinition arm_sickle_fore_0_2_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_2_1_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.3363F, -1.0451F, -0.307F, 0.6725F, 1.1951F, 0.614F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3463F, 2.863F, -0.4374F, 0.2492F, -0.532F, 2.2799F));

        PartDefinition arm_sickle_fore_0_2_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_2_0_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.4825F, -0.9513F, -0.4094F, 0.9649F, 1.1013F, 0.8187F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7608F, 2.35F, -0.2472F, 0.2744F, -0.503F, 2.1555F));

        PartDefinition arm_sickle_fore_0_1_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_1_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0693F, -1.7928F, -0.0693F, 0.1386F, 1.9428F, 0.1386F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5797F, 7.146F, 4.8618F, 2.7877F, 0.6119F, -0.1128F));

        PartDefinition arm_sickle_fore_0_1_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_1_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3217F, -2.0804F, -0.297F, 0.6435F, 2.2304F, 0.594F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3374F, 5.382F, 4.1162F, -2.6555F, 0.5974F, 0.1524F));

        PartDefinition arm_sickle_fore_0_1_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_1_1_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.5692F, -2.7149F, -0.5197F, 1.1384F, 2.8649F, 1.0394F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7923F, 3.52F, 2.4385F, -2.2799F, 0.532F, 0.2492F));

        PartDefinition arm_sickle_fore_0_1_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_1_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.8167F, -2.4462F, -0.693F, 1.6334F, 2.5962F, 1.3859F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2472F, 2.05F, 0.7608F, -2.1555F, 0.503F, 0.2744F));

        PartDefinition arm_sickle_fore_0_0_3_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_0_3_r1", CubeListBuilder.create().texOffs(121, 0).addBox(-0.0664F, -1.6587F, -0.0664F, 0.1328F, 1.8087F, 0.1328F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.527F, 7.03F, 1.4709F, 0.1128F, -0.6119F, 2.7877F));

        PartDefinition arm_sickle_fore_0_0_2_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_0_2_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-0.3083F, -1.9228F, -0.2846F, 0.6166F, 2.0728F, 0.5692F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8423F, 5.41F, 1.2484F, -0.1524F, -0.5974F, -2.6555F));

        PartDefinition arm_sickle_fore_0_0_1_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_0_1_r1", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5455F, -2.5055F, -0.4981F, 1.091F, 2.6555F, 0.9961F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3016F, 3.7F, 0.7478F, -0.2492F, -0.532F, -2.2799F));

        PartDefinition arm_sickle_fore_0_0_0_r1 = right_elbow.addOrReplaceChild("arm_sickle_fore_0_0_0_r1", CubeListBuilder.create().texOffs(113, 20).addBox(-0.7827F, -2.2588F, -0.6641F, 1.5653F, 2.4088F, 1.3282F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7608F, 2.35F, 0.2472F, -0.2744F, -0.503F, -2.1555F));

        PartDefinition hand_spurs = right_elbow.addOrReplaceChild("hand_spurs", CubeListBuilder.create().texOffs(60, 20).addBox(-3.8F, -0.9F, -1.8F, 4.3F, 5.3F, 3.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition mace_distal_lobe_r1 = hand_spurs.addOrReplaceChild("mace_distal_lobe_r1", CubeListBuilder.create().texOffs(98, 20).addBox(-1.5F, -1.0F, -1.5F, 3.3F, 2.2F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.1396F, -0.0873F));

        PartDefinition mace_outer_lobe_r1 = hand_spurs.addOrReplaceChild("mace_outer_lobe_r1", CubeListBuilder.create().texOffs(79, 20).addBox(-2.2F, -2.3F, -2.15F, 4.0F, 4.3F, 4.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 2.0F, 0.0F, 0.0F, -0.2094F, 0.1047F));

        PartDefinition mace_wrist_r1 = hand_spurs.addOrReplaceChild("mace_wrist_r1", CubeListBuilder.create().texOffs(45, 20).addBox(-2.3F, -2.0F, -1.3F, 3.15F, 3.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.1571F));

        PartDefinition left_stump = body.addOrReplaceChild("left_stump", CubeListBuilder.create().texOffs(98, 0).addBox(-0.3F, -1.3F, -1.15F, 1.5F, 2.3F, 2.3F, new CubeDeformation(0.0F))
        .texOffs(109, 0).addBox(0.95F, -1.05F, -0.85F, 0.5F, 1.85F, 1.7F, new CubeDeformation(0.0F)), PartPose.offset(4.2F, 1.5F, 0.0F));

        PartDefinition stump_torn_rear_r1 = left_stump.addOrReplaceChild("stump_torn_rear_r1", CubeListBuilder.create().texOffs(121, 0).addBox(0.0F, -0.9F, -0.15F, 0.8F, 1.3F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2F, -0.5F, 0.7F, -0.1047F, 0.0F, -0.1222F));

        PartDefinition stump_torn_lower_r1 = left_stump.addOrReplaceChild("stump_torn_lower_r1", CubeListBuilder.create().texOffs(116, 0).addBox(-0.15F, -0.1F, -0.7F, 1.0F, 0.7F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.8F, 0.0F, 0.0F, 0.0F, 0.1571F));

        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 9.0F, 0.0F));

        PartDefinition right_knee = right_leg.addOrReplaceChild("right_knee", CubeListBuilder.create().texOffs(9, 0).addBox(-1.0F, -0.1F, -1.0F, 2.0F, 14.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0524F, 0.0F, 0.0F));

        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 9.0F, 0.0F));

        PartDefinition left_knee = left_leg.addOrReplaceChild("left_knee", CubeListBuilder.create().texOffs(9, 0).addBox(-1.0F, -0.1F, -1.0F, 2.0F, 14.1F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0524F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
