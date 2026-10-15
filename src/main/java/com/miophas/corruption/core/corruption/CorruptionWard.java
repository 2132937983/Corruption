package com.miophas.corruption.core.corruption;

import java.util.function.DoubleSupplier;

/**
 * What a worn thing does to how a player's corruption grows, as a robe does. This is plain Java; the equipment
 * itself is api.corruption.CorruptionWardItem.
 *
 * <p>Its {@code share} runs from -1 to 1. Above none it holds that share of every gain back: a share of 1 stops all
 * growth. Below none it makes growth worse: every gain is doubled that share of the time. Everything a player wears
 * counts together: the shares are added up, but never past holding back all of a gain, nor past doubling all of it.
 * It bears on the corruption's growth alone; nothing worn lowers corruption that is already there.
 */
public record CorruptionWard(double share) {
    public static final CorruptionWard NONE = new CorruptionWard(0);

    public CorruptionWard { share = Math.max(-1, Math.min(1, share)); }

    /** How everything worn bears on growth together: its shares added up, and held between -1 and 1. */
    public static double total(Iterable<CorruptionWard> worn) {
        double sum = 0;
        for (CorruptionWard ward : worn) sum += ward.share();
        return Math.max(-1, Math.min(1, sum));
    }

    /**
     * How much of a gain gets through attire that bears on it this much. Each point of the gain is held back if its
     * roll falls under the share. Below none, each point is instead doubled if its roll falls under the share's size.
     * {@code roll} gives a number from 0 up to, but not including, 1 for each point.
     */
    public static int gain(int gain, double total, DoubleSupplier roll) {
        if (gain <= 0) return 0;
        double share = Math.max(-1, Math.min(1, total));
        if (share == 0) return gain;
        int through = 0;
        for (int point = 0; point < gain; point++) {
            double chance = roll.getAsDouble();
            if (share > 0) { if (chance >= share) through++; }
            else through += chance < -share ? 2 : 1;
        }
        return through;
    }
}
