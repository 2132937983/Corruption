package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.geom.ModelPart;

/** A model with a lash of its own (a lurker's tentacle, a drifter's tendril): a piece of it and its end, drawn again and again along the lash. */
public interface LashModel {
    ModelPart lash();
    ModelPart lashEnd();
}
