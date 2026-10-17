package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.CorruptedDrifter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Draws the corrupted drifter as every elite is drawn, tipped as it drifts; and the tendril it reaches out, from under its
 * bell to what it reaches for or lifts (LashingRenderer).
 */
public final class CorruptedDrifterRenderer extends LashingRenderer<CorruptedDrifter, CorruptedDrifterModel> {
    public CorruptedDrifterRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedDrifterModel(context.bakeLayer(CorruptedDrifterModel.LAYER_LOCATION)), .8F, "corrupted_drifter", true);
    }
}
