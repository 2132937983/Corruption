package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionApi;
import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.block.StaticCorruptedBlock;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.callback.AddCallback;

@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class GeneratedCorruption {
    private static final Map<Block, StaticCorruptedBlock> BLOCKS = new IdentityHashMap<>();
    private static final Map<BlockState, BlockState> STATES = new IdentityHashMap<>();

    @SubscribeEvent public static void callbacks(ModifyRegistriesEvent event) {
        event.getRegistry(Registries.BLOCK).addCallback((AddCallback<Block>) (registry, id, key, block) -> register(block));
    }
    @SubscribeEvent(priority = EventPriority.LOWEST) public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            // Snapshot vanilla/earlier mod entries; the callback covers all later mod registrations.
            for (Block source : BuiltInRegistries.BLOCK.stream().toList()) register(source);
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            BLOCKS.values().stream().sorted(java.util.Comparator.comparing(block -> BuiltInRegistries.BLOCK.getKey(block).toString())).forEach(block ->
                    Registry.register(BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK.getKey(block), new BlockItem(block, new Item.Properties()) {
                        @Override public Component getName(ItemStack stack) { return block.getName(); }
                    }));
        }
    }
    private static void register(Block source) {
        if (source instanceof CorruptionBlock || source instanceof net.minecraft.world.level.block.LiquidBlock || BLOCKS.containsKey(source)
                || source.defaultBlockState().isAir() || source.defaultBlockState().getRenderShape() == RenderShape.INVISIBLE && !(source instanceof EntityBlock)) return;
        StaticCorruptedBlock corrupted = StaticCorruptedBlock.create(source);
        BLOCKS.put(source, corrupted);
        for (BlockState state : corrupted.getStateDefinition().getPossibleStates()) STATES.put(corrupted.source(state), state);
        Registry.register(BuiltInRegistries.BLOCK, CorruptionApi.variantId(source.defaultBlockState()), corrupted);
    }
    public static BlockState convert(BlockState original) { return STATES.get(original); }
    public static java.util.Collection<StaticCorruptedBlock> blocks() { return Collections.unmodifiableCollection(BLOCKS.values()); }
    public static ItemStack stack(BlockState corrupted) {
        ItemStack stack = new ItemStack(corrupted.getBlock());
        Map<String, String> properties = new LinkedHashMap<>();
        for (Property<?> property : corrupted.getProperties()) properties.put(property.getName(), propertyName(corrupted, property));
        if (!properties.isEmpty()) stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(properties));
        return stack;
    }
    private static <T extends Comparable<T>> String propertyName(BlockState state, Property<T> property) { return property.getName(state.getValue(property)); }
    private GeneratedCorruption() {}
}
