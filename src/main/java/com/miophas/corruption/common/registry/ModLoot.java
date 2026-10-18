package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.loot.InStructurePiece;
import com.miophas.corruption.common.loot.OncePerStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The mod's loot conditions, for its loot tables and any data pack's. */
public final class ModLoot {
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Corruption.MOD_ID);

    /** corruption:once_per_structure: once for each structure and prize ({@link OncePerStructure}). */
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> ONCE_PER_STRUCTURE =
            CONDITIONS.register("once_per_structure", () -> new LootItemConditionType(OncePerStructure.CODEC));
    /** corruption:structure_piece: inside a piece built from one of some templates ({@link InStructurePiece}). */
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> STRUCTURE_PIECE =
            CONDITIONS.register("structure_piece", () -> new LootItemConditionType(InStructurePiece.CODEC));

    private ModLoot() {}
}
