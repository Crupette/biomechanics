package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class FatStorageBlockEntity extends BlockEntity implements BiologicalNetworked, BlockEntityClientSerializable {
    private BlockPos parent;
    private CirculatoryNetwork network;

    public FatStorageBlockEntity() {
        super(BiomechanicsBlockEntities.FAT_STORAGE);
    }

    @Override
    public BlockPos getParent() {
        return this.parent;
    }

    @Override
    public void setParent(BlockPos pos) {
        this.parent = pos;
        if(pos != null){
            this.network = ((HeartCaseBlockEntity)this.world.getBlockEntity(pos)).network;
        }
    }

    @Override
    public int getCalorieStorageCapacity() {
        return 65535;
    }

    @Override
    public void onBeat() {
        this.network.requestOxygen(1);
        this.network.requestCalories(1);
        sync();
    }

    public CirculatoryNetwork getNetwork(){
        return this.network;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        if(this.network == null) this.network = new CirculatoryNetwork(null);

        this.network.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        if(this.network == null) return compoundTag;
        this.network.toTag(compoundTag);
        return compoundTag;
    }
}
