package com.miophas.corruption.api.arcane;

import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.core.arcane.CastingUnit;
import com.miophas.corruption.core.arcane.Grimoire;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellCasting;
import com.miophas.corruption.core.arcane.SpellRule;
import com.miophas.corruption.core.energy.EnergyTool;
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
 * A casting tool: an item that holds Corruption Energy and the {@link WandSpells spells it has been taught},
 * and casts the chosen one when used. A cast costs the wand its energy, costs the caster one parchment bearing
 * the spell, may leave them more corrupted (never wholly), and wears off over a cooldown; what it does then is
 * {@link ArcaneCasting.World}, at the caster. In creative play a cast costs nothing and needs nothing but the
 * cooldown to have worn off.
 *
 * <p>The rules are {@link CastingUnit}, {@link SpellCasting} and the spell's {@link SpellRule}, all in
 * core.arcane. The charge it casts with is a {@link CorruptionEnergyTool}'s, not a battery's: how much it holds
 * and how much goes in at a time is this item's own, given here in code, and what it holds is drawn on by its
 * casts and filled again at a charger (see {@link EnergyCharger}) rather than being poured out and consumed as
 * a Corruption Candle's is. A wand of an add-on's is made by extending this class.
 */
public class ArcaneCastingItem extends Item implements CorruptionEnergyTool {
    /** What the mod's own wand is: a tool of twenty KCE, filled a KCE a time. */
    public static final EnergyTool DEFAULT_TOOL = new EnergyTool(CorruptionEnergyApi.kilo(20), CorruptionEnergyApi.kilo(1));
    private static final int BAR_COLOUR = 0xC6728C;

    private final EnergyTool tool;
    private final int spellCapacity;

    /** A casting tool that is the mod's own wand, with room for {@link Grimoire#CAPACITY} spells. */
    public ArcaneCastingItem(Item.Properties properties) { this(properties, DEFAULT_TOOL); }
    /** @param tool how much charge it holds, and how much goes in at a time */
    public ArcaneCastingItem(Item.Properties properties, EnergyTool tool) { this(properties, tool, Grimoire.CAPACITY); }
    /**
     * @param tool how much charge it holds, and how much goes in at a time
     * @param spellCapacity how many spells it has room for; its wheel shows that many places
     */
    public ArcaneCastingItem(Item.Properties properties, EnergyTool tool, int spellCapacity) {
        super(properties);
        this.tool = tool;
        this.spellCapacity = Math.max(1, spellCapacity);
    }

    @Override public EnergyTool tool() { return tool; }
    /** How many spells this tool has room for. */
    public int capacity() { return spellCapacity; }
    /** What one of these holds now, in CE. */
    public long getEnergy(ItemStack wand) { return CorruptionEnergyTool.held(wand); }

    /** What this wand, as it is held, has been taught and holds. */
    public CastingUnit unit(ItemStack wand) { return new CastingUnit(WandSpells.of(wand), getEnergy(wand), spellCapacity); }

    /** Writes a unit back onto the wand itself: the spells it has been taught, and the energy it holds. */
    public void store(ItemStack wand, CastingUnit unit) {
        WandSpells.set(wand, unit.grimoire());
        wand.set(CorruptionEnergyCarrier.ENERGY.get(), Math.max(0, unit.energy()));
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack wand = player.getItemInHand(hand);
        // The server decides whether the cast happens; it swings the caster's arm if it does.
        if (!(player instanceof ServerPlayer caster)) return InteractionResultHolder.consume(wand);

        CastingUnit unit = unit(wand);
        String spell = unit.current();
        ArcaneSpell known = spell == null ? null : ArcaneSpellApi.spell(caster.level().registryAccess(), ArcaneApi.key(spell));
        if (known == null) {
            notice(player, "message.corruption.spell_none");
            return InteractionResultHolder.fail(wand);
        }

        SpellRule rule = known.rule();
        ArcaneCasting.World world = ArcaneCasting.world();
        CastingUnit.Situation situation = new CastingUnit.Situation(world.corruption(caster), PlayerCorruptionApi.max(), parchments(player, spell),
                player.getCooldowns().isOnCooldown(wand.getItem()), player.getAbilities().instabuild);
        SpellCasting.Check check = SpellCasting.cast(rule, unit.caster(situation, (energy, usedUp, corruptionGain) -> {
            store(wand, unit.spending(energy));
            useParchments(player, spell, usedUp);
            world.corrupt(caster, corruptionGain);
        }));
        if (check != SpellCasting.Check.READY) {
            notice(player, messageFor(check));
            caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, .6F, 1.6F);
            return InteractionResultHolder.fail(wand);
        }

        ArcaneCasting.Origin origin = ArcaneCasting.Origin.of(caster);
        for (SpellAction action : rule.actions()) world.perform(caster, origin, action);
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
            case TOO_CORRUPTED -> "message.corruption.spell_too_corrupted";
            case NO_ENERGY -> "message.corruption.spell_no_energy";
            case NO_PARCHMENT -> "message.corruption.spell_no_parchment";
            case READY -> "message.corruption.spell_none";
        };
    }

    /** A bar under it shows what charge it holds, as a tool's bar shows its wear. */
    @Override public boolean isBarVisible(ItemStack wand) { return tool.capacity() > 0 && getEnergy(wand) < tool.capacity(); }
    @Override public int getBarWidth(ItemStack wand) { return Math.round(13 * CorruptionEnergyApi.fraction(getEnergy(wand), tool.capacity())); }
    @Override public int getBarColor(ItemStack wand) { return BAR_COLOUR; }

    private static void notice(Player player, String key) {
        player.displayClientMessage(Component.translatable(key), true);
    }
}
