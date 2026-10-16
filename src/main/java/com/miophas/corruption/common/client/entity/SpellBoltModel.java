package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.SpellProjectile;
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
 * The corruption's own spell bolt, as its spell draws it unless it names an item to be drawn as. Its geometry is data
 * (see {@link GeoModel}, built by gamemodels/spell_bolt/build_bolt.js); this finds the bones that move and poses them:
 * a crystal of light that throbs, in a shell of four dark shards turning slowly about it, two rings of runes turning
 * the faster, the one against the other, and a tail of light swaying behind.
 *
 * <p>It is drawn in three passes ({@link #only}): the dark shell, lit as the world is; then its light, unlit, in the
 * spell's colour (the bones named _glow); then the crystal's white-hot heart (the bones named _core).
 */
public final class SpellBoltModel extends HierarchicalModel<SpellProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "spell_bolt"), "main");
    static final String GEOMETRY = "spell_bolt";
    /** Which pieces a pass draws. */
    public static final int SHELL = 0, LIGHT = 1, HEART = 2;
    static final int FINS = 4;

    // Seen by SpellBoltModelTest, which poses the baked model and looks at where the bones went.
    final ModelPart root, crystal, ringFront, ringBack, tail, tailMiddle, tailEnd;
    final ModelPart[] all, fins, shell, light, heart;

    public SpellBoltModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        List<ModelPart> dark = new ArrayList<>(), glow = new ArrayList<>(), hot = new ArrayList<>();
        bones.forEach((name, part) -> (name.endsWith("_glow") ? glow : name.endsWith("_core") ? hot : dark).add(part));
        shell = dark.toArray(ModelPart[]::new);
        light = glow.toArray(ModelPart[]::new);
        heart = hot.toArray(ModelPart[]::new);
        crystal = DantalionBookModel.need(bones, "crystal");
        ringFront = DantalionBookModel.need(bones, "ring_front");
        ringBack = DantalionBookModel.need(bones, "ring_back");
        tail = DantalionBookModel.need(bones, "tail");
        tailMiddle = DantalionBookModel.need(bones, "tail_1");
        tailEnd = DantalionBookModel.need(bones, "tail_2");
        fins = new ModelPart[FINS];
        for (int index = 0; index < FINS; index++) fins[index] = DantalionBookModel.need(bones, "fin_" + index);
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    /** Draws only the pieces of this pass ({@link #SHELL}, {@link #LIGHT} or {@link #HEART}); any other draws them all. */
    public void only(int pass) {
        for (ModelPart part : shell) part.skipDraw = pass == LIGHT || pass == HEART;
        for (ModelPart part : light) part.skipDraw = pass == SHELL || pass == HEART;
        for (ModelPart part : heart) part.skipDraw = pass == SHELL || pass == LIGHT;
    }

    @Override public void setupAnim(SpellProjectile bolt, float swing, float amount, float age, float yaw, float pitch) {
        pose(age + bolt.getId() * 3.1F);
    }

    /** @param age ticks, its own: for the turning and throbbing that never stops */
    public void pose(float age) {
        for (ModelPart part : all) part.resetPose();
        // A positive Z turn here is about its line of flight; the rings turn against each other, the shell slowly.
        ringFront.zRot += age * .3F;
        ringBack.zRot -= age * .22F;
        for (ModelPart fin : fins) fin.zRot += age * .07F;
        float throb = 1 + .07F * Mth.sin(age * .6F);
        crystal.xScale = throb; crystal.yScale = throb; crystal.zScale = 1 + .04F * Mth.sin(age * .6F);
        tail.xRot += .12F * Mth.sin(age * .5F);
        tail.yRot += .12F * Mth.cos(age * .43F);
        tailMiddle.xRot += .18F * Mth.sin(age * .5F - .9F);
        tailMiddle.yRot += .18F * Mth.cos(age * .43F - .9F);
        tailEnd.xRot += .24F * Mth.sin(age * .5F - 1.8F);
        tailEnd.yRot += .24F * Mth.cos(age * .43F - 1.8F);
    }
}
