package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BeamVolleyRules;
import com.miophas.corruption.core.demon.skill.BlinkRules;
import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.ChariotRules;
import com.miophas.corruption.core.demon.skill.PsychicRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Belial, the sixty-eighth spirit of the Goetia: a king, created next after Lucifer, who "appeareth in the Form of Two
 * beautiful Angels sitting in a Chariot of Fire" and "speaketh with a Comely Voice" (the Lesser Key of Solomon). As
 * rules: plain Java, no game code. What he does, and when, is decided here ({@link #choose}); the entity carries it out
 * with the skill modules every boss may share (core.demon.skill).
 *
 * <p>He is the two angels, one after the other, and both fly. The first is the <b>fair</b> one, "a fairer person lost
 * not Heav'n" (Milton): he must be brought down altogether, his health to nothing, before the second rises in his place,
 * whole; the second is the <b>hollow</b> one, what the fair face hid ("all was false and hollow"), six-winged, his face
 * "like a lightning of fire and his eyes full of darkness" (the Questions of Bartholomew).
 *
 * <p>The fair angel looses volleys of the corruption's bolts (his common blow), pours beams from his two hands, blinks
 * away when pressed, calls up a host of the corruption's remnants round his quarry, and drives his chariot of fire at it
 * through whatever stands in its way. The hollow angel does all that and more: six beams, from the eyes of his six
 * wings; volleys thicker and quicker; likenesses of himself, which are gone the moment he himself is struck, and he
 * stunned; and strikes at the mind, again and again.
 */
public final class BelialRules {
    /** Each angel's health (the hollow rises with all of it), armour, how far he follows, and that nothing throws him. */
    public static final double MAX_HEALTH = 400, ARMOR = 10, FOLLOW_RANGE = 64, KNOCKBACK_RESISTANCE = 1;
    /** How fast he flies at the most, in blocks a tick. */
    public static final double FLY_SPEED = .5;
    /** His box, in blocks, as either angel stands in the air (the wings not counted). */
    public static final float WIDTH = 1.5F, HEIGHT = 3.6F;
    /** How long the hollow angel takes to die. */
    public static final int DEATH_TICKS = 80;

    /** His two shapes: the fair angel, then the hollow. */
    public enum Form { FAIR, HOLLOW }

    /** What he does, as the client poses it. The last two only the hollow angel does. */
    public enum Skill {
        NONE, VOLLEY, BEAMS, BLINK, SUMMON, CHARIOT, CLONES, PSYCHIC;

        public boolean hollowOnly() { return this == CLONES || this == PSYCHIC; }
    }

    // ---- the fair angel's fall, and the hollow's rising ----
    /**
     * How long it takes, from the fair angel's being brought down to the hollow standing whole: through it nothing hurts
     * him, the fair angel falls away in its first half, and the hollow rises in its second, his health filling as he does.
     */
    public static final int CHANGE_TICKS = 100;

    /** His health this far through his change (0 as it begins, 1 as it ends): from nothing to all of it. */
    public static float healthAt(float max, float through) { return DantalionRules.healthAt(0, max, through); }

    // ---- where he keeps ----
    /** He keeps about this far from his quarry, drawing back nearer than NEAR and closing in further than FAR, this high over the ground. */
    public static final double KEEP_AT = 12, NEAR = 6, FAR = 22, HOVER_LOW = 3.5, HOVER_HIGH = 7;

    /** Which way he should move from his quarry: 1 closer, -1 away, 0 where he is. */
    public static int keep(double distance) { return distance > FAR ? 1 : distance < NEAR ? -1 : 0; }

    // ---- both angels' skills ----
    /** The fair angel's volley, his common blow: from four points round him, every 6 ticks for 1.5 s after 0.6 s, 20 bolts of 5 each. */
    public static final SpinVolleyRules VOLLEY = new SpinVolleyRules(new SkillTiming(12, 42, 40), 2, 32, 4, 6, 3, 1, 5, 0);
    /** The hollow angel's, thicker and quicker: from six points slowly turning round him, every 3 ticks for 1.9 s after 0.4 s, 78 bolts of 5 each. */
    public static final SpinVolleyRules HOLLOW_VOLLEY = new SpinVolleyRules(new SkillTiming(8, 45, 24), 2, 32, 6, 3, 4, 1.15, 5, 4);
    /**
     * His beams: gathering 1.5 s, then pouring for 3.5 s, following at 2 degrees a tick; 4 every 4 ticks to what is within
     * 1.3 of each. The fair angel pours two, from his hands; the hollow six, from the eyes of his wings.
     */
    public static final BeamVolleyRules BEAMS = new BeamVolleyRules(new BeamRules(new SkillTiming(30, 100, 300), 4, 40, 44, 1.3, 4, 4, 2F), 6, 2.6);
    public static final int FAIR_BEAMS = 2, HOLLOW_BEAMS = 6;
    /** He blinks 9 to 16 blocks from his quarry, 3 to 7 up: crowded by three, any player within 5, or a tenth of his health lost in 3 s. */
    public static final BlinkRules BLINK = new BlinkRules(new SkillTiming(6, 10, 140), 9, 16, 3, 7, 16, 4.5, 3, 60, .1F, 5);
    /** The likenesses the remnants he calls up take, in turn. */
    public static final List<String> LOOKS = List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:vindicator", "minecraft:husk", "minecraft:stray",
            "minecraft:pillager");
    /** His host: eight remnants rising in a ring round where his quarry stood, never more than sixteen of his about. */
    public static final SummonRules SUMMON = new SummonRules(new SkillTiming(30, 40, 560), 8, 16, 4.5, LOOKS, true);
    /**
     * His chariot of fire: 2 s to gather, its line marked; then from 24 blocks short of his quarry, through it and 12
     * beyond, at 1.2 blocks a tick: 20 to everything within 2.4 of its path, thrown and left burning for 8 s, and every
     * block softer than obsidian in its way broken.
     */
    public static final ChariotRules CHARIOT = new ChariotRules(new SkillTiming(40, 41, 480), 6, 48, 24, 12, 1.2, 2.4, 20, 1.5, 8, ChargeRules.OBSIDIAN);

    // ---- the hollow angel's own ----
    /** His likenesses: gathering 1.2 s, then four of him, himself among them; its cooldown starts when they are gone (CLONES_COOLDOWN). */
    public static final SkillTiming CLONES = new SkillTiming(24, 25, 0);
    /** How many likenesses he leaves, how much health each has, and how far round where he was the five of them stand, in blocks. */
    public static final int LIKENESSES = 4;
    public static final double LIKENESS_HEALTH = 30, SCATTER = 7;
    /** He calls up his likenesses only once hurt, at or below this share of his health. */
    public static final float CLONES_BELOW = .9F;
    /** How long they may go unfound (after which they are gone all the same, and he not stunned); how long he is stunned once found out; how long after he does not call them again. */
    public static final int CLONES_MOST = 600, STUN_TICKS = 140, CLONES_COOLDOWN = 900;
    /** His strike at the mind: 0.5 s to fix his quarry, coming round every 3 s; within 3 blocks of it, 4, Nausea 5 s, Darkness 2 s, Slowness II 2 s. */
    public static final PsychicRules PSYCHIC = new PsychicRules(new SkillTiming(10, 12, 60), 0, 36, 3, 4, 100, 40, 40, 1);

    /**
     * Where the index-th of the five of him goes as his likenesses appear, from where he was: {x, z} in blocks, the five
     * spread evenly round a circle, turned as {@code turn} (radians) says.
     */
    public static double[] scatter(int index, double turn) {
        double angle = turn + index * Math.PI * 2 / (LIKENESSES + 1);
        return new double[]{Math.cos(angle) * SCATTER, Math.sin(angle) * SCATTER};
    }

    /** His skills in the order the entity keeps them, NONE left out. */
    public static final List<Skill> SKILLS = List.of(Skill.VOLLEY, Skill.BEAMS, Skill.BLINK, Skill.SUMMON, Skill.CHARIOT, Skill.CLONES, Skill.PSYCHIC);

    /** The volley each shape looses. */
    public static SpinVolleyRules volley(Form form) { return form == Form.HOLLOW ? HOLLOW_VOLLEY : VOLLEY; }

    /** How many beams each shape pours. */
    public static int beams(Form form) { return form == Form.HOLLOW ? HOLLOW_BEAMS : FAIR_BEAMS; }

    /**
     * What he sees of a moment, to choose by.
     *
     * @param form     his shape now
     * @param distance how far his quarry is
     * @param sight    whether he can see it
     * @param health   his share of his health
     * @param unsafe   whether he stands in danger (see BlinkRules.unsafe)
     * @param called   how many of the remnants he called up are still about
     * @param split    whether his likenesses are about
     * @param ready    which of his skills have come round
     */
    public record Situation(Form form, double distance, boolean sight, float health, boolean unsafe, int called, boolean split, Set<Skill> ready) {
        public Situation { ready = ready.isEmpty() ? EnumSet.noneOf(Skill.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What he does next, if anything. In danger, with his blink come round, he blinks. Otherwise, his quarry in sight, each
     * choice as likely as its weight ({@code roll}, 0 to 1, picks), among what has come round and would serve:
     * <ul>
     *   <li><b>Either angel</b>: his volley, the likeliest; his beams; his host, the fair angel the more given to it, while
     *       there is room for more; his chariot.</li>
     *   <li><b>The hollow angel</b> besides: his likenesses, once he has been hurt; and his strikes at the mind, as likely as
     *       his volley.</li>
     * </ul>
     * While his likenesses are about he does only what they do ({@link #chooseLikeness}), so as to be one of them.
     */
    public static Skill choose(Situation now, double roll) {
        if (now.unsafe() && now.ready().contains(Skill.BLINK) && !now.split()) return Skill.BLINK;
        if (!now.sight()) return Skill.NONE;
        if (now.split()) return chooseLikeness(now.distance(), true, now.ready(), roll);
        boolean hollow = now.form() == Form.HOLLOW;
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        consider(now, options, weights, Skill.VOLLEY, 6, volley(now.form()).inRange(now.distance()));
        consider(now, options, weights, Skill.BEAMS, 4, BEAMS.beam().inRange(now.distance()));
        consider(now, options, weights, Skill.SUMMON, hollow ? 2 : 3, SUMMON.wanted(now.called()));
        consider(now, options, weights, Skill.CHARIOT, 3, CHARIOT.inRange(now.distance()));
        consider(now, options, weights, Skill.CLONES, 4, now.health() <= CLONES_BELOW);
        consider(now, options, weights, Skill.PSYCHIC, 6, PSYCHIC.inRange(now.distance()));
        return pick(options, weights, roll);
    }

    /** What one of the five does while his likenesses are about (they, and he among them): the hollow volley, or a strike at the mind. */
    public static Skill chooseLikeness(double distance, boolean sight, Set<Skill> ready, double roll) {
        if (!sight) return Skill.NONE;
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        if (ready.contains(Skill.VOLLEY) && HOLLOW_VOLLEY.inRange(distance)) { options.add(Skill.VOLLEY); weights.add(5); }
        if (ready.contains(Skill.PSYCHIC) && PSYCHIC.inRange(distance)) { options.add(Skill.PSYCHIC); weights.add(3); }
        return pick(options, weights, roll);
    }

    private static void consider(Situation now, List<Skill> options, List<Integer> weights, Skill skill, int weight, boolean serves) {
        if (!serves || !now.ready().contains(skill) || skill.hollowOnly() && now.form() != Form.HOLLOW) return;
        options.add(skill);
        weights.add(weight);
    }

    private static Skill pick(List<Skill> options, List<Integer> weights, double roll) {
        int total = 0;
        for (int weight : weights) total += weight;
        if (total == 0) return Skill.NONE;
        double pick = Math.max(0, Math.min(.999999, roll)) * total;
        for (int index = 0; index < options.size(); index++) {
            pick -= weights.get(index);
            if (pick < 0) return options.get(index);
        }
        return options.get(options.size() - 1);
    }

    private BelialRules() {}
}
