package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Seere;
import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.SeereRules.Skill;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Seere, the winged horse. His geometry is data (see {@link GeoModel}, made by gamemodels/seere/seere_model.py); this
 * finds the bones that move and poses them. The shape is plain and the detail is all in his texture: his wings are each
 * three pieces, a leading edge and a thin plane of feathers whose outlines are transparent texels.
 *
 * <p>His wings are built spread straight out to either side; at rest they lie folded along his flanks (arm back,
 * forearm forward over it, hand back over that), and the more he flies ({@code flight}, 0 on the ground to
 * 1 in the air, eased by the renderer) the wider they open and the harder they beat. How far through its beat a wing
 * is ({@code flap}) is wound on frame by frame by the renderer ({@link SeereWinding}), so a change of pace never jumps
 * it; his gallop goes by the distance he has run, as any creature's walk does.
 *
 * <p>A skill goes on being posed after it is over, until he begins another, and every pose eases back to rest by itself.
 * As in every model of this space, -Y is up and -Z before him: a positive X turn swings a hanging leg's hoof back, and
 * tips his body nose down. {@code side} is 1 for his left (built at +X, here -X), -1 for his right: a wing folds back
 * with a positive Y turn times its side, and rises with a positive Z turn times its side.
 */
public final class SeereModel extends HierarchicalModel<Seere> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "seere"), "main");
    static final String GEOMETRY = "seere";
    /**
     * How his wings fold at rest. The arm swings back along him and turns about its own length, so that its feathers
     * hang down his flank, a little out at the foot; then the forearm shuts forward over the outside of it as a book
     * shuts (a half turn about the line of its feathers, so they still hang down), and the hand back over that. Folded
     * in the plane of the wing, as a bird folds, every other piece's feathers would stand up over his back instead.
     */
    static final float FOLD_SWEEP = Mth.HALF_PI, FOLD_PITCH = -Mth.HALF_PI, FOLD_SPLAY = .07F, FOLD_SHUT = Mth.PI;
    /** How far out from his flank a folded wing lies, and each piece of it out over the last, in pixels: its feathers are thin planes, so a little does. */
    static final float FOLD_OUT = 1.2F, FOLD_FOREARM_OUT = .9F, FOLD_HAND_OUT = .9F;
    /** How much shorter the feathers of the forearm and the hand lie, folded (they close up as a fan does). */
    static final float FOLD_TUCK = .3F;
    /** How long a stride of his gallop is, in the distance he has run, and how high he bounds. */
    static final float STRIDE = .42F, BOUND = 1.3F;

    // Seen by SeereModelTest, which poses the baked model and looks at where the bones went.
    final ModelPart root, body, neck, neckUpper, head, jaw, mane, maneUpper, tail, tailMiddle, tailEnd;
    final ModelPart[] all;
    /** Left then right: upper leg, lower leg, hoof. */
    final ModelPart[] front = new ModelPart[2], frontLower = new ModelPart[2], frontHoof = new ModelPart[2];
    final ModelPart[] hind = new ModelPart[2], hindLower = new ModelPart[2], hindHoof = new ModelPart[2];
    /** Left then right: arm, forearm, hand. */
    final ModelPart[] wing = new ModelPart[2], wingFore = new ModelPart[2], wingHand = new ModelPart[2];
    /** The winding of the Seere about to be drawn, set by the renderer; with none, his wings beat at a steady pace. */
    SeereWinding winding;

    public SeereModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        body = need(bones, "body"); neck = need(bones, "neck"); neckUpper = need(bones, "neck_1"); head = need(bones, "head"); jaw = need(bones, "jaw");
        mane = need(bones, "mane"); maneUpper = need(bones, "mane_1"); tail = need(bones, "tail"); tailMiddle = need(bones, "tail_1"); tailEnd = need(bones, "tail_2");
        String[] sides = {"left", "right"};
        for (int side = 0; side < 2; side++) {
            front[side] = need(bones, "leg_front_" + sides[side]);
            frontLower[side] = need(bones, "leg_front_" + sides[side] + "_1");
            frontHoof[side] = need(bones, "hoof_front_" + sides[side]);
            hind[side] = need(bones, "leg_hind_" + sides[side]);
            hindLower[side] = need(bones, "leg_hind_" + sides[side] + "_1");
            hindHoof[side] = need(bones, "hoof_hind_" + sides[side]);
            wing[side] = need(bones, "wing_" + sides[side]);
            wingFore[side] = need(bones, "wing_" + sides[side] + "_1");
            wingHand[side] = need(bones, "wing_" + sides[side] + "_2");
        }
    }

    private static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Seere's geometry has no bone " + name);
        return bone;
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    @Override public void setupAnim(Seere seere, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - seere.tickCount;
        float dying = seere.deathTime > 0 ? Mth.clamp((seere.deathTime + partial) / SeereRules.DEATH_TICKS, 0, 1) : 0;
        float flight = winding != null ? winding.flight : seere.mode() == SeereRules.Mode.SKY ? 1 : 0;
        float flap = winding != null ? winding.flap : age * .3F;
        pose(age, swing, amount, seere.posedSkill(), seere.skillTicks(partial), flight, flap, dying, seere.getId());
    }

    /**
     * @param age    ticks, for the motion that never stops
     * @param swing  how far he has run, as any creature's walk is counted, and {@code amount} how fast he runs now
     * @param skill  what he is doing (or last did), and {@code ticks} since he began it
     * @param flight how much he flies, 0 on the ground to 1 in the air
     * @param flap   how far his wings are through their beats, in radians
     * @param dying  0 alive to 1 dead
     * @param phase  his own, so that no two of him move together
     */
    public void pose(float age, float swing, float amount, Skill skill, float ticks, float flight, float flap, float dying, int phase) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 4.7F, run = Mth.clamp(amount, 0, 1) * (1 - flight), ground = 1 - flight;

        // ---- at rest, and his gallop ----
        float breath = Mth.sin(time * .08F);
        body.y += .3F * breath * ground;
        neck.xRot += .04F * Mth.sin(time * .05F) * ground;
        head.xRot += .05F * Mth.sin(time * .05F + 1);
        tail.zRot += .15F * Mth.sin(time * .07F) * ground;
        tailMiddle.zRot += .2F * Mth.sin(time * .07F - .8F) * ground;
        float stride = swing * STRIDE;
        // A rotary gallop: the hind legs a little apart, the forelegs half a stride after them.
        float[] hindAt = {0, .35F}, frontAt = {Mth.PI, Mth.PI + .35F};
        for (int side = 0; side < 2; side++) {
            float h = Mth.sin(stride + hindAt[side]), f = Mth.sin(stride + frontAt[side]);
            hind[side].xRot += .75F * h * run;
            hindLower[side].xRot += -.6F * Math.max(0, -h) * run;
            front[side].xRot += .8F * f * run;
            frontLower[side].xRot += 1.1F * Math.max(0, f) * run;
            frontHoof[side].xRot += .4F * Math.max(0, f) * run;
        }
        body.y -= BOUND * Math.abs(Mth.sin(stride)) * run;
        body.xRot += .07F * Mth.sin(stride + .5F) * run;
        neck.xRot += .12F * Mth.sin(stride + 1.2F) * run + .1F * run;
        tail.xRot += .9F * run;
        mane.xRot += .3F * run;
        maneUpper.xRot += .3F * run;

        // ---- flight: legs drawn up, neck stretched out, tail streaming, wings beating ----
        for (int side = 0; side < 2; side++) {
            front[side].xRot += -.7F * flight;
            frontLower[side].xRot += 1.7F * flight;
            frontHoof[side].xRot += .5F * flight;
            hind[side].xRot += .9F * flight;
            hindLower[side].xRot += .5F * flight;
        }
        neck.xRot += .45F * flight;
        head.xRot -= .3F * flight;
        tail.xRot += 1.15F * flight;
        tailMiddle.xRot += .25F * flight * Mth.sin(time * .2F);
        mane.xRot += .5F * flight;
        maneUpper.xRot += .5F * flight;

        // How far open his wings are (0 folded, 1 straight out), how high they are lifted, how hard they beat, how much
        // further back they are swept, and how far their feathers are turned back up level from hanging down.
        float open = .08F * run + flight, lift = 0, beat = flight * .75F, sweep = 0, level = 0;

        // ---- what he is doing ----
        float windup = windup(skill), length = length(skill);
        float rear = 0, crouch = 0, lunge = 0, nose = 0, paw = 0, mouth = 0;
        switch (skill) {
            case HOOVES -> {
                // Rearing up, forehooves pawing the air, then down on them.
                rear = ease(ticks / (windup - 2)) * (1 - ease((ticks - windup) / 4));
                paw = rear;
                mouth = rear;
                open = Math.max(open, .85F * rear);
                lift += .55F * rear;
            }
            case SPIKES -> {
                // Rearing high, wings flung up, then down on the ground with all his weight.
                rear = 1.2F * ease(ticks / (windup - 4)) * (1 - ease((ticks - windup + 1) / 3));
                paw = .6F * rear;
                mouth = rear;
                open = Math.max(open, rear);
                lift += .8F * rear;
                crouch = .7F * ease((ticks - windup) / 2) * (1 - ease((ticks - windup - 4) / 8));
            }
            case BLINKS -> {
                // Crouched to spring, then flung out long between his blinks.
                crouch = ease(ticks / windup) * (1 - ease((ticks - windup) / 3));
                lunge = ease((ticks - windup) / 2) * (1 - ease((ticks - length) / 6));
                open = Math.max(open, .5F * lunge);
                sweep = .35F * lunge;
                level = .45F * lunge;
            }
            case DIVE -> {
                // Climbing on great beats, then folded tight and falling like a stone, forehooves out for the ground; then landing.
                float climbing = ease(ticks / 6) * (1 - ease((ticks - windup) / 3));
                float diving = ease((ticks - windup) / 3) * (1 - ease((ticks - length) / 6));
                beat += .5F * climbing;
                lift += .5F * climbing;
                // Swept back close and flat, as a falcon stoops.
                open = Mth.lerp(diving, Math.max(open, climbing), .45F);
                sweep = .35F * diving;
                level = .6F * diving;
                beat *= 1 - diving;
                for (int side = 0; side < 2; side++) {
                    front[side].xRot += -.6F * diving;
                    frontLower[side].xRot -= 1.2F * diving;
                    hind[side].xRot += .3F * diving;
                }
                neck.xRot += .2F * diving;
            }
            case BEAMS -> {
                // Hanging in the air on slow, mighty beats, breast to the quarry, head up.
                float casting = ease(ticks / 10) * (1 - ease((ticks - length) / 8));
                beat += .15F * casting;
                lift += .15F * casting;
                rear = .35F * casting;
                paw = .5F * casting;
                neck.xRot -= .35F * casting;
                if (ticks >= windup) body.xRot -= .03F * casting * Mth.sin((ticks - windup) * 1.6F);
            }
            case TAKE_OFF -> {
                // Crouched, then off the ground with his wings beating down.
                crouch = ease(ticks / 4) * (1 - ease((ticks - 6) / 3));
                rear = .4F * ease((ticks - 4) / 6) * (1 - ease((ticks - length) / 8));
                float rising = ease(ticks / 6) * (1 - ease((ticks - length) / 8));
                open = Math.max(open, rising);
                beat += .4F * rising;
            }
            case LAND -> {
                // Wings spread wide to brake, hooves reaching down for the ground; folded once he is down.
                float braking = ease(ticks / 6) * (1 - ease((ticks - length) / 8));
                open = Math.max(open, braking);
                lift += .6F * braking;
                beat *= 1 - .6F * braking;
                rear = .3F * braking;
                for (int side = 0; side < 2; side++) {
                    front[side].xRot += .4F * braking;
                    frontLower[side].xRot -= 1.3F * braking;
                    hind[side].xRot -= .6F * braking;
                }
            }
            case BURROW -> {
                // Up on his hind legs, then nose first into the ground, wings tight.
                rear = .6F * ease(ticks / 4) * (1 - ease((ticks - 4) / 3));
                nose = ease((ticks - 5) / 6) * (1 - ease((ticks - length - 10) / 8));
                open *= 1 - nose;
            }
            case EMERGE -> {
                // Up out of the earth rearing, wings flung wide, then down on all four.
                rear = 1.1F * ease(ticks / 3) * (1 - ease((ticks - length + 2) / 6));
                paw = rear;
                mouth = rear;
                open = Math.max(open, rear);
                lift += .6F * rear;
            }
            default -> {}
        }

        // Rearing turns him up about his hind legs: up at the front, the hind legs keeping their hooves under him.
        if (rear > 0) {
            body.xRot -= .85F * rear;
            body.y -= 6 * rear;
            body.z += 3 * rear;
            for (int side = 0; side < 2; side++) {
                hind[side].xRot += .85F * rear;
                hindLower[side].xRot -= .3F * rear;
                float pawing = paw * Mth.sin(ticks * 1.1F + side * Mth.PI);
                front[side].xRot += -1.1F * rear + .45F * pawing;
                frontLower[side].xRot += 1.2F * rear - .5F * pawing;
            }
            neck.xRot -= .35F * rear;
            head.xRot += .45F * rear;
        }
        if (crouch > 0) {
            body.y += 3 * crouch;
            for (int side = 0; side < 2; side++) {
                front[side].xRot -= .35F * crouch;
                frontLower[side].xRot += .7F * crouch;
                hind[side].xRot += .5F * crouch;
                hindLower[side].xRot -= .9F * crouch;
            }
            neck.xRot += .3F * crouch;
        }
        if (lunge > 0) {
            for (int side = 0; side < 2; side++) {
                front[side].xRot -= 1.1F * lunge;
                frontLower[side].xRot += .2F * lunge;
                hind[side].xRot += 1.1F * lunge;
                hindLower[side].xRot += .2F * lunge;
            }
            neck.xRot += .45F * lunge;
            head.xRot -= .25F * lunge;
            tail.xRot += 1.2F * lunge;
        }
        if (nose > 0) {
            body.xRot += 1.3F * nose;
            body.y += 4 * nose;
            neck.xRot += .3F * nose;
        }
        jaw.xRot += .5F * mouth;

        // ---- dying: legs buckling, down on his side, wings fallen open ----
        if (dying > 0) {
            float down = ease(dying / .6F);
            body.y += 12 * down;
            body.zRot += 1.25F * ease((dying - .2F) / .6F);
            for (int side = 0; side < 2; side++) {
                front[side].xRot -= .4F * down;
                frontLower[side].xRot += 1.6F * down;
                hind[side].xRot += .5F * down;
                hindLower[side].xRot -= 1.2F * down;
            }
            neck.xRot += .9F * down;
            head.xRot += .4F * down;
            open = Mth.lerp(down, open, .55F);
            lift = Mth.lerp(down, lift, -.45F);
            beat *= 1 - down;
        }

        // ---- the wings ----
        open = Mth.clamp(open, 0, 1);
        // Folding, the whole wing swings back and turns down along him first; only the last of the way do the forearm
        // and the hand shut over it, so that a wing half open is a wing swept back, not one broken in three.
        float folded = 1 - open, shut = ease((folded - .45F) / .55F);
        for (int side = 0; side < 2; side++) {
            float sign = side == 0 ? 1 : -1;
            float stroke = beat * Mth.sin(flap), lag = beat * Mth.sin(flap - .7F), whip = beat * Mth.sin(flap - 1.4F);
            wing[side].x -= sign * FOLD_OUT * folded;
            wing[side].xRot = FOLD_PITCH * folded + level;
            wing[side].yRot = sign * (FOLD_SWEEP * folded + .15F * open + sweep);
            wing[side].zRot = sign * (FOLD_SPLAY * folded + lift + .9F * stroke);
            wingFore[side].y += FOLD_FOREARM_OUT * shut;
            wingFore[side].yRot = sign * -.5F * sweep;
            wingFore[side].zRot = sign * (.35F * lag - FOLD_SHUT * shut);
            wingFore[side].zScale = 1 - FOLD_TUCK * shut;
            wingHand[side].y -= FOLD_HAND_OUT * shut;
            wingHand[side].yRot = sign * .35F * sweep;
            wingHand[side].zRot = sign * (.45F * whip + FOLD_SHUT * shut);
            // Folded, the feathers lie close and still but for a stir now and then.
            wing[side].zRot += sign * .03F * folded * Mth.sin(time * .11F + side);
        }
    }

    /** The wind-up of the skill he is doing, in ticks, to pose by. */
    static float windup(Skill skill) {
        return switch (skill) {
            case HOOVES -> SeereRules.HOOVES.timing().windup();
            case BLINKS -> SeereRules.BLINKS.timing().windup();
            case SPIKES -> SeereRules.SPIKES.timing().windup();
            case DIVE -> SeereRules.DIVE.timing().windup();
            case BEAMS -> SeereRules.BEAMS.beam().timing().windup();
            case TAKE_OFF, LAND, BURROW, EMERGE -> SeereRules.shiftTicks(skill) / 2F;
            case NONE -> 1;
        };
    }

    /** How long the skill he is doing runs, in ticks, to pose by. */
    static float length(Skill skill) {
        return switch (skill) {
            case HOOVES -> SeereRules.HOOVES.timing().ticks();
            case BLINKS -> SeereRules.BLINKS.timing().ticks();
            case SPIKES -> SeereRules.SPIKES.timing().ticks();
            case DIVE -> SeereRules.DIVE.timing().ticks();
            case BEAMS -> SeereRules.BEAMS.beam().timing().ticks();
            case TAKE_OFF, LAND, BURROW, EMERGE -> SeereRules.shiftTicks(skill);
            case NONE -> 1;
        };
    }

    /**
     * How fast his wings beat, in radians a tick: not at all on the ground, hard in the air, harder climbing for a dive or
     * leaving the ground, slow and mighty hanging to fire his beams, and not at all falling in his dive.
     */
    static float flapRate(Skill skill, float ticks, float flight, boolean moving) {
        float rate = flight * (moving ? .26F : .32F);
        switch (skill) {
            case DIVE -> rate = ticks < windup(skill) ? .5F : ticks < length(skill) ? 0 : rate;
            case BEAMS -> rate = ticks < length(skill) ? .2F : rate;
            case TAKE_OFF -> rate = ticks < length(skill) ? .55F : rate;
            case LAND -> rate = ticks < length(skill) ? .4F : rate;
            default -> {}
        }
        return rate;
    }
}
