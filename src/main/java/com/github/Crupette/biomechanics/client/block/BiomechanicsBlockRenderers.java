package com.github.Crupette.biomechanics.client.block;

import com.github.Crupette.biomechanics.block.BiomechanicsBlocks;
import com.github.Crupette.biomechanics.block.entity.BiomechanicsBlockEntities;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class BiomechanicsBlockRenderers {

    public static void init(){
        BlockEntityRendererRegistry.INSTANCE.register(BiomechanicsBlockEntities.FAT_STORAGE, FatStorageBlockRenderer::new);
    }
}
