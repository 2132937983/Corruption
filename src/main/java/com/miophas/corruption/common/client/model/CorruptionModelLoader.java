package com.miophas.corruption.common.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

public final class CorruptionModelLoader implements IGeometryLoader<CorruptionModelLoader.Geometry> {
    @Override
    public Geometry read(JsonObject json, JsonDeserializationContext context) { return new Geometry(); }

    public static final class Geometry implements IUnbakedGeometry<Geometry> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> textures,
                ModelState state, ItemOverrides overrides) {
            BakedModel fallback = baker.bake(ResourceLocation.withDefaultNamespace("block/stone"), state);
            return new CorruptedBakedModel(fallback, textures.apply(context.getMaterial("overlay")));
        }
    }
}
