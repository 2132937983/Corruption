package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedAberration;
import com.miophas.corruption.common.entity.CorruptedBlighthound;
import com.miophas.corruption.common.entity.CorruptedBloater;
import com.miophas.corruption.common.entity.CorruptedBrute;
import com.miophas.corruption.common.entity.CorruptedCrawler;
import com.miophas.corruption.common.entity.CorruptedGazer;
import com.miophas.corruption.common.entity.CorruptedHivemother;
import com.miophas.corruption.common.entity.CorruptedHowler;
import com.miophas.corruption.common.entity.CorruptedMaw;
import com.miophas.corruption.common.entity.CorruptedMindweaver;
import com.miophas.corruption.common.entity.CorruptedMortar;
import com.miophas.corruption.common.entity.CorruptedNaiad;
import com.miophas.corruption.common.entity.CorruptedScreecher;
import com.miophas.corruption.common.entity.CorruptedSower;
import com.miophas.corruption.common.entity.CorruptedStalker;
import com.miophas.corruption.common.entity.CorruptionElite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws one of the corruption's elites: as the light falls on it, and then what of it glows (its eyes, its crystals, its
 * veins, painted alone on its glow texture) drawn over it again, unlit. One that flies or swims is tipped as it goes (nose
 * up climbing, nose down diving), about the middle of it.
 */
public class CorruptionEliteRenderer<T extends CorruptionElite<T>, M extends EliteModel<T>> extends MobRenderer<T, M> {
    private final ResourceLocation texture, glow;
    private final boolean tipped;

    protected CorruptionEliteRenderer(EntityRendererProvider.Context context, M model, float shadow, String name, boolean tipped) {
        super(context, model, shadow);
        texture = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/" + name + ".png");
        glow = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/" + name + "_glow.png");
        this.tipped = tipped;
        addLayer(new Glow<>(this, glow));
    }

