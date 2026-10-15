package com.miophas.corruption.common.item;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.CorruptionWardItem;
import com.miophas.corruption.api.corruption.PlayerCorruptionHolder;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModItems;
import com.mojang.authlib.GameProfile;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Purified water lowers a player's corruption by a point and leaves its bottle; the robe holds back the
 * corruption's growth in its wearer, piece by piece; both are made with pure leaves, and are drawn.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class PurificationServerTest {
    private static FakePlayer player(ServerLevel level, String name, int corruption) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        for (EquipmentSlot slot : EquipmentSlot.values()) player.setItemSlot(slot, ItemStack.EMPTY);
        player.setData(ModAttachments.PLAYER_CORRUPTION, corruption);
        return player;
    }

    @Test void purifiedWaterTakesAPointOfCorruptionAwayAndLeavesItsBottle(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer drinker = player(level, "water_drinker", 5);
            ItemStack waters = new ItemStack(ModItems.PURIFIED_WATER.get(), 2);
            ItemStack left = waters.finishUsingItem(level, drinker);
            assertEquals(4, PlayerCorruptionHolder.of(drinker), "a point lower");
            assertEquals(1, left.getCount(), "one drunk");
            assertTrue(drinker.getInventory().contains(new ItemStack(Items.GLASS_BOTTLE)), "its bottle kept");

            ItemStack last = left.finishUsingItem(level, drinker);
            assertEquals(3, PlayerCorruption.value(drinker));
            assertTrue(last.is(Items.GLASS_BOTTLE), "the last leaves its bottle in hand");

            FakePlayer pure = player(level, "water_pure", 0);
            new ItemStack(ModItems.PURIFIED_WATER.get()).finishUsingItem(level, pure);
            assertEquals(0, PlayerCorruptionHolder.of(pure), "never below none");
        });
    }

    @Test void theRobeHoldsBackTheCorruptionsGrowthPieceByPiece(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer bare = player(level, "robe_bare", 0);
            for (int point = 0; point < 5; point++) PlayerCorruption.raise(bare, 1);
            assertEquals(5, PlayerCorruption.value(bare), "unrobed, every point of growth takes");

            FakePlayer wearer = player(level, "robe_wearer", 0);
            wearer.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ROBE.get()));
            assertEquals(.3, CorruptionWardItem.worn(wearer), 1e-9, "the robe alone holds back three tenths");
            wearer.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.ROBE_HOOD.get()));
            wearer.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ROBE_LEGGINGS.get()));
            wearer.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ROBE_BOOTS.get()));
            assertEquals(.8, CorruptionWardItem.worn(wearer), 1e-9, "the whole of it four fifths");
            wearer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ROBE_HOOD.get()));
            assertEquals(.8, CorruptionWardItem.worn(wearer), 1e-9, "a piece held, not worn, does nothing");

            // Four hundred single points of growth, each tried afresh: about a fifth get through.
            int through = 0;
            for (int trial = 0; trial < 400; trial++) {
                wearer.setData(ModAttachments.PLAYER_CORRUPTION, 0);
                through += PlayerCorruption.raise(wearer, 1);
            }
            assertTrue(through > 40 && through < 130, "about a fifth of the growth gets through: " + through + " of 400");
            wearer.setData(ModAttachments.PLAYER_CORRUPTION, 0);
        });
    }

    @Test void bothAreMadeWithPureLeavesAndTheRobeIsDrawnWorn(MinecraftServer server) throws Exception {
        TestWorld.onServer(server, level -> {
            var water = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "purified_water")).orElse(null);
            assertNotNull(water, "the purified water's recipe loads");
            var bottle = water.value().getIngredients().get(0);
            assertTrue(bottle.test(PotionContents.createItemStack(Items.POTION, Potions.WATER)), "a bottle of water");
            assertFalse(bottle.test(PotionContents.createItemStack(Items.POTION, Potions.AWKWARD)), "not just any potion");
            assertEquals(2, water.value().getIngredients().stream().filter(ingredient -> ingredient.test(new ItemStack(ModItems.PURE_LEAF.get()))).count(),
                    "and two pure leaves");
            for (var piece : ModItems.ROBE_SET) {
                var recipe = level.getRecipeManager().byKey(piece.getId()).orElse(null);
                assertNotNull(recipe, piece.getId() + " is made");
                assertTrue(recipe.value().getIngredients().stream().anyMatch(ingredient -> ingredient.test(new ItemStack(ModItems.PURE_LEAF.get()))),
                        piece.getId() + " with pure leaves woven in");
            }
        });
        assertEquals(java.util.List.of(1, 3, 2, 1), ModItems.ROBE_SET.stream().map(piece -> piece.get().getDefense()).toList(), "as light as leather");
        assertEquals(java.util.List.of(ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS),
                ModItems.ROBE_SET.stream().map(piece -> piece.get().getType()).toList());
        for (String path : new String[]{"models/armor/robe_layer_1", "models/armor/robe_layer_2"}) {
            BufferedImage worn = image("/assets/corruption/textures/" + path + ".png");
            assertEquals(64, worn.getWidth());
            assertEquals(32, worn.getHeight());
        }
        BufferedImage outer = image("/assets/corruption/textures/models/armor/robe_layer_1.png");
        assertEquals(0, outer.getRGB(12, 12) >>> 24, "the hood is open over the face");
        assertNotEquals(0, outer.getRGB(24, 24) >>> 24, "the robe covers the body");
        for (String item : new String[]{"purified_water", "robe_hood", "robe", "robe_leggings", "robe_boots"}) image("/assets/corruption/textures/item/" + item + ".png");
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = PurificationServerTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
