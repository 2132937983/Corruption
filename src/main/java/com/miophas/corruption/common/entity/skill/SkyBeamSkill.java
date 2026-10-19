package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SkyBeamRules;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Beams driven straight down out of magic circles in the sky ({@link SkyBeamRules}), a circle for every beam: as it begins,
 * the circles open high over the ground where the quarry stands (one over it, or a star's at its points) and each beam's
 * first strike is marked under its circle. When the warning is over the beams pour down, a star's turning and closing in
 * on the mark, and every pulse hurts whatever stands in one of them, from the ground up to its circle, through anything over
 * it. Each is hurt once a pulse, however many beams it stands in, and its hurt cooldown does not shield it. The effect
 * ({@link #effect}) draws the circles and beams where the same rules put them. Any boss may carry it.
 */
public final class SkyBeamSkill implements RegisteredSkill<SkyBeamRules> {
    private final SkyBeamRules rules;
    private final int colour, accent;
    @Nullable private Vec3 mark;
    private double turn;
    private final List<LivingEntity> struck = new ArrayList<>();

    public SkyBeamSkill(SkyBeamRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public SkyBeamRules rules() { return rules; }
    /** The ground its beams strike about (the middle of a star), once it has begun. */
    @Nullable public Vec3 mark() { return mark; }
    /** How far its star was turned to begin with, in degrees. */
    public double turn() { return turn; }
    /** What its beams have caught, this use. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    /**
     * The effect that draws beams this many (SpellVisuals): {@code sky_beam}, {@code pentagram_beams}, {@code hexagram_beams}
     * or {@code octagram_beams}. Its radius is a star's spread, or a single beam's width; its height, the circles' altitude.
     */
    public static String effect(int beams) {
        return switch (beams) {
            case 5 -> "pentagram_beams";
            case 6 -> "hexagram_beams";
            case 8 -> "octagram_beams";
            default -> "sky_beam";
        };
    }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        struck.clear();
        turn = boss.getRandom().nextDouble() * 360;
        if (!(boss.level() instanceof ServerLevel level)) {
            mark = target.position();
            return;
        }
        mark = SkillShows.ground(level, target.position());
        SkillShows.show(level, mark, effect(rules.beams()), rules.beams() == 1 ? rules.width() : rules.spread(), rules.altitude(), rules.timing().ticks(),
                colour, accent, .15, (float) turn, false);
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_ATTACK, 3, .5F);
        SkillShows.sound(level, mark.add(0, rules.altitude(), 0), SoundEvents.BEACON_ACTIVATE, 4, .6F);
        SkillShows.sound(level, mark, SoundEvents.LIGHTNING_BOLT_THUNDER, 1.5F, 1.4F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (mark == null || !(boss.level() instanceof ServerLevel level)) return;
        if (rules.timing().strikes(elapsed)) {
            SkillShows.sound(level, mark, SoundEvents.LIGHTNING_BOLT_IMPACT, 4, .7F);
            SkillShows.sound(level, mark.add(0, rules.altitude(), 0), SoundEvents.BEACON_POWER_SELECT, 4, .5F);
        }
        if (rules.pulses(elapsed)) pulse(level, boss, elapsed - rules.timing().windup());
    }

    /** One pulse: whatever stands in a beam, from the ground under it up to its circle, is hurt once. */
    private void pulse(ServerLevel level, Mob boss, int since) {
        double[][] points = rules.points(turn, since);
        double bottom = mark.y - 1, top = mark.y + rules.altitude(), reach = rules.spread() + rules.width() + 2;
        DamageSource hurt = ModDamageTypes.corruption(level, boss);
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(mark.x - reach, bottom, mark.z - reach, mark.x + reach, top, mark.z + reach),
                near -> near != boss && near.isAlive() && !boss.isAlliedTo(near))) {
            for (double[] point : points) {
                if (!rules.catches(Math.hypot(near.getX() - mark.x - point[0], near.getZ() - mark.z - point[1]), near.getBbWidth() / 2)) continue;
                near.invulnerableTime = 0;
                if (near.hurt(hurt, (float) rules.damage()) && !struck.contains(near)) struck.add(near);
                break;
            }
        }
        // Sparks over the ground each strikes, as many as it is wide.
        int sparks = 6 + (int) (rules.width() * rules.width() / 2);
        for (double[] point : points)
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, mark.x + point[0], mark.y + .3, mark.z + point[1], sparks, rules.width() / 2, .2, rules.width() / 2, .3);
    }

    @Override public void end(Mob boss, boolean broken) { mark = null; }
    @Override public BossSkillRegistry.Type<SkyBeamRules> type() { return BossSkillApi.SKY_BEAMS; }
    @Override public SkyBeamRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
