package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptionPurifier;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

/**
 * An item taken to lower a player's corruption: drunk or eaten, it is used up and takes away what its
 * {@link CorruptionPurifier} says, leaving its container behind if it has one (a bottle, say). It can be taken
 * whenever, hungry or not. The mod's purified water is one; an add-on's elixir is made by making another, with a
 * purifier of its own.
 */
public class CorruptionPurifyingItem extends Item {
    /** Ticks it takes to drink or eat one, as a potion takes. */
    public static final int USE_TICKS = 32;

    private final CorruptionPurifier purifier;
    private final UseAnim animation;
    private final ItemLike container;

    /**
     * @param animation how it is taken: {@link UseAnim#DRINK} or {@link UseAnim#EAT}
     * @param container what is left of one once taken, or null for nothing
     */
    public CorruptionPurifyingItem(Item.Properties properties, CorruptionPurifier purifier, UseAnim animation, ItemLike container) {
        super(properties);
        this.purifier = purifier;
        this.animation = animation;
        this.container = container;
    }

    public CorruptionPurifier purifier() { return purifier; }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) { return ItemUtils.startUsingInstantly(level, player, hand); }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return animation; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return USE_TICKS; }

    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (user instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide && user instanceof Player player) purify(player);
        boolean free = user instanceof Player player && player.getAbilities().instabuild;
        if (!free) stack.shrink(1);
        if (container == null || free) return stack;
        ItemStack left = new ItemStack(container);
        if (stack.isEmpty()) return left;
        if (user instanceof Player player && !player.getInventory().add(left)) player.drop(left, false);
        return stack;
    }

    /** Lowers a player's corruption as this purifier does. */
    public void purify(Player player) { PlayerCorruptionHolder.set(player, purifier.purify(PlayerCorruptionHolder.of(player))); }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.corruption.purifies", purifier.amount()).withStyle(ChatFormatting.AQUA));
    }
}
