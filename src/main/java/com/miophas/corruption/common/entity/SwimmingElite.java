package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.api.elite.CorruptionElite;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

/**
 * An elite of the sea. In the water it darts at its quarry terribly fast ({@link #dart}), and strikes it close in (its
 * basic blow); at a quarry ashore it comes to the water's edge and no further, and surfaces there, its mouth just over the
 * water, the better to reach over the bank. With nothing to hunt it drifts slowly about the water, or makes for it if it
 * is out of it, where it can only crawl. It never drowns, and the water does not push it.
 */
public abstract class SwimmingElite<E extends SwimmingElite<E>> extends CorruptionElite<E> implements SpitCaster {

    private int bitten;

    protected SwimmingElite(EntityType<? extends E> type, Level level) {
        super(type, level);
        moveControl = new SmoothSwimmingMoveControl(this, 85, 10, wander(), crawl(), true);
        lookControl = new SmoothSwimmingLookControl(this, 10);
        setPathfindingMalus(PathType.WATER, 0);
    }

    /**
     * How much of its speed it swims with as it drifts about, and crawls with on land; how fast it darts at its quarry in
     * the water (blocks a tick) and how much faster it gets each tick; how near its middle must be to its quarry's to
     * strike, and how often it strikes. Asked as it is made: constants.
     */
    protected abstract float wander();
    protected abstract float crawl();
    protected abstract double dart();
    protected abstract double thrust();
    protected abstract double strikeReach();
    protected abstract int strikeEvery();

    /** Its mouth, in its own frame from its feet ({right, up, ahead}, in blocks): what it shoots leaves it there. */
    protected abstract double[] mouth();

    @Override protected PathNavigation createNavigation(Level level) { return new AmphibiousPathNavigation(this, level); }

    /** With nothing to hunt it makes for the water if it is out of it, and drifts about in it. */
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new TryFindWaterGoal(this) {
            @Override public boolean canUse() { return getTarget() == null && super.canUse(); }
        });
        goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1, 40) {
            @Override public boolean canUse() { return getTarget() == null && isInWater() && super.canUse(); }
            @Override public boolean canContinueToUse() { return getTarget() == null && super.canContinueToUse(); }
        });
        huntTheUncorrupted(1);
    }

    /**
     * Hunting: in the water it darts at its quarry, but stops at the water's edge for one ashore, and surfaces there, its
     * mouth just over the water; out of it, it crawls at it; near enough, it strikes.
     */
    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        if (bitten > 0) bitten--;
        if (target == null) return;
        if (busy) {
            getNavigation().stop();
            return;
        }
        Vec3 heart = getBoundingBox().getCenter(), there = target.getBoundingBox().getCenter();
        if (heart.distanceTo(there) <= strikeReach()) {
            getNavigation().stop();
            if (isInWater()) setDeltaMovement(getDeltaMovement().scale(.8));
            if (bitten == 0) {
                bitten = strikeEvery();
                doHurtTarget(target);
            }
            return;
        }
        if (!isInWater()) {
            getNavigation().moveTo(target, 1);
            return;
        }
        getNavigation().stop();
        Vec3 way = there.subtract(heart).normalize();
        Vec3 ahead = heart.add(way.multiply(1, 0, 1).scale(getBbWidth() / 2 + .8));
        boolean ashore = !target.isInWater() && !level().getFluidState(BlockPos.containing(ahead)).is(FluidTags.WATER);
        if (ashore) surfaceAt(way);
        else dart(way, way);
    }

    /**
     * At the edge for a quarry ashore, it surfaces, the better to reach over the bank: it rises (or sinks) until its mouth
     * is just over the water and holds there, coming no nearer, facing its quarry.
     */
    protected void surfaceAt(Vec3 look) {
        double low = surface() + com.miophas.corruption.core.elite.EliteRules.SURFACED - (getY() + mouth()[1]);
        Vec3 moving = getDeltaMovement();
        setDeltaMovement(moving.x * .6, Mth.clamp(low * .3, -.1, .12), moving.z * .6);
        turn(look);
    }

    /** The top of the water it is in: the top of the water in the last block of it over its feet (a few blocks up at the most). */
    protected double surface() {
        BlockPos.MutableBlockPos at = blockPosition().mutable();
        double top = getY();
        for (int up = 0; up < 6 && level().getFluidState(at).is(FluidTags.WATER); up++) {
            top = at.getY() + level().getFluidState(at).getHeight(level(), at);
            at.move(0, 1, 0);
        }
        return top;
    }

    /** It darts along a way, turned to face where it looks. */
    protected void dart(Vec3 way, Vec3 look) {
        Vec3 moving = getDeltaMovement().add(way.scale(thrust()));
        if (moving.length() > dart()) moving = moving.normalize().scale(dart());
        setDeltaMovement(moving);
        turn(look);
    }

    /** Turned, body and head, to face where it looks, tipped up or down as that is. */
    protected void turn(Vec3 look) {
        float yaw = (float) (Mth.atan2(look.z, look.x) * Mth.RAD_TO_DEG) - 90;
        setYRot(Mth.approachDegrees(getYRot(), yaw, 15));
        yBodyRot = getYRot();
        yHeadRot = getYRot();
        setXRot((float) Mth.clamp(-Mth.atan2(look.y, Math.max(.05, look.horizontalDistance())) * Mth.RAD_TO_DEG, -60, 60));
    }

    /** In the water it swims, as the game's swimmers do; out of it, it crawls. */
    @Override public void travel(Vec3 input) {
        if (isControlledByLocalInstance() && isInWater()) {
            moveRelative(getSpeed(), input);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(.9));
            calculateEntityAnimation(false);
        } else super.travel(input);
    }

    @Override public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) { return false; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public float getWalkTargetValue(BlockPos pos, LevelReader level) { return level.getFluidState(pos).is(FluidTags.WATER) ? 10 : 0; }
    @Override public int getMaxHeadXRot() { return 1; }
    @Override public int getMaxHeadYRot() { return 1; }

    /** Its mouth, in the world. */
    @Override public Vec3 spitFrom(int index) { return inFrame(mouth()); }
}
