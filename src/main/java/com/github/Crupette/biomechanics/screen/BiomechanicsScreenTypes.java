package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.OxygenPumpBlock;
import com.github.Crupette.biomechanics.block.entity.*;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public class BiomechanicsScreenTypes {

    public static ScreenHandlerType<SqueezerScreenHandler> SQUEEZER = null;
    public static ScreenHandlerType<HeartCaseScreenHandler> HEART_CASE = null;
    public static ScreenHandlerType<OxygenPumpScreenHandler> OXYGEN_PUMP = null;
    public static ScreenHandlerType<DigestorScreenHandler> DIGESTOR = null;
    public static ScreenHandlerType<BoilerScreenHandler> BOILER = null;

    public static void init(){

       SQUEEZER = ScreenHandlerRegistry.registerExtended(Biomechanics.identify("squeezer"), (sync, playerInv, buf) -> {
            final BlockEntity blockEntity = playerInv.player.world.getBlockEntity(buf.readBlockPos());
            return (SqueezerScreenHandler) ((SqueezerBlockEntity)blockEntity).createMenu(sync, playerInv, playerInv.player);
        });

        HEART_CASE = ScreenHandlerRegistry.registerExtended(Biomechanics.identify("heart_case"), (sync, playerInv, buf) -> {
            final BlockEntity blockEntity = playerInv.player.world.getBlockEntity(buf.readBlockPos());
            return (HeartCaseScreenHandler) ((HeartCaseBlockEntity)blockEntity).createMenu(sync, playerInv, playerInv.player);
        });

        OXYGEN_PUMP = ScreenHandlerRegistry.registerExtended(Biomechanics.identify("oxygen_pump"), (sync, playerInv, buf) -> {
            final BlockEntity blockEntity = playerInv.player.world.getBlockEntity(buf.readBlockPos());
            return (OxygenPumpScreenHandler) ((OxygenPumpBlockEntity)blockEntity).createMenu(sync, playerInv, playerInv.player);
        });

        DIGESTOR = ScreenHandlerRegistry.registerExtended(Biomechanics.identify("digestor"), (sync, playerInv, buf) -> {
            final BlockEntity blockEntity = playerInv.player.world.getBlockEntity(buf.readBlockPos());
            return (DigestorScreenHandler) ((DigestorBlockEntity)blockEntity).createMenu(sync, playerInv, playerInv.player);
        });

        BOILER = ScreenHandlerRegistry.registerExtended(Biomechanics.identify("boiler"), (sync, playerInventory, buf) -> {
            final BlockEntity blockEntity = playerInventory.player.world.getBlockEntity(buf.readBlockPos());
            return (BoilerScreenHandler) ((BoilerBlockEntity)blockEntity).createMenu(sync, playerInventory, playerInventory.player);
        });
    }
}
