package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.FieryChariot;
import com.miophas.corruption.core.demon.skill.ChariotRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A chariot of fire ({@link ChariotRules}) as the mod carries it out for any boss. As it begins, the line the chariot
 * will run is fixed (from the boss's side of its quarry, turned a little either way, through where the quarry stands)
 * and marked: a magic circle at each end, and flames along the ground between, so that what stands on it can see it
 * coming and get out of its way. When the wind-up is over the chariot ({@link FieryChariot}) is there at the start of the
 * line, and runs it on its own, whatever the boss does next.
 */
public final class ChariotSkill implements BossSkill<Mob> {
    /** How far apart the flames marking its line are, in blocks. */
    private static final double MARKS = 3;

    private final ChariotRules rules;
    private final int colour, accent;
    @Nullable private Vec3 start, end;
    @Nullable private FieryChariot driven;

    public ChariotSkill(ChariotRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public ChariotRules rules() { return rules; }

    /** The line this use's chariot runs, once it has begun: {start, end}. */
    @Nullable public Vec3[] line() { return start == null ? null : new Vec3[]{start, end}; }

    /** The chariot this use set going, once it has. */
    @Nullable public FieryChariot chariot() { return driven; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        driven = null;
        double turn = (boss.getRandom().nextDouble() - .5) * .8;
        double[] path = rules.path(target.getX(), target.getZ(), boss.getX(), boss.getZ(), turn);
        double y = target.getY();
        start = new Vec3(path[0], y, path[1]);
        end = new Vec3(path[2], y, path[3]);
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_ATTACK, 2.5F, .5F);
        if (!(boss.level() instanceof ServerLevel level)) return;
        start = SkillShows.ground(level, start);
        end = new Vec3(end.x, start.y, end.z);
        float yaw = (float) (Mth.atan2(end.z - start.z, end.x - start.x) * Mth.RAD_TO_DEG) - 90;
        int lasts = rules.timing().windup() + rules.runTicks() + 10;
        SkillShows.show(level, start, "magic_circle", rules.reach() + 1.5, 1, lasts, colour, accent, 0, yaw, true);
        SkillShows.show(level, end, "magic_circle", rules.reach() + .5, 1, lasts, colour, accent, 0, yaw, true);
        double length = start.distanceTo(end);
        for (double along = MARKS; along < length; along += MARKS)
            SkillShows.show(level, start.lerp(end, along / length), "hellfire", .8, .6, lasts, colour, accent, 0, yaw, true);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (start == null || !rules.timing().strikes(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        driven = FieryChariot.drive(level, boss, start, end, rules, colour, accent);
    }
}
