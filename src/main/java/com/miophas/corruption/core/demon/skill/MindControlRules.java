package com.miophas.corruption.core.demon.skill;

/**
 * Taking hold of a mind, as rules. Through the wind-up the boss reaches for a player within {@code range} that it can
 * see; then, for {@code holds} ticks, their view is drawn round to the boss however they turn, {@code grip} of the way
 * every frame, so that they cannot look away. Held harder ({@code moves}), their feet are not their own either: they
 * walk where they are made to, on towards the boss, weaving, and can neither jump nor crouch.
 *
 * <p>The skill itself is only the reaching and the taking: once the mind is taken, the hold outlasts it, and the boss is
 * free to do other things to the player it holds. That is what holding them is for.
 */
public record MindControlRules(SkillTiming timing, double range, int holds, boolean moves, float grip) {
    public MindControlRules {
        holds = Math.max(1, holds);
        grip = Math.max(0, Math.min(1, grip));
        // It runs at least to the taking, at the end of its wind-up.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + 1), timing.cooldown());
    }

    /** An angle (degrees) drawn towards another by this share of the way, the short way round. */
    public static float pull(float from, float to, float share) { return from + (float) BeamRules.wrap(to - from) * share; }

    /** How far a held player's walk weaves across their way at this tick of the hold, -1 to 1: a slow sway. */
    public static float weave(float ticks) { return (float) Math.sin(ticks * .15) * .6F; }
}
