package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.PsychicStreamRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * A thread drawn out of a quarry's mind ({@link PsychicStreamRules}) as the mod carries it out for any creature. Through
 * the wind-up it fixes its quarry, an eye opening over the quarry's head and a heartbeat sounding. Then at every pulse,
 * while it sees its quarry and holds it within reach, a thread of light runs from it (from where a {@link SpitCaster}
 * says, else from its eyes) to the quarry's eyes, and the quarry is hurt in the mind (corruption:psychic, which armour
 * does not turn aside; a moment's protection after another blow does not keep it off), the caster healed by what it
 * took; a ringing over the quarry's head now and then. Pulses that find the quarry gone, hidden or out of reach, as many
 * one after another as the rules allow, break the thread, and the skill is over.
 */
public final class PsychicStreamSkill implements BossSkill<Mob> {
    /** How far apart the motes of light along the thread are, in blocks. */
    private static final double MOTES = .4;

    private final PsychicStreamRules rules;
    private final int colour, accent;
    private int landed, missed;
    private boolean broken;

    public PsychicStreamSkill(PsychicStreamRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public PsychicStreamRules rules() { return rules; }

    /** How many pulses of this use have hurt its quarry. */
    public int landed() { return landed; }

    /** Whether this use's thread has broken. */
    public boolean broken() { return broken; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        landed = missed = 0;
        broken = false;
        SkillShows.sound(boss, SoundEvents.WARDEN_HEARTBEAT, 2, .8F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, target.position().add(0, target.getBbHeight() + .9, 0), "eye", .6, 0, rules.timing().windup() + 6, colour, accent, 0,
                    boss.getYRot(), false);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (target != null) boss.getLookControl().setLookAt(target, 30, 30);
        if (!rules.pulses(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        if (target == null || !holds(boss, target)) {
            if (++missed >= rules.breaksAfter()) broken = true;
            return;
        }
        missed = 0;
        Vec3 to = target.getEyePosition();
        thread(level, from(boss), to);
        target.invulnerableTime = 0;
        if (target.hurt(ModDamageTypes.psychic(level, boss), (float) rules.damage())) {
            landed++;
            if (rules.heal() > 0) boss.heal((float) rules.heal());
            if (landed % 3 == 1) SkillShows.show(level, to, "resonance", .6, 0, 12, colour, accent, .1, boss.getYRot(), false);
        }
        SkillShows.sound(level, to, SoundEvents.AMETHYST_BLOCK_RESONATE, 1, .5F + .12F * (landed % 4));
    }

    /** Over once its thread has broken. */
    @Override public boolean finished(Mob boss) { return broken; }

    /** Whether the thread still holds this quarry: alive, in sight and within reach, and no player in creative or spectator. */
    private boolean holds(Mob boss, LivingEntity target) {
        return target.isAlive() && target.level() == boss.level() && !(target instanceof Player player && (player.isCreative() || player.isSpectator()))
                && rules.holds(boss.distanceTo(target)) && boss.hasLineOfSight(target);
    }

    /** Where the thread leaves the caster. */
    private static Vec3 from(Mob boss) { return boss instanceof SpitCaster caster ? caster.spitFrom(0) : boss.getEyePosition(); }

    /** The thread: motes of the caster's light from it to its quarry, and souls rising off the quarry's head. */
    private void thread(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 way = to.subtract(from);
        int motes = Math.max(2, (int) Math.ceil(way.length() / MOTES));
        DustParticleOptions light = new DustParticleOptions(new Vector3f(((colour >> 16) & 255) / 255F, ((colour >> 8) & 255) / 255F, (colour & 255) / 255F), 1.1F);
        for (int mote = 0; mote <= motes; mote++) {
            Vec3 at = from.add(way.scale((double) mote / motes));
            level.sendParticles(light, at.x, at.y, at.z, 1, .02, .02, .02, 0);
        }
        level.sendParticles(ParticleTypes.SCULK_SOUL, to.x, to.y + .2, to.z, 2, .2, .1, .2, .01);
    }
}
