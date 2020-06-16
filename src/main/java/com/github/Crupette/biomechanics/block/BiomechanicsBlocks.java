package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.Biomechanics;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class BiomechanicsBlocks {

    public static final Block SQUEEZER = new SqueezerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL));

    public static final Block HEART_CASE = new HeartCaseBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL));
    public static final Block OXYGEN_PUMP = new OxygenPumpBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL));

    public static final Block BLOOD_VESSEL = new BloodVesselBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).sounds(BlockSoundGroup.HONEY));

    private static void register(String name, Block block, Item.Settings settings){
        Registry.register(Registry.BLOCK, Biomechanics.identify(name), block);
        Registry.register(Registry.ITEM, Biomechanics.identify(name), new BlockItem(block, settings));
    }

    public static void init(){
        register("squeezer", SQUEEZER, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("heart_case", HEART_CASE, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("oxygen_pump", OXYGEN_PUMP, new Item.Settings().group(ItemGroup.DECORATIONS));

        register("blood_vessel", BLOOD_VESSEL, new Item.Settings().group(ItemGroup.DECORATIONS));
    }
}
