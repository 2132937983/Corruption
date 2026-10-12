package com.miophas.corruption.common.block;

import com.miophas.corruption.api.corruption.CorruptionCurseApi;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/** Liquid that lays the corruption curse on any creature that enters it. Unlike corrupted water and lava it does not spread infection. */
public final class FlowingCorruptionBlock extends LiquidBlock {
    public FlowingCorruptionBlock(FlowingFluid fluid, Properties properties) { super(fluid, properties); }

    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        // A flowing cell is mostly empty, so only the entity's own contact with the fluid counts.
        // Armor stands and the like are living entities too, but nothing a potion could affect.
        if (level.isClientSide || !(entity instanceof LivingEntity living) || !living.isAlive() || !living.isAffectedByPotions()
                || !living.isInFluidType(state.getFluidState().getFluidType())) return;
        boolean exempt = living instanceof Player player && (player.isCreative() || player.isSpectator());
        if (!CorruptionCurseApi.fluidCurses(living instanceof Player, exempt, CorruptionFactionApi.isRemnant(living))) return;
        var rules = CorruptionCurseApi.flowingCorruption();
        MobEffectInstance current = living.getEffect(ModEffects.CORRUPTION_CURSE);
        if (current != null && CorruptionCurseApi.fluidCurseHolds(current.getDuration(), current.getAmplifier(), !current.getCures().isEmpty())) return;
        MobEffectInstance curse = new MobEffectInstance(ModEffects.CORRUPTION_CURSE, rules.curseTicks(), rules.curseAmplifier());
        if (!rules.curable()) curse.getCures().clear();
        // Merging into an existing curse would keep that curse's cures, so a weaker one is replaced outright.
        if (current != null && current.getAmplifier() <= rules.curseAmplifier()) living.removeEffect(ModEffects.CORRUPTION_CURSE);
        living.addEffect(curse);
    }
}
