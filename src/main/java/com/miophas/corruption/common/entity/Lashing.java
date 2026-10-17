package com.miophas.corruption.common.entity;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

/**
 * An elite that lashes out with a tentacle (a lurker's, a drifter's tendrils): what it reaches for or holds, as every client
 * sees it, and where the lash leaves it; its renderer draws the lash between the two.
 */
public interface Lashing {
    /** What its lash reaches for or holds, as every client sees it; null if nothing. */
    @Nullable Entity reaching();

    /** Where its lash leaves it, in its own frame from its feet ({right, up, ahead}, in blocks). */
    double[] lashFrom();

    /** How far out its lash is, 0 to 1, this many ticks into its lash (with the partial tick). */
    float lashOut(float partial);
}
