package com.miophas.corruption.api.corruption;

/**
 * What Agares is doing besides walking. Each has a stretch of ticks, counted from its start, during which
 * it does its harm. The order is the network id, so add new ones at the end.
 */
public enum AgaresAttack {
    NONE(0, 0, 0),
    /** Throws its head back and curses everything that hears it. */
    SCREECH(120, 40, 40),
    /** Rears, plunges its jaws to the ground at its own feet and rakes them away from it, through everything in front. */
    BITE(130, 62, 86),
    /** Raises one foot and brings it down where it stands, for whatever is under it. */
    STOMP_LEFT(90, 52, 52), STOMP_RIGHT(90, 52, 52),
    /** Whips its tail across everything behind it, ending on the side it is named for. */
    TAIL_LEFT(110, 40, 70), TAIL_RIGHT(110, 40, 70),
    /** Opens its wings and beats them once, hurling away what is in front of it, on the ground or in the air. */
    GUST(140, 70, 70);

    private final int ticks, strikeFrom, strikeTo;

    AgaresAttack(int ticks, int strikeFrom, int strikeTo) { this.ticks = ticks; this.strikeFrom = strikeFrom; this.strikeTo = strikeTo; }

    /** How long the whole move takes. */
    public int ticks() { return ticks; }
    /** The first tick on which it does harm, and the last. */
    public int strikeFrom() { return strikeFrom; }
    public int strikeTo() { return strikeTo; }
    public boolean strikes(int tick) { return this != NONE && tick >= strikeFrom && tick <= strikeTo; }
    public boolean stomp() { return this == STOMP_LEFT || this == STOMP_RIGHT; }
    public boolean tail() { return this == TAIL_LEFT || this == TAIL_RIGHT; }
    /** True for the moves made with, or towards, the left. */
    public boolean left() { return this == STOMP_LEFT || this == TAIL_LEFT; }

    public static AgaresAttack byId(int id) { return id >= 0 && id < values().length ? values()[id] : NONE; }
}
