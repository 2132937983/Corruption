package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Stuns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * This player, stunned (ModEffects.STUNNED): their keys go dead (they neither walk, jump nor crouch, strike nor use),
 * and their sight is dazzled, a pale glare throbbing at its edges, until it wears off.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class StunClient {
    private static boolean stunned() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && Stuns.stunned(player);
    }

    @SubscribeEvent static void still(MovementInputUpdateEvent event) {
        if (!Stuns.stunned(event.getEntity())) return;
        Input input = event.getInput();
        input.forwardImpulse = 0;
        input.leftImpulse = 0;
        input.up = input.down = input.left = input.right = false;
        input.jumping = false;
        input.shiftKeyDown = false;
        event.getEntity().setSprinting(false);
    }

    @SubscribeEvent static void keys(InputEvent.InteractionKeyMappingTriggered event) {
        if (!stunned()) return;
        event.setSwingHand(false);
        event.setCanceled(true);
    }

    @SubscribeEvent static void glare(RenderGuiEvent.Post event) {
        if (!stunned()) return;
        GuiGraphics graphics = event.getGuiGraphics();
        LocalPlayer player = Minecraft.getInstance().player;
        float time = player.tickCount + event.getPartialTick().getGameTimeDeltaPartialTick(true);
        int alpha = Math.round(255 * (.18F + .1F * Mth.sin(time * .5F))), width = graphics.guiWidth(), height = graphics.guiHeight();
        int glare = alpha << 24 | 0xFFF2C8, clear = 0xFFF2C8;
        graphics.fillGradient(0, 0, width, height / 4, glare, clear);
        graphics.fillGradient(0, height - height / 4, width, height, clear, glare);
    }

    private StunClient() {}
}
