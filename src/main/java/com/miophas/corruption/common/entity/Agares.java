package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLegs;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The eagle-crocodile. It closes on its prey and then stands: what is under it is stomped, what is in front
 * is raked up in its jaws, what is behind is swept by its tail, and what flies is beaten down by its wings.
 */
public final class Agares extends Colossus {
    /**
     * Its death, in frost, thirty times the size it is written at: a ring of frost racing out as it falls, ice bursting
     * up all round it and the ground splitting under it, a storm breaking over it, a storm of its feathers bursting from
     * its breast, a second ring as it sinks onto its haunches, and its heart bursting as it goes down; a pillar of light
     * stands where it fell, and souls rise out of the ground.
     */
    public static final BossDeath DEATH = new BossDeath(AgaresApi.deathTicks(), 90, 30, new BossDeath.Rays(24, 18, 0x3F8FD0, 0xF0FCFF), .3F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.FEET, "shockwave", 16, 3, 50, 0x7FC8FF, 0xF0FCFF, .5, "minecraft:entity.ender_dragon.death", 64, .45F),
            BossDeath.beat(20, BossDeath.Anchor.FEET, "spikes", 12, 5, 160, 0x7FC8FF, 0xF0FCFF, .2, "minecraft:block.glass.break", 64, .4F),
            BossDeath.beat(45, BossDeath.Anchor.FEET, "fissure", 16, 3, 180, 0x3F8FD0, 0xE0F8FF, 0, "minecraft:item.mace.smash_ground_heavy", 64, .4F),
            BossDeath.beat(90, BossDeath.Anchor.FEET, "lightning", 14, 30, 120, 0x9ADFFF, 0xFFFFFF, .3, "minecraft:entity.lightning_bolt.thunder", 64, .5F),
            BossDeath.beat(120, BossDeath.Anchor.HEART, "feathers", 6, 2, 100, 0x9ADFFF, 0xF0FCFF, 0, "minecraft:entity.ender_dragon.flap", 64, .5F),
            BossDeath.beat(156, BossDeath.Anchor.FEET, "shockwave", 22, 4, 50, 0x7FC8FF, 0xF0FCFF, .5, "minecraft:entity.generic.explode", 64, .4F),
            BossDeath.beat(256, BossDeath.Anchor.HEART, "nova", 20, 0, 40, 0x7FC8FF, 0xFFFFFF, 1, "minecraft:entity.generic.explode", 64, .3F),
            BossDeath.beat(256, BossDeath.Anchor.FEET, "light_pillar", 3, 40, 80, 0x9ADFFF, 0xFFFFFF, 0, "minecraft:block.beacon.deactivate", 64, .5F),
            BossDeath.beat(258, BossDeath.Anchor.FEET, "shockwave", 30, 5, 60, 0x7FC8FF, 0xF0FCFF, .7, "minecraft:entity.lightning_bolt.thunder", 64, .4F),
            BossDeath.beat(260, BossDeath.Anchor.FEET, "souls", 5, 28, 90, 0x9ADFFF, 0xF0FCFF, 0, "minecraft:particle.soul_escape", 64, .5F)));

    private final Set<UUID> struck = new HashSet<>();
    private long nextScreech;

    public Agares(EntityType<? extends Agares> type, Level level) { super(type, level, AgaresApi.parts(), AgaresApi.blocks(1)); }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, AgaresApi.maxHealth()).add(Attributes.ARMOR, AgaresApi.armor())
                .add(Attributes.ARMOR_TOUGHNESS, AgaresApi.armorToughness()).add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.FOLLOW_RANGE, AgaresApi.sightBlocks());
    }

    public AgaresAttack attack() { return AgaresAttack.byId(attackId()); }

    @Override protected ColossusLegs legs() { return AgaresApi.legs(); }
    @Override protected ColossusGait gait(float cycle, float walk, int attack, float attackTicks, float collapse) { return AgaresApi.gait(cycle, walk, AgaresAttack.byId(attack), attackTicks, collapse); }
    @Override protected int deathTicks() { return AgaresApi.deathTicks(); }
    @Override protected double heightBlocks() { return AgaresApi.heightBlocks(); }
    @Override public BossDeath death() { return DEATH; }
    @Override protected double sightBlocks() { return AgaresApi.sightBlocks(); }
    @Override protected double walkBlocksPerTick() { return AgaresApi.walkBlocksPerTick(); }
    @Override protected float turnDegreesPerTick() { return AgaresApi.turnDegreesPerTick(); }
    @Override protected float walkFacingDegrees() { return AgaresApi.walkFacingDegrees(); }
    @Override protected int attackCooldownTicks() { return AgaresApi.attackCooldownTicks(); }
    @Override protected float damageTaken(boolean weakPart) { return AgaresApi.damageTaken(weakPart); }
    @Override protected int attackDuration(int attack) { return AgaresAttack.byId(attack).ticks(); }

    @Override protected int chooseAttack(ServerLevel level, LivingEntity target, double forward, double side, float off) {
        AgaresAttack choice = AgaresApi.attackFor(forward, side, target.getY() - getY(), off, level.getGameTime() >= nextScreech);
        if (choice != AgaresAttack.NONE) return choice.ordinal();
        return Math.hypot(forward, side) > AgaresApi.standOffBlocks() ? WALK : HOLD;
    }

    @Override protected void attackStarted(ServerLevel level, int attack) {
        struck.clear();
        if (attack == AgaresAttack.SCREECH.ordinal()) nextScreech = level.getGameTime() + AgaresApi.screechIntervalTicks();
    }

    @Override protected void attackTick(ServerLevel level, int id, int ticks) {
        AgaresAttack attack = AgaresAttack.byId(id);
        if (!attack.strikes(ticks)) return;
        switch (attack) {
            case SCREECH -> roar(level, AgaresApi.screechRadiusBlocks(), AgaresApi.screechCurseTicks(), AgaresApi.screechCurseAmplifier());
            case STOMP_LEFT, STOMP_RIGHT -> {
                var sole = gait().leg(attack.left());
                blow(level, attack, localPixels(sole.side(), 0, sole.forward()), null, true);
            }
            // The jaws are dragged along the ground: each tick they are somewhere new, and nothing is bitten twice.
            case BITE -> blow(level, attack, localPixels(0, 0, AgaresApi.biteForward(ticks)), struck, ticks == attack.strikeFrom() || ticks % 6 == 0);
            case TAIL_LEFT, TAIL_RIGHT -> sweep(level, attack, ticks);
            case GUST -> gust(level);
            default -> {}
        }
    }

    @Override protected void footfall(ServerLevel level, boolean left) {
        var sole = gait().leg(left);
        blow(level, AgaresAttack.NONE, localPixels(sole.side(), 0, sole.forward()), null, true);
    }

    /** A blow on the ground; NONE is an ordinary footfall. */
    private void blow(ServerLevel level, AgaresAttack attack, Vec3 centre, Set<UUID> spared, boolean loud) {
        strike(level, centre, AgaresApi.radiusBlocks(attack), distance -> AgaresApi.damage(attack, distance), spared);
        if (loud) crash(level, centre, AgaresApi.radiusBlocks(attack));
    }

    /** A place as Agares sees it: how far to its left and how far ahead, in blocks. */
    private double[] seen(Vec3 place) {
        Vec3 to = place.subtract(position());
        double sin = Mth.sin(yBodyRot * Mth.DEG_TO_RAD), cos = Mth.cos(yBodyRot * Mth.DEG_TO_RAD);
        return new double[]{to.x * cos + to.z * sin, -to.x * sin + to.z * cos};
    }

    private void fling(LivingEntity victim, float damage, double push, double lift) {
        if (!victim.hurt(damageSources().mobAttack(this), damage)) return;
        Vec3 away = victim.position().subtract(position());
        double distance = Math.max(.01, away.horizontalDistance());
        victim.push(away.x / distance * push, lift, away.z / distance * push); victim.hurtMarked = true;
    }

    private void sweep(ServerLevel level, AgaresAttack attack, int ticks) {
        double reach = AgaresApi.tailReachBlocks();
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(reach, 80, reach), this::prey)) {
            double[] at = seen(victim.position());
            if (struck.contains(victim.getUUID()) || !AgaresApi.tailHits(attack, ticks, at[1], at[0])) continue;
            struck.add(victim.getUUID());
            fling(victim, AgaresApi.flatDamage(attack), 2.2, 1.1);
        }
        if (ticks % 8 == 0) level.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 24, .6F);
    }

    /** One beat of the wings. It reaches as high as Agares stands, so flying over it is no escape. */
    private void gust(ServerLevel level) {
        double reach = AgaresApi.gustRangeBlocks();
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(reach, 0, reach).expandTowards(0, AgaresApi.heightBlocks() * 1.5, 0).expandTowards(0, -32, 0), this::prey)) {
            double[] at = seen(victim.position());
            if (AgaresApi.gustHits(at[1], at[0])) fling(victim, AgaresApi.flatDamage(AgaresAttack.GUST), AgaresApi.gustPush(), 1.4);
        }
        Vec3 chest = local(0, getEyeHeight() * .7, 100);
        level.playSound(null, chest.x, chest.y, chest.z, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 30, .4F);
        particles(level, ParticleTypes.CLOUD, chest, 200, 60, .6);
    }
}
