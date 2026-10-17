package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedMaw;
import com.miophas.corruption.core.elite.MawRules;
import com.miophas.corruption.core.elite.MawRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted maw. In the water its body sways from side to side, a wave running down it to its tail (the faster the
 * faster it swims), its fins beating and its lure bobbing; out of it, it flops slowly, its fins limp. Its four lips draw
 * apart to bite; they gape wide and tremble as it sucks in the water about it, its tail thrashing; they open as it
 * lunges, its fins swept back; and they purse through its spout's wind-up and snap open as it spouts.
 */
public final class CorruptedMawModel extends PosedEliteModel<CorruptedMaw> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_maw"), "main");
    static final String GEOMETRY = "corrupted_maw";

    final ModelPart head, lure, lipTop, lipBottom, lipLeft, lipRight, finLeft, finRight;
    final ModelPart[] chain;
    /** How wide its lips are open, as last posed (for the tests to see). */
    float open;

    public CorruptedMawModel(ModelPart root) {
        super(root, GEOMETRY, MawRules.SKILLS.size());
        head = need("head");
        lure = need("lure");
        lipTop = need("lip_top");
        lipBottom = need("lip_bottom");
        lipLeft = need("lip_left");
        lipRight = need("lip_right");
        finLeft = need("fin_left");
        finRight = need("fin_right");
        chain = new ModelPart[] {need("body_1"), need("body_2"), need("tail")};
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    /** In the water, it swims. */
    @Override protected boolean state(CorruptedMaw maw) { return maw.isInWater(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float whirl = on[Skill.WHIRLPOOL.ordinal()], lunge = on[Skill.LUNGE.ordinal()], spout = on[Skill.SPOUT.ordinal()];
        float speed = Math.min(1, amount * 2);
        float phase = age * (.15F + (.25F * speed + .5F * whirl) * state);
        wave(chain, phase, (.12F + .08F * speed + .1F * whirl) * (.4F + .6F * state), .8F, true);
        float fin = .4F * Mth.sin(phase * 1.5F) * state + .05F * Mth.sin(age * .2F) * (1 - state);
        finLeft.zRot += fin - .3F * (1 - state);
        finRight.zRot -= fin - .3F * (1 - state);
        lure.xRot += Mth.sin(age * .1F) * .15F;
        lure.zRot += Mth.sin(age * .07F) * .1F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .2F;

        float gape = .05F + .05F * Mth.sin(age * .12F);
        gape = Math.max(gape, strike(bite));

        // Its whirlpool: lips gaping wide and trembling.
        gape = Math.max(gape, whirl * (.9F + .1F * Mth.sin(age * 2.3F)));

        // Its lunge: lips open, fins swept back.
        gape = Math.max(gape, .8F * lunge);
        finLeft.yRot += .8F * lunge;
        finRight.yRot -= .8F * lunge;

        // Its spout: lips pursed through the wind-up, snapped open as it spouts.
        if (posed == Skill.SPOUT.ordinal()) {
            int up = MawRules.SPOUT.timing().windup();
            gape = Mth.lerp(spout, gape, since < up ? .15F : .15F + .85F * pulse(since, up - 1, 8));
        }

        open = gape;
        lipTop.y -= 2 * gape;
        lipTop.xRot -= .6F * gape;
        lipBottom.y += 2 * gape;
        lipBottom.xRot += .6F * gape;
        lipLeft.x -= 2 * gape;
        lipLeft.yRot += .6F * gape;
        lipRight.x += 2 * gape;
        lipRight.yRot -= .6F * gape;
    }
}
