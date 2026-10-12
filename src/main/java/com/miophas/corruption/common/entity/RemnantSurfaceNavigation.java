package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.RemnantAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

/** Ordinary ground paths; submerged prey are approached at the surface rather than by diving. */
public final class RemnantSurfaceNavigation extends GroundPathNavigation {
    private final CorruptedRemnant remnant;
    private BlockPos climbingTarget;
    public RemnantSurfaceNavigation(CorruptedRemnant remnant,Level level){super(remnant,level);this.remnant=remnant;setCanFloat(true);}
    @Override public Path createPath(Entity target,int accuracy){
        if(remnant.hasAbility(RemnantAbility.WALL_CLIMB))climbingTarget=target.blockPosition();
        var pos=target.blockPosition();
        if(remnant.isInWater()&&level.getFluidState(pos).is(FluidTags.WATER))
            pos=new BlockPos(pos.getX(),Mth.floor(remnant.getY()+remnant.getFluidHeight(FluidTags.WATER)+.001),pos.getZ());
        return super.createPath(pos,accuracy);
    }
    @Override public boolean moveTo(Entity target,double speed){
        if(super.moveTo(target,speed))return true;
        if(climbingTarget==null||remnant.isInWater())return false;
        speedModifier=speed;return true;
    }
    @Override public void tick(){
        super.tick();if(!isDone()||climbingTarget==null||remnant.isInWater()||remnant.getTarget()==null)return;
        if(climbingTarget.closerToCenterThan(remnant.position(),Math.max(remnant.getBbWidth(),1)))climbingTarget=null;
        else remnant.getMoveControl().setWantedPosition(climbingTarget.getX(),climbingTarget.getY(),climbingTarget.getZ(),speedModifier);
    }
    @Override public void stop(){climbingTarget=null;super.stop();}
}
