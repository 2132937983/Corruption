package com.miophas.corruption.common.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miophas.corruption.common.blockentity.InfectedBlockEntity;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import com.miophas.corruption.core.corruption.OverlayGeometry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

public final class CorruptedBakedModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<ModelData> ORIGINAL_DATA = new ModelProperty<>();
    private static final ChunkRenderTypeSet OVERLAY_LAYER = ChunkRenderTypeSet.of(RenderType.translucent());
    private final OverlayQuadBaker overlay;
    @Nullable private final BlockState itemSource;
    @Nullable private final Cache<BlockState, BakedModel> itemModels;
    private final ItemOverrides overrides = new ItemOverrides() {
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            if (itemModels == null) return model;
            BlockState source = InfectedBlockItem.source(stack);
            BakedModel cached = itemModels.getIfPresent(source);
            if (cached == null) {
                cached = new CorruptedBakedModel(originalModel, overlay, source);
                itemModels.put(source, cached);
            }
            return cached;
        }
    };

    public CorruptedBakedModel(BakedModel fallback, TextureAtlasSprite overlay) {
        this(fallback, new OverlayQuadBaker(overlay), null);
    }

    private CorruptedBakedModel(BakedModel fallback, OverlayQuadBaker overlay, @Nullable BlockState itemSource) {
        super(fallback);
        this.overlay = overlay;
        this.itemSource = itemSource;
        this.itemModels = itemSource == null ? CacheBuilder.newBuilder().maximumSize(2048).build() : null;
    }

    private BlockState source(ModelData data) {
        if (itemSource != null) return itemSource;
        BlockState state = data.get(InfectedBlockEntity.SOURCE);
        return state == null ? Blocks.STONE.defaultBlockState() : state;
    }

    private BakedModel base(BlockState source) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(source);
    }

    private static ModelData originalData(ModelData data) {
        ModelData original = data.get(ORIGINAL_DATA);
        return original == null ? ModelData.EMPTY : original;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random,
            ModelData data, @Nullable RenderType renderType) {
        BlockState source = source(data);
        BakedModel model = base(source);
        ModelData original = originalData(data);
        long seed = random.nextLong();
        List<BakedQuad> result = new ArrayList<>();
        ChunkRenderTypeSet layers = model.getRenderTypes(source, RandomSource.create(seed), original);
        if (renderType == null || layers.contains(renderType)) {
            result.addAll(model.getQuads(source, side, RandomSource.create(seed), original, renderType));
        }
        if (renderType == null) {
            Set<Surface> surfaces = new HashSet<>();
            for (BakedQuad quad : List.copyOf(result)) {
                if (surfaces.add(Surface.of(quad))) result.add(overlay.bake(quad));
            }
        } else if (renderType == RenderType.translucent()) {
            // Sample each source layer separately for models whose null-layer quad list is empty.
            Set<Surface> surfaces = new HashSet<>();
            for (RenderType layer : layers) {
                for (BakedQuad quad : model.getQuads(source, side, RandomSource.create(seed), original, layer)) {
                    if (surfaces.add(Surface.of(quad))) result.add(overlay.bake(quad));
                }
            }
        }
        return result;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        BlockState original = level.getBlockEntity(pos) instanceof InfectedBlockEntity entity ? entity.source() : source(data);
        ModelData nested = base(original).getModelData(new SourceRenderView(level), pos, original, ModelData.EMPTY);
        return data.derive().with(InfectedBlockEntity.SOURCE, original).with(ORIGINAL_DATA, nested).build();
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        BlockState original = source(data);
        return ChunkRenderTypeSet.union(base(original).getRenderTypes(original, random, originalData(data)), OVERLAY_LAYER);
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        BlockState original = source(data);
        return base(original).useAmbientOcclusion(original, originalData(data), renderType);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) { return base(source(data)).getParticleIcon(originalData(data)); }

    @Override
    public TextureAtlasSprite getParticleIcon() { return base(source(ModelData.EMPTY)).getParticleIcon(); }

    @Override
    public ItemOverrides getOverrides() { return itemSource == null ? overrides : ItemOverrides.EMPTY; }

    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack pose, boolean leftHand) {
        base(source(ModelData.EMPTY)).applyTransform(context, pose, leftHand);
        return this;
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
        return List.of(RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }

    /** Grass and layered mod models may contain several textured quads on the same surface. */
    private static final class Surface {
        private final int[] positions;
        private final Direction face;

        private Surface(int[] positions, Direction face) { this.positions = positions; this.face = face; }

        static Surface of(BakedQuad quad) {
            int[] v = quad.getVertices();
            int s = v.length / 4;
            float[] positions = new float[12];
            for (int vertex = 0; vertex < 4; vertex++) {
                for (int axis = 0; axis < 3; axis++) positions[vertex * 3 + axis] = Float.intBitsToFloat(v[vertex * s + axis]);
            }
            return new Surface(OverlayGeometry.canonicalPositions(positions), quad.getDirection());
        }

        @Override public boolean equals(Object other) {
            return other instanceof Surface surface && face == surface.face && Arrays.equals(positions, surface.positions);
        }

        @Override public int hashCode() { return 31 * Arrays.hashCode(positions) + face.hashCode(); }
    }
}
