package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BarrageSkill;
import com.miophas.corruption.common.entity.skill.DiveChainSkill;
import com.miophas.corruption.common.entity.skill.DiveSkill;
import com.miophas.corruption.common.entity.skill.ScreechSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpinSkill;
import com.miophas.corruption.common.entity.skill.SpinVolleySkill;
import com.miophas.corruption.common.entity.skill.StrikeSkill;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.DecarabiaRules.Form;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Decarabia, the sixty-ninth spirit of the Goetia: the marquis who comes as a star in a pentacle, who knows the virtues
 * of birds and of precious stones; brought forth at the altar (data/corruption/recipe/altar/decarabia.json). What he does
 * and when is {@link DecarabiaRules}; his skills are the modules every boss may share (common.entity.skill), and this
 * carries them out, and what is his alone: his likenesses, and his change.
 *
 * <p>He flies, a five-pointed star, hovering a few blocks over the ground about his quarry. Once hurt he may split into
 * five small stars, himself among them and the rest his likenesses: the same creature as he, small, with a little
 * health of their own and no boss bar, no loot, not kept when the world is saved, gone as soon as he is whole again. Each
 * of the five shoots and spins as the star does. Struck himself, he is found out: they all come back into him and he
 * hangs stunned. Brought to half his health he changes, as Dantalion does, into a great goshawk, whole again, and has
 * the goshawk's skills instead of the star's. As every {@link GoeticDemon}, only the arcane can hurt him.
 */
public final class Decarabia extends GoeticDemon {
    /** His colours: a star's gold, its brightest a pale gold-white; and his bolts, the corruption's rose. */
    public static final int GOLD = 0xF2C46A, PALE = 0xFFF2D0, ROSE = 0xE0609A;

