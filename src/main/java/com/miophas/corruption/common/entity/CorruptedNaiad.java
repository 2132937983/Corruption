package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PounceSkill;
import com.miophas.corruption.common.entity.skill.ScreechSkill;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.core.elite.NaiadRules;
import com.miophas.corruption.core.elite.NaiadRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * The corrupted naiad, a sea elite (NaiadRules): a drowned thing with a woman's gaunt body and a finned tail, gills cut in
 * its neck, a spear of bone grown out of its arm. It hunts as every sea elite does; thrusts with its spear at what it
 * reaches; hurls spears of bone; surges at its quarry; and sings a song that slows what hears it.
 */
public final class CorruptedNaiad extends SwimmingElite<CorruptedNaiad> {
    public static final int BONE = 0xE8DCC8, FOAM = 0xE0D0FF, WATER = 0x8A5AE0;
    /** Its spear arm, raised to throw: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {-.4, 1.7, .4};

    private final SpitSkill spear = new SpitSkill(NaiadRules.SPEAR, BONE, FOAM);
    private final PounceSkill surge = new PounceSkill(NaiadRules.SURGE, WATER, FOAM);
    private final ScreechSkill song = new ScreechSkill(NaiadRules.SONG, WATER, FOAM);
    private final SkillSet<CorruptedNaiad> skills = SkillSet.<CorruptedNaiad>of()
            .with("spear", spear)
            .with("surge", surge)
            .with("song", song);

    public CorruptedNaiad(EntityType<? extends CorruptedNaiad> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, NaiadRules.MAX_HEALTH).add(Attributes.ARMOR, NaiadRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, NaiadRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, NaiadRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, NaiadRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, NaiadRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedNaiad elite() { return this; }
    @Override public SkillSet<CorruptedNaiad> skills() { return skills; }
    @Override protected float wander() { return NaiadRules.WANDER; }
    @Override protected float crawl() { return NaiadRules.CRAWL; }
    @Override protected double dart() { return NaiadRules.DART; }
    @Override protected double thrust() { return NaiadRules.THRUST; }
    @Override protected double strikeReach() { return NaiadRules.BITE_REACH; }
    @Override protected int strikeEvery() { return NaiadRules.BITE_EVERY; }
    @Override protected double[] mouth() { return MOUTH; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : NaiadRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = NaiadRules.choose(distanceTo(target), target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    @Override public boolean doHurtTarget(Entity target) {
        playSound(SoundEvents.TRIDENT_HIT, 1, .7F);
        return super.doHurtTarget(target);
    }

    @Override protected SoundEvent getAmbientSound() { return isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT; }
    @Override protected SoundEvent getDeathSound() { return isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH; }
    @Override protected SoundEvent getSwimSound() { return SoundEvents.DOLPHIN_SWIM; }
    @Override public float getVoicePitch() { return .8F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public SpitSkill spear() { return spear; }
    public PounceSkill surge() { return surge; }
    public ScreechSkill song() { return song; }
}
