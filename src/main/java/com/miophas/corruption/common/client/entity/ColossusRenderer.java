package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Colossus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;

/**
 * Draws a colossus: its model at its scale, visible however far up it reaches (see {@link FarView}), and
 * sinking into the ground as it dies instead of tipping onto its side; and its death about it as it dies
 * ({@link DeathVisual}), seen from as far off as it is. Its body is gone at the end of its death.
 */
public abstract class ColossusRenderer<T extends Colossus, M extends EntityModel<T>> extends MobRenderer<T, M> {
    private final float scale;

    protected ColossusRenderer(EntityRendererProvider.Context context, M model, float scale) {
        super(context, model, 0);
        this.scale = scale;
    }

    @Override public void render(T entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
        MultiBufferSource far = FarView.wrap(buffers);
        if (entity.deathTime < entity.death().ticks()) super.render(entity, yaw, partial, poses, far, light);
        if (entity.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(entity, partial, entityRenderDispatcher.cameraOrientation()), poses, far, true);
    }
    @Override protected RenderType getRenderType(T entity, boolean visible, boolean translucent, boolean glowing) {
        return visible ? FarView.body(getTextureLocation(entity)) : super.getRenderType(entity, visible, translucent, glowing);
    }
    @Override protected void scale(T entity, PoseStack poses, float partial) { poses.scale(scale, scale, scale); }
    @Override protected float getFlipDegrees(T entity) { return 0; }
    @Override protected void setupRotations(T entity, PoseStack poses, float age, float yaw, float partial, float scale) {
        super.setupRotations(entity, poses, age, yaw, partial, scale);
        double sunk = entity.sunkBlocks(partial);
        if (sunk > 0) poses.translate(0, -sunk, 0);
    }
}
