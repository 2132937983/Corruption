package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedDrifter;
import com.miophas.corruption.core.elite.DrifterRules;
import com.miophas.corruption.core.elite.DrifterRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted drifter. Its bell pulses as it drifts, its tendrils flaring as it squeezes and trailing as it eases, a
 * wave running down each; it whips its fore tendrils forward at what it stings; it gathers its tendrils in under it as
 * it lifts what it holds; and it swells through its rain's wind-up and squeezes hard, its tendrils flung wide. The
 * tendril it reaches out is no part of it as it is posed: its renderer draws it along the lash.
 */
public final class CorruptedDrifterModel extends PosedEliteModel<CorruptedDrifter> implements LashModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_drifter"), "main");
    static final String GEOMETRY = "corrupted_drifter";
    private static final String[] SIDES = {"left", "right"};
    static final int TENDRILS = 3;

    final ModelPart bell, lash, lashClub;
    final ModelPart[][][] tendrils = new ModelPart[2][TENDRILS][2];
    /** How far its bell is squeezed, as last posed, -1 to 1 (for the tests to see). */
    float squeezed;

    public CorruptedDrifterModel(ModelPart root) {
        super(root, GEOMETRY, DrifterRules.SKILLS.size());
        bell = need("bell");
        lash = need("lash");
        lashClub = need("lash_club");
        for (int side = 0; side < 2; side++)
            for (int index = 0; index < TENDRILS; index++) {
                String first = "tendril_" + SIDES[side] + "_" + index;
                tendrils[side][index][0] = need(first);
                tendrils[side][index][1] = need(first + "_1");
            }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart lash() { return lash; }
    @Override public ModelPart lashEnd() { return lashClub; }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        lash.visible = lashClub.visible = false;
        float lift = on[Skill.LIFT.ordinal()], rain = on[Skill.RAIN.ordinal()];
        float phase = age * (.12F + .1F * Math.min(1, amount));
        float squeeze = Mth.sin(phase);

        // Its rain: swelling through the wind-up, then squeezing hard.
        int up = DrifterRules.RAIN.timing().windup();
        if (posed == Skill.RAIN.ordinal())
            squeeze = Mth.lerp(rain, squeeze, since < up ? -gathering(since, up) : 1 - Math.min(1, (since - up) / 12));
        squeezed = squeeze;
        bell.xScale = bell.zScale = 1 - .08F * squeeze;
        bell.yScale = 1 + .06F * squeeze;
        bell.y += Mth.sin(age * .05F) * .8F - .5F * squeeze;

        float blow = strike(bite);
        for (int side = 0; side < 2; side++) {
            float out = side == 0 ? 1 : -1;
            for (int index = 0; index < TENDRILS; index++) {
                ModelPart[] tendril = tendrils[side][index];
                wave(tendril, phase * 2 - index * .8F - side * .5F, .12F, .9F, false);
                // Flared as it squeezes, flung wide in its rain; gathered in under it as it lifts.
                tendril[0].zRot += out * (.25F * Math.max(0, squeeze) + .6F * rain - .45F * lift);
                tendril[1].zRot -= out * .3F * lift;
                if (index == 0) {
                    tendril[0].xRot -= 1.1F * blow;
                    tendril[1].xRot -= .4F * blow;
                }
            }
        }
    }
}
