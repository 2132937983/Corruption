package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BlightSkill;
import com.miophas.corruption.common.entity.skill.DrakeRushSkill;
import com.miophas.corruption.common.entity.skill.EliteCallSkill;
import com.miophas.corruption.common.entity.skill.GazeSkill;
import com.miophas.corruption.common.entity.skill.ScreechSkill;
import com.miophas.corruption.common.entity.skill.SummonSkill;
import com.miophas.corruption.common.entity.skill.UpheavalSkill;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.SolomonRules;
import com.miophas.corruption.core.demon.SolomonRules.Edict;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Solomon (SolomonRules): the crowned seal of eight points his eight flood dragons burst out of, hanging in the sky over the
 * altar he was called up at. Nothing can hurt it: his life is in his dragons ({@link SolomonWyrm}), and his bar shows theirs,
 * each its own share of it ({@link #share}). When the last of them falls, so does he, and what he leaves (his relic) he leaves
 * here, to whoever slew the last.
 *
 * <p>It calls up his dragons the first time it reaches the place it hangs; keeps count of them, alive or fallen, wherever they
 * fly (a dragon that falls where his seal cannot see it still tells it, through {@link SolomonLedger}); lets no more than
 * {@link SolomonRules#CASTERS} of them be at work at once ({@link #mayCast}); and works its own edicts upon whoever they hunt.
 */
public final class Solomon extends GoeticDemon {
    /** His death: his crown cracking, his seal turning faster and faster, his light breaking out of it; then he bursts. */
    public static final BossDeath DEATH = new BossDeath(SolomonRules.DEATH_TICKS, 80, 3F,
            new BossDeath.Rays(32, 18, SolomonRules.GOLD, SolomonRules.VIOLET), .4F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "crown", 3, 2, 60, SolomonRules.GOLD, SolomonRules.VIOLET, .2, "minecraft:block.bell.resonate", 6, .5F),
            BossDeath.beat(20, BossDeath.Anchor.HEART, "sigil", 8, 0, 80, SolomonRules.VIOLET, SolomonRules.GOLD, .3, "minecraft:block.respawn_anchor.deplete", 6, .6F),
            BossDeath.beat(50, BossDeath.Anchor.HEART, "shards", 4, 2, 40, SolomonRules.GOLD, 0xFFFFFF, .3, "minecraft:block.glass.break", 6, .5F),
            BossDeath.beat(80, BossDeath.Anchor.HEART, "rays", 12, 0, 60, SolomonRules.GOLD, SolomonRules.VIOLET, .5, "minecraft:entity.wither.death", 8, .7F),
            BossDeath.beat(138, BossDeath.Anchor.HEART, "nova", 24, 0, 40, SolomonRules.GOLD, 0xFFFFFF, 1, "minecraft:entity.generic.explode", 8, .6F),
            BossDeath.beat(140, BossDeath.Anchor.FEET, "light_pillar", 3, 60, 70, SolomonRules.GOLD, SolomonRules.VIOLET, 0, "minecraft:block.beacon.deactivate", 8, .5F)));

    /** Each dragon's share of his bar, as the clients are told it: its health left, as a share of its greatest; 0 once it has fallen. */
    private static final List<EntityDataAccessor<Float>> SHARES = new ArrayList<>();
    static {
        for (int dragon = 0; dragon < SolomonRules.DRAGONS; dragon++) SHARES.add(SynchedEntityData.defineId(Solomon.class, EntityDataSerializers.FLOAT));
    }
    private static final EntityDataAccessor<Byte> EDICT = SynchedEntityData.defineId(Solomon.class, EntityDataSerializers.BYTE);

    private final UUID[] dragons = new UUID[SolomonRules.DRAGONS];
    private final float[] shares = new float[SolomonRules.DRAGONS];
    private final boolean[] fallen = new boolean[SolomonRules.DRAGONS];
    /** The blow that felled each of his dragons, as far as it is known; the last of them is his own death's. */
    @Nullable private DamageSource lastBlow;
    @Nullable private Player lastSlayer;
    @Nullable private Vec3 home;
    private boolean called;

    private final GazeSkill gaze = new GazeSkill(SolomonRules.GAZE, SolomonRules.VIOLET, SolomonRules.GOLD);
    private final GazeSkill greaterGaze = new GazeSkill(SolomonRules.GREATER_GAZE, SolomonRules.VIOLET, SolomonRules.GOLD);
    private final EliteCallSkill elites = new EliteCallSkill(SolomonRules.ELITES, SolomonRules.VIOLET, SolomonRules.GOLD);
    private final SummonSkill remnants = new SummonSkill(SolomonRules.REMNANTS, SolomonRules.VIOLET, SolomonRules.GOLD).counting(SolomonRules.FOLLOW_RANGE);
    private final UpheavalSkill upheaval = new UpheavalSkill(SolomonRules.UPHEAVAL, SolomonRules.VIOLET, SolomonRules.GOLD);
    private final BlightSkill dormant = new BlightSkill(SolomonRules.DORMANT, SolomonRules.VIOLET, SolomonRules.GOLD, true);
    private final ScreechSkill stun = new ScreechSkill(SolomonRules.STUN, SolomonRules.VIOLET, SolomonRules.GOLD, true);
    private final DrakeRushSkill drakes = new DrakeRushSkill(SolomonRules.DRAKES, SolomonRules.VIOLET, SolomonRules.GOLD);
    /** His edicts, in the order of SolomonRules.EDICTS. */
    private final SkillSet<Solomon> edicts = SkillSet.<Solomon>of()
            .with("gaze", gaze).with("greater_gaze", greaterGaze).with("elites", elites).with("remnants", remnants)
            .with("upheaval", upheaval).with("dormant", dormant).with("stun", stun).with("drakes", drakes);
    private int rest = SolomonRules.CORE_REST;

    public Solomon(EntityType<? extends Solomon> type, Level level) {
        super(type, level, BossEvent.BossBarColor.YELLOW);
        xpReward = SolomonRules.EXPERIENCE;
        setNoGravity(true);
        noPhysics = true;
        for (int dragon = 0; dragon < shares.length; dragon++) shares[dragon] = 1;
        // As the Ender Dragon's does, his bar fogs the world of whoever sees it; as the Wither's, it darkens their sky.
        bossEvent().setDarkenScreen(true).setCreateWorldFog(true).setPlayBossMusic(true);
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, SolomonRules.WYRM_HEALTH * SolomonRules.DRAGONS)
                .add(Attributes.FOLLOW_RANGE, SolomonRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.MOVEMENT_SPEED, .2);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        for (EntityDataAccessor<Float> share : SHARES) builder.define(share, 1F);
        builder.define(EDICT, (byte) 0);
    }

    /** He hunts what is not of the corruption, as far below him as about him; his dragons take his quarry when they have none. */
    @Override protected void registerGoals() { huntTheUncorruptedFromOnHigh(1); }

    // ---- nothing can hurt it ----

    /** Nothing hurts it but what passes every defence (/kill, the void): his life is in his dragons. */
    @Override public boolean isInvulnerableTo(DamageSource source) { return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY); }
    @Override public boolean hurt(DamageSource source, float amount) { return !isInvulnerableTo(source) && super.hurt(source, amount); }
    @Override public boolean isPickable() { return false; }
    /** Nothing hunts his seal, which nothing can hurt: what would fight him turns on his dragons. */
    @Override public boolean canBeSeenAsEnemy() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean isAffectedByPotions() { return false; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override protected void doPush(Entity entity) {}

    // ---- his dragons ----

    /** The share of his bar a dragon holds: its health left, as a share of its greatest; 0 once it has fallen. */
    public float share(int dragon) { return entityData.get(SHARES.get(dragon)); }

    /** His bar: his dragons' health together, as a share of all of theirs at their greatest. */
    @Override protected float barProgress() {
        float sum = 0;
        for (float share : shares) sum += share;
        return sum / shares.length;
    }

    /** Whether one more of his dragons may set to work now. */
    public boolean mayCast() {
        if (!(level() instanceof ServerLevel level)) return true;
        int casting = 0;
        for (UUID id : dragons) if (id != null && level.getEntity(id) instanceof SolomonWyrm dragon && dragon.isAlive() && dragon.casting()) casting++;
        return SolomonRules.mayCast(casting);
    }

    /** Whether one more of his dragons may swoop now (no more than two at once). */
    public boolean maySwoop() {
        int swooping = 0;
        for (SolomonWyrm dragon : dragons()) if (dragon.swooping()) swooping++;
        return SolomonRules.maySwoop(swooping);
    }

    /** Where he hangs: over the altar he was called up at. */
    public Vec3 home() { return home == null ? position() : home; }

    /** A dragon of his has fallen to this blow (from a dragon's death, or the ledger). */
    void fell(int dragon, @Nullable DamageSource blow) {
        if (dragon < 0 || dragon >= fallen.length || fallen[dragon]) return;
        fallen[dragon] = true;
        shares[dragon] = 0;
        if (blow != null) {
            lastBlow = blow;
            if (blow.getEntity() instanceof Player player) lastSlayer = player;
        }
    }

    /** How many of his dragons have fallen. */
    public int fallen() {
        int count = 0;
        for (boolean down : fallen) if (down) count++;
        return count;
    }

    /** His dragons that live and are about, in no order. */
    public List<SolomonWyrm> dragons() {
        List<SolomonWyrm> found = new ArrayList<>();
        if (level() instanceof ServerLevel level)
            for (UUID id : dragons) if (id != null && level.getEntity(id) instanceof SolomonWyrm dragon && dragon.isAlive()) found.add(dragon);
        return found;
    }

    /** Calls up his eight dragons out of his seal, bursting out the eight ways about it. */
    private void callDragons(ServerLevel level) {
        called = true;
        for (int index = 0; index < SolomonRules.DRAGONS; index++) {
            SolomonWyrm dragon = ModEntities.SOLOMON_WYRM.get().create(level);
            if (dragon == null) continue;
            double angle = 2 * Math.PI * index / SolomonRules.DRAGONS;
            Vec3 out = new Vec3(Math.cos(angle), .35, Math.sin(angle)).normalize();
            Vec3 at = position().add(0, getBbHeight() / 2 - SolomonRules.HEAD_HEIGHT / 2, 0);
            dragon.moveTo(at.x, at.y, at.z, (float) Math.toDegrees(Math.atan2(-out.x, out.z)), 0);
            dragon.serve(this, index, out.scale(SolomonRules.CRUISE));
            dragon.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPosition()), MobSpawnType.EVENT, null);
            if (getTarget() != null) dragon.setTarget(getTarget());
            level.addFreshEntity(dragon);
            dragons[index] = dragon.getUUID();
        }
        com.miophas.corruption.common.entity.skill.SkillShows.show(level, position().add(0, getBbHeight() / 2, 0), "nova", 12, 0, 40,
                SolomonRules.GOLD, SolomonRules.VIOLET, .8, getYRot(), false);
        com.miophas.corruption.common.entity.skill.SkillShows.sound(this, SoundEvents.ENDER_DRAGON_GROWL, 8, .5F);
    }

    // ---- his mind ----

    @Override public void tick() {
        noPhysics = true;
        super.tick();
    }

    /** It moves only as it is told to, through anything. */
    @Override public void travel(Vec3 input) { if (isControlledByLocalInstance()) move(MoverType.SELF, getDeltaMovement()); }

    @Override protected void customServerAiStep() {
        if (!(level() instanceof ServerLevel level)) return;
        if (home == null) home = position().add(0, SolomonRules.CORE_HEIGHT, 0);
        storm(level);
        hang();
        if (!called && position().distanceTo(home) < 1.5) callDragons(level);
        keepCount(level);
        super.customServerAiStep();
        if (called && fallen() == fallen.length && !isDeadOrDying()) { fall(level); return; }
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target.level() != level() || target.distanceToSqr(home) > SolomonRules.LEASH * SolomonRules.LEASH)) {
            setTarget(null);
            target = null;
        }
        if (target != null) for (SolomonWyrm dragon : dragons()) if (dragon.getTarget() == null) dragon.setTarget(target);
        if (rest > 0) rest--;
        if (called && !edicts.busy() && target != null && rest == 0) decree(target);
        edicts.tick(this, target, cooldown -> cooldown);
        if (!edicts.busy() && edict() != Edict.NONE) {
            setEdict(Edict.NONE);
            rest = SolomonRules.CORE_REST;
        }
    }

    /**
     * While he is about, the sky over him is a thunderstorm: it breaks as he comes, with a clap of thunder, and is held for
     * some while ahead, so that it ends about half a minute after he falls. Only where there is weather (a sky).
     */
    private void storm(ServerLevel level) {
        if (!level.dimensionType().hasSkyLight()) return;
        boolean raging = level.getLevelData().isThundering() && level.getLevelData().isRaining();
        if (raging && tickCount % SolomonRules.STORM_RENEW != 0) return;
        level.setWeatherParameters(0, SolomonRules.STORM_HOLD, true, true);
        if (!raging) level.playSound(null, getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, net.minecraft.sounds.SoundSource.WEATHER, 10000, .6F);
    }

    /** It rises to the place it hangs, and hangs there, rising and falling a little, turning slowly. */
    private void hang() {
        Vec3 to = home().add(0, Math.sin(tickCount / 30.0) * .6, 0).subtract(position());
        double speed = Math.min(.25, to.length() * .1);
        setDeltaMovement(to.lengthSqr() < 1e-6 ? Vec3.ZERO : to.normalize().scale(speed));
        setYRot(getYRot() + .6F);
        yBodyRot = yHeadRot = getYRot();
    }

    /** Looks over his dragons: how hurt each is, and which have fallen (from here, or as the ledger says). */
    private void keepCount(ServerLevel level) {
        for (int index : SolomonLedger.of(level).fallen(getUUID())) fell(index, null);
        for (int index = 0; index < dragons.length; index++) {
            if (dragons[index] == null || fallen[index]) continue;
            Entity found = level.getEntity(dragons[index]);
            if (found instanceof SolomonWyrm dragon) {
                if (dragon.isDeadOrDying()) fell(index, dragon.getLastDamageSource());
                else shares[index] = dragon.getHealth() / dragon.getMaxHealth();
            }
        }
        for (int index = 0; index < shares.length; index++)
            if (entityData.get(SHARES.get(index)) != shares[index]) entityData.set(SHARES.get(index), shares[index]);
    }

    /** The last of his dragons has fallen: so does he, by the blow that felled it, to whoever struck it. */
    private void fall(ServerLevel level) {
        if (lastSlayer != null) setLastHurtByPlayer(lastSlayer);
        DamageSource blow = lastBlow != null ? lastBlow : damageSources().magic();
        setHealth(0);
        die(blow);
    }

    private void decree(LivingEntity target) {
        Set<Edict> ready = EnumSet.noneOf(Edict.class);
        for (Edict edict : SolomonRules.EDICTS) if (edicts.usable(index(edict), this, target)) ready.add(edict);
        if (ready.isEmpty()) return;
        int elitesAbout = level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(SolomonRules.ELITES.radius() * 2),
                elite -> elite.isAlive() && com.miophas.corruption.api.corruption.CorruptionFactionApi.isElite(elite)).size();
        SolomonRules.EdictSituation now = new SolomonRules.EdictSituation(distanceTo(target), hasLineOfSight(target), elitesAbout,
                SummonSkill.called(this, SolomonRules.FOLLOW_RANGE), target.onGround(), ready);
        Edict next = SolomonRules.chooseEdict(now, random.nextDouble());
        if (next != Edict.NONE) begin(next, target);
    }

    /** Begins one of his edicts on a quarry, if it has come round; whether it began (package-private, for the tests). */
    boolean begin(Edict edict, LivingEntity target) {
        if (!edicts.begin(index(edict), this, target)) return false;
        setEdict(edict);
        return true;
    }

    private static int index(Edict edict) { return SolomonRules.EDICTS.indexOf(edict); }

    private void setEdict(Edict edict) { entityData.set(EDICT, (byte) edict.ordinal()); }

    /** The edict he is working, as the clients are told. */
    public Edict edict() {
        int index = entityData.get(EDICT);
        return index >= 0 && index < Edict.values().length ? Edict.values()[index] : Edict.NONE;
    }

    @Override public void die(DamageSource source) {
        edicts.cease(this);
        setEdict(Edict.NONE);
        // Struck down by what passes every defence: his dragons go with him; and his page in the ledger is done with.
        for (SolomonWyrm dragon : dragons()) if (!dragon.isDeadOrDying()) dragon.kill();
        if (level() instanceof ServerLevel level) SolomonLedger.of(level).forget(getUUID());
        super.die(source);
    }

    // ---- what it is ----

    @Override public BossDeath death() { return DEATH; }
    @Override public int emblem() { return 0; }
    /** The middle of his seal. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() / 2, 0); }

    /** It is drawn from far off, a crown in the sky. */
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 256 * 256; }
    @Override public AABB getBoundingBoxForCulling() { return super.getBoundingBoxForCulling().inflate(6); }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.BEACON_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.AMETHYST_BLOCK_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WITHER_DEATH; }
    @Override public int getAmbientSoundInterval() { return 160; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        for (int index = 0; index < dragons.length; index++) {
            CompoundTag dragon = new CompoundTag();
            if (dragons[index] != null) dragon.putUUID("Id", dragons[index]);
            dragon.putFloat("Share", shares[index]);
            dragon.putBoolean("Fallen", fallen[index]);
            list.add(dragon);
        }
        tag.put("Dragons", list);
        tag.putBoolean("Called", called);
        if (home != null) tag.put("Home", NbtUtils.writeBlockPos(BlockPos.containing(home)));
        tag.put("Edicts", edicts.save());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag list = tag.getList("Dragons", Tag.TAG_COMPOUND);
        for (int index = 0; index < Math.min(list.size(), dragons.length); index++) {
            CompoundTag dragon = list.getCompound(index);
            dragons[index] = dragon.hasUUID("Id") ? dragon.getUUID("Id") : null;
            shares[index] = dragon.getFloat("Share");
            fallen[index] = dragon.getBoolean("Fallen");
        }
        called = tag.getBoolean("Called");
        NbtUtils.readBlockPos(tag, "Home").ifPresent(at -> home = Vec3.atBottomCenterOf(at));
        edicts.load(tag.getCompound("Edicts"));
    }

    /** His edicts, as they stand. */
    public SkillSet<Solomon> edicts() { return edicts; }
    public EliteCallSkill elites() { return elites; }
    public DrakeRushSkill drakes() { return drakes; }
    public BlightSkill dormant() { return dormant; }
    public ScreechSkill stun() { return stun; }
}
