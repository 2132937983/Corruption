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

    /** Corrupts the blocks around the origin within {@code radius}, each of them this much of the time. */
    record Corrupt(double radius, double chance) implements SpellAction {}

    /** Brings forth this many of a creature, scattered around the origin. */
    record Summon(String entity, int count) implements SpellAction {}

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

    /** Bursts about the origin with this much force, breaking blocks or not. */
    record Explode(double power, boolean breaksBlocks) implements SpellAction {}

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
     * a tick as well.
     */
    record Push(double radius, double strength, double lift) implements SpellAction {}

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
     */
    record LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit, String look,
                      List<Particles> trail, List<Particles> burst, int count, double fan, Source from, double height) implements SpellAction {
        /** What a bolt gives off in flight if its spell says nothing of it: the corruption's own motes. */
        public static final List<Particles> CORRUPTION_TRAIL = List.of(new Particles("corruption:corruption_particle", 1, 0, 0));
        /** How high over the origin bolts from the sky set off, unless the spell says otherwise. */
        public static final double SKY_HEIGHT = 12;

        public LaunchBolt {
            onHit = List.copyOf(onHit);
            trail = List.copyOf(trail);
            burst = List.copyOf(burst);
            count = Math.max(1, count);
            fan = Math.max(0, fan);
            height = Math.max(0, height);
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
