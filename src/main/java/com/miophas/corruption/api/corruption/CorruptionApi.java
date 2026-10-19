package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptionVariants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/** Stable appearance identifiers and source-state persistence, independent of client rendering. */
public final class CorruptionApi {
    public static final String SOURCE_STATE_KEY = "SourceState";

    public static ResourceLocation variantId(BlockState source) {
        var id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(source.getBlock());
        return ResourceLocation.parse(CorruptionVariants.variantId(id.getNamespace(), id.getPath()));
    }

    public static BlockState readSource(CompoundTag tag) {
        return SourceBlockStates.read(tag);
    }

    public static int blendOverlayTexel(int source, int overlay) {
        return com.miophas.corruption.core.corruption.OverlayPixels.blend(source, overlay);
    }

    public static int muteFluidTexel(int pixel) {
        return com.miophas.corruption.core.corruption.MutedFluidPixels.transform(pixel);
    }

    private CorruptionApi() {}
}
