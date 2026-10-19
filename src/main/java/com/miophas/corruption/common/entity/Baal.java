package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.api.demon.BossDeath;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** The first colossus: it walks its prey down, stomps what is under it, slams what is in front and roars a curse. */
public final class Baal extends Colossus {
    /**
     * Its death, in fire, eight times the size it is written at: a ring of fire racing out as it falls, the ground
     * splitting and burning under it, a storm of fire breaking over it, a second ring as it drops to its knees, and
     * its heart bursting as it sinks; a pillar of light stands where it went down, and souls rise out of the ground.
     */
    public static final BossDeath DEATH = new BossDeath(BaalApi.deathTicks(), 70, 8, new BossDeath.Rays(20, 18, 0xFF4A10, 0xFFE08A), .25F, List.of(
            BossDeath.beat(0, BossDeath.Anchor.FEET, "shockwave", 18, 3, 40, 0xFF6A1A, 0xFFE08A, .6, "minecraft:entity.ender_dragon.death", 48, .55F),
            BossDeath.beat(10, BossDeath.Anchor.FEET, "fissure", 16, 3, 140, 0xFF4A10, 0xFFD27A, 0, "minecraft:item.mace.smash_ground_heavy", 40, .5F),
            BossDeath.beat(30, BossDeath.Anchor.FEET, "hellfire", 10, 4, 110, 0xFF4A10, 0xFFD27A, .2, "minecraft:entity.blaze.shoot", 40, .5F),
            BossDeath.beat(55, BossDeath.Anchor.FEET, "lightning", 14, 26, 90, 0xFF8A3A, 0xFFF4D0, .3, "minecraft:entity.lightning_bolt.thunder", 48, .6F),
            BossDeath.beat(96, BossDeath.Anchor.FEET, "shockwave", 24, 4, 40, 0xFF6A1A, 0xFFE08A, .5, "minecraft:entity.generic.explode", 40, .45F),
            BossDeath.beat(156, BossDeath.Anchor.HEART, "nova", 22, 0, 34, 0xFF6A1A, 0xFFFFFF, 1, "minecraft:entity.generic.explode", 48, .35F),
            BossDeath.beat(156, BossDeath.Anchor.FEET, "light_pillar", 3, 40, 70, 0xFFB040, 0xFFFFFF, 0, "minecraft:block.beacon.deactivate", 40, .5F),
            BossDeath.beat(158, BossDeath.Anchor.FEET, "shockwave", 34, 5, 55, 0xFF6A1A, 0xFFE08A, .7, "minecraft:entity.lightning_bolt.thunder", 48, .5F),
            BossDeath.beat(160, BossDeath.Anchor.FEET, "souls", 6, 30, 70, 0xFF6A1A, 0xFFE08A, 0, "minecraft:particle.soul_escape", 40, .6F)));

    private long nextRoar;

    public Baal(EntityType<? extends Baal> type, Level level) { super(type, level, BaalApi.parts(), BaalApi.blocks(1)); }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, BaalApi.maxHealth()).add(Attributes.ARMOR, BaalApi.armor())
                .add(Attributes.ARMOR_TOUGHNESS, BaalApi.armorToughness()).add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.FOLLOW_RANGE, BaalApi.sightBlocks());
    }

    public BaalAttack attack() { return BaalAttack.byId(attackId()); }

    @Override protected ColossusLegs legs() { return BaalApi.legs(); }
    @Override protected ColossusGait gait(float cycle, float walk, int attack, float attackTicks, float collapse) { return BaalApi.gait(cycle, walk, BaalAttack.byId(attack), attackTicks, collapse); }
    @Override protected int deathTicks() { return BaalApi.deathTicks(); }
    @Override protected double heightBlocks() { return BaalApi.heightBlocks(); }
    @Override public BossDeath death() { return DEATH; }
    @Override protected double sightBlocks() { return BaalApi.sightBlocks(); }
    @Override protected double walkBlocksPerTick() { return BaalApi.walkBlocksPerTick(); }
    @Override protected float turnDegreesPerTick() { return BaalApi.turnDegreesPerTick(); }
    @Override protected float walkFacingDegrees() { return BaalApi.walkFacingDegrees(); }
    @Override protected int attackCooldownTicks() { return BaalApi.attackCooldownTicks(); }
    @Override protected float damageTaken(boolean weakPart) { return BaalApi.damageTaken(weakPart); }
    @Override protected int attackDuration(int attack) { return BaalAttack.byId(attack).ticks(); }

    @Override protected int chooseAttack(ServerLevel level, LivingEntity target, double forward, double side, float off) {
        if (level.getGameTime() >= nextRoar && Math.hypot(forward, side) <= BaalApi.roarRadiusBlocks()) return BaalAttack.ROAR.ordinal();
        BaalAttack choice = BaalApi.attackFor(forward, side);
        if (choice == BaalAttack.NONE) return WALK;
        // A stomp is for what is already under it, so it does not wait to face that.
        return choice.stomp() || off <= BaalApi.strikeFacingDegrees() ? choice.ordinal() : HOLD;
    }

    @Override protected void attackStarted(ServerLevel level, int attack) {
        if (attack == BaalAttack.ROAR.ordinal()) nextRoar = level.getGameTime() + BaalApi.roarIntervalTicks();
    }

    @Override protected void attackTick(ServerLevel level, int id, int ticks) {
        BaalAttack attack = BaalAttack.byId(id);
        if (ticks != attack.impactTick()) return;
        if (attack == BaalAttack.ROAR) { roar(level, BaalApi.roarRadiusBlocks(), BaalApi.roarCurseTicks(), BaalApi.roarCurseAmplifier()); return; }
        float[] at = BaalApi.impact(attack, gait());
        blow(level, attack, localPixels(at[0], 0, at[1]));
    }

    @Override protected void footfall(ServerLevel level, boolean left) {
        var sole = gait().leg(left);
        blow(level, BaalAttack.NONE, localPixels(sole.side(), 0, sole.forward()));
    }

    /** A blow on the ground; NONE is an ordinary footfall. */
    private void blow(ServerLevel level, BaalAttack attack, Vec3 centre) {
        strike(level, centre, BaalApi.radiusBlocks(attack), distance -> BaalApi.damage(attack, distance), null);
        crash(level, centre, BaalApi.radiusBlocks(attack));
    }
}
