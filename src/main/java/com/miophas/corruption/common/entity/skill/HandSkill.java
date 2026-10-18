package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.SolomonHand;
import com.miophas.corruption.core.demon.skill.HandRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Giant hands out of the ground ({@link HandRules}) as the mod carries them out for any boss. Through the wind-up the boss
 * holds still, turned on its quarry, and the ground under its quarry rumbles; at its end the hands begin to grow
 * ({@link SolomonHand}): the first under its quarry, the others round it, each over a circle of runes where it will fall, all
 * facing in on the first.
 */
public final class HandSkill implements BossSkill<Mob> {
    private final HandRules rules;
    private final int colour, accent;
    private final List<SolomonHand> raised = new ArrayList<>();

    public HandSkill(HandRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public HandRules rules() { return rules; }
    /** The hands its last use raised. */
    public List<SolomonHand> raised() { return List.copyOf(raised); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        raised.clear();
        SkillShows.sound(boss, SoundEvents.WARDEN_EMERGE, 3, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.setDeltaMovement(boss.getDeltaMovement().scale(.8));
        if (!rules.timing().strikes(elapsed) || target == null || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 middle = SkillShows.ground(level, target.position());
        double turn = boss.getRandom().nextDouble() * Math.PI * 2;
        for (int index = 0; index < rules.hands(); index++) {
            double[] off = rules.place(index, turn);
            Vec3 at = SkillShows.ground(level, middle.add(off[0], 0, off[1]));
            // Each faces in on the middle; the one in the middle faces the boss's way.
            Vec3 in = index == 0 ? at.subtract(boss.position()) : middle.subtract(at);
            float yaw = (float) (Mth.atan2(-in.x, in.z) * Mth.RAD_TO_DEG);
            raised.add(SolomonHand.raise(level, boss, at, yaw, rules, colour, accent));
        }
        SkillShows.sound(level, middle, SoundEvents.WARDEN_DIG, 4, .4F);
    }
}
