package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedGazer;
import com.miophas.corruption.core.elite.GazerRules;
import com.miophas.corruption.core.elite.GazerRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted gazer. It bobs as it hangs in the air, its eye-stalks writhing each to a time of its own and the nerves
 * under it swaying, a wave running down each; it whips its nerves forward at what it lashes; it folds its stalks back
 * and shudders as its beam burns; it turns every stalk forward to stare; and it snaps a stalk forward at every bolt.
 */
public final class CorruptedGazerModel extends PosedEliteModel<CorruptedGazer> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_gazer"), "main");
    static final String GEOMETRY = "corrupted_gazer";
    static final String[] STALKS = {"stalk_top", "stalk_top_left", "stalk_top_right", "stalk_side_left", "stalk_side_right", "stalk_low_left", "stalk_low_right"};
    static final int NERVES = 3;

    final ModelPart body;
    final ModelPart[] stalks = new ModelPart[STALKS.length];
    final ModelPart[][] nerves = new ModelPart[NERVES][2];

    public CorruptedGazerModel(ModelPart root) {
        super(root, GEOMETRY, GazerRules.SKILLS.size());
        body = need("body");
        for (int index = 0; index < STALKS.length; index++) stalks[index] = need(STALKS[index]);
        for (int index = 0; index < NERVES; index++) {
            nerves[index][0] = need("nerve_" + index);
            nerves[index][1] = need("nerve_" + index + "_1");
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        body.y += Mth.sin(age * .08F) * .8F;
        body.yRot += yaw * Mth.DEG_TO_RAD * .3F;
        body.xRot += pitch * Mth.DEG_TO_RAD * .5F;
        for (int index = 0; index < stalks.length; index++) {
            stalks[index].xRot += Mth.sin(age * .13F + index * 1.7F) * .15F;
            stalks[index].zRot += Mth.cos(age * .11F + index * 2.3F) * .12F;
        }
        float blow = strike(bite);
        for (int index = 0; index < NERVES; index++) {
            wave(nerves[index], age * .15F + index, .15F, .9F, false);
            nerves[index][0].zRot += Mth.sin(age * .1F + index * 2) * .1F;
            nerves[index][0].xRot -= 1.2F * blow;
            nerves[index][1].xRot -= .5F * blow;
        }

        // Its beam: stalks folded back, shuddering as it burns.
        float beam = on[Skill.BEAM.ordinal()];
        float burning = posed == Skill.BEAM.ordinal() && since >= GazerRules.BEAM.timing().windup() ? beam : 0;
        for (ModelPart stalk : stalks) stalk.xRot -= .6F * beam;
        body.zRot += Mth.sin(age * 2.1F) * .04F * burning;
        body.xRot += Mth.sin(age * 1.7F) * .03F * burning;

        // Its gaze: every stalk turned forward.
        float gaze = on[Skill.GAZE.ordinal()];
        for (ModelPart stalk : stalks) stalk.xRot += .9F * gaze;

        // Its bolts: a stalk snapped forward at every bolt.
        float bolts = on[Skill.BOLTS.ordinal()];
        for (ModelPart stalk : stalks) stalk.xRot += .3F * bolts;
        if (posed == Skill.BOLTS.ordinal())
            for (int shot = 0; shot < GazerRules.BOLTS.shots(); shot++)
                stalks[1 + shot % (stalks.length - 1)].xRot += .9F * bolts * pulse(since, GazerRules.BOLTS.timing().windup() + shot * GazerRules.BOLTS.every(), 4);
    }
}
