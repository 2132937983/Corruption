package com.miophas.corruption.common.client.model;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FrozenBlockEntityModelsTest {
    private static final class Sprite extends TextureAtlasSprite {
        Sprite(ResourceLocation atlas,SpriteContents contents,int atlasSize,int x,int y) {
            super(atlas,contents,atlasSize,atlasSize,x,y);
        }
    }
    @Test void nativeChestMeshIsCapturedAsStaticQuadsWithoutAWorldOrBlockEntity() {
        var image=ResourceLocation.parse("minecraft:entity/chest/normal");
        try(var contents=new SpriteContents(image,new FrameSize(64,64),new NativeImage(64,64,false),ResourceMetadata.EMPTY)) {
            var sprite=new Sprite(TextureAtlas.LOCATION_BLOCKS,contents,256,64,128);
            List<BakedQuad> quads=new ArrayList<>();
            var collector=new FrozenBlockEntityModels.Collector(ResourceLocation.parse("minecraft:textures/entity/chest/normal.png"),quads,
                    id->List.of(),id->sprite);
            net.minecraft.client.renderer.blockentity.ChestRenderer.createSingleBodyLayer().bakeRoot().render(
                    new com.mojang.blaze3d.vertex.PoseStack(),collector,net.minecraft.client.renderer.LightTexture.FULL_BRIGHT,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            collector.flush();assertEquals(18,quads.size());
            float minX=Float.POSITIVE_INFINITY,maxX=Float.NEGATIVE_INFINITY,maxY=Float.NEGATIVE_INFINITY;
            for(var quad:quads)for(int i=0;i<4;i++) {
                var vertices=quad.getVertices();float x=Float.intBitsToFloat(vertices[i*8]),y=Float.intBitsToFloat(vertices[i*8+1]);
                minX=Math.min(minX,x);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);
                float u=Float.intBitsToFloat(vertices[i*8+4]),v=Float.intBitsToFloat(vertices[i*8+5]);
                assertTrue(u>=sprite.getU0()&&u<=sprite.getU1());assertTrue(v>=sprite.getV0()&&v<=sprite.getV1());
            }
            assertEquals(1F/16,minX,.00001F);assertEquals(15F/16,maxX,.00001F);assertEquals(14F/16,maxY,.00001F);
        }
    }
    @Test void frozenEntityAtlasQuadRetainsPositionsAndRemapsToTheComposedBlockSprite() {
        ResourceLocation atlas=ResourceLocation.parse("minecraft:textures/atlas/chest.png");
        ResourceLocation image=ResourceLocation.parse("minecraft:entity/chest/normal");
        try(var source=new SpriteContents(image,new FrameSize(16,16),new NativeImage(16,16,false),ResourceMetadata.EMPTY);
            var target=new SpriteContents(CorruptedSpriteSource.spriteId(image),new FrameSize(32,32),new NativeImage(32,32,false),ResourceMetadata.EMPTY)) {
            var old=new Sprite(atlas,source,128,32,48);var composed=new Sprite(TextureAtlas.LOCATION_BLOCKS,target,256,96,128);
            List<BakedQuad> quads=new ArrayList<>();
            var collector=new FrozenBlockEntityModels.Collector(atlas,quads,id->List.of(old),id->{assertEquals(image,id);return composed;});
            float[][] vertices={{0,0,0,0,0},{1,0,0,1,0},{1,1,0,1,1},{0,1,0,0,1}};
            for(var vertex:vertices) collector.addVertex(vertex[0],vertex[1],vertex[2]).setColor(31,47,63,255)
                    .setUv(old.getU(vertex[3]),old.getV(vertex[4])).setNormal(0,0,1);
            collector.flush();assertEquals(1,quads.size());assertSame(composed,quads.getFirst().getSprite());
            var packed=quads.getFirst().getVertices();
            for(int i=0;i<4;i++) {
                for(int axis=0;axis<3;axis++) assertEquals(vertices[i][axis],Float.intBitsToFloat(packed[i*8+axis]));
                assertEquals(composed.getU(vertices[i][3]),Float.intBitsToFloat(packed[i*8+4]),.000001F);
                assertEquals(composed.getV(vertices[i][4]),Float.intBitsToFloat(packed[i*8+5]),.000001F);
                assertEquals(0xff3f2f1f,packed[i*8+3]);
            }
        }
    }
    @Test void directEntityTextureUsesNormalizedUvAndDegenerateFacesKeepTheirNormal() {
        var image=ResourceLocation.parse("minecraft:entity/signs/oak");
        try(var contents=new SpriteContents(image,new FrameSize(16,16),new NativeImage(16,16,false),ResourceMetadata.EMPTY)) {
            var sprite=new Sprite(TextureAtlas.LOCATION_BLOCKS,contents,128,16,32);
            List<BakedQuad> quads=new ArrayList<>();
            var collector=new FrozenBlockEntityModels.Collector(ResourceLocation.parse("minecraft:textures/entity/signs/oak.png"),quads,
                    id->{fail("Direct texture must not resolve an entity atlas");return List.of();},
                    id->{assertEquals(image,id);return sprite;});
            for(int i=0;i<4;i++)collector.addVertex(0,0,0).setUv(.25F,.75F).setNormal(1,0,0);
            collector.flush();var packed=quads.getFirst().getVertices();
            assertEquals(sprite.getU(.25F),Float.intBitsToFloat(packed[4]),.000001F);
            assertEquals(sprite.getV(.75F),Float.intBitsToFloat(packed[5]),.000001F);
            assertEquals(net.minecraft.core.Direction.EAST,quads.getFirst().getDirection());assertEquals(127,packed[7]);
        }
    }
}
