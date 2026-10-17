package com.miophas.corruption.api.demon.skill;

import net.minecraft.world.phys.Vec3;

/**
 * A creature that says where what it spits leaves it ({@link BossSkillApi#spit}): its mouth, as it is now. One that does
 * not say spits from a little before its eyes.
 */
public interface SpitCaster {
    /** Where the index-th shot of a spit leaves it, in the world. */
    Vec3 spitFrom(int index);
}
