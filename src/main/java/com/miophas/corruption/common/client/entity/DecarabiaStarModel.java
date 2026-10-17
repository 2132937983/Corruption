package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Decarabia;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import com.miophas.corruption.core.demon.skill.SpinRules;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Decarabia's first shape, the star. Its geometry is data (see {@link GeoModel}, made by
 * gamemodels/decarabia/decarabia_model.py): a core with his eye on it, five rays of three steps each on a wheel of their
 * own about it, and the pentacle's ring behind them; everything finer is painted. The wheel turns as the winding has it
 * ({@link DecarabiaWinding}) and the ring the other way, while the core, and his eye, stay upright on his quarry.
 *
 * <p>The rays breathe. Through a volley's wind-up they draw in, and at each of its beats they thrust out; spinning at his
 * quarry he leans into it; gathering himself to split, his rays stretch and he trembles; stunned, he reels. In the first
 * half of his change the rays fold into the core and the ring closes up, the core dwindling; in the second the goshawk
 * unfolds (his other model). A skill goes on being posed after it is over, until he begins another, and every pose
 * eases back by itself.
 *
 * <p>As in every model of this space, -Y is up and -Z before him; a positive Z turn takes a ray from straight up towards
 * his left, and a positive X turn leans the top of him forward.
 */
public final class DecarabiaStarModel extends HierarchicalModel<Decarabia> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "decarabia_star"), "main");
    static final String GEOMETRY = "decarabia_star";
    /** How fast his points turn at rest, in degrees a tick. */
    static final float IDLE = 2.5F;

    // Seen by DecarabiaModelTest.
    final ModelPart root, star, wheel, ring;
    final ModelPart[] rays = new ModelPart[5], all;
    /** The winding of the Decarabia about to be drawn, set by the renderer; with none, his points turn at an idle pace. */
    DecarabiaWinding winding;

    public DecarabiaStarModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        star = need(bones, "star");
        wheel = need(bones, "wheel");
        ring = need(bones, "ring");
        for (int ray = 0; ray < rays.length; ray++) rays[ray] = need(bones, "ray_" + ray);
    }

    private static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Decarabia's star has no bone " + name);
        return bone;
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    /** The volley, and the spin, of the star this is: a small star's own (his likenesses', and his while he is one of five), or his. */
    static SpinVolleyRules volley(Decarabia decarabia) { return decarabia.small() ? DecarabiaRules.SMALL_VOLLEY : DecarabiaRules.VOLLEY; }
    static SpinRules spin(Decarabia decarabia) { return decarabia.small() ? DecarabiaRules.SMALL_SPIN : DecarabiaRules.SPIN; }

    /**
     * How fast his points turn as his pose asks, in degrees a tick (the winding eases onto it): slowly at rest; fast as
     * he spins at his quarry, gathers himself to split or shoots; faster through his change; hardly at all stunned or
     * dying. Through a volley the winding keeps them to the volley's own turning.
     */
    static float pace(Decarabia decarabia, Skill skill, float ticks) {
        if (decarabia.deathTime > 0) return .5F;
        if (decarabia.changing() > 0) return 30;
        if (decarabia.stunned() > 0) return .8F;
        return switch (skill) {
            case SPIN -> ticks < spin(decarabia).timing().ticks() ? 36 : IDLE;
            case SPLIT -> ticks < DecarabiaRules.SPLIT.ticks() ? 24 : IDLE;
            case VOLLEY -> ticks < volley(decarabia).timing().ticks() ? (float) volley(decarabia).spin() : IDLE;
            default -> IDLE;
        };
    }

    @Override public void setupAnim(Decarabia decarabia, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - decarabia.tickCount;
        float dying = decarabia.deathTime > 0 ? Mth.clamp((decarabia.deathTime + partial) / decarabia.death().ticks(), 0, 1) : 0;
        float spin = winding != null ? winding.spin : age * IDLE, ringTurn = winding != null ? winding.ring : age * IDLE * .3F;
        pose(age, spin, ringTurn, decarabia.posedSkill(), decarabia.skillTicks(partial), decarabia.small(), decarabia.stunned() > 0,
                decarabia.changed(partial), dying, decarabia.getId());
    }

    /**
     * @param spin     how far round his points have turned, in degrees towards his right
     * @param ringTurn how far round the ring has turned, in degrees towards his left
     * @param skill    what he is doing (or last did), and {@code ticks} since he began it
     * @param small    whether this is a small star (whose skills run shorter)
     * @param stunned  whether he hangs stunned
     * @param change   how far through his change he is, 0 to 1 (0 when he is not changing)
     * @param dying    0 alive to 1 dead
     * @param phase    his own, so that no two of him (nor his likenesses) breathe together
     */
    public void pose(float age, float spin, float ringTurn, Skill skill, float ticks, boolean small, boolean stunned, float change, float dying, int phase) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 3.7F;
        wheel.zRot = -spin * Mth.DEG_TO_RAD;
        ring.zRot = ringTurn * Mth.DEG_TO_RAD;
        // How long his rays reach and how thick they are, against how they were built; how far he leans in; how hard he trembles.
        float reach = 1, thick = 1, lean = 0, tremble = 0;
        switch (skill) {
            case VOLLEY -> {
                SpinVolleyRules volley = small ? DecarabiaRules.SMALL_VOLLEY : DecarabiaRules.VOLLEY;
                float windup = volley.timing().windup(), length = volley.timing().ticks();
                if (ticks < windup) reach -= .08F * ease(ticks / windup);
                else if (ticks < length) {
                    float kick = 1 - (ticks - windup) % volley.interval() / volley.interval();
                    reach += .14F * kick * kick;
                }
            }
            case SPIN -> {
                SpinRules rules = small ? DecarabiaRules.SMALL_SPIN : DecarabiaRules.SPIN;
                lean = ease(ticks / rules.timing().windup()) * (1 - ease((ticks - rules.timing().ticks()) / 6));
            }
            case SPLIT -> {
                float windup = DecarabiaRules.SPLIT.windup();
                tremble = ease(ticks / windup) * (1 - ease((ticks - windup) / 4));
                reach += .3F * tremble;
            }
            default -> {}
        }
        star.xRot += .45F * lean;
        if (tremble > 0) {
            star.x += .35F * tremble * Mth.sin(time * 2.3F);
            star.y += .35F * tremble * Mth.cos(time * 2.9F);
        }
        if (stunned) {
            star.xRot += .3F * Mth.sin(time * .21F);
            star.zRot += .25F * Mth.sin(time * .13F + 1);
            star.y += .8F * Mth.sin(time * .1F);
            reach -= .1F;
        }
        if (dying > 0) {
            star.xRot += .5F * ease(dying / .7F);
            reach *= 1 - .35F * ease(dying);
        }
        // His change: the rays folding into the core and the ring closing up, the core dwindling, all by its middle.
        float fold = ease(change * 2), core = Math.max(.01F, 1 - .6F * fold), ringSize = Math.max(.01F, (1 - fold) * (1 + .02F * Mth.sin(time * .07F)));
        reach *= 1 - fold;
        thick *= 1 - .6F * fold;
        star.xScale = star.yScale = star.zScale = core;
        ring.xScale = ring.yScale = ringSize;
        ring.zScale = 1;
        ring.visible = fold < .99F;
        for (int ray = 0; ray < rays.length; ray++) {
            float breath = 1 + .04F * Mth.sin(time * .1F + ray * 1.3F);
            rays[ray].yScale = Math.max(.01F, reach * breath);
            rays[ray].xScale = rays[ray].zScale = Math.max(.01F, thick);
            rays[ray].visible = reach > .02F;
        }
    }
}
