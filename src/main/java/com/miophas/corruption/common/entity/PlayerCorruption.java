package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionWardItem;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.core.corruption.CorruptionWard;
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
        raise(player,PlayerCorruptionApi.curseGain());
        if(value(player)==PlayerCorruptionApi.max())player.hurt(ModDamageTypes.conversion(player.level()),Float.MAX_VALUE);
    }
    /**
     * Raises a player's corruption by this much: the one way it grows, whatever grows it (the curse, a spell's cost,
     * the corruption's bread). What they wear bears on it first (CorruptionWardItem: a robe holds some of it back).
     * Made whole by it, a player in survival is converted at once; a player in creative never is. Answers how much it
     * grew.
     */
    public static int raise(Player player,int gain){
        if(player.level().isClientSide||!player.isAlive()||player.isSpectator()||gain<=0)return 0;
        int through=CorruptionWard.gain(gain,CorruptionWardItem.worn(player),player.getRandom()::nextDouble);
        int before=value(player),after=PlayerCorruptionApi.increase(before,through);
        if(after!=before)player.setData(ModAttachments.PLAYER_CORRUPTION,after);
        if(PlayerCorruptionApi.completed(before,after)&&!player.isCreative())player.hurt(ModDamageTypes.conversion(player.level()),Float.MAX_VALUE);
        return after-before;
    }
    /**
     * Raises a player's corruption by this much, as eating the corruption's bread does; made whole by it, a player
     * in survival is converted at once, as by the curse. A player in creative is corrupted too, but never converted.
     */
    public static void feed(Player player,int gain){raise(player,gain);}
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
