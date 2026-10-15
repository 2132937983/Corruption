package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.arcane.SpellCodecs;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellBolt;
import java.util.List;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;

/**
 * A spell bolt: the casting entity a spell's {@link SpellAction.LaunchBolt} flies. It carries the actions of
 * its "on_hit" — written out in the spell's own data — and carries them out where it strikes, so what a bolt
 * does is data, and it does it where it strikes, round that point. How it flies is {@link SpellBolt}: it keeps
 * that share of its speed each tick, is pulled down as it goes, is spent once it has flown long enough, and
 * strikes a creature its line of flight passes within that reach of, or a block it flies into.
 *
 * <p>How it shows itself is its spell's data too (see {@link SpellAction.LaunchBolt}): the item it is drawn as,
 * the particles it trails in flight, scattered along the stretch it has just flown so that a fast bolt leaves an
 * unbroken trail, and those that burst from where it strikes. Its look and its trail are sent to the clients
 * when it first appears to them; the burst is sent from the server where it strikes.
 *
 * <p>Its owner must be a player for the payload to tell who cast it; a bolt with any other owner hits nothing.
 */
public final class SpellProjectile extends Projectile implements ItemSupplier, IEntityWithComplexSpawn {
    /** What it looks like in flight, unless the spell it flies for names another look. */
    private static final ItemStack DEFAULT_LOOK = new ItemStack(ModItems.CORRUPTION_CRYSTAL.get());
    private static final String ON_HIT = "OnHit", LOOK_TAG = "Look", TRAIL = "Trail", BURST = "Burst";
    /** A particle list, as it goes over the wire with a bolt. */
    private static final StreamCodec<ByteBuf, List<SpellAction.Particles>> PARTICLES_STREAM = ByteBufCodecs.fromCodec(SpellCodecs.PARTICLE_LIST);
    /** What a colour is drawn as when a particle that takes one is given none: the corruption's violet. */
    private static final int VIOLET = 0xB05AA8;
    /** What it keeps of its speed a tick in water, as thrown things do. */
    private static final float WATER_DRAG = 0.8F;

    private List<SpellAction> onHit = List.of();
    private ItemStack look = DEFAULT_LOOK;
    private List<SpellAction.Particles> trail = SpellAction.LaunchBolt.CORRUPTION_TRAIL, burst = List.of();

    public SpellProjectile(EntityType<? extends SpellProjectile> type, Level level) { super(type, level); }

    /** What this bolt does where it strikes, and how it shows itself, as its spell's data says. */
    public SpellProjectile flying(SpellAction.LaunchBolt bolt) {
        carrying(bolt.onHit(), bolt.look());
        trail = bolt.trail();
        burst = bolt.burst();
        return this;
    }

    /** What this bolt does where it strikes, under the corruption's own look. */
    public SpellProjectile carrying(List<SpellAction> actions) { return carrying(actions, ""); }

    /**
     * What this bolt does where it strikes, and what it is drawn as in flight: an item whose model stands for
     * it, a fire charge for a fireball say, or "" for the corruption's crystal.
     */
    public SpellProjectile carrying(List<SpellAction> actions, String look) {
        onHit = List.copyOf(actions);
        this.look = lookOf(look);
        return this;
    }

    /** The model of an item by its id, or the corruption's crystal for none or for an id the game does not know. */
    private static ItemStack lookOf(String id) {
        if (id == null || id.isEmpty()) return DEFAULT_LOOK;
        ResourceLocation name = ResourceLocation.tryParse(id);
        return name == null ? DEFAULT_LOOK
                : BuiltInRegistries.ITEM.getOptional(name).map(item -> new ItemStack(item, 1)).orElse(DEFAULT_LOOK);
    }

    @Override public ItemStack getItem() { return look; }
    /** What it trails in flight, and what bursts from where it strikes. */
    public List<SpellAction.Particles> trail() { return trail; }
    public List<SpellAction.Particles> burst() { return burst; }

    /**
     * What a particle of a spell's data is drawn as: one of the game's that needs nothing but its id, or one that
     * takes a colour (dust, a mob effect's swirl) in the colour given, violet if none is; null for an id the game
     * does not know, or for a particle that needs more than that.
     */
    public static ParticleOptions options(SpellAction.Particles particles) {
        ResourceLocation id = ResourceLocation.tryParse(particles.particle());
        ParticleType<?> type = id == null ? null : BuiltInRegistries.PARTICLE_TYPE.get(id);
        int colour = particles.colour() < 0 ? VIOLET : particles.colour();
        if (type instanceof SimpleParticleType simple) return simple;
        if (type == ParticleTypes.DUST) return new DustParticleOptions(Vec3.fromRGB24(colour).toVector3f(), 1F);
        if (type == ParticleTypes.ENTITY_EFFECT) return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFF000000 | colour);
        return null;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected double getDefaultGravity() { return SpellBolt.DEFAULT.gravity(); }

