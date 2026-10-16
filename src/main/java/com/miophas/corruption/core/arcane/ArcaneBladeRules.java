package com.miophas.corruption.core.arcane;

/**
 * The corruption's blades, as rules.
 *
 * <p>A blade may bear the arcane's mark (the attack tag, corruption:arcane_weapons): a blow struck with it by hand
 * counts as the arcane's, so it reaches what only the arcane reaches, such as the Goetic demons, and the corruption's
 * creatures, whatever else they turn aside.
 *
 * <p>A blade may carry the corruption's curse (corruption:cursed_weapons): every blow struck with it lays the curse on
 * what it strikes, if that is not of the corruption (the curse does not touch the corruption's own); and the
 * corruption's lords will not be touched by the curse's own blade at all, mark or no mark.
 *
 * <p>The mod's two such blades: the Corruption Energy Sword, marked and clean; the Corrupted Arcane Sword, marked and
 * cursed, twice as heavy, for everything but the corruption's lords.
 */
public final class ArcaneBladeRules {
    /** How heavy a blow each sword strikes, all told (a bare hand strikes 1); and how fast either swings, a sword's pace. */
    public static final int ENERGY_SWORD_DAMAGE = 16, ARCANE_SWORD_DAMAGE = 32;
    public static final double SWORD_SPEED = 1.6;
    /** How long each lasts, and how readily either takes enchantments. */
    public static final int ENERGY_SWORD_DURABILITY = 2500, ARCANE_SWORD_DURABILITY = 3000, ENCHANTABILITY = 15;
    /**
     * The curse a cursed blade's blow lays: at its weakest, for a second and a half, so that it bites once (a seventh of
     * the creature's whole health and a little more). The curse takes a share of a creature's whole health each second
     * and starts its count again whenever it is laid again, so a long or strong one, laid by every blow, would kill
     * anything at all.
     */
    public static final int CURSE_TICKS = 30, CURSE_LEVEL = 0;

    /** Whether a blow carries the arcane's mark: one struck by hand with a marked blade. */
    public static boolean arcane(boolean marked, boolean byHand) { return marked && byHand; }

    /** Whether a blow is turned aside where it lands: a cursed blade's, on a lord of the corruption. */
    public static boolean refused(boolean cursed, boolean lord) { return cursed && lord; }

    /** Whether a blow lays the curse: a cursed blade's, on what is not of the corruption. */
    public static boolean curses(boolean cursed, boolean ofCorruption) { return cursed && !ofCorruption; }

    private ArcaneBladeRules() {}
}
