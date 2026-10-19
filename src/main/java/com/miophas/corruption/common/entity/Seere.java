package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.BossDeath;
import com.miophas.corruption.api.demon.GoeticDemon;
import com.miophas.corruption.api.demon.skill.BeamCaster;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BeamVolleySkill;
import com.miophas.corruption.common.entity.skill.Debris;
import com.miophas.corruption.common.entity.skill.DiveSkill;
import com.miophas.corruption.common.entity.skill.MeleeSkill;
import com.miophas.corruption.common.entity.skill.PhaseDashSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpikeFieldSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.BodyPart;
import com.miophas.corruption.core.demon.SeereRules.Mode;
import com.miophas.corruption.core.demon.SeereRules.Skill;
import com.miophas.corruption.core.demon.SeereRules;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Seere, the seventieth spirit of the Goetia: a prince on a winged horse, who goes and comes in a moment and carries
 * anything anywhere across the earth in the blink of an eye; here the winged horse himself, brought forth at the altar
 * (data/corruption/recipe/altar/seere.json). What he does and when is {@link SeereRules}; his blows are the modules
 * every boss may share (common.entity.skill), and this carries them out, and his going from one way to another.
 *
 * <p>He goes three ways ({@link Mode}): on the ground at a gallop faster than any horse; in the sky on his wings,
 * circling his quarry high over the ground; and under the earth, through it as through air, unseen but for the ground
 * trembling over him, to rise up out of it beside his quarry, throwing it up and the ground about with it. Going from
 * one way to another (taking off, landing, going under, rising) takes him a moment of its own, a shift, posed as a
 * skill is. His dive ends on the ground, where it struck. As every {@link GoeticDemon}, only the arcane can hurt him;
 * under the earth nothing reaches him.
 */
public final class Seere extends GoeticDemon implements BeamCaster {
    /** His colours: a night indigo, and a pale silver where it is brightest. */
    public static final int INDIGO = 0x5A6CFF, SILVER = 0xDDE6FF;
    /** His beams': indigo round a white-hot core. */
    public static final int BEAM = 0x7484FF, BEAM_CORE = 0xF2F5FF;
    /** His body's hitboxes: his forequarters, his hindquarters, his head on its neck. */
    public static final List<BodyPart> BODY = List.of(
            new BodyPart("chest", .8, 0, .9, 1.9, 1.9),
            new BodyPart("rump", -1.2, 0, .9, 1.8, 1.8),
            new BodyPart("head", 2.0, 0, 2.3, 1.1, 1.2));

