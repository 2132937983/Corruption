package com.miophas.corruption.common.network;

import com.miophas.corruption.Corruption;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * A boss taking hold of a player's mind (the mind control skill), from the server to that player's client: which
 * creature holds it (its id in the world), for how many ticks (0 lets go), whether their feet are taken as well as their
 * view, and how hard their view is drawn to it each frame (0 to 1).
 */
public record MindControlPayload(int holder, int ticks, boolean moves, float grip) implements CustomPacketPayload {
    public static final Type<MindControlPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "mind_control"));
    public static final StreamCodec<ByteBuf, MindControlPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MindControlPayload::holder, ByteBufCodecs.VAR_INT, MindControlPayload::ticks,
            ByteBufCodecs.BOOL, MindControlPayload::moves, ByteBufCodecs.FLOAT, MindControlPayload::grip, MindControlPayload::new);

    /** What the client does with it: set as the game starts on a client, so that this class knows nothing of the client. */
    public static Consumer<MindControlPayload> received = payload -> {};

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MindControlPayload payload, IPayloadContext context) { context.enqueueWork(() -> received.accept(payload)); }

    /** Takes hold of this player's mind for so long, from this creature. */
    public static void hold(ServerPlayer player, int holder, int ticks, boolean moves, float grip) {
        PacketDistributor.sendToPlayer(player, new MindControlPayload(holder, ticks, moves, grip));
    }

    /** Lets this player's mind go. */
    public static void release(ServerPlayer player) { PacketDistributor.sendToPlayer(player, new MindControlPayload(-1, 0, false, 0)); }
}
