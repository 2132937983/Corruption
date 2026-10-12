package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Sound events; the files behind each one are listed in assets/corruption/sounds.json. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Corruption.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> REMNANT_AMBIENT = register("entity.corrupted_remnant.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMNANT_ATTACK = register("entity.corrupted_remnant.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMNANT_STEP = register("entity.corrupted_remnant.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMNANT_HURT = register("entity.corrupted_remnant.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMNANT_DEATH = register("entity.corrupted_remnant.death");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, id)));
    }

    private ModSounds() {}
}
