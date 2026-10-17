package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.entity.skill.PounceSkill;
import com.miophas.corruption.common.entity.skill.SpitSkill;
import com.miophas.corruption.core.elite.CrawlerRules;
import com.miophas.corruption.core.elite.CrawlerRules.Skill;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The corrupted crawler, a land elite (CrawlerRules): a long body of plated segments on many legs, mandibles and a cluster
 * of eyes at its head, a stinger at its tail. It climbs walls as a spider does; bites what it reaches; springs at its
 * quarry from a little off; and spits venom that leaves a small cursed pool.
 */
public final class CorruptedCrawler extends WalkingElite<CorruptedCrawler> {
    public static final int ROSE = 0xE0609A, PALE = 0xFFD0E4, VENOM = 0xB040C8;
    /** Its mandibles: in its own frame ({right, up, ahead}). */
    public static final double[] MOUTH = {0, .25, 1.5};

    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(CorruptedCrawler.class, EntityDataSerializers.BOOLEAN);

    private final PounceSkill pounce = new PounceSkill(CrawlerRules.POUNCE, ROSE, PALE);
    private final SpitSkill venom = new SpitSkill(CrawlerRules.VENOM, VENOM, PALE);
    private final SkillSet<CorruptedCrawler> skills = SkillSet.<CorruptedCrawler>of()
            .with("pounce", pounce)
            .with("venom", venom);

    public CorruptedCrawler(EntityType<? extends CorruptedCrawler> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, CrawlerRules.MAX_HEALTH).add(Attributes.ARMOR, CrawlerRules.ARMOR)
                .add(Attributes.ATTACK_DAMAGE, CrawlerRules.ATTACK_DAMAGE).add(Attributes.MOVEMENT_SPEED, CrawlerRules.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, CrawlerRules.FOLLOW_RANGE).add(Attributes.KNOCKBACK_RESISTANCE, CrawlerRules.KNOCKBACK_RESISTANCE);
    }

    @Override protected CorruptedCrawler elite() { return this; }
    @Override public SkillSet<CorruptedCrawler> skills() { return skills; }
    @Override protected double[] mouth() { return MOUTH; }
    @Override protected double chase() { return CrawlerRules.CHASE; }
    @Override protected int strikeEvery() { return CrawlerRules.BITE_EVERY; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, false);
    }

    /** It finds its way up walls, as a spider does. */
    @Override protected PathNavigation createNavigation(Level level) { return new WallClimberNavigation(this, level); }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide) entityData.set(CLIMBING, horizontalCollision);
    }

    /** Against a wall it climbs it. */
    @Override public boolean onClimbable() { return climbing(); }
    public boolean climbing() { return entityData.get(CLIMBING); }

    @Override protected void choose(LivingEntity target) {
        Set<Skill> ready = EnumSet.noneOf(Skill.class);
        for (Skill skill : CrawlerRules.SKILLS) if (skills.usable(skill.ordinal() - 1, this, target)) ready.add(skill);
        Skill next = CrawlerRules.choose(distanceTo(target), ready, random.nextDouble());
        if (next != Skill.NONE) begin(next.ordinal(), target);
    }

    @Override protected SoundEvent strikeSound() { return SoundEvents.SPIDER_HURT; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SILVERFISH_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SPIDER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SPIDER_STEP, .4F, 1.3F); }
    @Override public float getVoicePitch() { return .6F + (random.nextFloat() - random.nextFloat()) * .05F; }

    public PounceSkill pounce() { return pounce; }
    public SpitSkill venom() { return venom; }
}
