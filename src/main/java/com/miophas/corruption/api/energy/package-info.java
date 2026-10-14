/**
 * Corruption Energy for add-ons. Everything another mod needs is in this package; nothing here changes
 * without care, unlike the mod's own code in common.
 *
 * <ul>
 *   <li><b>The unit.</b> CE, counted in thousands: KCE, MCE, BCE, TCE, QaCE, QiCE ({@link com.miophas.corruption.api.energy.CorruptionEnergyUnit}).
 *   {@link com.miophas.corruption.api.energy.CorruptionEnergyApi#parse parse}
 *   and {@link com.miophas.corruption.api.energy.CorruptionEnergyApi#format format} read and write amounts,
 *   and {@link com.miophas.corruption.api.energy.CorruptionEnergyCodec#AMOUNT} does the same in data files.</li>
 *   <li><b>Holding it.</b> {@link com.miophas.corruption.api.energy.CorruptionEnergyStorage} is a store as anyone
 *   sees it. Make your own with {@link com.miophas.corruption.api.energy.CorruptionEnergyApi#buffer buffer}.</li>
 *   <li><b>Moving it.</b> Offer a store through {@link com.miophas.corruption.api.energy.CorruptionEnergyCapabilities},
 *   find others' with {@link com.miophas.corruption.api.energy.CorruptionEnergyApi#storage storage}, and move
 *   energy between two with {@link com.miophas.corruption.api.energy.CorruptionEnergyApi#move move}. The arcane
 *   transmuter takes energy from any side, so a generator of yours can feed it.</li>
 *   <li><b>Carrying it.</b> Items carry energy as data: the {@link com.miophas.corruption.api.energy.CorruptionEnergyCarrier#DATA_MAP carrier data map}
 *   says which items can and how much, and the {@link com.miophas.corruption.api.energy.CorruptionEnergyCarrier#ENERGY corruption:corruption_energy}
 *   component how much one carries now. An item of your own extends {@link com.miophas.corruption.api.energy.CorruptionEnergyItem},
 *   which gives its energy out all at once ({@code extractAll}) or bit by bit ({@code extractStep}). Give it to any block's or entity's store with
 *   {@link com.miophas.corruption.api.energy.CorruptionEnergyApi#importFrom importFrom}, which takes it in as a
 *   {@link com.miophas.corruption.api.energy.CorruptionEnergyAcceptance} says. A player pours it in by sneaking and using
 *   the item on the block or entity, so offering a store through the capabilities is all an add-on's machine or
 *   creature needs to accept it; the arcane transmuter also loads such items from its fuel slot.</li>
 *   <li><b>Making it.</b> Fuels are a data map, {@link com.miophas.corruption.api.energy.CorruptionFuel#DATA_MAP};
 *   add your own in a data pack, and burn them in your own machines by asking
 *   {@link com.miophas.corruption.api.energy.CorruptionFuel#of}.</li>
 *   <li><b>Spending it.</b> Corrupted Arcane Transmutation recipes are data: type
 *   "corruption:arcane_transmutation", with an "ingredient", an "energy" cost and a "result".</li>
 * </ul>
 *
 * The rules behind all of this (limits, shares, the unit) live in com.miophas.corruption.core.energy, plain
 * Java with no game code in it.
 */
package com.miophas.corruption.api.energy;