    /** The aberration's renderer: on the ground, as wide as it stands. */
    public static CorruptionEliteRenderer<CorruptedAberration, CorruptedAberrationModel> aberration(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedAberrationModel(context.bakeLayer(CorruptedAberrationModel.LAYER_LOCATION)), 1.1F,
                "corrupted_aberration", false);
    }

    /** The screecher's renderer: tipped as it flies. */
    public static CorruptionEliteRenderer<CorruptedScreecher, CorruptedScreecherModel> screecher(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedScreecherModel(context.bakeLayer(CorruptedScreecherModel.LAYER_LOCATION)), .8F,
                "corrupted_screecher", true);
    }

    /** The brute's renderer: on the ground, broad. */
    public static CorruptionEliteRenderer<CorruptedBrute, CorruptedBruteModel> brute(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedBruteModel(context.bakeLayer(CorruptedBruteModel.LAYER_LOCATION)), 1.1F, "corrupted_brute", false);
    }

    /** The howler's renderer: on the ground. */
    public static CorruptionEliteRenderer<CorruptedHowler, CorruptedHowlerModel> howler(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedHowlerModel(context.bakeLayer(CorruptedHowlerModel.LAYER_LOCATION)), .6F, "corrupted_howler", false);
    }

    /** The stalker's renderer: on the ground (faded, only its glow shows, as an invisible spider's eyes do). */
    public static CorruptionEliteRenderer<CorruptedStalker, CorruptedStalkerModel> stalker(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedStalkerModel(context.bakeLayer(CorruptedStalkerModel.LAYER_LOCATION)), .5F, "corrupted_stalker", false);
    }

    /** The bloater's renderer: on the ground, broad. */
    public static CorruptionEliteRenderer<CorruptedBloater, CorruptedBloaterModel> bloater(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedBloaterModel(context.bakeLayer(CorruptedBloaterModel.LAYER_LOCATION)), 1.1F, "corrupted_bloater", false);
    }

    /** The mortar's renderer: on the ground, broad. */
    public static CorruptionEliteRenderer<CorruptedMortar, CorruptedMortarModel> mortar(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedMortarModel(context.bakeLayer(CorruptedMortarModel.LAYER_LOCATION)), 1.2F, "corrupted_mortar", false);
    }

    /** The crawler's renderer: on the ground (or a wall). */
    public static CorruptionEliteRenderer<CorruptedCrawler, CorruptedCrawlerModel> crawler(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedCrawlerModel(context.bakeLayer(CorruptedCrawlerModel.LAYER_LOCATION)), .9F, "corrupted_crawler", false);
    }

    /** The gazer's renderer: tipped as it flies. */
    public static CorruptionEliteRenderer<CorruptedGazer, CorruptedGazerModel> gazer(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedGazerModel(context.bakeLayer(CorruptedGazerModel.LAYER_LOCATION)), .6F, "corrupted_gazer", true);
    }

    /** The hivemother's renderer: tipped as it flies. */
    public static CorruptionEliteRenderer<CorruptedHivemother, CorruptedHivemotherModel> hivemother(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedHivemotherModel(context.bakeLayer(CorruptedHivemotherModel.LAYER_LOCATION)), .7F, "corrupted_hivemother", true);
    }

    /** The maw's renderer: tipped as it swims. */
    public static CorruptionEliteRenderer<CorruptedMaw, CorruptedMawModel> maw(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedMawModel(context.bakeLayer(CorruptedMawModel.LAYER_LOCATION)), 1.1F, "corrupted_maw", true);
    }

    /** The naiad's renderer: tipped as it swims. */
    public static CorruptionEliteRenderer<CorruptedNaiad, CorruptedNaiadModel> naiad(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedNaiadModel(context.bakeLayer(CorruptedNaiadModel.LAYER_LOCATION)), .5F, "corrupted_naiad", true);
    }

    /** The mindweaver's renderer: on the ground, broad. */
    public static CorruptionEliteRenderer<CorruptedMindweaver, CorruptedMindweaverModel> mindweaver(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedMindweaverModel(context.bakeLayer(CorruptedMindweaverModel.LAYER_LOCATION)), 1.0F, "corrupted_mindweaver", false);
    }

    /** The blighthound's renderer: on the ground. */
    public static CorruptionEliteRenderer<CorruptedBlighthound, CorruptedBlighthoundModel> blighthound(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedBlighthoundModel(context.bakeLayer(CorruptedBlighthoundModel.LAYER_LOCATION)), .5F, "corrupted_blighthound", false);
    }

    /** The sower's renderer: on the ground, broad. */
    public static CorruptionEliteRenderer<CorruptedSower, CorruptedSowerModel> sower(EntityRendererProvider.Context context) {
        return new CorruptionEliteRenderer<>(context, new CorruptedSowerModel(context.bakeLayer(CorruptedSowerModel.LAYER_LOCATION)), 1.1F, "corrupted_sower", false);
    }

    @Override public ResourceLocation getTextureLocation(T elite) { return texture; }

    /** Tipped as it flies or swims, about the middle of it. */
    @Override protected void setupRotations(T elite, PoseStack pose, float bob, float yaw, float partial, float scale) {
        super.setupRotations(elite, pose, bob, yaw, partial, scale);
        float pitch = Mth.lerp(partial, elite.xRotO, elite.getXRot());
        if (!tipped || pitch == 0 || elite.deathTime > 0) return;
        float middle = elite.getBbHeight() * .5F;
        pose.translate(0, middle, 0);
        pose.mulPose(Axis.XP.rotationDegrees(-pitch));
        pose.translate(0, -middle, 0);
    }

    /** Its glow texture, drawn over all of it again, unlit: only what is painted on it shows. */
    private static final class Glow<T extends CorruptionElite<T>, M extends EliteModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation glow;

        Glow(RenderLayerParent<T, M> renderer, ResourceLocation glow) {
            super(renderer);
            this.glow = glow;
        }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, T elite, float swing, float amount, float partial, float age, float yaw,
                                     float pitch) {
            getParentModel().renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(glow)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }
}
