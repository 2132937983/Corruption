package com.miophas.corruption.common.network;

import com.miophas.corruption.Corruption;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** What the mod sends over the wire: the wheel's choice of spell, from the client to the server. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModNetwork {
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(SelectSpellPayload.TYPE, SelectSpellPayload.STREAM_CODEC, SelectSpellPayload::handle);
    }

    private ModNetwork() {}
}
