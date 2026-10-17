package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.common.entity.DemonBolt;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Spitting the corruption's bolts ({@link SpitRules}) as the mod carries it out for any creature. Through the wind-up it
 * faces its quarry, drawing back; then it looses its shots ({@link DemonBolt}, looking as the rules say) one after another
 * from its mouth (where a {@link SpitCaster} says, else a little before its eyes): each straight at where its quarry is
 * as it is loosed, swept across it as the fan goes; or lobbed high to come down on it, each a little off the last, as a
 * spray of acid falls.
 */
public final class SpitSkill implements BossSkill<Mob> {
    /** How far apart lobbed shots come down about their quarry, in blocks, one after another. */
    private static final double SCATTER = 1.3;

    private final SpitRules rules;
    private final int colour, accent;
    private int loosed;
    @Nullable private Vec3 last;

    public SpitSkill(SpitRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public SpitRules rules() { return rules; }

    /** How many shots this use has loosed, and where its last was aimed. */
    public int loosed() { return loosed; }
    @Nullable public Vec3 last() { return last; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        loosed = 0;
        last = null;
        SkillShows.sound(boss, drawing(), 1.5F, .7F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.5, boss.isNoGravity() ? .5 : 1, .5));
        if (target == null) return;
        MeleeSkill.face(boss, target.position());
        int index = rules.shot(elapsed);
        if (index < 0 || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 from = boss instanceof SpitCaster caster ? caster.spitFrom(index)
                : boss.getEyePosition().add(Vec3.directionFromRotation(0, boss.getYRot()).scale(boss.getBbWidth() * .6));
        Vec3 velocity;
        double gravity = 0;
        if (rules.lob()) {
            double turn = boss.getRandom().nextDouble() * Math.PI * 2, off = index == 0 ? 0 : SCATTER * Math.sqrt(index);
            Vec3 land = SkillShows.ground(level, target.position().add(Math.cos(turn) * off, 0, Math.sin(turn) * off));
            double[] thrown = rules.lobbed(land.x - from.x, land.y - from.y, land.z - from.z);
            velocity = new Vec3(thrown[0], thrown[1], thrown[2]);
            gravity = SpitRules.GRAVITY;
            last = land;
        } else {
            Vec3 at = target.getBoundingBox().getCenter();
            velocity = at.subtract(from).normalize().yRot((float) Math.toRadians(rules.across(index))).scale(rules.speed());
            last = at;
        }
        if (DemonBolt.loose(level, boss, from, velocity, rules.damage(), rules.splash(), gravity, colour, rules.look(), rules.push(), rules.pool()) != null)
            loosed++;
        // A flash at its mouth as the first is loosed.
        if (index == 0) SkillShows.show(level, from, "nova", .6, 0, 6, colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(boss, loosing(), 1.2F, .9F + boss.getRandom().nextFloat() * .3F);
    }

    /** The sound of its drawing back, and of each shot, by what it spits. */
    private SoundEvent drawing() {
        return switch (rules.look()) {
            case FEATHER -> SoundEvents.PHANTOM_FLAP;
            case GLOB -> SoundEvents.SLIME_SQUISH;
            case WATER -> SoundEvents.GUARDIAN_ATTACK;
            default -> SoundEvents.EVOKER_PREPARE_ATTACK;
        };
    }

    private SoundEvent loosing() {
        return switch (rules.look()) {
            case FEATHER -> SoundEvents.ARROW_SHOOT;
            case GLOB -> SoundEvents.LLAMA_SPIT;
            case WATER -> SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE;
            default -> SoundEvents.BLAZE_SHOOT;
        };
    }
}
