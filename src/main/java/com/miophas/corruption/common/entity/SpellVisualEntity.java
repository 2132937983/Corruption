package com.miophas.corruption.common.entity;

import com.miophas.corruption.core.arcane.SpellAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/**
 * A spell's effect, standing on the ground for as long as it shows: the server sets it down where a spell's
 * "visual" action says and takes it away once its time is up; the clients draw it (SpellVisualRenderer, by the
 * effect's id, from api.arcane.client.SpellVisuals) and shake their view if it says to. It is nothing in the world:
 * it does not move, collide, burn or get in the way, cannot be hit, and is never saved.
 */
public final class SpellVisualEntity extends Entity implements IEntityWithComplexSpawn {
    private String effect = "";
    private float radius = 3, height = 3, shake;
    private int lifetime = 40, colour = SpellAction.Visual.ROSE, accent = SpellAction.Visual.GOLD;
    private boolean appeared;
    /**
     * What the client does when an effect first appears to it, once its spawn data has come: the client sets this
     * (it shakes the view), so that this class knows nothing of the client.
     */
    public static java.util.function.Consumer<SpellVisualEntity> appearing = shown -> {};

    public SpellVisualEntity(EntityType<? extends SpellVisualEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    /** Shows what this action says, for as long as it says. */
    public void showing(SpellAction.Visual visual, float yaw) {
        effect = visual.effect();
        radius = (float) visual.radius();
        height = (float) visual.height();
        lifetime = visual.ticks();
        colour = visual.colour();
        accent = visual.accent();
        shake = (float) visual.shake();
        setYRot(yaw);
    }

    /** Which effect it is, by its id; null if the id is no id. */
    public ResourceLocation effect() { return ResourceLocation.tryParse(effect); }
    public float radius() { return radius; }
    public float height() { return height; }
    public int lifetime() { return lifetime; }
    public int colour() { return colour; }
    public int accent() { return accent; }
    public float shake() { return shake; }

    /** It only stands there; on the server, its time up, it goes. On a client it tells of its appearing, once. */
    @Override public void tick() {
        if (!level().isClientSide) {
            if (tickCount >= lifetime) discard();
        } else if (!appeared) {
            appeared = true;
            appearing.accept(this);
        }
    }

    /** Drawn whenever any of it could be in sight, not only its foot. */
    @Override public AABB getBoundingBoxForCulling() { return reach(getX(), getY(), getZ(), radius, height); }

    /**
     * All that any effect this size may reach, standing at x, y, z: well past its radius round it (wings spread,
     * shards burst outward, a sigil stands before its caster) and well over its height (an eye hangs high, rays of
     * glory round it), so that none of it is ever cut off; and never less than the least some effects are drawn at,
     * however small (a storm's cloud hangs six blocks up).
     */
    public static AABB reach(double x, double y, double z, float radius, float height) {
        double across = radius * 2.6 + 5, up = Math.max(height, radius) * 1.7 + radius * 1.7 + 8;
        return new AABB(x - across, y - 2, z - across, x + across, y + up, z + across);
    }

    /** Seen from as far as a spell's might deserves. */
    @Override public boolean shouldRenderAtSqrDistance(double distance) {
        double far = (64 + radius * 4) * getViewScale();
        return distance < far * far;
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isAttackable() { return false; }
    @Override public boolean ignoreExplosion(net.minecraft.world.level.Explosion explosion) { return true; }
    @Override public boolean isIgnoringBlockTriggers() { return true; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }
    @Override public boolean displayFireAnimation() { return false; }

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(effect);
        buffer.writeFloat(radius);
        buffer.writeFloat(height);
        buffer.writeVarInt(lifetime);
        buffer.writeInt(colour);
        buffer.writeInt(accent);
        buffer.writeFloat(shake);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        effect = buffer.readUtf();
        radius = buffer.readFloat();
        height = buffer.readFloat();
        lifetime = buffer.readVarInt();
        colour = buffer.readInt();
        accent = buffer.readInt();
        shake = buffer.readFloat();
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
}
