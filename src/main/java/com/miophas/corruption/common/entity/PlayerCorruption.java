package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.registry.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Server-owned, automatically synchronized player progression; healing never writes this attachment. */
@EventBusSubscriber(modid=Corruption.MOD_ID)
public final class PlayerCorruption {
    public static int value(Player player){return player.getData(ModAttachments.PLAYER_CORRUPTION);}
    public static void cursePulse(Player player){
        if(player.level().isClientSide||!player.isAlive()||player.isCreative()||player.isSpectator())return;
        int before=value(player),after=PlayerCorruptionApi.increase(before,PlayerCorruptionApi.curseGain());
        if(after!=before)player.setData(ModAttachments.PLAYER_CORRUPTION,after);
        if(after==PlayerCorruptionApi.max())player.hurt(ModDamageTypes.conversion(player.level()),Float.MAX_VALUE);
    }
    @SubscribeEvent public static void tick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event){
        var player=event.getEntity();
        if(!player.level().isClientSide&&player.isAlive()&&!player.isCreative()&&!player.isSpectator()&&player.tickCount%20==0&&value(player)==PlayerCorruptionApi.max())
            player.hurt(ModDamageTypes.conversion(player.level()),Float.MAX_VALUE);
    }
    // NeoForge copies copyOnDeath attachments at NORMAL priority; reset after that copy.
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST) public static void clone(PlayerEvent.Clone event){
        Player old=event.getOriginal();
        int value=event.isWasDeath()?0:value(old);
        event.getEntity().setData(ModAttachments.PLAYER_CORRUPTION,value);
        event.getEntity().getPersistentData().remove("corruption:completed_transformation");
    }
    private PlayerCorruption(){}
}
