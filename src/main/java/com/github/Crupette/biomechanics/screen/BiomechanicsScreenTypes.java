package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.DigestorBlockEntity;
import com.github.Crupette.biomechanics.block.entity.HeartCaseBlockEntity;
import com.github.Crupette.biomechanics.block.entity.OxygenPumpBlockEntity;
import com.github.Crupette.biomechanics.block.entity.SqueezerBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;

public class BiomechanicsScreenTypes {

    public static void init(){

        ContainerProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("squeezer"), (sync, identifier, player, buf) -> {
            final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
            return ((SqueezerBlockEntity)blockEntity).createMenu(sync, player.inventory, player);
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("heart_case"), (sync, identifier, player, buf) -> {
            final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
            return ((HeartCaseBlockEntity)blockEntity).createMenu(sync, player.inventory, player);
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("oxygen_pump"), (sync, identifier, player, buf) -> {
            final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
            return ((OxygenPumpBlockEntity)blockEntity).createMenu(sync, player.inventory, player);
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("digestor"), (sync, identifier, player, buf) -> {
            final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
            return ((DigestorBlockEntity)blockEntity).createMenu(sync, player.inventory, player);
        });
    }
}
