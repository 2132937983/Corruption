package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.RemnantAnimationApi;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.mojang.logging.LogUtils;
import java.util.WeakHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/** Replays a remnant's attacks on its never-ticking source proxy so the source mob's own animation plays. */
final class RemnantAttackAnimations {
    private static final class Session { int age = -1, lastTick; boolean failed; }
    private static final WeakHashMap<LivingEntity, Session> SESSIONS = new WeakHashMap<>();

    static void update(LivingEntity source, CorruptedRemnant remnant) {
        var driver = RemnantAnimationApi.driver(BuiltInRegistries.ENTITY_TYPE.getKey(source.getType()).toString());
        Session session = driver == null ? null : SESSIONS.computeIfAbsent(source, key -> new Session());
        if (session == null || session.failed) {
            while (remnant.pollRelayedEvent() != null) { /* nothing consumes these for this source */ }
            return;
        }
        try {
            Byte event;
            while ((event = remnant.pollRelayedEvent()) != null) {
                driver.start(source, event);
                session.age = 0; session.lastTick = remnant.tickCount;
            }
            if (session.age < 0) return;
            int steps = Mth.clamp(remnant.tickCount - session.lastTick, 0, 3);
            session.lastTick = remnant.tickCount;
            for (int i = 0; i < steps && session.age >= 0; i++)
                if (!driver.tick(source, session.age) || ++session.age >= driver.duration()) session.age = -1;
        } catch (RuntimeException failure) {
            session.failed = true; session.age = -1;
            LogUtils.getLogger().warn("Disabling attack animation replay for remnant appearance {}", BuiltInRegistries.ENTITY_TYPE.getKey(source.getType()), failure);
        }
    }

    private RemnantAttackAnimations() {}
}
