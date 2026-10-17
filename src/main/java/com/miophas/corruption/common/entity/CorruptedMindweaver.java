package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PsychicSkill;
import com.miophas.corruption.common.entity.skill.PsychicStreamSkill;
import com.miophas.corruption.core.elite.MindweaverRules;
import com.miophas.corruption.core.elite.MindweaverRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The corrupted mindweaver, a land elite (MindweaverRules): a great brain of the corruption borne on a squat body, its
 * thin fingers ever weaving. It is slow and very hard to wear down, and its slap is next to nothing; its harm is in the
 * mind. It draws a thread of light out of its quarry's mind and pulls at it over and over (psychic hurt, which armour
 * does not turn aside), each pull mending it; and it wails, striking at the minds of everything about its quarry.
 */
public final class CorruptedMindweaver extends WalkingElite<CorruptedMindweaver> {
    /** Its colours: the violet of its thoughts, and a pale violet where they are brightest. */
    public static final int VIOLET = 0xB070FF, PALE = 0xF0E0FF;
    /** The front of its brain, where its thread leaves it: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, 1.5, .5};

    private final PsychicStreamSkill thread = new PsychicStreamSkill(MindweaverRules.THREAD, VIOLET, PALE);
    private final PsychicSkill wail = new PsychicSkill(MindweaverRules.WAIL, VIOLET, PALE);
    private final SkillSet<CorruptedMindweaver> skills = SkillSet.<CorruptedMindweaver>of()
            .with("thread", thread)
            .with("wail", wail);

    public CorruptedMindweaver(EntityType<? extends CorruptedMindweaver> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, MindweaverRules.MAX_HEALTH).add(Attributes.ARMOR, MindweaverRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, MindweaverRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, MindweaverRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, MindweaverRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, MindweaverRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override protected CorruptedMindweaver elite() { return this; }
    @Override public SkillSet<CorruptedMindweaver> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return MindweaverRules.CHASE; }
    @Override protected int strikeEvery() { return MindweaverRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : MindweaverRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = MindweaverRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** Its thoughts drift up off its brain as motes of light. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && random.nextInt(4) == 0)
            level().addParticle(ParticleTypes.ENCHANT, getRandomX(.6), getY() + getBbHeight() * (.75 + random.nextDouble() * .3), getRandomZ(.6), 0, .05, 0);
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.SLIME_ATTACK; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ILLUSIONER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ILLUSIONER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ILLUSIONER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SLIME_SQUISH_SMALL, .5F, .6F); }
    @Override public float getVoicePitch() { return .45F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public PsychicStreamSkill thread() { return thread; }
    public PsychicSkill wail() { return wail; }
}
