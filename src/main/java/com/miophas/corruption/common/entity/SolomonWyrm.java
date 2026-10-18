package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BeamCaster;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.common.entity.skill.BarrageSkill;
import com.miophas.corruption.common.entity.skill.BeamSkill;
import com.miophas.corruption.common.entity.skill.BurrowSkill;
import com.miophas.corruption.common.entity.skill.FireballSkill;
import com.miophas.corruption.common.entity.skill.HandSkill;
import com.miophas.corruption.common.entity.skill.RamSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SkyBeamSkill;
import com.miophas.corruption.common.entity.skill.StrikeSkill;
import com.miophas.corruption.common.entity.skill.VenusSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModTags;
import com.miophas.corruption.core.demon.SolomonRules;
import com.miophas.corruption.core.demon.SolomonRules.Wyrm;
import com.miophas.corruption.core.demon.WyrmChain;
import com.miophas.corruption.core.demon.WyrmPose;
import com.miophas.corruption.core.demon.WyrmWake;
import com.miophas.corruption.core.demon.skill.SwoopRules;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;

/**
 * One of Solomon's eight flood dragons (SolomonRules): a long serpent of the deep, four chunks from snout to tail, that hangs in
 * the air. What is struck at is its head (this entity) and every link of its body ({@link Link}), which follows its head the way
 * it went ({@link WyrmChain}) and moves as it works ({@link WyrmPose}); each a blow on the dragon itself, and only the arcane
 * can hurt it (GoeticDemon). What lies buried of it cannot be struck at.
 *
 * <p>It breaks what it flies through, as the Ender Dragon does ({@link #smash}), but for what the Ender Dragon cannot break and
 * the altar's offering and pillars; burrowing, it swims through the earth instead. Whatever its head and body pass through is
 * hurt, again and again ({@link #touch}).
 *
 * <p>While it does nothing else it circles its quarry on a ring of its own ({@link SolomonRules#orbit}). When it has rested, and
 * its seal lets it (no more than three of the eight at work at once), it does one of twelve things: its beam, a deluge of bolts,
 * Baal's fireballs, lightning, giant hands, beams out of magic circles in the sky (one circle, or a pentagram, hexagram or
 * octagram of them) (all hanging where it is, its head turned on its quarry, its body writhing); burrowing
 * under its quarry to burst up through it, or ramming down at where it stood and away under the ground (both of which take it
 * where they go); or the blessing of Venus on itself and its kin about it. Between them it swoops on its quarry, through where it
 * is and away (no more than two of the eight at once).
 *
 * <p>It falls apart from the tail up when it dies, and tells its seal it has fallen.
 */
