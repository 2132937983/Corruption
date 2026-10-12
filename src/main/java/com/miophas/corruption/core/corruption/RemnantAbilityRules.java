package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantAbilitySpec;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Source abilities and bounded combat rules; no world scans or concrete entities here. */
public final class RemnantAbilityRules {
    private static final Map<String, Integer> REGISTERED = new ConcurrentHashMap<>();
    public static final int VEX_PER_CAST = 3, MAX_OWNED_VEX = 6;
    public static final float SONIC_DAMAGE = 10;
    private static final RemnantAbilitySpec SUMMON = new RemnantAbilitySpec(20, 100, 340, 16, 16);
    private static final RemnantAbilitySpec FANGS = new RemnantAbilitySpec(20, 40, 100, 20, 8);
    private static final RemnantAbilitySpec SONIC = new RemnantAbilitySpec(34, 60, 100, 15, 20);
    public static int flags(String source, int nativeFlags) {
        int defaults = switch (source) {
            case "minecraft:evoker" -> RemnantAbility.SUMMON_VEX.mask() | RemnantAbility.EVOKER_FANGS.mask();
            case "minecraft:warden" -> RemnantAbility.SONIC_BOOM.mask();
            case "minecraft:spider", "minecraft:cave_spider" -> RemnantAbility.WALL_CLIMB.mask();
            case "minecraft:vex" -> RemnantAbility.FLIGHT.mask() | RemnantAbility.PHASE_WALLS.mask();
            case "minecraft:ghast", "minecraft:blaze", "minecraft:wither", "minecraft:allay", "minecraft:bat", "minecraft:bee", "minecraft:parrot", "minecraft:phantom" -> RemnantAbility.FLIGHT.mask();
            case "minecraft:shulker" -> RemnantAbility.SHULKER_COMBAT.mask();
            case "minecraft:axolotl", "minecraft:drowned", "minecraft:frog", "minecraft:turtle" -> RemnantAbility.AMPHIBIOUS.mask();
            case "minecraft:cod", "minecraft:salmon", "minecraft:pufferfish", "minecraft:tadpole", "minecraft:tropical_fish", "minecraft:dolphin", "minecraft:guardian", "minecraft:elder_guardian", "minecraft:glow_squid", "minecraft:squid" -> RemnantAbility.SWIMMING.mask();
            case "minecraft:ender_dragon" -> RemnantAbility.FLIGHT.mask() | RemnantAbility.DRAGON_COMBAT.mask();
            default -> 0;
        };
        int flags = defaults | nativeFlags | REGISTERED.getOrDefault(source, 0);
        // Ordinary remnants walk and float at the surface; only native aquatic sources swim.
        return flags & ~RemnantAbility.BASE_SWIMMING.mask();
    }
    public static void register(String source, Set<RemnantAbility> abilities) {
        int flags = abilities.stream().mapToInt(RemnantAbility::mask).reduce(0, (a, b) -> a | b);
        REGISTERED.merge(source, flags, (a, b) -> a | b);
    }
    public static RemnantAbilitySpec spec(RemnantAbility ability) {
        return switch (ability) {
            case SUMMON_VEX -> SUMMON;
            case EVOKER_FANGS -> FANGS;
            case SONIC_BOOM -> SONIC;
            default -> throw new IllegalArgumentException("Not a cast ability: " + ability);
        };
    }
    public static boolean inRange(RemnantAbility ability, double horizontalDistanceSquared, double verticalDistance) {
        RemnantAbilitySpec spec = spec(ability);
        return horizontalDistanceSquared < spec.horizontalRange() * spec.horizontalRange()
                && Math.abs(verticalDistance) < spec.verticalRange();
    }
    public static int summonCount(int owned) { return Math.clamp(MAX_OWNED_VEX - owned, 0, VEX_PER_CAST); }
    private RemnantAbilityRules() {}
}
