package com.miophas.corruption.api.corruption;

/**
 * One brewable potion of the corruption curse.
 *
 * @param id            registry path of the potion, for example {@code long_corruption_curse}
 * @param durationTicks how long the drinkable potion curses for; splash, lingering and arrows scale it as vanilla does
 * @param amplifier     0 is level I; each level adds another share of curse damage per pulse
 * @param brewedFrom    registry id of the potion this one is brewed from
 * @param reagent       registry id of the item added to that potion in a brewing stand
 */
public record CursePotionDefinition(String id, int durationTicks, int amplifier, String brewedFrom, String reagent) {}
