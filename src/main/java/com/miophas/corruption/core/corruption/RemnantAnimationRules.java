package com.miophas.corruption.core.corruption;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Pure selection policy; drivers are opaque values supplied by the client-facing adapter. */
public final class RemnantAnimationRules<T> {
    public static final byte ATTACK_EVENT = 4;
    public static final int DEFAULT_DURATION = 40;
    private final T fallback;
    private final Map<String, Optional<T>> drivers = new ConcurrentHashMap<>();
    public RemnantAnimationRules(T fallback) { this.fallback = Objects.requireNonNull(fallback); }
    public T driver(String source) {
        Optional<T> registered = drivers.get(source);
        return registered != null ? registered.orElse(null) : source.startsWith("minecraft:") ? null : fallback;
    }
    public void register(String source, T driver) { drivers.put(source, Optional.of(driver)); }
    public void disable(String source) { drivers.put(source, Optional.empty()); }
    public void unregister(String source) { drivers.remove(source); }
}
