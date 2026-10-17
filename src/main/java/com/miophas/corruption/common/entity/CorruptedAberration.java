package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PounceSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.common.registry.ModSounds;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.elite.AberrationRules;
import com.miophas.corruption.core.elite.AberrationRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * The corrupted aberration, the land's elite (AberrationRules): a low, twisted mass of flesh on six legs, crusted with the
 * corruption's crystals, its maw splitting four ways. It runs its quarry down and bites; from further off it pounces, or
 * spits globs of acid that leave a cursed pool where they burst; and brought low it roars and goes berserk for the rest
 * of its life, faster, harder-hitting and quicker to its skills, its crystals blazing.
 */
public final class CorruptedAberration extends WalkingElite<CorruptedAberration> {
    /** Its colours: its flesh's rose, a pale rose for what is brightest, and its acid's violet. */
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, ACID = 0xB040C8;
    /** Its maw, in its own frame from its feet ({right, up, ahead}, in blocks): what it spits leaves it there. */
    public static final double[] MAW = {0, .72, 1.2};

    private static final EntityDataAccessor<Boolean> BERSERK = SynchedEntityData.defineId(CorruptedAberration.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation BERSERK_SPEED = ResourceLocation.fromNamespaceAndPath("corruption", "aberration_berserk_speed"),
            BERSERK_DAMAGE = ResourceLocation.fromNamespaceAndPath("corruption", "aberration_berserk_damage");

    private final PounceSkill pounce = new PounceSkill(AberrationRules.POUNCE, ROSE, PALE);
    private final SpitSkill acid = new SpitSkill(AberrationRules.ACID, ACID, PALE);
    /** Its skills, in the order of AberrationRules.SKILLS. */
    private final SkillSet<CorruptedAberration> skills = SkillSet.<CorruptedAberration>of()
            .with("pounce", pounce)
            .with("acid", acid)
            .with("roar", new Roar());

    public CorruptedAberration(EntityType<? extends CorruptedAberration> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, AberrationRules.MAX_HEALTH).add(Attributes.ARMOR, AberrationRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, AberrationRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, AberrationRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, AberrationRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, AberrationRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override protected CorruptedAberration elite() { return this; }
    @Override public SkillSet<CorruptedAberration> skills() { return skills; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BERSERK, false);
    }

    @Override protected double[] mouth() { return MAW; }
    @Override protected double chase() { return AberrationRules.CHASE; }
    @Override protected int strikeEvery() { return AberrationRules.BITE_EVERY; }

    // ---- its mind ----

    /** Brought low, it breaks off whatever it is about and roars, going berserk. */
    @Override protected void before(@Nullable LivingEntity target) {
        if (berserk() || skill() == Skill.ROAR.ordinal() || !AberrationRules.berserk(getHealth() / getMaxHealth())) return;
        skills.interrupt(this, this::cooldown);
        skills.comeRound(index(Skill.ROAR));
        begin(Skill.ROAR.ordinal(), target != null ? target : this);
    }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : AberrationRules.SKILLS) if (skill != Skill.ROAR && skills.usable(index(skill), this, target)) ready.add(skill);
        Skill next = AberrationRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    private static int index(Skill skill) { return AberrationRules.SKILLS.indexOf(skill); }

    @Override protected int cooldown(int cooldown) { return AberrationRules.cooldown(cooldown, berserk()); }

    // ---- its roar ----

    /**
     * Its roar as it goes berserk: rearing through the wind-up, then a roar that throws back everything not of its side
     * near it (and hurts nothing); it is berserk from then on.
     */
    private final class Roar implements BossSkill<CorruptedAberration> {
        @Override public SkillTiming timing() { return AberrationRules.ROAR; }
        /** It is never chosen: it comes when it goes berserk. */
        @Override public boolean usable(CorruptedAberration boss, LivingEntity target) { return false; }

        @Override public void begin(CorruptedAberration boss, LivingEntity target) {
            boss.getNavigation().stop();
            SkillShows.sound(boss, SoundEvents.RAVAGER_STUNNED, 2, .6F);
        }

        @Override public void tick(CorruptedAberration boss, @Nullable LivingEntity target, int elapsed) {
            boss.getNavigation().stop();
            boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
            if (AberrationRules.ROAR.strikes(elapsed)) boss.goBerserk();
        }
    }

    /** It goes berserk: faster, harder-hitting, its skills quicker to come round; and it roars, throwing back what is near. */
    void goBerserk() {
        if (berserk()) return;
        entityData.set(BERSERK, true);
        enrage();
        SkillShows.sound(this, SoundEvents.RAVAGER_ROAR, 3, .7F);
        if (!(level() instanceof ServerLevel level)) return;
        Vec3 at = position();
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(AberrationRules.ROAR_RADIUS), near -> near != this
                && near.isAlive() && !isAlliedTo(near) && near.distanceTo(this) <= AberrationRules.ROAR_RADIUS
                && !(near instanceof Player player && (player.isCreative() || player.isSpectator())))) {
            Vec3 away = near.position().subtract(at).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? new Vec3(1, 0, 0) : away.normalize();
            near.push(away.x * AberrationRules.ROAR_KNOCKBACK, .4, away.z * AberrationRules.ROAR_KNOCKBACK);
            near.hurtMarked = true;
        }
        SkillShows.show(level, at, "shockwave", AberrationRules.ROAR_RADIUS * 1.6, 2, 20, ROSE, PALE, .5, getYRot(), true);
        SkillShows.show(level, getBoundingBox().getCenter(), "resonance", 2.5, 1, 20, ROSE, PALE, 0, getYRot(), false);
        level.sendParticles(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), getX(), getY() + 1, getZ(), 40, 1, .6, 1, .15);
    }

    /** Its rage, as its attributes show it (kept off the save: it is put back as it is loaded). */
    private void enrage() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        var damage = getAttribute(Attributes.ATTACK_DAMAGE);
        if (speed != null) speed.addOrUpdateTransientModifier(new AttributeModifier(BERSERK_SPEED, AberrationRules.BERSERK_SPEED,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (damage != null) damage.addOrUpdateTransientModifier(new AttributeModifier(BERSERK_DAMAGE, AberrationRules.BERSERK_DAMAGE,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    /** Whether it has gone berserk. */
    public boolean berserk() { return entityData.get(BERSERK); }

    // ---- what it is ----

    /** Its crystals glint; berserk, the corruption sparks off it. */
    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide || !isAlive()) return;
        if (random.nextInt(berserk() ? 2 : 8) == 0)
            level().addParticle(berserk() ? ModParticles.CORRUPTION_SPARK_PARTICLE.get() : ModParticles.CORRUPTION_PARTICLE.get(),
                    getRandomX(.8), getY() + getBbHeight() * (.6 + random.nextDouble() * .5), getRandomZ(.8), 0, .02, 0);
    }

    @Override protected SoundEvent getAmbientSound() { return ModSounds.REMNANT_AMBIENT.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.REMNANT_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SPIDER_STEP, .5F, .6F); }
    @Override public float getVoicePitch() { return (berserk() ? .7F : .55F) + (random.nextFloat() - random.nextFloat()) * .05F; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Berserk", berserk());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(BERSERK, tag.getBoolean("Berserk"));
        if (berserk()) enrage();
    }

    /** Its skills, for whoever would look at what they did. */
    public PounceSkill pounce() { return pounce; }
    public SpitSkill acid() { return acid; }
}
