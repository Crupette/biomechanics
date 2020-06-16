package com.github.Crupette.biomechanics.client.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.screen.HeartCaseScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HeartCaseScreen extends HandledScreen<HeartCaseScreenHandler> {
    private static final Identifier BACKGROUND = Biomechanics.identify("textures/gui/container/heart_case.png");

    public HeartCaseScreen(HeartCaseScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        this.drawBackground(matrices, delta, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);

        if(this.handler.getStacks().get(0).isEmpty()){
            this.textRenderer.drawWithShadow(matrices, "Missing heart", this.x + 47, this.y + 39, 0xFF0000);
        }else
        if(this.handler.getDepletedBottles() < this.handler.getDepletedBottlesNeeded() || this.handler.getSaturatedBottles() < this.handler.getSaturatedBottlesNeeded()){
            this.textRenderer.drawWithShadow(matrices, "Need blood!", this.x + 47, this.y + 39, 0xFF0000);
        }else if(this.handler.getHeartAttack()) {
            this.textRenderer.drawWithShadow(matrices, "Cardiac arrest!", this.x + 47, this.y + 39, 0xFF0000);
        }else{
            this.textRenderer.drawWithShadow(
                    matrices, "Beating normally", this.x + 47, this.y + 39, 0xFFFFFF);
        }

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.client.getTextureManager().bindTexture(new Identifier("textures/block/water_still.png"));
        if(this.handler.getSaturatedBottles() > 0){
            RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1.F);
            int height = (int)(((float)this.handler.getSaturatedBottles() / (float)this.handler.getSaturatedBottlesNeeded()) * 52);
            int ypos = 17 + (52 - height);

            this.drawTexture(matrices, i + 134, j + ypos, 0, 0, 16, height, 16, 512);
        }
        if(this.handler.getDepletedBottles() > 0){
            RenderSystem.color4f(0.8F, 0.0F, 0.0F, 1.F);
            int height = (int)(((float)this.handler.getDepletedBottles() / (float)this.handler.getDepletedBottlesNeeded()) * 52);
            int ypos = 17 + (52 - height);

            this.drawTexture(matrices, i + 26, j + ypos, 0, 0, 16, height, 16, 512);
        }
    }
}
