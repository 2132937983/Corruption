package com.miophas.corruption.common.item;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModItems;
import com.mojang.authlib.GameProfile;
import java.io.InputStream;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Corrupted Bread: eaten as bread is, it restores no hunger and no saturation, can be eaten however full one is,
 * and leaves its eater one point more corrupted; it is baked in the brass cauldron.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptedBreadServerTest {
    @Test void eatingItFeedsTheCorruptionNotTheEater(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer eater = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes("bread_eater".getBytes()), "bread_eater"));
            eater.setGameMode(GameType.SURVIVAL);
            eater.setData(ModAttachments.PLAYER_CORRUPTION, 0);
            eater.getFoodData().setFoodLevel(10);
            eater.getFoodData().setSaturation(2);
            ItemStack loaves = new ItemStack(ModItems.CORRUPTED_BREAD.get(), 3);
            assertNotNull(loaves.getFoodProperties(eater), "it is food");
            assertTrue(eater.canEat(loaves.getFoodProperties(eater).canAlwaysEat()), "eaten as food is");

            ItemStack left = loaves.finishUsingItem(level, eater);
            assertEquals(1, PlayerCorruption.value(eater), "one point more corrupted");
            assertEquals(10, eater.getFoodData().getFoodLevel(), "and no hunger restored");
            assertEquals(2, eater.getFoodData().getSaturationLevel(), 1e-6, "nor saturation");
            assertEquals(2, left.getCount(), "a loaf eaten");

            eater.getFoodData().setFoodLevel(20);
            assertTrue(eater.canEat(loaves.getFoodProperties(eater).canAlwaysEat()), "it can be eaten however full one is");
            left.finishUsingItem(level, eater);
            assertEquals(2, PlayerCorruption.value(eater), "and every loaf counts");
            eater.setData(ModAttachments.PLAYER_CORRUPTION, 0);
        });
    }

    @Test void itIsBakedInTheBrassCauldron(MinecraftServer server) throws Exception {
        TestWorld.onServer(server, level -> {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "brass_cooking/corrupted_bread")).orElse(null);
            assertNotNull(recipe, "the recipe loads");
            assertTrue(recipe.value().getResultItem(level.registryAccess()).is(ModItems.CORRUPTED_BREAD.get()), "and makes the bread");
        });
        for (String path : new String[]{"/assets/corruption/textures/item/corrupted_bread.png", "/assets/corruption/models/item/corrupted_bread.json"})
            try (InputStream in = CorruptedBreadServerTest.class.getResourceAsStream(path)) { assertNotNull(in, path); }
    }
}
