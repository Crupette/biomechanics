package com.github.Crupette.biomechanics.recipe;

import com.github.Crupette.biomechanics.Biomechanics;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class BiomechanicsRecipes {

    public static final RecipeType<MaceratorRecipe> MACERATOR =
            RecipeType.register("biomechanics:macerating");
    public static final RecipeSerializer<MaceratorRecipe> MACERATOR_SERIALIZER = new MaceratorRecipe.Serializer();

    public static void init(){
        RecipeSerializer.register("biomechanics:macerating", MACERATOR_SERIALIZER);
    }
}
