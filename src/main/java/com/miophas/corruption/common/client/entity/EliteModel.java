package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.elite.CorruptionElite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * What the elites' models share: their geometry is data (see {@link GeoModel}, made by
 * gamemodels/corrupted_elites/elites_model.py), every bone of it found by name; each bone is put back as it was built
 * before it is posed; and a pose a skill gives eases in and out as the skill begins and ends ({@link #blend}), however
 * early the skill is over.
 */
public abstract class EliteModel<T extends CorruptionElite<T>> extends EntityModel<T> {
    // Seen by EliteModelTest.
    final ModelPart root;
    final ModelPart[] all;
    final Map<String, ModelPart> bones;
    /** Each elite's blends, by the key its model gives each, after the age it was last posed at. */
    private final Map<T, float[]> blends = new WeakHashMap<>();
    /** The elite being posed: its blends, and the ticks since it was last posed. */
    private float[] held = new float[17];
    private float since;

    EliteModel(ModelPart root, String geometry) {
        this.root = root;
        bones = GeoModel.load(geometry).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
    }

    /** A bone of the model, by name. */
    ModelPart need(String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException(getClass().getSimpleName() + " has no bone " + name);
        return bone;
    }

    /** Every bone put back as it was built, its scale too (resetPose leaves that). */
    void reset() {
        for (ModelPart part : all) {
            part.resetPose();
            part.xScale = part.yScale = part.zScale = 1;
        }
    }

    /** Begins posing an elite, its age in ticks with the partial tick: its blends, and how long since it was last posed. */
    void posing(T elite, float age) {
        held = blends.computeIfAbsent(elite, unused -> {
            float[] fresh = new float[17];
            fresh[0] = age;
            return fresh;
        });
        since = Mth.clamp(age - held[0], 0, 4);
        held[0] = age;
    }

    /**
     * How far into a pose the elite being posed is, 0 to 1, by a key of this model's own (0 to 15): easing towards 1
     * while {@code on}, towards 0 when not, at {@code rate} of the way a tick.
     */
    float blend(int key, boolean on, float rate) {
        int at = 1 + Mth.clamp(key, 0, 15);
        held[at] += ((on ? 1 : 0) - held[at]) * Math.min(1, since * rate);
        return held[at];
    }

    /** Smooth in and out between 0 and 1. */
    static float ease(float t) {
        t = Mth.clamp(t, 0, 1);
        return t * t * (3 - 2 * t);
    }

    /** A swell up and back down over a span of ticks, from its start: 0 outside it, 1 at its middle. */
    static float pulse(float ticks, float start, float length) {
        float at = (ticks - start) / length;
        return at <= 0 || at >= 1 ? 0 : Mth.sin(at * Mth.PI);
    }

    @Override public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, int colour) {
        root.render(pose, buffer, light, overlay, colour);
    }
}
