package com.miophas.corruption.common.entity;

import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.util.Mth;

/** Ground locomotion with horizontal surface steering, without repeated water jumps or diving. */
final class RemnantSurfaceMoveControl extends MoveControl {
    RemnantSurfaceMoveControl(CorruptedRemnant mob){super(mob);}
    @Override public void tick(){
        if(!mob.isInWater()){super.tick();return;}
        mob.setYya(0);mob.setXxa(0);
        if(operation!=Operation.MOVE_TO||mob.getNavigation().isDone()){mob.setSpeed(0);mob.setZza(0);return;}
        double dx=wantedX-mob.getX(),dz=wantedZ-mob.getZ();
        if(dx*dx+dz*dz<.0025){mob.setSpeed(0);mob.setZza(0);return;}
        mob.setYRot(rotlerp(mob.getYRot(),(float)(Mth.atan2(dz,dx)*180/Math.PI)-90,30));
        float speed=(float)(speedModifier*mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        mob.setSpeed(speed);mob.setZza(speed);
    }
}
