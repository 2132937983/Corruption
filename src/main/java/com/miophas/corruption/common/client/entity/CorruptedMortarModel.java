package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedMortar;
import com.miophas.corruption.core.elite.MortarRules;
import com.miophas.corruption.core.elite.MortarRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted mortar. It plods on its four legs, its eyes swaying on their stalks; it rams with its head; its tube
 * swells through its barrage's wind-up and kicks back at every bolt it throws; and it rears and stamps its forelegs down
 * to drive its spikes up.
 */
public final class CorruptedMortarModel extends PosedEliteModel<CorruptedMortar> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_mortar"), "main");
    static final String GEOMETRY = "corrupted_mortar";

    final ModelPart body, cannon, head, foreLeft, foreRight, hindLeft, hindRight;

    public CorruptedMortarModel(ModelPart root) {
        super(root, GEOMETRY, MortarRules.SKILLS.size());
        body = need("body");
        cannon = need("cannon");
        head = need("head");
        foreLeft = need("leg_left_0");
        foreRight = need("leg_right_0");
        hindLeft = need("leg_left_1");
        hindRight = need("leg_right_1");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        trot(foreLeft, foreRight, hindLeft, hindRight, swing, amount, .5F);
        head.yRot += yaw * Mth.DEG_TO_RAD * .4F + Mth.sin(age * .06F) * .1F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .3F + .4F * strike(bite);
        cannon.xRot += Mth.sin(age * .05F) * .04F;

        // Its barrage: the tube swelling through the wind-up, kicking back at every bolt.
        float barrage = on[Skill.BARRAGE.ordinal()];
        int up = MortarRules.BARRAGE.timing().windup();
        float swell = posed == Skill.BARRAGE.ordinal() && since < up ? gathering(since, up) : 0;
        cannon.xScale = cannon.zScale = 1 + .18F * swell * barrage;
        cannon.yScale = 1 + .1F * swell * barrage;
        if (posed == Skill.BARRAGE.ordinal())
            for (int bolt = 0; bolt < MortarRules.BARRAGE.count(); bolt++)
                cannon.xRot -= .25F * barrage * pulse(since, up + (float) bolt / MortarRules.BARRAGE.perTick(), 2);
        body.xRot += .08F * barrage;

        // Its spikes: rearing, then its forelegs stamped down.
        float spikes = on[Skill.SPIKES.ordinal()];
        int rear = MortarRules.SPIKES.timing().windup();
        float raised = posed == Skill.SPIKES.ordinal() && since < rear ? gathering(since, rear) : 0;
        body.xRot -= .3F * raised * spikes;
        foreLeft.xRot -= .9F * raised * spikes;
        foreRight.xRot -= .9F * raised * spikes;
    }
}
