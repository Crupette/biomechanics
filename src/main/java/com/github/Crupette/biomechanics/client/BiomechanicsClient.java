package com.github.Crupette.biomechanics.client;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.client.screen.BloodSqueezerScreen;
import com.github.Crupette.biomechanics.screen.BloodSqueezerScreenHandler;
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
        ScreenProviderRegistry.INSTANCE.registerFactory(Biomechanics.identify("blood_squeezer"), (container) -> {
            return new BloodSqueezerScreen((BloodSqueezerScreenHandler) container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.biomechanic.blood_squeezer"));
        });
    }
}
