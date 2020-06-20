package com.github.Crupette.biomechanics.block;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.entity.MaceratorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class BiomechanicsBlocks {

    public static final Block SQUEEZER = new SqueezerBlock(FabricBlockSettings.of(Material.METAL)
            .sounds(BlockSoundGroup.METAL).strength(4.f)
            .lightLevel((blockState) -> (Boolean)blockState.get(Properties.LIT) ? 13 : 0)
            .requiresTool());

    public static final Block HEART_CASE = new HeartCaseBlock(FabricBlockSettings.of(Material.METAL)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
            .strength(4.f));
    public static final Block OXYGEN_PUMP = new OxygenPumpBlock(FabricBlockSettings.of(Material.METAL)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
            .strength(4.f));
    public static final Block DIGESTOR = new DigestorBlock(FabricBlockSettings.of(Material.METAL)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
            .strength(4.f));
    public static final Block BOILER = new BoilerBlock(FabricBlockSettings.of(Material.METAL)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
            .strength(4.f));

    public static final Block BLOOD_VESSEL = new BloodVesselBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC)
            .sounds(BlockSoundGroup.HONEY)
            .strength(0.5f));
    public static final Block MACERATOR = new MaceratorBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC)
            .sounds(BlockSoundGroup.HONEY)
            .strength(0.5f));

    public static final Block FLESHY_HOPPER = new FleshyHopperBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC)
            .requiresTool()
            .strength(3.f, 4.8f)
            .sounds(BlockSoundGroup.HONEY));

    public static final Block PHOTOSYNTHESIZER = new PhotosynthesizerBlock(FabricBlockSettings.of(Material.METAL)
            .requiresTool()
            .strength(3.5f)
            .sounds(BlockSoundGroup.METAL));

    private static void register(String name, Block block, Item.Settings settings){
        Registry.register(Registry.BLOCK, Biomechanics.identify(name), block);
        Registry.register(Registry.ITEM, Biomechanics.identify(name), new BlockItem(block, settings));
    }

    public static void init(){
        register("squeezer", SQUEEZER, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("heart_case", HEART_CASE, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("oxygen_pump", OXYGEN_PUMP, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("digestor", DIGESTOR, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("boiler", BOILER, new Item.Settings().group(ItemGroup.DECORATIONS));

        register("macerator", MACERATOR, new Item.Settings().group(ItemGroup.DECORATIONS));

        register("blood_vessel", BLOOD_VESSEL, new Item.Settings().group(ItemGroup.DECORATIONS));
        register("fleshy_hopper", FLESHY_HOPPER, new Item.Settings().group(ItemGroup.REDSTONE));

        register("photosynthesizer", PHOTOSYNTHESIZER, new Item.Settings().group(ItemGroup.DECORATIONS));
    }
}
