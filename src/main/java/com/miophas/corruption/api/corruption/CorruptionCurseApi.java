package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptionCurseRules;
import java.util.List;

/** Read access to the corruption curse rules: its strength, its potions and the fluid that inflicts it. */
public final class CorruptionCurseApi {
    /** Curse duration that never runs out; the same value as Minecraft's infinite effect duration. */
    public static final int ENDLESS = CorruptionCurseRules.ENDLESS;

    /** Base potion first, then its long and strong variants. */
    public static List<CursePotionDefinition> potions() { return CorruptionCurseRules.potions(); }
    /** Shared name of every curse potion, so all variants use one set of translation keys. */
    public static String potionName() { return CorruptionCurseRules.POTION_NAME; }
    public static FlowingCorruptionDefinition flowingCorruption() { return CorruptionCurseRules.flowingCorruption(); }
    /** Multiplier on the curse's damage per pulse for an effect amplifier. */
    public static float damageScale(int amplifier) { return CorruptionCurseRules.damageScale(amplifier); }
    public static boolean fluidCurses(boolean player, boolean creativeOrSpectator, boolean remnant) {
        return CorruptionCurseRules.fluidCurses(player, creativeOrSpectator, remnant);
    }
    /** True when the fluid has nothing to add to a curse the entity already carries. */
    public static boolean fluidCurseHolds(int durationTicks, int amplifier, boolean curable) {
        return CorruptionCurseRules.fluidCurseHolds(durationTicks, amplifier, curable);
    }
    private CorruptionCurseApi() {}
}