    /**
     * His death: his feathers scattering in a storm, a ring of force from where he falls, the souls he carried off
     * rising, a rift opening and closing once more as he goes nowhere; then he bursts in his indigo light, a ring of it
     * racing out and a pillar of it standing where he fell.
     */
    public static final BossDeath DEATH = new BossDeath(SeereRules.DEATH_TICKS, 50, 1.4F, new BossDeath.Rays(18, 7, INDIGO, SILVER), .15F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "feathers", 4, 2, 60, INDIGO, SILVER, .2, "minecraft:entity.ender_dragon.flap", 4, .5F),
            BossDeath.beat(4, BossDeath.Anchor.FEET, "shockwave", 6, 1.5, 24, INDIGO, SILVER, .3, "minecraft:entity.horse.death", 4, .5F),
            BossDeath.beat(20, BossDeath.Anchor.FEET, "souls", 2.4, 6, 46, INDIGO, SILVER, 0, "minecraft:particle.soul_escape", 4, .7F),
            BossDeath.beat(36, BossDeath.Anchor.FEET, "rift", 2.2, 2, 30, INDIGO, SILVER, 0, "minecraft:entity.enderman.teleport", 4, .5F),
            BossDeath.beat(66, BossDeath.Anchor.HEART, "nova", 10, 0, 26, INDIGO, 0xFFFFFF, .6, "minecraft:entity.generic.explode", 4, .8F),
            BossDeath.beat(66, BossDeath.Anchor.FEET, "shockwave", 10, 2, 26, INDIGO, SILVER, 0, "", 0, 1),
            BossDeath.beat(67, BossDeath.Anchor.FEET, "light_pillar", 1.4, 30, 44, INDIGO, BEAM_CORE, 0, "minecraft:block.beacon.deactivate", 4, .6F)));

    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(Seere.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> MODE = SynchedEntityData.defineId(Seere.class, EntityDataSerializers.BYTE);

    private final DiveSkill dive = new DiveSkill(SeereRules.DIVE, INDIGO, SILVER);
    /** His blows, in the order of SeereRules.BLOWS. */
    private final SkillSet<Seere> skills = SkillSet.<Seere>of()
            .with("hooves", new MeleeSkill(SeereRules.HOOVES))
            .with("blinks", new PhaseDashSkill(SeereRules.BLINKS, INDIGO, SILVER))
            .with("spikes", new SpikeFieldSkill(SeereRules.SPIKES, INDIGO, SILVER))
            .with("dive", dive)
            .with("beams", new BeamVolleySkill(SeereRules.BEAMS, boss -> SeereRules.beams(boss.getHealth() / boss.getMaxHealth()), BEAM, BEAM_CORE));
    private int rest = 30, modeTicks, unseenTicks, shiftElapsed, wander;
    /**
     * The quarry he went under the earth after. He cannot see it from there, and would forget it as any creature forgets
     * what it has not seen a while; he keeps it himself until he is up again.
     */
    @Nullable private LivingEntity burrowedAfter;
    private double hoverAt = SeereRules.SKY_LOW + 1;
    /** Where a landing comes down, and a rising comes up; and how deep a rising starts. */
    private Vec3 shiftTo = Vec3.ZERO;
    private double shiftFrom;
    /** On a client: the tick his skill and his way last changed, and the skill to pose him by (it settles after it is over). */
    private int skillSeen, modeSeen;
    private Skill posed = Skill.NONE;

    public Seere(EntityType<? extends Seere> type, Level level) {
        super(type, level, BossEvent.BossBarColor.BLUE, BODY);
        xpReward = 100;
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, SeereRules.MAX_HEALTH).add(Attributes.ARMOR, SeereRules.ARMOR)
                .add(Attributes.FOLLOW_RANGE, SeereRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, SeereRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, SeereRules.SPEED).add(Attributes.FLYING_SPEED, SeereRules.FLY_SPEED)
                .add(Attributes.STEP_HEIGHT, SeereRules.STEP_HEIGHT).add(Attributes.ATTACK_DAMAGE, SeereRules.HOOVES.damage());
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKILL, (byte) 0);
        builder.define(MODE, (byte) 0);
    }

    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        huntTheUncorrupted(2);
    }

    // ---- his mind ----

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = getTarget();
        if (target == null && (mode() == Mode.UNDERGROUND || skill() == Skill.BURROW)) target = burrowedAfter;
        if (target != null && (!target.isAlive() || target.level() != level())) target = null;
        modeTicks++;
        unseenTicks = target != null && hasLineOfSight(target) ? 0 : unseenTicks + 1;
        if (skill().shift()) {
            shiftTick(target);
            return;
        }
        if (mode() == Mode.UNDERGROUND) {
            burrowOn(target);
            return;
        }
        if (rest > 0) rest--;
        if (!skills.busy() && target != null && rest == 0) choose(target);
        if (skill().shift()) return;
        skills.tick(this, target, cooldown -> cooldown);
        if (!skills.busy() && skill() != Skill.NONE) {
            Skill done = skill();
            setSkill(Skill.NONE);
            rest = mode() == Mode.SKY ? SeereRules.SKY_REST : SeereRules.GROUND_REST;
            // His dive ends on the ground, where it struck.
            if (done == Skill.DIVE) enter(Mode.GROUND);
        }
        if (skills.busy()) return;
        if (mode() == Mode.SKY) fly(target);
        else gallop(target);
    }

    private void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill blow : SeereRules.BLOWS) if (skills.usable(index(blow), this, target)) ready.add(blow);
        SeereRules.Situation now = new SeereRules.Situation(mode(), distanceTo(target), target.getY() - getY(), hasLineOfSight(target),
                Mth.wrapDegrees(Mth.atan2(target.getZ() - getZ(), target.getX() - getX()) * Mth.RAD_TO_DEG - 90 - getYRot()), modeTicks, unseenTicks, ready);
        Skill next = SeereRules.choose(now, random.nextDouble());
        if (next.shift()) beginShift(next, target);
        else if (next != Skill.NONE) begin(next, target);
    }

    /** Begins one of his blows on his quarry, if it has come round; whether it began (package-private, for the tests to begin one by hand). */
    boolean begin(Skill blow, LivingEntity target) {
        if (!skills.begin(index(blow), this, target)) return false;
        setSkill(blow);
        return true;
    }

    private static int index(Skill blow) { return SeereRules.BLOWS.indexOf(blow); }

    private void setSkill(Skill skill) { entityData.set(SKILL, (byte) skill.ordinal()); }

    /** On the ground: after his quarry at a gallop, up to his hooves' reach of it. */
    private void gallop(@Nullable LivingEntity target) {
        if (target == null) return;
        if (distanceTo(target) > SeereRules.HOOVES.reach() * .7) getNavigation().moveTo(target, 1);
        else getNavigation().stop();
        getLookControl().setLookAt(target, 40, 40);
    }

    /**
     * In the sky: round his quarry at the distance he keeps, high over the ground (and over it), circling it fast and now
     * and then flying higher or lower; with none, hanging where he is. He leans into his flight.
     */
    private void fly(@Nullable LivingEntity target) {
        Vec3 want;
        if (target == null) want = new Vec3(getX(), groundUnder(position()) + hoverAt, getZ());
        else {
            Vec3 from = target.position(), off = position().subtract(from).multiply(1, 0, 1);
            if (off.lengthSqr() < 1e-4) off = new Vec3(1, 0, 0);
            if (--wander <= 0) {
                wander = 50 + random.nextInt(60);
                hoverAt = Mth.nextDouble(random, SeereRules.SKY_LOW, SeereRules.SKY_HIGH);
            }
            Vec3 at = from.add(off.normalize().yRot((float) Math.toRadians(2.2 * (wander % 120 < 60 ? 1 : -1))).scale(SeereRules.SKY_KEEP));
            want = new Vec3(at.x, Math.max(groundUnder(at) + hoverAt, target.getY() + SeereRules.SKY_LOW * .6), at.z);
            turnTo(target.position());
        }
        Vec3 to = want.subtract(position());
        double speed = getAttributeValue(Attributes.FLYING_SPEED);
        Vec3 push = to.lengthSqr() > 1e-4 ? to.normalize().scale(Math.min(.12, to.length() * .08)) : Vec3.ZERO;
        Vec3 moving = getDeltaMovement().add(push);
        if (moving.length() > speed) moving = moving.normalize().scale(speed);
        setDeltaMovement(moving);
        setXRot((float) Mth.clamp(-Mth.atan2(moving.y, Math.max(.05, moving.horizontalDistance())) * Mth.RAD_TO_DEG, -25, 25));
    }

    /** Turns him, body and head, to face a place along the ground. */
    private void turnTo(Vec3 at) {
        Vec3 to = at.subtract(position());
        if (to.horizontalDistanceSqr() < 1e-6) return;
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90;
        setYRot(Mth.approachDegrees(getYRot(), yaw, 18));
        yBodyRot = getYRot();
        yHeadRot = getYRot();
    }

    /** The top of the ground under a place: the highest block there, or under a roof the floor under him. */
    private double groundUnder(Vec3 at) {
        int x = Mth.floor(at.x), z = Mth.floor(at.z);
        if (!level().hasChunk(x >> 4, z >> 4)) return at.y - hoverAt;
        double top = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return top > at.y + 2 ? at.y - hoverAt : top;
    }

    /** The surface over a place under the earth: the highest block there. */
    private double surfaceAt(double x, double z) {
        int bx = Mth.floor(x), bz = Mth.floor(z);
        if (!level().hasChunk(bx >> 4, bz >> 4)) return getY() + SeereRules.DEPTH;
        return level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
    }

    // ---- his going from one way to another ----

    /** Begins going from one way to another (package-private, for the tests to begin one by hand). */
    void beginShift(Skill shift, @Nullable LivingEntity target) {
        setSkill(shift);
        shiftElapsed = 0;
        getNavigation().stop();
        if (!(level() instanceof ServerLevel level)) return;
        switch (shift) {
            case TAKE_OFF -> {
                setNoGravity(true);
                SkillShows.sound(this, SoundEvents.ENDER_DRAGON_FLAP, 3, .7F);
                level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + .2, getZ(), 30, 1.5, .1, 1.5, .08);
            }
            case LAND -> {
                Vec3 near = target == null ? position() : target.position().add(position().subtract(target.position()).multiply(1, 0, 1).normalize().scale(6));
                shiftTo = SkillShows.ground(level, near);
                SkillShows.sound(this, SoundEvents.ENDER_DRAGON_FLAP, 2, .9F);
            }
            case BURROW -> {
                burrowedAfter = target;
                setNoGravity(true);
                noPhysics = true;
                setDeltaMovement(Vec3.ZERO);
                SkillShows.show(level, position(), "fissure", 3, 1, 30, INDIGO, SILVER, .2, getYRot(), true);
                SkillShows.sound(this, SoundEvents.ROOTED_DIRT_BREAK, 3, .5F);
                SkillShows.sound(this, SoundEvents.HORSE_ANGRY, 2, .5F);
            }
            case EMERGE -> rise(level);
            default -> {}
        }
    }

    private void shiftTick(@Nullable LivingEntity target) {
        shiftElapsed++;
        int ticks = SeereRules.shiftTicks(skill());
        switch (skill()) {
            case TAKE_OFF -> {
                setDeltaMovement(getDeltaMovement().multiply(.6, 0, .6).add(0, .42, 0));
                if (shiftElapsed >= ticks) enter(Mode.SKY);
            }
            case LAND -> {
                Vec3 to = shiftTo.subtract(position());
                setDeltaMovement(to.length() < .3 ? Vec3.ZERO : to.normalize().scale(Math.min(.8, to.length())));
                if (target != null) turnTo(target.position());
                if (onGround() || to.length() < .8 || shiftElapsed >= ticks * 3) enter(Mode.GROUND);
            }
            case BURROW -> {
                setPos(getX(), getY() - (SeereRules.DEPTH + getBbHeight()) / ticks, getZ());
                if (level() instanceof ServerLevel level) dust(level, getX(), getZ(), 10);
                if (shiftElapsed >= ticks) enter(Mode.UNDERGROUND);
            }
            case EMERGE -> {
                float through = Math.min(1, shiftElapsed / (float) ticks);
                setPos(shiftTo.x, Mth.lerp(through * through * (3 - 2 * through), shiftFrom, shiftTo.y), shiftTo.z);
                if (target != null) turnTo(target.position());
                if (shiftElapsed >= ticks) enter(Mode.GROUND);
            }
            default -> enter(mode());
        }
    }

    /** Takes up going this way, at rest from going any other. */
    private void enter(Mode mode) {
        entityData.set(MODE, (byte) mode.ordinal());
        modeTicks = 0;
        setSkill(Skill.NONE);
        switch (mode) {
            case GROUND -> {
                setNoGravity(false);
                noPhysics = false;
                rest = SeereRules.GROUND_REST;
                // Up out of the earth, he takes up again the quarry he went under after, if he had let it go.
                if (burrowedAfter != null && getTarget() == null && burrowedAfter.isAlive() && burrowedAfter.level() == level()) setTarget(burrowedAfter);
                burrowedAfter = null;
            }
            case SKY -> {
                setNoGravity(true);
                noPhysics = false;
                rest = SeereRules.SKY_REST;
                hoverAt = SeereRules.SKY_LOW + 1;
            }
            case UNDERGROUND -> {
                setNoGravity(true);
                noPhysics = true;
                setDeltaMovement(Vec3.ZERO);
                rest = 0;
            }
        }
    }

    /** Under the earth: on towards his quarry along the ground, the ground trembling over him; up out of it once he is near (or has been under long enough). */
    private void burrowOn(@Nullable LivingEntity target) {
        Vec3 to = target == null ? Vec3.ZERO : target.position().subtract(position()).multiply(1, 0, 1);
        double along = to.length();
        if (SeereRules.rises(along, modeTicks, target != null)) {
            beginShift(Skill.EMERGE, target);
            return;
        }
        Vec3 step = along < 1e-4 ? Vec3.ZERO : to.scale(Math.min(SeereRules.BURROW_SPEED, along) / along);
        double x = getX() + step.x, z = getZ() + step.z;
        setPos(x, surfaceAt(x, z) - SeereRules.DEPTH - getBbHeight() * .5, z);
        setDeltaMovement(Vec3.ZERO);
        if (target != null) turnTo(target.position());
        if (level() instanceof ServerLevel level) {
            dust(level, x, z, 6);
            if (modeTicks % 8 == 0) SkillShows.sound(level, new Vec3(x, surfaceAt(x, z), z), SoundEvents.ROOTED_DIRT_BREAK, 1.5F, .5F);
        }
    }

    /** The ground over him trembles and spits dust. */
    private void dust(ServerLevel level, double x, double z, int count) {
        double top = surfaceAt(x, z);
        BlockState ground = level.getBlockState(BlockPos.containing(x, top - 1, z));
        if (!ground.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), x, top + .1, z, count, .9, .05, .9, .1);
    }

    /**
     * He rises up out of the earth where he is: the ground bursts, everything near is hurt and thrown up, the ground about
     * is torn up as by his dive, and he comes up through it to stand on it.
     */
    private void rise(ServerLevel level) {
        Vec3 top = SkillShows.ground(level, new Vec3(getX(), surfaceAt(getX(), getZ()) + 1, getZ()));
        shiftTo = top;
        shiftFrom = Math.min(getY(), top.y - getBbHeight());
        noPhysics = true;
        setNoGravity(true);
        setDeltaMovement(Vec3.ZERO);
        for (LivingEntity met : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().move(0, top.y - getY(), 0).inflate(SeereRules.RISE_RADIUS),
                met -> met != this && met.isAlive() && !isAlliedTo(met) && met.position().distanceTo(top) <= SeereRules.RISE_RADIUS + met.getBbWidth() / 2)) {
            met.invulnerableTime = 0;
            if (met.hurt(ModDamageTypes.corruption(level, this), (float) SeereRules.RISE_DAMAGE)) {
                met.push(0, .9, 0);
                met.hurtMarked = true;
            }
        }
        Debris.tear(level, this, top.add(0, -.5, 0), SeereRules.RISE_RADIUS - 1, 24, SeereRules.DIVE);
        SkillShows.show(level, top, "fissure", SeereRules.RISE_RADIUS + 1.5, 1.5, 36, INDIGO, SILVER, .4, getYRot(), true);
        SkillShows.show(level, top, "shockwave", SeereRules.RISE_RADIUS * 2, 1.5, 20, INDIGO, SILVER, .3, getYRot(), true);
        SkillShows.sound(this, SoundEvents.GENERIC_EXPLODE.value(), 2.5F, .8F);
        SkillShows.sound(this, SoundEvents.HORSE_ANGRY, 3, .45F);
    }

    // ---- how he moves ----

    /** On the ground he goes as anything does; in the sky he flies, held up by nothing, the air slowing him; under the earth he passes through it. */
    @Override public void travel(Vec3 input) {
        if (isDeadOrDying() || mode() == Mode.GROUND && !skill().shift() || !isControlledByLocalInstance()) {
            super.travel(input);
            return;
        }
        move(MoverType.SELF, getDeltaMovement());
        if (mode() == Mode.SKY || skill() == Skill.TAKE_OFF || skill() == Skill.LAND) setDeltaMovement(getDeltaMovement().scale(.9));
        calculateEntityAnimation(false);
    }

    /** Under the earth (rising out of it aside), nothing can strike at him, nor hurt him but what passes every defence. */
    @Override protected boolean exposed() { return mode() != Mode.UNDERGROUND || skill() == Skill.EMERGE; }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (!exposed() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return super.hurt(source, amount);
    }

    @Override public boolean onClimbable() { return false; }

    @Override public void die(DamageSource source) {
        skills.cease(this);
        setSkill(Skill.NONE);
        burrowedAfter = null;
        // Wherever he is, he falls as anything does to die: up out of the earth, or down out of the sky.
        if (mode() == Mode.UNDERGROUND && level() instanceof ServerLevel level) {
            Vec3 top = SkillShows.ground(level, new Vec3(getX(), surfaceAt(getX(), getZ()) + 1, getZ()));
            setPos(top.x, top.y, top.z);
        }
        entityData.set(MODE, (byte) Mode.GROUND.ordinal());
        noPhysics = false;
        setNoGravity(false);
        super.die(source);
    }

    // ---- what the client sees ----

    public Skill skill() {
        int index = entityData.get(SKILL);
        return index >= 0 && index < Skill.values().length ? Skill.values()[index] : Skill.NONE;
    }

    public Mode mode() {
        int index = entityData.get(MODE);
        return index >= 0 && index < Mode.values().length ? Mode.values()[index] : Mode.GROUND;
    }

    /** On a client: the skill to pose him by, the one he last began; its pose eases back by itself once it is over. */
    public Skill posedSkill() { return posed; }

    /** On a client: ticks since the skill to pose him by began. */
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    /** On a client: ticks since he took to the way he goes now. */
    public float modeTicks(float partial) { return tickCount - modeSeen + partial; }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key) && skill() != Skill.NONE) {
            posed = skill();
            skillSeen = tickCount;
        }
        if (MODE.equals(key)) modeSeen = tickCount;
    }

    /** His beams leave from before his breast, a little ahead of him, the way he faces. */
    @Override public Vec3 beamFrom() { return position().add(0, getBbHeight() * .62, 0).add(Vec3.directionFromRotation(0, getYRot()).scale(2.2)); }

    /** His heart: in his breast. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() * .55, 0).add(Vec3.directionFromRotation(0, getYRot()).scale(.6)); }

    /** Indigo flames lick about his hooves as he gallops, and feathers of light drift off him in the sky. */
    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide || !isAlive() || mode() == Mode.UNDERGROUND) return;
        if (mode() == Mode.SKY && random.nextInt(3) == 0)
            level().addParticle(ParticleTypes.END_ROD, getRandomX(1.6), getY() + random.nextDouble() * getBbHeight(), getRandomZ(1.6), 0, -.03, 0);
        if (mode() == Mode.GROUND && walkAnimation.speed() > .3F && random.nextInt(2) == 0)
            level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getRandomX(.9), getY() + .1, getRandomZ(.9), 0, .02, 0);
    }

    @Override public int emblem() { return 4; }
    @Override public BossDeath death() { return DEATH; }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.HORSE_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.HORSE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.HORSE_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.HORSE_GALLOP, .6F, .6F); }
    @Override public float getVoicePitch() { return .6F; }
    @Override public int getAmbientSoundInterval() { return 140; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Mode", (byte) mode().ordinal());
        if (skill().shift()) tag.putByte("Shift", (byte) skill().ordinal());
        tag.put("Skills", skills.save());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        skills.load(tag.getCompound("Skills"));
        Mode mode = Mode.values()[Math.max(0, Math.min(Mode.values().length - 1, tag.getByte("Mode")))];
        Skill shift = Skill.values()[Math.max(0, Math.min(Skill.values().length - 1, tag.getByte("Shift")))];
        // A shift broken off by the world's being saved is finished as it loads: going under, he is under; anything else, he is where it was taking him.
        enter(switch (shift) {
            case BURROW -> Mode.UNDERGROUND;
            case TAKE_OFF -> Mode.SKY;
            case LAND, EMERGE -> Mode.GROUND;
            default -> mode;
        });
        if (shift == Skill.EMERGE) {
            double top = surfaceAt(getX(), getZ());
            if (getY() < top) setPos(getX(), top, getZ());
        }
    }

    /** His blows, as they stand. */
    public SkillSet<Seere> skills() { return skills; }

    /** His dive, for whoever would look at where it struck. */
    public DiveSkill dive() { return dive; }
}
