package com.miophas.corruption.core.altar;

/** What an Altar Offering is doing: how its altar stands, and whether it holds creatures on it. */
public enum AltarState {
    /** Its pillars do not all stand: nothing shows and nothing happens. */
    DORMANT,
    /** Its altar stands and its circle shows, but it holds nothing: so after a rite, until it is used again. */
    FORMED,
    /** Its altar stands and it binds a creature on it, ready for a rite that asks for one. */
    BINDING;

    /** Whether the magic circle shows beneath it. */
    public boolean showsCircle() { return this != DORMANT; }
    /** Whether creatures on it are held there. */
    public boolean binds() { return this == BINDING; }
    /** Whether a rite may be performed at it. */
    public boolean performs() { return this != DORMANT; }
}
