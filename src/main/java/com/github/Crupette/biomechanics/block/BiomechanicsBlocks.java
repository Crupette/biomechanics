package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class BiomechanicsBlocks {
    public static final Block BLOOD_SQUEEZER = new BloodSqueezerBlock(FabricBlockSettings.of(Material.METAL));
    private static final Item blood_squeezer = new BlockItem(BLOOD_SQUEEZER, new Item.Settings().group(ItemGroup.DECORATIONS));

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
