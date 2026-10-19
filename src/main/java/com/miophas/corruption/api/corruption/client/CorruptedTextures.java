package com.miophas.corruption.api.corruption.client;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Client render-thread access to the native bounded texture cache and resource-reload cleanup. */
public final class CorruptedTextures {
    public interface Provider {
        ResourceLocation texture(ResourceLocation source);
        ResourceLocation playerTexture(ResourceLocation source, int corruption);
        RenderType layer(RenderType source, @Nullable ResourceLocation playerSkin, int corruption);
    }
    private static Provider provider;
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    private static Provider cache() {
        if (provider == null) throw new IllegalStateException("Corrupted textures are unavailable before client setup");
        return provider;
    }
    public static ResourceLocation texture(ResourceLocation source) { return cache().texture(source); }
    public static ResourceLocation playerTexture(ResourceLocation source, int corruption) { return cache().playerTexture(source, corruption); }
    public static RenderType layer(RenderType source) { return cache().layer(source, null, 20); }
    public static RenderType playerLayer(RenderType source, ResourceLocation skin, int corruption) { return cache().layer(source, Objects.requireNonNull(skin), corruption); }
    private CorruptedTextures() {}
}
