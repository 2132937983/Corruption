package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantAnimationRules;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/** Source animation policy and its public client playback port. */
public final class RemnantAnimationApi {
    public static final byte ATTACK_EVENT = RemnantAnimationRules.ATTACK_EVENT;
    public interface Playback {
        void start(LivingEntity source, byte event);
        void tick(LivingEntity source);
    }
    private static volatile Playback playback;
    private static Playback playback() {
        Playback current = playback;
        if (current == null) throw new IllegalStateException("Source animation playback must be installed during client setup");
        return current;
    }
    /** Installed by the client adapter; neither the core nor dedicated servers load that implementation. */
    public static void installPlayback(Playback implementation) { playback = Objects.requireNonNull(implementation); }
    private static final RemnantAnimationRules<RemnantAnimationDriver> DRIVERS =
            new RemnantAnimationRules<>((source, event) -> playback().start(source, event));
    public static void register(ResourceLocation source, RemnantAnimationDriver driver) { DRIVERS.register(source.toString(), driver); }
    public static void disable(ResourceLocation source) { DRIVERS.disable(source.toString()); }
    public static void unregister(ResourceLocation source) { DRIVERS.unregister(source.toString()); }
    public static RemnantAnimationDriver driver(String source) { return DRIVERS.driver(source); }
    public static void tickSource(LivingEntity source) { playback().tick(source); }
    private RemnantAnimationApi() {}
}
