package com.miophas.corruption.api.infection;

import com.miophas.corruption.core.infection.InfectionEngine;

/** The bridge through which common obtains the core infection implementation. */
public final class InfectionApi {
    public static InfectionScheduler createScheduler(InfectionWorld world) {
        return createScheduler(world, InfectionSettings.DEFAULT);
    }

    public static InfectionScheduler createScheduler(InfectionWorld world, InfectionSettings settings) {
        return new InfectionEngine(world, settings);
    }

    private InfectionApi() {}
}
