package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.block.entity.Biological;
import com.github.Crupette.biomechanics.block.entity.BloodVesselBlockEntity;
import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BloodVesselBlock extends ConnectingBlock implements BlockEntityProvider {

    public BloodVesselBlock(Settings settings) {
        super(0.3125f, settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
    }

    public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
        boolean down =  world.getBlockEntity(pos.down())    instanceof Biological;
        boolean up =    world.getBlockEntity(pos.up())      instanceof Biological;
        boolean north = world.getBlockEntity(pos.north())   instanceof Biological;
        boolean east =  world.getBlockEntity(pos.east())    instanceof Biological;
        boolean south = world.getBlockEntity(pos.south())   instanceof Biological;
        boolean west =  world.getBlockEntity(pos.west())    instanceof Biological;
        return this.getDefaultState().with(DOWN, down).with(UP, up).with(NORTH, north).with(EAST, east).with(SOUTH, south).with(WEST, west);
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
        return state.with(FACING_PROPERTIES.get(direction), world.getBlockEntity(posFrom) instanceof Biological);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
