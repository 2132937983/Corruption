package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptionCurseRules;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

/** Read access to the corruption curse rules: its strength, its potions and the fluid that inflicts it. */
public final class CorruptionCurseApi {
    /** Curse duration that never runs out; the same value as Minecraft's infinite effect duration. */
    public static final int ENDLESS = CorruptionCurseRules.ENDLESS;

    /** Base potion first, then its long and strong variants. */
    public static List<CursePotionDefinition> potions() { return CorruptionCurseRules.potions(); }
    /** Shared name of every curse potion, so all variants use one set of translation keys. */
    public static String potionName() { return CorruptionCurseRules.POTION_NAME; }
    public static FlowingCorruptionDefinition flowingCorruption() { return CorruptionCurseRules.flowingCorruption(); }
    /** Multiplier on the curse's damage per pulse for an effect amplifier. */
    public static float damageScale(int amplifier) { return CorruptionCurseRules.damageScale(amplifier); }
    public static boolean fluidCurses(boolean player, boolean creativeOrSpectator, boolean remnant) {
        return CorruptionCurseRules.fluidCurses(player, creativeOrSpectator, remnant);
    }
    /** True when the fluid has nothing to add to a curse the entity already carries. */
    public static boolean fluidCurseHolds(int durationTicks, int amplifier, boolean curable) {
        return CorruptionCurseRules.fluidCurseHolds(durationTicks, amplifier, curable);
    }

    /** The damage type the curse hurts with: the corruption's own, corruption:corruption. */
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("corruption", "corruption"));

    /** The hurt of one pulse of the curse, in this level: the corruption's damage, known for the curse's own ({@link #isCurse}). */
    public static DamageSource damage(Level level) {
        return new CurseDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE));
    }

    /** Whether this hurt is the curse's own pulse, and not the rest of the corruption's damage (its ground, spells and creatures). */
    public static boolean isCurse(DamageSource source) { return source instanceof CurseDamageSource; }
    private CorruptionCurseApi() {}
}
