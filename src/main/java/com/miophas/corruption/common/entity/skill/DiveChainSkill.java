package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.core.demon.skill.DiveChainRules;
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
 * Dives one after another ({@link DiveChainRules}) as the mod carries them out for any flying boss: each as a dive is
 * ({@link DiveSkill}), on the place its quarry stands as that dive begins, marked on the ground; the boss climbs, dives,
 * strikes, and at once climbs for the next. A dive that has not come down in its time is given up and the next begun.
 * It is over as soon as the last has struck. While it runs, it moves the boss itself.
 */
public final class DiveChainSkill implements RegisteredSkill<DiveChainRules> {
    /** How near the mark the boss must come for a dive to strike there, in blocks. */
    private static final double THERE = 1.2;

    private final DiveChainRules rules;
    private final int colour, accent;
    private Vec3 aim;
    private boolean climbing, done;
    private int dive, ticks, struck;

    public DiveChainSkill(DiveChainRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public DiveChainRules rules() { return rules; }

    /** How many of this use's dives have struck. */
    public int struck() { return struck; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) {
        return rules.serves(boss.distanceTo(target), boss.getY() - target.getY()) && boss.hasLineOfSight(target);
    }

    @Override public void begin(Mob boss, LivingEntity target) {
        dive = 0;
        struck = 0;
        done = false;
        climb(boss, target);
    }

    /** Climbs for the next dive, its place fixed where the quarry stands now (or where the last was, with no quarry) and marked. */
    private void climb(Mob boss, @Nullable LivingEntity target) {
        climbing = true;
        ticks = 0;
        if (target != null) aim = boss.level() instanceof ServerLevel level ? SkillShows.ground(level, target.position()) : target.position();
        if (aim != null && boss.level() instanceof ServerLevel level)
            SkillShows.show(level, aim, "magic_circle", rules.dive().radius(), .6, rules.climb() + rules.most() + 10, colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(boss, SoundEvents.PHANTOM_SWOOP, 3, .7F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (done || aim == null) return;
        ticks++;
        if (climbing) {
            boss.setDeltaMovement(new Vec3(0, .14, 0));
            MeleeSkill.face(boss, aim);
            if (ticks >= rules.climb()) {
                climbing = false;
                ticks = 0;
                SkillShows.sound(boss, SoundEvents.ENDER_DRAGON_FLAP, 3, .6F);
            }
            return;
        }
        Vec3 way = aim.subtract(boss.position());
        boolean there = way.length() <= THERE || boss.onGround() || boss.verticalCollision;
        if (there || ticks >= rules.most()) {
            if (there && boss.level() instanceof ServerLevel level) {
                DiveSkill.land(level, boss, rules.dive(), colour, accent);
                struck++;
            }
            if (++dive >= rules.dives()) done = true;
            else climb(boss, target);
            return;
        }
        Vec3 diving = way.normalize().scale(Math.min(rules.dive().speed(), way.length()));
        boss.setDeltaMovement(diving);
        boss.hurtMarked = true;
        MeleeSkill.face(boss, aim);
        boss.setXRot((float) Mth.clamp(-Mth.atan2(diving.y, diving.horizontalDistance()) * Mth.RAD_TO_DEG, -90, 90));
        if (boss.level() instanceof ServerLevel level && ticks % 2 == 0)
            level.sendParticles(ParticleTypes.CLOUD, boss.getX(), boss.getY() + boss.getBbHeight() / 2, boss.getZ(), 3, .4, .4, .4, .02);
    }

    @Override public boolean finished(Mob boss) { return done; }

    @Override public void end(Mob boss, boolean broken) {
        aim = null;
        boss.setXRot(0);
    }
    @Override public BossSkillRegistry.Type<DiveChainRules> type() { return BossSkillApi.DIVE_CHAIN; }
    @Override public DiveChainRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
