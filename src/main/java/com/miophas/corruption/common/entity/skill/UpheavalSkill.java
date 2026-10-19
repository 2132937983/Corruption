package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.infection.GroundBlight;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.UpheavalRules.Form;
import com.miophas.corruption.core.demon.skill.UpheavalRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * The ground under a quarry heaved up, broken open or speared ({@link UpheavalRules}) as the mod carries it out for any
 * creature. As it begins, the ground under where the quarry stands is marked with a circle, the form it will take chosen
 * then (only spikes, where the world does not give leave to change the ground: the mobGriefing rule), and through the
 * wind-up the marked ground trembles, earth jumping off it and the earth rumbling. At its end:
 * <ul>
 *   <li>rising, a pillar of the dormant corruption heaves up on every marked column (only into air, or what grows in the
 *       open), whatever stands there lifted with it so that nothing is shut inside it, then flung up and hurt;</li>
 *   <li>sinking, the top of every marked column is thrown up and out round it as a dive throws the ground (only what
 *       {@link Debris} would tear up; a column stops at the first block that will not go), the pit's floor turns to the
 *       dormant corruption, and whatever stood there falls in, hurt;</li>
 *   <li>spikes, great spikes burst up there, hurting whatever stands there and flinging it high, the ground as it was.</li>
 * </ul>
 * Everything not of the caster's side (players in creative or spectator aside) is caught; the earth bursts with a ring
 * of force and the sound of it.
 */
public final class UpheavalSkill implements RegisteredSkill<UpheavalRules> {
    /** How far above and below the mark it looks for the top of the ground in each column. */
    private static final int ABOVE = 2, BELOW = 3;
    /** How fast the ground a pit throws out flies, out and up, in blocks a tick. */
    private static final double THROWN_OUT = .35, THROWN_UP = .55;

    private final UpheavalRules rules;
    private final int colour, accent;
    @Nullable private BlockPos mark;
    private Form form = Form.SPIKES;
    private int raised, sunk;
    private final List<LivingEntity> struck = new ArrayList<>();

    public UpheavalSkill(UpheavalRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public UpheavalRules rules() { return rules; }

    /** The form this use takes, where it marked (the top block of the ground under its quarry), and what it did. */
    public Form form() { return form; }
    @Nullable public BlockPos mark() { return mark; }
    public int raised() { return raised; }
    public int sunk() { return sunk; }
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        struck.clear();
        raised = sunk = 0;
        if (!(boss.level() instanceof ServerLevel level)) {
            mark = target.blockPosition().below();
            return;
        }
        Vec3 ground = SkillShows.ground(level, target.position());
        mark = BlockPos.containing(ground).below();
        form = rules.form(EventHooks.canEntityGrief(level, boss), boss.getRandom().nextDouble());
        SkillShows.show(level, ground, "magic_circle", rules.radius() + .6, .2, rules.timing().windup() + 6, colour, accent, 0, boss.getYRot(), false);
        SkillShows.sound(level, ground, SoundEvents.WARDEN_DIG, 2, .7F);
    }

