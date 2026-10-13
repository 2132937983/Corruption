package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> TYPES=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,Corruption.MOD_ID);
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<Integer>> PLAYER_CORRUPTION=TYPES.register("player_corruption",
        ()->AttachmentType.builder(()->0).serialize(Codec.intRange(0,PlayerCorruptionApi.max())).copyOnDeath().sync(ByteBufCodecs.VAR_INT).build());
    private ModAttachments(){}
}
