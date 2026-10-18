package com.miophas.corruption.common.loot;

import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModLoot;
import com.miophas.corruption.core.loot.StructurePrizes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;

/**
 * The loot condition corruption:once_per_structure. Inside one of the given structures it holds the first time it is asked
 * there and never again, once for each structure and prize; outside them it never holds. A chest's loot is rolled when
 * the chest is first opened, broken or emptied by a hopper, so of all the chests of one structure, only the first to be
 * filled passes it.
 *
 * <pre>{"condition": "corruption:once_per_structure", "structures": "minecraft:stronghold", "prize": "my_pack:my_prize"}</pre>
 *
 * <p>{@code structures} is one structure, a list of them or a tag. The place must be inside one of the structure's pieces,
 * as vanilla finds a fortress for its spawns; a structure placed with /place structure is not recorded in its chunks, so
 * it is not found. The prize names what is taken, so that two prizes in one structure are counted apart. What has been
 * taken is kept with the level ({@link ModAttachments#STRUCTURE_PRIZES}).
 *
 * <p>The prize is taken whenever the condition holds, so put it last, after the conditions that say which loot it is.
 */
public record OncePerStructure(HolderSet<Structure> structures, ResourceLocation prize) implements LootItemCondition {
    public static final MapCodec<OncePerStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(OncePerStructure::structures),
            ResourceLocation.CODEC.fieldOf("prize").forGetter(OncePerStructure::prize)).apply(instance, OncePerStructure::new));
    /** How a level's taken prizes are saved: their keys. */
    public static final Codec<StructurePrizes> PRIZES_CODEC = Codec.STRING.listOf().xmap(StructurePrizes::new, prizes -> List.copyOf(prizes.keys()));

    @Override public LootItemConditionType getType() { return ModLoot.ONCE_PER_STRUCTURE.get(); }

    @Override public Set<LootContextParam<?>> getReferencedContextParams() { return Set.of(LootContextParams.ORIGIN); }

    @Override public boolean test(LootContext context) {
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) return false;
        ServerLevel level = context.getLevel();
        StructureStart start = StructureLookup.at(level, BlockPos.containing(origin), structures);
        if (!start.isValid()) return false;
        ResourceLocation structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(start.getStructure());
        return structure != null && level.getData(ModAttachments.STRUCTURE_PRIZES).take(prize.toString(), structure.toString(), start.getChunkPos().x,
                start.getChunkPos().z);
    }
}
