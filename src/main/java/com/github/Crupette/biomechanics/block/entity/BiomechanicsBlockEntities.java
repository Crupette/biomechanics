package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.BiomechanicsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BiomechanicsBlockEntities {

    public static final BlockEntityType<SqueezerBlockEntity> SQUEEZER =
            BlockEntityType.Builder.create(SqueezerBlockEntity::new, new Block[]{BiomechanicsBlocks.SQUEEZER}).build(null);

    public static final BlockEntityType<HeartCaseBlockEntity> HEART_CASE =
            BlockEntityType.Builder.create(HeartCaseBlockEntity::new, new Block[]{BiomechanicsBlocks.HEART_CASE}).build(null);

    public static final BlockEntityType<OxygenPumpBlockEntity> OXYGEN_PUMP =
            BlockEntityType.Builder.create(OxygenPumpBlockEntity::new, new Block[]{BiomechanicsBlocks.OXYGEN_PUMP}).build(null);

    public static final BlockEntityType<DigestorBlockEntity> DIGESTOR =
            BlockEntityType.Builder.create(DigestorBlockEntity::new, new Block[] {BiomechanicsBlocks.DIGESTOR}).build(null);

    public static final BlockEntityType<BloodVesselBlockEntity> BLOOD_VESSEL =
            BlockEntityType.Builder.create(BloodVesselBlockEntity::new, new Block[]{BiomechanicsBlocks.BLOOD_VESSEL}).build(null);

    public static void init(){
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Biomechanics.identify("squeezer"), SQUEEZER);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Biomechanics.identify("heart_case"), HEART_CASE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Biomechanics.identify("oxygen_pump"), OXYGEN_PUMP);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Biomechanics.identify("digestor"), DIGESTOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Biomechanics.identify("blood_vessel"), BLOOD_VESSEL);
    }
}
