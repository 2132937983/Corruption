package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionCurseApi;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Potions of the corruption curse. Registering a potion also provides its splash, lingering and tipped-arrow forms. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, Corruption.MOD_ID);
    /** Base, long and strong, in the order the curse rules define them. */
    public static final List<DeferredHolder<Potion, Potion>> CORRUPTION_CURSE = CorruptionCurseApi.potions().stream()
            .map(definition -> POTIONS.register(definition.id(), () -> new Potion(CorruptionCurseApi.potionName(),
                    new MobEffectInstance(ModEffects.CORRUPTION_CURSE, definition.durationTicks(), definition.amplifier()))))
            .toList();

    @SubscribeEvent public static void brewing(RegisterBrewingRecipesEvent event) {
        var definitions = CorruptionCurseApi.potions();
        for (int index = 0; index < definitions.size(); index++) {
            var definition = definitions.get(index);
            var from = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(definition.brewedFrom()));
            var reagent = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(definition.reagent()));
            if (from.isEmpty() || reagent.isEmpty()) throw new IllegalStateException("Unknown brewing input for potion " + definition.id());
            event.getBuilder().addMix(from.get(), reagent.get(), CORRUPTION_CURSE.get(index));
        }
    }

    private ModPotions() {}
}
