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
 * Belial's second shape, the hollow angel. His geometry is data (see {@link GeoModel}, made by
 * gamemodels/belial/belial_model.py); his rags, his face of fire and his feathers are painted. This finds the bones that
 * move and poses them.
 *
 * <p>His six wings are a seraph's: held fanned out in the plane behind him, the highest pair raised, the middle spread,
 * the lowest lowered, each turned so that its face, and the eye on it, looks before him (his beams leave from those
 * eyes); the pairs beat one after another, the highest first, a slow ripple. His crown of thorns stands behind his head,
 * turning. To pour his beams he spreads his wings wider and his arms out and down; to loose a volley he raises both hands; to strike
 * at a mind he puts his hand to his brow and bows his head; to call up his likenesses he wraps his wings about him and
 * flings them open; blinking, he wraps them about him. Stunned, his head and wings hang and he reels. In the second half of
 * his change he rises out of nothing, his wings unfolding last; dying, his wings fall. A skill goes on being posed after
 * it is over, until he begins another, and every pose eases back by itself.
 *
 * <p>{@code side} is 1 for an arm or wing built at -X, -1 for the other: a wing rises with a positive Z turn times its
 * side. Each wing is turned about X by {@link #FACING} first, so that the plane of it stands upright, facing forward.
 */
public final class BelialHollowModel extends HierarchicalModel<Belial> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "belial_hollow"), "main");
    static final String GEOMETRY = "belial_hollow";
    /** His pairs of wings, highest first, and how many primaries each hand has. */
    static final String[] PAIRS = {"upper", "middle", "lower"};
    static final int PRIMARIES = 4;
    /** How far each wing is turned about X so that its plane stands upright behind him, facing forward. */
    static final float FACING = -1.35F;
    /** How high each pair is raised in the fan (highest first), and how deep they beat. */
    static final float[] FAN = {.95F, .22F, -.32F};
    static final float BEAT = .16F;

    // Seen by BelialModelTest.
    final ModelPart root, body, skirt, head, crown;
    final ModelPart[] all;
    /** Left then right (as built: the first at -X): arms and forearms; each pair's arm and hand; each hand's primaries. */
    final ModelPart[] arm = new ModelPart[2], forearm = new ModelPart[2];
    final ModelPart[][] wing = new ModelPart[3][2], wingHand = new ModelPart[3][2];
    final ModelPart[][][] primaries = new ModelPart[3][2][PRIMARIES];
    /** The winding of the Belial about to be drawn, set by the renderer; with none, his wings beat at their hanging pace. */
    BelialWinding winding;

    public BelialHollowModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        body = BelialFairModel.need(bones, "body"); skirt = BelialFairModel.need(bones, "skirt"); head = BelialFairModel.need(bones, "head");
        crown = BelialFairModel.need(bones, "crown");
        String[] sides = {"left", "right"};
        for (int side = 0; side < 2; side++) {
            arm[side] = BelialFairModel.need(bones, "arm_" + sides[side]);
            forearm[side] = BelialFairModel.need(bones, "forearm_" + sides[side]);
            for (int pair = 0; pair < PAIRS.length; pair++) {
                String name = PAIRS[pair] + "_" + sides[side];
                wing[pair][side] = BelialFairModel.need(bones, name);
                wingHand[pair][side] = BelialFairModel.need(bones, name + "_1");
                for (int index = 0; index < PRIMARIES; index++) primaries[pair][side][index] = BelialFairModel.need(bones, name + "_1#" + index);
            }
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    static float ease(float amount) { return BelialFairModel.ease(amount); }

    @Override public void setupAnim(Belial belial, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - belial.tickCount;
        float dying = belial.deathTime > 0 ? Mth.clamp((belial.deathTime + partial) / belial.death().ticks(), 0, 1) : 0;
        float flap = winding != null ? winding.flap : age * BelialWinding.HANG;
        pose(age, flap, belial.posedSkill(), belial.skillTicks(partial), belial.stunned() > 0, belial.changed(partial), dying, belial.getId());
    }

    /**
     * @param flap    how far his wings are through their beat, in radians
     * @param skill   what he is doing (or last did), and {@code ticks} since he began it
     * @param stunned whether he hangs stunned
     * @param change  how far through his change he is, 0 to 1 (0 when he is not changing): he rises in its second half
     * @param dying   0 alive to 1 dead
     * @param phase   his own, so that no two of him (nor his likenesses) move together
     */
    public void pose(float age, float flap, Skill skill, float ticks, boolean stunned, float change, float dying, int phase) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 2.9F;
        body.y += .7F * Mth.sin(time * .07F);
        skirt.xRot += .1F + .05F * Mth.sin(time * .05F);
        skirt.zRot += .05F * Mth.sin(time * .045F + 1);
        head.xRot += .05F * Mth.sin(time * .06F);
        crown.zRot += time * .015F;
        float spread = 0, beat = BEAT, wrap = 0, droop = 0, open = 0;
        float[] armForward = new float[2], armOut = {.12F, .12F}, foreBend = {-.15F, -.15F};
        switch (skill) {
            case BEAMS -> {
                // His wings fanned wider, his arms out: his eyes all on his quarry.
                float pour = BelialFairModel.held(ticks, 10, BelialRules.BEAMS.beam().timing(), 8);
                spread = .12F * pour;
                open = .3F * pour;
                beat *= 1 - .85F * pour;
                for (int side = 0; side < 2; side++) {
                    armOut[side] = .12F + .45F * pour;
                    armForward[side] = -.45F * pour;
                }
            }
            case VOLLEY -> {
                float cast = BelialFairModel.held(ticks, 5, BelialRules.HOLLOW_VOLLEY.timing(), 6);
                for (int side = 0; side < 2; side++) {
                    armForward[side] = -2.1F * cast;
                    armOut[side] = .12F + .3F * cast;
                }
                spread = .12F * cast;
            }
            case PSYCHIC -> {
                // His hand to his brow, his head bowed: the strike goes out of him.
                float strike = BelialFairModel.held(ticks, 6, BelialRules.PSYCHIC.timing(), 8);
                armForward[1] = -2.5F * strike;
                armOut[1] = .12F - .55F * strike;
                foreBend[1] = -.15F - 1.1F * strike;
                head.xRot += .35F * strike;
                spread = -.1F * strike;
            }
            case CLONES -> {
                // His wings wrapped about him, then flung open as the five of him stand.
                SkillTiming timing = BelialRules.CLONES;
                wrap = ease(ticks / timing.windup()) * (1 - ease((ticks - timing.windup()) / 3));
                spread = .35F * ease((ticks - timing.windup()) / 3) * (1 - ease((ticks - timing.ticks() - 4) / 8));
            }
            case SUMMON -> {
                float call = BelialFairModel.held(ticks, 10, BelialRules.SUMMON.timing(), 8);
                for (int side = 0; side < 2; side++) {
                    armOut[side] = .12F + .75F * call;
                    armForward[side] = -.35F * call;
                }
                spread = .15F * call;
            }
            case CHARIOT -> {
                SkillTiming timing = BelialRules.CHARIOT.timing();
                float lift = ease(ticks / (timing.windup() * .6F)) * (1 - ease((ticks - timing.windup() + 4) / 4));
                float throwing = ease((ticks - timing.windup() + 4) / 4) * (1 - ease((ticks - timing.ticks() - 6) / 8));
                armForward[1] = -2.8F * lift - 1.4F * throwing;
                spread = .15F * lift;
            }
            case BLINK -> wrap = BelialFairModel.held(ticks, 2, BelialRules.BLINK.timing(), 6);
            default -> {}
        }
        if (stunned) {
            droop = 1;
            body.zRot += .12F * Mth.sin(time * .17F);
            body.xRot += .1F * Mth.sin(time * .13F + 1);
            head.xRot += .5F;
            beat *= .2F;
        }
        float fallen = ease(dying / .6F);
        droop = Math.max(droop, fallen);
        if (fallen > 0) {
            body.xRot -= .6F * fallen;
            beat *= 1 - fallen;
        }
        // Rising out of nothing in the second half of his change: his body first, his wings unfolding after it.
        float grown = change > 0 ? ease((change - .5F) * 2) : 1, unfolded = change > 0 ? ease((change - .65F) / .35F) : 1;
        body.xScale = body.yScale = body.zScale = Math.max(.01F, grown);

        for (int side = 0; side < 2; side++) {
            float s = side == 0 ? 1 : -1;
            arm[side].xRot += armForward[side];
            arm[side].zRot += s * (armOut[side] - (stunned ? .1F : 0));
            forearm[side].xRot += foreBend[side];
            for (int pair = 0; pair < PAIRS.length; pair++) {
                float stroke = Mth.sin(flap - pair * .7F), lag = Mth.sin(flap - pair * .7F - .6F);
                // Opening his fan to pour his beams, his lowest pair rises towards level, the better to look on his quarry.
                float raise = FAN[pair] * unfolded + (pair == 0 ? spread : pair == 2 ? open - spread : 0) - .5F * droop;
                ModelPart arm = wing[pair][side], hand = wingHand[pair][side];
                arm.xRot = FACING * (1 - wrap) + .6F * wrap;
                arm.yRot = s * (1.4F * wrap - .15F * (1 - unfolded));
                arm.zRot = s * (raise * (1 - wrap) + beat * stroke);
                hand.zRot = s * (beat * .6F * (lag - stroke) - .1F - .3F * droop);
                for (int index = 0; index < PRIMARIES; index++)
                    primaries[pair][side][index].zRot += s * (.08F * (PRIMARIES - index) / PRIMARIES + beat * .3F * (lag - stroke));
            }
        }
    }
}
