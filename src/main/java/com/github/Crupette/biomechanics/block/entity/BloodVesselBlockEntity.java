package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class BloodVesselBlockEntity extends BlockEntity implements BiologicalNetworked {
    public BlockPos parent;
    private CirculatoryNetwork network;

    public BloodVesselBlockEntity() {
        super(BiomechanicsBlockEntities.BLOOD_VESSEL);
    }

    @Override
    public BlockPos getParent() {
        return this.parent;
    }

    @Override
    public void setParent(BlockPos pos) {
        this.parent = pos;
        if (pos == null) {
            this.network = null;
        }else {
            this.network = ((HeartCaseBlockEntity) this.world.getBlockEntity(pos)).network;
        }
    }

    @Override
    public int getCalorieStorageCapacity() {
        return 256;
    }

    @Override
    public void onBeat() {
        this.network.requestCalories(1);
        this.network.requestOxygen(1);
    }
}
