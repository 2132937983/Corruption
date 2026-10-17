package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.DiveSkill;
import com.miophas.corruption.common.entity.skill.ScreechSkill;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.entity.skill.SwoopSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.ScreecherRules;
import com.miophas.corruption.core.elite.ScreecherRules.Skill;
import java.util.EnumSet;
import java.util.Set;
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
 * The corrupted screecher, the air's elite (ScreecherRules): a flayed bird, all bare bone and torn wing, its eyes burning.
 * It circles its quarry high up (as every flying elite does) and swoops on it to bite; it sweeps fans of the corruption's
 * feathers at it; it folds its wings and dives on where it stands; and it screeches, stunning everything near and leaving
 * it slowed.
 */
public final class CorruptedScreecher extends FlyingElite<CorruptedScreecher> {
    /** Its colours: its eyes' red, its feathers' rose, and a pale rose for what is brightest. */
    public static final int RED = 0xFF3048, ROSE = 0xE0609A, PALE = 0xFFD0E4;
    /** Its beak's point, in its own frame from its feet ({right, up, ahead}, in blocks), its long neck out before it: what it looses leaves it there. */
    public static final double[] BEAK = {0, .98, 1.72};

    private final SwoopSkill swoop = new SwoopSkill(ScreecherRules.SWOOP, ScreecherRules.SWOOP_RANGE, ScreecherRules.SWOOP_SPEED);
    private final DiveSkill dive = new DiveSkill(ScreecherRules.DIVE, ROSE, PALE, true);
    private final SpitSkill feathers = new SpitSkill(ScreecherRules.FEATHERS, ROSE, PALE);
    private final ScreechSkill screech = new ScreechSkill(ScreecherRules.SCREECH, RED, PALE);
    /** Its skills, in the order of ScreecherRules.SKILLS. */
    private final SkillSet<CorruptedScreecher> skills = SkillSet.<CorruptedScreecher>of()
            .with("swoop", swoop)
            .with("dive", dive)
            .with("feathers", feathers)
            .with("screech", screech);

    public CorruptedScreecher(EntityType<? extends CorruptedScreecher> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, ScreecherRules.MAX_HEALTH).add(Attributes.ARMOR, ScreecherRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, ScreecherRules.ATTACK_DAMAGE).add(Attributes.FLYING_SPEED, ScreecherRules.FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FOLLOW_RANGE, ScreecherRules.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, ScreecherRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedScreecher elite() { return this; }
    @Override public SkillSet<CorruptedScreecher> skills() { return skills; }
    @Override protected double keepAt() { return ScreecherRules.KEEP_AT; }
    @Override protected double hoverLow() { return ScreecherRules.HOVER_LOW; }
    @Override protected double hoverHigh() { return ScreecherRules.HOVER_HIGH; }
    @Override protected double[] mouth() { return BEAK; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : ScreecherRules.SKILLS) if (skills.usable(index(skill), this, target)) ready.add(skill);
        Skill next = ScreecherRules.choose(distanceTo(target), Stuns.stunned(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    private static int index(Skill skill) { return ScreecherRules.SKILLS.indexOf(skill); }

    /** Its swoop and its dive move it themselves. */
    @Override protected boolean drivenBySkill() { return skill() == Skill.SWOOP.ordinal() || skill() == Skill.DIVE.ordinal(); }

    /** Ash falls from its torn wings. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && random.nextInt(6) == 0)
            level().addParticle(ModParticles.CORRUPTION_PARTICLE.get(), getRandomX(1.2), getY() + random.nextDouble() * getBbHeight(), getRandomZ(1.2), 0, -.03, 0);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PHANTOM_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.PHANTOM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PHANTOM_DEATH; }
    @Override public float getVoicePitch() { return .6F + (random.nextFloat() - random.nextFloat()) * .05F; }
    @Override public int getAmbientSoundInterval() { return 160; }

    /** Its skills, for whoever would look at what they did. */
    public SwoopSkill swoop() { return swoop; }
    public DiveSkill dive() { return dive; }
    public SpitSkill feathers() { return feathers; }
    public ScreechSkill screech() { return screech; }
}
