package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.common.entity.SolomonFireball;
import com.miophas.corruption.core.demon.skill.FireballRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Baal's fireballs ({@link FireballRules}) as the mod carries them out for any boss. Through the wind-up the boss holds still,
 * turned on its quarry, fire gathering at its mouth (its {@link SpitCaster} point, or its eyes); then it looses its fireballs
 * one after another ({@link SolomonFireball}), each aimed where its quarry will be when it gets there.
 */
public final class FireballSkill implements BossSkill<Mob> {
    private final FireballRules rules;
    private final int colour, accent;
    private final List<SolomonFireball> loosed = new ArrayList<>();

    public FireballSkill(FireballRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public FireballRules rules() { return rules; }
    /** The fireballs this use has loosed so far. */
    public List<SolomonFireball> loosed() { return List.copyOf(loosed); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        loosed.clear();
        SkillShows.sound(boss, SoundEvents.BLAZE_AMBIENT, 3, .5F);
        if (boss.level() instanceof ServerLevel level) SkillShows.show(level, mouth(boss), "hellfire", 1.5, 1, rules.timing().windup(), colour, accent, 0, boss.getYRot(), false);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.setDeltaMovement(boss.getDeltaMovement().scale(.8));
        if (!(boss.level() instanceof ServerLevel level)) return;
        Vec3 mouth = mouth(boss);
        if (rules.timing().windingUp(elapsed)) {
            level.sendParticles(ParticleTypes.FLAME, mouth.x, mouth.y, mouth.z, 4, .4, .4, .4, .02);
            return;
        }
        int shot = rules.shot(elapsed);
        if (shot < 0 || target == null) return;
        Vec3 aim = target.getBoundingBox().getCenter().add(target.getDeltaMovement().scale(rules.leading(mouth.distanceTo(target.position()))));
        Vec3 way = aim.subtract(mouth);
        if (way.lengthSqr() < 1e-6) return;
        loosed.add(SolomonFireball.loose(level, boss, mouth, way.normalize().scale(rules.speed()), rules, colour, accent));
        SkillShows.sound(level, mouth, SoundEvents.GHAST_SHOOT, 3, .6F);
    }

    private static Vec3 mouth(Mob boss) { return boss instanceof SpitCaster caster ? caster.spitFrom(0) : boss.getEyePosition(); }
}
