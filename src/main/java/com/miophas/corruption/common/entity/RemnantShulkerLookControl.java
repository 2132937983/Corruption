package com.miophas.corruption.common.entity;

import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;
import org.joml.Vector3f;

/** Aim within the attached shell's plane, including walls and ceilings. */
final class RemnantShulkerLookControl extends LookControl {
    private final CorruptedRemnant actor;
    RemnantShulkerLookControl(CorruptedRemnant actor) { super(actor); this.actor = actor; }
    @Override protected void clampHeadRotationToBody() {}
    @Override protected Optional<Float> getYRotD() {
        var direction = actor.shulkerAttachFace().getOpposite();
        var forward = direction.getRotation().transform(new Vector3f(0, 0, 1));
        var normal = direction.getNormal();
        var side = new Vector3f(normal.getX(), normal.getY(), normal.getZ()).cross(forward);
        var delta = new Vector3f((float)(wantedX - actor.getX()), (float)(wantedY - actor.getEyeY()), (float)(wantedZ - actor.getZ()));
        float lateral = side.dot(delta), along = forward.dot(delta);
        return Math.abs(lateral) < 1.0E-5F && Math.abs(along) < 1.0E-5F ? Optional.empty()
                : Optional.of((float)(Mth.atan2(-lateral, along) * Mth.RAD_TO_DEG));
    }
    @Override protected Optional<Float> getXRotD() { return Optional.of(0F); }
}
