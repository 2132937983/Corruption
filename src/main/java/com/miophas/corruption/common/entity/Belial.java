package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.BossDeath;
import com.miophas.corruption.api.demon.GoeticDemon;
import com.miophas.corruption.api.demon.skill.BeamCaster;
import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.BeamVolleySkill;
import com.miophas.corruption.common.entity.skill.BlinkSkill;
import com.miophas.corruption.common.entity.skill.ChariotSkill;
import com.miophas.corruption.common.entity.skill.PsychicSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpinVolleySkill;
import com.miophas.corruption.common.entity.skill.SummonSkill;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.BelialRules.Form;
import com.miophas.corruption.core.demon.BelialRules.Skill;
import com.miophas.corruption.core.demon.BelialRules;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Belial, the sixty-eighth spirit of the Goetia: the king who comes as two fair angels in a chariot of fire; brought
 * forth at the altar (data/corruption/recipe/altar/belial.json). What he does and when is {@link BelialRules}; his skills
 * are the modules every boss may share (common.entity.skill), and this carries them out, and what is his alone: his fall
 * and rising, his likenesses, his stun.
 *
 * <p>He is first the fair angel, flying about his quarry a few blocks over the ground. He must be brought down, his
 * health to nothing, and then he does not die: he falls, and the hollow angel rises in his place, whole, as nothing hurts
 * him (only what passes every defence brings the fair angel down for good). The hollow angel has the fair angel's skills
 * and his own: likenesses of himself, of his own kind, with a little health of their own and no boss bar, no loot, not
 * kept when the world is saved; struck himself, he is found out, they are gone and he hangs stunned. As every
 * {@link GoeticDemon}, only the arcane hurts him (unless the server says otherwise).
 *
 * <p>His beams leave from his hands, the fair angel's two, and from the eyes on the hollow angel's six wings
 * ({@link #beamFrom(int, int, Vec3)}).
 */
public final class Belial extends GoeticDemon implements BeamCaster {
    /** His colours: the fair angel's gold and its white light; the corruption's rose; the hollow angel's fire and its embers. */
    public static final int GOLD = 0xF6D27A, WHITE = 0xFFF6E0, ROSE = 0xE0609A, FIRE = 0xFF7A33, EMBER = 0xFFD9A0;

    /**
     * The hollow angel's death: his wings spread a last time and his feathers scatter, a crown of fire over him, the souls
     * he held rising, a ring of runes; then he bursts in his fire, a ring of it racing out and a pillar of it standing.
     */
    public static final BossDeath DEATH = new BossDeath(BelialRules.DEATH_TICKS, 50, 1.3F, new BossDeath.Rays(18, 7, FIRE, EMBER), .15F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "wings", 3, 2, 60, FIRE, EMBER, .2, "minecraft:entity.allay.death", 4, .4F),
            BossDeath.beat(4, BossDeath.Anchor.HEART, "feathers", 4, 2, 60, FIRE, EMBER, 0, "minecraft:entity.phantom.death", 4, .5F),
            BossDeath.beat(10, BossDeath.Anchor.FEET, "crown", 2.4, 4.6, 50, FIRE, EMBER, 0, "minecraft:block.fire.extinguish", 4, .5F),
            BossDeath.beat(24, BossDeath.Anchor.FEET, "souls", 2.2, 6, 46, FIRE, EMBER, 0, "minecraft:particle.soul_escape", 4, .7F),
            BossDeath.beat(40, BossDeath.Anchor.FEET, "rune_ring", 2.6, 1.6, 30, FIRE, EMBER, 0, "minecraft:entity.illusioner.mirror_move", 4, .6F),
            BossDeath.beat(76, BossDeath.Anchor.HEART, "nova", 9, 0, 26, FIRE, 0xFFFFFF, .6, "minecraft:entity.generic.explode", 4, .7F),
            BossDeath.beat(76, BossDeath.Anchor.FEET, "shockwave", 9, 2, 26, FIRE, EMBER, 0, "", 0, 1),
            BossDeath.beat(77, BossDeath.Anchor.FEET, "light_pillar", 1.3, 30, 44, FIRE, EMBER, 0, "minecraft:block.beacon.deactivate", 4, .6F)));
    /** A likeness's end: a little burst of his fire, and it is gone. */
    public static final BossDeath LIKENESS_DEATH = new BossDeath(6, 4, .5F, new BossDeath.Rays(6, 2, FIRE, EMBER), 0, List.of(
            BossDeath.beat(0, BossDeath.Anchor.HEART, "nova", 1.4, 0, 10, FIRE, EMBER, 0, "minecraft:entity.allay.hurt", 1.5F, .5F)));

    /**
     * Where his beams leave from, in blocks from his feet in his own frame ({right, up, ahead}): the fair angel's two
     * hands raised before him, and the eyes on the hollow angel's six wings (upper, middle, lower; his left first), as his
     * models hold them for it (BelialModelTest holds them to the models).
     */
    public static final double[][] FAIR_HANDS = {{-.44, 2.48, 1.19}, {.44, 2.48, 1.19}};
    public static final double[][] HOLLOW_EYES = {{-.77, 2.78, -.27}, {.77, 2.78, -.27}, {-.73, 1.94, -.29}, {.73, 1.94, -.29}, {-.53, 1.44, -.24}, {.53, 1.44, -.24}};

    private static final EntityDataAccessor<Byte> SKILL = SynchedEntityData.defineId(Belial.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HOLLOW = SynchedEntityData.defineId(Belial.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHANGING = SynchedEntityData.defineId(Belial.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STUNNED = SynchedEntityData.defineId(Belial.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LIKENESS = SynchedEntityData.defineId(Belial.class, EntityDataSerializers.BOOLEAN);
    /** Ticks he rests between one skill and the next: the fair angel, the hollow, one of five (a little more or less each time). */
    private static final int FAIR_REST = 22, HOLLOW_REST = 14, LIKENESS_REST = 20;
    /** How fast he gathers speed towards where he would be, and how far he turns to face his quarry, a tick. */
    private static final double THRUST = .05;
    private static final float TURN = 12;

    private final SpinVolleySkill volley = new SpinVolleySkill(BelialRules.VOLLEY, ROSE, GOLD);
    private final SpinVolleySkill hollowVolley = new SpinVolleySkill(BelialRules.HOLLOW_VOLLEY, FIRE, EMBER);
    private final BeamVolleySkill beams = new BeamVolleySkill(BelialRules.BEAMS, boss -> BelialRules.beams(((Belial) boss).form()), GOLD, WHITE);
    private final BlinkSkill blink = new BlinkSkill(BelialRules.BLINK, GOLD, WHITE);
    private final SummonSkill summon = new SummonSkill(BelialRules.SUMMON, ROSE, GOLD);
    private final ChariotSkill chariot = new ChariotSkill(BelialRules.CHARIOT, FIRE, EMBER);
    private final PsychicSkill psychic = new PsychicSkill(BelialRules.PSYCHIC, ROSE, EMBER);
    /** His skills. The volley's slot is each shape's own (see {@link #slot}). */
    private final SkillSet<Belial> skills = SkillSet.<Belial>of()
            .with("volley", volley)
            .with("hollow_volley", hollowVolley)
            .with("beams", beams)
            .with("blink", blink)
            .with("summon", summon)
            .with("chariot", chariot)
            .with("clones", new Clones())
            .with("psychic", psychic);
    /** What one of the five does while his likenesses are about (they, and he among them): the hollow volley, the strike at the mind. */
    private final SkillSet<Belial> likenessSkills = SkillSet.<Belial>of()
            .with("volley", new SpinVolleySkill(BelialRules.HOLLOW_VOLLEY, FIRE, EMBER))
            .with("psychic", new PsychicSkill(BelialRules.PSYCHIC, ROSE, EMBER));
    private int rest = 30, wander;
    private double hoverAt = BelialRules.HOVER_LOW + 1;
    /** Split: how long his likenesses have been about (-1 while they are not), whether he has been found out, and the likenesses. */
    private int splitTicks = -1;
    private boolean found;
    private final List<Belial> likenesses = new ArrayList<>();
    /** A likeness: whose it is. */
    @Nullable private Belial master;
    /** On a client: the tick his skill and his change last began, and the skill to pose him by (it settles after it is over). */
    private int skillSeen, changeSeen;
    private Skill posed = Skill.NONE;

    public Belial(EntityType<? extends Belial> type, Level level) {
        super(type, level, BossEvent.BossBarColor.WHITE);
        xpReward = 120;
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, BelialRules.MAX_HEALTH).add(Attributes.ARMOR, BelialRules.ARMOR)
                .add(Attributes.FOLLOW_RANGE, BelialRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, BelialRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.FLYING_SPEED, BelialRules.FLY_SPEED);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKILL, (byte) 0);
        builder.define(HOLLOW, false);
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
            hover(target, true);
            return;
        }
        if (split()) {
            splitOn(target);
            return;
        }
        if (stunned() > 0) {
            stunOn();
            return;
        }
        boolean unsafe = blink.weigh(this, target);
        if (rest > 0) rest--;
        if (!skills.busy() && target != null && (rest == 0 || unsafe)) choose(target, unsafe);
        skills.tick(this, target, cooldown -> cooldown);
        if (!skills.busy() && skill() != Skill.NONE) {
            setSkill(Skill.NONE);
            rest = hollow() ? HOLLOW_REST : FAIR_REST;
        }
        hover(target, skills.busy());
    }

    private void choose(LivingEntity target, boolean unsafe) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : BelialRules.SKILLS) if (skills.usable(slot(skill), this, target)) ready.add(skill);
        BelialRules.Situation now = new BelialRules.Situation(form(), distanceTo(target), hasLineOfSight(target), getHealth() / getMaxHealth(), unsafe,
                SummonSkill.called(this), false, ready);
        Skill next = BelialRules.choose(now, random.nextDouble());
        if (next != Skill.NONE) begin(next, target);
    }

    /** Begins one of his skills on his quarry, if it has come round; whether it began (package-private, for the tests to begin one by hand). */
    boolean begin(Skill skill, LivingEntity target) {
        if (!skills.begin(slot(skill), this, target)) return false;
        setSkill(skill);
        return true;
    }

    /** Where a skill of his is kept in his set: the volley each shape's own, the rest each in its place. */
    int slot(Skill skill) {
        return switch (skill) {
            case VOLLEY -> hollow() ? 1 : 0;
            case BEAMS -> 2;
            case BLINK -> 3;
            case SUMMON -> 4;
            case CHARIOT -> 5;
            case CLONES -> 6;
            case PSYCHIC -> 7;
            case NONE -> -1;
        };
    }

    private void setSkill(Skill skill) { entityData.set(SKILL, (byte) skill.ordinal()); }

    // ---- where he flies ----

    /**
     * He hovers about his quarry at the distance he keeps, a few blocks over the ground, drifting slowly round it (at a
     * skill, holding nearly still); one of five, a little closer and lower. With no quarry he hangs where he is. He always
     * turns to face it.
     */
    private void hover(@Nullable LivingEntity target, boolean still) {
        Vec3 want;
        if (target == null) want = new Vec3(getX(), groundUnder(position()) + hoverAt, getZ());
        else {
            Vec3 from = target.position(), off = position().subtract(from).multiply(1, 0, 1);
            if (off.lengthSqr() < 1e-4) off = new Vec3(1, 0, 0);
            if (--wander <= 0) {
                wander = 60 + random.nextInt(80);
                hoverAt = Mth.nextDouble(random, BelialRules.HOVER_LOW, BelialRules.HOVER_HIGH);
            }
            double distance = Math.sqrt(off.lengthSqr());
            double keep = BelialRules.keep(distance) != 0 ? BelialRules.KEEP_AT : Mth.clamp(distance, BelialRules.NEAR + 1, BelialRules.FAR - 2);
            if (likeness() || split()) keep *= .8;
            Vec3 round = still ? off : off.yRot((float) Math.toRadians(.9 * (wander % 200 < 100 ? 1 : -1)));
            Vec3 at = from.add(round.normalize().scale(keep));
            want = new Vec3(at.x, Math.max(groundUnder(at) + hoverAt, target.getY() + 1.5), at.z);
            face(target);
        }
        Vec3 to = want.subtract(position());
        double speed = (still ? .35 : 1) * getAttributeValue(Attributes.FLYING_SPEED);
        Vec3 push = to.lengthSqr() > 1e-4 ? to.normalize().scale(Math.min(THRUST, to.length() * .05)) : Vec3.ZERO;
        Vec3 moving = getDeltaMovement().add(push);
        if (moving.length() > speed) moving = moving.normalize().scale(speed);
        setDeltaMovement(moving);
    }

    private void face(LivingEntity target) {
        Vec3 to = target.getEyePosition().subtract(getEyePosition());
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90, pitch = (float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG);
        setYRot(Mth.approachDegrees(getYRot(), yaw, TURN));
        setXRot(Mth.approachDegrees(getXRot(), Mth.clamp(pitch, -40, 40), TURN / 2));
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

    /** He flies: no gravity, the air slowing him. */
    @Override public void travel(Vec3 input) {
        if (isControlledByLocalInstance()) {
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(.86));
        }
        calculateEntityAnimation(false);
    }

    @Override public boolean onClimbable() { return false; }

    // ---- his beams ----

    /** Where his beams leave from by default: before his breast. */
    @Override public Vec3 beamFrom() { return frame(0, getBbHeight() * .7, .6); }

    /** The fair angel's from his two hands; the hollow angel's from the eyes of his six wings. */
    @Override public Vec3 beamFrom(int index, int count, Vec3 circled) {
        double[][] places = hollow() ? HOLLOW_EYES : FAIR_HANDS;
        if (index >= places.length) return circled;
        double[] at = places[index];
        return frame(at[0], at[1] * getScale(), at[2]);
    }

    /** A place in his own frame ({right, up, ahead} from his feet, in blocks), in the world. */
    private Vec3 frame(double right, double up, double ahead) {
        Vec3 forward = Vec3.directionFromRotation(0, getYRot()), side = new Vec3(-forward.z, 0, forward.x);
        return position().add(side.scale(right)).add(forward.scale(ahead)).add(0, up, 0);
    }

    // ---- his fall and rising ----

    /**
     * The fair angel brought to nothing does not die: he is held at the last of his health and falls, and the hollow angel
     * rises (see {@link #beginChange}). What passes every defence (/kill, the void) brings him down for good; a likeness
     * only dies.
     */
    @Override protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, amount);
        if (getHealth() <= 0 && !hollow() && !likeness() && changing() == 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            setHealth(1);
            beginChange();
        }
    }

    void beginChange() {
        if (split()) merge(false);
        entityData.set(STUNNED, 0);
        skills.interrupt(this, cooldown -> cooldown);
        setSkill(Skill.NONE);
        entityData.set(CHANGING, BelialRules.CHANGE_TICKS);
        playSound(SoundEvents.ALLAY_DEATH, 3, .5F);
        playSound(SoundEvents.BEACON_DEACTIVATE, 3, .6F);
        if (level() instanceof ServerLevel level) {
            Vec3 heart = getBoundingBox().getCenter();
            SkillShows.show(level, heart, "feathers", 3, 2, BelialRules.CHANGE_TICKS / 2, GOLD, WHITE, 0, getYRot(), false);
            SkillShows.show(level, heart, "rays", 4, 4, BelialRules.CHANGE_TICKS / 2, GOLD, WHITE, .2, getYRot(), false);
            SkillShows.show(level, position(), "light_pillar", 1.2, 18, BelialRules.CHANGE_TICKS / 2, GOLD, WHITE, 0, getYRot(), true);
        }
    }

    /**
     * A tick of his change: his health fills from nothing as it goes, all of it by its end; at its middle the fair angel is
     * gone and the hollow angel is there, rising out of his fire.
     */
    private void change() {
        int left = changing() - 1;
        entityData.set(CHANGING, left);
        setHealth(left == 0 ? getMaxHealth() : Math.max(1, BelialRules.healthAt(getMaxHealth(), 1 - left / (float) BelialRules.CHANGE_TICKS)));
        if (!(level() instanceof ServerLevel level)) return;
        Vec3 heart = getBoundingBox().getCenter();
        if (left % 5 == 0) level.sendParticles(left > BelialRules.CHANGE_TICKS / 2 ? ParticleTypes.END_ROD : ParticleTypes.FLAME, heart.x, heart.y, heart.z, 24, .8, 1.4, .8, .08);
        if (left == BelialRules.CHANGE_TICKS / 2) {
            entityData.set(HOLLOW, true);
            playSound(SoundEvents.WITHER_SPAWN, 2.5F, .8F);
            SkillShows.show(level, heart, "nova", 5, 0, 22, FIRE, 0xFFFFFF, .5, getYRot(), false);
            SkillShows.show(level, position(), "hellfire", 3, 3, BelialRules.CHANGE_TICKS / 2, FIRE, EMBER, 0, getYRot(), true);
            SkillShows.show(level, heart, "wings", 3.2, 2, BelialRules.CHANGE_TICKS / 2, FIRE, EMBER, 0, getYRot(), false);
        }
        if (left == 0) {
            playSound(SoundEvents.ENDER_DRAGON_GROWL, 2, .7F);
            SkillShows.show(level, position(), "shockwave", 8, 2, 24, FIRE, EMBER, .4, getYRot(), true);
            rest = HOLLOW_REST;
        }
    }

    // ---- his likenesses ----

    /** His likenesses, as a skill of his own: gathering himself through its wind-up, wings of fire about him; then the five of him. */
    private final class Clones implements BossSkill<Belial> {
        @Override public SkillTiming timing() { return BelialRules.CLONES; }
        @Override public boolean usable(Belial boss, LivingEntity target) { return boss.hollow() && !boss.split() && boss.hasLineOfSight(target); }

        @Override public void begin(Belial boss, LivingEntity target) {
            SkillShows.sound(boss, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 3, .6F);
            if (boss.level() instanceof ServerLevel level)
                SkillShows.show(level, boss.getBoundingBox().getCenter(), "wings", 3, 2, BelialRules.CLONES.windup() + 10, FIRE, EMBER, 0, boss.getYRot(), false);
        }

        @Override public void tick(Belial boss, @Nullable LivingEntity target, int elapsed) {
            boss.setDeltaMovement(boss.getDeltaMovement().scale(.5));
            if (BelialRules.CLONES.strikes(elapsed)) boss.splitNow(target);
        }
    }

    /** The five of him round where he was, himself one of them (which, nobody can tell), the rest his likenesses. */
    void splitNow(@Nullable LivingEntity target) {
        if (!(level() instanceof ServerLevel level) || split()) return;
        Vec3 from = position();
        double turn = random.nextDouble() * Math.PI * 2;
        int himself = random.nextInt(BelialRules.LIKENESSES + 1);
        for (int index = 0; index <= BelialRules.LIKENESSES; index++) {
            double[] at = BelialRules.scatter(index, turn);
            Vec3 place = from.add(at[0], 0, at[1]);
            SkillShows.show(level, place, "nova", 2, 0, 12, FIRE, EMBER, 0, getYRot(), false);
            if (index == himself) {
                moveTo(place.x, place.y, place.z, getYRot(), getXRot());
                continue;
            }
            Belial likeness = ModEntities.BELIAL.get().create(level);
            if (likeness == null) continue;
            likeness.becomeLikeness(this, place, target);
            level.addFreshEntity(likeness);
            likenesses.add(likeness);
        }
        splitTicks = 0;
        found = false;
        rest = 10;
        SkillShows.sound(this, SoundEvents.ILLUSIONER_MIRROR_MOVE, 3, .6F);
    }

    /** One of the five, while his likenesses are about: as they do; and when he is found out, or they have gone unfound long enough, alone again. */
    private void splitOn(@Nullable LivingEntity target) {
        splitTicks++;
        likenesses.removeIf(likeness -> !likeness.isAlive() || likeness.isRemoved());
        if (found || splitTicks >= BelialRules.CLONES_MOST) {
            merge(found);
            return;
        }
        asOneOfFive(target);
    }

    /**
     * Every likeness he left is gone (a streak of fire from each back to him); found out ({@code caught}), he hangs
     * stunned. He does not call them up again for a long while.
     */
    void merge(boolean caught) {
        if (level() instanceof ServerLevel level) {
            Vec3 heart = getBoundingBox().getCenter();
            for (Belial likeness : likenesses) {
                if (!likeness.isAlive()) continue;
                Vec3 there = likeness.getBoundingBox().getCenter();
                for (int step = 0; step <= 12; step++) {
                    Vec3 along = there.lerp(heart, step / 12.0);
                    level.sendParticles(ParticleTypes.FLAME, along.x, along.y, along.z, 1, .05, .05, .05, 0);
                }
                likeness.discard();
            }
        }
        likenesses.clear();
        likenessSkills.interrupt(this, cooldown -> cooldown);
        splitTicks = -1;
        found = false;
        setSkill(Skill.NONE);
        skills.holdOff(slot(Skill.CLONES), BelialRules.CLONES_COOLDOWN);
        if (caught) {
            entityData.set(STUNNED, BelialRules.STUN_TICKS);
            SkillShows.sound(this, SoundEvents.ILLUSIONER_MIRROR_MOVE, 3, .5F);
            SkillShows.sound(this, SoundEvents.ANVIL_LAND, 1.5F, .6F);
        } else {
            rest = HOLLOW_REST;
            SkillShows.sound(this, SoundEvents.ILLUSIONER_MIRROR_MOVE, 3, 1.2F);
        }
    }

    /** As one of the five does, he among them and they: the hollow volley, the strike at the mind; on about the quarry, a little nearer. */
    private void asOneOfFive(@Nullable LivingEntity target) {
        if (rest > 0) rest--;
        if (!likenessSkills.busy() && target != null && rest == 0) {
            Set<Skill> ready = EnumSet.noneOf(Skill.class);
            if (likenessSkills.usable(0, this, target)) ready.add(Skill.VOLLEY);
            if (likenessSkills.usable(1, this, target)) ready.add(Skill.PSYCHIC);
            Skill next = BelialRules.chooseLikeness(distanceTo(target), hasLineOfSight(target), ready, random.nextDouble());
            if (next != Skill.NONE && likenessSkills.begin(next == Skill.VOLLEY ? 0 : 1, this, target)) setSkill(next);
        }
        likenessSkills.tick(this, target, cooldown -> cooldown);
        if (!likenessSkills.busy() && skill() != Skill.NONE) {
            setSkill(Skill.NONE);
            rest = LIKENESS_REST + random.nextInt(20);
        }
        hover(target, likenessSkills.busy());
    }

    /** Made one of his likenesses: the hollow angel too, with a little health of its own, at this place, after this quarry. */
    void becomeLikeness(Belial of, Vec3 place, @Nullable LivingEntity target) {
        master = of;
        entityData.set(LIKENESS, true);
        entityData.set(HOLLOW, true);
        xpReward = 0;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(BelialRules.LIKENESS_HEALTH);
        setHealth((float) BelialRules.LIKENESS_HEALTH);
        moveTo(place.x, place.y, place.z, of.getYRot(), 0);
        rest = 10 + random.nextInt(30);
        if (target != null) setTarget(target);
    }

    /** A likeness: as one of the five does, after his quarry; gone as soon as he is alone again, or gone. */
    private void likenessOn(@Nullable LivingEntity target) {
        if (master == null || !master.isAlive() || !master.split()) {
            if (level() instanceof ServerLevel level) {
                Vec3 heart = getBoundingBox().getCenter();
                level.sendParticles(ParticleTypes.FLAME, heart.x, heart.y, heart.z, 16, .4, .8, .4, .05);
            }
            discard();
            return;
        }
        LivingEntity quarry = master.getTarget() != null && master.getTarget().isAlive() ? master.getTarget() : target;
        if (quarry != null && quarry != getTarget()) setTarget(quarry);
        asOneOfFive(quarry);
    }

    // ---- his stun ----

    /** Stunned: no skill, no flight of his own; he sinks a little, stars wheeling over his head, until it wears off. */
    private void stunOn() {
        int left = stunned() - 1;
        entityData.set(STUNNED, left);
        double over = getY() - groundUnder(position());
        setDeltaMovement(getDeltaMovement().scale(.7).add(0, over > 1.5 ? -.03 : 0, 0));
        if (level() instanceof ServerLevel level && left % 4 == 0)
            for (int star = 0; star < 3; star++) {
                double round = tickCount * .3 + star * Math.PI * 2 / 3;
                level.sendParticles(ParticleTypes.CRIT, getX() + Math.cos(round) * 1.2, getY() + getBbHeight() + .4, getZ() + Math.sin(round) * 1.2, 1, 0, 0, 0, 0);
            }
        if (left == 0) {
            rest = HOLLOW_REST;
            SkillShows.sound(this, SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 2, .5F);
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
        likenessSkills.cease(this);
        setSkill(Skill.NONE);
        if (!likeness()) {
            for (Belial likeness : likenesses) if (likeness.isAlive()) likeness.discard();
            likenesses.clear();
            splitTicks = -1;
        }
        super.die(source);
    }

    @Override public void remove(RemovalReason reason) {
        if (!level().isClientSide && !likeness()) for (Belial likeness : likenesses) if (likeness.isAlive()) likeness.discard();
        super.remove(reason);
    }

    /** Dying, he sinks to the ground for his fire to burst there; a likeness is only gone. */
    @Override protected void tickDeath() {
        if (!onGround() && !likeness()) setDeltaMovement(getDeltaMovement().multiply(.6, 1, .6).add(0, -.05, 0));
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

    /** Whether he is the hollow angel now. */
    public boolean hollow() { return entityData.get(HOLLOW); }

    public Form form() { return hollow() ? Form.HOLLOW : Form.FAIR; }

    /** Ticks left of his change, and of his stun; 0 when he is not changing, or stunned. */
    public int changing() { return entityData.get(CHANGING); }
    public int stunned() { return entityData.get(STUNNED); }

    /** Whether this is only one of his likenesses. */
    public boolean likeness() { return entityData.get(LIKENESS); }

    /** Whether his likenesses are about (for a likeness: whether its master's are). */
    public boolean split() { return likeness() ? master != null && master.split() : splitTicks >= 0; }

    /** On a client: the skill to pose him by, the one he last began; its pose eases back by itself once it is over. */
    public Skill posedSkill() { return posed; }

    /** On a client: ticks since the skill to pose him by began. */
    public float skillTicks(float partial) { return tickCount - skillSeen + partial; }

    /** On a client: how far through his change he is, 0 to 1; 0 when he is not changing. */
    public float changed(float partial) {
        return changing() <= 0 ? 0 : Mth.clamp((tickCount - changeSeen + partial) / BelialRules.CHANGE_TICKS, 0, 1);
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SKILL.equals(key) && skill() != Skill.NONE) {
            posed = skill();
            skillSeen = tickCount;
        }
        if (CHANGING.equals(key) && changing() == BelialRules.CHANGE_TICKS) {
            changeSeen = tickCount;
            posed = Skill.NONE;
        }
    }

    /** His heart: a little over halfway up him. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() * .62, 0); }

    /** Motes of light about the fair angel; sparks off the hollow. */
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide && isAlive() && random.nextInt(4) == 0)
            level().addParticle(hollow() ? ParticleTypes.SMALL_FLAME : ParticleTypes.END_ROD, getRandomX(1.2), getY() + random.nextDouble() * getBbHeight(),
                    getRandomZ(1.2), 0, hollow() ? .02 : -.01, 0);
    }

    @Override public int emblem() { return 6; }
    @Override public BossDeath death() { return likeness() ? LIKENESS_DEATH : DEATH; }

    /** "He speaketh with a Comely Voice": an angel's, deep; the hollow angel's, deeper still and wrong. */
    @Override protected SoundEvent getAmbientSound() { return hollow() ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return hollow() ? SoundEvents.WITHER_HURT : SoundEvents.ALLAY_HURT; }
    @Override protected SoundEvent getDeathSound() { return likeness() ? SoundEvents.ALLAY_HURT : SoundEvents.WITHER_DEATH; }
    @Override public float getVoicePitch() { return hollow() ? .4F : .6F; }
    @Override public int getAmbientSoundInterval() { return 140; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Hollow", hollow());
        tag.put("Skills", skills.save());
        if (stunned() > 0) tag.putInt("Stunned", stunned());
        if (changing() > 0) tag.putInt("Changing", changing());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(HOLLOW, tag.getBoolean("Hollow"));
        skills.load(tag.getCompound("Skills"));
        entityData.set(STUNNED, Math.max(0, tag.getInt("Stunned")));
        entityData.set(CHANGING, Math.max(0, tag.getInt("Changing")));
    }

    /** His skills, and a likeness's, as they stand. */
    public SkillSet<Belial> skills() { return skills; }
    public SkillSet<Belial> likenessSkills() { return likenessSkills; }

    /** The likenesses he left, while they are about. */
    public List<Belial> likenesses() { return List.copyOf(likenesses); }

    /** His skills, for whoever would look at what they did. */
    public SpinVolleySkill volley() { return hollow() ? hollowVolley : volley; }
    public BeamVolleySkill beams() { return beams; }
    public BlinkSkill blink() { return blink; }
    public SummonSkill summon() { return summon; }
    public ChariotSkill chariot() { return chariot; }
    public PsychicSkill psychic() { return psychic; }
}
