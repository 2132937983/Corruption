package com.miophas.corruption.api.corruption;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class CorruptionFactionApi {
    private static final ResourceLocation REMNANT_TYPE = ResourceLocation.parse("corruption:corrupted_remnant");
    public static final TagKey<EntityType<?>> REMNANTS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("corruption:corrupted_remnants"));
    /**
     * The corruption's lords: its bosses, the entity type tag corruption:bosses (the colossi and the Goetic demons).
     * They are of the corruption too, so they are also in {@link #REMNANTS}; an add-on's boss joins by data alone.
     */
    public static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("corruption:bosses"));

    public static boolean isRemnant(Entity entity) {
        return entity.getType().is(REMNANTS) || REMNANT_TYPE
                .equals(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }

    /** Whether this is one of the corruption's lords. */
    public static boolean isBoss(Entity entity) { return entity.getType().is(BOSSES); }

    private CorruptionFactionApi() {}
}
