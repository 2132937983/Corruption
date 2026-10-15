package com.miophas.corruption.api.arcane;

import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCodec;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellRule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

/**
 * How a spell is written in data. A spell is a rule — what it costs and what it does — and each action is one
 * object, named by "type", so a data pack can give any string of the arcane a spell of its own:
 * <pre>{@code
 * { "energy": "2KCE", "cooldown": 40, "min_corruption": 5, "corruption_gain": 2,
 *   "actions": [
 *     { "type": "corrupt_area", "radius": 3, "chance": 0.25 },
 *     { "type": "ignite_area", "radius": 4, "seconds": 8 },
 *     { "type": "launch_bolt", "speed": 1.5,
 *       "on_hit": [ { "type": "damage", "amount": 6, "radius": 2 } ] } ] }
 * }</pre>
 * A bolt's "on_hit" holds more actions, so the action codec is recursive: a bolt may carry any action,
 * including another bolt. Every field but the ones naming an effect, a creature or an item may be left out; what
 * it falls back to is written beside each kind below.
 */
public final class SpellCodecs {
    /** An id as data writes it, "corruption:spell_bolt", kept as the plain string the rules speak in. */
    private static final Codec<String> ID = ResourceLocation.CODEC.xmap(ResourceLocation::toString, ResourceLocation::parse);
    /** Who an effect falls on: "self" or "nearby". */
    private static final Codec<SpellAction.Target> TARGET = Codec.STRING.comapFlatMap(text -> switch (text) {
        case "self" -> DataResult.success(SpellAction.Target.SELF);
        case "nearby" -> DataResult.success(SpellAction.Target.NEARBY);
        default -> DataResult.error(() -> "A spell's target is self or nearby, not " + text);
    }, target -> target == SpellAction.Target.SELF ? "self" : "nearby");
    /** What the sky is told to do: "clear", "rain" or "thunder". */
    private static final Codec<SpellAction.Sky> SKY = Codec.STRING.comapFlatMap(text -> switch (text) {
        case "clear" -> DataResult.success(SpellAction.Sky.CLEAR);
        case "rain" -> DataResult.success(SpellAction.Sky.RAIN);
        case "thunder" -> DataResult.success(SpellAction.Sky.THUNDER);
        default -> DataResult.error(() -> "A spell's sky is clear, rain or thunder, not " + text);
    }, sky -> switch (sky) {
        case CLEAR -> "clear";
        case RAIN -> "rain";
        case THUNDER -> "thunder";
    });

    /** One action, named by its "type"; a bolt's payload may hold more of them. */
    public static final Codec<SpellAction> ACTION = Codec.recursive("SpellAction", SpellCodecs::actions);
    public static final Codec<List<SpellAction>> ACTIONS = ACTION.listOf();

    /** The rule of a spell: what it costs and what it does. */
    public static final Codec<SpellRule> RULE = RecordCodecBuilder.create(instance -> instance.group(
            CorruptionEnergyCodec.AMOUNT.optionalFieldOf("energy", 0L).forGetter(SpellRule::energy),
            Codec.intRange(0, Short.MAX_VALUE).optionalFieldOf("cooldown", 0).forGetter(SpellRule::cooldown),
            Codec.intRange(0, PlayerCorruptionApi.max()).optionalFieldOf("min_corruption", 0).forGetter(SpellRule::minCorruption),
            Codec.intRange(0, PlayerCorruptionApi.max()).optionalFieldOf("corruption_gain", 0).forGetter(SpellRule::corruptionGain),
            ACTIONS.fieldOf("actions").forGetter(SpellRule::actions)).apply(instance, SpellRule::new));

    /** What an action of each kind is called in data. */
    private enum Kind implements StringRepresentable {
        APPLY_EFFECT("apply_effect"), DAMAGE("damage"), IGNITE_AREA("ignite_area"), FREEZE_AREA("freeze_area"),
        CORRUPT_AREA("corrupt_area"), SUMMON("summon"), LIGHTNING("lightning"), EXPLODE("explode"),
        TELEPORT("teleport"), WEATHER("weather"), PACIFY("pacify"), GIVE_ITEM("give_item"), LAUNCH_BOLT("launch_bolt");

        static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);

        private final String name;
        Kind(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }

