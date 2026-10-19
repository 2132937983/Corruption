package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.entity.SolomonDrake;
import com.miophas.corruption.core.demon.skill.DrakeRushRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Small dragons rushing in from every side ({@link DrakeRushRules}) as the mod carries it out for any boss. At the end of the
 * wind-up the place its quarry stands is marked with a circle of runes, and the drakes ({@link SolomonDrake}) appear about it on
 * the compass, facing it; a moment later they rush through it all at once and on to the far side, where they are gone.
 */
public final class DrakeRushSkill implements RegisteredSkill<DrakeRushRules> {
    private final DrakeRushRules rules;
    private final int colour, accent;
    private final List<SolomonDrake> sent = new ArrayList<>();
    @Nullable private Vec3 mark;

    public DrakeRushSkill(DrakeRushRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public DrakeRushRules rules() { return rules; }
    /** The drakes its last use sent, and where they rush through. */
    public List<SolomonDrake> sent() { return List.copyOf(sent); }
    @Nullable public Vec3 mark() { return mark; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        sent.clear();
        mark = null;
        SkillShows.sound(boss, SoundEvents.ENDER_DRAGON_GROWL, 3, 1.4F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (!rules.timing().strikes(elapsed) || target == null || !(boss.level() instanceof ServerLevel level)) return;
        mark = target.position().add(0, 1, 0);
        for (int index = 0; index < rules.drakes(); index++) {
            double[] start = rules.start(index);
            Vec3 from = mark.add(start[0], start[1], start[2]), to = mark.subtract(start[0], start[1], start[2]);
            sent.add(SolomonDrake.send(level, boss, from, to, rules, colour, accent));
        }
        SkillShows.show(level, SkillShows.ground(level, mark), "magic_circle", 3, .5, rules.gather() + 20, colour, accent, 0, 0, true);
        SkillShows.sound(level, mark, SoundEvents.ENDER_DRAGON_FLAP, 4, 1.6F);
    }
    @Override public BossSkillRegistry.Type<DrakeRushRules> type() { return BossSkillApi.DRAKE_RUSH; }
    @Override public DrakeRushRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
