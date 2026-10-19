package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.core.demon.skill.DebrisRules;
import com.miophas.corruption.core.demon.skill.DiveRules;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * The ground torn up as by a blast: blocks flung away from a place, out and up, to fall back where they may (as falling
 * blocks do, set down again where they land, or dropped as an item where they cannot be). Only a block that can be
 * broken at all is torn up (never bedrock and the like), and never a liquid, a block that holds anything (a chest), half
 * of a door or a bed, nor what grows in the open (grass, flowers); and nothing at all where the game does not let
 * creatures break blocks (the mobGriefing rule).
 */
public final class Debris {
    /** Tears up the ground about this place, as the rules say: within their reach, as many as they let, the nearest first; how many were torn up. */
    public static int tear(ServerLevel level, Entity by, Vec3 at, DiveRules rules) {
        return tear(level, by, at, rules.debrisRadius(), rules.debrisMost(), rules);
    }

    /** As {@link #tear(ServerLevel, Entity, Vec3, DiveRules)}, within this reach and as many as this. */
    public static int tear(ServerLevel level, Entity by, Vec3 at, double radius, int most, DiveRules rules) {
        return tear(level, by, at, radius, most, rules.debris());
    }

    public static int tear(ServerLevel level, Entity by, Vec3 at, double radius, int most, DebrisRules rules) {
        if (most <= 0 || radius <= 0 || !EventHooks.canEntityGrief(level, by)) return 0;
        int reach = (int) Math.ceil(radius);
        BlockPos middle = BlockPos.containing(at);
        List<BlockPos> places = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-reach, -reach, -reach), middle.offset(reach, reach, reach)))
            if (Vec3.atCenterOf(pos).distanceToSqr(at) <= radius * radius) places.add(pos.immutable());
        places.sort(Comparator.comparingDouble(pos -> Vec3.atCenterOf(pos).distanceToSqr(at)));
        int torn = 0;
        for (BlockPos pos : places) {
            if (torn >= most) break;
            if (!level.isLoaded(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (!tearable(level, pos, state)) continue;
            FallingBlockEntity flung = FallingBlockEntity.fall(level, pos, state);
            Vec3 off = Vec3.atCenterOf(pos).subtract(at);
            double[] fling = rules.fling(off.x, off.y, off.z);
            double scatter = .08;
            flung.setDeltaMovement(fling[0] + (level.random.nextDouble() - .5) * scatter, fling[1], fling[2] + (level.random.nextDouble() - .5) * scatter);
            flung.hurtMarked = true;
            torn++;
        }
        return torn;
    }

    /**
     * Flings one block away at this speed, to fall back where it may, if it is {@link #tearable}; whether it went. The
     * caller asks the world's leave first (the mobGriefing rule).
     */
    public static boolean fling(ServerLevel level, BlockPos pos, Vec3 velocity) {
        if (!level.isLoaded(pos)) return false;
        BlockState state = level.getBlockState(pos);
        if (!tearable(level, pos, state)) return false;
        FallingBlockEntity flung = FallingBlockEntity.fall(level, pos, state);
        flung.setDeltaMovement(velocity);
        flung.hurtMarked = true;
        return true;
    }

    /** Whether a block is torn up: one that can be broken at all, that holds nothing, is no liquid, half of nothing, and no plant in the open. */
    public static boolean tearable(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity() || state.canBeReplaced()) return false;
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) || state.hasProperty(BlockStateProperties.BED_PART)) return false;
        return DebrisRules.tears(state.getDestroySpeed(level, pos));
    }

    private Debris() {}
}
