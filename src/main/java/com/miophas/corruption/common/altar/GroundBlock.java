package com.miophas.corruption.common.altar;

import com.miophas.corruption.core.altar.AltarLayout;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

/**
 * A block a rite asks for on the altar's ground: where, in blocks east and north of the offering on its level
 * as though the altar faced north, turned with it otherwise (from -4 to 4, not the offering; nor one of the
 * pillars, which the rite checks, knowing its altar's shape), and what, a block or any block of a tag:
 * <pre>{"at": [1, 1], "block": "#minecraft:candles"}</pre>
 */
public record GroundBlock(int east, int north, Either<TagKey<Block>, Block> block) {
    private static final Codec<Either<TagKey<Block>, Block>> BLOCK = Codec.either(TagKey.hashedCodec(Registries.BLOCK), BuiltInRegistries.BLOCK.byNameCodec());
    private static final Codec<List<Integer>> AT = Codec.INT.listOf(2, 2);
    public static final Codec<GroundBlock> CODEC = RecordCodecBuilder.<GroundBlock>create(instance -> instance.group(
            AT.fieldOf("at").forGetter(ground -> List.of(ground.east(), ground.north())),
            BLOCK.fieldOf("block").forGetter(GroundBlock::block)).apply(instance, (at, block) -> new GroundBlock(at.get(0), at.get(1), block)))
            .validate(ground -> !AltarLayout.onGround(ground.east(), ground.north())
                    ? DataResult.error(() -> "The altar's ground runs from -" + AltarLayout.REACH + " to " + AltarLayout.REACH + ", not " + ground.east() + ", " + ground.north())
                    : ground.east() == 0 && ground.north() == 0
                    ? DataResult.error(() -> "The offering itself stands at 0, 0")
                    : DataResult.success(ground));
    private static final StreamCodec<ByteBuf, TagKey<Block>> TAG = ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(Registries.BLOCK, id), TagKey::location);
    public static final StreamCodec<RegistryFriendlyByteBuf, GroundBlock> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GroundBlock::east, ByteBufCodecs.VAR_INT, GroundBlock::north,
            ByteBufCodecs.either(TAG, ByteBufCodecs.registry(Registries.BLOCK)), GroundBlock::block, GroundBlock::new);

    public boolean test(BlockState state) { return block.map(tag -> state.is(tag), one -> state.is(one)); }

    /** What it asks for, by name: the block's, or the tag's, or failing that the tag's id. */
    public Component name() {
        return block.map(tag -> Component.translatableWithFallback(Tags.getTagTranslationKey(tag), "#" + tag.location()), Block::getName);
    }

    /** What it asks for, as items to show: the block, or each block of the tag that has an item. */
    public List<ItemStack> shown() {
        return block.map(tag -> BuiltInRegistries.BLOCK.getTag(tag).map(set -> set.stream().map(Holder::value).toList()).orElse(List.of()), List::of)
                .stream().map(Block::asItem).filter(item -> item != Items.AIR).map(ItemStack::new).toList();
    }
}
