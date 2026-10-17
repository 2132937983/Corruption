package com.miophas.corruption.core.corruption;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The corrupted robes, as rules: the robe dyed in the corruption, in three grades, worn as a set of four (hood, robe,
 * leggings, boots). Worn whole, a set veils its wearer from the corruption's creatures, so that they do not take it
 * for prey, and holds its wearer's corruption fast, no lower than the grade's threshold, so that nothing raises or
 * lowers it;
 * the higher grades also turn aside the corruption's own hurt, half of it and then all of it, and veil their wearer from
 * the corruption's elites, which see through a novice's robes; and the highest veils its wearer from the corruption's
 * lords as well, so that they do not turn on it even when it strikes them.
 *
 * <p>A set is only as good as its worst piece: four pieces of mixed grades are a set of the lowest of them, and three
 * pieces with anything else in the fourth slot are no set at all.
 *
 * <p>The hold: when a set is put on, the wearer's corruption as it was is kept. A corruption below the grade's
 * threshold is raised to it; one at or above it is left as it is; and the set holds it there, unchanged, while it is
 * worn. A change to a grade with a higher threshold raises it again, if it is below that; a change to a lower one never
 * lowers it. When the set comes off, the corruption kept is given back. So a robe never lowers a wearer's corruption,
 * and is no way to be rid of it nor to gain it: it is a mask over it. Death takes all of a player's corruption, and what
 * a robe kept of it with it (PlayerCorruption), so nothing is given back after death.
 */
public final class CorruptedRobeRules {
    /** How many pieces make a set. */
    public static final int PIECES = 4;
    /** Nothing kept, nothing held: no robe holds the wearer's corruption. */
    public static final int NOTHING_KEPT = -1;

    /** The three grades. */
    public enum Tier {
        NOVICE(5, 1, false, false), INTERMEDIATE(12, .5, true, false), ADVANCED(19, 0, true, true);

        private final int threshold;
        private final double corruptionDamageTaken;
        private final boolean hiddenFromElites, hiddenFromBosses;

        Tier(int threshold, double corruptionDamageTaken, boolean hiddenFromElites, boolean hiddenFromBosses) {
            this.threshold = threshold;
            this.corruptionDamageTaken = corruptionDamageTaken;
            this.hiddenFromElites = hiddenFromElites;
            this.hiddenFromBosses = hiddenFromBosses;
        }

        /** The corruption this grade holds its wearer at the least: a wearer with less is raised to it, one with more held where they are. */
        public int threshold() { return threshold; }

        /** How much of the corruption's own hurt still reaches its wearer: all of it, half, or none. */
        public double corruptionDamageTaken() { return corruptionDamageTaken; }

        /** Whether it veils its wearer from the corruption's creatures: every grade does. */
        public boolean hiddenFromRemnants() { return true; }

        /** Whether it veils its wearer from the corruption's elites, which see through a novice's robes: every grade above it. */
        public boolean hiddenFromElites() { return hiddenFromElites; }

        /** Whether it veils its wearer from the corruption's lords too: only the highest. */
        public boolean hiddenFromBosses() { return hiddenFromBosses; }

        /** How much of a hurt of the corruption's this much reaches its wearer. */
        public float corruptionDamage(float amount) { return (float) (amount * corruptionDamageTaken); }
    }

    /**
     * The grade a wearer's four armour slots make together, each null where it holds no corrupted robe: none unless all
     * four hold one; the lowest of them if they do.
     */
    public static Optional<Tier> worn(List<Tier> pieces) {
        if (pieces.size() != PIECES || pieces.stream().anyMatch(Objects::isNull)) return Optional.empty();
        return pieces.stream().min(Enum::compareTo);
    }

    /**
     * A wearer's corruption; what it was before a robe took hold of it, to be given back when the robe comes off; and
     * what the robe holds it at. Both are {@link #NOTHING_KEPT} while no robe holds it.
     */
    public record Hold(int corruption, int kept, int held) {
        public boolean holding() { return kept != NOTHING_KEPT; }
    }

    /**
     * The hold, one moment on: with the grade worn now (null for none), from the wearer's corruption, what was kept of
     * it and what it is held at. Taking hold, the corruption as it was is kept, and held at it or at the grade's
     * threshold, whichever is higher. Holding, it is put back where it is held, whatever moved it, and a grade with a
     * higher threshold raises it there; a lower one never lowers it. Letting go, what was kept is given back.
     */
    public static Hold step(int corruption, int kept, int held, Tier worn) {
        if (worn == null) return new Hold(kept == NOTHING_KEPT ? corruption : kept, NOTHING_KEPT, NOTHING_KEPT);
        boolean taking = kept == NOTHING_KEPT;
        int at = Math.max(taking || held == NOTHING_KEPT ? corruption : held, worn.threshold());
        return new Hold(at, taking ? corruption : kept, at);
    }

    private CorruptedRobeRules() {}
}
