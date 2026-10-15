package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * The view shaking as a mighty spell lands near the player: the nearer, the harder, dying away over a second or
 * so. It is smooth, not jittery (sines of different speeds, not noise), and it follows the game's own "Screen
 * Effect Scale" accessibility setting, so a player who turns screen effects down feels it less, or not at all.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class CameraShake {
    /** How much shaking is stored up, 0 to 1; the view shakes by its square, so a little is gentle. */
    private static float trauma;
    /** How much it dies away each tick. */
    private static final float SETTLING = .045F;
    /** How far the view swings at the most, in degrees. */
    private static final float YAW = 4, PITCH = 3.5F, ROLL = 5;

    /** An effect appearing: shakes the view by its strength, less the further off it is, none past four times its size. */
    public static void appears(SpellVisualEntity shown) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (shown.shake() <= 0 || player == null) return;
        double reach = Math.max(10, shown.radius() * 4), distance = player.position().distanceTo(shown.position());
        if (distance < reach) add((float) (shown.shake() * (1 - distance / reach)));
    }

    public static void add(float amount) { trauma = Mth.clamp(trauma + amount, 0, 1); }

    @SubscribeEvent public static void settle(ClientTickEvent.Post event) { trauma = Math.max(0, trauma - SETTLING); }

    @SubscribeEvent public static void swing(ViewportEvent.ComputeCameraAngles event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (trauma <= 0 || minecraft.level == null) return;
        float shake = trauma * trauma * minecraft.options.screenEffectScale().get().floatValue();
        if (shake <= 0) return;
        float time = (float) (minecraft.level.getGameTime() + event.getPartialTick());
        event.setYaw(event.getYaw() + YAW * shake * wave(time, 1.7F, 0));
        event.setPitch(event.getPitch() + PITCH * shake * wave(time, 2.3F, 1.3F));
        event.setRoll(event.getRoll() + ROLL * shake * wave(time, 1.1F, 2.9F));
    }

    /** A smooth wobble between -1 and 1: two sines of unrelated speeds, so it never settles into a rhythm. */
    private static float wave(float time, float speed, float phase) {
        return .6F * Mth.sin(time * speed + phase) + .4F * Mth.sin(time * speed * 2.71F + phase * 1.9F);
    }

    private CameraShake() {}
}
