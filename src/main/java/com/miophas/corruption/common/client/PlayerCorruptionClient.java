package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.entity.CrimsonTextures;
import com.miophas.corruption.common.entity.PlayerCorruption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

/** Reuse the real player's renderer and pose; only replace buffers using the player's skin. */
@EventBusSubscriber(modid=Corruption.MOD_ID,value=Dist.CLIENT)
public final class PlayerCorruptionClient {
    private static boolean rendering;
    private static boolean renderingArm;
    private static MultiBufferSource buffers(AbstractClientPlayer player,MultiBufferSource source,int value){
        var skin=player.getSkin().texture();
        return type->source.getBuffer(CrimsonTextures.INSTANCE.layer(type,skin,value));
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void player(RenderPlayerEvent.Pre event){
        if(rendering||!(event.getEntity() instanceof AbstractClientPlayer player))return;
        // Remnant appearance proxies are rendered by the remnant's full corruption pipeline.
        var level=Minecraft.getInstance().level;
        if(level==null||level.getPlayerByUUID(player.getUUID())!=player)return;
        int value=PlayerCorruption.value(player);if(value==0)return;
        event.setCanceled(true);rendering=true;
        try{event.getRenderer().render(player,Mth.rotLerp(event.getPartialTick(),player.yRotO,player.getYRot()),event.getPartialTick(),
            event.getPoseStack(),buffers(player,event.getMultiBufferSource(),value),event.getPackedLight());}
        finally{rendering=false;}
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void arm(RenderArmEvent event){
        if(renderingArm)return;
        int value=PlayerCorruption.value(event.getPlayer());if(value==0)return;
        var renderer=(PlayerRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(event.getPlayer());
        var buffers=buffers(event.getPlayer(),event.getMultiBufferSource(),value);event.setCanceled(true);renderingArm=true;
        try{
            if(event.getArm()==HumanoidArm.RIGHT)renderer.renderRightHand(event.getPoseStack(),buffers,event.getPackedLight(),event.getPlayer());
            else renderer.renderLeftHand(event.getPoseStack(),buffers,event.getPackedLight(),event.getPlayer());
        }finally{renderingArm=false;}
    }
    private PlayerCorruptionClient(){}
}
