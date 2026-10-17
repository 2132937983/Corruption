package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedLurker;
import com.miophas.corruption.core.elite.LurkerRules;
import com.miophas.corruption.core.elite.LurkerRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted lurker. In the water its tentacles ripple behind it, a wave running down each from its root to its tip
 * (the faster the faster it swims), its fins beat and its body rolls a little; out of the water they lie limp and drag,
 * and only twitch. Its jaw drops to bite and to shoot, and gapes as its tentacle lashes out; its two arms reach forward
 * for what it grabs. The tentacle it lashes out is no part of it as it is posed: its renderer draws it along the lash.
 */
public final class CorruptedLurkerModel extends EliteModel<CorruptedLurker> implements LashModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_lurker"), "main");
    static final String GEOMETRY = "corrupted_lurker";
    private static final String[] SIDES = {"left", "right"};

    // Seen by EliteModelTest and CorruptedLurkerRenderer.
    final ModelPart body, jaw, lash, lashClub;
    final ModelPart[] fins = new ModelPart[2];
    final ModelPart[][] arms = new ModelPart[2][2];
    final ModelPart[][][] tentacles = new ModelPart[2][3][3];

    public CorruptedLurkerModel(ModelPart root) {
        super(root, GEOMETRY);
        body = need("body");
        jaw = need("jaw");
        lash = need("lash");
        lashClub = need("lash_club");
        for (int side = 0; side < 2; side++) {
            fins[side] = need("fin_" + SIDES[side]);
            arms[side][0] = need("arm_" + SIDES[side]);
            arms[side][1] = need("arm_" + SIDES[side] + "_1");
            for (int index = 0; index < 3; index++) {
                String first = "tentacle_" + SIDES[side] + "_" + index;
                tentacles[side][index][0] = need(first);
                tentacles[side][index][1] = need(first + "_1");
                tentacles[side][index][2] = need(first + "_2");
            }
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart lash() { return lash; }
    @Override public ModelPart lashEnd() { return lashClub; }

    @Override public void setupAnim(CorruptedLurker lurker, float swing, float amount, float age, float yaw, float pitch) {
        posing(lurker, age);
        float partial = age - lurker.tickCount, since = lurker.skillTicks(partial);
        int skill = lurker.skill();
        float wet = blend(1, lurker.isInWater(), .1F);
        float shoot = blend(2, skill == Skill.BOLTS.ordinal(), .3F);
        float grab = blend(3, skill == Skill.GRAB.ordinal(), .3F);
        pose(amount, age, wet, lurker.posedSkill(), since, lurker.biteTicks(partial), shoot, grab);
    }

    /**
     * Its pose: how fast it swims ({@code amount}, as the game counts its steps), at its age in ticks; how far it is in the
     * water (0 to 1, easing as it goes in or comes out); the skill it last began and the ticks since; the ticks since its
     * last bite; and how far it is into shooting and lashing out (each 0 to 1).
     */
    void pose(float amount, float age, float wet, int posed, float since, float bite, float shoot, float grab) {
        reset();
        lash.visible = lashClub.visible = false;
        float speed = Math.min(1, amount * 2);
        float phase = age * (.22F + .3F * speed * wet);

        // Its tentacles: a wave down each from its root, in the water; limp and dragging out of it, twitching.
        for (int side = 0; side < 2; side++) {
            float sign = side == 0 ? 1 : -1;
            for (int index = 0; index < 3; index++)
                for (int piece = 0; piece < 3; piece++) {
                    ModelPart part = tentacles[side][index][piece];
                    float wave = phase - piece * .9F - index * .6F + side * .4F;
                    part.yRot += Mth.sin(wave) * (.12F + .1F * piece) * (.3F + .7F * wet);
                    part.xRot += Mth.cos(wave * .8F) * .07F * (piece + 1) * wet - (piece == 0 ? .35F : .15F) * (1 - wet);
                }
            fins[side].zRot += sign * (.5F * Mth.sin(phase * 1.6F) * wet + .1F * Mth.sin(age * .4F) * (1 - wet));
        }
        body.zRot += Mth.sin(phase * .5F) * .05F * wet;
        body.y += Mth.sin(age * .1F) * .3F * wet;

        // Its jaw: dropped to bite, opened through the wind-up of its bolts and snapping at each, gaping as it lashes out.
        float open = .06F + .05F * Mth.sin(age * .12F);
        if (bite < 10) open = Math.max(open, Mth.sin(Math.max(0, bite) / 10 * Mth.PI));
        open = Math.max(open, .6F * shoot);
        if (posed == Skill.BOLTS.ordinal())
            for (int shot = 0; shot < LurkerRules.BOLTS.shots(); shot++)
                open = Math.max(open, .6F * shoot + .4F * pulse(since, LurkerRules.BOLTS.timing().windup() + shot * LurkerRules.BOLTS.every(), 4));
        open = Math.max(open, .7F * grab);
        jaw.xRot += .7F * open;

        // Its arms trail under it, swaying; they reach forward for what it grabs.
        for (int side = 0; side < 2; side++) {
            arms[side][0].yRot += Mth.sin(phase - 1 + side) * .15F * (1 - grab);
            arms[side][0].xRot -= 2.6F * grab;
            arms[side][1].xRot -= .4F * grab + .1F * Mth.sin(phase - 1.5F);
        }
    }
}
