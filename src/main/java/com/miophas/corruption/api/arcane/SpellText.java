package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

/**
 * What a spell does, read out in words: one line for each action it carries out, in the order it carries them
 * out, and a bolt's payload told under it as what it does where it strikes. What each line says is data
 * (spell.corruption.*), and a creature, an item or a mob effect is named by the game's own name for it, so the
 * lines read in whatever language the player has chosen.
 *
 * <p>Read wherever a spell is to be understood: under a wand's tooltip, under the wheel, and in JEI. A spell
 * that hurts with a kind of its own says so (see {@link ArcaneDamage}); one that hurts with the arcane mark
 * everyone carries says nothing, since that is what a spell's hurt is by default.
 */
public final class SpellText {
    /** What this spell does: one line an action, in the order it does them. */
    public static List<Component> lines(SpellRule rule) {
        List<Component> lines = new ArrayList<>();
        for (SpellAction action : rule.actions()) add(lines, action, "");
        return lines;
    }

    /** One action read out, set in this far; a bolt's payload follows it, set in a little further. */
    private static void add(List<Component> lines, SpellAction action, String indent) {
        lines.add(line(action, indent));
        if (!(action instanceof SpellAction.LaunchBolt bolt) || bolt.onHit().isEmpty()) return;
        lines.add(tell(indent, "spell.corruption.on_hit"));
        for (SpellAction payload : bolt.onHit()) add(lines, payload, indent + "  ");
    }

    /** One action, in words. */
    private static Component line(SpellAction action, String indent) {
        return switch (action) {
            case SpellAction.ApplyEffect effect -> effect.target() == SpellAction.Target.SELF
                    ? tell(indent, "spell.corruption.effect_self", effectName(effect.effect()), seconds(effect.duration()))
                    : tell(indent, "spell.corruption.effect_nearby", effectName(effect.effect()), number(effect.radius()), seconds(effect.duration()));
            case SpellAction.Damage hurt -> hurt.type().isEmpty()
                    ? tell(indent, "spell.corruption.damage", number(hurt.amount()), number(hurt.radius()))
                    : tell(indent, "spell.corruption.damage_typed", number(hurt.amount()), number(hurt.radius()), damageName(hurt.type()));
            case SpellAction.Ignite ignite -> tell(indent, "spell.corruption.ignite_area", number(ignite.seconds()), number(ignite.radius()));
            case SpellAction.Freeze freeze -> tell(indent, "spell.corruption.freeze_area", seconds(freeze.ticks()), number(freeze.radius()));
            case SpellAction.Corrupt corrupt -> tell(indent, "spell.corruption.corrupt_area", number(corrupt.radius()), percent(corrupt.chance()));
            case SpellAction.Summon summon -> tell(indent, "spell.corruption.summon", entityName(summon.entity()), number(summon.count()));
            case SpellAction.Lightning strike -> tell(indent, "spell.corruption.lightning", number(strike.count()), number(strike.radius()));
            case SpellAction.Explode burst -> tell(indent, burst.breaksBlocks() ? "spell.corruption.explode_breaks" : "spell.corruption.explode", number(burst.power()));
            case SpellAction.Teleport step -> tell(indent, "spell.corruption.teleport", number(step.distance()));
            case SpellAction.Weather sky -> tell(indent, "spell.corruption.weather", skyName(sky.sky()), seconds(sky.ticks()));
            case SpellAction.Pacify quiet -> tell(indent, "spell.corruption.pacify", number(quiet.radius()));
            case SpellAction.GiveItem give -> tell(indent, "spell.corruption.give_item", itemName(give.item()), number(give.count()));
            case SpellAction.LaunchBolt bolt -> tell(indent, "spell.corruption.launch_bolt", entityName(bolt.entity()), number(bolt.speed()));
        };
    }

    /** A line of its own, set in from the left as far as this, in the plain grey of what things are worth. */
    private static Component tell(String indent, String key, Object... args) {
        return Component.literal(indent).append(Component.translatable(key, args)).withStyle(ChatFormatting.GRAY);
    }

    /** What a spell's hurt is called: "腐化咒蚀" for the corruption's own, and its id for a kind with no name. */
    private static Component damageName(String type) {
        ResourceLocation id = ResourceLocation.tryParse(type);
        return id == null ? Component.literal(type)
                : Component.translatableWithFallback("damage." + id.getNamespace() + "." + id.getPath(), type);
    }

    /** A mob effect by name, in the player's own language. */
    private static Component effectName(String id) {
        ResourceLocation name = ResourceLocation.tryParse(id);
        return name == null ? Component.literal(id)
                : BuiltInRegistries.MOB_EFFECT.getOptional(name).map(effect -> effect.getDisplayName()).orElse(Component.literal(id));
    }

    /** A creature by name, in the player's own language. */
    private static Component entityName(String id) {
        return EntityType.byString(id).map(EntityType::getDescription).orElse(Component.literal(id));
    }

    /** An item by name, in the player's own language. */
    private static Component itemName(String id) {
        ResourceLocation name = ResourceLocation.tryParse(id);
        return name == null ? Component.literal(id)
                : BuiltInRegistries.ITEM.getOptional(name).map(item -> new ItemStack(item).getHoverName()).orElse(Component.literal(id));
    }

    private static Component skyName(SpellAction.Sky sky) {
        return Component.translatable("spell.corruption.sky." + sky.name().toLowerCase(Locale.ROOT));
    }

    /** A count of ticks as the seconds a player thinks in. */
    private static String seconds(int ticks) { return number(ticks / 20.0); }

    /** A share as the hundredths a player thinks in. */
    private static String percent(double chance) { return number(chance * 100) + "%"; }

    /** A number as a player reads it: no trailing .0, and one place kept for a part of a whole. */
    private static String number(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.format(Locale.ROOT, "%.1f", value);
    }

    private SpellText() {}
}
