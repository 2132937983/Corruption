package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.demon.skill.SpitRules;
import javax.annotation.Nullable;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;

/**
 * A bolt of the corruption as a demon (or an elite) looses it: the star's volley, the goshawk's barrage, an aberration's
 * acid, a screecher's feathers, a lurker's bolts of water. It flies as it was thrown, falling if it was thrown to fall; it
 * hurts what it strikes with the corruption's own hurt (and, with a splash, everything else within it of where it
 * strikes), as any blow does (a creature just struck is not struck again for a moment); and it is spent. It never strikes
 * its own side, nor a player in creative or spectator. Drawn as a spell bolt is (SpellBoltModel), in its colour, or as a
 * feather, as its look says ({@link SpitRules.Look}).
 *
 * <p>It may throw back what it strikes (a bolt of water under great pressure), and it may leave a pool where it strikes,
 * cursing whatever stands in it ({@link SpitRules.Pool}).
 */
public final class DemonBolt extends Projectile implements IEntityWithComplexSpawn {
    /** How long it flies at the most, in ticks, and how big it is, in blocks. */
    private static final int LIFE = 120;
    public static final double SIZE = .4;

    private double damage = 4, splash, gravity, push;
    private int colour = SpellAction.Visual.ROSE;
    private SpitRules.Look look = SpitRules.Look.BOLT;
    private SpitRules.Pool pool = SpitRules.Pool.NONE;

    public DemonBolt(EntityType<? extends DemonBolt> type, Level level) { super(type, level); }

    /**
     * Looses one from this place at this speed (blocks a tick), for this demon: hurting for {@code damage} what it strikes
     * and what is within {@code splash} of it, falling {@code gravity} blocks a tick faster each tick, in this colour.
     */
    public static DemonBolt loose(ServerLevel level, LivingEntity owner, Vec3 from, Vec3 velocity, double damage, double splash, double gravity, int colour) {
        return loose(level, owner, from, velocity, damage, splash, gravity, colour, SpitRules.Look.BOLT, 0, SpitRules.Pool.NONE);
    }

    /** The same, looking as {@code look} says, throwing back what it strikes {@code push} blocks a tick, leaving {@code pool} where it strikes. */
    public static DemonBolt loose(ServerLevel level, LivingEntity owner, Vec3 from, Vec3 velocity, double damage, double splash, double gravity, int colour,
                                  SpitRules.Look look, double push, SpitRules.Pool pool) {
        DemonBolt bolt = ModEntities.DEMON_BOLT.get().create(level);
        if (bolt == null) return null;
        bolt.setOwner(owner);
        bolt.damage = damage;
        bolt.splash = splash;
        bolt.gravity = gravity;
        bolt.colour = colour;
        bolt.look = look;
        bolt.push = push;
        bolt.pool = pool;
        bolt.moveTo(from.x, from.y - SIZE / 2, from.z);
        bolt.setDeltaMovement(velocity);
        bolt.turnTo(velocity);
        level.addFreshEntity(bolt);
        return bolt;
    }

