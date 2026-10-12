package com.miophas.corruption.common.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;

/** Shared joint animation for dedicated corrupted humanoid appearances. */
public abstract class CorruptedHumanoidModel<T extends Mob> extends HumanoidModel<T> {
    private final ModelPart jaw;
    private final float jawRestAngle;
    protected final ModelPart rightElbow, leftElbow, rightKnee, leftKnee;
    private final ModelPart shoulderSickles, headSickles;
    private final ModelPart[] shoulderBlades = new ModelPart[3], headBlades = new ModelPart[3];
    private final ModelPart[] parts;
    private final Map<T, Motion> motions = new WeakHashMap<>();

    private static final class Motion {
        float lastAttack, lastAge, air;
        float landedAt = Float.NEGATIVE_INFINITY;
        boolean rightAttack = true, hasAttacked, grounded;

        Motion(Mob entity, float age) {
            grounded = entity.onGround();
            lastAge = age;
        }

        void update(Mob entity, float age, float attack, boolean airborne) {
            // State belongs to the appearance entity: rendering another remnant cannot steal a swing.
            if (attack > 0 && (lastAttack <= 0 || attack < lastAttack - 0.2F)) {
                if (hasAttacked) rightAttack = !rightAttack;
                hasAttacked = true;
            }
            lastAttack = attack;
            float elapsed = Mth.clamp(age - lastAge, 0, 4);
            if (entity.onGround() && !grounded && air > 0.1F) landedAt = age;
            grounded = entity.onGround();
            air += Mth.clamp((airborne ? 1 : 0) - air, -elapsed / 3, elapsed / 3);
            lastAge = age;
        }
    }

    protected CorruptedHumanoidModel(ModelPart root) {
        this(root, "right", "right");
    }

