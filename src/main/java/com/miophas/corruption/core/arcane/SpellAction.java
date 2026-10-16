package com.miophas.corruption.core.arcane;

import java.util.List;

/**
 * One thing a spell does, when it is cast by hand or when a spell bolt strikes. A spell is an ordered list of
 * these (see {@link SpellRule}), carried out in order; the same set of kinds serves a cast from the wand and the
 * payload of a bolt, so a spell may launch a bolt that carries more of the same: which effects a spell has, and
 * what a bolt does where it lands, is entirely data.
 *
 * <p>Nothing here touches the game: what an effect, a creature or an item is called is a plain id, and the
 * numbers are what the effect is worth. The game side turns each kind into its effect (see the mod's
 * common.arcane).
 *
 * <p>Whatever cannot be said with one of these kinds is often best said with a mob effect instead: blindness,
 * levitation, withering, hunger, darkness and dozens more are all {@link ApplyEffect}, and need no kind of
 * their own.
 */
public sealed interface SpellAction {
    /**
     * How far round its origin any action may reach, in blocks: what the data may ask for and what the spell then
     * reaches are both this at most, so a radius is either honoured or refused as the data loads, never quietly
     * cut short.
     */
    double MAX_RADIUS = 128;
    /**
     * How far round its origin {@link Corrupt} may reach, in blocks a way: less than any other, since every block
     * it reaches is a block it may change.
     */
    double MAX_CORRUPTION_RADIUS = 8;

    /** Who an effect falls on: the caster alone, or everything near the origin of the spell. */
    enum Target { SELF, NEARBY }

    /** What the sky is told to do. */
    enum Sky { CLEAR, RAIN, THUNDER }

    /**
     * Shows a spell's might and does nothing else: an effect drawn on the client, on the ground under the origin,
     * for {@code ticks} — a turning magic circle, a shockwave, a pillar of light, a vortex, a dome, a ring of runes,
     * or whatever an add-on draws — named by {@code effect} ("corruption:magic_circle"). It is {@code radius} wide
     * and {@code height} tall, drawn in {@code colour} with {@code accent} for its brightest parts (0xRRGGBB), shakes
     * the view of whoever is near by {@code shake} (0 to 1), and sounds {@code sound} as it appears ("" for none).
     */
    record Visual(String effect, double radius, double height, int ticks, int colour, int accent, double shake, String sound) implements SpellAction {
        /** The corruption's own colours, unless a spell says otherwise: its rose, and gold. */
        public static final int ROSE = 0xC6728C, GOLD = 0xFFD24A;

        public Visual {
            radius = Math.max(0, radius);
            height = Math.max(0, height);
            ticks = Math.max(1, ticks);
            shake = Math.max(0, Math.min(1, shake));
        }
    }

    /** Where a bolt sets off from: the caster (or where a bolt struck), along their gaze; or the sky over the origin, falling. */
    enum Source { CASTER, SKY }

    /** The shape particles are drawn in about the origin of a {@link Spectacle}. */
    enum Shape { BURST, RING, SPHERE, COLUMN, SPIRAL }

    /** Gives a mob effect to the caster alone ({@link Target#SELF}) or to everything within {@code radius} blocks. */
    record ApplyEffect(String effect, Target target, double radius, int duration, int amplifier) implements SpellAction {}

    /**
     * Hurts everything within {@code radius} blocks of the origin for this much, with a hurt of the kind
     * {@code type} names — a damage type of the game's own, named as any data names it — or, for "", the mark
     * a spell's hurt carries unless it asks for another (see the mod's api.arcane.ArcaneDamage).
     *
     * <p>A spell may hurt with a kind of its own, so that what it does can be told from what anything else
     * does: the corruption's own hurt, say, which the corruption's children are proof against.
     */
    record Damage(double amount, double radius, String type) implements SpellAction {
        /** A hurt of the mark a spell's hurt carries unless it asks for another. */
        public Damage(double amount, double radius) { this(amount, radius, ""); }
    }

    /** Sets everything within {@code radius} blocks of the origin alight, for this many seconds. */
    record Ignite(double radius, int seconds) implements SpellAction {}

