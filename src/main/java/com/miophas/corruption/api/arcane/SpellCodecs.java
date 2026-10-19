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
import java.util.Locale;
import java.util.Optional;
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
 * including another bolt. How the mod's own bolt shows itself is written beside it: "look", the item it is
 * drawn as; "trail", the {@link #PARTICLES particles} it gives off in flight (the corruption's motes if left
 * out, none for an empty list); "burst", those that burst from where it strikes:
 * <pre>{@code
 * { "type": "launch_bolt", "look": "minecraft:fire_charge",
 *   "trail": [ { "particle": "minecraft:flame", "count": 2, "spread": 0.08 }, { "particle": "minecraft:smoke" } ],
 *   "burst": [ { "particle": "minecraft:flame", "count": 24, "spread": 0.5, "speed": 0.05 } ],
 *   "on_hit": [ { "type": "ignite_area", "radius": 3, "seconds": 5 } ] }
 * }</pre>
 * A volley is "count" bolts, fanned across "fan" degrees; "from": "sky" drops them from "height" blocks over the
 * origin instead, scattered "fan" blocks round it. "lightning" may be "corrupt" (the corruption's violet bolt) and
 * hurt where it falls ("damage", "damage_type"); "push" thrusts away or draws in; "particles" only shows, drawing
 * particles in a "shape" about the origin; "visual" only shows too, an effect drawn on the client under the origin:
 * <pre>{"type": "visual", "effect": "corruption:magic_circle", "radius": 5, "ticks": 60, "color": "#9B4562",
 *  "accent": "#FFD24A", "shake": 0.3, "sound": "minecraft:entity.evoker.prepare_summon"}</pre> A "beam" fires from
 * the caster's hands along their gaze and follows it, for "ticks", "length" blocks or to the first block in its way;
 * every "interval" ticks it does its "on_hit" at each creature within "width" blocks of it, and it is drawn, in
 * "color" and "accent", for exactly as long as it fires:
 * <pre>{"type": "beam", "length": 64, "width": 2, "ticks": 60, "interval": 5, "color": "#FFD24A", "accent": "#FFFFFF",
 *  "on_hit": [{"type": "damage", "amount": 24, "radius": 3}]}</pre> The mod's own bolt, drawn as its own (no
 * "look"), shines in its "color" and is as big as its "size", its box and its look, in blocks (0.5 unless written).
 * "corrupt_area" may be "inert", leaving only the dormant corruption that spreads no further; "quell_corruption"
 * stills the corruption already there; "raise_remnants" calls up remnants to serve the caster; "mark_prey" sends the
 * corruption's creatures, its bosses among them, after the creature a bolt struck; "truce" has them spare the caster:
 * <pre>{"type": "mark_prey", "radius": 24, "ticks": 600}</pre> Every field but the ones naming an effect, a
 * creature or an item may be left out; what it falls back to is written beside each kind below.
 */
public final class SpellCodecs {
    /** An id as data writes it, "corruption:spell_bolt", kept as the plain string the rules speak in. */
    private static final Codec<String> ID = ResourceLocation.CODEC.xmap(ResourceLocation::toString, ResourceLocation::parse);
    /**
     * How far round its origin an action reaches: refused past {@link SpellAction#MAX_RADIUS} as the data loads,
     * since the spell reaches no further than that, so that no radius is ever quietly cut short.
     */
    private static final Codec<Double> RADIUS = Codec.doubleRange(0, SpellAction.MAX_RADIUS);
    /** How far round its origin corrupting reaches: refused past {@link SpellAction#MAX_CORRUPTION_RADIUS}. */
    private static final Codec<Double> CORRUPTION_RADIUS = Codec.doubleRange(0, SpellAction.MAX_CORRUPTION_RADIUS);
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

    /** Where a bolt sets off from: "caster" or "sky". */
    private static final Codec<SpellAction.Source> SOURCE = Codec.STRING.comapFlatMap(text -> switch (text) {
        case "caster" -> DataResult.success(SpellAction.Source.CASTER);
        case "sky" -> DataResult.success(SpellAction.Source.SKY);
        default -> DataResult.error(() -> "A bolt sets off from the caster or the sky, not " + text);
    }, source -> source == SpellAction.Source.SKY ? "sky" : "caster");
    /** The shape particles are drawn in: "burst", "ring", "sphere", "column" or "spiral". */
    private static final Codec<SpellAction.Shape> SHAPE = Codec.STRING.comapFlatMap(text -> {
        for (SpellAction.Shape shape : SpellAction.Shape.values())
            if (shape.name().toLowerCase(Locale.ROOT).equals(text)) return DataResult.success(shape);
        return DataResult.error(() -> "Particles are drawn in a burst, ring, sphere, column or spiral, not " + text);
    }, shape -> shape.name().toLowerCase(Locale.ROOT));

