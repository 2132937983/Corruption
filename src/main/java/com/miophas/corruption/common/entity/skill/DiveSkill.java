package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A dive out of the sky ({@link DiveRules}) as the mod carries it out for any flying boss. As it begins, the place its
 * quarry stands on is fixed and marked on the ground with a circle in the skill's colour; through the wind-up the boss
 * climbs, facing it; then it dives straight at it. Where it strikes the ground (or anything else that stops it, or once
 * it is there) everything near is hurt and thrown off, the ground is torn up ({@link Debris}), and a ring of force runs
 * out over it. A boss diving holds its own gravity off; what lands it afterwards is its own to say.
 */
public final class DiveSkill implements BossSkill<Mob> {
    /** How near the mark the boss must come for the dive to strike there, in blocks. */
    private static final double THERE = 1.2;

    private final DiveRules rules;
    private final int colour, accent;
    /** Whether the dive is over as soon as it strikes, the boss away again at once, rather than running out its time. */
    private final boolean upAtOnce;
    private Vec3 aim;
    private boolean struck;
    private int torn;

    public DiveSkill(DiveRules rules, int colour, int accent) { this(rules, colour, accent, false); }

    /** @param upAtOnce whether it is over as soon as it strikes (no rest after it) */
    public DiveSkill(DiveRules rules, int colour, int accent, boolean upAtOnce) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
        this.upAtOnce = upAtOnce;
    }

    public DiveRules rules() { return rules; }

    /** Where this use dives, if it has begun. */
    @Nullable public Vec3 aim() { return aim; }

    /** Whether this use has struck, and how many blocks it tore up. */
    public boolean struck() { return struck; }
    public int torn() { return torn; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) {
        return rules.serves(boss.distanceTo(target), boss.getY() - target.getY()) && boss.hasLineOfSight(target);
    }

    @Override public void begin(Mob boss, LivingEntity target) {
        struck = false;
        torn = 0;
        if (!(boss.level() instanceof ServerLevel level)) { aim = target.position(); return; }
        aim = SkillShows.ground(level, target.position());
        SkillShows.show(level, aim, "magic_circle", rules.radius(), .6, rules.timing().windup() + 30, colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(boss, SoundEvents.PHANTOM_SWOOP, 3, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (aim == null) return;
        if (rules.timing().windingUp(elapsed)) {
            boss.setDeltaMovement(new Vec3(0, .12, 0));
            MeleeSkill.face(boss, aim);
            return;
        }
        if (struck) { boss.setDeltaMovement(boss.getDeltaMovement().multiply(.5, 1, .5)); return; }
        if (rules.timing().strikes(elapsed)) SkillShows.sound(boss, SoundEvents.ENDER_DRAGON_FLAP, 3, .5F);
        Vec3 way = aim.subtract(boss.position());
        if (way.length() <= THERE || boss.onGround() || boss.verticalCollision && elapsed > rules.timing().windup()) {
            if (boss.level() instanceof ServerLevel level) strike(level, boss);
            return;
        }
        Vec3 diving = way.normalize().scale(Math.min(rules.speed(), way.length()));
        boss.setDeltaMovement(diving);
        boss.hurtMarked = true;
        MeleeSkill.face(boss, aim);
        boss.setXRot((float) Mth.clamp(-Mth.atan2(diving.y, diving.horizontalDistance()) * Mth.RAD_TO_DEG, -90, 90));
        if (boss.level() instanceof ServerLevel level && elapsed % 2 == 0)
            level.sendParticles(ParticleTypes.CLOUD, boss.getX(), boss.getY() + boss.getBbHeight() / 2, boss.getZ(), 4, .5, .5, .5, .02);
    }

    @Override public boolean finished(Mob boss) { return upAtOnce && struck; }

    @Override public void end(Mob boss, boolean broken) {
        aim = null;
        boss.setXRot(0);
    }

    /** It strikes: everything near hurt and thrown up and off, the ground torn up, a ring of force over it. */
    private void strike(ServerLevel level, Mob boss) {
        struck = true;
        torn = land(level, boss, rules, colour, accent);
    }

    /**
     * A dive strikes where its boss is: everything within its radius hurt and thrown up and off, the ground torn up
     * ({@link Debris}), a ring of force over it; how many blocks it tore up. Dives one after another strike so too.
     */
    static int land(ServerLevel level, Mob boss, DiveRules rules, int colour, int accent) {
        Vec3 at = boss.position();
        boss.setDeltaMovement(Vec3.ZERO);
        boss.setXRot(0);
        for (LivingEntity met : level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(rules.radius()), met -> met != boss && met.isAlive()
                && !boss.isAlliedTo(met) && met.getBoundingBox().distanceToSqr(at) <= rules.radius() * rules.radius())) {
            met.invulnerableTime = 0;
            if (!met.hurt(boss.damageSources().mobAttack(boss), (float) rules.damage())) continue;
            Vec3 away = met.position().subtract(at).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? new Vec3(1, 0, 0) : away.normalize();
            met.push(away.x * rules.knockback(), .6, away.z * rules.knockback());
            met.hurtMarked = true;
        }
        int torn = rules.debrisMost() > 0 ? Debris.tear(level, boss, at.add(0, -.5, 0), rules) : 0;
        SkillShows.show(level, at, "shockwave", rules.radius() * 2.2, 2, 22, colour, accent, .6, boss.getYRot(), true);
        SkillShows.show(level, at, "fissure", rules.radius() + 2, 1.5, 40, colour, accent, 0, boss.getYRot(), true);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + .5, at.z, 1, 0, 0, 0, 0);
        SkillShows.sound(boss, SoundEvents.GENERIC_EXPLODE.value(), 3, .7F);
        return torn;
    }
}
