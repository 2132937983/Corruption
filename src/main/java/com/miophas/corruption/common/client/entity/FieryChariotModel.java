package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.FieryChariot;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Belial's chariot of fire. Its geometry is data (see {@link GeoModel}, made by gamemodels/belial/belial_model.py): a
 * car with a high front, two great spoked wheels, a pole and a yoke, and two horses of fire. This turns its wheels as far
 * as it has run, and sets its horses galloping by the same: each leg swinging in turn, the pair a little apart, their
 * heads tossing, their manes and tails flaring.
 */
public final class FieryChariotModel extends EntityModel<FieryChariot> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "fiery_chariot"), "main");
    static final String GEOMETRY = "fiery_chariot";
    /** A wheel's radius, in blocks (it turns once for every so far it runs), and how far a stride of the horses is. */
    static final float WHEEL = .75F, STRIDE = 1.6F;
    private static final String[] LEGS = {"front_left", "front_right", "hind_left", "hind_right"};
    /** Where in its stride each leg is, in turns: a gallop, the hind legs a little apart and the forelegs half a stride after them. */
    private static final float[] LEG_PHASE = {.5F, .62F, 0, .12F};

    // Seen by BelialModelTest.
    final ModelPart root;
    final ModelPart[] all, wheels = new ModelPart[2], horses = new ModelPart[2], necks = new ModelPart[2], heads = new ModelPart[2];
    final ModelPart[][] legs = new ModelPart[2][4];

    public FieryChariotModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        String[] sides = {"left", "right"};
        for (int side = 0; side < 2; side++) {
            wheels[side] = BelialFairModel.need(bones, "wheel_" + sides[side]);
            String horse = "horse_" + sides[side];
            horses[side] = BelialFairModel.need(bones, horse);
            necks[side] = BelialFairModel.need(bones, horse + "_neck");
            heads[side] = BelialFairModel.need(bones, horse + "_head");
            for (int leg = 0; leg < 4; leg++) legs[side][leg] = BelialFairModel.need(bones, horse + "_leg_" + LEGS[leg]);
        }
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public void setupAnim(FieryChariot chariot, float swing, float amount, float age, float yaw, float pitch) {
        pose(chariot.run(age - chariot.tickCount), age);
    }

    /** @param run how far it has run, in blocks; {@code age} in ticks, for what flickers whatever it does */
    public void pose(float run, float age) {
        for (ModelPart part : all) part.resetPose();
        for (ModelPart wheel : wheels) wheel.xRot = run / WHEEL;
        for (int side = 0; side < 2; side++) {
            float stride = run / STRIDE * Mth.TWO_PI + side * .9F;
            horses[side].y += .6F * Mth.abs(Mth.sin(stride));
            necks[side].xRot += .12F * Mth.sin(stride + .6F);
            heads[side].xRot += .15F * Mth.sin(stride + 1.2F);
            for (int leg = 0; leg < 4; leg++) legs[side][leg].xRot = .7F * Mth.sin(stride + LEG_PHASE[leg] * Mth.TWO_PI);
        }
    }

    @Override public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, int colour) {
        root.render(pose, buffer, light, overlay, colour);
    }
}