    /** A colour as data writes it: "#RRGGBB". */
    private static final Codec<Integer> COLOUR = Codec.STRING.comapFlatMap(text -> {
        String hex = text.startsWith("#") ? text.substring(1) : text;
        if (hex.length() != 6) return DataResult.error(() -> "A colour is written #RRGGBB, not " + text);
        try { return DataResult.success(Integer.parseInt(hex, 16)); }
        catch (NumberFormatException notHex) { return DataResult.error(() -> "A colour is written #RRGGBB, not " + text); }
    }, colour -> String.format(Locale.ROOT, "#%06X", colour));

    /**
     * Particles a bolt gives off: {@code {"particle": "minecraft:flame", "count": 2, "spread": 0.1, "speed": 0.01}},
     * and for a particle that takes a colour, "minecraft:dust" say, {@code "color": "#B05AA8"}. All but the
     * particle may be left out: one at a time, all at the one spot, standing still, of no colour of its own.
     */
    public static final Codec<SpellAction.Particles> PARTICLES = RecordCodecBuilder.create(instance -> instance.group(
            ID.fieldOf("particle").forGetter(SpellAction.Particles::particle),
            Codec.intRange(0, 64).optionalFieldOf("count", 1).forGetter(SpellAction.Particles::count),
            Codec.doubleRange(0, 4).optionalFieldOf("spread", 0.0).forGetter(SpellAction.Particles::spread),
            Codec.doubleRange(0, 2).optionalFieldOf("speed", 0.0).forGetter(SpellAction.Particles::speed),
            COLOUR.optionalFieldOf("color").forGetter(particles -> particles.colour() < 0 ? Optional.empty() : Optional.of(particles.colour())))
            .apply(instance, (particle, count, spread, speed, colour) -> new SpellAction.Particles(particle, count, spread, speed, colour.orElse(-1))));
    public static final Codec<List<SpellAction.Particles>> PARTICLE_LIST = PARTICLES.listOf();

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
        CUSTOM("custom"), APPLY_EFFECT("apply_effect"), DAMAGE("damage"), IGNITE_AREA("ignite_area"), FREEZE_AREA("freeze_area"),
        CORRUPT_AREA("corrupt_area"), CORRUPT_CREATURES("corrupt_creatures"), SUMMON("summon"), LIGHTNING("lightning"), EXPLODE("explode"),
        TELEPORT("teleport"), WEATHER("weather"), PACIFY("pacify"), GIVE_ITEM("give_item"), PUSH("push"), PARTICLES("particles"),
        VISUAL("visual"), LAUNCH_BOLT("launch_bolt"), BEAM("beam"), QUELL_CORRUPTION("quell_corruption"), RAISE_REMNANTS("raise_remnants"),
        MARK_PREY("mark_prey"), TRUCE("truce");

