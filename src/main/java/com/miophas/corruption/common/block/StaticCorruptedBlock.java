package com.miophas.corruption.common.block;

import com.miophas.corruption.common.registry.GeneratedCorruption;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.config.InfectionConfig;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** One registered block per source block, ordinary static states, no block entity. */
public final class StaticCorruptedBlock extends CorruptionBlock implements net.minecraft.world.level.block.SimpleWaterloggedBlock {
    private static final ThreadLocal<Block> CONSTRUCTING = new ThreadLocal<>();
    private final Block original;
    private final Map<BlockState, BlockState> sources = new IdentityHashMap<>();

    public static StaticCorruptedBlock create(Block source) {
        CONSTRUCTING.set(source);
        try { return new StaticCorruptedBlock(properties(source)); }
        finally { CONSTRUCTING.remove(); }
    }

    private static BlockBehaviour.Properties properties(Block source) {
        var original = source.defaultBlockState();
        var properties = BlockBehaviour.Properties.of().strength(2).sound(ModBlocks.INFECTED_SOUND)
                .mapColor(original.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))
                .lightLevel(state -> originalState(source, state).getLightEmission())
                .speedFactor(source.getSpeedFactor())
                .isSuffocating((state, level, pos) -> originalState(source, state).isSuffocating(level, pos))
                .isViewBlocking((state, level, pos) -> originalState(source, state).isViewBlocking(level, pos));
        if (!original.canOcclude()) properties.noOcclusion();
        if (source.hasDynamicShape()) properties.dynamicShape();
        return properties;
    }

    private StaticCorruptedBlock(Properties properties) {
        super(properties);
        original = CONSTRUCTING.get();
        for (BlockState state : stateDefinition.getPossibleStates()) {
            BlockState source = originalState(original, state);
            sources.put(state, source);
            if (source == original.defaultBlockState()) registerDefaultState(state);
        }
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONSTRUCTING.get().getStateDefinition().getProperties().toArray(Property[]::new));
    }

    private static BlockState originalState(Block block, BlockState state) {
        BlockState source = block.defaultBlockState();
        for (Property<?> property : block.getStateDefinition().getProperties()) source = copy(source, state, property);
        return source;
    }
    private static <T extends Comparable<T>> BlockState copy(BlockState target, BlockState from, Property<T> property) { return target.setValue(property, from.getValue(property)); }
    public Block original() { return original; }
    public BlockState source(BlockState state) { return sources.get(state); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return source(state).getShape(level, pos, context); }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return source(state).getCollisionShape(level, pos, context); }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) { return source(state).getOcclusionShape(level, pos); }
    @Override protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) { return source(state).getBlockSupportShape(level, pos); }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return source(state).getVisualShape(level, pos, context); }
    @Override protected boolean useShapeForLightOcclusion(BlockState state) {
        // BlockState computes this flag before our constructor has populated the source map.
        return originalState(original != null ? original : CONSTRUCTING.get(), state).useShapeForLightOcclusion();
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return source(state).getLightBlock(level, pos); }
    @Override protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) { return source(state).getShadeBrightness(level, pos); }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return source(state).propagatesSkylightDown(level, pos); }
    @Override protected FluidState getFluidState(BlockState state) { return source(state).getFluidState(); }
    @Override public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) { return source(state).getLightEmission(level, pos); }
    @Override public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) { return source(state).isLadder(level, pos, entity); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (original instanceof net.minecraft.world.level.block.SlabBlock && context.getLevel().getBlockState(context.getClickedPos()).is(this)) {
            return context.getLevel().getBlockState(context.getClickedPos())
                    .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.DOUBLE)
                    .setValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED, false);
        }
        BlockState placed = original.getStateForPlacement(context);
        if (placed != null && updatesConnections()) {
            var view = OriginalBlockView.of(context.getLevel());
            for(Direction direction:Direction.values()) placed=placed.updateShape(direction,
                    view.getBlockState(context.getClickedPos().relative(direction)),view,context.getClickedPos(),context.getClickedPos().relative(direction));
        }
        return placed == null ? null : GeneratedCorruption.convert(placed);
    }
    @Override protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (!(original instanceof net.minecraft.world.level.block.SlabBlock)) return super.canBeReplaced(state, context);
        var type = state.getValue(net.minecraft.world.level.block.SlabBlock.TYPE);
        if (type == net.minecraft.world.level.block.state.properties.SlabType.DOUBLE || !context.getItemInHand().is(asItem())) return false;
        if (!context.replacingClickedOnBlock()) return true;
        boolean topHalf = context.getClickLocation().y - context.getClickedPos().getY() > .5;
        Direction face = context.getClickedFace();
        return type == net.minecraft.world.level.block.state.properties.SlabType.BOTTOM
                ? face == Direction.UP || topHalf && face.getAxis().isHorizontal()
                : face == Direction.DOWN || !topHalf && face.getAxis().isHorizontal();
    }
    @Override public boolean canPlaceLiquid(net.minecraft.world.entity.player.Player player, BlockGetter level, BlockPos pos, BlockState state, net.minecraft.world.level.material.Fluid fluid) {
        return state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
                && !state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
                && !(original instanceof net.minecraft.world.level.block.SlabBlock && state.getValue(net.minecraft.world.level.block.SlabBlock.TYPE) == net.minecraft.world.level.block.state.properties.SlabType.DOUBLE)
                && fluid == net.minecraft.world.level.material.Fluids.WATER;
    }
    @Override public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        return canPlaceLiquid(null, level, pos, state, fluid.getType()) && net.minecraft.world.level.block.SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluid);
    }
    @Override public ItemStack pickupBlock(net.minecraft.world.entity.player.Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        return state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
                ? net.minecraft.world.level.block.SimpleWaterloggedBlock.super.pickupBlock(player, level, pos, state) : ItemStack.EMPTY;
    }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !(original instanceof LadderBlock) || source(state).canSurvive(level, pos);
    }
    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
                && state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED))
            level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
        if (!(original instanceof LadderBlock) && !updatesConnections()) return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
        BlockState updated = source(state).updateShape(direction, OriginalBlockView.unwrap(neighbor), OriginalBlockView.of(level), pos, neighborPos);
        BlockState corrupted = GeneratedCorruption.convert(updated);
        return corrupted != null ? corrupted : updated;
    }
    private boolean updatesConnections() {
        return original instanceof StairBlock || original instanceof net.minecraft.world.level.block.CrossCollisionBlock
                || original instanceof net.minecraft.world.level.block.WallBlock || original instanceof net.minecraft.world.level.block.DoorBlock
                || original instanceof net.minecraft.world.level.block.BedBlock || original instanceof net.minecraft.world.level.block.DoublePlantBlock;
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return GeneratedCorruption.convert(source(state).rotate(rotation)); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return GeneratedCorruption.convert(source(state).mirror(mirror)); }
    @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return InfectionConfig.corruptedBlocksFlammable() ? 20 : 0;
    }
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return InfectionConfig.corruptedBlocksFlammable() ? 5 : 0;
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(ModItems.corruptionDustDrop(params.getLevel().getRandom()));
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return GeneratedCorruption.stack(state); }
    @Override public MutableComponent getName() { return Component.translatable("block.corruption.infected_block.variant", original.getName()); }
}
