package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BlinkRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.MindControlRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Dantalion, the seventy-first spirit, the duke of many faces who knows the thoughts of all and turns them, as rules:
 * plain Java, no game code. What he does, and when, is decided here ({@link #choose}); the entity carries it out with
 * the skill modules every boss may share (core.demon.skill).
 *
 * <p>He comes as a great book flying, and keeps his distance from his quarry, hovering a few blocks up. He turns its
 * mind with his <b>gaze</b> (an Eye of Providence opens over him; the eye flashes before its eyes, slowing and blinding
 * it) and with a <b>greater gaze</b> (Eyes of Providence all round it; longer, and withering too); he calls up a band
 * of the corruption's <b>remnants</b>; and he calls the corruption's <b>lightning</b> down on it, one bolt or a
 * <b>storm</b> of them, every place a bolt will fall marked on the ground first. All the while he weighs how safe he
 * stands, and when he is crowded, hurt hard or caught in what hurts, he <b>blinks</b> away.
 *
 * <p>Brought to half his health he <b>changes</b>: for three seconds nothing hurts him while the book tears itself
 * apart and becomes a great mirror, and he is made whole again, every last point of his health restored. As the mirror he pours a <b>beam</b> of the corruption from its face, and takes
 * hold of minds: <b>mind control</b> turns his quarry's view to him and holds it there; <b>greater mind control</b>
 * takes its feet as well, and walks it towards him. Held, it is what his beam and his lightning are for.
 */
public final class DantalionRules {
    public static final double MAX_HEALTH = 280, ARMOR = 6, FOLLOW_RANGE = 48, KNOCKBACK_RESISTANCE = 1;
    /** How fast he flies at the most, in blocks a tick. */
    public static final double FLY_SPEED = .3;
    /** His size as the book, and as the mirror, in blocks. */
    public static final float BOOK_WIDTH = 2.2F, BOOK_HEIGHT = 1.8F, MIRROR_WIDTH = 1.8F, MIRROR_HEIGHT = 3.2F;

    // ---- his phases ----
    /** At or below this share of his health he becomes the mirror, over this many ticks, through which nothing hurts him. */
    public static final float CHANGE_AT = .5F;
    public static final int CHANGE_TICKS = 60;
    /** How long he takes to die. */
    public static final int DEATH_TICKS = 80;
    /** As the mirror, his skills come round sooner, by this share of their cooldowns. */
    public static final double MIRROR_COOLDOWN = .8;

    public static boolean changes(float healthShare) { return healthShare <= CHANGE_AT; }

    /**
     * His health this far through his change (0 as it begins, 1 as it ends), from what it was as it began: the mirror
     * rises whole, his health filling back up as it is made, all of it by the end.
     */
    public static float healthAt(float from, float max, float through) {
        float share = Math.max(0, Math.min(1, through));
        return from + (max - from) * share * share * (3 - 2 * share);
    }

    // ---- where he keeps ----
    /** He keeps about this far from his quarry, drawing back nearer than {@link #NEAR} and closing in further than {@link #FAR}. */
    public static final double KEEP_AT = 12, NEAR = 7, FAR = 20;
    /** How high over the ground he hovers, in blocks. */
    public static final double HOVER_LOW = 3.5, HOVER_HIGH = 6;

    /** Which way he should move from his quarry: 1 closer, -1 away, 0 where he is. */
    public static int keep(double distance) { return distance > FAR ? 1 : distance < NEAR ? -1 : 0; }

    // ---- his skills ----

    /** What he does, as the client poses it. The first six he has as the book; the last three only as the mirror. */
    public enum Skill {
        NONE, GAZE, GREATER_GAZE, SUMMON, BLINK, STRIKE, STORM, BEAM, MIND_CONTROL, GREATER_MIND_CONTROL;

        public boolean mirrorOnly() { return this == BEAM || this == MIND_CONTROL || this == GREATER_MIND_CONTROL; }
    }

    /** His gaze, Andromalius's own (Slowness II for 8 seconds and Blindness for 5), with an Eye of Providence over him. */
    public static final GazeRules GAZE = new GazeRules(new SkillTiming(24, 25, 200), 3, 24,
            AndromaliusRules.SLOWNESS_TICKS, AndromaliusRules.SLOWNESS_LEVEL, AndromaliusRules.BLINDNESS_TICKS, 0, 0, 1);
    /** His greater gaze: five Eyes of Providence; Slowness II for 13 seconds, Blindness for 8, and Wither II for 7. */
    public static final GazeRules GREATER_GAZE = new GazeRules(new SkillTiming(32, 33, 420), 3, 24, 260, 1, 160, 140, 1, 5);
    /** Three remnants at a time, six at the most, in the likenesses of the dead of a raid and a crypt. */
    public static final SummonRules SUMMON = new SummonRules(new SkillTiming(30, 40, 520), 3, 6, 3.5,
            List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:vindicator", "minecraft:husk", "minecraft:stray", "minecraft:pillager"));
    /** He blinks 10 to 16 blocks from his quarry, 3 to 6 up, trying 16 places; crowded by three, or any player within 4.5. */
    public static final BlinkRules BLINK = new BlinkRules(new SkillTiming(6, 10, 180), 10, 16, 3, 6, 16, 4.5, 3, 60, .12F, 4.5);
    /** One bolt where his quarry stood, 1.5 seconds after it is marked: twelve within two and a half blocks. */
    public static final StrikeRules STRIKE = new StrikeRules(new SkillTiming(30, 34, 130), 2, 32, 1, 0, 0, 2.5, 12);
    /** Nine bolts within seven blocks of it, falling one every three ticks after a wind-up of 36: ten each. */
    public static final StrikeRules STORM = new StrikeRules(new SkillTiming(36, 36, 320), 4, 32, 9, 7, 3, 2.2, 10);
    /** The mirror's beam: 40 blocks long, 1.6 round; five every four ticks for three seconds; turning 2.2 degrees a tick. */
    public static final BeamRules BEAM = new BeamRules(new SkillTiming(30, 90, 300), 3, 36, 40, 1.6, 4, 5, 2.2F);
    /** His mind control: his quarry's view held on him for three seconds. */
    public static final MindControlRules MIND_CONTROL = new MindControlRules(new SkillTiming(24, 24, 380), 24, 60, false, .35F);
    /** His greater mind control: its view and its feet, for three and a half. */
    public static final MindControlRules GREATER_MIND_CONTROL = new MindControlRules(new SkillTiming(30, 30, 620), 24, 70, true, .45F);

    /** His skills in the order the entity keeps them, NONE left out. */
    public static final List<Skill> SKILLS = List.of(Skill.GAZE, Skill.GREATER_GAZE, Skill.SUMMON, Skill.BLINK, Skill.STRIKE, Skill.STORM,
            Skill.BEAM, Skill.MIND_CONTROL, Skill.GREATER_MIND_CONTROL);

    /**
     * What he sees of a moment, to choose by.
     *
     * @param distance  how far his quarry is
     * @param sight     whether he can see it
     * @param mirror    whether he is the mirror
     * @param unsafe    whether he stands in danger (BlinkRules.unsafe)
     * @param remnants  how many of the remnants he called are about
     * @param impaired  whether his quarry is already blinded or slowed
     * @param player    whether his quarry is a player (only a player's mind can be taken)
     * @param held      whether his quarry's mind is held now
     * @param ready     which of his skills have come round
     */
    public record Situation(double distance, boolean sight, boolean mirror, boolean unsafe, int remnants, boolean impaired, boolean player,
                            boolean held, Set<Skill> ready) {
        public Situation { ready = ready.isEmpty() ? EnumSet.noneOf(Skill.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What he does next, if anything. In danger, he blinks first of all, if he can. Otherwise, with his quarry in sight,
     * he picks among what has come round and would serve, each as likely as its weight ({@code roll}, 0 to 1, picks):
     * his gazes for what is not already blinded or slowed, remnants when there is room for more, lightning for what is in
     * its reach; and, as the mirror, his beam, and his hold on a player's mind. While its mind is held, the beam and the
     * lightning weigh twice as much: that is what holding it is for.
     */
    public static Skill choose(Situation now, double roll) {
        if (now.unsafe() && now.ready().contains(Skill.BLINK)) return Skill.BLINK;
        if (!now.sight()) return Skill.NONE;
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        int strike = now.held() ? 2 : 1;
        consider(now, options, weights, Skill.GREATER_GAZE, 3, !now.impaired() && GREATER_GAZE.inRange(now.distance()));
        consider(now, options, weights, Skill.GAZE, 4, !now.impaired() && GAZE.inRange(now.distance()));
        consider(now, options, weights, Skill.SUMMON, 3, SUMMON.wanted(now.remnants()));
        consider(now, options, weights, Skill.STORM, 3 * strike, STORM.inRange(now.distance()));
        consider(now, options, weights, Skill.STRIKE, 4 * strike, STRIKE.inRange(now.distance()));
        if (now.mirror()) {
            consider(now, options, weights, Skill.BEAM, 6 * strike, BEAM.inRange(now.distance()));
            consider(now, options, weights, Skill.GREATER_MIND_CONTROL, 3, now.player() && !now.held() && now.distance() <= GREATER_MIND_CONTROL.range());
            consider(now, options, weights, Skill.MIND_CONTROL, 4, now.player() && !now.held() && now.distance() <= MIND_CONTROL.range());
        }
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

    private static void consider(Situation now, List<Skill> options, List<Integer> weights, Skill skill, int weight, boolean serves) {
        if (!serves || !now.ready().contains(skill) || skill.mirrorOnly() && !now.mirror()) return;
        options.add(skill);
        weights.add(weight);
    }

    /** A cooldown as long as it is for him now: shorter as the mirror. */
    public static int cooldown(int ticks, boolean mirror) { return mirror ? (int) Math.round(ticks * MIRROR_COOLDOWN) : ticks; }

    private DantalionRules() {}
}
