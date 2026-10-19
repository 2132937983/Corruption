package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.api.elite.CorruptionElite;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * An elite that flies: no gravity, the air slowing it. It circles its quarry at the distance it keeps ({@link #keepAt}),
 * high over the ground ({@link #hoverLow} to {@link #hoverHigh}), now one way and now the other, always facing it; held
 * still while it looses or screams; with no quarry it wheels slowly over where it was. A skill that moves it itself (a
 * swoop, a dive) takes over its flight ({@link #drivenBySkill}). One that does not swoop strikes what comes within its
 * reach instead ({@link #strikeReach}). Dead, it falls out of the sky.
 */
public abstract class FlyingElite<E extends FlyingElite<E>> extends CorruptionElite<E> implements SpitCaster {
    /** How fast it gathers speed towards where it would be, and how far it turns to face it, a tick. */
    private static final double THRUST = .07;
    private static final float TURN = 12;

    private int wander, struck;
    private double hoverAt;
    @Nullable private Vec3 roost;

    protected FlyingElite(EntityType<? extends E> type, Level level) {
        super(type, level);
        setNoGravity(true);
        hoverAt = hoverLow() + 1;
    }

    /** How far from its quarry it circles, and how high over the ground. Asked as it is made: constants. */
    protected abstract double keepAt();
    protected abstract double hoverLow();
    protected abstract double hoverHigh();

    /** Its mouth (its beak, its great eye), in its own frame from its feet ({right, up, ahead}, in blocks). */
    protected abstract double[] mouth();

    /** Whether the skill it is about moves it itself, so that its own flight lets go. */
    protected boolean drivenBySkill() { return false; }

    /** How near it strikes what comes to it, while no skill runs (none, for one that swoops instead), and how often. */
    protected double strikeReach() { return 0; }
    protected int strikeEvery() { return 20; }

    @Override protected void registerGoals() { huntTheUncorrupted(1); }

    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        if (struck > 0) struck--;
        if (target != null && !busy && strikeReach() > 0 && struck == 0
                && getBoundingBox().getCenter().distanceTo(target.getBoundingBox().getCenter()) <= strikeReach()) {
            struck = strikeEvery();
            doHurtTarget(target);
        }
        if (!drivenBySkill()) fly(target, busy);
    }

    /**
     * It circles its quarry at the distance it keeps, high over the ground, now one way and now the other, always facing
     * it; held still, it hangs where it is. With no quarry it wheels slowly over where it was first.
     */
    protected void fly(@Nullable LivingEntity target, boolean still) {
        if (--wander <= 0) {
            wander = 50 + random.nextInt(70);
            hoverAt = Mth.nextDouble(random, hoverLow(), hoverHigh());
        }
        Vec3 want;
        if (target == null) {
            if (roost == null) roost = position();
            Vec3 round = position().subtract(roost).multiply(1, 0, 1);
            if (round.lengthSqr() < 1) round = new Vec3(1, 0, 0);
            Vec3 at = roost.add(round.normalize().scale(5).yRot(.06F));
            want = new Vec3(at.x, groundUnder(at) + hoverAt, at.z);
            faceAlong(getDeltaMovement());
        } else {
            roost = null;
            Vec3 from = target.position(), off = position().subtract(from).multiply(1, 0, 1);
            if (off.lengthSqr() < 1e-4) off = new Vec3(1, 0, 0);
            Vec3 turned = still ? off : off.yRot((float) Math.toRadians(2.2 * (wander % 140 < 70 ? 1 : -1)));
            Vec3 at = from.add(turned.normalize().scale(keepAt()));
            want = new Vec3(at.x, Math.max(groundUnder(at) + hoverAt, target.getY() + hoverLow()), at.z);
            face(target.getBoundingBox().getCenter());
        }
        Vec3 to = want.subtract(position());
        double speed = (still ? .3 : 1) * getAttributeValue(Attributes.FLYING_SPEED);
        Vec3 push = to.lengthSqr() > 1e-4 ? to.normalize().scale(Math.min(THRUST, to.length() * .06)) : Vec3.ZERO;
        Vec3 moving = getDeltaMovement().add(push);
        if (moving.length() > speed) moving = moving.normalize().scale(speed);
        setDeltaMovement(moving);
        setXRot((float) Mth.clamp(-Mth.atan2(moving.y, Math.max(.05, moving.horizontalDistance())) * Mth.RAD_TO_DEG, -20, 20));
    }

    /** Turned, body and head, towards a place. */
    protected void face(Vec3 at) {
        Vec3 to = at.subtract(getBoundingBox().getCenter());
        turnTo((float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90);
    }

    private void faceAlong(Vec3 moving) {
        if (moving.horizontalDistanceSqr() > 1e-4) turnTo((float) (Mth.atan2(moving.z, moving.x) * Mth.RAD_TO_DEG) - 90);
    }

    private void turnTo(float yaw) {
        setYRot(Mth.approachDegrees(getYRot(), yaw, TURN));
        yBodyRot = getYRot();
        yHeadRot = getYRot();
    }

    /** The top of the ground under a place: the highest block there, or under a roof the floor under it. */
    protected double groundUnder(Vec3 at) {
        int x = Mth.floor(at.x), z = Mth.floor(at.z);
        if (!level().hasChunk(x >> 4, z >> 4)) return at.y - hoverAt;
        double top = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return top > at.y + 2 ? at.y - hoverAt : top;
    }

    /** It flies: no gravity, the air slowing it. */
    @Override public void travel(Vec3 input) {
        if (isControlledByLocalInstance()) {
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(.9));
        }
        calculateEntityAnimation(false);
    }

    @Override public boolean onClimbable() { return false; }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }

    /** Dead, it falls out of the sky. */
    @Override protected void tickDeath() {
        if (!onGround()) setDeltaMovement(getDeltaMovement().multiply(.8, 1, .8).add(0, -.08, 0));
        super.tickDeath();
    }

    /** Its mouth, in the world. */
    @Override public Vec3 spitFrom(int index) { return inFrame(mouth()); }
}
