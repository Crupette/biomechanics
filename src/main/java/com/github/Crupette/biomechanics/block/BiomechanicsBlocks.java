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
    private static final Item squeezer = new BlockItem(SQUEEZER, new Item.Settings().group(ItemGroup.DECORATIONS));

    public static final Block HEART_CASE = new HeartCaseBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL));
    private static final Item heart_case = new BlockItem(HEART_CASE, new Item.Settings().group(ItemGroup.DECORATIONS));

    public static final Block BLOOD_VESSEL = new BloodVesselBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).sounds(BlockSoundGroup.HONEY));
    private static final Item blood_vessel = new BlockItem(BLOOD_VESSEL, new Item.Settings().group(ItemGroup.DECORATIONS));

    public static void init(){
        for(Field field : BiomechanicsBlocks.class.getDeclaredFields()){
            try {
                if(field.get(null) instanceof Block){
                    Registry.register(Registry.BLOCK, Biomechanics.identify(field.getName().toLowerCase()), (Block)field.get(null));
                }
                if(field.get(null) instanceof Item){
                    Registry.register(Registry.ITEM, Biomechanics.identify(field.getName().toLowerCase()), (Item)field.get(null));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
