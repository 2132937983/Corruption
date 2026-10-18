package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.entity.skill.BlightSkill;
import com.miophas.corruption.common.entity.skill.SnareSkill;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.infection.GroundBlight;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.SowerRules;
import com.miophas.corruption.core.elite.SowerRules.Skill;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

/**
 * The corrupted sower, a land elite (SowerRules): a walking mound of the corruption's flesh on legs like roots, a heart
 * throbbing in its breast and pods of the corruption on its back. The ground under its feet is taken by the infection as
 * it walks, as though the corruption had spread there (the blocks it stands on, a few a tick, where the world gives leave:
 * the mobGriefing rule). That ground is dormant unless the configuration says otherwise: an ordinary corrupted variant that
 * never spreads on from there. And it mends on the corruption's own ground. It roots itself
 * and sows the ground all about it, throwing back whatever is near; every block that sowing takes keeps its own corrupted
 * variant and is left dormant, spreading no further (as the ground under its feet). And it sends roots up out of the ground
 * under a quarry further off, holding it fast, the longer on the corruption's own ground.
 */
public final class CorruptedSower extends WalkingElite<CorruptedSower> {
    /** Its colours: the corruption's rose, and a pale rose. */
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4;
    /** The flower of a mouth on its front: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, .85, .6};

    private final BlightSkill seed = new BlightSkill(SowerRules.SEED, ROSE, PALE);
    private final SnareSkill snare = new SnareSkill(SowerRules.SNARE, ROSE, PALE);
    private final SkillSet<CorruptedSower> skills = SkillSet.<CorruptedSower>of()
            .with("seed", seed)
            .with("snare", snare);
    /** The block it last stood on, and how many its feet have taken all told. */
    @Nullable private BlockPos stood;
    private int sown;

    public CorruptedSower(EntityType<? extends CorruptedSower> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, SowerRules.MAX_HEALTH).add(Attributes.ARMOR, SowerRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, SowerRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, SowerRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, SowerRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, SowerRules.KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override protected CorruptedSower elite() { return this; }
    @Override public SkillSet<CorruptedSower> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return SowerRules.CHASE; }
    @Override protected int strikeEvery() { return SowerRules.BITE_EVERY; }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : SowerRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = SowerRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level && isAlive()) {
            sow(level);
            if (tickCount % SowerRules.REGEN_EVERY == 0) mend(level);
        }
    }

    /**
     * The ground under its feet taken by the infection: every block under its box, once each time it comes onto a block,
     * a few a tick at the most, where the world gives leave. How many it took this time.
     */
    int sow(ServerLevel level) {
        if (!onGround()) return 0;
        BlockPos on = getOnPos();
        if (on.equals(stood)) return 0;
        stood = on;
        if (!EventHooks.canEntityGrief(level, this)) return 0;
        Set<BlockPos> under = new LinkedHashSet<>();
        under.add(on);
        double half = getBbWidth() / 2 - .1, below = getY() - .2;
        for (double east : new double[]{-half, half})
            for (double south : new double[]{-half, half}) under.add(BlockPos.containing(getX() + east, below, getZ() + south));
        int took = 0;
        boolean dormant = InfectionConfig.sowerGroundDormant();
        for (BlockPos at : under) {
            if (took >= SowerRules.SOW_MOST) break;
            if (GroundBlight.unbreakable(level, at) || !GroundBlight.infectable(level, at)
                    || !GroundBlight.infect(level, at, dormant)) continue;
            took++;
            level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), at.getX() + .5, at.getY() + 1.05, at.getZ() + .5, 3, .3, .05, .3, .01);
        }
        sown += took;
        return took;
    }

    /** On the corruption's own ground it mends. Whether it did. */
    boolean mend(ServerLevel level) {
        if (getHealth() >= getMaxHealth() || !CorruptionConversions.isCorrupted(level.getBlockState(getOnPos()))) return false;
        heal(SowerRules.REGEN);
        level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), getX(), getY() + getBbHeight() * .6, getZ(), 2, .4, .3, .4, .01);
        return true;
    }

    /** How many blocks its feet have taken, all told. */
    public int sown() { return sown; }

    @Override protected SoundEvent strikeSound() { return SoundEvents.SLIME_ATTACK; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SNIFFER_IDLE; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SNIFFER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SNIFFER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.ROOTED_DIRT_STEP, .7F, .6F); }
    @Override public float getVoicePitch() { return .55F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public BlightSkill seed() { return seed; }
    public SnareSkill snare() { return snare; }
}
