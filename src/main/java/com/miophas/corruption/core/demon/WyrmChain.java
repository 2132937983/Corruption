package com.miophas.corruption.core.demon;

/**
 * A long body that follows its head, as rules: a chain of joints {@code spacing} apart, the first held at the back of the
 * head, each after it drawn along behind the one before, so that the body follows the way the head has gone, curling where
 * it curled. Joint 0 is where the body meets the head; the last is the tip of the tail. Each link of the body lies between a
 * joint and the next ({@link #link}), and narrows from the head to the tail ({@link #taper}).
 *
 * <p>Plain Java: the same chain is kept on the server, where its links are struck at, and on each client, where it is drawn.
 */
public final class WyrmChain {
    private final double[] x, y, z;
    private final double spacing;

    /** A chain of this many joints, all where the head is: a body still coiled in on itself, unwinding as the head moves off. */
    public WyrmChain(int joints, double spacing, double atX, double atY, double atZ) {
        if (joints < 2) throw new IllegalArgumentException("A body has at least two joints");
        this.spacing = spacing;
        x = new double[joints];
        y = new double[joints];
        z = new double[joints];
        reset(atX, atY, atZ);
    }

    public int joints() { return x.length; }
    /** The links of the body: one fewer than its joints. */
    public int links() { return x.length - 1; }
    public double spacing() { return spacing; }
    /** How long the body is, joint to joint. */
    public double length() { return spacing * links(); }

    public double x(int joint) { return x[joint]; }
    public double y(int joint) { return y[joint]; }
    public double z(int joint) { return z[joint]; }

    /** Every joint where the head is. */
    public void reset(double atX, double atY, double atZ) {
        for (int joint = 0; joint < x.length; joint++) set(joint, atX, atY, atZ);
    }

    public void set(int joint, double atX, double atY, double atZ) {
        x[joint] = atX;
        y[joint] = atY;
        z[joint] = atZ;
    }

    /**
     * The head has moved, its back now here: the first joint goes with it, and every joint after it is drawn towards the one
     * before until it lies {@code spacing} behind it, along the way it lay. A joint that already lies within reach of the
     * one before it is left where it is, so that a body coiled in on itself unwinds a link at a time.
     */
    public void follow(double headX, double headY, double headZ) {
        set(0, headX, headY, headZ);
        for (int joint = 1; joint < x.length; joint++) {
            double dx = x[joint] - x[joint - 1], dy = y[joint] - y[joint - 1], dz = z[joint] - z[joint - 1];
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance <= spacing) continue;
            double keep = spacing / distance;
            set(joint, x[joint - 1] + dx * keep, y[joint - 1] + dy * keep, z[joint - 1] + dz * keep);
        }
    }

    /** The middle of a link, {x, y, z}: halfway from its joint to the next. */
    public double[] link(int link) {
        return new double[]{(x[link] + x[link + 1]) / 2, (y[link] + y[link + 1]) / 2, (z[link] + z[link + 1]) / 2};
    }

    /**
     * Which way a link points, from the tail end to the head end, as degrees: {yaw, pitch}, in the game's sense (yaw 0 is
     * south, +z, turning towards west, -x; pitch 0 is level, positive looking down). A link folded flat on its next joint
     * points nowhere: then 0 and 0.
     */
    public double[] facing(int link) {
        double dx = x[link] - x[link + 1], dy = y[link] - y[link + 1], dz = z[link] - z[link + 1];
        double flat = Math.sqrt(dx * dx + dz * dz);
        if (flat < 1e-9 && Math.abs(dy) < 1e-9) return new double[]{0, 0};
        return new double[]{Math.toDegrees(Math.atan2(-dx, dz)), Math.toDegrees(-Math.atan2(dy, flat))};
    }

    /** How wide a link is, as a share of the widest (the first, behind the head), narrowing evenly down to {@code tip} at the last. */
    public double taper(int link, double tip) {
        return links() <= 1 ? 1 : 1 - (1 - tip) * link / (links() - 1.0);
    }

    /** The joints as one array, x y z for each in turn: what a client is sent when it first sees the body. */
    public double[] save() {
        double[] all = new double[x.length * 3];
        for (int joint = 0; joint < x.length; joint++) {
            all[joint * 3] = x[joint];
            all[joint * 3 + 1] = y[joint];
            all[joint * 3 + 2] = z[joint];
        }
        return all;
    }

    /** Takes the joints back from {@link #save}; an array of another length leaves the chain as it is. */
    public void load(double[] all) {
        if (all.length != x.length * 3) return;
        for (int joint = 0; joint < x.length; joint++) set(joint, all[joint * 3], all[joint * 3 + 1], all[joint * 3 + 2]);
    }
}
