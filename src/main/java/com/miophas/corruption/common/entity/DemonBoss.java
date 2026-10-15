package com.miophas.corruption.common.entity;

import java.util.Optional;
import java.util.UUID;

/**
 * A demon with a boss bar drawn in the mod's own style (ColossusBossBar): which bar on the client is its own,
 * and which emblem the bar's medallion shows.
 */
public interface DemonBoss {
    /** Which boss bar on the client is this creature's; empty until the server has said. */
    Optional<UUID> bossBar();

    /** Its emblem on textures/gui/colossus_boss_bar.png: 0 Baal's, 1 Agares's, 2 Andromalius's. */
    int emblem();
}
