package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PounceSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.elite.BlighthoundRules;
import com.miophas.corruption.core.elite.BlighthoundRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The corrupted blighthound, a land elite (BlighthoundRules): a flayed hound of the corruption, faster than anything a
 * player can outrun, its bite weak but festering. Every bite lays the corruption's curse on deeper (CurseStacks): longer
 * each time, and twice as hard once it has long to run; the curse goes on biting after the hound is dead. It springs
 * through its quarry, laying the curse on three bites deep at once; it worries a quarry close by, five bites in a second
 * and a half; and as it runs it leaves a trail of the cursed gas behind it, which spares its own side.
 */
public final class CorruptedBlighthound extends WalkingElite<CorruptedBlighthound> {
    /** Its colours: the corruption's rose, a pale rose, and the violet of its blight. */
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, BLIGHT = 0x9A4CC8;
    /** Its jaws: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, .95, 1.3};

    private final PounceSkill rend = new PounceSkill(BlighthoundRules.REND, ROSE, PALE);
    private final Frenzy frenzy = new Frenzy();
    private final SkillSet<CorruptedBlighthound> skills = SkillSet.<CorruptedBlighthound>of()
            .with("rend", rend)
            .with("frenzy", frenzy);
    /** What its last spring struck that it has cursed already, so it curses it once. */
    @Nullable private LivingEntity rent;
    private int trailed;

    public CorruptedBlighthound(EntityType<? extends CorruptedBlighthound> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, BlighthoundRules.MAX_HEALTH).add(Attributes.ARMOR, BlighthoundRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, BlighthoundRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, BlighthoundRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, BlighthoundRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, BlighthoundRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override protected CorruptedBlighthound elite() { return this; }
    @Override public SkillSet<CorruptedBlighthound> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return BlighthoundRules.CHASE; }
    @Override protected int strikeEvery() { return BlighthoundRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : BlighthoundRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = BlighthoundRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /** Its bite lays the curse on deeper, if it bites. */
    @Override public boolean doHurtTarget(Entity target) {
        boolean bit = super.doHurtTarget(target);
        if (bit && target instanceof LivingEntity living) CurseStacks.deepen(living, this, BlighthoundRules.CURSE);
        return bit;
    }

    /** Its spring curses what it strikes, three bites deep; as it runs it trails the cursed gas; its frenzy chases on as it bites. */
    @Override protected void act(@Nullable LivingEntity target, boolean busy) {
        if (!(busy && skill() == Skill.FRENZY.ordinal())) super.act(target, busy);
        LivingEntity struck = skill() == Skill.REND.ordinal() ? rend.struck() : null;
        if (struck == null) rent = null;
        else if (struck != rent) {
            rent = struck;
            CurseStacks.deepen(struck, this, BlighthoundRules.CURSE, BlighthoundRules.REND_BITES);
        }
        trail();
    }

    /** Running fast on the ground, it leaves a small cloud of the cursed gas behind it every so often. */
    void trail() {
        if (!(level() instanceof ServerLevel level) || !onGround() || getDeltaMovement().horizontalDistance() < BlighthoundRules.TRAIL_SPEED) return;
        if (++trailed < BlighthoundRules.TRAIL_EVERY) return;
        trailed = 0;
        EliteClouds.release(level, this, position(), BlighthoundRules.TRAIL);
    }

    /** Its frenzy: it chases its quarry close and bites it again and again, each bite laying the curse on deeper. */
    private final class Frenzy implements BossSkill<CorruptedBlighthound> {
        @Override public SkillTiming timing() { return BlighthoundRules.FRENZY; }
        @Override public boolean usable(CorruptedBlighthound hound, LivingEntity target) {
            return hound.distanceTo(target) <= BlighthoundRules.FRENZY_REACH + .6 && hound.hasLineOfSight(target);
        }

        @Override public void begin(CorruptedBlighthound hound, LivingEntity target) { SkillShows.sound(hound, SoundEvents.WOLF_GROWL, 1.5F, .6F); }

        @Override public void tick(CorruptedBlighthound hound, @Nullable LivingEntity target, int elapsed) {
            if (target == null || !target.isAlive()) return;
            float yaw = (float) (Mth.atan2(target.getZ() - hound.getZ(), target.getX() - hound.getX()) * Mth.RAD_TO_DEG) - 90;
            hound.setYRot(yaw);
            hound.yBodyRot = yaw;
            hound.yHeadRot = yaw;
            if (hound.distanceTo(target) > BlighthoundRules.FRENZY_REACH * .7) hound.getNavigation().moveTo(target, BlighthoundRules.CHASE);
            else hound.getNavigation().stop();
            if (!BlighthoundRules.frenzyBites(elapsed) || hound.distanceTo(target) > BlighthoundRules.FRENZY_REACH
                    || target instanceof Player player && (player.isCreative() || player.isSpectator())) return;
            hound.level().broadcastEntityEvent(hound, CorruptionElite.BITE_EVENT);
            hound.playSound(SoundEvents.WOLF_GROWL, .8F, 1.4F);
            target.invulnerableTime = 0;
            if (target.hurt(hound.damageSources().mobAttack(hound), (float) BlighthoundRules.FRENZY_DAMAGE)) CurseStacks.deepen(target, hound, BlighthoundRules.CURSE);
        }
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.WOLF_GROWL; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WOLF_GROWL; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.WOLF_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WOLF_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.WOLF_STEP, .3F, .8F); }
    @Override public float getVoicePitch() { return .6F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public PounceSkill rend() { return rend; }
}
