package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedBloater;
import com.miophas.corruption.core.elite.BloaterRules;
import com.miophas.corruption.core.elite.BloaterRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted bloater. It waddles on its four stubs, its sac heaving with every breath; it butts with its head; it
 * swells through its vent's wind-up and sags as the gas goes out of it; and it rocks back to lob, its head snapping up at
 * every gob.
 */
public final class CorruptedBloaterModel extends PosedEliteModel<CorruptedBloater> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_bloater"), "main");
    static final String GEOMETRY = "corrupted_bloater";

    final ModelPart body, head, foreLeft, foreRight, hindLeft, hindRight;
    /** How swollen its body is, as last posed (for the tests to see). */
    float swollen;

    public CorruptedBloaterModel(ModelPart root) {
        super(root, GEOMETRY, BloaterRules.SKILLS.size());
        body = need("body");
        head = need("head");
        foreLeft = need("leg_left_0");
        foreRight = need("leg_right_0");
        hindLeft = need("leg_left_1");
        hindRight = need("leg_right_1");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        trot(foreLeft, foreRight, hindLeft, hindRight, swing, amount, .6F);
        body.zRot += Mth.cos(swing * .6662F) * .06F * Math.min(1, amount);
        head.yRot += yaw * Mth.DEG_TO_RAD * .3F;
        float swell = 1 + .03F * Mth.sin(age * .1F);

        head.xRot += .5F * strike(bite);

        // Its vent: swelling through the wind-up, sagging as the gas goes.
        float vent = on[Skill.VENT.ordinal()];
        int ventUp = BloaterRules.VENT.windup();
        if (posed == Skill.VENT.ordinal())
            swell += vent * (since < ventUp ? .25F * gathering(since, ventUp) : -.1F * Math.max(0, 1 - (since - ventUp) / 10));

        // Its lob: rocked back, its head snapping up at every gob.
        float lob = on[Skill.LOB.ordinal()];
        body.xRot -= .15F * lob;
        head.xRot -= .3F * lob;
        if (posed == Skill.LOB.ordinal())
            for (int shot = 0; shot < BloaterRules.LOB.shots(); shot++)
                head.xRot -= .5F * lob * pulse(since, BloaterRules.LOB.timing().windup() + shot * BloaterRules.LOB.every(), 5);

        swollen = swell;
        body.xScale = body.zScale = swell;
        body.yScale = 1 + (swell - 1) * .7F;
    }
}
