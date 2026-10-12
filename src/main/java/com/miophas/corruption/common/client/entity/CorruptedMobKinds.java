package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.CorruptedMobApi;
import com.miophas.corruption.api.corruption.CorruptedMobFamily;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/** Resolves a source entity to the model kind of a family from the shared corrupted mob definitions. */
final class CorruptedMobKinds {
    private static final Map<EntityType<?>, String> SOURCE_IDS = new ConcurrentHashMap<>();

    static String sourceId(EntityType<?> type) { return SOURCE_IDS.computeIfAbsent(type, key -> BuiltInRegistries.ENTITY_TYPE.getKey(key).toString()); }
    static String sourceId(LivingEntity entity) { return sourceId(entity.getType()); }

    static <K> K of(CorruptedMobFamily family, LivingEntity entity, Map<String, K> byId) {
        var definition = CorruptedMobApi.of(family, sourceId(entity));
        return definition == null ? null : byId.get(definition.id());
    }

    private CorruptedMobKinds() {}
}
