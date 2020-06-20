package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class PhotosynthesizerBlockEntity extends BlockEntity implements BiologicalAdjacent {
    private BlockPos parent;
    private CirculatoryNetwork network;

    public PhotosynthesizerBlockEntity() {
        super(BiomechanicsBlockEntities.PHOTOSYNTHESIZER);
    }

    @Override
    public BlockPos getParent() {
        return this.parent;
    }

    @Override
    public void setParent(BlockPos pos) {
        System.out.println("Setting parent");
        this.parent = pos;
        if(pos != null){
            this.network = ((HeartCaseBlockEntity)this.world.getBlockEntity(pos)).network;
        }
    }

    @Override
    public int getCalorieStorageCapacity() {
        return 2048;
    }

    @Override
    public void onBeat() {
        this.network.requestOxygen(1);
        this.network.requestCalories(1);
        if(!this.world.getBlockState(this.pos.up()).isSolidBlock(this.world, this.pos.up())){
            int light = this.world.getLightLevel(this.pos.up());
            this.network.provideCalories((int) ((float) light / 2.f));
        }
    }
}
