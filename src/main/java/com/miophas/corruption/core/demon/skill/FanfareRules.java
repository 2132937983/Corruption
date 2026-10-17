package com.miophas.corruption.core.demon.skill;

/**
 * A fanfare of the corruption's bolts out of a horn, as rules: a great many of them, each hurting hard, sprayed back and
 * forth across a quarry for as long as it plays. Through the wind-up the caster draws breath, its horn raised. Then on
 * every tick until it is over {@code perTick} bolts leave the horn in a spray of two arms that sweep across the quarry
 * scissor-wise, each as far as {@code sweep} degrees to either side of it, out and back in {@code sweepTicks}; every
 * {@code aimedEvery} ticks a fan of {@code aimed} more goes straight at it, {@code aimedSpread} degrees across. Each bolt
 * is a little above or below the one before, so the spray is a sheet and not a line. Every bolt flies {@code speed}
 * blocks a tick and hurts what it strikes for {@code damage}, and what is within {@code splash} of it besides. Out of the
 * way of both arms is the way to live through it: the arms leave the quarry's own line only as they cross it.
 */
public record FanfareRules(SkillTiming timing, double minRange, double maxRange, int perTick, double sweep, int sweepTicks, int aimedEvery, int aimed,
                           double aimedSpread, double speed, double damage, double splash) {
    /** How far above and below the quarry the spray reaches, in degrees. */
    public static final double RISE = 7;

    public FanfareRules {
        maxRange = Math.max(minRange, maxRange);
        perTick = Math.max(0, perTick);
        sweep = Math.max(0, sweep);
        sweepTicks = Math.max(2, sweepTicks);
        aimedEvery = Math.max(1, aimedEvery);
        aimed = Math.max(0, aimed);
        aimedSpread = Math.max(0, aimedSpread);
        speed = Math.max(.05, speed);
        damage = Math.max(0, damage);
        splash = Math.max(0, splash);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether it plays on this tick of the skill: after the wind-up, until it is over. */
    public boolean playing(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks(); }

    /**
     * Where the {@code index}-th bolt of the spray on this tick goes: {degrees across from straight at the quarry (to its
     * right, as the caster faces it), degrees up}. The bolts take the two arms by turns.
     */
    public double[] spray(int elapsed, int index) {
        double played = elapsed - timing.windup();
        double swing = Math.sin(Math.PI * 2 * played / sweepTicks);
        double across = (index % 2 == 0 ? 1 : -1) * sweep * swing;
        // Each bolt a little above or below the last, by the golden angle's share: no two alike, spread evenly.
        double share = (played * Math.max(1, perTick) + index) * 0.6180339887498949;
        double up = RISE * 2 * (share - Math.floor(share) - .5);
        return new double[]{across, up};
    }

    /** Whether a fan of bolts goes straight at the quarry on this tick of the skill. */
    public boolean volleys(int elapsed) { return playing(elapsed) && (elapsed - timing.windup()) % aimedEvery == 0; }

    /** How far across from straight at the quarry the {@code index}-th bolt of an aimed fan goes, in degrees. */
    public double volleyAcross(int index) { return aimed <= 1 ? 0 : -aimedSpread / 2 + aimedSpread * index / (aimed - 1); }

    /** How many bolts a whole fanfare looses. */
    public int bolts() {
        int played = timing.ticks() - timing.windup();
        return played * perTick + ((played - 1) / aimedEvery + 1) * aimed;
    }
}
