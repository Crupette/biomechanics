package com.github.Crupette.biomechanics.client;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.client.screen.*;
import com.github.Crupette.biomechanics.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class BiomechanicsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ScreenProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("squeezer"), (container) -> {
            return new SqueezerScreen((SqueezerScreenHandler) container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.biomechanics.squeezer"));
        });

        ScreenProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("heart_case"), (container) -> {
            return new HeartCaseScreen((HeartCaseScreenHandler) container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.biomechanics.heart_case"));
        });

        ScreenProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("oxygen_pump"), (container) -> {
            return new OxygenPumpScreen((OxygenPumpScreenHandler) container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.biomechanics.oxygen_pump"));
        });

        ScreenProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("digestor"), (container) -> {
            return new DigestorScreen((DigestorScreenHandler) container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.biomechanics.digestor"));
        });

        ScreenRegistry.register(BiomechanicsScreenTypes.BOILER, (ScreenRegistry.Factory<BoilerScreenHandler, BoilerScreen>) (boilerScreenHandler, playerInventory, text) -> {
            return new BoilerScreen(boilerScreenHandler, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.biomechanics.boiler"));
        });

        BiomechanicsScreenTypes.BOILER = ScreenHandlerRegistry.registerSimple(Biomechanics.identify("boiler"), BoilerScreenHandler::new);
    }
}
