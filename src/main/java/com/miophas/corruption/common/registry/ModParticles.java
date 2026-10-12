package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Corruption.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORRUPTION_SCREAM =
            TYPES.register("corruption_scream", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
