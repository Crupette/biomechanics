package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.DigestorBlockEntity;
import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import com.github.Crupette.biomechanics.block.entity.OxygenPumpBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DigestorBlock extends FacingWithEntity {

    protected DigestorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new DigestorBlockEntity();
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            ContainerProviderRegistry.INSTANCE.openContainer(Biomechanics.identify("digestor"), player, buf -> buf.writeBlockPos(pos));
            return ActionResult.CONSUME;
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DigestorBlockEntity) {
                ((DigestorBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }

    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DigestorBlockEntity) {
                ItemScatterer.spawn(world, pos, (DigestorBlockEntity)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof DigestorBlockEntity){
            DigestorBlockEntity digestorBlockEntity = (DigestorBlockEntity)blockEntity;
            if(digestorBlockEntity.getParent() == null){
                digestorBlockEntity.setParent(null);
            }else {
                HeartCaseBlockEntity parent = (HeartCaseBlockEntity) world.getBlockEntity(digestorBlockEntity.getParent());
                if(parent != null) {
                    parent.updateConnectionTree();
                }
            }
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }
}
