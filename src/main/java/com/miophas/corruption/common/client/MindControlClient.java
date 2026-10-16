package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.DemonBoss;
import com.miophas.corruption.common.network.MindControlPayload;
import com.miophas.corruption.core.demon.skill.MindControlRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * This player's mind, held by a boss (MindControlPayload, the mind control skill). While it is held, every frame their
 * view is drawn round towards the boss's heart by the hold's grip, however they turn, so that they cannot look away;
 * held harder, their feet are taken too: they walk on towards the boss, weaving, and can neither jump nor crouch, nor
 * stop. The edges of their sight darken to violet, throbbing, while it lasts. The hold ends when its time is up, when
 * the boss lets go or is gone, or when the player leaves the world.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class MindControlClient {
    private static int holder = -1, ticks;
    private static boolean moves;
    private static float grip;

    /** Fits this as what the client does with a hold, as the game starts on a client. */
    public static void install() { MindControlPayload.received = MindControlClient::received; }

    static void received(MindControlPayload payload) {
        holder = payload.ticks() > 0 ? payload.holder() : -1;
        ticks = Math.max(0, payload.ticks());
        moves = payload.moves();
        grip = payload.grip();
    }

    /** Whether this player's mind is held now. */
    public static boolean held() { return holder >= 0 && ticks > 0; }

    /** Whether their feet are taken too. */
    public static boolean walked() { return held() && moves; }

    private static Entity holder() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null || !held() ? null : minecraft.level.getEntity(holder);
    }

    private static void letGo() { holder = -1; ticks = 0; }

    @SubscribeEvent static void tick(ClientTickEvent.Post event) {
        if (!held()) return;
        Entity boss = holder();
        if (--ticks <= 0 || boss == null || !boss.isAlive()) letGo();
    }

    /** Every frame, their view is drawn round to the boss's heart (its middle, for a creature with none). */
    @SubscribeEvent static void frame(RenderFrameEvent.Pre event) {
        Entity boss = holder();
        LocalPlayer player = Minecraft.getInstance().player;
        if (boss == null || player == null) return;
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        Vec3 eye = player.getEyePosition(partial);
        Vec3 heart = boss instanceof DemonBoss demon ? demon.heart(partial) : boss.getBoundingBox().getCenter();
        Vec3 to = heart.subtract(eye);
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90, pitch = (float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG);
        float turned = MindControlRules.pull(player.getYRot(), yaw, grip), tilted = Mth.clamp(MindControlRules.pull(player.getXRot(), pitch, grip), -90, 90);
        player.setYRot(turned);
        player.setXRot(tilted);
        player.yRotO = turned;
        player.xRotO = tilted;
        player.setYHeadRot(turned);
    }

    /** Held harder, their feet are not their own: on towards the boss, weaving, never jumping, crouching or standing still. */
    @SubscribeEvent static void walk(MovementInputUpdateEvent event) {
        if (!walked() || holder() == null) return;
        Input input = event.getInput();
        float weave = MindControlRules.weave(event.getEntity().tickCount);
        input.forwardImpulse = 1;
        input.leftImpulse = weave;
        input.up = true;
        input.down = false;
        input.left = weave > .3F;
        input.right = weave < -.3F;
        input.jumping = false;
        input.shiftKeyDown = false;
        event.getEntity().setSprinting(false);
    }

    /** The edges of their sight darken to violet, throbbing, while it is held; deeper while their feet are taken. */
    @SubscribeEvent static void overlay(RenderGuiEvent.Post event) {
        if (holder() == null) return;
        GuiGraphics graphics = event.getGuiGraphics();
        float time = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount + event.getPartialTick().getGameTimeDeltaPartialTick(true);
        float throb = .5F + .5F * Mth.sin(time * .35F), depth = (moves ? .55F : .38F) + .12F * throb;
        int width = graphics.guiWidth(), height = graphics.guiHeight(), edgeX = width / 5, edgeY = height / 5;
        int dark = Math.round(255 * depth) << 24 | 0x2A0838, clear = 0x2A0838;
        graphics.fillGradient(0, 0, width, edgeY, dark, clear);
        graphics.fillGradient(0, height - edgeY, width, height, clear, dark);
        sideways(graphics, 0, edgeX, height, dark, clear);
        sideways(graphics, width - edgeX, width, height, clear, dark);
    }

    /** A gradient across, from left to right, drawn in thin strips (the game's own gradients only run down). */
    private static void sideways(GuiGraphics graphics, int from, int to, int height, int left, int right) {
        int strips = Math.max(1, (to - from) / 2);
        for (int strip = 0; strip < strips; strip++) {
            float share = strip / (float) strips;
            int alpha = Math.round(Mth.lerp(share, left >>> 24, right >>> 24));
            int x0 = from + (to - from) * strip / strips, x1 = from + (to - from) * (strip + 1) / strips;
            graphics.fill(x0, 0, x1, height, alpha << 24 | 0x2A0838);
        }
    }

    @SubscribeEvent static void leaving(ClientPlayerNetworkEvent.LoggingOut event) { letGo(); }

    private MindControlClient() {}
}
