package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedStalker;
import com.miophas.corruption.core.elite.StalkerRules;
import com.miophas.corruption.core.elite.StalkerRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted stalker. It lopes, stooped, its blades swinging; it slashes with a blade; it crouches, blades crossed
 * before it, as it fades (and faded, it goes the lower); it spreads its blades wide and leans in to flicker through its
 * quarry; and it draws its arms back and flicks them forward to fling its spines.
 */
public final class CorruptedStalkerModel extends PosedEliteModel<CorruptedStalker> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_stalker"), "main");
    static final String GEOMETRY = "corrupted_stalker";

    final ModelPart body, head, armLeft, armRight, bladeLeft, bladeRight, legLeft, legRight;

    public CorruptedStalkerModel(ModelPart root) {
        super(root, GEOMETRY, StalkerRules.SKILLS.size());
        body = need("body");
        head = need("head");
        armLeft = need("arm_left");
        armRight = need("arm_right");
        bladeLeft = need("blade_left");
        bladeRight = need("blade_right");
        legLeft = need("leg_left");
        legRight = need("leg_right");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    /** Faded from sight, it goes lower. */
    @Override protected boolean state(CorruptedStalker stalker) { return stalker.faded(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        walk(legLeft, legRight, swing, amount, .9F);
        walk(armRight, armLeft, swing, amount, .4F);
        head.yRot += yaw * Mth.DEG_TO_RAD * .5F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;
        head.zRot += Mth.sin(age * .11F) * .05F;
        body.xRot += .25F * state;
        body.y += 1.5F * state;

        float blow = strike(bite);
        armRight.xRot -= 1.5F * blow;
        armRight.zRot -= .4F * blow;
        bladeRight.xRot -= .6F * blow;

        // Its fade: crouched, blades crossed before it.
        float fade = on[Skill.FADE.ordinal()];
        body.y += 2 * fade;
        armLeft.xRot -= .9F * fade;
        armRight.xRot -= .9F * fade;
        armLeft.yRot -= .5F * fade;
        armRight.yRot += .5F * fade;

        // Its flicker through its quarry: blades spread wide, leaning in.
        float phase = on[Skill.PHASE.ordinal()];
        armLeft.zRot += 1.1F * phase;
        armRight.zRot -= 1.1F * phase;
        body.xRot += .3F * phase;

        // Its spines: arms drawn back, flicked forward at every throw.
        float spines = on[Skill.SPINES.ordinal()];
        armLeft.xRot += .6F * spines;
        armRight.xRot += .6F * spines;
        if (posed == Skill.SPINES.ordinal())
            for (int shot = 0; shot < StalkerRules.SPINES.shots(); shot++) {
                float flick = pulse(since, StalkerRules.SPINES.timing().windup() + shot * StalkerRules.SPINES.every(), 3);
                (shot % 2 == 0 ? armRight : armLeft).xRot -= 1.6F * flick * spines;
            }
    }
}
