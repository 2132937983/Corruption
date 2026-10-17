package com.miophas.corruption.core.plant;

/**
 * What a malice vine does besides growing, as rules: it broods the corruption's elites, and it defends itself.
 *
 * <p>Its brood: every so often (the server's setting, {@link #BROOD_TICKS} by default: half a minute) it brings forth one elite, at
 * random from those that could live where it stands: one of the land's or the air's anywhere, one of the sea's only where
 * there is water near it ({@link #WATER_RANGE}). It broods only with a player near it ({@link #BROOD_PLAYER_RANGE}),
 * since no one is there to meet what it brings forth otherwise; and not while there are as many elites about it
 * ({@link #BROOD_COUNT_RANGE}) as the server allows ({@link #BROOD_CAP} by default). The elite stands a few blocks
 * from it ({@link #BROOD_NEAR} to {@link #BROOD_FAR}).
 *
 * <p>Its defence, a shockwave: when something not of its side comes close ({@link #shockTrigger}) or strikes it, it
 * gathers itself through {@link #SHOCK_WINDUP} ticks, then lets loose a ring of force round itself that hurts everything
 * not of its side within {@link #shockRadius} for {@link #SHOCK_DAMAGE} and throws it away and up; then it must wait
 * {@link #SHOCK_COOLDOWN} ticks to do it again. The bigger it has grown, the further it reaches.
 */
public final class MaliceVineRules {
    /** Ticks between one elite and the next, and how many elites may be about it, unless the server says otherwise. */
    public static final int BROOD_TICKS = 600, BROOD_CAP = 2;
    /** How near a player must be for it to brood, and how far about it the elites already there are counted, in blocks. */
    public static final double BROOD_PLAYER_RANGE = 32, BROOD_COUNT_RANGE = 32;
    /** How far from it an elite stands as it is brought forth, in blocks along the ground, and how far it looks for water. */
    public static final int BROOD_NEAR = 2, BROOD_FAR = 6, WATER_RANGE = 8;
    /** How many places it tries for an elite before it gives up until its next time. */
    public static final int BROOD_TRIES = 16;

    /** Its shockwave: the wind-up and the cooldown, in ticks; the hurt; and how hard it throws what it strikes away and up. */
    public static final int SHOCK_WINDUP = 14, SHOCK_COOLDOWN = 120;
    public static final double SHOCK_DAMAGE = 6, SHOCK_KNOCKBACK = 1.3, SHOCK_LIFT = .45;

    /** How far its shockwave reaches at a stage of its growth (0 to 4): three blocks to six. */
    public static double shockRadius(int stage) { return 3 + Math.max(0, Math.min(4, stage)) * .75; }

    /** How near something must come for it to let loose its shockwave at a stage of its growth: well within its reach. */
    public static double shockTrigger(int stage) { return shockRadius(stage) * .7; }

    /** Its brood's time, a tick on: how long it has waited for its next elite. */
    public static int waited(int waited, int step) { return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, waited) + Math.max(0, step)); }

    /** Whether it has waited long enough for its next elite: never, where the server has turned its brood off (an interval of 0). */
    public static boolean due(int waited, int interval) { return interval > 0 && waited >= interval; }

    /** Whether there is room about it for another elite, with so many already there. */
    public static boolean room(int about, int cap) { return about < cap; }

    /**
     * Which elite it brings forth, by a roll from 0 to 1, of {@code land} of the land's and the air's and {@code sea} of the
     * sea's (these only where there is water near it): its place in the land's then the sea's, or -1 if there is none.
     */
    public static int pick(int land, int sea, boolean water, double roll) {
        int choices = Math.max(0, land) + (water ? Math.max(0, sea) : 0);
        if (choices == 0) return -1;
        return Math.min(choices - 1, (int) Math.floor(Math.max(0, roll) * choices));
    }

    private MaliceVineRules() {}
}
