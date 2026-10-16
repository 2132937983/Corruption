package com.miophas.corruption.api.demon.skill;

import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * One of a boss's skills, as a module any boss may carry: how it runs in time, whether it would serve against a quarry
 * now, and what it does tick by tick. A boss keeps its skills in a {@link SkillSet}, which counts their cooldowns down
 * and runs one at a time; the mod's own are made by {@link BossSkillApi} from the rules in core.demon.skill, where
 * every number of them is, and an add-on may write its own.
 *
 * <p>A module may keep what one use of it needs (the line a charge runs along, the places a storm's bolts will fall),
 * so each boss carries modules of its own.
 *
 * @param <E> the kind of boss it is for; the mod's own serve any mob
 */
public interface BossSkill<E extends Mob> {
    /** How it runs in time: its wind-up, how long it runs, its cooldown. */
    SkillTiming timing();

    /** Whether it would serve against this quarry now: in reach, in sight, and the like. Whether it has come round is the set's to say. */
    boolean usable(E boss, LivingEntity target);

    /** It begins: whatever shows that it is coming. */
    default void begin(E boss, LivingEntity target) {}

    /**
     * One tick of it, counted from 0 (the tick it began) to {@code timing().ticks() - 1}; it takes effect at the end of
     * its wind-up. Its quarry may be gone by now (null), and it carries on as best it can.
     */
    void tick(E boss, @Nullable LivingEntity target, int elapsed);

    /** It is over: run its course, or broken off ({@code broken}: its boss died, changed, or was taken away). */
    default void end(E boss, boolean broken) {}

    /**
     * Every tick, whether it runs or not: for what a use of it leaves behind that outlasts its run, to keep up or let
     * go of. A mind taken is held on after the taking, while its boss does other things. Most skills leave nothing.
     */
    default void linger(E boss, @Nullable LivingEntity target) {}

    /** Its boss is done with it for good (it died, or is gone): whatever it still holds is let go. */
    default void cease(E boss) {}
}
