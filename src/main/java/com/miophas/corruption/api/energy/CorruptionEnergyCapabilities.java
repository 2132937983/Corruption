package com.miophas.corruption.api.energy;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * How blocks and items offer their Corruption Energy to the world, as NeoForge capabilities. To make a block
 * hold CE, register a provider in RegisterCapabilitiesEvent:
 * <pre>
 * event.registerBlockEntity(CorruptionEnergyCapabilities.BLOCK, MY_MACHINE.get(), (machine, side) -&gt; machine.energy());
 * </pre>
 * and to find one: {@code level.getCapability(CorruptionEnergyCapabilities.BLOCK, pos, side)}, null if
 * there is none. The arcane transmuter answers on every side, taking energy in but giving none out.
 * Entities offer theirs the same way, with {@code event.registerEntity(ENTITY, MY_ENTITY.get(), (entity, nothing) -> ...)}.
 * Whatever a block or entity offers here, a player can pour the energy an item carries into it, sneaking
 * and using the item on it.
 */
public final class CorruptionEnergyCapabilities {
    /** A block's store; the side is the one being asked from, or null for no side in particular. */
    public static final BlockCapability<CorruptionEnergyStorage, Direction> BLOCK =
            BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath("corruption", "corruption_energy"), CorruptionEnergyStorage.class);
    /**
     * An item's store, for anything that carries energy about. Every item the corruption_energy_carrier data
     * map names has one (see {@link CorruptionEnergyCarrier}); an add-on's own items may offer their own.
     */
    public static final ItemCapability<CorruptionEnergyStorage, Void> ITEM =
            ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath("corruption", "corruption_energy"), CorruptionEnergyStorage.class);
    /** An entity's store, for creatures and machines that move about holding energy. */
    public static final EntityCapability<CorruptionEnergyStorage, Void> ENTITY =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("corruption", "corruption_energy"), CorruptionEnergyStorage.class);

    private CorruptionEnergyCapabilities() {}
}
