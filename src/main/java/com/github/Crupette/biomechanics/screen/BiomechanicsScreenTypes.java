package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.*;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class BiomechanicsScreenTypes {

    public static ScreenHandlerType<BoilerScreenHandler> BOILER = null;

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
