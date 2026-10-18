package com.miophas.corruption.core.corruption;

/**
 * What a worn thing does to the hurt of the corruption's curse, as the echo alloy's armour does: it turns aside a share
 * of every pulse, from none to all. This is plain Java; the equipment itself is api.corruption.CurseWardItem.
 *
 * <p>Everything worn counts together: the shares are added up, never past all of it. It bears on the curse's own hurt
 * alone (CorruptionCurseApi.isCurse), not on the rest of the corruption's: its ground, its spells, the blows and skills
 * of its creatures.
 */
public record CurseWard(double share) {
    public static final CurseWard NONE = new CurseWard(0);

    public CurseWard { share = Math.max(0, Math.min(1, share)); }

    /** How everything worn bears on the curse together: its shares added up, and held between none and all. */
    public static double total(Iterable<CurseWard> worn) {
        double sum = 0;
        for (CurseWard ward : worn) sum += ward.share();
        return Math.max(0, Math.min(1, sum));
    }

    /** How much of a pulse of the curse gets through attire that turns aside this much of it. */
    public static float taken(float amount, double total) {
        if (amount <= 0) return 0;
        return (float) (amount * (1 - Math.max(0, Math.min(1, total))));
    }
}
