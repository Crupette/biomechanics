package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.BloodSqueezerBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;

public class BiomechanicsScreenTypes {

    public static void init(){
        ContainerProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("blood_squeezer"), (sync, identifier, player, buf) -> {
            final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
            return ((BloodSqueezerBlockEntity)blockEntity).createScreenHandler(sync, player.inventory);
        });
    }
}
