package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.GrabSkill;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.LurkerRules;
import com.miophas.corruption.core.elite.LurkerRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * The corrupted lurker, the sea's elite (LurkerRules): a bloated, many-eyed horror trailing tentacles, the corruption
 * leaking out of it. It hunts as every sea elite does (darting in the water, surfacing at the edge for a quarry ashore);
 * it lashes a tentacle out to drag its quarry in, or shoots bolts of the corruption's water at it that throw it back.
 */
public final class CorruptedLurker extends SwimmingElite<CorruptedLurker> implements Lashing {
    /** Its colours: its tentacles' rose, its water's violet, and a pale violet for what is brightest. */
    public static final int ROSE = 0xE0609A, WATER = 0x8A5AE0, FOAM = 0xE0D0FF;
    /** Its maw, in its own frame from its feet ({right, up, ahead}, in blocks): what it shoots leaves it there, and its tentacle. */
    public static final double[] MAW = {0, .45, 1};

    private static final EntityDataAccessor<Integer> REACHING = SynchedEntityData.defineId(CorruptedLurker.class, EntityDataSerializers.INT);

    private final GrabSkill grab = new GrabSkill(LurkerRules.GRAB, ROSE, FOAM);
    private final SpitSkill bolts = new SpitSkill(LurkerRules.BOLTS, WATER, FOAM);
    /** Its skills, in the order of LurkerRules.SKILLS. */
    private final SkillSet<CorruptedLurker> skills = SkillSet.<CorruptedLurker>of()
            .with("grab", grab)
            .with("bolts", bolts);

    public CorruptedLurker(EntityType<? extends CorruptedLurker> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, LurkerRules.MAX_HEALTH).add(Attributes.ARMOR, LurkerRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, LurkerRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, LurkerRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, LurkerRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, LurkerRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedLurker elite() { return this; }
    @Override public SkillSet<CorruptedLurker> skills() { return skills; }
    @Override protected float wander() { return LurkerRules.WANDER; }
    @Override protected float crawl() { return LurkerRules.CRAWL; }
    @Override protected double dart() { return LurkerRules.DART; }
    @Override protected double thrust() { return LurkerRules.THRUST; }
    @Override protected double strikeReach() { return LurkerRules.BITE_REACH; }
    @Override protected int strikeEvery() { return LurkerRules.BITE_EVERY; }
    @Override protected double[] mouth() { return MAW; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REACHING, -1);
    }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : LurkerRules.SKILLS) if (skills.usable(index(skill), this, target)) ready.add(skill);
        Skill next = LurkerRules.choose(distanceTo(target), target.isInWater(), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    private static int index(Skill skill) { return LurkerRules.SKILLS.indexOf(skill); }

    /** It hunts as every sea elite does; and what its tentacle reaches for is shown to every client. */
    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        LivingEntity reaching = skill() == Skill.GRAB.ordinal() ? grab.reachingFor() : null;
        entityData.set(REACHING, reaching == null ? -1 : reaching.getId());
        super.act(target, busy);
    }

    @Override public boolean doHurtTarget(Entity target) {
        playSound(SoundEvents.GUARDIAN_ATTACK, 1, .6F);
        return super.doHurtTarget(target);
    }

    /** What its tentacle reaches for or holds, as every client sees it; null if nothing. */
    @Override @Nullable public Entity reaching() {
        int id = entityData.get(REACHING);
        return id < 0 ? null : level().getEntity(id);
    }

    /** Its tentacle leaves its maw. */
    @Override public double[] lashFrom() { return MAW; }

    /** Through the grab's wind-up its tentacle reaches out, the further the later; it holds at full length. */
    @Override public float lashOut(float partial) {
        return skill() != Skill.GRAB.ordinal() ? 1 : net.minecraft.util.Mth.clamp(skillTicks(partial) / Math.max(1, LurkerRules.GRAB.timing().windup()), .1F, 1);
    }

    /** The corruption leaks out of it: a murk in the water about it, drips out of it. */
    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide || !isAlive()) return;
        if (isInWater()) {
            if (random.nextInt(2) == 0)
                level().addParticle(ModParticles.CORRUPTION_PARTICLE.get(), getRandomX(.9), getY() + random.nextDouble() * getBbHeight(), getRandomZ(.9),
                        0, .01, 0);
        } else if (random.nextInt(3) == 0)
            level().addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, getRandomX(.7), getY() + random.nextDouble() * getBbHeight() * .6, getRandomZ(.7), 0, 0, 0);
    }

    @Override protected SoundEvent getAmbientSound() { return isInWater() ? SoundEvents.ELDER_GUARDIAN_AMBIENT : SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return isInWater() ? SoundEvents.GUARDIAN_HURT : SoundEvents.GUARDIAN_HURT_LAND; }
    @Override protected SoundEvent getDeathSound() { return isInWater() ? SoundEvents.ELDER_GUARDIAN_DEATH : SoundEvents.ELDER_GUARDIAN_DEATH_LAND; }
    @Override protected SoundEvent getSwimSound() { return SoundEvents.DOLPHIN_SWIM; }
    @Override public float getVoicePitch() { return .7F + (random.nextFloat() - random.nextFloat()) * .05F; }
    @Override public int getAmbientSoundInterval() { return 160; }

    /** Its skills, for whoever would look at what they did. */
    public GrabSkill grab() { return grab; }
    public SpitSkill bolts() { return bolts; }
}
