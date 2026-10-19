package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.entity.DemonBolt;
import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A rain of the corruption's bolts ({@link BarrageRules}) as the mod carries it out for any boss. As it begins, the place
 * its quarry stands is fixed and marked on the ground with a circle as wide as the rain; through the wind-up the boss
 * gathers itself, facing it; then it looses the bolts ({@link DemonBolt}), a few a tick from either side of it by turns
 * (a bird's two wings), each thrown up and over to come down on its own place within the mark.
 */
public final class BarrageSkill implements RegisteredSkill<BarrageRules> {
    private final BarrageRules rules;
    private final int colour, accent;
    private Vec3 mark;
    private double turn;
    private int loosed;

    public BarrageSkill(BarrageRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public BarrageRules rules() { return rules; }

    /** Where this use's bolts come down, if it has begun; and how many it has loosed. */
    @Nullable public Vec3 mark() { return mark; }
    public int loosed() { return loosed; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        loosed = 0;
        turn = boss.getRandom().nextDouble() * Math.PI * 2;
        if (!(boss.level() instanceof ServerLevel level)) { mark = target.position(); return; }
        mark = SkillShows.ground(level, target.position());
        SkillShows.show(level, mark, "magic_circle", rules.radius(), .6, rules.timing().ticks() + rules.flight(), colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(boss, SoundEvents.ENDER_DRAGON_FLAP, 3, .8F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (mark == null) return;
        MeleeSkill.face(boss, mark);
        boss.setDeltaMovement(boss.getDeltaMovement().scale(.7));
        int[] now = rules.loosed(elapsed);
        if (now[1] == 0 || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 heart = boss.getBoundingBox().getCenter(), right = Vec3.directionFromRotation(0, boss.getYRot() + 90);
        for (int index = now[0]; index < now[0] + now[1]; index++) {
            double[] at = rules.landing(index, turn);
            Vec3 land = SkillShows.ground(level, mark.add(at[0], 0, at[1]));
            Vec3 from = heart.add(right.scale((index % 2 == 0 ? 1 : -1) * boss.getBbWidth() * .6)).add(0, .4, 0);
            double[] throwing = rules.launch(land.x - from.x, land.y - from.y, land.z - from.z);
            if (DemonBolt.loose(level, boss, from, new Vec3(throwing[0], throwing[1], throwing[2]), rules.damage(), rules.splash(), BarrageRules.GRAVITY, colour) != null)
                loosed++;
        }
        if (elapsed % 2 == 0) SkillShows.sound(boss, SoundEvents.ENDER_DRAGON_FLAP, 2, 1.2F);
    }

    @Override public void end(Mob boss, boolean broken) { mark = null; }
    @Override public BossSkillRegistry.Type<BarrageRules> type() { return BossSkillApi.BARRAGE; }
    @Override public BarrageRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
