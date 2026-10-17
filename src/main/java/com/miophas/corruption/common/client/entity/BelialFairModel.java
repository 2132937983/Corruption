package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Belial;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.BelialRules.Skill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Belial's first shape, the fair angel. His geometry is data (see {@link GeoModel}, made by
 * gamemodels/belial/belial_model.py); his robe, face, hair and feathers are painted. This finds the bones that move and
 * poses them.
 *
 * <p>He hangs in the air, rising and falling a little, his robe trailing behind him and swaying; his halo floats over his
 * head, turning slowly; his two great wings are raised into a V behind him and swept back, beating slowly and deeply
 * (their beat wound on by {@link BelialWinding}), each piece a little after the last, the six primaries' tips bent up.
 * To loose a volley he raises his right hand; to pour his beams he raises both before him, palms out (his beams leave from
 * them), his wings spread wide; to call up his host he spreads his arms down and out, his wings high; to call his chariot
 * he lifts his right arm high and throws it forward; blinking, he wraps his wings about him. Brought down, in the first
 * half of his change, he falls back, his wings dropping, his halo falling, and dwindles into light. A skill goes on being
 * posed after it is over, until he begins another, and every pose eases back by itself.
 *
 * <p>As in every model of this space, -Y is up and -Z before him; a negative X turn raises an arm forward. {@code side}
 * is 1 for the arm and wing built at -X, -1 for the other: a wing rises with a positive Z turn times its side, and sweeps
 * back with a positive Y turn times it.
 */
