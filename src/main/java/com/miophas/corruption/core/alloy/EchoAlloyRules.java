package com.miophas.corruption.core.alloy;

/**
 * The corruption echo alloy's gear, as rules. The alloy is netherite's equal and a little more (its scrap and gold go
 * into it, with a resonant corruption crystal), so its tools last longer, dig faster and strike harder than
 * netherite's, and its armour guards as netherite's does. What sets the armour apart is the corruption's curse: each
 * piece turns aside a share of the curse's hurt, the whole set all of it; and nothing else of the corruption's (the
 * blows, spells and skills of its lords, its elites and its remnants are not the curse). The sword carries the curse
 * itself (the item tag corruption:cursed_weapons; see ArcaneBladeRules). Plain Java.
 */
public final class EchoAlloyRules {
    /** The tools' tier. Netherite's: 2031 uses, digging speed 9, +4 to a tool's blow, enchantability 15. */
    public static final int TOOL_DURABILITY = 2531;
    public static final float TOOL_SPEED = 10, ATTACK_BONUS = 5;
    public static final int TOOL_ENCHANTABILITY = 18;

    /** Each tool's own blow and swing, as vanilla gives a sword, a pickaxe and an axe theirs. */
    public static final float SWORD_DAMAGE = 3, SWORD_SWING = -2.4F, PICKAXE_DAMAGE = 1, PICKAXE_SWING = -2.8F, AXE_DAMAGE = 5, AXE_SWING = -3;
    /** A bare hand strikes for 1 already. */
    public static final float HAND = 1;

    /** The armour, helmet to boots: netherite's defence and toughness, a little more easily enchanted, a little longer lasting. */
    public static final int[] DEFENCE = {3, 8, 6, 3};
    public static final float TOUGHNESS = 3, KNOCKBACK_RESISTANCE = .1F;
    public static final int ARMOUR_ENCHANTABILITY = 18, ARMOUR_DURABILITY = 40;

    /** Each piece's share of the curse's hurt turned aside, helmet to boots, as its share of the set's defence: the set, all of it. */
    public static final double[] CURSE_SHARE = {.15, .4, .3, .15};

    /** What a tool strikes for, all told: a hand, the tier's bonus and its own. */
    public static float damage(float own) { return HAND + ATTACK_BONUS + own; }

    /** How many swings a second a tool of this swing makes (a hand makes four). */
    public static float swings(float swing) { return 4 + swing; }

    /** The pieces' slots, helmet to boots, by their index in DEFENCE and CURSE_SHARE. */
    public static int piece(String slot) {
        return switch (slot) {
            case "helmet" -> 0;
            case "chestplate" -> 1;
            case "leggings" -> 2;
            case "boots" -> 3;
            default -> throw new IllegalArgumentException("no such piece: " + slot);
        };
    }

    private EchoAlloyRules() {}
}
