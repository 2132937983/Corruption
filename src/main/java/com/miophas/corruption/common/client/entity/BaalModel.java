package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.common.entity.Baal;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Baal's rig. The legs are not animated here: they take the angles the gait works out, which is also what
 * places the leg hitboxes, so a hoof is drawn where it does its damage. Only the hips' height moves the
 * whole body; nothing rotates the hips, or the planted hoof would swing with them.
 *
 * In this space a positive X turn leans an upright bone forward and swings a hanging one back.
 */
public final class BaalModel extends HierarchicalModel<Baal> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "baal"), "main");
    /** How far a slam bows the body and swings the arm down, tuned so the hand lands where BaalApi.impact says. */
    static final float SLAM_BOW = .85F, SLAM_CHEST = .22F, SLAM_TWIST = .2F, SLAM_ARM = -1.6F, SLAM_FOREARM = .3F;

    private final ModelPart root, hips, torso, chest, core, neck, head, jaw;
    private final ModelPart[] parts, tail;
    /** Indexed by side, 0 left and 1 right, then from the body outwards. */
    private final ModelPart[][] legs = new ModelPart[2][], arms = new ModelPart[2][];
    private final ModelPart[] wings = new ModelPart[2];
    private final ModelPart[][][] fingers = new ModelPart[2][3][], wingFingers = new ModelPart[2][3][];

    public BaalModel(ModelPart root) {
        this.root = root;
        parts = root.getAllParts().toArray(ModelPart[]::new);
        hips = root.getChild("baal").getChild("hips");
        torso = hips.getChild("torso"); chest = torso.getChild("chest"); core = chest.getChild("core");
        neck = chest.getChild("neck"); head = neck.getChild("head"); jaw = head.getChild("jaw");
        tail = chain(hips, "tail", 10);
        for (int side = 0; side < 2; side++) {
            String name = side == 0 ? "left_" : "right_";
            ModelPart leg = hips.getChild(name + "leg"), shin = leg.getChild(name + "shin"), foot = shin.getChild(name + "foot");
            legs[side] = new ModelPart[]{leg, shin, foot, foot.getChild(name + "hoof")};
            ModelPart arm = chest.getChild(name + "arm"), forearm = arm.getChild(name + "forearm"), hand = forearm.getChild(name + "hand");
            arms[side] = new ModelPart[]{arm, forearm, hand};
            wings[side] = chest.getChild(name + "wing");
            for (int finger = 0; finger < 3; finger++) {
                fingers[side][finger] = chain(hand, name + "finger_" + finger, 4);
                wingFingers[side][finger] = chain(wings[side], name + "wing_finger_" + finger, 4);
            }
        }
    }

    /** A run of bones each inside the last, named as the build script names them: name, name_1, name_2... */
    private static ModelPart[] chain(ModelPart parent, String name, int length) {
        ModelPart[] bones = new ModelPart[length];
        for (int index = 0; index < length; index++) parent = bones[index] = parent.getChild(index == 0 ? name : name + "_" + index);
        return bones;
    }

    @Override public ModelPart root() { return root; }
    public static LayerDefinition createBodyLayer() { return BaalGeometry.createBodyLayer(); }

    @Override public void setupAnim(Baal entity, float swing, float amount, float age, float yaw, float pitch) { pose(BaalPose.of(entity, age, yaw, pitch)); }

    private static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    public void pose(BaalPose pose) {
        for (ModelPart part : parts) part.resetPose();
        BaalAttack attack = pose.attack();
        float age = pose.age(), walk = pose.walk(), ticks = pose.attackTicks(), collapse = pose.collapse(), alive = 1 - collapse;

        ColossusGait gait = BaalApi.gait(pose.cycle(), walk, attack, ticks, collapse);
        hips.y = -gait.hip();
        for (int side = 0; side < 2; side++) {
            ColossusLeg leg = gait.leg(side == 0);
            legs[side][0].xRot = -leg.thigh() * Mth.DEG_TO_RAD; legs[side][1].xRot = -leg.shin() * Mth.DEG_TO_RAD;
            legs[side][2].xRot = -leg.cannon() * Mth.DEG_TO_RAD; legs[side][3].xRot = -leg.hoof() * Mth.DEG_TO_RAD;
        }

        // Breathing, a pulsing heart, a slack jaw and a tail that never rests.
        float breath = Mth.sin(age * .045F);
        torso.xRot += breath * .008F; chest.xRot += breath * .012F; neck.xRot += Mth.sin(age * .021F) * .02F;
        core.xScale = core.yScale = core.zScale = 1 + .07F * Mth.sin(age * .16F);
        jaw.xRot += (.5F + .5F * Mth.sin(age * .033F)) * .07F;

        // Walking: the shoulders turn against the hips and each arm swings with the opposite leg.
        float stride = pose.cycle() * Mth.TWO_PI, step = Mth.cos(stride) * walk;
        torso.yRot -= step * .07F; torso.zRot += Mth.sin(stride) * walk * .025F; torso.xRot += walk * .05F;
        for (int index = 0; index < tail.length; index++) {
            tail[index].yRot += Mth.sin(age * .03F - index * .55F) * .05F + Mth.sin(stride - index * .5F) * walk * .05F;
            tail[index].xRot += Mth.sin(age * .021F - index * .4F) * .02F - (index >= 2 ? .09F * collapse : 0);
        }

        float roar = attack == BaalAttack.ROAR ? ease(ticks / 24) * (1 - ease((ticks - 70) / 30)) : 0;
        float raise = BaalApi.slamRaise(attack, ticks), strike = BaalApi.slamStrike(attack, ticks), stomp = BaalApi.stompRaise(attack, ticks);
        float attackSide = attack.left() ? 1 : -1;

        torso.xRot += -.22F * roar - .10F * raise + SLAM_BOW * strike + .75F * collapse;
        chest.xRot += -.14F * roar + SLAM_CHEST * strike + .25F * collapse;
        // Drawing the arm back turns that shoulder away; the blow brings it through.
        torso.yRot += attackSide * (SLAM_TWIST * strike - .22F * raise) + Mth.sin(age * 2.3F) * .012F * roar;
        torso.zRot += Mth.sin(age * 1.7F) * .015F * collapse * alive;
        neck.xRot += -.30F * roar - .25F * strike + .45F * collapse;
        head.xRot += -.45F * roar - .20F * strike + .35F * collapse;
        jaw.xRot += (.80F + Mth.sin(age * 1.9F) * .05F) * roar + .25F * strike + .35F * collapse;
        if (attack.stomp()) {
            // It leans off the hoof it lifts, then sags as the hoof lands.
            float settle = ticks > attack.impactTick() ? Math.max(0, 1 - (ticks - attack.impactTick()) / 20) : 0;
            torso.zRot -= attackSide * .07F * stomp; chest.zRot -= attackSide * .04F * stomp; torso.xRot += .12F * settle;
        }

        for (int side = 0; side < 2; side++) {
            float outward = side == 0 ? 1 : -1;
            boolean striking = attack.slam() && attack.left() == (side == 0);
            ModelPart arm = arms[side][0], forearm = arms[side][1];
            arm.xRot += Mth.sin(age * .019F + side * 1.7F) * .025F + outward * step * .22F + .30F * roar - collapse;
            arm.zRot -= outward * (.65F * roar + .22F * stomp);
            // Dying, it folds its forearms under it so its hands come to rest on the ground instead of through it.
            forearm.xRot += -.35F * roar - .60F * collapse;
            if (striking) {
                arm.xRot += -2.5F * raise + SLAM_ARM * strike;
                forearm.xRot += .25F * raise + SLAM_FOREARM * strike;
            } else if (attack.slam()) { arm.xRot += .5F * strike; arm.zRot -= outward * .2F * strike; }
            wings[side].zRot += outward * (Mth.sin(age * .017F + side) * .05F + .30F * roar + .45F * collapse);
            wings[side].xRot += Mth.sin(age * .013F + side * 2) * .03F + .25F * roar - .5F * collapse;
            for (int finger = 0; finger < 3; finger++) {
                fingers[side][finger][0].zRot -= (finger - 1) * .2F * roar;
                for (int joint = 1; joint < 4; joint++) {
                    fingers[side][finger][joint].xRot += Mth.sin(age * .05F + finger * .9F + side) * .04F;
                    wingFingers[side][finger][joint].xRot += Mth.sin(age * .04F + finger * 1.1F + joint * .5F) * .05F - .12F * roar;
                }
            }
        }

        float yaw = pose.headYaw() * Mth.DEG_TO_RAD * alive, pitch = pose.headPitch() * Mth.DEG_TO_RAD * alive;
        neck.yRot += yaw * .4F; head.yRot += yaw * .6F; neck.xRot += pitch * .35F; head.xRot += pitch * .65F;
    }
}
