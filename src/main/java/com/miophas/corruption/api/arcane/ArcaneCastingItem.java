package com.miophas.corruption.api.arcane;

import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyItem;
import com.miophas.corruption.core.arcane.CastingUnit;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellCasting;
import com.miophas.corruption.core.arcane.SpellRule;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A casting tool: an item that carries Corruption Energy and the {@link WandSpells spells it has been taught},
 * and casts the chosen one when used. A cast costs the wand its energy, costs the caster one parchment bearing
 * the spell, may leave them more corrupted, and wears off over a cooldown; what it does then is
 * {@link ArcaneCasting.World}.
 *
 * <p>The rules are {@link CastingUnit}, {@link SpellCasting} and the spell's {@link SpellRule}, all in
 * core.arcane; this is where they meet the game, and it is what a wand of the mod's own or of an add-on is:
 * extend it and register the item as one that carries energy (corruption_energy_carrier).
 */
public class ArcaneCastingItem extends CorruptionEnergyItem {
    public ArcaneCastingItem(Item.Properties properties) { super(properties); }
    /** @param step how much it gives away at a time when its energy is poured out, in CE */
    public ArcaneCastingItem(Item.Properties properties, long step) { super(properties, step); }

    /** What this wand, as it is held, has been taught and holds. */
    public CastingUnit unit(ItemStack wand) { return new CastingUnit(WandSpells.of(wand), getEnergy(wand)); }

    /** Writes a unit back onto the wand itself: the spells it has been taught, and the energy it holds. */
    public void store(ItemStack wand, CastingUnit unit) {
        WandSpells.set(wand, unit.grimoire());
        wand.set(CorruptionEnergyCarrier.ENERGY.get(), Math.max(0, unit.energy()));
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack wand = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer caster)) return InteractionResultHolder.success(wand);

        CastingUnit unit = unit(wand);
        String spell = unit.current();
        ArcaneSpell known = spell == null ? null : ArcaneSpellApi.spell(caster.level().registryAccess(), ArcaneApi.key(spell));
        if (known == null) {
            notice(player, "message.corruption.spell_none");
            return InteractionResultHolder.fail(wand);
        }

        SpellRule rule = known.rule();
        ArcaneCasting.World world = ArcaneCasting.world();
        SpellCasting.Check check = SpellCasting.cast(rule, unit.caster(world.corruption(caster), parchments(player, spell),
                player.getCooldowns().isOnCooldown(wand.getItem()), (energy, usedUp, corruptionGain) -> {
                    store(wand, unit.spending(energy));
                    useParchments(player, spell, usedUp);
                    world.corrupt(caster, corruptionGain);
                }));
        if (check != SpellCasting.Check.READY) {
            notice(player, messageFor(check));
            caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, .6F, 1.6F);
            return InteractionResultHolder.fail(wand);
        }

        for (SpellAction action : rule.actions()) world.perform(caster, action);
        if (rule.cooldown() > 0) player.getCooldowns().addCooldown(wand.getItem(), rule.cooldown());
        caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.swing(hand, true);
        return InteractionResultHolder.success(wand);
    }

    /** How many parchments bearing this spell the player carries. */
    protected int parchments(Player player, String spell) {
        int found = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack carried = player.getInventory().getItem(slot);
            if (bears(carried, spell)) found += carried.getCount();
        }
        return found;
    }

    /** Uses up this many parchments bearing the spell, taking them from wherever they lie. */
    protected void useParchments(Player player, String spell, int count) {
        int left = count;
        for (int slot = 0; slot < player.getInventory().getContainerSize() && left > 0; slot++) {
            ItemStack carried = player.getInventory().getItem(slot);
            if (!bears(carried, spell)) continue;
            int taken = Math.min(left, carried.getCount());
            carried.shrink(taken);
            left -= taken;
        }
    }

    /** Whether a stack is parchment bearing this spell. */
    protected boolean bears(ItemStack stack, String spell) {
        ResourceKey<ArcaneString> inscription = ArcaneApi.inscription(stack);
        return inscription != null && ArcaneApi.id(inscription).equals(spell);
    }

    /** What to tell the caster when the spell does not work. */
    protected String messageFor(SpellCasting.Check check) {
        return switch (check) {
            case COOLING -> "message.corruption.spell_cooling";
            case NOT_CORRUPTED_ENOUGH -> "message.corruption.spell_not_corrupted";
            case NO_ENERGY -> "message.corruption.spell_no_energy";
            case NO_PARCHMENT -> "message.corruption.spell_no_parchment";
            case READY -> "message.corruption.spell_none";
        };
    }

    private static void notice(Player player, String key) {
        player.displayClientMessage(Component.translatable(key), true);
    }
}
