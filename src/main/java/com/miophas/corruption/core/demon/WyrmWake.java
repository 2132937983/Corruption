package com.miophas.corruption.core.demon;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The way a dragon's head has gone, and which stretches of it it swam through the earth (burrowing) rather than breaking its
 * way through, as rules: plain Java. Its body follows its head the way it went ({@link WyrmChain}), so a link of it swims
 * where the head swam and breaks its way where the head broke its way, however long the body takes to come by: a body that
 * stops half out of the ground does not carve out the rest of the tunnel. The way is measured as the head goes, the
 * distance it has come in all; it is kept only as far back as the body reaches.
 */
public final class WyrmWake {
    private final double reach;
    private double travelled;
    private boolean swimming;
    /** The stretches swum, {from, to}, as distances come, oldest first. */
    private final Deque<double[]> swum = new ArrayDeque<>();

    /**
     * How much further back along the way than along itself a body that has curled may lie, as a share (each joint is drawn
     * straight towards the one before it, and so cuts the corners the head turned); and a little slack either way, in blocks.
     */
    public static final double CURL = 1.25, SLACK = 1;

    /** A wake kept this far back behind the head. */
    public WyrmWake(double reach) { this.reach = reach; }

    /** How far the head has come in all. */
    public double travelled() { return travelled; }

    /** The head has come this much further, swimming through the earth or not. */
    public void go(double distance, boolean swims) {
        double from = travelled;
        travelled += Math.max(0, distance);
        if (swims) {
            if (swimming && !swum.isEmpty()) swum.peekLast()[1] = travelled;
            else swum.addLast(new double[]{from, travelled});
        }
        swimming = swims;
        while (!swum.isEmpty() && swum.peekFirst()[1] < travelled - reach) swum.removeFirst();
    }

    /** Whether a link of a body behind the head (counting from 0 at the head), its joints {@code spacing} apart, lies on a stretch swum, and so swims too. */
    public boolean swims(int link, double spacing) { return swum(link * spacing - SLACK, (link + 1) * spacing * CURL + SLACK); }

    /** Whether any of the way from {@code nearest} to {@code farthest} behind the head was swum. */
    public boolean swum(double nearest, double farthest) {
        double from = travelled - farthest, to = travelled - nearest;
        for (double[] stretch : swum) if (stretch[1] >= from && stretch[0] <= to) return true;
        return false;
    }

    /** As one array: how far it has come, whether it swims now (1 or 0), then each stretch's ends in turn. */
    public double[] save() {
        double[] all = new double[2 + swum.size() * 2];
        all[0] = travelled;
        all[1] = swimming ? 1 : 0;
        int at = 2;
        for (double[] stretch : swum) {
            all[at++] = stretch[0];
            all[at++] = stretch[1];
        }
        return all;
    }

    /** Takes back what {@link #save} gave; anything else leaves an empty wake. */
    public void load(double[] all) {
        swum.clear();
        travelled = 0;
        swimming = false;
        if (all.length < 2 || all.length % 2 != 0) return;
        travelled = all[0];
        swimming = all[1] != 0;
        for (int at = 2; at < all.length; at += 2) swum.addLast(new double[]{all[at], all[at + 1]});
    }
}
