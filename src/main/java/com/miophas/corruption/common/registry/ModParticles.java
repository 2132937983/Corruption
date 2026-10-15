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
    /** The sparks of Corruption Energy an arcane charger sends into the item it charges. */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORRUPTION_SPARK_PARTICLE =
            TYPES.register("corruption_spark_particle", () -> new SimpleParticleType(false));
    /**
     * Andromalius's eye, flashed before one player's eyes as an elder guardian's face is: sent to that player alone,
     * at their own position, and drawn over their view (GazeParticle).
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ANDROMALIUS_GAZE =
            TYPES.register("andromalius_gaze", () -> new SimpleParticleType(true));
    /** How long a spark flies, in ticks: one sent this many times its speed away arrives as it goes out. */
    public static final int SPARK_FLIGHT = 8;

    private ModParticles() {}
}
