package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.PsychicRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * A strike at the mind ({@link PsychicRules}) as the mod carries it out for any boss. Through the wind-up the boss fixes
 * its quarry with its eyes: an eye opens over the quarry's head, and a heartbeat sounds. When the wind-up is over, if the
 * boss still sees it, the quarry and every creature within the strike's reach of it that is not of the boss's side
 * (players in creative or spectator aside) is struck: hurt with the corruption's own hurt (a moment's protection after
 * another blow does not keep it off), its sight swimming and darkening, its limbs heavy; and the strike rings out over
 * each.
 */
public final class PsychicSkill implements BossSkill<Mob> {
    private final PsychicRules rules;
    private final int colour, accent;
    private final List<LivingEntity> struck = new ArrayList<>();

    public PsychicSkill(PsychicRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public PsychicRules rules() { return rules; }

    /** What this use struck, once it has. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        struck.clear();
        SkillShows.sound(boss, SoundEvents.WARDEN_HEARTBEAT, 2, .6F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, target.position().add(0, target.getBbHeight() + .9, 0), "eye", .7, 0, rules.timing().windup() + 6, colour, accent, 0, boss.getYRot(), false);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (target == null) return;
        boss.getLookControl().setLookAt(target, 30, 30);
        if (!rules.timing().strikes(elapsed) || !(boss.level() instanceof ServerLevel level) || !boss.hasLineOfSight(target)) return;
        Vec3 at = target.position();
        for (LivingEntity mind : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(rules.radius()), mind -> mind.isAlive()
                && (mind == target || rules.reaches(mind.position().distanceTo(at))) && mind != boss && !boss.isAlliedTo(mind)
                && !(mind instanceof Player player && (player.isCreative() || player.isSpectator()))))
            strike(level, boss, mind);
        SkillShows.sound(level, at, SoundEvents.ELDER_GUARDIAN_CURSE, 1.2F, 1.5F);
    }

    /** One mind struck: hurt, its sight swimming and darkening, its limbs heavy; a ring of the strike over its head. */
    private void strike(ServerLevel level, Mob boss, LivingEntity mind) {
        mind.invulnerableTime = 0;
        mind.hurt(ModDamageTypes.corruption(level, boss), (float) rules.damage());
        if (rules.nauseaTicks() > 0) mind.addEffect(new MobEffectInstance(MobEffects.CONFUSION, rules.nauseaTicks(), 0), boss);
        if (rules.darknessTicks() > 0) mind.addEffect(new MobEffectInstance(MobEffects.DARKNESS, rules.darknessTicks(), 0), boss);
        if (rules.slowTicks() > 0) mind.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, rules.slowTicks(), rules.slowLevel()), boss);
        Vec3 head = mind.getEyePosition();
        SkillShows.show(level, head, "resonance", .8, 0, 16, colour, accent, .2, boss.getYRot(), false);
        level.sendParticles(ParticleTypes.SCULK_SOUL, head.x, head.y + .3, head.z, 6, .3, .2, .3, .02);
        struck.add(mind);
    }
}
