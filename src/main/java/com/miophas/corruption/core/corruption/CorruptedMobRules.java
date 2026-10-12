package com.miophas.corruption.core.corruption;

import static com.miophas.corruption.api.corruption.CorruptedMobFamily.*;

import com.miophas.corruption.api.corruption.CorruptedMobDefinition;
import com.miophas.corruption.api.corruption.CorruptedMobFamily;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Every corrupted mob species and its source type. Ids and flags only, so the data stays testable without Minecraft. */
public final class CorruptedMobRules {
    private static final String MAIN = "main";
    private static final Map<CorruptedMobFamily, List<CorruptedMobDefinition>> BY_FAMILY = new EnumMap<>(CorruptedMobFamily.class);
    private static final Map<CorruptedMobFamily, Map<String, CorruptedMobDefinition>> BY_SOURCE = new EnumMap<>(CorruptedMobFamily.class);
    private static final Map<CorruptedMobFamily, Map<String, CorruptedMobDefinition>> BY_ID = new EnumMap<>(CorruptedMobFamily.class);

    static {
        for (var family : CorruptedMobFamily.values()) {
            BY_FAMILY.put(family, new ArrayList<>()); BY_SOURCE.put(family, new HashMap<>()); BY_ID.put(family, new HashMap<>());
        }
        define(DEDICATED, "zombie creeper skeleton spider enderman", Set.of(), Set.of(), Set.of());
        define(LIVESTOCK, "pig cow sheep chicken horse donkey", Set.of(), Set.of(), Set.of());
        define(VILLAGE, "villager pillager vindicator evoker illusioner vex ravager", Set.of(), Set.of(), Set.of());
        define(NETHER, "piglin piglin_brute zombified_piglin hoglin zoglin ghast blaze magma_cube strider wither_skeleton wither endermite ender_dragon shulker",
                Set.of("ghast", "blaze", "wither", "ender_dragon"), Set.of("piglin", "piglin_brute", "zombified_piglin", "wither_skeleton"), Set.of());
        define(WILDLIFE, "allay armadillo axolotl bat bee bogged breeze camel cat cave_spider cod dolphin drowned elder_guardian fox frog glow_squid goat guardian husk "
                + "iron_golem llama mooshroom mule ocelot panda parrot phantom polar_bear pufferfish rabbit salmon silverfish skeleton_horse slime sniffer snow_golem squid stray "
                + "tadpole trader_llama tropical_fish turtle vex wandering_trader warden witch wolf zombie_horse",
                Set.of("allay", "bat", "bee", "parrot", "phantom", "vex"),
                Set.of("allay", "bogged", "drowned", "husk", "iron_golem", "stray", "vex", "warden", "witch"), Set.of("vex"));
    }

    private static void define(CorruptedMobFamily family, String ids, Set<String> flying, Set<String> humanoid, Set<String> nativeLayer) {
        for (String id : ids.split(" ")) {
            var definition = new CorruptedMobDefinition(family, id, "minecraft:" + id, flying.contains(id), humanoid.contains(id),
                    nativeLayer.contains(id) ? "native_wildlife" : MAIN);
            BY_FAMILY.get(family).add(definition); BY_SOURCE.get(family).put(definition.source(), definition); BY_ID.get(family).put(id, definition);
        }
    }

    public static List<CorruptedMobDefinition> family(CorruptedMobFamily family) { return List.copyOf(BY_FAMILY.get(family)); }
    /** The species of this family that replaces the source type, or null. A source may appear in several families. */
    public static CorruptedMobDefinition of(CorruptedMobFamily family, String source) { return BY_SOURCE.get(family).get(source); }
    public static CorruptedMobDefinition require(CorruptedMobFamily family, String id) {
        var definition = BY_ID.get(family).get(id);
        if (definition == null) throw new IllegalStateException("No " + family + " corrupted mob definition for " + id);
        return definition;
    }
    public static boolean hasDedicatedModel(String source) {
        for (var family : CorruptedMobFamily.values()) if (BY_SOURCE.get(family).containsKey(source)) return true;
        return false;
    }

    private CorruptedMobRules() {}
}
