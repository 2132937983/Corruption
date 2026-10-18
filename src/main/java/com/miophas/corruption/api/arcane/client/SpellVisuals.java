package com.miophas.corruption.api.arcane.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

/**
 * The kinds of spell effect the client can draw, by the id a spell's "visual" action names them by. The mod
 * registers its own ({@link #BUILT_IN}); an add-on registers more the same way, in client setup. An effect no
 * client knows how to draw is simply not drawn.
 */
public final class SpellVisuals {
    /** A magic circle on the ground, its rings of runes turning against each other, an echo of it rising. */
    public static final ResourceLocation MAGIC_CIRCLE = id("magic_circle");
    /** A ring of force racing out over the ground, a wall of light riding on it. */
    public static final ResourceLocation SHOCKWAVE = id("shockwave");
    /** A pillar of light come down from the sky, a bright core inside it. */
    public static final ResourceLocation LIGHT_PILLAR = id("light_pillar");
    /** A whirling disc on the ground, ribbons of light spiralling up out of it into a funnel. */
    public static final ResourceLocation VORTEX = id("vortex");
    /** A dome of hexagons over the ground, springing up and shimmering, as a ward does. */
    public static final ResourceLocation DOME = id("dome");
    /** Glowing runes circling and rising round the spot, two rings of them turning opposite ways. */
    public static final ResourceLocation RUNE_RING = id("rune_ring");
    /** A ring of flame bursting up out of the ground, a lower ring inside it, embers whirling up. */
    public static final ResourceLocation HELLFIRE = id("hellfire");
    /** Great wings of feathers of light spreading behind the caster and beating; its height is where the shoulders are. */
    public static final ResourceLocation WINGS = id("wings");
    /** A storm of feathers bursting out of the spot, swirling, and drifting down. */
    public static final ResourceLocation FEATHERS = id("feathers");
    /** A storm's eye overhead, forked lightning striking the ground round the spot one bolt after another. */
    public static final ResourceLocation LIGHTNING = id("lightning");
    /** Cracks racing out over the ground, molten light in them, shards of the ground flung up. */
    public static final ResourceLocation FISSURE = id("fissure");
    /** Ring after ring of faceted spikes bursting up out of the ground, outward from the middle. */
    public static final ResourceLocation SPIKES = id("spikes");
    /** Swords of light appearing high over the ground, point down, then plunging into it one after another. */
    public static final ResourceLocation BLADES = id("blades");
    /** Souls, wisps and skulls, spiralling up out of a whirl of light on the ground, trailing light. */
    public static final ResourceLocation SOULS = id("souls");
    /** An eye opening in the air, turned to whoever looks, in a triangle of light, a beam of its sight sweeping the ground. */
    public static final ResourceLocation EYE = id("eye");
    /** Stars appearing in a dome overhead, joined by lines of light into a figure, the sky turning. */
    public static final ResourceLocation CONSTELLATION = id("constellation");
    /** A spirit's seal traced stroke by stroke in a ring of runes standing before the caster, the way they faced. */
    public static final ResourceLocation SIGIL = id("sigil");
    /** A wave of water rising in a ring and rushing out, its crest curling over in foam and spray. */
    public static final ResourceLocation TIDAL_WAVE = id("tidal_wave");
    /** A flower of light opening on the ground, light and pollen rising from its heart. */
    public static final ResourceLocation BLOOM = id("bloom");
    /** Chains of light shooting up from round the spot and lashing together over its middle. */
    public static final ResourceLocation CHAINS = id("chains");
    /** Rings of sound pulsing out on the beat, staves of music round them, notes riding and rising off them. */
    public static final ResourceLocation RESONANCE = id("resonance");
    /** A portal of racing sparks spinning open in the air before the caster, space whirling inside it. */
    public static final ResourceLocation RIFT = id("rift");
    /** Billows of glowing, sickly cloud rolling out low over the ground, bubbles rising and bursting. */
    public static final ResourceLocation MIASMA = id("miasma");
    /** A crown of light coming down over the spot, rays of glory round it, gold dust falling. */
    public static final ResourceLocation CROWN = id("crown");
    /** A transmutation circle drawn on the ground, arcs of light leaping over it, the reaction flaring up. */
    public static final ResourceLocation ALCHEMY = id("alchemy");
    /** Mirror shards gathering round the spot, circling, tumbling and glinting, then bursting outward. */
    public static final ResourceLocation SHARDS = id("shards");
    /** Rays of light bursting one after another out of a point high over the spot, turning slowly, as a great creature dies. */
    public static final ResourceLocation RAYS = id("rays");
    /** A star bursting high over the spot: a blinding flash and a cross of light, rings racing out, sparks flung every way. */
    public static final ResourceLocation NOVA = id("nova");
    /** A staff of music racing out over the ground at an even pace, notes riding it and lifting off it: one ring of a crescendo. */
    public static final ResourceLocation CRESCENDO = id("crescendo");
    /**
     * A magic circle of runes opening high over the spot ({@code height} up), lightning crackling round its rim and a mark
     * pulsing on the ground under it; then, after the first third of its life, a beam driven straight down out of it onto the
     * mark, {@code radius} round, lightning coiling down it (SkyBeamRules).
     */
    public static final ResourceLocation SKY_BEAM = id("sky_beam");
    /**
     * Five magic circles opening one after another at the points of a pentagram {@code radius} round the spot, the figure
     * drawn faintly between them, lightning crackling from circle to circle, a mark under each; then each pours a beam down,
     * as wide as the star leaves room for (SkyBeamRules.width), and the star turns and closes in, circles and beams together,
     * to meet over the spot.
     */
    public static final ResourceLocation PENTAGRAM_BEAMS = id("pentagram_beams");
    /** As {@link #PENTAGRAM_BEAMS}, six circles at the points of a hexagram (two triangles). */
    public static final ResourceLocation HEXAGRAM_BEAMS = id("hexagram_beams");
    /** As {@link #PENTAGRAM_BEAMS}, eight circles at the points of an octagram. */
    public static final ResourceLocation OCTAGRAM_BEAMS = id("octagram_beams");
    /** The mod's own, in the order above. */
    public static final List<ResourceLocation> BUILT_IN = List.of(MAGIC_CIRCLE, SHOCKWAVE, LIGHT_PILLAR, VORTEX, DOME, RUNE_RING,
            HELLFIRE, WINGS, FEATHERS, LIGHTNING, FISSURE, SPIKES, BLADES, SOULS, EYE, CONSTELLATION, SIGIL, TIDAL_WAVE, BLOOM, CHAINS, RESONANCE,
            RIFT, MIASMA, CROWN, ALCHEMY, SHARDS, RAYS, NOVA, CRESCENDO, SKY_BEAM, PENTAGRAM_BEAMS, HEXAGRAM_BEAMS, OCTAGRAM_BEAMS);

    private static final Map<ResourceLocation, SpellVisual> VISUALS = new ConcurrentHashMap<>();

    /** Draws effects of this id with this; a second registration of the same id replaces the first. */
    public static void register(ResourceLocation id, SpellVisual visual) { VISUALS.put(id, visual); }

    /** How effects of this id are drawn, or null if no one has said. */
    public static SpellVisual get(ResourceLocation id) { return id == null ? null : VISUALS.get(id); }

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("corruption", path); }

    private SpellVisuals() {}
}
