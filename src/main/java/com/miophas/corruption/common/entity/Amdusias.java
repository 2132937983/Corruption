package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.BossDeath;
import com.miophas.corruption.api.demon.GoeticDemon;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.common.entity.skill.BlightSkill;
import com.miophas.corruption.common.entity.skill.CrescendoSkill;
import com.miophas.corruption.common.entity.skill.FanfareSkill;
import com.miophas.corruption.common.entity.skill.MeleeSkill;
import com.miophas.corruption.common.entity.skill.PhaseDashSkill;
import com.miophas.corruption.common.entity.skill.PsychicSkill;
import com.miophas.corruption.common.entity.skill.SpikeFieldSkill;
import com.miophas.corruption.common.entity.skill.UpheavalSkill;
import com.miophas.corruption.core.demon.AmdusiasRules.Skill;
import com.miophas.corruption.core.demon.AmdusiasRules;
import com.miophas.corruption.core.demon.BodyPart;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Amdusias, the sixty-seventh spirit of the Goetia: a great duke who comes first as a unicorn, at whose bidding trumpets
 * and every kind of instrument are heard and no one is seen playing, and the trees bow down; brought forth at the altar
 * (data/corruption/recipe/altar/amdusias.json) for a horse. What he does and when is {@link AmdusiasRules}; his blows are
 * the modules every boss may share (common.entity.skill), and this carries them out.
 *
 * <p>He is the unicorn himself, a great one, and goes on the ground only: he cannot fly, and he is one shape from first to
 * last. His hooves, his blinks and his spikes are Seere's; his own are the blight he stamps into the ground about him, the
 * ground he heaves up, breaks open or spears under his quarry, his strike at its mind, the fanfare of bolts his horn plays
 * (they leave from its tip: {@link #spitFrom}), and the rings of music that swell out from him. As every
 * {@link GoeticDemon}, only the arcane can hurt him.
 */
public final class Amdusias extends GoeticDemon implements SpitCaster {
    /** His colours: a pale green as of new leaves, and a white of pearl where it is brightest; and the gold of his horn's bands. */
    public static final int MINT = 0x50D890, PEARL = 0xE8FFF2, GOLD = 0xF2C46A;
    /** The tip of his horn, in his own frame from his feet ({right, up, ahead}, in blocks): his fanfare leaves it there. */
    public static final double[] HORN = {0, 2.75, 2.45};
    /** His body's hitboxes: his forequarters, his hindquarters, his head on its neck. */
    public static final List<BodyPart> BODY = List.of(
            new BodyPart("chest", .8, 0, .9, 1.9, 1.9),
            new BodyPart("rump", -1.2, 0, .9, 1.8, 1.8),
            new BodyPart("head", 2.0, 0, 2.3, 1.1, 1.2));

    /**
     * His death: his last call on his horn, music ringing out about him; a ring of force from where he falls; the souls he
     * held rising; his horn shattering; then he bursts in his pale green light, a ring of it racing out and a pillar of it
     * standing where he fell.
     */
    public static final BossDeath DEATH = new BossDeath(AmdusiasRules.DEATH_TICKS, 50, 1.4F, new BossDeath.Rays(18, 7, MINT, PEARL), .15F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "resonance", 4, 3, 60, MINT, PEARL, .2, "minecraft:item.goat_horn.sound.5", 4, .6F),
            BossDeath.beat(4, BossDeath.Anchor.FEET, "shockwave", 6, 1.5, 24, MINT, PEARL, .3, "minecraft:entity.horse.death", 4, .5F),
            BossDeath.beat(20, BossDeath.Anchor.FEET, "souls", 2.4, 6, 46, MINT, PEARL, 0, "minecraft:particle.soul_escape", 4, .7F),
            BossDeath.beat(36, BossDeath.Anchor.HEART, "shards", 2.4, 2, 30, MINT, PEARL, 0, "minecraft:block.amethyst_block.break", 4, .5F),
            BossDeath.beat(66, BossDeath.Anchor.HEART, "nova", 10, 0, 26, MINT, 0xFFFFFF, .6, "minecraft:entity.generic.explode", 4, .8F),
            BossDeath.beat(66, BossDeath.Anchor.FEET, "shockwave", 10, 2, 26, MINT, PEARL, 0, "", 0, 1),
            BossDeath.beat(67, BossDeath.Anchor.FEET, "light_pillar", 1.4, 30, 44, MINT, PEARL, 0, "minecraft:block.beacon.deactivate", 4, .6F)));

    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(Amdusias.class, EntityDataSerializers.BYTE);

    private final BlightSkill blight = new BlightSkill(AmdusiasRules.BLIGHT, MINT, PEARL);
    private final UpheavalSkill upheaval = new UpheavalSkill(AmdusiasRules.UPHEAVAL, MINT, PEARL);
    private final PsychicSkill psychic = new PsychicSkill(AmdusiasRules.PSYCHIC, MINT, PEARL);
    private final FanfareSkill fanfare = new FanfareSkill(AmdusiasRules.FANFARE, MINT, PEARL);
    private final CrescendoSkill crescendo = new CrescendoSkill(AmdusiasRules.CRESCENDO, MINT, PEARL);
    /** His blows, in the order of AmdusiasRules.BLOWS. */
    private final SkillSet<Amdusias> skills = SkillSet.<Amdusias>of()
            .with("hooves", new MeleeSkill(AmdusiasRules.HOOVES))
            .with("blinks", new PhaseDashSkill(AmdusiasRules.BLINKS, MINT, PEARL))
            .with("spikes", new SpikeFieldSkill(AmdusiasRules.SPIKES, MINT, PEARL))
            .with("blight", blight)
            .with("upheaval", upheaval)
            .with("psychic", psychic)
            .with("fanfare", fanfare)
            .with("crescendo", crescendo);
    private int rest = 30;
    /** On a client: the tick his skill last changed, and the skill to pose him by (it settles after it is over). */
    private int skillSeen;
    private Skill posed = Skill.NONE;

    public Amdusias(EntityType<? extends Amdusias> type, Level level) {
        super(type, level, BossEvent.BossBarColor.GREEN, BODY);
        xpReward = AmdusiasRules.EXPERIENCE;
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, AmdusiasRules.MAX_HEALTH).add(Attributes.ARMOR, AmdusiasRules.ARMOR)
                .add(Attributes.FOLLOW_RANGE, AmdusiasRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, AmdusiasRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, AmdusiasRules.SPEED).add(Attributes.STEP_HEIGHT, AmdusiasRules.STEP_HEIGHT)
                .add(Attributes.ATTACK_DAMAGE, AmdusiasRules.HOOVES.damage());
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKILL, (byte) 0);
    }

    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        huntTheUncorrupted(2);
    }

    // ---- his mind ----

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target.level() != level())) target = null;
        if (rest > 0) rest--;
        if (!skills.busy() && target != null && rest == 0) choose(target);
        skills.tick(this, target, cooldown -> cooldown);
        if (!skills.busy() && skill() != Skill.NONE) {
            setSkill(Skill.NONE);
            rest = AmdusiasRules.REST;
        }
        if (!skills.busy()) gallop(target);
    }

    private void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill blow : AmdusiasRules.BLOWS) if (skills.usable(index(blow), this, target)) ready.add(blow);
        double near = AmdusiasRules.CRESCENDO.radius() / 2;
        int about = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(near), other -> other != this && other.isAlive() && !isAlliedTo(other)
                && !(other instanceof Player player && (player.isCreative() || player.isSpectator())) && distanceTo(other) <= near).size();
        AmdusiasRules.Situation now = new AmdusiasRules.Situation(distanceTo(target), target.getY() - getY(), hasLineOfSight(target),
                Mth.wrapDegrees(Mth.atan2(target.getZ() - getZ(), target.getX() - getX()) * Mth.RAD_TO_DEG - 90 - getYRot()), about, ready);
        Skill next = AmdusiasRules.choose(now, random.nextDouble());
        if (next != Skill.NONE) begin(next, target);
    }

    /** Begins one of his blows on his quarry, if it has come round; whether it began (package-private, for the tests to begin one by hand). */
    boolean begin(Skill blow, LivingEntity target) {
        if (!skills.begin(index(blow), this, target)) return false;
        setSkill(blow);
        return true;
    }

    private static int index(Skill blow) { return AmdusiasRules.BLOWS.indexOf(blow); }

    private void setSkill(Skill skill) { entityData.set(SKILL, (byte) skill.ordinal()); }

    /** After his quarry at a gallop, up to his hooves' reach of it. */
    private void gallop(@Nullable LivingEntity target) {
        if (target == null) return;
        if (distanceTo(target) > AmdusiasRules.HOOVES.reach() * .7) getNavigation().moveTo(target, 1);
        else getNavigation().stop();
        getLookControl().setLookAt(target, 40, 40);
    }

    @Override public void die(DamageSource source) {
        skills.cease(this);
        setSkill(Skill.NONE);
        super.die(source);
    }

    // ---- what the client sees ----

    public Skill skill() {
        int index = entityData.get(SKILL);
        return index >= 0 && index < Skill.values().length ? Skill.values()[index] : Skill.NONE;
    }

    /** On a client: the skill to pose him by, the one he last began; its pose eases back by itself once it is over. */
    public Skill posedSkill() { return posed; }

    /** On a client: ticks since the skill to pose him by began. */
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key) && skill() != Skill.NONE) {
            posed = skill();
            skillSeen = tickCount;
        }
    }

    /** The tip of his horn, where his fanfare leaves him: the way he faces. */
    @Override public Vec3 spitFrom(int index) {
        Vec3 forward = Vec3.directionFromRotation(0, getYRot()), right = new Vec3(-forward.z, 0, forward.x);
        return position().add(right.scale(HORN[0])).add(0, HORN[1], 0).add(forward.scale(HORN[2]));
    }

    /** His heart: in his breast. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() * .5, 0).add(Vec3.directionFromRotation(0, getYRot()).scale(.6)); }

    /** Notes rise off his horn now and then, and pale fire licks about his hooves as he gallops. */
    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide || !isAlive()) return;
        if (random.nextInt(12) == 0) {
            Vec3 horn = spitFrom(0);
            level().addParticle(ParticleTypes.NOTE, horn.x, horn.y + .2, horn.z, random.nextDouble(), 0, 0);
        }
        if (walkAnimation.speed() > .3F && random.nextInt(2) == 0)
            level().addParticle(ParticleTypes.HAPPY_VILLAGER, getRandomX(.9), getY() + .1, getRandomZ(.9), 0, .02, 0);
    }

    @Override public int emblem() { return 7; }
    @Override public BossDeath death() { return DEATH; }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.HORSE_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.HORSE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.HORSE_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.HORSE_GALLOP, .6F, .7F); }
    @Override public float getVoicePitch() { return .7F; }
    @Override public int getAmbientSoundInterval() { return 140; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Skills", skills.save());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        skills.load(tag.getCompound("Skills"));
    }

    /** His blows, as they stand. */
    public SkillSet<Amdusias> skills() { return skills; }
    public BlightSkill blight() { return blight; }
    public UpheavalSkill upheaval() { return upheaval; }
    public PsychicSkill psychic() { return psychic; }
    public FanfareSkill fanfare() { return fanfare; }
    public CrescendoSkill crescendo() { return crescendo; }
}
