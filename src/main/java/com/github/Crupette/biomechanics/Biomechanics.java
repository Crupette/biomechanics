package com.github.Crupette.biomechanics;

import com.github.Crupette.biomechanics.block.BiomechanicsBlocks;
import com.github.Crupette.biomechanics.block.entity.BiomechanicsBlockEntities;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.recipe.BiomechanicsRecipes;
import com.github.Crupette.biomechanics.screen.BiomechanicsScreenTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Biomechanics implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "biomechanics";
    public static final String MOD_NAME = "Bio Mechanics";

    public static Identifier identify(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        BiomechanicsItems.init();
        BiomechanicsBlocks.init();
        BiomechanicsBlockEntities.init();
        BiomechanicsScreenTypes.init();
        BiomechanicsRecipes.init();
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}