package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.block.entity.Biological;
import com.github.Crupette.biomechanics.block.entity.BloodVesselBlockEntity;
import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BloodVesselBlock extends BlockWithEntity {

    public BloodVesselBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BloodVesselBlockEntity();
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof Biological){
            Biological biological = (Biological)blockEntity;
            if(biological.getParent() != null){
                BlockEntity heartCase = world.getBlockEntity(biological.getParent());
                if(heartCase != null) {
                    if(heartCase instanceof HeartCaseBlockEntity)
                        ((HeartCaseBlockEntity) world.getBlockEntity(biological.getParent())).updateConnectionTree();
                }else{
                    biological.setParent(null);
                }
            }
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }
}
