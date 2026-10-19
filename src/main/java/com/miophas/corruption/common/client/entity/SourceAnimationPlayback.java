package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.RemnantAnimationApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Advances a parked source proxy while preserving the remnant renderer's pose. */
public final class SourceAnimationPlayback implements RemnantAnimationApi.Playback {
    @Override public void start(LivingEntity source, byte event) { source.handleEntityEvent(event); }
    @Override public void tick(LivingEntity source) {
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
}
