package com.github.Crupette.biomechanics.item;

import com.github.Crupette.biomechanics.Biomechanics;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class BiomechanicsItems {
    public static Item HEART = new HeartItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC));
    public static Item DECAY_STABILIZER = new RandomlyDecayingItem(new Item.Settings().maxDamage(1024).group(ItemGroup.MISC));
    public static Item BLOOD_BOTTLE = new Item(new Item.Settings().group(ItemGroup.MISC));

    public static void init(){
        for(Field field : BiomechanicsItems.class.getDeclaredFields()){
            try {
                if(field.get(null) instanceof Item){
                    Registry.register(Registry.ITEM, Biomechanics.identify(field.getName().toLowerCase()), (Item)field.get(null));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