    /**
     * Flies as {@link SpellBolt} says, and trails corruption as it goes. It is spent once it has struck or
     * flown long enough.
     */
    @Override public void tick() {
        super.tick();
        HitResult hit = strike();
        if (hit.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hit)) hitTargetOrDeflectSelf(hit);
        checkInsideBlocks();
        Vec3 move = getDeltaMovement();
        double x = getX() + move.x, y = getY() + move.y, z = getZ() + move.z;
        updateRotation();
        setDeltaMovement(move.scale(isInWater() ? WATER_DRAG : SpellBolt.DEFAULT.drag()));
        applyGravity();
        setPos(x, y, z);
        if (level().isClientSide) {
            if (tickCount > 1) trail(xo, yo, zo);
        } else if (tickCount > SpellBolt.DEFAULT.lifetime()) discard();
    }

    /** Gives off its trail along the stretch it has flown since it was last at this point. */
    private void trail(double fromX, double fromY, double fromZ) {
        for (SpellAction.Particles particles : trail) {
            ParticleOptions options = options(particles);
            if (options == null) continue;
            for (int one = 0; one < particles.count(); one++) {
                double along = (one + random.nextDouble()) / particles.count();
                level().addParticle(options,
                        fromX + (getX() - fromX) * along + scatter(particles.spread()),
                        fromY + (getY() - fromY) * along + scatter(particles.spread()),
                        fromZ + (getZ() - fromZ) * along + scatter(particles.spread()),
                        scatter(particles.speed()), scatter(particles.speed()), scatter(particles.speed()));
            }
        }
    }

    /** A little way either side of nothing, as far as this at most. */
    private double scatter(double reach) { return reach <= 0 ? 0 : (random.nextDouble() * 2 - 1) * reach; }

    /**
     * What it strikes this tick, if anything: the first block on its way, or a creature it passes within its
     * reach of before that, whichever comes first.
     */
    private HitResult strike() {
        Vec3 from = position(), move = getDeltaMovement(), to = from.add(move);
        HitResult block = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (block.getType() != HitResult.Type.MISS) to = block.getLocation();
        double reach = SpellBolt.DEFAULT.hitRadius();
        EntityHitResult creature = ProjectileUtil.getEntityHitResult(level(), this, from, to, getBoundingBox().expandTowards(move).inflate(1.0 + reach),
                this::canHitEntity, (float) reach);
        return creature != null ? creature : block;
    }

    /** Where it strikes, what it carried is carried out round that point, and the bolt is spent. */
    @Override protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(level() instanceof ServerLevel)) return;
        if (getOwner() instanceof ServerPlayer caster) {
            ArcaneCasting.Origin struck = ArcaneCasting.Origin.impact(result.getLocation(), getDeltaMovement());
            for (SpellAction action : onHit) ArcaneCasting.world().perform(caster, struck, action);
        }
        if (level() instanceof ServerLevel server) {
            Vec3 at = result.getLocation();
            for (SpellAction.Particles particles : burst) {
                ParticleOptions options = options(particles);
                if (options != null && particles.count() > 0)
                    server.sendParticles(options, at.x, at.y, at.z, particles.count(), particles.spread(), particles.spread(), particles.spread(), particles.speed());
            }
        }
        discard();
    }

    /** Its look and its trail, sent with it to the clients as it first appears to them. */
    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(BuiltInRegistries.ITEM.getKey(look.getItem()).toString());
        PARTICLES_STREAM.encode(buffer, trail);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        look = lookOf(buffer.readUtf());
        trail = List.copyOf(PARTICLES_STREAM.decode(buffer));
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(LOOK_TAG)) look = lookOf(tag.getString(LOOK_TAG));
        if (tag.contains(TRAIL)) SpellCodecs.PARTICLE_LIST.parse(NbtOps.INSTANCE, tag.get(TRAIL)).result().ifPresent(read -> trail = List.copyOf(read));
        if (tag.contains(BURST)) SpellCodecs.PARTICLE_LIST.parse(NbtOps.INSTANCE, tag.get(BURST)).result().ifPresent(read -> burst = List.copyOf(read));
        if (!tag.contains(ON_HIT)) return;
        SpellCodecs.ACTIONS.parse(NbtOps.INSTANCE, tag.get(ON_HIT)).result().ifPresent(actions -> onHit = List.copyOf(actions));
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        SpellCodecs.ACTIONS.encodeStart(NbtOps.INSTANCE, onHit).result().ifPresent(encoded -> tag.put(ON_HIT, encoded));
        tag.putString(LOOK_TAG, BuiltInRegistries.ITEM.getKey(look.getItem()).toString());
        SpellCodecs.PARTICLE_LIST.encodeStart(NbtOps.INSTANCE, trail).result().ifPresent(encoded -> tag.put(TRAIL, encoded));
        SpellCodecs.PARTICLE_LIST.encodeStart(NbtOps.INSTANCE, burst).result().ifPresent(encoded -> tag.put(BURST, encoded));
    }
}
