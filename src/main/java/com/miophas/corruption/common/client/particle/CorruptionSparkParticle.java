package com.miophas.corruption.common.client.particle;

import com.miophas.corruption.common.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * A spark of Corruption Energy, textures/particle/corruption_spark_particle.png: a small point of light that
 * flies straight and steady for {@link ModParticles#SPARK_FLIGHT} ticks and is gone, shrinking and fading as it
 * arrives. Whoever sends one sends it at the speed that brings it to its mark in that time: the arcane charger
 * sends them from the ring of sparks about the item it charges into that item.
 */
public final class CorruptionSparkParticle extends TextureSheetParticle {
    /** How wide a spark is drawn as it sets out, before a little variety is added. */
    private static final float START_SIZE = .055F;

    private final float startSize;

    public CorruptionSparkParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z);
        lifetime = ModParticles.SPARK_FLIGHT;
        startSize = quadSize = START_SIZE * (.85F + random.nextFloat() * .3F);
        gravity = 0F;
        friction = 1F;
        hasPhysics = false;
        setParticleSpeed(dx, dy, dz);
        pickSprite(sprites);
    }

    /** It keeps its pace, and shrinks and fades through the last part of its flight. */
    @Override public void tick() {
        super.tick();
        float life = Math.min(1F, (float) age / lifetime);
        quadSize = startSize * (1 - life * .55F);
        setAlpha(life < .5F ? 1F : (1 - life) / .5F);
    }

    /** Lit by its own charge, not the world: it shows the same by night as by day. */
    @Override public int getLightColor(float partialTick) { return LightTexture.FULL_BRIGHT; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new CorruptionSparkParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
