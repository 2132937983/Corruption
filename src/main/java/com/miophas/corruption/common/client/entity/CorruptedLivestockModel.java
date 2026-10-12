package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptedMobApi;
import com.miophas.corruption.api.corruption.CorruptedMobDefinition;
import com.miophas.corruption.api.corruption.CorruptedMobFamily;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Six species-specific static meshes share jointed gait and remnant-driven animation state. */
public final class CorruptedLivestockModel extends HierarchicalModel<Mob> {
    public enum Kind {
        PIG("pig"), COW("cow"), SHEEP("sheep"), CHICKEN("chicken"), HORSE("horse"), DONKEY("donkey");
        private static final Map<String, Kind> BY_ID = new java.util.HashMap<>();
        static { for (var kind : values()) BY_ID.put(kind.id, kind); }
        public final String id;
        public final ModelLayerLocation layer;
        private final CorruptedMobDefinition definition;
        Kind(String id) {
            this.id = id;
            definition = CorruptedMobApi.require(CorruptedMobFamily.LIVESTOCK, id);
            layer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_" + id), definition.layerVariant());
        }
        public LayerDefinition createLayer() {
            return switch (this) {
                case PIG -> CorruptedLivestockGeometry.pig();
                case COW -> CorruptedLivestockGeometry.cow();
                case SHEEP -> CorruptedLivestockGeometry.sheep();
                case CHICKEN -> CorruptedLivestockGeometry.chicken();
                case HORSE -> CorruptedLivestockGeometry.horse();
                case DONKEY -> CorruptedLivestockGeometry.donkey();
            };
        }
        public static Kind of(LivingEntity entity) { return CorruptedMobKinds.of(CorruptedMobFamily.LIVESTOCK, entity, BY_ID); }
    }

    private final Kind kind;
    private final ModelPart root, body, neck, head, jaw, tail, ornament;
    private final ModelPart leftWing, rightWing;
    private final ModelPart[] parts, legs, knees, feet, fingers;
    private final Vector3f[] restUpper, restLower, restFeet;
    private final Vector3f[] hips, footTargets;
    private final float[] legReach;
    private final Map<Mob, Motion> motions = new WeakHashMap<>();

    private static final class Motion {
        float age, air, previousAttack, landed = Float.NEGATIVE_INFINITY;
        int attackIndex;
        boolean ground;
        Motion(Mob entity, float age) {this.age = age; ground = entity.onGround();}
        void update(Mob entity, float now, boolean airborne, float attack) {
            float elapsed = Mth.clamp(now - age, 0, 4);
            if (entity.onGround() && !ground && air > .1F) landed = now;
            ground = entity.onGround();
            air += Mth.clamp((airborne ? 1 : 0) - air, -elapsed / 3, elapsed / 3);
            if (attack > 0 && previousAttack <= 0) attackIndex++;
            previousAttack = attack; age = now;
        }
    }

    public CorruptedLivestockModel(ModelPart root, Kind kind) {
        this.root = root; this.kind = kind;
        body = root.getChild("body"); neck = body.getChild("neck"); head = neck.getChild("head");
        jaw = head.getChild("jaw"); tail = body.getChild("tail"); ornament = body.getChild("ornament");
        leftWing = kind == Kind.CHICKEN ? body.getChild("left_wing") : null;
        rightWing = kind == Kind.CHICKEN ? body.getChild("right_wing") : null;
        int count = kind == Kind.CHICKEN ? 2 : 4;
        legs = new ModelPart[count]; knees = new ModelPart[count]; feet = new ModelPart[count];
        restUpper = new Vector3f[count]; restLower = new Vector3f[count]; restFeet = new Vector3f[count];
        hips = new Vector3f[count]; footTargets = new Vector3f[count]; legReach = new float[count];
        for (int i = 0; i < count; i++) {
            String name = "leg_" + i;
            legs[i] = body.getChild(name); knees[i] = legs[i].getChild(name + "_lower"); feet[i] = knees[i].getChild(name + "_foot");
            restUpper[i] = new Vector3f(knees[i].x, knees[i].y, knees[i].z);
            restLower[i] = new Vector3f(feet[i].x, feet[i].y, feet[i].z);
            restFeet[i] = new Vector3f(body.x + legs[i].x, body.y + legs[i].y, body.z + legs[i].z).add(restUpper[i]).add(restLower[i]);
            hips[i] = new Vector3f(legs[i].x, legs[i].y, legs[i].z); footTargets[i] = new Vector3f();
            legReach[i] = restUpper[i].length() + restLower[i].length() - .02F;
        }
        fingers = new ModelPart[3];
        for (int i = 0; i < 3; i++) fingers[i] = ornament.getChild("finger_" + i);
        parts = root.getAllParts().toArray(ModelPart[]::new);
    }

