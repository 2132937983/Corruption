package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.skill.ChariotRules;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;

/**
 * A chariot of fire, driven along a line by the demon that called it ({@link ChariotRules}, ChariotSkill). It runs from
 * one end of the line to the other at its pace, whatever its demon does meanwhile. Every block in its way that gives way
 * to it is broken (where creatures may break blocks: the mobGriefing rule), and one that will not wrecks it there.
 * Everything within its reach of its path that is not of its demon's side is struck once: hurt as by its demon's blow,
 * thrown away from the line and up, and left burning. At the end of the line it is gone, in a burst of flame. It is no
 * creature: nothing hurts it, and it is not kept when the world is saved. Drawn as a great war chariot of gold on
 * burning wheels, drawn by two horses of fire (FieryChariotRenderer).
 *
 * <p>A client is sent its line and its pace as it appears, and runs it along the line itself, tick by tick, as the
 * server does: it goes too fast for the places the server sends to be drawn smoothly between.
 */
public final class FieryChariot extends Entity implements IEntityWithComplexSpawn {
    /** Its box, in blocks: as wide and as tall as the chariot and its horses stand (the line it runs is measured by its reach). */
    public static final float WIDTH = 3.2F, HEIGHT = 3F;
    /** Its wheels clear the ground by this much, so that it breaks what stands in its way, not what it runs on. */
    private static final double WHEELS = .25;

    @Nullable private Mob driver;
    @Nullable private ChariotRules rules;
    private Vec3 start = Vec3.ZERO, end = Vec3.ZERO;
    private int ticks, colour, accent;
    /** Its pace, as a share of its line a tick (a client is sent it; the server's rules say it). */
    private double pace;
    private boolean wrecked;
    private final Set<UUID> struck = new HashSet<>();
    /** On a client: how far it has run, to turn its wheels and its horses' legs by. */
    private float run, runBefore;

