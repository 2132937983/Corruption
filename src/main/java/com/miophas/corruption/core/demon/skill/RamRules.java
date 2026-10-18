package com.miophas.corruption.core.demon.skill;

/**
 * A long body ramming down at where its quarry stood, through it and into the ground, and away under it, bursting out
 * again into the sky, as rules. Through the wind-up it rears and takes aim; at its end it marks where its quarry stands, and
 * from then on that place, not its quarry, is what it rams: straight at it at {@code speed} a tick, everything within
 * {@code width} of its head as it passes that is not of its side hurt for {@code damage} (each once) and thrown
 * {@code knockback} away. It does not stop there: it runs on {@code through} blocks past the mark along the same line, into
 * the ground, then turns away from its quarry under the ground ({@code depth} down) and runs {@code away} blocks further;
 * there it bursts straight up out of the ground, throwing up what is there (hurting what is within {@code burst} of it for
 * half as much), and climbs {@code climb} blocks into the sky, and is done.
 */
public record RamRules(SkillTiming timing, double minRange, double maxRange, double speed, double width, double damage, double knockback,
                       double through, double depth, double away, double burst, double climb) {
    public RamRules {
        maxRange = Math.max(minRange, maxRange);
        speed = Math.max(.1, speed);
        width = Math.max(0, width);
        through = Math.max(0, through);
        depth = Math.max(1, depth);
        away = Math.max(0, away);
        burst = Math.max(0, burst);
        climb = Math.max(0, climb);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /**
     * Where it runs to past its mark: {@code through} blocks on along the line it came by, from the mark {dx, dy, dz}, given
     * the line {dx, dy, dz} it came along (its length does not matter); but never higher than it would go into the ground.
     */
    public double[] past(double lineX, double lineY, double lineZ) {
        double length = Math.sqrt(lineX * lineX + lineY * lineY + lineZ * lineZ);
        if (length < 1e-9) return new double[]{0, -through, 0};
        double scale = through / length;
        return new double[]{lineX * scale, Math.min(-depth, lineY * scale), lineZ * scale};
    }

    /** Where it bursts out, {dx, dz} from its mark: {@code away} blocks on, the way it came (away from its quarry, beyond it). */
    public double[] outlet(double lineX, double lineZ) {
        double flat = Math.sqrt(lineX * lineX + lineZ * lineZ);
        if (flat < 1e-9) return new double[]{away, 0};
        return new double[]{lineX / flat * away, lineZ / flat * away};
    }

    /** What its burst out of the ground does to what it catches: half its ram's hurt. */
    public double burstDamage() { return damage / 2; }
}
