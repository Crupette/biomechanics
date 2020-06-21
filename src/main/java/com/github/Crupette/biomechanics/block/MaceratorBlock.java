package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import com.github.Crupette.biomechanics.block.entity.MaceratorBlockEntity;
import com.github.Crupette.biomechanics.block.entity.MaceratorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MaceratorBlock extends FacingWithEntity{
    protected MaceratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new MaceratorBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof MaceratorBlockEntity){
            MaceratorBlockEntity mblockEntity = (MaceratorBlockEntity)blockEntity;
            BlockPos up = pos.up();
            if(world.getBlockState(up).isSolidBlock(world, up)){
                return ActionResult.success(world.isClient);
            }else{
                ItemStack holdingStack = player.getStackInHand(hand);
                ItemStack maceratorStack = mblockEntity.getStack(0);
                if(holdingStack.isEmpty()){
                    if(player.isSneaking()){
                        player.setStackInHand(hand, mblockEntity.getStack(0).copy());
                        mblockEntity.setStack(0, ItemStack.EMPTY);
                    }
                    return ActionResult.CONSUME;
                }
                if(maceratorStack.isEmpty()){
                    mblockEntity.setStack(0, holdingStack.copy());
                    player.setStackInHand(hand, ItemStack.EMPTY);
                }else{
                    if(maceratorStack.isItemEqual(holdingStack) && maceratorStack.getCount() < maceratorStack.getMaxCount()){
                        int insert = Math.min(maceratorStack.getMaxCount() - maceratorStack.getCount(), holdingStack.getCount());
                        maceratorStack.increment(insert);
                        holdingStack.decrement(insert);
                    }
                }
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.success(world.isClient);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MaceratorBlockEntity) {
                ItemScatterer.spawn(world, pos, (MaceratorBlockEntity)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof MaceratorBlockEntity){
            MaceratorBlockEntity maceratorBlockEntity = (MaceratorBlockEntity)blockEntity;
            if(maceratorBlockEntity.getParent() == null){
                maceratorBlockEntity.setParent(null);
            }else {
                HeartCaseBlockEntity parent = (HeartCaseBlockEntity) world.getBlockEntity(maceratorBlockEntity.getParent());
                if(parent != null) {
                    parent.updateConnectionTree();
                }
            }
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }
}
