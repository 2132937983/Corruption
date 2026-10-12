package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantCombatProfile;
import com.miophas.corruption.core.corruption.RemnantRules;

public final class RemnantCombatApi {
    public static RemnantDefinition definition() { return RemnantRules.DEFINITION; }
    public static RemnantCombatProfile capture(double maxHealth, double damage) { return RemnantCombatProfile.capture(maxHealth, damage); }
    private RemnantCombatApi() {}
}
