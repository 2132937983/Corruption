package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.BossDeath;
import com.miophas.corruption.common.entity.DemonBoss;
import com.miophas.corruption.common.entity.SpellBeamEntity;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
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

    /**
     * A beam firing near the player: the view held at least this shaken by it, the more the nearer, none past its
     * length (or sixteen blocks), every tick it fires; so it rumbles steadily while the beam fires, and settles as
     * soon as it stops.
     */
    public static void rumble(SpellBeamEntity beam) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (beam.shake() <= 0 || player == null) return;
        double reach = Math.max(16, beam.length()), distance = player.position().distanceTo(beam.position());
        if (distance < reach) trauma = Math.max(trauma, Mth.clamp((float) (beam.shake() * (1 - distance / reach)), 0, 1));
    }

    /**
     * The bosses dying about the player: the ground trembles under the viewer all through a death, by its rumble, the
     * more the nearer, none past three times the length of its rays; and each moment of a death that shakes shakes the
     * view as it comes, as an effect appearing does, by its size times the death's scale.
     */
    static void dying(LocalPlayer player, Iterable<Entity> entities) {
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity boss) || !(entity instanceof DemonBoss demon) || boss.deathTime <= 0) continue;
            BossDeath death = demon.death();
            if (boss.deathTime >= death.lasts()) continue;
            double reach = Math.max(16, death.rays().length() * death.scale() * 3), distance = player.position().distanceTo(boss.position());
            if (boss.deathTime < death.ticks() && distance < reach) trauma = Math.max(trauma, (float) (death.rumble() * (1 - distance / reach)));
            for (BossDeath.Beat beat : death.beats()) {
                if (beat.tick() != boss.deathTime || beat.visual().shake() <= 0) continue;
                Vec3 at = beat.at() == BossDeath.Anchor.HEART ? demon.heart(1) : boss.position();
                double near = Math.max(10, beat.visual().radius() * death.scale() * 4), off = player.position().distanceTo(at);
                if (off < near) add((float) (beat.visual().shake() * (1 - off / near)));
            }
        }
    }

    @SubscribeEvent public static void settle(ClientTickEvent.Post event) {
        trauma = Math.max(0, trauma - SETTLING);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.level != null) dying(minecraft.player, minecraft.level.entitiesForRendering());
    }

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
