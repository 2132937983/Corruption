package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.BurrowingCaster;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.BurrowRules;
import com.miophas.corruption.core.demon.skill.BurrowState;
import com.miophas.corruption.core.demon.skill.DebrisRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** World adapter for burrowing: movement comes from BurrowingCaster, progression from BurrowState. */
public final class BurrowSkill implements RegisteredSkill<BurrowRules> {
    /** Where it is in its burrowing. */
    public enum Phase { REAR, DIVE, TUNNEL, WARN, RISE, DONE }

    private final BurrowRules rules;
    private final int colour, accent;
    private final BurrowState flow = new BurrowState();
    private final DebrisRules debris;
    @Nullable private Vec3 entry, mark;
    private final List<LivingEntity> struck = new ArrayList<>();

    public BurrowSkill(BurrowRules rules, int colour, int accent) {
        this(rules, colour, accent, rules.debris());
    }

    public BurrowSkill(BurrowRules rules, int colour, int accent, DebrisRules debris) {
        this.debris = java.util.Objects.requireNonNull(debris);
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public BurrowRules rules() { return rules; }
    public Phase phase() { return Phase.valueOf(flow.phase().name()); }
    /** Where it marked its quarry, once it had come under it. */
    @Nullable public Vec3 mark() { return mark; }
    /** What its burst caught. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    /**
     * Whether it is going through the ground on purpose: diving into it, running under it, waiting under its mark, and rising
     * until it bursts out. Then it swims through the earth and breaks none of it (the host).
     */
    public boolean tunnelling() { return flow.tunnelling(); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob dragon, LivingEntity target) { return dragon instanceof BurrowingCaster && rules.inRange(dragon.distanceTo(target)); }

    @Override public void begin(Mob dragon, LivingEntity target) {
        movement(dragon);
        flow.begin();
        mark = null;
        struck.clear();
        double[] in = rules.entry(target.getX() - dragon.getX(), target.getZ() - dragon.getZ());
        // The ground under where it goes in, however high over it it hangs (through any trees there).
        Vec3 over = dragon.position().add(in[0], 0, in[1]);
        entry = dragon.level() instanceof ServerLevel level
                ? new Vec3(over.x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(over.x), Mth.floor(over.z)), over.z) : dragon.position();
        SkillShows.sound(dragon, SoundEvents.ENDER_DRAGON_GROWL, 5, .5F);
    }

    @Override public void tick(Mob dragon, @Nullable LivingEntity target, int elapsed) {
        if (!(dragon.level() instanceof ServerLevel level) || entry == null) return;
        double speed = rules.speed();
        switch (flow.phase()) {
            case REAR -> {
                if (target != null) movement(dragon).face(target.getEyePosition());
                flow.rear(rules, elapsed);
            }
            case DIVE -> {
                Vec3 down = entry.subtract(0, rules.depth(), 0);
                movement(dragon).steer(down, speed * 1.4, 10);
                flow.dive(rules, movement(dragon).headCentre().y, entry.y);
            }
            case TUNNEL -> {
                Vec3 under = target != null ? target.position() : entry;
                Vec3 ground = SkillShows.ground(level, under);
                movement(dragon).steer(ground.subtract(0, rules.depth(), 0), speed, 12);
                // The ground trembles over it as it goes.
                Vec3 above = SkillShows.ground(level, movement(dragon).headCentre());
                if (elapsed % 3 == 0) level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, above.x, above.y + .2, above.z, 2, .6, 0, .6, .01);
                if (flow.tunnel(rules, Math.hypot(dragon.getX() - ground.x, dragon.getZ() - ground.z))) {
                    mark = ground;
                    SkillShows.show(level, mark, "fissure", rules.radius(), .5, rules.warn() + 10, colour, accent, .3, dragon.getYRot(), true);
                    SkillShows.sound(level, mark, SoundEvents.WARDEN_DIG, 3, .5F);
                }
            }
            case WARN -> {
                movement(dragon).steer(mark.subtract(0, rules.depth(), 0), .1, 30);
                flow.warn(rules);
                if (flow.warned() % 4 == 0) level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, mark.x, mark.y + .2, mark.z, 6, rules.radius() / 2, 0, rules.radius() / 2, .02);
            }
            case RISE -> {
                Vec3 top = mark.add(0, rules.climb(), 0);
                movement(dragon).steer(top, rules.rise(), 40);
                if (flow.rise(rules, movement(dragon).headCentre().y, mark.y)) burst(level, dragon);
            }
            case DONE -> {}
        }
    }

    /** It bursts up through the mark: what stands there is hurt and flung up, and the ground is torn up about it. */
    private void burst(ServerLevel level, Mob dragon) {
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(mark, mark).inflate(rules.radius(), rules.radius() + 2, rules.radius()),
                near -> near != dragon && near.isAlive() && !dragon.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                        && rules.catches(Math.hypot(near.getX() - mark.x, near.getZ() - mark.z), near.getY() - mark.y))) {
            near.hurt(ModDamageTypes.corruption(level, dragon), (float) rules.damage());
            near.push(0, rules.launch(), 0);
            near.hurtMarked = true;
            struck.add(near);
        }
        Debris.tear(level, dragon, mark, rules.debrisRadius(), rules.debrisMost(), debris);
        SkillShows.show(level, mark, "shockwave", rules.radius() + 2, 2, 24, colour, accent, .8, dragon.getYRot(), true);
        SkillShows.sound(level, mark, SoundEvents.GENERIC_EXPLODE.value(), 5, .5F);
        SkillShows.sound(level, mark, SoundEvents.ENDER_DRAGON_GROWL, 5, .6F);
    }

    @Override public boolean finished(Mob dragon) { return flow.phase() == BurrowState.Phase.DONE; }

    @Override public void end(Mob dragon, boolean broken) {
        flow.end();
        entry = null;
    }
    private static BurrowingCaster movement(Mob mob) {
        if (mob instanceof BurrowingCaster caster) return caster;
        throw new IllegalArgumentException("Burrow skill requires BurrowingCaster");
    }
    @Override public BossSkillRegistry.Type<BurrowRules> type() { return BossSkillApi.BURROW; }
    @Override public BurrowRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
