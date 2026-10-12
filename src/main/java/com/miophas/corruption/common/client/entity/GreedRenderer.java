package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Greed;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

public final class GreedRenderer extends MobRenderer<Greed,GreedModel> {
    public static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"textures/entity/greed.png");
    public GreedRenderer(EntityRendererProvider.Context context){super(context,new GreedModel(context.bakeLayer(GreedModel.LAYER_LOCATION)),0);}
    @Override public ResourceLocation getTextureLocation(Greed entity){return TEXTURE;}
    @Override protected void setupRotations(Greed entity,PoseStack poses,float age,float yaw,float partial,float scale){
        super.setupRotations(entity,poses,age,yaw,partial,scale);
        if(entity.hanging())poses.mulPose(Axis.ZP.rotationDegrees(180));
    }
}
