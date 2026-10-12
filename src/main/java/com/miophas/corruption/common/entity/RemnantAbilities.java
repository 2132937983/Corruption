package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantAbilityApi;
import com.miophas.corruption.common.registry.ModEntities;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.phys.Vec3;

/** Per-remnant ability state. Never ticks the source proxy or runs its complete AI. */
final class RemnantAbilities {
    private static final RemnantAbility[] CAST_ABILITIES = { RemnantAbility.SUMMON_VEX, RemnantAbility.EVOKER_FANGS, RemnantAbility.SONIC_BOOM };
    private record Summon(UUID id, long expires) {}
    private final CorruptedRemnant remnant;
    private final EnumMap<RemnantAbility, Integer> readyAt = new EnumMap<>(RemnantAbility.class);
    private final List<Summon> summons = new ArrayList<>();
    UUID summoner;
    int remainingLife = -1;
    private long expiresAt = -1;

    RemnantAbilities(CorruptedRemnant remnant) { this.remnant = remnant; }
    boolean valid(LivingEntity target) {
        return target != null && target.isAlive() && !target.isSpectator() && remnant.canAttack(target);
    }
    boolean inRange(RemnantAbility ability, LivingEntity target) {
        double dx = target.getX() - remnant.getX(), dz = target.getZ() - remnant.getZ();
        return RemnantAbilityApi.inRange(ability, dx * dx + dz * dz, target.getY() - remnant.getY());
    }
    RemnantAbility choose() {
        if (!remnant.hasAbility(RemnantAbility.SUMMON_VEX) && !remnant.hasAbility(RemnantAbility.EVOKER_FANGS)
                && !remnant.hasAbility(RemnantAbility.SONIC_BOOM)) return null;
        LivingEntity target = remnant.getTarget();
        if (!valid(target)) return null;
        for (RemnantAbility ability : CAST_ABILITIES) {
            if (!remnant.hasAbility(ability) || remnant.tickCount < readyAt.getOrDefault(ability, 0) || !inRange(ability, target)) continue;
            // Sonic boom deliberately penetrates terrain, as the native Warden ability does.
            if (ability != RemnantAbility.SONIC_BOOM && !remnant.getSensing().hasLineOfSight(target)) continue;
            if (ability == RemnantAbility.SONIC_BOOM && remnant.distanceToSqr(target) < 9) continue;
            if (ability == RemnantAbility.SUMMON_VEX && RemnantAbilityApi.summonCount(ownedCount()) == 0) continue;
            return ability;
        }
        return null;
    }
    void begin(RemnantAbility ability) {
        readyAt.put(ability, remnant.tickCount + RemnantAbilityApi.spec(ability).reuseTicks());
        remnant.beginAbility(ability);
        remnant.playSound(switch (ability) {
            case SUMMON_VEX -> SoundEvents.EVOKER_PREPARE_SUMMON;
            case EVOKER_FANGS -> SoundEvents.EVOKER_PREPARE_ATTACK;
            case SONIC_BOOM -> SoundEvents.WARDEN_SONIC_CHARGE;
            default -> throw new IllegalArgumentException("Not a spell");
        }, ability == RemnantAbility.SONIC_BOOM ? 3 : 1, 1);
    }
    void perform(RemnantAbility ability, LivingEntity target) {
        if (!(remnant.level() instanceof ServerLevel level) || !valid(target) || !inRange(ability, target)) return;
        switch (ability) {
            case SUMMON_VEX -> summon(level, target);
            case EVOKER_FANGS -> fangs(level, target);
            case SONIC_BOOM -> sonic(level, target);
            default -> {}
        }
    }
    private int ownedCount() {
        if (!(remnant.level() instanceof ServerLevel level)) return summons.size();
        summons.removeIf(summon -> summon.expires <= level.getGameTime()
                || level.getEntity(summon.id) instanceof CorruptedRemnant child && !child.isAlive());
        // An unloaded child keeps its slot until expiry: never scan or load its chunk to count it.
        return summons.size();
    }
    void release(UUID child) { summons.removeIf(summon -> summon.id.equals(child)); }
    private void summon(ServerLevel level, LivingEntity target) {
        int count = RemnantAbilityApi.summonCount(ownedCount());
        for (int i = 0; i < count; i++) {
            BlockPos pos = remnant.blockPosition().offset(remnant.getRandom().nextInt(5) - 2, 1, remnant.getRandom().nextInt(5) - 2);
            if (level.isOutsideBuildHeight(pos) || !level.hasChunkAt(pos)) continue;
            var source = EntityType.VEX.create(level);
            var child = ModEntities.CORRUPTED_REMNANT.get().create(level);
            if (source == null || child == null) continue;
            source.moveTo(pos, 0, 0);
            source.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
            child.initialize(RemnantAppearance.capture(source, true), source.getMaxHealth());
            child.moveTo(pos.getX() + .5, pos.getY(), pos.getZ() + .5, remnant.getYRot(), 0);
            child.setTarget(target);
            int life = 20 * (30 + remnant.getRandom().nextInt(90));
            child.abilities().summoner = remnant.getUUID();
            child.abilities().remainingLife = life;
            child.abilities().expiresAt = level.getGameTime() + life;
            if (level.addFreshEntity(child)) summons.add(new Summon(child.getUUID(), level.getGameTime() + life));
        }
        remnant.playSound(SoundEvents.EVOKER_CAST_SPELL, 1, 1);
    }
    private void sonic(ServerLevel level, LivingEntity target) {
        Vec3 origin = remnant.position().add(0, remnant.getBbHeight() * .6, 0);
        Vec3 delta = target.getEyePosition().subtract(origin), direction = delta.normalize();
        for (int i = 1, length = Mth.floor(delta.length()) + 7; i < length; i++) {
            Vec3 pos = origin.add(direction.scale(i));
            level.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
        remnant.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3, 1);
        if (target.hurt(level.damageSources().sonicBoom(remnant), RemnantAbilityApi.sonicDamage())) {
            double resistance = 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            target.push(direction.x * 2.5 * resistance, direction.y * .5 * resistance, direction.z * 2.5 * resistance);
        }
    }
    private void fangs(ServerLevel level, LivingEntity target) {
        double minY = Math.min(target.getY(), remnant.getY()), maxY = Math.max(target.getY(), remnant.getY()) + 1;
        float angle = (float) Mth.atan2(target.getZ() - remnant.getZ(), target.getX() - remnant.getX());
        if (remnant.distanceToSqr(target) < 9) {
            for (int i = 0; i < 5; i++) fang(level, angle + i * Mth.PI * .4F, 1.5, minY, maxY, 0);
            for (int i = 0; i < 8; i++) fang(level, angle + i * Mth.TWO_PI / 8 + Mth.TWO_PI / 5, 2.5, minY, maxY, 3);
        } else for (int i = 0; i < 16; i++) fang(level, angle, 1.25 * (i + 1), minY, maxY, i);
        remnant.playSound(SoundEvents.EVOKER_CAST_SPELL, 1, 1);
    }
    private void fang(ServerLevel level, float angle, double distance, double minY, double maxY, int delay) {
        double x = remnant.getX() + Mth.cos(angle) * distance, z = remnant.getZ() + Mth.sin(angle) * distance;
        BlockPos pos = BlockPos.containing(x, maxY, z);
        while (pos.getY() >= Mth.floor(minY) - 1) {
            if (level.isOutsideBuildHeight(pos) || !level.hasChunkAt(pos) || !level.hasChunkAt(pos.below())) return;
            if (level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                var shape = level.getBlockState(pos).getCollisionShape(level, pos);
                double y = pos.getY() + (shape.isEmpty() ? 0 : shape.max(Direction.Axis.Y));
                level.addFreshEntity(new EvokerFangs(level, x, y, z, angle, delay, remnant));
                return;
            }
            pos = pos.below();
        }
    }
    void tick() {
        if (!(remnant.level() instanceof ServerLevel level) || !remnant.isAlive()) return;
        if (remainingLife >= 0 && (--remainingLife <= 0 || expiresAt >= 0 && level.getGameTime() >= expiresAt)) { remnant.discard(); return; }
        if (summoner != null && !remnant.isNoAi() && remnant.tickCount % 10 == 0
                && level.getEntity(summoner) instanceof CorruptedRemnant owner && valid(owner.getTarget())) remnant.setTarget(owner.getTarget());
    }
    void save(CompoundTag tag) {
        CompoundTag state = new CompoundTag();
        readyAt.forEach((ability, ready) -> state.putInt(ability.name(), Math.max(0, ready - remnant.tickCount)));
        ListTag children = new ListTag();
        for (Summon summon : summons) {
            CompoundTag child = new CompoundTag(); child.putUUID("Id", summon.id); child.putLong("Expires", summon.expires); children.add(child);
        }
        state.put("Summons", children);
        if (summoner != null) state.putUUID("Summoner", summoner);
        state.putInt("RemainingLife", remainingLife); state.putLong("ExpiresAt", expiresAt); tag.put("RemnantAbilities", state);
    }
    void load(CompoundTag tag) {
        if (!tag.contains("RemnantAbilities", 10)) return;
        readyAt.clear(); summons.clear();
        CompoundTag state = tag.getCompound("RemnantAbilities");
        for (RemnantAbility ability : CAST_ABILITIES)
            readyAt.put(ability, remnant.tickCount + Math.clamp(state.getInt(ability.name()), 0, RemnantAbilityApi.spec(ability).reuseTicks()));
        ListTag children = state.getList("Summons", 10);
        for (int i = 0; i < Math.min(children.size(), RemnantAbilityApi.maxOwnedVex()); i++) {
            CompoundTag child = children.getCompound(i);
            if (child.hasUUID("Id")) summons.add(new Summon(child.getUUID("Id"), child.getLong("Expires")));
        }
        summoner = state.hasUUID("Summoner") ? state.getUUID("Summoner") : null;
        remainingLife = state.contains("RemainingLife") ? Math.clamp(state.getInt("RemainingLife"), -1, 2400) : -1;
        expiresAt = state.contains("ExpiresAt") ? state.getLong("ExpiresAt") : -1;
    }
}
