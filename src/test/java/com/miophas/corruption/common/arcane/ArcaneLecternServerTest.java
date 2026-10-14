package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.block.ArcaneLecternBlock;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.ArcaneReading;
import com.miophas.corruption.core.arcane.Codex;
import com.miophas.corruption.core.arcane.Transcription;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The arcane on a real server, where the strings, the Clavicula's tag, the arcane_key data map and the item
 * tags are loaded from the mod's data: what the key reveals, copying at the lectern, and reading parchment.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class ArcaneLecternServerTest {
    private static final List<String> CLAVICULA = List.of("corruption:baal", "corruption:agares", "corruption:corruption", "corruption:transmutation");

    private static ArcaneLecternBlockEntity place(ServerLevel level, int x) {
        BlockPos pos = new BlockPos(x, -50, 3000);
        TestWorld.load(level, x, x, 3000, 3000);
        level.setBlock(pos, ModBlocks.CORRUPTION_ARCANE_LECTERN.get().defaultBlockState(), 3);
        ArcaneLecternBlockEntity lectern = (ArcaneLecternBlockEntity) level.getBlockEntity(pos);
        assertNotNull(lectern);
        return lectern;
    }
    private static ArcaneLecternMenu open(ServerLevel level, ArcaneLecternBlockEntity lectern, Player player) {
        return new ArcaneLecternMenu(1, player.getInventory(), lectern, ContainerLevelAccess.create(level, lectern.getBlockPos()));
    }
    private static ItemStack clavicula() { return new ItemStack(ModItems.CLAVICULA_SALOMONIS.get()); }

    /**
     * Breaks the block and gathers what it spilled, caught as each item joins the world: the test world never
     * ticks, so its entities are never brought into sight to be found afterwards.
     */
    private static List<ItemStack> breakAndGather(ServerLevel level, BlockPos pos) {
        List<ItemEntity> spilled = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> {
            if (event.getEntity() instanceof ItemEntity item && item.blockPosition().closerThan(pos, 3)) spilled.add(item);
        };
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { level.removeBlock(pos, false); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        spilled.forEach(ItemEntity::discard);
        return spilled.stream().map(ItemEntity::getItem).toList();
    }

    @Test void theClaviculaRevealsTheStringsItsTagHolds(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var strings = level.registryAccess().registryOrThrow(ArcaneString.REGISTRY);
            for (String id : CLAVICULA) assertNotNull(strings.get(ArcaneApi.key(id)), id + " is loaded");
            assertEquals(new Codex(CLAVICULA), ArcaneApi.codex(level.registryAccess(), clavicula()), "in the tag's order");
            assertEquals("solve et coagula", ArcaneApi.strings(level.registryAccess(), clavicula()).get(3).value().text());
            assertEquals(Codex.EMPTY, ArcaneApi.codex(level.registryAccess(), new ItemStack(Items.BOOK)), "a book that is no key reveals nothing");
            assertTrue(ArcaneApi.isBlankParchment(new ItemStack(ModItems.PARCHMENT.get())));
            assertTrue(ArcaneApi.isInk(new ItemStack(ModItems.INK.get())));
            assertFalse(ArcaneApi.isInk(new ItemStack(Items.INK_SAC)), "ink sacs are made into ink first");
        });
    }

    @Test void aCopyAtTheLecternUsesOneParchmentAndOneInk(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneLecternBlockEntity lectern = place(level, 41_000);
            Player player = FakePlayerFactory.getMinecraft(level);
            ArcaneLecternMenu menu = open(level, lectern, player);
            assertEquals(Transcription.Check.NO_KEY, menu.check(0));
            lectern.setItem(ArcaneLecternBlockEntity.KEY, clavicula());
            assertTrue(level.getBlockState(lectern.getBlockPos()).getValue(ArcaneLecternBlock.HAS_KEY), "it shows the key lying open");
            assertEquals(Transcription.Check.NO_PARCHMENT, menu.check(1));
            lectern.setItem(ArcaneLecternBlockEntity.PARCHMENT, new ItemStack(ModItems.PARCHMENT.get(), 3));
            assertEquals(Transcription.Check.NO_INK, menu.check(1));
            ItemStack ink = new ItemStack(ModItems.INK.get());
            ink.setDamageValue(ModItems.INK_USES - 2);
            lectern.setItem(ArcaneLecternBlockEntity.INK, ink);
            assertEquals(Transcription.Check.NOT_IN_KEY, menu.check(9), "no such place in the list");
            assertTrue(menu.clickMenuButton(player, 1));
            ItemStack copy = lectern.getItem(ArcaneLecternBlockEntity.OUTPUT);
            assertTrue(copy.is(ModItems.PARCHMENT.get()));
            assertEquals(ArcaneApi.key("corruption:agares"), ArcaneApi.inscription(copy));
            assertEquals(2, lectern.getItem(ArcaneLecternBlockEntity.PARCHMENT).getCount());
            assertEquals(1, ArcaneApi.inkIn(lectern.getItem(ArcaneLecternBlockEntity.INK)), "the bottle has one copy's worth left");
            assertEquals(1, lectern.getItem(ArcaneLecternBlockEntity.KEY).getCount(), "the key is not used up");
            assertEquals(Transcription.Check.NO_ROOM, menu.check(0), "copies of another string have nowhere to go");
            assertTrue(menu.clickMenuButton(player, 1), "copies of the same string stack");
            assertEquals(2, lectern.getItem(ArcaneLecternBlockEntity.OUTPUT).getCount());
            ItemStack bottle = lectern.getItem(ArcaneLecternBlockEntity.INK);
            assertTrue(bottle.is(Items.GLASS_BOTTLE), "used up, the ink leaves its bottle");
            assertTrue(lectern.canTakeItemThroughFace(ArcaneLecternBlockEntity.INK, bottle, Direction.DOWN), "for a hopper below to take");
            assertFalse(menu.clickMenuButton(player, 1), "out of ink");
            assertEquals(1, lectern.getItem(ArcaneLecternBlockEntity.PARCHMENT).getCount());
            lectern.setItem(ArcaneLecternBlockEntity.KEY, ItemStack.EMPTY);
            assertFalse(level.getBlockState(lectern.getBlockPos()).getValue(ArcaneLecternBlock.HAS_KEY));
            List<ItemStack> spilled = breakAndGather(level, lectern.getBlockPos());
            assertTrue(spilled.stream().anyMatch(item -> item.is(ModItems.PARCHMENT.get()) && ArcaneApi.inscription(item) != null), "broken, it spills the copies");
        });
    }

    @Test void onlyBlankParchmentInkAndKeysGoIn(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneLecternBlockEntity lectern = place(level, 41_016);
            ItemStack written = ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key("corruption:baal"));
            assertFalse(lectern.canPlaceItem(ArcaneLecternBlockEntity.PARCHMENT, written), "a parchment bears one string, for good");
            assertTrue(lectern.canPlaceItem(ArcaneLecternBlockEntity.PARCHMENT, new ItemStack(ModItems.PARCHMENT.get())));
            assertFalse(lectern.canPlaceItem(ArcaneLecternBlockEntity.INK, new ItemStack(Items.COAL)));
            assertFalse(lectern.canPlaceItem(ArcaneLecternBlockEntity.INK, new ItemStack(Items.INK_SAC)));
            assertTrue(lectern.canPlaceItem(ArcaneLecternBlockEntity.INK, new ItemStack(ModItems.INK.get())));
            assertFalse(lectern.canTakeItemThroughFace(ArcaneLecternBlockEntity.INK, new ItemStack(ModItems.INK.get()), Direction.DOWN), "ink stays in until used up");
            assertTrue(lectern.canPlaceItem(ArcaneLecternBlockEntity.KEY, clavicula()));
            assertFalse(lectern.canPlaceItem(ArcaneLecternBlockEntity.KEY, new ItemStack(Items.BOOK)));
            assertFalse(lectern.canPlaceItem(ArcaneLecternBlockEntity.OUTPUT, written), "nothing is put among the copies");
            // Hoppers: parchment from above, ink from the sides, copies out below; never the key.
            assertArrayEquals(new int[]{ArcaneLecternBlockEntity.PARCHMENT}, lectern.getSlotsForFace(Direction.UP));
            assertArrayEquals(new int[]{ArcaneLecternBlockEntity.INK}, lectern.getSlotsForFace(Direction.EAST));
            assertArrayEquals(new int[]{ArcaneLecternBlockEntity.OUTPUT, ArcaneLecternBlockEntity.INK}, lectern.getSlotsForFace(Direction.DOWN));
            assertFalse(lectern.canPlaceItemThroughFace(ArcaneLecternBlockEntity.KEY, clavicula(), Direction.UP));
            // What it holds is saved with it, the copies' inscriptions too.
            lectern.setItem(ArcaneLecternBlockEntity.KEY, clavicula());
            lectern.setItem(ArcaneLecternBlockEntity.OUTPUT, written);
            CompoundTag saved = lectern.saveWithoutMetadata(level.registryAccess());
            ArcaneLecternBlockEntity copy = new ArcaneLecternBlockEntity(lectern.getBlockPos(), level.getBlockState(lectern.getBlockPos()));
            copy.loadWithComponents(saved, level.registryAccess());
            assertEquals(ArcaneApi.key("corruption:baal"), ArcaneApi.inscription(copy.getItem(ArcaneLecternBlockEntity.OUTPUT)));
            assertTrue(copy.getItem(ArcaneLecternBlockEntity.KEY).is(ModItems.CLAVICULA_SALOMONIS.get()));
            List<ItemStack> spilled = breakAndGather(level, lectern.getBlockPos());
            assertTrue(spilled.stream().anyMatch(item -> item.is(ModItems.CLAVICULA_SALOMONIS.get())), "broken, it spills the key with the rest");
            assertTrue(spilled.stream().anyMatch(item -> ArcaneApi.inscription(item) != null));
        });
    }

    @Test void aBottleOfInkIsGoodForEightCopiesThenLeavesItsBottle(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ItemStack ink = new ItemStack(ModItems.INK.get());
            assertEquals(ModItems.INK_USES, ArcaneApi.inkIn(ink));
            ItemStack used = ArcaneApi.useInk(ink, 1);
            assertEquals(ModItems.INK_USES - 1, ArcaneApi.inkIn(used));
            assertEquals(ModItems.INK_USES, ArcaneApi.inkIn(ink), "the stack given is left alone");
            assertTrue(ArcaneApi.useInk(used, ModItems.INK_USES - 1).is(Items.GLASS_BOTTLE));
            assertTrue(ArcaneApi.useInk(ink, 100).is(Items.GLASS_BOTTLE), "never more than it holds");
            assertEquals(0, ArcaneApi.inkIn(new ItemStack(Items.INK_SAC)));
        });
    }

    @Test void aParchmentIsUnderstoodOnlyByWhoeverCarriesTheKey(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var baal = ArcaneApi.key("corruption:baal");
            assertEquals(ArcaneReading.Sight.GLYPHS, ArcaneApi.sight(level.registryAccess(), baal, List.of(new ItemStack(Items.BOOK))));
            assertEquals(ArcaneReading.Sight.MEANING, ArcaneApi.sight(level.registryAccess(), baal, List.of(new ItemStack(Items.BOOK), clavicula())));
            ArcaneString string = ArcaneApi.string(level.registryAccess(), baal);
            assertNotNull(string);
            assertEquals("bael rex primus orientis", string.text());
            assertNull(ArcaneApi.string(level.registryAccess(), ArcaneApi.key("corruption:paimon")), "a string the data does not hold");
            ItemStack written = ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), baal);
            ItemStack loaded = ItemStack.parseOptional(level.registryAccess(), (CompoundTag) written.save(level.registryAccess()));
            assertEquals(baal, ArcaneApi.inscription(loaded), "what a parchment bears is saved with it");
        });
    }
}
