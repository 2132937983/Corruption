package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Calling up the corruption's remnants ({@link SummonRules}) as the mod carries it out for any boss. A circle opens
 * through the wind-up under the boss (or under its quarry, where it stood as the call began, for a call round the
 * quarry); then the remnants rise round it, each over a whirl of souls, in the likenesses asked for (armed and dressed
 * as such creatures come), at once set on the boss's quarry. Each is marked as the boss's, so that only those still
 * about count against how many it may keep.
 */
public final class SummonSkill implements BossSkill<Mob> {
    /** Where a called remnant keeps who called it up. */
    public static final String CALLER = "corruption:called_by";
    /** How far round its caller a remnant still counts as about. */
    private static final double ABOUT = 48;

    private final SummonRules rules;
    private final int colour, accent;
    /** Where this call's remnants rise round. */
    private Vec3 middle = Vec3.ZERO;

    public SummonSkill(SummonRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public SummonRules rules() { return rules; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.wanted(called(boss)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        middle = rules.atQuarry() && target != null ? target.position() : boss.position();
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_SUMMON, 2, .6F);
        if (boss.level() instanceof ServerLevel level) {
            middle = SkillShows.ground(level, middle);
            SkillShows.show(level, middle, "magic_circle", rules.radius() + 1.5, 1.5, rules.timing().windup() + 20, colour, accent, 0, boss.getYRot(), true);
        }
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (!rules.timing().strikes(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        int calling = rules.toCall(called(boss)), start = boss.getRandom().nextInt(Math.max(1, rules.looks().size()));
        double turn = boss.getRandom().nextDouble() * Math.PI * 2;
        for (int one = 0; one < calling; one++) {
            double[] place = rules.place(one, calling, turn);
            Vec3 at = SkillShows.ground(level, (rules.atQuarry() ? middle : boss.position()).add(place[0], 0, place[1]));
            EntityType<?> likeness = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(rules.look(one, start))).orElse(EntityType.ZOMBIE);
            CorruptedRemnant remnant = CorruptedRemnant.raised(level, likeness, at, boss.getYRot());
            if (remnant == null) continue;
            remnant.getPersistentData().putUUID(CALLER, boss.getUUID());
            if (rules.passes()) remnant.crumbleAt(level.getGameTime() + rules.lasts());
            if (target != null) remnant.setTarget(target);
            if (!level.addFreshEntity(remnant)) continue;
            SkillShows.show(level, at, "souls", 1.2, 3, 30, colour, accent, 0, 0, true);
        }
        SkillShows.sound(boss, SoundEvents.EVOKER_CAST_SPELL, 2, .7F);
    }

    /** How many of the remnants this boss called up are still about. */
    public static int called(Mob boss) {
        UUID caller = boss.getUUID();
        return boss.level().getEntitiesOfClass(CorruptedRemnant.class, boss.getBoundingBox().inflate(ABOUT), remnant -> remnant.isAlive()
                && remnant.getPersistentData().hasUUID(CALLER) && caller.equals(remnant.getPersistentData().getUUID(CALLER))).size();
    }
}
