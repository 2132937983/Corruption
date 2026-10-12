package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;

/** Dedicated remnant appearances; never replaces vanilla renderers or boss fight rendering. */
public final class CorruptedNetherRenderer extends MobRenderer<Mob,CorruptedNetherModel> {
    private final CorruptedNetherModel.Kind kind;
    private final ResourceLocation texture;
    private static ResourceLocation asset(CorruptedNetherModel.Kind kind,String suffix){return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"textures/entity/corrupted_"+kind.id+suffix+".png");}
    public static ResourceLocation texture(CorruptedNetherModel.Kind kind){return asset(kind,"");}
    public CorruptedNetherRenderer(EntityRendererProvider.Context context,CorruptedNetherModel.Kind kind){
        super(context,new CorruptedNetherModel(context.bakeLayer(kind.layer),kind),.5F);this.kind=kind;texture=texture(kind);if(kind==CorruptedNetherModel.Kind.SHULKER)shadowRadius=0;
        addLayer(new EyesLayer<Mob,CorruptedNetherModel>(this){@Override public RenderType renderType(){return RenderType.eyes(asset(kind,"_eyes"));}});
        if(kind.humanoid())addLayer(new ItemInHandLayer<>(this,context.getItemInHandRenderer()));
    }
    @Override protected void setupRotations(Mob entity,PoseStack poses,float bob,float bodyYaw,float partial,float scale) {
        if(kind==CorruptedNetherModel.Kind.SHULKER) {
            super.setupRotations(entity,poses,bob,180,partial,scale);
            poses.rotateAround(RemnantAnimationState.shulkerAttachFace(entity).getOpposite().getRotation(),0,.5F,0);
        } else super.setupRotations(entity,poses,bob,bodyYaw,partial,scale);
    }
    @Override protected void scale(Mob entity,PoseStack poses,float partial){
        float scale=entity.isBaby()?.5F:1;
        if(kind==CorruptedNetherModel.Kind.GHAST)scale*=4.5F;
        if(kind==CorruptedNetherModel.Kind.WITHER_SKELETON)scale*=1.2F;
        if(kind==CorruptedNetherModel.Kind.WITHER)scale*=2;
        if(kind==CorruptedNetherModel.Kind.MAGMA_CUBE&&entity instanceof Slime slime)scale*=slime.getSize()*.999F;
        poses.scale(scale,scale,scale);
    }
    @Override public ResourceLocation getTextureLocation(Mob entity){return texture;}
}
