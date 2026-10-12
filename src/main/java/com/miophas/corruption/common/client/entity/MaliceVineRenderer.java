package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.MaliceVine;
import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class MaliceVineRenderer extends MobRenderer<MaliceVine, MaliceVineModel> {
    public static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"textures/entity/malice_vine.png");
    public MaliceVineRenderer(EntityRendererProvider.Context context) {
        super(context,new MaliceVineModel(context.bakeLayer(MaliceVineModel.LAYER_LOCATION)),0);
    }
    @Override public ResourceLocation getTextureLocation(MaliceVine entity) { return TEXTURE; }
    @Override protected void setupRotations(MaliceVine entity, PoseStack poses, float age, float yaw, float partialTick, float scale) {
        super.setupRotations(entity,poses,age,yaw,partialTick,scale);
        if(entity.hanging()) poses.mulPose(Axis.ZP.rotationDegrees(180));
    }
}
