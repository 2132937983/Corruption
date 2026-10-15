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
    /** The motes that drift out of a working arcane transmuter. */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORRUPTION_PARTICLE =
            TYPES.register("corruption_particle", () -> new SimpleParticleType(false));
    /** The sparks of stored Corruption Energy an arcane charger holds over itself. */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORRUPTION_SPARK_PARTICLE =
            TYPES.register("corruption_spark_particle", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
