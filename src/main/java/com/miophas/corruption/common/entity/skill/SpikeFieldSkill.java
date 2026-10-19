package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A field of spikes ({@link SpikeFieldRules}) as the mod carries it out for any boss. As it begins, the boss turns to
 * its quarry, and the field is laid out before it that way, fixed: every place a cluster of spikes will come up is
 * marked on the ground with a small circle. At the end of the wind-up (the boss stamping, as its model shows it) the
 * spikes burst up at every mark, and everything standing in the field is hurt with the corruption's own hurt and
 * slowed.
 */
public final class SpikeFieldSkill implements RegisteredSkill<SpikeFieldRules> {
    /** How far above or below the field's ground a creature may stand and still be caught in it. */
    private static final double REACH_UP = 3;

    private final SpikeFieldRules rules;
    private final int colour, accent;
    private final List<Vec3> marks = new ArrayList<>();
    private Vec3 origin = Vec3.ZERO;
    private float yaw;

    public SpikeFieldSkill(SpikeFieldRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public SpikeFieldRules rules() { return rules; }

    /** Where this use's spikes come up. */
    public List<Vec3> marks() { return List.copyOf(marks); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        marks.clear();
        boss.getNavigation().stop();
        MeleeSkill.face(boss, target.position());
        origin = boss.position();
        yaw = boss.getYRot();
        if (!(boss.level() instanceof ServerLevel level)) return;
        Vec3 ahead = Vec3.directionFromRotation(0, yaw), aside = new Vec3(-ahead.z, 0, ahead.x);
        for (double[] cluster : rules.clusters()) {
            Vec3 at = SkillShows.ground(level, origin.add(ahead.scale(cluster[0])).add(aside.scale(cluster[1])));
            marks.add(at);
            SkillShows.show(level, at, "magic_circle", 1.1, .3, rules.timing().windup() + 4, colour, accent, 0, yaw, false);
        }
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_ATTACK, 2, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.getNavigation().stop();
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
        boss.setYRot(yaw);
        boss.yBodyRot = yaw;
        if (rules.timing().strikes(elapsed) && boss.level() instanceof ServerLevel level) burst(level, boss);
    }

    @Override public void end(Mob boss, boolean broken) { marks.clear(); }

    /** The spikes burst up at every mark, and everything standing in the field is hurt and slowed. */
    private void burst(ServerLevel level, Mob boss) {
        for (Vec3 at : marks) {
            SkillShows.show(level, at, "spikes", 1.5, 2.6, 30, colour, accent, .15, yaw, false);
            BlockState ground = level.getBlockState(BlockPos.containing(at).below());
            if (!ground.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), at.x, at.y + .2, at.z, 12, .5, .2, .5, .12);
        }
        double reach = rules.far() + rules.farWidth();
        DamageSource hurt = ModDamageTypes.corruption(level, boss);
        for (LivingEntity caught : level.getEntitiesOfClass(LivingEntity.class, new AABB(origin, origin).inflate(reach, REACH_UP + 2, reach), caught -> caught != boss
                && caught.isAlive() && !boss.isAlliedTo(caught) && covers(caught))) {
            caught.invulnerableTime = 0;
            if (caught.hurt(hurt, (float) rules.damage()))
                caught.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, rules.slownessTicks(), rules.slownessLevel()), boss);
        }
        SkillShows.sound(boss, SoundEvents.EVOKER_FANGS_ATTACK, 2.5F, .6F);
        SkillShows.sound(boss, SoundEvents.POINTED_DRIPSTONE_LAND, 2.5F, .5F);
    }

    /** Whether a creature stands in the field: within it along the ground, and not far above or below where it was laid. */
    private boolean covers(LivingEntity caught) {
        Vec3 ahead = Vec3.directionFromRotation(0, yaw), to = caught.position().subtract(origin);
        double along = to.x * ahead.x + to.z * ahead.z, across = to.x * -ahead.z + to.z * ahead.x;
        return Math.abs(to.y) <= REACH_UP && rules.covers(along, Math.max(0, Math.abs(across) - caught.getBbWidth() / 2));
    }
    @Override public BossSkillRegistry.Type<SpikeFieldRules> type() { return BossSkillApi.SPIKE_FIELD; }
    @Override public SpikeFieldRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