    public double damage() { return damage; }
    public double splash() { return splash; }
    public int colour() { return colour; }
    public SpitRules.Look look() { return look; }
    public double push() { return push; }
    public SpitRules.Pool pool() { return pool; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected double getDefaultGravity() { return 0; }
    @Override public EntityDimensions getDimensions(Pose pose) { return EntityDimensions.scalable((float) SIZE, (float) SIZE); }

    @Override public void tick() {
        super.tick();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hit)) hitTargetOrDeflectSelf(hit);
        if (isRemoved()) return;
        Vec3 move = getDeltaMovement();
        setPos(getX() + move.x, getY() + move.y, getZ() + move.z);
        turnTo(move);
        if (gravity > 0) setDeltaMovement(move.add(0, -gravity, 0));
        if (level().isClientSide) trail();
        else if (tickCount > LIFE) discard();
    }

    /** What it leaves behind it as it flies, on a client: a bolt its colour's dust; acid drips; water bubbles, or spray in the air. */
    private void trail() {
        float red = (colour >> 16 & 255) / 255F, green = (colour >> 8 & 255) / 255F, blue = (colour & 255) / 255F;
        double y = yo + SIZE / 2;
        switch (look) {
            case FEATHER -> { if (random.nextInt(3) == 0) level().addParticle(new DustParticleOptions(new org.joml.Vector3f(red, green, blue), .6F), xo, y, zo, 0, 0, 0); }
            case GLOB -> {
                level().addParticle(new DustParticleOptions(new org.joml.Vector3f(red, green, blue), 1.1F), xo, y, zo, 0, 0, 0);
                if (random.nextInt(2) == 0) level().addParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR, getX(), getY(), getZ(), 0, 0, 0);
            }
            case WATER -> {
                level().addParticle(isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.SPLASH, xo, y, zo, 0, 0, 0);
                level().addParticle(new DustParticleOptions(new org.joml.Vector3f(red, green, blue), .8F), xo, y, zo, 0, 0, 0);
            }
            default -> {
                level().addParticle(new DustParticleOptions(new org.joml.Vector3f(red, green, blue), .9F), xo, y, zo, 0, 0, 0);
                if (random.nextInt(3) == 0) level().addParticle(ModParticles.CORRUPTION_PARTICLE.get(), getX(), getY() + SIZE / 2, getZ(), 0, 0, 0);
            }
        }
    }

    /** Turned the way it flies, as a projectile's turn is counted. */
    private void turnTo(Vec3 move) {
        if (move.lengthSqr() < 1e-8) return;
        setYRot((float) (Math.atan2(move.x, move.z) * (180 / Math.PI)));
        setXRot((float) (Math.atan2(move.y, move.horizontalDistance()) * (180 / Math.PI)));
    }

    /** Never its own side (the corruption's, as its demon reckons it), another bolt, nor a player in creative or spectator. */
    @Override protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity) || entity instanceof DemonBolt) return false;
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return false;
        return !(getOwner() instanceof LivingEntity demon && (entity == demon || demon.isAlliedTo(entity)));
    }

    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!(level() instanceof ServerLevel level) || !(hit.getEntity() instanceof LivingEntity struck)) return;
        if (struck.hurt(hurt(level), (float) damage) && push > 0) {
            Vec3 along = getDeltaMovement().multiply(1, 0, 1);
            along = along.lengthSqr() < 1e-6 ? Vec3.ZERO : along.normalize();
            struck.push(along.x * push, .15 + push * .2, along.z * push);
            struck.hurtMarked = true;
        }
    }

    /** Where it strikes: a splash of the same hurt round it, a pool if it leaves one, a burst of the corruption, and it is spent. */
    @Override protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(level() instanceof ServerLevel level)) return;
        Vec3 at = result.getLocation();
        Entity direct = result instanceof EntityHitResult creature ? creature.getEntity() : null;
        if (splash > 0)
            for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(at, at).inflate(splash),
                    near -> near != direct && near.isAlive() && canHitEntity(near) && near.getBoundingBox().distanceToSqr(at) <= splash * splash))
                near.hurt(hurt(level), (float) damage);
        if (pool.any()) leavePool(level, at);
        switch (look) {
            case WATER -> level.sendParticles(ParticleTypes.SPLASH, at.x, at.y, at.z, 16, .3, .2, .3, .1);
            case GLOB -> level.sendParticles(ParticleTypes.FALLING_OBSIDIAN_TEAR, at.x, at.y + .3, at.z, 10, .5, .2, .5, 0);
            default -> {}
        }
        level.sendParticles(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), at.x, at.y, at.z, 6, .15, .15, .15, .08);
        discard();
    }

    /** A pool of the corruption on the ground where it struck, shrinking away as it lies, cursing whatever stands in it. */
    private void leavePool(ServerLevel level, Vec3 at) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, at.x, at.y, at.z);
        if (getOwner() instanceof LivingEntity demon) cloud.setOwner(demon);
        cloud.setParticle(ModParticles.CORRUPTION_PARTICLE.get());
        cloud.setRadius(pool.radius());
        cloud.setDuration(pool.ticks());
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-pool.radius() / (pool.ticks() * 2F));
        if (pool.curseTicks() > 0) cloud.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, pool.curseTicks()));
        level.addFreshEntity(cloud);
    }

    private DamageSource hurt(ServerLevel level) {
        return getOwner() instanceof LivingEntity demon ? ModDamageTypes.corruption(level, demon) : ModDamageTypes.corruption(level);
    }

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(colour);
        buffer.writeByte(look.ordinal());
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        colour = buffer.readInt();
        look = look(buffer.readByte());
    }

    private static SpitRules.Look look(int index) {
        return index >= 0 && index < SpitRules.Look.values().length ? SpitRules.Look.values()[index] : SpitRules.Look.BOLT;
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("Damage", damage);
        tag.putDouble("Splash", splash);
        tag.putDouble("Fall", gravity);
        tag.putInt("Color", colour);
        tag.putByte("Look", (byte) look.ordinal());
        if (push > 0) tag.putDouble("Push", push);
        if (pool.any()) {
            tag.putFloat("PoolRadius", pool.radius());
            tag.putInt("PoolTicks", pool.ticks());
            tag.putInt("PoolCurse", pool.curseTicks());
        }
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getDouble("Damage");
        splash = tag.getDouble("Splash");
        gravity = tag.getDouble("Fall");
        colour = tag.contains("Color") ? tag.getInt("Color") : SpellAction.Visual.ROSE;
        look = look(tag.getByte("Look"));
        push = tag.getDouble("Push");
        pool = new SpitRules.Pool(tag.getFloat("PoolRadius"), tag.getInt("PoolTicks"), tag.getInt("PoolCurse"));
    }

    @Nullable public LivingEntity demon() { return getOwner() instanceof LivingEntity demon ? demon : null; }
}
