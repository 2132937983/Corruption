package com.miophas.corruption.common.block;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.infection.InfectionEvents;
import com.miophas.corruption.common.particle.CorruptionFootsteps;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CorruptionBlock extends Block {
    public static final float CONTACT_DAMAGE_FRACTION = 0.15F;
    public static final MapCodec<CorruptionBlock> CODEC = simpleCodec(CorruptionBlock::new);

    public CorruptionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    /**
     * Whether this corruption spreads: whether it is a source of the infection, which takes the blocks next to it in
     * turn. Every corrupted block does, but the dormant corruption ({@link DeactivatedCorruptionBlock}).
     */
    public boolean spreads() { return true; }
    @Override public int getFlammability(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction face) {
        return state.is(com.miophas.corruption.common.registry.ModTags.FLAMMABLE_CORRUPTION) ? 100 : 0;
    }
    @Override public int getFireSpreadSpeed(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction face) {
        return state.is(com.miophas.corruption.common.registry.ModTags.FLAMMABLE_CORRUPTION) ? 60 : 0;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity.onGround()) applyContactDamage(level, entity);
        super.stepOn(level, pos, state, entity);
        CorruptionFootsteps.step(level, pos, entity);
    }

    /** The corruption hurts what touches it, but never its own (corruption:corrupted_remnants): its remnants, its plants, its bosses. */
    static void applyContactDamage(Level level, Entity entity) {
        if (level instanceof ServerLevel && entity instanceof LivingEntity living
                && living.isAlive() && !living.isSpectator() && !CorruptionFactionApi.isRemnant(living)) {
            living.hurt(ModDamageTypes.corruption(level), living.getMaxHealth() * CONTACT_DAMAGE_FRACTION);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!level.isClientSide || random.nextInt(80) != 0) return;
        BlockPos above = pos.above();
        if (!level.getFluidState(above).isEmpty()
                || level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) return;
        var shape = state.getCollisionShape(level, pos);
        if (shape.isEmpty()) return;
        // Only check this small area after the random roll succeeds; no server scans or extra tickers.
        if (!level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).expandTowards(0, 2, 0),
                living -> living.isAlive() && !living.isSpectator() && living.onGround()
                        && living.getOnPosLegacy().equals(pos) && CorruptionFootsteps.isMoving(living)).isEmpty()) return;
        level.addParticle(ModParticles.CORRUPTION_SCREAM.get(),
                pos.getX() + 0.15 + random.nextDouble() * 0.7,
                pos.getY() + shape.max(Direction.Axis.Y) + 0.05,
                pos.getZ() + 0.15 + random.nextDouble() * 0.7,
                (random.nextDouble() - 0.5) * 0.008, 0.025, (random.nextDouble() - 0.5) * 0.008);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (spreads() && level instanceof ServerLevel serverLevel && !oldState.is(state.getBlock())) {
            InfectionEvents.activate(serverLevel, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, level, pos, newState, moved);
        if (spreads() && level instanceof ServerLevel serverLevel && !newState.is(state.getBlock())) {
            InfectionEvents.deactivate(serverLevel, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
            Block neighbor, BlockPos neighborPos, boolean moved) {
        super.neighborChanged(state, level, pos, neighbor, neighborPos, moved);
        if (spreads() && level instanceof ServerLevel serverLevel) {
            InfectionEvents.neighborChanged(serverLevel, pos, neighborPos);
        }
    }
}
