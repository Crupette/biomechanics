package com.github.Crupette.biomechanics.client.block;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.FatStorageBlockEntity;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class FatStorageBlockRenderer extends BlockEntityRenderer<FatStorageBlockEntity> {
    private static final Identifier FAT_TEXTURE = Biomechanics.identify("textures/block/fat.png");

    public FatStorageBlockRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FatStorageBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CirculatoryNetwork network = entity.getNetwork();
        if(network == null) {
            return;
        }

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(FAT_TEXTURE));
        MatrixStack.Entry matrixEntry = matrices.peek();
        Matrix4f model = matrixEntry.getModel();
        Matrix3f normal = matrixEntry.getNormal();

        if(network.getCalorieStorage() == 0) return;
        if(network.getCalorieStorageCapacity() == 0) return;
        float fat_height = ((float)network.getCalorieStorage() / (float)network.getCalorieStorageCapacity()) + (5.f / 16.f);

        final float min = 1.f / 16.f;
        final float max = 15.f / 16.f;

        //Top face
        consumer.vertex(model, min, fat_height, min).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, min, fat_height, max).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, fat_height, max).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, fat_height, min).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();

        //-z face
        consumer.vertex(model, min, 0, min).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, min, fat_height, min).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, fat_height, min).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, 0, min).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();

        //+z face
        consumer.vertex(model, min, 0, max).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, 0, max).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, fat_height, max).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, min, fat_height, max).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();

        //-x face
        consumer.vertex(model, min, 0, min).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, min, 0, max).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, min, fat_height, max).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, min, fat_height, min).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();

        //+x face
        consumer.vertex(model, max, 0, min).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, fat_height, min).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, fat_height, max).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
        consumer.vertex(model, max, 0, max).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal, 0, 1, 0).next();
    }
}
