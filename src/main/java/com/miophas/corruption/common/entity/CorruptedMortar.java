package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BarrageSkill;
import com.miophas.corruption.common.entity.skill.SpikeFieldSkill;
import com.miophas.corruption.core.elite.MortarRules;
import com.miophas.corruption.core.elite.MortarRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The corrupted mortar, a land elite (MortarRules): a squat, plated beast with a swollen tube of flesh on its back. It keeps
 * its distance, backing away from a quarry too near and closing on one too far; rams what corners it; throws up a rain of
 * the corruption's bolts onto where its quarry stood; and drives spikes up out of the ground at what comes too close.
 */
public final class CorruptedMortar extends WalkingElite<CorruptedMortar> {
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, VIOLET = 0xB040C8;
    /** The mouth of the tube on its back: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, 1.9, .2};
    /** How near a quarry must come for it to stand and ram it (cornered). */
    private static final double CORNERED = 3;

    private final BarrageSkill barrage = new BarrageSkill(MortarRules.BARRAGE, VIOLET, PALE);
    private final SpikeFieldSkill spikes = new SpikeFieldSkill(MortarRules.SPIKES, ROSE, PALE);
    private final SkillSet<CorruptedMortar> skills = SkillSet.<CorruptedMortar>of()
            .with("barrage", barrage)
            .with("spikes", spikes);

    public CorruptedMortar(EntityType<? extends CorruptedMortar> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, MortarRules.MAX_HEALTH).add(Attributes.ARMOR, MortarRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, MortarRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, MortarRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, MortarRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, MortarRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedMortar elite() { return this; }
    @Override public SkillSet<CorruptedMortar> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return MortarRules.CHASE; }
    @Override protected int strikeEvery() { return MortarRules.BITE_EVERY; }

    /** It closes on its quarry to ram it only when cornered. */
    @Override protected boolean closes(LivingEntity target) { return distanceTo(target) <= CORNERED; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : MortarRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = MortarRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** It keeps its distance: back from a quarry too near, on towards one too far, standing its ground between. */
    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        super.act(target, busy);
        if (busy || target == null || closes(target)) return;
        int keep = MortarRules.keep(distanceTo(target));
        if (keep == 0) {
            getNavigation().stop();
            getLookControl().setLookAt(target);
            return;
        }
        Vec3 off = position().subtract(target.position()).multiply(1, 0, 1);
        off = off.lengthSqr() < 1e-4 ? new Vec3(1, 0, 0) : off.normalize();
        Vec3 to = keep < 0 ? position().add(off.scale(6)) : target.position();
        if (getNavigation().isDone() || tickCount % 20 == 0) getNavigation().moveTo(to.x, to.y, to.z, keep < 0 ? 1.2 : 1);
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.TURTLE_EGG_BREAK; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.TURTLE_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SHULKER_HURT_CLOSED; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.TURTLE_DEATH; }
    @Override public float getVoicePitch() { return .5F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public BarrageSkill barrage() { return barrage; }
    public SpikeFieldSkill spikes() { return spikes; }
}
