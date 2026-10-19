package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.BossDeath;
import com.miophas.corruption.api.demon.GoeticDemon;
import com.miophas.corruption.api.demon.skill.BeamCaster;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BeamSkill;
import com.miophas.corruption.common.entity.skill.BlinkSkill;
import com.miophas.corruption.common.entity.skill.GazeSkill;
import com.miophas.corruption.common.entity.skill.MindControlSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.StrikeSkill;
import com.miophas.corruption.common.entity.skill.SummonSkill;
import com.miophas.corruption.core.demon.DantalionRules.Skill;
import com.miophas.corruption.core.demon.DantalionRules;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Dantalion, the seventy-first spirit of the Goetia: the duke of many faces, who knows the thoughts of all and turns
 * them, brought forth at the altar (data/corruption/recipe/altar/dantalion.json). What he does and when is
 * {@link DantalionRules}; his skills are the modules every boss may share (common.entity.skill), and this carries them
 * out. He flies, a great book, keeping his distance from his quarry a few blocks up; brought to half his health he
 * changes, for three seconds untouchable, into a great mirror, and has his mirror's skills besides. As every
 * {@link GoeticDemon}, only the arcane can hurt him.
 */
public final class Dantalion extends GoeticDemon implements BeamCaster {
    /** His colours: the corruption's violet, and a pale gold where it is brightest. */
    public static final int VIOLET = 0x8A4CFF, GOLD = 0xFFDFA0;
    /** His beam's: violet round a white-hot core. */
    public static final int BEAM = 0xA070FF, BEAM_CORE = 0xF6EEFF;

