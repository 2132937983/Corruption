package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.core.demon.AndromaliusRules;
import com.miophas.corruption.core.demon.AndromaliusRules.Attack;
import com.miophas.corruption.common.entity.Andromalius;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Andromalius's rig. Its geometry is data (see {@link GeoModel}, built by gamemodels/andromalius/build_model.js);
 * this finds the bones that move and poses them.
 *
 * <p>As in every model of this space, a positive X turn leans an upright bone forward and dips one that points
 * forward, and a positive Y turn swings the head to its right and the tail to its left. The left hood (on the
 * model's +X) is drawn forward round the head by a positive Y turn, the right by a negative one.
 */
public final class AndromaliusModel extends HierarchicalModel<Andromalius> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "andromalius"), "main");
    static final String GEOMETRY = "andromalius";
    /** The bones a pose moves, by the names the build script gives them. */
    static final int TAIL = 12, NECK = 6;

    // Seen by AndromaliusModelTest, which poses the baked model and looks at where the bones went.
    final ModelPart root, body, head, jaw, tongue, leftHood, rightHood, tailTip;
    final ModelPart[] all, dull, tail, neck;

    public AndromaliusModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        List<ModelPart> unlit = new ArrayList<>();
        bones.forEach((name, part) -> { if (!name.endsWith("_glow")) unlit.add(part); });
        dull = unlit.toArray(ModelPart[]::new);
        body = need(bones, "body"); head = need(bones, "head"); jaw = need(bones, "jaw"); tongue = need(bones, "tongue");
        leftHood = need(bones, "left_hood"); rightHood = need(bones, "right_hood"); tailTip = need(bones, "tail_tip");
        tail = chain(bones, "tail", TAIL); neck = chain(bones, "neck", NECK);
    }

    private static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Andromalius's geometry has no bone " + name);
        return bone;
    }

    /** A run of bones each inside the last, named as the build script names them: name, name_1, name_2... */
    private static ModelPart[] chain(Map<String, ModelPart> bones, String name, int length) {
        ModelPart[] run = new ModelPart[length];
        for (int index = 0; index < length; index++) run[index] = need(bones, index == 0 ? name : name + "_" + index);
        return run;
    }

    @Override public ModelPart root() { return root; }
    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    /** Draws only the pieces that glow, or everything again. The glowing pieces are the bones named _glow. */
    public void glowOnly(boolean only) { for (ModelPart part : dull) part.skipDraw = only; }

    private static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    @Override public void setupAnim(Andromalius serpent, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - serpent.tickCount;
        Attack attack = serpent.attack();
        float ticks = serpent.attackTicks(partial);
        float dying = serpent.deathTime > 0 ? ease((serpent.deathTime + partial) / 20F) : 0;
        pose(age, swing, Mth.clamp(amount, 0, 1), attack, ticks, dying, yaw, pitch);
    }

    /**
     * @param age     ticks, for the motion that never stops
     * @param swing   how far it has slithered, as the walk animation counts it
     * @param moving  0 lying still to 1 slithering at full speed
     * @param ticks   ticks since its present attack began
     * @param dying   0 alive to 1 fallen
     * @param yaw     degrees its head is turned from its body
     * @param pitch   degrees its head looks down
     */
    public void pose(float age, float swing, float moving, Attack attack, float ticks, float dying, float yaw, float pitch) {
        for (ModelPart part : all) part.resetPose();
        float alive = 1 - dying;

        // The gaze: it rears back and spreads its hood wide round its head, then lowers its head to stare.
        float rear = attack == Attack.GAZE ? ease(ticks / 10) * (1 - ease((ticks - AndromaliusRules.GAZE_WINDUP) / 6)) : 0;
        float stare = attack == Attack.GAZE ? ease((ticks - AndromaliusRules.GAZE_WINDUP + 8) / 6) * (1 - ease((ticks - AndromaliusRules.GAZE_WINDUP) / 6)) : 0;
        // The volley: its jaws gape through the wind-up and stay open, its head jerking back at every pair it spits.
        float gape = attack == Attack.VOLLEY ? ease(ticks / 8) : 0;
        float firing = attack == Attack.VOLLEY && ticks >= AndromaliusRules.VOLLEY_WINDUP ? 1 : 0;
        float recoil = firing * Math.max(0, Mth.sin(ticks * Mth.PI));
        // The bite: he draws his head back through the wind-up, then strikes down and forward, jaws wide, and recovers.
        float coil = attack == Attack.BITE ? ease(ticks / AndromaliusRules.BITE_WINDUP) * (1 - ease((ticks - AndromaliusRules.BITE_WINDUP + 1) / 2)) : 0;
        float strike = attack == Attack.BITE ? ease((ticks - AndromaliusRules.BITE_WINDUP + 1) / 2) * (1 - ease((ticks - AndromaliusRules.BITE_WINDUP - 1) / 5)) : 0;
        // The tail: drawn round to one side through the wind-up, then whipped across to the other.
        float tailCoil = attack == Attack.TAIL ? ease(ticks / AndromaliusRules.TAIL_WINDUP) * (1 - ease((ticks - AndromaliusRules.TAIL_WINDUP) / 2)) : 0;
        float tailWhip = attack == Attack.TAIL ? ease((ticks - AndromaliusRules.TAIL_WINDUP + 1) / 3) * (1 - ease((ticks - AndromaliusRules.TAIL_TICKS + 4) / 4)) : 0;

        // Slithering: a wave runs down the tail, faster and wider as it moves; it never quite stops.
        float phase = swing * .55F + age * .06F, sway = (.07F + .20F * moving) * alive;
        for (int index = 0; index < tail.length; index++) {
            tail[index].yRot += Mth.sin(phase - index * .75F) * sway * (.6F + .4F * index / tail.length);
            tail[index].xRot += Mth.sin(age * .05F - index * .4F) * .015F * alive;
            // The sweep bends the whole tail into a hook, one way and then hard the other.
            tail[index].yRot += (.22F * tailCoil - .30F * tailWhip) * (index < 6 ? 1 : .6F);
        }
        tailTip.yRot += Mth.sin(phase - tail.length * .75F) * sway;
        // The barb at its end rattles now and then.
        tailTip.xRot += Mth.sin(age * 1.9F) * .12F * Math.max(0, Mth.sin(age * .07F)) * alive;
        body.yRot += Mth.sin(phase + .6F) * sway * .25F;

        // The forebody sways as a cobra's does, and rears or lunges for its attacks; dying, it falls forward.
        for (int index = 0; index < neck.length; index++) {
            neck[index].zRot += Mth.sin(age * .05F - index * .5F) * .03F * alive;
            neck[index].xRot += Mth.sin(age * .04F - index * .4F) * .02F * alive;
        }
        neck[1].xRot += -.22F * rear + .12F * gape + .05F * recoil + .55F * dying - .25F * coil + .40F * strike;
        neck[2].xRot += -.18F * rear + .08F * gape + .04F * recoil + .45F * dying - .20F * coil + .35F * strike;
        neck[3].xRot += -.10F * rear + .30F * dying - .10F * coil + .20F * strike;
        neck[0].xRot += .25F * dying + .15F * strike;
        head.xRot += .30F * stare - .18F * gape - .12F * recoil + .35F * dying - .30F * coil + .25F * strike;
        jaw.xRot += .04F + .02F * Mth.sin(age * .08F) + .75F * gape + .25F * dying + .55F * coil + .70F * strike;
        body.yRot += .12F * tailCoil - .18F * tailWhip;

        // The hood half spread at rest, drawn wide and forward to frame the head as it stares.
        float flare = Math.max(rear, stare * .9F);
        leftHood.yRot += .35F * flare + .03F * Mth.sin(age * .07F);
        rightHood.yRot -= .35F * flare + .03F * Mth.sin(age * .07F);
        float spread = 1 + .25F * flare;
        leftHood.xScale = spread; leftHood.yScale = spread; rightHood.xScale = spread; rightHood.yScale = spread;

        // The forked tongue flicks out every few seconds, while the jaws are not busy.
        float flick = Math.max(0, Mth.sin((age % 70) / 10F * Mth.PI)) * ((age % 70) < 10 ? 1 : 0) * (1 - Math.max(gape, Math.max(coil, strike))) * alive;
        tongue.z -= 3 * flick;
        tongue.xRot += Mth.sin(age * 2.2F) * .15F * flick;

        // Where it looks: shared by the upper neck and the head, the head taking the most.
        float turn = yaw * Mth.DEG_TO_RAD * alive, tilt = pitch * Mth.DEG_TO_RAD * alive;
        for (int index = 3; index < neck.length; index++) neck[index].yRot += turn * .15F;
        head.yRot += turn * .55F;
        head.xRot += tilt * .6F * (1 - stare);
    }
}
