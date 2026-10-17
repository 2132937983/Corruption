package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.arcane.PreyMarks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.EliteRules;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * One of the corruption's elites (the entity type tag corruption:elites): between its remnants and its lords, as
 * EliteRules has it. What makes one is here, so that every one of them is alike in it:
 *
 * <ul>
 *   <li>It is of the corruption's side (give its type the tag corruption:corrupted_remnants as well): the corruption's
 *       own are its allies, everything else its prey, players first ({@link #huntTheUncorrupted}).</li>
 *   <li>Any blow hurts it; it needs no arcane, as a lord does.</li>
 *   <li>The corruption does it no harm: its curse takes no hold on it, and its ground, its waters and its hurt do
 *       nothing to it.</li>
 *   <li>A novice's corrupted robes do not hide their wearer from it; better ones do (CorruptionVeilApi).</li>
 *   <li>It has skills of its own in a {@link SkillSet}, one at a time, resting a little between one and the next; what
 *       it does while it uses none (its basic blow, how it moves) is its own to say ({@link #act}).</li>
 *   <li>It leaves what a remnant leaves: its loot table refers to the remnant's.</li>
 * </ul>
 *
 * An elite of an add-on's is made by extending this class, its type in both tags.
 *
 * @param <E> the elite itself, whose skills its set carries out
 */
public abstract class CorruptionElite<E extends CorruptionElite<E>> extends Monster {
    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(CorruptionElite.class, EntityDataSerializers.BYTE);
    /** The entity event that shows its basic blow (a bite, a club, a sting), for its model to strike with. */
    public static final byte BITE_EVENT = 4;

    /** Ticks it has to rest before it chooses its next skill. */
    protected int rest = EliteRules.REST;
    /** On a client: the skill to pose it by (the last it began, by its place in its rules' Skill, 0 for none), and the tick that began. */
    private int posed, skillSeen;
    /** On a client: the tick its last basic blow began. */
    private int bitSeen = -100;

    protected CorruptionElite(EntityType<? extends E> type, Level level) {
        super(type, level);
        xpReward = EliteRules.EXPERIENCE;
    }

    /** Itself, as its skills know it. */
    protected abstract E elite();

    /** Its skills. */
    public abstract SkillSet<E> skills();

    /** Chooses its next skill against this quarry, and begins it ({@link #begin}), if any would serve. */
    protected abstract void choose(LivingEntity target);

    /** What it does every tick besides its skills (moving, its basic blow), given its quarry and whether a skill runs. */
    protected abstract void act(@Nullable LivingEntity target, boolean busy);

    /** What it looks to before it chooses or runs a skill each tick (going berserk, say); nothing, unless it says otherwise. */
    protected void before(@Nullable LivingEntity target) {}

    /** A skill's cooldown as it serves it now (shorter when it is berserk, say). */
    protected int cooldown(int cooldown) { return cooldown; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKILL, (byte) 0);
    }

    /**
     * Turns it on whatever is not of the corruption: what strikes it first (at this priority), then players, then
     * anything living in sight that is not of the corruption. A robe it cannot see through hides its wearer from all three.
     */
    protected void huntTheUncorrupted(int priority) {
        targetSelector.addGoal(priority, new HurtByTargetGoal(this));
        targetSelector.addGoal(priority + 1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(priority + 2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, prey -> !CorruptionFactionApi.isRemnant(prey)));
    }

    // ---- its mind ----

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = quarry();
        before(target);
        if (rest > 0) rest--;
        if (!skills().busy() && target != null && rest == 0) choose(target);
        skills().tick(elite(), target, this::cooldown);
        if (!skills().busy() && skill() != 0) {
            entityData.set(SKILL, (byte) 0);
            rest = EliteRules.REST + random.nextInt(EliteRules.REST_SPREAD);
        }
        act(target, skills().busy());
    }

    /** Its quarry, while there is one alive in its world. */
    @Nullable protected LivingEntity quarry() {
        LivingEntity target = getTarget();
        return target != null && target.isAlive() && target.level() == level() ? target : null;
    }

    /**
     * Begins one of its skills on its quarry, if it has come round: the skill by its place in its rules' Skill (1 for the
     * first of its set, as NONE is 0); whether it began.
     */
    public boolean begin(int skill, LivingEntity target) {
        if (!skills().begin(skill - 1, elite(), target)) return false;
        entityData.set(SKILL, (byte) skill);
        return true;
    }

    /** The skill it is using, by its place in its rules' Skill; 0 for none. */
    public int skill() { return entityData.get(SKILL); }

    /** Where a place in its own frame ({right, up, ahead} in blocks, from its feet) is in the world, as it faces now. */
    public Vec3 inFrame(double[] at) {
        Vec3 forward = Vec3.directionFromRotation(0, getYRot()), right = new Vec3(-forward.z, 0, forward.x);
        return position().add(right.scale(at[0])).add(0, at[1], 0).add(forward.scale(at[2]));
    }

    /** Its basic blow is shown to every client. */
    @Override public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        level().broadcastEntityEvent(this, BITE_EVENT);
        return super.doHurtTarget(target);
    }

    @Override public void handleEntityEvent(byte event) {
        if (event == BITE_EVENT) bitSeen = tickCount;
        else super.handleEntityEvent(event);
    }

    /** On a client: ticks since its last basic blow began. */
    public float biteTicks(float partial) { return tickCount - bitSeen + partial; }

    // ---- its side ----

    /** Its own side is its allies, but for one marked as its prey, or hunting it (see PreyMarks). */
    @Override public boolean isAlliedTo(Entity entity) {
        return CorruptionFactionApi.isRemnant(entity) && !PreyMarks.feuding(this, entity) || super.isAlliedTo(entity);
    }

    /** Nothing of its own side, nor anything a robe it cannot see through hides (CorruptionVeilApi). */
    @Override public boolean canAttack(LivingEntity target) {
        return !isAlliedTo(target) && !CorruptionVeilApi.hiddenFrom(target, this) && super.canAttack(target);
    }

    @Override public boolean canAttackType(EntityType<?> type) { return !type.is(CorruptionFactionApi.REMNANTS) && super.canAttackType(type); }

    // ---- what reaches it ----

    /** The corruption's own hurt (its curse, its ground, its waters) does it no harm. */
    @Override public boolean isInvulnerableTo(DamageSource source) { return source.is(ModDamageTypes.CORRUPTION) || super.isInvulnerableTo(source); }

    /** The corruption's curse takes no hold on it. */
    @Override public boolean canBeAffected(MobEffectInstance effect) { return !effect.is(ModEffects.CORRUPTION_CURSE) && super.canBeAffected(effect); }

    @Override public void die(DamageSource source) {
        skills().cease(elite());
        entityData.set(SKILL, (byte) 0);
        if (level() instanceof ServerLevel server)
            server.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), getX(), getY() + getBbHeight() / 2, getZ(), 30, getBbWidth() / 2, getBbHeight() / 2,
                    getBbWidth() / 2, .05);
        super.die(source);
    }

    @Override public void remove(RemovalReason reason) {
        if (!level().isClientSide) skills().cease(elite());
        super.remove(reason);
    }

    // ---- what the client sees ----

    /** On a client: the skill to pose it by, the one it last began (its pose eases back by itself once it is over); 0 for none yet. */
    public int posedSkill() { return posed; }

    /** On a client: ticks since the skill to pose it by began. */
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key) && skill() != 0) {
            posed = skill();
            skillSeen = tickCount;
        }
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Skills", skills().save());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        skills().load(tag.getCompound("Skills"));
    }
}
