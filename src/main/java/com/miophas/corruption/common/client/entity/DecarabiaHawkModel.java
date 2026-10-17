package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Decarabia;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Decarabia's second shape, the goshawk. His geometry is data (see {@link GeoModel}, made by
 * gamemodels/decarabia/decarabia_model.py); his plumage is painted. Each wing is three pieces (arm, forearm, hand), each
 * a leading edge with coverts and flight feathers in layers behind it, and the hand's six primaries each a feather, and a
 * bone, of its own; this finds the bones that move and poses them.
 *
 * <p>His wings beat as the winding has them ({@link DecarabiaWinding}), each piece a little after the last, and the
 * primaries' tips bend up as they bear him and flex after each beat. Climbing for a stoop they beat hard; stooping (as
 * steeply as he is tipped, which the renderer does) they sweep back along him, the primaries drawn together, his tail
 * closes and his talons reach out before him. At his barrage he lifts his wings high, then beats them hard as the
 * bolts pour out; he throws his head back to draw breath for his screech, then thrusts it out with his beak wide; at his
 * storm he holds his wings up and spread. In the second half of his change he unfolds out of nothing, his wings last; dying,
 * his wings fall still and droop. A skill goes on being posed after it is over, until he begins another, and every pose
 * eases back by itself.
 *
 * <p>As in every model of this space, -Y is up and -Z before him; a positive X turn tips the beak down and swings a
 * hanging leg back. {@code side} is 1 for the wing built at -X, -1 for the other: a wing rises with a positive Z turn
 * times its side, and sweeps back with a positive Y turn times it.
 */