    public FieryChariot(EntityType<? extends FieryChariot> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    /** Sets one going along this line for this demon, as these rules say, in these colours; and into the world. */
    public static FieryChariot drive(ServerLevel level, Mob driver, Vec3 start, Vec3 end, ChariotRules rules, int colour, int accent) {
        FieryChariot chariot = ModEntities.FIERY_CHARIOT.get().create(level);
        if (chariot == null) return null;
        chariot.driver = driver;
        chariot.rules = rules;
        chariot.start = start;
        chariot.end = end;
        chariot.colour = colour;
        chariot.accent = accent;
        chariot.pace = rules.speed() / rules.length();
        Vec3 way = end.subtract(start);
        chariot.moveTo(start.x, start.y, start.z, (float) (Mth.atan2(way.z, way.x) * Mth.RAD_TO_DEG) - 90, 0);
        level.addFreshEntity(chariot);
        SkillShows.sound(level, start, SoundEvents.BLAZE_SHOOT, 3, .5F);
        SkillShows.sound(level, start, SoundEvents.HORSE_ANGRY, 3, .6F);
        SkillShows.show(level, start, "nova", 3, 0, 18, colour, accent, .4, chariot.getYRot(), true);
        return chariot;
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override public void tick() {
        super.tick();
        if (level().isClientSide) {
            // Run along its line as the server does; how far it went turns its wheels.
            Vec3 was = position(), now = start.lerp(end, Math.min(1, ++ticks * pace));
            setPos(now.x, now.y, now.z);
            runBefore = run;
            run += (float) now.subtract(was).multiply(1, 0, 1).length();
            flames();
            return;
        }
        if (rules == null || driver == null || !(level() instanceof ServerLevel level)) {
            discard();
            return;
        }
        ticks++;
        Vec3 was = position(), now = start.lerp(end, rules.along(ticks));
        if (!giveWay(level, was, now)) {
            wreck(level, was);
            return;
        }
        setPos(now.x, now.y, now.z);
        strike(level, was, now);
        if (ticks % 4 == 0) SkillShows.sound(level, now, SoundEvents.HORSE_GALLOP, 2.5F, .6F);
        if (ticks % 3 == 0) level.sendParticles(ParticleTypes.FLAME, getX(), getY() + .6, getZ(), 10, WIDTH * .4, .4, WIDTH * .4, .03);
        if (rules.along(ticks) >= 1) finish(level, now);
    }

    /**
     * Breaks every block between where it was and where it is now, within its reach of its line, that gives way to it;
     * and whether its way is clear: nothing left there that would stop it.
     */
    private boolean giveWay(ServerLevel level, Vec3 was, Vec3 now) {
        boolean griefing = EventHooks.canEntityGrief(level, driver), clear = true;
        double reach = rules.reach();
        AABB swept = new AABB(was, now).inflate(reach, 0, reach);
        for (BlockPos at : BlockPos.betweenClosed(Mth.floor(swept.minX), Mth.floor(swept.minY + WHEELS), Mth.floor(swept.minZ),
                Mth.floor(swept.maxX - 1e-7), Mth.floor(swept.minY + HEIGHT - 1e-7), Mth.floor(swept.maxZ - 1e-7))) {
            if (ChariotRules.offLine(at.getX() + .5, at.getZ() + .5, was.x, was.z, now.x, now.z) > reach) continue;
            BlockState state = level.getBlockState(at);
            if (state.isAir() || !state.getFluidState().isEmpty() && state.getCollisionShape(level, at).isEmpty()) continue;
            if (griefing && rules.breaks(state.getDestroySpeed(level, at))) level.destroyBlock(at, true, driver);
            else if (!state.getCollisionShape(level, at).isEmpty()) clear = false;
        }
        return clear;
    }

    /** Everything within its reach of the stretch it just ran, not of its demon's side and not struck already, is struck. */
    private void strike(ServerLevel level, Vec3 was, Vec3 now) {
        double reach = rules.reach();
        Vec3 way = now.subtract(was).multiply(1, 0, 1);
        way = way.lengthSqr() < 1e-6 ? end.subtract(start).multiply(1, 0, 1).normalize() : way.normalize();
        for (LivingEntity met : level.getEntitiesOfClass(LivingEntity.class, new AABB(was, now).inflate(reach, 0, reach).expandTowards(0, HEIGHT, 0),
                met -> met.isAlive() && met != driver && !driver.isAlliedTo(met) && !struck.contains(met.getUUID())
                        && !(met instanceof Player player && (player.isCreative() || player.isSpectator()))
                        && ChariotRules.offLine(met.getX(), met.getZ(), was.x, was.z, now.x, now.z) <= reach + met.getBbWidth() / 2)) {
            struck.add(met.getUUID());
            met.invulnerableTime = 0;
            DamageSource blow = level.damageSources().mobAttack(driver);
            if (!met.hurt(blow, (float) rules.damage())) continue;
            met.igniteForSeconds(rules.burnSeconds());
            // Thrown aside from the line, the way it stood from it, and onward and up.
            Vec3 aside = met.position().subtract(now).multiply(1, 0, 1);
            Vec3 side = new Vec3(-way.z, 0, way.x);
            double which = aside.dot(side) >= 0 ? 1 : -1;
            met.push(way.x * rules.knockback() * .6 + side.x * which * rules.knockback(), .55, way.z * rules.knockback() * .6 + side.z * which * rules.knockback());
            met.hurtMarked = true;
            SkillShows.sound(level, met.position(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.5F, .6F);
        }
    }

    /** At the end of its line: gone in a burst of flame. */
    private void finish(ServerLevel level, Vec3 at) {
        burst(level, at);
        discard();
    }

    /** Something would not give way: it is wrecked there, in a burst of flame. */
    private void wreck(ServerLevel level, Vec3 at) {
        wrecked = true;
        SkillShows.sound(level, at, SoundEvents.ANVIL_LAND, 2, .5F);
        burst(level, at);
        discard();
    }

    private void burst(ServerLevel level, Vec3 at) {
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 1, at.z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.FLAME, at.x, at.y + 1, at.z, 60, 1.5, 1, 1.5, .12);
        SkillShows.show(level, at, "hellfire", 3, 2, 30, colour, accent, .5, getYRot(), true);
        SkillShows.sound(level, at, SoundEvents.GENERIC_EXPLODE.value(), 2.5F, .8F);
    }

    /** On a client: flames off its wheels and its horses as it runs. */
    private void flames() {
        if (random.nextInt(2) != 0) return;
        float yaw = getYRot() * Mth.DEG_TO_RAD;
        double ax = -Mth.sin(yaw), az = Mth.cos(yaw);
        for (double along : new double[]{-.8, 1.8}) {
            double side = (random.nextDouble() - .5) * WIDTH;
            level().addParticle(ParticleTypes.FLAME, getX() + ax * along - az * side, getY() + .3 + random.nextDouble() * 1.6, getZ() + az * along + ax * side,
                    -ax * .05, .04, -az * .05);
        }
    }

    /** How far it has run, as a client draws it, this far into the tick. */
    public float run(float partial) { return Mth.lerp(partial, runBefore, run); }

    /** Whether it came to grief against something that would not give way (for the tests). */
    public boolean wrecked() { return wrecked; }

    /** What it has struck so far. */
    public Set<UUID> struck() { return Set.copyOf(struck); }

    /** It is drawn larger than its box (FieryChariotRenderer.SIZE): it is not to be left undrawn while any of it can be seen. */
    @Override public AABB getBoundingBoxForCulling() { return getBoundingBox().inflate(3, 1.5, 3); }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean fireImmune() { return true; }
    @Override public boolean displayFireAnimation() { return false; }
    /** A client runs its line itself (see above): the places the server sends would only jar against it. */
    @Override public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {}

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        for (Vec3 at : new Vec3[]{start, end}) { buffer.writeDouble(at.x); buffer.writeDouble(at.y); buffer.writeDouble(at.z); }
        buffer.writeDouble(pace);
        buffer.writeVarInt(ticks);
        buffer.writeInt(colour);
        buffer.writeInt(accent);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        start = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        end = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        pace = buffer.readDouble();
        ticks = buffer.readVarInt();
        colour = buffer.readInt();
        accent = buffer.readInt();
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
}
