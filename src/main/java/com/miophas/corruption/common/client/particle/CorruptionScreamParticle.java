package com.miophas.corruption.common.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class CorruptionScreamParticle extends TextureSheetParticle {
    private static final int FRAME_COUNT = 11;
    private static final int TICKS_PER_FRAME = 2;
    private final SpriteSet sprites;

    public CorruptionScreamParticle(ClientLevel level, double x, double y, double z,
            double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.lifetime = FRAME_COUNT * TICKS_PER_FRAME;
        this.quadSize = 0.14F + random.nextFloat() * 0.06F;
        this.gravity = 0;
        this.friction = 0.98F;
        this.hasPhysics = false;
        this.setParticleSpeed(dx, dy, dz);
        this.setSprite(sprites.get(0, FRAME_COUNT - 1));
    }

    @Override
    public void tick() {
        super.tick();
        if (age >= lifetime) {
            remove();
            return;
        }
        setSprite(sprites.get(age / TICKS_PER_FRAME, FRAME_COUNT - 1));
        if (age >= 16) setAlpha((lifetime - age) / 6.0F);
    }

    @Override
    public int getLightColor(float partialTick) { return 240; }

    @Override
    public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                double dx, double dy, double dz) {
            return new CorruptionScreamParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
