package org.jahdoo.block.wandBlockManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.Nullable;

public class WandManagerTableBlock extends BaseEntityBlock {

    public static VoxelShape TOP_SHAPE_NS = Block.box(0, 13.5, 1, 16, 16, 14.999999999999998);
    public static VoxelShape TOP_SHAPE_EW = Block.box(0.9999999999999991, 13.5, -8.881784197001252e-16, 15, 16, 16);

    public static VoxelShape SHAPE_2 = Block.box(4.5, 0.125, 4.5, 11.5, 14.925, 11.5);
    public static VoxelShape SHAPE_3 = Block.box(3, 0, 3, 13, 3, 13);

    public static final VoxelShape SHAPE_COMMON = Shapes.or(TOP_SHAPE_NS, SHAPE_2, SHAPE_3);
    public static final VoxelShape SHAPE_COMMON_2 = Shapes.or(TOP_SHAPE_EW, SHAPE_2, SHAPE_3);

    public static final DirectionProperty FACING = DirectionalBlock.FACING;


    public WandManagerTableBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getClockWise());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        if(facing == Direction.NORTH || facing == Direction.SOUTH) {
            return SHAPE_COMMON_2;
        }
        return SHAPE_COMMON;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof WandManagerTableEntity) {
                ((WandManagerTableEntity) blockEntity).dropsAllInventory(pLevel);
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if(!(pLevel.getBlockEntity(pPos) instanceof WandManagerTableEntity wandManager)) return ItemInteractionResult.FAIL;

        this.rotate(pState, Rotation.CLOCKWISE_180);


        ItemStack stack = pPlayer.getItemInHand(pHand);

        if(!stack.isEmpty()){
            for (int i = 0; i < wandManager.inputItemHandler.getSlots(); i++) {
                ItemStack itemStack = wandManager.inputItemHandler.getStackInSlot(i);
                if (itemStack.isEmpty()) {
                    wandManager.inputItemHandler.setStackInSlot(i, stack.copyWithCount(1));
                    if (!pPlayer.isCreative()) stack.shrink(1);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        } else {
            for(int i = 0; i < wandManager.inputItemHandler.getSlots(); i++){
                ItemStack itemStack1 = wandManager.inputItemHandler.getStackInSlot(wandManager.inputItemHandler .getSlots() - (i+1));
                if(!itemStack1.isEmpty()) {
                    pPlayer.setItemInHand(pPlayer.getUsedItemHand(), itemStack1.copy());
                    itemStack1.shrink(1);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WandManagerTableEntity(pPos,pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}

