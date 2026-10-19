package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * The brainwashing gaze ({@link GazeRules}) as the mod carries it out for any boss. As it begins, its Eyes of
 * Providence open (one over the boss, the rest in a ring round its quarry, staring at whoever looks) and stay open
 * through the wind-up; when the wind-up is over, if the boss still sees its quarry within reach, it {@link #stare stares}.
 */
public final class GazeSkill implements RegisteredSkill<GazeRules> {
    private final GazeRules rules;
    private final int colour, accent;

    public GazeSkill(GazeRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public GazeRules rules() { return rules; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        SkillShows.sound(boss, SoundEvents.ELDER_GUARDIAN_AMBIENT, 2, .5F);
        if (!(boss.level() instanceof ServerLevel level) || rules.eyes() == 0) return;
        int open = rules.timing().windup() + 16;
        SkillShows.show(level, boss.position(), "eye", 2, boss.getBbHeight() + 2.5, open, colour, accent, 0, boss.getYRot(), true);
        double turn = boss.getRandom().nextDouble() * Math.PI * 2;
        for (int eye = 0; eye < rules.ringed(); eye++) {
            double angle = rules.ringAngle(eye, turn);
            Vec3 at = target.position().add(Math.cos(angle) * GazeRules.RING, 0, Math.sin(angle) * GazeRules.RING);
            SkillShows.show(level, at, "eye", 1.4, target.getBbHeight() + GazeRules.RING_HEIGHT, open, colour, accent, 0, 0, true);
        }
        if (rules.ringed() > 0) SkillShows.sound(level, target.position(), SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, 1.5F, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (target == null) return;
        boss.getLookControl().setLookAt(target, 30, 30);
        if (rules.timing().strikes(elapsed) && rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target)) stare(boss, target, rules);
    }

    /**
     * The gaze's blow: a player sees an eye flash before their eyes, as an elder guardian's face does, and hears the
     * guardian's curse; whatever it is, it is slowed and blinded, and withered by a gaze that withers. Not a player in
     * creative or spectator play.
     */
    public static void stare(Mob boss, LivingEntity target, GazeRules rules) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) return;
        if (target instanceof ServerPlayer player && boss.level() instanceof ServerLevel level) {
            level.sendParticles(player, ModParticles.ANDROMALIUS_GAZE.get(), true, player.getX(), player.getEyeY(), player.getZ(), 1, 0, 0, 0, 0);
            player.playNotifySound(SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1, .6F);
        }
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, rules.slownessTicks(), rules.slownessLevel()), boss);
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, rules.blindnessTicks()), boss);
        if (rules.withers()) target.addEffect(new MobEffectInstance(MobEffects.WITHER, rules.witherTicks(), rules.witherLevel()), boss);
    }
    @Override public BossSkillRegistry.Type<GazeRules> type() { return BossSkillApi.GAZE; }
    @Override public GazeRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
