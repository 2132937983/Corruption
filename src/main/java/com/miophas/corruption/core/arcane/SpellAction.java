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

    /** Strikes this many bolts of lightning about the origin, within {@code radius} blocks of it. */
    record Lightning(double radius, int count) implements SpellAction {}

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
     * Flies a spell bolt from the caster's eyes along their gaze: an entity, "corruption:spell_bolt" unless
     * another is named, that carries {@code onHit} and does it where it strikes. {@code speed} is in blocks a
     * tick, {@code inaccuracy} the spread in blocks, 0 for a true shot.
     *
     * <p>{@code look} is what the mod's own bolt is drawn as in flight: an item whose model stands for it, a
     * fire charge for a fireball say, or "" for the corruption's crystal. Only the mod's own bolt reads it; a
     * bolt of the game's own, a small fireball, looks as the game draws it and does what the game has it do,
     * ignoring {@code onHit} — so a spell that must do something where it strikes flies the mod's bolt.
     */
    record LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit, String look) implements SpellAction {
        /** A bolt that looks like the corruption's own crystal. */
        public LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit) { this(entity, speed, inaccuracy, onHit, ""); }
    }
}
