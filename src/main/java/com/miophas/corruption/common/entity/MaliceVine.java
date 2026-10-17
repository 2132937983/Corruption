package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.api.plant.*;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.plant.MaliceVineRules;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Minecraft implementation of the core malice-vine species: it grows as every anchored plant does, and besides, as
 * MaliceVineRules says, it broods the corruption's elites (one now and then, at random from the tags
 * corruption:vine_brood and corruption:vine_brood_aquatic, while a player is near and there is room about it) and
 * defends itself with a shockwave when something not of its side comes close or strikes it.
 */
public final class MaliceVine extends AnchoredLivingPlant {
    /** The entity event that shows it bringing forth an elite. */
    public static final byte BROOD_EVENT = 70;
    /** Its colours, for what it shows: the corruption's rose, and a pale rose. */
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4;

    private static final EntityDataAccessor<Integer> SHOCK = SynchedEntityData.defineId(MaliceVine.class, EntityDataSerializers.INT);

    /** Ticks it has waited for its next elite, and has yet to wait before it may let loose its shockwave again. */
    private int broodWaited, shockCooling;
    /** On a client: the ticks its last shockwave and its last elite came at. */
    private int shockSeen = -100, broodSeen = -100;

    public MaliceVine(EntityType<? extends MaliceVine> type, Level level) { super(type, level); }
    @Override protected LivingPlantKind plantKind() { return LivingPlantKind.MALICE_VINE; }
    public static AttributeSupplier.Builder attributes() { return attributes(LivingPlantKind.MALICE_VINE); }
    @Override protected int growthInterval() { return InfectionConfig.vineGrowthTicks(); }
    public static boolean hasRoom(LevelReader level, BlockPos anchor, int direction, int height) {
        return LivingPlantApi.hasRoom(LivingPlantKind.MALICE_VINE, height - 2, direction, new LevelLivingPlantSpace(level, anchor));
    }
    public static MaliceVine find(ServerLevel level, BlockPos anchor) {
        return AnchoredLivingPlant.find(level, anchor) instanceof MaliceVine vine ? vine : null;
    }
    public static MaliceVine sprout(ServerLevel level, BlockPos anchor) {
        return sprout(level, anchor, direction(level, anchor));
    }
    public static MaliceVine sprout(ServerLevel level, BlockPos anchor, int direction) {
        if (!level.hasChunkAt(anchor) || !level.getBlockState(anchor).is(ModBlocks.MALICE.get())) return null;
        AnchoredLivingPlant existing = AnchoredLivingPlant.find(level, anchor);
        if (existing != null) return existing instanceof MaliceVine vine ? vine : null;
        LevelLivingPlantSpace space = new LevelLivingPlantSpace(level, anchor);
        if (!LivingPlantApi.hasRoom(LivingPlantKind.MALICE_VINE, 0, direction, space)) return null;
        MaliceVine vine = ModEntities.MALICE_VINE.get().create(level);
        if (vine == null) return null;
        LivingPlantApi.prepareGrowth(LivingPlantKind.MALICE_VINE, 0, direction, space);
        vine.initializeAnchor(anchor, direction);
        vine.setYRot(level.random.nextFloat() * 360); vine.yBodyRot = vine.yHeadRot = vine.getYRot();
        return level.addFreshEntity(vine) ? vine : null;
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOCK, 0);
    }

    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (shockCooling > 0) shockCooling--;
        if (shocking() > 0) {
            int left = shocking() - 1;
            entityData.set(SHOCK, left);
            if (left == 0) release(server);
        } else if (shockCooling == 0 && (tickCount + getId()) % 10 == 0 && threatened(server)) gather();
        if ((tickCount + getId()) % 20 == 0) {
            broodWaited = MaliceVineRules.waited(broodWaited, 20);
            if (MaliceVineRules.due(broodWaited, InfectionConfig.vineEliteInterval())) brood(server);
        }
    }

    // ---- its defence ----

    /** Struck by what is not of its side (and not hidden from it), it gathers itself to strike back, if it can. */
    @Override public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && isAlive() && shocking() == 0 && shockCooling == 0 && source.getEntity() instanceof LivingEntity striker
                && foe(striker)) gather();
        return hurt;
    }

    /** Whether something not of its side, and not hidden from it, is close enough to set its shockwave off. */
    boolean threatened(ServerLevel level) {
        double near = MaliceVineRules.shockTrigger(stage());
        return !level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(near), this::foe).isEmpty();
    }

    /** What its shockwave is for: anything alive not of its side and not hidden from it, but a player in creative or spectator. */
    private boolean foe(LivingEntity creature) {
        return creature != this && creature.isAlive() && !isAlliedTo(creature) && !CorruptionVeilApi.hiddenFrom(creature, this)
                && !(creature instanceof Player player && (player.isCreative() || player.isSpectator()));
    }

    /** It gathers itself (unless it already is, or must wait yet): its crown swells and its tendrils rise, the ground it will sweep marked. */
    void gather() {
        if (!(level() instanceof ServerLevel level) || shocking() > 0 || shockCooling > 0) return;
        entityData.set(SHOCK, MaliceVineRules.SHOCK_WINDUP);
        double radius = MaliceVineRules.shockRadius(stage());
        SkillShows.show(level, base(), "rune_ring", radius, .6, MaliceVineRules.SHOCK_WINDUP + 4, ROSE, PALE, 0, getYRot(), !hanging());
        SkillShows.sound(this, SoundEvents.EVOKER_PREPARE_ATTACK, 1.5F, .5F);
        SkillShows.sound(this, SoundEvents.CAVE_VINES_PLACE, 2, .5F);
    }

    /** It lets loose: a ring of force round it, hurting what is not of its side within its reach and throwing it away and up. */
    private void release(ServerLevel level) {
        double radius = MaliceVineRules.shockRadius(stage());
        Vec3 middle = getBoundingBox().getCenter();
        AABB reach = getBoundingBox().inflate(radius);
        for (LivingEntity struck : level.getEntitiesOfClass(LivingEntity.class, reach, creature -> foe(creature)
                && creature.getBoundingBox().distanceToSqr(closest(creature)) <= radius * radius)) {
            struck.hurt(ModDamageTypes.corruption(level, this), (float) MaliceVineRules.SHOCK_DAMAGE);
            Vec3 away = struck.position().subtract(middle).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? new Vec3(1, 0, 0) : away.normalize();
            struck.push(away.x * MaliceVineRules.SHOCK_KNOCKBACK, MaliceVineRules.SHOCK_LIFT, away.z * MaliceVineRules.SHOCK_KNOCKBACK);
            struck.hurtMarked = true;
        }
        shockCooling = MaliceVineRules.SHOCK_COOLDOWN;
        SkillShows.show(level, base(), "shockwave", radius * 1.2, 2, 18, ROSE, PALE, .4, getYRot(), !hanging());
        SkillShows.show(level, middle, "nova", 1.5, 0, 10, ROSE, PALE, 0, getYRot(), false);
        SkillShows.sound(this, SoundEvents.EVOKER_FANGS_ATTACK, 2, .6F);
        SkillShows.sound(this, SoundEvents.GENERIC_EXPLODE.value(), 1, 1.4F);
    }

    /** The nearest point of it to what it strikes: anywhere along its stalk. */
    private Vec3 closest(LivingEntity creature) {
        AABB box = getBoundingBox();
        Vec3 at = creature.getBoundingBox().getCenter();
        return new Vec3(Mth.clamp(at.x, box.minX, box.maxX), Mth.clamp(at.y, box.minY, box.maxY), Mth.clamp(at.z, box.minZ, box.maxZ));
    }

    /** Where it stands on its malice: the foot of its stalk, or for one that hangs, its top. */
    private Vec3 base() {
        BlockPos anchor = anchor();
        return new Vec3(anchor.getX() + .5, anchor.getY() + (hanging() ? 0 : 1), anchor.getZ() + .5);
    }

    /** Ticks left of its gathering itself for a shockwave; 0 when it is not. */
    public int shocking() { return entityData.get(SHOCK); }

    // ---- its brood ----

    /**
     * Its time has come: if a player is near, and there is room about it, it brings forth an elite, at random from those
     * that could live there, a few blocks from it (one of the sea's in the water). Where it found no place, it tries again
     * a little later.
     */
    void brood(ServerLevel level) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(MaliceVineRules.BROOD_PLAYER_RANGE), player -> player.isAlive() && !player.isSpectator())
                .isEmpty()) return;
        int about = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(MaliceVineRules.BROOD_COUNT_RANGE),
                creature -> creature.isAlive() && CorruptionFactionApi.isElite(creature)).size();
        if (!MaliceVineRules.room(about, InfectionConfig.vineEliteCap())) return;
        List<EntityType<?>> land = tagged(MaliceVineApi.BROOD), sea = tagged(MaliceVineApi.AQUATIC_BROOD);
        boolean water = !sea.isEmpty() && inWater(level, sea.get(0)) != null;
        int picked = MaliceVineRules.pick(land.size(), sea.size(), water, random.nextDouble());
        if (picked < 0) return;
        boolean aquatic = picked >= land.size();
        EntityType<?> type = aquatic ? sea.get(picked - land.size()) : land.get(picked);
        Vec3 place = aquatic ? inWater(level, type) : onGround(level, type);
        if (place == null || bringForth(level, type, place) == null) {
            // No place for it now: it tries again in a little while.
            broodWaited = Math.max(0, InfectionConfig.vineEliteInterval() - 200);
            return;
        }
        broodWaited = 0;
    }

    /** Brings forth an elite of this kind at this place, as its brood; what it brought forth, or null if it could not. */
    @Nullable Mob bringForth(ServerLevel level, EntityType<?> type, Vec3 place) {
        if (!(type.create(level) instanceof Mob elite)) return null;
        elite.moveTo(place.x, place.y, place.z, random.nextFloat() * 360, 0);
        net.neoforged.neoforge.event.EventHooks.finalizeMobSpawn(elite, level, level.getCurrentDifficultyAt(BlockPos.containing(place)), MobSpawnType.MOB_SUMMONED, null);
        if (!level.addFreshEntity(elite)) return null;
        level.broadcastEntityEvent(this, BROOD_EVENT);
        SkillShows.show(level, place, "miasma", 1.6, 2.5, 30, ROSE, PALE, 0, getYRot(), false);
        SkillShows.show(level, getBoundingBox().getCenter(), "bloom", 1.2, 1, 20, ROSE, PALE, 0, getYRot(), false);
        SkillShows.sound(level, place, SoundEvents.SNIFFER_EGG_HATCH, 1.5F, .5F);
        SkillShows.sound(this, SoundEvents.CAVE_VINES_PLACE, 1.5F, .4F);
        return elite;
    }

    private static List<EntityType<?>> tagged(TagKey<EntityType<?>> tag) {
        return BuiltInRegistries.ENTITY_TYPE.getTag(tag).map(set -> set.stream().<EntityType<?>>map(Holder::value).toList()).orElse(List.of());
    }

    /** A place on the ground a few blocks from it where an elite of this kind would stand clear, or null if none was found. */
    @Nullable Vec3 onGround(ServerLevel level, EntityType<?> type) {
        AABB box = getBoundingBox();
        int top = Mth.floor(box.maxY) + 2, bottom = Mth.floor(box.minY) - 8;
        for (int tried = 0; tried < MaliceVineRules.BROOD_TRIES; tried++) {
            double turn = random.nextDouble() * Math.PI * 2, out = Mth.nextDouble(random, MaliceVineRules.BROOD_NEAR, MaliceVineRules.BROOD_FAR);
            double x = anchor().getX() + .5 + Math.cos(turn) * out, z = anchor().getZ() + .5 + Math.sin(turn) * out;
            for (int y = top; y >= bottom; y--) {
                BlockPos below = BlockPos.containing(x, y - 1, z);
                if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) continue;
                AABB there = type.getSpawnAABB(x, y, z);
                if (level.noCollision(there) && !level.containsAnyLiquid(there)) return new Vec3(x, y, z);
                break;
            }
        }
        return null;
    }

    /**
     * A place in the water near it where an elite of this kind would swim clear, or null if there is none: it looks
     * through all the water about it (this only when its time has come), and takes one of the first places it finds.
     */
    @Nullable Vec3 inWater(ServerLevel level, EntityType<?> type) {
        BlockPos anchor = anchor();
        int range = MaliceVineRules.WATER_RANGE;
        List<Vec3> found = new java.util.ArrayList<>();
        for (BlockPos at : BlockPos.betweenClosed(anchor.offset(-range, -6, -range), anchor.offset(range, 2, range))) {
            if (!level.getFluidState(at).is(FluidTags.WATER)) continue;
            Vec3 place = new Vec3(at.getX() + .5, at.getY(), at.getZ() + .5);
            AABB there = type.getSpawnAABB(place.x, place.y, place.z);
            if (level.noCollision(there) && allWater(level, there) && found.add(place) && found.size() >= MaliceVineRules.BROOD_TRIES) break;
        }
        return found.isEmpty() ? null : found.get(random.nextInt(found.size()));
    }

    private static boolean allWater(ServerLevel level, AABB box) {
        for (BlockPos at : BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX - 1e-4, box.maxY - 1e-4, box.maxZ - 1e-4)))
            if (!level.getFluidState(at).is(FluidTags.WATER)) return false;
        return true;
    }

    /** Ticks it has waited for its next elite; and, for the tests, how long it has. */
    public int broodWaited() { return broodWaited; }
    void waitedFor(int ticks) { broodWaited = Math.max(0, ticks); }

    // ---- what the client sees ----

    /** On a client: how far through gathering itself it is, 0 to 1 (0 when it is not). */
    public float gathered(float partial) {
        int left = shocking();
        return left <= 0 ? 0 : Mth.clamp(1 - (left - partial) / MaliceVineRules.SHOCK_WINDUP, 0, 1);
    }

    /** On a client: ticks since its last shockwave, and since it last brought forth an elite. */
    public float sinceShock(float partial) { return tickCount - shockSeen + partial; }
    public float sinceBrood(float partial) { return tickCount - broodSeen + partial; }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SHOCK.equals(key) && shocking() == 0 && level().isClientSide && tickCount - shockSeen > 4) shockSeen = tickCount;
    }

    @Override public void handleEntityEvent(byte event) {
        if (event == BROOD_EVENT) broodSeen = tickCount;
        else super.handleEntityEvent(event);
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BroodWaited", broodWaited);
        tag.putInt("ShockCooling", shockCooling);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        broodWaited = Math.max(0, tag.getInt("BroodWaited"));
        shockCooling = Math.max(0, tag.getInt("ShockCooling"));
    }

    /** Whether something is one of the elites it may bring forth (for whoever would ask). */
    public static boolean brood(Entity entity) { return entity.getType().is(MaliceVineApi.BROOD) || entity.getType().is(MaliceVineApi.AQUATIC_BROOD); }
}
