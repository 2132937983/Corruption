package com.miophas.corruption.common.block;

import com.miophas.corruption.common.altar.AltarMode;
import com.miophas.corruption.common.altar.AltarStar;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.common.altar.AltarOfferingBlockEntity;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarState;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Altar Offering: a slab of corruption arcane bricks under a red cloth trimmed in gold
 * (gamemodels/altar/altar_offering.bbmodel), the middle of an altar of six Altar Pillars, or eight (AltarForm).
 * Its state, "altar", is dormant while its pillars do not all stand; formed, its magic circle drawn beneath it,
 * once they do; and binding, holding a creature on it, its model changed to show it, from when they first all
 * stand. "star" is the shape it last found standing, hexagram or octagram, whose circle is drawn. Used by hand,
 * at any time, it looks over its altar at once and, if it stands, starts binding or stops; if it does not, the
 * player is told where its first pillar goes.
 *
 * It faces, "facing", the way the player placing it looked; its first pillar stands that way, the gold mark on
 * its cloth points there, and the whole altar turns with it (see AltarFacing).
 * See {@link AltarOfferingBlockEntity} for its rites.
 */
public final class AltarOfferingBlock extends BaseEntityBlock {
    public static final MapCodec<AltarOfferingBlock> CODEC = simpleCodec(AltarOfferingBlock::new);
    public static final EnumProperty<AltarMode> ALTAR = EnumProperty.create("altar", AltarMode.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    /** The shape of the altar it last found standing round it, which decides the magic circle drawn beneath it. */
    public static final EnumProperty<AltarStar> STAR = EnumProperty.create("star", AltarStar.class);
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 8, 16);

    public AltarOfferingBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ALTAR, AltarMode.DORMANT).setValue(FACING, Direction.NORTH).setValue(STAR, AltarStar.HEXAGRAM));
    }

    /** Which way an offering's altar faces. */
    public static AltarFacing facing(BlockState state) {
        return switch (state.getValue(FACING)) {
            case EAST -> AltarFacing.EAST;
            case SOUTH -> AltarFacing.SOUTH;
            case WEST -> AltarFacing.WEST;
            default -> AltarFacing.NORTH;
        };
    }

    @Override protected MapCodec<AltarOfferingBlock> codec() { return CODEC; }
    /** The shape of an offering's altar, as it last found it standing. */
    public static AltarForm form(BlockState state) { return state.getValue(STAR).form(); }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ALTAR, FACING, STAR); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected boolean useShapeForLightOcclusion(BlockState state) { return true; }

    /** It faces the way the player placing it looks. */
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new AltarOfferingBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ALTAR_OFFERING.get(), AltarOfferingBlockEntity::serverTick);
    }

    /** Used by hand, it starts binding or stops if its altar stands, and says which; if not, it says where the first pillar goes. */
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AltarOfferingBlockEntity offering) {
            AltarState now = offering.use(level);
            level.playSound(null, pos, now.binds() ? SoundEvents.RESPAWN_ANCHOR_CHARGE : SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, .7F, 1.2F);
            Direction first = state.getValue(FACING);
            player.displayClientMessage(now == AltarState.DORMANT
                    ? Component.translatable("corruption.altar.unformed", Component.translatable("corruption.altar.towards." + first.getSerializedName()))
                    : Component.translatable(now.binds() ? "corruption.altar.binding" : "corruption.altar.released"), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
