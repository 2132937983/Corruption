package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.RemnantDefinition;

/** Base species definition; captured source attributes still override health and damage. */
public final class RemnantRules {
    public static final RemnantDefinition DEFINITION = new RemnantDefinition(
            20, 3, .25, 24, .6F, 1.8F, 1.1, .8, .9, 40, 16);
    private RemnantRules() {}
}
