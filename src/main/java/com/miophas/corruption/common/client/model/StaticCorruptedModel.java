package com.miophas.corruption.common.client.model;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

/** Reuses the source's quads and render layers with precomposed atlas sprites; no extra geometry. */
public final class StaticCorruptedModel extends BakedModelWrapper<BakedModel> {
    private final BlockState source;
    private final Function<TextureAtlasSprite, TextureAtlasSprite> sprites;
    private final Map<List<BakedQuad>, List<BakedQuad>> lists = new IdentityHashMap<>();
    private final Map<BakedQuad, BakedQuad> quads = new IdentityHashMap<>();
    private BakedModel base;
    private volatile Map<RenderType, List<List<BakedQuad>>> fastLayers;
    private final ItemOverrides overrides = new ItemOverrides() {
        @Override public BakedModel resolve(BakedModel model, net.minecraft.world.item.ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.world.entity.LivingEntity entity, int seed) {
            var properties = stack.get(net.minecraft.core.component.DataComponents.BLOCK_STATE);
            var generated = com.miophas.corruption.common.registry.GeneratedCorruption.convert(source);
            if (properties == null || generated == null) return model;
            return net.minecraft.client.Minecraft.getInstance().getBlockRenderer().getBlockModel(properties.apply(generated.getBlock().defaultBlockState()));
        }
    };

    public StaticCorruptedModel(BakedModel base, BlockState source, Function<TextureAtlasSprite, TextureAtlasSprite> sprites) {
        super(base); this.base = base; this.source = source; this.sprites = sprites; prepare();
    }
    public void refresh(BakedModel model) { if (base != model) { base = model; lists.clear(); quads.clear(); prepare(); } }
    private void prepare() {
        fastLayers = null;
        if (!(base instanceof net.minecraft.client.resources.model.SimpleBakedModel)) return;
        Map<RenderType, List<List<BakedQuad>>> prepared = new java.util.HashMap<>();
        java.util.List<RenderType> layers = new java.util.ArrayList<>(base.getRenderTypes(source, RandomSource.create(42), ModelData.EMPTY).asList());
        layers.add(null);
        for (RenderType layer : layers) {
            java.util.List<List<BakedQuad>> faces = new java.util.ArrayList<>(7);
            for (int face = -1; face < 6; face++) faces.add(base.getQuads(source, face < 0 ? null : Direction.values()[face], RandomSource.create(42), ModelData.EMPTY, layer)
                    .stream().map(quad -> quads.computeIfAbsent(quad, this::remap)).toList());
            prepared.put(layer, List.copyOf(faces));
        }
        fastLayers = java.util.Collections.unmodifiableMap(prepared);
    }
    @Override public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) { return getQuads(state, side, random, ModelData.EMPTY, null); }
    @Override public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data, @Nullable RenderType layer) {
        var fast = fastLayers;
        if (fast != null) {
            var faces = fast.get(layer);
            return faces == null ? List.of() : faces.get(side == null ? 0 : side.ordinal() + 1);
        }
        return dynamicQuads(side, random, layer);
    }
    private synchronized List<BakedQuad> dynamicQuads(@Nullable Direction side, RandomSource random, @Nullable RenderType layer) {
        List<BakedQuad> original = base.getQuads(source, side, random, ModelData.EMPTY, layer);
        List<BakedQuad> cached = lists.get(original);
        if (cached != null) return cached;
        if (lists.size() >= 64 || quads.size() >= 512) { lists.clear(); quads.clear(); }
        List<BakedQuad> result = original.stream().map(quad -> quads.computeIfAbsent(quad, this::remap)).toList();
        lists.put(original, result);
        return result;
    }
    private BakedQuad remap(BakedQuad original) {
        TextureAtlasSprite old = original.getSprite(), target = remapSprite(old);
        if (old == target) return original;
        int[] vertices = original.getVertices().clone();
        int stride = vertices.length / 4;
        for (int vertex = 0; vertex < 4; vertex++) {
            int at = vertex * stride;
            vertices[at + 4] = Float.floatToRawIntBits(com.miophas.corruption.core.corruption.OverlayGeometry.remapUv(Float.intBitsToFloat(vertices[at + 4]), old.getU0(), old.getU1(), target.getU0(), target.getU1()));
            vertices[at + 5] = Float.floatToRawIntBits(com.miophas.corruption.core.corruption.OverlayGeometry.remapUv(Float.intBitsToFloat(vertices[at + 5]), old.getV0(), old.getV1(), target.getV0(), target.getV1()));
        }
        return new BakedQuad(vertices, original.getTintIndex(), original.getDirection(), target, original.isShade(), original.hasAmbientOcclusion());
    }
    private TextureAtlasSprite remapSprite(TextureAtlasSprite sprite) {
        var id = sprite.contents().name();
        // Frozen entity meshes already use the composed block atlas. Never resolve them twice,
        // nor try to corrupt our own fallback textures or the missing-texture placeholder.
        return id.getNamespace().equals("corruption") || id.equals(net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation())
                ? sprite : sprites.apply(sprite);
    }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return base.getRenderTypes(source, random, ModelData.EMPTY); }
    @Override public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) { return ModelData.EMPTY; }
    @Override public TextureAtlasSprite getParticleIcon() { return remapSprite(base.getParticleIcon(ModelData.EMPTY)); }
    @Override public TextureAtlasSprite getParticleIcon(ModelData data) { return getParticleIcon(); }
    @Override public ItemOverrides getOverrides() { return overrides; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext context, com.mojang.blaze3d.vertex.PoseStack poses, boolean leftHand) {
        base.applyTransform(context, poses, leftHand);
        return this;
    }
    @Override public List<BakedModel> getRenderPasses(net.minecraft.world.item.ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(net.minecraft.world.item.ItemStack stack, boolean fabulous) {
        return base.getRenderTypes(new net.minecraft.world.item.ItemStack(source.getBlock()), fabulous);
    }
}
