package com.miophas.corruption.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.joml.Vector3f;

/** Captures an unplaced source renderer once per resource reload; corrupted blocks never own or tick a block entity. */
public final class FrozenBlockEntityModels {
    private static final Map<BlockState,BakedModel> CACHE = new IdentityHashMap<>();
    private static final Map<ResourceLocation,List<TextureAtlasSprite>> ATLASES = new HashMap<>();
    private static final Set<Block> WARNED = Collections.newSetFromMap(new IdentityHashMap<>());
    public static void clear() { CACHE.clear(); ATLASES.clear(); WARNED.clear(); }
    public static BakedModel bake(BlockState source) {
        BlockState canonical = source.hasProperty(BlockStateProperties.WATERLOGGED) ? source.setValue(BlockStateProperties.WATERLOGGED,false) : source;
        return CACHE.computeIfAbsent(canonical,FrozenBlockEntityModels::capture);
    }
    private static BakedModel capture(BlockState state) {
        var original = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        Capture buffers = new Capture();
        boolean ordinary = state.getRenderShape() == RenderShape.MODEL;
        try {
            BlockEntity entity = ((EntityBlock)state.getBlock()).newBlockEntity(BlockPos.ZERO,state);
            if (entity != null) {
                if (ordinary && Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(entity) == null) return original;
                PoseStack poses = new PoseStack();
                if (state.getBlock() instanceof AbstractChestBlock<?>) renderChest(entity,state,poses,buffers);
                else if (entity instanceof BedBlockEntity bed) renderBed(bed,state,poses,buffers);
                else if (entity instanceof BannerBlockEntity banner) renderBanner(banner,state,poses,buffers);
                else {
                    if (state.getBlock() instanceof ShulkerBoxBlock) {
                        poses.translate(.5,.5,.5);poses.mulPose(state.getValue(ShulkerBoxBlock.FACING).getRotation());poses.translate(-.5,-.5,-.5);
                    }
                    var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(entity);
                    if (renderer != null) renderer.render(entity,0,poses,buffers,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
                }
            }
            buffers.finish();
        } catch (RuntimeException failure) {
            if (WARNED.add(state.getBlock())) LogUtils.getLogger().warn("Cannot freeze source renderer {}; using its static shape",state.getBlock(),failure);
            buffers.quads.clear();
        }
        if (ordinary) {
            var random = net.minecraft.util.RandomSource.create(42);
            buffers.quads.addAll(original.getQuads(state,null,random));
            for(Direction face:Direction.values()) buffers.quads.addAll(original.getQuads(state,face,random));
        }
        if (buffers.quads.isEmpty()) fallback(state,original.getParticleIcon(),buffers.quads);
        var particle = buffers.quads.isEmpty() ? original.getParticleIcon() : buffers.quads.getFirst().getSprite();
        Map<Direction,List<BakedQuad>> culled = new EnumMap<>(Direction.class);
        for (Direction face:Direction.values()) culled.put(face,List.of());
        return new SimpleBakedModel(List.copyOf(buffers.quads),culled,true,true,true,particle,original.getTransforms(),ItemOverrides.EMPTY,
                new RenderTypeGroup(RenderType.cutout(),RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS)));
    }
    private static void renderChest(BlockEntity entity,BlockState state,PoseStack poses,Capture buffers) {
        ChestType type = state.hasProperty(ChestBlock.TYPE) ? state.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        var layer = switch(type) {
            case LEFT -> ChestRenderer.createDoubleBodyLeftLayer();
            case RIGHT -> ChestRenderer.createDoubleBodyRightLayer();
            case SINGLE -> ChestRenderer.createSingleBodyLayer();
        };
        var root = layer.bakeRoot();
        poses.translate(.5,.5,.5);poses.mulPose(Axis.YP.rotationDegrees(-state.getValue(ChestBlock.FACING).toYRot()));poses.translate(-.5,-.5,-.5);
        root.render(poses,Sheets.chooseMaterial(entity,type,false).buffer(buffers,RenderType::entityCutout),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
    }
    private static void renderBed(BedBlockEntity bed,BlockState state,PoseStack poses,Capture buffers) {
        var root = (state.getValue(BedBlock.PART)==BedPart.HEAD ? BedRenderer.createHeadLayer() : BedRenderer.createFootLayer()).bakeRoot();
        poses.translate(0,.5625,0);poses.mulPose(Axis.XP.rotationDegrees(90));poses.translate(.5,.5,.5);
        poses.mulPose(Axis.ZP.rotationDegrees(180+state.getValue(BedBlock.FACING).toYRot()));poses.translate(-.5,-.5,-.5);
        root.render(poses,Sheets.BED_TEXTURES[bed.getColor().getId()].buffer(buffers,RenderType::entitySolid),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
    }
    private static void renderBanner(BannerBlockEntity banner,BlockState state,PoseStack poses,Capture buffers) {
        var root = BannerRenderer.createBodyLayer().bakeRoot();
        if(state.getBlock() instanceof WallBannerBlock){
            poses.translate(.5,-.16666667,.5);poses.mulPose(Axis.YP.rotationDegrees(-state.getValue(WallBannerBlock.FACING).toYRot()));
            poses.translate(0,-.3125,-.4375);root.getChild("pole").visible=false;
        }else{
            poses.translate(.5,.5,.5);poses.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees(state.getValue(BannerBlock.ROTATION))));
        }
        poses.scale(.6666667F,-.6666667F,-.6666667F);
        var material = net.minecraft.client.resources.model.ModelBakery.BANNER_BASE;
        var vertices = material.buffer(buffers,RenderType::entitySolid);
        root.getChild("pole").render(poses,vertices,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
        root.getChild("bar").render(poses,vertices,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
        var flag = root.getChild("flag");flag.y=-32;flag.xRot=-.0125F*(float)Math.PI;
        // One colored fabric layer, avoiding coincident base/pattern geometry in the frozen chunk mesh.
        flag.render(poses,Sheets.BANNER_BASE.buffer(buffers,RenderType::entitySolid),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,
                net.minecraft.util.FastColor.ARGB32.opaque(banner.getBaseColor().getTextureDiffuseColor()));
    }
    private static void fallback(BlockState state,TextureAtlasSprite particle,List<BakedQuad> quads) {
        net.minecraft.world.phys.shapes.VoxelShape shape;
        try { shape = state.getShape(EmptyBlockGetter.INSTANCE,BlockPos.ZERO); }
        catch (RuntimeException failure) { shape = net.minecraft.world.phys.shapes.Shapes.block(); }
        var bakery = new FaceBakery();
        for(var box:shape.toAabbs()) for(Direction face:Direction.values()){
            var element = new BlockElementFace(null,-1,"particle",new BlockFaceUV(new float[]{0,0,16,16},0));
            quads.add(bakery.bakeQuad(new Vector3f((float)box.minX*16,(float)box.minY*16,(float)box.minZ*16),
                    new Vector3f((float)box.maxX*16,(float)box.maxY*16,(float)box.maxZ*16),element,particle,face,BlockModelRotation.X0_Y0,null,true));
        }
    }
    private static TextureAtlasSprite composed(ResourceLocation original) {
        var atlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
        var sprite = atlas.getSprite(CorruptedSpriteSource.spriteId(original));
        return sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())
                ? atlas.getSprite(ResourceLocation.parse("corruption:block/block_corruption")) : sprite;
    }
    private static final class Capture implements MultiBufferSource {
        final List<BakedQuad> quads = new ArrayList<>();
        final Map<RenderType,Collector> consumers = new IdentityHashMap<>();
        @Override public VertexConsumer getBuffer(RenderType type) { return consumers.computeIfAbsent(type,t->new Collector(t,quads)); }
        void finish() { consumers.values().forEach(Collector::flush); }
    }
    static final class Collector implements VertexConsumer {
        final List<BakedQuad> output;
        final ResourceLocation texture;
        final java.util.function.Function<ResourceLocation,List<TextureAtlasSprite>> atlasLookup;
        final java.util.function.Function<ResourceLocation,TextureAtlasSprite> spriteLookup;
        final float[][] vertices = new float[4][9];
        int count;
        Collector(RenderType type,List<BakedQuad> output){
            this(type instanceof RenderType.CompositeRenderType composite && composite.state().textureState instanceof RenderStateShard.TextureStateShard shard
                    ? shard.texture.orElse(TextureAtlas.LOCATION_BLOCKS) : TextureAtlas.LOCATION_BLOCKS,output,
                    id -> ATLASES.computeIfAbsent(id,key->new ArrayList<>(Minecraft.getInstance().getModelManager().getAtlas(key).getTextures().values())),
                    FrozenBlockEntityModels::composed);
        }
        Collector(ResourceLocation texture,List<BakedQuad> output,
                java.util.function.Function<ResourceLocation,List<TextureAtlasSprite>> atlasLookup,
                java.util.function.Function<ResourceLocation,TextureAtlasSprite> spriteLookup) {
            this.output=output;
            this.texture=texture;this.atlasLookup=atlasLookup;this.spriteLookup=spriteLookup;
        }
        @Override public VertexConsumer addVertex(float x,float y,float z){
            if(count==4)flush();var vertex=vertices[count++];Arrays.fill(vertex,0);vertex[0]=x;vertex[1]=y;vertex[2]=z;vertex[3]=Float.intBitsToFloat(-1);return this;
        }
        @Override public VertexConsumer setColor(int r,int g,int b,int a){vertices[count-1][3]=Float.intBitsToFloat(a<<24|b<<16|g<<8|r);return this;}
        @Override public VertexConsumer setUv(float u,float v){vertices[count-1][4]=u;vertices[count-1][5]=v;return this;}
        @Override public VertexConsumer setUv1(int u,int v){return this;}
        @Override public VertexConsumer setUv2(int u,int v){return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){vertices[count-1][6]=x;vertices[count-1][7]=y;vertices[count-1][8]=z;return this;}
        void flush(){
            if(count!=4)return;
            float centerU=0,centerV=0;for(var v:vertices){centerU+=v[4]/4;centerV+=v[5]/4;}
            TextureAtlasSprite old=null;ResourceLocation image;
            if(texture.getPath().startsWith("textures/atlas/")){
                var sprites=atlasLookup.apply(texture);
                for(var sprite:sprites)if(centerU>=sprite.getU0()&&centerU<=sprite.getU1()&&centerV>=sprite.getV0()&&centerV<=sprite.getV1()){old=sprite;break;}
                image=old==null?MissingTextureAtlasSprite.getLocation():old.contents().name();
            }else{
                String path=texture.getPath();image=ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),path.startsWith("textures/")&&path.endsWith(".png")?path.substring(9,path.length()-4):path);
            }
            var sprite=spriteLookup.apply(image);int[] packed=new int[32];
            Vector3f a=new Vector3f(vertices[1][0]-vertices[0][0],vertices[1][1]-vertices[0][1],vertices[1][2]-vertices[0][2]);
            Vector3f b=new Vector3f(vertices[2][0]-vertices[0][0],vertices[2][1]-vertices[0][1],vertices[2][2]-vertices[0][2]);
            var normal=a.cross(b);
            if (normal.lengthSquared()<1.0E-12F) normal.set(vertices[0][6],vertices[0][7],vertices[0][8]);
            if (normal.lengthSquared()<1.0E-12F) normal.set(0,1,0);
            normal.normalize();Direction direction=Direction.getNearest(normal.x,normal.y,normal.z);
            for(int i=0;i<4;i++){
                var v=vertices[i];int at=i*8;for(int axis=0;axis<3;axis++)packed[at+axis]=Float.floatToRawIntBits(v[axis]);packed[at+3]=Float.floatToRawIntBits(v[3]);
                float u=old==null?v[4]:(v[4]-old.getU0())/(old.getU1()-old.getU0()),vv=old==null?v[5]:(v[5]-old.getV0())/(old.getV1()-old.getV0());
                packed[at+4]=Float.floatToRawIntBits(sprite.getU(u));packed[at+5]=Float.floatToRawIntBits(sprite.getV(vv));
                packed[at+7]=((byte)(normal.x*127)&255)|((byte)(normal.y*127)&255)<<8|((byte)(normal.z*127)&255)<<16;
            }
            output.add(new BakedQuad(packed,-1,direction,sprite,true));count=0;
        }
    }
    private FrozenBlockEntityModels(){}
}
