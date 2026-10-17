package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PounceSkill;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.elite.MawRules;
import com.miophas.corruption.core.elite.MawRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The corrupted maw, a sea elite (MawRules): a great eel whose whole front is a round mouth ringed with teeth, a glowing
 * lure dangling over it. It hunts as every sea elite does; bites what it reaches; lunges at its quarry, its mouth agape;
 * spits great bolts of water; and, in the water, opens its mouth wide and sucks in the water about it, drawing everything
 * near towards its teeth.
 */
public final class CorruptedMaw extends SwimmingElite<CorruptedMaw> {
    public static final int WATER = 0x8A5AE0, FOAM = 0xE0D0FF, ROSE = 0xE0609A;
    /** Its round mouth, the whole front of it: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, .7, 1.4};

    private final PounceSkill lunge = new PounceSkill(MawRules.LUNGE, ROSE, FOAM);
    private final SpitSkill spout = new SpitSkill(MawRules.SPOUT, WATER, FOAM);
    private final Whirlpool whirlpool = new Whirlpool();
    private final SkillSet<CorruptedMaw> skills = SkillSet.<CorruptedMaw>of()
            .with("whirlpool", whirlpool)
            .with("lunge", lunge)
            .with("spout", spout);

    public CorruptedMaw(EntityType<? extends CorruptedMaw> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, MawRules.MAX_HEALTH).add(Attributes.ARMOR, MawRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, MawRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, MawRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, MawRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, MawRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedMaw elite() { return this; }
    @Override public SkillSet<CorruptedMaw> skills() { return skills; }
    @Override protected float wander() { return MawRules.WANDER; }
    @Override protected float crawl() { return MawRules.CRAWL; }
    @Override protected double dart() { return MawRules.DART; }
    @Override protected double thrust() { return MawRules.THRUST; }
    @Override protected double strikeReach() { return MawRules.BITE_REACH; }
    @Override protected int strikeEvery() { return MawRules.BITE_EVERY; }
    @Override protected double[] mouth() { return MOUTH; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : MawRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = MawRules.choose(distanceTo(target), isInWater(), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    /**
     * Its whirlpool, in the water: gathering with its mouth wide through the wind-up, then sucking in the water about it,
     * everything not of its side within reach drawn towards its mouth, the harder the nearer, until the skill is over.
     */
    final class Whirlpool implements BossSkill<CorruptedMaw> {
        private int drawn;

        /** How many it drew in, on the last tick it drew. */
        int drawn() { return drawn; }

        @Override public SkillTiming timing() { return MawRules.WHIRLPOOL; }
        @Override public boolean usable(CorruptedMaw maw, LivingEntity target) {
            return maw.isInWater() && maw.distanceTo(target) <= MawRules.WHIRL_RADIUS;
        }

        @Override public void begin(CorruptedMaw maw, LivingEntity target) {
            drawn = 0;
            SkillShows.sound(maw, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 3, .5F);
        }

        @Override public void tick(CorruptedMaw maw, @Nullable LivingEntity target, int elapsed) {
            maw.setDeltaMovement(maw.getDeltaMovement().scale(.5));
            if (target != null) maw.turn(target.position().subtract(maw.position()));
            if (MawRules.WHIRLPOOL.windingUp(elapsed) || !(maw.level() instanceof ServerLevel level)) return;
            Vec3 mouth = maw.inFrame(MOUTH);
            drawn = 0;
            for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, maw.getBoundingBox().inflate(MawRules.WHIRL_RADIUS), near -> near != maw
                    && near.isAlive() && !maw.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator())))) {
                Vec3 to = mouth.subtract(near.getBoundingBox().getCenter());
                double pull = MawRules.pull(to.length());
                if (pull <= 0 || to.lengthSqr() < 1e-4) continue;
                Vec3 round = new Vec3(-to.z, 0, to.x).normalize().scale(pull * .5);
                near.setDeltaMovement(near.getDeltaMovement().scale(.6).add(to.normalize().scale(pull)).add(round));
                near.hurtMarked = true;
                drawn++;
            }
            if (elapsed % 2 == 0) {
                double turn = elapsed * .5;
                for (int arm = 0; arm < 3; arm++) {
                    double angle = turn + arm * Math.PI * 2 / 3, out = 2 + (elapsed % 10) * .6;
                    level.sendParticles(ParticleTypes.BUBBLE, mouth.x + Math.cos(angle) * out, mouth.y, mouth.z + Math.sin(angle) * out, 3, .2, .2, .2, .02);
                }
            }
        }
    }

    @Override public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.2F, .5F);
        return super.doHurtTarget(target);
    }

    @Override protected SoundEvent getAmbientSound() { return isInWater() ? SoundEvents.ELDER_GUARDIAN_AMBIENT : SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return isInWater() ? SoundEvents.ELDER_GUARDIAN_HURT : SoundEvents.ELDER_GUARDIAN_HURT_LAND; }
    @Override protected SoundEvent getDeathSound() { return isInWater() ? SoundEvents.ELDER_GUARDIAN_DEATH : SoundEvents.ELDER_GUARDIAN_DEATH_LAND; }
    @Override protected SoundEvent getSwimSound() { return SoundEvents.DOLPHIN_SWIM; }
    @Override public float getVoicePitch() { return .45F + (random.nextFloat() - random.nextFloat()) * .05F; }
    @Override public int getAmbientSoundInterval() { return 200; }

    public PounceSkill lunge() { return lunge; }
    public SpitSkill spout() { return spout; }
    Whirlpool whirlpool() { return whirlpool; }
}
