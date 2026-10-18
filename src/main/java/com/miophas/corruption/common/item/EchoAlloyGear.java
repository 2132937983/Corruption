package com.miophas.corruption.common.item;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneWeapons;
import com.miophas.corruption.api.corruption.CorruptionCurseApi;
import com.miophas.corruption.api.corruption.CurseWardItem;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.alloy.EchoAlloyRules;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * The corruption echo alloy's tools and what its armour does (EchoAlloyRules). The tools are of a tier past netherite's,
 * mended with the alloy, and do not burn; the sword carries the corruption's curse by the item tag
 * corruption:cursed_weapons (CorruptionBlades lays it, and turns its blows aside from the corruption's lords). The armour
 * turns aside the curse's own hurt (CurseWardItem): of every pulse of the curse on its wearer, each piece takes its share
 * away, the whole set all of it; nothing else of the corruption's hurt is touched.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class EchoAlloyGear {
    /** The tools' tier: what netherite cannot dig, nor can it; longer-lasting, keener and heavier. */
    public static final Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, EchoAlloyRules.TOOL_DURABILITY, EchoAlloyRules.TOOL_SPEED,
            EchoAlloyRules.ATTACK_BONUS, EchoAlloyRules.TOOL_ENCHANTABILITY, () -> Ingredient.of(ModItems.CORRUPTION_ECHO_ALLOY.get()));

    public static SwordItem sword() {
        return new Sword(new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(TIER, EchoAlloyRules.SWORD_DAMAGE, EchoAlloyRules.SWORD_SWING)));
    }

    public static PickaxeItem pickaxe() {
        return new PickaxeItem(TIER, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(TIER, EchoAlloyRules.PICKAXE_DAMAGE,
                EchoAlloyRules.PICKAXE_SWING)));
    }

    public static AxeItem axe() {
        return new AxeItem(TIER, new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(TIER, EchoAlloyRules.AXE_DAMAGE, EchoAlloyRules.AXE_SWING)));
    }

    /** The sword: under its name, the curse it carries (by its tag). */
    private static final class Sword extends SwordItem {
        private Sword(Item.Properties properties) { super(TIER, properties); }

        @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
            if (stack.is(ArcaneWeapons.CURSED)) lines.add(Component.translatable("tooltip.corruption.blade_cursed").withStyle(ChatFormatting.DARK_RED));
        }
    }

    /** Of each pulse of the curse, what the wearer's armour turns aside is taken away; all of it, and the pulse does not land. */
    @SubscribeEvent
    public static void wardOffTheCurse(LivingIncomingDamageEvent event) {
        if (!CorruptionCurseApi.isCurse(event.getSource())) return;
        float through = CurseWardItem.taken(event.getEntity(), event.getAmount());
        if (through <= 0) event.setCanceled(true);
        else if (through != event.getAmount()) event.setAmount(through);
    }

    private EchoAlloyGear() {}
}
