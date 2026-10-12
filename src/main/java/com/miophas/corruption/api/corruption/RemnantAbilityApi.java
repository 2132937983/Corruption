package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantAbilityRules;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

/** Additional source types may opt into these supported abilities without depending on common. */
public final class RemnantAbilityApi {
    public static int flags(String source, int nativeFlags) { return RemnantAbilityRules.flags(source, nativeFlags); }
    public static void register(ResourceLocation source, Set<RemnantAbility> abilities) { RemnantAbilityRules.register(source.toString(), Set.copyOf(abilities)); }
    public static RemnantAbilitySpec spec(RemnantAbility ability) { return RemnantAbilityRules.spec(ability); }
    public static boolean inRange(RemnantAbility ability, double horizontalSquared, double vertical) {
        return RemnantAbilityRules.inRange(ability, horizontalSquared, vertical);
    }
    public static int summonCount(int owned) { return RemnantAbilityRules.summonCount(owned); }
    public static int maxOwnedVex() { return RemnantAbilityRules.MAX_OWNED_VEX; }
    public static float sonicDamage() { return RemnantAbilityRules.SONIC_DAMAGE; }
    private RemnantAbilityApi() {}
}
