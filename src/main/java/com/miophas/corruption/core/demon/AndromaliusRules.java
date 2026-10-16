package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.List;

/**
 * Andromalius, the seventy-second spirit, as rules: plain Java, no game code. What he does, and when, is decided
 * here ({@link #choose}, {@link #intent}); the entity only carries it out.
 *
 * <p>He fights as a great snake hunts. Most of the time he <b>circles</b> his quarry at a middle distance, facing
 * it, and works on it from there:
 * <ul>
 *   <li><b>The gaze.</b> When it stands neither too near nor too far, he rears and fixes it with the eye on his
 *       brow: an eye flashes before its eyes, as an elder guardian's face does, and it is left slowed and blinded.</li>
 *   <li><b>The volley.</b> In range and in sight, he spits fireballs at it, two a tick: ten while he is whole, more
 *       as he is hurt, twenty-two at the last. Half are aimed where it is going, half where it is.</li>
 * </ul>
 * Every so often, and always when it is blinded or slowed, he <b>hunts</b>: closes in fast and strikes.
 * <ul>
 *   <li><b>The charge.</b> Hunting it, or with it hidden from him behind a wall, he coils and then rushes it head
 *       down, straight along the line he last saw it on, ramming what he meets and smashing through every block
 *       softer than obsidian in his way ({@link #CHARGE}).</li>
 *   <li><b>The bite.</b> Within reach, he rears and lunges, his fangs leaving poison.</li>
 *   <li><b>The tail.</b> Something behind him or at his side is lashed with his tail and thrown off.</li>
 * </ul>
 * After a few bites he breaks off and draws back to circle again. Whatever he is doing:
 * <ul>
 *   <li><b>The ward.</b> When he feels the distance closing fast (something rushing him), he raises a ward for a
 *       few seconds, through which even the arcane (the only thing that hurts a demon at all) hurts him by half;
 *       then it falls and takes a few seconds to come back.</li>
 *   <li><b>Dodging.</b> While the ward is down, an arrow or anything else thrown at him that will strike him is
 *       slithered aside from.</li>
 * </ul>
 * Brought below half his health he is <b>enraged</b>: faster, his attacks come round sooner, his volleys fan out.
 */
public final class AndromaliusRules {
    public static final double MAX_HEALTH = 160, ARMOR = 8, SPEED = .32, FOLLOW_RANGE = 48, KNOCKBACK_RESISTANCE = .8;
    /**
     * His body's hitboxes, measured from his model at rest: his head reared some three and a half blocks up and a
     * block and a half ahead, his neck under it, the coil he rises from, and his tail lying in an S on the ground
     * behind him to nearly seven blocks back. His own box only stands for him in the world (it bumps into walls
     * and is what creatures go for); blows land on these.
     */
    public static final List<BodyPart> BODY = List.of(
            new BodyPart("head", 1.45, 0, 2.95, 1.1, 1.05),
            new BodyPart("neck", 1.2, 0, 1, 1.05, 2),
            new BodyPart("coil", .3, 0, 0, 1.4, 1.2),
            new BodyPart("tail_0", -1.1, 0, 0, 1, .9),
            new BodyPart("tail_1", -2.3, -.5, 0, .95, .8),
            new BodyPart("tail_2", -3.5, -.75, 0, .9, .75),
            new BodyPart("tail_3", -4.7, -.3, 0, .85, .7),
            new BodyPart("tail_4", -5.85, -.4, 0, .75, .6),
            new BodyPart("tail_tip", -6.75, -.85, 0, .6, .5));

    /** What a bite does, before armour: nine, and Poison II for four seconds. */
    public static final double BITE_DAMAGE = 9;
    public static final int POISON_TICKS = 80, POISON_LEVEL = 1;

    /** What he does, as the client poses it. */
    public enum Attack { NONE, GAZE, VOLLEY, BITE, TAIL, CHARGE }

    /** How he moves when he is not striking. */
    public enum Intent {
        /** Slithers towards it, to bring it into reach or sight. */
        CLOSE_IN,
        /** Circles it at a middle distance, facing it. */
        CIRCLE,
        /** Draws back from it. */
        BACK_OFF,
        /** Closes in fast, to bite. */
        HUNT
    }

    // ---- distances ----

