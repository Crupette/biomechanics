package com.github.Crupette.biomechanics.block.entity;

import net.minecraft.util.math.BlockPos;

public interface Biological {
    BlockPos getParent();
    void setParent(BlockPos pos);

    int getCalorieCost();
    int getOxygenCost();
}
