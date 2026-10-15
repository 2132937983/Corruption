package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.arcane.ArcaneSpellApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.core.arcane.CastingUnit;
import net.minecraft.resources.ResourceKey;
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
 * The arcane pedestal's screen: a parchment bearing a string and the wand to be taught on the left; in the
 * middle, the wand's slots, as many as it has room for, each holding a spell or nothing; then the player's
 * inventory. Choosing a slot and pressing Inscribe sends the slot as a button press, and the server writes the
 * parchment's spell into it: the next free slot takes it, a written one is written over. Teaching costs
 * nothing. The positions are those drawn in gui_corruption_arcane_pedestal.png, but for the wand's slots, which
 * {@link #placeAt} lays out for however many there are.
 */
public final class ArcanePedestalMenu extends AbstractContainerMenu {
    public static final int PARCHMENT_X = 8, PARCHMENT_Y = 20, WAND_X = 8, WAND_Y = 74;
    public static final int INVENTORY_X = 8, INVENTORY_Y = 128, HOTBAR_Y = 186;
    public static final int FIRST_PLAYER_SLOT = ArcanePedestalBlockEntity.SLOTS, END_PLAYER_SLOT = FIRST_PLAYER_SLOT + 36;
    /** The sunken frame the wand's slots are shown in: left, top, right, bottom, all drawn. */
    public static final int GRIMOIRE_LEFT = 29, GRIMOIRE_TOP = 16, GRIMOIRE_RIGHT = 150, GRIMOIRE_BOTTOM = 82;
    /** A wand's slot as shown, at most; how far apart they stand; how many to a row. */
    public static final int PLACE = 18, GAP = 2, PLACES_PER_ROW = 6;

    /** Whether the parchment's spell can be written into a slot, and if not, why not. */
    public enum Status { READY, NO_PARCHMENT, NO_WAND, NO_SPELL, ALREADY_KNOWN, NO_ROOM }

    private final Container pedestal;
    private final ContainerLevelAccess access;
    private final Level level;

    /** Client side: the server sends the contents. */
    public ArcanePedestalMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(ArcanePedestalBlockEntity.SLOTS), ContainerLevelAccess.NULL);
    }

    public ArcanePedestalMenu(int id, Inventory inventory, Container pedestal, ContainerLevelAccess access) {
        super(ModMenus.ARCANE_PEDESTAL.get(), id);
        checkContainerSize(pedestal, ArcanePedestalBlockEntity.SLOTS);
        this.pedestal = pedestal; this.access = access; this.level = inventory.player.level();
        addSlot(new Holding(pedestal, ArcanePedestalBlockEntity.PARCHMENT, PARCHMENT_X, PARCHMENT_Y));
        addSlot(new Holding(pedestal, ArcanePedestalBlockEntity.WAND, WAND_X, WAND_Y));
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++)
                addSlot(new Slot(inventory, 9 + row * 9 + column, INVENTORY_X + column * 18, INVENTORY_Y + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
    }

    /** A pedestal slot, taking one of only what belongs in it. */
    private static final class Holding extends Slot {
        Holding(Container container, int slot, int x, int y) { super(container, slot, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return ArcanePedestalBlockEntity.fits(getContainerSlot(), stack); }
        @Override public int getMaxStackSize() { return 1; }
    }

    public ItemStack parchment() { return pedestal.getItem(ArcanePedestalBlockEntity.PARCHMENT); }
    public ItemStack wand() { return pedestal.getItem(ArcanePedestalBlockEntity.WAND); }

    /** The wand on the pedestal, as a casting unit, or null if there is none. */
    public CastingUnit unit() { return wand().getItem() instanceof ArcaneCastingItem casting ? casting.unit(wand()) : null; }

    /** How many slots the wand on the pedestal has, or none without one. */
    public int capacity() { CastingUnit unit = unit(); return unit == null ? 0 : unit.capacity(); }

    /** The string the parchment bears, or null. */
    public ResourceKey<ArcaneString> string() { return ArcaneApi.inscription(parchment()); }

    /** The wand's next free slot, or -1 if it has none or there is no wand. */
    public int firstFree() {
        CastingUnit unit = unit();
        return unit == null || unit.grimoire().size() >= unit.capacity() ? -1 : unit.grimoire().size();
    }

    /** Whether the parchment's spell could be written into this slot of the wand now, and if not, why not. */
    public Status check(int slot) {
        if (parchment().isEmpty()) return Status.NO_PARCHMENT;
        CastingUnit unit = unit();
        if (unit == null) return Status.NO_WAND;
        ResourceKey<ArcaneString> string = string();
        if (string == null || !ArcaneSpellApi.castable(level.registryAccess(), string)) return Status.NO_SPELL;
        return switch (unit.checkAt(slot, ArcaneApi.id(string))) {
            case READY -> Status.READY;
            case ALREADY_KNOWN -> Status.ALREADY_KNOWN;
            case FULL -> Status.NO_ROOM;
        };
    }

    /** Inscribe, pressed with this slot of the wand chosen: the parchment's spell is written into it. */
    @Override public boolean clickMenuButton(Player player, int slot) {
        if (check(slot) != Status.READY || !(wand().getItem() instanceof ArcaneCastingItem casting)) return false;
        casting.store(wand(), unit().writingAt(slot, ArcaneApi.id(string())));
        pedestal.setChanged();
        access.execute((world, pos) -> world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1, 1.2F));
        return true;
    }

    /**
     * Where one of a wand's slots is shown, for a wand with room for so many: {x, y, size} from the panel's
     * corner. Six to a row, the rows centred in the frame, the last row's slots centred under the others; with
     * more than three rows, they are shown smaller so that all of them fit.
     */
    public static int[] placeAt(int index, int capacity) {
        int width = GRIMOIRE_RIGHT - GRIMOIRE_LEFT - 3, height = GRIMOIRE_BOTTOM - GRIMOIRE_TOP - 3;
        int columns = Math.max(1, Math.min(capacity, PLACES_PER_ROW)), rows = Math.max(1, (capacity + PLACES_PER_ROW - 1) / PLACES_PER_ROW);
        int size = Math.min(PLACE, Math.min((width - (columns - 1) * GAP) / columns, (height - (rows - 1) * GAP) / rows));
        int row = index / PLACES_PER_ROW, column = index % PLACES_PER_ROW;
        int inRow = Math.min(PLACES_PER_ROW, capacity - row * PLACES_PER_ROW);
        int rowWidth = inRow * size + (inRow - 1) * GAP, allHeight = rows * size + (rows - 1) * GAP;
        int left = GRIMOIRE_LEFT + 2 + (width - rowWidth) / 2, top = GRIMOIRE_TOP + 2 + (height - allHeight) / 2;
        return new int[]{left + column * (size + GAP), top + row * (size + GAP), size};
    }

    /** Shift-click: off the pedestal into the inventory; from the inventory onto the slot that takes it, else between inventory and hotbar. */
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < FIRST_PLAYER_SLOT) {
            if (!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, true)) return ItemStack.EMPTY;
        } else {
            int target = -1;
            for (int pedestalSlot = 0; pedestalSlot < ArcanePedestalBlockEntity.SLOTS && target < 0; pedestalSlot++)
                if (ArcanePedestalBlockEntity.fits(pedestalSlot, stack) && !slots.get(pedestalSlot).hasItem()) target = pedestalSlot;
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

    @Override public boolean stillValid(Player player) { return pedestal.stillValid(player); }
}
