package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.api.elite.CorruptionElite;
import com.miophas.corruption.common.registry.ModSounds;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * An elite that goes on the ground: it runs its quarry down and strikes it close in (its basic blow, by the game's own
 * melee goal, which gives way whenever one of its skills runs), wanders and looks about when it has none, and spits
 * whatever it spits from its mouth ({@link #mouth}).
 */
public abstract class WalkingElite<E extends WalkingElite<E>> extends CorruptionElite<E> implements SpitCaster {
    protected WalkingElite(EntityType<? extends E> type, Level level) { super(type, level); }

    /** Its mouth, in its own frame from its feet ({right, up, ahead}, in blocks): what it spits leaves it there. */
    protected abstract double[] mouth();

    /** How fast it runs at its quarry (times its speed), and how often it strikes, in ticks. Asked as it is made: constants. */
    protected abstract double chase();
    protected abstract int strikeEvery();

    /** Whether it runs at its quarry to strike it at all: one that keeps its distance does not, unless it is cornered. */
    protected boolean closes(LivingEntity target) { return true; }

    /** The sound of its basic blow. */
    protected SoundEvent strikeSound() { return ModSounds.REMNANT_ATTACK.get(); }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, chase(), true) {
            @Override public boolean canUse() { return !skills().busy() && getTarget() != null && closes(getTarget()) && super.canUse(); }
            @Override public boolean canContinueToUse() { return !skills().busy() && getTarget() != null && closes(getTarget()) && super.canContinueToUse(); }
            @Override protected int getAttackInterval() { return adjustedTickDelay(strikeEvery()); }
        });
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, .8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        huntTheUncorrupted(1);
    }

    /** Its goals move it and strike; a skill that runs holds it still, but as it says. */
    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        if (busy) getNavigation().stop();
    }

    @Override public boolean doHurtTarget(Entity target) {
        playSound(strikeSound(), 1.2F, getVoicePitch());
        return super.doHurtTarget(target);
    }

    /** Its mouth, in the world. */
    @Override public Vec3 spitFrom(int index) { return inFrame(mouth()); }
}
