package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedBrute;
import com.miophas.corruption.core.elite.BruteRules;
import com.miophas.corruption.core.elite.BruteRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted brute. It lumbers, its arms swinging against its legs; it brings its club down for its basic blow; it
 * raises its club high and smashes it down (its slam); it leans into its charge, arms back, legs pounding; and it reaches
 * its club down and back over its shoulder to hurl, and lets fly.
 */
public final class CorruptedBruteModel extends PosedEliteModel<CorruptedBrute> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_brute"), "main");
    static final String GEOMETRY = "corrupted_brute";

    final ModelPart body, head, jaw, club, clubFore, claw, clawFore, legLeft, legRight;

    public CorruptedBruteModel(ModelPart root) {
        super(root, GEOMETRY, BruteRules.SKILLS.size());
        body = need("body");
        head = need("head");
        jaw = need("jaw");
        club = need("club");
        clubFore = need("club_fore");
        claw = need("claw");
        clawFore = need("claw_fore");
        legLeft = need("leg_left");
        legRight = need("leg_right");
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float charge = on[Skill.CHARGE.ordinal()];
        walk(legLeft, legRight, swing * (1 + charge), amount, .9F);
        walk(club, claw, swing * (1 + charge), amount, .35F);
        body.y += Mth.cos(swing * 1.3F) * amount * .6F + Mth.sin(age * .07F) * .3F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .4F;
        head.xRot += pitch * Mth.DEG_TO_RAD * .4F;

        // Its basic blow: the club brought down.
        float blow = strike(bite);
        club.xRot -= 1.6F * blow;
        clubFore.xRot -= .4F * blow;
        jaw.xRot += .3F * blow;

        // Its slam: the club raised high through the wind-up, smashed down at its end.
        float slam = on[Skill.SLAM.ordinal()];
        int slamUp = BruteRules.SLAM.timing().windup();
        float raised = posed == Skill.SLAM.ordinal() ? since < slamUp ? gathering(since, slamUp) : Math.max(0, 1 - (since - slamUp) / 3) : 0;
        club.xRot -= slam * (2.8F * raised + .8F * (1 - raised));
        clubFore.xRot -= slam * .6F * raised;
        body.xRot -= slam * .25F * raised - slam * .2F * (1 - raised);
        jaw.xRot += slam * .5F;

        // Its charge: leaning in, head down, arms swept back.
        body.xRot += .4F * charge;
        head.xRot += .2F * charge;
        club.xRot += .7F * charge;
        claw.xRot += .7F * charge;

        // Its hurl: the club reached back over its shoulder, then flung forward.
        float hurl = on[Skill.HURL.ordinal()];
        int hurlUp = BruteRules.HURL.timing().windup();
        float drawn = posed == Skill.HURL.ordinal() && since < hurlUp ? gathering(since, hurlUp) : 0;
        club.xRot -= hurl * (3.6F * drawn + 1.2F * (1 - drawn));
        club.zRot -= hurl * .3F * drawn;
        body.yRot += hurl * .3F * drawn;
    }
}
