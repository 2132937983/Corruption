package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedNaiad;
import com.miophas.corruption.core.elite.NaiadRules;
import com.miophas.corruption.core.elite.NaiadRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted naiad. In the water its tail kicks, a wave running down it to its fluke, and the faster it swims the
 * further it lies forward, its arms trailing; out of it, it stands coiled on its tail, swaying. It thrusts its spear at
 * what it strikes; it draws its spear back over its shoulder and hurls it (and has none in its hand until it is over); it
 * lies forward to surge at its quarry, arms swept back, its tail kicking hard; and it throws its head back and its arms
 * wide to sing.
 */
public final class CorruptedNaiadModel extends PosedEliteModel<CorruptedNaiad> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_naiad"), "main");
    static final String GEOMETRY = "corrupted_naiad";

    final ModelPart tail0, tail1, fluke, body, head, armLeft, armRight, forearmLeft, forearmRight, spear;
    final ModelPart[] kick;

    public CorruptedNaiadModel(ModelPart root) {
        super(root, GEOMETRY, NaiadRules.SKILLS.size());
        tail0 = need("tail_0");
        tail1 = need("tail_1");
        fluke = need("fluke");
        body = need("body");
        head = need("head");
        armLeft = need("arm_left");
        armRight = need("arm_right");
        forearmLeft = need("forearm_left");
        forearmRight = need("forearm_right");
        spear = need("spear");
        kick = new ModelPart[] {tail1, fluke};
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    /** In the water, it swims. */
    @Override protected boolean state(CorruptedNaiad naiad) { return naiad.isInWater(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float surge = on[Skill.SURGE.ordinal()], song = on[Skill.SONG.ordinal()], throwing = on[Skill.SPEAR.ordinal()];
        float speed = Math.min(1, amount * 3);
        float prone = state * Math.max(speed, surge);
        float phase = age * (.12F + (.3F * speed + .3F * surge) * state);
        wave(kick, phase, .15F + .15F * state * (speed + surge), 1, false);
        tail0.zRot += Mth.sin(age * .06F) * .05F * (1 - state);

        // Lying forward as it swims, looking ahead; arms trailing.
        tail0.xRot += 1.3F * prone;
        head.xRot -= 1.0F * prone;
        armLeft.xRot += .9F * prone * (1 - throwing);
        armRight.xRot += .9F * prone * (1 - throwing);
        head.yRot += yaw * Mth.DEG_TO_RAD * .5F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;
        armLeft.zRot += Mth.sin(age * .07F) * .05F;

        // Its spear thrust at what it strikes.
        float blow = strike(bite);
        armRight.xRot -= .9F * blow;
        forearmRight.xRot += .9F * blow;
        armRight.z -= 2 * blow;

        // Its throw: the spear drawn back over its shoulder through the wind-up, hurled, and gone until it is over.
        spear.visible = true;
        if (posed == Skill.SPEAR.ordinal()) {
            int up = NaiadRules.SPEAR.timing().windup();
            float drawn = since < up ? gathering(since, up) : 0, hurled = since < up ? 0 : Math.max(0, 1 - (since - up) / 8);
            armRight.xRot -= throwing * (3.4F * drawn + 1.4F * hurled);
            forearmRight.xRot += throwing * 3.1F * drawn;
            body.yRot += throwing * (.3F * drawn - .2F * hurled);
            spear.visible = since < up || since >= NaiadRules.SPEAR.timing().ticks();
        }

        // Its surge: arms swept back.
        armLeft.xRot += .6F * surge;
        armRight.xRot += .6F * surge;

        // Its song: head thrown back, arms wide.
        head.xRot -= .6F * song;
        head.zRot += Mth.sin(age * 1.3F) * .05F * song;
        body.xRot -= .15F * song;
        armLeft.zRot += 1.2F * song;
        armRight.zRot -= 1.2F * song;
        forearmLeft.zRot += .3F * song;
        forearmRight.zRot -= .3F * song;
    }
}
