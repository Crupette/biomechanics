package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.block.entity.FatStorageBlockEntity;
import com.github.Crupette.biomechanics.block.entity.PhotosynthesizerBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class FatStorageBlock extends BlockWithEntity {
    public FatStorageBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FatStorageBlockEntity();
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }
}