    /** Takes this form instead of the one it chose, if asked before it strikes: for a boss that would choose for itself, or a test. */
    public void force(Form forced) { form = forced; }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (mark == null || !(boss.level() instanceof ServerLevel level)) return;
        if (target != null) boss.getLookControl().setLookAt(target, 30, 30);
        if (rules.timing().windingUp(elapsed)) {
            if (elapsed % 2 == 0) tremble(level);
            if (elapsed % 6 == 0) SkillShows.sound(level, Vec3.atCenterOf(mark), SoundEvents.ROOTED_DIRT_BREAK, 1.2F, .5F);
            return;
        }
        if (!rules.timing().strikes(elapsed)) return;
        switch (form) {
            case RISE -> rise(level, boss);
            case SINK -> sink(level, boss);
            case SPIKES -> spike(level, boss);
        }
        Vec3 at = Vec3.atCenterOf(mark.above());
        SkillShows.show(level, at, "shockwave", rules.radius() + 2.5, 1.2, 18, colour, accent, .35, boss.getYRot(), true);
        SkillShows.sound(level, at, SoundEvents.GENERIC_EXPLODE.value(), 1.4F, .7F);
    }

    @Override public void end(Mob boss, boolean broken) { mark = null; }

    /** The marked ground trembles: earth jumps off the top of every marked column. */
    private void tremble(ServerLevel level) {
        for (int[] column : rules.columns()) {
            BlockPos top = top(level, column);
            if (top == null) continue;
            BlockState ground = level.getBlockState(top);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), top.getX() + .5, top.getY() + 1.05, top.getZ() + .5, 2, .3, 0, .3, .05);
        }
    }

    /** The top block of the ground in a marked column, near the mark's height. */
    @Nullable private BlockPos top(ServerLevel level, int[] column) {
        return GroundBlight.surface(level, mark.getX() + column[0], mark.getY(), mark.getZ() + column[1], ABOVE, BELOW);
    }

    /** A pillar of the dormant corruption heaved up on every marked column; whatever stands there lifted with it, flung up and hurt. */
    private void rise(ServerLevel level, Mob boss) {
        Map<Long, Integer> tops = new HashMap<>();
        BlockState pillar = ModBlocks.DEACTIVATED_CORRUPTION.get().defaultBlockState();
        for (int[] column : rules.columns()) {
            BlockPos top = top(level, column);
            if (top == null) continue;
            int made = 0;
            for (int up = 1; up <= rules.rise(); up++) {
                BlockPos at = top.above(up);
                BlockState there = level.getBlockState(at);
                if (!level.isLoaded(at) || !(there.isAir() || there.canBeReplaced()) || !there.getFluidState().isEmpty()) break;
                level.setBlock(at, pillar, Block.UPDATE_ALL);
                made++;
            }
            raised += made;
            tops.put(columnKey(top.getX(), top.getZ()), top.getY() + made);
            if (made > 0) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, pillar), top.getX() + .5, top.getY() + made + .5, top.getZ() + .5, 8, .4, .4, .4, .1);
        }
        for (LivingEntity caught : caught(level, boss, rules.rise() + 3)) {
            Integer standsOn = tops.get(columnKey(Mth.floor(caught.getX()), Mth.floor(caught.getZ())));
            if (standsOn != null && caught.getY() < standsOn + 1) caught.setPos(caught.getX(), standsOn + 1, caught.getZ());
            hit(level, boss, caught, rules.damage(), rules.launch());
        }
    }

    /** The top of every marked column thrown up and out round it, the pit's floor turned to the dormant corruption; whatever stood there falls in, hurt. */
    private void sink(ServerLevel level, Mob boss) {
        Vec3 middle = Vec3.atCenterOf(mark);
        for (int[] column : rules.columns()) {
            BlockPos top = top(level, column);
            if (top == null) continue;
            int down = 0;
            for (; down < rules.sink(); down++) {
                BlockPos at = top.below(down);
                Vec3 out = Vec3.atCenterOf(at).subtract(middle).multiply(1, 0, 1);
                out = out.lengthSqr() < 1e-4 ? Vec3.directionFromRotation(0, level.random.nextFloat() * 360) : out.normalize();
                Vec3 thrown = out.scale(THROWN_OUT + level.random.nextDouble() * .1).add(0, THROWN_UP + level.random.nextDouble() * .15, 0);
                if (!Debris.fling(level, at, thrown)) break;
            }
            sunk += down;
            if (down > 0 && !GroundBlight.unbreakable(level, top.below(down))) GroundBlight.dormant(level, top.below(down));
        }
        for (LivingEntity caught : caught(level, boss, 3)) hit(level, boss, caught, rules.damage(), 0);
    }

    /** Great spikes burst up on the marked ground, hurting whatever stands there and flinging it high. */
    private void spike(ServerLevel level, Mob boss) {
        Vec3 at = Vec3.atBottomCenterOf(mark.above());
        SkillShows.show(level, at, "spikes", rules.radius() + .4, 3.5, 26, colour, accent, .2, boss.getYRot(), false);
        SkillShows.sound(level, at, SoundEvents.EVOKER_FANGS_ATTACK, 2, .5F);
        for (LivingEntity caught : caught(level, boss, 3)) hit(level, boss, caught, rules.spikeDamage(), rules.spikeLaunch());
    }

    /** Everything not of the caster's side standing on the marked ground (up to this high over it). */
    private List<LivingEntity> caught(ServerLevel level, Mob boss, double over) {
        Vec3 middle = Vec3.atBottomCenterOf(mark.above());
        AABB area = new AABB(middle, middle).inflate(rules.radius() + 1, 0, rules.radius() + 1).expandTowards(0, over, 0).expandTowards(0, -2, 0);
        return level.getEntitiesOfClass(LivingEntity.class, area, near -> near != boss && near.isAlive() && !boss.isAlliedTo(near)
                && !(near instanceof Player player && (player.isCreative() || player.isSpectator())) && rules.catches(near.getX() - middle.x, near.getZ() - middle.z));
    }

    private void hit(ServerLevel level, Mob boss, LivingEntity caught, double damage, double launch) {
        caught.hurt(ModDamageTypes.corruption(level, boss), (float) damage);
        if (launch > 0) {
            caught.setDeltaMovement(caught.getDeltaMovement().multiply(.3, 0, .3).add(0, launch, 0));
            caught.hurtMarked = true;
        }
        struck.add(caught);
    }

    private static long columnKey(int x, int z) { return ((long) x << 32) ^ (z & 0xFFFFFFFFL); }
    @Override public BossSkillRegistry.Type<UpheavalRules> type() { return BossSkillApi.UPHEAVAL; }
    @Override public UpheavalRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