public final class DecarabiaHawkModel extends HierarchicalModel<Decarabia> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "decarabia_hawk"), "main");
    static final String GEOMETRY = "decarabia_hawk";
    /** How fast his wings beat gliding, and climbing hard, in radians a tick. */
    static final float GLIDE = .28F, CLIMB = .5F;
    /** How many primaries each hand has, each a bone of its own: the outermost first. */
    static final int PRIMARIES = 6;

    // Seen by DecarabiaModelTest.
    final ModelPart root, body, head, jaw, tail;
    final ModelPart[] all;
    /** Left then right (as built: the first at -X): arm, forearm, hand; and the legs. */
    final ModelPart[] wing = new ModelPart[2], wingFore = new ModelPart[2], wingHand = new ModelPart[2], leg = new ModelPart[2];
    /** Left then right: each hand's primaries, the outermost first (each a turned box, so a bone of its own, "#" and its place). */
    final ModelPart[][] primaries = new ModelPart[2][PRIMARIES];
    /** The winding of the Decarabia about to be drawn, set by the renderer; with none, his wings beat at a gliding pace. */
    DecarabiaWinding winding;

    public DecarabiaHawkModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        body = need(bones, "body"); head = need(bones, "head"); jaw = need(bones, "jaw"); tail = need(bones, "tail");
        String[] sides = {"left", "right"};
        for (int side = 0; side < 2; side++) {
            wing[side] = need(bones, "wing_" + sides[side]);
            wingFore[side] = need(bones, "wing_" + sides[side] + "_1");
            wingHand[side] = need(bones, "wing_" + sides[side] + "_2");
            leg[side] = need(bones, "leg_" + sides[side]);
            for (int index = 0; index < PRIMARIES; index++) primaries[side][index] = need(bones, "wing_" + sides[side] + "_2#" + index);
        }
    }

    private static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Decarabia's goshawk has no bone " + name);
        return bone;
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    static float ease(float amount) { return DecarabiaStarModel.ease(amount); }

    /** The timing of one of the goshawk's skills, to pose by; null for none of his. */
    static SkillTiming timing(Skill skill) {
        return switch (skill) {
            case SCREECH -> DecarabiaRules.SCREECH.timing();
            case DIVE -> DecarabiaRules.DIVE.timing();
            case DIVES -> DecarabiaRules.DIVES.timing();
            case STORM -> DecarabiaRules.STORM.timing();
            case BARRAGE -> DecarabiaRules.BARRAGE.timing();
            default -> null;
        };
    }

    /**
     * How fast his wings beat, in radians a tick: gliding; hard climbing for a stoop (in it, they are swept back and still,
     * however fast they would beat); slowly lifted at his barrage, then hard as it pours out; slowly at his screech and his
     * storm; hardly at all dying.
     */
    static float flapRate(Skill skill, float ticks, boolean dying) {
        if (dying) return .05F;
        SkillTiming timing = timing(skill);
        if (timing == null || ticks >= timing.ticks()) return GLIDE;
        return switch (skill) {
            case DIVE -> ticks < timing.windup() ? CLIMB : GLIDE;
            case DIVES -> CLIMB;
            case BARRAGE -> ticks < timing.windup() ? .1F : .9F;
            case SCREECH, STORM -> .15F;
            default -> GLIDE;
        };
    }

    @Override public void setupAnim(Decarabia decarabia, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - decarabia.tickCount;
        float dying = decarabia.deathTime > 0 ? Mth.clamp((decarabia.deathTime + partial) / decarabia.death().ticks(), 0, 1) : 0;
        float flap = winding != null ? winding.flap : age * GLIDE;
        pose(age, flap, decarabia.posedSkill(), decarabia.skillTicks(partial), pitch, decarabia.changed(partial), dying, decarabia.getId());
    }

    /**
     * @param flap   how far his wings are through their beats, in radians
     * @param skill  what he is doing (or last did), and {@code ticks} since he began it
     * @param pitch  how far he is tipped nose down, in degrees (the renderer tips him; this folds him for it)
     * @param change how far through his change he is, 0 to 1 (0 when he is not changing): he unfolds in its second half
     * @param dying  0 alive to 1 dead
     * @param phase  his own, so that no two of him move together
     */
    public void pose(float age, float flap, Skill skill, float ticks, float pitch, float change, float dying, int phase) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 4.1F;
        // How hard his wings beat and how high they are held; how far he stoops (wings swept back, tail closed, talons
        // out); how far his tail is spread; how far his head is thrown back, and his beak open.
        float beat = .55F, lift = 0, stoop = 0, fan = 1, headUp = 0, mouth = 0, splay = 0;
        SkillTiming timing = timing(skill);
        switch (skill) {
            case SCREECH -> {
                float drawing = ease(ticks / timing.windup()) * (1 - ease((ticks - timing.windup()) / 3));
                float crying = ease((ticks - timing.windup() + 1) / 3) * (1 - ease((ticks - timing.ticks()) / 6));
                headUp = .6F * drawing - .35F * crying;
                mouth = .25F * drawing + .9F * crying;
                lift = .3F * Math.max(drawing, crying);
                beat = Mth.lerp(Math.max(drawing, crying), beat, .2F);
                fan += .25F * crying;
            }
            case DIVE, DIVES -> {
                stoop = ease((pitch - 15) / 30);
                float climbing = ticks < (skill == Skill.DIVE ? timing.windup() : timing.ticks()) ? ease(ticks / 4) : 0;
                beat = Mth.lerp(climbing, beat, .75F) * (1 - stoop);
                lift = .25F * climbing * (1 - stoop);
            }
            case STORM -> {
                float calling = ease(ticks / 8) * (1 - ease((ticks - timing.ticks()) / 8));
                lift = .45F * calling;
                beat = Mth.lerp(calling, beat, .2F);
                headUp = .4F * calling;
                mouth = .3F * calling;
                fan += .3F * calling;
                splay = .5F * calling;
            }
            case BARRAGE -> {
                float raising = ease(ticks / timing.windup()) * (1 - ease((ticks - timing.windup()) / 3));
                float loosing = ease((ticks - timing.windup() + 2) / 3) * (1 - ease((ticks - timing.ticks()) / 8));
                lift = .9F * raising + .2F * loosing;
                beat = Mth.lerp(loosing, Mth.lerp(raising, beat, .1F), .85F);
                headUp = .2F * raising;
                fan += .2F * Math.max(raising, loosing);
                splay = Math.max(raising, loosing);
            }
            default -> {}
        }
        if (dying > 0) {
            float down = ease(dying / .6F);
            beat *= 1 - down;
            lift = Mth.lerp(down, lift, -.5F);
            headUp -= .5F * down;
            body.zRot += .5F * ease((dying - .3F) / .7F);
        }

        float stroke = Mth.sin(flap), lag = Mth.sin(flap - .6F), whip = Mth.sin(flap - 1.2F);
        for (int side = 0; side < 2; side++) {
            float s = side == 0 ? 1 : -1;
            wing[side].zRot = s * (lift + beat * stroke - .08F + .12F * stoop);
            wing[side].yRot = s * stoop;
            wingFore[side].zRot = s * beat * .6F * (lag - stroke);
            wingFore[side].yRot = s * .35F * stoop;
            wingHand[side].zRot = s * beat * .5F * (whip - lag);
            wingHand[side].yRot = s * .3F * stoop;
            // His primaries: splayed into fingers, spread wider as he beats his hardest; their tips bent up as they bear
            // him, the outermost most; flexing a little after each beat; drawn together, the fan closed, in a stoop.
            float outermost = primaries[side][0].getInitialPose().yRot;
            for (int index = 0; index < PRIMARIES; index++) {
                ModelPart feather = primaries[side][index];
                float rest = feather.getInitialPose().yRot;
                feather.yRot = Mth.lerp(.85F * stoop, rest * (1 + .15F * splay), outermost);
                feather.zRot += s * (.12F * (PRIMARIES - index) / PRIMARIES * (1 - stoop) + beat * .35F * (whip - lag));
            }
            // In flight his legs are tucked back under his tail; stooping, his talons reach out before him.
            leg[side].xRot = Mth.lerp(stoop, 1.1F, -1.2F);
        }
        head.xRot += .05F * Mth.sin(time * .07F) - headUp;
        head.yRot += .1F * Mth.sin(time * .045F) * (1 - stoop);
        jaw.xRot += mouth;
        tail.xRot += .06F * Mth.sin(flap + .8F) * (1 - stoop);
        tail.xScale = fan * (1 - .5F * stoop);

        // Unfolding out of nothing in the second half of his change: his body first, his wings after it.
        float grown = change > 0 ? ease((change - .5F) * 2) : 1, spread = change > 0 ? ease((change - .65F) / .35F) : 1;
        body.xScale = body.yScale = body.zScale = Math.max(.01F, grown);
        for (int side = 0; side < 2; side++) wing[side].xScale = Math.max(.01F, spread);
    }
}
