package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedHowler;
import com.miophas.corruption.core.elite.HowlerRules;
import com.miophas.corruption.core.elite.HowlerRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted howler. It stalks on its long legs, its arms swinging loose to the ground; it rakes with a long arm; it
 * throws its head back, its jaw hanging open, its arms flung wide, to howl; and it spits its bile with its head thrust out,
 * its jaw snapping at every gob.
 */
public final class CorruptedHowlerModel extends PosedEliteModel<CorruptedHowler> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_howler"), "main");
    static final String GEOMETRY = "corrupted_howler";

    final ModelPart body, neck, head, jaw, armLeft, armRight, forearmLeft, forearmRight, legLeft, legRight;

    public CorruptedHowlerModel(ModelPart root) {
        super(root, GEOMETRY, HowlerRules.SKILLS.size());
        body = need("body");
        neck = need("neck");
        head = need("head");
        jaw = need("jaw");
        armLeft = need("arm_left");
        armRight = need("arm_right");
        forearmLeft = need("forearm_left");
        forearmRight = need("forearm_right");
        legLeft = need("leg_left");
        legRight = need("leg_right");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        walk(legLeft, legRight, swing, amount, .7F);
        walk(armRight, armLeft, swing, amount, .3F);
        armLeft.zRot += Mth.sin(age * .05F) * .04F;
        armRight.zRot -= Mth.sin(age * .05F) * .04F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .5F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;
        float open = .1F + .08F * Mth.sin(age * .09F);

        float blow = strike(bite);
        armRight.xRot -= 1.4F * blow;
        forearmRight.xRot -= .5F * blow;

        // Its howl: head thrown back, jaw hanging open, arms flung wide.
        float howl = on[Skill.HOWL.ordinal()];
        neck.xRot -= .6F * howl;
        head.xRot -= .7F * howl;
        body.xRot -= .2F * howl;
        armLeft.zRot += .9F * howl;
        armRight.zRot -= .9F * howl;
        open = Math.max(open, howl);
        head.zRot += Mth.sin(age * 1.7F) * .06F * howl;

        // Its bile: head thrust out, its jaw snapping at every gob.
        float bile = on[Skill.BILE.ordinal()];
        neck.xRot += .4F * bile;
        open = Math.max(open, .4F * bile);
        if (posed == Skill.BILE.ordinal())
            for (int shot = 0; shot < HowlerRules.BILE.shots(); shot++)
                open = Math.max(open, bile * (.4F + .6F * pulse(since, HowlerRules.BILE.timing().windup() + shot * HowlerRules.BILE.every(), 5)));
        jaw.xRot += .9F * open;
    }
}
