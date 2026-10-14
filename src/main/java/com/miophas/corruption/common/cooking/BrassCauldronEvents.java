package com.miophas.corruption.common.cooking;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.block.BrassCauldronBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** The game's events the brass cauldron answers: sneaking with a bucket at it fills or empties it. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class BrassCauldronEvents {
    @SubscribeEvent public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) { BrassCauldronBlock.onSneakUse(event); }

    private BrassCauldronEvents() {}
}