public final class BelialFairModel extends HierarchicalModel<Belial> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "belial_fair"), "main");
    static final String GEOMETRY = "belial_fair";
    /** How many primaries each wing's hand has, each a bone of its own: the outermost first. */
    static final int PRIMARIES = 6;
    /** How high his wings are raised behind him, how far swept back, and how deep they beat, as he hangs in the air. */
    static final float RAISE = .55F, SWEEP = .5F, BEAT = .35F;

    // Seen by BelialModelTest.
    final ModelPart root, body, skirt, head, halo;
    final ModelPart[] all;
    /** Left then right (as built: the first at -X): arms, forearms; wing arm, forearm, hand; each hand's primaries. */
    final ModelPart[] arm = new ModelPart[2], forearm = new ModelPart[2], wing = new ModelPart[2], wingFore = new ModelPart[2], wingHand = new ModelPart[2];
    final ModelPart[][] primaries = new ModelPart[2][PRIMARIES];
    /** The winding of the Belial about to be drawn, set by the renderer; with none, his wings beat at their hanging pace. */
    BelialWinding winding;

    public BelialFairModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        body = need(bones, "body"); skirt = need(bones, "skirt"); head = need(bones, "head"); halo = need(bones, "halo");
        String[] sides = {"left", "right"};
        for (int side = 0; side < 2; side++) {
            arm[side] = need(bones, "arm_" + sides[side]);
            forearm[side] = need(bones, "forearm_" + sides[side]);
            wing[side] = need(bones, "wing_" + sides[side]);
            wingFore[side] = need(bones, "wing_" + sides[side] + "_1");
            wingHand[side] = need(bones, "wing_" + sides[side] + "_2");
            for (int index = 0; index < PRIMARIES; index++) primaries[side][index] = need(bones, "wing_" + sides[side] + "_2#" + index);
        }
    }

    static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Belial has no bone " + name);
        return bone;
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    /** How far into a skill's pose he is: easing in over {@code in} ticks, held to its end, easing out over {@code out} after. */
    static float held(float ticks, float in, SkillTiming timing, float out) {
        return ease(ticks / Math.max(1, in)) * (1 - ease((ticks - timing.ticks()) / Math.max(1, out)));
    }

    @Override public void setupAnim(Belial belial, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - belial.tickCount;
        float dying = belial.deathTime > 0 ? Mth.clamp((belial.deathTime + partial) / belial.death().ticks(), 0, 1) : 0;
        float flap = winding != null ? winding.flap : age * BelialWinding.HANG;
        pose(age, flap, belial.posedSkill(), belial.skillTicks(partial), belial.changed(partial), dying, belial.getId());
    }

    /**
     * @param flap   how far his wings are through their beat, in radians
     * @param skill  what he is doing (or last did), and {@code ticks} since he began it
     * @param change how far through his change he is, 0 to 1 (0 when he is not changing): he falls in its first half
     * @param dying  0 alive to 1 dead
     * @param phase  his own, so that no two of him move together
     */
    public void pose(float age, float flap, Skill skill, float ticks, float change, float dying, int phase) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 3.3F;
        // Hanging in the air: rising and falling a little, his robe trailing and swaying, his head and halo stirring.
        body.y += .6F * Mth.sin(time * .08F);
        skirt.xRot += .12F + .04F * Mth.sin(time * .06F);
        skirt.zRot += .04F * Mth.sin(time * .05F + 1);
        head.xRot += .04F * Mth.sin(time * .05F);
        halo.yRot += time * .02F;
        halo.y += .5F * Mth.sin(time * .1F + .5F);
        float raise = RAISE, sweep = SWEEP, beat = BEAT;
        float[] armForward = new float[2], armOut = {.1F, .1F}, foreBend = {-.12F, -.12F};
        switch (skill) {
            case VOLLEY -> {
                // His right hand raised, as he looses them.
                float cast = held(ticks, 6, BelialRules.VOLLEY.timing(), 6);
                armForward[1] = -1.9F * cast;
                foreBend[1] -= .2F * cast;
                raise += .15F * cast;
            }
            case BEAMS -> {
                // Both hands raised before him, palms out; his wings spread wide behind.
                float pour = held(ticks, 10, BelialRules.BEAMS.beam().timing(), 8);
                for (int side = 0; side < 2; side++) {
                    armForward[side] = -1.57F * pour;
                    armOut[side] = .1F + .08F * pour;
                    foreBend[side] = -.12F * (1 - pour);
                }
                raise = Mth.lerp(pour, raise, .15F);
                sweep = Mth.lerp(pour, sweep, .12F);
                beat *= 1 - .8F * pour;
            }
            case SUMMON -> {
                // His arms spread down and out over the ground, his wings raised high.
                float call = held(ticks, 10, BelialRules.SUMMON.timing(), 8);
                for (int side = 0; side < 2; side++) {
                    armOut[side] = .1F + .75F * call;
                    armForward[side] = -.35F * call;
                }
                raise += .45F * call;
            }
            case CHARIOT -> {
                // His right arm lifted high to call it, then thrown forward as it comes.
                SkillTiming timing = BelialRules.CHARIOT.timing();
                float lift = ease(ticks / (timing.windup() * .6F)) * (1 - ease((ticks - timing.windup() + 4) / 4));
                float throwing = ease((ticks - timing.windup() + 4) / 4) * (1 - ease((ticks - timing.ticks() - 6) / 8));
                armForward[1] = -2.8F * lift - 1.4F * throwing;
                armForward[0] = -.6F * (lift + throwing);
                raise += .3F * lift;
            }
            case BLINK -> {
                // His wings wrapped about him, for the moment he is gone.
                float wrap = held(ticks, 2, BelialRules.BLINK.timing(), 6);
                sweep = Mth.lerp(wrap, sweep, 1.35F);
                raise = Mth.lerp(wrap, raise, -.1F);
            }
            default -> {}
        }
        // Brought down: in the first half of his change he falls back, his wings dropping, his halo falling, and dwindles.
        float fall = ease(change * 2), down = Math.max(fall, ease(dying / .6F));
        if (down > 0) {
            body.xRot -= .7F * down;
            raise = Mth.lerp(down, raise, -.6F);
            sweep += .4F * down;
            beat *= 1 - down;
            halo.y += 8 * down;
            head.xRot -= .4F * down;
        }
        float shrink = Math.max(.01F, 1 - .92F * fall);
        body.xScale = body.yScale = body.zScale = shrink;

        for (int side = 0; side < 2; side++) {
            float s = side == 0 ? 1 : -1;
            arm[side].xRot += armForward[side];
            arm[side].zRot += s * armOut[side];
            forearm[side].xRot += foreBend[side];
        }
        float stroke = Mth.sin(flap), lag = Mth.sin(flap - .6F), whip = Mth.sin(flap - 1.2F);
        for (int side = 0; side < 2; side++) {
            float s = side == 0 ? 1 : -1;
            wing[side].zRot = s * (raise + beat * stroke);
            wing[side].yRot = s * sweep;
            wingFore[side].zRot = s * (beat * .6F * (lag - stroke) - .12F);
            wingHand[side].zRot = s * (beat * .5F * (whip - lag) - .08F);
            for (int index = 0; index < PRIMARIES; index++)
                primaries[side][index].zRot += s * (.1F * (PRIMARIES - index) / PRIMARIES + beat * .3F * (whip - lag));
        }
    }
}
