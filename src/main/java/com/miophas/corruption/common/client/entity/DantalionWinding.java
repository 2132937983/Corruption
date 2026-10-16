package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.Dantalion;
import com.miophas.corruption.core.demon.DantalionRules.Skill;
import net.minecraft.util.Mth;

/**
 * One Dantalion's turning things (the book's leaves, the mirror's shards and spiral), wound on frame by frame at the
 * speed his pose asks for. Posed as a speed times the time, they would jump all at once whenever the speed changed:
 * by thousands of turns, the time being his age.
 */
final class DantalionWinding {
    /** The most ticks one frame winds on: a Dantalion long out of sight takes up where he was. */
    private static final float MOST = 4;
    private float time = Float.NaN;
    /** How far the leaves have turned, in leafing beats; how far round the shards have circled, and the spiral turned, in radians. */
    float leaves, shards, spiral;

    /** Winds them on to this moment of this Dantalion. */
    void wind(Dantalion entity, float partial) {
        float now = entity.tickCount + partial;
        if (Float.isNaN(time)) {
            // Each begins from a place of his own, so that no two turn together.
            leaves = entity.getId() * 7.3F * DantalionBookModel.LEAF_RATE % (2 * DantalionBookModel.LEAVES);
            shards = entity.getId() * 5.9F * DantalionMirrorModel.ORBIT % Mth.TWO_PI;
            time = now;
            return;
        }
        float step = Mth.clamp(now - time, 0, MOST);
        time = now;
        if (step <= 0) return;
        Skill skill = entity.posedSkill();
        float ticks = entity.skillTicks(partial), holding = entity.holding(partial);
        boolean hard = holding > 0 && entity.heldHard() || skill == Skill.GREATER_MIND_CONTROL;
        leaves = (leaves + step * DantalionBookModel.leafRate(skill, ticks, DantalionBookModel.dying(entity, partial))) % (2 * DantalionBookModel.LEAVES);
        shards = (shards + step * DantalionMirrorModel.shardRate(skill, ticks, holding, hard)) % Mth.TWO_PI;
        spiral = (spiral + step * DantalionMirrorModel.spiralRate(hard)) % Mth.TWO_PI;
    }
}
