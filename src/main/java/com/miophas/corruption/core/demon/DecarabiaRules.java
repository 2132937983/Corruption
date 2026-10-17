package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.DiveChainRules;
import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpinRules;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Decarabia, the sixty-ninth spirit of the Goetia: a marquis who comes as a star in a pentacle, who knows the virtues of
 * birds and of precious stones, and makes the birds come and sing before the conjurer as though they were tame. As
 * rules: plain Java, no game code. What he does, and when, is decided here ({@link #choose}); the entity carries it out
 * with the skill modules every boss may share (core.demon.skill).
 *
 * <p>He is the star itself, five-pointed, flying; brought to half his health he <b>changes</b>, as Dantalion does (for
 * three seconds nothing hurts him, and he is made whole again), into a great goshawk, the bird of his knowledge. He
 * flies in both shapes, the star hovering a few blocks over the ground about his quarry, the goshawk circling high over
 * it.
 *
 * <p>As the star: he spins about the way he faces and pours the corruption's bolts out of his five points at his quarry
 * (the <b>volley</b>); he flies at it spinning, a wheel of blades (the <b>spin</b>); and once he has been hurt he
 * <b>splits</b> into five stars smaller than himself, one of them him and the rest his likenesses, scattered round where
 * he was. Each does as the star does, with its volley and its spin. Until he himself is struck: then every likeness he
 * left comes back into him, he is the star again, whole in size, and he hangs <b>stunned</b> a long while, and does not
 * split again for a long while after. A likeness struck down is only gone; and if he is not found out in time, they
 * come back into him all the same, but he is not stunned.
 *
 * <p>As the goshawk (the star's skills left behind): he <b>screeches</b>, and everything near is stunned; he
 * <b>stoops</b> on the place his quarry stood, as Seere dives, and is up and away again at once; he <b>stoops</b> on
 * it again and again; he calls the corruption's <b>lightning</b> down round it; and he beats his wings and rains
 * sixty-four of the corruption's bolts on where it stood (the <b>barrage</b>). A quarry he has stunned is what his stoops
 * are for.
 */
public final class DecarabiaRules {
    public static final double MAX_HEALTH = 360, ARMOR = 8, FOLLOW_RANGE = 64, KNOCKBACK_RESISTANCE = 1;
    /** How fast he flies at the most, in blocks a tick: the star, and the goshawk. */
    public static final double STAR_SPEED = .45, HAWK_SPEED = .95;
    /** His box, in blocks: the star (as tall as it is wide), and the goshawk. */
    public static final float STAR_SIZE = 2.4F, HAWK_WIDTH = 2.0F, HAWK_HEIGHT = 1.4F;
    /** How long he takes to die. */
    public static final int DEATH_TICKS = 70;

    /** His two shapes. */
    public enum Form { STAR, HAWK }

    /** What he does, as the client poses it. The first three the star does; the rest the goshawk. */
    public enum Skill {
        NONE, VOLLEY, SPIN, SPLIT, SCREECH, DIVE, DIVES, STORM, BARRAGE;

        public boolean hawk() { return ordinal() >= SCREECH.ordinal(); }
    }

    // ---- his change ----
    /** At or below this share of his health the star becomes the goshawk, over this many ticks, through which nothing hurts him. */
    public static final float CHANGE_AT = .5F;
    public static final int CHANGE_TICKS = 60;

    public static boolean changes(float healthShare) { return healthShare <= CHANGE_AT; }

    /** His health this far through his change (0 as it begins, 1 as it ends), from what it was as it began: all of it by the end, as Dantalion's. */
    public static float healthAt(float from, float max, float through) { return DantalionRules.healthAt(from, max, through); }

    // ---- where he keeps ----
    /** The star keeps about this far from his quarry, drawing back nearer than NEAR and closing in further than FAR, this high over the ground. */
    public static final double KEEP_AT = 9, NEAR = 5, FAR = 16, HOVER_LOW = 3.5, HOVER_HIGH = 6;
    /** The goshawk circles this far from his quarry, this high over the ground. */
    public static final double SKY_KEEP = 12, SKY_LOW = 8, SKY_HIGH = 13;

    /** Which way the star should move from his quarry: 1 closer, -1 away, 0 where he is. */
    public static int keep(double distance) { return distance > FAR ? 1 : distance < NEAR ? -1 : 0; }

    // ---- the star's skills ----

    /** His volley: spinning up for 0.8 s, then every 4 ticks for 3 s a bolt from each of his five points at his quarry, up to 3 degrees off; 6 each. */
    public static final SpinVolleyRules VOLLEY = new SpinVolleyRules(new SkillTiming(16, 76, 140), 3, 26, 5, 4, 3, .9, 6, 13);
    /** His spin: spinning up for 0.6 s, then flying at his quarry for 2 s, striking everything within 3 blocks every quarter of a second for 5, and throwing it. */
    public static final SpinRules SPIN = new SpinRules(new SkillTiming(12, 52, 120), 0, 16, .55, 3, 5, 5, 1.1);
    /** He spins rather than shoots for a quarry closer than this. */
    public static final double SPIN_CLOSE = 7;
    /** His split: gathering himself for 1.2 s, then gone into five. Its cooldown starts when his likenesses come back into him (SPLIT_COOLDOWN). */
    public static final SkillTiming SPLIT = new SkillTiming(24, 25, 0);

    // ---- his likenesses ----
    /** How many likenesses he leaves, how big each is (and he, among them) beside the star he was, and how much health each has. */
    public static final int LIKENESSES = 4;
    public static final float SMALL = .45F;
    public static final double LIKENESS_HEALTH = 24;
    /** How far from where he split the five stars scatter, in blocks. */
    public static final double SCATTER = 5;
    /** He splits only once hurt, at or below this share of his health. */
    public static final float SPLIT_BELOW = .95F;
    /** How long they may go unfound: after this many ticks they come back into him all the same, and he is not stunned. */
    public static final int SPLIT_MOST = 600;
    /** How long he hangs stunned once he is found out, and how long after it he does not split again. */
    public static final int STUN_TICKS = 160, SPLIT_COOLDOWN = 900;
    /** A small star's volley and spin: fewer bolts, and weaker. */
    public static final SpinVolleyRules SMALL_VOLLEY = new SpinVolleyRules(new SkillTiming(12, 52, 120), 2, 22, 5, 8, 4, .8, 3, 13);
    public static final SpinRules SMALL_SPIN = new SpinRules(new SkillTiming(10, 40, 100), 0, 12, .5, 1.6, 3, 6, .6);

    /**
     * Where the index-th of the five stars goes as he splits, from where he was: {x, z} in blocks, the five spread evenly
     * round a circle, turned as {@code turn} (radians) says.
     */
    public static double[] scatter(int index, double turn) {
        double angle = turn + index * Math.PI * 2 / (LIKENESSES + 1);
        return new double[]{Math.cos(angle) * SCATTER, Math.sin(angle) * SCATTER};
    }

    // ---- the goshawk's skills ----

    /** His screech: drawing breath for 0.7 s, then everything within 18 blocks stunned for 2.5 s. */
    public static final ScreechRules SCREECH = new ScreechRules(new SkillTiming(14, 26, 260), 0, 22, 18, 50);
    /** His stoop: climbing for 0.8 s, then down at 1.9 a tick on where his quarry stood; 18 within 3.5 blocks, the ground torn up. No rest after it. */
    public static final DiveRules DIVE = new DiveRules(new SkillTiming(16, 60, 160), 4, 46, 3, 1.9, 3.5, 18, 1.4, 2.5, 18, .8);
    /** His stoops: three, one after another, each on where his quarry stands as it begins; 12 within 3 blocks each. */
    public static final DiveChainRules DIVES = new DiveChainRules(new DiveRules(new SkillTiming(10, 40, 0), 4, 46, 3, 2, 3, 12, 1.2, 1.5, 6, .6),
            3, 10, 30, 320);
    /** His lightning: nine bolts within seven blocks of his quarry, one every three ticks after 1.6 s; ten each. */
    public static final StrikeRules STORM = new StrikeRules(new SkillTiming(32, 32, 300), 4, 40, 9, 7, 3, 2.2, 10);
    /** His barrage: wings raised for 1.2 s, then 64 bolts, four a tick, coming down within six blocks of where his quarry stood a second later; 6 each, and within 1.2 of each. */
    public static final BarrageRules BARRAGE = new BarrageRules(new SkillTiming(24, 40, 420), 4, 40, 64, 4, 6, 20, 6, 1.2);

    /** His skills in the order the entity keeps them, NONE left out. */
    public static final List<Skill> SKILLS = List.of(Skill.VOLLEY, Skill.SPIN, Skill.SPLIT, Skill.SCREECH, Skill.DIVE, Skill.DIVES, Skill.STORM,
            Skill.BARRAGE);

    /**
     * What he sees of a moment, to choose by.
     *
     * @param form     his shape now
     * @param distance how far his quarry is
     * @param below    how far its feet are below his
     * @param sight    whether he can see it
     * @param health   his share of his health
     * @param stunned  whether it is stunned now
     * @param ready    which of his skills have come round
     */
    public record Situation(Form form, double distance, double below, boolean sight, float health, boolean stunned, Set<Skill> ready) {
        public Situation { ready = ready.isEmpty() ? EnumSet.noneOf(Skill.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What he does next, if anything; each choice as likely as its weight ({@code roll}, 0 to 1, picks), among what has
     * come round and would serve, his quarry in sight.
     * <ul>
     *   <li><b>The star</b>: his volley; his spin, the likelier for a quarry close; his split, once he has been hurt.</li>
     *   <li><b>The goshawk</b>: his screech, for a quarry not already stunned; his stoop and his stoops, three times as
     *       likely on a quarry stunned; his lightning; his barrage.</li>
     * </ul>
     */
    public static Skill choose(Situation now, double roll) {
        if (!now.sight()) return Skill.NONE;
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        if (now.form() == Form.STAR) {
            consider(now, options, weights, Skill.SPLIT, 4, now.health() <= SPLIT_BELOW);
            consider(now, options, weights, Skill.VOLLEY, 5, VOLLEY.inRange(now.distance()));
            consider(now, options, weights, Skill.SPIN, now.distance() <= SPIN_CLOSE ? 6 : 2, SPIN.inRange(now.distance()));
        } else {
            int stoop = now.stunned() ? 3 : 1;
            consider(now, options, weights, Skill.SCREECH, 3, !now.stunned() && SCREECH.inRange(now.distance()));
            consider(now, options, weights, Skill.DIVE, 4 * stoop, DIVE.serves(now.distance(), now.below()));
            consider(now, options, weights, Skill.DIVES, 3 * stoop, DIVES.serves(now.distance(), now.below()));
            consider(now, options, weights, Skill.STORM, 3, STORM.inRange(now.distance()));
            consider(now, options, weights, Skill.BARRAGE, 3, BARRAGE.inRange(now.distance()));
        }
        return pick(options, weights, roll);
    }

    /** What a small star does next (one of his likenesses, or he among them): its volley, or its spin for a quarry close. */
    public static Skill chooseSmall(double distance, boolean sight, Set<Skill> ready, double roll) {
        if (!sight) return Skill.NONE;
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        if (ready.contains(Skill.VOLLEY) && SMALL_VOLLEY.inRange(distance)) { options.add(Skill.VOLLEY); weights.add(4); }
        if (ready.contains(Skill.SPIN) && SMALL_SPIN.inRange(distance)) { options.add(Skill.SPIN); weights.add(distance <= SPIN_CLOSE ? 5 : 1); }
        return pick(options, weights, roll);
    }

    private static void consider(Situation now, List<Skill> options, List<Integer> weights, Skill skill, int weight, boolean serves) {
        if (!serves || !now.ready().contains(skill) || skill.hawk() != (now.form() == Form.HAWK)) return;
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

    private DecarabiaRules() {}
}
