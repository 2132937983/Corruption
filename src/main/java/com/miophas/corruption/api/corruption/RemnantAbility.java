package com.miophas.corruption.api.corruption;

public enum RemnantAbility {
    SUMMON_VEX, EVOKER_FANGS, SONIC_BOOM, WALL_CLIMB, FLIGHT, PHASE_WALLS, DRAGON_COMBAT, SHULKER_COMBAT, AMPHIBIOUS, SWIMMING,
    /** Reserved legacy bit; ordinary remnants now retain ground navigation and surface buoyancy. */
    BASE_SWIMMING;
    public int mask() { return 1 << ordinal(); }
    public boolean in(int flags) { return (flags & mask()) != 0; }
}
