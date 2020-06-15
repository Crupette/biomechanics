package com.github.Crupette.biomechanics.block.entity;

import net.minecraft.util.math.BlockPos;

public interface Biological {
    BlockPos getParent();

    int getCalorieCost();
    int getOxygenCost();
}
