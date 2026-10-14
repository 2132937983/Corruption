package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.core.arcane.Codex;
import com.miophas.corruption.core.arcane.Transcription;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * The arcane lectern's slots as a transcription desk: the key, blank parchment and ink it holds, and its
 * output, where each copy goes. The rules are core.arcane.Transcription's; this only reads and changes slots.
 * The lectern's menu makes one on either side, so its screen can say what a copy still needs.
 */
public final class LecternDesk implements Transcription.Desk {
    private final Container slots;
    private final HolderLookup.Provider registries;

    public LecternDesk(Container slots, HolderLookup.Provider registries) { this.slots = slots; this.registries = registries; }

    @Override public Codex key() { return ArcaneApi.codex(registries, slots.getItem(ArcaneLecternBlockEntity.KEY)); }

    @Override public int blankParchment() {
        ItemStack parchment = slots.getItem(ArcaneLecternBlockEntity.PARCHMENT);
        return ArcaneApi.isBlankParchment(parchment) ? parchment.getCount() : 0;
    }

    @Override public int ink() { return ArcaneApi.inkIn(slots.getItem(ArcaneLecternBlockEntity.INK)); }

    @Override public boolean hasRoomFor(String string) {
        ItemStack copy = copy(string), output = slots.getItem(ArcaneLecternBlockEntity.OUTPUT);
        if (copy.isEmpty()) return false;
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, copy) && output.getCount() < output.getMaxStackSize();
    }

    @Override public void write(String string, int parchment, int ink) {
        ItemStack copy = copy(string), output = slots.getItem(ArcaneLecternBlockEntity.OUTPUT);
        slots.removeItem(ArcaneLecternBlockEntity.PARCHMENT, parchment);
        slots.setItem(ArcaneLecternBlockEntity.INK, ArcaneApi.useInk(slots.getItem(ArcaneLecternBlockEntity.INK), ink));
        if (output.isEmpty()) slots.setItem(ArcaneLecternBlockEntity.OUTPUT, copy);
        else output.grow(copy.getCount());
        slots.setChanged();
    }

    /** One of the parchment waiting, bearing the string; nothing without parchment or a string by that id. */
    private ItemStack copy(String string) {
        ResourceKey<ArcaneString> key = ArcaneApi.key(string);
        ItemStack parchment = slots.getItem(ArcaneLecternBlockEntity.PARCHMENT);
        return key == null || parchment.isEmpty() ? ItemStack.EMPTY : ArcaneApi.inscribed(parchment, key);
    }
}
