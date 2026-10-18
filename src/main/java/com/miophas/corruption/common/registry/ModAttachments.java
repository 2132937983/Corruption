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
    /**
     * A player's corruption as it was before a corrupted robe took hold of it, given back when the robe comes off
     * (CorruptedRobeRules.step); -1 while no robe holds it. Saved, so the robe's hold outlasts leaving the game; not kept
     * through death, which takes all of a player's corruption (PlayerCorruption.clone), so there is nothing to give back.
     */
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<Integer>> CORRUPTION_KEPT=TYPES.register("corruption_kept",
        ()->AttachmentType.builder(()->com.miophas.corruption.core.corruption.CorruptedRobeRules.NOTHING_KEPT).serialize(Codec.intRange(-1,PlayerCorruptionApi.max())).build());
    /**
     * What a corrupted robe holds a player's corruption at: what it was, or the grade's threshold if that is higher
     * (CorruptedRobeRules.step); -1 while no robe holds it. Saved, and not kept through death, as CORRUPTION_KEPT.
     */
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<Integer>> CORRUPTION_HELD=TYPES.register("corruption_held",
        ()->AttachmentType.builder(()->com.miophas.corruption.core.corruption.CorruptedRobeRules.NOTHING_KEPT).serialize(Codec.intRange(-1,PlayerCorruptionApi.max())).build());
    /**
     * The corrupted ground of a chunk that is dormant: positions the infection took that never spread on from there
     * (DormantGround). Saved with the chunk itself, so a save writes only the chunks whose marks changed; nothing is
     * written for a chunk whose set is empty.
     */
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<com.miophas.corruption.common.infection.DormantGround.Marks>> DORMANT_GROUND=
        TYPES.register("dormant_ground",()->AttachmentType.builder(com.miophas.corruption.common.infection.DormantGround.Marks::new)
            .serialize(com.miophas.corruption.common.infection.DormantGround.Marks.CODEC,marks->!marks.isEmpty()).build());
    private ModAttachments(){}
}
