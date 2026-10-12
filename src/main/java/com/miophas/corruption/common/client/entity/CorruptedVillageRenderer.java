package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Fixed village appearances are rendered only through corrupted remnants. */
public final class CorruptedVillageRenderer extends MobRenderer<Mob,CorruptedVillageModel> {
    public static ResourceLocation texture(CorruptedVillageModel.Kind kind){return asset(kind,"");}
    private static ResourceLocation asset(CorruptedVillageModel.Kind kind,String suffix){return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"textures/entity/corrupted_"+kind.id+suffix+".png");}
    private final ResourceLocation texture;
    public CorruptedVillageRenderer(EntityRendererProvider.Context context,CorruptedVillageModel.Kind kind){
        super(context,new CorruptedVillageModel(context.bakeLayer(kind.layer),kind),kind==CorruptedVillageModel.Kind.RAVAGER?1.1F:kind==CorruptedVillageModel.Kind.VEX?.2F:.5F);
        texture=texture(kind);
        addLayer(new EyesLayer<Mob,CorruptedVillageModel>(this){@Override public RenderType renderType(){return RenderType.eyes(asset(kind,"_eyes"));}});
        if(kind!=CorruptedVillageModel.Kind.RAVAGER)addLayer(new ItemInHandLayer<>(this,context.getItemInHandRenderer()));
    }
    @Override protected void scale(Mob entity,PoseStack poses,float partial){if(entity.isBaby())poses.scale(.5F,.5F,.5F);}
    @Override public ResourceLocation getTextureLocation(Mob entity){return texture;}
}
