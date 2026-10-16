package com.miophas.corruption.common.entity;

import com.miophas.corruption.core.demon.skill.BeamRules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/**
 * A boss's beam (the beam skill, core.demon.skill.BeamRules): it stands where the beam leaves the boss and faces the way
 * it is aimed, and the skill moves and turns it every tick. While it gathers, the clients draw a thin, flickering line
 * along its aim and a light swelling where it leaves the boss, so that its quarry can see it coming; once it fires,
 * the beam itself, to the first block in its way (common.client.visual.DemonBeamRenderer). It is nothing in the world:
 * it neither collides nor can be hit, and is never saved. Left behind by a skill that has ended, it goes.
 */
public final class DemonBeamEntity extends Entity implements IEntityWithComplexSpawn {
    /** The tick it began firing on, or -1 while it gathers. */
    private static final EntityDataAccessor<Integer> FIRED_AT = SynchedEntityData.defineId(DemonBeamEntity.class, EntityDataSerializers.INT);
    /** Ticks it lasts at the most, if its skill never takes it away. */
    private static final int LEFT_BEHIND = 600;

    private float length = 32, width = 1;
    private int gathering = 20, firing = 60, colour = 0xC6728C, accent = 0xFFD24A;

    public DemonBeamEntity(EntityType<? extends DemonBeamEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    /** Made to the beam's rules, in these colours (0xRRGGBB). */
    public void shaped(BeamRules rules, int colour, int accent) {
        length = (float) rules.length();
        width = (float) rules.width();
        gathering = rules.timing().windup();
        firing = rules.timing().ticks() - rules.timing().windup();
        this.colour = colour;
        this.accent = accent;
    }

    /** Stands it where the beam leaves the boss and turns it the way it is aimed, in degrees. */
    public void aim(Vec3 from, float yaw, float pitch) {
        setPos(from);
        setYRot(yaw);
        setXRot(pitch);
    }

    /** It gathers no longer: it fires. */
    public void fire() { if (!fired()) entityData.set(FIRED_AT, tickCount); }

    public boolean fired() { return entityData.get(FIRED_AT) >= 0; }

    /** Ticks since it began firing, this far through the tick; none while it gathers. */
    public float firedFor(float partialTick) { return fired() ? Math.max(0, tickCount - entityData.get(FIRED_AT) + partialTick) : 0; }

    public float length() { return length; }
    public float width() { return width; }
    public int gathering() { return gathering; }
    public int firing() { return firing; }
    public int colour() { return colour; }
    public int accent() { return accent; }

    /** The first block in its way, from here along this way; a miss if it reaches its length clear. */
    public BlockHitResult reach(Vec3 from, Vec3 way) {
        return level().clip(new ClipContext(from, from.add(way.scale(length)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
    }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > LEFT_BEHIND) discard();
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(FIRED_AT, -1); }

    @Override public AABB getBoundingBoxForCulling() {
        Vec3 end = position().add(getLookAngle().scale(length));
        return new AABB(position(), end).inflate(width * 3 + 3);
    }

    @Override public boolean shouldRenderAtSqrDistance(double distance) {
        double far = (64 + length) * getViewScale();
        return distance < far * far;
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isAttackable() { return false; }
    @Override public boolean ignoreExplosion(net.minecraft.world.level.Explosion explosion) { return true; }
    @Override public boolean isIgnoringBlockTriggers() { return true; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }
    @Override public boolean displayFireAnimation() { return false; }
    @Override public boolean shouldBeSaved() { return false; }

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(length);
        buffer.writeFloat(width);
        buffer.writeVarInt(gathering);
        buffer.writeVarInt(firing);
        buffer.writeInt(colour);
        buffer.writeInt(accent);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        length = buffer.readFloat();
        width = buffer.readFloat();
        gathering = buffer.readVarInt();
        firing = buffer.readVarInt();
        colour = buffer.readInt();
        accent = buffer.readInt();
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
}
