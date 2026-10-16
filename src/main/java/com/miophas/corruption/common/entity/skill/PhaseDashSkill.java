package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A run of blinks ({@link PhaseDashRules}) as the mod carries it out for any boss. Through the wind-up it gathers itself,
 * the air shimmering about it; then, a blink at a time, it is gone and there: on the far side of its quarry, through it,
 * set down on the ground where it fits, a shower of shards where it left and a rift where it came. Everything near the
 * line it blinked along is hurt with the corruption's own hurt and laid under its curse, each creature by so many of the
 * blinks of a run as the rules let and no more. A blink with nowhere to land is tried the other way round, then half as
 * far, and else not made.
 */
public final class PhaseDashSkill implements BossSkill<Mob> {
    private final PhaseDashRules rules;
    private final int colour, accent;
    private int made;
    /** How many times this use has hurt each creature, by its id. */
    private final Map<UUID, Integer> hurt = new HashMap<>();

    public PhaseDashSkill(PhaseDashRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public PhaseDashRules rules() { return rules; }

    /** How many blinks this use has made so far. */
    public int made() { return made; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        made = 0;
        hurt.clear();
        boss.getNavigation().stop();
        SkillShows.sound(boss, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 2, 1.3F);
        if (boss.level() instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, boss.getX(), boss.getY() + boss.getBbHeight() / 2, boss.getZ(), 50, .8, .8, .8, .08);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.getNavigation().stop();
        if (rules.timing().windingUp(elapsed)) {
            boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
            if (target != null) MeleeSkill.face(boss, target.position());
            return;
        }
        int hop = rules.hopping(elapsed);
        if (hop >= 0 && target != null && target.isAlive() && boss.level() instanceof ServerLevel level) blink(level, boss, target, hop);
    }

    /** One blink: through the quarry to where it lands, and the corruption laid along the way. */
    private void blink(ServerLevel level, Mob boss, LivingEntity target, int hop) {
        Vec3 from = boss.position(), to = landing(level, boss, target, hop);
        if (to == null) return;
        made++;
        boss.teleportTo(to.x, to.y, to.z);
        boss.setDeltaMovement(Vec3.ZERO);
        MeleeSkill.face(boss, target.position());
        double middle = boss.getBbHeight() / 2;
        Vec3 a = from.add(0, middle, 0), b = to.add(0, middle, 0);
        DamageSource corruption = ModDamageTypes.corruption(level, boss);
        for (LivingEntity caught : level.getEntitiesOfClass(LivingEntity.class, new AABB(a, b).inflate(rules.width() + middle), caught -> caught != boss
                && caught.isAlive() && !boss.isAlliedTo(caught) && near(caught, a, b) && hurt.getOrDefault(caught.getUUID(), 0) < rules.hitsEach())) {
            hurt.merge(caught.getUUID(), 1, Integer::sum);
            caught.invulnerableTime = 0;
            if (caught.hurt(corruption, (float) rules.damage()))
                caught.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, rules.curseTicks(), rules.curseLevel()), boss);
        }
        SkillShows.show(level, from, "shards", 1.4, middle, 16, colour, accent, 0, boss.getYRot(), false);
        SkillShows.show(level, to, "rift", 1.6, middle + .6, 18, colour, accent, 0, boss.getYRot(), false);
        double length = a.distanceTo(b);
        for (double along = 0; along <= length; along += .5) {
            Vec3 at = a.lerp(b, length < 1e-6 ? 0 : along / length);
            level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), at.x, at.y, at.z, 2, .25, .4, .25, .02);
        }
        SkillShows.sound(level, from, SoundEvents.ENDERMAN_TELEPORT, 1.5F, 1.4F);
        SkillShows.sound(level, to, SoundEvents.ENDERMAN_TELEPORT, 1.5F, 1.1F);
    }

    /** Whether a creature's box comes within the blink's width of the line it went along. */
    private boolean near(LivingEntity caught, Vec3 a, Vec3 b) {
        Vec3 at = caught.getBoundingBox().getCenter();
        return PhaseDashRules.offLine(a.x, a.y, a.z, b.x, b.y, b.z, at.x, at.y, at.z) <= rules.width() + Math.max(caught.getBbWidth(), caught.getBbHeight()) / 2;
    }

    /** Where the hop-th blink lands: through the quarry as the rules say, else swerving the other way, else half as far; null if nowhere fits. */
    @Nullable private Vec3 landing(ServerLevel level, Mob boss, LivingEntity target, int hop) {
        Vec3 quarry = target.position(), from = boss.position();
        for (int tried = 0; tried < 3; tried++) {
            double[] off = rules.landing(tried == 1 ? hop + 1 : hop, from.x - quarry.x, from.z - quarry.z);
            double scale = tried == 2 ? .5 : 1;
            Vec3 at = SkillShows.ground(level, quarry.add(off[0] * scale, 0, off[1] * scale));
            if (level.noCollision(boss, boss.getDimensions(boss.getPose()).makeBoundingBox(at)) && !level.containsAnyLiquid(boss.getDimensions(boss.getPose()).makeBoundingBox(at)))
                return at;
        }
        return null;
    }
}