    /** Chills everything within {@code radius} blocks of the origin, for this long. */
    record Freeze(double radius, int ticks) implements SpellAction {}

    /**
     * Corrupts the blocks around the origin within {@code radius}, each of them this much of the time, as the
     * infection does. Dormant ({@code inert}), it leaves only the dormant corruption: every solid block it takes
     * becomes the one block of the corruption that spreads no further, never malice nor the corruption that does,
     * and nothing grows on it; so a spell may blight the ground without loosing the corruption on the world.
     */
    record Corrupt(double radius, double chance, boolean inert) implements SpellAction {
        /** The infection's own corruption, which spreads as it does. */
        public Corrupt(double radius, double chance) { this(radius, chance, false); }
    }

    /**
     * Quiets the corruption round the origin, within {@code radius}: every whole block of it that would spread (the
     * corruption itself, malice, and the solid blocks it has taken) falls still, the dormant corruption that spreads
     * no further. What is not a whole block (stairs, slabs, doors) and the corruption's liquids are left as they are.
     */
    record Quell(double radius) implements SpellAction {}

    /**
     * Turns the creatures within {@code radius} blocks of the origin into the corruption's remnants on the spot, each
     * of them this much of the time: each falls to the corruption at once and rises where it stood, a remnant in its
     * own likeness, as one the corruption's curse has taken. Never a player (the curse takes players its own way),
     * nor what is of the corruption already, nor what the caster owns or summoned, nor what the world's data holds
     * beyond it (bosses, by default).
     */
    record CorruptCreatures(double radius, double chance) implements SpellAction {
        public CorruptCreatures { chance = Math.max(0, Math.min(1, chance)); }
        /** Every creature within reach. */
        public CorruptCreatures(double radius) { this(radius, 1); }
    }

    /** Brings forth this many of a creature, scattered around the origin. */
    record Summon(String entity, int count) implements SpellAction {}

    /**
     * Raises a band of the corruption's remnants round the origin, the caster's to command: {@code count} of them,
     * set evenly round it {@code radius} blocks off, in the likenesses named (creatures' ids), one after another, armed
     * and dressed as such creatures come. They never turn on the caster, nor on anything else the caster called up,
     * and they crumble away after {@code lifetime} ticks (0: they stay).
     */
    record RaiseRemnants(int count, double radius, List<String> likenesses, int lifetime) implements SpellAction {
        /** The dead of a raid and a crypt, unless a spell names its own. */
        public static final List<String> LIKENESSES = List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:husk", "minecraft:stray",
                "minecraft:vindicator", "minecraft:drowned");

        public RaiseRemnants {
            count = Math.max(1, count);
            radius = Math.max(0, radius);
            likenesses = likenesses.isEmpty() ? LIKENESSES : List.copyOf(likenesses);
            lifetime = Math.max(0, lifetime);
        }

