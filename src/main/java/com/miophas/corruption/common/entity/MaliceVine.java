package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.plant.*;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/** Minecraft implementation of the core malice-vine species. */
public final class MaliceVine extends AnchoredLivingPlant {
    public MaliceVine(EntityType<? extends MaliceVine> type, Level level) { super(type, level); }
    @Override protected LivingPlantKind plantKind() { return LivingPlantKind.MALICE_VINE; }
    public static AttributeSupplier.Builder attributes() { return attributes(LivingPlantKind.MALICE_VINE); }
    @Override protected int growthInterval() { return InfectionConfig.vineGrowthTicks(); }
    public static boolean hasRoom(LevelReader level, BlockPos anchor, int direction, int height) {
        return LivingPlantApi.hasRoom(LivingPlantKind.MALICE_VINE, height - 2, direction, new LevelLivingPlantSpace(level, anchor));
    }
    public static MaliceVine find(ServerLevel level, BlockPos anchor) {
        return AnchoredLivingPlant.find(level, anchor) instanceof MaliceVine vine ? vine : null;
    }
    public static MaliceVine sprout(ServerLevel level, BlockPos anchor) {
        return sprout(level, anchor, direction(level, anchor));
    }
    public static MaliceVine sprout(ServerLevel level, BlockPos anchor, int direction) {
        if (!level.hasChunkAt(anchor) || !level.getBlockState(anchor).is(ModBlocks.MALICE.get())) return null;
        AnchoredLivingPlant existing = AnchoredLivingPlant.find(level, anchor);
        if (existing != null) return existing instanceof MaliceVine vine ? vine : null;
        LevelLivingPlantSpace space = new LevelLivingPlantSpace(level, anchor);
        if (!LivingPlantApi.hasRoom(LivingPlantKind.MALICE_VINE, 0, direction, space)) return null;
        MaliceVine vine = ModEntities.MALICE_VINE.get().create(level);
        if (vine == null) return null;
        LivingPlantApi.prepareGrowth(LivingPlantKind.MALICE_VINE, 0, direction, space);
        vine.initializeAnchor(anchor, direction);
        vine.setYRot(level.random.nextFloat() * 360); vine.yBodyRot = vine.yHeadRot = vine.getYRot();
        return level.addFreshEntity(vine) ? vine : null;
    }
}
