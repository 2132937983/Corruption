package com.miophas.corruption.common.client.model;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/** Flatten textures before atlas stitching, retaining source frame metadata and animation. */
public final class CorruptedSpriteSource implements SpriteSource {
    public static final MapCodec<CorruptedSpriteSource> CODEC = MapCodec.unit(CorruptedSpriteSource::new);
    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);
    public static ResourceLocation spriteId(ResourceLocation source) {
        if (source.getNamespace().equals("corruption") && source.getPath().startsWith("composed/")) return source;
        return ResourceLocation.fromNamespaceAndPath("corruption", "composed/" + source.getNamespace() + "/" + source.getPath());
    }
    public static ResourceLocation fluidSpriteId(String texture) {
        return ResourceLocation.fromNamespaceAndPath("corruption", "muted/minecraft/block/" + texture);
    }
    @Override public SpriteSourceType type() { return TYPE; }
    @Override public void run(ResourceManager manager, Output output) {
        int[] overlay;
        int width, height;
        try (var stream = manager.getResourceOrThrow(ResourceLocation.parse("corruption:textures/block/block_corruption_overlay.png")).open();
                NativeImage image = NativeImage.read(stream)) {
            width = image.getWidth(); height = image.getHeight(); overlay = new int[width * height];
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) overlay[y * width + x] = image.getPixelRGBA(x, y);
        } catch (IOException failure) { LogUtils.getLogger().error("Cannot load corruption overlay", failure); return; }
        final int ow = width, oh = height;
        var resources = new java.util.HashMap<>(manager.listResources("textures", location -> {
            String path = location.getPath();
            boolean entityTexture = path.startsWith("textures/entity/") && (!location.getNamespace().equals("minecraft")
                    || path.equals("textures/entity/enchanting_table_book.png")
                    || java.util.stream.Stream.of("chest/", "bed/", "signs/", "shulker/", "bell/", "conduit/", "decorated_pot/", "banner/",
                            "skeleton/", "zombie/", "creeper/", "piglin/", "enderdragon/", "player/", "wither_skeleton/")
                            .anyMatch(folder -> path.startsWith("textures/entity/" + folder)));
            return path.endsWith(".png") && (path.startsWith("textures/block/") || path.startsWith("textures/blocks/") || entityTexture)
                    && !location.getNamespace().equals("corruption");
        }));
        // Some mods keep block textures outside block/. Include actual model references too.
        manager.listResources("models", location -> location.getPath().endsWith(".json")
                && !location.getPath().startsWith("models/item/")).forEach((model, resource) -> {
            try (var reader = resource.openAsReader()) {
                var json = com.google.gson.JsonParser.parseReader(reader);
                if (!json.isJsonObject() || !json.getAsJsonObject().has("textures")) return;
                var textures = json.getAsJsonObject().get("textures");
                if (!textures.isJsonObject()) return;
                textures.getAsJsonObject().asMap().values().forEach(value -> {
                    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) return;
                    var id = ResourceLocation.tryParse(value.getAsString());
                    if (id == null || id.getNamespace().equals("corruption")) return;
                    var file = TEXTURE_ID_CONVERTER.idToFile(id);
                    manager.getResource(file).ifPresent(texture -> resources.put(file, texture));
                });
            } catch (IOException | RuntimeException failure) {
                LogUtils.getLogger().debug("Cannot inspect block model textures {}", model, failure);
            }
        });
        resources.forEach((file, resource) -> {
            ResourceLocation original = TEXTURE_ID_CONVERTER.fileToId(file);
            ResourceLocation generated = spriteId(original);
            output.add(generated, loader -> loader.loadSprite(generated, resource, (id, frame, image, metadata) -> {
                // One overlay repeats on every animation frame, with UVs scaled to the source resolution.
                for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
                    int ox = (x % frame.width()) * ow / frame.width(), oy = (y % frame.height()) * oh / frame.height();
                    image.setPixelRGBA(x, y, com.miophas.corruption.api.corruption.CorruptionApi.blendOverlayTexel(image.getPixelRGBA(x, y), overlay[oy * ow + ox]));
                }
                return new SpriteContents(id, frame, image, metadata);
            }));
        });
        for (String texture : new String[] {"water_still", "water_flow", "water_overlay", "lava_still", "lava_flow"}) {
            var file = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/" + texture + ".png");
            manager.getResource(file).ifPresent(resource -> {
                var generated = fluidSpriteId(texture);
                output.add(generated, loader -> loader.loadSprite(generated, resource, (id, frame, image, metadata) -> {
                    for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++)
                        image.setPixelRGBA(x, y, com.miophas.corruption.api.corruption.CorruptionApi.muteFluidTexel(image.getPixelRGBA(x, y)));
                    return new SpriteContents(id, frame, image, metadata);
                }));
            });
        }
    }
}
