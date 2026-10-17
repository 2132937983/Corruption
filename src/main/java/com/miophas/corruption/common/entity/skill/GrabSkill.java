package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.GrabRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * A grab with a tentacle ({@link GrabRules}) as the mod carries it out for any creature. Through the wind-up it lashes
 * a tentacle out at its quarry (its model draws it reaching, from {@link #reachingFor}); at its end, if its quarry is still
 * in reach and in sight, it has hold of it: it hurts it, pulls it off whatever it rides, and drags it in to itself, and
 * down under the water once it is in it, until the quarry is close or the skill's time is up. A creature in creative or
 * spectator slips its hold.
 */
public final class GrabSkill implements BossSkill<Mob> {
    /** How much further than its reach a quarry may be as it takes hold (it moved through the wind-up), and before it lets go. */
    private static final double SLACK = 1.25, SNAP = 1.6;

    private final GrabRules rules;
    private final int colour, accent;
    @Nullable private LivingEntity quarry, held;
    private boolean missed, drawnIn;

    public GrabRules rules() { return rules; }

    public GrabSkill(GrabRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    /** What it is reaching for, through the wind-up, or has hold of; null if nothing. */
    @Nullable public LivingEntity reachingFor() { return held != null ? held : missed ? null : quarry; }
    /** What it has hold of, if anything. */
    @Nullable public LivingEntity held() { return held; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        quarry = target;
        held = null;
        missed = drawnIn = false;
        SkillShows.sound(boss, SoundEvents.SQUID_SQUIRT, 1.5F, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.setDeltaMovement(boss.getDeltaMovement().scale(.6));
        if (rules.timing().windingUp(elapsed)) {
            if (quarry != null) MeleeSkill.face(boss, quarry.position());
            return;
        }
        if (rules.timing().strikes(elapsed)) takeHold(boss);
        if (held == null) return;
        if (!held.isAlive() || held.isRemoved() || held.level() != boss.level() || slips(held) || boss.distanceTo(held) > rules.reach() * SNAP) {
            held = null;
            missed = true;
            return;
        }
        Vec3 to = boss.getBoundingBox().getCenter().subtract(held.position());
        if (rules.drawnIn(boss.distanceTo(held))) {
            drawnIn = true;
            return;
        }
        Vec3 pull = to.normalize().scale(rules.pull());
        // Under once in the water; over the edge first, while it is on the ground out of it.
        double y = held.isInWater() ? pull.y - rules.down() : held.onGround() ? Math.max(pull.y, .12) : pull.y;
        held.setDeltaMovement(held.getDeltaMovement().scale(.4).add(pull.x, y, pull.z));
        held.hurtMarked = true;
        held.fallDistance = 0;
        MeleeSkill.face(boss, held.position());
        if (boss.level() instanceof ServerLevel level && elapsed % 3 == 0)
            level.sendParticles(held.isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.SPLASH, held.getX(), held.getY() + held.getBbHeight() / 2, held.getZ(),
                    4, .3, .3, .3, .05);
    }

    /** At the end of the wind-up: its quarry, if still in reach and in sight, is caught, hurt, and torn off what it rides. */
    private void takeHold(Mob boss) {
        LivingEntity caught = quarry;
        if (caught == null || !caught.isAlive() || slips(caught) || boss.distanceTo(caught) > rules.reach() * SLACK || !boss.hasLineOfSight(caught)) {
            missed = true;
            SkillShows.sound(boss, SoundEvents.SLIME_SQUISH, 1, .5F);
            return;
        }
        held = caught;
        caught.hurt(boss.damageSources().mobAttack(boss), (float) rules.damage());
        if (caught.isPassenger()) caught.stopRiding();
        SkillShows.sound(boss, SoundEvents.SLIME_ATTACK, 1.5F, .5F);
        SkillShows.sound(boss, SoundEvents.FISHING_BOBBER_RETRIEVE, 1.5F, .6F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, caught.getBoundingBox().getCenter(), "chains", 1, 1.5, 16, colour, accent, 0, boss.getYRot(), false);
    }

    private static boolean slips(LivingEntity caught) { return caught instanceof Player player && (player.isCreative() || player.isSpectator()); }

    /** Over once its tentacle has missed, lost its hold, or drawn its quarry in. */
    @Override public boolean finished(Mob boss) { return missed || drawnIn; }

    @Override public void end(Mob boss, boolean broken) {
        quarry = held = null;
    }
}
