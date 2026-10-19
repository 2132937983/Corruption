package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.BurrowingCaster;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.DebrisRules;
import com.miophas.corruption.core.demon.skill.RamRules;
import com.miophas.corruption.core.demon.skill.RamState;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** World adapter for ramming: movement comes from BurrowingCaster, progression from RamState. */
public final class RamSkill implements RegisteredSkill<RamRules> {
    /** Where it is in its ram. */
    public enum Phase { AIM, CHARGE, PLUNGE, UNDER, OUT, DONE }

    private final RamRules rules;
    private final int colour, accent;
    private final RamState flow = new RamState();
    private final DebrisRules debris;
    @Nullable private Vec3 mark, past, outlet, line;
    private final Set<UUID> hit = new HashSet<>();
    private final List<LivingEntity> struck = new ArrayList<>();

    public RamSkill(RamRules rules, int colour, int accent) {
        this(rules, colour, accent, rules.debris());
    }

    public RamSkill(RamRules rules, int colour, int accent, DebrisRules debris) {
        this.debris = java.util.Objects.requireNonNull(debris);
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public RamRules rules() { return rules; }
    public Phase phase() { return Phase.valueOf(flow.phase().name()); }
    @Nullable public Vec3 mark() { return mark; }
    /** What its ram and its burst caught. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    /**
     * Whether it is going through the ground on purpose: plunging on into it, running away under it, and coming back up until
     * it bursts out. Then it swims through the earth and breaks none of it (the host); it breaks what it rams on the way down.
     */
    public boolean tunnelling() { return flow.tunnelling(); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob dragon, LivingEntity target) { return dragon instanceof BurrowingCaster && rules.inRange(dragon.distanceTo(target)) && dragon.hasLineOfSight(target); }

    @Override public void begin(Mob dragon, LivingEntity target) {
        movement(dragon);
        flow.begin();
        mark = past = outlet = line = null;
        hit.clear();
        struck.clear();
        SkillShows.sound(dragon, SoundEvents.ENDER_DRAGON_GROWL, 5, .7F);
    }

    @Override public void tick(Mob dragon, @Nullable LivingEntity target, int elapsed) {
        if (!(dragon.level() instanceof ServerLevel level)) return;
        switch (flow.phase()) {
            case AIM -> {
                if (target != null) movement(dragon).face(target.getEyePosition());
                if (!flow.aim(rules, elapsed)) return;
                mark = target != null ? target.position().add(0, .5, 0) : dragon.position();
                line = mark.subtract(movement(dragon).headCentre());
                double[] on = rules.past(line.x, line.y, line.z);
                past = mark.add(on[0], on[1], on[2]);
                double[] away = rules.outlet(line.x, line.z);
                outlet = SkillShows.ground(level, mark.add(away[0], 0, away[1]));
                SkillShows.show(level, SkillShows.ground(level, mark), "magic_circle", rules.width() + 1, .4, 40, colour, accent, 0, dragon.getYRot(), true);
                SkillShows.sound(dragon, SoundEvents.ENDER_DRAGON_FLAP, 5, .5F);
            }
            case CHARGE -> {
                movement(dragon).steer(mark, rules.speed(), 3);
                ram(level, dragon);
                flow.charge(rules, movement(dragon).headCentre().distanceTo(mark));
            }
            case PLUNGE -> {
                movement(dragon).steer(past, rules.speed(), 8);
                ram(level, dragon);
                flow.plunge(rules, movement(dragon).headCentre().distanceTo(past));
            }
            case UNDER -> {
                Vec3 below = outlet.subtract(0, rules.depth(), 0);
                movement(dragon).steer(below, rules.speed() * .8, 10);
                flow.under(rules, Math.hypot(dragon.getX() - below.x, dragon.getZ() - below.z));
            }
            case OUT -> {
                Vec3 top = outlet.add(0, rules.climb(), 0);
                movement(dragon).steer(top, rules.speed(), 40);
                if (flow.out(rules, movement(dragon).headCentre().y, outlet.y)) burst(level, dragon);
            }
            case DONE -> {}
        }
    }

    /** Whatever its head passes within its width is hurt and thrown, each once. */
    private void ram(ServerLevel level, Mob dragon) {
        Vec3 head = movement(dragon).headCentre();
        Vec3 way = line == null ? movement(dragon).forward() : line.normalize();
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(head, head).inflate(rules.width() + 1.5), near -> near != dragon && near.isAlive()
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
    private void burst(ServerLevel level, Mob dragon) {
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(outlet, outlet).inflate(rules.burst(), rules.burst() + 2, rules.burst()),
                near -> near != dragon && near.isAlive() && !dragon.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                        && Math.hypot(near.getX() - outlet.x, near.getZ() - outlet.z) <= rules.burst())) {
            near.hurt(ModDamageTypes.corruption(level, dragon), (float) rules.burstDamage());
            near.push(0, .8, 0);
            near.hurtMarked = true;
            struck.add(near);
        }
        Debris.tear(level, dragon, outlet, rules.burst() - 1, 24, debris);
        SkillShows.show(level, outlet, "shockwave", rules.burst() + 2, 2, 24, colour, accent, .6, dragon.getYRot(), true);
        SkillShows.sound(level, outlet, SoundEvents.GENERIC_EXPLODE.value(), 5, .6F);
    }

    @Override public boolean finished(Mob dragon) { return flow.phase() == RamState.Phase.DONE; }

    @Override public void end(Mob dragon, boolean broken) { flow.end(); }
    private static BurrowingCaster movement(Mob mob) {
        if (mob instanceof BurrowingCaster caster) return caster;
        throw new IllegalArgumentException("Ram skill requires BurrowingCaster");
    }
    @Override public BossSkillRegistry.Type<RamRules> type() { return BossSkillApi.RAM; }
    @Override public RamRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