    /** He circles at about this distance, drawing back when nearer than {@link #NEAR} and closing in when further than {@link #FAR}. */
    public static final double CIRCLE_AT = 11, NEAR = 6, FAR = 18;
    /** How far his bite reaches, and his tail. */
    public static final double BITE_REACH = 4.5, TAIL_REACH = 5.5;
    /** Something further round than this from where he faces, in degrees, is behind him or at his side, for his tail. */
    public static final double TAIL_ARC = 100;

    // ---- the gaze ----
    public static final double GAZE_MIN = 8, GAZE_MAX = 24;
    public static final int GAZE_WINDUP = 20, GAZE_COOLDOWN = 160;
    public static final int SLOWNESS_TICKS = 160, SLOWNESS_LEVEL = 1, BLINDNESS_TICKS = 100;

    // ---- the volley ----
    public static final double VOLLEY_RANGE = 30, VOLLEY_MIN_RANGE = 5;
    public static final int VOLLEY_WINDUP = 12, VOLLEY_COOLDOWN = 80;
    public static final int VOLLEY_MIN = 10, VOLLEY_MAX = 22, PER_TICK = 2;
    /** How fast his fireballs go, about, in blocks a tick, for leading his aim; and how far ahead he leads at most, in ticks. */
    public static final double FIREBALL_SPEED = 1.2;
    public static final int MAX_LEAD = 20;
    /** Enraged, his volleys sweep across this many degrees. */
    public static final double ENRAGED_FAN = 40;

    // ---- the bite and the tail ----
    public static final int BITE_WINDUP = 6, BITE_TICKS = 12, BITE_COOLDOWN = 20;
    public static final int TAIL_WINDUP = 8, TAIL_TICKS = 16, TAIL_COOLDOWN = 50;
    public static final double TAIL_DAMAGE = 7;

    // ---- hunting ----
    /** He hunts this often, at the latest; for this long, or this many bites; then draws back for this long. */
    public static final int HUNT_EVERY = 200, HUNT_TICKS = 120, HUNT_BITES = 3, RETREAT_TICKS = 30;

    // ---- dying ----
    /** He takes this long to die: rearing and writhing, then falling, and gone in a burst of light at the end of it. */
    public static final int DEATH_TICKS = 70;

    // ---- the ward and dodging ----
    public static final int SHIELD_TICKS = 80, SHIELD_COOLDOWN = 80;
    /** How much of a hurt reaches him through his ward. */
    public static final double WARD_SHARE = .5;
    public static final double CLOSING = .25, SHIELD_REACH = 16;
    public static final int APPROACH_SAMPLES = 6;
    /** A thrown thing is dodged if it will pass within this of his middle in the next so many ticks; he can dodge this often. */
    public static final double DODGE_MARGIN = 1.6;
    public static final int DODGE_HORIZON = 15, DODGE_COOLDOWN = 40;

    // ---- enraged ----
    /** At or below this share of his health he is enraged: this much faster, his cooldowns this share of their length. */
    public static final float ENRAGED_AT = .5F;
    public static final double ENRAGED_SPEED = .25, ENRAGED_COOLDOWN = .6;

    public static boolean enraged(float healthShare) { return healthShare <= ENRAGED_AT; }

    // ---- his skills, as the modules every boss may share carry them out ----

    /** His gaze: the eye on his brow, no Eyes of Providence about it. */
    public static final GazeRules GAZE = new GazeRules(new SkillTiming(GAZE_WINDUP, GAZE_WINDUP + 1, GAZE_COOLDOWN), GAZE_MIN, GAZE_MAX,
            SLOWNESS_TICKS, SLOWNESS_LEVEL, BLINDNESS_TICKS, 0, 0, 0);
    /**
     * His charge: coiled for 16 ticks, then 16 ticks at 1.35 blocks a tick (some 21 blocks) at what is 7 to 22 blocks
     * off; twelve to what he rams, thrown off hard; every 10 seconds at the most.
     */
    public static final ChargeRules CHARGE = new ChargeRules(new SkillTiming(16, 32, 200), 7, 22, 1.35, 12, 1.5, ChargeRules.OBSIDIAN);

    /** A cooldown as long as it is for him now: shorter enraged. */
    public static int cooldown(int ticks, boolean enraged) { return enraged ? (int) Math.round(ticks * ENRAGED_COOLDOWN) : ticks; }

