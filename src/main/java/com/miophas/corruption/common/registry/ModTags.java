package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    /** This tag, rather than a wood tag, controls malice combustion. */
    public static final TagKey<Block> FLAMMABLE_CORRUPTION = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "flammable_corruption"));
    /** What a spell's "summon" may call forth; a creature outside it is refused, so data cannot bring in anything. */
    public static final TagKey<EntityType<?>> SPELL_SUMMONS = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "spell_summons"));
    /** What a spell's "launch_bolt" may fly, the spell bolt and a few thrown things by default. */
    public static final TagKey<EntityType<?>> SPELL_BOLTS = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "spell_bolts"));
    /** What a spell's "corrupt_creatures" never turns into a remnant: bosses by default ({@code #c:bosses}). */
    public static final TagKey<EntityType<?>> SPELL_CONVERSION_IMMUNE = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "spell_conversion_immune"));
    private ModTags() {}
}
