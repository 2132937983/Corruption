package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedScreecher;
import com.miophas.corruption.core.elite.ScreecherRules;
import com.miophas.corruption.core.elite.ScreecherRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted screecher. Its wings beat, each piece a little after the one it hangs from (so they whip), faster and
 * harder the faster it flies, its body rising and falling against them; its tail sways, and its head follows what it
 * looks at. It swoops with its wings half folded and its talons thrown forward, its beak open; it dives with its wings
 * folded tight and its neck stretched out; it raises its wings to loose its feathers and snaps them down at each; and it
 * screeches with its head drawn back and then thrust out, its beak wide, its wings spread and trembling.
 */
public final class CorruptedScreecherModel extends EliteModel<CorruptedScreecher> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_screecher"), "main");
    static final String GEOMETRY = "corrupted_screecher";
    private static final String[] SIDES = {"left", "right"};

    // Seen by EliteModelTest.
    final ModelPart body, neck, neck1, head, jaw, tail;
    final ModelPart[][] wings = new ModelPart[2][3];
    final ModelPart[] legs = new ModelPart[2];

    public CorruptedScreecherModel(ModelPart root) {
        super(root, GEOMETRY);
        body = need("body");
        neck = need("neck");
        neck1 = need("neck_1");
        head = need("head");
        jaw = need("jaw");
        tail = need("tail");
        for (int side = 0; side < 2; side++) {
            wings[side][0] = need("wing_" + SIDES[side]);
            wings[side][1] = need("wing_" + SIDES[side] + "_1");
            wings[side][2] = need("wing_" + SIDES[side] + "_2");
            legs[side] = need("leg_" + SIDES[side]);
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public void setupAnim(CorruptedScreecher bird, float swing, float amount, float age, float yaw, float pitch) {
        posing(bird, age);
        float partial = age - bird.tickCount, since = bird.skillTicks(partial);
        int skill = bird.skill();
        float swoop = blend(1, skill == Skill.SWOOP.ordinal() && since >= ScreecherRules.SWOOP.windup(), .4F);
        float dive = blend(2, skill == Skill.DIVE.ordinal() && since >= ScreecherRules.DIVE.timing().windup(), .4F);
        float raise = blend(3, skill == Skill.FEATHERS.ordinal(), .3F);
        float draw = blend(4, skill == Skill.SCREECH.ordinal() && since < ScreecherRules.SCREECH.timing().windup(), .3F);
        float scream = blend(5, skill == Skill.SCREECH.ordinal() && since >= ScreecherRules.SCREECH.timing().windup(), .5F);
        pose(amount, age, yaw, pitch, bird.posedSkill(), since, swoop, dive, raise, draw, scream);
    }

    /**
     * Its pose: how fast it flies ({@code amount}, as the game counts its steps), looking, at its age in ticks; the skill it
     * last began and the ticks since; and how far it is into its swoop, its dive, raising its wings to loose its feathers,
     * drawing breath to screech and screaming (each 0 to 1).
     */
    void pose(float amount, float age, float yaw, float pitch, int posed, float since, float swoop, float dive, float raise, float draw, float scream) {
        reset();
        float speed = Math.min(1, amount);
        float folded = Math.max(swoop * .7F, dive);

        // Its wings beat, each piece after the one it hangs from.
        float phase = age * (.32F + .22F * speed + .2F * draw), beat = (.55F + .25F * speed) * (1 - folded) * (1 - .6F * scream);
        for (int side = 0; side < 2; side++) {
            float sign = side == 0 ? 1 : -1;
            wings[side][0].zRot += sign * beat * Mth.sin(phase);
            wings[side][1].zRot += sign * beat * .6F * Mth.sin(phase - .7F);
            wings[side][2].zRot += sign * beat * .45F * Mth.sin(phase - 1.3F);
            // Folded back for a swoop, tight for a dive.
            wings[side][0].yRot += sign * (.7F * swoop + 1.1F * dive);
            wings[side][0].zRot -= sign * .15F * dive;
            wings[side][1].zRot -= sign * (.5F * swoop + .9F * dive);
            wings[side][1].yRot += sign * .8F * dive;
            wings[side][2].zRot -= sign * .6F * dive;
            // Raised to loose its feathers; spread and trembling as it screams.
            wings[side][0].zRot += sign * (.9F * raise + .35F * draw + .3F * scream);
            wings[side][0].yRot -= sign * .2F * (draw + scream);
            wings[side][0].zRot += sign * .07F * Mth.sin(age * 3) * scream;
        }
        if (posed == Skill.FEATHERS.ordinal())
            for (int shot = 0; shot < ScreecherRules.FEATHERS.shots(); shot++) {
                float snap = pulse(since, ScreecherRules.FEATHERS.timing().windup() + shot * ScreecherRules.FEATHERS.every(), 3);
                wings[0][0].zRot -= 1.1F * snap;
                wings[1][0].zRot += 1.1F * snap;
            }
        body.y -= Mth.cos(phase) * .8F * (1 - folded);

        // Its neck and head (a turn about X tips them down): drawn back and up to screech, thrust out and down as it
        // screams; stretched out level in a dive or a swoop.
        neck.xRot += .05F * Mth.sin(phase + .5F) - .4F * draw + .5F * scream + .45F * dive + .3F * swoop;
        head.xRot += -.3F * draw + .3F * scream + .2F * dive;
        head.yRot += yaw * Mth.DEG_TO_RAD * .5F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;
        float open = Math.max(.05F + .05F * Mth.sin(age * .15F), Math.max(scream, .8F * swoop));
        jaw.xRot += .9F * open;

        // Its talons thrown forward to strike; tucked back tighter in a dive. Its tail sways, and is held still in a dive.
        for (ModelPart leg : legs) leg.xRot += -1.5F * swoop + .3F * dive;
        tail.yRot += Mth.sin(age * .07F) * .15F * (1 - dive);
        tail.xRot += Mth.sin(phase - 1) * .1F * (1 - folded) - .2F * dive;
    }
}
