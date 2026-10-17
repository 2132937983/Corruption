package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.entity.skill.SummonSkill;
import com.miophas.corruption.common.entity.skill.SwoopSkill;
import com.miophas.corruption.core.elite.HivemotherRules;
import com.miophas.corruption.core.elite.HivemotherRules.Skill;
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
 * The corrupted hivemother, an air elite (HivemotherRules): a flying hive of flesh on four droning wings, its cells swollen
 * with brood, a stinger beneath. It circles its quarry and swoops to sting it; looses fans of stingers; and lets out a swarm
 * of the corruption's bees and vexes, which crumble away in time.
 */
public final class CorruptedHivemother extends FlyingElite<CorruptedHivemother> {
    public static final int GOLD = 0xF2C46A, PALE = 0xFFF2D0, ROSE = 0xE0609A;
    /** Its stinger, curled down beneath it as it looses: in its own frame ({right, up, ahead}). */
    public static final double[] STINGER = {0, -.25, -.4};

    private final SwoopSkill swoop = new SwoopSkill(HivemotherRules.SWOOP, HivemotherRules.SWOOP_RANGE, HivemotherRules.SWOOP_SPEED);
    private final SpitSkill stingers = new SpitSkill(HivemotherRules.STINGERS, GOLD, PALE);
    private final SummonSkill swarm = new SummonSkill(HivemotherRules.SWARM, GOLD, ROSE);
    private final SkillSet<CorruptedHivemother> skills = SkillSet.<CorruptedHivemother>of()
            .with("swoop", swoop)
            .with("stingers", stingers)
            .with("swarm", swarm);

    public CorruptedHivemother(EntityType<? extends CorruptedHivemother> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, HivemotherRules.MAX_HEALTH).add(Attributes.ARMOR, HivemotherRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, HivemotherRules.ATTACK_DAMAGE).add(Attributes.FLYING_SPEED, HivemotherRules.FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FOLLOW_RANGE, HivemotherRules.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, HivemotherRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedHivemother elite() { return this; }
    @Override public SkillSet<CorruptedHivemother> skills() { return skills; }
    @Override protected double keepAt() { return HivemotherRules.KEEP_AT; }
    @Override protected double hoverLow() { return HivemotherRules.HOVER_LOW; }
    @Override protected double hoverHigh() { return HivemotherRules.HOVER_HIGH; }
    @Override protected double[] mouth() { return STINGER; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : HivemotherRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = HivemotherRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** Its swoop moves it itself. */
    @Override protected boolean drivenBySkill() { return skill() == Skill.SWOOP.ordinal(); }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.BEEHIVE_WORK; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.BEE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BEE_DEATH; }
    @Override public float getVoicePitch() { return .45F + (random.nextFloat() - random.nextFloat()) * .05F; }
    @Override public int getAmbientSoundInterval() { return 60; }

    public SwoopSkill swoop() { return swoop; }
    public SpitSkill stingers() { return stingers; }
    public SummonSkill swarm() { return swarm; }
}
