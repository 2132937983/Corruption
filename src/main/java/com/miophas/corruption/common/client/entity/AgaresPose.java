package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.common.entity.Agares;

/**
 * Everything Agares's model needs to pose itself, so it can be posed without an entity.
 *
 * @param age ticks, for the motion that never stops
 * @param cycle how far it has walked, in walk cycles
 * @param walk 0 standing to 1 walking
 * @param collapse 0 alive to 1 sunk in death
 * @param headYaw degrees the head is turned from the body, positive to its right
 * @param headPitch degrees the head looks down
 */
public record AgaresPose(float age, float cycle, float walk, AgaresAttack attack, float attackTicks, float collapse, float headYaw, float headPitch) {
    public static AgaresPose standing(float age) { return new AgaresPose(age, 0, 0, AgaresAttack.NONE, 0, 0, 0, 0); }

    public static AgaresPose of(Agares agares, float age, float headYaw, float headPitch) {
        float partial = age - agares.tickCount;
        return new AgaresPose(age, agares.cycle(partial), agares.walk(partial), agares.attack(), agares.attackTicks(partial), agares.collapse(partial), headYaw, headPitch);
    }
}
