package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Dantalion;
import com.miophas.corruption.core.demon.DantalionRules;
import com.miophas.corruption.core.demon.DantalionRules.Skill;
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
 * Dantalion's second shape, the mirror. Its geometry is data (see {@link GeoModel}, built by
 * gamemodels/dantalion/build_mirror.js); this finds the bones that move and poses them.
 *
 * <p>The frame is eight arcs, each a bone of its own, so that as he changes they fly together out of the air and the
 * mirror is made before the eyes; the masks round it glance about; shards of glass circle it; the spiral on its glass
 * shows only while he reaches for a mind or holds one (whatever else he does meanwhile), and turns the faster the
 * harder he holds.
 *
 * <p>As with the book, a skill goes on being posed after it is over, until he begins another, and every pose eases back
 * to rest by itself; how far the shards and the spiral have turned is wound on frame by frame by the renderer
 * ({@link DantalionWinding}), so a change of speed never makes them jump.
 */
public final class DantalionMirrorModel extends HierarchicalModel<Dantalion> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "dantalion_mirror"), "main");
    static final String GEOMETRY = "dantalion_mirror";
    static final int ARCS = 8, MASKS = 6, SHARDS = 6;
    /** How far round it its shards circle, in model pixels; drawn in to this as its beam gathers. */
    static final float CIRCLE = 22, DRAWN_IN = 13;
    /** How fast its shards circle it at rest, in radians a tick. */
    static final float ORBIT = .035F;

    // Seen by DantalionModelTest, which poses the baked model and looks at where the bones went.
    final ModelPart root, mirror, glass, spiral, crest, spike;
    final ModelPart[] all, dull, arcs, masks, shards;
    /** The winding of the Dantalion about to be drawn, set by the renderer; with none, its shards and spiral turn at their resting rates. */
    DantalionWinding winding;

    public DantalionMirrorModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        List<ModelPart> unlit = new ArrayList<>();
        bones.forEach((name, part) -> { if (!name.endsWith("_glow")) unlit.add(part); });
        dull = unlit.toArray(ModelPart[]::new);
        mirror = DantalionBookModel.need(bones, "mirror"); glass = DantalionBookModel.need(bones, "glass");
        spiral = DantalionBookModel.need(bones, "spiral"); crest = DantalionBookModel.need(bones, "crest"); spike = DantalionBookModel.need(bones, "spike");
        arcs = new ModelPart[ARCS];
        for (int index = 0; index < ARCS; index++) arcs[index] = DantalionBookModel.need(bones, "frame_" + index);
        masks = new ModelPart[MASKS];
        for (int index = 0; index < MASKS; index++) masks[index] = DantalionBookModel.need(bones, "mask_" + index);
        shards = new ModelPart[SHARDS];
        for (int index = 0; index < SHARDS; index++) shards[index] = DantalionBookModel.need(bones, "shard_" + index);
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    /** Draws only the pieces that glow, or everything again. The glowing pieces are the bones named _glow. */
    public void glowOnly(boolean only) { for (ModelPart part : dull) part.skipDraw = only; }

    private static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    @Override public void setupAnim(Dantalion entity, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - entity.tickCount, time = age + entity.getId() * 5.9F;
        float shardTurn = winding != null ? winding.shards : time * ORBIT, spiralTurn = winding != null ? winding.spiral : time * spiralRate(false);
        pose(age, entity.posedSkill(), entity.skillTicks(partial), entity.changed(partial), DantalionBookModel.dying(entity, partial), entity.getViewXRot(partial),
                entity.getId(), entity.holding(partial), shardTurn, spiralTurn);
    }

    /** Its pose holding no mind, its shards and spiral turning at their resting rates. */
    public void pose(float age, Skill skill, float ticks, float changed, float dying, float pitch, int phase) {
        float time = age + phase * 5.9F;
        pose(age, skill, ticks, changed, dying, pitch, phase, 0, time * ORBIT, time * spiralRate(false));
    }

    /**
     * @param age     ticks, for the motion that never stops
     * @param skill   what he is doing, and {@code ticks} since he began it
     * @param changed how far through his change he is, 0 to 1 (the mirror shows its second half, being made); 0 when he is not changing
     * @param dying   0 alive to 1 dead
     * @param pitch   degrees he looks down at his quarry
     * @param phase   his own, so that no two mirrors sway together
     * @param holding how hard he holds a mind now, 0 to 1
     * @param shardTurn how far round its shards have circled, in radians
     * @param spiralTurn how far round its spiral has turned, in radians
     */
    public void pose(float age, Skill skill, float ticks, float changed, float dying, float pitch, int phase, float holding,
                     float shardTurn, float spiralTurn) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 5.9F;
        float lift = 1.2F * Mth.sin(time * .07F), lean = 0, turn = 0, shake = 0, held = 0, circle = CIRCLE, stare = 0;
        float windup = DantalionBookModel.windup(skill);

        switch (skill) {
            case MIND_CONTROL, GREATER_MIND_CONTROL -> {
                // Reaching for a mind: the spiral swells out of the glass, the masks all stare out, and it leans in.
                held = reaching(ticks, windup);
                stare = held;
                lean = .1F * held;
            }
            case BEAM -> {
                // Gathering, it trembles and its shards are drawn in close and whirl; firing, it recoils a little, again and again.
                float gathering = gathering(ticks, windup), firing = firing(ticks, windup);
                shake = .03F * gathering + .015F * firing;
                circle = Mth.lerp(Math.max(gathering, firing), CIRCLE, DRAWN_IN);
                stare = Math.max(gathering, firing);
                lean -= .05F * firing * Math.max(0, Mth.sin(ticks * 1.6F));
            }
            case GAZE, GREATER_GAZE -> {
                float gazing = ease(ticks / 10) * (1 - ease((ticks - windup - 6) / 6));
                stare = gazing;
                lean = .12F * gazing;
                if (skill == Skill.GREATER_GAZE) shake = .03F * gazing;
            }
            case STRIKE, STORM -> {
                float falling = skill == Skill.STORM ? (DantalionRules.STORM.bolts() - 1) * DantalionRules.STORM.interval() + 4 : 4;
                float rearing = ease(ticks / 10) * (1 - ease((ticks - windup - falling) / 8));
                lean = -.45F * rearing;
                lift += 3 * rearing;
                circle = CIRCLE * (1 + .3F * rearing);
            }
            case SUMMON -> lean = .5F * ease(ticks / 12) * (1 - ease((ticks - windup - 4) / 8));
            case BLINK -> turn = Mth.TWO_PI * ease(ticks / windup) % Mth.TWO_PI;
            default -> {}
        }
        // Holding a mind, whatever else it does: the spiral turns on its glass and its masks stare out.
        held = Math.max(held, holding);
        stare = Math.max(stare, holding);

        // Being made, as he changes: the arcs of the frame fly in out of the air and lock together, the masks after
        // them, then the glass fills it and the crest and spike are set on; and it slows from a whirl.
        float making = changed <= 0 ? 1 : Mth.clamp((changed - .5F) / .5F, 0, 1);
        if (changed > 0) turn += 4 * Mth.PI * (1 - ease(making)) * (1 - ease(making));

        // Dying, its glass goes, its masks fall away, and it tips over.
        float shattered = ease((dying - .3F) / .5F);

        mirror.y -= lift * (1 - dying);
        mirror.xRot += pitch * Mth.DEG_TO_RAD * .7F * (1 - dying) + lean + 1.3F * ease(dying);
        mirror.yRot += turn;
        mirror.zRot += .05F * Mth.sin(time * .045F);
        if (shake > 0) { mirror.xRot += shake * Mth.sin(time * 3.1F); mirror.zRot += shake * Mth.sin(time * 4.3F); }

        for (int index = 0; index < ARCS; index++) {
            ModelPart arc = arcs[index];
            float coming = 1 - ease((making - index * .04F) / .55F);
            float out = 1 + 2.5F * coming;
            arc.x *= out;
            arc.y *= out;
            arc.zRot += Mth.PI * coming * (index % 2 == 0 ? 1 : -1);
            arc.xRot += .8F * coming;
        }
        for (int index = 0; index < MASKS; index++) {
            ModelPart mask = masks[index];
            float coming = 1 - ease((making - .3F - index * .04F) / .5F);
            mask.x *= 1 + 2 * coming;
            mask.y *= 1 + 2 * coming;
            float looking = 1 - stare;
            mask.yRot += .35F * Mth.sin(time * .03F + index * 1.7F) * looking;
            mask.xRot += .18F * Mth.sin(time * .04F + index) * looking;
            mask.y += 14 * shattered * (1 + index % 3);
            mask.zRot += (index % 2 == 0 ? 1 : -1) * 1.2F * shattered;
        }
        float filled = ease((making - .55F) / .35F) * (1 - shattered);
        glass.xScale = filled; glass.yScale = filled;
        float set = ease((making - .65F) / .35F);
        crest.xScale = set; crest.yScale = set; crest.zScale = set;
        spike.xScale = set; spike.yScale = set; spike.zScale = set;

        spiral.zRot = spiralTurn;
        float shown = held * (1 + .08F * Mth.sin(time * .9F)) * filled;
        spiral.xScale = shown; spiral.yScale = shown; spiral.zScale = Math.max(shown, .01F);

        for (int index = 0; index < SHARDS; index++) {
            ModelPart shard = shards[index];
            float angle = shardTurn + index * Mth.PI / 3, out = circle * (1 + 1.2F * (1 - making) + 1.5F * dying);
            shard.x = Mth.cos(angle) * out;
            shard.z = Mth.sin(angle) * out;
            shard.y = Mth.cos(angle) * out * .25F - 4 * Mth.sin(time * .07F + index) + 12 * dying;
            shard.yRot = -angle;
            shard.zRot = .4F * Mth.sin(time * .1F + index * 1.3F);
        }
    }

    /**
     * How fast its shards circle it, in radians a tick: faster as it reaches for a mind and while it holds one (the
     * faster the harder), and whirling as its beam gathers and fires.
     */
    static float shardRate(Skill skill, float ticks, float holding, boolean hard) {
        float windup = DantalionBookModel.windup(skill), rate = ORBIT;
        if (skill == Skill.MIND_CONTROL || skill == Skill.GREATER_MIND_CONTROL)
            rate = Mth.lerp(reaching(ticks, windup), ORBIT, skill == Skill.GREATER_MIND_CONTROL ? .09F : .06F);
        else if (skill == Skill.BEAM) rate = ORBIT + .12F * Math.max(gathering(ticks, windup), firing(ticks, windup));
        return Math.max(rate, Mth.lerp(holding, ORBIT, hard ? .09F : .06F));
    }

    /** How fast its spiral turns, in radians a tick: the faster the harder it holds. */
    static float spiralRate(boolean hard) { return hard ? .45F : .25F; }

    /** Reaching for a mind: swelling over eight ticks, and gone over six after the taking, unless the mind is held. */
    private static float reaching(float ticks, float windup) { return ease(ticks / 8) * (1 - ease((ticks - windup - 1) / 6)); }
    /** Its beam gathering, through the wind-up. */
    private static float gathering(float ticks, float windup) { return ticks < windup ? ease(ticks / windup) : 0; }
    /** Its beam firing, from the end of the wind-up, dying away over its last four ticks. */
    private static float firing(float ticks, float windup) {
        return ticks >= windup ? 1 - ease((ticks - DantalionRules.BEAM.timing().ticks() + 4) / 4) : 0;
    }
}
