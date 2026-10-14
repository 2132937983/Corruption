package com.miophas.corruption.common.altar;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

/**
 * What a rite brings forth on the offering: an item, written as any item is,
 * <pre>"result": {"id": "corruption:ancient_corruption_crystal", "count": 1}</pre>
 * or a creature, by its kind:
 * <pre>"result": {"entity": "minecraft:zombified_piglin"}</pre>
 */
public record AltarResult(Optional<ItemStack> item, Optional<EntityType<?>> creature) {
    private static final Codec<EntityType<?>> CREATURE = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(type -> type)).apply(instance, type -> type));
    public static final Codec<AltarResult> CODEC = Codec.either(ItemStack.STRICT_CODEC, CREATURE).xmap(
            either -> either.map(AltarResult::of, AltarResult::of),
            result -> result.item().<Either<ItemStack, EntityType<?>>>map(Either::left).orElseGet(() -> Either.right(result.creature().orElseThrow())));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarResult> STREAM_CODEC = ByteBufCodecs.either(ItemStack.STREAM_CODEC, ByteBufCodecs.registry(Registries.ENTITY_TYPE)).map(
            either -> either.map(AltarResult::of, AltarResult::of),
            result -> result.item().<Either<ItemStack, EntityType<?>>>map(Either::left).orElseGet(() -> Either.right(result.creature().orElseThrow())));

    public static AltarResult of(ItemStack item) { return new AltarResult(Optional.of(item), Optional.empty()); }
    public static AltarResult of(EntityType<?> creature) { return new AltarResult(Optional.empty(), Optional.of(creature)); }
}