    /**
     * His death: the thoughts he took flee him as souls, his eye opens over him and closes for the last time, shards of
     * his mirror (or the pages of his book) whirl round him, and he falls to the ground; and there he bursts in his
     * violet light, a ring of it racing out and a pillar of it standing where he fell.
     */
    public static final BossDeath DEATH = new BossDeath(DantalionRules.DEATH_TICKS, 50, 1.2F, new BossDeath.Rays(16, 6, VIOLET, GOLD), .1F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "shards", 2.4, 0, 70, VIOLET, GOLD, 0, "minecraft:block.glass.break", 4, .5F),
            BossDeath.beat(2, BossDeath.Anchor.FEET, "eye", 2.6, 5, 70, VIOLET, GOLD, .2, "minecraft:entity.elder_guardian.curse", 4, .6F),
            BossDeath.beat(20, BossDeath.Anchor.FEET, "souls", 2, 6, 52, VIOLET, GOLD, 0, "minecraft:particle.soul_escape", 4, .7F),
            BossDeath.beat(40, BossDeath.Anchor.FEET, "rune_ring", 2.6, 1.6, 38, VIOLET, GOLD, 0, "minecraft:entity.illusioner.mirror_move", 4, .6F),
            BossDeath.beat(76, BossDeath.Anchor.HEART, "nova", 9, 0, 26, VIOLET, 0xFFFFFF, .6, "minecraft:entity.generic.explode", 4, .8F),
            BossDeath.beat(76, BossDeath.Anchor.FEET, "shockwave", 9, 2, 26, VIOLET, GOLD, 0, "", 0, 1),
            BossDeath.beat(77, BossDeath.Anchor.FEET, "light_pillar", 1.3, 28, 44, VIOLET, 0xF6EEFF, 0, "minecraft:block.beacon.deactivate", 4, .6F)));

    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(Dantalion.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> MIRROR = SynchedEntityData.defineId(Dantalion.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHANGING = SynchedEntityData.defineId(Dantalion.class, EntityDataSerializers.INT);
    /** Whose mind he holds: 0 none, 1 a mind held, 2 a mind and its feet. */
    private static final EntityDataAccessor<Byte> HOLD = SynchedEntityData.defineId(Dantalion.class, EntityDataSerializers.BYTE);
    /** Ticks he rests between one skill and the next, as the book and as the mirror. */
    private static final int REST = 24, MIRROR_REST = 16;
    /** How fast he gathers speed towards where he wants to be, a tick, and how far round he turns to face his quarry. */
    private static final double THRUST = .045;
    private static final float TURN = 12;

    private final BlinkSkill blink = new BlinkSkill(DantalionRules.BLINK, VIOLET, GOLD);
    private final MindControlSkill mindControl = new MindControlSkill(DantalionRules.MIND_CONTROL, VIOLET, GOLD);
    private final MindControlSkill greaterMindControl = new MindControlSkill(DantalionRules.GREATER_MIND_CONTROL, VIOLET, GOLD);
    /** His skills, in the order of DantalionRules.SKILLS. */
    private final SkillSet<Dantalion> skills = SkillSet.<Dantalion>of()
            .with("gaze", new GazeSkill(DantalionRules.GAZE, VIOLET, GOLD))
            .with("greater_gaze", new GazeSkill(DantalionRules.GREATER_GAZE, VIOLET, GOLD))
            .with("summon", new SummonSkill(DantalionRules.SUMMON, VIOLET, GOLD))
            .with("blink", blink)
            .with("strike", new StrikeSkill(DantalionRules.STRIKE, VIOLET, GOLD))
            .with("storm", new StrikeSkill(DantalionRules.STORM, VIOLET, GOLD))
            .with("beam", new BeamSkill(DantalionRules.BEAM, BEAM, BEAM_CORE))
            .with("mind_control", mindControl)
            .with("greater_mind_control", greaterMindControl);
    private int rest = 40, wander;
    /** His health as his change began: he is healed from it to the full as he changes. */
    private float changedFrom;
    private double hoverAt = DantalionRules.HOVER_LOW + 1;
    /** On a client, the tick his skill and his change last began, and his hold last changed, to pose them from. */
    private int skillSeen, changeSeen, holdSeen = -1000;
    /** On a client, the skill he last began: it goes on being posed after it is over, as he settles back from it. */
    private Skill posed = Skill.NONE;
    /** On a client, whether the last mind he held, he held hard. */
    private boolean heldHard;

    public Dantalion(EntityType<? extends Dantalion> type, Level level) {
        super(type, level, BossEvent.BossBarColor.PURPLE);
        xpReward = 80;
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, DantalionRules.MAX_HEALTH).add(Attributes.ARMOR, DantalionRules.ARMOR)
                .add(Attributes.FOLLOW_RANGE, DantalionRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, DantalionRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FLYING_SPEED, DantalionRules.FLY_SPEED);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKILL, (byte) 0);
        builder.define(MIRROR, false);
        builder.define(CHANGING, 0);
        builder.define(HOLD, (byte) 0);
    }

    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        huntTheUncorrupted(2);
    }

    // ---- his mind ----

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = getTarget();
        if (target != null && !target.isAlive()) target = null;
        if (changing() > 0) {
            change();
            hover(target, true);
            return;
        }
        if (!mirror() && DantalionRules.changes(getHealth() / getMaxHealth())) {
            beginChange();
            return;
        }
        boolean unsafe = blink.weigh(this, target);
        if (rest > 0) rest--;
        if (!skills.busy() && target != null && (rest == 0 || unsafe)) choose(target, unsafe);
        skills.tick(this, target, cooldown -> DantalionRules.cooldown(cooldown, mirror()));
        if (!skills.busy() && skill() != Skill.NONE) {
            entityData.set(SKILL, (byte) 0);
            rest = mirror() ? MIRROR_REST : REST;
        }
        entityData.set(HOLD, (byte) (greaterMindControl.held() != null ? 2 : mindControl.held() != null ? 1 : 0));
        hover(target, skills.busy());
    }

    private void choose(LivingEntity target, boolean unsafe) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : DantalionRules.SKILLS) if (skills.usable(index(skill), this, target)) ready.add(skill);
        boolean impaired = target.hasEffect(MobEffects.BLINDNESS) || target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
        DantalionRules.Situation now = new DantalionRules.Situation(distanceTo(target), hasLineOfSight(target), mirror(), unsafe, SummonSkill.called(this),
                impaired, target instanceof Player, held(target), ready);
        Skill next = DantalionRules.choose(now, random.nextDouble());
        if (next != Skill.NONE && skills.begin(index(next), this, target)) entityData.set(SKILL, (byte) next.ordinal());
    }

    private static int index(Skill skill) { return DantalionRules.SKILLS.indexOf(skill); }

    /** Whether he holds this creature's mind now. */
    public boolean held(LivingEntity target) { return mindControl.held() == target || greaterMindControl.held() == target; }

    /**
     * Where he flies: round his quarry at the distance he keeps, a few blocks over the ground, drifting slowly round it
     * (and, at a skill, holding nearly still); with none, he hangs where he is. He always turns to face it.
     */
    private void hover(LivingEntity target, boolean still) {
        Vec3 want;
        if (target == null) want = new Vec3(getX(), groundUnder() + hoverAt, getZ());
        else {
            Vec3 from = target.position(), off = position().subtract(from).multiply(1, 0, 1);
            if (off.lengthSqr() < 1e-4) off = new Vec3(1, 0, 0);
            double distance = Math.sqrt(off.lengthSqr()), keep = Mth.clamp(distance, DantalionRules.NEAR + 1, DantalionRules.FAR - 2);
            if (DantalionRules.keep(distance) != 0) keep = DantalionRules.KEEP_AT;
            if (--wander <= 0) { wander = 60 + random.nextInt(80); hoverAt = Mth.nextDouble(random, DantalionRules.HOVER_LOW, DantalionRules.HOVER_HIGH); }
            // Drifting round it, a little each tick, unless a skill holds him.
            Vec3 round = still ? off : off.yRot((float) Math.toRadians(.8 * (wander % 200 < 100 ? 1 : -1)));
            Vec3 at = from.add(round.normalize().scale(keep));
            want = new Vec3(at.x, Math.max(groundUnder(at) + hoverAt, target.getY() + 1.5), at.z);
            faceTowards(target);
        }
        Vec3 to = want.subtract(position());
        double speed = (still ? .35 : 1) * getAttributeValue(Attributes.FLYING_SPEED);
        Vec3 push = to.lengthSqr() > 1e-4 ? to.normalize().scale(Math.min(THRUST, to.length() * .05)) : Vec3.ZERO;
        Vec3 moving = getDeltaMovement().add(push);
        if (moving.length() > speed) moving = moving.normalize().scale(speed);
        setDeltaMovement(moving);
    }

    private void faceTowards(LivingEntity target) {
        Vec3 to = target.getEyePosition().subtract(getEyePosition());
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90, pitch = (float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG);
        setYRot(Mth.approachDegrees(getYRot(), yaw, TURN));
        setXRot(Mth.approachDegrees(getXRot(), Mth.clamp(pitch, -45, 45), TURN / 2));
        yBodyRot = getYRot();
        yHeadRot = getYRot();
    }

    private double groundUnder() { return groundUnder(position()); }

    private double groundUnder(Vec3 at) {
        int x = Mth.floor(at.x), z = Mth.floor(at.z);
        if (!level().hasChunk(x >> 4, z >> 4)) return at.y - hoverAt;
        double top = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        // Under a roof (a cave, a hall), the floor under him, not the roof over it.
        return top > at.y + 1 ? at.y - hoverAt : top;
    }

    /** He flies: no gravity, no ground to walk on, the air holding him back. */
    @Override public void travel(Vec3 input) {
        if (isControlledByLocalInstance()) {
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(.86));
        }
        calculateEntityAnimation(false);
    }

    @Override public boolean onClimbable() { return false; }

    // ---- his change ----

    private void beginChange() {
        changedFrom = getHealth();
        skills.interrupt(this, cooldown -> DantalionRules.cooldown(cooldown, false));
        entityData.set(SKILL, (byte) 0);
        entityData.set(CHANGING, DantalionRules.CHANGE_TICKS);
        playSound(SoundEvents.BOOK_PAGE_TURN, 3, .5F);
        playSound(SoundEvents.ELDER_GUARDIAN_CURSE, 3, .5F);
        if (level() instanceof ServerLevel level) {
            SkillShows.show(level, position(), "vortex", 3, 4, DantalionRules.CHANGE_TICKS, VIOLET, GOLD, .3, getYRot(), true);
            level.sendParticles(ParticleTypes.ENCHANT, getX(), getY() + getBbHeight() / 2, getZ(), 120, 1.5, 1.5, 1.5, 1);
        }
    }

    /**
     * A tick of his change: his health fills back up as it goes, all of it by its end; at its middle the book is gone
     * and the mirror is there; at its end, it is done.
     */
    private void change() {
        int left = changing() - 1;
        entityData.set(CHANGING, left);
        setHealth(left == 0 ? getMaxHealth() : DantalionRules.healthAt(changedFrom, getMaxHealth(), 1 - left / (float) DantalionRules.CHANGE_TICKS));
        if (!(level() instanceof ServerLevel level)) return;
        if (left % 6 == 0) level.sendParticles(ParticleTypes.ENCHANT, getX(), getY() + getBbHeight() / 2, getZ(), 30, 1, 1, 1, .8);
        if (left == DantalionRules.CHANGE_TICKS / 2) {
            entityData.set(MIRROR, true);
            playSound(SoundEvents.GLASS_BREAK, 3, .5F);
            playSound(SoundEvents.ILLUSIONER_MIRROR_MOVE, 3, .6F);
            SkillShows.show(level, getBoundingBox().getCenter(), "nova", 5, 0, 22, VIOLET, 0xFFFFFF, .5, getYRot(), false);
            SkillShows.show(level, getBoundingBox().getCenter(), "shards", 2.6, 0, 30, VIOLET, GOLD, 0, getYRot(), false);
        }
        if (left == 0) {
            playSound(SoundEvents.ENDER_DRAGON_GROWL, 2, 1.4F);
            SkillShows.show(level, position(), "shockwave", 8, 2, 24, VIOLET, GOLD, .4, getYRot(), true);
            rest = MIRROR_REST;
        }
    }

    /** Through his change nothing hurts him but what passes every defence. */
    @Override public boolean hurt(DamageSource source, float amount) {
        if (changing() > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return super.hurt(source, amount);
    }

    @Override public void die(DamageSource source) {
        skills.cease(this);
        entityData.set(SKILL, (byte) 0);
        entityData.set(HOLD, (byte) 0);
        super.die(source);
    }

    /** Gone from the world however he goes, he lets go of every mind he holds. */
    @Override public void remove(RemovalReason reason) {
        if (!level().isClientSide) skills.cease(this);
        super.remove(reason);
    }

    /** Dying, he sinks to the ground, for his light to burst there. */
    @Override protected void tickDeath() {
        if (!onGround()) setDeltaMovement(getDeltaMovement().multiply(.6, 1, .6).add(0, -.06, 0));
        super.tickDeath();
    }

    // ---- what the client sees ----

    public Skill skill() {
        int index = entityData.get(SKILL);
        return index >= 0 && index < Skill.values().length ? Skill.values()[index] : Skill.NONE;
    }

    /** Whether he is the mirror now. */
    public boolean mirror() { return entityData.get(MIRROR); }

    /** Ticks left of his change, 0 when he is not changing. */
    public int changing() { return entityData.get(CHANGING); }

    /** On a client: the skill to pose him by, the one he last began; its pose eases back to rest by itself once it is over. */
    public Skill posedSkill() { return posed; }

    /** On a client: ticks since the skill to pose him by began. */
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    /** Whether he holds a mind now: 0 none, 1 a mind held, 2 a mind and its feet. */
    public int hold() { return entityData.get(HOLD); }

    /** On a client: how hard he holds a mind, 0 to 1: wholly while he holds it, fading over a few ticks once he lets go. */
    public float holding(float partial) { return hold() > 0 ? 1 : 1 - Mth.clamp((tickCount - holdSeen + partial) / 6, 0, 1); }

    /** On a client: whether the mind he holds, or last held, he holds hard (its feet too). */
    public boolean heldHard() { return heldHard; }

    /** On a client: how far through his change he is, 0 to 1; 0 when he is not changing. */
    public float changed(float partial) {
        return changing() <= 0 ? 0 : Mth.clamp((tickCount - changeSeen + partial) / DantalionRules.CHANGE_TICKS, 0, 1);
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key) && skill() != Skill.NONE) {
            posed = skill();
            skillSeen = tickCount;
        }
        if (CHANGING.equals(key) && changing() == DantalionRules.CHANGE_TICKS) {
            changeSeen = tickCount;
            posed = Skill.NONE;
        }
        if (MIRROR.equals(key)) refreshDimensions();
        if (HOLD.equals(key)) {
            holdSeen = tickCount;
            if (hold() > 0) heldHard = hold() == 2;
        }
    }

    /** The book, or the mirror, standing taller. */
    @Override public EntityDimensions getDefaultDimensions(Pose pose) {
        return mirror() ? EntityDimensions.scalable(DantalionRules.MIRROR_WIDTH, DantalionRules.MIRROR_HEIGHT)
                : EntityDimensions.scalable(DantalionRules.BOOK_WIDTH, DantalionRules.BOOK_HEIGHT);
    }

    /** His beam leaves from the face of his mirror, a little before the glass, the way he faces. */
    @Override public Vec3 beamFrom() {
        return position().add(0, DantalionRules.MIRROR_HEIGHT * .55, 0).add(getViewVector(1).scale(.5));
    }

    /** His heart: the middle of his book, or of his glass. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() * (mirror() ? .55 : .5), 0); }

    /** Pages flutter off him as the book, and motes of glass glint off him as the mirror. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && random.nextInt(4) == 0 && isAlive())
            level().addParticle(mirror() ? ParticleTypes.END_ROD : ParticleTypes.ENCHANT, getRandomX(.9), getY() + random.nextDouble() * getBbHeight(),
                    getRandomZ(.9), 0, .02, 0);
    }

    @Override public int emblem() { return 3; }
    @Override public BossDeath death() { return DEATH; }

    @Override protected SoundEvent getAmbientSound() { return mirror() ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.BOOK_PAGE_TURN; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return mirror() ? SoundEvents.GLASS_HIT : SoundEvents.BOOK_PUT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ILLUSIONER_DEATH; }
    @Override public float getVoicePitch() { return .55F; }
    @Override public int getAmbientSoundInterval() { return 120; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Mirror", mirror());
        tag.put("Skills", skills.save());
        if (changing() > 0) {
            tag.putInt("Changing", changing());
            tag.putFloat("ChangedFrom", changedFrom);
        }
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(MIRROR, tag.getBoolean("Mirror"));
        skills.load(tag.getCompound("Skills"));
        entityData.set(CHANGING, Math.max(0, tag.getInt("Changing")));
        changedFrom = tag.getFloat("ChangedFrom");
    }

    /** His skills, as they stand. */
    public SkillSet<Dantalion> skills() { return skills; }
}
