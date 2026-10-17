package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedAberration;
import com.miophas.corruption.core.elite.AberrationRules;
import com.miophas.corruption.core.elite.AberrationRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted aberration. It walks on its six legs three at a time (a tripod, the first and last of one side with the
 * middle of the other), each lifted as it swings forward, its body bobbing at every step; its maw's four petals part to
 * bite and spit, and gape when it roars. It crouches to pounce and sweeps its legs back in the leap; it rears to spit,
 * snapping its head down at each glob; it rears and shakes its head as it roars; and berserk, its crystals swell and
 * pulse and its body trembles.
 */
public final class CorruptedAberrationModel extends EliteModel<CorruptedAberration> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_aberration"), "main");
    static final String GEOMETRY = "corrupted_aberration";
    private static final String[] SIDES = {"left", "right"};

    // Seen by EliteModelTest.
    final ModelPart body, crystals, head, mawTop, mawBottom;
    final ModelPart[] mawSides = new ModelPart[2];
    final ModelPart[][] legs = new ModelPart[2][3];
    /** How far its maw is open, as last posed (for the tests to see). */
    float opened;

    public CorruptedAberrationModel(ModelPart root) {
        super(root, GEOMETRY);
        body = need("body");
        crystals = need("crystals");
        head = need("head");
        mawTop = need("maw_top");
        mawBottom = need("maw_bottom");
        for (int side = 0; side < 2; side++) {
            mawSides[side] = need("maw_" + SIDES[side]);
            for (int leg = 0; leg < 3; leg++) legs[side][leg] = need("leg_" + SIDES[side] + "_" + leg);
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public void setupAnim(CorruptedAberration aberration, float swing, float amount, float age, float yaw, float pitch) {
        posing(aberration, age);
        float partial = age - aberration.tickCount, since = aberration.skillTicks(partial);
        int skill = aberration.skill();
        float crouch = blend(1, skill == Skill.POUNCE.ordinal() && since < AberrationRules.POUNCE.timing().windup(), .35F);
        float leap = blend(2, skill == Skill.POUNCE.ordinal() && since >= AberrationRules.POUNCE.timing().windup(), .5F);
        float rear = blend(3, skill == Skill.ACID.ordinal(), .25F);
        float roar = blend(4, skill == Skill.ROAR.ordinal(), .3F);
        pose(swing, amount, age, yaw, pitch, aberration.posedSkill(), since, crouch, leap, rear, roar, aberration.biteTicks(partial), aberration.berserk());
    }

    /**
     * Its pose: walking (by {@code swing} and {@code amount}, as the game counts its steps), looking, at its age in ticks;
     * the skill it last began and the ticks since; how far it is into its crouch and its leap, its rearing to spit and its
     * roar (each 0 to 1); the ticks since its last bite; and whether it is berserk.
     */
    void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float crouch, float leap, float rear, float roar,
              float bite, boolean berserk) {
        reset();
        walk(swing, Math.min(1, amount * 1.6F));
        body.y += Mth.sin(age * .08F) * .25F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .6F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .5F;
        float open = .08F + .06F * Mth.sin(age * .1F);
        if (bite < 10) open = Math.max(open, Mth.sin(Math.max(0, bite) / 10 * Mth.PI));

        // The pounce: crouched through its wind-up, then its legs swept back and its maw agape through the leap.
        body.y += 3 * crouch;
        head.xRot += .3F * crouch - .3F * leap;
        open = Math.max(open, Math.max(.3F * crouch, leap));
        for (int side = 0; side < 2; side++) {
            float sign = side == 0 ? 1 : -1;
            for (ModelPart leg : legs[side]) {
                leg.zRot += sign * (.2F * crouch + .35F * leap);
                leg.yRot += sign * .5F * leap;
            }
        }

        // Its acid: reared through the wind-up, its head snapping down at each glob.
        body.xRot -= .22F * rear;
        head.xRot -= .3F * rear;
        open = Math.max(open, rear);
        if (posed == Skill.ACID.ordinal())
            for (int shot = 0; shot < AberrationRules.ACID.shots(); shot++) {
                float snap = pulse(since, AberrationRules.ACID.timing().windup() + shot * AberrationRules.ACID.every(), 5);
                head.xRot += .25F * snap;
            }

        // Its roar: reared, gaping, its head shaking.
        body.xRot -= .3F * roar;
        head.xRot -= .35F * roar;
        head.yRot += Mth.sin(age * 1.6F) * .18F * roar;
        open = Math.max(open, roar);

        // Berserk, its crystals swell and pulse, and it trembles.
        if (berserk) {
            float swell = 1.12F + .08F * Mth.sin(age * .7F);
            crystals.xScale = crystals.yScale = crystals.zScale = swell;
            body.x += Mth.sin(age * 2.1F) * .15F;
        } else crystals.yScale = 1 + .04F * Mth.sin(age * .12F);

        opened = open;
        mawTop.xRot -= .8F * open;
        mawBottom.xRot += .7F * open;
        mawSides[0].yRot += .7F * open;
        mawSides[1].yRot -= .7F * open;
    }

    /**
     * Its legs, three at a time: each swings back while it bears it and forward lifted, the first and last of one side
     * with the middle of the other; its body bobs at every step. {@code stride} is how hard it walks, 0 to 1.
     */
    private void walk(float swing, float stride) {
        float phase = swing * 1.1F;
        for (int side = 0; side < 2; side++) {
            float sign = side == 0 ? 1 : -1;
            for (int leg = 0; leg < 3; leg++) {
                float at = phase + (leg + side) % 2 * Mth.PI;
                legs[side][leg].yRot += sign * .45F * Mth.sin(at) * stride;
                legs[side][leg].zRot += sign * .35F * Math.max(0, -Mth.cos(at)) * stride;
            }
        }
        body.y += Mth.sin(phase * 2) * .4F * stride;
    }
}
