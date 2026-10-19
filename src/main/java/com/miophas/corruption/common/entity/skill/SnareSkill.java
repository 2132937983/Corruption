package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SnareRules;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Roots bursting up under a quarry ({@link SnareRules}) as the mod carries it out for any creature. As it begins, the
 * ground under where the quarry stands is marked with a crack, and through the wind-up it trembles there, earth jumping
 * off it. At its end roots (drawn as spikes of the caster's colour) burst up: everything within reach of the mark that is
 * not of the caster's side (players in creative or spectator aside) is hurt with the corruption's hurt, held fast with
 * Slowness (twice as long on the corruption's own ground) and thrown a little up. The ground is left as it was.
 */
public final class SnareSkill implements RegisteredSkill<SnareRules> {
    private final SnareRules rules;
    private final int colour, accent;
    @Nullable private Vec3 mark;
    private final List<LivingEntity> caught = new ArrayList<>();

    public SnareSkill(SnareRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public SnareRules rules() { return rules; }

    /** Where this use's roots come up, once marked, and what they caught. */
    @Nullable public Vec3 mark() { return mark; }
    public List<LivingEntity> caught() { return List.copyOf(caught); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        caught.clear();
        if (!(boss.level() instanceof ServerLevel level)) {
            mark = target.position();
            return;
        }
        mark = SkillShows.ground(level, target.position());
        SkillShows.show(level, mark, "fissure", rules.radius() + .4, .5, rules.timing().windup() + 4, colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(level, mark, SoundEvents.ROOTED_DIRT_BREAK, 1.5F, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (mark == null || !(boss.level() instanceof ServerLevel level)) return;
        BlockState ground = level.getBlockState(BlockPos.containing(mark).below());
        if (rules.timing().windingUp(elapsed)) {
            if (elapsed % 3 == 0 && !ground.isAir())
                level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), mark.x, mark.y + .1, mark.z, 6, rules.radius() / 2, .05, rules.radius() / 2, .05);
            return;
        }
        if (!rules.timing().strikes(elapsed)) return;
        Vec3 at = mark;
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(rules.radius() + 1, 2, rules.radius() + 1), near -> near != boss
                && near.isAlive() && !boss.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                && rules.catches(Math.hypot(near.getX() - at.x, near.getZ() - at.z)))) {
            near.hurt(ModDamageTypes.corruption(level, boss), (float) rules.damage());
            boolean onCorruption = CorruptionConversions.isCorrupted(level.getBlockState(near.getOnPos()));
            if (rules.holdTicks() > 0)
                near.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, rules.holds(onCorruption), rules.holdLevel()), boss);
            near.push(0, rules.lift(), 0);
            near.hurtMarked = true;
            caught.add(near);
        }
        SkillShows.show(level, at, "spikes", rules.radius() + .3, 1.6, 24, colour, accent, .15, boss.getYRot(), false);
        SkillShows.sound(level, at, SoundEvents.EVOKER_FANGS_ATTACK, 1.5F, .7F);
        if (!ground.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), at.x, at.y + .2, at.z, 14, rules.radius() / 2, .2, rules.radius() / 2, .12);
    }

    @Override public void end(Mob boss, boolean broken) { mark = null; }
    @Override public BossSkillRegistry.Type<SnareRules> type() { return BossSkillApi.SNARE; }
    @Override public SnareRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
