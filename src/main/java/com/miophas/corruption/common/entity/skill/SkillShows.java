package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.common.arcane.SpellEffects;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.arcane.SpellAction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** The sights and sounds the skills (and the bosses that carry them) share: one of the spell effects (api.arcane.client.SpellVisuals) set down where it is asked for, and a sound. */
public final class SkillShows {
    /**
     * Sets an effect down at this place, for every client near to draw, for as long as it says: on the ground under the
     * place if {@code grounded} (as a spell's effect is), or at the place itself.
     */
    public static void show(ServerLevel level, Vec3 at, String effect, double radius, double height, int ticks, int colour, int accent, double shake,
                     float yaw, boolean grounded) {
        SpellVisualEntity shown = ModEntities.SPELL_VISUAL.get().create(level);
        if (shown == null) return;
        shown.showing(new SpellAction.Visual("corruption:" + effect, radius, height, ticks, colour, accent, shake, ""), yaw);
        double y = grounded ? SpellEffects.groundAt(level, BlockPos.containing(at)).getY() : at.y;
        shown.moveTo(at.x, y, at.z, yaw, 0);
        level.addFreshEntity(shown);
    }

    /** Where the ground is under (or a little over) this place: the foot of a column of air on something solid. */
    public static Vec3 ground(ServerLevel level, Vec3 at) { return new Vec3(at.x, SpellEffects.groundAt(level, BlockPos.containing(at)).getY(), at.z); }

    /** A sound from this creature, heard by all near it. */
    public static void sound(Entity from, SoundEvent sound, float volume, float pitch) {
        from.level().playSound(null, from.getX(), from.getY(), from.getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    /** A sound at this place, heard by all near it. */
    public static void sound(ServerLevel level, Vec3 at, SoundEvent sound, float volume, float pitch) {
        level.playSound(null, at.x, at.y, at.z, sound, SoundSource.HOSTILE, volume, pitch);
    }

    private SkillShows() {}
}
