package com.github.Crupette.biomechanics.tag;

import com.github.Crupette.biomechanics.Biomechanics;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

public class BiomechanicsTags {

    public static Tag<Item> MEATS = TagRegistry.item(Biomechanics.identify("meats"));

}
