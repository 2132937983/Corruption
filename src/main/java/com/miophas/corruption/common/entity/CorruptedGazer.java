package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BeamCaster;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BeamSkill;
import com.miophas.corruption.common.entity.skill.GazeSkill;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.GazerRules;
import com.miophas.corruption.core.elite.GazerRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The corrupted gazer, an air elite (GazerRules): one great eye ringed with lesser eyes on stalks, trailing nerves beneath
 * it. It hangs well off its quarry, lashing with its nerves at what comes close; looses bolts from its lesser eyes; fixes
 * its quarry with its great eye until it is slowed and blinded; and burns it with a beam from its great eye.
 */
public final class CorruptedGazer extends FlyingElite<CorruptedGazer> implements BeamCaster {
    public static final int GOLD = 0xF2C46A, PALE = 0xFFF2D0, ROSE = 0xE0609A;
    /** Its great eye's pupil, in its own frame ({right, up, ahead}): its beam and its bolts leave it there. */
    public static final double[] EYE = {0, .6, .55};

    private final BeamSkill beam = new BeamSkill(GazerRules.BEAM, GOLD, PALE);
    private final GazeSkill gaze = new GazeSkill(GazerRules.GAZE, GOLD, PALE);
    private final SpitSkill bolts = new SpitSkill(GazerRules.BOLTS, ROSE, PALE);
    private final SkillSet<CorruptedGazer> skills = SkillSet.<CorruptedGazer>of()
            .with("beam", beam)
            .with("gaze", gaze)
            .with("bolts", bolts);

    public CorruptedGazer(EntityType<? extends CorruptedGazer> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, GazerRules.MAX_HEALTH).add(Attributes.ARMOR, GazerRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, GazerRules.ATTACK_DAMAGE).add(Attributes.FLYING_SPEED, GazerRules.FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FOLLOW_RANGE, GazerRules.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, GazerRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedGazer elite() { return this; }
    @Override public SkillSet<CorruptedGazer> skills() { return skills; }
    @Override protected double keepAt() { return GazerRules.KEEP_AT; }
    @Override protected double hoverLow() { return GazerRules.HOVER_LOW; }
    @Override protected double hoverHigh() { return GazerRules.HOVER_HIGH; }
    @Override protected double[] mouth() { return EYE; }
    @Override protected double strikeReach() { return GazerRules.LASH_REACH; }
    @Override protected int strikeEvery() { return GazerRules.LASH_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : GazerRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = GazerRules.choose(distanceTo(target), target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** Its beam leaves its great eye. */
    @Override public Vec3 beamFrom() { return inFrame(EYE); }

    /** Motes of its gold drift off its eyes. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && random.nextInt(6) == 0)
            level().addParticle(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), getRandomX(.8), getY() + random.nextDouble() * getBbHeight(), getRandomZ(.8), 0, -.02, 0);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.GUARDIAN_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GUARDIAN_HURT_LAND; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GUARDIAN_DEATH_LAND; }
    @Override public float getVoicePitch() { return .6F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public BeamSkill beam() { return beam; }
    public GazeSkill gaze() { return gaze; }
    public SpitSkill bolts() { return bolts; }
}
