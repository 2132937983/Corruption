package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.PlayerCorruptionRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Where a player's corruption is kept: the attachment corruption:player_corruption, which the mod registers, saves,
 * keeps through death (as the mod's rules say) and sends to the player's client. Reading and setting it here is
 * for what lowers corruption, such as a purifier; raising it goes through the mod's own rules instead, which
 * convert a player made whole by it and let what they wear hold it back.
 */
public final class PlayerCorruptionHolder {
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ATTACHMENT =
            DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ResourceLocation.fromNamespaceAndPath("corruption", "player_corruption"));

    /** How corrupted a player is, 0 to {@link PlayerCorruptionApi#max()}. */
    public static int of(Player player) { return player.getData(ATTACHMENT.get()); }

    /** Sets how corrupted a player is, kept within the scale. */
    public static void set(Player player, int corruption) { player.setData(ATTACHMENT.get(), PlayerCorruptionRules.clamp(corruption)); }

    private PlayerCorruptionHolder() {}
}
