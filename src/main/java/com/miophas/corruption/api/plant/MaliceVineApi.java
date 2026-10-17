package com.miophas.corruption.api.plant;

import com.miophas.corruption.core.plant.MaliceVineRules;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * What a malice vine broods, and how it defends itself (MaliceVineRules), for whatever asks: an add-on's elite joins its
 * brood by data alone, through one of these tags.
 */
public final class MaliceVineApi {
    /** What a vine may bring forth anywhere (the entity type tag corruption:vine_brood): the land's elites and the air's, set down on the ground near it. */
    public static final TagKey<EntityType<?>> BROOD = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("corruption:vine_brood"));
    /** What a vine may bring forth only where there is water near it (corruption:vine_brood_aquatic): the sea's elites, set down in the water. */
    public static final TagKey<EntityType<?>> AQUATIC_BROOD = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("corruption:vine_brood_aquatic"));

    /** How far a vine's shockwave reaches at a stage of its growth, and how near something must come to set it off. */
    public static double shockRadius(int stage) { return MaliceVineRules.shockRadius(stage); }
    public static double shockTrigger(int stage) { return MaliceVineRules.shockTrigger(stage); }

    private MaliceVineApi() {}
}
