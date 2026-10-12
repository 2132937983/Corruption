package com.miophas.corruption.api.corruption;

/**
 * One corrupted mob species: which source type it replaces and how its dedicated model behaves.
 *
 * @param id           species id; also the suffix of its model layer and texture names
 * @param source       registry id of the source entity type, for example {@code minecraft:wolf}
 * @param flying       bobs and hovers instead of walking
 * @param humanoid     has arms that hold items and swing
 * @param layerVariant model layer variant name
 */
public record CorruptedMobDefinition(CorruptedMobFamily family, String id, String source, boolean flying, boolean humanoid,
        String layerVariant) {}