    protected CorruptedHumanoidModel(ModelPart root, String shoulderSide, String headSide) {
        super(root);
        jaw = head.getChild("jaw");
        jawRestAngle = jaw.getInitialPose().xRot;
        rightElbow = rightArm.getChild("right_elbow");
        leftElbow = leftArm.getChild("left_elbow");
        rightKnee = rightLeg.getChild("right_knee");
        leftKnee = leftLeg.getChild("left_knee");
        shoulderSickles = shoulderSide.equals("back") ? body.getChild("back_sickles")
                : (shoulderSide.equals("right") ? rightArm : leftArm).getChild(shoulderSide + "_shoulder_sickles");
        headSickles = head.getChild(headSide + "_head_sickles");
        for (int i = 0; i < 3; i++) {
            shoulderBlades[i] = shoulderSickles.getChild(shoulderSide.equals("back") ? "back_blade_" + i
                    : shoulderSide + "_shoulder_blade_" + i);
            headBlades[i] = headSickles.getChild(headSide + "_head_blade_" + i);
        }
        parts = root.getAllParts().toArray(ModelPart[]::new);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        for (ModelPart part : parts) part.resetPose();
        Motion motion = motions.computeIfAbsent(entity, key -> new Motion(key, ageInTicks));
        float attack = usesMeleeAnimation(entity) ? Mth.clamp(attackTime, 0, 1) : 0;
        boolean fluid = RemnantAnimationState.fluid(entity) || !entity.level().getFluidState(entity.blockPosition()).isEmpty();
        boolean seated = RemnantAnimationState.riding(entity, riding), crouched = RemnantAnimationState.crouching(entity, crouching);
        boolean airborne = !entity.onGround() && !seated && !fluid
                && (Math.abs(entity.getDeltaMovement().y) > 0.015 || motion.air > 0);
        motion.update(entity, ageInTicks, attack, airborne);

        float amplitude = animationAmplitude();
        float stride = Mth.clamp(limbSwingAmount, 0, 1.25F);
        float phase = limbSwing * 0.9F;
        float step = Mth.sin(phase), lift = Mth.cos(phase);
        float breath = Mth.sin(ageInTicks * 0.105F);
        float impact = Mth.sin(attack * Mth.PI);
        float bob = -Math.abs(lift) * stride * 0.7F * amplitude;
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;
        head.zRot = breath * 0.012F;
        body.xRot = 0.09F + stride * 0.20F + breath * 0.015F;
        body.yRot = step * stride * 0.10F;
        body.zRot = step * stride * 0.045F;

        float armStride = stride * (1 - impact * Math.min(amplitude - 1, 1) * .8F);
        rightArm.xRot = -0.30F - step * armStride * 0.55F * Math.min(amplitude, 1.6F) + breath * 0.025F;
        leftArm.xRot = -0.30F + step * armStride * 0.55F * Math.min(amplitude, 1.6F) - breath * 0.025F;
        rightArm.zRot += 0.16F + lift * stride * 0.025F;
        leftArm.zRot -= 0.16F + lift * stride * 0.025F;
        rightElbow.xRot -= Math.max(0, step) * stride * 0.35F * amplitude;
        leftElbow.xRot -= Math.max(0, -step) * stride * 0.35F * amplitude;
        rightLeg.xRot = step * stride * 1.05F * Math.min(amplitude, 1.4F);
        leftLeg.xRot = -step * stride * 1.05F * Math.min(amplitude, 1.4F);
        rightKnee.xRot += Math.max(0, lift) * stride * 0.8F * amplitude;
        leftKnee.xRot += Math.max(0, -lift) * stride * 0.8F * amplitude;

        // A separate, slow idle cycle fades out as movement, attacks or airborne poses take over.
        // Offset its phase per entity so a group of remnants does not breathe in lockstep.
        float idle = (1 - smooth(stride * 2)) * (1 - impact) * (1 - smooth(motion.air)) * amplitude;
        if (fluid || seated || crouched) idle = 0;
        float idleTime = ageInTicks + Math.floorMod(entity.getId(), 37) * 7.0F;
        float inhale = Mth.sin(idleTime * 0.07F);
        float weightShift = Mth.sin(idleTime * 0.036F + 0.8F);
        body.xRot += idle * inhale * 0.038F;
        body.yRot += idle * Mth.sin(idleTime * 0.025F) * 0.025F;
        body.zRot += idle * weightShift * 0.035F;
        head.xRot += idle * Mth.sin(idleTime * 0.046F + 1.2F) * 0.018F;
        head.yRot += idle * Mth.sin(idleTime * 0.04F + 0.7F) * 0.035F;
        rightArm.xRot += idle * Mth.sin(idleTime * 0.07F + 0.5F) * 0.065F;
        leftArm.xRot += idle * Mth.sin(idleTime * 0.07F + 1.4F) * 0.065F;
        rightArm.zRot += idle * inhale * 0.025F;
        leftArm.zRot -= idle * inhale * 0.025F;
        rightElbow.xRot -= idle * Mth.sin(idleTime * 0.07F + 0.8F) * 0.05F;
        leftElbow.xRot -= idle * Mth.sin(idleTime * 0.07F + 1.6F) * 0.05F;
        rightKnee.xRot += idle * weightShift * 0.045F;
        leftKnee.xRot -= idle * weightShift * 0.045F;

        if (fluid) {
            body.xRot = 0.20F;
            rightArm.xRot = -1.0F + step * 0.5F;
            leftArm.xRot = -1.0F - step * 0.5F;
            rightElbow.xRot = leftElbow.xRot = -0.70F;
            rightKnee.xRot = leftKnee.xRot = 0.55F;
        } else if (seated) {
            rightLeg.xRot = leftLeg.xRot = -1.15F;
            rightKnee.xRot = leftKnee.xRot = 1.1F;
            rightLeg.yRot = 0.22F;
            leftLeg.yRot = -0.22F;
            bob = 0;
        } else if (motion.air > 0) {
            float air = smooth(motion.air);
            boolean rising = entity.getDeltaMovement().y > 0.02;
            body.xRot = Mth.lerp(air, body.xRot, rising ? -0.08F : 0.26F);
            body.zRot *= 1 - air;
            rightArm.xRot = Mth.lerp(air, rightArm.xRot, -0.9F);
            leftArm.xRot = Mth.lerp(air, leftArm.xRot, -0.7F);
            rightArm.zRot += air * 0.25F;
            leftArm.zRot -= air * 0.25F;
            rightElbow.xRot = Mth.lerp(air, rightElbow.xRot, -0.80F);
            leftElbow.xRot = Mth.lerp(air, leftElbow.xRot, -0.65F);
            rightLeg.xRot = Mth.lerp(air, rightLeg.xRot, rising ? -0.70F * amplitude : 0.15F * amplitude);
            leftLeg.xRot = Mth.lerp(air, leftLeg.xRot, rising ? -0.35F : -0.15F);
            rightKnee.xRot = Mth.lerp(air, rightKnee.xRot, rising ? 1.15F * Math.min(amplitude,1.35F) : 0.55F * amplitude);
            leftKnee.xRot = Mth.lerp(air, leftKnee.xRot, rising ? 0.85F : 0.40F);
            bob *= 1 - air;
        }
        float landing = 1 - Mth.clamp((ageInTicks - motion.landedAt) / 6, 0, 1);
        landing *= landing;
        bob += landing * 1.8F * amplitude;
        body.xRot += landing * 0.16F;
        rightLeg.xRot -= landing * 0.35F;
        leftLeg.xRot -= landing * 0.35F;
        rightKnee.xRot += landing * 0.60F * amplitude;
        leftKnee.xRot += landing * 0.60F * amplitude;
        if (attack > 0) slash(motion.rightAttack, attack);
        if (crouched) {
            body.xRot += 0.22F;
            bob += 1.3F;
            rightKnee.xRot += 0.3F;
            leftKnee.xRot += 0.3F;
        }
        body.xRot -= Mth.sin(entity.hurtTime / 10.0F * Mth.PI) * 0.12F;
        jaw.xRot = jawRestAngle + breath * 0.025F + impact * 0.25F + idle * inhale * 0.03F;
        shoulderSickles.xRot += breath * 0.015F + step * stride * 0.025F + impact * 0.045F
                + idle * Mth.sin(idleTime * 0.07F - 0.5F) * 0.012F;
        headSickles.zRot += breath * 0.015F + step * stride * 0.02F
                + idle * Mth.sin(idleTime * 0.07F - 0.8F) * 0.012F;
        for (int i = 0; i < 3; i++) {
            shoulderBlades[i].zRot += idle * Mth.sin(idleTime * 0.07F + i * 0.45F) * 0.006F;
            headBlades[i].xRot += idle * Mth.sin(idleTime * 0.07F + i * 0.45F - 0.4F) * 0.010F;
        }

        body.xRot += restingHunch() * (1 - motion.air * 0.6F);
        body.zRot += restingSideLean() * (1 - motion.air * 0.6F);
        bob += restingHunch() * 2;
        // The exported humanoid roots are siblings. Rotate their attachment points with the torso.
        attach(body, 0, -12, 0, bob);
        body.y += 12;
        attach(head, 0, 0, 0, 0);
        attach(rightArm, -5, 2, 0, 0);
        attach(leftArm, 5, 2, 0, 0);
        rightArm.xRot += body.xRot;
        leftArm.xRot += body.xRot;
        rightArm.yRot += body.yRot;
        leftArm.yRot += body.yRot;
        rightArm.zRot += body.zRot;
        leftArm.zRot += body.zRot;
        head.xRot += body.xRot * 0.15F;
        rightLeg.y += bob;
        leftLeg.y += bob;
        hat.copyFrom(head);
    }

