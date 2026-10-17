package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedBlighthound;
import com.miophas.corruption.core.elite.BlighthoundRules;
import com.miophas.corruption.core.elite.BlighthoundRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted blighthound. It runs at a gallop, its forelegs together and its hind legs together, bounding, its whip of
 * a tail streaming; its split jaw parts to bite, the two halves of it swinging apart; it crouches to spring, then
 * stretches out long in the leap, forelegs reaching and hind legs thrown back; and in its frenzy it worries its quarry,
 * its head shaking and its jaws snapping at every bite.
 */
public final class CorruptedBlighthoundModel extends PosedEliteModel<CorruptedBlighthound> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_blighthound"), "main");
    static final String GEOMETRY = "corrupted_blighthound";

    final ModelPart body, neck, head, jawLeft, jawRight, tail, tailEnd;
    final ModelPart foreLeft, foreRight, foreShinLeft, foreShinRight, hindLeft, hindRight, hindShinLeft, hindShinRight;
    /** How far its jaw is open, as last posed (for the tests to see). */
    float open;

    public CorruptedBlighthoundModel(ModelPart root) {
        super(root, GEOMETRY, BlighthoundRules.SKILLS.size());
        body = need("body");
        neck = need("neck");
        head = need("head");
        jawLeft = need("jaw_left");
        jawRight = need("jaw_right");
        tail = need("tail");
        tailEnd = need("tail_1");
        foreLeft = need("leg_left_0");
        foreRight = need("leg_right_0");
        foreShinLeft = need("shin_left_0");
        foreShinRight = need("shin_right_0");
        hindLeft = need("leg_left_1");
        hindRight = need("leg_right_1");
        hindShinLeft = need("shin_left_1");
        hindShinRight = need("shin_right_1");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float run = Math.min(1, amount), stride = swing * .8F;
        // A gallop: the forelegs together, the hind legs together, half a stride apart; the body bounding.
        float fore = Mth.sin(stride) * .9F * run, hind = Mth.sin(stride + Mth.PI) * .9F * run;
        foreLeft.xRot += fore;
        foreRight.xRot += fore * .85F;
        hindLeft.xRot += hind;
        hindRight.xRot += hind * .85F;
        foreShinLeft.xRot -= Math.max(0, fore) * .6F;
        foreShinRight.xRot -= Math.max(0, fore) * .5F;
        hindShinLeft.xRot += Math.max(0, -hind) * .6F;
        hindShinRight.xRot += Math.max(0, -hind) * .5F;
        body.y -= Math.abs(Mth.cos(stride)) * 1.2F * run;
        body.xRot += Mth.sin(stride) * .08F * run;
        tail.xRot += .5F * run + .1F * Mth.sin(age * .2F);
        tail.yRot += Mth.sin(age * .15F) * .3F * (1 - run);
        tailEnd.yRot += Mth.sin(age * .15F - .8F) * .35F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .5F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;
        float gape = .05F + .05F * Mth.sin(age * .3F);
        gape = Math.max(gape, strike(bite));

        // Its spring: crouched through the wind-up, then stretched out long.
        float rend = on[Skill.REND.ordinal()];
        int crouch = BlighthoundRules.REND.timing().windup();
        float down = posed == Skill.REND.ordinal() && since < crouch ? gathering(since, crouch) : 0, leap = rend * (1 - down);
        body.y += 2.5F * down * rend;
        neck.xRot -= .3F * down * rend;
        foreLeft.xRot -= 1.1F * leap;
        foreRight.xRot -= 1.1F * leap;
        hindLeft.xRot += 1.1F * leap;
        hindRight.xRot += 1.1F * leap;
        gape = Math.max(gape, leap);

        // Its frenzy: its head shaking, its jaws snapping at every bite.
        float frenzy = on[Skill.FRENZY.ordinal()];
        head.zRot += Mth.sin(age * 2.2F) * .25F * frenzy;
        neck.yRot += Mth.sin(age * 1.6F) * .2F * frenzy;
        if (posed == Skill.FRENZY.ordinal())
            for (int snap = 0; snap * BlighthoundRules.FRENZY_EVERY < BlighthoundRules.FRENZY.ticks(); snap++)
                gape = Math.max(gape, frenzy * pulse(since, BlighthoundRules.FRENZY.windup() + snap * BlighthoundRules.FRENZY_EVERY - 1, 4));

        open = gape;
        jawLeft.yRot += .45F * gape;
        jawRight.yRot -= .45F * gape;
        jawLeft.xRot += .35F * gape;
        jawRight.xRot += .35F * gape;
    }
}