        static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);

        private final String name;
        Kind(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }

    private static final MapCodec<SpellAction.ApplyEffect> APPLY_EFFECT = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("effect").forGetter(SpellAction.ApplyEffect::effect),
            TARGET.optionalFieldOf("target", SpellAction.Target.SELF).forGetter(SpellAction.ApplyEffect::target),
            RADIUS.optionalFieldOf("radius", 0.0).forGetter(SpellAction.ApplyEffect::radius),
            Codec.intRange(1, 20 * 60 * 10).optionalFieldOf("duration", 200).forGetter(SpellAction.ApplyEffect::duration),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(SpellAction.ApplyEffect::amplifier))
            .apply(instance, SpellAction.ApplyEffect::new));
    private static final MapCodec<SpellAction.Damage> DAMAGE = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("amount", 4.0).forGetter(SpellAction.Damage::amount),
            RADIUS.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Damage::radius),
            ID.optionalFieldOf("damage_type", "").forGetter(SpellAction.Damage::type))
            .apply(instance, SpellAction.Damage::new));
    private static final MapCodec<SpellAction.Ignite> IGNITE_AREA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Ignite::radius),
            Codec.intRange(1, 20 * 60 * 5).optionalFieldOf("seconds", 8).forGetter(SpellAction.Ignite::seconds))
            .apply(instance, SpellAction.Ignite::new));
    private static final MapCodec<SpellAction.Freeze> FREEZE_AREA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Freeze::radius),
            Codec.intRange(1, 20 * 60).optionalFieldOf("ticks", 200).forGetter(SpellAction.Freeze::ticks))
            .apply(instance, SpellAction.Freeze::new));
    private static final MapCodec<SpellAction.Corrupt> CORRUPT_AREA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CORRUPTION_RADIUS.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Corrupt::radius),
            Codec.DOUBLE.optionalFieldOf("chance", 0.25).forGetter(SpellAction.Corrupt::chance),
            Codec.BOOL.optionalFieldOf("inert", false).forGetter(SpellAction.Corrupt::inert))
            .apply(instance, SpellAction.Corrupt::new));
    private static final MapCodec<SpellAction.Quell> QUELL_CORRUPTION = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CORRUPTION_RADIUS.optionalFieldOf("radius", 6.0).forGetter(SpellAction.Quell::radius))
            .apply(instance, SpellAction.Quell::new));
    private static final MapCodec<SpellAction.RaiseRemnants> RAISE_REMNANTS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 16).optionalFieldOf("count", 4).forGetter(SpellAction.RaiseRemnants::count),
            Codec.doubleRange(0, 16).optionalFieldOf("radius", 3.0).forGetter(SpellAction.RaiseRemnants::radius),
            ID.listOf().optionalFieldOf("likenesses", SpellAction.RaiseRemnants.LIKENESSES).forGetter(SpellAction.RaiseRemnants::likenesses),
            Codec.intRange(0, 20 * 60 * 10).optionalFieldOf("lifetime", 1200).forGetter(SpellAction.RaiseRemnants::lifetime))
            .apply(instance, SpellAction.RaiseRemnants::new));
    private static final MapCodec<SpellAction.MarkPrey> MARK_PREY = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 24.0).forGetter(SpellAction.MarkPrey::radius),
            Codec.intRange(1, 20 * 60 * 5).optionalFieldOf("ticks", 600).forGetter(SpellAction.MarkPrey::ticks))
            .apply(instance, SpellAction.MarkPrey::new));
    private static final MapCodec<SpellAction.Truce> TRUCE = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 16.0).forGetter(SpellAction.Truce::radius),
            Codec.intRange(1, 20 * 60 * 10).optionalFieldOf("ticks", 1200).forGetter(SpellAction.Truce::ticks))
            .apply(instance, SpellAction.Truce::new));
    private static final MapCodec<SpellAction.CorruptCreatures> CORRUPT_CREATURES = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 4.0).forGetter(SpellAction.CorruptCreatures::radius),
            Codec.doubleRange(0, 1).optionalFieldOf("chance", 1.0).forGetter(SpellAction.CorruptCreatures::chance))
            .apply(instance, SpellAction.CorruptCreatures::new));
    private static final MapCodec<SpellAction.Summon> SUMMON = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("entity").forGetter(SpellAction.Summon::entity),
            Codec.intRange(1, 16).optionalFieldOf("count", 1).forGetter(SpellAction.Summon::count))
            .apply(instance, SpellAction.Summon::new));
    private static final MapCodec<SpellAction.Lightning> LIGHTNING = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 4.0).forGetter(SpellAction.Lightning::radius),
            Codec.intRange(1, 128).optionalFieldOf("count", 1).forGetter(SpellAction.Lightning::count),
            Codec.BOOL.optionalFieldOf("corrupt", false).forGetter(SpellAction.Lightning::corrupt),
            Codec.doubleRange(0, 1000).optionalFieldOf("damage", 0.0).forGetter(SpellAction.Lightning::damage),
            ID.optionalFieldOf("damage_type", "").forGetter(SpellAction.Lightning::damageType))
            .apply(instance, SpellAction.Lightning::new));
    private static final MapCodec<SpellAction.Explode> EXPLODE = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("power", 2.0).forGetter(SpellAction.Explode::power),
            Codec.BOOL.optionalFieldOf("breaks_blocks", false).forGetter(SpellAction.Explode::breaksBlocks),
            RADIUS.optionalFieldOf("break_radius", SpellAction.Explode.ANY_REACH).forGetter(SpellAction.Explode::breakRadius),
            Codec.intRange(0, SpellAction.Explode.ANY_COUNT).optionalFieldOf("max_blocks", SpellAction.Explode.ANY_COUNT).forGetter(SpellAction.Explode::maxBlocks),
            Codec.doubleRange(0, SpellAction.Explode.ANY_RESISTANCE).optionalFieldOf("max_resistance", SpellAction.Explode.ANY_RESISTANCE)
                    .forGetter(SpellAction.Explode::maxResistance),
            Codec.doubleRange(0, 1000).optionalFieldOf("crush_hardness")
                    .forGetter(explode -> explode.crushHardness() < 0 ? Optional.empty() : Optional.of(explode.crushHardness())))
            .apply(instance, (power, breaks, radius, most, resistance, crush) -> new SpellAction.Explode(power, breaks, radius, most, resistance,
                    crush.orElse(SpellAction.Explode.NO_CRUSH))));
    private static final MapCodec<SpellAction.Teleport> TELEPORT = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("distance", 8.0).forGetter(SpellAction.Teleport::distance))
            .apply(instance, SpellAction.Teleport::new));
    private static final MapCodec<SpellAction.Weather> WEATHER = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SKY.optionalFieldOf("sky", SpellAction.Sky.CLEAR).forGetter(SpellAction.Weather::sky),
            Codec.intRange(1, 20 * 60 * 60).optionalFieldOf("ticks", 6000).forGetter(SpellAction.Weather::ticks))
            .apply(instance, SpellAction.Weather::new));
    private static final MapCodec<SpellAction.Pacify> PACIFY = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 8.0).forGetter(SpellAction.Pacify::radius))
            .apply(instance, SpellAction.Pacify::new));
    private static final MapCodec<SpellAction.GiveItem> GIVE_ITEM = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("item").forGetter(SpellAction.GiveItem::item),
            Codec.intRange(1, 64).optionalFieldOf("count", 1).forGetter(SpellAction.GiveItem::count))
            .apply(instance, SpellAction.GiveItem::new));

    /** What a push thrusts away from: "origin" or "caster". */
    private static final Codec<SpellAction.Push.From> PUSH_FROM = Codec.STRING.comapFlatMap(text -> switch (text) {
        case "origin" -> DataResult.success(SpellAction.Push.From.ORIGIN);
        case "caster" -> DataResult.success(SpellAction.Push.From.CASTER);
        default -> DataResult.error(() -> "A push is from the origin or the caster, not " + text);
    }, from -> from == SpellAction.Push.From.ORIGIN ? "origin" : "caster");

    private static final MapCodec<SpellAction.Push> PUSH = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RADIUS.optionalFieldOf("radius", 5.0).forGetter(SpellAction.Push::radius),
            Codec.doubleRange(-4, 4).optionalFieldOf("strength", 1.0).forGetter(SpellAction.Push::strength),
            Codec.doubleRange(0, 2).optionalFieldOf("lift", 0.2).forGetter(SpellAction.Push::lift),
            PUSH_FROM.optionalFieldOf("from", SpellAction.Push.From.ORIGIN).forGetter(SpellAction.Push::from))
            .apply(instance, SpellAction.Push::new));
    private static final MapCodec<SpellAction.Spectacle> SPECTACLE = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SHAPE.optionalFieldOf("shape", SpellAction.Shape.BURST).forGetter(SpellAction.Spectacle::shape),
            RADIUS.optionalFieldOf("radius", 2.0).forGetter(SpellAction.Spectacle::radius),
            Codec.doubleRange(0, 128).optionalFieldOf("height", 2.0).forGetter(SpellAction.Spectacle::height),
            PARTICLE_LIST.fieldOf("particles").forGetter(SpellAction.Spectacle::particles))
            .apply(instance, SpellAction.Spectacle::new));

    private static final MapCodec<SpellAction.Visual> VISUAL = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ID.fieldOf("effect").forGetter(SpellAction.Visual::effect),
            RADIUS.optionalFieldOf("radius", 3.0).forGetter(SpellAction.Visual::radius),
            Codec.doubleRange(0, 64).optionalFieldOf("height", 3.0).forGetter(SpellAction.Visual::height),
            Codec.intRange(1, 400).optionalFieldOf("ticks", 40).forGetter(SpellAction.Visual::ticks),
            COLOUR.optionalFieldOf("color", SpellAction.Visual.ROSE).forGetter(SpellAction.Visual::colour),
            COLOUR.optionalFieldOf("accent", SpellAction.Visual.GOLD).forGetter(SpellAction.Visual::accent),
            Codec.doubleRange(0, 1).optionalFieldOf("shake", 0.0).forGetter(SpellAction.Visual::shake),
            ID.optionalFieldOf("sound", "").forGetter(SpellAction.Visual::sound))
            .apply(instance, SpellAction.Visual::new));

    /** A bolt's payload is a list of actions, so its codec is built from the recursive one it is made of. */
    private static Codec<SpellAction> actions(Codec<SpellAction> self) {
        return Kind.CODEC.dispatch("type", SpellCodecs::kindOf, kind -> codecFor(kind, self));
    }

    /** What an action is, as data names it. */
    private static Kind kindOf(SpellAction action) {
        if (action instanceof SpellAction.Custom) return Kind.CUSTOM;
        if (action instanceof SpellAction.ApplyEffect) return Kind.APPLY_EFFECT;
        if (action instanceof SpellAction.Damage) return Kind.DAMAGE;
        if (action instanceof SpellAction.Ignite) return Kind.IGNITE_AREA;
        if (action instanceof SpellAction.Freeze) return Kind.FREEZE_AREA;
        if (action instanceof SpellAction.Corrupt) return Kind.CORRUPT_AREA;
        if (action instanceof SpellAction.CorruptCreatures) return Kind.CORRUPT_CREATURES;
        if (action instanceof SpellAction.Summon) return Kind.SUMMON;
        if (action instanceof SpellAction.Lightning) return Kind.LIGHTNING;
        if (action instanceof SpellAction.Explode) return Kind.EXPLODE;
        if (action instanceof SpellAction.Teleport) return Kind.TELEPORT;
        if (action instanceof SpellAction.Weather) return Kind.WEATHER;
        if (action instanceof SpellAction.Pacify) return Kind.PACIFY;
        if (action instanceof SpellAction.GiveItem) return Kind.GIVE_ITEM;
        if (action instanceof SpellAction.Push) return Kind.PUSH;
        if (action instanceof SpellAction.Spectacle) return Kind.PARTICLES;
        if (action instanceof SpellAction.Visual) return Kind.VISUAL;
        if (action instanceof SpellAction.Beam) return Kind.BEAM;
        if (action instanceof SpellAction.Quell) return Kind.QUELL_CORRUPTION;
        if (action instanceof SpellAction.RaiseRemnants) return Kind.RAISE_REMNANTS;
        if (action instanceof SpellAction.MarkPrey) return Kind.MARK_PREY;
        if (action instanceof SpellAction.Truce) return Kind.TRUCE;
        return Kind.LAUNCH_BOLT;
    }

    /** How an action of this kind is read; a bolt's "on_hit" holds more actions. */
    private static MapCodec<? extends SpellAction> codecFor(Kind kind, Codec<SpellAction> action) {
        return switch (kind) {
            case CUSTOM -> RecordCodecBuilder.<SpellAction.Custom>mapCodec(instance -> instance.group(
                    ID.fieldOf("id").forGetter(SpellAction.Custom::id),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("parameters", java.util.Map.of()).forGetter(SpellAction.Custom::parameters)
                    ).apply(instance, SpellAction.Custom::new));
            case APPLY_EFFECT -> APPLY_EFFECT;
            case DAMAGE -> DAMAGE;
            case IGNITE_AREA -> IGNITE_AREA;
            case FREEZE_AREA -> FREEZE_AREA;
            case CORRUPT_AREA -> CORRUPT_AREA;
            case CORRUPT_CREATURES -> CORRUPT_CREATURES;
            case SUMMON -> SUMMON;
            case LIGHTNING -> LIGHTNING;
            case EXPLODE -> EXPLODE;
            case TELEPORT -> TELEPORT;
            case WEATHER -> WEATHER;
            case PACIFY -> PACIFY;
            case GIVE_ITEM -> GIVE_ITEM;
            case PUSH -> PUSH;
            case PARTICLES -> SPECTACLE;
            case VISUAL -> VISUAL;
            case QUELL_CORRUPTION -> QUELL_CORRUPTION;
            case RAISE_REMNANTS -> RAISE_REMNANTS;
            case MARK_PREY -> MARK_PREY;
            case TRUCE -> TRUCE;
            case LAUNCH_BOLT -> RecordCodecBuilder.<SpellAction.LaunchBolt>mapCodec(instance -> instance.group(
                    ID.optionalFieldOf("entity", "corruption:spell_bolt").forGetter(SpellAction.LaunchBolt::entity),
                    Codec.DOUBLE.optionalFieldOf("speed", 1.0).forGetter(SpellAction.LaunchBolt::speed),
                    Codec.FLOAT.optionalFieldOf("inaccuracy", 0.0F).forGetter(SpellAction.LaunchBolt::inaccuracy),
                    action.listOf().optionalFieldOf("on_hit", List.<SpellAction>of()).forGetter(SpellAction.LaunchBolt::onHit),
                    ID.optionalFieldOf("look", "").forGetter(SpellAction.LaunchBolt::look),
                    PARTICLE_LIST.optionalFieldOf("trail", SpellAction.LaunchBolt.CORRUPTION_TRAIL).forGetter(SpellAction.LaunchBolt::trail),
                    PARTICLE_LIST.optionalFieldOf("burst", List.of()).forGetter(SpellAction.LaunchBolt::burst),
                    Codec.intRange(1, SpellAction.LaunchBolt.MAX_COUNT).optionalFieldOf("count", 1).forGetter(SpellAction.LaunchBolt::count),
                    Codec.doubleRange(0, 360).optionalFieldOf("fan", 0.0).forGetter(SpellAction.LaunchBolt::fan),
                    SOURCE.optionalFieldOf("from", SpellAction.Source.CASTER).forGetter(SpellAction.LaunchBolt::from),
                    Codec.doubleRange(0, 64).optionalFieldOf("height", SpellAction.LaunchBolt.SKY_HEIGHT).forGetter(SpellAction.LaunchBolt::height),
                    COLOUR.optionalFieldOf("color").forGetter(bolt -> bolt.colour() < 0 ? Optional.empty() : Optional.of(bolt.colour())),
                    Codec.doubleRange(SpellAction.LaunchBolt.MIN_SIZE, SpellAction.LaunchBolt.MAX_SIZE).optionalFieldOf("size", SpellAction.LaunchBolt.DEFAULT_SIZE)
                            .forGetter(SpellAction.LaunchBolt::size))
                    .apply(instance, (entity, speed, inaccuracy, onHit, look, trail, burst, count, fan, from, height, colour, size) -> new SpellAction.LaunchBolt(
                            entity, speed, inaccuracy, onHit, look, trail, burst, count, fan, from, height, colour.orElse(-1), size)));
            case BEAM -> RecordCodecBuilder.<SpellAction.Beam>mapCodec(instance -> instance.group(
                    Codec.doubleRange(1, SpellAction.MAX_RADIUS).optionalFieldOf("length", 32.0).forGetter(SpellAction.Beam::length),
                    Codec.doubleRange(.1, 16).optionalFieldOf("width", 1.0).forGetter(SpellAction.Beam::width),
                    Codec.intRange(1, 400).optionalFieldOf("ticks", 60).forGetter(SpellAction.Beam::ticks),
                    Codec.intRange(1, 100).optionalFieldOf("interval", 5).forGetter(SpellAction.Beam::interval),
                    action.listOf().optionalFieldOf("on_hit", List.<SpellAction>of()).forGetter(SpellAction.Beam::onHit),
                    COLOUR.optionalFieldOf("color", SpellAction.Visual.ROSE).forGetter(SpellAction.Beam::colour),
                    COLOUR.optionalFieldOf("accent", SpellAction.Visual.GOLD).forGetter(SpellAction.Beam::accent),
                    Codec.doubleRange(0, 1).optionalFieldOf("shake", 0.0).forGetter(SpellAction.Beam::shake),
                    ID.optionalFieldOf("sound", "").forGetter(SpellAction.Beam::sound))
                    .apply(instance, SpellAction.Beam::new));
        };
    }

    private SpellCodecs() {}
}
