package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.common.entity.Baal;

/**
 * Everything Baal's model needs to pose itself, so it can be posed without an entity.
 *
 * @param age ticks, for the motion that never stops
 * @param cycle how far it has walked, in walk cycles
 * @param walk 0 standing to 1 walking
 * @param collapse 0 alive to 1 sunk in death
 * @param headYaw degrees the head is turned from the body, positive to its right
 * @param headPitch degrees the head looks down
 */
public record BaalPose(float age, float cycle, float walk, BaalAttack attack, float attackTicks, float collapse, float headYaw, float headPitch) {
    public static BaalPose standing(float age) { return new BaalPose(age, 0, 0, BaalAttack.NONE, 0, 0, 0, 0); }

    public static BaalPose of(Baal baal, float age, float headYaw, float headPitch) {
        float partial = age - baal.tickCount;
        return new BaalPose(age, baal.cycle(partial), baal.walk(partial), baal.attack(), baal.attackTicks(partial), baal.collapse(partial), headYaw, headPitch);
    }
}
