package com.miophas.corruption.api.demon.client;

import com.miophas.corruption.api.demon.DemonBoss;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionfc;

/** Call from an add-on's renderer at the entity's feet, using the camera orientation. Client only. */
public final class BossDeathVisuals {
    @FunctionalInterface public interface Provider {
        void draw(LivingEntity boss, float partial, Quaternionfc camera, PoseStack pose, MultiBufferSource buffers, boolean far);
    }
    private static Provider provider;
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    public static <T extends LivingEntity & DemonBoss> void draw(T boss, float partial, Quaternionfc camera,
            PoseStack pose, MultiBufferSource buffers, boolean far) {
        if (provider == null) throw new IllegalStateException("Boss death rendering is unavailable before client setup");
        provider.draw(boss, partial, camera, pose, buffers, far);
    }
    public static <T extends LivingEntity & DemonBoss> boolean bodyVisible(T boss) { return boss.deathTime < boss.death().ticks(); }
    private BossDeathVisuals() {}
}
