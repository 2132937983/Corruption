package com.miophas.corruption.common.client.model;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StaticCorruptedModelTest {
    @BeforeAll static void bootstrap() { SharedConstants.tryDetectVersion();Bootstrap.bootStrap(); }
    private static final class Sprite extends TextureAtlasSprite {
        Sprite(SpriteContents contents) { super(TextureAtlas.LOCATION_BLOCKS,contents,256,256,64,128); }
    }
    private static SpriteContents contents(ResourceLocation id) {
        return new SpriteContents(id,new FrameSize(64,64),new NativeImage(64,64,false),ResourceMetadata.EMPTY);
    }
    private static BakedModel simple(List<BakedQuad> quads,TextureAtlasSprite particle) {
        Map<Direction,List<BakedQuad>> faces=new EnumMap<>(Direction.class);
        for(var face:Direction.values())faces.put(face,List.of());
        return new SimpleBakedModel(quads,faces,true,true,true,particle,ItemTransforms.NO_TRANSFORMS,ItemOverrides.EMPTY) {
            @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data) { return ChunkRenderTypeSet.none(); }
        };
    }
    @Test void composedTextureIdentifiersAreStableAcrossRepeatedProcessing() {
        for(String id:List.of("minecraft:entity/enderdragon/dragon","goety:entity/statue/sculptured/4","minecraft:block/stone")) {
            var once=CorruptedSpriteSource.spriteId(ResourceLocation.parse(id));
            for(int i=0;i<100;i++)assertEquals(once,CorruptedSpriteSource.spriteId(once));
        }
    }
    @Test void refreshingWithFrozenNativeChestKeepsQuadsAndParticlesWithoutAnotherTextureLookup() {
        var source=ResourceLocation.parse("minecraft:entity/chest/normal");
        try(var originalContents=contents(source);var frozenContents=contents(CorruptedSpriteSource.spriteId(source))) {
            var original=new Sprite(originalContents);var composed=new Sprite(frozenContents);var lookups=new AtomicInteger();
            var model=new StaticCorruptedModel(simple(List.of(),original),Blocks.CHEST.defaultBlockState(),sprite->{
                assertSame(original,sprite,"Already composed texture was submitted for another atlas lookup");lookups.incrementAndGet();return composed;
            });
            assertSame(composed,model.getParticleIcon());assertEquals(1,lookups.get());
            List<BakedQuad> frozen=new ArrayList<>();
            var collector=new FrozenBlockEntityModels.Collector(ResourceLocation.parse("minecraft:textures/entity/chest/normal.png"),frozen,
                    id->List.of(),id->composed);
            net.minecraft.client.renderer.blockentity.ChestRenderer.createSingleBodyLayer().bakeRoot().render(
                    new PoseStack(),collector,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
            collector.flush();assertEquals(18,frozen.size());
            for(int reload=0;reload<12;reload++) {
                var snapshot=simple(List.copyOf(frozen),composed);
                model.refresh(reload%2==0?snapshot:new BakedModelWrapper<>(snapshot) {});
                var rendered=model.getQuads(Blocks.CHEST.defaultBlockState(),null,RandomSource.create(42));
                assertEquals(18,rendered.size());
                for(int face=0;face<18;face++)assertSame(frozen.get(face),rendered.get(face));
                assertSame(composed,model.getParticleIcon());assertSame(composed,model.getParticleIcon(ModelData.EMPTY));
                assertEquals(1,lookups.get(),"A refresh performed repeated corruption texture lookups");
            }
        }
    }
    @Test void nativeCorruptionFallbackAndMissingTextureSkipTheComposedAtlasLookup() {
        for(var id:List.of(ResourceLocation.parse("corruption:block/block_corruption"),MissingTextureAtlasSprite.getLocation()))
            try(var contents=contents(id)) {
                var sprite=new Sprite(contents);var quad=new BakedQuad(new int[32],-1,Direction.UP,sprite,true);
                var model=new StaticCorruptedModel(simple(List.of(quad),sprite),Blocks.STONE.defaultBlockState(),old->{
                    fail("Fallback sprite must not request a composed texture: "+id);return old;
                });
                assertSame(quad,model.getQuads(null,null,RandomSource.create()).getFirst());assertSame(sprite,model.getParticleIcon());
            }
    }
}
