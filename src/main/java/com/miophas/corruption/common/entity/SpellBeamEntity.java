package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.core.arcane.SpellAction;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/**
 * A spell's beam as it fires ({@link SpellAction.Beam}): it keeps with its caster and fires from their hands along
 * their gaze, following it as they turn, until its time is up; every so often it does what it carries at each
 * creature it catches. The server moves it and pulses it; the clients draw it (SpellBeamRenderer), from the caster
 * as they see them, and feel it rumble if it says to. It goes the moment its caster does, and is never saved.
 */
public final class SpellBeamEntity extends Entity implements IEntityWithComplexSpawn {
    private int caster = -1, lifetime = 60, interval = 5, colour = SpellAction.Visual.ROSE, accent = SpellAction.Visual.GOLD;
    private float length = 32, width = 1, shake;
    private List<SpellAction> onHit = List.of();
    /**
     * The caster themselves, on the server: found by their id alone, a caster no level keeps (a fake player, as
     * machines and tests use) would be lost at once.
     */
    private Entity holder;
    /**
     * What a client does every tick a beam fires near it, once it knows the beam: the client sets this (it rumbles
     * the view), so that this class knows nothing of the client.
     */
    public static Consumer<SpellBeamEntity> firing = beam -> {};

    public SpellBeamEntity(EntityType<? extends SpellBeamEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    /** Fires what this action says, from this caster. */
    public void firing(ServerPlayer from, SpellAction.Beam beam) {
        holder = from;
        caster = from.getId();
        length = (float) beam.length();
        width = (float) beam.width();
        lifetime = beam.ticks();
        interval = beam.interval();
        onHit = beam.onHit();
        colour = beam.colour();
        accent = beam.accent();
        shake = (float) beam.shake();
        follow(from);
    }

    /** Who fires it, if they are still about. */
    public Entity caster() {
        if (holder != null && !holder.isRemoved()) return holder;
        return caster < 0 ? null : level().getEntity(caster);
    }
    public float length() { return length; }
    public float width() { return width; }
    public int lifetime() { return lifetime; }
    public int colour() { return colour; }
    public int accent() { return accent; }
    public float shake() { return shake; }

    /**
     * Where a beam leaves whoever fires it: a little before them, at their right hand, a little under their eyes,
     * so that it pours from their hand rather than from their face (and does not fill their own sight).
     */
    public static Vec3 muzzle(Entity from, float partialTick) {
        Vec3 eye = from.getEyePosition(partialTick), look = from.getViewVector(partialTick);
        Vec3 right = new Vec3(-look.z, 0, look.x);
        if (right.lengthSqr() < 1e-6) {
            float yaw = (float) Math.toRadians(from.getViewYRot(partialTick));
            right = new Vec3(-Mth.cos(yaw), 0, -Mth.sin(yaw));
        }
        return eye.add(look.scale(.7)).add(right.normalize().scale(.3)).add(0, -.3, 0);
    }

    /** Where a beam fired from here this way stops: as far as it reaches, or at the first block in its way. */
    public BlockHitResult reach(Vec3 from, Vec3 way) {
        return level().clip(new ClipContext(from, from.add(way.scale(length)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
    }

    /** It keeps with its caster; on the server, it pulses as it should, and goes when its time is up or its caster does. */
    @Override public void tick() {
        Entity from = caster();
        if (level().isClientSide) {
            if (from != null) firing.accept(this);
            return;
        }
        if (!(from instanceof ServerPlayer player) || !player.isAlive() || player.isRemoved() || tickCount > lifetime) {
            discard();
            return;
        }
        follow(player);
        if ((tickCount - 1) % interval == 0) pulse(player);
    }

    /** Stands at the caster's hand, turned the way they look, so the game keeps it near them and turned as they are. */
    private void follow(Entity from) {
        Vec3 at = muzzle(from, 1);
        moveTo(at.x, at.y, at.z, from.getYRot(), from.getXRot());
    }

    /**
     * One pulse: what it carries, done at each creature it catches. Every creature caught may be hurt again by it,
     * however close together its pulses come; but within one pulse each is hurt once, however many of its
     * neighbours' blasts reach it.
     */
    private void pulse(ServerPlayer player) {
        Vec3 from = muzzle(player, 1), way = player.getLookAngle(), to = reach(from, way).getLocation();
        List<LivingEntity> caught = caught(player, from, to);
        for (LivingEntity target : caught) target.invulnerableTime = 0;
        for (LivingEntity target : caught) {
            Vec3 at = target.getBoundingBox().getCenter();
            ArcaneCasting.Origin origin = new ArcaneCasting.Origin(at, at, way);
            for (SpellAction action : onHit) ArcaneCasting.world().perform(player, origin, action);
        }
    }

    /** Every living thing within the beam's width of its line from here to there, its caster left out. */
    public List<LivingEntity> caught(Entity player, Vec3 from, Vec3 to) {
        return level().getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(width), target -> {
            if (target == player || !target.isAlive()) return false;
            AABB reach = target.getBoundingBox().inflate(width);
            return reach.contains(from) || reach.clip(from, to).isPresent();
        });
    }

    /** Drawn whenever any of it could be in sight: all the way from its caster's hand to where it stops. */
    @Override public AABB getBoundingBoxForCulling() {
        Entity from = caster();
        Vec3 start = from != null ? muzzle(from, 1) : position(), way = from != null ? from.getViewVector(1) : getViewVector(1);
        return new AABB(start, reach(start, way).getLocation()).inflate(width * 4 + 3);
    }

    /** Seen from as far as the beam itself reaches, and further. */
    @Override public boolean shouldRenderAtSqrDistance(double distance) {
        double far = (64 + length * 2) * getViewScale();
        return distance < far * far;
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isAttackable() { return false; }
    @Override public boolean ignoreExplosion(net.minecraft.world.level.Explosion explosion) { return true; }
    @Override public boolean isIgnoringBlockTriggers() { return true; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }
    @Override public boolean displayFireAnimation() { return false; }

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(caster);
        buffer.writeFloat(length);
        buffer.writeFloat(width);
        buffer.writeVarInt(lifetime);
        buffer.writeInt(colour);
        buffer.writeInt(accent);
        buffer.writeFloat(shake);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        caster = buffer.readVarInt();
        length = buffer.readFloat();
        width = buffer.readFloat();
        lifetime = buffer.readVarInt();
        colour = buffer.readInt();
        accent = buffer.readInt();
        shake = buffer.readFloat();
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
}
