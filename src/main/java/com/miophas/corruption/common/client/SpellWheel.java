package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.core.arcane.Grimoire;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * Pressing the wheel key opens the {@link SpellWheelScreen}, if the wand in hand has been taught anything at
 * all, with a sector for each of its slots; the screen closes itself when the key is let go, and tells the
 * server what was chosen.
 *
 * <p>Once a screen is open the game stops tracking key mappings (it lets them all go, and an in-game key reads
 * as up while any screen is open), so whether the key is still held is read off the keyboard or mouse itself.
 * After the wheel closes, the key must be let go before it opens again, so that one held through Escape does
 * not open it again at once.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class SpellWheel {
    public static final KeyMapping OPEN = new KeyMapping("key.corruption.spell_wheel", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.corruption");

    /** Whether the key has been let go since the wheel last opened. */
    private static boolean letGo = true;

    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) { event.register(OPEN); }

    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!held()) letGo = true;
        while (OPEN.consumeClick()) {
            if (!letGo || minecraft.player == null || minecraft.screen != null) continue;
            ItemStack held = minecraft.player.getMainHandItem();
            Grimoire grimoire = WandSpells.of(held);
            if (grimoire.isEmpty()) continue;
            letGo = false;
            minecraft.setScreen(new SpellWheelScreen(grimoire, held.getItem() instanceof ArcaneCastingItem casting ? casting.capacity() : Grimoire.CAPACITY));
        }
    }

    /**
     * Whether the wheel key is held down now, read off the keyboard or mouse. A key known only by its scancode
     * cannot be read this way and counts as held; the screen sees it let go instead.
     */
    public static boolean held() {
        InputConstants.Key key = OPEN.getKey();
        long window = Minecraft.getInstance().getWindow().getWindow();
        return switch (key.getType()) {
            case MOUSE -> GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
            case KEYSYM -> key.getValue() != InputConstants.UNKNOWN.getValue() && InputConstants.isKeyDown(window, key.getValue());
            case SCANCODE -> true;
        };
    }

    private SpellWheel() {}
}
