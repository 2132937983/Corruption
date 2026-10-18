package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.SolomonWyrm;
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

/**
 * The blessing of Venus ({@link VenusRules}): a flood dragon works the Pentacle of Venus on itself and its kin. Through the
 * wind-up it hangs still, a bloom of pale rose light opening about its head; at its end it and every dragon of Solomon's within
 * reach is healed and shielded (Absorption) and set to mending (Regeneration), a dome of the same light about each.
 */
public final class VenusSkill implements BossSkill<SolomonWyrm> {
    private final VenusRules rules;
    private final int colour, accent;
    private final List<SolomonWyrm> blessed = new ArrayList<>();

    public VenusSkill(VenusRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public VenusRules rules() { return rules; }
    /** Those its last blessing reached. */
    public List<SolomonWyrm> blessed() { return List.copyOf(blessed); }

    @Override public SkillTiming timing() { return rules.timing(); }

    /** Whenever one of its kin within reach (itself among them) is hurt enough to want it. */
    @Override public boolean usable(SolomonWyrm dragon, LivingEntity target) {
        double lowest = 1;
        for (SolomonWyrm kin : kin(dragon)) lowest = Math.min(lowest, kin.getHealth() / kin.getMaxHealth());
        return rules.wanted(lowest);
    }

    @Override public void begin(SolomonWyrm dragon, LivingEntity target) {
        blessed.clear();
        SkillShows.sound(dragon, SoundEvents.AMETHYST_BLOCK_RESONATE, 4, 1.2F);
        if (dragon.level() instanceof ServerLevel level)
            SkillShows.show(level, dragon.headCentre(), "bloom", 4, 2, rules.timing().windup() + 10, colour, accent, 0, dragon.getYRot(), false);
    }

    @Override public void tick(SolomonWyrm dragon, @Nullable LivingEntity target, int elapsed) {
        if (!rules.timing().strikes(elapsed) || !(dragon.level() instanceof ServerLevel level)) return;
        for (SolomonWyrm kin : kin(dragon)) {
            kin.heal((float) rules.healed(kin.getMaxHealth()));
            kin.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, rules.absorptionTicks(), rules.absorptionLevel() - 1), dragon);
            kin.addEffect(new MobEffectInstance(MobEffects.REGENERATION, rules.regenerationTicks(), rules.regenerationLevel() - 1), dragon);
            SkillShows.show(level, kin.headCentre(), "dome", 3.5, 3.5, 30, colour, accent, 0, kin.getYRot(), false);
            blessed.add(kin);
        }
        SkillShows.sound(dragon, SoundEvents.BEACON_POWER_SELECT, 4, 1.4F);
    }

    /** Itself and every dragon of Solomon's within its blessing's reach, alive. */
    private List<SolomonWyrm> kin(SolomonWyrm dragon) {
        return dragon.level().getEntitiesOfClass(SolomonWyrm.class, dragon.getBoundingBox().inflate(rules.radius()),
                kin -> kin.isAlive() && rules.reaches(kin.distanceTo(dragon)));
    }
}
