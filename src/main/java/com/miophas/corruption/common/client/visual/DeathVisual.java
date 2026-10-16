package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.miophas.corruption.api.arcane.client.SpellVisuals;
import com.miophas.corruption.common.client.entity.FarView;
import com.miophas.corruption.common.entity.BossDeath;
import com.miophas.corruption.common.entity.DemonBoss;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/**
 * A boss dying, drawn about its body as the body is drawn (see {@link BossDeath}): the rays of light pouring out of its
 * heart one after another, the glow swelling there, all gone in its last burst; and each moment of its death, drawn
 * as the effect it names draws it, made as many times larger as its death says. A moment set down at its feet stands
 * on the ground under it; one set down at its heart stands on the ground under the heart, as tall as the heart is high,
 * so that what an effect draws at its height (a burst's point, an eye, a ring of shards) is at the heart. All of it is
 * drawn from the boss's own death time, so that every client sees the same, and it goes with the boss.
 */
public final class DeathVisual {
    /** Over this share of its death its rays come, one after another. */
    private static final float RAYS_COME = .75F;

    /**
     * A boss's death as it stands at the frame being drawn.
     *
     * @param death  how it dies
     * @param time   ticks since it fell, the part-tick included
     * @param heart  where its heart is, from its feet, in blocks
     * @param yaw    the way it faced, in degrees
     * @param seed   the same for this boss every frame, different from another's
     * @param facing how the viewer's camera is turned
     */
    public record Dying(BossDeath death, float time, Vector3f heart, float yaw, long seed, Quaternionfc facing) {
        /** The boss as it stands this frame. */
        public static <T extends LivingEntity & DemonBoss> Dying of(T boss, float partial, Quaternionfc facing) {
            Vec3 heart = boss.heart(partial).subtract(boss.getPosition(partial));
            return new Dying(boss.death(), boss.deathTime + partial, new Vector3f((float) heart.x, (float) heart.y, (float) heart.z),
                    Mth.lerp(partial, boss.yBodyRotO, boss.yBodyRot), boss.getUUID().getLeastSignificantBits(), facing);
        }
    }

    /**
     * Draws it, the pose at the boss's feet. Drawn {@code far}, it is drawn as a colossus is, in the colossus's long fog,
     * so that it is seen from as far off as the colossus is (see FarView); then the buffers should be FarView's too.
     */
    public static void draw(Dying dying, PoseStack pose, MultiBufferSource buffers, boolean far) {
        if (dying.time() <= 0 || dying.time() >= dying.death().lasts()) return;
        if (far) VisualDraw.drawnAs(FarView::glow, () -> drawAll(dying, pose, buffers));
        else drawAll(dying, pose, buffers);
    }

    private static void drawAll(Dying dying, PoseStack pose, MultiBufferSource buffers) {
        BossDeath death = dying.death();
        float time = dying.time(), scale = death.scale();
        Vector3f heart = dying.heart();

        // Its light, pouring out of its heart until its body is gone, and fading a little after.
        float lit = 1 - VisualDraw.within(time, death.ticks() - 2, 8);
        BossDeath.Rays rays = death.rays();
        if (lit > 0) {
            float coming = death.ticks() * RAYS_COME, risen = VisualDraw.within(time, 0, coming);
            RaysVisual.burst(pose.last(), buffers, dying.facing(), heart.x, heart.y, heart.z, rays.count(), coming, rays.length() * scale, time, lit, 0,
                    rays.colour(), rays.accent(), dying.seed());
            RaysVisual.heart(pose.last(), buffers, dying.facing(), heart.x, heart.y, heart.z, rays.length() * scale * (.08F + .14F * risen), time,
                    lit * (.4F + .6F * risen), rays.colour(), rays.accent());
        }

        List<BossDeath.Beat> beats = death.beats();
        for (int index = 0; index < beats.size(); index++) {
            BossDeath.Beat beat = beats.get(index);
            SpellAction.Visual visual = beat.visual();
            float age = time - beat.tick();
            if (age < 0 || age >= visual.ticks()) continue;
            SpellVisual drawn = SpellVisuals.get(ResourceLocation.tryParse(visual.effect()));
            if (drawn == null) continue;
            boolean atHeart = beat.at() == BossDeath.Anchor.HEART;
            float height = (float) visual.height() + (atHeart ? Math.max(0, heart.y) / scale : 0);
            pose.pushPose();
            if (atHeart) pose.translate(heart.x, 0, heart.z);
            pose.scale(scale, scale, scale);
            drawn.draw(new SpellVisual.Shown(age, visual.ticks(), (float) visual.radius(), height, visual.colour(), visual.accent(), dying.yaw(),
                    dying.seed() + index * 0x9E3779B97F4A7C15L, dying.facing()), pose, buffers);
            pose.popPose();
        }
    }

    private DeathVisual() {}
}
