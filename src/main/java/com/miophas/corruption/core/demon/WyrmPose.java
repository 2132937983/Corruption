package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.SolomonRules.Wyrm;

/**
 * How a flood dragon's body moves while it is at work, as rules: plain Java. Its body lies where its head has flown
 * ({@link WyrmChain}); at work, it moves about that: waves running down it, from side to side and up and down, its middle
 * arched, a shudder through it. Each thing it does has a motion of its own, one as it winds up and another as it works, the
 * one flowing into the other; the motion takes hold as it begins and dies away once it is done. Its neck stays where its
 * head is, and each link keeps its length, so that a body thrown into curves draws its tail in after it.
 *
 * <p>The same pose is kept on the server, where its links are struck at and hurt and break what they pass through, and on
 * each client, where it is drawn.
 */
public final class WyrmPose {
    /**
     * One way a body moves: a wave down it, so far to either side ({@code side}) and up and down ({@code up}) at its fullest,
     * {@code length} blocks from crest to crest, running from the head to the tail once every {@code period} ticks (from the
     * tail to the head if less than 0; if 0, standing where it is, swelling and easing as it breathes); its middle arched up
     * so far ({@code arch}; bowed down if less than 0); and shuddering so far ({@code tremor}). The wave grows a little
     * towards the tail, which whips.
     */
    public record Wave(double side, double up, double length, double period, double arch, double tremor) {
        public static final Wave STILL = new Wave(0, 0, 1, 0, 0, 0);

        public Wave {
            if (length <= 0) throw new IllegalArgumentException("A wave is some length");
        }
    }

    /** How it moves at one of the things it does: as it winds up, for so many ticks; then as it works. */
    public record Motion(Wave windup, int windupTicks, Wave work) {}

    /** Ticks a motion takes to take hold, to flow from its wind-up into its work, and to die away once the work is done. */
    public static final int EASE_IN = 10, BLEND = 8, EASE_OUT = 16;
    /** How far down its body, as a share of it, a motion has fully taken hold; the neck itself never moves. */
    public static final double NECK = .12;
    /** How long a standing wave takes to swell and ease once, in ticks. */
    public static final double BREATH = 40;

    /** How a dragon's body moves at each thing it does; still, at nothing. */
    public static Motion motion(Wyrm skill) {
        return switch (skill) {
            // Drawing breath, an S gathering up towards its head, its back arched; then braced in a stiff S, shuddering as it pours its beam out.
            case BEAM -> new Motion(new Wave(2.4, 0, 22, -40, 1.5, 0), SolomonRules.BEAM.timing().windup(), new Wave(1.6, 0, 22, 0, 1, .35));
            // Gathering itself; then pumping, swell after swell running up its body to its mouth, a bolt with each.
            case DELUGE -> new Motion(new Wave(1.6, .6, 18, -30, 1, 0), SolomonRules.DELUGE.timing().windup(), new Wave(.5, 1.4, 14, -10, .5, .15));
            // Rearing; then heaving, a heave running up it to its mouth with each fireball.
            case FIREBALLS -> new Motion(new Wave(0, 1.2, 24, -24, 2, 0), SolomonRules.FIREBALLS.timing().windup(),
                    new Wave(.4, 1.8, 16, -SolomonRules.FIREBALLS.every(), 1, 0));
            // Rearing up; then swimming down through the earth.
            case BURROW -> new Motion(new Wave(0, 0, 24, 0, 2.5, 0), SolomonRules.BURROW.timing().windup(), new Wave(1.8, 0, 18, 16, 0, 0));
            // Coiled like a spring, straining; then loosed, straight as a spear but for a ripple.
            case RAM -> new Motion(new Wave(2.8, .8, 16, 0, 1, .25), SolomonRules.RAM.timing().windup(), new Wave(.8, 0, 24, 12, 0, 0));
            // A slow and graceful corkscrew.
            case VENUS -> new Motion(new Wave(1.2, .8, 36, 40, 1.2, 0), SolomonRules.VENUS.timing().windup(), new Wave(1.5, 1, 36, 30, 1.5, 0));
            // Rising in a corkscrew to call the storm; then thrashing in it.
            case STORM -> new Motion(new Wave(1.4, 1.4, 22, 20, 2, 0), SolomonRules.STORM.timing().windup(), new Wave(2.2, 1.2, 16, 9, 1.5, .3));
            // Heaving, sagging towards the ground the hands come out of; then pounding down with them.
            case HANDS -> new Motion(new Wave(0, 2, 30, 24, -1.2, 0), SolomonRules.HANDS.timing().windup(), new Wave(.6, 2.4, 30, 15, -1, 0));
            // Swimming hard through the air.
            case SWOOP -> new Motion(Wave.STILL, 0, new Wave(1.4, .4, 20, 12, 0, 0));
            // Rearing up at the sky, its body arched as the circle opens; then braced and shuddering as the beam pours.
            case SKY_BEAM -> new Motion(new Wave(0, 1, 26, -26, 2.2, 0), SolomonRules.SKY_BEAM.timing().windup(), new Wave(.8, .6, 20, 0, 1.6, .3));
            // Coiling slowly as though drawing the star of circles, swells running up it; then turning with the star as it closes in.
            case PENTAGRAM -> new Motion(new Wave(1.6, 1, 24, -24, 1.6, 0), SolomonRules.PENTAGRAM.timing().windup(), new Wave(1.4, .8, 20, 15, 1.2, .2));
            case HEXAGRAM -> new Motion(new Wave(1.4, 1.2, 22, -22, 1.8, 0), SolomonRules.HEXAGRAM.timing().windup(), new Wave(1.6, .9, 22, 13, 1.3, .2));
            case OCTAGRAM -> new Motion(new Wave(1.8, 1.2, 26, -28, 2, 0), SolomonRules.OCTAGRAM.timing().windup(), new Wave(1.8, 1, 24, 12, 1.5, .25));
            case NONE -> new Motion(Wave.STILL, 0, Wave.STILL);
        };
    }

