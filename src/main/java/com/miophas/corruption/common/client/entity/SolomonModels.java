package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Solomon's shapes (gamemodels/solomon: assets/corruption/geo/solomon_*.json) and their textures, and how each is set in the
 * world to be drawn. Every shape is built facing -Z; the dragon's, the hand's and the crown's at half size, drawn twice as
 * large ({@link #HALF}).
 */
public final class SolomonModels {
    public static final ModelLayerLocation HEAD = layer("solomon_wyrm_head"), BODY = layer("solomon_wyrm_body"), LEGS = layer("solomon_wyrm_legs"),
            TAIL = layer("solomon_wyrm_tail"), HAND = layer("solomon_hand"), DRAKE = layer("solomon_drake"), CROWN = layer("solomon_crown");
    private static final List<ModelLayerLocation> ALL = List.of(HEAD, BODY, LEGS, TAIL, HAND, DRAKE, CROWN);

    public static final ResourceLocation WYRM = texture("solomon_wyrm"), WYRM_GLOW = texture("solomon_wyrm_glow");
    public static final ResourceLocation HAND_TEXTURE = texture("solomon_hand"), HAND_GLOW = texture("solomon_hand_glow");
    public static final ResourceLocation DRAKE_TEXTURE = texture("solomon_drake"), DRAKE_GLOW = texture("solomon_drake_glow");
    public static final ResourceLocation CROWN_TEXTURE = texture("solomon_crown"), CROWN_GLOW = texture("solomon_crown_glow");
    /** The octagram his rings are drawn with: the octagram altar's own circle. */
    public static final ResourceLocation SEAL = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/misc/altar_circle_octagram.png");

    /** The size the half-size shapes are drawn at. */
    public static final float HALF = 2;

    private static ModelLayerLocation layer(String name) { return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, name), "main"); }
    private static ResourceLocation texture(String name) { return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/" + name + ".png"); }

    /** Each shape's layer, read from its geometry. */
    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (ModelLayerLocation layer : ALL) event.registerLayerDefinition(layer, () -> GeoModel.load(layer.getModel().getPath()).layer());
    }

    /**
     * Sets the pose to draw a shape built facing -Z with its middle at this point, facing as a creature with this yaw and pitch
     * faces (the game's sense: yaw 0 south, pitch positive looking down), {@code across} times its built size across and up and
     * {@code along} times along.
     */
    public static void place(PoseStack pose, double x, double y, double z, float yaw, float pitch, float across, float along) {
        pose.translate(x, y, z);
        pose.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        pose.mulPose(Axis.XP.rotationDegrees(-pitch));
        pose.scale(-across, -across, along);
        // The shapes' geometry is read with its root 24 pixels up, as Blockbench's export has it: brought back to the middle.
        pose.translate(0, -1.5, 0);
    }

    private SolomonModels() {}
}
