package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BeamCaster;
import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.entity.DemonBeamEntity;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A beam of the corruption ({@link BeamRules}) as the mod carries it out for any boss. As it begins, the beam stands
 * where it will leave the boss ({@link BeamCaster}, else its eyes) aimed at the quarry, gathering: a thin line of
 * aim and a swelling light, with the hum of a beacon. When the wind-up is over it fires, roaring, and follows the
 * quarry no faster than it may turn; every pulse it hurts whatever is within its width of its line, with the
 * corruption's own hurt, up to the first block in its way.
 */
public final class BeamSkill implements RegisteredSkill<BeamRules> {
    private final BeamRules rules;
    private final int colour, accent;
    private DemonBeamEntity beam;
    private float yaw, pitch;

    public BeamSkill(BeamRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public BeamRules rules() { return rules; }

    /** The beam this use stands, while it runs. */
    public @Nullable DemonBeamEntity beam() { return beam; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        Vec3 from = from(boss), to = target.getBoundingBox().getCenter().subtract(from);
        yaw = yawOf(to);
        pitch = pitchOf(to);
        if (beam != null) beam.discard();
        beam = ModEntities.DEMON_BEAM.get().create(boss.level());
        if (beam != null) {
            beam.shaped(rules, colour, accent);
            beam.aim(from, yaw, pitch);
            beam.moveTo(from.x, from.y, from.z, yaw, pitch);
            boss.level().addFreshEntity(beam);
        }
        SkillShows.sound(boss, SoundEvents.BEACON_POWER_SELECT, 2, .5F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        Vec3 from = from(boss);
        if (target != null && target.isAlive()) {
            Vec3 to = target.getBoundingBox().getCenter().subtract(from);
            // Gathering, it holds its quarry in its sights; firing, it can only follow so fast.
            float wantYaw = yawOf(to), wantPitch = pitchOf(to);
            yaw = rules.timing().windingUp(elapsed) ? wantYaw : rules.follow(yaw, wantYaw);
            pitch = rules.timing().windingUp(elapsed) ? wantPitch : rules.follow(pitch, wantPitch);
        }
        boss.getLookControl().setLookAt(from.add(Vec3.directionFromRotation(pitch, yaw).scale(8)));
        if (beam == null || !beam.isAlive()) return;
        beam.aim(from, yaw, pitch);
        if (rules.timing().strikes(elapsed)) {
            beam.fire();
            SkillShows.sound(boss, SoundEvents.WARDEN_SONIC_BOOM, 2.5F, .6F);
        }
        if (rules.pulses(elapsed) && boss.level() instanceof ServerLevel level) pulse(level, boss, from);
    }

    @Override public void end(Mob boss, boolean broken) {
        if (beam != null) beam.discard();
        beam = null;
    }

    /** One pulse: whatever is within its width of its line, up to the first block in its way, is hurt. */
    private void pulse(ServerLevel level, Mob boss, Vec3 from) {
        Vec3 way = Vec3.directionFromRotation(pitch, yaw);
        var reached = beam.reach(from, way);
        Vec3 to = reached.getType() == HitResult.Type.MISS ? from.add(way.scale(rules.length())) : reached.getLocation();
        DamageSource hurt = ModDamageTypes.corruption(level, boss);
        for (LivingEntity caught : level.getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(rules.width()),
                caught -> caught != boss && caught.isAlive() && !boss.isAlliedTo(caught) && near(caught, from, to, rules.width()))) {
            caught.invulnerableTime = 0;
            caught.hurt(hurt, (float) rules.damage());
        }
    }

    /** Whether something's box comes within this far of the line from a to b. */
    private static boolean near(LivingEntity caught, Vec3 a, Vec3 b, double width) {
        Vec3 middle = caught.getBoundingBox().getCenter(), line = b.subtract(a);
        double along = Mth.clamp(middle.subtract(a).dot(line) / Math.max(1e-6, line.lengthSqr()), 0, 1);
        Vec3 nearest = a.add(line.scale(along));
        return caught.getBoundingBox().inflate(width).contains(nearest);
    }

    /** Where the beam leaves the boss. */
    private static Vec3 from(Mob boss) { return boss instanceof BeamCaster caster ? caster.beamFrom() : boss.getEyePosition(); }

    private static float yawOf(Vec3 way) { return (float) (Mth.atan2(way.z, way.x) * Mth.RAD_TO_DEG) - 90; }
    private static float pitchOf(Vec3 way) { return (float) -(Mth.atan2(way.y, way.horizontalDistance()) * Mth.RAD_TO_DEG); }
    @Override public BossSkillRegistry.Type<BeamRules> type() { return BossSkillApi.BEAM; }
    @Override public BeamRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
