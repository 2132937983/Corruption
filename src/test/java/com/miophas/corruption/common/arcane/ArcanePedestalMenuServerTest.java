package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.common.client.ArcanePedestalScreen;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.Grimoire;
import com.mojang.authlib.GameProfile;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The arcane pedestal's screen on a real server, where the spells are loaded from the mod's data: what its
 * slots take, writing a spell into a wand's slot of choice, what it refuses, and its picture.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class ArcanePedestalMenuServerTest {
    private static final String AGARES = "corruption:agares", BAAL = "corruption:baal", CORRUPTION = "corruption:corruption", TRANSMUTATION = "corruption:transmutation";

    private static ArcanePedestalBlockEntity place(ServerLevel level, int x) {
        BlockPos pos = new BlockPos(x, -50, 3000);
        TestWorld.load(level, x, x, 3000, 3000);
        level.setBlock(pos, ModBlocks.CORRUPTION_ARCANE_PEDESTAL.get().defaultBlockState(), 3);
        return (ArcanePedestalBlockEntity) level.getBlockEntity(pos);
    }
    private static FakePlayer player(ServerLevel level, String name) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.getInventory().clearContent();
        return player;
    }
    private static ArcanePedestalMenu menu(ServerLevel level, FakePlayer player, ArcanePedestalBlockEntity pedestal) {
        return new ArcanePedestalMenu(0, player.getInventory(), pedestal, ContainerLevelAccess.create(level, pedestal.getBlockPos()));
    }
    private static ItemStack parchment(String spell) { return ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key(spell)); }
    private static ItemStack wand(String... spells) {
        ItemStack wand = new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get());
        Grimoire grimoire = Grimoire.EMPTY;
        for (String spell : spells) grimoire = grimoire.inscribed(spell);
        WandSpells.set(wand, grimoire);
        return wand;
    }

    @Test void itsSlotsTakeOnlyWhatBelongsOneAtATimeAndUsingItOpensIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcanePedestalBlockEntity pedestal = place(level, 47_000);
            FakePlayer player = player(level, "pedestal_opener");
            try {
                ArcanePedestalMenu menu = menu(level, player, pedestal);
                assertTrue(menu.getSlot(ArcanePedestalBlockEntity.PARCHMENT).mayPlace(parchment(AGARES)));
                assertFalse(menu.getSlot(ArcanePedestalBlockEntity.PARCHMENT).mayPlace(new ItemStack(ModItems.PARCHMENT.get())), "blank parchment teaches nothing");
                assertFalse(menu.getSlot(ArcanePedestalBlockEntity.PARCHMENT).mayPlace(wand()));
                assertTrue(menu.getSlot(ArcanePedestalBlockEntity.WAND).mayPlace(wand()));
                assertFalse(menu.getSlot(ArcanePedestalBlockEntity.WAND).mayPlace(new ItemStack(Items.STICK)));
                assertEquals(1, menu.getSlot(ArcanePedestalBlockEntity.WAND).getMaxStackSize());
                assertEquals(0, pedestal.getSlotsForFace(Direction.UP).length, "hoppers have no business with it");

                BlockPos pos = pedestal.getBlockPos();
                BlockState state = level.getBlockState(pos);
                BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
                player.setItemInHand(InteractionHand.MAIN_HAND, parchment(AGARES));
                assertEquals(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION, state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit),
                        "nothing in hand acts on it at once");
                assertTrue(pedestal.isEmpty());
                List<MenuProvider> opened = new ArrayList<>();
                FakePlayer watched = new FakePlayer(level, new GameProfile(UUID.nameUUIDFromBytes("pedestal_watched".getBytes()), "pedestal_watched")) {
                    @Override public OptionalInt openMenu(MenuProvider menu) { opened.add(menu); return OptionalInt.of(1); }
                    @Override public OptionalInt openMenu(MenuProvider menu, Consumer<RegistryFriendlyByteBuf> extra) { opened.add(menu); return OptionalInt.of(1); }
                };
                assertEquals(InteractionResult.CONSUME, state.useWithoutItem(level, watched, hit));
                assertEquals(List.of(pedestal), opened, "it opens its screen");
            } finally {
                level.removeBlock(pedestal.getBlockPos(), false);
                player.getInventory().clearContent();
            }
        });
    }

    @Test void aSpellIsWrittenIntoTheNextFreeSlotOrOverAWrittenOneAndTheParchmentStays(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcanePedestalBlockEntity pedestal = place(level, 47_016);
            FakePlayer player = player(level, "pedestal_teacher");
            try {
                ArcanePedestalMenu menu = menu(level, player, pedestal);
                pedestal.setItem(ArcanePedestalBlockEntity.PARCHMENT, parchment(AGARES));
                pedestal.setItem(ArcanePedestalBlockEntity.WAND, wand(BAAL));
                assertEquals(3, menu.capacity(), "the mod's wand has three slots");
                assertEquals(1, menu.firstFree());
                assertEquals(ArcanePedestalMenu.Status.READY, menu.check(1));
                assertEquals(ArcanePedestalMenu.Status.NO_ROOM, menu.check(2), "no gaps: the next free slot, or one already written");
                assertTrue(menu.clickMenuButton(player, 1));
                assertEquals(List.of(BAAL, AGARES), WandSpells.of(pedestal.wand()).spells());
                assertTrue(ArcaneApi.inscription(pedestal.parchment()) != null, "teaching costs nothing: the parchment stays");
                assertEquals(ArcanePedestalMenu.Status.ALREADY_KNOWN, menu.check(2));
                assertFalse(menu.clickMenuButton(player, 2), "a spell is never held twice");

                pedestal.setItem(ArcanePedestalBlockEntity.WAND, wand(BAAL, CORRUPTION, TRANSMUTATION));
                WandSpells.set(pedestal.wand(), WandSpells.of(pedestal.wand()).selecting(2));
                assertEquals(-1, menu.firstFree(), "full");
                assertEquals(ArcanePedestalMenu.Status.NO_ROOM, menu.check(3));
                assertTrue(menu.clickMenuButton(player, 1), "but a written slot may be written over");
                assertEquals(List.of(BAAL, AGARES, TRANSMUTATION), WandSpells.of(pedestal.wand()).spells());
                assertEquals(2, WandSpells.of(pedestal.wand()).selected(), "and the wand casts what it cast before");
            } finally {
                level.removeBlock(pedestal.getBlockPos(), false);
            }
        });
    }

    @Test void itRefusesWhatItCannotDoWhateverTheClientSays(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcanePedestalBlockEntity pedestal = place(level, 47_032);
            FakePlayer player = player(level, "pedestal_refused");
            try {
                ArcanePedestalMenu menu = menu(level, player, pedestal);
                assertEquals(ArcanePedestalMenu.Status.NO_PARCHMENT, menu.check(0));
                pedestal.setItem(ArcanePedestalBlockEntity.PARCHMENT, parchment(AGARES));
                assertEquals(ArcanePedestalMenu.Status.NO_WAND, menu.check(0));
                assertFalse(menu.clickMenuButton(player, 0));
                pedestal.setItem(ArcanePedestalBlockEntity.WAND, wand());
                for (int slot : new int[]{-1, 1, 3, 99}) assertFalse(menu.clickMenuButton(player, slot), "slot " + slot);
                assertTrue(WandSpells.of(pedestal.wand()).isEmpty(), "and the wand is untouched");
                pedestal.setItem(ArcanePedestalBlockEntity.PARCHMENT, parchment("corruption:no_such_string"));
                assertEquals(ArcanePedestalMenu.Status.NO_SPELL, menu.check(0), "a string with no spell cannot be taught");
                assertFalse(menu.clickMenuButton(player, 0));
            } finally {
                level.removeBlock(pedestal.getBlockPos(), false);
            }
        });
    }

    @Test void shiftClickingLaysEachOnItsSlotAndPlayersNearItSeeWhatLiesThere(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcanePedestalBlockEntity pedestal = place(level, 47_048);
            FakePlayer player = player(level, "pedestal_shifter");
            try {
                ArcanePedestalMenu menu = menu(level, player, pedestal);
                player.getInventory().setItem(10, parchment(AGARES).copyWithCount(3));
                player.getInventory().setItem(11, wand(BAAL));
                int first = ArcanePedestalMenu.FIRST_PLAYER_SLOT;
                menu.quickMoveStack(player, first + 1);
                menu.quickMoveStack(player, first + 2);
                assertEquals(1, pedestal.parchment().getCount(), "one parchment is laid on it");
                assertEquals(2, player.getInventory().getItem(10).getCount(), "the rest stay in hand");
                assertTrue(pedestal.wand().is(ModItems.CORRUPTION_ARCANE_STICK.get()));

                ArcanePedestalBlockEntity seen = new ArcanePedestalBlockEntity(pedestal.getBlockPos(), pedestal.getBlockState());
                seen.handleUpdateTag(pedestal.getUpdateTag(level.registryAccess()), level.registryAccess());
                assertEquals(ArcaneApi.key(AGARES), ArcaneApi.inscription(seen.parchment()), "players near it see the parchment");
                assertTrue(seen.wand().is(ModItems.CORRUPTION_ARCANE_STICK.get()), "and the wand");

                menu.quickMoveStack(player, ArcanePedestalBlockEntity.WAND);
                assertTrue(pedestal.wand().isEmpty(), "shift-clicked off, back into the inventory");
                pedestal.setItem(ArcanePedestalBlockEntity.PARCHMENT, ItemStack.EMPTY);
                CompoundTag emptied = pedestal.getUpdateTag(level.registryAccess());
                assertFalse(emptied.isEmpty(), "an update with nothing in it is dropped, and the old items would go on floating there");
                seen.handleUpdateTag(emptied, level.registryAccess());
                assertTrue(seen.parchment().isEmpty() && seen.wand().isEmpty());
            } finally {
                level.removeBlock(pedestal.getBlockPos(), false);
                player.getInventory().clearContent();
            }
        });
    }

    @Test void brokenItSpillsWhatLiesOnIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcanePedestalBlockEntity pedestal = place(level, 47_064);
            pedestal.setItem(ArcanePedestalBlockEntity.PARCHMENT, parchment(AGARES));
            pedestal.setItem(ArcanePedestalBlockEntity.WAND, wand(BAAL));
            List<Entity> spilled = new ArrayList<>();
            Consumer<EntityJoinLevelEvent> catcher = event -> spilled.add(event.getEntity());
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
            try { level.destroyBlock(pedestal.getBlockPos(), false); }
            finally { NeoForge.EVENT_BUS.unregister(catcher); }
            List<ItemStack> items = spilled.stream().filter(ItemEntity.class::isInstance).map(entity -> ((ItemEntity) entity).getItem()).toList();
            assertTrue(items.stream().anyMatch(item -> ArcaneApi.inscription(item) != null), "the parchment: " + items);
            assertTrue(items.stream().anyMatch(item -> WandSpells.of(item).knows(BAAL)), "and the wand, as it was: " + items);
            spilled.forEach(Entity::discard);
        });
    }

    @Test void theWandsSlotsAreLaidOutInTheFrameHoweverManyThereAre() {
        for (int capacity = 1; capacity <= 30; capacity++) {
            List<int[]> places = new ArrayList<>();
            for (int slot = 0; slot < capacity; slot++) {
                int[] at = ArcanePedestalMenu.placeAt(slot, capacity);
                assertTrue(at[2] >= 6, capacity + ": slot " + slot + " stays large enough to see");
                assertTrue(at[0] > ArcanePedestalMenu.GRIMOIRE_LEFT && at[1] > ArcanePedestalMenu.GRIMOIRE_TOP, capacity + ": slot " + slot + " inside the frame");
                assertTrue(at[0] + at[2] < ArcanePedestalMenu.GRIMOIRE_RIGHT && at[1] + at[2] < ArcanePedestalMenu.GRIMOIRE_BOTTOM, capacity + ": slot " + slot + " inside the frame");
                for (int[] other : places)
                    assertFalse(at[0] < other[0] + other[2] && other[0] < at[0] + at[2] && at[1] < other[1] + other[2] && other[1] < at[1] + at[2], capacity + ": slots apart");
                places.add(at);
            }
        }
        assertEquals(ArcanePedestalMenu.PLACE, ArcanePedestalMenu.placeAt(0, 3)[2], "a wand of three shows them full size");
        assertEquals(ArcanePedestalMenu.PLACE, ArcanePedestalMenu.placeAt(11, 12)[2], "and a tool of twelve, in two rows");
        assertTrue(ArcanePedestalMenu.placeAt(6, 12)[1] > ArcanePedestalMenu.placeAt(0, 12)[1], "the seventh starts the second row");
    }

    private static BufferedImage gui() throws Exception {
        try (InputStream in = ArcanePedestalMenuServerTest.class.getResourceAsStream("/assets/corruption/textures/gui/gui_corruption_arcane_pedestal.png")) {
            assertNotNull(in);
            return ImageIO.read(in);
        }
    }
    private static int rgb(BufferedImage image, int x, int y) { return image.getRGB(x, y) & 0xFFFFFF; }

    @Test void itsPictureHasItsSlotsFrameAndButtonWhereTheCodeSays() throws Exception {
        BufferedImage gui = gui();
        int slotFill = rgb(gui, ArcanePedestalMenu.PARCHMENT_X, ArcanePedestalMenu.PARCHMENT_Y);
        for (int[] slot : new int[][]{{ArcanePedestalMenu.PARCHMENT_X, ArcanePedestalMenu.PARCHMENT_Y}, {ArcanePedestalMenu.WAND_X, ArcanePedestalMenu.WAND_Y}}) {
            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) assertEquals(slotFill, rgb(gui, slot[0] + x, slot[1] + y), "slot at " + slot[0] + "," + slot[1]);
            assertNotEquals(slotFill, rgb(gui, slot[0] - 1, slot[1] + 8), "and no wider");
        }
        int inventoryFill = rgb(gui, ArcanePedestalMenu.INVENTORY_X, ArcanePedestalMenu.INVENTORY_Y);
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++)
                assertEquals(inventoryFill, rgb(gui, ArcanePedestalMenu.INVENTORY_X + column * 18 + 15, ArcanePedestalMenu.INVENTORY_Y + row * 18 + 15));
        for (int column = 0; column < 9; column++) assertEquals(inventoryFill, rgb(gui, ArcanePedestalMenu.INVENTORY_X + column * 18, ArcanePedestalMenu.HOTBAR_Y));
        int frameFill = rgb(gui, ArcanePedestalMenu.GRIMOIRE_LEFT + 1, ArcanePedestalMenu.GRIMOIRE_TOP + 1);
        assertEquals(frameFill, rgb(gui, ArcanePedestalMenu.GRIMOIRE_RIGHT - 1, ArcanePedestalMenu.GRIMOIRE_BOTTOM - 1), "the frame's inside");
        assertNotEquals(frameFill, rgb(gui, ArcanePedestalMenu.GRIMOIRE_RIGHT, ArcanePedestalMenu.GRIMOIRE_BOTTOM - 1), "and its edge");
        for (int look = 0; look < 3; look++)
            assertEquals(255, gui.getRGB(ArcanePedestalScreen.BUTTON_U + ArcanePedestalScreen.BUTTON_WIDTH / 2, ArcanePedestalScreen.BUTTON_V + look * ArcanePedestalScreen.BUTTON_HEIGHT + 3) >>> 24, "button look " + look);
    }
}
