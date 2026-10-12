package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Used only by livestock-origin remnants; original animal renderers remain registered. */
public final class CorruptedLivestockRenderer extends MobRenderer<Mob, CorruptedLivestockModel> {
    private final CorruptedLivestockModel.Kind kind;
    private final ResourceLocation texture;
    public static ResourceLocation texture(CorruptedLivestockModel.Kind kind) {return asset(kind, "");}
    private static ResourceLocation asset(CorruptedLivestockModel.Kind kind, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/corrupted_" + kind.id + suffix + ".png");
    }
    public CorruptedLivestockRenderer(EntityRendererProvider.Context context, CorruptedLivestockModel.Kind kind) {
        super(context, new CorruptedLivestockModel(context.bakeLayer(kind.layer), kind), kind == CorruptedLivestockModel.Kind.CHICKEN ? .3F : .6F);
        this.kind = kind; texture = texture(kind);
        addLayer(new EyesLayer<Mob, CorruptedLivestockModel>(this) {
            @Override public RenderType renderType() {return RenderType.eyes(asset(kind, "_eyes"));}
        });
    }
    @Override protected void scale(Mob entity, PoseStack poses, float partial) {
        float scale = (entity.isBaby() ? .5F : 1) * (kind == CorruptedLivestockModel.Kind.DONKEY ? .87F : 1);
        poses.scale(scale, scale, scale);
    }
    @Override public ResourceLocation getTextureLocation(Mob entity) {return texture;}
}