    // ---- choosing ----

    /**
     * What he sees of a moment, to choose by.
     *
     * @param distance  how far his quarry is
     * @param sight     whether he can see it
     * @param behind    whether it is behind him or at his side ({@link #TAIL_ARC})
     * @param impaired  whether it is blinded or slowed
     * @param hunting   whether he is hunting it
     * @param retreating whether he is drawing back after a hunt
     * @param gazeReady and the rest: whether each attack has come round again
     */
    public record Situation(double distance, boolean sight, boolean behind, boolean impaired, boolean hunting, boolean retreating,
                            boolean gazeReady, boolean volleyReady, boolean biteReady, boolean tailReady, boolean chargeReady) {
        /** As things stood before he could charge: his charge not ready. */
        public Situation(double distance, boolean sight, boolean behind, boolean impaired, boolean hunting, boolean retreating,
                         boolean gazeReady, boolean volleyReady, boolean biteReady, boolean tailReady) {
            this(distance, sight, behind, impaired, hunting, retreating, gazeReady, volleyReady, biteReady, tailReady, false);
        }
    }

    /**
     * What he does next, if anything: his tail for what is close behind him, his fangs for what is in reach in
     * front, his charge for what he is hunting or cannot see within its reach, his gaze for what is at the right
     * distance and not already blinded, his fireballs for what is in range but not on top of him.
     */
    public static Attack choose(Situation now) {
        if (now.tailReady() && now.behind() && now.distance() <= TAIL_REACH) return Attack.TAIL;
        if (now.biteReady() && !now.behind() && now.distance() <= BITE_REACH) return Attack.BITE;
        if (now.chargeReady() && !now.retreating() && (now.hunting() || !now.sight()) && CHARGE.inRange(now.distance())) return Attack.CHARGE;
        if (!now.sight()) return Attack.NONE;
        if (now.gazeReady() && !now.impaired() && inGaze(now.distance())) return Attack.GAZE;
        if (now.volleyReady() && now.distance() >= VOLLEY_MIN_RANGE && now.distance() <= VOLLEY_RANGE) return Attack.VOLLEY;
        return Attack.NONE;
    }

    /** How he moves meanwhile: hunting or after something impaired, he closes for the bite; else he keeps his distance and circles. */
    public static Intent intent(Situation now) {
        if (now.retreating()) return Intent.BACK_OFF;
        if (now.hunting() || now.impaired()) return Intent.HUNT;
        if (!now.sight() || now.distance() > FAR) return Intent.CLOSE_IN;
        if (now.distance() < NEAR) return Intent.BACK_OFF;
        return Intent.CIRCLE;
    }

    /** Whether he should break off a hunt: it has gone on long enough, or he has bitten enough. */
    public static boolean huntOver(int ticks, int bites) { return ticks >= HUNT_TICKS || bites >= HUNT_BITES; }

    /** Whether something at this many degrees round from where he faces is behind him or at his side. */
    public static boolean behind(double degreesOff) { return Math.abs(wrap(degreesOff)) >= TAIL_ARC; }

    /** An angle brought between -180 and 180 degrees. */
    public static double wrap(double degrees) {
        double d = degrees % 360;
        if (d > 180) d -= 360;
        if (d <= -180) d += 360;
        return d;
    }

    // ---- the volley ----

    /** How many fireballs a volley looses: ten while he is whole, more as he is hurt, twenty-two at the last. */
    public static int volleySize(float healthShare) {
        float hurt = 1 - Math.max(0, Math.min(1, healthShare));
        return VOLLEY_MIN + Math.round((VOLLEY_MAX - VOLLEY_MIN) * hurt);
    }

    /** How many of a volley's fireballs leave on this tick of its firing, counting from 0: two a tick, until all have. */
    public static int firedOn(int tick, int total) {
        if (tick < 0) return 0;
        return Math.max(0, Math.min(PER_TICK, total - tick * PER_TICK));
    }

    /** Ticks a volley of this many takes to fire. */
    public static int firingTicks(int total) { return (Math.max(0, total) + PER_TICK - 1) / PER_TICK; }

    /** Whether this fireball of a volley is aimed where its mark is going (every other one) rather than where it is. */
    public static boolean leads(int index) { return index % 2 == 0; }

