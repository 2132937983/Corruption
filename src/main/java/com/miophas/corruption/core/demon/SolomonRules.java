package com.miophas.corruption.core.demon;

import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BlightRules;
import com.miophas.corruption.core.demon.skill.BurrowRules;
import com.miophas.corruption.core.demon.skill.DrakeRushRules;
import com.miophas.corruption.core.demon.skill.EliteCallRules;
import com.miophas.corruption.core.demon.skill.FireballRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.HandRules;
import com.miophas.corruption.core.demon.skill.RamRules;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SkyBeamRules;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import com.miophas.corruption.core.demon.skill.SwoopRules;
import com.miophas.corruption.core.demon.skill.UpheavalRules;
import com.miophas.corruption.core.demon.skill.VenusRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Solomon, the king who bound the seventy-two spirits of the Goetia with his seal and wrote their names in his Key; as rules:
 * plain Java, no game code. He is the last but one of the corruption's lords, called up at the octagram altar of the Echo
 * Sanctum with the eight books of his Key.
 *
 * <p>He is eight flood dragons in one: a crowned seal of eight points hangs in the sky over the altar (his core, which
 * nothing can hurt), and out of it the eight dragons burst, each {@link #LENGTH} blocks long, the eight books of his Key each
 * made a dragon. Each must be slain; when the last falls, he falls. The dragons hang in the air, circling their quarry
 * ({@link #orbit}); every one of them may do any of twelve things ({@link Wyrm}), but no more than {@link #CASTERS} of them at
 * once, so that the sky is full and yet each blow can be seen coming; between them, they swoop on it ({@link #SWOOP}). Whatever
 * a dragon's body passes through is hurt ({@link #HEAD_CONTACT}) and, as the Ender Dragon breaks it, broken. The core, for its
 * part, works eight things of its own ({@link Edict}), every so often, upon whoever its dragons hunt.
 *
 * <p>Like the spirits he bound, only the arcane can hurt his dragons; unlike them, no corrupted robe hides anyone from him, nor
 * turns aside his hurt.
 */
public final class SolomonRules {
    /** How many dragons he is. */
    public static final int DRAGONS = 8;
    /** Each dragon's health, armour and the rest. */
    public static final double WYRM_HEALTH = 400, ARMOR = 12, FOLLOW_RANGE = 112, KNOCKBACK_RESISTANCE = 1;
    /** A dragon's body: this many joints (one link fewer) this far apart behind its head, which is this long. */
    public static final int JOINTS = 31;
    public static final double SPACING = 2, HEAD_LENGTH = 4;
    /** How long a dragon is, from its snout to the tip of its tail: four chunks. */
    public static final double LENGTH = HEAD_LENGTH + SPACING * (JOINTS - 1);
    /** A dragon's head (its own box), the widest link of its body, and how wide its last is as a share of that. */
    public static final float HEAD_WIDTH = 3, HEAD_HEIGHT = 2.4F;
    public static final double LINK_WIDTH = 2.6, TAPER = .4;
    /** His core's box, and how far above the altar's offering it hangs. */
    public static final float CORE_SIZE = 3;
    public static final double CORE_HEIGHT = 16;
    /** How fast a dragon flies, in blocks a tick, and how fast it can turn, in degrees a tick. */
    public static final double CRUISE = .85, TURN = 7;
    /** How near the ground a dragon comes as it circles. */
    public static final double CLEARANCE = 5;
    /**
     * What touching a dragon does: its head hurts so much (his hurt, which no robe turns aside), every link of its body so
     * much, again as soon as the touched can be hurt again (half a second); its head throws them on the way it goes, its body
     * off to the side, so hard.
     */
    public static final double HEAD_CONTACT = 14, BODY_CONTACT = 10, HEAD_THROW = 1, BODY_THROW = .5;
    /** How far from his core a dragon may stray after its quarry before it turns back. */
    public static final double LEASH = 112;
    /** How many of his dragons may be at work at once. */
    public static final int CASTERS = 3;
    /** Ticks a dragon rests between one thing it does and the next, and his core between its edicts. */
    public static final int WYRM_REST = 50, CORE_REST = 70;
    /**
     * While he is about, the weather is a thunderstorm: set to hold this many ticks ahead, and set again this often, so that
     * it ends about half a minute after he falls.
     */
    public static final int STORM_HOLD = 600, STORM_RENEW = 100;
    /** How long a dragon takes to die, and he himself once the last has fallen; and the experience he leaves. */
    public static final int WYRM_DEATH_TICKS = 50, DEATH_TICKS = 140, EXPERIENCE = 2000;
    /** The light of each of his eight dragons, the eight books of his Key in order: gold, crimson, violet, azure, jade, amber, pearl, rose. */
    public static final int[] ACCENTS = {0xE8B84A, 0xE0405A, 0xA060E0, 0x40A0F0, 0x40D890, 0xF08A30, 0xE8E8F8, 0xF060B0};
    /** His own colours: the gold of his crown and the deep violet of his seal. */
    public static final int GOLD = 0xF2C46A, VIOLET = 0x6A2C9A;

    /**
     * What a dragon may do; NONE while it only flies. SWOOP is none of its things, but what it does between them. The last
     * four are its beams out of magic circles in the sky: one circle, or a pentagram, a hexagram or an octagram of them.
     */
    public enum Wyrm { NONE, BEAM, DELUGE, FIREBALLS, BURROW, RAM, VENUS, STORM, HANDS, SWOOP, SKY_BEAM, PENTAGRAM, HEXAGRAM, OCTAGRAM }

    /** What his core may work upon his dragons' quarry; NONE while it works nothing. */
    public enum Edict { NONE, GAZE, GREATER_GAZE, ELITES, REMNANTS, UPHEAVAL, DORMANT, STUN, DRAKES }

    // ---- each dragon's ----

    /** Its breath made a beam: wound up 1.8 s, then 4 s of a beam 64 blocks long and 2.2 wide, every fifth of a second 9 of his hurt, turning 2 degrees a tick after its quarry. */
    public static final BeamRules BEAM = new BeamRules(new SkillTiming(36, 116, 340), 6, 56, 64, 2.2, 4, 9, 2F);
    /** A deluge of the corruption's bolts: 160 of them lobbed four a tick over 9 blocks about its quarry, each 6 and a splash of 1.5. */
    public static final BarrageRules DELUGE = new BarrageRules(new SkillTiming(30, 74, 400), 8, 64, 160, 4, 9, 24, 6, 1.5);
    /** Baal's fireballs: five, one every 8 ticks, at 1.4 a tick, leading by up to 20 ticks; 18 at the heart of each burst (radius 3.5), alight 5 s, breaking up to 30 blocks within 2.5 softer than obsidian. */
    public static final FireballRules FIREBALLS = new FireballRules(new SkillTiming(24, 64, 300), 8, 64, 5, 8, 1.4, 20, 18, 3.5, 5, 2.5, 30, 50F);
    /** Under its quarry and up through it: 6 down at 1.3 a tick, a second's warning, up at 2 a tick, 22 and flung 1.2 up within 4.5, the ground torn up within 3. */
    public static final BurrowRules BURROW = new BurrowRules(new SkillTiming(14, 240, 360), 10, 80, 6, 1.3, 20, 2, 22, 4.5, 22, 1.2, 3, 36);
    /** Down at where its quarry stood at 2 a tick: 20 and thrown 1.6 within 3; on 10 into the ground, 18 under it 6 down, and out into the sky, 10 within 4. */
    public static final RamRules RAM = new RamRules(new SkillTiming(26, 200, 320), 12, 64, 2, 3, 20, 1.6, 10, 6, 18, 4, 26);
    /** The Pentacle of Venus: when one of them within 40 has fallen below three quarters, a fifth of each one's health back, Absorption IV 15 s, Regeneration II 10 s. */
    public static final VenusRules VENUS = new VenusRules(new SkillTiming(30, 50, 640), 40, .2, 300, 4, 200, 2, .75);
    /** Lightning: nine bolts about its quarry, one every fifth of a second, each 14 within 2.6. */
    public static final StrikeRules STORM = new StrikeRules(new SkillTiming(30, 70, 300), 0, 80, 9, 6, 4, 2.6, 14);
    /** Giant hands: three out of the ground, one under its quarry and two 5 from it; each a second rising, half a second reared, down in a quarter: 24 and flung up within 3.5. */
    public static final HandRules HANDS = new HandRules(new SkillTiming(24, 40, 340), 0, 80, 3, 5, 20, 10, 5, 20, 3.5, 24, .7);
    /**
     * How far round each of its beams out of the sky reaches: as much ground as a chunk (16 by 16, 256 blocks), round. 9.03.
     */
    public static final double CHUNK_BEAM = Math.sqrt(16 * 16 / Math.PI);
    /**
     * A beam out of a magic circle in the sky (SkyBeamRules), 24 over the ground where its quarry stood, as wide as a chunk
     * ({@link #CHUNK_BEAM} round): 2.25 s marked, long enough to sprint out of it from the mark (1.7 s) or just to walk out
     * (2.2 s); then 4.5 s pouring down on the mark, 10 every quarter second.
     */
    public static final SkyBeamRules SKY_BEAM = new SkyBeamRules(new SkillTiming(45, 135, 300), 0, 80, 1, 24, CHUNK_BEAM, 10, 5);
    /**
     * A pentagram of five circles, each pouring a beam as wide as a chunk, its points 24.4 out (67 blocks across it all,
     * 10.6 between two beams): 2.4 s marked, long enough to walk out of it from the mark through a gap; then 4.8 s pouring,
     * the star turning slowly and closing in on the mark; 8 every quarter second.
     */
    public static final SkyBeamRules PENTAGRAM = new SkyBeamRules(new SkillTiming(48, 144, 360), 0, 80, 5, 24, CHUNK_BEAM, 8, 5);
    /** A hexagram of six such circles, its points 28.7 out (75 across): 3 s marked, then 6 s pouring, as the pentagram's. */
    public static final SkyBeamRules HEXAGRAM = new SkyBeamRules(new SkillTiming(60, 180, 360), 0, 80, 6, 24, CHUNK_BEAM, 8, 5);
    /** An octagram of eight such circles, its points 37.5 out (93 across), its gaps narrower round: 4.8 s marked, then 9.6 s pouring, as the pentagram's. */
    public static final SkyBeamRules OCTAGRAM = new SkyBeamRules(new SkillTiming(96, 288, 420), 0, 80, 8, 24, CHUNK_BEAM, 8, 5);

    /**
     * Its swoop, between its eight: on a quarry within 64, diving at 1.25 a tick, turning at most 6 degrees a tick, for at most
     * 5 s; pulling up within 2 of it, or once by it; climbing away 1.5 s; 6 to 11 s between swoops; no more than two of a seal's
     * dragons swooping at once.
     */
    public static final SwoopRules SWOOP = new SwoopRules(64, 1.25, 6, 2, 100, 30, 14, 120, 220, 2);

    /** What a dragon may do, in the order it keeps them. */
    public static final List<Wyrm> WYRM_SKILLS = List.of(Wyrm.BEAM, Wyrm.DELUGE, Wyrm.FIREBALLS, Wyrm.BURROW, Wyrm.RAM, Wyrm.VENUS, Wyrm.STORM, Wyrm.HANDS,
            Wyrm.SKY_BEAM, Wyrm.PENTAGRAM, Wyrm.HEXAGRAM, Wyrm.OCTAGRAM);

    // ---- his core's ----

    /** Brainwashing: Dantalion's gaze, from as far as 96: Slowness II 8 s, Blindness 5 s. */
    public static final GazeRules GAZE = new GazeRules(new SkillTiming(24, 30, 240), 0, 96, 160, 2, 100, 0, 1, 1);
    /** Greater brainwashing: five eyes; Slowness II 13 s, Blindness 8 s, Wither II 7 s. */
    public static final GazeRules GREATER_GAZE = new GazeRules(new SkillTiming(32, 40, 480), 0, 96, 260, 2, 160, 140, 2, 5);
    /** Three or four of the corruption's elites about the quarry, 7 from it; never more than six about it. */
    public static final EliteCallRules ELITES = new EliteCallRules(new SkillTiming(30, 60, 900), 0, 96, 3, 6, 7);
    /** A band of remnants risen about the quarry: eight, 5 from it, sixteen at the most; they crumble after a minute. */
    public static final SummonRules REMNANTS = new SummonRules(new SkillTiming(30, 50, 600), 8, 16, 5,
            List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:husk", "minecraft:stray", "minecraft:vindicator", "minecraft:pillager",
                    "minecraft:wither_skeleton", "minecraft:drowned"), true, 1200);
    /** The ground under the quarry heaved up, broken open or speared, seven blocks across: risen 4 (12), sunk 4 (12), or speared (16). */
    public static final UpheavalRules UPHEAVAL = new UpheavalRules(new SkillTiming(20, 26, 300), 0, 96, 3.5, 4, 4, 12, 1.1, 16, 1.3);
    /** The ground about the quarry turned to the dormant corruption, 8 round, 220 blocks at the most; 10 and thrown as it strikes. */
    public static final BlightRules DORMANT = new BlightRules(new SkillTiming(20, 40, 360), 96, 8, 220, 16, true, 10, .8, .4);
    /** Everything about the quarry within 7 stunned, 2.5 s. */
    public static final ScreechRules STUN = new ScreechRules(new SkillTiming(24, 30, 420), 0, 96, 7, 50);
    /** Eight small dragons from the eight ways, 18 out and 3 up, gathering 1.5 s, then through at 1.1 a tick: 10 within 1.6, thrown 1. */
    public static final DrakeRushRules DRAKES = new DrakeRushRules(new SkillTiming(30, 90, 420), 0, 96, 8, 18, 3, 30, 1.1, 1.6, 10, 1);

    /** What his core may work, in the order it keeps them. */
    public static final List<Edict> EDICTS = List.of(Edict.GAZE, Edict.GREATER_GAZE, Edict.ELITES, Edict.REMNANTS, Edict.UPHEAVAL, Edict.DORMANT,
            Edict.STUN, Edict.DRAKES);

    /** Whether one more of his dragons may set to work, this many being at work already. */
    public static boolean mayCast(int casting) { return casting < CASTERS; }

    /** Whether one more of his dragons may swoop, this many swooping already. */
    public static boolean maySwoop(int swooping) { return SWOOP.mayBegin(swooping); }

    /**
     * Where a dragon flies while it does nothing else, {dx, dy, dz} from its quarry (or from his core, with no quarry): round it,
     * each dragon on a ring of its own (16 to 28 out, 8 to 17 up, rising and falling a little), close enough to strike from, the
     * eight spread evenly round, all going round together a full turn in fifteen seconds.
     */
    public static double[] orbit(int dragon, double ticks) {
        double angle = 2 * Math.PI * dragon / DRAGONS + ticks * 2 * Math.PI / 300;
        double radius = 16 + 6 * (dragon % 3), height = 8 + 3 * (dragon % 4) + 2 * Math.sin(ticks / 40 + dragon);
        return new double[]{Math.cos(angle) * radius, height, Math.sin(angle) * radius};
    }

    /**
     * What a dragon sees of a moment, to choose by.
     *
     * @param distance  how far its quarry is from its head
     * @param sight     whether it can see its quarry
     * @param lowest    the share of its health left to the most hurt of its kind within Venus's reach (itself among them)
     * @param below     how far its quarry stands below its head
     * @param ready     what it may do that has come round
     */
    public record WyrmSituation(double distance, boolean sight, double lowest, double below, Set<Wyrm> ready) {
        public WyrmSituation { ready = ready.isEmpty() ? EnumSet.noneOf(Wyrm.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What a dragon does next, if anything; each choice as likely as its weight ({@code roll}, 0 to 1, picks). Venus first,
     * when its kind is hurt enough. Then: its beam and its deluge and its fireballs for a quarry it can see; burrowing and
     * ramming, the likelier for a quarry far below it; lightning, hands and its beams out of the sky for any quarry in
     * reach, seen or not.
     */
    public static Wyrm chooseWyrm(WyrmSituation now, double roll) {
        if (now.ready().contains(Wyrm.VENUS) && VENUS.wanted(now.lowest())) return Wyrm.VENUS;
        List<Wyrm> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        double distance = now.distance();
        boolean low = now.below() > 8;
        consider(now.ready(), options, weights, Wyrm.BEAM, 4, now.sight() && BEAM.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.DELUGE, 4, now.sight() && DELUGE.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.FIREBALLS, 4, now.sight() && FIREBALLS.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.BURROW, 3 + (low ? 2 : 0), BURROW.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.RAM, 3 + (low ? 2 : 0), now.sight() && RAM.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.STORM, 3, STORM.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.HANDS, 3, HANDS.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.SKY_BEAM, 2, SKY_BEAM.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.PENTAGRAM, 2, PENTAGRAM.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.HEXAGRAM, 2, HEXAGRAM.inRange(distance));
        consider(now.ready(), options, weights, Wyrm.OCTAGRAM, 2, OCTAGRAM.inRange(distance));
        return pick(options, weights, roll, Wyrm.NONE);
    }

    /**
     * What his core sees of a moment, to choose by.
     *
     * @param distance  how far the quarry is from it
     * @param sight     whether it can see the quarry
     * @param elites    how many of the corruption's elites stand about the quarry
     * @param remnants  how many remnants it has called that still stand
     * @param grounded  whether the quarry stands on the ground
     * @param ready     what it may work that has come round
     */
    public record EdictSituation(double distance, boolean sight, int elites, int remnants, boolean grounded, Set<Edict> ready) {
        public EdictSituation { ready = ready.isEmpty() ? EnumSet.noneOf(Edict.class) : EnumSet.copyOf(ready); }
    }

    /**
     * What his core works next, if anything, each as likely as its weight. The gazes, the upheaval and the turning of the
     * ground need the quarry in its sight, the last two on the ground; elites and remnants are called only while there is room
     * for more of them; the stun and the drakes reach a quarry anywhere in reach.
     */
    public static Edict chooseEdict(EdictSituation now, double roll) {
        List<Edict> options = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        double distance = now.distance();
        consider(now.ready(), options, weights, Edict.GAZE, 4, now.sight() && GAZE.inRange(distance));
        consider(now.ready(), options, weights, Edict.GREATER_GAZE, 3, now.sight() && GREATER_GAZE.inRange(distance));
        consider(now.ready(), options, weights, Edict.ELITES, 2, ELITES.inRange(distance) && ELITES.wanted(now.elites()));
        consider(now.ready(), options, weights, Edict.REMNANTS, 3, REMNANTS.wanted(now.remnants()));
        consider(now.ready(), options, weights, Edict.UPHEAVAL, 3, now.sight() && now.grounded() && UPHEAVAL.inRange(distance));
        consider(now.ready(), options, weights, Edict.DORMANT, 3, now.sight() && now.grounded() && DORMANT.inRange(distance));
        consider(now.ready(), options, weights, Edict.STUN, 3, STUN.inRange(distance));
        consider(now.ready(), options, weights, Edict.DRAKES, 3, DRAKES.inRange(distance));
        return pick(options, weights, roll, Edict.NONE);
    }

    private static <T> void consider(Set<T> ready, List<T> options, List<Integer> weights, T skill, int weight, boolean serves) {
        if (!serves || !ready.contains(skill)) return;
        options.add(skill);
        weights.add(weight);
    }

    private static <T> T pick(List<T> options, List<Integer> weights, double roll, T none) {
        int total = 0;
        for (int weight : weights) total += weight;
        if (total == 0) return none;
        double pick = Math.max(0, Math.min(.999999, roll)) * total;
        for (int index = 0; index < options.size(); index++) {
            pick -= weights.get(index);
            if (pick < 0) return options.get(index);
        }
        return options.get(options.size() - 1);
    }

    private SolomonRules() {}
}
