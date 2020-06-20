package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.block.entity.PhotosynthesizerBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class PhotosynthesizerBlock extends BlockWithEntity {
    public PhotosynthesizerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new PhotosynthesizerBlockEntity();
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
