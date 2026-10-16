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
 * Dantalion's first shape, the book. Its geometry is data (see {@link GeoModel}, built by
 * gamemodels/dantalion/build_book.js); this finds the bones that move and poses them.
 *
 * <p>Everything that opens turns on the one upright hinge at the spine: the covers, each with its block of pages, and
 * the four loose leaves. How far the book stands open is its openness, 0 shut to 1 flat open; at rest it stands open in
 * a V (25 degrees from flat each way) and breathes a little. The leaves flip over from the right block to the left one
 * after another, and then back, the faster the more he is about. The hinge is at the front of the spine, so as the
 * covers close their inner edges swing out to either side of it and forward: the spine widens and comes forward to
 * meet them. As in every model of this space, a positive X turn leans an upright bone forward; here a positive Y turn
 * swings a cover's edge forward on the left and back on the right.
 *
 * <p>A skill goes on being posed after it is over, as he settles back from it, until he begins another; every pose
 * eases back to rest by itself. The leaves turn the faster the more he is about: how far they have turned is wound on
 * frame by frame by the renderer ({@link DantalionWinding}), so a change of speed never makes them jump.
 */
public final class DantalionBookModel extends HierarchicalModel<Dantalion> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "dantalion_book"), "main");
    static final String GEOMETRY = "dantalion_book";
    /** How far open the book stands at rest: 25 degrees short of flat each way. */
    static final float REST_OPEN = 1 - 25F / 90;
    /** Shut, the covers stop a little short of meeting, so the pages between them never pass through each other. */
    static final float SHUT = .06F;
    static final int LEAVES = 4, LOOSE = 4;
    /** How far round him his loose pages circle, in model pixels. */
    static final float CIRCLE = 24;
    /** How fast the leaves turn over at rest, in leafing beats a tick. */
    static final float LEAF_RATE = .02F;

    // Seen by DantalionModelTest, which poses the baked model and looks at where the bones went.
    final ModelPart root, book, spine, leftCover, rightCover, eye, ribbon, ribbonEnd, clasp;
    final ModelPart[] all, dull, leaves, loose;
    /** The winding of the Dantalion about to be drawn, set by the renderer; with none, the leaves turn at their resting rate. */
    DantalionWinding winding;

    public DantalionBookModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        List<ModelPart> unlit = new ArrayList<>();
        bones.forEach((name, part) -> { if (!name.endsWith("_glow")) unlit.add(part); });
        dull = unlit.toArray(ModelPart[]::new);
        book = need(bones, "book"); spine = need(bones, "spine"); leftCover = need(bones, "cover_left"); rightCover = need(bones, "cover_right"); eye = need(bones, "eye");
        ribbon = need(bones, "ribbon"); ribbonEnd = need(bones, "ribbon_1"); clasp = need(bones, "clasp");
        leaves = new ModelPart[LEAVES];
        for (int index = 0; index < LEAVES; index++) leaves[index] = need(bones, "leaf_" + index);
        loose = new ModelPart[LOOSE];
        for (int index = 0; index < LOOSE; index++) loose[index] = need(bones, "loose_" + index);
    }

    static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Dantalion's geometry has no bone " + name);
        return bone;
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    /** Draws only the pieces that glow, or everything again. The glowing pieces are the bones named _glow. */
    public void glowOnly(boolean only) { for (ModelPart part : dull) part.skipDraw = only; }

    private static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    @Override public void setupAnim(Dantalion book, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - book.tickCount;
        float leafCycle = winding != null ? winding.leaves : (age + book.getId() * 7.3F) * LEAF_RATE;
        pose(age, book.posedSkill(), book.skillTicks(partial), book.changed(partial), dying(book, partial), book.getViewXRot(partial), book.getId(), leafCycle);
    }

    /** How far through his death he is, 0 alive to 1 dead. */
    static float dying(Dantalion entity, float partial) {
        return entity.deathTime > 0 ? Mth.clamp((entity.deathTime + partial) / DantalionRules.DEATH_TICKS, 0, 1) : 0;
    }

    /** His pose with the leaves turning at their resting rate. */
    public void pose(float age, Skill skill, float ticks, float changed, float dying, float pitch, int phase) {
        pose(age, skill, ticks, changed, dying, pitch, phase, (age + phase * 7.3F) * LEAF_RATE);
    }

    /**
     * @param age     ticks, for the motion that never stops
     * @param skill   what he is doing, and {@code ticks} since he began it
     * @param changed how far through his change he is, 0 to 1 (the book shows its first half); 0 when he is not changing
     * @param dying   0 alive to 1 dead
     * @param pitch   degrees he looks down at his quarry
     * @param phase   his own, so that no two books flutter together
     * @param leafCycle how far the leaves have turned, in leafing beats (see {@link #flipped})
     */
    public void pose(float age, Skill skill, float ticks, float changed, float dying, float pitch, int phase, float leafCycle) {
        for (ModelPart part : all) part.resetPose();
        float time = age + phase * 7.3F;
        float open = REST_OPEN + .05F * Mth.sin(time * .07F), lift = 1.4F * Mth.sin(time * .08F), lean = 0, turn = 0, shake = 0, eyeOpen = 0;
        float windup = windup(skill);

        switch (skill) {
            case GAZE, GREATER_GAZE -> {
                // Thrown open flat, risen, the eye on its page opening as the gaze gathers; the greater gaze shudders.
                float gathering = gathering(ticks, windup);
                open = Mth.lerp(gathering, open, 1);
                lift += 3 * gathering;
                eyeOpen = ease(ticks / windup) * (1 - ease((ticks - windup - 6) / 5));
                if (skill == Skill.GREATER_GAZE) { shake = .05F * gathering; turn = .15F * Mth.sin(ticks * .4F) * gathering; }
            }
            case SUMMON -> {
                // Bowed forward, its pages turned down to the circle under it.
                float bowing = bowing(ticks, windup);
                lean = .75F * bowing;
                open = Mth.lerp(bowing, open, .92F);
            }
            case STRIKE, STORM -> {
                // Reared back and up, its pages to the sky, jolting as each bolt falls.
                float rearing = rearing(skill, ticks, windup);
                lean = -.6F * rearing;
                lift += 4 * rearing;
                open = Mth.lerp(rearing, open, 1);
                if (ticks >= windup) shake = .04F * rearing * Math.max(0, Mth.sin((ticks - windup) * 1.05F));
            }
            case BLINK -> {
                // Snapped shut and whirled once round as he goes; there again, it opens.
                open = Mth.lerp(snapping(ticks), open, SHUT);
                turn = Mth.TWO_PI * ease(ticks / windup) % Mth.TWO_PI;
            }
            default -> {}
        }

        // His change: slammed shut, shaking harder and harder, whirling faster and faster, his loose pages flung off.
        float tearing = changed <= 0 ? 0 : Mth.clamp(changed / .5F, 0, 1);
        float flung = 0;
        if (tearing > 0) {
            open = Mth.lerp(ease(tearing / .15F), open, SHUT);
            shake = Math.max(shake, .12F * tearing);
            turn += 9 * tearing * tearing;
            lift += 3 * tearing;
            flung = ease(tearing);
            eyeOpen = 0;
        }
        // Dying, it sags half shut and tips over.
        if (dying > 0) {
            open = Mth.lerp(ease(dying / .6F), open, .25F);
            lean += 1.3F * ease(dying);
            lift *= 1 - dying;
        }

        book.y -= lift;
        book.xRot += pitch * Mth.DEG_TO_RAD * .6F * (1 - dying) + lean;
        book.yRot += turn;
        book.zRot += .04F * Mth.sin(time * .05F);
        if (shake > 0) { book.xRot += shake * Mth.sin(time * 2.9F); book.zRot += shake * Mth.sin(time * 3.7F); }

        float swing = Mth.HALF_PI * (1 - Mth.clamp(open, 0, 1));
        leftCover.yRot = swing;
        rightCover.yRot = -swing;
        // The covers are two pixels thick and lie three to five behind the hinge, so their inner edges stand out to
        // either side of it by five times the sine of the swing and back from it by three times its cosine.
        spine.xScale = Math.max(1, 2.5F * Mth.sin(swing));
        spine.z += 3 * (Mth.cos(swing) - 1);
        eye.yScale = .12F + .88F * eyeOpen;
        clasp.yRot += .25F * Mth.sin(time * .11F);
        ribbon.xRot += .2F * Mth.sin(time * .09F) - .3F * lean;
        ribbonEnd.xRot += .25F * Mth.sin(time * .09F - 1);
        ribbon.zRot += .12F * Mth.sin(time * .07F);

        // The leaves: one after another over to the left, then one after another back, each flip a third of its beat.
        float right = -swing, left = -Mth.PI + swing, cycle = ((leafCycle % (2 * LEAVES)) + 2 * LEAVES) % (2 * LEAVES);
        for (int index = 0; index < LEAVES; index++) {
            float over = flipped(index, cycle);
            leaves[index].yRot = Mth.lerp(over, right, left);
            float lifting = Mth.sin(over * Mth.PI);
            leaves[index].zRot += .12F * lifting;
            leaves[index].xRot += .08F * lifting * Mth.sin(time * .5F + index);
        }

        // The loose pages circle him, fluttering; flung off as he changes, drifting away as he dies.
        for (int index = 0; index < LOOSE; index++) {
            ModelPart page = loose[index];
            float angle = time * .035F + index * Mth.HALF_PI, out = CIRCLE * (1 + 1.5F * flung + .8F * dying);
            page.x = Mth.cos(angle) * out;
            page.z = Mth.sin(angle) * out;
            page.y = -3 * Mth.sin(time * .06F + index * 1.7F) - 6 * flung + 10 * dying;
            page.yRot = -angle + Mth.HALF_PI;
            page.xRot = .35F * Mth.sin(time * .2F + index);
            page.zRot = .25F * Mth.sin(time * .13F + index * 2.1F);
        }
    }

    /**
     * How far over to the left the index-th leaf is, 0 to 1, at this point of the leafing cycle (0 to 2 x leaves): over
     * the first half the leaves go over one by one, over the second they come back one by one, the last over the
     * first back. Each turns over in the first third of its beat and then lies still.
     */
    static float flipped(int index, float cycle) {
        int beat = (int) Math.floor(cycle);
        float within = ease((cycle - beat) / .33F);
        if (beat < LEAVES) return index < beat ? 1 : index == beat ? within : 0;
        int back = 2 * LEAVES - 1 - beat;
        return index < back ? 1 : index == back ? 1 - within : 0;
    }

    /**
     * How fast the leaves turn over, in leafing beats a tick: the faster the more he is about (thrown open to gaze,
     * bowed to call up the dead, reared to call down lightning); still as he dies.
     */
    static float leafRate(Skill skill, float ticks, float dying) {
        if (dying > 0) return 0;
        float windup = windup(skill);
        return LEAF_RATE * switch (skill) {
            case GAZE, GREATER_GAZE -> 1 + 4 * gathering(ticks, windup);
            case SUMMON -> 1 + 3 * bowing(ticks, windup);
            case STRIKE, STORM -> 1 + 2 * rearing(skill, ticks, windup);
            default -> 1;
        };
    }

    /** Through a gaze: thrown open over ten ticks, and settling back over six once the gaze has struck and six more gone by. */
    private static float gathering(float ticks, float windup) { return ease(ticks / 10) * (1 - ease((ticks - windup - 6) / 6)); }
    /** Through a summoning: bowed over twelve ticks, and back over eight once the dead have risen. */
    private static float bowing(float ticks, float windup) { return ease(ticks / 12) * (1 - ease((ticks - windup - 4) / 8)); }
    /** Calling down lightning: reared over ten ticks, and back over eight once the last bolt has fallen. */
    private static float rearing(Skill skill, float ticks, float windup) {
        float falling = skill == Skill.STORM ? (DantalionRules.STORM.bolts() - 1) * DantalionRules.STORM.interval() + 4 : 4;
        return ease(ticks / 10) * (1 - ease((ticks - windup - falling) / 8));
    }
    /** Blinking: snapped shut over three ticks, and open again over six once he is there. */
    private static float snapping(float ticks) {
        return ease(ticks / 3) * (1 - ease((ticks - DantalionRules.BLINK.timing().ticks()) / 6));
    }

    /** The wind-up of the skill he is doing, in ticks, to pose by. */
    static float windup(Skill skill) {
        return switch (skill) {
            case GAZE -> DantalionRules.GAZE.timing().windup();
            case GREATER_GAZE -> DantalionRules.GREATER_GAZE.timing().windup();
            case SUMMON -> DantalionRules.SUMMON.timing().windup();
            case BLINK -> DantalionRules.BLINK.timing().windup();
            case STRIKE -> DantalionRules.STRIKE.timing().windup();
            case STORM -> DantalionRules.STORM.timing().windup();
            case BEAM -> DantalionRules.BEAM.timing().windup();
            case MIND_CONTROL -> DantalionRules.MIND_CONTROL.timing().windup();
            case GREATER_MIND_CONTROL -> DantalionRules.GREATER_MIND_CONTROL.timing().windup();
            case NONE -> 1;
        };
    }
}
