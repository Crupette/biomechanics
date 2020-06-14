package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.BiomechanicsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BiomechanicsBlockEntities {

    public static final BlockEntityType<SqueezerBlockEntity> SQUEEZER =
            BlockEntityType.Builder.create(SqueezerBlockEntity::new, new Block[]{BiomechanicsBlocks.SQUEEZER}).build(null);

    public static void init(){
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Biomechanics.identify("test"), SQUEEZER);
    }
}
