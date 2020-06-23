package com.github.Crupette.biomechanics.item;

import com.github.Crupette.biomechanics.Biomechanics;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class BiomechanicsItemGroup {

    public static final ItemGroup BIOMECHANICS = FabricItemGroupBuilder.build(
            Biomechanics.identify("items"), () -> new ItemStack(BiomechanicsItems.HEART));
}
