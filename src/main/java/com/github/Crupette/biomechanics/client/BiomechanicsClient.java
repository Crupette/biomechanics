package com.github.Crupette.biomechanics.client;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.client.block.BiomechanicsBlockRenderers;
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

        ScreenRegistry.register(BiomechanicsScreenTypes.SQUEEZER, SqueezerScreen::new);
        ScreenRegistry.register(BiomechanicsScreenTypes.HEART_CASE, HeartCaseScreen::new);
        ScreenRegistry.register(BiomechanicsScreenTypes.OXYGEN_PUMP, OxygenPumpScreen::new);
        ScreenRegistry.register(BiomechanicsScreenTypes.DIGESTOR, DigestorScreen::new);

        ScreenRegistry.register(BiomechanicsScreenTypes.BOILER, BoilerScreen::new);

        BiomechanicsBlockRenderers.init();
    }
}
