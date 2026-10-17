package com.miophas.corruption.core.demon.skill;

import java.util.ArrayList;
import java.util.List;

/**
 * The ground under a quarry heaved up, broken open or speared, as rules. As it begins, the ground under where the quarry
 * stands is marked (every column within {@code radius} of the middle of that block) and trembles through the wind-up,
 * so that a quarry that keeps moving may step off it. At its end it takes one of three forms ({@link #form}):
 * <ul>
 *   <li>{@link Form#RISE}: the ground there rises {@code rise} blocks, a pillar of the dormant corruption heaved up on top
 *       of it, and whatever stands there is flung up {@code launch} blocks a tick and hurt for {@code damage};</li>
 *   <li>{@link Form#SINK}: the ground there breaks open {@code sink} blocks deep, its blocks thrown up and out round the
 *       pit as a dive throws them, and the floor of the pit turns to the dormant corruption; whatever stood there falls in,
 *       hurt for {@code damage};</li>
 *   <li>{@link Form#SPIKES}: great spikes burst up there, hurting whatever stands there for {@code spikeDamage} and flinging
 *       it {@code spikeLaunch} blocks a tick up, the ground as it was.</li>
 * </ul>
 * Raising and breaking the ground needs the world's leave (the mobGriefing rule); without it, it is always SPIKES.
 */
public record UpheavalRules(SkillTiming timing, double minRange, double maxRange, double radius, int rise, int sink, double damage, double launch,
                            double spikeDamage, double spikeLaunch) {
    /** What the ground does. */
    public enum Form { RISE, SINK, SPIKES }

    public UpheavalRules {
        maxRange = Math.max(minRange, maxRange);
        radius = Math.max(.5, radius);
        rise = Math.max(1, rise);
        sink = Math.max(1, sink);
        damage = Math.max(0, damage);
        spikeDamage = Math.max(0, spikeDamage);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /**
     * Which form it takes, by a roll from 0 to 1: with the world's leave, rising four times in ten, sinking three and
     * spikes three; without it, spikes.
     */
    public Form form(boolean griefing, double roll) {
        if (!griefing) return Form.SPIKES;
        return roll < .4 ? Form.RISE : roll < .7 ? Form.SINK : Form.SPIKES;
    }

    /** Whether something this far (along the ground) from the middle of the marked block is caught. */
    public boolean catches(double dx, double dz) { return dx * dx + dz * dz <= (radius + .5) * (radius + .5); }

    /** The columns it takes, as offsets {east, south} in blocks from the marked block: every one within its radius. */
    public List<int[]> columns() {
        List<int[]> columns = new ArrayList<>();
        int reach = (int) Math.ceil(radius);
        for (int east = -reach; east <= reach; east++)
            for (int south = -reach; south <= reach; south++)
                if (east * east + south * south <= radius * radius) columns.add(new int[]{east, south});
        return columns;
    }
}
