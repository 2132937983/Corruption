package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.arcane.SpellCodecs;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellBolt;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import net.neoforged.neoforge.event.EventHooks;

/**
 * A spell bolt: the casting entity a spell's {@link SpellAction.LaunchBolt} flies. It carries the actions of
 * its "on_hit" — written out in the spell's own data — and carries them out where it strikes, so what a bolt
 * does is data, and it does it where it strikes, round that point. How it flies is {@link SpellBolt}: it keeps
 * that share of its speed each tick, is pulled down as it goes, is spent once it has flown long enough, and
 * strikes a creature its line of flight passes within that reach of, or a block it flies into.
 *
 * <p>Its owner must be a player for the payload to tell who cast it; a bolt with any other owner hits nothing.
 */
public final class SpellProjectile extends Projectile implements ItemSupplier {
    /** What it looks like in flight, until it has a look of its own. */
    private static final ItemStack LOOK = new ItemStack(ModItems.CORRUPTION_CRYSTAL.get());
    private static final String ON_HIT = "OnHit";
    /** What it keeps of its speed a tick in water, as thrown things do. */
    private static final float WATER_DRAG = 0.8F;

    private List<SpellAction> onHit = List.of();

    public SpellProjectile(EntityType<? extends SpellProjectile> type, Level level) { super(type, level); }

    /** What this bolt does where it strikes. */
    public SpellProjectile carrying(List<SpellAction> actions) { onHit = List.copyOf(actions); return this; }

    @Override public ItemStack getItem() { return LOOK; }
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
            if (tickCount > 1) level().addParticle(ModParticles.CORRUPTION_PARTICLE.get(), getX(), getY(), getZ(), 0, 0, 0);
        } else if (tickCount > SpellBolt.DEFAULT.lifetime()) discard();
    }

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
        discard();
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        if (!tag.contains(ON_HIT)) return;
        SpellCodecs.ACTIONS.parse(NbtOps.INSTANCE, tag.get(ON_HIT)).result().ifPresent(actions -> onHit = List.copyOf(actions));
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        SpellCodecs.ACTIONS.encodeStart(NbtOps.INSTANCE, onHit).result().ifPresent(encoded -> tag.put(ON_HIT, encoded));
    }
}
