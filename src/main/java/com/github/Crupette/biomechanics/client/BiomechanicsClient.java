package com.github.Crupette.biomechanics.client;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.client.screen.DigestorScreen;
import com.github.Crupette.biomechanics.client.screen.HeartCaseScreen;
import com.github.Crupette.biomechanics.client.screen.OxygenPumpScreen;
import com.github.Crupette.biomechanics.client.screen.SqueezerScreen;
import com.github.Crupette.biomechanics.screen.DigestorScreenHandler;
import com.github.Crupette.biomechanics.screen.HeartCaseScreenHandler;
import com.github.Crupette.biomechanics.screen.OxygenPumpScreenHandler;
import com.github.Crupette.biomechanics.screen.SqueezerScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.MinecraftClient;
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
    }
}
