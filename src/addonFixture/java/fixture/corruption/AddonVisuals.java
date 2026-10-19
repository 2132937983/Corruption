package fixture.corruption;

import com.miophas.corruption.api.corruption.client.CorruptedTextures;
import com.miophas.corruption.api.demon.DemonBoss;
import com.miophas.corruption.api.demon.client.BossDeathVisuals;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionfc;

/** Client-side consumer; never initialize this class from a dedicated server entry point. */
public final class AddonVisuals {
    public static ResourceLocation skin(ResourceLocation source) { return CorruptedTextures.texture(source); }
    public static RenderType skinLayer(RenderType source) { return CorruptedTextures.layer(source); }
    public static <T extends LivingEntity & DemonBoss> void death(T boss, float partial, Quaternionfc camera, PoseStack pose, MultiBufferSource buffers) {
        BossDeathVisuals.draw(boss, partial, camera, pose, buffers, false);
    }
    private AddonVisuals() {}
}
