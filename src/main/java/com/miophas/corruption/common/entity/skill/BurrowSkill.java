package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.SolomonWyrm;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.skill.BurrowRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A flood dragon burrowing under its quarry and bursting up through it ({@link BurrowRules}). It rears through the wind-up;
 * dives into the ground a third of the way to its quarry; runs under the ground after it; under it, marks where it stands and
 * holds while the ground there cracks; then bursts straight up through the mark and on into the sky. It flies itself where
 * this tells it ({@link SolomonWyrm#steer}).
 */
public final class BurrowSkill implements BossSkill<SolomonWyrm> {
    /** Where it is in its burrowing. */
    public enum Phase { REAR, DIVE, TUNNEL, WARN, RISE, DONE }

    private final BurrowRules rules;
    private final int colour, accent;
    private Phase phase = Phase.DONE;
    @Nullable private Vec3 entry, mark;
    private int warned;
    private boolean burst;
    private final List<LivingEntity> struck = new ArrayList<>();

    public BurrowSkill(BurrowRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public BurrowRules rules() { return rules; }
    public Phase phase() { return phase; }
    /** Where it marked its quarry, once it had come under it. */
    @Nullable public Vec3 mark() { return mark; }
    /** What its burst caught. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    /**
     * Whether it is going through the ground on purpose: diving into it, running under it, waiting under its mark, and rising
     * until it bursts out. Then it swims through the earth and breaks none of it (SolomonWyrm).
     */
    public boolean tunnelling() { return phase == Phase.DIVE || phase == Phase.TUNNEL || phase == Phase.WARN || phase == Phase.RISE && !burst; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(SolomonWyrm dragon, LivingEntity target) { return rules.inRange(dragon.distanceTo(target)); }

    @Override public void begin(SolomonWyrm dragon, LivingEntity target) {
        phase = Phase.REAR;
        mark = null;
        warned = 0;
        burst = false;
        struck.clear();
        double[] in = rules.entry(target.getX() - dragon.getX(), target.getZ() - dragon.getZ());
        // The ground under where it goes in, however high over it it hangs (through any trees there).
        Vec3 over = dragon.position().add(in[0], 0, in[1]);
        entry = dragon.level() instanceof ServerLevel level
                ? new Vec3(over.x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(over.x), Mth.floor(over.z)), over.z) : dragon.position();
        SkillShows.sound(dragon, SoundEvents.ENDER_DRAGON_GROWL, 5, .5F);
    }

    @Override public void tick(SolomonWyrm dragon, @Nullable LivingEntity target, int elapsed) {
        if (!(dragon.level() instanceof ServerLevel level) || entry == null) return;
        double speed = rules.speed();
        switch (phase) {
            case REAR -> {
                if (target != null) dragon.face(target.getEyePosition());
                if (!rules.timing().windingUp(elapsed)) phase = Phase.DIVE;
            }
            case DIVE -> {
                Vec3 down = entry.subtract(0, rules.depth(), 0);
                dragon.steer(down, speed * 1.4, 10);
                if (dragon.headCentre().y < entry.y - rules.depth() / 2) phase = Phase.TUNNEL;
            }
            case TUNNEL -> {
                Vec3 under = target != null ? target.position() : entry;
                Vec3 ground = SkillShows.ground(level, under);
                dragon.steer(ground.subtract(0, rules.depth(), 0), speed, 12);
                // The ground trembles over it as it goes.
                Vec3 above = SkillShows.ground(level, dragon.headCentre());
                if (elapsed % 3 == 0) level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, above.x, above.y + .2, above.z, 2, .6, 0, .6, .01);
                if (rules.under(Math.hypot(dragon.getX() - ground.x, dragon.getZ() - ground.z))) {
                    mark = ground;
                    phase = Phase.WARN;
                    SkillShows.show(level, mark, "fissure", rules.radius(), .5, rules.warn() + 10, colour, accent, .3, dragon.getYRot(), true);
                    SkillShows.sound(level, mark, SoundEvents.WARDEN_DIG, 3, .5F);
                }
            }
            case WARN -> {
                dragon.steer(mark.subtract(0, rules.depth(), 0), .1, 30);
                if (++warned % 4 == 0) level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, mark.x, mark.y + .2, mark.z, 6, rules.radius() / 2, 0, rules.radius() / 2, .02);
                if (warned >= rules.warn()) phase = Phase.RISE;
            }
            case RISE -> {
                Vec3 top = mark.add(0, rules.climb(), 0);
                dragon.steer(top, rules.rise(), 40);
                if (!burst && dragon.headCentre().y >= mark.y) burst(level, dragon);
                if (dragon.headCentre().y >= top.y - 2) phase = Phase.DONE;
            }
            case DONE -> {}
        }
    }

    /** It bursts up through the mark: what stands there is hurt and flung up, and the ground is torn up about it. */
    private void burst(ServerLevel level, SolomonWyrm dragon) {
        burst = true;
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(mark, mark).inflate(rules.radius(), rules.radius() + 2, rules.radius()),
                near -> near.isAlive() && !dragon.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                        && rules.catches(Math.hypot(near.getX() - mark.x, near.getZ() - mark.z), near.getY() - mark.y))) {
            near.hurt(ModDamageTypes.corruption(level, dragon), (float) rules.damage());
            near.push(0, rules.launch(), 0);
            near.hurtMarked = true;
            struck.add(near);
        }
        Debris.tear(level, dragon, mark, rules.debrisRadius(), rules.debrisMost(), SeereRules.DIVE);
        SkillShows.show(level, mark, "shockwave", rules.radius() + 2, 2, 24, colour, accent, .8, dragon.getYRot(), true);
        SkillShows.sound(level, mark, SoundEvents.GENERIC_EXPLODE.value(), 5, .5F);
        SkillShows.sound(level, mark, SoundEvents.ENDER_DRAGON_GROWL, 5, .6F);
    }

    @Override public boolean finished(SolomonWyrm dragon) { return phase == Phase.DONE; }

    @Override public void end(SolomonWyrm dragon, boolean broken) {
        phase = Phase.DONE;
        entry = null;
    }
}