    @Override public ModelPart root() {return root;}

    @Override public void setupAnim(Mob entity, float swing, float amount, float age, float yaw, float pitch) {
        for (ModelPart part : parts) part.resetPose();
        Motion motion = motions.computeIfAbsent(entity, key -> new Motion(key, age));
        boolean fluid = RemnantAnimationState.fluid(entity), seated = RemnantAnimationState.riding(entity, riding);
        boolean crouched = RemnantAnimationState.crouching(entity, false), bird = kind == Kind.CHICKEN;
        boolean airborne = !entity.onGround() && !fluid && !seated
                && (Math.abs(entity.getDeltaMovement().y) > .015 || motion.air > 0);
        float stride = Mth.clamp(amount, 0, 1), attack = Mth.clamp(attackTime, 0, 1);
        motion.update(entity, age, airborne, attack);
        float t = age + Math.floorMod(entity.getUUID().hashCode(), 53), breath = Mth.sin(t * .065F);
        float idle = (1 - smooth(Mth.clamp(stride * 2, 0, 1))) * (attack > 0 ? 0 : 1) * (1 - motion.air);
        if (fluid || seated || crouched) idle = 0;
        float shift = Mth.sin(t * .035F + .6F), side = motion.attackIndex % 2 == 0 ? 1 : -1;
        float windup = attack > 0 && attack < .28F ? smooth(attack / .28F) : 0;
        float strike = attack >= .28F && attack < .62F ? smooth((attack - .28F) / .34F)
                : attack >= .62F ? 1 - smooth((attack - .62F) / .38F) : 0;
        if (attack >= .28F && attack < .62F) windup = 1 - strike;
        float landing = 1 - Mth.clamp((age - motion.landed) / 7, 0, 1);
        body.y += idle * (.55F + breath * .30F) + stride * (.70F + Mth.sin(swing * 1.6F) * .16F)
                + landing * 1.35F + strike * 1.05F + (crouched ? 1 : 0);
        body.x += shift * idle * .32F;
        body.xRot += idle * breath * .025F - windup * .12F + strike * .17F + stride * .055F
                + motion.air * (entity.getDeltaMovement().y > .02 ? -.10F : .12F);
        body.zRot += shift * idle * .055F + Mth.sin(swing * .8F) * stride * .05F;
        body.yRot += side * (-windup * .11F + strike * .16F);
        body.yScale = 1 + idle * breath * .012F;
        neck.xRot += idle * Mth.sin(t * .053F) * .065F + windup * .18F - strike * .32F;
        neck.yRot += shift * idle * .045F + side * (windup * .13F - strike * .18F);
        head.yRot += Mth.clamp(yaw * Mth.DEG_TO_RAD, -.9F, .9F) - body.yRot - neck.yRot;
        head.xRot += Mth.clamp(pitch * Mth.DEG_TO_RAD, -.45F, .5F) + idle * Mth.sin(t * .048F) * .09F;
        head.zRot += shift * idle * .06F + side * strike * .11F;
        jaw.xRot += .035F + idle * (.10F + .075F * Mth.sin(t * .065F - .9F)) + windup * .40F + strike * .18F;
        tail.yRot += shift * .27F + stride * Mth.sin(swing * .8F) * .40F;
        ornament.xRot += idle * Mth.sin(t * .065F - 1.2F) * .09F + landing * .12F - motion.air * .075F;
        int layout = Math.floorMod(entity.getUUID().hashCode(), 3);
        if (layout == 1) ornament.x = -ornament.x;
        ornament.yRot += (layout - 1) * .12F + shift * idle * .06F;
        for (int i = 0; i < 3; i++) fingers[i].xRot += idle * Mth.sin(t * .065F - 1.4F + i * .4F) * .055F + windup * .06F;
        if (bird) {
            float flutter = motion.air * (.70F + Mth.sin(t * .55F) * .48F) + stride * .22F;
            leftWing.zRot -= flutter + windup * .38F - strike * .26F;
            rightWing.zRot += flutter + windup * .30F - strike * .34F;
            leftWing.xRot += breath * idle * .08F - strike * .38F;
            rightWing.xRot += Mth.sin(t * .065F - .5F) * idle * .10F - strike * .44F;
        }
        for (int i = 0; i < legs.length; i++) {
            boolean front = bird || i % 2 == 0;
            float phase = swing * (bird ? 1.15F : .85F) + (bird ? i * Mth.PI : i == 0 || i == 3 ? 0 : Mth.PI);
            float lift = Math.max(0, Mth.sin(phase)) * stride * (bird ? 1.4F : Math.min(2.5F, legReach[i] * .36F));
            Vector3f target = footTargets[i].set(restFeet[i]);
            target.z += Mth.cos(phase) * stride * (bird ? 1.5F : Math.min(2.8F, legReach[i] * .42F));
            float airLift = motion.air * (entity.getDeltaMovement().y > .02 ? bird ? 2.5F : Math.min(4.5F, legReach[i] * .60F) : .80F);
            target.y -= lift + airLift + (front ? windup * (bird ? 1.0F : 1.5F) : strike * .55F);
            if (fluid) {target.y -= 1 + Mth.sin(t * .16F + i) * .4F; target.z += Mth.cos(t * .16F + i) * 1.1F;}
            if (seated) target.y -= bird ? 1.5F : 2.7F;
        }
        if (!fluid && !seated && motion.air < .05F) RemnantAnimationGrounding.lowerBody(body, hips, footTargets, legReach);
        Matrix4f inverse = new Matrix4f().translation(body.x, body.y, body.z).rotateZYX(body.zRot, body.yRot, body.xRot).scale(body.xScale, body.yScale, body.zScale).invert();
        Quaternionf bodyRotation = new Quaternionf().rotationZYX(body.zRot, body.yRot, body.xRot);
        for (int i = 0; i < legs.length; i++) {
            Vector3f target = new Vector3f(footTargets[i]);
            inverse.transformPosition(target).sub(legs[i].x, legs[i].y, legs[i].z);
            poseLeg(i, target, bird || i % 2 == 0, bodyRotation);
        }
    }

