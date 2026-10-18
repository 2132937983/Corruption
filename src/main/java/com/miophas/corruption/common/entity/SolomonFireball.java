package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.SolomonRules;
import com.miophas.corruption.core.demon.skill.FireballRules;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * One of Baal's fireballs as a boss looses it (FireballRules): a great fire charge flying straight at a steady speed, that
 * bursts on whatever it meets. Everything about the burst that is not of its loosers' side is hurt (with the corruption's hurt,
 * or Solomon's, his own hurt that no robe turns aside) the more the nearer it stood, and set alight; where the world gives
 * leave, the blocks nearest the burst that are softer than obsidian are broken and fire is set about it, as the spell of Baal
 * does. Nothing can strike it back.
 */
public final class SolomonFireball extends Fireball {
    private FireballRules rules = SolomonRules.FIREBALLS;
    private int colour = 0xFF6A1A, accent = SolomonRules.GOLD;
    private int life;

    public SolomonFireball(EntityType<? extends SolomonFireball> type, Level level) {
        super(type, level);
        accelerationPower = 0;
    }

    /** Looses one from here at this velocity, its burst as the rules say. */
    public static SolomonFireball loose(ServerLevel level, LivingEntity owner, Vec3 from, Vec3 velocity, FireballRules rules, int colour, int accent) {
        SolomonFireball ball = new SolomonFireball(ModEntities.SOLOMON_FIREBALL.get(), level);
        ball.setOwner(owner);
        ball.rules = rules;
        ball.colour = colour;
        ball.accent = accent;
        ball.moveTo(from.x, from.y - ball.getBbHeight() / 2, from.z, owner.getYRot(), owner.getXRot());
        ball.setDeltaMovement(velocity);
        level.addFreshEntity(ball);
        return ball;
    }

    /** It flies straight on at its speed, burning, until it meets something or has flown a long while. */
    @Override public void tick() {
        super.tick();
        if (!level().isClientSide && ++life > 200) discard();
    }
    @Override protected float getInertia() { return 1; }
    @Override protected float getLiquidInertia() { return 1; }
    @Override protected ParticleOptions getTrailParticle() { return ParticleTypes.FLAME; }

    /** Nothing strikes it back. */
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public boolean isPickable() { return false; }

    /** It passes its loosers' own side. */
    @Override protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !(getOwner() != null && (getOwner().isAlliedTo(entity) || entity.isAlliedTo(getOwner())));
    }

    @Override protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (level() instanceof ServerLevel level) {
            burst(level, hit.getLocation());
            discard();
        }
    }

    /** It bursts here: hurting and burning what is about, breaking the ground and setting fire where the world gives leave. */
    void burst(ServerLevel level, Vec3 at) {
        Entity owner = getOwner();
        double radius = rules.radius();
        DamageSource hurt = owner instanceof LivingEntity looser ? ModDamageTypes.corruption(level, looser) : ModDamageTypes.solomon(level, this);
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(radius), near -> near.isAlive() && near != owner
                && !(owner != null && (owner.isAlliedTo(near) || near.isAlliedTo(owner))) && !(near instanceof Player player && (player.isCreative() || player.isSpectator())))) {
            double damage = rules.at(near.getBoundingBox().getCenter().distanceTo(at));
            if (damage <= 0) continue;
            near.hurt(hurt, (float) damage);
            near.igniteForSeconds(rules.burnSeconds());
        }
        SkillShows.show(level, at, "hellfire", radius, 2, 24, colour, accent, .4, getYRot(), false);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y, at.z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.FLAME, at.x, at.y, at.z, 40, radius / 2, radius / 3, radius / 2, .06);
        SkillShows.sound(level, at, SoundEvents.GENERIC_EXPLODE.value(), 4, .7F);
        if (owner == null || !EventHooks.canEntityGrief(level, owner)) return;
        crush(level, at);
        kindle(level, at);
    }

    /** Breaks the blocks nearest the burst that are softer than the rules' hardest, as many as they let. */
    private void crush(ServerLevel level, Vec3 at) {
        double reach = rules.crushRadius();
        int span = (int) Math.ceil(reach);
        BlockPos middle = BlockPos.containing(at);
        List<BlockPos> places = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-span, -span, -span), middle.offset(span, span, span)))
            if (Vec3.atCenterOf(pos).distanceToSqr(at) <= reach * reach) places.add(pos.immutable());
        places.sort(Comparator.comparingDouble(pos -> Vec3.atCenterOf(pos).distanceToSqr(at)));
        int broken = 0;
        for (BlockPos pos : places) {
            if (broken >= rules.crushMost()) break;
            BlockState state = level.getBlockState(pos);
            float hardness = state.getDestroySpeed(level, pos);
            if (state.isAir() || hardness < 0 || hardness >= rules.hardest() || !state.getFluidState().isEmpty() || level.getBlockEntity(pos) != null) continue;
            if (level.destroyBlock(pos, false, getOwner())) broken++;
        }
    }

    /** Sets fire here and there on the ground about the burst. */
    private void kindle(ServerLevel level, Vec3 at) {
        int span = (int) Math.ceil(rules.radius());
        for (int tried = 0; tried < 12; tried++) {
            BlockPos pos = BlockPos.containing(at.add(random.nextInt(span * 2 + 1) - span, random.nextInt(3) - 1, random.nextInt(span * 2 + 1) - span));
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolidRender(level, pos.below()))
                level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
        }
    }
}
