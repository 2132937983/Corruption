package com.miophas.corruption.common.network;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Choosing a spell in the wheel: the wand the player holds is told which of its spells to use. Sent from the
 * wheel screen when it closes; only the server writes the wand, as the client only ever reads its own copy.
 */
public record SelectSpellPayload(int index) implements CustomPacketPayload {
    public static final Type<SelectSpellPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "select_spell"));
    public static final StreamCodec<ByteBuf, SelectSpellPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SelectSpellPayload::index, SelectSpellPayload::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    /** The wand in hand is told which of its spells to use, if the choice is one it holds. */
    public static void handle(SelectSpellPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ItemStack wand = player.getMainHandItem();
            if (!(wand.getItem() instanceof ArcaneCastingItem casting)) return;
            casting.store(wand, casting.unit(wand).selecting(payload.index()));
        });
    }

    /** Sends this choice to the server, from the wheel screen; the world itself is untouched here. */
    public static void send(int index) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new SelectSpellPayload(index));
    }
}
