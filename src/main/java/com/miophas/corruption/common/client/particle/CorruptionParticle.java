package com.miophas.corruption.common.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * A mote of corruption, textures/particle/corruption_particle.png: it drifts off along the speed it is given,
 * rising a little, and shrinks and fades as it goes. A working arcane transmuter breathes them out.
 */
public final class CorruptionParticle extends TextureSheetParticle {
    private final float startSize;

    public CorruptionParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z);
        lifetime = 20 + random.nextInt(16);
        startSize = quadSize = .05F + random.nextFloat() * .06F;
        gravity = -.004F;
        friction = .93F;
        hasPhysics = false;
        setParticleSpeed(dx, dy, dz);
        pickSprite(sprites);
    }

    @Override public void tick() {
        super.tick();
        float life = (float) age / lifetime;
        quadSize = startSize * (1 - life * .6F);
        if (life > .6F) setAlpha((1 - life) / .4F);
    }

    /** Lit by its own corruption, not the world: it shows the same by night as by day. */
    @Override public int getLightColor(float partialTick) { return 240; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new CorruptionParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
