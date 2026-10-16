package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BeamVolleyRules;
import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Seere, the seventieth spirit of the Goetia: a prince who comes on a winged horse, goes and comes in a moment, and
 * carries anything anywhere across the earth in the blink of an eye; as rules: plain Java, no game code. What he does,
 * and when, is decided here ({@link #choose}); the entity carries it out with the skill modules every boss may share
 * (core.demon.skill).
 *
 * <p>He is the winged horse himself, and goes three ways: on the ground at a gallop faster than any horse; in the sky on
 * his wings; and under the earth, through it as through air, to rise up out of it beside his quarry. On the ground he
 * strikes with his front hooves, blinks back and forth through his quarry leaving the corruption's curse along every
 * line he blinks, and throws up a field of spikes before him. In the sky he dives on his quarry, tearing the ground up
 * where he strikes, and pours beams on it out of magic circles: one at full health, and one more for every quarter of it
 * he has lost, four at the last. A quarry far off, or long out of his sight, he goes to under the earth.
 */
public final class SeereRules {
    public static final double MAX_HEALTH = 360, ARMOR = 8, FOLLOW_RANGE = 64, KNOCKBACK_RESISTANCE = 1;
    /** How fast he gallops (the attribute; a horse's is .1125 to .3375), and flies and goes under the earth, in blocks a tick at the most. */
    public static final double SPEED = .5, FLY_SPEED = .75, BURROW_SPEED = .7;
    /** His box, in blocks, and how high a step he gallops up. */
    public static final float WIDTH = 2.2F, HEIGHT = 2.9F;
    public static final double STEP_HEIGHT = 1.6;
    /** How long he takes to die. */
    public static final int DEATH_TICKS = 70;

    /** The way he goes. */
    public enum Mode { GROUND, SKY, UNDERGROUND }

    /** What he is doing, as the client poses it: a blow; or going from one way to another, a shift. */
    public enum Skill {
        NONE, HOOVES, BLINKS, SPIKES, DIVE, BEAMS, TAKE_OFF, LAND, BURROW, EMERGE;

        /** Whether this is his going from one way to another, not a blow. */
        public boolean shift() { return this == TAKE_OFF || this == LAND || this == BURROW || this == EMERGE; }
    }

    // ---- his blows ----

    /** His front hooves: rearing for 0.6 s, then down on everything within 4.6 blocks before him, 16 and thrown. */
    public static final MeleeRules HOOVES = new MeleeRules(new SkillTiming(12, 20, 30), 4.6, 140, 16, 1.4, .5);
    /**
     * His blinks: four, a quarter of a second apart, each five blocks through his quarry. Every blink hurts what it
     * passes for 12 of the corruption's hurt (which armour turns aside as it does any hurt), and lays the curse on it at
     * its weakest for a second and a half. So short a curse, laid again by every blink, does not bite until half a
     * second after the last: once a run, for a seventh of a creature's whole health and a little more. Through full
     * diamond armour a run takes 16 of a player's 20 health; a Warden keeps three quarters of its own.
     */
    public static final PhaseDashRules BLINKS = new PhaseDashRules(new SkillTiming(10, 0, 160), 3, 18, 4, 5, 5, 35, 1.6, 12, 30, 0, 4);
    /** His spikes: a field 16 blocks long before him, 3 across at the near end and 11 at the far; 18, and Slowness III for 6 s. */
    public static final SpikeFieldRules SPIKES = new SpikeFieldRules(new SkillTiming(20, 26, 200), 2, 16, 1.5, 16, 3, 11, 2.5, 18, 120, 2);
    /** His dive: 1.2 s climbing, then down at 1.6 a tick; 24 within 4 blocks where he strikes, and up to 48 blocks torn up within 3.5. */
    public static final DiveRules DIVE = new DiveRules(new SkillTiming(24, 64, 220), 6, 48, 5, 1.6, 4, 24, 1.6, 3.5, 48, .9);
    /** His beams: each 44 blocks long, 5 every 4 ticks for 3 s; up to four at once, their circles 2.6 blocks round. */
    public static final BeamVolleyRules BEAMS = new BeamVolleyRules(new BeamRules(new SkillTiming(30, 90, 320), 4, 40, 44, 1.4, 4, 5, 2.4F), 4, 2.6);

    // ---- his going from one way to another ----

    /** Ticks each shift takes. */
    public static final int TAKE_OFF_TICKS = 20, LAND_TICKS = 20, BURROW_TICKS = 16, EMERGE_TICKS = 16;
    /** Ticks at the least he keeps to the ground before he takes off again, and to the sky before he lands. */
    public static final int GROUND_LEAST = 160, SKY_LEAST = 200;
    /** How high over the ground he flies, and how far from his quarry he keeps there. */
    public static final double SKY_LOW = 7, SKY_HIGH = 12, SKY_KEEP = 14;
    /** He takes to the sky at once for a quarry more than this many blocks above him. */
    public static final double TAKE_OFF_FOR = 4;
    /** Under the earth: how far below the surface he goes, how near his quarry (along the ground) he rises, and how long at the most he stays under. */
    public static final double DEPTH = 3, RISE_NEAR = 3;
    public static final int UNDERGROUND_MOST = 120;
    /**
     * He goes under the earth for a quarry further off than this, or one he has not seen for so many ticks: before he
     * would forget it, as any creature forgets what it has not seen for three seconds.
     */
    public static final double BURROW_BEYOND = 26;
    public static final int UNSEEN_BURROW = 50;
    /** Rising out of the earth: everything within this many blocks is hurt for so much and thrown up, and the ground about torn up as by his dive. */
    public static final double RISE_RADIUS = 3.5, RISE_DAMAGE = 12;
    /** Ticks he rests between one thing and the next, on the ground and in the sky. */
    public static final int GROUND_REST = 10, SKY_REST = 16;

    /** His blows in the order the entity keeps them. */
    public static final List<Skill> BLOWS = List.of(Skill.HOOVES, Skill.BLINKS, Skill.SPIKES, Skill.DIVE, Skill.BEAMS);

    /**
     * What he sees of a moment, to choose by.
     *
     * @param mode        the way he goes now
     * @param distance    how far his quarry is
     * @param above       how far its feet are above his (below: less than none)
     * @param sight       whether he can see it
     * @param offDegrees  how far round from the way he faces it is
     * @param modeTicks   how long he has gone this way
     * @param unseenTicks how long he has not seen it
     * @param ready       which of his blows have come round
     */
    public record Situation(Mode mode, double distance, double above, boolean sight, double offDegrees, int modeTicks, int unseenTicks, Set<Skill> ready) {
        public Situation { ready = ready.isEmpty() ? EnumSet.noneOf(Skill.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What he does next, if anything; each choice as likely as its weight ({@code roll}, 0 to 1, picks).
     * <ul>
     *   <li><b>On the ground</b>: a quarry too far off or too long unseen, he goes to under the earth. Close before him,
     *       his hooves come first. Otherwise his blinks, his spikes (for a quarry before him), or taking off: the
     *       likelier once he has kept to the ground a while, and at once for a quarry high above him.</li>
     *   <li><b>In the sky</b>: his dive (on a quarry well below him), his beams, or landing, once he has flown a while
     *       or lost sight of it.</li>
     *   <li><b>Under the earth</b>: nothing; he rises when he is near enough (the entity says when).</li>
     * </ul>
     */
    public static Skill choose(Situation now, double roll) {
        List<Skill> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        switch (now.mode()) {
            case GROUND -> {
                if (now.distance() > BURROW_BEYOND || now.unseenTicks() >= UNSEEN_BURROW) return Skill.BURROW;
                if (!now.sight()) return Skill.NONE;
                if (now.ready().contains(Skill.HOOVES) && HOOVES.catches(now.distance(), now.offDegrees())) return Skill.HOOVES;
                if (now.above() > TAKE_OFF_FOR) return Skill.TAKE_OFF;
                consider(now, options, weights, Skill.BLINKS, 4, BLINKS.inRange(now.distance()));
                consider(now, options, weights, Skill.SPIKES, 4, SPIKES.inRange(now.distance()) && Math.abs(now.offDegrees()) <= 30);
                if (now.modeTicks() >= GROUND_LEAST) { options.add(Skill.TAKE_OFF); weights.add(3); }
            }
            case SKY -> {
                if (!now.sight()) return now.modeTicks() >= SKY_LEAST / 2 ? Skill.LAND : Skill.NONE;
                consider(now, options, weights, Skill.DIVE, 5, DIVE.serves(now.distance(), -now.above()));
                consider(now, options, weights, Skill.BEAMS, 5, BEAMS.beam().inRange(now.distance()));
                if (now.modeTicks() >= SKY_LEAST) { options.add(Skill.LAND); weights.add(3); }
            }
            case UNDERGROUND -> { return Skill.NONE; }
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
        if (!serves || !now.ready().contains(skill)) return;
        options.add(skill);
        weights.add(weight);
    }

    /** How many beams he fires at once, with this share of his health left: one at full health, four at the last. */
    public static int beams(float share) { return BEAMS.byHealth(share); }

    /** Whether he rises out of the earth now: near enough his quarry along the ground, or under it long enough, or with none to go to. */
    public static boolean rises(double alongGround, int modeTicks, boolean quarry) { return !quarry || alongGround <= RISE_NEAR || modeTicks >= UNDERGROUND_MOST; }

    /** How many ticks a shift takes. */
    public static int shiftTicks(Skill shift) {
        return switch (shift) {
            case TAKE_OFF -> TAKE_OFF_TICKS;
            case LAND -> LAND_TICKS;
            case BURROW -> BURROW_TICKS;
            case EMERGE -> EMERGE_TICKS;
            default -> 0;
        };
    }

    private SeereRules() {}
}