    /**
     * How strongly its motion holds it, 0 to 1: taking hold over the first {@link #EASE_IN} ticks of its work, and dying away
     * over the {@link #EASE_OUT} after it is done.
     *
     * @param since how many ticks it has been at it
     * @param ended how many ticks it has been done with it; less than 0 while it is still at it
     */
    public static double strength(double since, double ended) {
        return smooth(since / EASE_IN) * (ended < 0 ? 1 : 1 - smooth(ended / EASE_OUT));
    }

    /**
     * Its body as it moves: at work at {@code skill} for {@code since} ticks, and done with it {@code ended} ticks since (less
     * than 0 while still at it); its joints, x y z for each in turn, written into {@code into} (three times as long as the chain
     * has joints), which is answered. With nothing to do, or its motion died away, its body lies just where the chain has it.
     */
    public static double[] pose(WyrmChain chain, Wyrm skill, double since, double ended, double[] into) {
        int joints = chain.joints();
        if (into.length != joints * 3) throw new IllegalArgumentException("Three numbers for each joint");
        double strength = skill == Wyrm.NONE ? 0 : strength(since, ended);
        if (strength <= 0) {
            for (int joint = 0; joint < joints; joint++) put(into, joint, chain.x(joint), chain.y(joint), chain.z(joint));
            return into;
        }
        Motion motion = motion(skill);
        double blend = smooth((since - motion.windupTicks()) / BLEND), length = chain.length();
        // Which way is to its side: along the ground, square to its body; carried on down a body that hangs straight up or down.
        double sideX = 1, sideZ = 0;
        put(into, 0, chain.x(0), chain.y(0), chain.z(0));
        for (int joint = 1; joint < joints; joint++) {
            int ahead = joint - 1, behind = Math.min(joints - 1, joint + 1);
            double tx = chain.x(ahead) - chain.x(behind), ty = chain.y(ahead) - chain.y(behind), tz = chain.z(ahead) - chain.z(behind);
            double run = Math.sqrt(tx * tx + ty * ty + tz * tz);
            if (run > 1e-6) {
                tx /= run;
                ty /= run;
                tz /= run;
                double flat = Math.hypot(tz, tx);
                if (flat > .2) {
                    sideX = -tz / flat;
                    sideZ = tx / flat;
                }
            }
            // Its up: square to its side and to its body; straight up where it lies all in a heap.
            double upX = -sideZ * ty, upY = sideZ * tx - sideX * tz, upZ = sideX * ty, up = Math.sqrt(upX * upX + upY * upY + upZ * upZ);
            if (run <= 1e-6 || up < .2) {
                upX = 0;
                upY = 1;
                upZ = 0;
            } else {
                upX /= up;
                upY /= up;
                upZ /= up;
            }
            double share = joint / (joints - 1.0);
            double[] wound = offset(motion.windup(), share, share * length, since, joint), working = offset(motion.work(), share, share * length, since, joint);
            double lateral = strength * (wound[0] + (working[0] - wound[0]) * blend), lift = strength * (wound[1] + (working[1] - wound[1]) * blend);
            double px = chain.x(joint) + sideX * lateral + upX * lift, py = chain.y(joint) + upY * lift, pz = chain.z(joint) + sideZ * lateral + upZ * lift;
            // It keeps its length: drawn back towards the joint before it, as far from it as the chain has it.
            double bx = into[ahead * 3], by = into[ahead * 3 + 1], bz = into[ahead * 3 + 2];
            double dx = px - bx, dy = py - by, dz = pz - bz, reach = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double keep = Math.sqrt(Math.pow(chain.x(joint) - chain.x(ahead), 2) + Math.pow(chain.y(joint) - chain.y(ahead), 2) + Math.pow(chain.z(joint) - chain.z(ahead), 2));
            if (reach < 1e-9) put(into, joint, px, py, pz);
            else put(into, joint, bx + dx * keep / reach, by + dy * keep / reach, bz + dz * keep / reach);
        }
        return into;
    }

    /** How far a wave moves a joint this share of the way down the body ({@code along} blocks down it) at this moment: {to its side, up}. */
    private static double[] offset(Wave wave, double share, double along, double ticks, int joint) {
        if (wave == Wave.STILL) return new double[]{0, 0};
        double grow = smooth(share / NECK), whip = .8 + .4 * share;
        double phase = 2 * Math.PI * (along / wave.length() - (wave.period() == 0 ? 0 : ticks / wave.period()));
        double swell = wave.period() == 0 ? .85 + .15 * Math.sin(2 * Math.PI * ticks / BREATH) : 1;
        double side = grow * whip * swell * wave.side() * Math.sin(phase);
        double up = grow * (whip * swell * wave.up() * Math.cos(phase) + wave.arch() * Math.sin(Math.PI * share) + wave.tremor() * Math.sin(ticks * 2.9 + joint * 1.7));
        return new double[]{side, up};
    }

    private static void put(double[] into, int joint, double x, double y, double z) {
        into[joint * 3] = x;
        into[joint * 3 + 1] = y;
        into[joint * 3 + 2] = z;
    }

    /** 0 below 0, 1 above 1, and an easy S between. */
    static double smooth(double x) {
        if (x <= 0) return 0;
        if (x >= 1) return 1;
        return x * x * (3 - 2 * x);
    }

    private WyrmPose() {}
}
