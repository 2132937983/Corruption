package com.miophas.corruption.common.item;

import com.miophas.corruption.api.winnow.LeafWinnowApi;
import com.miophas.corruption.api.winnow.LeafWinnowDrop;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shakes whatever a data pack winnowing table lists out of the block it is used on.
 * Blocks without a table are left alone, so the tool only reacts to leaves the pack covers.
 */
public final class LeafWinnowerItem extends Item {
    public LeafWinnowerItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        List<LeafWinnowDrop> drops = new ArrayList<>(
                LeafWinnowApi.drops(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString()));
        state.getBlockHolder().tags().forEach(tag -> drops.addAll(LeafWinnowApi.tagDrops(tag.location().toString())));
        if (drops.isEmpty()) return InteractionResult.PASS;
        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();
            int fortune = context.getItemInHand().getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE));
            for (LeafWinnowDrop drop : drops) {
                int bonus = LeafWinnowApi.fortuneBonus(fortune, random.nextFloat());
                int amount = LeafWinnowApi.amount(drop, random.nextFloat(), random.nextFloat(), bonus);
                if (amount <= 0) continue;
                ResourceLocation item = ResourceLocation.tryParse(drop.item());
                if (item == null) continue;
                Item dropped = BuiltInRegistries.ITEM.get(item);
                if (dropped == Items.AIR) continue;
                Block.popResource(serverLevel, pos, new ItemStack(dropped, amount));
            }
            // The shaken leaves are spent: one use per block, one point of durability per use.
            serverLevel.destroyBlock(pos, false, null, 512);
            Player player = context.getPlayer();
            if (player != null) context.getItemInHand().hurtAndBreak(1, player,
                    context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
