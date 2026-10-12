package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.RemnantAnimationDriver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Which source types replay their own attack animation, and how. */
public final class RemnantAnimationRules {
    /** Vanilla's conventional "mob attacked" entity event, which many native and modded mobs animate from. */
    public static final byte ATTACK_EVENT = 4;
    public static final int DEFAULT_DURATION = 40;
    private static final RemnantAnimationDriver DEFAULT = (source, event) -> source.handleEntityEvent(event);
    private static final RemnantAnimationDriver DISABLED = (source, event) -> {};
    private static final Map<String, RemnantAnimationDriver> DRIVERS = new ConcurrentHashMap<>();

    /** Registered drivers win; unregistered modded sources use the default event driver; vanilla sources keep their dedicated models. */
    public static RemnantAnimationDriver driver(String source) {
        var registered = DRIVERS.get(source);
        if (registered != null) return registered == DISABLED ? null : registered;
        return source.startsWith("minecraft:") ? null : DEFAULT;
    }
    public static void register(String source, RemnantAnimationDriver driver) { DRIVERS.put(source, driver); }
    public static void disable(String source) { DRIVERS.put(source, DISABLED); }
    public static void unregister(String source) { DRIVERS.remove(source); }

    private RemnantAnimationRules() {}
}
