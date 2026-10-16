package com.miophas.corruption.api.demon.skill;

import net.minecraft.world.phys.Vec3;

/** A boss whose beam (BossSkillApi.beam) leaves it from somewhere of its own (a mirror's face, a staff's head) rather than from its eyes. */
public interface BeamCaster {
    /** Where its beam leaves it from, in the world, this tick. */
    Vec3 beamFrom();
}
