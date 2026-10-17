package com.miophas.corruption.core.demon.skill;

/**
 * A grab with a tentacle (a tongue, a tendril), as rules: through the wind-up the creature reaches out towards its quarry,
 * if it is within {@code reach}; then it has hold of it and draws it in, {@code pull} blocks a tick towards itself (and
 * {@code down} blocks a tick under, for a creature that drags its prey into the water), for the rest of the skill or
 * until the quarry is within {@code near} of it; it hurts it for {@code damage} as it takes hold. A quarry it cannot see
 * as the wind-up ends gets away.
 */
public record GrabRules(SkillTiming timing, double reach, double near, double pull, double down, double damage) {
    public GrabRules {
        reach = Math.max(0, reach);
        near = Math.max(0, Math.min(reach, near));
        pull = Math.max(0, pull);
        down = Math.max(0, down);
        damage = Math.max(0, damage);
    }

    public boolean inRange(double distance) { return distance <= reach; }

    /** Whether it is drawing its quarry in on this tick of the skill. */
    public boolean holding(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks(); }

    /** How far its tentacle reaches out on this tick of the wind-up, 0 to 1: out to its quarry by the end of it. */
    public double reaching(double elapsed) { return timing.windup() <= 0 ? 1 : Math.max(0, Math.min(1, elapsed / timing.windup())); }

    /** Whether a quarry this far off is close enough that it stops drawing it in. */
    public boolean drawnIn(double distance) { return distance <= near; }
}
