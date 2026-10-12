package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantAnimationRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Lets other mods make a remnant play its source mob's attack animation instead of a plain arm swing. */
public final class RemnantAnimationApi {
    /** Entity event a remnant broadcasts every time it attacks. */
    public static final byte ATTACK_EVENT = RemnantAnimationRules.ATTACK_EVENT;

    /** Unregistered non-vanilla sources already replay {@link #ATTACK_EVENT} on their proxy; register to customise that. */
    public static void register(ResourceLocation source, RemnantAnimationDriver driver) { RemnantAnimationRules.register(source.toString(), driver); }
    /** Turns attack animation replay off for a source, including the default for non-vanilla ones. */
    public static void disable(ResourceLocation source) { RemnantAnimationRules.disable(source.toString()); }
    public static void unregister(ResourceLocation source) { RemnantAnimationRules.unregister(source.toString()); }
    public static RemnantAnimationDriver driver(String source) { return RemnantAnimationRules.driver(source); }

    /**
     * Runs one client tick on a source proxy parked below the world, so the source's animation counters advance
     * without pushing players, moving, or changing the pose the renderer synchronised from the remnant.
     */
    public static void tickSource(LivingEntity source) {
        double x = source.getX(), y = source.getY(), z = source.getZ(), xo = source.xo, yo = source.yo, zo = source.zo;
        double xOld = source.xOld, yOld = source.yOld, zOld = source.zOld;
        Vec3 motion = source.getDeltaMovement();
        float yRot = source.getYRot(), yRotO = source.yRotO, xRot = source.getXRot(), xRotO = source.xRotO;
        float body = source.yBodyRot, bodyO = source.yBodyRotO, head = source.yHeadRot, headO = source.yHeadRotO;
        float walk = source.walkAnimation.speed(), attack = source.attackAnim, attackO = source.oAttackAnim;
        boolean ground = source.onGround(), swinging = source.swinging;
        int swingTime = source.swingTime, hurt = source.hurtTime, death = source.deathTime;
        var arm = source.swingingArm;
        double parked = source.level().getMinBuildHeight() - 16;
        source.setPos(x, parked, z);
        source.xo = source.xOld = x; source.yo = source.yOld = parked; source.zo = source.zOld = z;
        try { source.tick(); }
        finally {
            source.setPos(x, y, z);
            source.xo = xo; source.yo = yo; source.zo = zo; source.xOld = xOld; source.yOld = yOld; source.zOld = zOld;
            source.setDeltaMovement(motion); source.setOnGround(ground);
            source.setYRot(yRot); source.yRotO = yRotO; source.setXRot(xRot); source.xRotO = xRotO;
            source.yBodyRot = body; source.yBodyRotO = bodyO; source.yHeadRot = head; source.yHeadRotO = headO;
            source.walkAnimation.setSpeed(walk);
            source.attackAnim = attack; source.oAttackAnim = attackO; source.swinging = swinging;
            source.swingTime = swingTime; source.swingingArm = arm; source.hurtTime = hurt; source.deathTime = death;
        }
    }

    private RemnantAnimationApi() {}
}
