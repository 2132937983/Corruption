package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    /** This tag, rather than a wood tag, controls malice combustion. */
    public static final TagKey<Block> FLAMMABLE_CORRUPTION = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "flammable_corruption"));
    private ModTags() {}
}
