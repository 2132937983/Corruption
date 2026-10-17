package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * A screech ({@link ScreechRules}) as the mod carries it out for any boss. Through the wind-up it draws breath, rearing
 * back, holding still; at its end it screams, a ring of sound racing out over the ground, and everything not of its side
 * within reach is stunned (ModEffects.STUNNED), and slowed after it too where its rules say: players in creative or
 * spectator aside.
 */
public final class ScreechSkill implements BossSkill<Mob> {
    private final ScreechRules rules;
    private final int colour, accent;
    private final List<LivingEntity> stunned = new ArrayList<>();

    public ScreechSkill(ScreechRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public ScreechRules rules() { return rules; }

    /** What the last scream stunned. */
    public List<LivingEntity> stunned() { return List.copyOf(stunned); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        stunned.clear();
        SkillShows.sound(boss, SoundEvents.PHANTOM_AMBIENT, 2, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.setDeltaMovement(boss.getDeltaMovement().scale(.6));
        if (target != null) MeleeSkill.face(boss, target.position());
        if (!rules.timing().strikes(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 heart = boss.getBoundingBox().getCenter();
        for (LivingEntity heard : level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(rules.radius()), heard -> heard != boss
                && heard.isAlive() && !boss.isAlliedTo(heard) && rules.reaches(heard.getBoundingBox().getCenter().distanceTo(heart))
                && !(heard instanceof Player player && (player.isCreative() || player.isSpectator())))) {
            heard.addEffect(new MobEffectInstance(ModEffects.STUNNED, rules.stunTicks(), 0, false, true, true), boss);
            if (rules.slows()) heard.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, rules.slowTicks(), rules.slowLevel() - 1), boss);
            stunned.add(heard);
        }
        SkillShows.show(level, boss.position(), "shockwave", rules.radius(), 2.5, 20, colour, accent, .5, boss.getYRot(), true);
        SkillShows.show(level, heart, "resonance", 3, 1, 24, colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(boss, SoundEvents.PHANTOM_DEATH, 4, .55F);
        SkillShows.sound(boss, SoundEvents.RAVAGER_ROAR, 3, 1.7F);
    }
}
