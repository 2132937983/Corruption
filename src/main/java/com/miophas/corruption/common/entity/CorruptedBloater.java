package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.elite.BloaterRules;
import com.miophas.corruption.core.elite.BloaterRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * The corrupted bloater, a land elite (BloaterRules): a great sac of rot on four stubby legs, vents on its back seeping the
 * corruption's gas. It butts what it reaches; swells and vents its gas all about it; lobs gobs of the gas that burst into
 * wide pools; and when it dies it bursts, leaving the greatest cloud of all.
 */
public final class CorruptedBloater extends WalkingElite<CorruptedBloater> {
    public static final int GAS = 0x9A4CC8, PALE = 0xE8C8FF;
    /** Its round mouth, low at its front: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, .7, 1};

    private final SpitSkill lob = new SpitSkill(BloaterRules.LOB, GAS, PALE);
    private final SkillSet<CorruptedBloater> skills = SkillSet.<CorruptedBloater>of()
            .with("vent", new Vent())
            .with("lob", lob);

    public CorruptedBloater(EntityType<? extends CorruptedBloater> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, BloaterRules.MAX_HEALTH).add(Attributes.ARMOR, BloaterRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, BloaterRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, BloaterRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, BloaterRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, BloaterRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedBloater elite() { return this; }
    @Override public SkillSet<CorruptedBloater> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return BloaterRules.CHASE; }
    @Override protected int strikeEvery() { return BloaterRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : BloaterRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = BloaterRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** Its vent: swelling through the wind-up, then its gas let out all about it. */
    private final class Vent implements BossSkill<CorruptedBloater> {
        @Override public SkillTiming timing() { return BloaterRules.VENT; }
        @Override public boolean usable(CorruptedBloater bloater, LivingEntity target) { return bloater.distanceTo(target) <= BloaterRules.VENT_RANGE; }

        @Override public void begin(CorruptedBloater bloater, LivingEntity target) {
            bloater.getNavigation().stop();
            SkillShows.sound(bloater, SoundEvents.PUFFER_FISH_BLOW_UP, 2, .5F);
        }

        @Override public void tick(CorruptedBloater bloater, @Nullable LivingEntity target, int elapsed) {
            bloater.setDeltaMovement(bloater.getDeltaMovement().multiply(.2, 1, .2));
            if (!BloaterRules.VENT.strikes(elapsed) || !(bloater.level() instanceof ServerLevel level)) return;
            EliteClouds.release(level, bloater, bloater.position(), BloaterRules.VENT_CLOUD);
            SkillShows.sound(bloater, SoundEvents.PUFFER_FISH_BLOW_OUT, 2.5F, .4F);
        }
    }

    /** Dying, it bursts. */
    @Override public void die(DamageSource source) {
        if (level() instanceof ServerLevel level && !isRemoved() && !dead) {
            EliteClouds.release(level, this, position(), BloaterRules.BURST);
            SkillShows.sound(this, SoundEvents.GENERIC_EXPLODE.value(), 1.5F, 1.4F);
        }
        super.die(source);
    }

    /** Gas seeps from its vents. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && random.nextInt(4) == 0)
            level().addParticle(ModParticles.CORRUPTION_PARTICLE.get(), getRandomX(.6), getY() + getBbHeight() * .9, getRandomZ(.6), 0, .04, 0);
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.SLIME_ATTACK; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PUFFER_FISH_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SLIME_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SLIME_DEATH; }
    @Override public float getVoicePitch() { return .5F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public SpitSkill lob() { return lob; }
}
