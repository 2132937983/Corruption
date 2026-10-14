package com.miophas.corruption.common.energy;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyAcceptance;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Pouring by hand: a player who sneaks and uses an item carrying Corruption Energy, a Corruption Candle
 * say, on a block or entity that holds energy pours it in. The store takes as much as it has room for
 * ({@link CorruptionEnergyAcceptance#PARTIAL}), from one item at a time; that item keeps the rest, or burns
 * away if it is the kind that does. Anything that offers a store through CorruptionEnergyCapabilities, an
 * add-on's included, can be poured into this way.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class EnergyPouring {
    @SubscribeEvent public static void onUseOnBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!pouring(event.getEntity(), event.getItemStack())) return;
        CorruptionEnergyStorage store = CorruptionEnergyApi.storage(event.getLevel(), event.getPos(), event.getFace());
        if (store == null || !store.canInsert()) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (!event.getLevel().isClientSide) pour(event.getEntity(), event.getHand(), store, event.getPos().getCenter());
    }

    @SubscribeEvent public static void onUseOnEntity(PlayerInteractEvent.EntityInteract event) {
        if (!pouring(event.getEntity(), event.getItemStack())) return;
        CorruptionEnergyStorage store = CorruptionEnergyApi.storage(event.getTarget());
        if (store == null || !store.canInsert()) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (!event.getLevel().isClientSide) pour(event.getEntity(), event.getHand(), store, event.getTarget().position());
    }

    private static boolean pouring(Player player, ItemStack held) { return player.isSecondaryUseActive() && CorruptionEnergyApi.carriesEnergy(held); }

    /** Pours what one held item carries into a store, as much as it takes. Answers how much went in. */
    public static long pour(Player player, InteractionHand hand, CorruptionEnergyStorage store, Vec3 at) {
        ItemStack held = player.getItemInHand(hand);
        CorruptionEnergyApi.Imported poured = CorruptionEnergyApi.importFrom(held, store, CorruptionEnergyAcceptance.PARTIAL);
        if (poured.energy() <= 0) {
            player.displayClientMessage(Component.translatable("message.corruption.energy_no_room"), true);
            return 0;
        }
        if (held.getCount() == 1) player.setItemInHand(hand, poured.left());
        else {
            held.shrink(1);
            if (!poured.left().isEmpty() && !player.getInventory().add(poured.left())) player.drop(poured.left(), false);
        }
        player.level().playSound(null, at.x, at.y, at.z, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, .5F, 1.6F);
        player.displayClientMessage(Component.translatable("message.corruption.energy_poured", CorruptionEnergyApi.format(poured.energy())), true);
        return poured.energy();
    }

    private EnergyPouring() {}
}
