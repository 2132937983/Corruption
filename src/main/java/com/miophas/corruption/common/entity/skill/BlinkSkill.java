package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.BlinkRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Blinking out of danger ({@link BlinkRules}) as the mod carries it out for any boss. The boss has it {@link #weigh}
 * how safe it stands every tick; once it is not, it begins the blink, shimmering for the wind-up, and then is gone, a
 * rift where it stood, and is there: at the best of the places it tried round its quarry, where it fits, out of
 * anything that hurts, in sight of its quarry and furthest from what crowded it. Finding nowhere, it stays.
 */
public final class BlinkSkill implements BossSkill<Mob> {
    private final BlinkRules rules;
    private final BlinkRules.Wounds wounds;
    private final int colour, accent;

    public BlinkSkill(BlinkRules rules, int colour, int accent) {
        this.rules = rules;
        this.wounds = new BlinkRules.Wounds(rules.hurtWindow());
        this.colour = colour;
        this.accent = accent;
    }

    public BlinkRules rules() { return rules; }

    @Override public SkillTiming timing() { return rules.timing(); }

    /** It serves whenever it is needed: what needs it is weighed by {@link #weigh}. */
    @Override public boolean usable(Mob boss, LivingEntity target) { return true; }

    /**
     * How safe the boss stands this tick, with its quarry where it is (or none): weighed every tick, since it remembers
     * how much the boss has lost of late. Whether it is not safe.
     */
    public boolean weigh(Mob boss, @Nullable LivingEntity target) {
        wounds.see(boss.getHealth() / boss.getMaxHealth());
        List<LivingEntity> near = threats(boss);
        boolean player = near.stream().anyMatch(threat -> threat instanceof Player);
        boolean harm = boss.isInWater() || boss.isInLava() || boss.isOnFire() || boss.isInWall() || boss.isInPowderSnow;
        double distance = target == null ? Double.MAX_VALUE : boss.distanceTo(target);
        return rules.unsafe(new BlinkRules.Danger(harm, wounds.lost(), near.size(), player, distance));
    }

    @Override public void begin(Mob boss, LivingEntity target) {
        SkillShows.sound(boss, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.5F, 1.1F);
        if (boss.level() instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, boss.getX(), boss.getY() + boss.getBbHeight() / 2, boss.getZ(), 40, .6, .8, .6, .05);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (!rules.timing().strikes(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 to = somewhereSafe(level, boss, target);
        if (to == null) return;
        Vec3 from = boss.position();
        SkillShows.show(level, from, "rift", 1.6, boss.getBbHeight() / 2 + .6, 24, colour, accent, 0, boss.getYRot(), false);
        SkillShows.show(level, from, "shards", 1.4, boss.getBbHeight() / 2, 20, colour, accent, 0, 0, false);
        level.sendParticles(ParticleTypes.PORTAL, from.x, from.y + boss.getBbHeight() / 2, from.z, 60, .5, .7, .5, .6);
        SkillShows.sound(level, from, SoundEvents.ENDERMAN_TELEPORT, 1.5F, .7F);
        boss.teleportTo(to.x, to.y, to.z);
        boss.setDeltaMovement(Vec3.ZERO);
        boss.getNavigation().stop();
        wounds.forget();
        SkillShows.show(level, to, "rift", 1.6, boss.getBbHeight() / 2 + .6, 24, colour, accent, 0, boss.getYRot(), false);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, to.x, to.y + boss.getBbHeight() / 2, to.z, 60, .5, .7, .5, .1);
        SkillShows.sound(level, to, SoundEvents.ENDERMAN_TELEPORT, 1.5F, .9F);
    }

    /** Things that would hurt the boss, crowding it: players and creatures not of its side, within the crowding radius. */
    private List<LivingEntity> threats(Mob boss) {
        return boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(rules.crowdRadius()), near -> near != boss && near.isAlive()
                && !near.isSpectator() && !CorruptionFactionApi.isRemnant(near) && !boss.isAlliedTo(near)
                && (near instanceof Player player ? !player.isCreative() : near instanceof Mob mob && mob.getTarget() == boss));
    }

    /** The best of the places tried round the quarry (round the boss itself if it has none), or null if none will do. */
    private Vec3 somewhereSafe(ServerLevel level, Mob boss, @Nullable LivingEntity target) {
        Vec3 middle = target != null ? target.position() : boss.position();
        List<LivingEntity> crowd = threats(boss);
        double turn = boss.getRandom().nextDouble() * Math.PI * 2, roll = boss.getRandom().nextDouble(), best = -Double.MAX_VALUE;
        Vec3 chosen = null;
        for (int tried = 0; tried < rules.tries(); tried++) {
            double[] trial = rules.trial(tried, turn, roll);
            Vec3 ground = SkillShows.ground(level, middle.add(Math.cos(trial[0]) * trial[1], 0, Math.sin(trial[0]) * trial[1]));
            Vec3 at = ground.add(0, trial[2], 0);
            AABB body = boss.getDimensions(boss.getPose()).makeBoundingBox(at);
            if (!level.noCollision(boss, body) || level.containsAnyLiquid(body)) continue;
            if (target != null && level.clip(new ClipContext(at.add(0, boss.getBbHeight() / 2, 0), target.getEyePosition(), ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, boss)).getType() != HitResult.Type.MISS) continue;
            double nearest = 24;
            for (LivingEntity threat : crowd) nearest = Math.min(nearest, threat.position().distanceTo(at));
            double score = rules.score(at.distanceTo(middle), nearest);
            if (score > best) { best = score; chosen = at; }
        }
        return chosen;
    }
}
