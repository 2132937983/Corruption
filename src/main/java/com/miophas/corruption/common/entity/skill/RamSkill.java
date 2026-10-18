package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.SolomonWyrm;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.skill.RamRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A flood dragon ramming down at where its quarry stood, on into the ground and away under it, and out again into the sky
 * ({@link RamRules}). Through the wind-up it rears and takes aim; at its end it marks where its quarry stands, and rams that
 * place, not its quarry: whoever steps aside is passed. It flies itself where this tells it ({@link SolomonWyrm#steer}).
 */
public final class RamSkill implements BossSkill<SolomonWyrm> {
    /** Where it is in its ram. */
    public enum Phase { AIM, CHARGE, PLUNGE, UNDER, OUT, DONE }

    private final RamRules rules;
    private final int colour, accent;
    private Phase phase = Phase.DONE;
    @Nullable private Vec3 mark, past, outlet, line;
    private boolean burst;
    private final Set<UUID> hit = new HashSet<>();
    private final List<LivingEntity> struck = new ArrayList<>();

    public RamSkill(RamRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public RamRules rules() { return rules; }
    public Phase phase() { return phase; }
    @Nullable public Vec3 mark() { return mark; }
    /** What its ram and its burst caught. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    /**
     * Whether it is going through the ground on purpose: plunging on into it, running away under it, and coming back up until
     * it bursts out. Then it swims through the earth and breaks none of it (SolomonWyrm); it breaks what it rams on the way down.
     */
    public boolean tunnelling() { return phase == Phase.PLUNGE || phase == Phase.UNDER || phase == Phase.OUT && !burst; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(SolomonWyrm dragon, LivingEntity target) { return rules.inRange(dragon.distanceTo(target)) && dragon.hasLineOfSight(target); }

    @Override public void begin(SolomonWyrm dragon, LivingEntity target) {
        phase = Phase.AIM;
        mark = past = outlet = line = null;
        burst = false;
        hit.clear();
        struck.clear();
        SkillShows.sound(dragon, SoundEvents.ENDER_DRAGON_GROWL, 5, .7F);
    }

    @Override public void tick(SolomonWyrm dragon, @Nullable LivingEntity target, int elapsed) {
        if (!(dragon.level() instanceof ServerLevel level)) return;
        switch (phase) {
            case AIM -> {
                if (target != null) dragon.face(target.getEyePosition());
                if (rules.timing().windingUp(elapsed)) return;
                mark = target != null ? target.position().add(0, .5, 0) : dragon.position();
                line = mark.subtract(dragon.headCentre());
                double[] on = rules.past(line.x, line.y, line.z);
                past = mark.add(on[0], on[1], on[2]);
                double[] away = rules.outlet(line.x, line.z);
                outlet = SkillShows.ground(level, mark.add(away[0], 0, away[1]));
                phase = Phase.CHARGE;
                SkillShows.show(level, SkillShows.ground(level, mark), "magic_circle", rules.width() + 1, .4, 40, colour, accent, 0, dragon.getYRot(), true);
                SkillShows.sound(dragon, SoundEvents.ENDER_DRAGON_FLAP, 5, .5F);
            }
            case CHARGE -> {
                dragon.steer(mark, rules.speed(), 3);
                ram(level, dragon);
                if (dragon.headCentre().distanceTo(mark) < rules.speed() * 1.5) phase = Phase.PLUNGE;
            }
            case PLUNGE -> {
                dragon.steer(past, rules.speed(), 8);
                ram(level, dragon);
                if (dragon.headCentre().distanceTo(past) < rules.speed() * 1.5) phase = Phase.UNDER;
            }
            case UNDER -> {
                Vec3 below = outlet.subtract(0, rules.depth(), 0);
                dragon.steer(below, rules.speed() * .8, 10);
                if (Math.hypot(dragon.getX() - below.x, dragon.getZ() - below.z) < rules.speed() * 1.5) phase = Phase.OUT;
            }
            case OUT -> {
                Vec3 top = outlet.add(0, rules.climb(), 0);
                dragon.steer(top, rules.speed(), 40);
                if (!burst && dragon.headCentre().y >= outlet.y) burst(level, dragon);
                if (dragon.headCentre().y >= top.y - 2) phase = Phase.DONE;
            }
            case DONE -> {}
        }
    }

    /** Whatever its head passes within its width is hurt and thrown, each once. */
    private void ram(ServerLevel level, SolomonWyrm dragon) {
        Vec3 head = dragon.headCentre();
        Vec3 way = line == null ? dragon.forward() : line.normalize();
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(head, head).inflate(rules.width() + 1.5), near -> near.isAlive()
                && !dragon.isAlliedTo(near) && !hit.contains(near.getUUID()) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                && near.getBoundingBox().getCenter().distanceTo(head) <= rules.width() + near.getBbWidth() / 2)) {
            hit.add(near.getUUID());
            near.hurt(ModDamageTypes.corruption(level, dragon), (float) rules.damage());
            near.push(way.x * rules.knockback(), .4, way.z * rules.knockback());
            near.hurtMarked = true;
            struck.add(near);
        }
    }

    /** It bursts out of the ground: what is there is hurt for half as much, and the ground is torn up about it. */
    private void burst(ServerLevel level, SolomonWyrm dragon) {
        burst = true;
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(outlet, outlet).inflate(rules.burst(), rules.burst() + 2, rules.burst()),
                near -> near.isAlive() && !dragon.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                        && Math.hypot(near.getX() - outlet.x, near.getZ() - outlet.z) <= rules.burst())) {
            near.hurt(ModDamageTypes.corruption(level, dragon), (float) rules.burstDamage());
            near.push(0, .8, 0);
            near.hurtMarked = true;
            struck.add(near);
        }
        Debris.tear(level, dragon, outlet, rules.burst() - 1, 24, SeereRules.DIVE);
        SkillShows.show(level, outlet, "shockwave", rules.burst() + 2, 2, 24, colour, accent, .6, dragon.getYRot(), true);
        SkillShows.sound(level, outlet, SoundEvents.GENERIC_EXPLODE.value(), 5, .6F);
    }

    @Override public boolean finished(SolomonWyrm dragon) { return phase == Phase.DONE; }

    @Override public void end(SolomonWyrm dragon, boolean broken) { phase = Phase.DONE; }
}
