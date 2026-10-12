package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptedMobRules;
import java.util.List;

/** Read access to the corrupted mob definitions; models and renderers resolve species through this. */
public final class CorruptedMobApi {
    public static List<CorruptedMobDefinition> family(CorruptedMobFamily family) { return CorruptedMobRules.family(family); }
    /** The species of this family that replaces the source type, or null. */
    public static CorruptedMobDefinition of(CorruptedMobFamily family, String source) { return CorruptedMobRules.of(family, source); }
    public static CorruptedMobDefinition require(CorruptedMobFamily family, String id) { return CorruptedMobRules.require(family, id); }
    public static boolean hasDedicatedModel(String source) { return CorruptedMobRules.hasDedicatedModel(source); }
    private CorruptedMobApi() {}
}
