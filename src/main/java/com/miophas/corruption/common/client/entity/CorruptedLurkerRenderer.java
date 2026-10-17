package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.CorruptedLurker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Draws the corrupted lurker as every elite is drawn, tipped as it swims; and the tentacle it lashes out, from its maw to
 * what it reaches for or holds (LashingRenderer).
 */
public final class CorruptedLurkerRenderer extends LashingRenderer<CorruptedLurker, CorruptedLurkerModel> {
    public CorruptedLurkerRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedLurkerModel(context.bakeLayer(CorruptedLurkerModel.LAYER_LOCATION)), 1, "corrupted_lurker", true);
    }
}
