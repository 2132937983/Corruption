package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.DemonBolt;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A star's volley ({@link SpinVolleyRules}) as the mod carries it out for any boss with points. Through the wind-up it
 * spins up where it hangs, facing its quarry, glittering; then on every beat each of its points looses a bolt of the
 * corruption ({@link DemonBolt}) from its tip at the quarry, as the points wheel round. The points stand evenly round a
 * circle as wide as the boss, in the plane square to the line from its heart to its quarry's.
 */
public final class SpinVolleySkill implements BossSkill<Mob> {
    private final SpinVolleyRules rules;
    private final int colour, accent;
    private int beat, loosed;

    public SpinVolleySkill(SpinVolleyRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public SpinVolleyRules rules() { return rules; }

    /** How many bolts this use has loosed so far. */
    public int loosed() { return loosed; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        beat = 0;
        loosed = 0;
        SkillShows.sound(boss, SoundEvents.AMETHYST_BLOCK_RESONATE, 2, 1.4F);
        if (boss.level() instanceof ServerLevel level) {
            Vec3 heart = boss.getBoundingBox().getCenter();
            level.sendParticles(ParticleTypes.END_ROD, heart.x, heart.y, heart.z, 24, boss.getBbWidth() / 2, boss.getBbHeight() / 2, boss.getBbWidth() / 2, .05);
            SkillShows.show(level, heart, "rays", boss.getBbWidth() * 1.2, boss.getBbWidth(), rules.timing().windup() + 6, colour, accent, 0, boss.getYRot(), false);
        }
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (target != null) MeleeSkill.face(boss, target.position());
        if (!rules.firing(elapsed) || target == null || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 heart = boss.getBoundingBox().getCenter(), aim = target.getBoundingBox().getCenter();
        Vec3 forward = aim.subtract(heart);
        if (forward.lengthSqr() < 1e-6) return;
        forward = forward.normalize();
        Vec3 right = forward.cross(new Vec3(0, 1, 0));
        right = right.lengthSqr() < 1e-6 ? new Vec3(1, 0, 0) : right.normalize();
        Vec3 up = right.cross(forward).normalize();
        double radius = boss.getBbWidth() * .5, turned = rules.turned(elapsed);
        for (int point = 0; point < rules.points(); point++) {
            double[] tip = SpinVolleyRules.tip(rules.pointAt(point, turned)), off = rules.scatter(beat, point);
            Vec3 from = heart.add(right.scale(tip[0] * radius)).add(up.scale(tip[1] * radius));
            double reach = from.distanceTo(aim);
            Vec3 at = aim.add(right.scale(Math.tan(Math.toRadians(off[0])) * reach)).add(up.scale(Math.tan(Math.toRadians(off[1])) * reach));
            if (DemonBolt.loose(level, boss, from, at.subtract(from).normalize().scale(rules.speed()), rules.damage(), 0, 0, colour) != null) loosed++;
            level.sendParticles(ParticleTypes.END_ROD, from.x, from.y, from.z, 2, .05, .05, .05, .02);
        }
        beat++;
        SkillShows.sound(boss, SoundEvents.AMETHYST_BLOCK_CHIME, 1.6F, 1.3F + boss.getRandom().nextFloat() * .4F);
    }
}