    private static final MapCodec<SpellAction.ApplyEffect> APPLY_EFFECT = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("effect").forGetter(SpellAction.ApplyEffect::effect),
            TARGET.optionalFieldOf("target", SpellAction.Target.SELF).forGetter(SpellAction.ApplyEffect::target),
            Codec.DOUBLE.optionalFieldOf("radius", 0.0).forGetter(SpellAction.ApplyEffect::radius),
            Codec.intRange(1, 20 * 60 * 10).optionalFieldOf("duration", 200).forGetter(SpellAction.ApplyEffect::duration),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(SpellAction.ApplyEffect::amplifier))
            .apply(instance, SpellAction.ApplyEffect::new));
    private static final MapCodec<SpellAction.Damage> DAMAGE = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("amount", 4.0).forGetter(SpellAction.Damage::amount),
            Codec.DOUBLE.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Damage::radius),
            ID.optionalFieldOf("damage_type", "").forGetter(SpellAction.Damage::type))
            .apply(instance, SpellAction.Damage::new));
    private static final MapCodec<SpellAction.Ignite> IGNITE_AREA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Ignite::radius),
            Codec.intRange(1, 20 * 60 * 5).optionalFieldOf("seconds", 8).forGetter(SpellAction.Ignite::seconds))
            .apply(instance, SpellAction.Ignite::new));
    private static final MapCodec<SpellAction.Freeze> FREEZE_AREA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Freeze::radius),
            Codec.intRange(1, 20 * 60).optionalFieldOf("ticks", 200).forGetter(SpellAction.Freeze::ticks))
            .apply(instance, SpellAction.Freeze::new));
    private static final MapCodec<SpellAction.Corrupt> CORRUPT_AREA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Corrupt::radius),
            Codec.DOUBLE.optionalFieldOf("chance", 0.25).forGetter(SpellAction.Corrupt::chance))
            .apply(instance, SpellAction.Corrupt::new));
    private static final MapCodec<SpellAction.Summon> SUMMON = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("entity").forGetter(SpellAction.Summon::entity),
            Codec.intRange(1, 16).optionalFieldOf("count", 1).forGetter(SpellAction.Summon::count))
            .apply(instance, SpellAction.Summon::new));
    private static final MapCodec<SpellAction.Lightning> LIGHTNING = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 4.0).forGetter(SpellAction.Lightning::radius),
            Codec.intRange(1, 16).optionalFieldOf("count", 1).forGetter(SpellAction.Lightning::count))
            .apply(instance, SpellAction.Lightning::new));
    private static final MapCodec<SpellAction.Explode> EXPLODE = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("power", 2.0).forGetter(SpellAction.Explode::power),
            Codec.BOOL.optionalFieldOf("breaks_blocks", false).forGetter(SpellAction.Explode::breaksBlocks))
            .apply(instance, SpellAction.Explode::new));
    private static final MapCodec<SpellAction.Teleport> TELEPORT = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("distance", 8.0).forGetter(SpellAction.Teleport::distance))
            .apply(instance, SpellAction.Teleport::new));
    private static final MapCodec<SpellAction.Weather> WEATHER = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SKY.optionalFieldOf("sky", SpellAction.Sky.CLEAR).forGetter(SpellAction.Weather::sky),
            Codec.intRange(1, 20 * 60 * 60).optionalFieldOf("ticks", 6000).forGetter(SpellAction.Weather::ticks))
            .apply(instance, SpellAction.Weather::new));
    private static final MapCodec<SpellAction.Pacify> PACIFY = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", 8.0).forGetter(SpellAction.Pacify::radius))
            .apply(instance, SpellAction.Pacify::new));
    private static final MapCodec<SpellAction.GiveItem> GIVE_ITEM = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("item").forGetter(SpellAction.GiveItem::item),
            Codec.intRange(1, 64).optionalFieldOf("count", 1).forGetter(SpellAction.GiveItem::count))
            .apply(instance, SpellAction.GiveItem::new));

    /** A bolt's payload is a list of actions, so its codec is built from the recursive one it is made of. */
    private static Codec<SpellAction> actions(Codec<SpellAction> self) {
        return Kind.CODEC.dispatch("type", SpellCodecs::kindOf, kind -> codecFor(kind, self));
    }

    /** What an action is, as data names it. */
    private static Kind kindOf(SpellAction action) {
        if (action instanceof SpellAction.ApplyEffect) return Kind.APPLY_EFFECT;
        if (action instanceof SpellAction.Damage) return Kind.DAMAGE;
        if (action instanceof SpellAction.Ignite) return Kind.IGNITE_AREA;
        if (action instanceof SpellAction.Freeze) return Kind.FREEZE_AREA;
        if (action instanceof SpellAction.Corrupt) return Kind.CORRUPT_AREA;
        if (action instanceof SpellAction.Summon) return Kind.SUMMON;
        if (action instanceof SpellAction.Lightning) return Kind.LIGHTNING;
        if (action instanceof SpellAction.Explode) return Kind.EXPLODE;
        if (action instanceof SpellAction.Teleport) return Kind.TELEPORT;
        if (action instanceof SpellAction.Weather) return Kind.WEATHER;
        if (action instanceof SpellAction.Pacify) return Kind.PACIFY;
        if (action instanceof SpellAction.GiveItem) return Kind.GIVE_ITEM;
        return Kind.LAUNCH_BOLT;
    }

    /** How an action of this kind is read; a bolt's "on_hit" holds more actions. */
    private static MapCodec<? extends SpellAction> codecFor(Kind kind, Codec<SpellAction> action) {
        return switch (kind) {
            case APPLY_EFFECT -> APPLY_EFFECT;
            case DAMAGE -> DAMAGE;
            case IGNITE_AREA -> IGNITE_AREA;
            case FREEZE_AREA -> FREEZE_AREA;
            case CORRUPT_AREA -> CORRUPT_AREA;
            case SUMMON -> SUMMON;
            case LIGHTNING -> LIGHTNING;
            case EXPLODE -> EXPLODE;
            case TELEPORT -> TELEPORT;
            case WEATHER -> WEATHER;
            case PACIFY -> PACIFY;
            case GIVE_ITEM -> GIVE_ITEM;
            case LAUNCH_BOLT -> RecordCodecBuilder.<SpellAction.LaunchBolt>mapCodec(instance -> instance.group(
                    ID.optionalFieldOf("entity", "corruption:spell_bolt").forGetter(SpellAction.LaunchBolt::entity),
                    Codec.DOUBLE.optionalFieldOf("speed", 1.0).forGetter(SpellAction.LaunchBolt::speed),
                    Codec.FLOAT.optionalFieldOf("inaccuracy", 0.0F).forGetter(SpellAction.LaunchBolt::inaccuracy),
                    action.listOf().optionalFieldOf("on_hit", List.<SpellAction>of()).forGetter(SpellAction.LaunchBolt::onHit),
                    ID.optionalFieldOf("look", "").forGetter(SpellAction.LaunchBolt::look))
                    .apply(instance, SpellAction.LaunchBolt::new));
        };
    }

    private SpellCodecs() {}
}
