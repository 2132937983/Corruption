package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.CursePotionDefinition;
import com.miophas.corruption.api.corruption.FlowingCorruptionDefinition;
import java.util.List;

/** The corruption curse wherever it is handed out: its strength, the brewable potions and the flowing corruption fluid. */
public final class CorruptionCurseRules {
    public static final int ENDLESS = -1;
    public static final String POTION_NAME = "corruption_curse";
    private static final String BASE = "corruption:corruption_curse";
    // A curse pulses once a second; the base potion lasts as long as a Greed sigil's curse.
    private static final List<CursePotionDefinition> POTIONS = List.of(
            new CursePotionDefinition("corruption_curse", GreedRules.CURSE_TICKS, 0, "minecraft:awkward", "corruption:flowing_corruption_bucket"),
            new CursePotionDefinition("long_corruption_curse", GreedRules.CURSE_TICKS * 2, 0, BASE, "minecraft:redstone"),
            new CursePotionDefinition("strong_corruption_curse", GreedRules.CURSE_TICKS / 2, 1, BASE, "minecraft:glowstone_dust"));
    // Thicker and slower than water; any creature that enters is cursed for good.
    private static final FlowingCorruptionDefinition FLOWING_CORRUPTION =
            new FlowingCorruptionDefinition("flowing_corruption", ENDLESS, 0, false, false, 10, 3, 1, 1800, 3000);

    public static List<CursePotionDefinition> potions() { return POTIONS; }
    public static FlowingCorruptionDefinition flowingCorruption() { return FLOWING_CORRUPTION; }
    public static float damageScale(int amplifier) { return 1 + Math.max(0, amplifier); }

    public static boolean fluidCurses(boolean player, boolean creativeOrSpectator, boolean remnant) {
        return !remnant && !creativeOrSpectator && (player || !FLOWING_CORRUPTION.playersOnly());
    }
    /** A timed, weaker or curable curse is replaced; one already as lasting, strong and incurable as the fluid's is left alone. */
    public static boolean fluidCurseHolds(int durationTicks, int amplifier, boolean curable) {
        var fluid = FLOWING_CORRUPTION;
        boolean lasting = durationTicks == ENDLESS || fluid.curseTicks() != ENDLESS && durationTicks >= fluid.curseTicks();
        return lasting && amplifier >= fluid.curseAmplifier() && (fluid.curable() || !curable);
    }

    private CorruptionCurseRules() {}
}