        /** The likeness of the index-th of them. */
        public String likeness(int index) { return likenesses.get(Math.floorMod(index, likenesses.size())); }
    }

    /**
     * Marks a creature as the corruption's prey: the one a bolt struck, or, if it struck none, the nearest within a
     * little of where it struck ({@link #REACH}). Every creature of the corruption, its bosses among them, within
     * {@code radius} blocks of the prey or of the caster leaves whatever it was about and goes to bring it down, and
     * keeps after it for {@code ticks}, or until it falls. The mark itself does no harm, and the prey glows while it
     * lasts. A creature of the corruption may be marked too: for as long as the mark lasts, those sent after it are no
     * longer its allies.
     */
    record MarkPrey(double radius, int ticks) implements SpellAction {
        /** How near where a bolt struck a creature must be to be marked, if the bolt struck none. */
        public static final double REACH = 2;

        public MarkPrey {
            radius = Math.max(0, Math.min(MAX_RADIUS, radius));
            ticks = Math.max(1, ticks);
        }
    }

    /**
     * Swears a truce with the corruption: its creatures within {@code radius} blocks of the caster let them be (those
     * after them give it up) and will not take them for their quarry again for {@code ticks}. Not its bosses: they
     * swear to no one.
     */
    record Truce(double radius, int ticks) implements SpellAction {
        public Truce {
            radius = Math.max(0, Math.min(MAX_RADIUS, radius));
            ticks = Math.max(1, ticks);
        }
    }

    /**
     * Strikes this many bolts of lightning about the origin, within {@code radius} blocks of it. Lightning of the
     * game's own burns and hurts as lightning does; corrupted lightning ({@code corrupt}) is the corruption's violet
     * bolt, which sets nothing alight and hurts only as the spell says. Either may hurt everything within a little of
     * where it falls for {@code damage} more, with a hurt of the kind {@code damageType} names ("" for a spell's
     * own mark; see {@link Damage}).
     */
    record Lightning(double radius, int count, boolean corrupt, double damage, String damageType) implements SpellAction {
        /** Lightning of the game's own, hurting only as lightning does. */
        public Lightning(double radius, int count) { this(radius, count, false, 0, ""); }
    }

    /**
     * Bursts about the origin with this much force, breaking blocks or not. How much it breaks is the spell's to say:
     * none further than {@code breakRadius} blocks from it, none whose blast resistance is above
     * {@code maxResistance} (stone and deepslate are 6, obsidian 1200), and at most {@code maxBlocks} of them, the
     * nearest first. Left at their widest ({@link #ANY_REACH}, {@link #ANY_COUNT}, {@link #ANY_RESISTANCE}), it
     * breaks all an explosion of its force would.
     *
     * <p>A blast may crush, too ({@code crushHardness}, {@link #NO_CRUSH} for none): then every block within its break
     * radius (and {@link #CRUSH_REACH} at most) no harder than that breaks as well, however well it would have withstood
     * an explosion of its force (obsidian's hardness is 50, its blast resistance 1200). Never a block that cannot be
     * broken at all (bedrock, a barrier: their hardness is below none), and never more than {@code maxBlocks} in all.
     */
    record Explode(double power, boolean breaksBlocks, double breakRadius, int maxBlocks, double maxResistance, double crushHardness) implements SpellAction {
        /** No limit of reach, count or resistance beyond what the explosion's own force sets. */
        public static final double ANY_REACH = MAX_RADIUS, ANY_RESISTANCE = 3_600_000;
        public static final int ANY_COUNT = 32_768;
        /** Crushing nothing; and how far round a blast crushes at most, in blocks, since it looks at every block so near. */
        public static final double NO_CRUSH = -1, CRUSH_REACH = 8;
        /** Obsidian's hardness. */
        public static final double OBSIDIAN = 50;

        public Explode {
            power = Math.max(0, power);
            breakRadius = Math.max(0, Math.min(ANY_REACH, breakRadius));
            maxBlocks = Math.max(0, Math.min(ANY_COUNT, maxBlocks));
            maxResistance = Math.max(0, Math.min(ANY_RESISTANCE, maxResistance));
            crushHardness = crushHardness < 0 ? NO_CRUSH : crushHardness;
        }

        /** A blast that breaks what its force breaks, as far as these bounds let it, and crushes nothing. */
        public Explode(double power, boolean breaksBlocks, double breakRadius, int maxBlocks, double maxResistance) {
            this(power, breaksBlocks, breakRadius, maxBlocks, maxResistance, NO_CRUSH);
        }

        /** An explosion that breaks all its force would, or nothing. */
        public Explode(double power, boolean breaksBlocks) { this(power, breaksBlocks, ANY_REACH, ANY_COUNT, ANY_RESISTANCE); }

        /** Whether anything but its force bounds what it breaks. */
        public boolean limited() { return breakRadius < ANY_REACH || maxBlocks < ANY_COUNT || maxResistance < ANY_RESISTANCE; }

        /** Whether it crushes blocks its force would not break. */
        public boolean crushes() { return breaksBlocks && crushHardness >= 0; }

        /** Whether it crushes a block this hard: one that can be broken at all, and no harder than it crushes. */
        public boolean crushes(double hardness) { return crushes() && hardness >= 0 && hardness <= crushHardness; }

        /** How far round it crushes, in blocks: its break radius, and {@link #CRUSH_REACH} at most. */
        public double crushReach() { return Math.min(breakRadius, CRUSH_REACH); }
    }

    /** Carries the caster where they are looking, up to this many blocks, stopping short of what is in the way. */
    record Teleport(double distance) implements SpellAction {}

    /** Tells the sky over the caster what to do for this long, in ticks. */
    record Weather(Sky sky, int ticks) implements SpellAction {}

    /** Quiets everything within {@code radius} blocks of the origin: it stops fighting and forgets its grudges. */
    record Pacify(double radius) implements SpellAction {}

    /** Puts this many of an item into the caster's hands, or at their feet if they have no room. */
    record GiveItem(String item, int count) implements SpellAction {}

    /**
     * Thrusts everything within {@code radius} blocks of the origin away from it, the caster left out, at
     * {@code strength} blocks a tick (a strength below none draws it in instead), lifting it {@code lift} blocks
     * a tick as well. Thrust {@link From#CASTER from the caster}, what a bolt's blast catches is driven away from
     * whoever cast it, wherever it stands about the blast, rather than flung out every way from where it struck.
     */
    record Push(double radius, double strength, double lift, From from) implements SpellAction {
        /** What a push thrusts away from: the origin (where a bolt struck, or the caster cast by hand), or the caster. */
        public enum From { ORIGIN, CASTER }

        /** A push away from the origin. */
        public Push(double radius, double strength, double lift) { this(radius, strength, lift, From.ORIGIN); }
    }

    /**
     * Shows something and does nothing: particles drawn about the origin in a shape, as far round as
     * {@code radius} and, for a column or a spiral, as high as {@code height}. Each of {@code particles} is drawn at
     * as many points of the shape as its count, each point scattered by its spread and drifting at its speed. What a
     * spell looks like is as much data as what it does.
     */
    record Spectacle(Shape shape, double radius, double height, List<Particles> particles) implements SpellAction {
        public Spectacle { particles = List.copyOf(particles); }
    }

    /**
     * Flies a spell bolt from the caster's eyes along their gaze: an entity, "corruption:spell_bolt" unless
     * another is named, that carries {@code onHit} and does it where it strikes. {@code speed} is in blocks a
     * tick, {@code inaccuracy} the spread in blocks, 0 for a true shot.
     *
     * <p>How the mod's own bolt shows itself is data too: {@code look} is what it is drawn as in flight, an item
     * whose model stands for it (a fire charge for a fireball, say) or "" for the corruption's crystal;
     * {@code trail} is what it gives off every tick of its flight, the corruption's own motes unless its spell
     * says otherwise (an empty trail leaves none); and {@code burst} is what bursts from where it strikes. Fire,
     * frost, wind or anything else a bolt should seem to be is a matter of which particles these name. Only the
     * mod's own bolt reads any of this; a bolt of the game's own, a small fireball, looks as the game draws it and
     * does what the game has it do, ignoring {@code onHit} — so a spell that must do something where it strikes
     * flies the mod's bolt.
     *
     * <p>A volley is {@code count} bolts at once. From the caster they fan out across {@code fan} degrees about
     * their gaze; {@link Source#SKY from the sky} they set off {@code height} blocks over the origin, scattered as
     * far round it as {@code fan} blocks, and fall on it — carried by a bolt, a rain of bolts on where it struck.
     *
     * <p>Drawn as the corruption's own (no {@code look}), the mod's bolt is a crystal in a shell of shards, ringed
     * with runes, trailing light: its light in {@code colour} (0xRRGGBB; -1 for the corruption's rose), and as big as
     * {@code size}, which is its box too, in blocks: a bigger bolt strikes creatures its line of flight passes the
     * further off, in proportion.
     */
    record LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit, String look,
                      List<Particles> trail, List<Particles> burst, int count, double fan, Source from, double height,
                      int colour, double size) implements SpellAction {
        /** What a bolt gives off in flight if its spell says nothing of it: the corruption's own motes. */
        public static final List<Particles> CORRUPTION_TRAIL = List.of(new Particles("corruption:corruption_particle", 1, 0, 0));
        /** How high over the origin bolts from the sky set off, unless the spell says otherwise. */
        public static final double SKY_HEIGHT = 12;
        /** How many bolts one volley may hold at most. */
        public static final int MAX_COUNT = 64;
        /** How big a bolt is, its box and its look, in blocks: unless its spell says otherwise; at the least; at the most. */
        public static final double DEFAULT_SIZE = .5, MIN_SIZE = .125, MAX_SIZE = 4;

        public LaunchBolt {
            onHit = List.copyOf(onHit);
            trail = List.copyOf(trail);
            burst = List.copyOf(burst);
            count = Math.max(1, count);
            fan = Math.max(0, fan);
            height = Math.max(0, height);
            colour = colour < 0 ? -1 : colour & 0xFFFFFF;
            size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, size));
        }

        /** A bolt of the corruption's rose, of the size bolts are unless their spell says otherwise. */
        public LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit, String look,
                          List<Particles> trail, List<Particles> burst, int count, double fan, Source from, double height) {
            this(entity, speed, inaccuracy, onHit, look, trail, burst, count, fan, from, height, -1, DEFAULT_SIZE);
        }

        /** One bolt from the caster, drawn as this and trailing and bursting into these. */
        public LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit, String look, List<Particles> trail, List<Particles> burst) {
            this(entity, speed, inaccuracy, onHit, look, trail, burst, 1, 0, Source.CASTER, SKY_HEIGHT);
        }

        /** A bolt drawn as this, trailing the corruption's motes and bursting into nothing. */
        public LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit, String look) {
            this(entity, speed, inaccuracy, onHit, look, CORRUPTION_TRAIL, List.of());
        }

        /** A bolt that looks like the corruption's own crystal. */
        public LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit) { this(entity, speed, inaccuracy, onHit, ""); }
    }

    /**
     * A beam, as an archmage fires one: it pours from the caster's hands along their gaze, and follows their gaze as
     * they turn, for {@code ticks}. It reaches {@code length} blocks, or to the first block in its way, and catches
     * whatever comes within {@code width} blocks of its line; every {@code interval} ticks it does {@code onHit} at
     * each creature it catches (never the caster), each pulse landing however close together they come. It is drawn
     * in {@code colour}, with {@code accent} for its white-hot core (0xRRGGBB), for exactly as long as it fires, so
     * the sight of it lasts until the spell ends; it shakes the view of whoever is near by {@code shake} (0 to 1)
     * while it fires, and sounds {@code sound} as it starts ("" for none). A beam always fires from the caster,
     * whatever the origin, even when a bolt carries it.
     */
    record Beam(double length, double width, int ticks, int interval, List<SpellAction> onHit, int colour, int accent, double shake, String sound)
            implements SpellAction {
        public Beam {
            length = Math.max(1, Math.min(MAX_RADIUS, length));
            width = Math.max(.1, width);
            ticks = Math.max(1, ticks);
            interval = Math.max(1, interval);
            onHit = List.copyOf(onHit);
            shake = Math.max(0, Math.min(1, shake));
        }
    }

    /**
     * Particles a bolt gives off, as its spell's data names them: which ({@code particle}, the game's id for it,
     * "minecraft:flame", "minecraft:snowflake", "minecraft:gust", "corruption:corruption_particle"), how many at a
     * time, how far round about they are scattered, in blocks, and how fast they drift off, in blocks a tick. A
     * particle that takes a colour, such as "minecraft:dust", takes {@code colour} (0xRRGGBB; -1 for none); one
     * that needs more than that to be drawn is passed over.
     */
    record Particles(String particle, int count, double spread, double speed, int colour) {
        public Particles {
            count = Math.max(0, count);
            spread = Math.max(0, spread);
            speed = Math.max(0, speed);
        }

        /** Particles of no colour of their own. */
        public Particles(String particle, int count, double spread, double speed) { this(particle, count, spread, speed, -1); }
    }
}
