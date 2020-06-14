package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.SqueezerBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;

public class BiomechanicsScreenTypes {

    public static void init(){

        ContainerProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("squeezer"), (sync, identifier, player, buf) -> {
            final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
            return ((SqueezerBlockEntity)blockEntity).createMenu(sync, player.inventory, player);
        });
    }
}
