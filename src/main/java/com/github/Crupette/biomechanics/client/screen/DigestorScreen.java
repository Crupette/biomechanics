//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.Crupette.biomechanics.client.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.screen.DigestorScreenHandler;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DigestorScreen extends HandledScreen<DigestorScreenHandler> {
    private static final Identifier BACKGROUND = Biomechanics.identify("textures/gui/container/digestor.png");

    public DigestorScreen(DigestorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }


    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        this.drawBackground(matrices, delta, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);

        int excess = this.handler.getNetworkCaloriesOverflow();
        if(excess > 0){
            this.textRenderer.draw(matrices, "Excess: " + excess, this.x +8, this.y + 60, 0x444444);
        }

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int processingCals = this.handler.getProcessingCalories();
        int processingMax = this.handler.getProcessingMaximum();

        if(processingMax > 0 && processingCals > 0){
            int stage = (int) (((float)processingCals / (float)processingMax) * 13);
            this.drawTexture(matrices, i + 101, j + 17 + 12 - stage, 176, 12 - stage, 14, stage + 1);
        }

        int stomachStorage = this.handler.getStoredCalories();
        int stomachMaximum = this.handler.getStoredMaximum();
        if(stomachMaximum > 0 && stomachStorage > 0){
            int stage = (int)(((float)stomachStorage / (float)stomachMaximum) * 35);
            stage = Math.min(35 , stage);
            this.drawTexture(matrices, i + 100, j + 34 + 36 - stage, 176, 13 + 36 - stage, 16, stage + 1);
        }

        int networkStorage = this.handler.getNetworkCalories();
        int networkMaximum = this.handler.getNetworkCaloriesStorage();
        if(networkMaximum > 0 && networkStorage > 0){
            int stage = (int)(((float)networkStorage / (float)networkMaximum) * 35);
            stage = Math.min(35 , stage);
            this.drawTexture(matrices, i + 120, j + 34 + 36 - stage, 176, 13 + 36 - stage, 16, stage + 1);
        }

        int bloodStorage = this.handler.getNetworkBloodCalories();
        int bloodMaximum = this.handler.getNetworkHeartHealth() * CirculatoryNetwork.BLOOD_CALORIE_MAX_SATURATION;

        if(bloodMaximum > 0 && bloodStorage > 0){
            int stage = (int)(((float)bloodStorage / (float)bloodMaximum) * 35);
            stage = Math.min(35 , stage);
            this.drawTexture(matrices, i + 140, j + 34 + 36 - stage, 176, 13 + 36 + 36 - stage, 16, stage + 1);
        }
    }
}
