package com.miophas.corruption.api.demon;

import com.miophas.corruption.core.arcane.SpellAction;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * How a boss dies, as a spectacle. From the moment it falls it takes {@code ticks} to die, all the while pouring out
 * {@link Rays rays} of light from its heart, and at the end of them its body is gone in a last burst of its light;
 * what its death set going plays out for {@code linger} ticks more before the boss itself is taken away. Its moments
 * ({@link Beat}) are effects of the kinds a spell's "visual" names (api.arcane.client.SpellVisuals), set down at its
 * feet or at its heart, each at a tick of its own, sounding as it comes and shaking the views of those near it; and
 * all through its dying the ground trembles under the viewer, by {@code rumble} (0 to 1) at the heart of it.
 *
 * <p>Every client draws all of it from the boss's own death time, about its body as the body is drawn, so it is seen
 * from as far as the boss is, however far that is (common.client.visual.DeathVisual); the server only sounds it.
 *
 * <p>Its sizes are in blocks times {@code scale}: each effect is drawn at the size it is given and then made
 * {@code scale} times larger, so that what an effect draws at a size of its own (a spike's height, a crown's drop)
 * grows with it, and a colossus's death keeps the proportions a serpent's has.
 */
public record BossDeath(int ticks, int linger, float scale, Rays rays, float rumble, List<Beat> beats) {
    public BossDeath {
        ticks = Math.max(1, ticks);
        linger = Math.max(0, linger);
        scale = Math.max(.01F, scale);
        rumble = Math.max(0, Math.min(1, rumble));
        beats = List.copyOf(beats);
    }

    /** Where an effect is set down: on the ground at its feet, or at its heart, where its light pours out. */
    public enum Anchor { FEET, HEART }

    /**
     * Light bursting out of its heart as it dies: one ray after another through the first three quarters of its death,
     * {@code count} at the most, each up to {@code length} long (times the scale), {@code accent} at the heart and
     * {@code colour} out along it (0xRRGGBB); gone in the last burst.
     */
    public record Rays(int count, float length, int colour, int accent) {
        public Rays { count = Math.max(0, count); length = Math.max(0, length); }
    }

    /**
     * One moment of its death: at {@code tick} of it, {@code visual} shows at its feet or its heart for as long as it
     * says, its radius and height times the scale; its sound sounds there, this loud (a sound is heard sixteen blocks
     * for every one of loudness) and at this pitch; and its shake shakes the views of those near it.
     */
    public record Beat(int tick, Anchor at, SpellAction.Visual visual, float volume, float pitch) {}

    /** Ticks from its fall to its going, the lingering included. */
    public int lasts() { return ticks + linger; }

    /** How far round its feet any of it may be drawn, in blocks: the longest of its rays, and well past every effect. */
    public float reach() {
        float reach = rays.length();
        for (Beat beat : beats) reach = Math.max(reach, (float) Math.max(beat.visual().radius() * 2.6 + 5, Math.max(beat.visual().height(), beat.visual().radius()) * 3.4 + 8));
        return reach * scale;
    }

    /** Sounds the moments that come at this tick of its death, where each is set down. */
    public void sound(ServerLevel level, LivingEntity boss, Vec3 heart, int tick) {
        for (Beat beat : beats) {
            if (beat.tick() != tick || beat.visual().sound().isEmpty()) continue;
            ResourceLocation id = ResourceLocation.tryParse(beat.visual().sound());
            if (id == null) continue;
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getOptional(id).orElseGet(() -> SoundEvent.createVariableRangeEvent(id));
            Vec3 at = beat.at() == Anchor.HEART ? heart : boss.position();
            level.playSound(null, at.x, at.y, at.z, sound, SoundSource.HOSTILE, beat.volume(), beat.pitch());
        }
    }

    /** A moment of a death, as briefly as the scripts below write one. */
    public static Beat beat(int tick, Anchor at, String effect, double radius, double height, int lifetime, int colour, int accent,
                            double shake, String sound, float volume, float pitch) {
        return new Beat(tick, at, new SpellAction.Visual(effect.contains(":") ? effect : "corruption:" + effect, radius, height, lifetime, colour, accent, shake, sound), volume, pitch);
    }

    /**
     * The death of a demon that says nothing of its own (an add-on's, say), as big as it is tall: its light pours out
     * for three seconds, and it is gone in a burst, a ring of force racing out over the ground and a pillar of light
     * standing where it fell.
     */
    public static BossDeath ordinary(float height, int colour, int accent) {
        float scale = Math.max(.5F, height / 4);
        return new BossDeath(60, 40, scale, new Rays(12, 8, colour, accent), 0, List.of(
                beat(56, Anchor.HEART, "nova", 10, 0, 24, colour, 0xFFFFFF, .5, "minecraft:entity.generic.explode", 4 * scale, .8F),
                beat(56, Anchor.FEET, "shockwave", 10, 2.5, 24, colour, accent, 0, "", 0, 1),
                beat(57, Anchor.FEET, "light_pillar", 1.4, 32, 40, colour, 0xFFFFFF, 0, "minecraft:block.beacon.deactivate", 4 * scale, .6F)));
    }
}
