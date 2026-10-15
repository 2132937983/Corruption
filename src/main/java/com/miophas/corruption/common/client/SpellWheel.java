package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.core.arcane.Grimoire;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * Holding the wheel key opens the {@link SpellWheelScreen}, if the wand in hand has been taught anything at
 * all; letting it go closes it and tells the server what was chosen.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class SpellWheel {
    public static final KeyMapping OPEN = new KeyMapping("key.corruption.spell_wheel", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.corruption");

    private static boolean showing;

    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) { event.register(OPEN); }

    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) { showing = false; return; }
        if (OPEN.isDown()) {
            if (showing) return;
            Grimoire grimoire = WandSpells.of(minecraft.player.getMainHandItem());
            if (grimoire.isEmpty()) return;
            showing = true;
            minecraft.setScreen(new SpellWheelScreen(grimoire));
        } else if (showing) {
            showing = false;
            if (minecraft.screen instanceof SpellWheelScreen wheel) {
                wheel.chosen();
                minecraft.setScreen(null);
            }
        }
    }

    private SpellWheel() {}
}
