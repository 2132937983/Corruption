package com.miophas.corruption.common.client.visual;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.Agares;
import com.miophas.corruption.common.entity.Andromalius;
import com.miophas.corruption.common.entity.Dantalion;
import com.miophas.corruption.common.entity.Baal;
import com.miophas.corruption.common.entity.BossDeath;
import com.miophas.corruption.common.entity.Seere;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Every boss's death, and the ordinary one a demon of an add-on's dies, drawn tick by tick from its fall to its going
 * with its heart where the boss's is, seen from a camera turned at a slant, into buffers that behave as the game's do
 * (see {@link SharedBuffers}): it never draws into a finished buffer; it draws whole faces at real places, all within
 * reach of the boss for the size of its death; its light shows all through its dying; every moment of it draws, at the
 * size it is made; and nothing is drawn once it is over.
 */
class DeathVisualDrawTest {
    private static final Quaternionf CAMERA = new Quaternionf().rotationYXZ((float) Math.PI - .7F, -.45F, 0);

    @BeforeAll static void effects() { SpellVisualRenderer.registerBuiltIn(); }

    /** Each boss's death, with its heart about where the boss's is: Baal's chest, Agares's, Andromalius's middle, the middle of Dantalion's glass, Seere's breast, Amdusias's. */
    private static Map<String, Object[]> deaths() {
        Map<String, Object[]> deaths = new LinkedHashMap<>();
        deaths.put("Baal's", new Object[]{Baal.DEATH, new Vector3f(0, 115, 8)});
        deaths.put("Agares's", new Object[]{Agares.DEATH, new Vector3f(0, 480, 90)});
        deaths.put("Andromalius's", new Object[]{Andromalius.DEATH, new Vector3f(0, 2.4F, 0)});
        deaths.put("Dantalion's", new Object[]{Dantalion.DEATH, new Vector3f(0, 1.76F, 0)});
        deaths.put("Seere's", new Object[]{Seere.DEATH, new Vector3f(0, 1.6F, .6F)});
        deaths.put("Amdusias's", new Object[]{com.miophas.corruption.common.entity.Amdusias.DEATH, new Vector3f(0, 1.6F, .6F)});
        deaths.put("an add-on's", new Object[]{BossDeath.ordinary(4, SpellAction.Visual.ROSE, SpellAction.Visual.GOLD), new Vector3f(0, 2.4F, 0)});
        return deaths;
    }

    /** All a death may reach: its rays round its heart, and every moment's own reach, made as large as the death says. */
    private static AABB reach(BossDeath death, Vector3f heart) {
        float scale = death.scale(), rays = death.rays().length() * scale * 1.1F + 1;
        AABB box = new AABB(heart.x - rays, Math.min(0, heart.y - rays), heart.z - rays, heart.x + rays, heart.y + rays, heart.z + rays);
        for (BossDeath.Beat beat : death.beats()) {
            SpellAction.Visual visual = beat.visual();
            boolean atHeart = beat.at() == BossDeath.Anchor.HEART;
            AABB own = SpellVisualEntity.reach(0, 0, 0, (float) visual.radius(), (float) visual.height() + (atHeart ? heart.y / scale : 0));
            own = new AABB(own.minX * scale, own.minY * scale, own.minZ * scale, own.maxX * scale, own.maxY * scale, own.maxZ * scale);
            box = box.minmax(atHeart ? own.move(heart.x, 0, heart.z) : own);
        }
        return box.inflate(1e-3);
    }

    private static SharedBuffers drawn(BossDeath death, Vector3f heart, float time) {
        SharedBuffers kept = new SharedBuffers();
        DeathVisual.draw(new DeathVisual.Dying(death, time, heart, 37, 99L, CAMERA), new PoseStack(), kept, false);
        return kept;
    }

    @Test void everyDeathDrawsWithinItsReachFromFallToGoing() {
        for (Map.Entry<String, Object[]> entry : deaths().entrySet()) {
            BossDeath death = (BossDeath) entry.getValue()[0];
            Vector3f heart = (Vector3f) entry.getValue()[1];
            AABB reach = reach(death, heart);
            for (float time = .5F; time < death.lasts() + 3; time += 1) {
                SharedBuffers kept = drawn(death, heart, time);
                String what = entry.getKey() + " death at " + time + " of " + death.lasts();
                assertEquals(0, kept.corners.size() % 4, what + " draws whole faces");
                assertEquals(kept.corners.size(), kept.textured);
                for (float[] corner : kept.corners) {
                    assertTrue(Float.isFinite(corner[0]) && Float.isFinite(corner[1]) && Float.isFinite(corner[2]), what + " draws at real places");
                    assertTrue(reach.contains(corner[0], corner[1], corner[2]), what + " draws within its reach: " + corner[0] + ", " + corner[1] + ", " + corner[2]);
                }
                if (time < death.ticks()) assertFalse(kept.corners.isEmpty(), what + ": its light shows while it dies");
                if (time >= death.lasts()) assertTrue(kept.corners.isEmpty(), what + ": nothing once it is over");
            }
        }
    }

    @Test void everyMomentOfEveryDeathDrawsAtItsSize() {
        BossDeath.Rays none = new BossDeath.Rays(0, 0, 0, 0);
        for (Map.Entry<String, Object[]> entry : deaths().entrySet()) {
            BossDeath death = (BossDeath) entry.getValue()[0];
            Vector3f heart = (Vector3f) entry.getValue()[1];
            for (BossDeath.Beat beat : death.beats()) {
                BossDeath alone = new BossDeath(death.ticks(), death.linger(), death.scale(), none, 0, List.of(beat));
                SharedBuffers kept = drawn(alone, heart, beat.tick() + beat.visual().ticks() * .3F);
                assertFalse(kept.corners.isEmpty(), entry.getKey() + " " + beat.visual().effect() + " at " + beat.tick() + " draws");
                float widest = 0;
                for (float[] corner : kept.corners) widest = Math.max(widest, Math.max(Math.abs(corner[0] - (beat.at() == BossDeath.Anchor.HEART ? heart.x : 0)),
                        Math.abs(corner[2] - (beat.at() == BossDeath.Anchor.HEART ? heart.z : 0))));
                assertTrue(widest >= beat.visual().radius() * death.scale() * .1, entry.getKey() + " " + beat.visual().effect() + " is drawn as large as its death makes it: " + widest);
            }
        }
    }
}
