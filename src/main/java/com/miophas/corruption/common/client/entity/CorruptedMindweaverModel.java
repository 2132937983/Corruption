package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedMindweaver;
import com.miophas.corruption.core.elite.MindweaverRules;
import com.miophas.corruption.core.elite.MindweaverRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted mindweaver. It shuffles on its four stubs, its brain throbbing slowly, its nerves swaying behind it, its
 * thin arms ever weaving before it; it slaps with an arm; it reaches out to its quarry with both arms as it draws its
 * thread, its brain swelling at every pull; and it throws its head back and its arms wide to wail, its brain swelling
 * through the wind-up.
 */
public final class CorruptedMindweaverModel extends PosedEliteModel<CorruptedMindweaver> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_mindweaver"), "main");
    static final String GEOMETRY = "corrupted_mindweaver";
    static final int NERVES = 3;

    final ModelPart body, brain, head, armLeft, armRight, forearmLeft, forearmRight, foreLeft, foreRight, hindLeft, hindRight;
    final ModelPart[] nerves = new ModelPart[NERVES];
    /** How swollen its brain is, as last posed (for the tests to see). */
    float swollen;

    public CorruptedMindweaverModel(ModelPart root) {
        super(root, GEOMETRY, MindweaverRules.SKILLS.size());
        body = need("body");
        brain = need("brain");
        head = need("head");
        armLeft = need("arm_left");
        armRight = need("arm_right");
        forearmLeft = need("forearm_left");
        forearmRight = need("forearm_right");
        foreLeft = need("leg_left_0");
        foreRight = need("leg_right_0");
        hindLeft = need("leg_left_1");
        hindRight = need("leg_right_1");
        for (int index = 0; index < NERVES; index++) nerves[index] = need("nerve_" + index);
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        trot(foreLeft, foreRight, hindLeft, hindRight, swing, amount, .5F);
        body.zRot += Mth.cos(swing * .6662F) * .05F * Math.min(1, amount);
        head.yRot += yaw * Mth.DEG_TO_RAD * .3F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .3F;
        wave(nerves, age * .12F, .12F, 1.1F, true);
        float swell = 1 + .03F * Mth.sin(age * .09F);

        // Its fingers weave: the arms circling a little out of time with each other.
        armLeft.yRot += .25F * Mth.sin(age * .17F);
        armRight.yRot -= .25F * Mth.sin(age * .17F + 1.3F);
        forearmLeft.xRot += .2F * Mth.cos(age * .21F);
        forearmRight.xRot += .2F * Mth.cos(age * .21F + 1.7F);

        // Its slap.
        float blow = strike(bite);
        armRight.xRot -= 1.1F * blow;
        armRight.zRot -= .3F * blow;

        // Its thread: both arms reaching out to its quarry, its brain swelling at every pull.
        float thread = on[Skill.THREAD.ordinal()];
        armLeft.xRot -= .6F * thread;
        armRight.xRot -= .6F * thread;
        forearmLeft.xRot -= .4F * thread;
        forearmRight.xRot -= .4F * thread;
        head.xRot -= .2F * thread;
        if (posed == Skill.THREAD.ordinal() && since >= MindweaverRules.THREAD.timing().windup()) {
            float pulled = (since - MindweaverRules.THREAD.timing().windup()) % MindweaverRules.THREAD.interval();
            swell += thread * .1F * pulse(pulled, 0, 5);
        }

        // Its wail: its brain swelling through the wind-up, then its head thrown back and its arms flung wide.
        float wail = on[Skill.WAIL.ordinal()];
        int up = MindweaverRules.WAIL.timing().windup();
        if (posed == Skill.WAIL.ordinal()) swell += wail * (since < up ? .15F * gathering(since, up) : .15F * Math.max(0, 1 - (since - up) / 6));
        head.xRot -= .6F * wail;
        armLeft.zRot += .9F * wail;
        armRight.zRot -= .9F * wail;
        armLeft.xRot -= .3F * wail;
        armRight.xRot -= .3F * wail;

        swollen = swell;
        brain.xScale = brain.yScale = brain.zScale = swell;
    }
}
