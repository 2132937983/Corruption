package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A flier's swoop (its basic blow on the wing): it draws up through the wind-up, then flies straight at its quarry at
 * {@code speed} blocks a tick and strikes it as it meets it (its own melee blow), and is away again at once, up and back.
 * It swoops only at a quarry within {@code range} that it can see.
 */
public final class SwoopSkill implements BossSkill<Mob> {
    private final SkillTiming timing;
    private final double range, speed;
    private boolean struck;

    public SwoopSkill(SkillTiming timing, double range, double speed) {
        this.timing = timing;
        this.range = range;
        this.speed = speed;
    }

    /** Whether this use struck its quarry. */
    public boolean struck() { return struck; }

    @Override public SkillTiming timing() { return timing; }

    @Override public boolean usable(Mob flier, LivingEntity target) { return flier.distanceTo(target) <= range && flier.hasLineOfSight(target); }

    @Override public void begin(Mob flier, LivingEntity target) {
        struck = false;
        SkillShows.sound(flier, SoundEvents.PHANTOM_SWOOP, 1.5F, .7F);
    }

    @Override public void tick(Mob flier, @Nullable LivingEntity target, int elapsed) {
        if (target == null) return;
        Vec3 heart = target.getBoundingBox().getCenter();
        MeleeSkill.face(flier, heart);
        if (timing.windingUp(elapsed)) {
            flier.setDeltaMovement(flier.getDeltaMovement().scale(.7).add(0, .05, 0));
            return;
        }
        Vec3 way = heart.subtract(flier.getBoundingBox().getCenter());
        Vec3 moving = way.normalize().scale(Math.min(speed, way.length()));
        flier.setDeltaMovement(moving);
        flier.setXRot((float) Mth.clamp(-Mth.atan2(moving.y, Math.max(.05, moving.horizontalDistance())) * Mth.RAD_TO_DEG, -60, 60));
        if (flier.getBoundingBox().inflate(.4).intersects(target.getBoundingBox())) {
            flier.doHurtTarget(target);
            struck = true;
            flier.setDeltaMovement(moving.scale(-.3).add(0, .4, 0));
        }
    }

    @Override public boolean finished(Mob flier) { return struck; }
    @Override public void end(Mob flier, boolean broken) { flier.setXRot(0); }
}
