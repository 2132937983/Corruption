package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedHivemother;
import com.miophas.corruption.core.elite.HivemotherRules;
import com.miophas.corruption.core.elite.HivemotherRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted hivemother. Its four wings buzz, the hind pair a little after the fore, its body bobbing against them and
 * its abdomen swaying; it jabs its abdomen under it at what it stings; it sweeps its wings back to swoop; it curls its
 * abdomen under and forward to loose its stingers, jabbing at each; and its abdomen throbs as it calls up its swarm, its
 * wings beating the harder.
 */
public final class CorruptedHivemotherModel extends PosedEliteModel<CorruptedHivemother> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_hivemother"), "main");
    static final String GEOMETRY = "corrupted_hivemother";

    final ModelPart body, head, tail, foreLeft, foreRight, hindLeft, hindRight;

    public CorruptedHivemotherModel(ModelPart root) {
        super(root, GEOMETRY, HivemotherRules.SKILLS.size());
        body = need("body");
        head = need("head");
        tail = need("tail");
        foreLeft = need("wing_left_0");
        foreRight = need("wing_right_0");
        hindLeft = need("wing_left_1");
        hindRight = need("wing_right_1");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float swoop = on[Skill.SWOOP.ordinal()], stingers = on[Skill.STINGERS.ordinal()], swarm = on[Skill.SWARM.ordinal()];
        float phase = age * (1.6F + .6F * swarm), beat = (.5F + .2F * Math.min(1, amount) + .15F * swarm) * (1 - .6F * swoop);
        beat(foreLeft, foreRight, phase, beat);
        beat(hindLeft, hindRight, phase - .8F, beat * .8F);
        body.y -= Mth.cos(phase) * .3F + Mth.sin(age * .07F) * .6F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .5F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;
        tail.yRot += Mth.sin(age * .09F) * .12F;
        tail.xRot += Mth.sin(age * .11F) * .06F;

        // Its sting: the abdomen jabbed under it.
        tail.xRot -= .9F * strike(bite);

        // Its swoop: wings swept back, head down.
        foreLeft.yRot += .8F * swoop;
        foreRight.yRot -= .8F * swoop;
        hindLeft.yRot += .5F * swoop;
        hindRight.yRot -= .5F * swoop;
        head.xRot += .3F * swoop;

        // Its stingers: the abdomen curled under and forward, jabbing at each.
        tail.xRot -= 1.1F * stingers;
        if (posed == Skill.STINGERS.ordinal())
            for (int shot = 0; shot < HivemotherRules.STINGERS.shots(); shot++)
                tail.xRot -= .3F * stingers * pulse(since, HivemotherRules.STINGERS.timing().windup() + shot * HivemotherRules.STINGERS.every(), 3);

        // Its swarm: the abdomen throbbing, lowered.
        float throb = 1 + .15F * swarm * (.5F + .5F * Mth.sin(age * .9F));
        tail.xRot -= .3F * swarm;
        tail.xScale = tail.yScale = throb;
    }
}
