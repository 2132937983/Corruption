package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.common.entity.Agares;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Agares's rig. Its geometry is data (see {@link GeoModel}); this finds the bones that move and poses them.
 * As with Baal the legs take the angles the shared gait works out, and nothing turns the hips.
 *
 * In this space a positive X turn leans an upright bone forward, swings a hanging one back, dips one that
 * points forward and lifts one that points back. A positive Y turn swings the head to its right and the
 * tail to its left. The left wing is raised and unfolded by negative Z turns and the right by positive.
 */
public final class AgaresModel extends HierarchicalModel<Agares> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "agares"), "main");
    private static final String GEOMETRY = "agares";
    /**
     * A bite, as X turns added to the torso, the chest, the five neck bones and the head: with the jaws on
     * the ground close in, half way out, and raked out to full stretch. Found by search so that the snout
     * stays just above the ground and follows AgaresApi.biteForward; AgaresModelTest holds them to it.
     */
    static final float[] BITE_NEAR = {.5F, .2F, .849F, .884F, .395F, .081F, .046F, -1.2F}, BITE_MID = {.5F, .2F, .749F, .784F, .295F, -.019F, -.054F, -1.2F},
            BITE_FAR = {.7F, .5F, .3292F, .3572F, -.034F, -.2852F, -.3132F, -.5F};
    /** The same eight turns for its death: fallen forward, with its neck and head laid out along the ground. */
    static final float[] DEATH = {1.1F, .4F, .1245F, .142F, -.1025F, -.2595F, -.277F, -1.3F};

    private final ModelPart root, hips, torso, chest, head, jaw, tailFan;
    private final ModelPart[] all, dull, neck, tail, crest;
    /** Indexed by side, 0 left and 1 right, then from the body outwards. */
    private final ModelPart[][] legs = new ModelPart[2][], arms = new ModelPart[2][], wings = new ModelPart[2][], toes = new ModelPart[2][], primaries = new ModelPart[2][10];
    private final ModelPart[][][] fingers = new ModelPart[2][4][];

    public AgaresModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        List<ModelPart> unlit = new ArrayList<>();
        bones.forEach((name, part) -> { if (!name.endsWith("_glow")) unlit.add(part); });
        dull = unlit.toArray(ModelPart[]::new);
        hips = need(bones, "hips"); torso = need(bones, "torso"); chest = need(bones, "chest"); head = need(bones, "head"); jaw = need(bones, "jaw"); tailFan = need(bones, "tail_fan");
        neck = chain(bones, "neck", 5); tail = chain(bones, "tail", 14);
        crest = new ModelPart[7];
        for (int index = 0; index < crest.length; index++) crest[index] = need(bones, "crest_" + index);
        for (int side = 0; side < 2; side++) {
            String name = side == 0 ? "left_" : "right_";
            legs[side] = new ModelPart[]{need(bones, name + "leg"), need(bones, name + "shin"), need(bones, name + "tarsus"), need(bones, name + "foot")};
            toes[side] = new ModelPart[]{need(bones, name + "toe_0"), need(bones, name + "toe_1"), need(bones, name + "toe_2"), need(bones, name + "hallux")};
            arms[side] = new ModelPart[]{need(bones, name + "arm"), need(bones, name + "forearm"), need(bones, name + "hand")};
            wings[side] = new ModelPart[]{need(bones, name + "wing"), need(bones, name + "wing_forearm"), need(bones, name + "wing_hand")};
            for (int index = 0; index < 10; index++) primaries[side][index] = need(bones, name + "primary_" + index);
            for (int finger = 0; finger < 4; finger++) fingers[side][finger] = chain(bones, name + "finger_" + finger, 5);
        }
    }

    private static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Agares's geometry has no bone " + name);
        return bone;
    }

    /** A run of bones each inside the last, named as the build script names them: name, name_1, name_2... */
    private static ModelPart[] chain(Map<String, ModelPart> bones, String name, int length) {
        ModelPart[] run = new ModelPart[length];
        for (int index = 0; index < length; index++) run[index] = need(bones, index == 0 ? name : name + "_" + index);
        return run;
    }

    @Override public ModelPart root() { return root; }
    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    /** Draws only the pieces that glow, or everything again. The glowing pieces are the bones named _glow. */
    public void glowOnly(boolean only) { for (ModelPart part : dull) part.skipDraw = only; }

    @Override public void setupAnim(Agares entity, float swing, float amount, float age, float yaw, float pitch) { pose(AgaresPose.of(entity, age, yaw, pitch)); }

    private static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    public void pose(AgaresPose pose) {
        for (ModelPart part : all) part.resetPose();
        AgaresAttack attack = pose.attack();
        float age = pose.age(), walk = pose.walk(), ticks = pose.attackTicks(), collapse = pose.collapse(), alive = 1 - collapse;

        ColossusGait gait = AgaresApi.gait(pose.cycle(), walk, attack, ticks, collapse);
        hips.y = -gait.hip();
        for (int side = 0; side < 2; side++) {
            ColossusLeg leg = gait.leg(side == 0);
            legs[side][0].xRot = -leg.thigh() * Mth.DEG_TO_RAD; legs[side][1].xRot = -leg.shin() * Mth.DEG_TO_RAD;
            legs[side][2].xRot = -leg.cannon() * Mth.DEG_TO_RAD; legs[side][3].xRot = -leg.hoof() * Mth.DEG_TO_RAD;
            // A foot in the air clenches its talons.
            float clench = Mth.clamp(leg.lift() / 60, 0, 1);
            for (int toe = 0; toe < 3; toe++) toes[side][toe].xRot += .55F * clench;
            toes[side][3].xRot -= .45F * clench;
        }

        float screech = attack == AgaresAttack.SCREECH ? ease(ticks / 30) * (1 - ease((ticks - 84) / 36)) : 0;
        float rear = AgaresApi.biteRear(attack, ticks), down = AgaresApi.biteDown(attack, ticks), rake = AgaresApi.biteRake(attack, ticks);
        float open = AgaresApi.gustOpen(attack, ticks), beat = AgaresApi.gustBeat(attack, ticks), stomp = AgaresApi.stompRaise(attack, ticks);
        float sweep = AgaresApi.tailAngle(attack, ticks) * Mth.DEG_TO_RAD, stompSide = attack.left() ? 1 : -1;
        // The jaws gape through the wind-up and the rake, and snap shut as the rake ends.
        float gape = attack == AgaresAttack.BITE ? (ticks < attack.strikeTo() ? Math.max(rear, down) : down * Math.max(0, 1 - (ticks - attack.strikeTo()) / 4)) : 0;

        // Breathing, and the slow sway of something standing still.
        float breath = Mth.sin(age * .03F);
        // The rake passes through all three bite poses, as a curve through three points.
        float[] bite = new float[8];
        float near = (1 - rake) * (1 - 2 * rake), mid = 4 * rake * (1 - rake), far = rake * (2 * rake - 1);
        for (int index = 0; index < bite.length; index++) bite[index] = down * (near * BITE_NEAR[index] + mid * BITE_MID[index] + far * BITE_FAR[index]) + collapse * DEATH[index];
        // The idle sway of the neck and tail would pull a strike off its line, so it stops while they are weapons.
        float calm = alive * (1 - down), tailCalm = attack.tail() ? 0 : 1;

        // Walking: the shoulders turn against the hips and the body rocks over the planted foot.
        float stride = pose.cycle() * Mth.TWO_PI, step = Mth.cos(stride) * walk;
        torso.xRot += breath * .006F + walk * .04F - .12F * screech - .20F * rear + bite[0] + .10F * beat - .08F * open;
        torso.yRot += -step * .045F - sweep * .12F;
        torso.zRot += Mth.sin(stride) * walk * .02F - stompSide * .05F * stomp + Mth.sin(age * 1.7F) * .012F * collapse * alive;
        chest.xRot += breath * .01F - .10F * screech - .12F * rear + bite[1] + .12F * beat;
        for (int index = 0; index < neck.length; index++) {
            float wave = Mth.sin(age * .021F - index * .6F);
            neck[index].xRot += wave * .012F * calm + (index < 2 ? -.12F * screech - .14F * rear : .05F * screech) + bite[2 + index] + .16F * beat * (index < 3 ? 1 : 0);
            neck[index].yRot += Mth.sin(age * .013F - index * .5F) * .02F * calm;
        }
        head.xRot += -.50F * screech - .22F * rear + bite[7] - .25F * beat;
        head.zRot += Mth.sin(age * 2.4F) * .02F * screech;
        jaw.xRot += (.5F + .5F * Mth.sin(age * .028F)) * .06F + (.85F + Mth.sin(age * 2.1F) * .05F) * screech + .80F * gape + .45F * beat + .25F * collapse;
        for (int index = 0; index < crest.length; index++)
            crest[index].xRot += Mth.sin(age * .05F - index * .7F) * .03F + .30F * screech + .22F * rear + .25F * open;

        // The tail sways always, counters the stride, and is the weapon of a sweep.
        for (int index = 0; index < tail.length; index++) {
            float share = index == 0 ? .55F : index < 5 ? .1125F : 0;
            tail[index].yRot += (Mth.sin(age * .024F - index * .45F) * .03F + Mth.sin(stride - index * .4F) * walk * .035F) * tailCalm + sweep * share;
            tail[index].xRot += Mth.sin(age * .017F - index * .35F) * .012F + (index < 4 ? .05F * screech + .06F * down : 0) - (index >= 2 ? .05F * collapse : 0);
        }
        tailFan.xRot += Mth.sin(age * .03F) * .05F;

        for (int side = 0; side < 2; side++) {
            // Turning a left-hand bone by a negative Z lifts or opens it; the right mirrors that.
            float lift = side == 0 ? -1 : 1, outward = side == 0 ? 1 : -1;
            ModelPart wing = wings[side][0], forearm = wings[side][1], hand = wings[side][2];
            float flutter = Mth.sin(age * .016F + side * 1.3F);
            // For a gust the upper wing stays where it is and the rest unfolds above it, into a tall V; the beat brings it down.
            float raise = flutter * .025F + Mth.sin(stride * 2) * walk * .03F + .10F * screech + .18F * rear + .12F * down - .45F * beat + .12F * stomp - .85F * collapse;
            float unfold = .40F * screech + .15F * rear + .90F * open + .10F * beat - .25F * collapse;
            wing.zRot += lift * raise;
            // How far the wing is swept back: drawn back to open, thrown forward in the beat.
            wing.yRot += lift * (.12F * open - .95F * beat - .18F * screech + .10F * down);
            wing.xRot += .10F * screech - .25F * beat + .30F * collapse;
            forearm.zRot += lift * unfold; hand.zRot += lift * (unfold * 1.1F);
            for (int index = 0; index < 10; index++)
                primaries[side][index].zRot += lift * ((index - 4.5F) * .012F * (flutter + 2.2F * screech + 2.6F * open) - .02F * index * collapse);

            ModelPart arm = arms[side][0], fore = arms[side][1];
            arm.xRot += Mth.sin(age * .019F + side * 1.7F) * .03F + outward * step * .10F - .35F * screech + .30F * rear - .55F * down - .30F * collapse;
            arm.zRot -= outward * (.35F * screech + .18F * stomp + .25F * open);
            fore.xRot += -.30F * screech + .25F * rear - .35F * down + .35F * collapse;
            for (int finger = 0; finger < 4; finger++)
                for (int joint = 1; joint < 5; joint++) fingers[side][finger][joint].xRot += Mth.sin(age * .05F + finger * .9F + side) * .04F - .10F * screech - .12F * down;
        }

        float yaw = pose.headYaw() * Mth.DEG_TO_RAD * alive * (1 - down), pitch = pose.headPitch() * Mth.DEG_TO_RAD * alive * (1 - down);
        for (int index = 2; index < neck.length; index++) { neck[index].yRot += yaw * .15F; neck[index].xRot += pitch * .12F; }
        head.yRot += yaw * .55F; head.xRot += pitch * .64F;
    }
}
