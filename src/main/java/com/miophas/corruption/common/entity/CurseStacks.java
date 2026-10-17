package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.core.corruption.CurseStackRules;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The corruption's curse laid on deeper by every blow ({@link CurseStackRules}): longer each time, and harder once it has
 * long enough to run. Never on the corruption's own, nor on a player in creative or spectator.
 */
public final class CurseStacks {
    /** Lays the curse on this creature once more, as the rules say, by whoever laid it; whether it took hold. */
    public static boolean deepen(LivingEntity target, @Nullable Entity by, CurseStackRules rules) {
        if (!target.isAlive() || CorruptionFactionApi.isRemnant(target) || target instanceof Player player && (player.isCreative() || player.isSpectator()))
            return false;
        MobEffectInstance had = target.getEffect(ModEffects.CORRUPTION_CURSE);
        int[] next = rules.next(had != null, had == null ? 0 : had.getDuration(), had == null ? 0 : had.getAmplifier());
        if (had != null && next[0] == had.getDuration() && next[1] == had.getAmplifier()) return true;
        // A weaker curse cannot take the place of a stronger one by the game's own rule, and a curse no stronger and no
        // longer is turned away; the one laid here is always at least as strong and as long as what it had.
        if (had != null) target.removeEffect(ModEffects.CORRUPTION_CURSE);
        return target.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, next[0], next[1]), by);
    }

    /** The curse laid on so many times over at once. */
    public static boolean deepen(LivingEntity target, @Nullable Entity by, CurseStackRules rules, int times) {
        boolean took = false;
        for (int time = 0; time < times; time++) took |= deepen(target, by, rules);
        return took;
    }

    private CurseStacks() {}
}
