package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedCrawler;
import com.miophas.corruption.core.elite.CrawlerRules;
import com.miophas.corruption.core.elite.CrawlerRules.Skill;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The corrupted crawler. Its legs ripple as it goes, a wave running down them from its head, its body winding from side
 * to side and its stinger swaying; its mandibles snap on what it bites; it draws its head down to spring and sweeps its
 * legs back through the leap; and it rears its head to spit, mandibles wide.
 */
public final class CorruptedCrawlerModel extends PosedEliteModel<CorruptedCrawler> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_crawler"), "main");
    static final String GEOMETRY = "corrupted_crawler";
    static final int SEGMENTS = 5;

    final ModelPart head, tail, mandibleLeft, mandibleRight;
    final ModelPart[] segments = new ModelPart[SEGMENTS];
    final ModelPart[][] legs = new ModelPart[2][SEGMENTS];

    public CorruptedCrawlerModel(ModelPart root) {
        super(root, GEOMETRY, CrawlerRules.SKILLS.size());
        head = need("head");
        tail = need("tail");
        mandibleLeft = need("mandible_left");
        mandibleRight = need("mandible_right");
        for (int index = 0; index < SEGMENTS; index++) {
            segments[index] = need("segment_" + index);
            legs[0][index] = need("leg_left_" + index);
            legs[1][index] = need("leg_right_" + index);
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state) {
        reset();
        float go = Math.min(1, amount * 1.5F);
        for (int index = 0; index < SEGMENTS; index++) {
            // Each leg forward (a turn about Y takes a left leg's tip back) and lifted as it swings forward; the
            // right legs half a step after the left.
            float left = swing * 1.4F - index * .9F, right = left + Mth.PI;
            legs[0][index].yRot -= Mth.sin(left) * .45F * go;
            legs[1][index].yRot += Mth.sin(right) * .45F * go;
            legs[0][index].zRot += Math.max(0, Mth.cos(left)) * .3F * go;
            legs[1][index].zRot -= Math.max(0, Mth.cos(right)) * .3F * go;
            segments[index].yRot += Mth.sin(age * .15F - index * .7F) * (.03F + .05F * go);
        }
        tail.yRot += Mth.sin(age * .1F) * .2F;
        tail.xRot += Mth.sin(age * .13F) * .1F;
        head.yRot += yaw * Mth.DEG_TO_RAD * .4F;
        float open = .1F + .1F * Mth.sin(age * .3F);
        open = Math.max(open, strike(bite));

        // Its spring: head drawn down through the wind-up; its legs swept back in the leap.
        float pounce = on[Skill.POUNCE.ordinal()];
        int crouch = CrawlerRules.POUNCE.timing().windup();
        float down = posed == Skill.POUNCE.ordinal() && since < crouch ? gathering(since, crouch) : 0;
        head.xRot += .35F * down * pounce;
        for (int index = 0; index < SEGMENTS; index++) {
            legs[0][index].yRot += .5F * (1 - down) * pounce;
            legs[1][index].yRot -= .5F * (1 - down) * pounce;
        }
        open = Math.max(open, (1 - down) * pounce);

        // Its venom: head reared, mandibles wide.
        float venom = on[Skill.VENOM.ordinal()];
        head.xRot -= .5F * venom;
        open = Math.max(open, venom);

        mandibleLeft.yRot += .6F * open;
        mandibleRight.yRot -= .6F * open;
    }
}
