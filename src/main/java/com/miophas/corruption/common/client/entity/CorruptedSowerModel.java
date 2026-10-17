package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedSower;
import com.miophas.corruption.core.elite.SowerRules;
import com.miophas.corruption.core.elite.SowerRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted sower. It walks on its roots a pair at a time, a ripple running back along them, its mound throbbing as
 * a heart does; the four petals of its mouth breathe open and shut, and part wide to bite; it digs its roots in and swells
 * as it sows, then shrinks as it lets the corruption go into the ground; and it leans in, its petals wide, as it sends
 * its roots up under a quarry.
 */
public final class CorruptedSowerModel extends PosedEliteModel<CorruptedSower> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_sower"), "main");
    static final String GEOMETRY = "corrupted_sower";
    static final int PAIRS = 3;

    final ModelPart body, mawTop, mawBottom, mawLeft, mawRight;
    final ModelPart[][] roots = new ModelPart[2][PAIRS];
    /** How far its mouth is open and how swollen it is, as last posed (for the tests to see). */
    float open, swollen;

    public CorruptedSowerModel(ModelPart root) {
        super(root, GEOMETRY, SowerRules.SKILLS.size());
        body = need("body");
        mawTop = need("maw_top");
        mawBottom = need("maw_bottom");
        mawLeft = need("maw_left");
        mawRight = need("maw_right");
        for (int index = 0; index < PAIRS; index++) {
            roots[0][index] = need("root_left_" + index);
            roots[1][index] = need("root_right_" + index);
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float go = Math.min(1, amount * 1.5F);
        // Its roots step a pair at a time, a ripple running back along them; each lifted as it swings forward.
        for (int index = 0; index < PAIRS; index++) {
            float step = swing * .9F - index * 1.2F;
            roots[0][index].xRot += Mth.sin(step) * .45F * go;
            roots[1][index].xRot += Mth.sin(step + Mth.PI) * .45F * go;
            roots[0][index].zRot += Math.max(0, Mth.cos(step)) * .2F * go;
            roots[1][index].zRot -= Math.max(0, Mth.cos(step + Mth.PI)) * .2F * go;
        }
        body.y -= Math.abs(Mth.sin(swing * .9F)) * .6F * go;
        float swell = 1 + .025F * Math.max(0, Mth.sin(age * .18F)) * Math.max(0, Mth.sin(age * .18F));
        float gape = .1F + .1F * Mth.sin(age * .07F);
        gape = Math.max(gape, strike(bite));

        // Its sowing: its roots dug in, swelling through the wind-up, shrinking as it lets the corruption go.
        float seed = on[Skill.SEED.ordinal()];
        int up = SowerRules.SEED.timing().windup();
        if (posed == Skill.SEED.ordinal())
            swell += seed * (since < up ? .18F * gathering(since, up) : -.08F * Math.max(0, 1 - (since - up) / 10));
        body.y += 2 * seed;
        for (int index = 0; index < PAIRS; index++) {
            roots[0][index].zRot += .35F * seed;
            roots[1][index].zRot -= .35F * seed;
        }
        gape = Math.max(gape, seed * (posed == Skill.SEED.ordinal() && since >= up ? 1 : .3F));

        // Its roots sent up: leaning in, its petals wide.
        float snare = on[Skill.SNARE.ordinal()];
        body.xRot += .2F * snare;
        gape = Math.max(gape, .9F * snare);

        open = gape;
        swollen = swell;
        body.xScale = body.zScale = swell;
        body.yScale = 1 + (swell - 1) * .6F;
        mawTop.xRot -= .6F * gape;
        mawBottom.xRot += .6F * gape;
        mawLeft.yRot += .6F * gape;
        mawRight.yRot -= .6F * gape;
    }
}
