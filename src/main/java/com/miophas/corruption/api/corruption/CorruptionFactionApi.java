package com.miophas.corruption.api.corruption;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class CorruptionFactionApi {
    private static final ResourceLocation REMNANT_TYPE = ResourceLocation.parse("corruption:corrupted_remnant");
    public static final TagKey<EntityType<?>> REMNANTS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("corruption:corrupted_remnants"));
    public static boolean isRemnant(Entity entity) {
        return entity.getType().is(REMNANTS) || REMNANT_TYPE
                .equals(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }
    private CorruptionFactionApi() {}
}
