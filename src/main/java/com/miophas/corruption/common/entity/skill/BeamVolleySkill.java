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
import com.miophas.corruption.core.demon.skill.BeamVolleyRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
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
 * A volley of beams ({@link BeamVolleyRules}) as the mod carries it out for any boss: as many as the boss says as it
 * begins (by its health, say), each out of a magic circle of its own. One leaves from before the boss itself (as a
 * {@link BeamCaster} says, else its eyes); more leave from circles set round that place, square to the way the boss
 * faces, each circle a rune ring turning in the air. Each beam gathers, fires and follows the quarry as a single beam
 * does ({@link BeamSkill}), and each hurts what it catches on its own: two beams on a quarry, two blows each pulse.
 */
public final class BeamVolleySkill implements RegisteredSkill<BeamVolleyRules> {
    /** How far before the boss a lone beam leaves from, when it says nothing of it. */
    private static final double BEFORE = 1.2;

    private final BeamVolleyRules rules;
    private final ToIntFunction<Mob> count;
    private final int colour, accent;
    private final List<DemonBeamEntity> beams = new ArrayList<>();
    private final List<double[]> circles = new ArrayList<>();
    private float[] yaws = new float[0], pitches = new float[0];

    /** @param count how many beams the boss fires as it begins, held to one and the rules' most */
    public BeamVolleySkill(BeamVolleyRules rules, ToIntFunction<Mob> count, int colour, int accent) {
        this.rules = rules;
        this.count = count;
        this.colour = colour;
        this.accent = accent;
    }

    public BeamVolleyRules rules() { return rules; }

    /** The beams this use stands, while it runs. */
    public List<DemonBeamEntity> beams() { return List.copyOf(beams); }

    @Override public SkillTiming timing() { return rules.beam().timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.beam().inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        end(boss, true);
        int many = Math.max(1, Math.min(rules.most(), count.applyAsInt(boss)));
        yaws = new float[many];
        pitches = new float[many];
        for (int index = 0; index < many; index++) {
            double[] circle = rules.circle(index, many);
            circles.add(circle);
            Vec3 from = origin(boss, index, many, circle), to = target.getBoundingBox().getCenter().subtract(from);
            yaws[index] = yawOf(to);
            pitches[index] = pitchOf(to);
            DemonBeamEntity beam = ModEntities.DEMON_BEAM.get().create(boss.level());
            if (beam == null) continue;
            beam.shaped(rules.beam(), colour, accent);
            beam.aim(from, yaws[index], pitches[index]);
            beam.moveTo(from.x, from.y, from.z, yaws[index], pitches[index]);
            boss.level().addFreshEntity(beam);
            beams.add(beam);
            if (boss.level() instanceof ServerLevel level) circle(level, boss, from);
        }
        SkillShows.sound(boss, SoundEvents.BEACON_POWER_SELECT, 2, .5F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        BeamRules beam = rules.beam();
        for (int index = 0; index < beams.size(); index++) {
            DemonBeamEntity standing = beams.get(index);
            Vec3 from = origin(boss, index, circles.size(), circles.get(index));
            if (target != null && target.isAlive()) {
                Vec3 to = target.getBoundingBox().getCenter().subtract(from);
                yaws[index] = beam.timing().windingUp(elapsed) ? yawOf(to) : beam.follow(yaws[index], yawOf(to));
                pitches[index] = beam.timing().windingUp(elapsed) ? pitchOf(to) : beam.follow(pitches[index], pitchOf(to));
            }
            if (!standing.isAlive()) continue;
            standing.aim(from, yaws[index], pitches[index]);
            if (beam.timing().strikes(elapsed)) standing.fire();
            if (beam.pulses(elapsed) && boss.level() instanceof ServerLevel level) pulse(level, boss, standing, from, yaws[index], pitches[index]);
        }
        if (target != null) boss.getLookControl().setLookAt(target, 30, 30);
        if (beam.timing().strikes(elapsed)) SkillShows.sound(boss, SoundEvents.WARDEN_SONIC_BOOM, 2.5F, .6F + .1F * beams.size());
    }

    @Override public void end(Mob boss, boolean broken) {
        beams.forEach(DemonBeamEntity::discard);
        beams.clear();
        circles.clear();
    }

    /**
     * Where the index-th of so many beams leaves from: its circle's place round the one before the boss, square to the way
     * it faces; or wherever the boss, a {@link BeamCaster}, says that beam leaves it from.
     */
    private Vec3 origin(Mob boss, int index, int count, double[] circle) {
        Vec3 middle = boss instanceof BeamCaster caster ? caster.beamFrom() : boss.getEyePosition().add(boss.getLookAngle().scale(BEFORE));
        Vec3 ahead = Vec3.directionFromRotation(0, boss.getYRot()), right = new Vec3(-ahead.z, 0, ahead.x);
        Vec3 circled = middle.add(right.scale(circle[0])).add(0, circle[1], 0);
        return boss instanceof BeamCaster caster ? caster.beamFrom(index, count, circled) : circled;
    }

    /** A rune ring standing in the air about a beam's place, facing the way the boss does, for as long as the beam lasts. */
    private void circle(ServerLevel level, Mob boss, Vec3 at) {
        double radius = 1.1, height = radius * 1.35 + .2;
        // The ring stands a little before its own place and above it (see SigilVisual): set back so that it is round the beam's.
        Vec3 ahead = Vec3.directionFromRotation(0, boss.getYRot()), place = at.subtract(ahead.scale(1.4)).subtract(0, height, 0);
        SkillShows.show(level, place, "sigil", radius, height, rules.beam().timing().ticks() + 6, colour, accent, 0, boss.getYRot(), false);
    }

    /** One pulse of one beam: whatever is within its width of its line, up to the first block in its way, is hurt. */
    private void pulse(ServerLevel level, Mob boss, DemonBeamEntity standing, Vec3 from, float yaw, float pitch) {
        Vec3 way = Vec3.directionFromRotation(pitch, yaw);
        var reached = standing.reach(from, way);
        Vec3 to = reached.getType() == HitResult.Type.MISS ? from.add(way.scale(rules.beam().length())) : reached.getLocation();
        DamageSource hurt = ModDamageTypes.corruption(level, boss);
        double width = rules.beam().width();
        for (LivingEntity caught : level.getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(width), caught -> caught != boss && caught.isAlive()
                && !boss.isAlliedTo(caught) && caught.getBoundingBox().inflate(width).contains(nearest(caught, from, to)))) {
            caught.invulnerableTime = 0;
            caught.hurt(hurt, (float) rules.beam().damage());
        }
    }

    private static Vec3 nearest(LivingEntity caught, Vec3 a, Vec3 b) {
        Vec3 middle = caught.getBoundingBox().getCenter(), line = b.subtract(a);
        double along = Mth.clamp(middle.subtract(a).dot(line) / Math.max(1e-6, line.lengthSqr()), 0, 1);
        return a.add(line.scale(along));
    }

    private static float yawOf(Vec3 way) { return (float) (Mth.atan2(way.z, way.x) * Mth.RAD_TO_DEG) - 90; }
    private static float pitchOf(Vec3 way) { return (float) -(Mth.atan2(way.y, way.horizontalDistance()) * Mth.RAD_TO_DEG); }
    @Override public BossSkillRegistry.Type<BeamVolleyRules> type() { return BossSkillApi.BEAM_VOLLEY; }
    @Override public BeamVolleyRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, count, false); }
}
