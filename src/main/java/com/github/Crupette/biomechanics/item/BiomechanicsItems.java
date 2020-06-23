package com.github.Crupette.biomechanics.item;

import com.github.Crupette.biomechanics.Biomechanics;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class BiomechanicsItems {
    public static Item HEART = new HeartItem(new Item.Settings().maxCount(1));
    public static Item STOMACH = new OrganItem(new Item.Settings().maxCount(1), "Stomach");
    public static Item LUNGS = new OrganItem(new Item.Settings().maxCount(1), "Lungs");
    public static Item SMALL_INTESTINE = new OrganItem(new Item.Settings().maxCount(1), "Small intestines");

    public static Item DECAY_STABILIZER = new RandomlyDecayingItem(new Item.Settings().maxDamage(1024).group(BiomechanicsItemGroup.BIOMECHANICS));
    public static Item BLOOD_BOTTLE = new Item(new Item.Settings().group(BiomechanicsItemGroup.BIOMECHANICS));

    public static Item SCALPEL = new SwordItem(ToolMaterials.IRON, 0, -1.5f, new Item.Settings().group(ItemGroup.COMBAT).maxDamage(256));

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
