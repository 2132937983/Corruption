package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.entity.PlayerCorruption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.ResourceLocation;

/** Ten outlined hearts, filled in half-heart units, above the other survival status rows. */
public final class CorruptionHud {
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"textures/gui/corruption_hearts.png");
    public static void render(GuiGraphics gui,DeltaTracker ticks){
        var client=Minecraft.getInstance();var player=client.player;
        if(player==null||client.options.hideGui||player.isSpectator()||!client.gameMode.canHurtPlayer())return;
        int x=gui.guiWidth()/2-91,y=gui.guiHeight()-Math.max(client.gui.leftHeight,client.gui.rightHeight)-1;
        renderHearts(gui,x,y,PlayerCorruption.value(player));client.gui.leftHeight+=11;
    }
    public static void renderHearts(GuiGraphics gui,int x,int y,int value){
        for(int i=0;i<PlayerCorruptionApi.hearts();i++)gui.blit(TEXTURE,x+i*8,y,PlayerCorruptionApi.heartFill(value,i)*9,0,9,9,27,9);
    }
    private CorruptionHud(){}
}
