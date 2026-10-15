package com.miophas.corruption.common.energy;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyAcceptance;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.core.energy.EnergyTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Pouring Corruption Energy by hand, out of an item that carries it (a Corruption Candle, say), one item at a time:
 * the receiver takes as much as it has room for ({@link CorruptionEnergyAcceptance#PARTIAL}), and the item keeps the
 * rest, or burns away if it is the kind that does.
 *
 * <ul>
 *   <li><b>Into a block or creature.</b> A player who sneaks and uses the item on anything that offers a store
 *       through CorruptionEnergyCapabilities, an add-on's included, pours it in.</li>
 *   <li><b>Into a tool in hand.</b> A player holding a Corruption Energy tool (anything that is a
 *       {@link CorruptionEnergyTool}: a wand, or an add-on's tool) in the main hand, and an item carrying energy in
 *       the off hand, charges the tool from it by using the tool: a candle at a use. This comes before what the tool
 *       does when used, so a wand with room for more is charged rather than cast with; a full one casts as ever.</li>
 * </ul>
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

    /** Using a tool with a battery in the other hand charges it, before the tool does anything else. */
    @SubscribeEvent(priority = EventPriority.HIGH) public static void onUseTool(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (event.getHand() != InteractionHand.MAIN_HAND || !chargeable(player.getMainHandItem(), player.getOffhandItem())) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (!event.getLevel().isClientSide) chargeHeld(player);
    }

    private static boolean pouring(Player player, ItemStack held) { return player.isSecondaryUseActive() && CorruptionEnergyApi.carriesEnergy(held); }

    /** Whether a tool can be charged from a battery by hand: it is a tool with room for more, and the battery has energy to give. */
    public static boolean chargeable(ItemStack tool, ItemStack battery) {
        EnergyTool kind = CorruptionEnergyTool.of(tool);
        return kind.isTool() && CorruptionEnergyTool.held(tool) < kind.capacity() && CorruptionEnergyApi.carriesEnergy(battery);
    }

    /** Pours what one held item carries into a store, as much as it takes. Answers how much went in. */
    public static long pour(Player player, InteractionHand hand, CorruptionEnergyStorage store, Vec3 at) {
        long poured = give(player, hand, store);
        if (poured <= 0) {
            player.displayClientMessage(Component.translatable("message.corruption.energy_no_room"), true);
            return 0;
        }
        player.level().playSound(null, at.x, at.y, at.z, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, .5F, 1.6F);
        player.displayClientMessage(Component.translatable("message.corruption.energy_poured", CorruptionEnergyApi.format(poured)), true);
        return poured;
    }

    /**
     * Charges the tool in a player's main hand from one of the batteries in their off hand, as much as it has room
     * for. Answers how much went in.
     */
    public static long chargeHeld(Player player) {
        ItemStack tool = player.getMainHandItem();
        if (!chargeable(tool, player.getOffhandItem())) return 0;
        CorruptionEnergyStorage store = CorruptionEnergyTool.storage(tool);
        long charged = store == null ? 0 : give(player, InteractionHand.OFF_HAND, store);
        if (charged <= 0) return 0;
        long held = CorruptionEnergyTool.held(tool), capacity = CorruptionEnergyTool.of(tool).capacity();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, .6F, 1.9F);
        if (player.level() instanceof ServerLevel server)
            server.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.2, player.getZ(), 16, .4, .4, .4, .5);
        player.displayClientMessage(Component.translatable("message.corruption.tool_charged", CorruptionEnergyApi.format(charged),
                CorruptionEnergyApi.format(held, CorruptionEnergyApi.unit(capacity)), CorruptionEnergyApi.format(capacity)), true);
        return charged;
    }

    /**
     * Takes one of the items in a hand and gives what it carries to a store, as much as it takes; puts back what is
     * left of it, or nothing if it burnt away. Answers how much went in; with none, the hand is left as it was.
     */
    static long give(Player player, InteractionHand hand, CorruptionEnergyStorage store) {
        ItemStack held = player.getItemInHand(hand);
        CorruptionEnergyApi.Imported given = CorruptionEnergyApi.importFrom(held, store, CorruptionEnergyAcceptance.PARTIAL);
        if (given.energy() <= 0) return 0;
        if (held.getCount() == 1) player.setItemInHand(hand, given.left());
        else {
            held.shrink(1);
            if (!given.left().isEmpty() && !player.getInventory().add(given.left())) player.drop(given.left(), false);
        }
        return given.energy();
    }

    private EnergyPouring() {}
}
