package com.miophas.corruption.api.demon.skill;

import net.minecraft.world.phys.Vec3;

/** Movement needed by burrowing and ramming skills; implemented by the host mob. */
public interface BurrowingCaster {
    Vec3 headCentre();
    Vec3 forward();
    void face(Vec3 point);
    void steer(Vec3 point, double speed, double turn);
}
