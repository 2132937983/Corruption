package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.BlightRules;
import com.miophas.corruption.core.demon.skill.CrescendoRules;
import com.miophas.corruption.core.demon.skill.FanfareRules;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.PsychicRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import com.miophas.corruption.core.demon.skill.UpheavalRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Amdusias, the sixty-seventh spirit of the Goetia: a great duke who comes first as a unicorn, at whose bidding trumpets
 * and every kind of instrument are heard and no one is seen playing, and the trees bow down; as rules: plain Java, no game
 * code. What he does, and when, is decided here ({@link #choose}); the entity carries it out with the skill modules every
 * boss may share (core.demon.skill).
 *
 * <p>He is the unicorn himself. He cannot fly, and he has one form only, from first to last. On the ground he is Seere's
 * match: the same hooves, the same blinks through his quarry and the same field of spikes ({@link #HOOVES}, {@link #BLINKS},
 * {@link #SPIKES}, Seere's own). He is the corruption's duke as well: he stamps and the ground about him turns to the
 * dormant corruption ({@link #BLIGHT}); he heaves the ground up under his quarry, breaks it open, or spears it with spikes
 * that fling it up into the air ({@link #UPHEAVAL}); he strikes at its mind ({@link #PSYCHIC}); his horn plays a fanfare of
 * the corruption's bolts, a great many, each hurting hard ({@link #FANFARE}); and rings of music swell out from him,
 * throwing back and hurting whatever they pass ({@link #CRESCENDO}).
 */
public final class AmdusiasRules {
    public static final double MAX_HEALTH = 420, ARMOR = 10, FOLLOW_RANGE = 64, KNOCKBACK_RESISTANCE = 1;
    /** How fast he gallops (the attribute; a horse's is .1125 to .3375). */
    public static final double SPEED = .45;
    /** His box, in blocks, and how high a step he gallops up. */
    public static final float WIDTH = 2F, HEIGHT = 3.2F;
    public static final double STEP_HEIGHT = 1.6;
    /** How long he takes to die, and the experience he leaves. */
    public static final int DEATH_TICKS = 70, EXPERIENCE = 120;
    /** Ticks he rests between one blow and the next. */
    public static final int REST = 12;

    /** What he is doing, as the client poses it. */
    public enum Skill { NONE, HOOVES, BLINKS, SPIKES, BLIGHT, UPHEAVAL, PSYCHIC, FANFARE, CRESCENDO }

    // ---- what he has of Seere's: the same blows on the ground ----

    /** His front hooves: Seere's, rearing for 0.6 s, then down on everything within 4.6 blocks before him, 16 and thrown. */
    public static final MeleeRules HOOVES = SeereRules.HOOVES;
    /** His blinks: Seere's, four through his quarry, 12 of the corruption's hurt each and the curse at its weakest along every line. */
    public static final PhaseDashRules BLINKS = SeereRules.BLINKS;
    /** His spikes: Seere's, a field 16 blocks long before him; 18, and Slowness III for 6 s. */
    public static final SpikeFieldRules SPIKES = SeereRules.SPIKES;

    // ---- his own ----

    /**
     * His blight: for a quarry within 10; rearing 0.8 s, then down: 8 and thrown within 7, and the ground within 7 turning
     * to the dormant corruption as the blight runs out over it in 0.7 s (the whole of it: 160 blocks at the most).
     */
    public static final BlightRules BLIGHT = new BlightRules(new SkillTiming(16, 34, 240), 10, 7, 160, 14, true, 8, .9, .35);
    /**
     * His upheaval: under a quarry 3 to 28 off, the ground three blocks round trembling 0.8 s; then risen three blocks
     * (flinging it up, 8), broken open three deep (8), or speared (12, flung higher).
     */
    public static final UpheavalRules UPHEAVAL = new UpheavalRules(new SkillTiming(16, 22, 180), 3, 28, 1.5, 3, 3, 8, .95, 12, 1.15);
    /** His strike at the mind: a quarry up to 32 off and everything within 3.5 of it; 8, Nausea 6 s, Darkness 3 s, Slowness II 3 s. */
    public static final PsychicRules PSYCHIC = new PsychicRules(new SkillTiming(14, 16, 140), 0, 32, 3.5, 8, 120, 60, 60, 1);
    /**
     * His fanfare: on a quarry 6 to 40 off, 1.5 s drawing breath; then for 3 s two bolts a tick in two arms sweeping 45
     * degrees either side of it and back each second and a half, and three straight at it every half second: 138 bolts,
     * each 7 of the corruption's hurt, at 1.1 a tick.
     */
    public static final FanfareRules FANFARE = new FanfareRules(new SkillTiming(30, 90, 340), 6, 40, 2, 45, 30, 10, 3, 16, 1.1, 7, 0);
    /**
     * His crescendo: for a quarry within 12; 1.2 s gathering, then three rings half a second apart, each out to 16 blocks
     * at 0.9 a tick; each 9 to what it passes, thrown 1.3 away and .45 up.
     */
    public static final CrescendoRules CRESCENDO = new CrescendoRules(new SkillTiming(24, 0, 260), 0, 12, 3, 10, .9, 16, .8, 3, 9, 1.3, .45);

    /** His blows in the order the entity keeps them. */
    public static final List<Skill> BLOWS = List.of(Skill.HOOVES, Skill.BLINKS, Skill.SPIKES, Skill.BLIGHT, Skill.UPHEAVAL, Skill.PSYCHIC, Skill.FANFARE,
            Skill.CRESCENDO);

    /**
     * What he sees of a moment, to choose by.
     *
     * @param distance   how far his quarry is
     * @param above      how far its feet are above his (below: less than none)
     * @param sight      whether he can see it
     * @param offDegrees how far round from the way he faces it is
     * @param near       how many creatures not of his side are within half his crescendo's reach of him
     * @param ready      which of his blows have come round
     */
    public record Situation(double distance, double above, boolean sight, double offDegrees, int near, Set<Skill> ready) {
        public Situation { ready = ready.isEmpty() ? EnumSet.noneOf(Skill.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What he does next, if anything; each choice as likely as its weight ({@code roll}, 0 to 1, picks). A quarry he cannot
     * see, he gallops after. Close before him, his hooves come first. Otherwise his blinks, his spikes (for a quarry
     * before him), his blight (the likelier the closer it is), his upheaval (the likelier for a quarry high above him),
     * his strike at the mind, his fanfare (the likelier the further off it is) and his crescendo (the likelier with
     * several about him, or one close).
     */
    public static Skill choose(Situation now, double roll) {
        if (!now.sight()) return Skill.NONE;
        if (now.ready().contains(Skill.HOOVES) && HOOVES.catches(now.distance(), now.offDegrees())) return Skill.HOOVES;
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        double distance = now.distance();
        consider(now, options, weights, Skill.BLINKS, 4, BLINKS.inRange(distance));
        consider(now, options, weights, Skill.SPIKES, 4, SPIKES.inRange(distance) && Math.abs(now.offDegrees()) <= 30);
        consider(now, options, weights, Skill.BLIGHT, 3 + (distance <= BLIGHT.radius() ? 3 : 0), BLIGHT.inRange(distance));
        consider(now, options, weights, Skill.UPHEAVAL, 4 + (now.above() > 2 ? 4 : 0), UPHEAVAL.inRange(distance));
        consider(now, options, weights, Skill.PSYCHIC, 3, PSYCHIC.inRange(distance));
        consider(now, options, weights, Skill.FANFARE, 3 + (distance > 14 ? 3 : 0), FANFARE.inRange(distance));
        consider(now, options, weights, Skill.CRESCENDO, 3 + (now.near() >= 2 ? 4 : 0) + (distance <= 8 ? 2 : 0), CRESCENDO.inRange(distance));
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
        if (!serves || !now.ready().contains(skill)) return;
        options.add(skill);
        weights.add(weight);
    }

    private AmdusiasRules() {}
}
