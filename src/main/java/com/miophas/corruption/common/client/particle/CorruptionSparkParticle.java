package com.miophas.corruption.common.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * A spark of Corruption Energy, textures/particle/corruption_spark_particle.png: small, bright and weightless,
 * it goes where it is sent, slows to a stop and fades. The arcane charger holds a few over itself to show the
 * energy in its store, and sends them into the item it charges.
 *
 * <p>It lasts long enough to still be there when the block is next asked to draw, so sparks set out again and
 * again read as a ring standing over the charger rather than as a flicker.
 */
public final class CorruptionSparkParticle extends TextureSheetParticle {
    /** How wide a spark is drawn, before a little variety is added. */
    private static final float START_SIZE = .16F;

    public CorruptionSparkParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z);
        lifetime = 16 + random.nextInt(8);
        quadSize = START_SIZE * (.75F + random.nextFloat() * .5F);
        gravity = 0F;
        friction = .93F;
        hasPhysics = false;
        setParticleSpeed(dx, dy, dz);
        pickSprite(sprites);
    }

    @Override public void tick() {
        super.tick();
        float life = (float) age / lifetime;
        if (life > .35F) setAlpha((1 - life) / .65F);
    }

    /** Lit by its own charge, not the world: it shows the same by night as by day. */
    @Override public int getLightColor(float partialTick) { return 240; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new CorruptionSparkParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
