package com.miophas.corruption.core.demon.skill;

/** Per-use burrowing lifecycle. World movement and effects are supplied by the adapter. */
public final class BurrowState {
    public enum Phase { REAR, DIVE, TUNNEL, WARN, RISE, DONE }
    private Phase phase = Phase.DONE;
    private int warned;
    private boolean burst;
    public Phase phase() { return phase; }
    public int warned() { return warned; }
    public void begin() { phase = Phase.REAR; warned = 0; burst = false; }
    public void end() { phase = Phase.DONE; }
    public boolean tunnelling() { return phase == Phase.DIVE || phase == Phase.TUNNEL || phase == Phase.WARN || phase == Phase.RISE && !burst; }
    public void rear(BurrowRules rules, int elapsed) { if (phase == Phase.REAR && !rules.timing().windingUp(elapsed)) phase = Phase.DIVE; }
    public void dive(BurrowRules rules, double headY, double entryY) { if (phase == Phase.DIVE && headY < entryY - rules.depth() / 2) phase = Phase.TUNNEL; }
    public boolean tunnel(BurrowRules rules, double distance) {
        if (phase != Phase.TUNNEL || !rules.under(distance)) return false;
        phase = Phase.WARN; return true;
    }
    public void warn(BurrowRules rules) { if (phase == Phase.WARN && ++warned >= rules.warn()) phase = Phase.RISE; }
    /** True exactly once when it breaks the surface, even if this tick also completes the climb. */
    public boolean rise(BurrowRules rules, double headY, double groundY) {
        if (phase != Phase.RISE) return false;
        boolean strike = !burst && headY >= groundY;
        burst |= strike;
        if (headY >= groundY + rules.climb() - 2) phase = Phase.DONE;
        return strike;
    }
}
