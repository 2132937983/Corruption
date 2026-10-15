package com.miophas.corruption.api.arcane.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

/**
 * The kinds of spell effect the client can draw, by the id a spell's "visual" action names them by. The mod
 * registers its own six; an add-on registers more the same way, in client setup. An effect no client knows how to
 * draw is simply not drawn.
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
    /** The mod's own, in the order above. */
    public static final List<ResourceLocation> BUILT_IN = List.of(MAGIC_CIRCLE, SHOCKWAVE, LIGHT_PILLAR, VORTEX, DOME, RUNE_RING);

    private static final Map<ResourceLocation, SpellVisual> VISUALS = new ConcurrentHashMap<>();

    /** Draws effects of this id with this; a second registration of the same id replaces the first. */
    public static void register(ResourceLocation id, SpellVisual visual) { VISUALS.put(id, visual); }

    /** How effects of this id are drawn, or null if no one has said. */
    public static SpellVisual get(ResourceLocation id) { return id == null ? null : VISUALS.get(id); }

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("corruption", path); }

    private SpellVisuals() {}
}
