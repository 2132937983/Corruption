package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.ArcaneReading;
import com.miophas.corruption.core.arcane.Grimoire;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * The arcane under items' names. A parchment shows the string it bears in glyphs, and what it means only if
 * its reader carries a key that reveals it; a key says how many strings it reveals, and where to read them.
 * An inscribed parchment also looks written on (models/item/parchment.json, by the corruption:inscribed
 * property).
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class ArcaneTooltip {
    private static final int GLYPH_COLOUR = 0xC6728C;

    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Player reader = event.getEntity();
        HolderLookup.Provider registries = event.getContext().registries();
        if (registries == null && reader != null) registries = reader.level().registryAccess();
        List<Component> lines = event.getToolTip();
        int under = Math.min(1, lines.size());
        if (stack.is(ArcaneApi.PARCHMENT)) lines.addAll(under, parchment(stack, reader, registries));
        if (ArcaneApi.isInk(stack) && stack.isDamageableItem())
            lines.add(under, Component.translatable("tooltip.corruption.ink_left", ArcaneApi.inkIn(stack)).withStyle(ChatFormatting.GRAY));
        if (ArcaneApi.isKey(stack)) {
            lines.add(under, Component.translatable("tooltip.corruption.key_strings", ArcaneApi.strings(registries, stack).size()).withStyle(ChatFormatting.LIGHT_PURPLE));
            lines.add(under + 1, Component.translatable("tooltip.corruption.key_hint").withStyle(ChatFormatting.GRAY));
        }
        if (stack.getItem() instanceof ArcaneCastingItem) lines.addAll(under, wand(stack, registries));
    }

    /** What a wand has been taught, slot by slot, the chosen one marked, and how another is chosen. */
    private static List<Component> wand(ItemStack stack, HolderLookup.Provider registries) {
        Grimoire grimoire = WandSpells.of(stack);
        List<Component> lines = new ArrayList<>();
        int capacity = stack.getItem() instanceof ArcaneCastingItem casting ? casting.capacity() : Grimoire.CAPACITY;
        for (int slot = 0; slot < capacity; slot++) {
            String spell = grimoire.at(slot);
            boolean chosen = slot == grimoire.selected();
            if (spell == null) lines.add(Component.translatable("tooltip.corruption.wand_slot_empty", slot + 1).withStyle(ChatFormatting.DARK_GRAY));
            else lines.add(Component.translatable(chosen ? "tooltip.corruption.wand_slot_chosen" : "tooltip.corruption.wand_slot", slot + 1, name(spell, registries))
                    .withStyle(chosen ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
        }
        lines.add(Component.translatable("tooltip.corruption.wand_key", SpellWheel.OPEN.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
        return lines;
    }

    /** What a spell reads as: its meaning, if the arcane data gives one, else its id. */
    private static Component name(String spell, HolderLookup.Provider registries) {
        ArcaneString string = ArcaneApi.string(registries, ArcaneApi.key(spell));
        return string == null ? Component.literal(spell) : string.meaning();
    }

    private static List<Component> parchment(ItemStack stack, Player reader, HolderLookup.Provider registries) {
        ResourceKey<ArcaneString> inscription = ArcaneApi.inscription(stack);
        if (inscription == null) return List.of(Component.translatable("tooltip.corruption.parchment_blank").withStyle(ChatFormatting.GRAY));
        ArcaneString string = ArcaneApi.string(registries, inscription);
        if (string == null) return List.of(Component.translatable("tooltip.corruption.unknown_string").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        List<ItemStack> carried = new ArrayList<>();
        if (reader != null) for (int slot = 0; slot < reader.getInventory().getContainerSize(); slot++) carried.add(reader.getInventory().getItem(slot));
        boolean understood = ArcaneApi.sight(registries, inscription, carried) == ArcaneReading.Sight.MEANING;
        return List.of(ArcaneApi.glyphs(string.text()).withColor(GLYPH_COLOUR),
                understood ? string.meaning().copy().withStyle(ChatFormatting.WHITE) : Component.translatable("tooltip.corruption.unreadable").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    /** For models/item/parchment.json: 1 once the parchment bears a string. Registered in client setup. */
    public static void registerItemProperties() {
        ItemProperties.register(ModItems.PARCHMENT.get(), ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "inscribed"),
                (stack, level, entity, seed) -> ArcaneApi.inscription(stack) == null ? 0 : 1);
    }

    private ArcaneTooltip() {}
}
