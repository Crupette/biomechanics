//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.Crupette.biomechanics.client.screen;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.BoilerBlockEntity;
import com.github.Crupette.biomechanics.network.UpdateBoilerC2SPacket;
import com.github.Crupette.biomechanics.screen.BoilerScreenHandler;
import com.github.Crupette.biomechanics.screen.SqueezerScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BoilerScreen extends HandledScreen<BoilerScreenHandler> {
    private static final Identifier BACKGROUND = Biomechanics.identify("textures/gui/container/boiler.png");

    private ButtonWidget upButton;
    private ButtonWidget upMajorButton;

    private ButtonWidget downButton;
    private ButtonWidget downMajorButton;

    public BoilerScreen(BoilerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.upButton = this.addButton(new ButtonWidget(this.x + 100, this.y + 60, 8,
                this.textRenderer.fontHeight + 2, new LiteralText(">"), (button) -> {
            BoilerScreen.this.client.getNetworkHandler().sendPacket(new UpdateBoilerC2SPacket(this.handler.getCalorieCost() + 5));
        }));

        this.upMajorButton = this.addButton(new ButtonWidget(this.x + 111, this.y + 60, 12,
                this.textRenderer.fontHeight + 2, new LiteralText(">>"), (button) -> {
            BoilerScreen.this.client.getNetworkHandler().sendPacket(new UpdateBoilerC2SPacket(this.handler.getCalorieCost() + 50));
        }));

        this.downButton = this.addButton(new ButtonWidget(this.x + 90, this.y + 60, 8,
                this.textRenderer.fontHeight + 2, new LiteralText("<"), (button) -> {
            BoilerScreen.this.client.getNetworkHandler().sendPacket(new UpdateBoilerC2SPacket(Math.max(this.handler.getCalorieCost() - 5, BoilerBlockEntity.CALORIE_MIN)));
        }));

        this.downMajorButton = this.addButton(new ButtonWidget(this.x + 75, this.y + 60, 12,
                this.textRenderer.fontHeight + 2, new LiteralText("<<"), (button) -> {
            BoilerScreen.this.client.getNetworkHandler().sendPacket(new UpdateBoilerC2SPacket(Math.max(this.handler.getCalorieCost() - 50, BoilerBlockEntity.CALORIE_MIN)));
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        this.drawBackground(matrices, delta, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int l;

        l = this.handler.getBoilProgress();
        this.drawTexture(matrices, i + 79, j + 34, 176, 14, l + 1, 16);
    }
}
