package com.miophas.corruption.core.demon.skill;

import java.util.Arrays;

/**
 * A boss's skills as they stand: which one is running, and for how many ticks it has run; and how long each has to go
 * before it comes round again. Skills are known by their place in the boss's list. Only one runs at a time, and one
 * still coming round cannot be begun.
 */
public final class SkillBook {
    private final int[] cooling;
    private int running = -1, elapsed;

    public SkillBook(int skills) { cooling = new int[Math.max(0, skills)]; }

    public int size() { return cooling.length; }

    /** The skill running, or -1 for none. */
    public int running() { return running; }

    public boolean busy() { return running >= 0; }

    /** Ticks the running skill has run, 0 on the tick it began. */
    public int elapsed() { return running >= 0 ? elapsed : 0; }

    /** Ticks this skill has to go before it comes round again. */
    public int cooling(int skill) { return cooling[skill]; }

    /** Whether this skill may be begun now: nothing else is running, and it has come round. */
    public boolean ready(int skill) { return running < 0 && cooling[skill] == 0; }

    /** Brings this skill round at once, however long it had to go. */
    public void comeRound(int skill) { cooling[skill] = 0; }

    /** Puts off this skill: it comes round no sooner than this many ticks from now. */
    public void holdOff(int skill, int ticks) { cooling[skill] = Math.max(cooling[skill], Math.max(0, ticks)); }

    /** Begins this skill, if it may be begun; whether it was. */
    public boolean begin(int skill) {
        if (!ready(skill)) return false;
        running = skill;
        elapsed = 0;
        return true;
    }

    /** A tick passes for every skill that is coming round. */
    public void cool() { for (int skill = 0; skill < cooling.length; skill++) if (cooling[skill] > 0) cooling[skill]--; }

    /** The running skill's tick to carry out now, counted from 0; it then moves on to the next. -1 if none runs. */
    public int advance() { return running < 0 ? -1 : elapsed++; }

    /** The running skill is over, run its course or broken off; it comes round again in this many ticks. */
    public void finish(int cooldown) {
        if (running < 0) return;
        cooling[running] = Math.max(0, cooldown);
        running = -1;
        elapsed = 0;
    }

    /** How long each skill has to go, to save. */
    public int[] save() { return cooling.clone(); }

    /** Sets how long each skill has to go, from what was saved: as many as both know of, each held to none or more. */
    public void load(int[] saved) {
        Arrays.fill(cooling, 0);
        for (int skill = 0; skill < Math.min(cooling.length, saved.length); skill++) cooling[skill] = Math.max(0, saved[skill]);
    }
}