public final class SolomonWyrm extends GoeticDemon implements BeamCaster, SpitCaster, IEntityWithComplexSpawn {
    /** Its death: its body crumbling from the tail up, a burst of its light at its head when the last of it goes. */
    public static final BossDeath DEATH = new BossDeath(SolomonRules.WYRM_DEATH_TICKS, 20, 1.6F, new BossDeath.Rays(12, 8, SolomonRules.GOLD, SolomonRules.VIOLET),
            .2F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "shards", 3, 2, 30, SolomonRules.GOLD, SolomonRules.VIOLET, .2, "minecraft:entity.ender_dragon.hurt", 6, .5F),
            BossDeath.beat(48, BossDeath.Anchor.HEART, "nova", 7, 0, 26, SolomonRules.GOLD, 0xFFFFFF, .5, "minecraft:entity.generic.explode", 6, .7F)));

    private static final EntityDataAccessor<Byte> INDEX = SynchedEntityData.defineId(SolomonWyrm.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(SolomonWyrm.class, EntityDataSerializers.BYTE);

    private final WyrmChain chain;
    private final Link[] links;
    /** Whether its body has been laid out where its head is (on its first tick, or as a client is sent it). */
    private boolean laid;
    /** Its joints as it moves now (its chain, posed as it works), x y z for each in turn; and a tick ago, for a client to draw it between ticks. */
    private final double[] body = new double[SolomonRules.JOINTS * 3];
    private double[] previous = new double[0];
    @Nullable private UUID seal;
    /** Where something it is doing has told it to fly this tick, how fast, and how sharply it may turn; or nothing. */
    @Nullable private Vec3 goal;
    private double goalSpeed, goalTurn;
    private boolean goalEase;
    private SkillSet<SolomonWyrm> skills;
    private BeamSkill beam;
    private BurrowSkill burrow;
    private RamSkill ram;
    private VenusSkill venus;
    private int rest = SolomonRules.WYRM_REST;
    private boolean wasBuried;
    /** Where its head has gone, and where it swam through the earth: its body swims there too, rather than breaking its way. */
    private final WyrmWake wake = new WyrmWake(SolomonRules.LENGTH * WyrmWake.CURL + 4);
    /** Where it is in a swoop, how long it has been there, and how long until it may swoop again. */
    private Swoop swoop = Swoop.NONE;
    private int swoopTicks, swoopRest = SolomonRules.SWOOP.restLeast();
    /** On server and client alike: the skill to pose it by (the one it last began), when it began, and when it ended (-1 while it goes on). */
    private int skillSeen, endedAt = -1;
    private Wyrm posed = Wyrm.NONE;

    /** Where it is in a swoop: none, diving on its quarry, or climbing away after. */
    private enum Swoop { NONE, DIVE, CLIMB }

    public SolomonWyrm(EntityType<? extends SolomonWyrm> type, Level level) {
        super(type, level, BossEvent.BossBarColor.YELLOW);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 0;
        chain = new WyrmChain(SolomonRules.JOINTS, SolomonRules.SPACING, 0, 0, 0);
        links = new Link[chain.links()];
        for (int link = 0; link < links.length; link++) links[link] = new Link(this, link);
        // Its links take the ids after its own, on server and client alike.
        setId(ENTITY_COUNTER.getAndAdd(links.length + 1) + 1);
        arm(0);
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, SolomonRules.WYRM_HEALTH).add(Attributes.ARMOR, SolomonRules.ARMOR)
                .add(Attributes.FOLLOW_RANGE, SolomonRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, SolomonRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FLYING_SPEED, .3);
    }

    /** Its twelve skills, in the colours of its book. */
    private void arm(int index) {
        int colour = accent(index);
        beam = new BeamSkill(SolomonRules.BEAM, colour, SolomonRules.GOLD);
        burrow = new BurrowSkill(SolomonRules.BURROW, colour, SolomonRules.GOLD);
        ram = new RamSkill(SolomonRules.RAM, colour, SolomonRules.GOLD);
        venus = new VenusSkill(SolomonRules.VENUS, 0xF4A6C0, SolomonRules.GOLD);
        skills = SkillSet.<SolomonWyrm>of()
                .with("beam", beam)
                .with("deluge", new BarrageSkill(SolomonRules.DELUGE, colour, SolomonRules.GOLD))
                .with("fireballs", new FireballSkill(SolomonRules.FIREBALLS, 0xFF6A1A, SolomonRules.GOLD))
                .with("burrow", burrow)
                .with("ram", ram)
                .with("venus", venus)
                .with("storm", new StrikeSkill(SolomonRules.STORM, colour, SolomonRules.GOLD))
                .with("hands", new HandSkill(SolomonRules.HANDS, colour, SolomonRules.GOLD))
                .with("sky_beam", new SkyBeamSkill(SolomonRules.SKY_BEAM, colour, SolomonRules.GOLD))
                .with("pentagram", new SkyBeamSkill(SolomonRules.PENTAGRAM, colour, SolomonRules.GOLD))
                .with("hexagram", new SkyBeamSkill(SolomonRules.HEXAGRAM, colour, SolomonRules.GOLD))
                .with("octagram", new SkyBeamSkill(SolomonRules.OCTAGRAM, colour, SolomonRules.GOLD));
    }

    /** The light of a dragon of this place among the eight. */
    public static int accent(int index) { return SolomonRules.ACCENTS[Math.floorMod(index, SolomonRules.ACCENTS.length)]; }

    /** Sets it to serve its seal, as the dragon of this place among the eight, flying off this way. */
    void serve(Solomon seal, int index, Vec3 velocity) {
        this.seal = seal.getUUID();
        entityData.set(INDEX, (byte) index);
        arm(index);
        setDeltaMovement(velocity);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(INDEX, (byte) 0);
        builder.define(SKILL, (byte) 0);
    }

    /** It turns on what hurts it, and hunts what is not of the corruption, players first, as far below it as about it. */
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        huntTheUncorruptedFromOnHigh(2);
    }

    /** Which of the eight it is: the book of the Key it was made of, I to VIII, counting from 0. */
    public int index() { return entityData.get(INDEX); }
    public int accent() { return accent(index()); }
    public WyrmChain chain() { return chain; }

    /** Its seal, if it is about. */
    @Nullable public Solomon seal() {
        return seal != null && level() instanceof ServerLevel level && level.getEntity(seal) instanceof Solomon found && found.isAlive() ? found : null;
    }

    // ---- its body ----

    @Override public void setId(int id) {
        super.setId(id);
        if (links != null) for (int link = 0; link < links.length; link++) links[link].setId(id + link + 1);
    }
    @Override public boolean isMultipartEntity() { return true; }
    @Override public PartEntity<?>[] getParts() { return links; }

    /** Its head can be struck at while it is not buried. */
    @Override protected boolean exposed() { return !buried(position().add(0, getBbHeight() / 2, 0)); }

    /** Which way its head faces. */
    public Vec3 forward() { return Vec3.directionFromRotation(getXRot(), getYRot()); }
    /** The middle of its head. */
    public Vec3 headCentre() { return position().add(0, getBbHeight() / 2, 0); }
    /** Its mouth: where its beam, its bolts and its fire leave it. */
    public Vec3 mouth() { return headCentre().add(forward().scale(SolomonRules.HEAD_LENGTH / 2)); }
    /** Where its body meets its head. */
    public Vec3 neck() { return headCentre().subtract(forward().scale(SolomonRules.HEAD_LENGTH / 2)); }

    @Override public Vec3 beamFrom() { return mouth(); }
    @Override public Vec3 spitFrom(int index) { return mouth(); }

    /** Its body follows its head, on server and client alike, every tick, and moves as it works; each link's hitbox with it. */
    private void follow() {
        Vec3 neck = neck();
        if (!laid) {
            chain.reset(neck.x, neck.y, neck.z);
            laid = true;
        }
        if (level().isClientSide) previous = body.clone();
        double moved = Math.sqrt(Math.pow(neck.x - chain.x(0), 2) + Math.pow(neck.y - chain.y(0), 2) + Math.pow(neck.z - chain.z(0), 2));
        chain.follow(neck.x, neck.y, neck.z);
        if (!level().isClientSide) wake.go(moved, tunnelling());
        pose();
        for (Link link : links) link.place();
    }

    /** Lays its body as it moves now ({@link WyrmPose}), from where its chain lies. */
    private void pose() { WyrmPose.pose(chain, posed, tickCount - skillSeen, endedAt < 0 ? -1 : tickCount - endedAt, body); }

    /**
     * A joint of its body as it moves, {x, y, z}: on a client, between where it was a tick ago and where it is now; on a
     * server, where it is.
     */
    public double[] joint(int joint, float partial) {
        double x = body[joint * 3], y = body[joint * 3 + 1], z = body[joint * 3 + 2];
        if (previous.length != body.length) return new double[]{x, y, z};
        return new double[]{previous[joint * 3] + (x - previous[joint * 3]) * partial, previous[joint * 3 + 1] + (y - previous[joint * 3 + 1]) * partial,
                previous[joint * 3 + 2] + (z - previous[joint * 3 + 2]) * partial};
    }

    /** The middle of a link of its body as it moves now, {x, y, z}. */
    public double[] link(int link) {
        return new double[]{(body[link * 3] + body[link * 3 + 3]) / 2, (body[link * 3 + 1] + body[link * 3 + 4]) / 2, (body[link * 3 + 2] + body[link * 3 + 5]) / 2};
    }

    /** Whether a place lies inside the ground (in a block one cannot pass through). */
    boolean buried(Vec3 at) {
        BlockPos pos = BlockPos.containing(at);
        BlockState state = level().getBlockState(pos);
        return !state.getCollisionShape(level(), pos).isEmpty();
    }

    /** Whether its head is under the ground. */
    public boolean burrowed() { return buried(headCentre()); }

    @Override public void tick() {
        noPhysics = true;
        super.tick();
        if (isAlive() || deathTime < SolomonRules.WYRM_DEATH_TICKS) follow();
        if (level() instanceof ServerLevel level) {
            breakSurface(level);
            if (isAlive()) {
                smash(level);
                touch(level);
            }
        }
    }

    /** Whether it is going through the ground on purpose, burrowing or ramming (package-private, for the tests). */
    boolean tunnelling() {
        Wyrm now = skill();
        return now == Wyrm.BURROW && burrow.tunnelling() || now == Wyrm.RAM && ram.tunnelling();
    }

    /**
     * It breaks what it flies through, as the Ender Dragon does: every block its head and the links of its body pass through
     * is gone, dropping nothing; but not one in {@link ModTags#SOLOMON_WYRM_IMMUNE} (the Ender Dragon's own, and the altar's
     * offering and pillars), nor one it passes through as through air ({@code minecraft:dragon_transparent}), nor any where
     * creatures may not break blocks (the mobGriefing rule). Not while it burrows, when it swims through the earth; nor
     * any link of its body where its head swam ({@link WyrmWake}), however long it takes to follow its head back out of the
     * ground. How many it broke (package-private, for the tests).
     */
    int smash(ServerLevel level) {
        if (tunnelling() || !EventHooks.canEntityGrief(level, this)) return 0;
        // Its head, and the way it goes this tick, so that however fast it goes it leaves nothing standing between one tick and the next.
        int broken = smash(level, getBoundingBox().expandTowards(getDeltaMovement()));
        for (Link link : links) if (!wake.swims(link.link(), SolomonRules.SPACING)) broken += smash(level, link.getBoundingBox());
        return broken;
    }

    private int smash(ServerLevel level, AABB box) {
        int minX = Mth.floor(box.minX), minY = Mth.floor(box.minY), minZ = Mth.floor(box.minZ);
        int maxX = Mth.floor(box.maxX), maxY = Mth.floor(box.maxY), maxZ = Mth.floor(box.maxZ);
        if (!level.hasChunksAt(minX, minZ, maxX, maxZ)) return 0;
        int broken = 0;
        BlockPos last = null;
        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || state.is(BlockTags.DRAGON_TRANSPARENT) || state.is(ModTags.SOLOMON_WYRM_IMMUNE) || !CommonHooks.canEntityDestroy(level, pos, this)) continue;
            if (level.removeBlock(pos, false)) {
                broken++;
                last = pos.immutable();
            }
        }
        if (last != null) level.levelEvent(2008, last, 0);
        return broken;
    }

    /**
     * Whatever its head and body pass through is hurt (his hurt, which no robe turns aside), again as soon as it can be hurt
     * again: by its head {@link SolomonRules#HEAD_CONTACT}, thrown on the way it goes; by any link of its body
     * {@link SolomonRules#BODY_CONTACT}, thrown off to the side. Nothing of its own side, nor a player who only builds or
     * watches. What it hurt (package-private, for the tests).
     */
    List<LivingEntity> touch(ServerLevel level) {
        AABB head = getBoundingBox(), reach = head;
        for (Link link : links) reach = reach.minmax(link.getBoundingBox());
        List<LivingEntity> near = level.getEntitiesOfClass(LivingEntity.class, reach, entity -> entity != this && entity.isAlive() && canAttack(entity));
        List<LivingEntity> hurt = new ArrayList<>();
        if (near.isEmpty()) return hurt;
        DamageSource source = ModDamageTypes.corruption(level, this);
        for (LivingEntity touched : near) {
            AABB box = touched.getBoundingBox();
            Vec3 thrown = null;
            double damage = 0;
            if (box.intersects(head)) {
                damage = SolomonRules.HEAD_CONTACT;
                thrown = forward().scale(SolomonRules.HEAD_THROW);
            } else for (Link link : links) {
                AABB part = link.getBoundingBox();
                if (!box.intersects(part)) continue;
                Vec3 away = box.getCenter().subtract(part.getCenter()).multiply(1, 0, 1);
                if (away.lengthSqr() < 1e-6) away = new Vec3(-forward().z, 0, forward().x);
                damage = SolomonRules.BODY_CONTACT;
                thrown = away.lengthSqr() < 1e-6 ? Vec3.ZERO : away.normalize().scale(SolomonRules.BODY_THROW);
                break;
            }
            if (thrown == null || !touched.hurt(source, (float) damage)) continue;
            touched.push(thrown.x, Math.max(thrown.y, 0) + .25, thrown.z);
            touched.hurtMarked = true;
            hurt.add(touched);
        }
        return hurt;
    }

    /** Where its head breaks into or out of the ground, earth flies and the ground rumbles. */
    private void breakSurface(ServerLevel level) {
        boolean buried = burrowed();
        if (buried != wasBuried) {
            Vec3 at = headCentre();
            BlockState earth = level.getBlockState(BlockPos.containing(at).below(buried ? 0 : 1));
            if (!earth.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, earth), at.x, at.y, at.z, 60, 1.6, .8, 1.6, .25);
            SkillShows.sound(level, at, buried ? SoundEvents.ROOTED_DIRT_BREAK : SoundEvents.GENERIC_EXPLODE.value(), 3, .5F);
            SkillShows.show(level, SkillShows.ground(level, at), "fissure", 3, .5, 30, accent(), SolomonRules.GOLD, .3, getYRot(), true);
        }
        wasBuried = buried;
    }

    /** It flies through anything, under the ground as through the air, and only as it is told. */
    @Override public void travel(Vec3 input) { if (isControlledByLocalInstance()) move(MoverType.SELF, getDeltaMovement()); }
    @Override public boolean isInWall() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override protected void doPush(Entity entity) {}

    // ---- its mind ----

    /** Whether it is at work at something, which counts against its seal's three. */
    public boolean casting() { return skills.busy(); }

    /** Something it is doing tells it to fly towards here this tick, this fast, turning at most this many degrees, slowing as it comes near. */
    public void steer(Vec3 to, double speed, double turn) { steer(to, speed, turn, true); }

    /** As {@link #steer(Vec3, double, double)}; keeping its speed right through, unless {@code ease}. */
    public void steer(Vec3 to, double speed, double turn, boolean ease) {
        goal = to;
        goalSpeed = speed;
        goalTurn = turn;
        goalEase = ease;
    }

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target.level() != level())) target = null;
        goal = null;
        if (rest > 0) rest--;
        if (swoopRest > 0) swoopRest--;
        Solomon seal = seal();
        if (!swooping() && !skills.busy() && target != null && rest == 0 && (seal == null || seal.mayCast())) choose(target);
        skills.tick(this, target, cooldown -> cooldown);
        if (!skills.busy() && skill() != Wyrm.NONE && !swooping()) {
            setSkill(Wyrm.NONE);
            rest = SolomonRules.WYRM_REST + random.nextInt(30);
        }
        // Between the things it works it swoops on its quarry, no more than two of his dragons at once.
        if (!swooping() && !skills.busy() && target != null && swoopRest == 0 && SolomonRules.SWOOP.inRange(distanceTo(target))
                && (seal == null || seal.maySwoop())) beginSwoop();
        if (swooping()) swoop(target);
        if (goal != null) fly(goal, goalSpeed, goalTurn, goalEase);
        else if (skills.busy()) hold(target);
        else cruise(target, seal);
    }

    /** Whether it is swooping on its quarry. */
    public boolean swooping() { return swoop != Swoop.NONE; }

    /** Sets off on a swoop (package-private, for the tests). */
    void beginSwoop() {
        swoop = Swoop.DIVE;
        swoopTicks = 0;
        setSkill(Wyrm.SWOOP);
        SkillShows.sound(this, SoundEvents.ENDER_DRAGON_GROWL, 4, .7F);
    }

    /** A tick of its swoop ({@link SolomonRules#SWOOP}): diving at its quarry's middle, through it, and up and away. */
    private void swoop(@Nullable LivingEntity target) {
        SwoopRules rules = SolomonRules.SWOOP;
        swoopTicks++;
        if (swoop == Swoop.DIVE) {
            Vec3 aim = target == null ? null : target.getBoundingBox().getCenter(), to = aim == null ? null : aim.subtract(headCentre());
            if (to == null || rules.done(to.length(), to.dot(forward())) || swoopTicks >= rules.longest()) {
                swoop = Swoop.CLIMB;
                swoopTicks = 0;
            } else steer(aim, rules.speed(), rules.turn(), false);
        }
        if (swoop == Swoop.CLIMB) {
            Vec3 on = forward().multiply(1, 0, 1);
            if (on.lengthSqr() < 1e-4) on = Vec3.directionFromRotation(0, getYRot());
            steer(headCentre().add(on.normalize().scale(rules.speed() * 10)).add(0, rules.rise(), 0), rules.speed(), rules.turn() * 1.5, false);
            if (swoopTicks >= rules.climb()) {
                swoop = Swoop.NONE;
                swoopRest = rules.rest(random.nextDouble());
                setSkill(Wyrm.NONE);
            }
        }
    }

    private void choose(LivingEntity target) {
        Set<Wyrm> ready = EnumSet.noneOf(Wyrm.class);
        for (Wyrm skill : SolomonRules.WYRM_SKILLS) if (skills.usable(index(skill), this, target)) ready.add(skill);
        if (ready.isEmpty()) return;
        double lowest = getHealth() / getMaxHealth();
        for (SolomonWyrm kin : level().getEntitiesOfClass(SolomonWyrm.class, getBoundingBox().inflate(SolomonRules.VENUS.radius()), SolomonWyrm::isAlive))
            lowest = Math.min(lowest, kin.getHealth() / kin.getMaxHealth());
        SolomonRules.WyrmSituation now = new SolomonRules.WyrmSituation(distanceTo(target), hasLineOfSight(target), lowest, getY() - target.getY(), ready);
        Wyrm next = SolomonRules.chooseWyrm(now, random.nextDouble());
        if (next != Wyrm.NONE) begin(next, target);
    }

    /** Begins one of its skills on its quarry, if it has come round; whether it began (package-private, for the tests). */
    boolean begin(Wyrm skill, LivingEntity target) {
        if (!skills.begin(index(skill), this, target)) return false;
        // Whatever it works, it works it from where it is: a swoop under way ends.
        if (swooping()) {
            swoop = Swoop.NONE;
            swoopRest = SolomonRules.SWOOP.rest(random.nextDouble());
        }
        setSkill(skill);
        return true;
    }

    private static int index(Wyrm skill) { return SolomonRules.WYRM_SKILLS.indexOf(skill); }

    private void setSkill(Wyrm skill) { entityData.set(SKILL, (byte) skill.ordinal()); }

    /** It circles its quarry on its own ring, never too near the ground; with none, its seal; and never strays far from its seal. */
    private void cruise(@Nullable LivingEntity target, @Nullable Solomon seal) {
        Vec3 centre = target != null ? target.position() : seal != null ? seal.home() : position();
        if (seal != null && centre.distanceTo(seal.home()) > SolomonRules.LEASH) centre = seal.home();
        double[] ring = SolomonRules.orbit(index(), tickCount);
        Vec3 to = centre.add(ring[0], ring[1], ring[2]);
        int ground = level().getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(to.x), Mth.floor(to.z));
        if (to.y < ground + SolomonRules.CLEARANCE) to = new Vec3(to.x, ground + SolomonRules.CLEARANCE, to.z);
        fly(to, SolomonRules.CRUISE, SolomonRules.TURN, true);
    }

    /** It hangs still where it is, its head on its quarry, at work. */
    private void hold(@Nullable LivingEntity target) {
        setDeltaMovement(getDeltaMovement().scale(.85));
        if (target != null) face(target.getEyePosition());
    }

    /** Turns its head on a place. */
    public void face(Vec3 at) {
        Vec3 to = at.subtract(headCentre());
        if (to.lengthSqr() < 1e-6) return;
        setRotation((float) (Mth.atan2(-to.x, to.z) * Mth.RAD_TO_DEG), (float) Mth.clamp(-Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG, -80, 80));
    }

    private void setRotation(float yaw, float pitch) {
        setYRot(yaw);
        setXRot(pitch);
        yBodyRot = yHeadRot = yaw;
    }

    /**
     * Flies towards a place: turning its way at most {@code turn} degrees a tick, speeding up or slowing down towards
     * {@code speed} (and slowing as it comes near, unless it is to keep its speed), its head the way it goes.
     */
    private void fly(Vec3 to, double speed, double turn, boolean ease) {
        Vec3 velocity = getDeltaMovement(), want = to.subtract(position());
        double distance = want.length();
        if (distance < 1e-3) {
            setDeltaMovement(velocity.scale(.7));
            return;
        }
        // Hanging still, or nearly, it sets off the way it faces (what it last did may have left it drifting some other way).
        Vec3 heading = velocity.length() < .15 ? forward() : velocity.normalize();
        Vec3 turned = turnTowards(heading, want.scale(1 / distance), Math.toRadians(turn));
        double now = velocity.length(), target = ease ? Math.min(speed, Math.max(.2, distance * .15)) : speed;
        double next = now + Mth.clamp(target - now, -.1, .1);
        setDeltaMovement(turned.scale(next));
        if (next > .02) setRotation((float) (Mth.atan2(-turned.x, turned.z) * Mth.RAD_TO_DEG),
                (float) Mth.clamp(-Mth.atan2(turned.y, turned.horizontalDistance()) * Mth.RAD_TO_DEG, -80, 80));
    }

    /** A way turned towards another by at most this many radians (along the great circle between them). */
    static Vec3 turnTowards(Vec3 from, Vec3 to, double most) {
        double cos = Mth.clamp(from.dot(to), -1, 1), angle = Math.acos(cos);
        if (angle <= most) return to;
        if (angle > Math.PI - 1e-4) {
            // Straight behind: turn round by way of the side.
            Vec3 side = new Vec3(-from.z, 0, from.x);
            if (side.lengthSqr() < 1e-6) side = new Vec3(1, 0, 0);
            to = side.normalize();
            cos = Mth.clamp(from.dot(to), -1, 1);
            angle = Math.acos(cos);
        }
        double share = most / angle, sin = Math.sin(angle);
        return from.scale(Math.sin((1 - share) * angle) / sin).add(to.scale(Math.sin(share * angle) / sin)).normalize();
    }

    // ---- what the client sees ----

    public Wyrm skill() {
        int index = entityData.get(SKILL);
        return index >= 0 && index < Wyrm.values().length ? Wyrm.values()[index] : Wyrm.NONE;
    }

    /** The skill to pose it by, the one it last began; ticks since. */
    public Wyrm posedSkill() { return posed; }
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key)) {
            if (skill() != Wyrm.NONE) {
                posed = skill();
                skillSeen = tickCount;
                endedAt = -1;
            } else if (posed != Wyrm.NONE && endedAt < 0) endedAt = tickCount;
        }
        if (INDEX.equals(key) && level().isClientSide) arm(index());
    }

    /** How many of its links have crumbled away from the tail, dying, at this moment (all of them once it is gone). */
    public float crumbled(float partial) {
        if (deathTime <= 0) return 0;
        return Math.min(links.length, (deathTime + partial) / (SolomonRules.WYRM_DEATH_TICKS - 8F) * links.length);
    }

    // ---- its death ----

    @Override public void die(DamageSource source) {
        skills.cease(this);
        swoop = Swoop.NONE;
        setSkill(Wyrm.NONE);
        if (!level().isClientSide && !isRemoved() && !dead && seal != null && level() instanceof ServerLevel level) {
            SolomonLedger.of(level).fell(seal, index());
            Solomon found = seal();
            if (found != null) found.fell(index(), source);
        }
        super.die(source);
    }

    /** It crumbles from the tail up: each link bursting in its light and its dust as it goes. */
    @Override protected void tickDeath() {
        int before = (int) crumbled(0);
        super.tickDeath();
        if (!(level() instanceof ServerLevel level)) return;
        int after = Math.min(links.length, (int) crumbled(0));
        for (int gone = before; gone < after; gone++) {
            int link = links.length - 1 - gone;
            double[] at = link(link);
            level.sendParticles(ParticleTypes.EXPLOSION, at[0], at[1], at[2], 1, 0, 0, 0, 0);
            level.sendParticles(new net.minecraft.core.particles.DustParticleOptions(
                    new org.joml.Vector3f((accent() >> 16 & 255) / 255F, (accent() >> 8 & 255) / 255F, (accent() & 255) / 255F), 2.5F),
                    at[0], at[1], at[2], 12, 1, 1, 1, .1);
            if (gone % 3 == 0) SkillShows.sound(level, new Vec3(at[0], at[1], at[2]), SoundEvents.ENDER_DRAGON_HURT, 3, .5F + .02F * gone);
        }
    }

    /** It hurts itself on nothing it flies through. */
    @Override public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(net.minecraft.tags.DamageTypeTags.IS_FALL) || source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)
                || source.is(net.minecraft.world.damagesource.DamageTypes.DROWN) || source.is(net.minecraft.world.damagesource.DamageTypes.FLY_INTO_WALL);
    }

    /** Only its seal shows a bar; its own share is shown there. */
    @Override protected boolean barShown() { return false; }
    @Override public BossDeath death() { return DEATH; }
    @Override public int emblem() { return 0; }
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() / 2, 0); }

    /** Its body reaches far from its head: it is drawn wherever any of it could be seen. */
    @Override public AABB getBoundingBoxForCulling() {
        AABB box = getBoundingBox();
        for (int joint = 0; joint < chain.joints(); joint += 3) {
            double x = body[joint * 3], y = body[joint * 3 + 1], z = body[joint * 3 + 2];
            box = box.minmax(new AABB(x, y, z, x, y, z));
        }
        return box.inflate(SolomonRules.LINK_WIDTH + 2);
    }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 320 * 320; }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENDER_DRAGON_GROWL; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENDER_DRAGON_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENDER_DRAGON_DEATH; }
    @Override public float getVoicePitch() { return .55F + random.nextFloat() * .1F; }
    @Override protected float getSoundVolume() { return 4; }
    @Override public int getAmbientSoundInterval() { return 240; }

    // ---- kept and sent ----

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(index());
        for (double value : chain.save()) buffer.writeDouble(value);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        int index = buffer.readByte();
        double[] joints = new double[chain.joints() * 3];
        for (int value = 0; value < joints.length; value++) joints[value] = buffer.readDouble();
        chain.load(joints);
        laid = true;
        arm(index);
        pose();
        for (Link link : links) link.place();
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Dragon", (byte) index());
        if (seal != null) tag.putUUID("Seal", seal);
        ListTag joints = new ListTag();
        for (double value : chain.save()) joints.add(DoubleTag.valueOf(value));
        tag.put("Body", joints);
        tag.put("Skills", skills.save());
        tag.putInt("Rest", rest);
        tag.putInt("SwoopRest", swoopRest);
        ListTag way = new ListTag();
        for (double value : wake.save()) way.add(DoubleTag.valueOf(value));
        tag.put("Wake", way);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int index = tag.getByte("Dragon");
        entityData.set(INDEX, (byte) index);
        arm(index);
        seal = tag.hasUUID("Seal") ? tag.getUUID("Seal") : null;
        ListTag joints = tag.getList("Body", Tag.TAG_DOUBLE);
        if (joints.size() == chain.joints() * 3) {
            double[] values = new double[joints.size()];
            for (int value = 0; value < values.length; value++) values[value] = joints.getDouble(value);
            chain.load(values);
            laid = true;
        }
        skills.load(tag.getCompound("Skills"));
        rest = tag.getInt("Rest");
        swoopRest = tag.getInt("SwoopRest");
        ListTag way = tag.getList("Wake", Tag.TAG_DOUBLE);
        double[] wakeValues = new double[way.size()];
        for (int value = 0; value < wakeValues.length; value++) wakeValues[value] = way.getDouble(value);
        wake.load(wakeValues);
        pose();
    }

    /** Its skills, as they stand. */
    public SkillSet<SolomonWyrm> skills() { return skills; }
    public BeamSkill beam() { return beam; }
    public BurrowSkill burrow() { return burrow; }
    public RamSkill ram() { return ram; }
    public VenusSkill venus() { return venus; }

    /**
     * One link of its body: a hitbox that has no life of its own, a blow on it a blow on the dragon, as the dragon takes it;
     * narrower towards the tail, and not to be struck at while it lies buried or has crumbled away.
     */
    public static final class Link extends PartEntity<SolomonWyrm> {
        private final int link;
        private final EntityDimensions size;
        private boolean buried;

        Link(SolomonWyrm dragon, int link) {
            super(dragon);
            this.link = link;
            float width = (float) (SolomonRules.LINK_WIDTH * dragon.chain.taper(link, SolomonRules.TAPER));
            size = EntityDimensions.scalable(width, width);
            refreshDimensions();
        }

        public int link() { return link; }

        /** Follows its place on the body, as the body moves. */
        void place() {
            double[] at = getParent().link(link);
            double x = getX(), y = getY(), z = getZ();
            setPos(at[0], at[1] - size.height() / 2, at[2]);
            xo = x; yo = y; zo = z; xOld = x; yOld = y; zOld = z;
            buried = getParent().buried(new Vec3(at[0], at[1], at[2]));
        }

        @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
        @Override protected void readAdditionalSaveData(CompoundTag tag) {}
        @Override protected void addAdditionalSaveData(CompoundTag tag) {}
        @Override public boolean shouldBeSaved() { return false; }
        @Override public boolean isPickable() {
            SolomonWyrm dragon = getParent();
            return dragon.isAlive() && !buried;
        }
        @Override public boolean is(Entity entity) { return this == entity || getParent() == entity; }
        @Override public EntityDimensions getDimensions(Pose pose) { return size == null ? super.getDimensions(pose) : size; }
        @Override public boolean hurt(DamageSource source, float amount) { return !isInvulnerableTo(source) && getParent().hurt(source, amount); }
    }
}
