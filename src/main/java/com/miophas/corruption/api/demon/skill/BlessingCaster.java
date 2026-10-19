package com.miophas.corruption.api.demon.skill;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Optional control of the allies and visual origin used by a blessing. */
public interface BlessingCaster {
    Iterable<? extends LivingEntity> blessingTargets(double radius);
    Vec3 blessingOrigin();
}
