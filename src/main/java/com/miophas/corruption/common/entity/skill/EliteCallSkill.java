package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.api.plant.MaliceVineApi;
import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.EliteCallRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Calling up the corruption's elites about a quarry ({@link EliteCallRules}) as the mod carries it out for any boss. Through
 * the wind-up a magic circle opens under the quarry; at its end three or four elites, each of a kind the malice vine broods
 * (the tag corruption:vine_brood), rise in a miasma on the ground about it and turn on it. A boss no robe deceives calls up
 * elites no robe deceives (CorruptionVeilApi).
 */
public final class EliteCallSkill implements BossSkill<Mob> {
    private final EliteCallRules rules;
    private final int colour, accent;
    private final List<Mob> called = new ArrayList<>();

    public EliteCallSkill(EliteCallRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public EliteCallRules rules() { return rules; }
    /** The elites its last use called up. */
    public List<Mob> called() { return List.copyOf(called); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && rules.wanted(about(target)); }

    /** How many of the corruption's elites stand about a quarry. */
    private int about(LivingEntity target) {
        return target.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(rules.radius() * 2),
                elite -> elite.isAlive() && CorruptionFactionApi.isElite(elite)).size();
    }

    @Override public void begin(Mob boss, LivingEntity target) {
        called.clear();
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, SkillShows.ground(level, target.position()), "magic_circle", rules.radius(), .5, rules.timing().windup() + 10, colour, accent, 0, 0, true);
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_SUMMON, 3, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (!rules.timing().strikes(elapsed) || target == null || !(boss.level() instanceof ServerLevel level)) return;
        List<EntityType<?>> kinds = BuiltInRegistries.ENTITY_TYPE.getTag(MaliceVineApi.BROOD)
                .map(set -> set.stream().<EntityType<?>>map(Holder::value).toList()).orElse(List.of());
        if (kinds.isEmpty()) return;
        int count = rules.count(about(target), boss.getRandom().nextDouble());
        double turn = boss.getRandom().nextDouble() * Math.PI * 2;
        for (int index = 0; index < count; index++) {
            EntityType<?> kind = kinds.get(boss.getRandom().nextInt(kinds.size()));
            double[] off = rules.place(index, count, turn);
            Vec3 at = stand(level, kind, target.position().add(off[0], 0, off[1]));
            if (at == null) continue;
            if (!(kind.create(level) instanceof Mob elite)) continue;
            elite.moveTo(at.x, at.y, at.z, boss.getRandom().nextFloat() * 360, 0);
            EventHooks.finalizeMobSpawn(elite, level, level.getCurrentDifficultyAt(BlockPos.containing(at)), MobSpawnType.MOB_SUMMONED, null);
            if (CorruptionVeilApi.pierces(boss)) CorruptionVeilApi.unveil(elite);
            elite.setTarget(target);
            if (!level.addFreshEntity(elite)) continue;
            called.add(elite);
            SkillShows.show(level, at, "miasma", 1.6, 2.5, 30, colour, accent, 0, 0, false);
        }
        SkillShows.sound(boss, SoundEvents.EVOKER_CAST_SPELL, 3, .5F);
    }

    /** A place on the ground near here where an elite of this kind stands clear, or null. */
    @Nullable private static Vec3 stand(ServerLevel level, EntityType<?> kind, Vec3 near) {
        int x = Mth.floor(near.x), z = Mth.floor(near.z), top = Mth.floor(near.y) + 6, bottom = Mth.floor(near.y) - 8;
        for (int y = top; y >= bottom; y--) {
            BlockPos below = new BlockPos(x, y - 1, z);
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) continue;
            AABB box = kind.getSpawnAABB(x + .5, y, z + .5);
            if (level.noCollision(box) && !level.containsAnyLiquid(box)) return new Vec3(x + .5, y, z + .5);
        }
        return null;
    }
}
