package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BlessingCaster;
import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.VenusRules;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/** A reusable blessing; the host may select its allies through BlessingCaster. */
public final class VenusSkill implements RegisteredSkill<VenusRules> {
    private final VenusRules rules;
    private final int colour, accent;
    private final List<LivingEntity> blessed = new ArrayList<>();

    public VenusSkill(VenusRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public VenusRules rules() { return rules; }
    /** Those its last blessing reached. */
    public List<LivingEntity> blessed() { return List.copyOf(blessed); }

    @Override public SkillTiming timing() { return rules.timing(); }

    /** Whenever one of its kin within reach (itself among them) is hurt enough to want it. */
    @Override public boolean usable(Mob dragon, LivingEntity target) {
        double lowest = 1;
        for (LivingEntity kin : kin(dragon)) lowest = Math.min(lowest, kin.getHealth() / kin.getMaxHealth());
        return rules.wanted(lowest);
    }

    @Override public void begin(Mob dragon, LivingEntity target) {
        blessed.clear();
        SkillShows.sound(dragon, SoundEvents.AMETHYST_BLOCK_RESONATE, 4, 1.2F);
        if (dragon.level() instanceof ServerLevel level)
            SkillShows.show(level, origin(dragon), "bloom", 4, 2, rules.timing().windup() + 10, colour, accent, 0, dragon.getYRot(), false);
    }

    @Override public void tick(Mob dragon, @Nullable LivingEntity target, int elapsed) {
        if (!rules.timing().strikes(elapsed) || !(dragon.level() instanceof ServerLevel level)) return;
        for (LivingEntity kin : kin(dragon)) {
            kin.heal((float) rules.healed(kin.getMaxHealth()));
            kin.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, rules.absorptionTicks(), rules.absorptionLevel() - 1), dragon);
            kin.addEffect(new MobEffectInstance(MobEffects.REGENERATION, rules.regenerationTicks(), rules.regenerationLevel() - 1), dragon);
            SkillShows.show(level, origin(kin), "dome", 3.5, 3.5, 30, colour, accent, 0, kin.getYRot(), false);
            blessed.add(kin);
        }
        SkillShows.sound(dragon, SoundEvents.BEACON_POWER_SELECT, 4, 1.4F);
    }

    /** Itself and every selected ally within its blessing's reach, alive. */
    private List<LivingEntity> kin(Mob caster) {
        Iterable<? extends LivingEntity> candidates = caster instanceof BlessingCaster blessing
                ? blessing.blessingTargets(rules.radius())
                : caster.level().getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(rules.radius()),
                        entity -> entity == caster || caster.isAlliedTo(entity));
        List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity ally : candidates)
            if (ally != null && ally.isAlive() && ally.level() == caster.level() && rules.reaches(ally.distanceTo(caster)) && !result.contains(ally)) result.add(ally);
        return result;
    }
    private static Vec3 origin(LivingEntity entity) {
        return entity instanceof BlessingCaster caster ? caster.blessingOrigin() : entity.getBoundingBox().getCenter();
    }
    @Override public BossSkillRegistry.Type<VenusRules> type() { return BossSkillApi.VENUS; }
    @Override public VenusRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
