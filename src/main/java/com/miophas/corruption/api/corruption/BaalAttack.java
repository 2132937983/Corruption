package com.miophas.corruption.api.corruption;

/** What Baal is doing besides walking. The order is the network id, so add new ones at the end. */
public enum BaalAttack {
    NONE(0, 0),
    /** Throws its head back and curses everything that hears it. */
    ROAR(100, 30),
    /** Bows and drives one hand into the ground in front of it. */
    SLAM_LEFT(90, 46), SLAM_RIGHT(90, 46),
    /** Raises one hoof and brings it down where it stands, for whatever is under it. */
    STOMP_LEFT(70, 40), STOMP_RIGHT(70, 40);

    private final int ticks, impactTick;

    BaalAttack(int ticks, int impactTick) { this.ticks = ticks; this.impactTick = impactTick; }

    /** How long the whole move takes. */
    public int ticks() { return ticks; }
    /** The tick, counted from the start of the move, on which it lands. */
    public int impactTick() { return impactTick; }
    public boolean slam() { return this == SLAM_LEFT || this == SLAM_RIGHT; }
    public boolean stomp() { return this == STOMP_LEFT || this == STOMP_RIGHT; }
    /** True for the moves made with the left hand or hoof. */
    public boolean left() { return this == SLAM_LEFT || this == STOMP_LEFT; }

    public static BaalAttack byId(int id) { return id >= 0 && id < values().length ? values()[id] : NONE; }
}
