package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Native-derived static remnant appearances with species-specific animation. */
public final class CorruptedWildlifeRenderer extends MobRenderer<Mob,CorruptedWildlifeModel> {
    private final CorruptedWildlifeModel.Kind kind;
    private static ResourceLocation asset(CorruptedWildlifeModel.Kind kind,String suffix){return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"textures/entity/corrupted_"+kind.id+suffix+".png");}
    public static ResourceLocation texture(CorruptedWildlifeModel.Kind kind){return asset(kind,"");}
    public CorruptedWildlifeRenderer(EntityRendererProvider.Context context,CorruptedWildlifeModel.Kind kind){
        super(context,new CorruptedWildlifeModel(context.bakeLayer(kind.layer),kind),.4F);this.kind=kind;
        addLayer(new EyesLayer<Mob,CorruptedWildlifeModel>(this){@Override public RenderType renderType(){return RenderType.eyes(asset(kind,"_eyes"));}});
        if(kind.humanoid())addLayer(new ItemInHandLayer<>(this,context.getItemInHandRenderer()));
    }
    @Override protected void scale(Mob entity,PoseStack poses,float partial){
        if(kind==CorruptedWildlifeModel.Kind.CAVE_SPIDER)poses.scale(.7F,.7F,.7F);
        if(kind==CorruptedWildlifeModel.Kind.ELDER_GUARDIAN)poses.scale(2.35F,2.35F,2.35F);
        if(kind==CorruptedWildlifeModel.Kind.MULE)poses.scale(.92F,.92F,.92F);
        if(kind==CorruptedWildlifeModel.Kind.RABBIT)poses.scale(.6F,.6F,.6F);
        if(kind==CorruptedWildlifeModel.Kind.PHANTOM&&entity instanceof net.minecraft.world.entity.monster.Phantom phantom){
            float size=1+.15F*phantom.getPhantomSize();poses.scale(size,size,size);
        }
        if(kind==CorruptedWildlifeModel.Kind.SLIME&&entity instanceof net.minecraft.world.entity.monster.Slime slime){
            float size=slime.getSize();poses.scale(size,size,size);
        }
        if(entity.isBaby())poses.scale(.5F,.5F,.5F);
    }
    @Override public ResourceLocation getTextureLocation(Mob entity){return texture(kind);}
}
