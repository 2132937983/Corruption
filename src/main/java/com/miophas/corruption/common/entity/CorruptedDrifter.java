package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BarrageSkill;
import com.miophas.corruption.common.entity.skill.GrabSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.DrifterRules;
import com.miophas.corruption.core.elite.DrifterRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * The corrupted drifter, an air elite (DrifterRules): a great bell of membrane with a glowing heart, a frilled skirt and
 * long tendrils beneath. It drifts slowly and high over its quarry, stinging what comes under it; rains acid onto where
 * its quarry stood; and takes hold of its quarry with its tendrils, draws it up to itself and lets it fall.
 */
public final class CorruptedDrifter extends FlyingElite<CorruptedDrifter> implements Lashing {
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, ACID = 0xB040C8;
    /** Under its bell, where its tendrils hang from: in its own frame ({right, up, ahead}). */
    public static final double[] UNDER = {0, .4, 0};

    private static final EntityDataAccessor<Integer> REACHING = SynchedEntityData.defineId(CorruptedDrifter.class, EntityDataSerializers.INT);

    private final GrabSkill lift = new GrabSkill(DrifterRules.LIFT, ROSE, PALE);
    private final BarrageSkill rain = new BarrageSkill(DrifterRules.RAIN, ACID, PALE);
    private final SkillSet<CorruptedDrifter> skills = SkillSet.<CorruptedDrifter>of()
            .with("lift", lift)
            .with("rain", rain);

    public CorruptedDrifter(EntityType<? extends CorruptedDrifter> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, DrifterRules.MAX_HEALTH).add(Attributes.ARMOR, DrifterRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, DrifterRules.ATTACK_DAMAGE).add(Attributes.FLYING_SPEED, DrifterRules.FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, .2).add(Attributes.FOLLOW_RANGE, DrifterRules.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, DrifterRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedDrifter elite() { return this; }
    @Override public SkillSet<CorruptedDrifter> skills() { return skills; }
    @Override protected double keepAt() { return DrifterRules.KEEP_AT; }
    @Override protected double hoverLow() { return DrifterRules.HOVER_LOW; }
    @Override protected double hoverHigh() { return DrifterRules.HOVER_HIGH; }
    @Override protected double[] mouth() { return UNDER; }
    @Override protected double strikeReach() { return DrifterRules.STING_REACH; }
    @Override protected int strikeEvery() { return DrifterRules.STING_EVERY; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REACHING, -1);
    }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : DrifterRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = DrifterRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** It drifts as every flier does; and what its tendrils reach for is shown to every client. */
    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        LivingEntity reaching = skill() == Skill.LIFT.ordinal() ? lift.reachingFor() : null;
        entityData.set(REACHING, reaching == null ? -1 : reaching.getId());
        super.act(target, busy);
    }

    @Override @Nullable public Entity reaching() {
        int id = entityData.get(REACHING);
        return id < 0 ? null : level().getEntity(id);
    }

    @Override public double[] lashFrom() { return UNDER; }

    @Override public float lashOut(float partial) {
        return skill() != Skill.LIFT.ordinal() ? 1 : Mth.clamp(skillTicks(partial) / Math.max(1, DrifterRules.LIFT.timing().windup()), .1F, 1);
    }

    /** Drops of its acid fall from its skirt. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && random.nextInt(5) == 0)
            level().addParticle(ModParticles.CORRUPTION_PARTICLE.get(), getRandomX(.7), getY() + .3, getRandomZ(.7), 0, -.05, 0);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.GLOW_SQUID_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GLOW_SQUID_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GLOW_SQUID_DEATH; }
    @Override public float getVoicePitch() { return .5F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public GrabSkill lift() { return lift; }
    public BarrageSkill rain() { return rain; }
}