    /**
     * His death: his feathers scattering, his stars wheeling about him as a constellation, the souls he held rising, a
     * ring of runes; then he bursts in his gold light, a ring of it racing out and a pillar of it standing where he fell.
     */
    public static final BossDeath DEATH = new BossDeath(DecarabiaRules.DEATH_TICKS, 50, 1.3F, new BossDeath.Rays(18, 6, GOLD, PALE), .15F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "feathers", 4, 2, 60, GOLD, PALE, .2, "minecraft:entity.phantom.death", 4, .6F),
            BossDeath.beat(4, BossDeath.Anchor.HEART, "constellation", 3.2, 2, 60, GOLD, PALE, 0, "minecraft:block.amethyst_block.resonate", 4, .5F),
            BossDeath.beat(20, BossDeath.Anchor.FEET, "souls", 2.2, 6, 46, GOLD, PALE, 0, "minecraft:particle.soul_escape", 4, .7F),
            BossDeath.beat(36, BossDeath.Anchor.FEET, "rune_ring", 2.6, 1.6, 30, GOLD, PALE, 0, "minecraft:entity.illusioner.mirror_move", 4, .6F),
            BossDeath.beat(66, BossDeath.Anchor.HEART, "nova", 9, 0, 26, GOLD, 0xFFFFFF, .6, "minecraft:entity.generic.explode", 4, .8F),
            BossDeath.beat(66, BossDeath.Anchor.FEET, "shockwave", 9, 2, 26, GOLD, PALE, 0, "", 0, 1),
            BossDeath.beat(67, BossDeath.Anchor.FEET, "light_pillar", 1.3, 30, 44, GOLD, PALE, 0, "minecraft:block.beacon.deactivate", 4, .6F)));
    /** A likeness's end: a little burst of his light, and it is gone. */
    public static final BossDeath LIKENESS_DEATH = new BossDeath(6, 4, .4F, new BossDeath.Rays(6, 2, GOLD, PALE), 0, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "nova", 1.2, 0, 10, GOLD, PALE, 0, "minecraft:block.amethyst_cluster.break", 1.5F, 1.4F)));

    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(Decarabia.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAWK = SynchedEntityData.defineId(Decarabia.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHANGING = SynchedEntityData.defineId(Decarabia.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STUNNED = SynchedEntityData.defineId(Decarabia.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LIKENESS = SynchedEntityData.defineId(Decarabia.class, EntityDataSerializers.BOOLEAN);
    /** Ticks he rests between one skill and the next: the star, the goshawk, a small star (a little more or less each time). */
    private static final int STAR_REST = 20, HAWK_REST = 14, SMALL_REST = 24;
    /** How fast he gathers speed towards where he would be, and how far he turns to face his quarry, a tick. */
    private static final double THRUST = .06;
    private static final float TURN = 14;

    private final SpinVolleySkill volley = new SpinVolleySkill(DecarabiaRules.VOLLEY, ROSE, PALE);
    private final DiveSkill dive = new DiveSkill(DecarabiaRules.DIVE, GOLD, PALE, true);
    private final DiveChainSkill dives = new DiveChainSkill(DecarabiaRules.DIVES, GOLD, PALE);
    private final ScreechSkill screech = new ScreechSkill(DecarabiaRules.SCREECH, GOLD, PALE);
    private final BarrageSkill barrage = new BarrageSkill(DecarabiaRules.BARRAGE, ROSE, PALE);
    /** His skills, in the order of DecarabiaRules.SKILLS. */
    private final SkillSet<Decarabia> skills = SkillSet.<Decarabia>of()
            .with("volley", volley)
            .with("spin", new SpinSkill(DecarabiaRules.SPIN))
            .with("split", new Split())
            .with("screech", screech)
            .with("dive", dive)
            .with("dives", dives)
            .with("storm", new StrikeSkill(DecarabiaRules.STORM, GOLD, PALE))
            .with("barrage", barrage);
    /** A small star's: its volley and its spin (his likenesses', and his own while he is one of them). */
    private final SkillSet<Decarabia> small = SkillSet.<Decarabia>of()
            .with("volley", new SpinVolleySkill(DecarabiaRules.SMALL_VOLLEY, ROSE, PALE))
            .with("spin", new SpinSkill(DecarabiaRules.SMALL_SPIN));
    private int rest = 30, wander;
    private double hoverAt = DecarabiaRules.HOVER_LOW + 1;
    /** His health as his change began: he is healed from it to the full as he changes. */
    private float changedFrom;
    /** Split: how long he has been one of five (-1 while he is whole), whether he has been found out, and the likenesses he left. */
    private int splitTicks = -1;
    private boolean found;
    private final List<Decarabia> likenesses = new ArrayList<>();
    /** A likeness: the star it is a likeness of. */
    @Nullable private Decarabia master;
    /** On a client: the tick his skill and his change last began, and the skill to pose him by (it settles after it is over). */
    private int skillSeen, changeSeen;
    private Skill posed = Skill.NONE;

    public Decarabia(EntityType<? extends Decarabia> type, Level level) {
        super(type, level, BossEvent.BossBarColor.YELLOW);
        xpReward = 100;
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, DecarabiaRules.MAX_HEALTH).add(Attributes.ARMOR, DecarabiaRules.ARMOR)
                .add(Attributes.FOLLOW_RANGE, DecarabiaRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, DecarabiaRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FLYING_SPEED, DecarabiaRules.STAR_SPEED)
                .add(Attributes.ATTACK_DAMAGE, DecarabiaRules.SPIN.damage());
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKILL, (byte) 0);
        builder.define(HAWK, false);
        builder.define(CHANGING, 0);
        builder.define(STUNNED, 0);
        builder.define(LIKENESS, false);
    }

    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        huntTheUncorrupted(2);
    }

    // ---- his mind ----

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = getTarget();
        if (target != null && (!target.isAlive() || target.level() != level())) target = null;
        if (likeness()) {
            likenessOn(target);
            return;
        }
        if (changing() > 0) {
            change();
            move(target, true);
            return;
        }
        if (split()) {
            splitOn(target);
            return;
        }
        if (!hawk() && DecarabiaRules.changes(getHealth() / getMaxHealth())) {
            beginChange();
            return;
        }
        if (stunned() > 0) {
            stunOn();
            return;
        }
        if (rest > 0) rest--;
        if (!skills.busy() && target != null && rest == 0) choose(target);
        skills.tick(this, target, cooldown -> cooldown);
        if (!skills.busy() && skill() != Skill.NONE) {
            setSkill(Skill.NONE);
            rest = hawk() ? HAWK_REST : STAR_REST;
        }
        if (!drivenBySkill()) move(target, skills.busy());
    }

    private void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : DecarabiaRules.SKILLS) if (skills.usable(index(skill), this, target)) ready.add(skill);
        DecarabiaRules.Situation now = new DecarabiaRules.Situation(hawk() ? Form.HAWK : Form.STAR, distanceTo(target), getY() - target.getY(),
                hasLineOfSight(target), getHealth() / getMaxHealth(), Stuns.stunned(target), ready);
        Skill next = DecarabiaRules.choose(now, random.nextDouble());
        if (next != Skill.NONE) begin(next, target);
    }

    /** Begins one of his skills on his quarry, if it has come round; whether it began (package-private, for the tests to begin one by hand). */
    boolean begin(Skill skill, LivingEntity target) {
        if (!skills.begin(index(skill), this, target)) return false;
        setSkill(skill);
        return true;
    }

    private static int index(Skill skill) { return DecarabiaRules.SKILLS.indexOf(skill); }

    private void setSkill(Skill skill) { entityData.set(SKILL, (byte) skill.ordinal()); }

    /** Whether the skill he is about moves him itself: his spin, and his stoops. */
    private boolean drivenBySkill() { return skill() == Skill.SPIN || skill() == Skill.DIVE || skill() == Skill.DIVES; }

    // ---- where he flies ----

    /**
     * The star hovers about his quarry at the distance he keeps, a few blocks over the ground, drifting slowly round it;
     * a small star the closer and lower; the goshawk circles it high and fast. With no quarry he hangs where he is. He
     * always turns to face it.
     */
    private void move(@Nullable LivingEntity target, boolean still) {
        boolean bird = hawk(), little = small();
        Vec3 want;
        if (target == null) want = new Vec3(getX(), groundUnder(position()) + hoverAt, getZ());
        else {
            Vec3 from = target.position(), off = position().subtract(from).multiply(1, 0, 1);
            if (off.lengthSqr() < 1e-4) off = new Vec3(1, 0, 0);
            if (--wander <= 0) {
                wander = 50 + random.nextInt(70);
                hoverAt = bird ? Mth.nextDouble(random, DecarabiaRules.SKY_LOW, DecarabiaRules.SKY_HIGH)
                        : Mth.nextDouble(random, DecarabiaRules.HOVER_LOW, DecarabiaRules.HOVER_HIGH) * (little ? .6 : 1);
            }
            double keep;
            float round;
            if (bird) {
                keep = DecarabiaRules.SKY_KEEP;
                round = 2.4F;
            } else {
                double distance = Math.sqrt(off.lengthSqr());
                keep = DecarabiaRules.keep(distance) != 0 ? DecarabiaRules.KEEP_AT : Mth.clamp(distance, DecarabiaRules.NEAR + 1, DecarabiaRules.FAR - 2);
                if (little) keep *= .7;
                round = little ? 2 : 1;
            }
            Vec3 turned = still ? off : off.yRot((float) Math.toRadians(round * (wander % 160 < 80 ? 1 : -1)));
            Vec3 at = from.add(turned.normalize().scale(keep));
            want = new Vec3(at.x, Math.max(groundUnder(at) + hoverAt, target.getY() + (bird ? DecarabiaRules.SKY_LOW * .6 : 1.2)), at.z);
            face(target);
        }
        Vec3 to = want.subtract(position());
        double speed = (still ? .4 : 1) * getAttributeValue(Attributes.FLYING_SPEED);
        Vec3 push = to.lengthSqr() > 1e-4 ? to.normalize().scale(Math.min(THRUST * (bird ? 2 : 1), to.length() * .06)) : Vec3.ZERO;
        Vec3 moving = getDeltaMovement().add(push);
        if (moving.length() > speed) moving = moving.normalize().scale(speed);
        setDeltaMovement(moving);
        if (bird && !still) setXRot((float) Mth.clamp(-Mth.atan2(moving.y, Math.max(.05, moving.horizontalDistance())) * Mth.RAD_TO_DEG, -25, 25));
    }

    private void face(LivingEntity target) {
        Vec3 to = target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter());
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90;
        setYRot(Mth.approachDegrees(getYRot(), yaw, TURN));
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

    /** He flies: no gravity, the air slowing him; a little more for the goshawk's speed. */
    @Override public void travel(Vec3 input) {
        if (isControlledByLocalInstance()) {
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(hawk() ? .9 : .86));
        }
        calculateEntityAnimation(false);
    }

    @Override public boolean onClimbable() { return false; }

    // ---- his split ----

    /** His split, as a skill of his own: gathering himself through its wind-up, his stars wheeling about him; then gone into five. */
    private final class Split implements BossSkill<Decarabia> {
        @Override public SkillTiming timing() { return DecarabiaRules.SPLIT; }
        @Override public boolean usable(Decarabia boss, LivingEntity target) { return !boss.hawk() && !boss.split() && boss.hasLineOfSight(target); }

        @Override public void begin(Decarabia boss, LivingEntity target) {
            SkillShows.sound(boss, SoundEvents.AMETHYST_BLOCK_RESONATE, 3, .5F);
            if (boss.level() instanceof ServerLevel level)
                SkillShows.show(level, boss.getBoundingBox().getCenter(), "constellation", 3.5, 2, DecarabiaRules.SPLIT.windup() + 10, GOLD, PALE, 0, boss.getYRot(), false);
        }

        @Override public void tick(Decarabia boss, @Nullable LivingEntity target, int elapsed) {
            boss.setDeltaMovement(boss.getDeltaMovement().scale(.5));
            if (DecarabiaRules.SPLIT.strikes(elapsed)) boss.splitNow(target);
        }
    }

    /** He splits: five small stars round where he was, himself one of them (which, nobody can tell), the rest his likenesses. */
    void splitNow(@Nullable LivingEntity target) {
        if (!(level() instanceof ServerLevel level) || split()) return;
        Vec3 from = position();
        double turn = random.nextDouble() * Math.PI * 2;
        int himself = random.nextInt(DecarabiaRules.LIKENESSES + 1);
        for (int index = 0; index <= DecarabiaRules.LIKENESSES; index++) {
            double[] at = DecarabiaRules.scatter(index, turn);
            Vec3 place = from.add(at[0], 0, at[1]);
            SkillShows.show(level, place, "nova", 1.6, 0, 12, GOLD, PALE, 0, getYRot(), false);
            if (index == himself) {
                moveTo(place.x, place.y, place.z, getYRot(), getXRot());
                continue;
            }
            Decarabia likeness = ModEntities.DECARABIA.get().create(level);
            if (likeness == null) continue;
            likeness.becomeLikeness(this, place, target);
            level.addFreshEntity(likeness);
            likenesses.add(likeness);
        }
        scaled(DecarabiaRules.SMALL);
        splitTicks = 0;
        found = false;
        rest = 10;
        SkillShows.sound(this, SoundEvents.AMETHYST_CLUSTER_BREAK, 3, .6F);
    }

    /** One of the five, while he is: as a small star does; and when he is found out, or they have gone unfound long enough, whole again. */
    private void splitOn(@Nullable LivingEntity target) {
        splitTicks++;
        likenesses.removeIf(likeness -> !likeness.isAlive() || likeness.isRemoved());
        if (found || splitTicks >= DecarabiaRules.SPLIT_MOST) {
            merge(found);
            return;
        }
        smallOn(target);
    }

    /**
     * Every likeness he left comes back into him (a streak of light from each), and he is the star again, whole in size;
     * found out ({@code caught}), he hangs stunned. He does not split again for a long while.
     */
    void merge(boolean caught) {
        if (level() instanceof ServerLevel level) {
            Vec3 heart = getBoundingBox().getCenter();
            for (Decarabia likeness : likenesses) {
                if (!likeness.isAlive()) continue;
                Vec3 there = likeness.getBoundingBox().getCenter();
                for (int step = 0; step <= 12; step++) {
                    Vec3 along = there.lerp(heart, step / 12.0);
                    level.sendParticles(ParticleTypes.END_ROD, along.x, along.y, along.z, 1, .05, .05, .05, 0);
                }
                likeness.discard();
            }
            SkillShows.show(level, heart, "constellation", 3, 2, 30, GOLD, PALE, .2, getYRot(), false);
        }
        likenesses.clear();
        small.interrupt(this, cooldown -> cooldown);
        scaled(1);
        splitTicks = -1;
        found = false;
        setSkill(Skill.NONE);
        skills.holdOff(index(Skill.SPLIT), DecarabiaRules.SPLIT_COOLDOWN);
        if (caught) {
            entityData.set(STUNNED, DecarabiaRules.STUN_TICKS);
            SkillShows.sound(this, SoundEvents.AMETHYST_BLOCK_BREAK, 3, .5F);
            SkillShows.sound(this, SoundEvents.ANVIL_LAND, 1.5F, .6F);
        } else {
            rest = STAR_REST;
            SkillShows.sound(this, SoundEvents.AMETHYST_BLOCK_RESONATE, 3, 1.2F);
        }
    }

    /** Small, as he is among his likenesses and they are: its volley and its spin, and on about its quarry as a small star does. */
    private void smallOn(@Nullable LivingEntity target) {
        if (rest > 0) rest--;
        if (!small.busy() && target != null && rest == 0) {
            Set<Skill> ready = EnumSet.noneOf(Skill.class);
            if (small.usable(0, this, target)) ready.add(Skill.VOLLEY);
            if (small.usable(1, this, target)) ready.add(Skill.SPIN);
            Skill next = DecarabiaRules.chooseSmall(distanceTo(target), hasLineOfSight(target), ready, random.nextDouble());
            if (next != Skill.NONE && small.begin(next == Skill.VOLLEY ? 0 : 1, this, target)) setSkill(next);
        }
        small.tick(this, target, cooldown -> cooldown);
        if (!small.busy() && skill() != Skill.NONE) {
            setSkill(Skill.NONE);
            rest = SMALL_REST + random.nextInt(20);
        }
        if (skill() != Skill.SPIN) move(target, small.busy());
    }

    /** Made one of his likenesses: small, with a little health of its own, at this place, after this quarry. */
    void becomeLikeness(Decarabia of, Vec3 place, @Nullable LivingEntity target) {
        master = of;
        entityData.set(LIKENESS, true);
        xpReward = 0;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(DecarabiaRules.LIKENESS_HEALTH);
        setHealth((float) DecarabiaRules.LIKENESS_HEALTH);
        scaled(DecarabiaRules.SMALL);
        moveTo(place.x, place.y, place.z, of.getYRot(), 0);
        rest = 10 + random.nextInt(30);
        if (target != null) setTarget(target);
    }

    /** A likeness: as a small star does, after its star's quarry; gone as soon as he is whole again, or gone. */
    private void likenessOn(@Nullable LivingEntity target) {
        if (master == null || !master.isAlive() || !master.split()) {
            if (level() instanceof ServerLevel level) {
                Vec3 heart = getBoundingBox().getCenter();
                level.sendParticles(ParticleTypes.END_ROD, heart.x, heart.y, heart.z, 12, .3, .3, .3, .05);
            }
            discard();
            return;
        }
        LivingEntity quarry = master.getTarget() != null && master.getTarget().isAlive() ? master.getTarget() : target;
        if (quarry != null && quarry != getTarget()) setTarget(quarry);
        smallOn(quarry);
    }

    private void scaled(double scale) { getAttribute(Attributes.SCALE).setBaseValue(scale); }

    // ---- his stun ----

    /** Stunned: no skill, no flight of his own; he sinks a little, stars wheeling about him, until it wears off. */
    private void stunOn() {
        int left = stunned() - 1;
        entityData.set(STUNNED, left);
        double over = getY() - groundUnder(position());
        setDeltaMovement(getDeltaMovement().scale(.7).add(0, over > 1.5 ? -.03 : 0, 0));
        if (level() instanceof ServerLevel level && left % 4 == 0)
            for (int star = 0; star < 3; star++) {
                double round = tickCount * .3 + star * Math.PI * 2 / 3;
                level.sendParticles(ParticleTypes.CRIT, getX() + Math.cos(round) * 1.4, getY() + getBbHeight() + .3, getZ() + Math.sin(round) * 1.4, 1, 0, 0, 0, 0);
            }
        if (left == 0) {
            rest = STAR_REST;
            SkillShows.sound(this, SoundEvents.AMETHYST_BLOCK_RESONATE, 2, 1.4F);
        }
    }

    // ---- his change ----

    void beginChange() {
        if (split()) merge(false);
        entityData.set(STUNNED, 0);
        changedFrom = getHealth();
        skills.interrupt(this, cooldown -> cooldown);
        setSkill(Skill.NONE);
        entityData.set(CHANGING, DecarabiaRules.CHANGE_TICKS);
        playSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 3, .5F);
        playSound(SoundEvents.PHANTOM_AMBIENT, 3, .5F);
        if (level() instanceof ServerLevel level) {
            SkillShows.show(level, position(), "vortex", 3, 4, DecarabiaRules.CHANGE_TICKS, GOLD, PALE, .3, getYRot(), true);
            SkillShows.show(level, getBoundingBox().getCenter(), "constellation", 3.5, 2, DecarabiaRules.CHANGE_TICKS, GOLD, PALE, 0, getYRot(), false);
        }
    }

    /** A tick of his change: his health fills back up as it goes, all of it by its end; at its middle the star is gone and the goshawk is there. */
    private void change() {
        int left = changing() - 1;
        entityData.set(CHANGING, left);
        setHealth(left == 0 ? getMaxHealth() : DecarabiaRules.healthAt(changedFrom, getMaxHealth(), 1 - left / (float) DecarabiaRules.CHANGE_TICKS));
        if (!(level() instanceof ServerLevel level)) return;
        Vec3 heart = getBoundingBox().getCenter();
        if (left % 6 == 0) level.sendParticles(ParticleTypes.END_ROD, heart.x, heart.y, heart.z, 20, 1, 1, 1, .1);
        if (left == DecarabiaRules.CHANGE_TICKS / 2) {
            entityData.set(HAWK, true);
            getAttribute(Attributes.FLYING_SPEED).setBaseValue(DecarabiaRules.HAWK_SPEED);
            playSound(SoundEvents.PHANTOM_DEATH, 3, .6F);
            SkillShows.show(level, heart, "nova", 5, 0, 22, GOLD, 0xFFFFFF, .5, getYRot(), false);
            SkillShows.show(level, heart, "feathers", 3, 2, 40, GOLD, PALE, 0, getYRot(), false);
        }
        if (left == 0) {
            playSound(SoundEvents.ENDER_DRAGON_GROWL, 2, 1.6F);
            SkillShows.show(level, position(), "shockwave", 8, 2, 24, GOLD, PALE, .4, getYRot(), true);
            rest = HAWK_REST;
        }
    }

    // ---- what reaches him ----

    /** Through his change nothing hurts him but what passes every defence; one of five, struck himself, he is found out. */
    @Override public boolean hurt(DamageSource source, float amount) {
        if (changing() > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        boolean hurt = super.hurt(source, amount);
        if (hurt && !likeness() && split()) found = true;
        return hurt;
    }

    @Override public void die(DamageSource source) {
        skills.cease(this);
        small.cease(this);
        setSkill(Skill.NONE);
        if (!likeness()) {
            for (Decarabia likeness : likenesses) if (likeness.isAlive()) likeness.discard();
            likenesses.clear();
            if (split()) scaled(1);
            splitTicks = -1;
        }
        super.die(source);
    }

    @Override public void remove(RemovalReason reason) {
        if (!level().isClientSide && !likeness()) for (Decarabia likeness : likenesses) if (likeness.isAlive()) likeness.discard();
        super.remove(reason);
    }

    /** Dying, he sinks to the ground for his light to burst there; a likeness is only gone. */
    @Override protected void tickDeath() {
        if (!onGround() && !likeness()) setDeltaMovement(getDeltaMovement().multiply(.6, 1, .6).add(0, -.06, 0));
        super.tickDeath();
    }

    /** A likeness has no bar, is not kept, and leaves nothing. */
    @Override protected boolean barShown() { return !likeness(); }
    @Override public boolean shouldBeSaved() { return !likeness() && super.shouldBeSaved(); }
    @Override protected boolean shouldDropLoot() { return !likeness() && super.shouldDropLoot(); }
    @Override public boolean shouldDropExperience() { return !likeness() && super.shouldDropExperience(); }

    // ---- what the client sees ----

    public Skill skill() {
        int index = entityData.get(SKILL);
        return index >= 0 && index < Skill.values().length ? Skill.values()[index] : Skill.NONE;
    }

    /** Whether he is the goshawk now. */
    public boolean hawk() { return entityData.get(HAWK); }

    /** Ticks left of his change, and of his stun; 0 when he is not changing, or stunned. */
    public int changing() { return entityData.get(CHANGING); }
    public int stunned() { return entityData.get(STUNNED); }

    /** Whether this is only one of his likenesses. */
    public boolean likeness() { return entityData.get(LIKENESS); }

    /** Whether he (or the star this is a likeness of) is one of five now; and whether this one is small. */
    public boolean split() { return likeness() ? master != null && master.split() : splitTicks >= 0; }
    public boolean small() { return getAttributeValue(Attributes.SCALE) < .99; }

    /** On a client: the skill to pose him by, the one he last began; its pose eases back by itself once it is over. */
    public Skill posedSkill() { return posed; }

    /** On a client: ticks since the skill to pose him by began. */
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    /** On a client: how far through his change he is, 0 to 1; 0 when he is not changing. */
    public float changed(float partial) {
        return changing() <= 0 ? 0 : Mth.clamp((tickCount - changeSeen + partial) / DecarabiaRules.CHANGE_TICKS, 0, 1);
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key) && skill() != Skill.NONE) {
            posed = skill();
            skillSeen = tickCount;
        }
        if (CHANGING.equals(key) && changing() == DecarabiaRules.CHANGE_TICKS) {
            changeSeen = tickCount;
            posed = Skill.NONE;
        }
        if (HAWK.equals(key)) refreshDimensions();
    }

    /** The star, or the goshawk; either as big as his scale makes him. */
    @Override public EntityDimensions getDefaultDimensions(Pose pose) {
        return hawk() ? EntityDimensions.scalable(DecarabiaRules.HAWK_WIDTH, DecarabiaRules.HAWK_HEIGHT)
                : EntityDimensions.scalable(DecarabiaRules.STAR_SIZE, DecarabiaRules.STAR_SIZE);
    }

    /** His heart: the middle of him. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() * .5, 0); }

    /** Motes of his light glint off the star. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && !hawk() && random.nextInt(5) == 0)
            level().addParticle(ParticleTypes.END_ROD, getRandomX(.8), getY() + random.nextDouble() * getBbHeight(), getRandomZ(.8), 0, -.01, 0);
    }

    @Override public int emblem() { return 5; }
    @Override public BossDeath death() { return likeness() ? LIKENESS_DEATH : DEATH; }

    @Override protected SoundEvent getAmbientSound() { return hawk() ? SoundEvents.PHANTOM_AMBIENT : SoundEvents.AMETHYST_BLOCK_CHIME; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return hawk() ? SoundEvents.PHANTOM_HURT : SoundEvents.AMETHYST_BLOCK_HIT; }
    @Override protected SoundEvent getDeathSound() { return likeness() ? SoundEvents.AMETHYST_CLUSTER_BREAK : SoundEvents.PHANTOM_DEATH; }
    @Override public float getVoicePitch() { return hawk() ? .7F : 1.1F; }
    @Override public int getAmbientSoundInterval() { return 120; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Hawk", hawk());
        tag.put("Skills", skills.save());
        if (stunned() > 0) tag.putInt("Stunned", stunned());
        if (changing() > 0) {
            tag.putInt("Changing", changing());
            tag.putFloat("ChangedFrom", changedFrom);
        }
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(HAWK, tag.getBoolean("Hawk"));
        skills.load(tag.getCompound("Skills"));
        entityData.set(STUNNED, Math.max(0, tag.getInt("Stunned")));
        entityData.set(CHANGING, Math.max(0, tag.getInt("Changing")));
        changedFrom = tag.getFloat("ChangedFrom");
        // His likenesses are not kept: saved while one of five, he comes back whole.
        scaled(1);
        if (hawk()) getAttribute(Attributes.FLYING_SPEED).setBaseValue(DecarabiaRules.HAWK_SPEED);
    }

    /** His skills, and a small star's, as they stand. */
    public SkillSet<Decarabia> skills() { return skills; }
    public SkillSet<Decarabia> smallSkills() { return small; }

    /** The likenesses he left, while he is one of five. */
    public List<Decarabia> likenesses() { return List.copyOf(likenesses); }

    /** His skills, for whoever would look at what they did. */
    public SpinVolleySkill volley() { return volley; }
    public DiveSkill dive() { return dive; }
    public DiveChainSkill dives() { return dives; }
    public ScreechSkill screech() { return screech; }
    public BarrageSkill barrage() { return barrage; }
}
