package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.entity.skill.ChargeSkill;
import com.miophas.corruption.common.entity.skill.GazeSkill;
import com.miophas.corruption.core.demon.AndromaliusRules;
import com.miophas.corruption.core.demon.AndromaliusRules.Attack;
import com.miophas.corruption.core.demon.AndromaliusRules.Intent;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Andromalius, the seventy-second spirit of the Goetia: a hell serpent, brought forth at the altar (see
 * data/corruption/recipe/altar/andromalius.json). What he does and when is {@link AndromaliusRules}: he circles his
 * quarry at a middle distance and works on it with his gaze and his fireballs, hunts it down every so often (and
 * at once when it is blinded or slowed) to bite it, lashes with his tail at whatever is behind him, raises his ward
 * when rushed, slithers aside from what is thrown at him, charges what he hunts or cannot see, smashing through what
 * stands in his way, and is enraged below half his health. This carries it out; his gaze and his charge are the skill
 * modules every boss may share (GazeSkill, ChargeSkill). As every {@link GoeticDemon}, only the arcane can hurt him;
 * while his ward stands, it hurts him only by half. He burns not at all. His body is his hitboxes
 * ({@link AndromaliusRules#BODY}): his head, his neck, his coil and his tail along the ground.
 */
public final class Andromalius extends GoeticDemon {
    /**
     * His death: the chains of his own punishing bind him, his eye opens over him and glares its last, the souls he
     * took rise out of him, and as the chains burst apart he is gone in a burst of his blood-red light, a ring of it
     * racing out and a pillar of it standing where he fell.
     */
    public static final BossDeath DEATH = new BossDeath(AndromaliusRules.DEATH_TICKS, 45, 1, new BossDeath.Rays(14, 9, 0xC01010, 0xFF9090), .1F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.FEET, "chains", 4, 1.2, 80, 0x2E7D32, 0xB0FF90, 0, "minecraft:block.chain.place", 3, .6F),
            BossDeath.beat(2, BossDeath.Anchor.FEET, "eye", 3.6, 7, 62, 0xC01010, 0xFF6060, .2, "minecraft:entity.elder_guardian.curse", 4, .7F),
            BossDeath.beat(24, BossDeath.Anchor.FEET, "souls", 2.5, 7, 55, 0xC01010, 0xB0FF90, 0, "minecraft:particle.soul_escape", 3, .8F),
            BossDeath.beat(66, BossDeath.Anchor.HEART, "nova", 12, 0, 26, 0xC01010, 0xFFFFFF, .6, "minecraft:entity.generic.explode", 4, .8F),
            BossDeath.beat(66, BossDeath.Anchor.FEET, "shockwave", 12, 2.5, 26, 0xC01010, 0xFF9090, 0, "", 0, 1),
            BossDeath.beat(67, BossDeath.Anchor.FEET, "light_pillar", 1.4, 36, 44, 0xC01010, 0xFFC0C0, 0, "minecraft:block.beacon.deactivate", 4, .6F)));

    private static final EntityDataAccessor<Byte> ATTACK = SynchedEntityData.defineId(Andromalius.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> SHIELDED = SynchedEntityData.defineId(Andromalius.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.defineId(Andromalius.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation ENRAGED_SPEED = ResourceLocation.fromNamespaceAndPath("corruption", "andromalius_enraged");
    /** Where his jaws are, in blocks from his feet: up, and forward along his body. */
    private static final double MOUTH_UP = 3.6, MOUTH_FORWARD = 1.4;
    /** How far off a fireball may stray from its mark, a little either way. */
    private static final double SPREAD = .08;
    /** How hard he lunges into a bite, slithers aside from a thrown thing, and throws off what his tail lashes, in blocks a tick. */
    private static final double LUNGE = .9, DODGE = 1.1, LASH = 1.2;

    private final GazeSkill gazing = new GazeSkill(AndromaliusRules.GAZE, 0xC01010, 0xFF6060);
    private final ChargeSkill charging = new ChargeSkill(AndromaliusRules.CHARGE);
    private final AndromaliusRules.Approach approach = new AndromaliusRules.Approach();
    private final AndromaliusRules.Shield shield = new AndromaliusRules.Shield();
    private int gazeCooldown = 30, volleyCooldown = 20, biteCooldown, tailCooldown, dodgeCooldown, huntCooldown = AndromaliusRules.HUNT_EVERY / 2, chargeCooldown = 60;
    private int attackTicks, volleySize, volleyFired, huntTicks, huntBites, retreatTicks, strafeTicks, strafeSide = 1;
    private boolean hunting;
    private Intent intent = Intent.CIRCLE;
    /** Where his quarry was last tick, and how fast it is going, smoothed, for leading his aim. */
    private Vec3 lastSeen, targetVelocity = Vec3.ZERO;
    /** On a client, the tick its attack and its ward last changed, to pose and fade them from. */
    private int attackSeen, shieldSeen;

    public Andromalius(EntityType<? extends Andromalius> type, Level level) {
        super(type, level, BossEvent.BossBarColor.RED, AndromaliusRules.BODY);
        xpReward = 60;
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, AndromaliusRules.MAX_HEALTH).add(Attributes.ARMOR, AndromaliusRules.ARMOR)
                .add(Attributes.MOVEMENT_SPEED, AndromaliusRules.SPEED).add(Attributes.FOLLOW_RANGE, AndromaliusRules.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, AndromaliusRules.KNOCKBACK_RESISTANCE).add(Attributes.ATTACK_DAMAGE, AndromaliusRules.BITE_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK, .6).add(Attributes.STEP_HEIGHT, 1.1);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK, (byte) 0);
        builder.define(SHIELDED, false);
        builder.define(ENRAGED, false);
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new Tactics(this));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, .8));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 24));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        huntTheUncorrupted(2);
    }

    // ---- his mind ----

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        boolean enraged = AndromaliusRules.enraged(getHealth() / getMaxHealth());
        if (enraged && !enraged()) enrage();
        if (shield.tick(AndromaliusRules.cooldown(AndromaliusRules.SHIELD_COOLDOWN, enraged))) {
            entityData.set(SHIELDED, false);
            playSound(SoundEvents.BEACON_DEACTIVATE, 1, 1.3F);
        }
        gazeCooldown = Math.max(0, gazeCooldown - 1); volleyCooldown = Math.max(0, volleyCooldown - 1);
        biteCooldown = Math.max(0, biteCooldown - 1); tailCooldown = Math.max(0, tailCooldown - 1);
        dodgeCooldown = Math.max(0, dodgeCooldown - 1); huntCooldown = Math.max(0, huntCooldown - 1); chargeCooldown = Math.max(0, chargeCooldown - 1);
        retreatTicks = Math.max(0, retreatTicks - 1);
        if (--strafeTicks <= 0 || horizontalCollision) {
            strafeSide = random.nextFloat() < .7F ? -strafeSide : strafeSide;
            strafeTicks = 40 + random.nextInt(60);
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            approach.forget();
            lastSeen = null;
            targetVelocity = Vec3.ZERO;
            hunting = false;
            intent = Intent.CIRCLE;
            if (attack() != Attack.NONE) end();
            return;
        }
        double distance = distanceTo(target);
        approach.see(distance);
        watch(target);
        getLookControl().setLookAt(target, 30, 30);
        if (!shield.raised() && AndromaliusRules.rushed(approach.closing(), distance)) raiseShield();
        if (!shield.raised() && dodgeCooldown == 0) dodgeIncoming();

        boolean impaired = target.hasEffect(MobEffects.BLINDNESS) || target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (!hunting && retreatTicks == 0 && (huntCooldown == 0 || impaired)) startHunt();
        if (hunting && AndromaliusRules.huntOver(++huntTicks, huntBites)) {
            hunting = false;
            retreatTicks = AndromaliusRules.RETREAT_TICKS;
            huntCooldown = AndromaliusRules.cooldown(AndromaliusRules.HUNT_EVERY, enraged);
        }
        boolean sight = hasLineOfSight(target);
        AndromaliusRules.Situation now = new AndromaliusRules.Situation(distance, sight, AndromaliusRules.behind(degreesOff(target)), impaired, hunting,
                retreatTicks > 0, gazeCooldown == 0, volleyCooldown == 0, biteCooldown == 0, tailCooldown == 0, chargeCooldown == 0);
        intent = AndromaliusRules.intent(now);

        switch (attack()) {
            case NONE -> {
                Attack next = AndromaliusRules.choose(now);
                if (next == Attack.VOLLEY) {
                    volleySize = AndromaliusRules.volleySize(getHealth() / getMaxHealth());
                    volleyFired = 0;
                }
                if (next != Attack.NONE) begin(next, target);
            }
            case GAZE -> {
                gazing.tick(this, target, attackTicks++);
                if (!gazing.timing().over(attackTicks)) return;
                gazeCooldown = AndromaliusRules.cooldown(AndromaliusRules.GAZE_COOLDOWN, enraged);
                end();
            }
            case CHARGE -> {
                charging.tick(this, target, attackTicks++);
                if (!charging.timing().over(attackTicks)) return;
                chargeCooldown = AndromaliusRules.cooldown(AndromaliusRules.CHARGE.timing().cooldown(), enraged);
                end();
            }
            case VOLLEY -> {
                int firing = attackTicks++ - AndromaliusRules.VOLLEY_WINDUP;
                int count = AndromaliusRules.firedOn(firing, volleySize);
                for (int one = 0; one < count; one++) fireball(target, volleyFired++);
                if (count > 0) playSound(SoundEvents.BLAZE_SHOOT, 1.2F, .6F + random.nextFloat() * .2F);
                if (firing + 1 >= AndromaliusRules.firingTicks(volleySize)) {
                    volleyCooldown = AndromaliusRules.cooldown(AndromaliusRules.VOLLEY_COOLDOWN, enraged);
                    end();
                }
            }
            case BITE -> {
                if (++attackTicks == AndromaliusRules.BITE_WINDUP) bite(target);
                if (attackTicks >= AndromaliusRules.BITE_TICKS) {
                    biteCooldown = AndromaliusRules.cooldown(AndromaliusRules.BITE_COOLDOWN, enraged);
                    end();
                }
            }
            case TAIL -> {
                if (++attackTicks == AndromaliusRules.TAIL_WINDUP) tailSweep();
                if (attackTicks >= AndromaliusRules.TAIL_TICKS) {
                    tailCooldown = AndromaliusRules.cooldown(AndromaliusRules.TAIL_COOLDOWN, enraged);
                    end();
                }
            }
        }
    }

    /** Notes where his quarry is and how fast it is going: smoothed, a jump between ticks (a teleport) ignored. */
    private void watch(LivingEntity target) {
        Vec3 seen = target.position();
        if (lastSeen != null) {
            Vec3 moved = seen.subtract(lastSeen);
            if (moved.lengthSqr() < 4) targetVelocity = targetVelocity.scale(.5).add(moved.scale(.5));
        }
        lastSeen = seen;
    }

    private void startHunt() {
        hunting = true;
        huntTicks = 0;
        huntBites = 0;
        playSound(SoundEvents.CREEPER_PRIMED, 2, .45F);
    }

    private void begin(Attack attack, LivingEntity target) {
        attackTicks = 0;
        entityData.set(ATTACK, (byte) attack.ordinal());
        if (attack == Attack.GAZE) gazing.begin(this, target);
        else if (attack == Attack.CHARGE) charging.begin(this, target);
        else if (attack == Attack.VOLLEY) playSound(SoundEvents.CREEPER_PRIMED, 2, .5F);
    }

    private void end() {
        if (attack() == Attack.CHARGE) charging.end(this, attackTicks < charging.timing().ticks());
        attackTicks = 0;
        entityData.set(ATTACK, (byte) 0);
    }

    /** Below half his health: faster for good, his gaze ready at once, and a roar to say so. */
    private void enrage() {
        entityData.set(ENRAGED, true);
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(ENRAGED_SPEED))
            speed.addPermanentModifier(new AttributeModifier(ENRAGED_SPEED, AndromaliusRules.ENRAGED_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        gazeCooldown = 0;
        volleyCooldown = Math.min(volleyCooldown, 20);
        playSound(SoundEvents.RAVAGER_ROAR, 3, .6F);
        if (level() instanceof ServerLevel server)
            server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 2, getZ(), 60, 1.2, 1.5, 1.2, .08);
    }

    // ---- what he does ----

    /**
     * Fixes something with the eye on his brow: a player sees it flash before their eyes, as an elder guardian's
     * face does, and hears the guardian's curse; whatever it is, it is slowed and blinded (GazeSkill.stare).
     */
    public void gaze(LivingEntity target) { GazeSkill.stare(this, target, AndromaliusRules.GAZE); }

    /** His charge, as it stands: the way it rushes, and whether something has stopped it. */
    public ChargeSkill charging() { return charging; }

    /** Where his fireballs leave from: his jaws, up on his raised forebody, a little ahead of him. */
    public Vec3 mouth() {
        float yaw = yBodyRot * Mth.DEG_TO_RAD;
        return position().add(-Mth.sin(yaw) * MOUTH_FORWARD, MOUTH_UP, Mth.cos(yaw) * MOUTH_FORWARD);
    }

    /**
     * Spits the {@code index}th fireball of a volley at something: every other one where it is going rather than where
     * it is; enraged, the whole volley sweeping across a fan, so stepping aside does not escape it.
     */
    public SmallFireball fireball(LivingEntity target, int index) {
        Vec3 from = mouth(), mark = target.getEyePosition();
        if (AndromaliusRules.leads(index)) {
            double[] led = AndromaliusRules.lead(new double[]{from.x, from.y, from.z}, new double[]{mark.x, mark.y, mark.z},
                    new double[]{targetVelocity.x, targetVelocity.y, targetVelocity.z});
            mark = new Vec3(led[0], led[1], led[2]);
        }
        Vec3 aim = mark.subtract(from).normalize();
        if (enraged()) aim = aim.yRot((float) Math.toRadians(AndromaliusRules.fanned(index, volleySize)));
        aim = aim.add(random.triangle(0, SPREAD), random.triangle(0, SPREAD * .6), random.triangle(0, SPREAD)).normalize();
        SmallFireball ball = new SmallFireball(level(), this, aim);
        ball.setPos(from.x, from.y, from.z);
        level().addFreshEntity(ball);
        return ball;
    }

    /** Lunges at something and, if it is still in reach, sinks his fangs in: his bite's damage, and poison. */
    public void bite(LivingEntity target) {
        Vec3 toward = new Vec3(target.getX() - getX(), 0, target.getZ() - getZ());
        if (toward.lengthSqr() > 1e-6) toward = toward.normalize();
        setDeltaMovement(getDeltaMovement().add(toward.x * LUNGE, .2, toward.z * LUNGE));
        playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.5F, .7F);
        if (distanceTo(target) <= AndromaliusRules.BITE_REACH + 1.5 && doHurtTarget(target)) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, AndromaliusRules.POISON_TICKS, AndromaliusRules.POISON_LEVEL), this);
            huntBites++;
        }
    }

    /** Whips his tail round at everything behind him or at his side, within its reach. */
    public void tailSweep() {
        playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, .5F);
        if (level() instanceof ServerLevel server)
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + .6, getZ(), 6, 2, .2, 2, 0);
        for (LivingEntity near : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(AndromaliusRules.TAIL_REACH),
                near -> near != this && near.isAlive() && inTailArc(near)))
            lash(near);
    }

    /** Whether something is where his tail sweeps: near enough, and behind him or at his side. */
    public boolean inTailArc(Entity other) {
        return Math.hypot(other.getX() - getX(), other.getZ() - getZ()) <= AndromaliusRules.TAIL_REACH && AndromaliusRules.behind(degreesOff(other));
    }

    /** How far round something is from where his body faces, in degrees, -180 to 180. */
    public double degreesOff(Entity other) {
        double facing = Mth.atan2(other.getZ() - getZ(), other.getX() - getX()) * Mth.RAD_TO_DEG - 90;
        return AndromaliusRules.wrap(facing - yBodyRot);
    }

    /** One stroke of his tail: hurts it and throws it off, away from him and up. */
    public void lash(LivingEntity victim) {
        if (!victim.hurt(damageSources().mobAttack(this), (float) AndromaliusRules.TAIL_DAMAGE)) return;
        Vec3 away = new Vec3(victim.getX() - getX(), 0, victim.getZ() - getZ());
        away = away.lengthSqr() < 1e-6 ? new Vec3(1, 0, 0) : away.normalize();
        victim.push(away.x * LASH, .45, away.z * LASH);
        victim.hurtMarked = true;
    }

    /** Looks for anything thrown at him that will strike him, and slithers aside from the first. */
    private void dodgeIncoming() {
        Vec3 middle = getBoundingBox().getCenter();
        double[] centre = {middle.x, middle.y, middle.z};
        for (Projectile thrown : level().getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(12), thrown -> thrown.getOwner() != this && thrown.isAlive())) {
            Vec3 at = thrown.position(), going = thrown.getDeltaMovement();
            if (AndromaliusRules.threatens(new double[]{at.x, at.y, at.z}, new double[]{going.x, going.y, going.z}, centre)) {
                evade(thrown);
                return;
            }
        }
    }

    /** Slithers aside, across the line something is coming along. */
    public void evade(Entity threat) {
        Vec3 going = threat.getDeltaMovement();
        Vec3 across = new Vec3(-going.z, 0, going.x);
        across = across.lengthSqr() < 1e-6 ? new Vec3(1, 0, 0) : across.normalize();
        if (random.nextBoolean()) across = across.scale(-1);
        setDeltaMovement(getDeltaMovement().add(across.x * DODGE, .15, across.z * DODGE));
        dodgeCooldown = AndromaliusRules.cooldown(AndromaliusRules.DODGE_COOLDOWN, enraged());
        playSound(SoundEvents.ILLUSIONER_MIRROR_MOVE, 1, .8F);
        if (level() instanceof ServerLevel server) server.sendParticles(ParticleTypes.CLOUD, getX(), getY() + .3, getZ(), 12, .8, .1, .8, .02);
    }

    /** Raises the ward against blows, if it is ready; whether it rose. */
    public boolean raiseShield() {
        if (!shield.raise()) return false;
        entityData.set(SHIELDED, true);
        playSound(SoundEvents.BEACON_ACTIVATE, 1.5F, 1.4F);
        if (level() instanceof ServerLevel server)
            server.sendParticles(ParticleTypes.ENCHANT, getX(), getY() + getBbHeight() / 2, getZ(), 40, 1.2, 1.2, 1.2, .4);
        return true;
    }

    /**
     * Whether his ward softens this hurt: one that reaches him at all (see {@link GoeticDemon#accepts}), unless it
     * passes every defence.
     */
    public static boolean wardSoftens(DamageSource source) {
        return accepts(source) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    /** While his ward stands, the arcane hurts him only by half, and the ward flares where it is struck. */
    @Override public boolean hurt(DamageSource source, float amount) {
        if (shield.raised() && wardSoftens(source)) {
            amount *= (float) AndromaliusRules.WARD_SHARE;
            if (level() instanceof ServerLevel server) {
                server.playSound(null, getX(), getY(), getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1, .8F + random.nextFloat() * .4F);
                server.sendParticles(ParticleTypes.ENCHANTED_HIT, getX(), getY() + getBbHeight() * .6, getZ(), 12, .8, .8, .8, .2);
            }
        }
        return super.hurt(source, amount);
    }

    // ---- what the client sees ----

    public Attack attack() {
        int index = entityData.get(ATTACK);
        return index >= 0 && index < Attack.values().length ? Attack.values()[index] : Attack.NONE;
    }

    public boolean shielded() { return entityData.get(SHIELDED); }
    public boolean enraged() { return entityData.get(ENRAGED); }
    /** How he means to move, as his tactics goal carries it out; and which way he circles, 1 or -1. */
    public Intent intent() { return intent; }
    public int strafeSide() { return strafeSide; }
    public boolean hunting() { return hunting; }

    /** On a client: ticks since its present attack began. */
    public float attackTicks(float partial) { return tickCount - attackSeen + partial; }

    /** On a client: how strongly the ward shows, 0 to 1, rising and falling over a few ticks. */
    public float wardStrength(float partial) {
        float since = Math.min(1, (tickCount - shieldSeen + partial) / 6);
        return shielded() ? since : 1 - since;
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ATTACK.equals(key)) attackSeen = tickCount;
        if (SHIELDED.equals(key)) shieldSeen = tickCount;
    }

    /** Enraged, soul fire seeps from him. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && enraged() && random.nextInt(3) == 0)
            level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getRandomX(1), getY() + random.nextDouble() * 3.5, getRandomZ(1), 0, .03, 0);
    }

    @Override public int emblem() { return 2; }
    @Override public BossDeath death() { return DEATH; }

    /** Gone in his light: a flash, and his souls and smoke bursting out of where he lay. */
    @Override protected void gone(ServerLevel level) {
        super.gone(level);
        Vec3 heart = heart(0);
        level.sendParticles(ParticleTypes.FLASH, heart.x, heart.y, heart.z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.SOUL, heart.x, heart.y, heart.z, 40, .8, .8, .8, .12);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, heart.x, heart.y, heart.z, 30, .9, .6, .9, .05);
    }

    // ---- the rest of what he is ----

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.CREEPER_PRIMED; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ELDER_GUARDIAN_DEATH; }
    @Override public float getVoicePitch() { return .55F; }
    @Override public int getAmbientSoundInterval() { return 160; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("WardUp", shield.up());
        tag.putInt("WardCooling", shield.cooling());
        tag.putInt("GazeCooldown", gazeCooldown);
        tag.putInt("VolleyCooldown", volleyCooldown);
        tag.putInt("HuntCooldown", huntCooldown);
        tag.putInt("ChargeCooldown", chargeCooldown);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shield.set(tag.getInt("WardUp"), tag.getInt("WardCooling"));
        entityData.set(SHIELDED, shield.raised());
        gazeCooldown = tag.getInt("GazeCooldown");
        volleyCooldown = tag.getInt("VolleyCooldown");
        huntCooldown = tag.getInt("HuntCooldown");
        chargeCooldown = tag.getInt("ChargeCooldown");
        entityData.set(ENRAGED, AndromaliusRules.enraged(getHealth() / getMaxHealth()));
    }

    /**
     * Moves him as his mind means: closing in when his quarry is far or out of sight, drawing back when it is too
     * near or a hunt is over, closing in fast to bite while hunting, and otherwise circling it at a middle
     * distance, facing it, sometimes one way and sometimes the other. He holds still to stare and to spit.
     */
    static final class Tactics extends Goal {
        private final Andromalius serpent;
        private int repath;

        Tactics(Andromalius serpent) {
            this.serpent = serpent;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override public boolean canUse() { return serpent.getTarget() != null && serpent.getTarget().isAlive(); }
        @Override public void stop() { serpent.getNavigation().stop(); }
        @Override public boolean requiresUpdateEveryTick() { return true; }

        @Override public void tick() {
            LivingEntity target = serpent.getTarget();
            if (target == null) return;
            Attack attack = serpent.attack();
            // His charge steers and turns him itself.
            if (attack == Attack.CHARGE) {
                serpent.getNavigation().stop();
                return;
            }
            serpent.getLookControl().setLookAt(target, 30, 30);
            if (attack == Attack.GAZE || attack == Attack.VOLLEY || attack == Attack.TAIL) {
                serpent.getNavigation().stop();
                return;
            }
            double distance = serpent.distanceTo(target);
            switch (serpent.intent()) {
                case CLOSE_IN -> chase(target, 1.2);
                case HUNT -> chase(target, 1.6);
                case BACK_OFF -> {
                    if (!serpent.getNavigation().isInProgress()) {
                        Vec3 away = DefaultRandomPos.getPosAway(serpent, 10, 4, target.position());
                        if (away != null) serpent.getNavigation().moveTo(away.x, away.y, away.z, 1.4);
                    }
                }
                case CIRCLE -> {
                    serpent.getNavigation().stop();
                    float forward = (float) Mth.clamp((distance - AndromaliusRules.CIRCLE_AT) * .15, -.6, .6);
                    serpent.getMoveControl().setWantedPosition(serpent.getX(), serpent.getY(), serpent.getZ(), 1.1);
                    serpent.getMoveControl().strafe(forward, .75F * serpent.strafeSide());
                }
            }
        }

        private void chase(LivingEntity target, double speed) {
            if (--repath <= 0 || !serpent.getNavigation().isInProgress()) {
                serpent.getNavigation().moveTo(target, speed);
                repath = 5;
            }
        }
    }
}
