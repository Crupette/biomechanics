package com.github.Crupette.biomechanics.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class BloodVesselBlockEntity extends BlockEntity implements Biological {
    public BlockPos parentPos;

    public BloodVesselBlockEntity() {
        super(BiomechanicsBlockEntities.BLOOD_VESSEL);
    }

    @Override
    public BlockPos getParent() {
        return this.parentPos;
    }

    @Override
    public void setParent(BlockPos pos) {
        this.parentPos = pos;
    }

    @Override
    public int getCalorieCost() {
        return 1;
    }

    @Override
    public int getOxygenCost() {
        return 1;
    }
}
