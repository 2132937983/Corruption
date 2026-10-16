package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.entity.CorruptLightning;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The corruption's lightning called down ({@link StrikeRules}) as the mod carries it out for any boss. As it begins,
 * every place a bolt will fall is marked: a circle of the skill's colour turning on the ground there, as wide as the
 * bolt's reach, for as long as it has to wait, with a rumble of thunder, so that its quarry can see it coming and get
 * out of the way. Then, one after another, the corruption's violet bolts fall on the marks, each hurting everything
 * within its reach with the corruption's own hurt, and splashing a ring of light.
 */
public final class StrikeSkill implements BossSkill<Mob> {
    private final StrikeRules rules;
    private final int colour, accent;
    private final List<Vec3> marks = new ArrayList<>();

    public StrikeSkill(StrikeRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public StrikeRules rules() { return rules; }

    /** Where this use's bolts will fall, in the order they fall. */
    public List<Vec3> marks() { return List.copyOf(marks); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        marks.clear();
        if (!(boss.level() instanceof ServerLevel level)) return;
        double turn = boss.getRandom().nextDouble() * Math.PI * 2;
        for (int bolt = 0; bolt < rules.bolts(); bolt++) {
            double[] mark = rules.mark(bolt, turn);
            Vec3 at = SkillShows.ground(level, target.position().add(mark[0], 0, mark[1]));
            marks.add(at);
            SkillShows.show(level, at, "magic_circle", rules.radius(), .6, rules.fallsOn(bolt) + 6, colour, accent, 0, 0, false);
        }
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_ATTACK, 2, .6F);
        SkillShows.sound(level, target.position(), SoundEvents.LIGHTNING_BOLT_THUNDER, .6F, 1.6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (target != null) boss.getLookControl().setLookAt(target, 30, 30);
        int bolt = rules.falling(elapsed);
        if (bolt < 0 || bolt >= marks.size() || !(boss.level() instanceof ServerLevel level)) return;
        fall(level, boss, marks.get(bolt));
    }

    @Override public void end(Mob boss, boolean broken) { marks.clear(); }

    /** One bolt falls here: the violet lightning, its hurt to everything within its reach, and a ring of light. */
    private void fall(ServerLevel level, Mob boss, Vec3 at) {
        CorruptLightning bolt = ModEntities.CORRUPT_LIGHTNING.get().create(level);
        if (bolt != null) {
            bolt.moveTo(at);
            level.addFreshEntity(bolt);
        }
        DamageSource hurt = ModDamageTypes.corruption(level, boss);
        AABB reach = new AABB(at, at).inflate(rules.radius(), 2.5, rules.radius()).expandTowards(0, 1.5, 0);
        for (LivingEntity struck : level.getEntitiesOfClass(LivingEntity.class, reach, struck -> struck != boss && struck.isAlive() && !boss.isAlliedTo(struck)
                && Math.hypot(struck.getX() - at.x, struck.getZ() - at.z) <= rules.radius()))
            struck.hurt(hurt, (float) rules.damage());
        SkillShows.show(level, at, "shockwave", rules.radius() + 1, 1.2, 14, colour, accent, .25, 0, false);
    }
}
