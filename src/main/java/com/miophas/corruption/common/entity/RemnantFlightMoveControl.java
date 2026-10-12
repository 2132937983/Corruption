package com.miophas.corruption.common.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/** Native Vex-style acceleration; the actual remnant owns movement and attack attribution. */
final class RemnantFlightMoveControl extends MoveControl {
    private final CorruptedRemnant remnant;
    RemnantFlightMoveControl(CorruptedRemnant remnant) { super(remnant); this.remnant = remnant; }
    @Override public void tick() {
        if (operation != Operation.MOVE_TO) return;
        Vec3 delta = new Vec3(wantedX - remnant.getX(), wantedY - remnant.getY(), wantedZ - remnant.getZ());
        double distance = delta.length();
        if (distance < .25) { operation = Operation.WAIT; remnant.setDeltaMovement(remnant.getDeltaMovement().scale(.5)); }
        else remnant.setDeltaMovement(remnant.getDeltaMovement().add(delta.scale(speedModifier * .05 / distance)));
        Vec3 facing = remnant.getTarget() == null ? remnant.getDeltaMovement() : remnant.getTarget().position().subtract(remnant.position());
        remnant.setYRot(-(float) Mth.atan2(facing.x, facing.z) * Mth.RAD_TO_DEG); remnant.yBodyRot = remnant.getYRot();
    }
}
