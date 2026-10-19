package com.miophas.corruption.core.demon.skill;

/** Per-use ramming lifecycle, driven by measured distances instead of game entities. */
public final class RamState {
    public enum Phase { AIM, CHARGE, PLUNGE, UNDER, OUT, DONE }
    private Phase phase = Phase.DONE;
    private boolean burst;
    public Phase phase() { return phase; }
    public void begin() { phase = Phase.AIM; burst = false; }
    public void end() { phase = Phase.DONE; }
    public boolean tunnelling() { return phase == Phase.PLUNGE || phase == Phase.UNDER || phase == Phase.OUT && !burst; }
    public boolean aim(RamRules rules, int elapsed) {
        if (phase != Phase.AIM || rules.timing().windingUp(elapsed)) return false;
        phase = Phase.CHARGE; return true;
    }
    public void charge(RamRules rules, double distance) { if (phase == Phase.CHARGE && distance < rules.speed() * 1.5) phase = Phase.PLUNGE; }
    public void plunge(RamRules rules, double distance) { if (phase == Phase.PLUNGE && distance < rules.speed() * 1.5) phase = Phase.UNDER; }
    public void under(RamRules rules, double distance) { if (phase == Phase.UNDER && distance < rules.speed() * 1.5) phase = Phase.OUT; }
    public boolean out(RamRules rules, double headY, double groundY) {
        if (phase != Phase.OUT) return false;
        boolean strike = !burst && headY >= groundY;
        burst |= strike;
        if (headY >= groundY + rules.climb() - 2) phase = Phase.DONE;
        return strike;
    }
}
