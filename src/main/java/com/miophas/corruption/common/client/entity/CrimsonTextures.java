package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CrimsonSkinApi;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.lwjgl.opengl.GL11;

/** Render-thread cache. Every source skin is generated once, with bounded GPU memory and reload cleanup. */
public final class CrimsonTextures implements ResourceManagerReloadListener {
    public static final CrimsonTextures INSTANCE = new CrimsonTextures();
    private record TextureKey(ResourceLocation source,int progress,boolean player){}
    private record LayerKey(RenderType source,ResourceLocation texture){}
    private final Map<TextureKey, ResourceLocation> textures = new LinkedHashMap<>(16, 0.75F, true);
    private final Map<LayerKey, RenderType> layers = new HashMap<>();
    private final Map<ResourceLocation, Long> retryAfter = new HashMap<>();
    private long serial;

    public ResourceLocation texture(ResourceLocation original) {
        return texture(original,20,false);
    }
    public ResourceLocation texture(ResourceLocation original,int progress) {
        return texture(original,progress,true);
    }
    private ResourceLocation texture(ResourceLocation original,int progress,boolean player) {
        progress=Math.clamp(progress,0,20);if(progress==0)return original;
        var key=new TextureKey(original,progress,player);
        ResourceLocation cached = textures.get(key);
        if (cached != null) return cached;
        long now = System.nanoTime();
        if (retryAfter.getOrDefault(original, 0L) > now) return original;
        Minecraft client = Minecraft.getInstance();
        try (NativeImage input = read(original)) {
            int width = input.getWidth(), height = input.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) pixels[y * width + x] = input.getPixelRGBA(x, y);
            int[] transformed = player?CrimsonSkinApi.playerProgress(pixels,width,height,original.toString().hashCode(),progress)
                :CrimsonSkinApi.transform(pixels, width, height, original.toString().hashCode());
            NativeImage output = new NativeImage(width, height, false);
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) output.setPixelRGBA(x, y, transformed[y * width + x]);
            ResourceLocation generated = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "dynamic/crimson/" + serial++);
            client.getTextureManager().register(generated, new DynamicTexture(output));
            textures.put(key, generated);
            retryAfter.remove(original);
            if (textures.size() > 128) {
                var iterator = textures.entrySet().iterator();
                var oldest = iterator.next();
                client.getTextureManager().release(oldest.getValue());
                iterator.remove();
                layers.clear();
            }
            return generated;
        } catch (IOException | RuntimeException failure) {
            if (retryAfter.size() >= 128) retryAfter.clear();
            retryAfter.put(original, now + 2_000_000_000L);
            LogUtils.getLogger().debug("Cannot yet transform entity texture {}", original, failure);
            return original;
        }
    }

    private NativeImage read(ResourceLocation original) throws IOException {
        Minecraft client = Minecraft.getInstance();
        var resource = client.getResourceManager().getResource(original);
        if (resource.isPresent()) {
            try (var stream = resource.get().open()) { return NativeImage.read(stream); }
        }
        // Downloaded player skins and mod-generated textures exist on the GPU rather than in resource packs.
        var texture = client.getTextureManager().getTexture(original);
        texture.bind();
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        if (width <= 0 || height <= 0 || width > 2048 || height > 2048) throw new IOException("Unsupported skin dimensions");
        NativeImage image = new NativeImage(width, height, false);
        image.downloadTexture(0, false);
        return image;
    }

    public RenderType layer(RenderType original) {
        return layer(original,null,20);
    }
    public RenderType layer(RenderType original,ResourceLocation playerSkin,int progress) {
        if (!(original instanceof RenderType.CompositeRenderType composite)) return original;
        if (!(composite.state().textureState instanceof net.minecraft.client.renderer.RenderStateShard.TextureStateShard shard)) return original;
        var location = shard.texture;
        if (location.isEmpty()) return original;
        ResourceLocation source = location.get();
        if(playerSkin!=null&&!source.equals(playerSkin))return original;
        // Keep font and shared item/block atlases intact; recolor the entity's body and texture layers.
        if (source.getPath().contains("atlas/") || source.getPath().contains("font/")
                || source.getPath().startsWith("dynamic/crimson/")) return original;
        ResourceLocation generated = texture(source,progress,playerSkin!=null);
        if (generated.equals(source)) return original;
        if (layers.size() >= 512) layers.clear();
        return layers.computeIfAbsent(new LayerKey(original,generated), key -> new SkinRenderType(key.source(), generated));
    }

    private static final class SkinRenderType extends RenderType {
        SkinRenderType(RenderType original, ResourceLocation texture) {
            super("corruption_crimson", original.format(), original.mode(), original.bufferSize(), original.affectsCrumbling(), original.sortOnUpload(),
                    () -> { original.setupRenderState(); RenderSystem.setShaderTexture(0, texture); }, original::clearRenderState);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        if (!RenderSystem.isOnRenderThread()) { RenderSystem.recordRenderCall(this::clear); return; }
        clear();
    }

    public void clear() {
        textures.values().forEach(Minecraft.getInstance().getTextureManager()::release);
        textures.clear(); layers.clear(); retryAfter.clear();
    }

    private CrimsonTextures() {}
}
