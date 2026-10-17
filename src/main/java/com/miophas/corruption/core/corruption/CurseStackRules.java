package com.miophas.corruption.core.corruption;

/**
 * The corruption's curse laid on again and again, as rules: each time it is laid on, it runs longer, and the longer it
 * has to run the harder it bites. A creature not cursed yet is cursed for {@code first} ticks; one cursed already has
 * {@code add} more ticks on what it has left, up to {@code most} (a longer curse it has from elsewhere is left as long as
 * it is). Its strength (the effect's amplifier: 0 for Curse I) is one step more for every {@code stronger} ticks it has to
 * run beyond the {@code first}, {@code strongest} at the most, and never less than it was. A curse that never ends is
 * left as it is.
 *
 * <p>The curse bites when what it has left is a whole number of seconds. So {@code add} is kept a whole number of seconds,
 * that the bites go on as they were (none comes at once for being laid on again); and {@code first} is best not one, since
 * a curse of whole seconds bites the moment it is laid.
 */
public record CurseStackRules(int first, int add, int most, int stronger, int strongest) {
    public CurseStackRules {
        first = Math.max(1, first);
        add = Math.max(20, Math.round(add / 20F) * 20);
        most = Math.max(first, most);
        stronger = Math.max(1, stronger);
        strongest = Math.max(0, strongest);
    }

    /**
     * The curse after it is laid on once more, as {ticks, amplifier}: on a creature not cursed yet, or on one with this
     * much left of a curse of this amplifier.
     */
    public int[] next(boolean cursed, int remaining, int amplifier) {
        if (cursed && remaining == CorruptionCurseRules.ENDLESS) return new int[]{remaining, amplifier};
        int ticks = !cursed || remaining <= 0 ? first : Math.max(remaining, Math.min(most, remaining + add));
        int level = Math.min(strongest, Math.max(0, ticks - first) / stronger);
        return new int[]{ticks, Math.max(cursed ? amplifier : 0, level)};
    }
}