    /**
     * Where to aim to meet something moving: where it will be by the time a fireball from {@code from} reaches it,
     * going at {@link #FIREBALL_SPEED}, but never more than {@link #MAX_LEAD} ticks ahead. Points are x, y, z.
     */
    public static double[] lead(double[] from, double[] at, double[] velocity) {
        double dx = at[0] - from[0], dy = at[1] - from[1], dz = at[2] - from[2];
        double ticks = Math.min(MAX_LEAD, Math.sqrt(dx * dx + dy * dy + dz * dz) / FIREBALL_SPEED);
        return new double[]{at[0] + velocity[0] * ticks, at[1] + velocity[1] * ticks, at[2] + velocity[2] * ticks};
    }

    /** Enraged, how far round this fireball of a volley is turned, in degrees: the volley sweeps from one side of the fan to the other. */
    public static double fanned(int index, int total) {
        if (total <= 1) return 0;
        return -ENRAGED_FAN / 2 + ENRAGED_FAN * index / (total - 1);
    }

    // ---- dodging ----

    /**
     * Whether something thrown, at {@code at} and moving by {@code velocity} a tick, will pass within
     * {@link #DODGE_MARGIN} of {@code middle} in the next {@link #DODGE_HORIZON} ticks.
     */
    public static boolean threatens(double[] at, double[] velocity, double[] middle) {
        double vx = velocity[0], vy = velocity[1], vz = velocity[2], speed = vx * vx + vy * vy + vz * vz;
        if (speed < 1e-4) return false;
        double rx = middle[0] - at[0], ry = middle[1] - at[1], rz = middle[2] - at[2];
        // The tick at which it comes nearest, held to the next few: before it is past, and not too far ahead.
        double when = (rx * vx + ry * vy + rz * vz) / speed;
        if (when < 0 || when > DODGE_HORIZON) return false;
        double cx = at[0] + vx * when - middle[0], cy = at[1] + vy * when - middle[1], cz = at[2] + vz * when - middle[2];
        return cx * cx + cy * cy + cz * cz <= DODGE_MARGIN * DODGE_MARGIN;
    }

    // ---- the gaze and the ward ----

    public static boolean inGaze(double distance) { return distance >= GAZE_MIN && distance <= GAZE_MAX; }

    public static boolean rushed(double closing, double distance) { return closing >= CLOSING && distance <= SHIELD_REACH; }

    /** Watches how far his quarry is, tick by tick, to tell how fast it is closing in. */
    public static final class Approach {
        private final double[] seen = new double[APPROACH_SAMPLES];
        private int count, next;

        public void see(double distance) {
            seen[next] = distance;
            next = (next + 1) % seen.length;
            count = Math.min(seen.length, count + 1);
        }

        public void forget() { count = 0; next = 0; }

        /** Blocks a tick it has been closing in by, on average: below none if it is drawing away; none until a full run has been seen. */
        public double closing() {
            if (count < seen.length) return 0;
            double oldest = seen[next], newest = seen[(next + seen.length - 1) % seen.length];
            return (oldest - newest) / (seen.length - 1);
        }
    }

    /** The ward's clock: up for {@link #SHIELD_TICKS}, then down and coming back for as long as it is told. */
    public static final class Shield {
        private int up, cooling;

        public boolean raised() { return up > 0; }
        public boolean ready() { return up == 0 && cooling == 0; }
        public int up() { return up; }
        public int cooling() { return cooling; }

        public boolean raise() {
            if (!ready()) return false;
            up = SHIELD_TICKS;
            return true;
        }

        /** A tick passes; true on the tick it falls, after which it comes back over {@code recovery} ticks. */
        public boolean tick(int recovery) {
            if (up > 0) {
                if (--up == 0) { cooling = Math.max(0, recovery); return true; }
            } else if (cooling > 0) cooling--;
            return false;
        }

        /** A tick passes, the ward coming back over its usual time. */
        public boolean tick() { return tick(SHIELD_COOLDOWN); }

        public void set(int up, int cooling) {
            this.up = Math.max(0, Math.min(SHIELD_TICKS, up));
            this.cooling = Math.max(0, Math.min(SHIELD_COOLDOWN, cooling));
        }
    }

    private AndromaliusRules() {}
}
