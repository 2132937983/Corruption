package com.miophas.corruption.common.loot;

import com.miophas.corruption.common.registry.ModLoot;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;

/**
 * The loot condition corruption:structure_piece: it holds where the place is inside a piece of one of the given structures
 * that was built from one of the given templates; the ship of an end city, for one:
 *
 * <pre>{"condition": "corruption:structure_piece", "structures": "minecraft:end_city", "templates": ["minecraft:end_city/ship"]}</pre>
 *
 * <p>A piece's template is the structure template it was placed from: a template piece's own (an end city's, a woodland
 * mansion's, an igloo's), or a jigsaw piece's single pool element (a bastion's, a village's). The pieces built in code
 * (a stronghold's rooms, a fortress's corridors) have none, so they never match. {@code structures} is one structure, a
 * list of them or a tag. It takes nothing, so it may stand anywhere among the conditions.
 */
public record InStructurePiece(HolderSet<Structure> structures, List<ResourceLocation> templates) implements LootItemCondition {
    public static final MapCodec<InStructurePiece> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(InStructurePiece::structures),
            ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(InStructurePiece::templates)).apply(instance, InStructurePiece::new));

    @Override public LootItemConditionType getType() { return ModLoot.STRUCTURE_PIECE.get(); }

    @Override public Set<LootContextParam<?>> getReferencedContextParams() { return Set.of(LootContextParams.ORIGIN); }

    @Override public boolean test(LootContext context) {
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) return false;
        BlockPos pos = BlockPos.containing(origin);
        StructureStart start = StructureLookup.at(context.getLevel(), pos, structures);
        if (!start.isValid()) return false;
        for (StructurePiece piece : start.getPieces())
            if (piece.getBoundingBox().isInside(pos) && templates.contains(template(piece))) return true;
        return false;
    }

    /** The structure template a piece was placed from, or null for a piece built in code. */
    @Nullable public static ResourceLocation template(StructurePiece piece) {
        if (piece instanceof TemplateStructurePiece placed) return placed.makeTemplateLocation();
        if (piece instanceof PoolElementStructurePiece jigsaw && jigsaw.getElement() instanceof SinglePoolElement single) return single.template.left().orElse(null);
        return null;
    }
}
