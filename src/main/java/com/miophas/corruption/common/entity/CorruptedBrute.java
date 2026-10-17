package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.ChargeSkill;
import com.miophas.corruption.common.entity.skill.MeleeSkill;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.core.elite.BruteRules;
import com.miophas.corruption.core.elite.BruteRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
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
 * The corrupted brute, a land elite (BruteRules): a hunched mountain of flesh, one arm swollen into a club of bone and
 * crystal. It clubs what it reaches; slams its club down on everything before it; charges, bowling over what it meets and
 * breaking the soft ground in its way; and hurls lumps of the corrupted earth.
 */
public final class CorruptedBrute extends WalkingElite<CorruptedBrute> {
    /** Its colours: the corruption's rose, a pale rose, and the earth it hurls. */
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, EARTH = 0x6A3A70;
    /** The end of its club as it flings it forward, where its lump of earth leaves it: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {-.55, 1.1, 1.65};

    private final MeleeSkill slam = new MeleeSkill(BruteRules.SLAM);
    private final ChargeSkill charge = new ChargeSkill(BruteRules.CHARGE);
    private final SpitSkill hurl = new SpitSkill(BruteRules.HURL, EARTH, PALE);
    private final SkillSet<CorruptedBrute> skills = SkillSet.<CorruptedBrute>of()
            .with("slam", slam)
            .with("charge", charge)
            .with("hurl", hurl);

    public CorruptedBrute(EntityType<? extends CorruptedBrute> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, BruteRules.MAX_HEALTH).add(Attributes.ARMOR, BruteRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, BruteRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, BruteRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, BruteRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, BruteRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.STEP_HEIGHT, 1);
    }

    @Override protected CorruptedBrute elite() { return this; }
    @Override public SkillSet<CorruptedBrute> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return BruteRules.CHASE; }
    @Override protected int strikeEvery() { return BruteRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : BruteRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = BruteRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.RAVAGER_ATTACK; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.RAVAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.RAVAGER_STEP, .6F, .7F); }
    @Override public float getVoicePitch() { return .6F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public MeleeSkill slam() { return slam; }
    public ChargeSkill charge() { return charge; }
    public SpitSkill hurl() { return hurl; }
}
