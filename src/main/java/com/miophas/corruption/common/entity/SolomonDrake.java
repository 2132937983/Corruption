package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.SolomonRules;
import com.miophas.corruption.core.demon.skill.DrakeRushRules;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/**
 * One of the small dragons Solomon sends rushing in from every side (DrakeRushRules): it appears where it is sent from, hangs
 * there a moment beating its wings, facing the mark, then rushes straight through the mark to the far side, where it is gone.
 * Whatever it passes within its width that is not of its sender's side is hurt (the corruption's hurt, or Solomon's) and
 * thrown on, each once. Server and client alike work out where it is from where it was sent and how long ago, so it flies
 * true. It is not there to be struck at, and is never saved.
 */
public final class SolomonDrake extends Entity implements IEntityWithComplexSpawn {
    private DrakeRushRules rules = SolomonRules.DRAKES;
    @Nullable private UUID sender;
    private Vec3 from = Vec3.ZERO, to = Vec3.ZERO;
    private int colour = SolomonRules.VIOLET;
    private int age;
    private final Set<UUID> hit = new HashSet<>();
    private final List<LivingEntity> struck = new ArrayList<>();

    public SolomonDrake(EntityType<? extends SolomonDrake> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    /** Sends one from here through the mark to there, for its sender, as the rules say. */
    public static SolomonDrake send(ServerLevel level, Mob sender, Vec3 from, Vec3 to, DrakeRushRules rules, int colour, int accent) {
        SolomonDrake drake = new SolomonDrake(ModEntities.SOLOMON_DRAKE.get(), level);
        drake.sender = sender.getUUID();
        drake.rules = rules;
        drake.from = from;
        drake.to = to;
        drake.colour = colour;
        drake.place();
        level.addFreshEntity(drake);
        SkillShows.show(level, from, "miasma", 1.2, 1.2, 20, colour, accent, 0, drake.getYRot(), false);
        return drake;
    }

    public DrakeRushRules rules() { return rules; }
    public int colour() { return colour; }
    public Vec3 from() { return from; }
    public Vec3 to() { return to; }
    /** How long it has been about, in ticks. */
    public float age(float partial) { return age + partial; }
    /** Whether it has begun its rush. */
    public boolean rushing() { return age > rules.gather(); }
    /** What it caught. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    /** Where it is: where it was sent from while it gathers, then along its way at its speed. */
    public Vec3 at(double age) {
        double flown = Math.max(0, age - rules.gather()) * rules.speed(), whole = from.distanceTo(to);
        return whole < 1e-6 ? from : from.add(to.subtract(from).scale(Math.min(1, flown / whole)));
    }

    private void place() {
        Vec3 at = at(age), way = to.subtract(from);
        setPos(at.x, at.y, at.z);
        if (way.lengthSqr() > 1e-6) {
            setYRot((float) (Mth.atan2(-way.x, way.z) * Mth.RAD_TO_DEG));
            setXRot((float) (-Mth.atan2(way.y, way.horizontalDistance()) * Mth.RAD_TO_DEG));
        }
    }

    @Override public void tick() {
        Vec3 was = position();
        super.tick();
        age++;
        place();
        xo = was.x; yo = was.y; zo = was.z;
        if (!(level() instanceof ServerLevel level)) return;
        if (rushing()) {
            strike(level);
            if (age % 2 == 0) level.sendParticles(ParticleTypes.DRAGON_BREATH, getX(), getY() + .3, getZ(), 2, .2, .2, .2, .01);
        }
        if (age == rules.gather()) SkillShows.sound(level, position(), SoundEvents.PHANTOM_SWOOP, 2, 1.3F);
        if (age >= rules.lasts()) {
            level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 8, .3, .3, .3, .02);
            discard();
        }
    }

    /** What it passes within its width is hurt and thrown on, each once. */
    private void strike(ServerLevel level) {
        Entity by = sender == null ? null : level.getEntity(sender);
        DamageSource hurt = by instanceof LivingEntity living ? ModDamageTypes.corruption(level, living) : ModDamageTypes.solomon(level, this);
        Vec3 way = to.subtract(from).normalize(), here = position().add(0, getBbHeight() / 2, 0);
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(here, here).inflate(rules.width() + 1), near -> near.isAlive()
                && near != by && !hit.contains(near.getUUID()) && !(by != null && (by.isAlliedTo(near) || near.isAlliedTo(by)))
                && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                && near.getBoundingBox().getCenter().distanceTo(here) <= rules.width() + near.getBbWidth() / 2)) {
            hit.add(near.getUUID());
            near.hurt(hurt, (float) rules.damage());
            near.push(way.x * rules.knockback(), .3, way.z * rules.knockback());
            near.hurtMarked = true;
            struck.add(near);
        }
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 160 * 160; }

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(age);
        buffer.writeInt(colour);
        buffer.writeDouble(from.x); buffer.writeDouble(from.y); buffer.writeDouble(from.z);
        buffer.writeDouble(to.x); buffer.writeDouble(to.y); buffer.writeDouble(to.z);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        age = buffer.readVarInt();
        colour = buffer.readInt();
        from = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        to = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        place();
        xo = getX(); yo = getY(); zo = getZ();
    }
}
