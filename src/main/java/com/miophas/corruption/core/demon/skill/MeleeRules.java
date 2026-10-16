package com.miophas.corruption.core.demon.skill;

/**
 * A blow struck close in, as rules: through the wind-up the boss rears or draws back; at its end it strikes everything
 * within {@code reach} blocks of it inside an arc {@code arc} degrees wide about the way it faces, for {@code damage},
 * throwing it {@code knockback} blocks a tick away and {@code lift} up.
 */
public record MeleeRules(SkillTiming timing, double reach, double arc, double damage, double knockback, double lift) {
    public MeleeRules {
        reach = Math.max(0, reach);
        arc = Math.max(0, Math.min(360, arc));
    }

    /** Whether it would serve against what is this far off: within its reach. */
    public boolean inRange(double distance) { return distance <= reach; }

    /** Whether the blow catches what is this far off, this many degrees off the way the boss faces. */
    public boolean catches(double distance, double offDegrees) { return distance <= reach && Math.abs(BeamRules.wrap(offDegrees)) <= arc / 2; }
}
