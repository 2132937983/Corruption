package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.Colossus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;

/**
 * Draws a colossus: its model at its scale, visible however far up it reaches (see {@link FarView}), and
 * sinking into the ground as it dies instead of tipping onto its side.
 */
public abstract class ColossusRenderer<T extends Colossus, M extends EntityModel<T>> extends MobRenderer<T, M> {
    /** Through the last part of its death it goes down into the ground by this share of its height. */
    private static final float SINK_FROM = .6F, SINK = .2F;
    private final float scale;
    private final double heightBlocks;

    protected ColossusRenderer(EntityRendererProvider.Context context, M model, float scale, double heightBlocks) {
        super(context, model, 0);
        this.scale = scale; this.heightBlocks = heightBlocks;
    }

    @Override public void render(T entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
        super.render(entity, yaw, partial, poses, FarView.wrap(buffers), light);
    }
    @Override protected RenderType getRenderType(T entity, boolean visible, boolean translucent, boolean glowing) {
        return visible ? FarView.body(getTextureLocation(entity)) : super.getRenderType(entity, visible, translucent, glowing);
    }
    @Override protected void scale(T entity, PoseStack poses, float partial) { poses.scale(scale, scale, scale); }
    @Override protected float getFlipDegrees(T entity) { return 0; }
    @Override protected void setupRotations(T entity, PoseStack poses, float age, float yaw, float partial, float scale) {
        super.setupRotations(entity, poses, age, yaw, partial, scale);
        float sunk = (entity.collapse(partial) - SINK_FROM) / (1 - SINK_FROM);
        if (sunk > 0) poses.translate(0, -sunk * SINK * heightBlocks, 0);
    }
}
