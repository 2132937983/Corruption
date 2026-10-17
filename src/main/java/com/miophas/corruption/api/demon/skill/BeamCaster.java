package com.miophas.corruption.api.demon.skill;

import net.minecraft.world.phys.Vec3;

/** A boss whose beam (BossSkillApi.beam) leaves it from somewhere of its own (a mirror's face, a staff's head) rather than from its eyes. */
public interface BeamCaster {
    /** Where its beam leaves it from, in the world, this tick. */
    Vec3 beamFrom();

    /**
     * Where the index-th of so many beams of a volley (BossSkillApi.beamVolley) leaves it from, in the world, this tick.
     * By default from the magic circle the volley's rules set round {@link #beamFrom()} ({@code circled}); a boss with
     * places of its own for them (its hands, the eyes on its wings) says so here.
     */
    default Vec3 beamFrom(int index, int count, Vec3 circled) { return circled; }
}
