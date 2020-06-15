package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.block.entity.BloodVesselBlockEntity;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BloodVesselBlock extends BlockWithEntity {

    public BloodVesselBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BloodVesselBlockEntity();
    }
}
