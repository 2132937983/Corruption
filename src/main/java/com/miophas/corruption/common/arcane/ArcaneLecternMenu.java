package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.core.arcane.Codex;
import com.miophas.corruption.core.arcane.Transcription;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The arcane lectern's screen: the key on the left, the list of its strings in the middle, and on the right
 * blank parchment, ink and the copies made, then the player's inventory. Choosing a string and pressing
 * Transcribe sends its place in the list as a button press; the server makes the copy as
 * core.arcane.Transcription allows. The positions are those drawn in gui_corruption_arcane_lectern.png.
 */
public final class ArcaneLecternMenu extends AbstractContainerMenu {
    public static final int KEY_X = 8, KEY_Y = 18, PARCHMENT_X = 152, PARCHMENT_Y = 18, INK_X = 152, INK_Y = 40, OUTPUT_X = 152, OUTPUT_Y = 76;
    public static final int INVENTORY_X = 8, INVENTORY_Y = 128, HOTBAR_Y = 186;
    public static final int FIRST_PLAYER_SLOT = ArcaneLecternBlockEntity.SLOTS, END_PLAYER_SLOT = FIRST_PLAYER_SLOT + 36;

    private final Container lectern;
    private final ContainerLevelAccess access;
    private final Level level;

    /** Client side: the server sends the contents. */
    public ArcaneLecternMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(ArcaneLecternBlockEntity.SLOTS), ContainerLevelAccess.NULL);
    }

    public ArcaneLecternMenu(int id, Inventory inventory, Container lectern, ContainerLevelAccess access) {
        super(ModMenus.ARCANE_LECTERN.get(), id);
        checkContainerSize(lectern, ArcaneLecternBlockEntity.SLOTS);
        this.lectern = lectern; this.access = access; this.level = inventory.player.level();
        addSlot(new Holding(lectern, ArcaneLecternBlockEntity.KEY, KEY_X, KEY_Y) {
            @Override public int getMaxStackSize() { return 1; }
        });
        addSlot(new Holding(lectern, ArcaneLecternBlockEntity.PARCHMENT, PARCHMENT_X, PARCHMENT_Y));
        addSlot(new Holding(lectern, ArcaneLecternBlockEntity.INK, INK_X, INK_Y));
        addSlot(new Holding(lectern, ArcaneLecternBlockEntity.OUTPUT, OUTPUT_X, OUTPUT_Y));
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++)
                addSlot(new Slot(inventory, 9 + row * 9 + column, INVENTORY_X + column * 18, INVENTORY_Y + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
    }

    /** A lectern slot, taking only what belongs in it. */
    private static class Holding extends Slot {
        Holding(Container container, int slot, int x, int y) { super(container, slot, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return ArcaneLecternBlockEntity.fits(getContainerSlot(), stack); }
    }

    /** The key on the lectern. */
    public ItemStack key() { return lectern.getItem(ArcaneLecternBlockEntity.KEY); }
    /** The strings it reveals, in its order, with what they say. */
    public List<Holder<ArcaneString>> strings() { return ArcaneApi.strings(level.registryAccess(), key()); }
    public Codex codex() { return ArcaneApi.codex(level.registryAccess(), key()); }
    /** The lectern's slots as a transcription desk. */
    public Transcription.Desk desk() { return new LecternDesk(lectern, level.registryAccess()); }
    /** Whether the string at this place in the list could be copied now, and if not, why. */
    public Transcription.Check check(int index) { return Transcription.check(desk(), codex().at(index)); }

    /** Transcribe, pressed with the string at this place in the list chosen. */
    @Override public boolean clickMenuButton(Player player, int index) {
        if (Transcription.transcribe(desk(), codex().at(index)) != Transcription.Check.READY) return false;
        access.execute((world, pos) -> world.playSound(null, pos, SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.BLOCKS, 1, 1));
        return true;
    }

    /** Shift-click: out of the lectern into the inventory; from the inventory to the slot that takes it, else between inventory and hotbar. */
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < FIRST_PLAYER_SLOT) {
            if (!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, true)) return ItemStack.EMPTY;
        } else {
            int target = -1;
            for (int lecternSlot = 0; lecternSlot < ArcaneLecternBlockEntity.OUTPUT && target < 0; lecternSlot++)
                if (ArcaneLecternBlockEntity.fits(lecternSlot, stack)) target = lecternSlot;
            int hotbar = END_PLAYER_SLOT - 9;
            boolean moved = target >= 0 ? moveItemStackTo(stack, target, target + 1, false)
                    : index < hotbar ? moveItemStackTo(stack, hotbar, END_PLAYER_SLOT, false) : moveItemStackTo(stack, FIRST_PLAYER_SLOT, hotbar, false);
            if (!moved) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override public boolean stillValid(Player player) { return lectern.stillValid(player); }
}
