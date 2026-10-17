package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.entity.skill.SummonSkill;
import com.miophas.corruption.common.registry.ModSounds;
import com.miophas.corruption.core.elite.HowlerRules;
import com.miophas.corruption.core.elite.HowlerRules.Skill;
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
 * The corrupted howler, a land elite (HowlerRules): a gaunt thing twice a man's height, its arms trailing to the ground,
 * its head split down the throat. It rakes what it reaches; howls the corruption's remnants up out of the ground about it,
 * which crumble away in time; and spits bile that leaves a cursed pool.
 */
public final class CorruptedHowler extends WalkingElite<CorruptedHowler> {
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, BILE = 0xA040B0;
    /** Its jaws, high up, thrust out as it spits: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, 2.2, .8};

    private final SummonSkill howl = new SummonSkill(HowlerRules.HOWL, ROSE, PALE);
    private final SpitSkill bile = new SpitSkill(HowlerRules.BILE, BILE, PALE);
    private final SkillSet<CorruptedHowler> skills = SkillSet.<CorruptedHowler>of()
            .with("howl", howl)
            .with("bile", bile);

    public CorruptedHowler(EntityType<? extends CorruptedHowler> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, HowlerRules.MAX_HEALTH).add(Attributes.ARMOR, HowlerRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, HowlerRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, HowlerRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, HowlerRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, HowlerRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedHowler elite() { return this; }
    @Override public SkillSet<CorruptedHowler> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return HowlerRules.CHASE; }
    @Override protected int strikeEvery() { return HowlerRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : HowlerRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = HowlerRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE && begin(next.ordinal(), target) && next == Skill.HOWL) playSound(SoundEvents.WOLF_HOWL, 3, .4F);
    }

    @Override protected SoundEvent getAmbientSound() { return ModSounds.REMNANT_AMBIENT.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.REMNANT_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.REMNANT_DEATH.get(); }
    @Override public float getVoicePitch() { return .5F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public SummonSkill howl() { return howl; }
    public SpitSkill bile() { return bile; }
}
