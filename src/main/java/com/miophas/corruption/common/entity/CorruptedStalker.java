package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PhaseDashSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.elite.StalkerRules;
import com.miophas.corruption.core.elite.StalkerRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * The corrupted stalker, a land elite (StalkerRules): a thin, stooping hunter on long legs, its forearms grown into blades,
 * its face a single burning slit. It slashes what it reaches; fades from sight, only its slit and its edges glowing, and
 * runs the faster until it strikes; flickers back and forth through its quarry, cutting at every pass; and flings spines.
 */
public final class CorruptedStalker extends WalkingElite<CorruptedStalker> {
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, BONE = 0xE8DCC8;
    /** Its slit of a face: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, 1.9, .4};

    private final PhaseDashSkill phase = new PhaseDashSkill(StalkerRules.PHASE, ROSE, PALE);
    private final SpitSkill spines = new SpitSkill(StalkerRules.SPINES, BONE, PALE);
    private final SkillSet<CorruptedStalker> skills = SkillSet.<CorruptedStalker>of()
            .with("fade", new Fade())
            .with("phase", phase)
            .with("spines", spines);

    public CorruptedStalker(EntityType<? extends CorruptedStalker> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, StalkerRules.MAX_HEALTH).add(Attributes.ARMOR, StalkerRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, StalkerRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, StalkerRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, StalkerRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, StalkerRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override protected CorruptedStalker elite() { return this; }
    @Override public SkillSet<CorruptedStalker> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return StalkerRules.CHASE; }
    @Override protected int strikeEvery() { return StalkerRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : StalkerRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = StalkerRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** Its fade: a moment's gathering, then it is gone from sight, and fast, until it strikes or the time is up. */
    private final class Fade implements BossSkill<CorruptedStalker> {
        @Override public SkillTiming timing() { return StalkerRules.FADE; }
        @Override public boolean usable(CorruptedStalker stalker, LivingEntity target) { return !stalker.hasEffect(MobEffects.INVISIBILITY); }

        @Override public void begin(CorruptedStalker stalker, LivingEntity target) { SkillShows.sound(stalker, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.5F, .6F); }

        @Override public void tick(CorruptedStalker stalker, @Nullable LivingEntity target, int elapsed) {
            if (!StalkerRules.FADE.strikes(elapsed)) return;
            stalker.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, StalkerRules.FADE_TICKS, 0, false, false));
            stalker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, StalkerRules.FADE_TICKS, 1, false, false));
            SkillShows.sound(stalker, SoundEvents.ILLUSIONER_MIRROR_MOVE, 1.5F, .5F);
            if (stalker.level() instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.LARGE_SMOKE, stalker.getX(), stalker.getY() + 1, stalker.getZ(), 20, .4, .8, .4, .02);
        }
    }

    /** Whether it is faded from sight. */
    public boolean faded() { return hasEffect(MobEffects.INVISIBILITY); }

    /** Striking, it comes out of its fade. */
    @Override public boolean doHurtTarget(Entity target) {
        if (faded()) {
            removeEffect(MobEffects.INVISIBILITY);
            removeEffect(MobEffects.MOVEMENT_SPEED);
        }
        return super.doHurtTarget(target);
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.PLAYER_ATTACK_SWEEP; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENDERMAN_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENDERMAN_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENDERMAN_DEATH; }
    @Override public float getVoicePitch() { return .5F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public PhaseDashSkill phase() { return phase; }
    public SpitSkill spines() { return spines; }
}
