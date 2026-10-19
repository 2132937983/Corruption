package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.CrescendoRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A crescendo ({@link CrescendoRules}) as the mod carries it out for any creature. It stands still where it is; through
 * the wind-up music rings about it and a bell tolls. Then its rings go out one after another, each with a note higher
 * than the last (a chord rising), each drawn as a staff of music racing out over the ground with notes riding it
 * (the effect "crescendo"), notes flying off its front as it goes. A ring catches what it passes, once each: everything
 * not of the caster's side (players in creative or spectator aside) is hurt with the corruption's hurt and thrown off.
 */
public final class CrescendoSkill implements RegisteredSkill<CrescendoRules> {
    /** The pitch of each ring's note: a major chord, rising. */
    private static final float[] CHORD = {.7F, .89F, 1.06F, 1.41F};

    private final CrescendoRules rules;
    private final int colour, accent;
    @Nullable private Vec3 origin;
    private final List<Set<LivingEntity>> caught = new ArrayList<>();
    private int hits;

    public CrescendoSkill(CrescendoRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public CrescendoRules rules() { return rules; }

    /** How many blows this use's rings have struck, all told. */
    public int hits() { return hits; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        origin = boss.position();
        caught.clear();
        for (int ring = 0; ring < rules.rings(); ring++) caught.add(new HashSet<>());
        hits = 0;
        boss.getNavigation().stop();
        SkillShows.sound(boss, SoundEvents.BELL_BLOCK, 3, .5F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, origin, "resonance", 2.5, 2, rules.timing().windup(), colour, accent, 0, boss.getYRot(), true);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.getNavigation().stop();
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
        if (origin == null || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 at = origin;
        for (int ring = 0; ring < rules.rings(); ring++) {
            if (elapsed == rules.starts(ring)) {
                SkillShows.show(level, at, "crescendo", rules.radius(), rules.height(), rules.travel() + 4, colour, accent, .25, boss.getYRot(), true);
                SkillShows.sound(level, at, SoundEvents.NOTE_BLOCK_BELL.value(), 4, CHORD[ring % CHORD.length]);
                SkillShows.sound(level, at, SoundEvents.NOTE_BLOCK_CHIME.value(), 3, CHORD[ring % CHORD.length]);
            }
            double front = rules.front(ring, elapsed);
            if (front < 0) continue;
            notes(level, at, front, ring);
            int index = ring;
            for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(front + rules.width() + 1, rules.height(),
                    front + rules.width() + 1), near -> near != boss && near.isAlive() && !boss.isAlliedTo(near)
                    && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                    && rules.catches(index, elapsed, Math.hypot(near.getX() - at.x, near.getZ() - at.z)) && Math.abs(near.getY() - at.y) <= rules.height())) {
                if (!caught.get(ring).add(near)) continue;
                near.invulnerableTime = 0;
                near.hurt(ModDamageTypes.corruption(level, boss), (float) rules.damage());
                Vec3 away = near.position().subtract(at).multiply(1, 0, 1);
                away = away.lengthSqr() < 1e-4 ? Vec3.directionFromRotation(0, boss.getYRot()) : away.normalize();
                near.push(away.x * rules.knockback(), rules.lift(), away.z * rules.knockback());
                near.hurtMarked = true;
                hits++;
            }
        }
    }

    @Override public void end(Mob boss, boolean broken) { origin = null; }

    /** Notes flying off a ring's front, a few to every few blocks of it, each of its own colour. */
    private void notes(ServerLevel level, Vec3 at, double front, int ring) {
        int count = Math.min(24, Math.max(4, (int) (front * 1.5)));
        for (int note = 0; note < count; note++) {
            double angle = Math.PI * 2 * (note + level.random.nextDouble() * .5) / count;
            double x = at.x + Math.cos(angle) * front, z = at.z + Math.sin(angle) * front;
            level.sendParticles(ParticleTypes.NOTE, x, at.y + .4 + level.random.nextDouble(), z, 0, (ring * 7 + note) % 24 / 24.0, 0, 0, 1);
        }
    }
    @Override public BossSkillRegistry.Type<CrescendoRules> type() { return BossSkillApi.CRESCENDO; }
    @Override public CrescendoRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