    private static float smooth(float value) {
        value = Mth.clamp(value, 0, 1);
        return value * value * (3 - 2 * value);
    }

    protected float animationAmplitude() { return 1; }

    private void slash(boolean right, float progress) {
        float twist, reach, sweep, spread, fold;
        if (progress < 0.25F) {
            float windup = smooth(progress / 0.25F);
            twist = -0.35F * windup;
            reach = -0.55F * windup;
            sweep = -0.70F * windup;
            spread = 0.55F * windup;
            fold = -0.40F * windup;
        } else if (progress < 0.55F) {
            float strike = smooth((progress - 0.25F) / 0.30F);
            twist = Mth.lerp(strike, -0.35F, 0.45F);
            reach = Mth.lerp(strike, -0.55F, -1.50F);
            sweep = Mth.lerp(strike, -0.70F, 0.65F);
            spread = Mth.lerp(strike, 0.55F, -0.45F);
            fold = Mth.lerp(strike, -0.40F, 0.30F);
        } else {
            float recover = 1 - smooth((progress - 0.55F) / 0.45F);
            twist = 0.45F * recover;
            reach = -1.50F * recover;
            sweep = 0.65F * recover;
            spread = -0.45F * recover;
            fold = 0.30F * recover;
        }
        float direction = right ? 1 : -1;
        ModelPart arm = right ? rightArm : leftArm;
        ModelPart elbow = right ? rightElbow : leftElbow;
        ModelPart guard = right ? leftArm : rightArm;
        float amplitude = Math.min(animationAmplitude(), 1.2F);
        body.yRot += twist * direction * amplitude;
        body.xRot += Mth.sin(progress * Mth.PI) * 0.12F;
        arm.xRot += reach * amplitude;
        arm.yRot += sweep * direction * amplitude;
        arm.zRot += spread * direction * amplitude;
        elbow.xRot += fold * amplitude;
        guard.xRot -= Mth.sin(progress * Mth.PI) * 0.30F;
    }

    private void attach(ModelPart part, float x, float y, float z, float bob) {
        float sx = Mth.sin(body.xRot), cx = Mth.cos(body.xRot);
        float sy = Mth.sin(body.yRot), cy = Mth.cos(body.yRot);
        float sz = Mth.sin(body.zRot), cz = Mth.cos(body.zRot);
        float ry = y * cx - z * sx, rz = y * sx + z * cx;
        float rx = x * cy + rz * sy;
        rz = -x * sy + rz * cy;
        part.x = rx * cz - ry * sz;
        part.y = rx * sz + ry * cz + bob;
        part.z = rz;
        if (part != body) {
            part.x += body.x;
            part.y += body.y;
            part.z += body.z;
        }
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poses) {
        super.translateToHand(side, poses);
        ModelPart elbow = side == HumanoidArm.RIGHT ? rightElbow : leftElbow;
        elbow.translateAndRotate(poses);
        // The vanilla item layer measures its grip from the shoulder, not the elbow.
        poses.translate(-elbow.x / 16.0F, -elbow.y / 16.0F, -elbow.z / 16.0F);
    }

    @Override
    public void copyPropertiesTo(HumanoidModel<T> target) {
        super.copyPropertiesTo(target);
        if (target instanceof CorruptedHumanoidArmorModel<?> armor) {
            armor.copyJoints(rightElbow, leftElbow, rightKnee, leftKnee);
        }
    }

    protected boolean usesMeleeAnimation(T entity) { return true; }
    protected float restingHunch() { return 0; }
    protected float restingSideLean() { return 0; }
}
