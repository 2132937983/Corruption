package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLegs;
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
