package com.miophas.corruption.common.extension;

import com.miophas.corruption.api.altar.AltarApi;
import com.miophas.corruption.api.corruption.PlayerCorruptionChanges;
import com.miophas.corruption.api.corruption.RemnantSpawns;
import com.miophas.corruption.api.demon.DemonServices;
import com.miophas.corruption.api.elite.EliteServices;
import com.miophas.corruption.api.plant.PlantServices;
import com.miophas.corruption.common.altar.AltarOfferingBlockEntity;
import com.miophas.corruption.common.altar.AltarStructure;
import com.miophas.corruption.common.arcane.PreyMarks;
import com.miophas.corruption.common.block.CorruptionPlantBlock;
import com.miophas.corruption.common.block.EerieBlock;
import com.miophas.corruption.common.block.EldritchVineBlock;
import com.miophas.corruption.common.config.DemonConfig;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.infection.InfectionEvents;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarLayout;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/** Native game services behind the public extension contracts. */
public final class ExtensionServices {
    public static void install() {
        PlayerCorruptionChanges.install(PlayerCorruption::raise);
        EliteServices.install((level, entity) ->
                level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(),
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 30,
                        entity.getBbWidth() / 2, entity.getBbHeight() / 2, entity.getBbWidth() / 2, .05));
        DemonServices.install(DemonConfig::arcaneOnly,
                PreyMarks::feuding);
        SkillShows.install();
        InfectionEvents.install();
        RemnantSpawns.install(new RemnantSpawns.Provider() {
            public Optional<Mob> raise(ServerLevel level,
                    EntityType<?> source, Vec3 at, float yaw) {
                return Optional.ofNullable(CorruptedRemnant.raised(level, source, at, yaw));
            }
            public Optional<Mob> copy(LivingEntity source) {
                return CorruptedRemnant.copyOf(source);
            }
        });
        PlantServices.install(InfectionConfig::blindnessRadius, InfectionConfig::attractionRadius,
                state -> state.getBlock() instanceof CorruptionPlantBlock
                        || state.getBlock() instanceof EerieBlock
                        || state.getBlock() instanceof EldritchVineBlock,
                (level, area, anchor, expires) -> {
                    for (var remnant : level.getEntitiesOfClass(CorruptedRemnant.class, area, Entity::isAlive))
                        remnant.attractTo(anchor, expires);
                });

        AltarApi.install(new AltarApi.Provider() {
            public Optional<AltarApi.View> find(ServerLevel level, BlockPos pos) {
                if (!level.hasChunkAt(pos) || !(level.getBlockEntity(pos) instanceof AltarOfferingBlockEntity altar)) return Optional.empty();
                return Optional.of(new AltarApi.View(altar.state(), altar.form(), altar.facing(), altar.elapsed(), altar.recipeId(), Optional.ofNullable(altar.bound())));
            }
            public List<ResourceLocation> structures(ServerLevel level) {
                return level.registryAccess().registryOrThrow(AltarStructure.REGISTRY).keySet().stream().sorted().toList();
            }
            public boolean build(ServerLevel level, BlockPos pos, ResourceLocation id,
                    AltarFacing facing, boolean withAltar) {
                var structure = level.registryAccess().registryOrThrow(AltarStructure.REGISTRY).get(id);
                if (structure == null) return false;
                for (var cell : structure.cells()) {
                    var at = AltarStructure.place(pos, facing, cell.east(), cell.up(), cell.north());
                    if (level.isOutsideBuildHeight(at) || !level.hasChunkAt(at)) return false;
                }
                if (withAltar) for (int x = -AltarLayout.REACH; x <= AltarLayout.REACH; x++)
                    for (int z = -AltarLayout.REACH; z <= AltarLayout.REACH; z++) {
                        var at = pos.offset(x, 0, z);
                        if (level.isOutsideBuildHeight(at) || !level.hasChunkAt(at)) return false;
                    }
                if (withAltar) structure.buildWithAltar(level, pos, facing); else structure.build(level, pos, facing);
                return true;
            }
        });
    }
    private ExtensionServices() {}
}
