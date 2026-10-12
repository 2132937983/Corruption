package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantAnimationRules;
import net.minecraft.world.entity.LivingEntity;

/**
 * Client-side hook that replays a source mob's own attack animation on the remnant's appearance proxy.
 * The proxy never ticks on its own, so a driver starts the animation and, if the source needs its
 * per-tick counters or animation states advanced, keeps it running until {@link #duration()} elapses.
 */
@FunctionalInterface
public interface RemnantAnimationDriver {
    /** Called when the remnant attacks. Typically sets a pose, starts an animation state or triggers a mod animation. */
    void start(LivingEntity source, byte event);

    /** Called once per game tick while the animation runs. Return false to stop early. */
    default boolean tick(LivingEntity source, int age) {
        RemnantAnimationApi.tickSource(source);
        return true;
    }

    default int duration() { return RemnantAnimationRules.DEFAULT_DURATION; }
}