    private static float smooth(float value) {return value * value * (3 - 2 * value);}
    private void poseLeg(int index, Vector3f target, boolean front, Quaternionf bodyRotation) {
        float upper = restUpper[index].length(), lower = restLower[index].length();
        float distance = Mth.clamp(target.length(), Math.abs(upper - lower) + .01F, upper + lower - .005F);
        Vector3f direction = new Vector3f(target).normalize(), goal = new Vector3f(direction).mul(distance);
        float along = (upper * upper - lower * lower + distance * distance) / (2 * distance);
        float out = (float) Math.sqrt(Math.max(0, upper * upper - along * along));
        Vector3f pole = new Vector3f(index < (legs.length == 2 ? 1 : 2) ? -.08F : .08F, 0, front ? 1 : -1);
        pole.sub(new Vector3f(direction).mul(pole.dot(direction))).normalize();
        Vector3f joint = new Vector3f(direction).mul(along).add(pole.mul(out));
        Quaternionf upperRotation = new Quaternionf().rotationTo(new Vector3f(restUpper[index]).normalize(), new Vector3f(joint).normalize());
        Vector3f localLower = new Quaternionf(upperRotation).conjugate().transform(new Vector3f(goal).sub(joint)).normalize();
        Quaternionf lowerRotation = new Quaternionf().rotationTo(new Vector3f(restLower[index]).normalize(), localLower);
        rotation(legs[index], upperRotation); rotation(knees[index], lowerRotation);
        rotation(feet[index], new Quaternionf(bodyRotation).mul(upperRotation).mul(lowerRotation).conjugate());
    }
    private static void rotation(ModelPart part, Quaternionf rotation) {
        Vector3f angles = rotation.getEulerAnglesZYX(new Vector3f());
        part.xRot = angles.x; part.yRot = angles.y; part.zRot = angles.z;
    }

}
