package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.RemnantCombatApi;
import com.miophas.corruption.api.plant.LivingPlantApi;
import com.miophas.corruption.api.plant.LivingPlantKind;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Corruption.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDragon>> CORRUPTED_DRAGON = TYPES.register("corrupted_dragon",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDragon::new, MobCategory.MONSTER)
                    .sized(16,8).fireImmune().clientTrackingRange(16).updateInterval(3).build("corruption:corrupted_dragon"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDragonFireball>> CORRUPTED_DRAGON_FIREBALL = TYPES.register("corrupted_dragon_fireball",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDragonFireball::new, MobCategory.MISC)
                    .sized(1,1).clientTrackingRange(10).updateInterval(1).build("corruption:corrupted_dragon_fireball"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDragonBreath>> CORRUPTED_DRAGON_BREATH = TYPES.register("corrupted_dragon_breath",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDragonBreath::new, MobCategory.MISC)
                    .sized(6,.5F).fireImmune().clientTrackingRange(10).updateInterval(10).build("corruption:corrupted_dragon_breath"));
    public static CorruptedRemnant createRemnant(net.minecraft.world.level.Level level, net.minecraft.nbt.CompoundTag appearance) {
        return appearance.getString("Type").equals("minecraft:ender_dragon") ? CORRUPTED_DRAGON.get().create(level) : CORRUPTED_REMNANT.get().create(level);
    }
    public static final DeferredHolder<EntityType<?>, EntityType<CorruptedRemnant>> CORRUPTED_REMNANT = TYPES.register("corrupted_remnant",
            () -> EntityType.Builder.of(CorruptedRemnant::new, MobCategory.MONSTER)
                    .sized(RemnantCombatApi.definition().width(), RemnantCombatApi.definition().height())
                    .clientTrackingRange(10).updateInterval(3).build("corruption:corrupted_remnant"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.MaliceVine>> MALICE_VINE = TYPES.register("malice_vine",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.MaliceVine::new, MobCategory.MISC)
                    .sized(LivingPlantApi.definition(LivingPlantKind.MALICE_VINE).width(0), LivingPlantApi.definition(LivingPlantKind.MALICE_VINE).height(0))
                    .clientTrackingRange(10).updateInterval(20).build("corruption:malice_vine"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Greed>> GREED = TYPES.register("greed",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Greed::new, MobCategory.MONSTER)
                    .sized(LivingPlantApi.definition(LivingPlantKind.GREED).width(0), LivingPlantApi.definition(LivingPlantKind.GREED).height(0))
                    .clientTrackingRange(12).updateInterval(10).build("corruption:greed"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.GreedProjectile>> GREED_PROJECTILE = TYPES.register("greed_projectile",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.GreedProjectile::new, MobCategory.MISC)
                    .sized(.7F, .7F).clientTrackingRange(8).updateInterval(2).build("corruption:greed_projectile"));
    /** Its own box is a small stand-in; the hitboxes are its parts. Sent from far off and every tick, so its stride stays in step. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Baal>> BAAL = TYPES.register("baal",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Baal::new, MobCategory.MONSTER)
                    .sized(BaalApi.bodyWidthBlocks(), BaalApi.bodyHeightBlocks()).eyeHeight(BaalApi.eyeHeightBlocks())
                    .fireImmune().clientTrackingRange(32).updateInterval(1).build("corruption:baal"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Agares>> AGARES = TYPES.register("agares",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Agares::new, MobCategory.MONSTER)
                    .sized(AgaresApi.bodyWidthBlocks(), AgaresApi.bodyHeightBlocks()).eyeHeight(AgaresApi.eyeHeightBlocks())
                    .fireImmune().clientTrackingRange(32).updateInterval(1).build("corruption:agares"));

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(CORRUPTED_REMNANT.get(), CorruptedRemnant.attributes().build());
        event.put(CORRUPTED_DRAGON.get(), CorruptedRemnant.attributes().build());
        event.put(MALICE_VINE.get(), com.miophas.corruption.common.entity.MaliceVine.attributes().build());
        event.put(GREED.get(), com.miophas.corruption.common.entity.Greed.attributes().build());
        event.put(BAAL.get(), com.miophas.corruption.common.entity.Baal.attributes().build());
        event.put(AGARES.get(), com.miophas.corruption.common.entity.Agares.attributes().build());
    }

    private ModEntities() {}
}
