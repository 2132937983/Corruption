package com.miophas.corruption.core.energy;

/**
 * The corruption's devices that run on a charge of Corruption Energy, as rules: tools in the sense of EnergyTool, filled
 * on the arcane charger (or poured into by hand) and drawn on only by their own use. Plain Java.
 */
public final class EnergyDeviceRules {
    /**
     * The corrupted lantern. Held in either hand, it spends {@code cost} every {@code every} ticks while it has it; and
     * while it burns, its holder sees in the dark ({@code sight} ticks of night vision, renewed before they run low
     * enough to flicker) and the corruption's creatures within {@code radius} blocks glow through walls for
     * {@code glow} ticks, renewed as long as it burns.
     */
    public record Lantern(EnergyTool tool, long cost, int every, double radius, int sight, int glow) {}

    /**
     * The ichor injector. Used, it spends {@code cost}: it takes the corruption's curse off its user and heals them
     * {@code heal}; then it rests {@code cooldown} ticks. Without the charge for it, nothing happens.
     */
    public record Injector(EnergyTool tool, long cost, float heal, int cooldown) {}

    /** 20 KCE, a KCE at a time; 25 CE a second, so a full lantern burns for 800 seconds, over thirteen minutes. */
    public static final Lantern LANTERN = new Lantern(new EnergyTool(CorruptionEnergy.kilo(20), CorruptionEnergy.kilo(1)), 25, 20, 24, 300, 40);
    /** 30 KCE, a KCE at a time; 6 KCE a use, so five uses to a full charge; three hearts; ten seconds between. */
    public static final Injector INJECTOR = new Injector(new EnergyTool(CorruptionEnergy.kilo(30), CorruptionEnergy.kilo(1)), CorruptionEnergy.kilo(6), 6, 200);

    /** Whether a device holding this much can pay this much. */
    public static boolean affords(long held, long cost) { return cost >= 0 && held >= cost; }

    /** What a device holds after paying this much, if it could. */
    public static long paid(long held, long cost) { return affords(held, cost) ? held - cost : held; }

    /** How many times a full charge pays this much. */
    public static long uses(EnergyTool tool, long cost) { return cost <= 0 ? Long.MAX_VALUE : tool.capacity() / cost; }

    /** Whether the lantern's night vision, with this much left of it, wants renewing now (night vision flickers below 200 ticks). */
    public static boolean renewSight(int left, Lantern lantern) { return left < Math.max(220, lantern.sight() - lantern.every() * 2); }

    private EnergyDeviceRules() {}
}
