package com.miophas.corruption.core.demon.skill;

/**
 * A flood dragon's swoop, between the things it works (it is none of them, and does not count against how many of its kind
 * may be at work at once): from where it circles it dives on its quarry, through where it is, and climbs away again; and
 * whatever its body passes through on the way is hurt and broken, as it always is. It homes on its quarry as it dives, but
 * can turn only so sharply: whoever steps aside in time is passed.
 *
 * @param range     how far from its head its quarry may be for it to swoop
 * @param speed     how fast it dives and climbs, blocks a tick
 * @param turn      how sharply it can turn as it dives, degrees a tick
 * @param passed    how near its head comes to its quarry's middle before it pulls up
 * @param longest   the most ticks it dives before it pulls up anyway
 * @param climb     ticks it climbs away, after
 * @param rise      how high over its head it makes for as it climbs
 * @param restLeast the fewest ticks between one swoop of a dragon's and its next
 * @param restMost  the most
 * @param most      how many of a seal's dragons may swoop at once
 */
public record SwoopRules(double range, double speed, double turn, double passed, int longest, int climb, double rise, int restLeast, int restMost,
        int most) {
    public SwoopRules {
        if (restMost < restLeast) throw new IllegalArgumentException("A rest of at most less than its least");
    }

    public boolean inRange(double distance) { return distance <= range; }

    /** Whether one more may swoop, this many swooping already. */
    public boolean mayBegin(int swooping) { return swooping < most; }

    /** How long it rests after a swoop, {@code roll} (0 to 1) choosing between the least and the most. */
    public int rest(double roll) { return restLeast + (int) Math.min(restMost - restLeast, Math.floor(Math.max(0, roll) * (restMost - restLeast + 1))); }

    /**
     * Whether it has done diving: its head come near its quarry, or gone by it (its quarry close, and behind its head).
     *
     * @param distance how far its quarry's middle is from its head
     * @param ahead    how far ahead of its head its quarry's middle lies, along the way it faces (less than 0 behind it)
     */
    public boolean done(double distance, double ahead) { return distance <= passed || ahead < 0 && distance < passed * 4; }
}
